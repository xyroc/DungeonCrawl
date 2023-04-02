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

package xiroc.dungeoncrawl.dungeon.decoration;


import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import xiroc.dungeoncrawl.dungeon.model.DungeonModel;
import xiroc.dungeoncrawl.dungeon.piece.DungeonPiece;

import java.lang.reflect.Type;
import java.util.Random;

public interface DungeonDecoration {
    Gson GSON = new GsonBuilder()
            .registerTypeAdapter(DungeonDecoration.class, new Deserializer())
            .registerTypeAdapter(VineDecoration.class, new VineDecoration.Serializer())
            .registerTypeAdapter(ScatteredDecoration.class, new ScatteredDecoration.Serializer())
            .create();

    void decorate(DungeonModel model, LevelAccessor world, BlockPos pos, Random random, BoundingBox worldGenBounds, BoundingBox structureBounds, DungeonPiece piece);

    static DungeonDecoration deserialize(JsonObject object) {
        return GSON.fromJson(object, DungeonDecoration.class);
    }

    class Deserializer implements JsonDeserializer<DungeonDecoration> {
        @Override
        public DungeonDecoration deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
            JsonObject object = json.getAsJsonObject();
            if (object.has(SharedSerializationConstants.KEY_DECORATION_TYPE)) {
                String type = object.get(SharedSerializationConstants.KEY_DECORATION_TYPE).getAsString().toLowerCase();
                return switch (type) {
                    case SharedSerializationConstants.DECORATION_TYPE_VINES -> GSON.fromJson(object, VineDecoration.class);
                    case SharedSerializationConstants.DECORATION_TYPE_SCATTERED -> GSON.fromJson(object, ScatteredDecoration.class);
                    default -> throw new JsonParseException("Unknown decoration type: " + type);
                };
            } else {
                throw new JsonParseException("Missing decoration type specification");
            }
        }
    }

    interface SharedSerializationConstants {
        String KEY_DECORATION_TYPE = "type";
        String DECORATION_TYPE_VINES = "vines";
        String DECORATION_TYPE_SCATTERED = "scattered";
    }
}