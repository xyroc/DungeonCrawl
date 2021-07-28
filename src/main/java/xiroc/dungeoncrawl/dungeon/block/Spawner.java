/*
        Dungeon Crawl, a procedural dungeon generator for Minecraft 1.14 and later.
        Copyright (C) 2020

        This program is free software: you can redistribute it and/or modify
        it under the terms of the GNU General Public License as published by
        the Free Software Foundation, either version 3 of the License, or
        (at your option) any later version.

        This program is distributed in the hope that it will be useful,
        but WITHOUT ANY WARRANTY; without even the implied warranty of
        MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
        GNU General Public License for more details.

        You should have received a copy of the GNU General Public License
        along with this program.  If not, see <https://www.gnu.org/licenses/>.
*/

package xiroc.dungeoncrawl.dungeon.block;

import com.google.common.collect.ImmutableSet;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.SpawnerBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import xiroc.dungeoncrawl.DungeonCrawl;
import xiroc.dungeoncrawl.config.Config;
import xiroc.dungeoncrawl.dungeon.PlacementContext;
import xiroc.dungeoncrawl.dungeon.monster.RandomEquipment;
import xiroc.dungeoncrawl.dungeon.monster.RandomMonster;
import xiroc.dungeoncrawl.dungeon.monster.RandomPotionEffect;
import xiroc.dungeoncrawl.dungeon.monster.SpawnRates;
import xiroc.dungeoncrawl.dungeon.treasure.RandomItems;
import xiroc.dungeoncrawl.theme.Theme;
import xiroc.dungeoncrawl.util.IBlockPlacementHandler;
import xiroc.dungeoncrawl.util.Range;

import javax.annotation.Nullable;
import java.util.Random;
import java.util.Set;

public class Spawner implements IBlockPlacementHandler {

    public static final Set<EntityType<?>> INVENTORY_ENTITIES = ImmutableSet.<EntityType<?>>builder()
            .add(EntityType.ZOMBIE).add(EntityType.SKELETON).add(EntityType.HUSK).add(EntityType.STRAY).add(EntityType.WITHER_SKELETON).build();
    public static final Set<EntityType<?>> RANGED_INVENTORY_ENTITIES = ImmutableSet.<EntityType<?>>builder()
            .add(EntityType.SKELETON).add(EntityType.STRAY).build();

    @Override
    public void place(LevelAccessor world, BlockState state, BlockPos pos, Random rand, PlacementContext context,
                      Theme theme, Theme.SecondaryTheme secondaryTheme, int stage) {
        world.setBlock(pos, Blocks.SPAWNER.defaultBlockState(), 2);
        BlockEntity tile = world.getBlockEntity(pos);
        if (tile instanceof SpawnerBlockEntity spawner) {
            EntityType<?> type = RandomMonster.randomMonster(rand, stage);
            spawner.getSpawner().setEntityId(type);
            if (!Config.VANILLA_SPAWNERS.get() && INVENTORY_ENTITIES.contains(type)) {
                CompoundTag spawnerNBT = spawner.getSpawner().save(spawner.getLevel(), pos, new CompoundTag());
                ListTag potentialSpawns = new ListTag();
                for (int i = 0; i < Config.SPAWNER_ENTITIES.get(); i++) {
                    CompoundTag nbt = new CompoundTag();
                    CompoundTag spawnData = createSpawnData(type, null, rand, stage);
                    nbt.put("Entity", spawnData);
                    nbt.putInt("Weight", 1);
                    if (i == 0)
                        spawnerNBT.put("SpawnData", spawnData);
                    potentialSpawns.add(nbt);
                }
                Range delay = SpawnRates.getDelay(stage);
                spawnerNBT.put("SpawnPotentials", potentialSpawns);
                spawnerNBT.putShort("MinSpawnDelay", (short) delay.min());
                spawnerNBT.putShort("MaxSpawnDelay", (short) delay.max());
                spawnerNBT.putShort("SpawnCount", (short) SpawnRates.getAmount(stage).nextInt(rand));
                spawnerNBT.putShort("RequiredPlayerRange", Config.SPAWNER_RANGE.get().shortValue());
                spawner.getSpawner().load(spawner.getLevel(), pos, spawnerNBT);
            }
        } else {
            DungeonCrawl.LOGGER.error("Failed to fetch a mob spawner at ({}, {}, {})", pos.getX(), pos.getY(),
                    pos.getZ());
        }
    }

    public static CompoundTag createSpawnData(@Nullable EntityType<?> type, @Nullable CompoundTag spawnData,
                                              Random rand, int stage) {
        if (type == null)
            type = RandomMonster.randomMonster(rand, stage);
        if (spawnData == null)
            spawnData = new CompoundTag();

        ResourceLocation registryName = type.getRegistryName();
        if (registryName == null) {
            DungeonCrawl.LOGGER.warn("Entity type {} has no registry name.", type);
            return new CompoundTag();
        }

        spawnData.putString("id", registryName.toString());
        if (INVENTORY_ENTITIES.contains(type)) {
            ItemStack[] armor = RandomEquipment.createArmor(rand, stage);
            ListTag armorList = new ListTag();

            for (ItemStack stack : armor) {
                armorList.add(stack.save(new CompoundTag()));
            }

            if (armorList.size() > 0) {
                spawnData.put("ArmorItems", armorList);
            }

            ListTag handItems = new ListTag();
            ItemStack mainHand = RANGED_INVENTORY_ENTITIES.contains(type)
                    ? RandomEquipment.getRangedWeapon(DungeonBlocks.RANDOM, stage)
                    : RandomEquipment.getMeleeWeapon(DungeonBlocks.RANDOM, stage);

            if (mainHand != ItemStack.EMPTY) {
                handItems.add(mainHand.save(new CompoundTag()));
            }

            handItems.add(rand.nextDouble() < Config.SHIELD_PROBABILITY.get()
                    ? RandomItems.createShield(rand, stage).save(new CompoundTag())
                    : ItemStack.EMPTY.save(new CompoundTag()));

            spawnData.put("HandItems", handItems);

            if (!Config.NATURAL_DESPAWN.get()) {
                spawnData.putBoolean("PersistenceRequired", true);
            }

            ListTag potionEffects = RandomPotionEffect.createPotionEffects(rand, stage);
            if (potionEffects != null) {
                spawnData.put("ActiveEffects", potionEffects);
            }
        }

        if (Config.OVERWRITE_ENTITY_LOOT_TABLES.get() && RandomMonster.NBT_PATCHERS.containsKey(type)) {
            RandomMonster.NBT_PATCHERS.get(type).patch(spawnData, rand, stage);
        }
        return spawnData;
    }

}
