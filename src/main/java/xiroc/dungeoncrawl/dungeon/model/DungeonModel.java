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
import net.minecraft.util.math.Vec3i;
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
    public List<MultipartModelData> multipartData;

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

        public Vec3i position;
        public Direction facing;

        public FeaturePosition(int x, int y, int z) {
            this.position = new Vec3i(x, y, z);
        }

        public FeaturePosition(int x, int y, int z, Direction facing) {
            this.position = new Vec3i(x, y, z);
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

        public List<MultipartModelData> multipartData;

        public boolean variation;

        public Vec3i offset;

        public int[] stages, weights;

        private Metadata(ModelCategory type, ModelCategory size, Integer id, DungeonModelFeature feature, DungeonModelFeature.Metadata featureMetadata,
                         Vec3i offset, Treasure.Type treasureType, List<MultipartModelData> multipartData,
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

        public static Metadata fromJson(JsonObject object, ResourceLocation file) throws JsonParseException {

            String modelType = object.get("type").getAsString();

            int id = object.get("id").getAsInt();

            DungeonModelFeature feature = null;
            DungeonModelFeature.Metadata featureMetadata = null;

            int[] stages, weights;

            Vec3i offset;

            boolean variation = false;

            Treasure.Type treasureType = null;

            List<MultipartModelData> multipartData = null;

            stages = object.has("stages") ? DungeonCrawl.GSON.fromJson(object.get("stages"), int[].class) : null;
            weights = object.has("weights") ? DungeonCrawl.GSON.fromJson(object.get("weights"), int[].class) : null;

            offset = object.has("offset") ? JSONUtils.getOffset(object.getAsJsonObject("offset")) : null;

            if (object.has("feature")) {
                JsonObject featureData = object.getAsJsonObject("feature");
                feature = DungeonModelFeature.getFromName(featureData.get("type").getAsString());
                featureMetadata = new DungeonModelFeature.Metadata(featureData);
            }

            if (object.has("variation")) {
                variation = object.get("variation").getAsBoolean();
            }

            if (object.has("treasure_type")) {
                try {
                    treasureType = Treasure.Type.valueOf(object.get("treasure_type").getAsString().toUpperCase(Locale.ROOT));
                } catch (IllegalArgumentException e) {
                    DungeonCrawl.LOGGER.error("Invalid treasure type {} for model {}", object.get("treasure_type"), id);
                    e.printStackTrace();
                }
            }

            if (object.has("multipart")) {
                JsonArray array = object.getAsJsonArray("multipart");
                int size = array.size();
                if (size > 0) {
                    multipartData = new ArrayList<>();
                    for (JsonElement element : array) {
                        MultipartModelData multipartModelData = MultipartModelData.fromJson(element.getAsJsonObject(), file);
                        if (multipartModelData != null) {
                            multipartData.add(multipartModelData);
                        }
                    }
                }
            }

            if (modelType.equalsIgnoreCase("node")) {
                if (object.has("node")) {
                    JsonObject nodeData = object.getAsJsonObject("node");
                    ModelCategory size;
                    String sizeString = nodeData.get("size").getAsString(), typeString = nodeData.get("type").getAsString();
                    if (sizeString.equals("large")) {
                        size = ModelCategory.LARGE_NODE;
                    } else if (sizeString.equals("normal")) {
                        size = ModelCategory.NORMAL_NODE;
                    } else {
                        throw new JsonParseException("Unknown node size \" " + sizeString + "\"");
                    }
                    return new Metadata(ModelCategory.valueOf("NODE_" + typeString.toUpperCase(Locale.ROOT)), size, id, feature, featureMetadata, offset, treasureType, multipartData, variation, stages, weights);
                } else {
                    throw new JsonParseException("Missing node data in node model metadata file " + file.toString());
                }
            } else {
                return new Metadata(getModelCategory(modelType), null, id, feature, featureMetadata, offset, treasureType, multipartData, variation, stages, weights);
            }
        }

        @Nullable
        private static ModelCategory getModelCategory(String name) {
            if (name.equalsIgnoreCase("normal")) {
                return null;
            }
            try {
                return ModelCategory.valueOf(name.toUpperCase(Locale.ROOT));
            } catch (IllegalArgumentException e) {
                DungeonCrawl.LOGGER.warn("Unknown model type: {} ({})", name, name.toUpperCase(Locale.ROOT));
                return null;
            }
        }


    }

}
