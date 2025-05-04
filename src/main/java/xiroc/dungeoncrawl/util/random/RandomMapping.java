package xiroc.dungeoncrawl.util.random;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import com.google.gson.reflect.TypeToken;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import xiroc.dungeoncrawl.datapack.registry.Delegate;
import xiroc.dungeoncrawl.datapack.registry.InheritingBuilder;
import xiroc.dungeoncrawl.dungeon.theme.PrimaryTheme;
import xiroc.dungeoncrawl.dungeon.theme.SecondaryTheme;
import xiroc.dungeoncrawl.dungeon.type.DungeonType;
import xiroc.dungeoncrawl.exception.DatapackLoadException;

import javax.annotation.Nullable;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Objects;
import java.util.Random;
import java.util.function.BiConsumer;

public class RandomMapping<V> {
    private final IRandom<Delegate<V>> fallback;
    private ImmutableMap<ResourceLocation, IRandom<Delegate<V>>> entries;
    private final ImmutableList<TagReference<V>> tagReferences;

    private RandomMapping(Builder<V> builder) {
        Objects.requireNonNull(builder.fallback, "No fallback pool was provided");
        try {
            this.fallback = builder.fallback.build();
        } catch (Exception e) {
            throw new DatapackLoadException("Invalid fallback pool: " + e.getMessage());
        }
        ImmutableMap.Builder<ResourceLocation, IRandom<Delegate<V>>> entries = ImmutableMap.builder();
        builder.entries.forEach((key, value) -> {
            try {
                entries.put(key, value.build());
            } catch (Exception e) {
                throw new DatapackLoadException("Invalid pool for key '" + key + "': " + e.getMessage());
            }
        });
        this.entries = entries.build();
        ImmutableList.Builder<TagReference<V>> tagReferences = ImmutableList.builder();
        builder.tagEntries.forEach((tagKey, entriesBuilder) -> tagReferences.add(new TagReference<>(tagKey, entriesBuilder.build())));
        this.tagReferences = tagReferences.build();
    }

    public Delegate<V> roll(ResourceLocation key, Random random) {
        if (key == null) {
            return fallback.roll(random);
        }
        return entries.getOrDefault(key, fallback).roll(random);
    }

    public <T> void resolveTagReferences(Registry<T> registry) {
        if (tagReferences.isEmpty()) {
            return;
        }
        final HashMap<ResourceLocation, IRandom.Builder<Delegate<V>>> updatedEntries = new HashMap<>();
        this.entries.forEach((key, entries) -> updatedEntries.put(key, IRandom.Builder.copy(entries)));
        tagReferences.forEach(reference -> reference.resolve(registry, (key, values) -> updatedEntries.computeIfAbsent(key, ignored -> new IRandom.Builder<>()).add(values)));

        ImmutableMap.Builder<ResourceLocation, IRandom<Delegate<V>>> builder = ImmutableMap.builder();
        updatedEntries.forEach((key, entriesBuilder) -> builder.put(key, entriesBuilder.build()));
        this.entries = builder.build();
    }

    public static class Builder<V> extends InheritingBuilder<RandomMapping<V>, Builder<V>> {
        @Nullable
        private IRandom.Builder<Delegate<V>> fallback = null;
        private final HashMap<ResourceLocation, IRandom.Builder<Delegate<V>>> entries = new HashMap<>();
        private final HashMap<ResourceLocation, IRandom.Builder<Delegate<V>>> tagEntries = new HashMap<>();

        private IRandom.Builder<Delegate<V>> get(ResourceLocation key) {
            return this.entries.computeIfAbsent(key, ignored -> new IRandom.Builder<>());
        }

        private IRandom.Builder<Delegate<V>> getTag(ResourceLocation tagKey) {
            return this.tagEntries.computeIfAbsent(tagKey, ignored -> new IRandom.Builder<>());
        }

        public Builder<V> add(ResourceLocation key, Delegate<V> entry) {
            return add(key, entry, 1);
        }

        public Builder<V> add(ResourceLocation key, Delegate<V> entry, int weight) {
            get(key).add(entry, weight);
            return this;
        }

        public Builder<V> add(ResourceLocation key, IRandom.Builder<Delegate<V>> entries) {
            get(key).add(entries);
            return this;
        }

        public Builder<V> addTag(ResourceLocation tagKey, IRandom.Builder<Delegate<V>> entries) {
            getTag(tagKey).add(entries);
            return this;
        }

        public Builder<V> addTag(TagKey<?> tagKey, IRandom.Builder<Delegate<V>> entries) {
            return addTag(tagKey.location(), entries);
        }

