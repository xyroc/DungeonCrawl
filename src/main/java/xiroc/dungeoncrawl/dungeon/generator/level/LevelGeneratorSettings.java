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

package xiroc.dungeoncrawl.dungeon.generator.level;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import xiroc.dungeoncrawl.util.random.value.Range;

import java.lang.reflect.Type;

public class LevelGeneratorSettings {
    /**
     * The minimum and maximum amount of rooms
     */
    public final Range roomCount;

    /**
     * The overall maximum depth for the layout generation.
     */
    public final int maxDepth;

    /**
     * The minimum depth for the stairs to the next layer.
     */
    public final int minStaircaseDepth;

    public LevelGeneratorSettings(Builder builder) {
        this.roomCount = builder.roomCount;
        this.maxDepth = builder.maxDepth;
        this.minStaircaseDepth = builder.minStaircaseDepth;
    }

    public static class Serializer implements JsonSerializer<LevelGeneratorSettings>, JsonDeserializer<LevelGeneratorSettings> {
        private static final String KEY_ROOM_COUNT = "room_count";
        private static final String KEY_MAX_GENERATION_DEPTH = "max_generation_depth";
        private static final String KEY_MIN_STAIRCASE_DEPTH = "min_staircase_depth";

        @Override
        public LevelGeneratorSettings deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
            Builder builder = new Builder();
            JsonObject object = json.getAsJsonObject();
            builder.roomCount = context.deserialize(object.get(KEY_ROOM_COUNT), Range.class);
            builder.maxDepth = object.get(KEY_MAX_GENERATION_DEPTH).getAsInt();
            builder.minStaircaseDepth = object.get(KEY_MIN_STAIRCASE_DEPTH).getAsInt();
            return builder.build();
        }

        @Override
        public JsonElement serialize(LevelGeneratorSettings settings, Type typeOfSrc, JsonSerializationContext context) {
            JsonObject object = new JsonObject();
            object.add(KEY_ROOM_COUNT, context.serialize(settings.roomCount));
            object.addProperty(KEY_MAX_GENERATION_DEPTH, settings.maxDepth);
            object.addProperty(KEY_MIN_STAIRCASE_DEPTH, settings.minStaircaseDepth);
            return object;
        }
    }

    public static class Builder {
        private Range roomCount = null;
        private Integer maxDepth = null;
        private Integer minStaircaseDepth = null;

        public LevelGeneratorSettings build() {
            return new LevelGeneratorSettings(this);
        }

        public Builder roomCount(Range roomCount) {
            this.roomCount = roomCount;
            return this;
        }

        public Builder maxDepth(int maxDepth) {
            this.maxDepth = maxDepth;
            return this;
        }

        public Builder minStairsDepth(int minStairsDepth) {
            this.minStaircaseDepth = minStairsDepth;
            return this;
        }
    }
}
