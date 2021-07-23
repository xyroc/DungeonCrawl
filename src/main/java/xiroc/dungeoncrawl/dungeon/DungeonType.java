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

package xiroc.dungeoncrawl.dungeon;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.minecraft.resources.IResourceManager;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Tuple;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.registries.ForgeRegistries;
import org.lwjgl.system.CallbackI;
import xiroc.dungeoncrawl.DungeonCrawl;
import xiroc.dungeoncrawl.dungeon.generator.DungeonGeneratorSettings;
import xiroc.dungeoncrawl.dungeon.generator.layer.LayerGeneratorSettings;
import xiroc.dungeoncrawl.dungeon.model.DungeonModel;
import xiroc.dungeoncrawl.dungeon.model.DungeonModels;
import xiroc.dungeoncrawl.dungeon.model.ModelSelector;
import xiroc.dungeoncrawl.dungeon.model.MultipartModelData;
import xiroc.dungeoncrawl.exception.DatapackLoadException;
import xiroc.dungeoncrawl.util.JSONUtils;
import xiroc.dungeoncrawl.util.WeightedRandom;

import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;
import java.util.Random;

public class DungeonType {

    private static final Hashtable<ResourceLocation, DungeonType> KEY_TO_TYPE = new Hashtable<>();

    private static final Hashtable<String, WeightedRandom<DungeonType>> BIOME_TO_TYPE = new Hashtable<>();
    private static WeightedRandom<DungeonType> DEFAULT_TYPE;

    private static final String TYPES_DIRECTORY = "dungeon/types";
    private static final String MAPPINGS_DIRECTORY = "dungeon/biome_mappings";

    public final ResourceLocation source;
    public final DungeonGeneratorSettings dungeonSettings;
    public final WeightedRandom<DungeonModel> entrances;

    public final Layer[] layers;

    private DungeonType(ResourceLocation source, DungeonGeneratorSettings dungeonSettings, Layer[] layers, WeightedRandom<DungeonModel> entrances) {
        this.source = source;
        this.dungeonSettings = dungeonSettings;
        this.layers = layers;
        this.entrances = entrances;
    }

    public Layer getLayer(int layer) {
        return layers[Math.min(layers.length - 1, layer)];
    }

