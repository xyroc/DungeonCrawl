package xiroc.dungeoncrawl.dungeon.segment;

import java.io.IOException;

import net.minecraft.resources.IResourceManager;
import net.minecraft.state.properties.Half;
import net.minecraft.util.Direction;
import xiroc.dungeoncrawl.DungeonCrawl;
import xiroc.dungeoncrawl.util.DungeonSegmentModelReader;

public class DungeonSegmentModelRegistry {

	public static boolean LOADED = false;

	public static final DungeonSegmentModelBlock NONE = new DungeonSegmentModelBlock(DungeonSegmentModelBlockType.NONE, null, false);

	public static final DungeonSegmentModelBlock WATER = new DungeonSegmentModelBlock(DungeonSegmentModelBlockType.NONE, null, false);
	public static final DungeonSegmentModelBlock LAVA = new DungeonSegmentModelBlock(DungeonSegmentModelBlockType.NONE, null, false);

	public static final DungeonSegmentModelBlock TORCH_DARK_NORTH = new DungeonSegmentModelBlock(DungeonSegmentModelBlockType.TORCH_DARK, Direction.NORTH, false);
	public static final DungeonSegmentModelBlock TORCH_DARK_EAST = new DungeonSegmentModelBlock(DungeonSegmentModelBlockType.TORCH_DARK, Direction.EAST, false);
	public static final DungeonSegmentModelBlock TORCH_DARK_SOUTH = new DungeonSegmentModelBlock(DungeonSegmentModelBlockType.TORCH_DARK, Direction.SOUTH, false);
	public static final DungeonSegmentModelBlock TORCH_DARK_WEST = new DungeonSegmentModelBlock(DungeonSegmentModelBlockType.TORCH_DARK, Direction.WEST, false);

	public static final DungeonSegmentModelBlock CEILING = new DungeonSegmentModelBlock(DungeonSegmentModelBlockType.CEILING, null, false);
	public static final DungeonSegmentModelBlock CEILING_STAIRS_NORTH = new DungeonSegmentModelBlock(DungeonSegmentModelBlockType.CEILING_STAIRS, Direction.NORTH, false);
	public static final DungeonSegmentModelBlock CEILING_STAIRS_EAST = new DungeonSegmentModelBlock(DungeonSegmentModelBlockType.CEILING_STAIRS, Direction.EAST, false);
	public static final DungeonSegmentModelBlock CEILING_STAIRS_SOUTH = new DungeonSegmentModelBlock(DungeonSegmentModelBlockType.CEILING_STAIRS, Direction.SOUTH, false);
	public static final DungeonSegmentModelBlock CEILING_STAIRS_WEST = new DungeonSegmentModelBlock(DungeonSegmentModelBlockType.CEILING_STAIRS, Direction.WEST, false);
	public static final DungeonSegmentModelBlock CEILING_STAIRS_NORTH_UD = new DungeonSegmentModelBlock(DungeonSegmentModelBlockType.CEILING_STAIRS, Direction.NORTH, true);
	public static final DungeonSegmentModelBlock CEILING_STAIRS_EAST_UD = new DungeonSegmentModelBlock(DungeonSegmentModelBlockType.CEILING_STAIRS, Direction.EAST, true);
	public static final DungeonSegmentModelBlock CEILING_STAIRS_SOUTH_UD = new DungeonSegmentModelBlock(DungeonSegmentModelBlockType.CEILING_STAIRS, Direction.SOUTH, true);
	public static final DungeonSegmentModelBlock CEILING_STAIRS_WEST_UD = new DungeonSegmentModelBlock(DungeonSegmentModelBlockType.CEILING_STAIRS, Direction.WEST, true);

	public static final DungeonSegmentModelBlock WALL = new DungeonSegmentModelBlock(DungeonSegmentModelBlockType.WALL, null, false);
	public static final DungeonSegmentModelBlock WALL_LOG = new DungeonSegmentModelBlock(DungeonSegmentModelBlockType.WALL_LOG, Direction.UP, false);

