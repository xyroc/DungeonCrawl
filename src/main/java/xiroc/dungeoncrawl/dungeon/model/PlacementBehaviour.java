package xiroc.dungeoncrawl.dungeon.model;

/*
 * DungeonCrawl (C) 2019 - 2020 XYROC (XIROC1337), All Rights Reserved 
 */

import java.util.Random;

import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IWorld;

public enum PlacementBehaviour {

	NON_SOLID((world, pos, rand, rx, ry, rz) -> false), RANDOM_IF_SOLID_NEARBY((world, pos, rand, rx, ry, rz) -> {
		if (world.getBlockState(pos).isSolid() || world.getBlockState(pos.north()).isSolid()
				|| world.getBlockState(pos.east()).isSolid() || world.getBlockState(pos.south()).isSolid()
				|| world.getBlockState(pos.west()).isSolid()) {
			return rand.nextFloat() < 0.5;
		} else {
			return false;
		}
	}), SOLID((world, pos, rand, rx, ry, rz) -> true);

	public final PlacementFunction function;

	private PlacementBehaviour(PlacementFunction function) {
		this.function = function;
	}

	public static interface PlacementFunction {

		boolean isSolid(IWorld world, BlockPos pos, Random rand, int relativeX, int relativeY, int relativeZ);

	}

}
