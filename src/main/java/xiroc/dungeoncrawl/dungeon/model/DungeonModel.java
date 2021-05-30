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
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Rotation;
import net.minecraft.util.Tuple;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MutableBoundingBox;
import net.minecraft.util.math.Vec3i;
import xiroc.dungeoncrawl.DungeonCrawl;
import xiroc.dungeoncrawl.util.JSONUtils;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

public class DungeonModel {

//    public static final DungeonModel EMPTY = new DungeonModel(new DungeonModelBlock[0][0][0], null);

    private ResourceLocation location;

    private ResourceLocation key;
    public Integer id; // ID's are no longer the main way to identify models. Kept only for backwards compatibility.

    public int width, height, length;

    public final List<DungeonModelBlock> blocks;

    @Nullable
    public List<MultipartModelData> multipartData;

    @Nullable
    private Metadata metadata;

    public DungeonModel(List<DungeonModelBlock> blocks, int width, int height, int length) {
        this.blocks = blocks;
        this.width = width;
        this.height = height;
        this.length = length;
    }

    public List<DungeonModelBlock> getBlocks() {
        return blocks;
    }

    public DungeonModel setId(int id) {
        DungeonModels.ID_TO_MODEL.put(id, this);
        this.id = id;
        return this;
    }

    public void setKey(ResourceLocation key) {
        this.key = key;
    }

    public void setLocation(ResourceLocation location) {
        this.location = location;
    }

    public ResourceLocation getKey() {
        return key;
    }

    public ResourceLocation getLocation() {
        return location;
    }

    @Nullable
    public Metadata getMetadata() {
        return metadata;
    }

    public void loadMetadata(Metadata metadata) {
        this.metadata = metadata;

        if (metadata.id != null) {
            this.id = metadata.id;
            DungeonModels.ID_TO_MODEL.put(id, this);
        }

        if (metadata.loot != null) {
            metadata.loot.forEach((loot) -> {
                for (DungeonModelBlock block : blocks) {
                    if (block.position.equals(loot.getA())) {
                        block.lootTable = loot.getB();
                        return;
                    }
                }
            });
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
                DungeonCrawl.LOGGER.warn("Unknown piece rotation: {}", rotation);
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
                DungeonCrawl.LOGGER.warn("Unknown piece rotation: {}", rotation);
                return new MutableBoundingBox(origin.getX(), origin.getY(), origin.getZ(), origin.getX() + width - 1, origin.getY() + height - 1, origin.getZ() + length - 1);
        }
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
        return "{key=" + key + "}";
    }

    public static class Metadata {

        @Nullable
        public Integer id;

        @Nullable
        public List<MultipartModelData> multipartData;

        public boolean variation;

        @Nullable
        private Vec3i offset, rotatedOffset;

        @Nullable
        public DungeonModelFeature[] features;

        @Nullable
        public ArrayList<Tuple<Vec3i, ResourceLocation>> loot;

        private Metadata() {
        }

        public static Metadata fromJson(JsonObject object, ResourceLocation file) throws JsonParseException {
            Metadata metadata = new Metadata();

            if (object.has("id")) {
                metadata.setId(object.get("id").getAsInt());
            }

            if (object.has("offset")) {
                JsonObject offset = object.getAsJsonObject("offset");
                metadata.setOffset(JSONUtils.getOffset(offset), offset.has("rotate") && offset.get("rotate").getAsBoolean());
            }

            if (object.has("features")) {
                JsonArray array = object.getAsJsonArray("features");
                DungeonModelFeature[] features = new DungeonModelFeature[array.size()];
                for (int i = 0; i < array.size(); i++) {
                    features[i] = DungeonModelFeature.fromJson(array.get(i).getAsJsonObject(), file);
                }
                metadata.setFeatures(features);
            }

            if (object.has("variation")) {
                metadata.setVariation(object.get("variation").getAsBoolean());
            }

            if (object.has("loot")) {
                JsonArray array = object.getAsJsonArray("loot");
                if (array.size() > 0) {
                    metadata.loot = new ArrayList<>();
                    array.forEach((element) -> {
                        JsonObject instance = element.getAsJsonObject();
                        Vec3i pos = JSONUtils.getOffset(instance.getAsJsonObject("pos"));
                        ResourceLocation lootTable = new ResourceLocation(instance.get("loot_table").getAsString());
                        metadata.loot.add(new Tuple<>(pos, lootTable));
                    });
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

        private void setId(@Nullable Integer id) {
            this.id = id;
        }

        private void setFeatures(@Nullable DungeonModelFeature[] features) {
            this.features = features;
        }

        private void setOffset(Vec3i offset, boolean rotatable) {
            this.offset = offset;
            this.rotatedOffset = rotatable ? new Vec3i(offset.getZ(), offset.getY(), offset.getX()) : null;
        }

        private void setMultipartData(@Nullable List<MultipartModelData> multipartData) {
            this.multipartData = multipartData;
        }

        private void setVariation(boolean variation) {
            this.variation = variation;
        }

    }

}