	public static final DungeonSegmentModelBlock FLOOR = new DungeonSegmentModelBlock(DungeonSegmentModelBlockType.FLOOR, null, false);
	public static final DungeonSegmentModelBlock FLOOR_STAIRS_NORTH = new DungeonSegmentModelBlock(DungeonSegmentModelBlockType.FLOOR_STAIRS, Direction.NORTH, false);
	public static final DungeonSegmentModelBlock FLOOR_STAIRS_EAST = new DungeonSegmentModelBlock(DungeonSegmentModelBlockType.FLOOR_STAIRS, Direction.EAST, false);
	public static final DungeonSegmentModelBlock FLOOR_STAIRS_SOUTH = new DungeonSegmentModelBlock(DungeonSegmentModelBlockType.FLOOR_STAIRS, Direction.SOUTH, false);
	public static final DungeonSegmentModelBlock FLOOR_STAIRS_WEST = new DungeonSegmentModelBlock(DungeonSegmentModelBlockType.FLOOR_STAIRS, Direction.WEST, false);
	public static final DungeonSegmentModelBlock FLOOR_STAIRS_NORTH_UD = new DungeonSegmentModelBlock(DungeonSegmentModelBlockType.FLOOR_STAIRS, Direction.NORTH, true);
	public static final DungeonSegmentModelBlock FLOOR_STAIRS_EAST_UD = new DungeonSegmentModelBlock(DungeonSegmentModelBlockType.FLOOR_STAIRS, Direction.EAST, true);
	public static final DungeonSegmentModelBlock FLOOR_STAIRS_SOUTH_UD = new DungeonSegmentModelBlock(DungeonSegmentModelBlockType.FLOOR_STAIRS, Direction.SOUTH, true);
	public static final DungeonSegmentModelBlock FLOOR_STAIRS_WEST_UD = new DungeonSegmentModelBlock(DungeonSegmentModelBlockType.FLOOR_STAIRS, Direction.WEST, true);

	public static final DungeonSegmentModelBlock SPAWNER = new DungeonSegmentModelBlock(DungeonSegmentModelBlockType.SPAWNER, null, false);
	public static final DungeonSegmentModelBlock CHEST_COMMON_NORTH = new DungeonSegmentModelBlock(DungeonSegmentModelBlockType.CHEST_COMMON, Direction.NORTH, false);
	public static final DungeonSegmentModelBlock CHEST_COMMON_EAST = new DungeonSegmentModelBlock(DungeonSegmentModelBlockType.CHEST_COMMON, Direction.EAST, false);
	public static final DungeonSegmentModelBlock CHEST_COMMON_SOUTH = new DungeonSegmentModelBlock(DungeonSegmentModelBlockType.CHEST_COMMON, Direction.SOUTH, false);
	public static final DungeonSegmentModelBlock CHEST_COMMON_WEST = new DungeonSegmentModelBlock(DungeonSegmentModelBlockType.CHEST_COMMON, Direction.WEST, false);
	public static final DungeonSegmentModelBlock RND_CC_FLOOR_SPWN_NORTH = new DungeonSegmentModelBlock(DungeonSegmentModelBlockType.RAND_FLOOR_CHESTCOMMON_SPAWNER, Direction.NORTH, false);
	public static final DungeonSegmentModelBlock RND_CC_FLOOR_SPWN_EAST = new DungeonSegmentModelBlock(DungeonSegmentModelBlockType.RAND_FLOOR_CHESTCOMMON_SPAWNER, Direction.EAST, false);
	public static final DungeonSegmentModelBlock RND_CC_FLOOR_SPWN_SOUTH = new DungeonSegmentModelBlock(DungeonSegmentModelBlockType.RAND_FLOOR_CHESTCOMMON_SPAWNER, Direction.SOUTH, false);
	public static final DungeonSegmentModelBlock RND_CC_FLOOR_SPWN_WEST = new DungeonSegmentModelBlock(DungeonSegmentModelBlockType.RAND_FLOOR_CHESTCOMMON_SPAWNER, Direction.WEST, false);

