package xiroc.dungeoncrawl.util.random;

import com.google.common.collect.ImmutableMap;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.minecraft.resources.ResourceLocation;
import xiroc.dungeoncrawl.datapack.registry.Delegate;
import xiroc.dungeoncrawl.datapack.registry.InheritingBuilder;
import xiroc.dungeoncrawl.exception.DatapackLoadException;

import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.Objects;
import java.util.Random;
import java.util.function.Function;

public class RandomMapping<K, V> {
    private static final String KEY_FALLBACK = "default";
    private static final String KEY_MAPPING = "mapping";

    private final IRandom<Delegate<V>> fallback;
    private final ImmutableMap<K, IRandom<Delegate<V>>> entries;

    private RandomMapping(Builder<K, V> builder) {
        Objects.requireNonNull(builder.fallback, "No fallback pool was provided");
        try {
            this.fallback = builder.fallback.build();
        } catch (Exception e) {
            throw new DatapackLoadException("Invalid fallback pool: " + e.getMessage());
        }
        ImmutableMap.Builder<K, IRandom<Delegate<V>>> entries = ImmutableMap.builder();
        builder.entries.forEach((key, value) -> {
            try {
                entries.put(key, value.build());
            } catch (Exception e) {
                throw new DatapackLoadException("Invalid pool for key '" + key + "': " + e.getMessage());
            }
        });
        this.entries = entries.build();
    }

    public V roll(K key, Random random) {
        if (key == null) {
            return fallback.roll(random).get();
        }
        return entries.getOrDefault(key, fallback).roll(random).get();
    }

    public static <K, V> JsonElement serialize(RandomMapping<K, V> mapping, Function<K, String> keySerializer, IRandom.Serializer<Delegate<V>> serializer) {
        JsonObject object = new JsonObject();
        JsonObject jsonMapping = new JsonObject();
        mapping.entries.forEach((key, value) -> jsonMapping.add(keySerializer.apply(key), serializer.serialize(value)));
        object.add(KEY_FALLBACK, serializer.serialize(mapping.fallback));
        object.add(KEY_MAPPING, jsonMapping);
        return object;
    }

    public static class Builder<K, V> extends InheritingBuilder<RandomMapping<K, V>, Builder<K, V>> {
        @Nullable
        private IRandom.Builder<Delegate<V>> fallback = null;
        private HashMap<K, IRandom.Builder<Delegate<V>>> entries = new HashMap<>();

        private IRandom.Builder<Delegate<V>> get(K key) {
            return this.entries.computeIfAbsent(key, (k) -> new IRandom.Builder<>());
        }

        public Builder<K, V> add(K key, ResourceLocation entry, int weight) {
            get(key).add(Delegate.of(entry), weight);
            return this;
        }

        public Builder<K, V> fallback(IRandom.Builder<Delegate<V>> fallback) {
            this.fallback = fallback;
            return this;
        }

        public Builder<K, V> deserialize(JsonElement json, IRandom.Serializer<Delegate<V>> serializer, Function<String, K> keyProvider) {
            JsonObject object = json.getAsJsonObject();
            if (object.has(KEY_FALLBACK)) {
                this.fallback = serializer.deserializeBuilder(object.get(KEY_FALLBACK));
            }
            if (object.has(KEY_MAPPING)) {
                JsonObject mapping = object.getAsJsonObject(KEY_MAPPING);
                mapping.entrySet().forEach((entry) -> {
                    final var key = keyProvider.apply(entry.getKey());
                    this.entries.put(key, serializer.deserializeBuilder(entry.getValue()));
                });
            }
            return this;
        }

        @Override
        public Builder<K, V> inherit(Builder<K, V> from) {
            this.fallback = InheritingBuilder.inheritOrReplaceOrChoose(this.fallback, from.fallback);
            if (from.replace()) {
                this.entries = from.entries;
            } else {
                from.entries.forEach((key, value) -> {
                    if (value == null) {
                        return;
                    }
                    if (value.replace()) {
                        this.entries.put(key, value);
                    } else {
                        this.entries.get(key).inherit(value);
                    }
                });
            }
            return this;
        }

        public RandomMapping<K, V> build() {
            return new RandomMapping<>(this);
        }
    }
}