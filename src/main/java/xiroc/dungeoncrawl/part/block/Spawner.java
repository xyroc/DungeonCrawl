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
import xiroc.dungeoncrawl.util.IBlockPlacementHandler;

public class Spawner implements IBlockPlacementHandler {

	public static int spawnerEntities = 5;
	public static final Set<EntityType<?>> INVENTORY_ENTITIES = ImmutableSet.<EntityType<?>>builder().add(EntityType.ZOMBIE).add(EntityType.SKELETON).add(EntityType.ZOMBIE_VILLAGER).add(EntityType.HUSK).build();

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
				for (int i = 0; i < spawnerEntities; i++) {
					CompoundNBT nbt = new CompoundNBT();
					CompoundNBT spawnData = new CompoundNBT();
					spawnData.putString("id", type.getRegistryName().toString());
					ItemStack[] armor = RandomEquipment.ARMOR.roll(new Random());
					ListNBT armorList = new ListNBT();
					for (ItemStack stack : armor)
						armorList.add(stack.write(new CompoundNBT()));
					spawnData.put("ArmorItems", armorList);
					ListNBT handItems = new ListNBT();
					ItemStack mainHand = type == EntityType.SKELETON ? RandomEquipment.RANGED_WEAPON.roll(rand) : RandomEquipment.MELEE_WEAPON.roll(new Random());
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
