package xiroc.dungeoncrawl.dungeon.segment;

/*
 * DungeonCrawl (C) 2019 XYROC (XIROC1337), All Rights Reserved 
 */

import net.minecraft.block.Block;
import net.minecraft.block.Blocks;

public enum DungeonSegmentModelBlockType {

	NONE, CEILING, CEILING_STAIRS, WALL, WALL_LOG, FLOOR, FLOOR_STAIRS, STAIRS, RAND_WALL_SPAWNER, CHEST_COMMON, RAND_WALL_AIR, RAND_FLOOR_CHESTCOMMON_SPAWNER, TRAPDOOR, TORCH, TORCH_DARK, LAVA, WATER, IRON_BARS, ANVIL, BARREL, FURNACE,
	WOOD, RAND_FLOOR_WATER, RAND_FLOOR_LAVA, TRIPWIRE, TRIPWIRE_HOOK, DISPENSER, OTHER, FWB_PLACEHOLDER;

	public static DungeonSegmentModelBlockType get(Block block) {
		if (block == Blocks.AIR)
			return null;
		if (block == Blocks.OAK_PLANKS)
			return WOOD;
		if (block == Blocks.BEDROCK)
			return NONE;
		if (block == Blocks.WATER)
			return WATER;
		if (block == Blocks.LAVA)
			return LAVA;
		if (block == Blocks.TRIPWIRE)
			return TRIPWIRE;
		if (block == Blocks.TRIPWIRE_HOOK)
			return TRIPWIRE_HOOK;
		if (block == Blocks.DISPENSER)
			return DISPENSER;
		if (block == Blocks.FURNACE)
			return FURNACE;
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
		if (block == Blocks.REDSTONE_WALL_TORCH)
			return TORCH_DARK;
		if (block == Blocks.IRON_BARS)
			return IRON_BARS;
		if (block == Blocks.BARREL)
			return BARREL;
		if (block == Blocks.ANVIL)
			return ANVIL;
		return NONE;
	}

}
