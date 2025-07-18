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

package xiroc.dungeoncrawl.util.random;

import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.DynamicOps;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.HolderSet;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;
import xiroc.dungeoncrawl.util.StorageHelper;
import xiroc.dungeoncrawl.util.storage.GlobalCodecs;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.function.BiConsumer;

/**
 * Used to provide random objects of various types.
 */
public interface IRandom<T> {
    T roll(RandomSource rand);

    int totalWeight();

    void forEach(BiConsumer<T, Integer> consumer);

    record Entry<T>(T value, int weight) {}

    static <T> Builder<T> builder() {
        return new Builder<>();
    }

    class Builder<T>  {
        private static final int DEFAULT_WEIGHT = 1;

        private final List<Entry<T>> entries = new ArrayList<>();
        @Nullable
        private HolderSet<IRandom<T>> pools = null;

        public static <T> Builder<T> copy(IRandom<T> instance) {
            return new Builder<T>().addInstance(instance);
        }

        public Builder<T> add(T value) {
            return add(value, DEFAULT_WEIGHT);
        }

        public Builder<T> add(T value, int weight) {
            entries.add(new Entry<>(value, weight));
            return this;
        }

        public Builder<T> addBuilder(Builder<T> builder) {
            entries.addAll(builder.entries);
            return this;
        }

        public Builder<T> addInstance(IRandom<T> random) {
            random.forEach(this::add);
            return this;
        }

        public Builder<T> addAll(Collection<Entry<T>> entries) {
            this.entries.addAll(entries);
            return this;
        }

        public Builder<T> setPools(HolderSet<IRandom<T>> pools) {
            if (this.pools != null) {
                throw new IllegalStateException("Tried to set the pools set twice!");
            }
            this.pools = pools;
            return this;
        }

        @Nullable
        private IRandom<T> buildEntries() {
            if (entries.isEmpty()) {
                return null;
            }
            if (entries.size() == 1) {
                return new SingleValueRandom<>(entries.getFirst().value());
            }
            if (entries.size() < 32) {
                return new ListWeightedRandom<>(entries);
            }
            return new AVLTreeWeightedRandom<>(entries);
        }

        public IRandom<T> build() {
            IRandom<T> base = buildEntries();
            if (base != null) {
                if (pools == null) {
                    return base;
                } else {
                    return new PooledRandom<>(base, pools);
                }
            } else {
                if (pools == null) {
                    throw new IllegalStateException("Random instance cannot be empty.");
                } else {
                    return new PooledRandom<>(pools);
                }
            }
        }
    }

    interface BaseCodecs {
        Codec<Builder<BlockState>> BLOCK_STATE = makeBuilderCodec(GlobalCodecs.BLOCK_STATE, "block", null);
        Codec<IRandom<Item>> ITEM = makeCodec(makeBuilderCodec(GlobalCodecs.ITEM, "item", null));
    }

    /**
     * Constructs a codec for a random instance builder using values encoded/decoded by the provided value codec.
     * Optionally supports global pools, if provided.
     *
     * @param <S>        The type of values the builder is supposed to accept.
     * @param valueCodec The codec for values.
     * @param valueName  The name of a value field when encoded in a map together with a weight.
     * @param poolCodec  Optionally, a codec for holder sets
     * @return The codec.
     */
    static <S> Codec<Builder<S>> makeBuilderCodec(Codec<S> valueCodec, String valueName, @Nullable Codec<HolderSet<IRandom<S>>> poolCodec) {
        final Codec<Entry<S>> entryCodec = RecordCodecBuilder.create(instance -> instance.group(
                valueCodec.fieldOf(valueName).forGetter(Entry::value),
                // Only encode the weight if it isn't the default weight.
                Codec.intRange(1, Integer.MAX_VALUE).optionalFieldOf("weight").forGetter(pair -> Optional.ofNullable(pair.weight() == Builder.DEFAULT_WEIGHT ? null : pair.weight()))
        ).apply(instance, (value, weight) -> new Entry<>(value, weight.orElse(Builder.DEFAULT_WEIGHT))));

        final Codec<Entry<S>> optionallyInlinedEntryCodec = new Codec<>() {
            @Override
            public <T> DataResult<Pair<Entry<S>, T>> decode(DynamicOps<T> ops, T input) {
                final boolean isMap = ops.getMapValues(input).result().isPresent();
                if (isMap) {
                    return entryCodec.decode(ops, input);
                }
                // If the piece of data is not a map, the wrapper was omitted and the value encoded directly. The weight is always the default weight in this case.
                return valueCodec.decode(ops, input).map(result -> result.mapFirst(value -> new Entry<>(value, Builder.DEFAULT_WEIGHT)));
            }

            @Override
            public <T> DataResult<T> encode(Entry<S> input, DynamicOps<T> ops, T prefix) {
                // If the weight is the default weight and the value does not encode to a map, we omit the wrapper with the weight entirely.
                if (input.weight() == Builder.DEFAULT_WEIGHT) {
                    final var encoded = valueCodec.encode(input.value, ops, prefix);
                    final boolean isNonMap = encoded.flatMap(ops::getMapValues).result().isEmpty();
                    if (isNonMap) {
                        return encoded;
                    }
                }
                return entryCodec.encode(input, ops, prefix);
            }
        };

        final Codec<Builder<S>> nonPooledBuilderCodec = optionallyInlinedEntryCodec.listOf().flatComapMap(entries -> new Builder<S>().addAll(entries), builder -> {
            if (builder.pools == null) {
                return DataResult.success(builder.entries);
            }
            return DataResult.error(() -> "Pools are not supported by this type.");
        });

        if (poolCodec == null) {
            return nonPooledBuilderCodec;
        }

        final Codec<Builder<S>> pooledBuilderCodec = RecordCodecBuilder.create(instance -> instance.group(
                poolCodec.optionalFieldOf("pools").forGetter(builder -> Optional.ofNullable(builder.pools)),
                optionallyInlinedEntryCodec.listOf().optionalFieldOf("values").forGetter(builder -> Optional.ofNullable(builder.entries.isEmpty() ? null : builder.entries))
        ).apply(instance, (pools, entries) -> {
            final Builder<S> builder = new Builder<>();
            builder.addAll(entries.orElse(List.of()));
            builder.pools = pools.orElse(null);
            return builder;
        }));

        // Dispatch between the two codes based on whether pools are being used or not.
        return new Codec<>() {
            @Override
            public <T> DataResult<Pair<Builder<S>, T>> decode(DynamicOps<T> ops, T input) {
                final boolean isMap = ops.getMapValues(input).result().isPresent();
                if (isMap) {
                    return pooledBuilderCodec.decode(ops, input);
                }
                return nonPooledBuilderCodec.decode(ops, input);
            }

            @Override
            public <T> DataResult<T> encode(Builder<S> input, DynamicOps<T> ops, T prefix) {
                if (input.pools == null) {
                    return nonPooledBuilderCodec.encode(input, ops, prefix);
                }
                return pooledBuilderCodec.encode(input, ops, prefix);
            }
        };
    }

    /**
     * Constructs a codec for random instances from a codec for random instance builders.
     * @param builderCodec A codec for builders of the type {@code T}.
     * @return The codec.
     * @param <T> The type of object that is randomized over.
     */
    static <T> Codec<IRandom<T>> makeCodec(Codec<Builder<T>> builderCodec) {
        return builderCodec.comapFlatMap(StorageHelper.tryToApply(Builder::build), instance -> new Builder<T>().addInstance(instance));
    }
}
