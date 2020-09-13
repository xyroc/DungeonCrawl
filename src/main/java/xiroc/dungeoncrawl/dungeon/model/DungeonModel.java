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
import net.minecraft.util.Rotation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.vector.Vector3i;
import xiroc.dungeoncrawl.DungeonCrawl;
import xiroc.dungeoncrawl.dungeon.model.DungeonModels.ModelCategory;
import xiroc.dungeoncrawl.dungeon.treasure.Treasure;
import xiroc.dungeoncrawl.util.DirectionalBlockPos;
import xiroc.dungeoncrawl.util.Orientation;

import java.lang.reflect.Type;
import java.util.Arrays;
import java.util.Locale;

public class DungeonModel {

    public Integer id;
    public int width, height, length;

    public DungeonModelBlock[][][] model;

    public FeaturePosition[] featurePositions;

    public Metadata metadata;

    public DungeonModel(DungeonModelBlock[][][] model, FeaturePosition[] featurePositions) {
        this.model = model;
        this.width = model.length;
        this.height = model[0].length;
        this.length = model[0][0].length;
        this.featurePositions = featurePositions;
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
    }

    @Override
    public String toString() {
        return "{" + id + (metadata != null ? ", " + (metadata.type.toString() + (metadata.stages != null ? ", " + Arrays.toString(metadata.stages) : "")) + "}" : "}");
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

        public boolean variation;

        public Vector3i offset;

        public int[] stages, weights;

        private Metadata(ModelCategory type, ModelCategory size, Integer id, DungeonModelFeature feature, DungeonModelFeature.Metadata featureMetadata, Vector3i offset, Treasure.Type treasureType, boolean variation, int[] stages, int[] weights) {
            this.type = type;
            this.size = size;
            this.id = id;
            this.feature = feature;
            this.featureMetadata = featureMetadata;
            this.offset = offset;
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

                if (object.has("data")) {
                    data = object.getAsJsonObject("data");

                    stages = data.has("stages") ? DungeonCrawl.GSON.fromJson(data.get("stages"), int[].class) : null;
                    weights = data.has("weights") ? DungeonCrawl.GSON.fromJson(data.get("weights"), int[].class) : null;

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
                    offset = data.has("offset") ? getOffset(data.getAsJsonObject("offset")) : null;
                }

                switch (modelType) {
                    case "normal":
                        return new Metadata(null, null, id, feature, featureMetadata, offset, treasureType, variation, stages, weights);
                    case "entrance":
                        return new Metadata(ModelCategory.ENTRANCE, null, id, feature, featureMetadata, offset, treasureType, variation, stages, weights);
                    case "room":
                        return new Metadata(ModelCategory.ROOM, null, id, feature, featureMetadata, offset, treasureType, variation, stages, weights);
                    case "node":
                        ModelCategory size;
                        String sizeString = data.get("size").getAsString(), typeString = data.get("type").getAsString();
                        if (sizeString.equals("large")) {
                            size = ModelCategory.LARGE_NODE;
                        } else if (sizeString.equals("normal")) {
                            size = ModelCategory.NORMAL_NODE;
                        } else {
                            throw new JsonParseException("Unknown node size \" " + sizeString + "\"");
                        }
                        return new Metadata(ModelCategory.valueOf("NODE_" + typeString.toUpperCase(Locale.ROOT)), size, id, feature, featureMetadata, offset, treasureType, variation, stages, weights);
                    case "corridor":
                        return new Metadata(ModelCategory.CORRIDOR, null, id, feature, featureMetadata, offset, treasureType, variation, stages, weights);
                    case "corridor_linker":
                        return new Metadata(ModelCategory.CORRIDOR_LINKER, null, id, feature, featureMetadata, offset, treasureType, variation, stages, weights);
                    case "node_connector":
                        return new Metadata(ModelCategory.NODE_CONNECTOR, null, id, feature, featureMetadata, offset, treasureType, variation, stages, weights);
                    case "side_room":
                        return new Metadata(ModelCategory.SIDE_ROOM, null, id, feature, featureMetadata, offset, treasureType, variation, stages, weights);
                    default:
                        throw new IllegalArgumentException("Unknown model type \"" + modelType + "\"");
                }

            }

            public Vector3i getOffset(JsonObject jsonObject) {
                int x = 0, y = 0, z = 0;
                if (jsonObject.has("x")) {
                    x = jsonObject.get("x").getAsInt();
                }
                if (jsonObject.has("y")) {
                    y = jsonObject.get("y").getAsInt();
                }
                if (jsonObject.has("z")) {
                    z = jsonObject.get("z").getAsInt();
                }
                if (x == 0 && y == 0 && z == 0) {
                    return null;
                }
                return new Vector3i(x, y, z);
            }

        }

    }

}
