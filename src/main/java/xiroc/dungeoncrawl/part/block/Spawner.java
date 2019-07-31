package xiroc.dungeoncrawl.part.block;

import java.util.Random;

import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.entity.EntityType;
import net.minecraft.tileentity.MobSpawnerTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IWorld;
import xiroc.dungeoncrawl.DungeonCrawl;
import xiroc.dungeoncrawl.util.IBlockPlacementHandler;

public class Spawner implements IBlockPlacementHandler {

	@Override
	public void setupBlock(IWorld world, BlockState state, BlockPos pos, Random rand, int lootLevel) {
		world.setBlockState(pos, Blocks.SPAWNER.getDefaultState(), 2);
		TileEntity tileentity = world.getTileEntity(pos);
		if (tileentity instanceof MobSpawnerTileEntity) {
			((MobSpawnerTileEntity) tileentity).getSpawnerBaseLogic().setEntityType(getRandomEntityType(rand));
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
