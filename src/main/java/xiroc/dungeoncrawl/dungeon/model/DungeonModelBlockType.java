package xiroc.dungeoncrawl.dungeon.model;

/*
 * DungeonCrawl (C) 2019 - 2020 XYROC (XIROC1337), All Rights Reserved 
 */

import net.minecraft.block.Block;
import net.minecraft.block.Blocks;

public enum DungeonModelBlockType {

	NONE, SOLID_STAIRS, /* Temporarily because of old models: */ FLOOR_STAIRS, CEILING_STAIRS /* END */, SOLID, WALL,
	WALL_LOG, FLOOR, MATERIAL_STAIRS, STAIRS, RAND_WALL_SPAWNER, CHEST, RAND_WALL_AIR, RAND_FLOOR_CHESTCOMMON_SPAWNER,
	TRAPDOOR, TORCH, TORCH_DARK, BARREL, DOOR, RAND_FLOOR_WATER, RAND_FLOOR_LAVA, RAND_BOOKSHELF_COBWEB, DISPENSER,
	RAND_COBWEB_AIR, VANILLA_WALL, MATERIAL, OTHER;

//	private static final Set<DungeonSegmentModelBlockType> SOLID_TYPES = ImmutableSet
//			.<DungeonSegmentModelBlockType>builder().add(SOLID).add(SOLID_STAIRS).build();

	public static boolean isSolid(DungeonModelBlockType type) {
//		return SOLID_TYPES.contains(type);
		return true;
	}

	public static DungeonModelBlockType get(Block block) {
		if (block == Blocks.AIR)
			return null;
		if (block == Blocks.OAK_PLANKS)
			return MATERIAL;
		if (block == Blocks.BEDROCK || block == Blocks.BARRIER)
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
		if (block == Blocks.BRICKS)
			return RAND_BOOKSHELF_COBWEB;
		if (block == Blocks.OAK_STAIRS)
			return MATERIAL_STAIRS;
		if (block == Blocks.STONE_BRICK_STAIRS)
			return SOLID_STAIRS;
		if (block == Blocks.COBBLESTONE)
			return WALL;
		if (block == Blocks.STONE_BRICKS)
			return SOLID;
		if (block == Blocks.OAK_LOG)
			return WALL_LOG;
		if (block == Blocks.GRAVEL)
			return FLOOR;
		if (block == Blocks.COBBLESTONE_STAIRS)
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
