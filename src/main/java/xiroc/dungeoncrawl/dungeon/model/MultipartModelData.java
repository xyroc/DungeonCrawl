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

import com.google.gson.JsonObject;
import net.minecraft.util.Rotation;
import net.minecraft.util.math.vector.Vector3i;
import xiroc.dungeoncrawl.dungeon.piece.DungeonMultipartModelPiece;
import xiroc.dungeoncrawl.dungeon.piece.DungeonPiece;
import xiroc.dungeoncrawl.util.JSONUtils;
import xiroc.dungeoncrawl.util.Orientation;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Locale;

public class MultipartModelData {

    public static final MultipartModelData EMPTY = new MultipartModelData(-1, DungeonModels.NO_OFFSET, Rotation.NONE);

    public final Vector3i offset;
    public final Rotation rotation;

    /**
     * This will be null for EMPTY.
     */
    @Nullable
    public DungeonModel model;

    @Nullable
    private String key;
    @Nullable
    private Integer id;

    public MultipartModelData(@Nonnull String key, Vector3i offset, Rotation rotation) {
        this.key = key;
        this.offset = offset;
        this.rotation = rotation;
    }

    public MultipartModelData(int id, Vector3i offset, Rotation rotation) {
        this.id = id;
        this.offset = offset;
        this.rotation = rotation;
    }

    public DungeonMultipartModelPiece createMultipartPiece(DungeonPiece parentPiece, DungeonModel parent, Rotation rotation, int x, int y, int z) {
        if (model != null) {
            DungeonMultipartModelPiece piece = new DungeonMultipartModelPiece(null, DungeonPiece.DEFAULT_NBT);
            Vector3i pos = Orientation.rotatedMultipartOffset(parent, model, offset, rotation);
            piece.setRealPosition(x + pos.getX(), y + pos.getY(), z + pos.getZ());
            piece.modelID = model.id;
            piece.rotation = this.rotation.add(rotation);
            piece.stage = parentPiece.stage;
            piece.theme = parentPiece.theme;
            piece.subTheme = parentPiece.subTheme;
            piece.setupBoundingBox();
            return piece;
        } else {
            throw new RuntimeException("Can't create a multipart piece without a model.");
        }
    }

    public void updateReference() {
        if (this == EMPTY) {
            return;
        }
        if (key != null) {
            this.model = DungeonModels.PATH_TO_MODEL.get(key);
        } else if (id != null) {
            this.model = DungeonModels.MODELS.get(id);
        }
        if (model == null) {
            throw new RuntimeException("A multipart model data instance does neither have a valid model key nor a valid model id. Model key: " + key + ", Model id: " + id);
        }
    }

    public static MultipartModelData fromJson(JsonObject object) {
        if (object.has("model_key")) {
            Vector3i offset = object.has("offset") ? JSONUtils.getOffset(object.getAsJsonObject("offset")) : DungeonModels.NO_OFFSET;

            Rotation rotation = object.has("rotation") ? Rotation.valueOf(object.get("rotation").getAsString().toUpperCase(Locale.ROOT)) : Rotation.NONE;

            return new MultipartModelData(object.get("model_key").getAsString(), offset, rotation);
        } else if (object.has("model_id")) {
            Vector3i offset = object.has("offset") ? JSONUtils.getOffset(object.getAsJsonObject("offset")) : DungeonModels.NO_OFFSET;

            Rotation rotation = object.has("rotation") ? Rotation.valueOf(object.get("rotation").getAsString().toUpperCase(Locale.ROOT)) : Rotation.NONE;

            return new MultipartModelData(object.get("model_id").getAsInt(), offset, rotation);
        } else {
            return EMPTY;
        }
    }

}