    public static void load(IResourceManager resourceManager) {
        resourceManager.getAllResourceLocations(TYPES_DIRECTORY, (path) -> path.endsWith(".json")).forEach((resource) -> {
            try {
                DungeonCrawl.LOGGER.debug("Loading {}", resource);
                JsonObject file = DungeonCrawl.JSON_PARSER.parse(new InputStreamReader(resourceManager.getResource(resource).getInputStream())).getAsJsonObject();

                DungeonType.Builder builder = new DungeonType.Builder(resource);
//                DungeonGeneratorSettings settings = DungeonGeneratorSettings.fromJson(file.getAsJsonObject("settings"), resource);
                builder.settings(new DungeonGeneratorSettings(5));
                builder.entrances(ModelSelector.loadRandom("entrances", file, resource));

                JsonArray layers = file.getAsJsonArray("layers");
                if (layers.size() == 0) {
                    throw new DatapackLoadException("Empty layer list in " + file);
                } else {
                    layers.forEach((element) -> {
                        JsonObject layer = element.getAsJsonObject();
                        String typeKey = layer.get("type").getAsString();

                        if (!DungeonLayerType.NAME_TO_TYPE.containsKey(typeKey)) {
                            throw new DatapackLoadException("Invalid layer type " + typeKey + " in " + resource);
                        }

                        DungeonLayerType layerType = DungeonLayerType.NAME_TO_TYPE.get(layer.get("type").getAsString());
                        LayerGeneratorSettings layerSettings = LayerGeneratorSettings.fromJson(layer.getAsJsonObject("settings"), resource);
                        ModelSelector modelSelector = ModelSelector.fromJson(layer.getAsJsonObject("models"), resource);
                        builder.layer(layerType, layerSettings, modelSelector, getMultipartOverrides(layer, resource));
                    });
                }

                ResourceLocation key = DungeonCrawl.key(resource, TYPES_DIRECTORY, ".json");
                KEY_TO_TYPE.put(key, builder.build());
            } catch (IOException e) {
                DungeonCrawl.LOGGER.error("Failed to load dungeon type " + resource);
                e.printStackTrace();
            }
        });

        WeightedRandom.Builder<DungeonType> defaultType = new WeightedRandom.Builder<>();

        resourceManager.getAllResourceLocations(MAPPINGS_DIRECTORY, (path) -> path.endsWith(".json")).forEach((resource) -> {
            try {
                DungeonCrawl.LOGGER.debug("Loading {}", resource);
                JsonObject file = DungeonCrawl.JSON_PARSER.parse(new InputStreamReader(resourceManager.getResource(resource).getInputStream())).getAsJsonObject();

                if (file.has("conditions")) {
                    JsonObject conditions = file.getAsJsonObject("conditions");
                    if (conditions.has("present")) {
                        JsonArray present = conditions.getAsJsonArray("present");
                        for (JsonElement element : present) {
                            if (!ModList.get().isLoaded(element.getAsString())) return;
                        }
                    }
                    if (conditions.has("absent")) {
                        JsonArray present = conditions.getAsJsonArray("absent");
                        for (JsonElement element : present) {
                            if (ModList.get().isLoaded(element.getAsString())) return;
                        }
                    }
                }

                if (file.has("default")) {
                    addEntries(defaultType, file.getAsJsonArray("default"), resource);
                }

                JsonObject mapping = file.getAsJsonObject("mapping");
                mapping.entrySet().forEach((entry) -> {
                    String biome = entry.getKey();

                    if (!ForgeRegistries.BIOMES.containsKey(new ResourceLocation(biome))) {
                        DungeonCrawl.LOGGER.warn("Unknown biome {} in {}", biome, resource);
                    }

                    BIOME_TO_TYPE.put(biome, dungeonTypeWeightedRandom(entry.getValue().getAsJsonArray(), resource));
                });
            } catch (IOException e) {
                DungeonCrawl.LOGGER.error("Failed to load {}", resource);
                e.printStackTrace();
            }
        });

        DEFAULT_TYPE = defaultType.build();

        if (DEFAULT_TYPE.isEmpty()) {
            throw new DatapackLoadException("No default case was specified in the dungeon type biome mappings.");
        }
    }

    private static WeightedRandom<DungeonType> dungeonTypeWeightedRandom(JsonArray entries, ResourceLocation resource) {
        WeightedRandom.Builder<DungeonType> builder = new WeightedRandom.Builder<>();
        addEntries(builder, entries, resource);
        return builder.build();
    }

    private static void addEntries(WeightedRandom.Builder<DungeonType> builder, JsonArray entries, ResourceLocation resource) {
        entries.forEach((element) -> {
            JsonObject entry = element.getAsJsonObject();
            ResourceLocation key = new ResourceLocation(entry.get("key").getAsString());
            if (!KEY_TO_TYPE.containsKey(key)) {
                throw new DatapackLoadException("Cannot resolve dungeon type " + key + " in " + resource);
            }
            builder.add(KEY_TO_TYPE.get(key), JSONUtils.getWeight(entry));
        });
    }

    public static DungeonType randomType(ResourceLocation biome, Random rand) {
        if (biome == null) {
            return DEFAULT_TYPE.roll(rand);
        } else {
            return BIOME_TO_TYPE.getOrDefault(biome.toString(), DEFAULT_TYPE).roll(rand);
        }
    }

