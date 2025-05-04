package xiroc.dungeoncrawl.datapack.registry;

import com.google.gson.JsonArray;
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
import java.util.ArrayList;
import java.util.List;

public abstract class InheritingBuilder<T, B extends InheritingBuilder<T, B>> {
    public static final boolean REPLACE_BY_DEFAULT = true;
    public static final String KEY_REPLACE = "replace";
    private static final String KEY_PARENTS = "inherit";

    protected boolean doesReplace = REPLACE_BY_DEFAULT;
    /**
     * List of parents to inherit from, in order.
     */
    protected final List<ResourceLocation> parents = new ArrayList<>(1);

    @SuppressWarnings("unchecked")
    private B self() {
        return (B) this;
    }

    public B addParent(ResourceLocation parent) {
        this.parents.add(parent);
        return self();
    }

    public B addParents(List<ResourceLocation> parents) {
        this.parents.addAll(parents);
        return self();
    }

    public B replace(boolean replace) {
        this.doesReplace = replace;
        return self();
    }

    public List<ResourceLocation> getParents() {
        return parents;
    }

    public boolean doesReplace() {
        return doesReplace;
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
    public B tryBuild() {
        try {
            build();
        } catch (Exception e) {
            throw new IllegalStateException("Builder " + getClass().getName() + " threw an exception on build", e);
        }
        return self();
    }

    public abstract B inherit(B from);

    public abstract T build();

    public static <T, B extends InheritingBuilder<T, B>> B inheritOrReplace(B primary, B secondary) {
        return primary.doesReplace() ? primary : primary.inherit(secondary);
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
     * Checks for a given builder whether it must be serialized to a JSON object.
     *
     * @param builder The builder to check.
     * @return True if the builder must be serialized to a JSON object, false otherwise.
     */
    public static <B extends InheritingBuilder<?, B>> boolean mustSerializeToJsonObject(B builder) {
        return builder.doesReplace != REPLACE_BY_DEFAULT || !builder.parents.isEmpty();
    }

    /**
     * Wrapper for a type adapter for an inheriting builder. Handles the serialization of common values.
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
            if (!json.isJsonObject()) {
                return builder;
            }

            JsonObject object = json.getAsJsonObject();
            if (object.has(KEY_PARENTS)) {
                JsonElement parents = object.get(KEY_PARENTS);
                if (parents.isJsonArray()) {
                    for (JsonElement parent : parents.getAsJsonArray()) {
                        builder.parents.add(new ResourceLocation(parent.getAsString()));
                    }
                } else {
                    builder.parents.add(new ResourceLocation(parents.getAsString()));
                }
            }

            builder.doesReplace = object.has(KEY_REPLACE) ? object.get(KEY_REPLACE).getAsBoolean() : REPLACE_BY_DEFAULT;
            return builder;
        }

        @Override
        public JsonElement serialize(B builder, Type type, JsonSerializationContext context) {
            JsonElement json = serializer.serialize(builder, type, context);
            if (!json.isJsonObject()) {
                if (mustSerializeToJsonObject(builder)) {
                    throw new IllegalStateException("Inheriting builder with non-default configuration must serialize to a json object.");
                }
                return json;
            }
            JsonObject object = json.getAsJsonObject();
            // Make sure that the serialized builder doesn't use any of the keys we require to be free
            checkForCollision(object, KEY_PARENTS, serializer);
            checkForCollision(object, KEY_REPLACE, serializer);
            if (!builder.parents.isEmpty()) {
                JsonArray parents = new JsonArray();
                for (ResourceLocation key : builder.parents) {
                    parents.add(key.toString());
                }
                object.add(KEY_PARENTS, parents.size() == 1 ? parents.get(0) : parents);
            }
            if (builder.doesReplace != REPLACE_BY_DEFAULT) {
                object.addProperty(KEY_REPLACE, builder.doesReplace);
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