	public static final DungeonSegmentModelTrapDoorBlock TRAPDOOR_NORTH = new DungeonSegmentModelTrapDoorBlock(DungeonSegmentModelBlockType.TRAPDOOR, Direction.NORTH, true, Half.BOTTOM, false);
	public static final DungeonSegmentModelTrapDoorBlock TRAPDOOR_EAST = new DungeonSegmentModelTrapDoorBlock(DungeonSegmentModelBlockType.TRAPDOOR, Direction.EAST, true, Half.BOTTOM, false);
	public static final DungeonSegmentModelTrapDoorBlock TRAPDOOR_SOUTH = new DungeonSegmentModelTrapDoorBlock(DungeonSegmentModelBlockType.TRAPDOOR, Direction.SOUTH, true, Half.BOTTOM, false);
	public static final DungeonSegmentModelTrapDoorBlock TRAPDOOR_WEST = new DungeonSegmentModelTrapDoorBlock(DungeonSegmentModelBlockType.TRAPDOOR, Direction.WEST, true, Half.BOTTOM, false);

	public static final DungeonSegmentModelTrapDoorBlock TRAPDOOR_CLOSED_NORTH = new DungeonSegmentModelTrapDoorBlock(DungeonSegmentModelBlockType.TRAPDOOR, Direction.NORTH, false, Half.BOTTOM, false);
	public static final DungeonSegmentModelTrapDoorBlock TRAPDOOR_CLOSED_EAST = new DungeonSegmentModelTrapDoorBlock(DungeonSegmentModelBlockType.TRAPDOOR, Direction.EAST, false, Half.BOTTOM, false);
	public static final DungeonSegmentModelTrapDoorBlock TRAPDOOR_CLOSED_SOUTH = new DungeonSegmentModelTrapDoorBlock(DungeonSegmentModelBlockType.TRAPDOOR, Direction.SOUTH, false, Half.BOTTOM, false);
	public static final DungeonSegmentModelTrapDoorBlock TRAPDOOR_CLOSED_WEST = new DungeonSegmentModelTrapDoorBlock(DungeonSegmentModelBlockType.TRAPDOOR, Direction.WEST, false, Half.BOTTOM, false);

