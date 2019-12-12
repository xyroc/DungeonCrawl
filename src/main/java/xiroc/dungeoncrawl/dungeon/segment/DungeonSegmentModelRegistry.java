package xiroc.dungeoncrawl.dungeon.segment;

/*
 * DungeonCrawl (C) 2019 XYROC (XIROC1337), All Rights Reserved 
 */

import java.io.DataInputStream;

import java.io.IOException;
import java.util.HashMap;

import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.NBTSizeTracker;
import net.minecraft.resources.IResourceManager;
import net.minecraft.world.server.ServerWorld;
import xiroc.dungeoncrawl.DungeonCrawl;
import xiroc.dungeoncrawl.api.event.DungeonSegmentModelLoadEvent;
import xiroc.dungeoncrawl.util.ModelHelper;

public class DungeonSegmentModelRegistry {

	public static boolean LOADED = false;

	public static final HashMap<Integer, DungeonSegmentModel> MAP = new HashMap<Integer, DungeonSegmentModel>();

//	public static final DungeonSegmentModelBlock NONE = new DungeonSegmentModelBlock(DungeonSegmentModelBlockType.NONE);
//
//	public static final DungeonSegmentModelBlock WATER = new DungeonSegmentModelBlock(
//			DungeonSegmentModelBlockType.NONE);
//	public static final DungeonSegmentModelBlock LAVA = new DungeonSegmentModelBlock(DungeonSegmentModelBlockType.NONE);

