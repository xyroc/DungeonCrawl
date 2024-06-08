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
import java.util.Objects;

public class LevelGeneratorSettings {
    private static final int DEFAULT_MAX_ROOMS = 16;
    private static final int DEFAULT_MIN_SEPARATION = 10;
    private static final int DEFAULT_MAX_CLUSTER_NODES = 0;

    /**
     * The maximum amount of rooms.
     */
    public final int maxRooms;

    /**
     * The maximum amount of cluster nodes.
     */
    public final int maxClusterNodes;

    /**
     * The overall maximum depth for the layout generation.
     */
    public final int maxDepth;

    /**
     * The minimum depth for the stairs to the next layer.
     */
    public final int minStaircaseDepth;


    /**
     * The minimum amount of blocks that should be between the floor of this layer and the above one.
     */
    public final int minSeparation;

    /**
     * The minimum and maximum length of corridors between rooms.
     */
    public final Range corridorLength;

    public LevelGeneratorSettings(Builder builder) {
        this.maxRooms = builder.maxRooms;
        this.maxClusterNodes = builder.maxClusterNodes;
        this.maxDepth = builder.maxDepth;
        this.minStaircaseDepth = builder.minStaircaseDepth;
        this.minSeparation = builder.minSeparation;
        this.corridorLength = builder.corridorLength;
    }

    public static class Serializer implements JsonSerializer<LevelGeneratorSettings>, JsonDeserializer<LevelGeneratorSettings> {
        private static final String KEY_MAX_ROOMS = "max_rooms";
        private static final String KEY_MAX_CLUSTER_NODES = "max_cluster_nodes";
        private static final String KEY_MAX_GENERATION_DEPTH = "max_generation_depth";
        private static final String KEY_MIN_STAIRCASE_DEPTH = "min_staircase_depth";
        private static final String KEY_MIN_SEPARATION = "min_separation";

        @Override
        public LevelGeneratorSettings deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
            Builder builder = new Builder();
            JsonObject object = json.getAsJsonObject();
            builder.maxRooms = object.get(KEY_MAX_ROOMS).getAsInt();
            builder.maxClusterNodes = object.has(KEY_MAX_CLUSTER_NODES) ? object.get(KEY_MAX_CLUSTER_NODES).getAsInt() : DEFAULT_MAX_CLUSTER_NODES;
            builder.maxDepth = object.get(KEY_MAX_GENERATION_DEPTH).getAsInt();
            builder.minStaircaseDepth = object.get(KEY_MIN_STAIRCASE_DEPTH).getAsInt();
            builder.minSeparation = object.has(KEY_MIN_SEPARATION) ? object.get(KEY_MIN_SEPARATION).getAsInt() : DEFAULT_MIN_SEPARATION;
            return builder.build();
        }

        @Override
        public JsonElement serialize(LevelGeneratorSettings settings, Type typeOfSrc, JsonSerializationContext context) {
            JsonObject object = new JsonObject();
            object.addProperty(KEY_MAX_ROOMS, settings.maxRooms);
            object.addProperty(KEY_MAX_GENERATION_DEPTH, settings.maxDepth);
            object.addProperty(KEY_MIN_STAIRCASE_DEPTH, settings.minStaircaseDepth);
            if (settings.maxClusterNodes != DEFAULT_MAX_CLUSTER_NODES) {
                object.addProperty(KEY_MAX_CLUSTER_NODES, settings.maxClusterNodes);
            }
            if (settings.minSeparation != DEFAULT_MIN_SEPARATION) {
                object.addProperty(KEY_MIN_SEPARATION, settings.minSeparation);
            }
            return object;
        }
    }

    public static class Builder {
        private int maxRooms = DEFAULT_MAX_ROOMS;
        private int maxClusterNodes = DEFAULT_MAX_CLUSTER_NODES;
        private Integer maxDepth = null;
        private Integer minStaircaseDepth = null;
        private int minSeparation = DEFAULT_MIN_SEPARATION;
        private Range corridorLength = null;

        public LevelGeneratorSettings build() {
            Objects.requireNonNull(maxDepth);
            Objects.requireNonNull(minStaircaseDepth);
            Objects.requireNonNull(corridorLength);
            return new LevelGeneratorSettings(this);
        }

        public Builder maxRooms(int maxRooms) {
            this.maxRooms = maxRooms;
            return this;
        }

        public Builder maxClusterNodes(int maxClusterNodes) {
            this.maxClusterNodes = maxClusterNodes;
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

        public Builder minSeparation(int minSeparation) {
            this.minSeparation = minSeparation;
            return this;
        }

        public Builder corridorLength(Range corridorLength) {
            this.corridorLength = corridorLength;
            return this;
        }
    }
}