	public static final DungeonSegmentModelFourWayBlock IRON_BARS_NORTH = new DungeonSegmentModelFourWayBlock(DungeonSegmentModelBlockType.IRON_BARS, true, false, false, false, false);
	public static final DungeonSegmentModelFourWayBlock IRON_BARS_NORTH_EAST = new DungeonSegmentModelFourWayBlock(DungeonSegmentModelBlockType.IRON_BARS, true, true, false, false, false);
	public static final DungeonSegmentModelFourWayBlock IRON_BARS_NORTH_EAST_SOUTH = new DungeonSegmentModelFourWayBlock(DungeonSegmentModelBlockType.IRON_BARS, true, true, true, false, false);
	public static final DungeonSegmentModelFourWayBlock IRON_BARS_NORTH_EAST_SOUTH_WEST = new DungeonSegmentModelFourWayBlock(DungeonSegmentModelBlockType.IRON_BARS, true, true, true, true, false);
	public static final DungeonSegmentModelFourWayBlock IRON_BARS_NORTH_EAST_WEST = new DungeonSegmentModelFourWayBlock(DungeonSegmentModelBlockType.IRON_BARS, true, true, false, true, false);
	public static final DungeonSegmentModelFourWayBlock IRON_BARS_NORTH_SOUTH = new DungeonSegmentModelFourWayBlock(DungeonSegmentModelBlockType.IRON_BARS, true, false, true, false, false);
	public static final DungeonSegmentModelFourWayBlock IRON_BARS_NORTH_SOUTH_WEST = new DungeonSegmentModelFourWayBlock(DungeonSegmentModelBlockType.IRON_BARS, true, false, true, true, false);
	public static final DungeonSegmentModelFourWayBlock IRON_BARS_NORTH_WEST = new DungeonSegmentModelFourWayBlock(DungeonSegmentModelBlockType.IRON_BARS, true, false, false, true, false);
	public static final DungeonSegmentModelFourWayBlock IRON_BARS_EAST = new DungeonSegmentModelFourWayBlock(DungeonSegmentModelBlockType.IRON_BARS, false, true, false, false, false);
	public static final DungeonSegmentModelFourWayBlock IRON_BARS_EAST_SOUTH = new DungeonSegmentModelFourWayBlock(DungeonSegmentModelBlockType.IRON_BARS, false, true, true, false, false);
	public static final DungeonSegmentModelFourWayBlock IRON_BARS_EAST_SOUTH_WEST = new DungeonSegmentModelFourWayBlock(DungeonSegmentModelBlockType.IRON_BARS, false, true, true, true, false);
	public static final DungeonSegmentModelFourWayBlock IRON_BARS_EAST_WEST = new DungeonSegmentModelFourWayBlock(DungeonSegmentModelBlockType.IRON_BARS, false, true, false, true, false);
	public static final DungeonSegmentModelFourWayBlock IRON_BARS_SOUTH = new DungeonSegmentModelFourWayBlock(DungeonSegmentModelBlockType.IRON_BARS, false, false, true, false, false);
	public static final DungeonSegmentModelFourWayBlock IRON_BARS_SOUTH_WEST = new DungeonSegmentModelFourWayBlock(DungeonSegmentModelBlockType.IRON_BARS, false, false, true, true, false);
	public static final DungeonSegmentModelFourWayBlock IRON_BARS_WEST = new DungeonSegmentModelFourWayBlock(DungeonSegmentModelBlockType.IRON_BARS, false, false, false, true, false);

	public static final DungeonSegmentModelFourWayBlock IRON_BARS_NORTH_WATERLOGGED = new DungeonSegmentModelFourWayBlock(DungeonSegmentModelBlockType.IRON_BARS, true, false, false, false, true);
	public static final DungeonSegmentModelFourWayBlock IRON_BARS_NORTH_EAST_WATERLOGGED = new DungeonSegmentModelFourWayBlock(DungeonSegmentModelBlockType.IRON_BARS, true, true, false, false, true);
	public static final DungeonSegmentModelFourWayBlock IRON_BARS_NORTH_EAST_SOUTH_WATERLOGGED = new DungeonSegmentModelFourWayBlock(DungeonSegmentModelBlockType.IRON_BARS, true, true, true, false, true);
	public static final DungeonSegmentModelFourWayBlock IRON_BARS_NORTH_EAST_SOUTH_WEST_WATERLOGGED = new DungeonSegmentModelFourWayBlock(DungeonSegmentModelBlockType.IRON_BARS, true, true, true, true, true);
	public static final DungeonSegmentModelFourWayBlock IRON_BARS_NORTH_EAST_WEST_WATERLOGGED = new DungeonSegmentModelFourWayBlock(DungeonSegmentModelBlockType.IRON_BARS, true, true, false, true, true);
	public static final DungeonSegmentModelFourWayBlock IRON_BARS_NORTH_SOUTH_WATERLOGGED = new DungeonSegmentModelFourWayBlock(DungeonSegmentModelBlockType.IRON_BARS, true, false, true, false, true);
	public static final DungeonSegmentModelFourWayBlock IRON_BARS_NORTH_SOUTH_WEST_WATERLOGGED = new DungeonSegmentModelFourWayBlock(DungeonSegmentModelBlockType.IRON_BARS, true, false, true, true, true);
	public static final DungeonSegmentModelFourWayBlock IRON_BARS_NORTH_WEST_WATERLOGGED = new DungeonSegmentModelFourWayBlock(DungeonSegmentModelBlockType.IRON_BARS, true, false, false, true, true);
	public static final DungeonSegmentModelFourWayBlock IRON_BARS_EAST_WATERLOGGED = new DungeonSegmentModelFourWayBlock(DungeonSegmentModelBlockType.IRON_BARS, false, true, false, false, true);
	public static final DungeonSegmentModelFourWayBlock IRON_BARS_EAST_SOUTH_WATERLOGGED = new DungeonSegmentModelFourWayBlock(DungeonSegmentModelBlockType.IRON_BARS, false, true, true, false, true);
	public static final DungeonSegmentModelFourWayBlock IRON_BARS_EAST_SOUTH_WEST_WATERLOGGED = new DungeonSegmentModelFourWayBlock(DungeonSegmentModelBlockType.IRON_BARS, false, true, true, true, true);
	public static final DungeonSegmentModelFourWayBlock IRON_BARS_EAST_WEST_WATERLOGGED = new DungeonSegmentModelFourWayBlock(DungeonSegmentModelBlockType.IRON_BARS, false, true, false, true, true);
	public static final DungeonSegmentModelFourWayBlock IRON_BARS_SOUTH_WATERLOGGED = new DungeonSegmentModelFourWayBlock(DungeonSegmentModelBlockType.IRON_BARS, false, false, true, false, true);
	public static final DungeonSegmentModelFourWayBlock IRON_BARS_SOUTH_WEST_WATERLOGGED = new DungeonSegmentModelFourWayBlock(DungeonSegmentModelBlockType.IRON_BARS, false, false, true, true, true);
	public static final DungeonSegmentModelFourWayBlock IRON_BARS_WEST_WATERLOGGED = new DungeonSegmentModelFourWayBlock(DungeonSegmentModelBlockType.IRON_BARS, false, false, false, true, true);

