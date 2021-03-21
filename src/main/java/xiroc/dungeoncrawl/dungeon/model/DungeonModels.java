/*
        Dungeon Crawl, a procedural dungeon generator for Minecraft 1.14 and later.
        Copyright (C) 2020

        This program is free software: you can redistribute it and/or modify
        it under the terms of the GNU General Public License as published by
        the Free Software Foundation, either version 3 of the License, or
        (at your option) any later version.

        This program is distributed in the hope that it will be useful,
        but WITHOUT ANY WARRANTY; without even the implied warranty of
        MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
        GNU General Public License for more details.

        You should have received a copy of the GNU General Public License
        along with this program.  If not, see <https://www.gnu.org/licenses/>.
*/

package xiroc.dungeoncrawl.dungeon.model;

import com.google.gson.JsonParser;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.CompressedStreamTools;
import net.minecraft.resources.IResourceManager;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Tuple;
import net.minecraft.util.math.Vec3i;
import org.jline.utils.InputStreamReader;
import xiroc.dungeoncrawl.DungeonCrawl;
import xiroc.dungeoncrawl.dungeon.model.DungeonModel.Metadata;
import xiroc.dungeoncrawl.util.WeightedRandom;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;

public class DungeonModels {

    public static final HashMap<String, DungeonModel> MODELS = new HashMap<>();

    public static final HashMap<Integer, DungeonModel> LEGACY_MODELS = new HashMap<>();

    public static final HashMap<Integer, WeightedRandom<DungeonModel>> WEIGHTED_MODELS = new HashMap<>();

    public static final Vec3i NO_OFFSET = new Vec3i(0, 0, 0);

    public static DungeonModel CORRIDOR, CORRIDOR_2, CORRIDOR_3, CORRIDOR_STONE, CORRIDOR_ROOM, CORRIDOR_SECRET_ROOM_ENTRANCE;

    public static DungeonModel DEFAULT_TOWER;
    public static DungeonModel FOOD_SIDE_ROOM;
    public static DungeonModel LOOT_ROOM, OLD_LOOT_ROOM;
    public static DungeonModel PRISON_CELL;
    public static DungeonModel SECRET_ROOM;
    public static DungeonModel STAIRCASE, STAIRS_BOTTOM, STAIRS_BOTTOM_2, STAIRS_TOP;
    public static DungeonModel STARTER_ROOM;

