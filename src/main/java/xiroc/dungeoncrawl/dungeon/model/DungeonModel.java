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

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import net.minecraft.util.Direction;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Rotation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MutableBoundingBox;
import net.minecraft.util.math.Vec3i;
import xiroc.dungeoncrawl.DungeonCrawl;
import xiroc.dungeoncrawl.dungeon.treasure.Treasure;
import xiroc.dungeoncrawl.util.DirectionalBlockPos;
import xiroc.dungeoncrawl.util.JSONUtils;
import xiroc.dungeoncrawl.util.Orientation;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

public class DungeonModel {

//    public static final DungeonModel EMPTY = new DungeonModel(new DungeonModelBlock[0][0][0], null);

    public ResourceLocation location;

    private String key;
    public Integer id; // ID's are no longer the main way to identify models. Kept only for backwards compatibility.

    public int width, height, length;

    public final List<DungeonModelBlock> blocks;
//    public DungeonModelBlock[][][] model;

    @Nullable
    public FeaturePosition[] featurePositions;

    @Nullable
    public List<MultipartModelData> multipartData;

    @Nullable
    public Metadata metadata;

    public DungeonModel(List<DungeonModelBlock> blocks, @Nullable FeaturePosition[] featurePositions, int width, int height, int length) {
        this.blocks = blocks;
        this.featurePositions = featurePositions;
        this.width = width;
        this.height = height;
        this.length = length;
    }

    public List<DungeonModelBlock> getBlocks() {
        return blocks;
    }

    public void setLocation(ResourceLocation location) {
        this.location = location;
    }