	public static DungeonSegmentModel CORRIDOR_EW;
	public static DungeonSegmentModel CORRIDOR_EW_TURN;
	public static DungeonSegmentModel CORRIDOR_EW_OPEN;
	public static DungeonSegmentModel CORRIDOR_EW_ALL_OPEN;
	public static DungeonSegmentModel CORRIDOR_EW_2;
	public static DungeonSegmentModel CORRIDOR_EW_3;

	public static DungeonSegmentModel STAIRS;
	public static DungeonSegmentModel STAIRS_TOP;
	public static DungeonSegmentModel STAIRS_BOTTOM;

	public static DungeonSegmentModel ROOM;
	
	public static DungeonSegmentModel ENTRANCE;

	public static void load(IResourceManager resourceManager) {
		if (LOADED)
			return;
		DungeonCrawl.LOGGER.info("Loading dungeon segment models");
		CORRIDOR_EW = loadFromFile("models/dungeon/corridor_ew.json", resourceManager);
		CORRIDOR_EW_TURN = loadFromFile("models/dungeon/corridor_ew_turn.json", resourceManager);
		CORRIDOR_EW_OPEN = loadFromFile("models/dungeon/corridor_ew_open.json", resourceManager);
		CORRIDOR_EW_ALL_OPEN = loadFromFile("models/dungeon/corridor_ew_all_open.json", resourceManager);
		CORRIDOR_EW_2 = loadFromFile("models/dungeon/corridor_ew_2.json", resourceManager);
		CORRIDOR_EW_3 = loadFromFile("models/dungeon/corridor_ew_3.json", resourceManager);
		STAIRS = loadFromFile("models/dungeon/stairs.json", resourceManager);
		STAIRS_TOP = loadFromFile("models/dungeon/stairs_top.json", resourceManager);
		STAIRS_BOTTOM = loadFromFile("models/dungeon/stairs_bottom.json", resourceManager);
		ROOM = loadFromFile("models/dungeon/room.json", resourceManager);
		ENTRANCE = loadFromFile("models/dungeon/entrance.json", resourceManager);
		LOADED = true;
	}

	public static DungeonSegmentModel loadFromFile(String path, IResourceManager resourceManager) {
		try {
			return DungeonSegmentModelReader.readModelFromInputStream(resourceManager.getResource(DungeonCrawl.locate(path)).getInputStream());
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}

}