    public static synchronized void load(IResourceManager resourceManager) {
        LEGACY_MODELS.clear();
        MODELS.clear();
        WEIGHTED_MODELS.clear();

        ModelCategory.clear();

        OLD_LOOT_ROOM = loadModel("models/dungeon/old_loot_room.nbt", resourceManager).setId(35);

        loadModel("models/dungeon/corridor/old_corridor_secret_room_entrance.nbt", resourceManager).setId(7);

        PRISON_CELL = loadModel("models/dungeon/prison_cell.nbt", resourceManager).setId(64);

        STAIRCASE = loadModel("models/dungeon/staircase.nbt", resourceManager).setId(72);
        STAIRS_TOP = loadModel("models/dungeon/stairs_top.nbt", resourceManager).setId(73);
        STAIRS_BOTTOM = loadModel("models/dungeon/stairs_bottom.nbt", resourceManager).setId(74);
        STAIRS_BOTTOM_2 = loadModel("models/dungeon/stairs_bottom_2.nbt", resourceManager).setId(75);

        // Models with metadata
        FOOD_SIDE_ROOM = load("models/dungeon/room/", "food_side_room", resourceManager);
        LOOT_ROOM = load("models/dungeon/", "loot_room", resourceManager);
        SECRET_ROOM = load("models/dungeon/room/", "secret_room", resourceManager);
        STARTER_ROOM = load("models/dungeon/room/", "starter_room", resourceManager);

        DEFAULT_TOWER = load("models/dungeon/entrance/", "roguelike_tower", resourceManager);
        load("models/dungeon/entrance/", "roguelike_house", resourceManager);
        load("models/dungeon/entrance/", "roguelike_entrance", resourceManager);

        load("models/dungeon/room/", "tnt_trap_side_room", resourceManager);

        load("models/dungeon/corridor/", "corridor", resourceManager);
        load("models/dungeon/corridor/", "corridor_2", resourceManager);
        load("models/dungeon/corridor/", "corridor_3", resourceManager);
        load("models/dungeon/corridor/", "old_corridor", resourceManager);
        load("models/dungeon/corridor/", "old_corridor_3", resourceManager);
        load("models/dungeon/corridor/", "stone_corridor", resourceManager);
        load("models/dungeon/corridor/", "corridor_room", resourceManager);
        load("models/dungeon/corridor/", "corridor_fire", resourceManager);
        load("models/dungeon/corridor/", "corridor_spawner", resourceManager);
        load("models/dungeon/corridor/", "corridor_light", resourceManager);

        CORRIDOR_SECRET_ROOM_ENTRANCE = load("models/dungeon/corridor/", "corridor_secret_room_entrance", resourceManager);

        load("models/dungeon/corridor/dark/", "corridor", resourceManager);
        load("models/dungeon/corridor/dark/", "corridor_2", resourceManager);
        load("models/dungeon/corridor/dark/", "corridor_3", resourceManager);
        load("models/dungeon/corridor/dark/", "corridor_4", resourceManager);
        load("models/dungeon/corridor/dark/", "corridor_spawner", resourceManager);

        load("models/dungeon/corridor/linker/", "cake_room", resourceManager);
        load("models/dungeon/corridor/linker/", "corridor_linker", resourceManager);
        load("models/dungeon/corridor/linker/", "corridor_linker_2", resourceManager);
        load("models/dungeon/corridor/linker/", "corridor_linker_3", resourceManager);
        load("models/dungeon/corridor/linker/", "corridor_linker_4", resourceManager);
        load("models/dungeon/corridor/linker/", "corridor_linker_5", resourceManager);
        load("models/dungeon/corridor/linker/", "catacomb_linker", resourceManager);
        load("models/dungeon/corridor/linker/", "catacomb_linker_hole", resourceManager);

        load("models/dungeon/node/", "forge", resourceManager);
        load("models/dungeon/node/", "node", resourceManager);
        load("models/dungeon/node/", "node_jukebox", resourceManager);
        load("models/dungeon/node/", "node_fork", resourceManager);
        load("models/dungeon/node/", "node_catacomb_dead_end", resourceManager);
        load("models/dungeon/node/", "node_catacomb_turn", resourceManager);
        load("models/dungeon/node/", "node_turn", resourceManager);
        load("models/dungeon/node/", "node_water", resourceManager);
        load("models/dungeon/node/", "node_water_2", resourceManager);
        load("models/dungeon/node/", "node_water_dead_end", resourceManager);
        load("models/dungeon/node/", "node_water_fork", resourceManager);
        load("models/dungeon/node/", "node_water_straight", resourceManager);
        load("models/dungeon/node/", "node_prison_fork", resourceManager);
        load("models/dungeon/node/", "node_prison", resourceManager);
        load("models/dungeon/node/", "large_node", resourceManager);
        load("models/dungeon/node/", "large_node_library", resourceManager);

        load("models/dungeon/node/dark/", "node_lava", resourceManager);
        load("models/dungeon/node/dark/", "node_lava_2", resourceManager);

        load("models/dungeon/node/connector/", "node_connector", resourceManager);
        load("models/dungeon/node/connector/", "node_connector_2", resourceManager);
        load("models/dungeon/node/connector/", "node_connector_3", resourceManager);
        load("models/dungeon/node/connector/", "node_connector_4", resourceManager);
        load("models/dungeon/node/connector/", "node_connector_5", resourceManager);
        load("models/dungeon/node/connector/", "node_connector_catacomb", resourceManager);
        load("models/dungeon/node/connector/", "node_connector_basic", resourceManager);

        load("models/dungeon/room/", "spawner_room", resourceManager);
        load("models/dungeon/room/", "spawner_room_2", resourceManager);
        load("models/dungeon/room/", "spawner_room_material", resourceManager);
        load("models/dungeon/room/", "spawner_room_material_2", resourceManager);

        load("models/dungeon/room/dark/", "spawner_room", resourceManager);

        // -Additional Models- //

        resourceManager.getAllResourceLocations(DungeonCrawl.locate("models/dungeon/multipart").getPath(), (s) -> s.endsWith(".nbt"))
                .forEach((resource) -> load(resource, resourceManager));
        resourceManager.getAllResourceLocations(DungeonCrawl.locate("models/dungeon/additional").getPath(), (s) -> s.endsWith(".nbt"))
                .forEach((resource) -> load(resource, resourceManager));

        // -End of Model loading- //

        HashMap<Integer, DungeonModel[]> tempMap = new HashMap<>();

        for (int i = 0; i < 5; i++) {
            ModelCategory stage = ModelCategory.STAGES[i];
            for (ModelCategory nodeType : ModelCategory.NODE_TYPES) {

                // Nodes
                {
                    WeightedRandom.Builder<DungeonModel> builder = new WeightedRandom.Builder<>();
                    ModelCategory[] category = new ModelCategory[]{ModelCategory.NORMAL_NODE, stage, nodeType};

                    for (DungeonModel model : ModelCategory.getIntersection(tempMap, category)) {
                        builder.entries.add(new Tuple<>(model.metadata.weights[i], model));
                    }

                    for (ModelCategory secondaryType : ModelCategory.getSecondaryNodeCategories(nodeType)) {
                        for (DungeonModel model : ModelCategory.getIntersection(tempMap, ModelCategory.NORMAL_NODE, stage, secondaryType)) {
                            builder.entries.add(new Tuple<>(1, model));
                        }
                    }

                    WEIGHTED_MODELS.put(Arrays.hashCode(category), builder.build());
                }

                // Large Nodes
                {
                    WeightedRandom.Builder<DungeonModel> builder = new WeightedRandom.Builder<>();
                    ModelCategory[] category = new ModelCategory[]{ModelCategory.LARGE_NODE, stage, nodeType};

                    for (DungeonModel model : ModelCategory.getIntersection(tempMap, category)) {
                        builder.entries.add(new Tuple<>(model.metadata.weights[i], model));
                    }

                    for (ModelCategory secondaryType : ModelCategory.getSecondaryNodeCategories(nodeType)) {
                        for (DungeonModel model : ModelCategory.getIntersection(tempMap, ModelCategory.LARGE_NODE, stage, secondaryType)) {
                            builder.entries.add(new Tuple<>(1, model));
                        }
                    }

                    WEIGHTED_MODELS.put(Arrays.hashCode(category), builder.build());
                }
            }
            createWeightedRandomModels(tempMap, ModelCategory.CORRIDOR, stage, i);
            createWeightedRandomModels(tempMap, ModelCategory.CORRIDOR_LINKER, stage, i);
            createWeightedRandomModels(tempMap, ModelCategory.NODE_CONNECTOR, stage, i);
            createWeightedRandomModels(tempMap, ModelCategory.SIDE_ROOM, stage, i);
            createWeightedRandomModels(tempMap, ModelCategory.ROOM, stage, i);
        }

        ModelCategory.CORRIDOR.verifyModelPresence(0, 1, 2, 3, 4);
        ModelCategory.CORRIDOR_LINKER.verifyModelPresence(0, 1, 2, 3, 4);
        ModelCategory.NODE_CONNECTOR.verifyModelPresence(0, 1, 2, 3, 4);
        ModelCategory.ROOM.verifyModelPresence(0, 1, 2, 3, 4);

        ModelCategory.NORMAL_NODE.verifyModelPresence(ModelCategory.NODE_FULL, 0, 1, 2, 3, 4);
        ModelCategory.NORMAL_NODE.verifyModelPresence(ModelCategory.NODE_FORK, 0, 1, 2, 3, 4);
        ModelCategory.NORMAL_NODE.verifyModelPresence(ModelCategory.NODE_STRAIGHT, 0, 1, 2, 3, 4);
        ModelCategory.NORMAL_NODE.verifyModelPresence(ModelCategory.NODE_TURN, 0, 1, 2, 3, 4);
        ModelCategory.NORMAL_NODE.verifyModelPresence(ModelCategory.NODE_DEAD_END, 0, 1, 2, 3, 4);

        ModelCategory.LARGE_NODE.verifyModelPresence(ModelCategory.NODE_FULL, 2, 3, 4);
        ModelCategory.LARGE_NODE.verifyModelPresence(ModelCategory.NODE_FORK, 2, 3, 4);
        ModelCategory.LARGE_NODE.verifyModelPresence(ModelCategory.NODE_STRAIGHT, 2, 3, 4);
        ModelCategory.LARGE_NODE.verifyModelPresence(ModelCategory.NODE_TURN, 2, 3, 4);
        ModelCategory.LARGE_NODE.verifyModelPresence(ModelCategory.NODE_DEAD_END, 2, 3, 4);
    }

