package xiroc.dungeoncrawl.dungeon.segment;

/*
 * DungeonCrawl (C) 2019 XYROC (XIROC1337), All Rights Reserved 
 */

import net.minecraft.block.Block;
import net.minecraft.block.Blocks;

public enum DungeonSegmentModelBlockType {

	NONE, CEILING, CEILING_STAIRS, WALL, WALL_LOG, FLOOR, FLOOR_STAIRS, STAIRS, RAND_WALL_SPAWNER, CHEST, RAND_WALL_AIR,
	RAND_FLOOR_CHESTCOMMON_SPAWNER, TRAPDOOR, TORCH, TORCH_DARK, BARREL, DOOR, RAND_FLOOR_WATER, RAND_FLOOR_LAVA,
	DISPENSER, RAND_COBWEB_AIR, VANILLA_WALL, MATERIAL, OTHER;

	public static DungeonSegmentModelBlockType get(Block block) {
		if (block == Blocks.AIR)
			return null;
		if (block == Blocks.OAK_PLANKS)
			return MATERIAL;
		if (block == Blocks.BEDROCK)
			return NONE;
		if (block == Blocks.OAK_DOOR)
			return DOOR;
		if (block == Blocks.COBWEB)
			return RAND_COBWEB_AIR;
		if (block == Blocks.DISPENSER)
			return DISPENSER;
		if (block == Blocks.SOUL_SAND)
			return RAND_FLOOR_LAVA;
		if (block == Blocks.CRACKED_STONE_BRICKS)
			return RAND_WALL_AIR;
		if (block == Blocks.CLAY)
			return RAND_FLOOR_WATER;
		if (block == Blocks.OAK_STAIRS)
			return FLOOR_STAIRS;
		if (block == Blocks.STONE_BRICK_STAIRS)
			return CEILING_STAIRS;
		if (block == Blocks.STONE_BRICKS)
			return WALL;
		if (block == Blocks.OAK_LOG)
			return WALL_LOG;
		if (block == Blocks.GRAVEL)
			return FLOOR;
		if (block == Blocks.BRICK_STAIRS)
			return STAIRS;
		if (block == Blocks.SPAWNER)
			return RAND_WALL_SPAWNER;
		if (block == Blocks.CHEST)
			return RAND_FLOOR_CHESTCOMMON_SPAWNER;
		if (block == Blocks.OAK_TRAPDOOR)
			return TRAPDOOR;
		if (block == Blocks.BARREL)
			return BARREL;
		if (block == Blocks.STONE_BRICK_WALL)
			return VANILLA_WALL;
		return OTHER;
	}

}
