package xiroc.dungeoncrawl.dungeon.model;

/*
 * DungeonCrawl (C) 2019 - 2020 XYROC (XIROC1337), All Rights Reserved 
 */

import java.io.DataInputStream;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

import com.google.common.collect.Lists;

import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.NBTSizeTracker;
import net.minecraft.resources.IResourceManager;
import net.minecraft.util.math.Vec3i;
import xiroc.dungeoncrawl.DungeonCrawl;
import xiroc.dungeoncrawl.api.event.DungeonSegmentModelLoadEvent;

public class DungeonModels {

	public static final HashMap<Integer, Vec3i> OFFSETS = new HashMap<Integer, Vec3i>();

	public static final HashMap<Integer, DungeonModel> MAP = new HashMap<Integer, DungeonModel>();

	public static final HashMap<Integer, DungeonModel[]> NODE_CATEGORY_INTERSECTIONS = new HashMap<Integer, DungeonModel[]>();

	public static final Vec3i NO_OFFSET = new Vec3i(0, 0, 0);

	public static DungeonModel CORRIDOR, CORRIDOR_2, CORRIDOR_ROOM;
	public static DungeonModel CAKE_ROOM;
	public static DungeonModel CORRIDOR_LIGHT_FEATURE;
	public static DungeonModel CORRIDOR_LINKER, CORRIDOR_LINKER_2, CORRIDOR_LINKER_3, CORRIDOR_LINKER_4,
			CORRIDOR_LINKER_5;
	public static DungeonModel ENTRANCE;
	public static DungeonModel FOOD_SIDE_ROOM;
	public static DungeonModel LARGE_CORRIDOR_START, LARGE_CORRIDOR_STRAIGHT, LARGE_CORRIDOR_OPEN, LARGE_CORRIDOR_TURN;
	public static DungeonModel FORGE;
	public static DungeonModel LARGE_NODE, LARGE_NODE_LIBRARY;
	public static DungeonModel LOOT_ROOM;
	public static DungeonModel NODE, NODE_2, NODE_3;
	public static DungeonModel NODE_CATACOMB_DEAD_END, NODE_CATACOMB_TURN;
	public static DungeonModel NODE_TURN, NODE_WATER, NODE_WATER_2, NODE_WATER_DEAD_END, NODE_WATER_STRAIGHT,
			NODE_WATER_OPEN;
	public static DungeonModel NODE_CONNECTOR, NODE_CONNECTOR_2, NODE_CONNECTOR_3, NODE_CONNECTOR_4, NODE_CONNECTOR_5;
	public static DungeonModel PRISON_CELL;
	public static DungeonModel SECRET_ROOM;
	public static DungeonModel SPAWNER_ROOM;
	public static DungeonModel STAIRCASE, STAIRS_BOTTOM, STAIRS_BOTTOM_2, STAIRS_TOP;
	public static DungeonModel STARTER_ROOM;

	public static DungeonModel[] CORRIDOR_LINKERS;

	public static DungeonModel[] NODE_CONNECTORS;

