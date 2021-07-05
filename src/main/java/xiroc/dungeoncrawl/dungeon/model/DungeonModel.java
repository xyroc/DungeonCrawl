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

import com.google.common.collect.ImmutableList;
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

    public static final DungeonModel EMPTY = new DungeonModel(DungeonCrawl.locate("empty"), ImmutableList.of(), 0, 0, 0);

    private final ResourceLocation key;

    public final int width, height, length;

    public final ImmutableList<DungeonModelBlock> blocks;

    @Nullable
    private Integer id;

    @Nullable
    private List<MultipartModelData> multipartData;

    private int entranceType;

    private Vec3i offset, rotatedOffset;
    @Nullable
    private DungeonModelFeature[] features;

    private boolean hasId = false;
    private boolean hasFeatures = false;
    private boolean hasMultipart = false;
    private boolean variation = false;

    public DungeonModel(ResourceLocation key, ImmutableList<DungeonModelBlock> blocks, int width, int height, int length) {
        this.key = key;
        this.blocks = blocks;
        this.width = width;
        this.height = height;
        this.length = length;

        this.offset = DungeonModels.NO_OFFSET;
        this.rotatedOffset = DungeonModels.NO_OFFSET;
    }

    public List<DungeonModelBlock> getBlocks() {
        return blocks;
    }

    public void loadMetadata(JsonObject object, ResourceLocation file) throws JsonParseException {
        if (object.has("id")) {
            this.id = object.get("id").getAsInt();
            this.hasId = true;
        }

        if (object.has("offset")) {
            JsonObject offset = object.getAsJsonObject("offset");
            this.offset = JSONUtils.getOffset(offset);
            if (offset.has("rotate") && offset.get("rotate").getAsBoolean()) {
                this.rotatedOffset = new Vec3i(this.offset.getZ(), this.offset.getY(), this.offset.getX());
            } else {
                this.rotatedOffset = this.offset;
            }
        }

        if (object.has("entrance_type")) {
            if (object.get("entrance_type").getAsString().equals("secondary")) {
                entranceType = 1;
            }
        }

        if (object.has("features")) {
            JsonArray array = object.getAsJsonArray("features");
            this.features = new DungeonModelFeature[array.size()];
            for (int i = 0; i < array.size(); i++) {
                this.features[i] = DungeonModelFeature.fromJson(array.get(i).getAsJsonObject(), file);
            }
            this.hasFeatures = true;
        }

        if (object.has("variation")) {
            variation = object.get("variation").getAsBoolean();
        }

        if (object.has("loot")) {
            JsonArray array = object.getAsJsonArray("loot");
            if (array.size() > 0) {
                ArrayList<Tuple<Vec3i, ResourceLocation>> loot = new ArrayList<>();
                array.forEach((element) -> {
                    JsonObject instance = element.getAsJsonObject();
                    Vec3i pos = JSONUtils.getOffset(instance.getAsJsonObject("pos"));
                    ResourceLocation lootTable = new ResourceLocation(instance.get("loot_table").getAsString());
                    loot.add(new Tuple<>(pos, lootTable));
                });
                loot.forEach((l) -> {
                    for (DungeonModelBlock block : blocks) {
                        if (block.position.equals(l.getA())) {
                            block.lootTable = l.getB();
                            return;
                        }
                    }
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
                this.multipartData = multipartData;
                this.hasMultipart = true;
            }
        }
    }

    public DungeonModel setId(int id) {
        DungeonModels.ID_TO_MODEL.put(id, this);
        this.id = id;
        return this;
    }

    public ResourceLocation getKey() {
        return key;
    }

    public int getEntranceType() {
        return entranceType;
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
        if ((rotation.ordinal() & 1) == 1) {
            return rotatedOffset;
        } else {
            return offset;
        }
    }

    public boolean hasId() {
        return hasId;
    }

    public boolean hasFeatures() {
        return hasFeatures;
    }

    public boolean hasMultipart() {
        return hasMultipart;
    }

    public boolean isVariationEnabled() {
        return variation;
    }

    @Nullable
    public DungeonModelFeature[] getFeatures() {
        return features;
    }

    @Nullable
    public List<MultipartModelData> getMultipartData() {
        return multipartData;
    }

    @Nullable
    public Integer getId() {
        return id;
    }

    @Override
    public String toString() {
        return "{key=" + key + "}";
    }

}