    public DungeonModel setId(int id) {
        DungeonModels.LEGACY_MODELS.put(id, this);
        this.id = id;
        return this;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getKey() {
        return key;
    }

    public void loadMetadata(Metadata metadata) {
        this.metadata = metadata;

        if (metadata.id != null) {
            this.id = metadata.id;
            DungeonModels.LEGACY_MODELS.put(id, this);
        }

        if (metadata.type != null) {
            metadata.type.members.add(this);
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
                return new MutableBoundingBox(x, y, z, x + width - 1, y + height - 1, z + length - 1);
            case CLOCKWISE_90:
            case COUNTERCLOCKWISE_90:
                return new MutableBoundingBox(x, y, z, x + length - 1, y + height - 1, z + width - 1);
            default:
                DungeonCrawl.LOGGER.warn("Unknown rotation: {}", rotation);
                return new MutableBoundingBox(x, y, z, x + width - 1, y + height - 1, z + length - 1);
        }
    }

    public MutableBoundingBox createBoundingBoxWithOffset(int x, int y, int z, Rotation rotation) {
        Vec3i offset = getOffset(rotation);
        return createBoundingBox(x + offset.getX(), y + offset.getY(), z + offset.getZ(), rotation);
    }

    public MutableBoundingBox createBoundingBox(BlockPos origin, Rotation rotation) {
        switch (rotation) {
            case NONE:
            case CLOCKWISE_180:
                return new MutableBoundingBox(origin.getX(), origin.getY(), origin.getZ(), origin.getX() + width - 1, origin.getY() + height - 1, origin.getZ() + length - 1);
            case CLOCKWISE_90:
            case COUNTERCLOCKWISE_90:
                return new MutableBoundingBox(origin.getX(), origin.getY(), origin.getZ(), origin.getX() + length - 1, origin.getY() + height - 1, origin.getZ() + width - 1);
            default:
                DungeonCrawl.LOGGER.warn("Unknown rotation: {}", rotation);
                return new MutableBoundingBox(origin.getX(), origin.getY(), origin.getZ(), origin.getX() + width - 1, origin.getY() + height - 1, origin.getZ() + length - 1);
        }
    }

    public Treasure.Type getTreasureType() {
        if (metadata != null && metadata.treasureType != null) {
            return metadata.treasureType;
        }
        return Treasure.Type.DEFAULT;
    }

    public Vec3i getOffset(Rotation rotation) {
        if (metadata != null && metadata.offset != null) {
            if (metadata.rotatedOffset != null && ((rotation.ordinal()) & 1) == 1) {
                return metadata.rotatedOffset;
            } else {
                return metadata.offset;
            }
        }
        return DungeonModels.NO_OFFSET;
    }

    @Override
    public String toString() {
        return "{" + id + (metadata != null ? ", " + ((metadata.type != null ? metadata.type.toString() : "generic") + (metadata.stages != null ? ", " + Arrays.toString(metadata.stages) : "[]")) + "}" : "}");
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

        public Vec3i offset, rotatedOffset;

        public int[] stages, weights;

        private Metadata(ModelCategory type) {
            this.type = type;
        }

        public static Metadata fromJson(JsonObject object, ResourceLocation file) throws JsonParseException {
            String modelType = object.get("type").getAsString();

            Metadata metadata;

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

                    metadata = new Metadata(ModelCategory.valueOf("NODE_" + typeString.toUpperCase(Locale.ROOT)));
                    metadata.setSize(size);
                } else {
                    throw new JsonParseException("Missing node data in node model metadata file " + file.toString());
                }
            } else {
                metadata = new Metadata(getModelCategory(modelType));
            }

            if (object.has("id")) {
                metadata.setId(object.get("id").getAsInt());
            }

            if (object.has("stages")) {
                metadata.setStages(DungeonCrawl.GSON.fromJson(object.get("stages"), int[].class));
            }

            if (object.has("weights")) {
                metadata.setWeights(DungeonCrawl.GSON.fromJson(object.get("weights"), int[].class));
            }

            if (object.has("offset")) {
                JsonObject offset = object.getAsJsonObject("offset");
                metadata.setOffset(JSONUtils.getOffset(offset), offset.has("rotate") && offset.get("rotate").getAsBoolean());
            }

            if (object.has("feature")) {
                JsonObject featureData = object.getAsJsonObject("feature");
                metadata.setFeature(DungeonModelFeature.getFromName(featureData.get("type").getAsString()));
                metadata.setFeatureMetadata(new DungeonModelFeature.Metadata(featureData));
            }

            if (object.has("variation")) {
                metadata.setVariation(object.get("variation").getAsBoolean());
            }

            if (object.has("treasure_type")) {
                try {
                    metadata.setLoot(Treasure.Type.valueOf(object.get("treasure_type").getAsString().toUpperCase(Locale.ROOT)));
                } catch (IllegalArgumentException e) {
                    DungeonCrawl.LOGGER.error("Invalid treasure type {} in metadata file {}", object.get("treasure_type"), file.toString());
                    e.printStackTrace();
                }
            }

            if (object.has("multipart")) {
                JsonArray array = object.getAsJsonArray("multipart");
                if (array.size() > 0) {
                    ArrayList<MultipartModelData> multipartData = new ArrayList<>();
                    for (JsonElement element : array) {
                        MultipartModelData multipartModelData = MultipartModelData.fromJson(element.getAsJsonObject(), file);
                        if (multipartModelData != null) {
                            multipartData.add(multipartModelData);
                        }
                    }
                    metadata.setMultipartData(multipartData);
                }
            }

            return metadata;
        }

        private void setSize(ModelCategory size) {
            this.size = size;
        }

        private void setId(Integer id) {
            this.id = id;
        }

        private void setStages(int[] stages) {
            this.stages = stages;
        }

        private void setWeights(int[] weights) {
            this.weights = weights;
        }

        private void setFeature(DungeonModelFeature feature) {
            this.feature = feature;
        }

        private void setFeatureMetadata(DungeonModelFeature.Metadata featureMetadata) {
            this.featureMetadata = featureMetadata;
        }

        private void setOffset(Vec3i offset, boolean rotatable) {
            this.offset = offset;
            this.rotatedOffset = rotatable ? new Vec3i(offset.getZ(), offset.getY(), offset.getX()) : null;
        }

        private void setLoot(Treasure.Type treasureType) {
            this.treasureType = treasureType;
        }

        private void setMultipartData(List<MultipartModelData> multipartData) {
            this.multipartData = multipartData;
        }

        private void setVariation(boolean variation) {
            this.variation = variation;
        }

        @Nullable
        private static ModelCategory getModelCategory(String name) {
            if (name.equalsIgnoreCase("generic")) {
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
