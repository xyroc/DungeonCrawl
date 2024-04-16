package xiroc.dungeoncrawl.dungeon.monster;

import com.google.gson.*;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;

import java.lang.reflect.Type;

public class SpawnerEntityType {
    protected final ResourceLocation entity;
    protected final SpawnerEntityProperties properties;

    public SpawnerEntityType(EntityType<?> entityType) {
        this(Registry.ENTITY_TYPE.getKey(entityType), null);
    }

    public SpawnerEntityType(EntityType<?> entityType, SpawnerEntityProperties properties) {
        this(Registry.ENTITY_TYPE.getKey(entityType), properties);
    }

    public SpawnerEntityType(ResourceLocation entity, SpawnerEntityProperties properties) {
        this.entity = entity;
        this.properties = properties;
    }

    public static class Serializer implements JsonSerializer<SpawnerEntityType>, JsonDeserializer<SpawnerEntityType> {
        private static final String KEY_ENTITY_TYPE = "name";
        private static final String KEY_PROPERTIES = "properties";

        @Override
        public SpawnerEntityType deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
            JsonObject object = json.getAsJsonObject();
            ResourceLocation entity = new ResourceLocation(object.get(KEY_ENTITY_TYPE).getAsString());
            SpawnerEntityProperties properties = null;
            if (object.has(KEY_PROPERTIES)) {
                properties = context.deserialize(object.get(KEY_PROPERTIES), SpawnerEntityProperties.class);
            }
            return new SpawnerEntityType(entity, properties);
        }

        @Override
        public JsonElement serialize(SpawnerEntityType type, Type typeOfSrc, JsonSerializationContext context) {
            JsonObject object = new JsonObject();
            object.addProperty(KEY_ENTITY_TYPE, type.entity.toString());
            if (type.properties != null) {
                object.add(KEY_PROPERTIES, context.serialize(type.properties));
            }
            return object;
        }
    }
}