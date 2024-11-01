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
import xiroc.dungeoncrawl.datapack.registry.InheritingBuilder;
import xiroc.dungeoncrawl.util.random.value.RandomValue;
import xiroc.dungeoncrawl.util.random.value.Range;

import javax.annotation.Nullable;
import java.lang.reflect.Type;
import java.util.Objects;

public class LevelGeneratorSettings {
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
    public final RandomValue corridorLength;

    private LevelGeneratorSettings(Builder builder) {
        this.maxRooms = Objects.requireNonNull(builder.maxRooms, "No maximum amount of rooms was specified");
        this.maxClusterNodes = Objects.requireNonNullElse(builder.maxClusterNodes, DEFAULT_MAX_CLUSTER_NODES);
        this.maxDepth = Objects.requireNonNull(builder.maxDepth, "No maximum generation depth was specified");
        this.minStaircaseDepth = Objects.requireNonNull(builder.minStaircaseDepth, "No minimum staircase depth was specified");
        this.minSeparation = Objects.requireNonNull(builder.minSeparation, "No minimum separation was specified");
        this.corridorLength = Objects.requireNonNull(builder.corridorLength, "No corridor length was specified");
    }

    public static class Builder extends InheritingBuilder<LevelGeneratorSettings, Builder> {
        @Nullable
        private Integer maxRooms = null;
        @Nullable
        private Integer maxClusterNodes = null;
        @Nullable
        private Integer maxDepth = null;
        @Nullable
        private Integer minStaircaseDepth = null;
        @Nullable
        private Integer minSeparation = null;
        @Nullable
        private Range corridorLength = null;

        @Override
        public Builder inherit(Builder from) {
            this.maxRooms = InheritingBuilder.choose(this.maxRooms, from.maxRooms);
            this.maxClusterNodes = InheritingBuilder.choose(this.maxClusterNodes, from.maxClusterNodes);
            this.maxDepth = InheritingBuilder.choose(this.maxDepth, from.maxDepth);
            this.minStaircaseDepth = InheritingBuilder.choose(this.minStaircaseDepth, from.minStaircaseDepth);
            this.minSeparation = InheritingBuilder.choose(this.minSeparation, from.minSeparation);
            this.corridorLength = InheritingBuilder.choose(this.corridorLength, from.corridorLength);
            return this;
        }

        @Override
        public LevelGeneratorSettings build() {
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

    public static class BuilderSerializer implements JsonSerializer<Builder>, JsonDeserializer<Builder> {
        private static final String KEY_MAX_ROOMS = "max_rooms";
        private static final String KEY_MAX_CLUSTER_NODES = "max_cluster_nodes";
        private static final String KEY_MAX_GENERATION_DEPTH = "max_generation_depth";
        private static final String KEY_MIN_STAIRCASE_DEPTH = "min_staircase_depth";
        private static final String KEY_MIN_SEPARATION = "min_separation";
        private static final String KEY_CORRIDOR_LENGTH = "corridor_length";

        @Override
        public Builder deserialize(JsonElement json, Type type, JsonDeserializationContext context) throws JsonParseException {
            Builder builder = new Builder();
            JsonObject object = json.getAsJsonObject();
            if (object.has(KEY_MAX_ROOMS)) builder.maxRooms = object.get(KEY_MAX_ROOMS).getAsInt();
            if (object.has(KEY_MAX_CLUSTER_NODES)) builder.maxClusterNodes = object.get(KEY_MAX_CLUSTER_NODES).getAsInt();
            if (object.has(KEY_MAX_GENERATION_DEPTH)) builder.maxDepth = object.get(KEY_MAX_GENERATION_DEPTH).getAsInt();
            if (object.has(KEY_MIN_STAIRCASE_DEPTH)) builder.minStaircaseDepth = object.get(KEY_MIN_STAIRCASE_DEPTH).getAsInt();
            if (object.has(KEY_MIN_SEPARATION)) builder.minSeparation = object.get(KEY_MIN_SEPARATION).getAsInt();
            if (object.has(KEY_CORRIDOR_LENGTH)) builder.corridorLength = context.deserialize(object.get(KEY_CORRIDOR_LENGTH), RandomValue.class);
            return builder;
        }

        @Override
        public JsonElement serialize(Builder builder, Type type, JsonSerializationContext context) {
            JsonObject object = new JsonObject();
            if (builder.maxRooms != null) object.addProperty(KEY_MAX_ROOMS, builder.maxRooms);
            if (builder.maxClusterNodes != null) object.addProperty(KEY_MAX_CLUSTER_NODES, builder.maxClusterNodes);
            if (builder.maxDepth != null) object.addProperty(KEY_MAX_GENERATION_DEPTH, builder.maxDepth);
            if (builder.minStaircaseDepth != null) object.addProperty(KEY_MIN_STAIRCASE_DEPTH, builder.minStaircaseDepth);
            if (builder.minSeparation != null) object.addProperty(KEY_MIN_SEPARATION, builder.minSeparation);
            if (builder.corridorLength != null) object.add(KEY_CORRIDOR_LENGTH, context.serialize(builder.corridorLength));
            return object;
        }
    }
}
