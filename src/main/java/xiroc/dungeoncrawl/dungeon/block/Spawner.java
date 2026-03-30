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
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.util.ProblemReporter;
import net.minecraft.util.RandomSource;
import net.minecraft.util.random.WeightedList;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.EntityEquipment;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.SpawnData;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.SpawnerBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.TagValueInput;
import net.minecraft.world.level.storage.TagValueOutput;
import xiroc.dungeoncrawl.DungeonCrawl;
import xiroc.dungeoncrawl.config.Config;
import xiroc.dungeoncrawl.dungeon.monster.RandomEquipment;
import xiroc.dungeoncrawl.dungeon.monster.RandomMonster;
import xiroc.dungeoncrawl.dungeon.monster.RandomPotionEffect;
import xiroc.dungeoncrawl.dungeon.monster.SpawnRates;
import xiroc.dungeoncrawl.dungeon.treasure.RandomItems;
import xiroc.dungeoncrawl.theme.SecondaryTheme;
import xiroc.dungeoncrawl.theme.Theme;
import xiroc.dungeoncrawl.util.IBlockPlacementHandler;
import xiroc.dungeoncrawl.util.Range;

import javax.annotation.Nullable;
import java.util.Optional;
import java.util.Set;

public class Spawner implements IBlockPlacementHandler {

    public static final Set<EntityType<?>> INVENTORY_ENTITIES = ImmutableSet.<EntityType<?>>builder()
            .add(EntityType.ZOMBIE).add(EntityType.SKELETON).add(EntityType.HUSK).add(EntityType.STRAY).add(EntityType.WITHER_SKELETON).build();
    public static final Set<EntityType<?>> RANGED_INVENTORY_ENTITIES = ImmutableSet.<EntityType<?>>builder()
            .add(EntityType.SKELETON).add(EntityType.STRAY).build();

    @Override
    public void place(LevelAccessor world, BlockState state, BlockPos pos, RandomSource rand, Theme theme, SecondaryTheme secondaryTheme, int stage) {
        world.setBlock(pos, Blocks.SPAWNER.defaultBlockState(), 2);
        BlockEntity tile = world.getBlockEntity(pos);
        if (tile instanceof SpawnerBlockEntity spawner) {
            EntityType<?> type = RandomMonster.randomMonster(rand, stage);
            spawner.getSpawner().setEntityId(type, null, rand, pos);
            if (Config.CUSTOM_SPAWNERS.get() && INVENTORY_ENTITIES.contains(type)) {
                TagValueOutput spawnerNBTOutput = TagValueOutput.createWithContext(ProblemReporter.DISCARDING, world.registryAccess());
                spawner.getSpawner().save(spawnerNBTOutput);

                WeightedList.Builder<SpawnData> spawnPotentials = WeightedList.builder();

                for (int i = 0; i < Config.SPAWNER_ENTITIES.get(); i++) {
                    SpawnData entity = createSpawnData(type, rand, stage, world.registryAccess());
                    spawnPotentials.add(entity);
                    if (i == 0) {
                        spawnerNBTOutput.store("SpawnData", SpawnData.CODEC, entity);
                    }
                }

                spawnerNBTOutput.store("SpawnPotentials", SpawnData.LIST_CODEC, spawnPotentials.build());

                Range delay = SpawnRates.getDelay(stage);
                spawnerNBTOutput.putShort("MinSpawnDelay", (short) delay.min());
                spawnerNBTOutput.putShort("MaxSpawnDelay", (short) delay.max());
                spawnerNBTOutput.putShort("SpawnCount", (short) SpawnRates.getAmount(stage).nextInt(rand));
                spawnerNBTOutput.putShort("RequiredPlayerRange", Config.SPAWNER_RANGE.get().shortValue());
                spawner.getSpawner().load(spawner.getLevel(), pos, TagValueInput.create(ProblemReporter.DISCARDING, world.registryAccess(), spawnerNBTOutput.buildResult()));
            }
        } else {
            DungeonCrawl.LOGGER.error("Failed to fetch a mob spawner at ({}, {}, {})", pos.getX(), pos.getY(),
                    pos.getZ());
        }
    }

    public static SpawnData createSpawnData(@Nullable EntityType<?> type,
                                            RandomSource rand, int stage, RegistryAccess registryAccess) {
        if (type == null)
            type = RandomMonster.randomMonster(rand, stage);

        Identifier registryName = BuiltInRegistries.ENTITY_TYPE.getKey(type);

        TagValueOutput nbt = TagValueOutput.createWithContext(ProblemReporter.DISCARDING, registryAccess);

        nbt.putString("id", registryName.toString());
        if (INVENTORY_ENTITIES.contains(type)) {
            EntityEquipment equipment = new EntityEquipment();
            RandomEquipment.createArmor(equipment, rand, stage, registryAccess);

            ItemStack mainHand = RANGED_INVENTORY_ENTITIES.contains(type)
                    ? RandomEquipment.getRangedWeapon(rand, stage, registryAccess)
                    : RandomEquipment.getMeleeWeapon(rand, stage, registryAccess);

            equipment.set(EquipmentSlot.MAINHAND, mainHand);

            if (rand.nextDouble() < 0.25) {
                equipment.set(EquipmentSlot.OFFHAND, RandomItems.createShield(rand, stage, registryAccess));
            }

            nbt.store("equipment", EntityEquipment.CODEC, equipment);

            if (!Config.NATURAL_DESPAWN.get()) {
                nbt.putBoolean("PersistenceRequired", true);
            }

            RandomPotionEffect.createPotionEffects(nbt.list("active_effects", MobEffectInstance.CODEC), rand, stage);
        }

        if (Config.OVERWRITE_ENTITY_LOOT_TABLES.get() && RandomMonster.NBT_PATCHERS.containsKey(type)) {
            RandomMonster.NBT_PATCHERS.get(type).patch(nbt, rand, stage);
        }

        return new SpawnData(nbt.buildResult(), Optional.empty(), Optional.empty());
    }

}