	// public static final DungeonSegmentModelBlock TORCH_DARK_NORTH = new
	// DungeonSegmentModelBlock(DungeonSegmentModelBlockType.TORCH_DARK);
	// public static final DungeonSegmentModelBlock TORCH_DARK_EAST = new
	// DungeonSegmentModelBlock(DungeonSegmentModelBlockType.TORCH_DARK);
	// public static final DungeonSegmentModelBlock TORCH_DARK_SOUTH = new
	// DungeonSegmentModelBlock(DungeonSegmentModelBlockType.TORCH_DARK);
	// public static final DungeonSegmentModelBlock TORCH_DARK_WEST = new
	// DungeonSegmentModelBlock(DungeonSegmentModelBlockType.TORCH_DARK);
	//
	// public static final DungeonSegmentModelBlock CEILING = new
	// DungeonSegmentModelBlock(DungeonSegmentModelBlockType.CEILING);
	// public static final DungeonSegmentModelBlock CEILING_STAIRS_NORTH = new
	// DungeonSegmentModelBlock(DungeonSegmentModelBlockType.CEILING_STAIRS,
	// Direction.NORTH, false);
	// public static final DungeonSegmentModelBlock CEILING_STAIRS_EAST = new
	// DungeonSegmentModelBlock(DungeonSegmentModelBlockType.CEILING_STAIRS,
	// Direction.EAST, false);
	// public static final DungeonSegmentModelBlock CEILING_STAIRS_SOUTH = new
	// DungeonSegmentModelBlock(DungeonSegmentModelBlockType.CEILING_STAIRS,
	// Direction.SOUTH, false);
	// public static final DungeonSegmentModelBlock CEILING_STAIRS_WEST = new
	// DungeonSegmentModelBlock(DungeonSegmentModelBlockType.CEILING_STAIRS,
	// Direction.WEST, false);
	// public static final DungeonSegmentModelBlock CEILING_STAIRS_NORTH_UD =
	// new DungeonSegmentModelBlock(DungeonSegmentModelBlockType.CEILING_STAIRS,
	// Direction.NORTH, true);
	// public static final DungeonSegmentModelBlock CEILING_STAIRS_EAST_UD = new
	// DungeonSegmentModelBlock(DungeonSegmentModelBlockType.CEILING_STAIRS,
	// Direction.EAST, true);
	// public static final DungeonSegmentModelBlock CEILING_STAIRS_SOUTH_UD =
	// new DungeonSegmentModelBlock(DungeonSegmentModelBlockType.CEILING_STAIRS,
	// Direction.SOUTH, true);
	// public static final DungeonSegmentModelBlock CEILING_STAIRS_WEST_UD = new
	// DungeonSegmentModelBlock(DungeonSegmentModelBlockType.CEILING_STAIRS,
	// Direction.WEST, true);
	//
	// public static final DungeonSegmentModelBlock WALL = new
	// DungeonSegmentModelBlock(DungeonSegmentModelBlockType.WALL);
	// public static final DungeonSegmentModelBlock WALL_LOG = new
	// DungeonSegmentModelBlock(DungeonSegmentModelBlockType.WALL_LOG,
	// Direction.UP, false);
	//
	// public static final DungeonSegmentModelBlock FLOOR = new
	// DungeonSegmentModelBlock(DungeonSegmentModelBlockType.FLOOR);
	// public static final DungeonSegmentModelBlock FLOOR_STAIRS_NORTH = new
	// DungeonSegmentModelBlock(DungeonSegmentModelBlockType.FLOOR_STAIRS,
	// Direction.NORTH, false);
	// public static final DungeonSegmentModelBlock FLOOR_STAIRS_EAST = new
	// DungeonSegmentModelBlock(DungeonSegmentModelBlockType.FLOOR_STAIRS,
	// Direction.EAST, false);
	// public static final DungeonSegmentModelBlock FLOOR_STAIRS_SOUTH = new
	// DungeonSegmentModelBlock(DungeonSegmentModelBlockType.FLOOR_STAIRS,
	// Direction.SOUTH, false);
	// public static final DungeonSegmentModelBlock FLOOR_STAIRS_WEST = new
	// DungeonSegmentModelBlock(DungeonSegmentModelBlockType.FLOOR_STAIRS,
	// Direction.WEST, false);
	// public static final DungeonSegmentModelBlock FLOOR_STAIRS_NORTH_UD = new
	// DungeonSegmentModelBlock(DungeonSegmentModelBlockType.FLOOR_STAIRS,
	// Direction.NORTH, true);
	// public static final DungeonSegmentModelBlock FLOOR_STAIRS_EAST_UD = new
	// DungeonSegmentModelBlock(DungeonSegmentModelBlockType.FLOOR_STAIRS,
	// Direction.EAST, true);
	// public static final DungeonSegmentModelBlock FLOOR_STAIRS_SOUTH_UD = new
	// DungeonSegmentModelBlock(DungeonSegmentModelBlockType.FLOOR_STAIRS,
	// Direction.SOUTH, true);
	// public static final DungeonSegmentModelBlock FLOOR_STAIRS_WEST_UD = new
	// DungeonSegmentModelBlock(DungeonSegmentModelBlockType.FLOOR_STAIRS,
	// Direction.WEST, true);
	//
	// public static final DungeonSegmentModelBlock RND_WALL_SPAWNER = new
	// DungeonSegmentModelBlock(DungeonSegmentModelBlockType.RAND_WALL_SPAWNER);
	// public static final DungeonSegmentModelBlock CHEST_COMMON_NORTH = new
	// DungeonSegmentModelBlock(DungeonSegmentModelBlockType.CHEST_COMMON,
	// Direction.NORTH, false);
	// public static final DungeonSegmentModelBlock CHEST_COMMON_EAST = new
	// DungeonSegmentModelBlock(DungeonSegmentModelBlockType.CHEST_COMMON,
	// Direction.EAST, false);
	// public static final DungeonSegmentModelBlock CHEST_COMMON_SOUTH = new
	// DungeonSegmentModelBlock(DungeonSegmentModelBlockType.CHEST_COMMON,
	// Direction.SOUTH, false);
	// public static final DungeonSegmentModelBlock CHEST_COMMON_WEST = new
	// DungeonSegmentModelBlock(DungeonSegmentModelBlockType.CHEST_COMMON,
	// Direction.WEST, false);
	// public static final DungeonSegmentModelBlock RND_CC_FLOOR_SPWN_NORTH =
	// new
	// DungeonSegmentModelBlock(DungeonSegmentModelBlockType.RAND_FLOOR_CHESTCOMMON_SPAWNER,
	// Direction.NORTH, false);
	// public static final DungeonSegmentModelBlock RND_CC_FLOOR_SPWN_EAST = new
	// DungeonSegmentModelBlock(DungeonSegmentModelBlockType.RAND_FLOOR_CHESTCOMMON_SPAWNER,
	// Direction.EAST, false);
	// public static final DungeonSegmentModelBlock RND_CC_FLOOR_SPWN_SOUTH =
	// new
	// DungeonSegmentModelBlock(DungeonSegmentModelBlockType.RAND_FLOOR_CHESTCOMMON_SPAWNER,
	// Direction.SOUTH, false);
	// public static final DungeonSegmentModelBlock RND_CC_FLOOR_SPWN_WEST = new
	// DungeonSegmentModelBlock(DungeonSegmentModelBlockType.RAND_FLOOR_CHESTCOMMON_SPAWNER,
	// Direction.WEST, false);
	//
	// public static final DungeonSegmentModelTrapDoorBlock TRAPDOOR_NORTH = new
	// DungeonSegmentModelTrapDoorBlock(DungeonSegmentModelBlockType.TRAPDOOR,
	// Direction.NORTH, true, Half.BOTTOM, false);
	// public static final DungeonSegmentModelTrapDoorBlock TRAPDOOR_EAST = new
	// DungeonSegmentModelTrapDoorBlock(DungeonSegmentModelBlockType.TRAPDOOR,
	// Direction.EAST, true, Half.BOTTOM, false);
	// public static final DungeonSegmentModelTrapDoorBlock TRAPDOOR_SOUTH = new
	// DungeonSegmentModelTrapDoorBlock(DungeonSegmentModelBlockType.TRAPDOOR,
	// Direction.SOUTH, true, Half.BOTTOM, false);
	// public static final DungeonSegmentModelTrapDoorBlock TRAPDOOR_WEST = new
	// DungeonSegmentModelTrapDoorBlock(DungeonSegmentModelBlockType.TRAPDOOR,
	// Direction.WEST, true, Half.BOTTOM, false);
	//
	// public static final DungeonSegmentModelTrapDoorBlock
	// TRAPDOOR_CLOSED_NORTH = new
	// DungeonSegmentModelTrapDoorBlock(DungeonSegmentModelBlockType.TRAPDOOR,
	// Direction.NORTH, false, Half.BOTTOM, false);
	// public static final DungeonSegmentModelTrapDoorBlock TRAPDOOR_CLOSED_EAST
	// = new
	// DungeonSegmentModelTrapDoorBlock(DungeonSegmentModelBlockType.TRAPDOOR,
	// Direction.EAST, false, Half.BOTTOM, false);
	// public static final DungeonSegmentModelTrapDoorBlock
	// TRAPDOOR_CLOSED_SOUTH = new
	// DungeonSegmentModelTrapDoorBlock(DungeonSegmentModelBlockType.TRAPDOOR,
	// Direction.SOUTH, false, Half.BOTTOM, false);
	// public static final DungeonSegmentModelTrapDoorBlock TRAPDOOR_CLOSED_WEST
	// = new
	// DungeonSegmentModelTrapDoorBlock(DungeonSegmentModelBlockType.TRAPDOOR,
	// Direction.WEST, false, Half.BOTTOM, false);
	//
	// public static final DungeonSegmentModelFourWayBlock IRON_BARS_NORTH = new
	// DungeonSegmentModelFourWayBlock(DungeonSegmentModelBlockType.IRON_BARS,
	// true, false, false, false, false);
	// public static final DungeonSegmentModelFourWayBlock IRON_BARS_NORTH_EAST
	// = new
	// DungeonSegmentModelFourWayBlock(DungeonSegmentModelBlockType.IRON_BARS,
	// true, true, false, false, false);
	// public static final DungeonSegmentModelFourWayBlock
	// IRON_BARS_NORTH_EAST_SOUTH = new
	// DungeonSegmentModelFourWayBlock(DungeonSegmentModelBlockType.IRON_BARS,
	// true, true, true, false, false);
	// public static final DungeonSegmentModelFourWayBlock
	// IRON_BARS_NORTH_EAST_SOUTH_WEST = new
	// DungeonSegmentModelFourWayBlock(DungeonSegmentModelBlockType.IRON_BARS,
	// true, true, true, true, false);
	// public static final DungeonSegmentModelFourWayBlock
	// IRON_BARS_NORTH_EAST_WEST = new
	// DungeonSegmentModelFourWayBlock(DungeonSegmentModelBlockType.IRON_BARS,
	// true, true, false, true, false);
	// public static final DungeonSegmentModelFourWayBlock IRON_BARS_NORTH_SOUTH
	// = new
	// DungeonSegmentModelFourWayBlock(DungeonSegmentModelBlockType.IRON_BARS,
	// true, false, true, false, false);
	// public static final DungeonSegmentModelFourWayBlock
	// IRON_BARS_NORTH_SOUTH_WEST = new
	// DungeonSegmentModelFourWayBlock(DungeonSegmentModelBlockType.IRON_BARS,
	// true, false, true, true, false);
	// public static final DungeonSegmentModelFourWayBlock IRON_BARS_NORTH_WEST
	// = new
	// DungeonSegmentModelFourWayBlock(DungeonSegmentModelBlockType.IRON_BARS,
	// true, false, false, true, false);
	// public static final DungeonSegmentModelFourWayBlock IRON_BARS_EAST = new
	// DungeonSegmentModelFourWayBlock(DungeonSegmentModelBlockType.IRON_BARS,
	// false, true, false, false, false);
	// public static final DungeonSegmentModelFourWayBlock IRON_BARS_EAST_SOUTH
	// = new
	// DungeonSegmentModelFourWayBlock(DungeonSegmentModelBlockType.IRON_BARS,
	// false, true, true, false, false);
	// public static final DungeonSegmentModelFourWayBlock
	// IRON_BARS_EAST_SOUTH_WEST = new
	// DungeonSegmentModelFourWayBlock(DungeonSegmentModelBlockType.IRON_BARS,
	// false, true, true, true, false);
	// public static final DungeonSegmentModelFourWayBlock IRON_BARS_EAST_WEST =
	// new
	// DungeonSegmentModelFourWayBlock(DungeonSegmentModelBlockType.IRON_BARS,
	// false, true, false, true, false);
	// public static final DungeonSegmentModelFourWayBlock IRON_BARS_SOUTH = new
	// DungeonSegmentModelFourWayBlock(DungeonSegmentModelBlockType.IRON_BARS,
	// false, false, true, false, false);
	// public static final DungeonSegmentModelFourWayBlock IRON_BARS_SOUTH_WEST
	// = new
	// DungeonSegmentModelFourWayBlock(DungeonSegmentModelBlockType.IRON_BARS,
	// false, false, true, true, false);
	// public static final DungeonSegmentModelFourWayBlock IRON_BARS_WEST = new
	// DungeonSegmentModelFourWayBlock(DungeonSegmentModelBlockType.IRON_BARS,
	// false, false, false, true, false);
	//
	// public static final DungeonSegmentModelFourWayBlock
	// IRON_BARS_NORTH_WATERLOGGED = new
	// DungeonSegmentModelFourWayBlock(DungeonSegmentModelBlockType.IRON_BARS,
	// true, false, false, false, true);
	// public static final DungeonSegmentModelFourWayBlock
	// IRON_BARS_NORTH_EAST_WATERLOGGED = new
	// DungeonSegmentModelFourWayBlock(DungeonSegmentModelBlockType.IRON_BARS,
	// true, true, false, false, true);
	// public static final DungeonSegmentModelFourWayBlock
	// IRON_BARS_NORTH_EAST_SOUTH_WATERLOGGED = new
	// DungeonSegmentModelFourWayBlock(DungeonSegmentModelBlockType.IRON_BARS,
	// true, true, true, false, true);
	// public static final DungeonSegmentModelFourWayBlock
	// IRON_BARS_NORTH_EAST_SOUTH_WEST_WATERLOGGED = new
	// DungeonSegmentModelFourWayBlock(DungeonSegmentModelBlockType.IRON_BARS,
	// true, true, true, true, true);
	// public static final DungeonSegmentModelFourWayBlock
	// IRON_BARS_NORTH_EAST_WEST_WATERLOGGED = new
	// DungeonSegmentModelFourWayBlock(DungeonSegmentModelBlockType.IRON_BARS,
	// true, true, false, true, true);
	// public static final DungeonSegmentModelFourWayBlock
	// IRON_BARS_NORTH_SOUTH_WATERLOGGED = new
	// DungeonSegmentModelFourWayBlock(DungeonSegmentModelBlockType.IRON_BARS,
	// true, false, true, false, true);
	// public static final DungeonSegmentModelFourWayBlock
	// IRON_BARS_NORTH_SOUTH_WEST_WATERLOGGED = new
	// DungeonSegmentModelFourWayBlock(DungeonSegmentModelBlockType.IRON_BARS,
	// true, false, true, true, true);
	// public static final DungeonSegmentModelFourWayBlock
	// IRON_BARS_NORTH_WEST_WATERLOGGED = new
	// DungeonSegmentModelFourWayBlock(DungeonSegmentModelBlockType.IRON_BARS,
	// true, false, false, true, true);
	// public static final DungeonSegmentModelFourWayBlock
	// IRON_BARS_EAST_WATERLOGGED = new
	// DungeonSegmentModelFourWayBlock(DungeonSegmentModelBlockType.IRON_BARS,
	// false, true, false, false, true);
	// public static final DungeonSegmentModelFourWayBlock
	// IRON_BARS_EAST_SOUTH_WATERLOGGED = new
	// DungeonSegmentModelFourWayBlock(DungeonSegmentModelBlockType.IRON_BARS,
	// false, true, true, false, true);
	// public static final DungeonSegmentModelFourWayBlock
	// IRON_BARS_EAST_SOUTH_WEST_WATERLOGGED = new
	// DungeonSegmentModelFourWayBlock(DungeonSegmentModelBlockType.IRON_BARS,
	// false, true, true, true, true);
	// public static final DungeonSegmentModelFourWayBlock
	// IRON_BARS_EAST_WEST_WATERLOGGED = new
	// DungeonSegmentModelFourWayBlock(DungeonSegmentModelBlockType.IRON_BARS,
	// false, true, false, true, true);
	// public static final DungeonSegmentModelFourWayBlock
	// IRON_BARS_SOUTH_WATERLOGGED = new
	// DungeonSegmentModelFourWayBlock(DungeonSegmentModelBlockType.IRON_BARS,
	// false, false, true, false, true);
	// public static final DungeonSegmentModelFourWayBlock
	// IRON_BARS_SOUTH_WEST_WATERLOGGED = new
	// DungeonSegmentModelFourWayBlock(DungeonSegmentModelBlockType.IRON_BARS,
	// false, false, true, true, true);
	// public static final DungeonSegmentModelFourWayBlock
	// IRON_BARS_WEST_WATERLOGGED = new
	// DungeonSegmentModelFourWayBlock(DungeonSegmentModelBlockType.IRON_BARS,
	// false, false, false, true, true);

