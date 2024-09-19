package xiroc.dungeoncrawl.dungeon.monster;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import xiroc.dungeoncrawl.datapack.registry.DatapackRegistries;
import xiroc.dungeoncrawl.datapack.registry.Delegate;
import xiroc.dungeoncrawl.datapack.registry.InheritingBuilder;
import xiroc.dungeoncrawl.datapack.registry.InheritingDelegate;

import javax.annotation.Nullable;
import java.lang.reflect.Type;
import java.util.Objects;
import java.util.Optional;

public record SpawnerEntityType(ResourceLocation entity, Optional<Delegate<SpawnerEntityProperties>> properties) {
    public SpawnerEntityType(EntityType<?> entityType) {
        this(Registry.ENTITY_TYPE.getKey(entityType), Optional.empty());
    }

    public SpawnerEntityType(EntityType<?> entityType, SpawnerEntityProperties properties) {
        this(Registry.ENTITY_TYPE.getKey(entityType), Optional.of(Delegate.of(properties)));
    }

    public static class Builder extends InheritingBuilder<SpawnerEntityType, Builder> {
        @Nullable
        private ResourceLocation entity = null;

        @Nullable
        private InheritingDelegate<SpawnerEntityProperties, SpawnerEntityProperties.Builder> properties = null;

        public Builder entity(@Nullable ResourceLocation entity) {
            this.entity = entity;
            return this;
        }

        public Builder properties(SpawnerEntityProperties.Builder builder) {
            this.properties = InheritingDelegate.ofBuilder(builder);
            return this;
        }

        public Builder properties(ResourceLocation key) {
            this.properties = InheritingDelegate.ofKey(key);
            return this;
        }

        @Override
        public Builder inherit(Builder from) {
            entity = choose(entity, from.entity);
            properties = InheritingDelegate.inheritOrChoose(properties, from.properties);
            return this;
        }

        @Override
        public SpawnerEntityType build() {
            Objects.requireNonNull(entity);
            return new SpawnerEntityType(entity, Optional.ofNullable(properties).map(delegate -> delegate.transform(DatapackRegistries.SPAWNER_ENTITY_PROPERTIES)));
        }
    }

    public static class BuilderSerializer implements JsonSerializer<Builder>, JsonDeserializer<Builder> {
        private static final String KEY_ENTITY_TYPE = "name";
        private static final String KEY_PROPERTIES = "properties";

        @Override
        public Builder deserialize(JsonElement json, Type type, JsonDeserializationContext context) throws JsonParseException {
            JsonObject object = json.getAsJsonObject();
            Builder builder = new Builder();
            builder.deserializeBase(object);
            builder.entity = new ResourceLocation(object.get(KEY_ENTITY_TYPE).getAsString());
            if (object.has(KEY_PROPERTIES)) {
                builder.properties = InheritingDelegate.deserialize(object.get(KEY_PROPERTIES), (properties) -> SpawnerSerializers.ENTITY_PROPERTIES.fromJson(properties, SpawnerEntityProperties.Builder.class));
            }
            return builder;
        }

        @Override
        public JsonElement serialize(Builder builder, Type type, JsonSerializationContext context) {
            JsonObject object = new JsonObject();
            builder.serializeBase(object);
            object.addProperty(KEY_ENTITY_TYPE, Objects.requireNonNull(builder.entity).toString());
            if (builder.properties != null) {
                object.add(KEY_PROPERTIES, builder.properties.serialize(SpawnerSerializers.ENTITY_PROPERTIES::toJsonTree));
            }
            return object;
        }
    }
}