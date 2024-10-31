package xiroc.dungeoncrawl.datapack.registry;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import net.minecraft.resources.ResourceLocation;

import javax.annotation.Nullable;
import java.lang.reflect.Type;

public abstract class InheritingBuilder<T, B extends InheritingBuilder<T, B>> {
    private static final String KEY_PARENT = "inherit";
    private static final String KEY_REPLACE = "replace";
    private static final boolean REPLACE_BY_DEFAULT = true;

    @Nullable
    protected ResourceLocation parent = null;
    protected boolean replace = REPLACE_BY_DEFAULT;

    @SuppressWarnings("unchecked")
    public B parent(@Nullable ResourceLocation parent) {
        this.parent = parent;
        return (B) this;
    }

    @SuppressWarnings("unchecked")
    public B replace(boolean replace) {
        this.replace = replace;
        return (B) this;
    }

    @Nullable
    public ResourceLocation parent() {
        return parent;
    }

    public boolean replace() {
        return replace;
    }

    /**
     * Invokes the build() method for this builder, meaning that if the current state of the builder
     *  is incomplete and therefore cannot produce an instance, an exception occurs.
     * Otherwise, the builder is returned.
     * <p>
     * This is useful to verify inheriting builders during data gen, since they may be incomplete.
     * It is *not* an exhaustive check for correctness though. For example, delegates referencing nonexistent entries will
     *  likely remain undetected.
     */
    @SuppressWarnings("unchecked")
    public B tryBuild() {
        try {
            build();
        } catch (Exception e) {
            throw new IllegalStateException("Builder " + getClass().getName() + " threw an exception on build", e);
        }
        return (B) this;
    }

    public abstract B inherit(B from);

    public abstract T build();

    public static <T, B extends InheritingBuilder<T, B>> B inheritOrReplace(B primary, B secondary) {
        return primary.replace() ? primary : primary.inherit(secondary);
    }

    @Nullable
    public static <T, B extends InheritingBuilder<T, B>> B inheritOrReplaceOrChoose(@Nullable B primary, @Nullable B secondary) {
        if (secondary != null && primary != null) {
            return inheritOrReplace(primary, secondary);
        }
        return choose(primary, secondary);
    }

    @Nullable
    public static <T> T choose(@Nullable T primary, @Nullable T secondary) {
        return primary != null ? primary : secondary;
    }

    /**
     * Wrapper for a type adapter for an inheriting builder. Handles the serialization of the common values of an inheriting builder.
     *
     * @param serializer   the type adapter for serialization
     * @param deserializer the type adapter for deserialization
     */
    public record WrappedSerializer<T, B extends InheritingBuilder<T, B>>(JsonSerializer<B> serializer,
                                                                          JsonDeserializer<B> deserializer) implements JsonSerializer<B>, JsonDeserializer<B> {

        public static <T, B extends InheritingBuilder<T, B>, S extends JsonSerializer<B> & JsonDeserializer<B>> WrappedSerializer<T, B> of(S serializer) {
            return new WrappedSerializer<>(serializer, serializer);
        }

        @Override
        public B deserialize(JsonElement json, Type type, JsonDeserializationContext context) throws JsonParseException {
            B builder = deserializer.deserialize(json, type, context);
            JsonObject object = json.getAsJsonObject();
            builder.parent = object.has(KEY_PARENT) ? new ResourceLocation(object.get(KEY_PARENT).getAsString()) : null;
            builder.replace = object.has(KEY_REPLACE) ? object.get(KEY_REPLACE).getAsBoolean() : REPLACE_BY_DEFAULT;
            return builder;
        }

        @Override
        public JsonElement serialize(B builder, Type type, JsonSerializationContext context) {
            JsonObject object = serializer.serialize(builder, type, context).getAsJsonObject();
            // Make sure that the serialized builder doesn't use any of the keys we require to be free
            checkForCollision(object, KEY_PARENT, serializer);
            checkForCollision(object, KEY_REPLACE, serializer);
            if (builder.parent != null) {
                object.addProperty(KEY_PARENT, builder.parent.toString());
            }
            if (builder.replace != REPLACE_BY_DEFAULT) {
                object.addProperty(KEY_REPLACE, builder.replace);
            }
            return object;
        }

        private static void checkForCollision(JsonObject object, String fieldName, JsonSerializer<?> serializer) {
            if (object.has(fieldName)) {
                throw new IllegalStateException("Serialization type adapter " + serializer.getClass().getName() + " for an inheriting builder has put a value for field '" + fieldName + "'");
            }
        }
    }
}