	public static DungeonSegmentModel BRIDGE;
	public static DungeonSegmentModel BRIDGE_TURN;
	public static DungeonSegmentModel BRIDGE_SIDE;
	public static DungeonSegmentModel BRIDGE_ALL_SIDES;

	public static DungeonSegmentModel CORRIDOR;
	public static DungeonSegmentModel CORRIDOR_TURN;
	public static DungeonSegmentModel CORRIDOR_OPEN;
	public static DungeonSegmentModel CORRIDOR_ALL_OPEN;

	public static DungeonSegmentModel CORRIDOR_2;
	public static DungeonSegmentModel CORRIDOR_2_TURN;
	public static DungeonSegmentModel CORRIDOR_2_OPEN;
	public static DungeonSegmentModel CORRIDOR_2_ALL_OPEN;

	public static DungeonSegmentModel CORRIDOR_3;
	public static DungeonSegmentModel CORRIDOR_3_TURN;
	public static DungeonSegmentModel CORRIDOR_3_OPEN;
	public static DungeonSegmentModel CORRIDOR_3_ALL_OPEN;

	public static DungeonSegmentModel CORRIDOR_ROOM, CORRIDOR_TRAP, CORRIDOR_FIRE, CORRIDOR_GRASS;

	public static DungeonSegmentModel HOLE;
	public static DungeonSegmentModel HOLE_LAVA;
	public static DungeonSegmentModel HOLE_TRAP;

