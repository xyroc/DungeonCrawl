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
import net.minecraft.util.WeightedSpawnerEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IWorld;
import xiroc.dungeoncrawl.DungeonCrawl;
import xiroc.dungeoncrawl.dungeon.monster.RandomEquipment;
import xiroc.dungeoncrawl.util.IBlockPlacementHandler;

public class Spawner implements IBlockPlacementHandler {

	public static final Set<EntityType<?>> INVENTORY_ENTITIES = ImmutableSet.<EntityType<?>>builder().add(EntityType.ZOMBIE).add(EntityType.SKELETON).add(EntityType.ZOMBIE_VILLAGER).add(EntityType.HUSK).build();

	@Override
	public void setupBlock(IWorld world, BlockState state, BlockPos pos, Random rand, int lootLevel) {
		world.setBlockState(pos, Blocks.SPAWNER.getDefaultState(), 2);
		TileEntity tileentity = world.getTileEntity(pos);
		if (tileentity instanceof MobSpawnerTileEntity) {
			MobSpawnerTileEntity tile = (MobSpawnerTileEntity) tileentity;
			EntityType<?> type = getRandomEntityType(rand);
			tile.getSpawnerBaseLogic().setEntityType(type);
			if (INVENTORY_ENTITIES.contains(type)) {
				CompoundNBT nbt = tile.getSpawnerBaseLogic().write(new CompoundNBT());
				CompoundNBT entity = new CompoundNBT();
				CompoundNBT spawnData = new CompoundNBT();
				entity.putString("id", type.getRegistryName().toString());
				ItemStack[] armor = RandomEquipment.ARMOR.roll(new Random());
				ListNBT armorList = new ListNBT();
				for (ItemStack stack : armor)
					armorList.add(stack.write(new CompoundNBT()));
				spawnData.put("ArmorItems", armorList);
				ListNBT handItems = new ListNBT();
				ItemStack mainHand = RandomEquipment.MELEE_WEAPON.roll(new Random());
				handItems.add(mainHand.write(new CompoundNBT()));
				handItems.add(ItemStack.EMPTY.write(new CompoundNBT()));
				spawnData.put("HandItems", handItems);
				nbt.put("SpawnData", spawnData);
				nbt.put("Entity", entity);
				nbt.putInt("Weight", 1);
				WeightedSpawnerEntity wse = new WeightedSpawnerEntity(nbt);
				tile.getSpawnerBaseLogic().setNextSpawnData(wse);
			}
		} else {
			DungeonCrawl.LOGGER.error("Failed to fetch mob spawner entity at ({}, {}, {})", pos.getX(), pos.getY(), pos.getZ());
		}
	}

	public static EntityType<?> getRandomEntityType(Random rand) {
		switch (rand.nextInt(6)) {
		case 0:
			return EntityType.ZOMBIE;
		case 1:
			return EntityType.SKELETON;
		case 2:
			return EntityType.SPIDER;
		case 3:
			return EntityType.CAVE_SPIDER;
		case 4:
			return EntityType.ZOMBIE_VILLAGER;
		case 5:
			return EntityType.HUSK;
		}
		return null;
	}

}
