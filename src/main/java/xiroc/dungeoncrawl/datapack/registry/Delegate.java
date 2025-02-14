package xiroc.dungeoncrawl.datapack.registry;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import com.google.gson.JsonSyntaxException;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import xiroc.dungeoncrawl.exception.DatapackLoadException;

import javax.json.JsonException;
import java.lang.reflect.Type;
import java.util.function.Function;
import java.util.function.Supplier;

public class Delegate<T> implements Supplier<T> {

    /**
     * Possible configurations:
     * [value: nonnull, key:nonnull]: value was referenced via key and was retrieved from the respective registry.
     * [value: nonnull, key:null]: value was defined in-place and therefore has no key.
     * [value: null, key:nonnull]: value was referenced via key but was not yet retrieved from a registry.
     */

    @Nullable
    protected T value;
    @Nullable
    protected final ResourceLocation key;

    protected Delegate(@Nullable T value, @Nullable ResourceLocation key) {
        if (key == null && value == null) {
            throw new IllegalArgumentException("Either a key or a value is required");
        }
        this.value = value;
        this.key = key;
    }

    public void resolve(DatapackRegistry<T> registry) {
        if (key == null) {
            throw new IllegalStateException("Cannot resolve immediate delegate");
        }
        this.value = registry.get(key);
        if (this.value == null) {
            throw new DatapackLoadException("No entry for: " + key);
        }
    }

    @Override
    public T get() {
        if (value == null) {
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

    public boolean hasValue() {
        return value != null;
    }

    public boolean hasKey() {
        return key != null;
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

    public static <T> Delegate<T> of(@NotNull T value, @NotNull ResourceLocation key) {
        return new Delegate<>(value, key);
    }

    public static <T> Delegate<T> of(@NotNull T value) {
        return new Delegate<>(value, null);
    }

    public static <T> Delegate<T> of(@NotNull ResourceLocation key) {
        return new Delegate<>(null, key);
    }

    public record Serializer<T>(DatapackRegistry<T> registry, @Nullable Type valueType) implements JsonSerializer<Delegate<T>>, JsonDeserializer<Delegate<T>> {
        @Override
        public Delegate<T> deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
            if (json.isJsonPrimitive()) {
                final ResourceLocation key = new ResourceLocation(json.getAsString());
                return registry.delegateOrThrow(key);
            }
            if (valueType == null) {
                throw new JsonParseException("Inline definitions are not allowed here.");
            }
            return new Delegate<>(context.deserialize(json, valueType), null);
        }

        @Override
        public JsonElement serialize(Delegate<T> src, Type typeOfSrc, JsonSerializationContext context) {
            if (src.key != null) {
                return new JsonPrimitive(src.key.toString());
            }
            if (valueType == null) {
                throw new JsonSyntaxException("Inline definitions are not allowed here.");
            }
            final JsonElement json = context.serialize(src.value, valueType);
            if (json.isJsonPrimitive()) {
                throw new JsonSyntaxException("Inline definitions must not serialize to a primitive value");
            }
            return json;
        }
    }
}
