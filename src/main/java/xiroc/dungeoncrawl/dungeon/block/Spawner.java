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
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IWorld;
import xiroc.dungeoncrawl.DungeonCrawl;
import xiroc.dungeoncrawl.config.Config;
import xiroc.dungeoncrawl.dungeon.monster.RandomEquipment;
import xiroc.dungeoncrawl.dungeon.monster.RandomMonster;
import xiroc.dungeoncrawl.dungeon.monster.RandomPotionEffect;
import xiroc.dungeoncrawl.dungeon.monster.SpawnRates;
import xiroc.dungeoncrawl.dungeon.treasure.RandomItems;
import xiroc.dungeoncrawl.dungeon.treasure.Treasure;
import xiroc.dungeoncrawl.util.IBlockPlacementHandler;

import javax.annotation.Nullable;
import java.util.Random;
import java.util.Set;

public class Spawner implements IBlockPlacementHandler {

    public static final EntityType<?>[] ENTITIES = new EntityType<?>[]{EntityType.ZOMBIE, EntityType.SKELETON,
            EntityType.SPIDER, EntityType.CAVE_SPIDER, EntityType.HUSK};
    public static final EntityType<?>[] ENTITIES_RARE = new EntityType<?>[]{EntityType.SILVERFISH, EntityType.CREEPER,
            EntityType.STRAY, EntityType.ENDERMAN};

    public static final Set<EntityType<?>> INVENTORY_ENTITIES = ImmutableSet.<EntityType<?>>builder()
            .add(EntityType.ZOMBIE).add(EntityType.SKELETON).add(EntityType.HUSK).add(EntityType.STRAY).add(EntityType.WITHER_SKELETON).build();
    public static final Set<EntityType<?>> RANGED_INVENTORY_ENTITIES = ImmutableSet.<EntityType<?>>builder()
            .add(EntityType.SKELETON).add(EntityType.STRAY).build();

    @Override
    public void placeBlock(IWorld world, BlockState state, BlockPos pos, Random rand, Treasure.Type treasureType,
                           int theme, int stage) {
        world.setBlockState(pos, Blocks.SPAWNER.getDefaultState(), 3);
        TileEntity tileentity = world.getTileEntity(pos);
        if (tileentity instanceof MobSpawnerTileEntity) {
            MobSpawnerTileEntity tile = (MobSpawnerTileEntity) tileentity;
            EntityType<?> type = RandomMonster.randomMonster(rand, stage);
            tile.getSpawnerBaseLogic().setEntityType(type);
            if (!Config.VANILLA_SPAWNERS.get() && INVENTORY_ENTITIES.contains(type)) {
                CompoundNBT spawnerNBT = tile.getSpawnerBaseLogic().write(new CompoundNBT());
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
                spawnerNBT.putShort("SpawnCount", (short) SpawnRates.getAmount(stage).generateInt(rand));
                spawnerNBT.putShort("RequiredPlayerRange", (short) 8);
                tile.getSpawnerBaseLogic().read(spawnerNBT);
            }
        } else {
            DungeonCrawl.LOGGER.error("Failed to fetch a mob spawner at ({}, {}, {})", pos.getX(), pos.getY(),
                    pos.getZ());
        }
    }

    public static CompoundNBT createSpawnData(@Nullable EntityType<?> type, @Nullable CompoundNBT spawnData,
                                              Random rand, int stage) {
        if (type == null)
            type = getRandomEntityType(rand);
        if (spawnData == null)
            spawnData = new CompoundNBT();

        spawnData.putString("id", type.getRegistryName().toString());
        if (INVENTORY_ENTITIES.contains(type)) {
            ItemStack[] armor = RandomEquipment.createArmor(rand, stage);
            ListNBT armorList = new ListNBT();
            for (ItemStack stack : armor) {
                armorList.add(stack.write(new CompoundNBT()));
            }
            if (armorList.size() > 0) {
                spawnData.put("ArmorItems", armorList);
            }

            ListNBT handItems = new ListNBT();
            ItemStack mainHand = RANGED_INVENTORY_ENTITIES.contains(type)
                    ? RandomEquipment.getRangedWeapon(WeightedRandomBlock.RANDOM, stage)
                    : RandomEquipment.getMeleeWeapon(WeightedRandomBlock.RANDOM, stage);

            if (mainHand != ItemStack.EMPTY) {
                handItems.add(mainHand.write(new CompoundNBT()));
            }

            handItems.add(rand.nextDouble() < Config.SHIELD_PROBABILITY.get()
                    ? RandomItems.createShield(rand, stage).write(new CompoundNBT())
                    : ItemStack.EMPTY.write(new CompoundNBT()));

            spawnData.put("HandItems", handItems);

            if (!Config.NATURAL_DESPAWN.get()) {
                spawnData.putBoolean("PersistenceRequired", true);
            }

            ListNBT potionEffects = RandomPotionEffect.createPotionEffects(rand, stage);
            if (potionEffects != null) {
                spawnData.put("ActiveEffects", potionEffects);
            }
        }

        if (RandomMonster.NBT_PATCHERS.containsKey(type)) {
            RandomMonster.NBT_PATCHERS.get(type).patch(spawnData, rand, stage);
        }
        return spawnData;
    }

    public static void equipMonster(MonsterEntity entity, Random rand, int stage) {
        if (INVENTORY_ENTITIES.contains(entity.getType())) {
            ItemStack[] armor = RandomEquipment.createArmor(rand, stage);
            entity.setItemStackToSlot(EquipmentSlotType.FEET, armor[0]);
            entity.setItemStackToSlot(EquipmentSlotType.LEGS, armor[1]);
            entity.setItemStackToSlot(EquipmentSlotType.CHEST, armor[2]);
            entity.setItemStackToSlot(EquipmentSlotType.HEAD, armor[3]);

            ItemStack mainHand = RANGED_INVENTORY_ENTITIES.contains(entity.getType())
                    ? RandomEquipment.getRangedWeapon(WeightedRandomBlock.RANDOM, stage)
                    : RandomEquipment.getMeleeWeapon(WeightedRandomBlock.RANDOM, stage);
            entity.setItemStackToSlot(EquipmentSlotType.MAINHAND, mainHand);

            if (rand.nextDouble() < Config.SHIELD_PROBABILITY.get())
                entity.setItemStackToSlot(EquipmentSlotType.OFFHAND, RandomItems.createShield(rand, stage));

            RandomPotionEffect.applyPotionEffects(entity, rand, stage);

            if (!Config.NATURAL_DESPAWN.get()) {
                entity.enablePersistence();
            }

            if (RandomMonster.NBT_PATCHERS.containsKey(entity.getType())) {
                CompoundNBT nbt = new CompoundNBT();
                entity.writeAdditional(nbt);
                RandomMonster.NBT_PATCHERS.get(entity.getType()).patch(nbt, rand, stage);
                entity.readAdditional(nbt);
            }
        }
    }

    public static EntityType<?> getRandomEntityType(Random rand) {
        return rand.nextFloat() < 0.04 ? ENTITIES_RARE[rand.nextInt(ENTITIES_RARE.length)]
                : ENTITIES[rand.nextInt(ENTITIES.length)];
    }

}
