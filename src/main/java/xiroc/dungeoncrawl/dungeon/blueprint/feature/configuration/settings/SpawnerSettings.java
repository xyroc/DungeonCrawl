package xiroc.dungeoncrawl.dungeon.blueprint.feature.configuration.settings;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import net.minecraft.resources.ResourceLocation;
import xiroc.dungeoncrawl.datapack.delegate.Delegate;
import xiroc.dungeoncrawl.dungeon.monster.SpawnerType;
import xiroc.dungeoncrawl.dungeon.monster.SpawnerTypes;
import xiroc.dungeoncrawl.util.random.IRandom;
import xiroc.dungeoncrawl.util.random.SingleValueRandom;
import xiroc.dungeoncrawl.util.random.WeightedRandom;

import java.lang.reflect.Type;

public record SpawnerSettings(IRandom<Delegate<SpawnerType>> types) {
    public static class Serializer implements JsonSerializer<SpawnerSettings>, JsonDeserializer<SpawnerSettings> {
        private static final String KEY_TYPES = "type";

        @Override
        public SpawnerSettings deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
            JsonObject object = jsonElement.getAsJsonObject();
            IRandom<Delegate<SpawnerType>> spawnerTypes;
            JsonElement spawnerTypesJson = object.get(KEY_TYPES);
            if (spawnerTypesJson.isJsonPrimitive()) {
                ResourceLocation key = new ResourceLocation(spawnerTypesJson.getAsString());
                spawnerTypes = new SingleValueRandom<>(Delegate.of(SpawnerTypes.get(key), key));
            } else {
                spawnerTypes = WeightedRandom.SPAWNER_TYPE.deserialize(spawnerTypesJson);
            }
            return new SpawnerSettings(spawnerTypes);
        }

        @Override
        public JsonElement serialize(SpawnerSettings spawnerSettings, Type type, JsonSerializationContext context) {
            JsonObject object = new JsonObject();
            if (spawnerSettings.types instanceof SingleValueRandom<Delegate<SpawnerType>> singleValueRandom) {
                object.add(KEY_TYPES, singleValueRandom.value().serialize());
            } else if (spawnerSettings.types instanceof WeightedRandom<Delegate<SpawnerType>> weightedRandom) {
                object.add(KEY_TYPES, WeightedRandom.SPAWNER_TYPE.serialize(weightedRandom));
            } else {
                throw new UnsupportedOperationException("Invalid spawner type random: " + spawnerSettings.types.getClass());
            }
            return object;
        }
    }
}
