package xiroc.dungeoncrawl.dungeon.model;

/*
 * DungeonCrawl (C) 2019 - 2020 XYROC (XIROC1337), All Rights Reserved 
 */

import java.io.DataInputStream;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;

import com.google.common.collect.Lists;

import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.NBTSizeTracker;
import net.minecraft.resources.IResourceManager;
import xiroc.dungeoncrawl.DungeonCrawl;
import xiroc.dungeoncrawl.api.event.DungeonSegmentModelLoadEvent;

public class DungeonModels {

	public static boolean LOADED = false;

	public static final HashMap<Integer, DungeonModel> MAP = new HashMap<Integer, DungeonModel>();

	public static DungeonModel BRIDGE;
	public static DungeonModel BRIDGE_TURN;
	public static DungeonModel BRIDGE_SIDE;
	public static DungeonModel BRIDGE_ALL_SIDES;

	public static DungeonModel CORRIDOR;
	public static DungeonModel CORRIDOR_TURN;
	public static DungeonModel CORRIDOR_OPEN;
	public static DungeonModel CORRIDOR_ALL_OPEN;

	public static DungeonModel CORRIDOR_2;
	public static DungeonModel CORRIDOR_2_TURN;
	public static DungeonModel CORRIDOR_2_OPEN;
	public static DungeonModel CORRIDOR_2_ALL_OPEN;

	public static DungeonModel CORRIDOR_3;
	public static DungeonModel CORRIDOR_3_TURN;
	public static DungeonModel CORRIDOR_3_OPEN;
	public static DungeonModel CORRIDOR_3_ALL_OPEN;

	public static DungeonModel CORRIDOR_ROOM, CORRIDOR_TRAP, CORRIDOR_FIRE, CORRIDOR_GRASS;

	public static DungeonModel HOLE;
	public static DungeonModel HOLE_LAVA;
	public static DungeonModel HOLE_TRAP;

	public static DungeonModel SIDE_ROOM_SMALL_LIBRARY, SIDE_ROOM_FARM, SIDE_ROOM_TNT;

	public static DungeonModel STAIRS;
	public static DungeonModel STAIRS_TOP;
	public static DungeonModel STAIRS_BOTTOM;

	public static DungeonModel ROOM;

	public static DungeonModel LARGE_ROOM, LIBRARY, LOOT_ROOM, BOSS_ROOM;

	public static DungeonModel ENTRANCE_TOWER_0, ENTRANCE_TOWER_1;

	public static DungeonModel KITCHEN, STARTER_ROOM;

