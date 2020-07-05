package xiroc.dungeoncrawl.dungeon.model;

/*
 * DungeonCrawl (C) 2019 - 2020 XYROC (XIROC1337), All Rights Reserved
 */

import com.google.common.collect.Lists;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.NBTSizeTracker;
import net.minecraft.resources.IResourceManager;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.Vec3i;
import org.jline.utils.InputStreamReader;
import xiroc.dungeoncrawl.DungeonCrawl;
import xiroc.dungeoncrawl.config.Config;
import xiroc.dungeoncrawl.dungeon.model.DungeonModel.Metadata;
import xiroc.dungeoncrawl.util.WeightedIntegerEntry;
import xiroc.dungeoncrawl.util.WeightedRandomInteger;

import java.io.DataInputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

public class DungeonModels {

    public static final HashMap<Integer, DungeonModel> MODELS = new HashMap<>();

    public static final HashMap<Integer, WeightedRandomInteger> WEIGHTED_MODELS = new HashMap<>();

    public static final HashMap<String, DungeonModel> NAME_TO_MODEL = Config.ENABLE_TOOLS.get() ? new HashMap<>() : null;

    public static final HashMap<Integer, Vec3i> OFFSETS = new HashMap<>();

    public static final Vec3i NO_OFFSET = new Vec3i(0, 0, 0);

    public static DungeonModel CORRIDOR, CORRIDOR_2, CORRIDOR_3, CORRIDOR_STONE, CORRIDOR_ROOM;
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
    public static DungeonModel NODE, NODE_2, NODE_FORK;
    public static DungeonModel NODE_CATACOMB_DEAD_END, NODE_CATACOMB_TURN;
    public static DungeonModel NODE_TURN, NODE_WATER, NODE_WATER_2, NODE_WATER_DEAD_END, NODE_WATER_STRAIGHT,
            NODE_WATER_FORK;
    public static DungeonModel NODE_CONNECTOR, NODE_CONNECTOR_2, NODE_CONNECTOR_3, NODE_CONNECTOR_4, NODE_CONNECTOR_5;
    public static DungeonModel PRISON_CELL;
    public static DungeonModel SECRET_ROOM;
    public static DungeonModel SPAWNER_ROOM;
    public static DungeonModel STAIRCASE, STAIRS_BOTTOM, STAIRS_BOTTOM_2, STAIRS_TOP;
    public static DungeonModel STARTER_ROOM;