	public static DungeonSegmentModel SIDE_ROOM_SMALL_LIBRARY, SIDE_ROOM_FARM, SIDE_ROOM_TNT;

	public static DungeonSegmentModel STAIRS;
	public static DungeonSegmentModel STAIRS_TOP;
	public static DungeonSegmentModel STAIRS_BOTTOM;

	public static DungeonSegmentModel ROOM;

	public static DungeonSegmentModel LARGE_ROOM, LIBRARY, LOOT_ROOM, BOSS_ROOM;

	public static DungeonSegmentModel ENTRANCE_TOWER_0, ENTRANCE_TOWER_1;

	public static DungeonSegmentModel KITCHEN, STARTER_ROOM;

	public static void load(ServerWorld world) {
		load(world.getServer().getResourceManager());
	}

	public static synchronized void load(IResourceManager resourceManager) {
		if (LOADED)
			return;
		LOADED = true;
		DungeonCrawl.LOGGER.info("Loading dungeon segment models");
		CORRIDOR = loadFromFile("models/dungeon/corridor.nbt", resourceManager).build().setId(0);
		CORRIDOR_TURN = loadFromFile("models/dungeon/corridor_turn.nbt", resourceManager).build().setId(1);
		CORRIDOR_OPEN = loadFromFile("models/dungeon/corridor_open.nbt", resourceManager).build().setId(2);
		CORRIDOR_ALL_OPEN = loadFromFile("models/dungeon/corridor_all_open.nbt", resourceManager).build().setId(3);
		CORRIDOR_2 = loadFromFile("models/dungeon/corridor_2.nbt", resourceManager).build().setId(4);
		CORRIDOR_2_TURN = loadFromFile("models/dungeon/corridor_2_turn.nbt", resourceManager).build().setId(5);
		CORRIDOR_2_OPEN = loadFromFile("models/dungeon/corridor_2_open.nbt", resourceManager).build().setId(6);
		CORRIDOR_2_ALL_OPEN = loadFromFile("models/dungeon/corridor_2_all_open.nbt", resourceManager).build().setId(7);
		CORRIDOR_3 = loadFromFile("models/dungeon/corridor_3.nbt", resourceManager).build().setId(8);
		CORRIDOR_3_TURN = loadFromFile("models/dungeon/corridor_3_turn.nbt", resourceManager).build().setId(9);
		CORRIDOR_3_OPEN = loadFromFile("models/dungeon/corridor_3_open.nbt", resourceManager).build().setId(10);
		CORRIDOR_3_ALL_OPEN = loadFromFile("models/dungeon/corridor_3_all_open.nbt", resourceManager).build()
				.setId(11);

		CORRIDOR_ROOM = loadFromFile("models/dungeon/corridor_room.nbt", resourceManager).build().setId(12);
		CORRIDOR_TRAP = loadFromFile("models/dungeon/corridor_trap.nbt", resourceManager).build().setId(13);

		HOLE = loadFromFile("models/dungeon/hole.nbt", resourceManager).build().setId(14);
		HOLE_LAVA = loadFromFile("models/dungeon/hole_lava.nbt", resourceManager).build().setId(15);

		STAIRS = loadFromFile("models/dungeon/stairs.nbt", resourceManager).build().setId(16);
		STAIRS_TOP = loadFromFile("models/dungeon/stairs_top.nbt", resourceManager).build().setId(17);
		STAIRS_BOTTOM = loadFromFile("models/dungeon/stairs_bottom.nbt", resourceManager).build().setId(18);
		ROOM = loadFromFile("models/dungeon/room.nbt", resourceManager).build().setId(19);
		ENTRANCE_TOWER_0 = loadFromFile("models/dungeon/entrance_tower_0.nbt", resourceManager).build().setId(20);

		BRIDGE = loadFromFile("models/dungeon/bridge.nbt", resourceManager).build().setId(21);
		BRIDGE_TURN = loadFromFile("models/dungeon/bridge_turn.nbt", resourceManager).build().setId(22);
		BRIDGE_SIDE = loadFromFile("models/dungeon/bridge_side.nbt", resourceManager).build().setId(23);
		BRIDGE_ALL_SIDES = loadFromFile("models/dungeon/bridge_all_sides.nbt", resourceManager).build().setId(24);

		LARGE_ROOM = loadFromFile("models/dungeon/large_room.nbt", resourceManager).build().setId(25);

//		HOLE_TRAP = loadFromFile("models/dungeon/hole_trap.nbt", resourceManager).build().setId(26);

		KITCHEN = loadFromFile("models/dungeon/kitchen.nbt", resourceManager).build().setId(27);

		LOOT_ROOM = loadFromFile("models/dungeon/loot_room.nbt", resourceManager).build().setId(28);

		CORRIDOR_FIRE = loadFromFile("models/dungeon/corridor_fire.nbt", resourceManager).build().setId(29);

		SIDE_ROOM_SMALL_LIBRARY = loadFromFile("models/dungeon/side_room_small_library.nbt", resourceManager).build()
				.setId(30);
		SIDE_ROOM_FARM = loadFromFile("models/dungeon/side_room_farm.nbt", resourceManager).build().setId(31);

		CORRIDOR_GRASS = loadFromFile("models/dungeon/corridor_grass.nbt", resourceManager).build().setId(32);

		ENTRANCE_TOWER_1 = loadFromFile("models/dungeon/entrance_tower_1.nbt", resourceManager).build().setId(32);

		SIDE_ROOM_TNT = loadFromFile("models/dungeon/side_room_tnt.nbt", resourceManager).build().setId(33);
		STARTER_ROOM = loadFromFile("models/dungeon/starter_room.nbt", resourceManager).build().setId(34);

		LIBRARY = loadFromFile("models/dungeon/library.nbt", resourceManager).build().setId(35);

		BOSS_ROOM = loadFromFile("models/dungeon/boss_room.nbt", resourceManager).build().setId(36);
	}