        public Builder<V> fallback(IRandom.Builder<Delegate<V>> fallback) {
            this.fallback = fallback;
            return this;
        }

        @Override
        public Builder<V> inherit(Builder<V> from) {
            this.fallback = InheritingBuilder.inheritOrReplaceOrChoose(this.fallback, from.fallback);
            from.entries.forEach((key, entriesBuilder) -> {
                if (entriesBuilder.doesReplace()) {
                    this.entries.put(key, entriesBuilder);
                } else {
                    this.entries.get(key).inherit(entriesBuilder);
                }
            });
            from.tagEntries.forEach((tagKey, entriesBuilder) -> {
                if (entriesBuilder.doesReplace()) {
                    this.tagEntries.put(tagKey, entriesBuilder);
                } else {
                    this.tagEntries.get(tagKey).inherit(entriesBuilder);
                }
            });
            return this;
        }

        public RandomMapping<V> build() {
            return new RandomMapping<>(this);
        }
    }

    public record BuilderSerializer<V>(Type builderType) implements JsonSerializer<Builder<V>>, JsonDeserializer<Builder<V>> {
        private static final String KEY_FALLBACK = "default";
        private static final String KEY_MAPPING = "mapping";
        private static final String TAG_PREFIX = "#";

        @Override
        public Builder<V> deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
            Builder<V> builder = new Builder<>();
            JsonObject object = json.getAsJsonObject();
            if (object.has(KEY_FALLBACK)) {
                builder.fallback = context.deserialize(object.get(KEY_FALLBACK), builderType);
            }
            if (object.has(KEY_MAPPING)) {
                JsonObject mapping = object.getAsJsonObject(KEY_MAPPING);
                mapping.entrySet().forEach((entry) -> {
                    if (entry.getKey().startsWith(TAG_PREFIX)) {
                        ResourceLocation tagKey = new ResourceLocation(entry.getKey().substring(1));
                        builder.tagEntries.put(tagKey, context.deserialize(entry.getValue(), builderType));
                        return;
                    }
                    final var key = new ResourceLocation(entry.getKey());
                    final IRandom.Builder<Delegate<V>> entries = context.deserialize(entry.getValue(), builderType);
                    builder.add(key, entries);
                });
            }
            return builder;
        }

        @Override
        public JsonElement serialize(Builder<V> src, Type typeOfSrc, JsonSerializationContext context) {
            JsonObject object = new JsonObject();
            JsonObject jsonMapping = new JsonObject();
            src.entries.forEach((key, value) -> jsonMapping.add(key.toString(), context.serialize(value, builderType)));
            src.tagEntries.forEach((tagKey, value) -> jsonMapping.add(TAG_PREFIX + tagKey.toString(), context.serialize(value, builderType)));
            if (src.fallback != null) {
                object.add(KEY_FALLBACK, context.serialize(src.fallback, builderType));
            }
            object.add(KEY_MAPPING, jsonMapping);
            return object;
        }
    }

    public record TagReference<V>(ResourceLocation tag, IRandom<Delegate<V>> entries) {
        public <T> void resolve(Registry<T> registry, BiConsumer<ResourceLocation, IRandom<Delegate<V>>> consumer) {
            TagKey<T> tagKey = TagKey.create(registry.key(), tag);
            registry.getTag(tagKey).ifPresentOrElse(tag -> tag.forEach(holder -> {
                ResourceLocation valueKey = registry.getKey(holder.value());
                consumer.accept(valueKey, entries);
            }), () -> {
                throw new DatapackLoadException("The tag " + tagKey + " does not exist for registry " + registry.key());
            });
        }
    }

    public interface Types {
        Type PRIMARY_THEME = new TypeToken<Builder<PrimaryTheme>>() {}.getType();
        Type SECONDARY_THEME = new TypeToken<Builder<SecondaryTheme>>() {}.getType();
        Type DUNGEON_TYPE = new TypeToken<Builder<DungeonType>>() {}.getType();
    }

    public static void gsonAdapters(GsonBuilder builder) {
        builder.registerTypeAdapter(Types.PRIMARY_THEME, new BuilderSerializer<PrimaryTheme>(PrimaryTheme.Types.RANDOM_BUILDER));
        builder.registerTypeAdapter(Types.SECONDARY_THEME, new BuilderSerializer<SecondaryTheme>(SecondaryTheme.Types.RANDOM_BUILDER));
        builder.registerTypeAdapter(Types.DUNGEON_TYPE, new BuilderSerializer<DungeonType>(DungeonType.Types.RANDOM_BUILDER));
    }
}