package xiroc.dungeoncrawl.dungeon.blueprint.feature.settings;

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
import java.util.Optional;

public record SpawnerSettings(Optional<IRandom<Delegate<SpawnerType>>> types) {
    public SpawnerSettings() {
        this(Optional.empty());
    }

    public static class Serializer implements JsonSerializer<SpawnerSettings>, JsonDeserializer<SpawnerSettings> {
        private static final String KEY_TYPES = "type";

        @Override
        public SpawnerSettings deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
            JsonObject object = jsonElement.getAsJsonObject();
            Optional<IRandom<Delegate<SpawnerType>>> spawnerTypes = Optional.empty();
            if (object.has(KEY_TYPES)) {
                spawnerTypes = Optional.of(IRandom.SPAWNER_TYPE.deserialize(object.get(KEY_TYPES)));
            }
            return new SpawnerSettings(spawnerTypes);
        }

        @Override
        public JsonElement serialize(SpawnerSettings spawnerSettings, Type type, JsonSerializationContext context) {
            JsonObject object = new JsonObject();
            spawnerSettings.types.ifPresent(spawnerTypes -> object.add(KEY_TYPES, IRandom.SPAWNER_TYPE.serialize(spawnerTypes)));
            return object;
        }
    }
}