    private static ImmutableMap<ResourceLocation, List<MultipartModelData>> getMultipartOverrides(JsonObject layer, ResourceLocation file) {
        if (layer.has("multipart")) {
            ImmutableMap.Builder<ResourceLocation, List<MultipartModelData>> builder = new ImmutableMap.Builder<>();
            JsonObject multipart = layer.getAsJsonObject("multipart");
            multipart.entrySet().forEach((entry) -> {
                JsonObject object = entry.getValue().getAsJsonObject();
                ResourceLocation target = new ResourceLocation(entry.getKey());
                if (DungeonModels.KEY_TO_MODEL.containsKey(target)) {
                    String action = object.get("action").getAsString();
                    switch (action) {
                        case "override": {
                            List<MultipartModelData> multipartData = DungeonModel.parseMultipartData(object.getAsJsonObject("data"), file);
                            if (multipartData != null) {
                                builder.put(target, multipartData);
                            }
                            break;
                        }
                        case "add": {
                            HashMap<String, Tuple<List<Tuple<MultipartModelData.Instance, Integer>>, List<Tuple<MultipartModelData.Instance, Integer>>>> additions = new HashMap<>();
                            object.getAsJsonObject("additions").entrySet().forEach(((entry1) -> {
                                JsonObject object1 = entry1.getValue().getAsJsonObject();
                                List<Tuple<MultipartModelData.Instance, Integer>> models = object1.has("models")
                                        ? MultipartModelData.getRawInstancesFromJson(object1.getAsJsonArray("models"), file)
                                        : new ArrayList<>(0);
                                List<Tuple<MultipartModelData.Instance, Integer>> alternatives = object1.has("alternatives")
                                        ? MultipartModelData.getRawInstancesFromJson(object1.getAsJsonArray("alternatives"), file)
                                        : new ArrayList<>(0);
                                additions.put(entry1.getKey(), new Tuple<>(models, alternatives));
                            }));

                            DungeonModel model = DungeonModels.KEY_TO_MODEL.get(target);
                            if (model.hasMultipart()) {
                                List<MultipartModelData> multipartData = model.getMultipartData();
                                ImmutableList.Builder<MultipartModelData> newDataBuilder = new ImmutableList.Builder<>();
                                multipartData.forEach((data) -> {
                                    if (additions.containsKey(data.name)) {
                                        Tuple<List<Tuple<MultipartModelData.Instance, Integer>>, List<Tuple<MultipartModelData.Instance, Integer>>> addition = additions.get(data.name);
                                        newDataBuilder.add(data.combine(addition.getA(), addition.getB()));
                                    } else {
                                        newDataBuilder.add(data);
                                    }
                                });
                                builder.put(target, newDataBuilder.build());
                            } else {
                                DungeonCrawl.LOGGER.warn(target + " doesn't have multipart data, but " + file + " adds to it.");
                            }
                            break;
                        }
                        default:
                            throw new DatapackLoadException("Unknown multipart action " + action + " in " + file);
                    }
                } else {
                    throw new DatapackLoadException("Cannot resolve model key " + entry.getKey() + " in " + file);
                }
            });
            return builder.build();
        } else {
            return ImmutableMap.of();
        }
    }

    public static class Layer {

        public final DungeonLayerType layerType;
        public final LayerGeneratorSettings settings;
        public final ModelSelector modelSelector;

        // Custom multipart data for models on this layer.
        private final ImmutableMap<ResourceLocation, List<MultipartModelData>> multipartOverrides;

        private Layer(DungeonLayerType layerType, LayerGeneratorSettings settings, ModelSelector modelSelector, ImmutableMap<ResourceLocation, List<MultipartModelData>> multipartOverrides) {
            this.layerType = layerType;
            this.settings = settings;
            this.modelSelector = modelSelector;
            this.multipartOverrides = multipartOverrides;
        }

        public boolean hasMultipartOverride(DungeonModel model) {
            return multipartOverrides.containsKey(model.getKey());
        }

        public List<MultipartModelData> getMultipartData(DungeonModel model) {
            return multipartOverrides.getOrDefault(model.getKey(), model.getMultipartData());
        }

    }

    public static class Builder {

        private final ResourceLocation source;
        private final List<Layer> layers;
        private WeightedRandom<DungeonModel> entrances;

        private DungeonGeneratorSettings settings;

        public Builder(ResourceLocation source) {
            this.source = source;
            this.layers = new ArrayList<>();
        }

        public Builder entrances(WeightedRandom<DungeonModel> entrances) {
            this.entrances = entrances;
            return this;
        }

        public Builder settings(DungeonGeneratorSettings settings) {
            this.settings = settings;
            return this;
        }

        public Builder layer(DungeonLayerType type, LayerGeneratorSettings settings, ModelSelector modelSelector, ImmutableMap<ResourceLocation, List<MultipartModelData>> multipartOverrides) {
            this.layers.add(new Layer(type, settings, modelSelector, multipartOverrides));
            return this;
        }

        public DungeonType build() {
            if (settings == null || layers.size() == 0) {
                throw new DatapackLoadException("Incomplete Dungeon Type " + source);
            }
            return new DungeonType(source, settings, layers.toArray(new Layer[0]), entrances);
        }

    }

}
