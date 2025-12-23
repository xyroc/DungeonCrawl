package xiroc.dungeoncrawl.util.random;

import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.mojang.serialization.codecs.UnboundedMapCodec;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderSet;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.RandomSource;

import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

public class RandomMapping<K, V> {
    public static <K, V> Codec<RandomMapping<K, V>> makeDirectCodec(Codec<HolderSet<K>> keySetCodec, Codec<IRandom<Holder<V>>> valuesCodec) {
        final Codec<Map<HolderSet<K>, IRandom<Holder<V>>>> mappingCodec = new UnboundedMapCodec<>(keySetCodec, valuesCodec);
        return RecordCodecBuilder.create(builder -> builder.group(
                valuesCodec.fieldOf("fallback").forGetter(mapping -> mapping.fallback),
                mappingCodec.fieldOf("mapping").forGetter(mapping -> mapping.rawMapping)
        ).apply(builder, RandomMapping::new));
    }

    private final Map<HolderSet<K>, IRandom<Holder<V>>> rawMapping;
    private final IRandom<Holder<V>> fallback;

    private Map<ResourceLocation, IRandom<IRandom<Holder<V>>>> mapping;

    private RandomMapping(IRandom<Holder<V>> fallback, Map<HolderSet<K>, IRandom<Holder<V>>> rawMapping) {
        this.fallback = fallback;
        this.rawMapping = rawMapping;
    }

    /**
     * Creates the fallback and mapping data structures from the raw data.
     * Requires that the holder sets of keys and values have been bound.
     */
    public void compile() {
        if (this.mapping == null) {
            final HashMap<ResourceLocation, IRandom.Builder<IRandom<Holder<V>>>> pooledMapping = new HashMap<>();
            rawMapping.forEach((keys, values) -> {
                for (var key : keys) {
                    ResourceLocation identifier = Objects.requireNonNull(key.getKey()).location();
                    pooledMapping.computeIfAbsent(identifier, ignored -> new IRandom.Builder<>())
                            .add(values);
                }
            });
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

    public static class Builder<K, V> {
        @Nullable
        private IRandom<Holder<V>> fallback = null;
        private final Map<HolderSet<K>, IRandom<Holder<V>>> entries = new HashMap<>();

        public void combineWith(RandomMapping<K, V> instance) {
            IRandom.Builder<Holder<V>> combinedFallback = new IRandom.Builder<>();
            if (this.fallback != null) {
                combinedFallback.addInstance(this.fallback);
            }
            combinedFallback.addInstance(instance.fallback);
            this.fallback = combinedFallback.build();

            instance.rawMapping.forEach((keys, values) -> {
                if (this.entries.containsKey(keys)) {
                    IRandom<Holder<V>> combinedEntries = new IRandom.Builder<Holder<V>>()
                            .addInstance(this.entries.get(keys))
                            .addInstance(values)
                            .build();
                    this.entries.put(keys, combinedEntries);
                } else {
                    this.entries.put(keys, values);
                }
            });
        }

        public Builder<K, V> put(HolderSet<K> keys, IRandom<Holder<V>> values) {
            entries.put(keys, values);
            return this;
        }

        public Builder<K, V> fallback(IRandom<Holder<V>> fallback) {
            this.fallback = fallback;
            return this;
        }

        public RandomMapping<K, V> build() {
            Objects.requireNonNull(fallback, "No fallback pool was provided");
            return new RandomMapping<>(fallback, entries);
        }
    }
}