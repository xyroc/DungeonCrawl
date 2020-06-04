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

import org.jline.utils.InputStreamReader;

import com.google.common.collect.Lists;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.NBTSizeTracker;
import net.minecraft.resources.IResourceManager;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.Vec3i;
import xiroc.dungeoncrawl.DungeonCrawl;
import xiroc.dungeoncrawl.api.event.DungeonSegmentModelLoadEvent;
import xiroc.dungeoncrawl.dungeon.model.DungeonModel.Metadata;
import xiroc.dungeoncrawl.util.WeightedRandomInteger;

public class DungeonModels {

	public static final HashMap<Integer, DungeonModel> MAP = new HashMap<Integer, DungeonModel>();

	public static final HashMap<Integer, WeightedRandomInteger> NODE_MODELS = new HashMap<Integer, WeightedRandomInteger>();

	public static final HashMap<Integer, Vec3i> OFFSETS = new HashMap<Integer, Vec3i>();

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

	public static synchronized void load(IResourceManager resourceManager) {
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

		CAKE_ROOM = load("models/dungeon/", "cake_room", resourceManager);

		ENTRANCE = loadFromFile("models/dungeon/entrance.nbt").build().setId(21);

		FOOD_SIDE_ROOM = loadFromFile("models/dungeon/food_side_room.nbt").build().setId(24);

		LARGE_CORRIDOR_START = loadFromFile("models/dungeon/large_corridor_start.nbt").build().setId(28);
		LARGE_CORRIDOR_STRAIGHT = loadFromFile("models/dungeon/large_corridor_straight.nbt").build().setId(29);
		LARGE_CORRIDOR_TURN = loadFromFile("models/dungeon/large_corridor_turn.nbt").build().setId(30);
		LARGE_CORRIDOR_OPEN = loadFromFile("models/dungeon/large_corridor_open.nbt").build().setId(31);

		LOOT_ROOM = loadFromFile("models/dungeon/loot_room.nbt").build().setId(35);

		// Models with metadata
		
		FORGE = load("models/dungeon/", "forge", resourceManager).build();

		LARGE_NODE = load("models/dungeon/", "large_node", resourceManager).build();
		LARGE_NODE_LIBRARY = load("models/dungeon/", "large_node_library", resourceManager).build();

		NODE = load("models/dungeon/", "node", resourceManager).build();
		NODE_2 = load("models/dungeon/", "node_2", resourceManager).build();
		NODE_3 = load("models/dungeon/", "node_3", resourceManager).build();

		NODE_CATACOMB_DEAD_END = load("models/dungeon/", "node_catacomb_dead_end", resourceManager).build();
		NODE_CATACOMB_TURN = load("models/dungeon/", "node_catacomb_turn", resourceManager).build();

		NODE_TURN = load("models/dungeon/", "node_turn", resourceManager).build();

		NODE_WATER = load("models/dungeon/", "node_water", resourceManager).build();
		NODE_WATER_2 = load("models/dungeon/", "node_water_2", resourceManager).build();

		NODE_WATER_DEAD_END = load("models/dungeon/", "node_water_dead_end", resourceManager).build();
		NODE_WATER_OPEN = load("models/dungeon/", "node_water_open", resourceManager).build();
		NODE_WATER_STRAIGHT = load("models/dungeon/", "node_water_straight", resourceManager).build();
		
		// ---

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

		HashMap<Integer, DungeonModel[]> tempMap = new HashMap<Integer, DungeonModel[]>();

		for (int i = 0; i < 5; i++) {
			ModelCategory stage = ModelCategory.STAGES[i];
			for (ModelCategory nodeType : ModelCategory.NODE_TYPES) {
				
				DungeonCrawl.LOGGER.info("Node type: {}, {}", i, nodeType);
				{
					WeightedRandomInteger.Builder builder = new WeightedRandomInteger.Builder();

					for (DungeonModel model : ModelCategory.getIntersection(tempMap, ModelCategory.NORMAL_NODE, stage,
							nodeType)) {
						DungeonCrawl.LOGGER.info("adding primary node entry ({} {})", stage, nodeType);
						builder.entries.add(
								new WeightedRandomInteger.Builder.IntegerEntry(model.metadata.weights[i], model.id));
					}

					for (ModelCategory secondaryType : ModelCategory.getSecondaryNodeCategories(nodeType)) {
						for (DungeonModel model : ModelCategory.getIntersection(tempMap, ModelCategory.NORMAL_NODE,
								stage, secondaryType)) {
							DungeonCrawl.LOGGER.info("adding secondary node entry: {}, {}", stage, model.id);
							builder.entries.add(new WeightedRandomInteger.Builder.IntegerEntry(1, model.id));
						}
					}

					NODE_MODELS.put(Objects.hash(ModelCategory.NORMAL_NODE, stage, nodeType), builder.build());
				}

				{
					WeightedRandomInteger.Builder builder = new WeightedRandomInteger.Builder();

					for (DungeonModel model : ModelCategory.getIntersection(tempMap, ModelCategory.LARGE_NODE, stage,
							nodeType)) {
						DungeonCrawl.LOGGER.info("adding primary node entry ({} {})", stage, nodeType);
						builder.entries.add(
								new WeightedRandomInteger.Builder.IntegerEntry(model.metadata.weights[i], model.id));
					}

					for (ModelCategory secondaryType : ModelCategory.getSecondaryNodeCategories(nodeType)) {
						for (DungeonModel model : ModelCategory.getIntersection(tempMap, ModelCategory.LARGE_NODE,
								stage, secondaryType)) {
							DungeonCrawl.LOGGER.info("adding secondary node entry: {}, {}", stage, model.id);
							builder.entries.add(new WeightedRandomInteger.Builder.IntegerEntry(1, model.id));
						}
					}

					NODE_MODELS.put(
							Objects.hash((Object[]) new ModelCategory[] { ModelCategory.LARGE_NODE, stage, nodeType }),
							builder.build());
				}

			}
		}

		CORRIDOR_LINKERS = new DungeonModel[] { CORRIDOR_LINKER, CORRIDOR_LINKER_2, CORRIDOR_LINKER_3,
				CORRIDOR_LINKER_4, CORRIDOR_LINKER_5 };

		NODE_CONNECTORS = new DungeonModel[] { NODE_CONNECTOR, NODE_CONNECTOR_2, NODE_CONNECTOR_3, NODE_CONNECTOR_4,
				NODE_CONNECTOR_5 };

		// -Offsets- //

		OFFSETS.put(ENTRANCE.id, new Vec3i(-2, 0, -2));

		OFFSETS.put(NODE_WATER.id, new Vec3i(0, -1, 0));
		OFFSETS.put(NODE_WATER_2.id, new Vec3i(0, -1, 0));
		OFFSETS.put(NODE_WATER_DEAD_END.id, new Vec3i(0, -1, 0));
		OFFSETS.put(NODE_WATER_OPEN.id, new Vec3i(0, -1, 0));
		OFFSETS.put(NODE_WATER_STRAIGHT.id, new Vec3i(0, -1, 0));

		DungeonCrawl.LOGGER.info("Finished model loading.");
	}