    private static void load(ResourceLocation resource, IResourceManager resourceManager) {
        DungeonModel model = loadModel(resource, resourceManager);
        ResourceLocation metadata = new ResourceLocation(resource.getNamespace(),
                resource.getPath().substring(0, resource.getPath().indexOf(".nbt")) + ".json");

        if (resourceManager.hasResource(metadata)) {
            DungeonCrawl.LOGGER.debug("Loading metadata for {}", resource.getPath());
            try {
                Metadata data = Metadata.fromJson(new JsonParser().parse(new InputStreamReader(resourceManager.getResource(metadata).getInputStream())).getAsJsonObject(), metadata);
                model.loadMetadata(data);
            } catch (Exception e) {
                DungeonCrawl.LOGGER.error("Failed to load metadata for {}", resource.getPath());
                e.printStackTrace();
            }
        }
    }

    private static DungeonModel load(String directory, String file, IResourceManager resourceManager) {
        DungeonModel model = loadModel(directory + file + ".nbt", resourceManager);

        ResourceLocation metadata = DungeonCrawl.locate(directory + file + ".json");

        if (resourceManager.hasResource(metadata)) {
            try {
                Metadata data = Metadata.fromJson(new JsonParser().parse(new InputStreamReader(resourceManager.getResource(metadata).getInputStream())).getAsJsonObject(), metadata);
                model.loadMetadata(data);
            } catch (Exception e) {
                DungeonCrawl.LOGGER.error("Failed to load metadata for {}", file);
                e.printStackTrace();
            }
        }

        return model;
    }

