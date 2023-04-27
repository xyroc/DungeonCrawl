package xiroc.dungeoncrawl.dungeon.blueprint.feature.configuration;

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

public class SpawnerConfiguration extends FeatureConfiguration {
    public final IRandom<Delegate<SpawnerType>> spawnerTypes;

    public SpawnerConfiguration(FeatureConfiguration baseConfiguration, IRandom<Delegate<SpawnerType>> spawnerTypes) {
        super(baseConfiguration);
        this.spawnerTypes = spawnerTypes;
    }

    public static class Serializer implements JsonSerializer<SpawnerConfiguration>, JsonDeserializer<SpawnerConfiguration> {
        private static final String KEY_SPAWNER_TYPES = "spawner_type";

        @Override
        public SpawnerConfiguration deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
            JsonObject object = json.getAsJsonObject();
            FeatureConfiguration baseConfiguration = context.deserialize(json, FeatureConfiguration.class);
            IRandom<Delegate<SpawnerType>> spawnerTypes;
            JsonElement spawnerTypesJson = object.get(KEY_SPAWNER_TYPES);
            if (spawnerTypesJson.isJsonPrimitive()) {
                ResourceLocation key = new ResourceLocation(spawnerTypesJson.getAsString());
                spawnerTypes = new SingleValueRandom<>(Delegate.of(SpawnerTypes.get(key), key));
            } else {
                spawnerTypes = WeightedRandom.SPAWNER_TYPE.deserialize(spawnerTypesJson);
            }
            return new SpawnerConfiguration(baseConfiguration, spawnerTypes);
        }

        @Override
        public JsonElement serialize(SpawnerConfiguration src, Type typeOfSrc, JsonSerializationContext context) {
            JsonObject object = context.serialize(src, FeatureConfiguration.class).getAsJsonObject();
            if (src.spawnerTypes instanceof SingleValueRandom<Delegate<SpawnerType>> singleValueRandom) {
                object.add(KEY_SPAWNER_TYPES, singleValueRandom.value().serialize());
            } else if (src.spawnerTypes instanceof WeightedRandom<Delegate<SpawnerType>> weightedRandom) {
                object.add(KEY_SPAWNER_TYPES, WeightedRandom.SPAWNER_TYPE.serialize(weightedRandom));
            } else {
                throw new UnsupportedOperationException("Invalid spawner type random: " + src.spawnerTypes.getClass());
            }
            return object;
        }
    }
}