	public static synchronized void load() {
		if (LOADED)
			return;
		LOADED = true;
		DungeonCrawl.LOGGER.info("Loading models");

		CORRIDOR = loadFromFile("models/dungeon/corridor.nbt").build().setId(0);
		CORRIDOR_TURN = loadFromFile("models/dungeon/corridor_turn.nbt").build().setId(1);
		CORRIDOR_OPEN = loadFromFile("models/dungeon/corridor_open.nbt").build().setId(2);
		CORRIDOR_ALL_OPEN = loadFromFile("models/dungeon/corridor_all_open.nbt").build().setId(3);
		CORRIDOR_2 = loadFromFile("models/dungeon/corridor_2.nbt").build().setId(4);
		CORRIDOR_2_TURN = loadFromFile("models/dungeon/corridor_2_turn.nbt").build().setId(5);
		CORRIDOR_2_OPEN = loadFromFile("models/dungeon/corridor_2_open.nbt").build().setId(6);
		CORRIDOR_2_ALL_OPEN = loadFromFile("models/dungeon/corridor_2_all_open.nbt").build().setId(7);
		CORRIDOR_3 = loadFromFile("models/dungeon/corridor_3.nbt").build().setId(8);
		CORRIDOR_3_TURN = loadFromFile("models/dungeon/corridor_3_turn.nbt").build().setId(9);
		CORRIDOR_3_OPEN = loadFromFile("models/dungeon/corridor_3_open.nbt").build().setId(10);
		CORRIDOR_3_ALL_OPEN = loadFromFile("models/dungeon/corridor_3_all_open.nbt").build().setId(11);

		CORRIDOR_ROOM = loadFromFile("models/dungeon/corridor_room.nbt").build().setId(12);
		CORRIDOR_TRAP = loadFromFile("models/dungeon/corridor_trap.nbt").build().setId(13);

		HOLE = loadFromFile("models/dungeon/hole.nbt").build().setId(14);
		HOLE_LAVA = loadFromFile("models/dungeon/hole_lava.nbt").build().setId(15);

		STAIRS = loadFromFile("models/dungeon/stairs.nbt").build().setId(16);
		STAIRS_TOP = loadFromFile("models/dungeon/stairs_top.nbt").build().setId(17);
		STAIRS_BOTTOM = loadFromFile("models/dungeon/stairs_bottom.nbt").build().setId(18);
		ROOM = loadFromFile("models/dungeon/room.nbt").build().setId(19);
		ENTRANCE_TOWER_0 = loadFromFile("models/dungeon/entrance_tower_0.nbt").build().setId(20);

		BRIDGE = loadFromFile("models/dungeon/bridge.nbt").build().setId(21);
		BRIDGE_TURN = loadFromFile("models/dungeon/bridge_turn.nbt").build().setId(22);
		BRIDGE_SIDE = loadFromFile("models/dungeon/bridge_side.nbt").build().setId(23);
		BRIDGE_ALL_SIDES = loadFromFile("models/dungeon/bridge_all_sides.nbt").build().setId(24);

		LARGE_ROOM = loadFromFile("models/dungeon/large_room.nbt").build().setId(25);

//		HOLE_TRAP = loadFromFile("models/dungeon/hole_trap.nbt").build().setId(26);

		KITCHEN = loadFromFile("models/dungeon/kitchen.nbt").build().setId(27);

		LOOT_ROOM = loadFromFile("models/dungeon/loot_room.nbt").build().setId(28);

		CORRIDOR_FIRE = loadFromFile("models/dungeon/corridor_fire.nbt").build().setId(29);

		SIDE_ROOM_SMALL_LIBRARY = loadFromFile("models/dungeon/side_room_small_library.nbt").build().setId(30);
		SIDE_ROOM_FARM = loadFromFile("models/dungeon/side_room_farm.nbt").build().setId(31);

		CORRIDOR_GRASS = loadFromFile("models/dungeon/corridor_grass.nbt").build().setId(32);

		ENTRANCE_TOWER_1 = loadFromFile("models/dungeon/entrance_tower_1.nbt").build().setId(33);

		SIDE_ROOM_TNT = loadFromFile("models/dungeon/side_room_tnt.nbt").build().setId(34);
		STARTER_ROOM = loadFromFile("models/dungeon/starter_room.nbt").build().setId(35);

		LIBRARY = loadFromFile("models/dungeon/library.nbt").build().setId(36);

		BOSS_ROOM = loadFromFile("models/dungeon/boss_room.nbt").build().setId(37);
		
		
	}

	public static DungeonModel loadFromFile(String path) {
		DungeonCrawl.LOGGER.debug("Loading {}", path);

		try {
			DataInputStream input = new DataInputStream(
					DungeonModels.class.getResourceAsStream("/data/dungeoncrawl/" + path));
			CompoundNBT nbt = new CompoundNBT();
			nbt.read(input, 16, NBTSizeTracker.INFINITE);
			return ModelHandler.getModelFromNBT(nbt);
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	@Deprecated
	public static DungeonModel loadFromFile(String path, IResourceManager resourceManager) {
		DungeonCrawl.LOGGER.debug("Loading {}", path);
		DungeonSegmentModelLoadEvent loadEvent = new DungeonSegmentModelLoadEvent(path);

		if (DungeonCrawl.EVENT_BUS.post(loadEvent))
			return null;
		try {
			DataInputStream input = new DataInputStream(
					resourceManager.getResource(DungeonCrawl.locate(loadEvent.path)).getInputStream());
			CompoundNBT nbt = new CompoundNBT();
			nbt.read(input, 16, NBTSizeTracker.INFINITE);
			return ModelHandler.getModelFromNBT(nbt);
		} catch (IOException e) {
			e.printStackTrace();
		}

		return null;
	}

	public static enum NodeCategory {

		STAGE_1, STAGE_2, STAGE_3, STAGE_4, STAGE_5, DEAD_END, TWO_OPENINGS, TWO_OPENINGS_TURN, THREE_OPENINGS,
		FOUR_OPENINGS, LARGE;

		public final List<DungeonModel> members;

		private NodeCategory() {
			members = Lists.newArrayList();
		}

		public List<DungeonModel> getIntersection(NodeCategory... categories) {
			List<DungeonModel> intersection = Lists.newArrayList();

			for (DungeonModel model : members) {
				boolean add = true;
				for (int i = 0; i < categories.length; i++)
					if (!categories[i].members.contains(model))
						add = false;
				if (add)
					intersection.add(model);
			}

			return intersection;
		}

	}

}
