package xiroc.dungeoncrawl.dungeon.model;

import java.util.Set;

import com.google.common.collect.ImmutableSet;

/*
 * DungeonCrawl (C) 2019 - 2020 XYROC (XIROC1337), All Rights Reserved 
 */

import net.minecraft.block.Block;
import net.minecraft.block.Blocks;

public enum DungeonModelBlockType {

	NONE, SOLID_STAIRS, SOLID, WALL, WALL_LOG, FLOOR, MATERIAL_STAIRS, STAIRS, SPAWNER, RARE_SPAWNER, RAND_WALL_SPAWNER,
	CHEST, RARE_CHEST, RAND_WALL_AIR, RAND_FLOOR_CHEST_SPAWNER, TRAPDOOR, TORCH, TORCH_DARK, BARREL, DOOR,
	RAND_FLOOR_WATER, RAND_FLOOR_LAVA, RAND_BOOKSHELF_COBWEB, DISPENSER, RAND_COBWEB_AIR, VANILLA_WALL, MATERIAL, OTHER;

	public static final Set<DungeonModelBlockType> SOLID_TYPES = ImmutableSet.<DungeonModelBlockType>builder()
			.add(SOLID).add(SOLID_STAIRS).build();

	/*
	 * spawnerType: 0: regular, 1: rare, 2: regular_wall
	 * chestType: 0: regular, 1: rare, 2: regular_floor_spawner
	 */
	public static DungeonModelBlockType get(Block block, int spawnerType, int chestType) {
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
		if (block == Blocks.SPAWNER) {
			switch (spawnerType) {
			case 0:
				return SPAWNER;
			case 1:
				return RARE_SPAWNER;
			case 2:
				return RAND_WALL_SPAWNER;
			default:
				return SPAWNER;
			}
		}
		if (block == Blocks.CHEST)
			switch (chestType) {
			case 0:
				return CHEST;
			case 1:
				return RARE_CHEST;
			case 2:
				return RAND_FLOOR_CHEST_SPAWNER;
			default:
				return CHEST;
			}
		if (block == Blocks.OAK_TRAPDOOR)
			return TRAPDOOR;
		if (block == Blocks.BARREL)
			return BARREL;
		if (block == Blocks.STONE_BRICK_WALL)
			return VANILLA_WALL;
		return OTHER;
	}

}
