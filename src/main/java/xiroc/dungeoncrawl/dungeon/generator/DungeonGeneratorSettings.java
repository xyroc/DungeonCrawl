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

package xiroc.dungeoncrawl.dungeon.generator;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

import java.lang.reflect.Type;

public record DungeonGeneratorSettings(int maxLayers) {

    public static class Serializer implements JsonSerializer<DungeonGeneratorSettings>, JsonDeserializer<DungeonGeneratorSettings> {
        private static final String KEY_MAX_LAYERS = "max_layers";

        @Override
        public DungeonGeneratorSettings deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
            int maxLayers = json.getAsJsonObject().get(KEY_MAX_LAYERS).getAsInt();
            return new DungeonGeneratorSettings(maxLayers);
        }

        @Override
        public JsonElement serialize(DungeonGeneratorSettings src, Type typeOfSrc, JsonSerializationContext context) {
            JsonObject object = new JsonObject();
            object.addProperty(KEY_MAX_LAYERS, src.maxLayers);
            return object;
        }
    }

}
