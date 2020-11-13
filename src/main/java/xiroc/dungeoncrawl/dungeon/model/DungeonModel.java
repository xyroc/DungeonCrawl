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

import com.google.gson.*;
import net.minecraft.util.Direction;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Rotation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MutableBoundingBox;
import net.minecraft.util.math.vector.Vector3i;
import xiroc.dungeoncrawl.DungeonCrawl;
import xiroc.dungeoncrawl.dungeon.model.DungeonModels.ModelCategory;
import xiroc.dungeoncrawl.dungeon.treasure.Treasure;
import xiroc.dungeoncrawl.util.DirectionalBlockPos;
import xiroc.dungeoncrawl.util.JSONUtils;
import xiroc.dungeoncrawl.util.Orientation;
import xiroc.dungeoncrawl.util.WeightedRandom;

import javax.annotation.Nullable;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

public class DungeonModel {

    public ResourceLocation location;

    public Integer id;
    public int width, height, length;

    public DungeonModelBlock[][][] model;

    @Nullable
    public FeaturePosition[] featurePositions;

    @Nullable
    public List<WeightedRandom<MultipartModelData>> multipartData;

    @Nullable
    public Metadata metadata;

    public DungeonModel(DungeonModelBlock[][][] model, FeaturePosition[] featurePositions) {
        this.model = model;
        this.width = model.length;
        this.height = model[0].length;
        this.length = model[0][0].length;
        this.featurePositions = featurePositions;
    }

    public void setLocation(ResourceLocation location) {
        this.location = location;
    }

    public DungeonModel setId(int id) {
        DungeonModels.MODELS.put(id, this);
        this.id = id;
        return this;
    }

    public void loadMetadata(Metadata metadata) {
        this.metadata = metadata;

        this.id = metadata.id;

        DungeonModels.MODELS.put(id, this);

        if (metadata.type != null) {
            metadata.type.members.add(this);
        }

        if (metadata.offset != null) {
            DungeonModels.OFFSETS.put(id, metadata.offset);
        }

        if (metadata.treasureType != null) {
            Treasure.MODEL_TREASURE_TYPES.put(id, metadata.treasureType);
        }

        if (metadata.size != null) {
            metadata.size.members.add(this);
        }

        if (metadata.stages != null) {
            for (int stage : metadata.stages) {
                ModelCategory.getCategoryForStage(stage - 1).members.add(this);
            }
        }

        if (metadata.multipartData != null) {
            this.multipartData = metadata.multipartData;
        }
    }

    public MutableBoundingBox createBoundingBox(int x, int y, int z, Rotation rotation) {
        switch (rotation) {
            case NONE:
            case CLOCKWISE_180:
                return new MutableBoundingBox(x, y, z, x + width, y + height, z + length);
            case CLOCKWISE_90:
            case COUNTERCLOCKWISE_90:
                return new MutableBoundingBox(x, y, z, x + length, y + height, z + width);
            default:
                DungeonCrawl.LOGGER.warn("Unknown rotation: {}", rotation);
                return new MutableBoundingBox(x, y, z, x + width, y + height, z + length);
        }
    }

    public MutableBoundingBox createBoundingBox(BlockPos origin, Rotation rotation) {
        switch (rotation) {
            case NONE:
            case CLOCKWISE_180:
                return new MutableBoundingBox(origin.getX(), origin.getY(), origin.getZ(), origin.getX() + width, origin.getY() + height, origin.getZ() + length);
            case CLOCKWISE_90:
            case COUNTERCLOCKWISE_90:
                return new MutableBoundingBox(origin.getX(), origin.getY(), origin.getZ(), origin.getX() + length, origin.getY() + height, origin.getZ() + width);
            default:
                DungeonCrawl.LOGGER.warn("Unknown rotation: {}", rotation);
                return new MutableBoundingBox(origin.getX(), origin.getY(), origin.getZ(), origin.getX() + width, origin.getY() + height, origin.getZ() + length);
        }
    }

    @Override
    public String toString() {
        return "{" + id + (metadata != null ? ", " + ((metadata.type != null ? metadata.type.toString() : "-") + (metadata.stages != null ? ", " + Arrays.toString(metadata.stages) : "")) + "}" : "}");
    }

