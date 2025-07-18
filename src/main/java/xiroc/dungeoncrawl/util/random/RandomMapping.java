package xiroc.dungeoncrawl.util.random;

import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderSet;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.RandomSource;
import xiroc.dungeoncrawl.exception.DatapackLoadException;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.function.BiConsumer;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class RandomMapping<K, V> {
    public static <K, V> Codec<RandomMapping<K, V>> makeDirectCodec(Codec<HolderSet<K>> keySetCodec, Codec<HolderSet<IRandom<Holder<V>>>> valuesSetCodec) {
        final Codec<Entry<K, V>> entryCodec = Entry.makeCodec(keySetCodec, valuesSetCodec);
        return RecordCodecBuilder.create(instance -> instance.group(
                valuesSetCodec.fieldOf("fallback").forGetter(mapping -> mapping.rawFallback),
                entryCodec.listOf().fieldOf("mapping").forGetter(mapping -> mapping.rawMapping)
        ).apply(instance, RandomMapping::new));
    }

    private final HolderSet<IRandom<Holder<V>>> rawFallback;
    private final List<Entry<K, V>> rawMapping;

    private IRandom<Holder<V>> fallback;
    private Map<ResourceLocation, IRandom<IRandom<Holder<V>>>> mapping;

    private RandomMapping(HolderSet<IRandom<Holder<V>>> rawFallback, List<Entry<K, V>> rawMapping) {
        this.rawFallback = rawFallback;
        this.rawMapping = rawMapping;
    }

    /**
     * Creates the fallback and mapping data structures from the raw data.
     * Requires that the holder sets of keys and values have been bound.
     */
    public void compile() {
        if (this.fallback == null) {
            final var compiledFallback = compilePools(this.rawFallback);
            if (compiledFallback.isEmpty()) {
                throw new DatapackLoadException("Fallback pool must not be empty.");
            }
            this.fallback = compiledFallback.get();
        }

        if (this.mapping == null) {
            final HashMap<ResourceLocation, IRandom.Builder<IRandom<Holder<V>>>> pooledMapping = new HashMap<>();
            for (Entry<K, V> entry : rawMapping) {
                entry.compilePools((key, pool) ->
                        pooledMapping.computeIfAbsent(key, ignored -> new IRandom.Builder<>()).add(pool, pool.totalWeight()));
            }
            this.mapping = pooledMapping.entrySet().stream()
                    .map(entry -> Pair.of(entry.getKey(), entry.getValue().build()))
                    .collect(Collectors.toMap(Pair::getFirst, Pair::getSecond));
        }
    }

    public Holder<V> roll(ResourceLocation key, RandomSource random) {
        var pools = mapping.get(key);
        if (pools == null) {
            return fallback.roll(random);
        }
        return pools.roll(random).roll(random);
    }

    private static <V> Optional<IRandom<Holder<V>>> compilePools(HolderSet<IRandom<Holder<V>>> pools) {
        return pools.stream()
                .map(Holder::value)
                .map(v -> new IRandom.Builder<Holder<V>>().addInstance(v))
                .reduce(IRandom.Builder::addBuilder)
                .map(IRandom.Builder::build);
    }

    private record Entry<K, V>(HolderSet<K> keys, HolderSet<IRandom<Holder<V>>> values) {
        private static <K, V> Codec<Entry<K, V>> makeCodec(Codec<HolderSet<K>> keysCodec, Codec<HolderSet<IRandom<Holder<V>>>> valuesCodec) {
            return RecordCodecBuilder.create(instance -> instance
                    .group(
                            keysCodec.fieldOf("keys").forGetter(Entry::keys),
                            valuesCodec.fieldOf("values").forGetter(Entry::values)
                    ).apply(instance, Entry::new));
        }

        private void compilePools(BiConsumer<ResourceLocation, IRandom<Holder<V>>> consumer) {
            var compiledPool = RandomMapping.compilePools(values);
            if (compiledPool.isEmpty()) {
                return;
            }
            keys.stream().map(Holder::getKey).filter(Objects::nonNull).forEach(key -> consumer.accept(key.location(), compiledPool.get()));
        }
    }

    public static class Builder<K, V> {
        @Nullable
        private HolderSet<IRandom<Holder<V>>> fallback = null;
        private final List<Entry<K, V>> entries = new ArrayList<>();

        public Builder<K, V> put(RandomMapping<K, V> instance) {
            this.fallback = HolderSet.direct(Stream.concat(
                    this.fallback == null ? Stream.of() : this.fallback.stream(),
                    instance.rawFallback.stream()
            ).toList());
            this.entries.addAll(instance.rawMapping);
            return this;
        }

        public Builder<K, V> put(HolderSet<K> keys, IRandom<Holder<V>> values) {
            return put(keys, Holder.direct(values));
        }

        public Builder<K, V> put(HolderSet<K> keys, Holder<IRandom<Holder<V>>> values) {
            return put(keys, HolderSet.direct(values));
        }

        public Builder<K, V> put(HolderSet<K> keys, HolderSet<IRandom<Holder<V>>> values) {
            entries.add(new Entry<>(keys, values));
            return this;
        }

        public Builder<K, V> fallback(IRandom<Holder<V>> fallback) {
            return fallback(Holder.direct(fallback));
        }

        public Builder<K, V> fallback(Holder<IRandom<Holder<V>>> fallback) {
            return fallback(HolderSet.direct(fallback));
        }

        public Builder<K, V> fallback(HolderSet<IRandom<Holder<V>>> fallback) {
            this.fallback = fallback;
            return this;
        }

        public RandomMapping<K, V> build() {
            Objects.requireNonNull(fallback, "No fallback pool was provided");
            return new RandomMapping<>(fallback, entries);
        }
    }
}