	public static synchronized void load() {
		DungeonCrawl.LOGGER.info("Loading all models...");

		CORRIDOR = loadFromFile("models/dungeon/corridor.nbt").build().setId(0);
//		CORRIDOR_2 = loadFromFile("models/dungeon/corridor_2.nbt").build().setId(1);
		CORRIDOR_ROOM = loadFromFile("models/dungeon/corridor_room.nbt").build().setId(3);
		CORRIDOR_LIGHT_FEATURE = loadFromFile("models/dungeon/corridor_light.nbt").build().setId(4);

		CORRIDOR_LINKER = loadFromFile("models/dungeon/corridor_linker.nbt").build().setId(16);
		CORRIDOR_LINKER_2 = loadFromFile("models/dungeon/corridor_linker_2.nbt").build().setId(17);
		CORRIDOR_LINKER_3 = loadFromFile("models/dungeon/corridor_linker_3.nbt").build().setId(18);
		CORRIDOR_LINKER_4 = loadFromFile("models/dungeon/corridor_linker_4.nbt").build().setId(19);
		CORRIDOR_LINKER_5 = loadFromFile("models/dungeon/corridor_linker_5.nbt").build().setId(20);

		ENTRANCE = loadFromFile("models/dungeon/entrance.nbt").build().setId(21);

		FOOD_SIDE_ROOM = loadFromFile("models/dungeon/food_side_room.nbt").build().setId(24);

		LARGE_CORRIDOR_START = loadFromFile("models/dungeon/large_corridor_start.nbt").build().setId(28);
		LARGE_CORRIDOR_STRAIGHT = loadFromFile("models/dungeon/large_corridor_start.nbt").build().setId(29);
		LARGE_CORRIDOR_TURN = loadFromFile("models/dungeon/large_corridor_start.nbt").build().setId(30);
		LARGE_CORRIDOR_OPEN = loadFromFile("models/dungeon/large_corridor_start.nbt").build().setId(31);

		FORGE = loadFromFile("models/dungeon/forge.nbt").build().set(32, ModelCategory.STAGE_1, ModelCategory.STAGE_2,
				ModelCategory.NODE_DEAD_END);
		LARGE_NODE = loadFromFile("models/dungeon/large_node.nbt").build().set(33, ModelCategory.STAGE_3,
				ModelCategory.STAGE_4, ModelCategory.STAGE_5, ModelCategory.LARGE_NODE, ModelCategory.NODE,
				ModelCategory.NODE_STRAIGHT, ModelCategory.NODE_OPEN, ModelCategory.NODE_TURN,
				ModelCategory.NODE_DEAD_END);
		LARGE_NODE_LIBRARY = loadFromFile("models/dungeon/large_node_library.nbt").build().set(34,
				ModelCategory.STAGE_2, ModelCategory.STAGE_3, ModelCategory.STAGE_4, ModelCategory.LARGE_NODE,
				ModelCategory.NODE, ModelCategory.NODE_STRAIGHT, ModelCategory.NODE_OPEN, ModelCategory.NODE_TURN,
				ModelCategory.NODE_DEAD_END);

		LOOT_ROOM = loadFromFile("models/dungeon/loot_room.nbt").build().set(35);

		NODE = loadFromFile("models/dungeon/node.nbt").build().set(36, ModelCategory.STAGE_1, ModelCategory.NORMAL_NODE,
				ModelCategory.NODE, ModelCategory.NODE_STRAIGHT, ModelCategory.NODE_OPEN, ModelCategory.NODE_TURN,
				ModelCategory.NODE_DEAD_END);

		NODE_2 = loadFromFile("models/dungeon/node_2.nbt").build().set(37, ModelCategory.STAGE_1, ModelCategory.STAGE_2,
				ModelCategory.NORMAL_NODE, ModelCategory.NODE, ModelCategory.NODE_STRAIGHT, ModelCategory.NODE_OPEN,
				ModelCategory.NODE_TURN, ModelCategory.NODE_DEAD_END);

		NODE_3 = loadFromFile("models/dungeon/node_3.nbt").build().set(38, ModelCategory.STAGE_1, ModelCategory.STAGE_2,
				ModelCategory.NORMAL_NODE, ModelCategory.NODE, ModelCategory.NODE_STRAIGHT, ModelCategory.NODE_OPEN,
				ModelCategory.NODE_TURN, ModelCategory.NODE_DEAD_END);

		NODE_CATACOMB_TURN = loadFromFile("models/dungeon/node_catacomb_turn.nbt").build().set(39,
				ModelCategory.NORMAL_NODE, ModelCategory.STAGE_3, ModelCategory.STAGE_4, ModelCategory.NODE_TURN,
				ModelCategory.NODE_DEAD_END);

		NODE_CATACOMB_DEAD_END = loadFromFile("models/dungeon/node_catacomb_dead_end.nbt").build().set(40,
				ModelCategory.STAGE_3, ModelCategory.STAGE_4, ModelCategory.NODE_DEAD_END);

		NODE_TURN = loadFromFile("models/dungeon/node_turn.nbt").build().set(41, ModelCategory.NORMAL_NODE,
				ModelCategory.STAGE_3, ModelCategory.STAGE_4, ModelCategory.NODE_TURN, ModelCategory.NODE_DEAD_END);

		NODE_WATER = loadFromFile("models/dungeon/node_water.nbt").build().set(42, ModelCategory.NORMAL_NODE,
				ModelCategory.STAGE_3, ModelCategory.STAGE_4, ModelCategory.NODE, ModelCategory.NODE_STRAIGHT,
				ModelCategory.NODE_OPEN, ModelCategory.NODE_TURN, ModelCategory.NODE_DEAD_END);

		NODE_WATER_2 = loadFromFile("models/dungeon/node_water_2.nbt").build().set(43, ModelCategory.NORMAL_NODE,
				ModelCategory.STAGE_3, ModelCategory.STAGE_4, ModelCategory.NODE, ModelCategory.NODE_STRAIGHT,
				ModelCategory.NODE_OPEN, ModelCategory.NODE_TURN, ModelCategory.NODE_DEAD_END);

		NODE_WATER_DEAD_END = loadFromFile("models/dungeon/node_water_dead_end.nbt").build().set(44,
				ModelCategory.NORMAL_NODE, ModelCategory.STAGE_3, ModelCategory.STAGE_4, ModelCategory.NODE_DEAD_END);

		NODE_WATER_OPEN = loadFromFile("models/dungeon/node_water_open.nbt").build().set(45, ModelCategory.NORMAL_NODE,
				ModelCategory.STAGE_3, ModelCategory.STAGE_4, ModelCategory.NODE_STRAIGHT, ModelCategory.NODE_OPEN,
				ModelCategory.NODE_TURN, ModelCategory.NODE_DEAD_END);

		NODE_WATER_STRAIGHT = loadFromFile("models/dungeon/node_water_straight.nbt").build().set(46,
				ModelCategory.NORMAL_NODE, ModelCategory.STAGE_3, ModelCategory.STAGE_4, ModelCategory.NODE_STRAIGHT,
				ModelCategory.NODE_DEAD_END);

		NODE_CONNECTOR = loadFromFile("models/dungeon/node_connector.nbt").build().setId(56);
		NODE_CONNECTOR_2 = loadFromFile("models/dungeon/node_connector_2.nbt").build().setId(57);
		NODE_CONNECTOR_3 = loadFromFile("models/dungeon/node_connector_3.nbt").build().setId(58);
		NODE_CONNECTOR_4 = loadFromFile("models/dungeon/node_connector_4.nbt").build().setId(59);
		NODE_CONNECTOR_5 = loadFromFile("models/dungeon/node_connector_5.nbt").build().setId(60);

		PRISON_CELL = loadFromFile("models/dungeon/prison_cell.nbt").build().setId(64);

		SECRET_ROOM = loadFromFile("models/dungeon/secret_room.nbt").build().setId(70);
		
		SPAWNER_ROOM = loadFromFile("models/dungeon/spawner_room.nbt").build().setId(71);

		STAIRCASE = loadFromFile("models/dungeon/staircase.nbt").build().setId(72);
		STAIRS_TOP = loadFromFile("models/dungeon/stairs_top.nbt").build().setId(73);
		STAIRS_BOTTOM = loadFromFile("models/dungeon/stairs_bottom.nbt").build().setId(74);
		STAIRS_BOTTOM_2 = loadFromFile("models/dungeon/stairs_bottom_2.nbt").build().setId(75);

		STARTER_ROOM = loadFromFile("models/dungeon/starter_room.nbt").build().setId(76);

		// -End of Model loading- //

		CORRIDOR_LINKERS = new DungeonModel[] { CORRIDOR_LINKER, CORRIDOR_LINKER_2, CORRIDOR_LINKER_3,
				CORRIDOR_LINKER_4, CORRIDOR_LINKER_5 };

		NODE_CONNECTORS = new DungeonModel[] { NODE_CONNECTOR, NODE_CONNECTOR_2, NODE_CONNECTOR_3, NODE_CONNECTOR_4,
				NODE_CONNECTOR_5 };

		// -Offsets- //

		OFFSETS.put(21, new Vec3i(-3, 0, -3));

		OFFSETS.put(42, new Vec3i(0, -1, 0));
		OFFSETS.put(43, new Vec3i(0, -1, 0));
		OFFSETS.put(44, new Vec3i(0, -1, 0));
		OFFSETS.put(45, new Vec3i(0, -1, 0));
		OFFSETS.put(46, new Vec3i(0, -1, 0));

	}