    public static synchronized void load(IResourceManager resourceManager) {
        DungeonCrawl.LOGGER.info("Loading all models...");

        MODELS.clear();
        WEIGHTED_MODELS.clear();

        if (NAME_TO_MODEL != null) {
            NAME_TO_MODEL.clear();
        }

        CORRIDOR_LIGHT_FEATURE = loadFromFile("models/dungeon/corridor/corridor_light.nbt", resourceManager).build().setId(9);

        FOOD_SIDE_ROOM = loadFromFile("models/dungeon/room/food_side_room.nbt", resourceManager).build().setId(24);

        LARGE_CORRIDOR_START = loadFromFile("models/dungeon/corridor/large_corridor_start.nbt", resourceManager).build().setId(28);
        LARGE_CORRIDOR_STRAIGHT = loadFromFile("models/dungeon/corridor/large_corridor_straight.nbt", resourceManager).build().setId(29);
        LARGE_CORRIDOR_TURN = loadFromFile("models/dungeon/corridor/large_corridor_turn.nbt", resourceManager).build().setId(30);
        LARGE_CORRIDOR_OPEN = loadFromFile("models/dungeon/corridor/large_corridor_open.nbt", resourceManager).build().setId(31);

        LOOT_ROOM = loadFromFile("models/dungeon/loot_room.nbt", resourceManager).build().setId(35);

        // Models with metadata

        load("models/dungeon/corridor/", "corridor", resourceManager).build();
        load("models/dungeon/corridor/", "corridor_2", resourceManager).build();
        load("models/dungeon/corridor/", "corridor_3", resourceManager).build();
        load("models/dungeon/corridor/", "stone_corridor", resourceManager).build();
        load("models/dungeon/corridor/", "corridor_room", resourceManager).build();

        ENTRANCE = load("models/dungeon/entrance/", "rogue_entrance", resourceManager).build();

        CAKE_ROOM = load("models/dungeon/corridor/linker/", "cake_room", resourceManager).build();
        CORRIDOR_LINKER = load("models/dungeon/corridor/linker/", "corridor_linker", resourceManager).build();
        CORRIDOR_LINKER_2 = load("models/dungeon/corridor/linker/", "corridor_linker_2", resourceManager).build();
        CORRIDOR_LINKER_3 = load("models/dungeon/corridor/linker/", "corridor_linker_3", resourceManager).build();
        CORRIDOR_LINKER_4 = load("models/dungeon/corridor/linker/", "corridor_linker_4", resourceManager).build();
        CORRIDOR_LINKER_5 = load("models/dungeon/corridor/linker/", "corridor_linker_5", resourceManager).build();

        FORGE = load("models/dungeon/node/", "forge", resourceManager).build();

        LARGE_NODE = load("models/dungeon/node/", "large_node", resourceManager).build();
        LARGE_NODE_LIBRARY = load("models/dungeon/node/", "large_node_library", resourceManager).build();

        NODE = load("models/dungeon/node/", "node", resourceManager).build();
        NODE_2 = load("models/dungeon/node/", "node_2", resourceManager).build();
        NODE_FORK = load("models/dungeon/node/", "node_fork", resourceManager).build();

        NODE_CATACOMB_DEAD_END = load("models/dungeon/node/", "node_catacomb_dead_end", resourceManager).build();
        NODE_CATACOMB_TURN = load("models/dungeon/node/", "node_catacomb_turn", resourceManager).build();

        NODE_TURN = load("models/dungeon/node/", "node_turn", resourceManager).build();

        NODE_WATER = load("models/dungeon/node/", "node_water", resourceManager).build();
        NODE_WATER_2 = load("models/dungeon/node/", "node_water_2", resourceManager).build();

        NODE_WATER_DEAD_END = load("models/dungeon/node/", "node_water_dead_end", resourceManager).build();
        NODE_WATER_FORK = load("models/dungeon/node/", "node_water_fork", resourceManager).build();
        NODE_WATER_STRAIGHT = load("models/dungeon/node/", "node_water_straight", resourceManager).build();

        load("models/dungeon/node/", "node_prison_fork", resourceManager).build();

        NODE_CONNECTOR = load("models/dungeon/node/connector/", "node_connector", resourceManager).build();
        NODE_CONNECTOR = load("models/dungeon/node/connector/", "node_connector_2", resourceManager).build();
        NODE_CONNECTOR = load("models/dungeon/node/connector/", "node_connector_3", resourceManager).build();
        NODE_CONNECTOR = load("models/dungeon/node/connector/", "node_connector_4", resourceManager).build();
        NODE_CONNECTOR = load("models/dungeon/node/connector/", "node_connector_5", resourceManager).build();

        // - - -

        PRISON_CELL = loadFromFile("models/dungeon/prison_cell.nbt", resourceManager).build().setId(64);

        SECRET_ROOM = loadFromFile("models/dungeon/room/secret_room.nbt", resourceManager).build().setId(70);

        //SPAWNER_ROOM = loadFromFile("models/dungeon/room/spawner_room.nbt", resourceManager).build().setId(71);
        SPAWNER_ROOM = load("models/dungeon/room/", "spawner_room", resourceManager);

        STAIRCASE = loadFromFile("models/dungeon/staircase.nbt", resourceManager).build().setId(72);
        STAIRS_TOP = loadFromFile("models/dungeon/stairs_top.nbt", resourceManager).build().setId(73);
        STAIRS_BOTTOM = loadFromFile("models/dungeon/stairs_bottom.nbt", resourceManager).build().setId(74);
        STAIRS_BOTTOM_2 = loadFromFile("models/dungeon/stairs_bottom_2.nbt", resourceManager).build().setId(75);

        STARTER_ROOM = loadFromFile("models/dungeon/room/starter_room.nbt", resourceManager).build().setId(76);

        // -End of Model loading- //

        HashMap<Integer, DungeonModel[]> tempMap = new HashMap<>();

        for (int i = 0; i < 5; i++) {
            ModelCategory stage = ModelCategory.STAGES[i];
            for (ModelCategory nodeType : ModelCategory.NODE_TYPES) {

                DungeonCrawl.LOGGER.debug("Node type: {}, {}", i, nodeType);

                // Nodes
                {
                    WeightedRandomInteger.Builder builder = new WeightedRandomInteger.Builder();
                    ModelCategory[] category = new ModelCategory[]{ModelCategory.NORMAL_NODE, stage, nodeType};

                    for (DungeonModel model : ModelCategory.getIntersection(tempMap, category)) {
                        DungeonCrawl.LOGGER.debug("adding primary node entry ({} {})", stage, nodeType);
                        builder.entries.add(
                                new WeightedIntegerEntry(model.metadata.weights[i], model.id));
                    }

                    for (ModelCategory secondaryType : ModelCategory.getSecondaryNodeCategories(nodeType)) {
                        for (DungeonModel model : ModelCategory.getIntersection(tempMap, ModelCategory.NORMAL_NODE,
                                stage, secondaryType)) {
                            DungeonCrawl.LOGGER.debug("adding secondary node entry: {}, {}", stage, model.id);
                            builder.entries.add(new WeightedIntegerEntry(1, model.id));
                        }
                    }

                    WEIGHTED_MODELS.put(Arrays.hashCode(category), builder.build());
                }

                // Large Nodes
                {
                    WeightedRandomInteger.Builder builder = new WeightedRandomInteger.Builder();
                    ModelCategory[] category = new ModelCategory[]{ModelCategory.LARGE_NODE, stage, nodeType};

                    for (DungeonModel model : ModelCategory.getIntersection(tempMap, category)) {
                        DungeonCrawl.LOGGER.debug("adding primary node entry ({} {})", stage, nodeType);
                        builder.entries.add(
                                new WeightedIntegerEntry(model.metadata.weights[i], model.id));
                    }

                    for (ModelCategory secondaryType : ModelCategory.getSecondaryNodeCategories(nodeType)) {
                        for (DungeonModel model : ModelCategory.getIntersection(tempMap, ModelCategory.LARGE_NODE,
                                stage, secondaryType)) {
                            DungeonCrawl.LOGGER.debug("adding secondary node entry: {}, {}", stage, model.id);
                            builder.entries.add(new WeightedIntegerEntry(1, model.id));
                        }
                    }

                    WEIGHTED_MODELS.put(Arrays.hashCode(category), builder.build());
                }
            }
            createWeightedRandomIntegers(tempMap, ModelCategory.CORRIDOR, stage, i);
            createWeightedRandomIntegers(tempMap, ModelCategory.CORRIDOR_LINKER, stage, i);
            createWeightedRandomIntegers(tempMap, ModelCategory.NODE_CONNECTOR, stage, i);
        }

        //OFFSETS.put(ENTRANCE.id, new Vec3i(-2, 0, -2));

        DungeonCrawl.LOGGER.info("Finished model loading.");
    }

