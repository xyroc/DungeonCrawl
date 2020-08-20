package xiroc.dungeoncrawl.dungeon.block;

/*
 * DungeonCrawl (C) 2019 - 2020 XYROC (XIROC1337), All Rights Reserved
 */

import com.google.common.collect.ImmutableSet;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.monster.MonsterEntity;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.tileentity.MobSpawnerTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IWorld;
import xiroc.dungeoncrawl.DungeonCrawl;
import xiroc.dungeoncrawl.config.Config;
import xiroc.dungeoncrawl.dungeon.misc.Banner;
import xiroc.dungeoncrawl.dungeon.monster.RandomEquipment;
import xiroc.dungeoncrawl.dungeon.monster.RandomMonster;
import xiroc.dungeoncrawl.dungeon.monster.RandomPotionEffect;
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
                    nbt.putShort("MinSpawnDelay", (short) 200);
                    nbt.putShort("MaxSpawnDelay", (short) 300);
                    //nbt.putShort("SpawnCount", (short) 1);
                    if (i == 0)
                        spawnerNBT.put("SpawnData", spawnData);
                    potentialSpawns.add(nbt);
                }
                spawnerNBT.put("SpawnPotentials", potentialSpawns);
                tile.getSpawnerBaseLogic().read(spawnerNBT);
            }
        } else {
            DungeonCrawl.LOGGER.error("Failed to fetch mob spawner entity at ({}, {}, {})", pos.getX(), pos.getY(),
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
                if (stack != ItemStack.EMPTY)
                    armorList.add(stack.write(new CompoundNBT()));
            }
            if (armorList.size() > 0)
                spawnData.put("ArmorItems", armorList);
            ListNBT handItems = new ListNBT();

            boolean swap = rand.nextFloat() < 0.1;
            ItemStack mainHand = swap || RANGED_INVENTORY_ENTITIES.contains(type)
                    ? RandomEquipment.getRangedWeapon(WeightedRandomBlock.RANDOM, stage)
                    : RandomEquipment.getMeleeWeapon(WeightedRandomBlock.RANDOM, stage);
            if (mainHand != ItemStack.EMPTY)
                handItems.add(mainHand.write(new CompoundNBT()));
            handItems.add(rand.nextDouble() < Config.SHIELD_PROBABILITY.get()
                    ? Banner.createShield(rand).write(new CompoundNBT())
                    : ItemStack.EMPTY.write(new CompoundNBT()));
            spawnData.put("HandItems", handItems);

            ListNBT potionEffects = RandomPotionEffect.createPotionEffects(rand, stage);
            if (potionEffects != null) {
                spawnData.put("ActiveEffects", potionEffects);
            }
        }

        if (RandomMonster.NBT_PROCESSORS.containsKey(type)) {
            RandomMonster.NBT_PROCESSORS.get(type).process(spawnData, rand, stage);
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
                entity.setItemStackToSlot(EquipmentSlotType.OFFHAND, Banner.createShield(rand));

            RandomPotionEffect.applyPotionEffects(entity, rand, stage);

            if (RandomMonster.NBT_PROCESSORS.containsKey(entity.getType())) {
                CompoundNBT nbt = new CompoundNBT();
                entity.writeAdditional(nbt);
                RandomMonster.NBT_PROCESSORS.get(entity.getType()).process(nbt, rand, stage);
                entity.readAdditional(nbt);
            }
        }
    }

    public static EntityType<?> getRandomEntityType(Random rand) {
        return rand.nextFloat() < 0.04 ? ENTITIES_RARE[rand.nextInt(ENTITIES_RARE.length)]
                : ENTITIES[rand.nextInt(ENTITIES.length)];
    }

}