	public static DungeonSegmentModel loadFromFile(String path, IResourceManager resourceManager) {
		DungeonCrawl.LOGGER.debug("Loading {}", path);
		DungeonSegmentModelLoadEvent loadEvent = new DungeonSegmentModelLoadEvent(path);

		if (DungeonCrawl.EVENT_BUS.post(loadEvent))
			return null;
		try {
//			model = ModelHelper.readModelFromInputStream(
//					resourceManager.getResource(DungeonCrawl.locate(loadEvent.path)).getInputStream());
//			String file = FMLPaths.GAMEDIR.get().toString()+ "/" + path.substring(path.lastIndexOf("/"), path.indexOf("."))
//					+ ".nbt";
//			DungeonCrawl.LOGGER.info(file);
//			DataOutputStream output = new DataOutputStream(new FileOutputStream(file));
//			ModelHelper.convertModelToNBT(model).write(output);
			DataInputStream input = new DataInputStream(resourceManager
					.getResource(DungeonCrawl.locate(loadEvent.path)).getInputStream());
			CompoundNBT nbt = new CompoundNBT();
			nbt.read(input, 16, NBTSizeTracker.INFINITE);
			return ModelHelper.getModelFromNBT(nbt);
		} catch (IOException e) {
			e.printStackTrace();
		}

		return null;
	}

}
