package xiroc.dungeoncrawl.datapack.delegate;

import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.google.gson.JsonPrimitive;
import net.minecraft.resources.ResourceLocation;
import xiroc.dungeoncrawl.datapack.DatapackRegistry;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.json.JsonException;
import java.util.function.Function;
import java.util.function.Supplier;

public class Delegate<T> implements Supplier<T> {

    /**
     * Possible configurations:
     * [value: nonnull, key:nonnull]: value was referenced via key and was retrieved from the respective registry.
     * [value: nonnull, key:null]: value was defined in-place and therefore has no key.
     * [value: null, key:nonnull]: value was referenced via key but was not retrieved from a registry. only used for datagen.
     */

    @Nullable
    private final T value;
    @Nullable
    private final ResourceLocation key;

    private Delegate(@Nullable T value, @Nullable ResourceLocation key) {
        this.value = value;
        this.key = key;
    }

    @Override
    public T get() {
        if (value == null) { // May only be null during datagen
            throw new IllegalStateException("Delegate was holding a reference to " + key + ", not a value");
        }
        return value;
    }

    public ResourceLocation key() {
        if (key == null) {
            throw new IllegalStateException("Delegate was holding a value without a key");
        }
        return key;
    }

    public JsonElement serialize(Function<T, JsonElement> serializer) {
        if (key != null) {
            return new JsonPrimitive(key.toString());
        }
        JsonElement json = serializer.apply(value);
        if (json.isJsonPrimitive()) {
            throw new JsonException("Delegate value must not serialize to a primitive value");
        }
        return json;
    }

    public static <T> Delegate<T> deserialize(JsonElement json, DatapackRegistry<T> registry, Function<JsonElement, T> deserializer) {
        if (json.isJsonPrimitive()) {
            ResourceLocation key = new ResourceLocation(json.getAsString());
            return of(registry.get(key), key);
        }
        return of(deserializer.apply(json));
    }

    public static <T> Delegate<T> deserialize(JsonElement json, DatapackRegistry<T> registry) {
        if (json.isJsonPrimitive()) {
            ResourceLocation key = new ResourceLocation(json.getAsString());
            return of(registry.get(key), key);
        }
        throw new JsonParseException("Direct definitions are not allowed");
    }

    public static <T> Delegate<T> of(@Nonnull T value, @Nonnull ResourceLocation key) {
        return new Delegate<>(value, key);
    }

    public static <T> Delegate<T> of(@Nonnull T value) {
        return new Delegate<>(value, null);
    }

    public static <T> Delegate<T> of(@Nonnull ResourceLocation key) {
        return new Delegate<>(null, key);
    }
}