	public static DungeonModel load(String directory, String file, IResourceManager resourceManager) {
		DungeonModel model = loadFromFile(directory + file + ".nbt", resourceManager);

		ResourceLocation metadata = DungeonCrawl.locate(directory + "metadata/" + file + ".json");

		if (resourceManager.hasResource(metadata)) {
			DungeonCrawl.LOGGER.info("Loading metadata for {}", file);

			try {
				Metadata data = DungeonCrawl.GSON.fromJson(
						new InputStreamReader(resourceManager.getResource(metadata).getInputStream()), Metadata.class);
				model.loadMetadata(data);
			} catch (Exception e) {
				DungeonCrawl.LOGGER.info("Failed to load metadata for {}", file);
				e.printStackTrace();
			}

		}

		return model;
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

		STAGE_1, STAGE_2, STAGE_3, STAGE_4, STAGE_5, NODE_DEAD_END, NODE_STRAIGHT, NODE_TURN, NODE_OPEN, NODE_FULL,
		NORMAL_NODE, LARGE_NODE, CORRIDOR, CORRIDOR_LINKER, SIDE_ROOM, NODE_CONNECTOR;

		public static final ModelCategory[] STAGES = new ModelCategory[] { STAGE_1, STAGE_2, STAGE_3, STAGE_4,
				STAGE_5 };

		public static final ModelCategory[] NODE_TYPES = new ModelCategory[] { NODE_FULL, NODE_OPEN, NODE_STRAIGHT,
				NODE_TURN, NODE_DEAD_END };

		public final List<DungeonModel> members;

		private ModelCategory() {
			members = Lists.newArrayList();
		}

		public static WeightedRandomInteger get(ModelCategory... categories) {
			int hash = Objects.hash((Object[]) categories);

			if (NODE_MODELS.containsKey(hash)) {
				return NODE_MODELS.get(hash);
			}

			return null;
		}

		public static DungeonModel[] getIntersection(HashMap<Integer, DungeonModel[]> map,
				ModelCategory... categories) {
			int hash = Objects.hash((Object[]) categories);

			if (map.containsKey(hash)) {
				return map.get(hash);
			}

			List<DungeonModel> intersection = Lists.newArrayList();

			mainLoop: for (DungeonModel model : categories[0].members) {
				for (int i = 1; i < categories.length; i++) {
					if (!categories[i].members.contains(model)) {
						continue mainLoop;
					}
				}
				intersection.add(model);
			}

			DungeonModel[] array = intersection.toArray(new DungeonModel[intersection.size()]);
			map.put(hash, array);
			
			DungeonCrawl.LOGGER.info("Intersection of {} is {}", Arrays.toString(categories),  Arrays.toString(array));
			
			return array;
		}

		public static ModelCategory[] getSecondaryNodeCategories(ModelCategory primeCategory) {
			switch (primeCategory) {
			case NODE_FULL:
				return new ModelCategory[0];
			case NODE_DEAD_END:
				return new ModelCategory[] { NODE_TURN, NODE_STRAIGHT, NODE_OPEN, NODE_FULL };
			case NODE_OPEN:
				return new ModelCategory[] { NODE_FULL };
			case NODE_STRAIGHT:
				return new ModelCategory[] { NODE_FULL, NODE_OPEN };
			case NODE_TURN:
				return new ModelCategory[] { NODE_FULL, NODE_OPEN };
			default:
				return null;
			}
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
