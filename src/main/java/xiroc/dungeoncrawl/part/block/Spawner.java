package xiroc.dungeoncrawl.part.block;

/*
 * DungeonCrawl (C) 2019 XYROC (XIROC1337), All Rights Reserved 
 */

import java.util.Random;
import java.util.Set;

import com.google.common.collect.ImmutableSet;

import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.entity.EntityType;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.tileentity.MobSpawnerTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IWorld;
import xiroc.dungeoncrawl.DungeonCrawl;
import xiroc.dungeoncrawl.dungeon.monster.RandomEquipment;
import xiroc.dungeoncrawl.util.Config;
import xiroc.dungeoncrawl.util.IBlockPlacementHandler;

public class Spawner implements IBlockPlacementHandler {
	
	public static final EntityType<?>[] ENTITIES = new EntityType<?>[] { EntityType.ZOMBIE, EntityType.SKELETON, EntityType.ZOMBIE_VILLAGER, EntityType.SPIDER, EntityType.CAVE_SPIDER, EntityType.HUSK };
	public static final EntityType<?>[] ENTITIES_RARE = new EntityType<?>[] { EntityType.SILVERFISH, EntityType.CREEPER, EntityType.WITCH, EntityType.STRAY, EntityType.ENDERMAN };
	public static final EntityType<?>[] ENTITIES_SPECIAL = new EntityType<?>[] { EntityType.BLAZE, EntityType.RAVAGER }; // Unused

	public static final Set<EntityType<?>> INVENTORY_ENTITIES = ImmutableSet.<EntityType<?>>builder().add(EntityType.ZOMBIE).add(EntityType.SKELETON).add(EntityType.ZOMBIE_VILLAGER).add(EntityType.HUSK).add(EntityType.STRAY).build();
	public static final Set<EntityType<?>> RANGED_INVENTORY_ENTITIES = ImmutableSet.<EntityType<?>>builder().add(EntityType.SKELETON).add(EntityType.STRAY).build();

	@Override
	public void setupBlock(IWorld world, BlockState state, BlockPos pos, Random rand, int stage) {
		world.setBlockState(pos, Blocks.SPAWNER.getDefaultState(), 2);
		TileEntity tileentity = world.getTileEntity(pos);
		if (tileentity instanceof MobSpawnerTileEntity) {
			MobSpawnerTileEntity tile = (MobSpawnerTileEntity) tileentity;
			EntityType<?> type = getRandomEntityType(rand);
			tile.getSpawnerBaseLogic().setEntityType(type);
			if (INVENTORY_ENTITIES.contains(type)) {
				CompoundNBT spawnerNBT = tile.getSpawnerBaseLogic().write(new CompoundNBT());
				ListNBT potentialSpawns = new ListNBT();
				for (int i = 0; i < Config.SPAWNER_ENTITIES.get(); i++) {
					CompoundNBT nbt = new CompoundNBT();
					CompoundNBT spawnData = new CompoundNBT();
					spawnData.putString("id", type.getRegistryName().toString());
					ItemStack[] armor = getArmor(rand, stage);
					ListNBT armorList = new ListNBT();
					for (ItemStack stack : armor)
						armorList.add(stack.write(new CompoundNBT()));
					spawnData.put("ArmorItems", armorList);
					ListNBT handItems = new ListNBT();
					ItemStack mainHand = RANGED_INVENTORY_ENTITIES.contains(type) ? RandomEquipment.getRangedWeapon(new Random(), stage) : RandomEquipment.getMeleeWeapon(new Random(), stage);
					handItems.add(mainHand.write(new CompoundNBT()));
					handItems.add(ItemStack.EMPTY.write(new CompoundNBT()));
					spawnData.put("HandItems", handItems);
					nbt.put("Entity", spawnData);
					nbt.putInt("Weight", 1);
					if (i == 0)
						spawnerNBT.put("SpawnData", spawnData);
					potentialSpawns.add(nbt);
				}
				spawnerNBT.put("SpawnPotentials", potentialSpawns);
				tile.getSpawnerBaseLogic().read(spawnerNBT);
			}
		} else {
			DungeonCrawl.LOGGER.error("Failed to fetch mob spawner entity at ({}, {}, {})", pos.getX(), pos.getY(), pos.getZ());
		}
	}

	public static ItemStack[] getArmor(Random rand, int stage) {
		switch (stage) {
		case 0:
			return RandomEquipment.ARMOR_1.roll(rand);
		case 1:
			return RandomEquipment.ARMOR_2.roll(rand);
		case 2:
			return RandomEquipment.ARMOR_3.roll(rand);
		default:
			return RandomEquipment.ARMOR_1.roll(rand);
		}
	}

	public static EntityType<?> getRandomEntityType(Random rand) {
		return rand.nextFloat() < 0.04 ? ENTITIES_RARE[rand.nextInt(ENTITIES_RARE.length)] : ENTITIES[rand.nextInt(ENTITIES.length)];
	}

}