    public static DungeonModel load(String directory, String file, IResourceManager resourceManager) {
        DungeonModel model = loadFromFile(directory + file + ".nbt", resourceManager);

        //ResourceLocation metadata = DungeonCrawl.locate(directory + "metadata/" + file + ".json");
        ResourceLocation metadata = DungeonCrawl.locate(directory + file + ".json");

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

//	public static DungeonModel loadFromFile(String path) {
//		DungeonCrawl.LOGGER.info("Loading {}", path);
//
//		try {
//			DataInputStream input = new DataInputStream(
//					DungeonModels.class.getResourceAsStream("/data/dungeoncrawl/" + path));
//			CompoundNBT nbt = new CompoundNBT();
//			nbt.read(input, 16, NBTSizeTracker.INFINITE);
//			DungeonModel model = ModelHandler.getModelFromNBT(nbt);
//			return model;
//		} catch (Exception e) {
//			e.printStackTrace();
//			return null;
//		}
//	}

    public static DungeonModel loadFromFile(String path, IResourceManager resourceManager) {
        DungeonCrawl.LOGGER.info("Loading {}", path);

        try {
            DataInputStream input = new DataInputStream(resourceManager.getResource(DungeonCrawl.locate(path)).getInputStream());
            CompoundNBT nbt = new CompoundNBT();
            nbt.read(input, 16, NBTSizeTracker.INFINITE);
            DungeonModel model = ModelHandler.getModelFromNBT(nbt);
            if (Config.ENABLE_TOOLS.get()) {
                String name = path.substring(path.lastIndexOf("/") + 1, path.indexOf(".nbt"));
                DungeonCrawl.LOGGER.debug("Adding {}", name);
                if (NAME_TO_MODEL.containsKey(name)) {
                    DungeonCrawl.LOGGER.warn("Found multiple models with the same name (\"{}\")", name);
                }
                NAME_TO_MODEL.put(name, model);
            }
            return model;
        } catch (IOException e) {
            e.printStackTrace();
        }

        throw new RuntimeException("Failed to load the model " + path);
    }

    private static void createWeightedRandomIntegers(HashMap<Integer, DungeonModel[]> tempMap, ModelCategory baseCategory, ModelCategory stageCategory, int stage) {
        WeightedRandomInteger.Builder builder = new WeightedRandomInteger.Builder();
        ModelCategory[] categories = new ModelCategory[]{baseCategory, stageCategory};
        for (DungeonModel model : ModelCategory.getIntersection(tempMap, categories)) {
            builder.entries.add(new WeightedIntegerEntry(model.metadata.weights[stage], model.id));
        }
        WEIGHTED_MODELS.put(Arrays.hashCode(categories), builder.build());
    }

    public static Vec3i getOffset(int model) {
        return OFFSETS.getOrDefault(model, NO_OFFSET);
    }

    public enum ModelCategory {

        STAGE_1, STAGE_2, STAGE_3, STAGE_4, STAGE_5, NODE_DEAD_END, NODE_STRAIGHT, NODE_TURN, NODE_FORK, NODE_FULL,
        NORMAL_NODE, LARGE_NODE, CORRIDOR, CORRIDOR_LINKER, SIDE_ROOM, NODE_CONNECTOR;

        public static final ModelCategory[] STAGES = new ModelCategory[]{STAGE_1, STAGE_2, STAGE_3, STAGE_4,
                STAGE_5};

        public static final ModelCategory[] NODE_TYPES = new ModelCategory[]{NODE_FULL, NODE_FORK, NODE_STRAIGHT,
                NODE_TURN, NODE_DEAD_END};

        public final List<DungeonModel> members;

        ModelCategory() {
            members = Lists.newArrayList();
        }

        public static WeightedRandomInteger get(ModelCategory... categories) {
            //int hash = Objects.hash((Object) categories);
            int hash = Arrays.hashCode(categories);

            return WEIGHTED_MODELS.get(hash);
        }

        public static DungeonModel[] getIntersection(HashMap<Integer, DungeonModel[]> map,
                                                     ModelCategory... categories) {
            //int hash = Objects.hash((Object) categories);
            int hash = Arrays.hashCode(categories);

            if (map.containsKey(hash)) {
                return map.get(hash);
            }

            List<DungeonModel> intersection = Lists.newArrayList();

            mainLoop:
            for (DungeonModel model : categories[0].members) {
                for (int i = 1; i < categories.length; i++) {
                    if (!categories[i].members.contains(model)) {
                        continue mainLoop;
                    }
                }
                intersection.add(model);
            }

            DungeonModel[] array = intersection.toArray(new DungeonModel[0]);
            map.put(hash, array);

            DungeonCrawl.LOGGER.debug("Intersection of {} is {}", Arrays.toString(categories), Arrays.toString(array));

            return array;
        }

        public static ModelCategory[] getSecondaryNodeCategories(ModelCategory primeCategory) {
            switch (primeCategory) {
                case NODE_FULL:
                    return new ModelCategory[0];
                case NODE_DEAD_END:
                    return new ModelCategory[]{NODE_TURN, NODE_STRAIGHT, NODE_FORK, NODE_FULL};
                case NODE_FORK:
                    return new ModelCategory[]{NODE_FULL};
                case NODE_STRAIGHT:
                case NODE_TURN:
                    return new ModelCategory[]{NODE_FULL, NODE_FORK};
                default:
                    return null;
            }
        }

        /**
         * @param stage range: [0,4]
         */
        public static ModelCategory getCategoryForStage(int stage) {
            if (stage < 0 || stage > 4)
                return STAGE_1;
            return STAGES[stage];
        }

    }

}