    public static class FeaturePosition {

        public Vector3i position;
        public Direction facing;

        public FeaturePosition(int x, int y, int z) {
            this.position = new Vector3i(x, y, z);
        }

        public FeaturePosition(int x, int y, int z, Direction facing) {
            this.position = new Vector3i(x, y, z);
            this.facing = facing;
        }

        public FeaturePosition rotate(Rotation rotation, DungeonModel model) {
            switch (rotation) {
                case CLOCKWISE_90:
                    return new FeaturePosition(model.length - position.getZ() - 1, position.getY(), position.getX(), Orientation.rotateY(facing));
                case CLOCKWISE_180:
                    return new FeaturePosition(model.width - position.getX() - 1, position.getY(), model.length - position.getZ() - 1, Orientation.horizontalOpposite(facing));
                case COUNTERCLOCKWISE_90:
                    return new FeaturePosition(position.getZ(), position.getY(), model.width - position.getX() - 1, Orientation.rotateYCCW(facing));
                default:
                    return this;
            }
        }

        public BlockPos blockPos(int x, int y, int z) {
            return new BlockPos(x + position.getX(), y + position.getY(), z + position.getZ());
        }

        public BlockPos blockPos(int x, int y, int z, Rotation rotation, DungeonModel model) {
            switch (rotation) {
                case CLOCKWISE_90:
                    return new BlockPos(x + model.length - position.getZ() - 1, y + position.getY(), z + position.getX());
                case CLOCKWISE_180:
                    return new BlockPos(x + model.length - position.getZ() - 1, y + position.getY(), z + model.width - position.getX() - 1);
                case COUNTERCLOCKWISE_90:
                    return new BlockPos(x + position.getZ(), y + position.getY(), z + model.width - position.getX() - 1);
                default:
                    return new BlockPos(x + position.getX(), y + position.getY(), z + position.getZ());
            }
        }

        public DirectionalBlockPos directionalBlockPos(int x, int y, int z, Rotation rotation, DungeonModel model) {
            switch (rotation) {
                case CLOCKWISE_90:
                    return new DirectionalBlockPos(x + model.length - position.getZ() - 1, y + position.getY(), z + position.getX(), Orientation.rotateY(facing));
                case CLOCKWISE_180:
                    return new DirectionalBlockPos(x + model.width - position.getX() - 1, y + position.getY(), z + model.length - position.getZ() - 1, Orientation.horizontalOpposite(facing));
                case COUNTERCLOCKWISE_90:
                    return new DirectionalBlockPos(x + position.getZ(), y + position.getY(), z + model.width - position.getX() - 1, Orientation.rotateYCCW(facing));
                default:
                    return new DirectionalBlockPos(x + position.getX(), y + position.getY(), z + position.getZ(), facing);
            }
        }

        public DirectionalBlockPos directionalBlockPos(int x, int y, int z) {
            return new DirectionalBlockPos(x + position.getX(), y + position.getY(), z + position.getZ(), this.facing);
        }

        public DirectionalBlockPos directionalBlockPos(int x, int y, int z, Direction facing) {
            return new DirectionalBlockPos(x + position.getX(), y + position.getY(), z + position.getZ(), facing);
        }

    }

    public static class Metadata {

        public ModelCategory type, size;

        public Integer id;

        public DungeonModelFeature feature;
        public DungeonModelFeature.Metadata featureMetadata;

        public Treasure.Type treasureType;

        public List<WeightedRandom<MultipartModelData>> multipartData;

        public boolean variation;

        public Vector3i offset;

        public int[] stages, weights;

        private Metadata(ModelCategory type, ModelCategory size, Integer id, DungeonModelFeature feature, DungeonModelFeature.Metadata featureMetadata,
                         Vector3i offset, Treasure.Type treasureType, List<WeightedRandom<MultipartModelData>> multipartData,
                         boolean variation, int[] stages, int[] weights) {
            this.type = type;
            this.size = size;
            this.id = id;
            this.feature = feature;
            this.featureMetadata = featureMetadata;
            this.offset = offset;
            this.multipartData = multipartData;
            this.treasureType = treasureType;
            this.variation = variation;
            this.stages = stages;
            this.weights = weights;
        }

