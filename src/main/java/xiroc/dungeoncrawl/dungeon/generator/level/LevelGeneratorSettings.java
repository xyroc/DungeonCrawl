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

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import xiroc.dungeoncrawl.util.StorageHelper;
import xiroc.dungeoncrawl.util.random.value.RandomValue;

import javax.annotation.Nullable;
import java.util.Objects;
import java.util.Optional;

public class LevelGeneratorSettings {
    public static final Codec<LevelGeneratorSettings> CODEC = Builder.CODEC.comapFlatMap(StorageHelper.tryToApply(LevelGeneratorSettings::new), Builder::fromInstance);

    private static final int DEFAULT_MAX_CLUSTER_NODES = 0;

    /**
     * The maximum number of rooms.
     */
    public final int maxRooms;

    /**
     * The maximum number of cluster nodes.
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
     * The minimum number of blocks that should be between the floor of this layer and the above one.
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

    public static class Builder {
        public static final Codec<Builder> CODEC = RecordCodecBuilder.create(instance -> instance.group(
                Codec.INT.optionalFieldOf("max_rooms").forGetter(builder -> Optional.ofNullable(builder.maxRooms)),
                Codec.INT.optionalFieldOf("max_cluster_nodes").forGetter(builder -> Optional.ofNullable(builder.maxClusterNodes)),
                Codec.INT.optionalFieldOf("max_depth").forGetter(builder -> Optional.ofNullable(builder.maxDepth)),
                Codec.INT.optionalFieldOf("min_staircase_depth").forGetter(builder -> Optional.ofNullable(builder.minStaircaseDepth)),
                Codec.INT.optionalFieldOf("min_separation").forGetter(builder -> Optional.ofNullable(builder.minSeparation)),
                RandomValue.CODEC.optionalFieldOf("corridor_length").forGetter(builder -> Optional.ofNullable(builder.corridorLength))
        ).apply(instance, (maxRooms, maxClusterNodes, maxDepth, minStaircaseDepth, minSeparation, corridorLength) -> {
            final Builder builder = new Builder();
            builder.maxRooms = maxRooms.orElse(null);
            builder.maxClusterNodes = maxClusterNodes.orElse(null);
            builder.maxDepth = maxDepth.orElse(null);
            builder.minStaircaseDepth = minStaircaseDepth.orElse(null);
            builder.minSeparation = minSeparation.orElse(null);
            builder.corridorLength = corridorLength.orElse(null);
            return builder;
        }));

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
        private RandomValue corridorLength = null;

        public static Builder fromInstance(LevelGeneratorSettings instance) {
            final Builder builder = new Builder();
            builder.maxRooms = instance.maxRooms;
            builder.maxClusterNodes = instance.maxClusterNodes;
            builder.maxDepth = instance.maxDepth;
            builder.minStaircaseDepth = instance.minStaircaseDepth;
            builder.minSeparation = instance.minSeparation;
            builder.corridorLength = instance.corridorLength;
            return builder;
        }

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

        public Builder corridorLength(RandomValue corridorLength) {
            this.corridorLength = corridorLength;
            return this;
        }
    }
}
