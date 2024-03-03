package xiroc.dungeoncrawl.dungeon.blueprint.feature.configuration;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import xiroc.dungeoncrawl.dungeon.blueprint.anchor.Anchor;
import xiroc.dungeoncrawl.dungeon.blueprint.feature.PlacedFeature;
import xiroc.dungeoncrawl.dungeon.blueprint.feature.configuration.settings.PlacementSettings;
import xiroc.dungeoncrawl.dungeon.blueprint.feature.configuration.settings.SpawnerSettings;
import xiroc.dungeoncrawl.dungeon.blueprint.feature.type.SpawnerFeature;

import java.lang.reflect.Type;
import java.util.Random;

public record SpawnerConfiguration(PlacementSettings placement, SpawnerSettings spawner) implements FeatureConfiguration.AnchorBased {
    @Override
    public PlacedFeature createInstance(Anchor anchor, Random random) {
        return new SpawnerFeature(anchor.position(), spawner.types().roll(random));
    }

    @Override
    public int type() {
        return PlacedFeature.SPAWNER;
    }

    public static class Serializer implements JsonSerializer<SpawnerConfiguration>, JsonDeserializer<SpawnerConfiguration> {
        @Override
        public SpawnerConfiguration deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
            JsonObject object = json.getAsJsonObject();
            PlacementSettings placement = context.deserialize(object, PlacementSettings.class);
            SpawnerSettings spawner = context.deserialize(object.get(SharedSerializationConstants.KEY_SPAWNER_SETTINGS), SpawnerSettings.class);
            return new SpawnerConfiguration(placement, spawner);
        }

        @Override
        public JsonElement serialize(SpawnerConfiguration configuration, Type typeOfSrc, JsonSerializationContext context) {
            JsonObject object = context.serialize(configuration.placement).getAsJsonObject();
            object.add(SharedSerializationConstants.KEY_SPAWNER_SETTINGS, context.serialize(configuration.spawner));
            object.addProperty(SharedSerializationConstants.KEY_FEATURE_TYPE, FeatureConfiguration.TYPE_SPAWNER);
            return object;
        }
    }
}