    private static DungeonModel loadModel(String path, IResourceManager resourceManager) {
        DungeonCrawl.LOGGER.debug("Loading {}", path);

        try {
            ResourceLocation resource = DungeonCrawl.locate(path);
            CompoundNBT nbt = CompressedStreamTools.readCompressed(resourceManager.getResource(resource).getInputStream());
            DungeonModel model = ModelHandler.loadModelFromNBT(nbt, resource);
//            ModelHandler.writeModelToFile(model, "converted/" + resource.getPath());

            String key = path.substring(15, path.indexOf(".nbt"));
            model.setKey(key);
            MODELS.put(key, model);
            DungeonCrawl.LOGGER.debug("Model {} has key {}", path, key);

            model.setLocation(resource);
            return model;
        } catch (IOException e) {
            e.printStackTrace();
        }

        throw new RuntimeException("Failed to load " + path);
    }

    private static DungeonModel loadModel(ResourceLocation resource, IResourceManager resourceManager) {
        DungeonCrawl.LOGGER.debug("Loading {}", resource.getPath());

        try {
            CompoundNBT nbt = CompressedStreamTools.readCompressed(resourceManager.getResource(resource).getInputStream());
            DungeonModel model = ModelHandler.loadModelFromNBT(nbt, resource);
//            ModelHandler.writeModelToFile(model, "converted/" + resource.getPath());

            String path = resource.getPath();
            String key = path.substring(15, path.indexOf(".nbt"));
            model.setKey(key);
            MODELS.put(key, model);
            DungeonCrawl.LOGGER.debug("Model {} has key {}", path, key);

            model.setLocation(resource);
            return model;
        } catch (IOException e) {
            e.printStackTrace();
        }

        throw new RuntimeException("Failed to load " + resource.getPath());
    }

    private static void createWeightedRandomModels(HashMap<Integer, DungeonModel[]> tempMap, ModelCategory baseCategory, ModelCategory stageCategory, int stage) {
        WeightedRandom.Builder<DungeonModel> builder = new WeightedRandom.Builder<>();
        ModelCategory[] categories = new ModelCategory[]{baseCategory, stageCategory};
        for (DungeonModel model : ModelCategory.getIntersection(tempMap, categories)) {
            builder.entries.add(new Tuple<>(model.metadata.weights[stage], model));
        }
        WEIGHTED_MODELS.put(Arrays.hashCode(categories), builder.build());
    }

}