	public static DungeonModel loadFromFile(String path) {
		DungeonCrawl.LOGGER.info("Loading {}", path);

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

	public static DungeonModel loadFromFile(String path, IResourceManager resourceManager) {
		DungeonCrawl.LOGGER.info("Loading {}", path);
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

	public static Vec3i getOffset(int model) {
		return OFFSETS.getOrDefault(model, NO_OFFSET);
	}

	public static enum ModelCategory {

		STAGE_1, STAGE_2, STAGE_3, STAGE_4, STAGE_5, NODE_DEAD_END, NODE_STRAIGHT, NODE_TURN, NODE_OPEN, NODE,
		NORMAL_NODE, LARGE_NODE;

		public final List<DungeonModel> members;

		public static final ModelCategory[] STAGES = new ModelCategory[] { STAGE_1, STAGE_2, STAGE_3, STAGE_4,
				STAGE_5 };

		private ModelCategory() {
			members = Lists.newArrayList();
		}

		public static DungeonModel[] getIntersection(ModelCategory... categories) {
			int hash = Objects.hash((Object[]) categories);

			if (NODE_CATEGORY_INTERSECTIONS.containsKey(hash)) {
				DungeonCrawl.LOGGER.debug("Looking up {}.", Arrays.toString(categories));
				return NODE_CATEGORY_INTERSECTIONS.get(hash);
			}

			List<DungeonModel> intersection = Lists.newArrayList();

			mainLoop: for (DungeonModel model : categories[0].members) {
				for (int i = 0; i < categories.length; i++)
					if (!categories[i].members.contains(model))
						continue mainLoop;
				intersection.add(model);
			}

			DungeonModel[] array = intersection.toArray(new DungeonModel[intersection.size()]);
			NODE_CATEGORY_INTERSECTIONS.put(hash, array);
			return array;
		}

		/**
		 * @param stage range: 0-4
		 */
		public static ModelCategory getCategoryForStage(int stage) {
			if (stage < 0 || stage > 4)
				return STAGE_1;
			return STAGES[stage];
		}

	}

}
