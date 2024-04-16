package xiroc.dungeoncrawl.util.random;

import com.google.common.collect.ImmutableMap;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.minecraft.resources.ResourceLocation;
import xiroc.dungeoncrawl.datapack.delegate.Delegate;

import java.util.HashMap;
import java.util.Random;
import java.util.function.Consumer;
import java.util.function.Function;

public class RandomMapping<K, V> {
    private static final String KEY_FALLBACK = "default";
    private static final String KEY_MAPPING = "mapping";

    private final IRandom<Delegate<V>> fallback;
    private final ImmutableMap<K, IRandom<Delegate<V>>> entries;

    private RandomMapping(Builder<K, V> builder) {
        this.fallback = builder.fallback.build();
        ImmutableMap.Builder<K, IRandom<Delegate<V>>> entries = ImmutableMap.builder();
        builder.entries.forEach((key, value) -> entries.put(key, value.build()));
        this.entries = entries.build();
    }

    public V roll(K key, Random random) {
        if (key == null) {
            return fallback.roll(random).get();
        }
        return entries.getOrDefault(key, fallback).roll(random).get();
    }

    public void validate(Consumer<String> errorHandler) {
        // TODO
    }

    public static <K, V> JsonElement serialize(RandomMapping<K, V> mapping, Function<K, String> keySerializer, IRandom.Serializer<Delegate<V>> serializer) {
        JsonObject object = new JsonObject();
        JsonObject jsonMapping = new JsonObject();
        mapping.entries.forEach((key, value) -> jsonMapping.add(keySerializer.apply(key), serializer.serialize(value)));
        object.add(KEY_FALLBACK, serializer.serialize(mapping.fallback));
        object.add(KEY_MAPPING, jsonMapping);
        return object;
    }

    public static class Builder<K, V> {
        private final IRandom.Builder<Delegate<V>> fallback = new IRandom.Builder<>();
        private final HashMap<K, IRandom.Builder<Delegate<V>>> entries = new HashMap<>();

        private IRandom.Builder<Delegate<V>> get(K key) {
            return this.entries.computeIfAbsent(key, (k) -> new IRandom.Builder<>());
        }

        public Builder<K, V> add(K key, ResourceLocation entry, int weight) {
            get(key).add(Delegate.of(entry), weight);
            return this;
        }

        public Builder<K, V> fallback(ResourceLocation value, int weight) {
            this.fallback.add(Delegate.of(value), weight);
            return this;
        }

        public Builder<K, V> deserialize(JsonElement file, IRandom.Serializer<Delegate<V>> serializer, Function<String, K> keyProvider) {
            JsonObject object = file.getAsJsonObject();
            if (object.has(KEY_FALLBACK)) {
                serializer.deserializePartial(object.get(KEY_FALLBACK), this.fallback);
            }
            if (object.has(KEY_MAPPING)) {
                JsonObject mapping = object.getAsJsonObject(KEY_MAPPING);
                mapping.entrySet().forEach((entry) -> serializer.deserializePartial(entry.getValue(), get(keyProvider.apply(entry.getKey()))));
            }
            return this;
        }

        public RandomMapping<K, V> build() {
            return new RandomMapping<>(this);
        }
    }
}