package xiroc.dungeoncrawl.dungeon.blueprint.feature.configuration.settings;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import xiroc.dungeoncrawl.datapack.delegate.Delegate;
import xiroc.dungeoncrawl.dungeon.monster.SpawnerType;
import xiroc.dungeoncrawl.util.random.IRandom;

import java.lang.reflect.Type;

public record SpawnerSettings(IRandom<Delegate<SpawnerType>> types) {
    public static class Serializer implements JsonSerializer<SpawnerSettings>, JsonDeserializer<SpawnerSettings> {
        private static final String KEY_TYPES = "type";

        @Override
        public SpawnerSettings deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
            JsonObject object = jsonElement.getAsJsonObject();
            IRandom<Delegate<SpawnerType>> spawnerTypes = IRandom.SPAWNER_TYPE.deserialize(object.get(KEY_TYPES));
            return new SpawnerSettings(spawnerTypes);
        }

        @Override
        public JsonElement serialize(SpawnerSettings spawnerSettings, Type type, JsonSerializationContext context) {
            JsonObject object = new JsonObject();
            object.add(KEY_TYPES, IRandom.SPAWNER_TYPE.serialize(spawnerSettings.types));
            return object;
        }
    }
}
