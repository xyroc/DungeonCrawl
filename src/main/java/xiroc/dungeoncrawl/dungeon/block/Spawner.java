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
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.monster.MonsterEntity;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.ItemStack;
import net.minecraft.loot.RandomValueRange;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.tileentity.MobSpawnerTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IWorld;
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

import javax.annotation.Nullable;
import java.util.Random;
import java.util.Set;

public class Spawner implements IBlockPlacementHandler {

    public static final Set<EntityType<?>> INVENTORY_ENTITIES = ImmutableSet.<EntityType<?>>builder()
            .add(EntityType.ZOMBIE).add(EntityType.SKELETON).add(EntityType.HUSK).add(EntityType.STRAY).add(EntityType.WITHER_SKELETON).build();
    public static final Set<EntityType<?>> RANGED_INVENTORY_ENTITIES = ImmutableSet.<EntityType<?>>builder()
            .add(EntityType.SKELETON).add(EntityType.STRAY).build();

    @Override
    public void place(IWorld world, BlockState state, BlockPos pos, Random rand, PlacementContext context,
                      Theme theme, Theme.SecondaryTheme secondaryTheme, int stage) {
        if (world.isEmptyBlock(pos.below())) {
            return;
        }
        world.setBlock(pos, Blocks.SPAWNER.defaultBlockState(), 2);
        TileEntity tileentity = world.getBlockEntity(pos);
        if (tileentity instanceof MobSpawnerTileEntity) {
            MobSpawnerTileEntity tile = (MobSpawnerTileEntity) tileentity;
            EntityType<?> type = RandomMonster.randomMonster(rand, stage);
            tile.getSpawner().setEntityId(type);
            if (!Config.VANILLA_SPAWNERS.get() && INVENTORY_ENTITIES.contains(type)) {
                CompoundNBT spawnerNBT = tile.getSpawner().save(new CompoundNBT());
                ListNBT potentialSpawns = new ListNBT();
                for (int i = 0; i < Config.SPAWNER_ENTITIES.get(); i++) {
                    CompoundNBT nbt = new CompoundNBT();
                    CompoundNBT spawnData = createSpawnData(type, null, rand, stage);
                    nbt.put("Entity", spawnData);
                    nbt.putInt("Weight", 1);
                    if (i == 0)
                        spawnerNBT.put("SpawnData", spawnData);
                    potentialSpawns.add(nbt);
                }
                RandomValueRange delay = SpawnRates.getDelay(stage);
                spawnerNBT.put("SpawnPotentials", potentialSpawns);
                spawnerNBT.putShort("MinSpawnDelay", (short) delay.getMin());
                spawnerNBT.putShort("MaxSpawnDelay", (short) delay.getMax());
                spawnerNBT.putShort("SpawnCount", (short) SpawnRates.getAmount(stage).getInt(rand));
                spawnerNBT.putShort("RequiredPlayerRange", Config.SPAWNER_RANGE.get().shortValue());
                tile.getSpawner().load(spawnerNBT);
            }
        } else {
            DungeonCrawl.LOGGER.error("Failed to fetch a mob spawner at ({}, {}, {})", pos.getX(), pos.getY(),
                    pos.getZ());
        }
    }

    public static CompoundNBT createSpawnData(@Nullable EntityType<?> type, @Nullable CompoundNBT spawnData,
                                              Random rand, int stage) {
        if (type == null)
            type = RandomMonster.randomMonster(rand, stage);
        if (spawnData == null)
            spawnData = new CompoundNBT();

        ResourceLocation registryName = type.getRegistryName();
        if (registryName == null) {
            DungeonCrawl.LOGGER.warn("Entity type {} has no registry name.", type);
            return new CompoundNBT();
        }

        spawnData.putString("id", registryName.toString());
        if (INVENTORY_ENTITIES.contains(type)) {
            ItemStack[] armor = RandomEquipment.createArmor(rand, stage);
            ListNBT armorList = new ListNBT();

            for (ItemStack stack : armor) {
                armorList.add(stack.save(new CompoundNBT()));
            }

            if (armorList.size() > 0) {
                spawnData.put("ArmorItems", armorList);
            }

            ListNBT handItems = new ListNBT();
            ItemStack mainHand = RANGED_INVENTORY_ENTITIES.contains(type)
                    ? RandomEquipment.getRangedWeapon(DungeonBlocks.RANDOM, stage)
                    : RandomEquipment.getMeleeWeapon(DungeonBlocks.RANDOM, stage);

            if (mainHand != ItemStack.EMPTY) {
                handItems.add(mainHand.save(new CompoundNBT()));
            }

            handItems.add(rand.nextDouble() < Config.SHIELD_PROBABILITY.get()
                    ? RandomItems.createShield(rand, stage).save(new CompoundNBT())
                    : ItemStack.EMPTY.save(new CompoundNBT()));

            spawnData.put("HandItems", handItems);

            if (!Config.NATURAL_DESPAWN.get()) {
                spawnData.putBoolean("PersistenceRequired", true);
            }

            ListNBT potionEffects = RandomPotionEffect.createPotionEffects(rand, stage);
            if (potionEffects != null) {
                spawnData.put("ActiveEffects", potionEffects);
            }
        }

        if (Config.OVERWRITE_ENTITY_LOOT_TABLES.get() && RandomMonster.NBT_PATCHERS.containsKey(type)) {
            RandomMonster.NBT_PATCHERS.get(type).patch(spawnData, rand, stage);
        }
        return spawnData;
    }

    public static void equipMonster(MonsterEntity entity, Random rand, int stage) {
        if (INVENTORY_ENTITIES.contains(entity.getType())) {
            ItemStack[] armor = RandomEquipment.createArmor(rand, stage);
            entity.setItemSlot(EquipmentSlotType.FEET, armor[0]);
            entity.setItemSlot(EquipmentSlotType.LEGS, armor[1]);
            entity.setItemSlot(EquipmentSlotType.CHEST, armor[2]);
            entity.setItemSlot(EquipmentSlotType.HEAD, armor[3]);

            ItemStack mainHand = RANGED_INVENTORY_ENTITIES.contains(entity.getType())
                    ? RandomEquipment.getRangedWeapon(DungeonBlocks.RANDOM, stage)
                    : RandomEquipment.getMeleeWeapon(DungeonBlocks.RANDOM, stage);
            entity.setItemSlot(EquipmentSlotType.MAINHAND, mainHand);

            if (rand.nextDouble() < Config.SHIELD_PROBABILITY.get())
                entity.setItemSlot(EquipmentSlotType.OFFHAND, RandomItems.createShield(rand, stage));

            RandomPotionEffect.applyPotionEffects(entity, rand, stage);

            if (!Config.NATURAL_DESPAWN.get()) {
                entity.setPersistenceRequired();
            }

            if (RandomMonster.NBT_PATCHERS.containsKey(entity.getType())) {
                CompoundNBT nbt = new CompoundNBT();
                entity.addAdditionalSaveData(nbt);
                RandomMonster.NBT_PATCHERS.get(entity.getType()).patch(nbt, rand, stage);
                entity.readAdditionalSaveData(nbt);
            }
        }
    }

}