        public static class Deserializer implements JsonDeserializer<Metadata> {

            @Override
            public Metadata deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context)
                    throws JsonParseException {

                JsonObject object = json.getAsJsonObject();
                String modelType = object.get("type").getAsString();

                int id = object.get("id").getAsInt();

                DungeonModelFeature feature = null;
                DungeonModelFeature.Metadata featureMetadata = null;

                int[] stages = null, weights = null;

                Vector3i offset = null;

                boolean variation = false;

                JsonObject data = null;

                Treasure.Type treasureType = null;

                List<WeightedRandom<MultipartModelData>> multipartData = null;

                if (object.has("data")) {
                    data = object.getAsJsonObject("data");

                    stages = data.has("stages") ? DungeonCrawl.GSON.fromJson(data.get("stages"), int[].class) : null;
                    weights = data.has("weights") ? DungeonCrawl.GSON.fromJson(data.get("weights"), int[].class) : null;

                    offset = data.has("offset") ? JSONUtils.getOffset(data.getAsJsonObject("offset")) : null;

                    if (data.has("feature")) {
                        JsonObject featureData = data.getAsJsonObject("feature");
                        feature = DungeonModelFeature.getFromName(featureData.get("type").getAsString());
                        featureMetadata = new DungeonModelFeature.Metadata(featureData);
                    }

                    if (data.has("variation")) {
                        variation = data.get("variation").getAsBoolean();
                    }

                    if (data.has("treasure_type")) {
                        try {
                            treasureType = Treasure.Type.valueOf(data.get("treasure_type").getAsString().toUpperCase(Locale.ROOT));
                        } catch (IllegalArgumentException e) {
                            DungeonCrawl.LOGGER.error("Invalid treasure type {} for model {}", data.get("treasure_type"), id);
                            e.printStackTrace();
                        }
                    }

                    if (data.has("multipart")) {
                        JsonArray array = data.getAsJsonArray("multipart");
                        int size = array.size();
                        if (size > 0) {
                            multipartData = new ArrayList<>();

                            for (int i = 0; i < size; i++) {
                                JsonArray array1 = array.get(i).getAsJsonArray();
                                WeightedRandom.Builder<MultipartModelData> builder = new WeightedRandom.Builder<>();

                                array1.forEach((element1) -> {
                                    JsonObject object1 = element1.getAsJsonObject();
                                    MultipartModelData multipartModelData = MultipartModelData.fromJson(object1);
                                    builder.add(multipartModelData, JSONUtils.getWeightOrDefault(object1));
                                    if (multipartModelData != MultipartModelData.EMPTY) {
                                        DungeonModels.REFERENCES_TO_UPDATE.add(multipartModelData);
                                    }
                                });

                                multipartData.add(builder.build());
                            }
                        }
                    }
                }

                if (modelType.equalsIgnoreCase("node")) {
                    ModelCategory size;
                    if (data != null) {
                        String sizeString = data.get("size").getAsString(), typeString = data.get("type").getAsString();
                        if (sizeString.equals("large")) {
                            size = ModelCategory.LARGE_NODE;
                        } else if (sizeString.equals("normal")) {
                            size = ModelCategory.NORMAL_NODE;
                        } else {
                            throw new JsonParseException("Unknown node size \" " + sizeString + "\"");
                        }
                        return new Metadata(ModelCategory.valueOf("NODE_" + typeString.toUpperCase(Locale.ROOT)), size, id, feature, featureMetadata, offset, treasureType, multipartData, variation, stages, weights);
                    } else {
                        throw new RuntimeException("Missing metadata for a node model. (ID: " + id + ")");
                    }
                } else {
                    return new Metadata(getModelCategory(modelType), null, id, feature, featureMetadata, offset, treasureType, multipartData, variation, stages, weights);
                }
            }

            @Nullable
            private static ModelCategory getModelCategory(String name) {
                if (name.toLowerCase().equals("normal")) {
                    return null;
                }
                try {
                    return ModelCategory.valueOf(name.toUpperCase());
                } catch (IllegalArgumentException e) {
                    DungeonCrawl.LOGGER.warn("Unknown model type: {}", name);
                    return null;
                }
            }
        }
    }
}
