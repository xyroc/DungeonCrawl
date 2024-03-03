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
import xiroc.dungeoncrawl.dungeon.blueprint.feature.configuration.settings.ChestSettings;
import xiroc.dungeoncrawl.dungeon.blueprint.feature.configuration.settings.PlacementSettings;
import xiroc.dungeoncrawl.dungeon.blueprint.feature.configuration.settings.SpawnerSettings;
import xiroc.dungeoncrawl.dungeon.blueprint.feature.type.SarcophagusFeature;

import java.lang.reflect.Type;
import java.util.Random;

public record SarcophagusConfiguration(PlacementSettings placement, ChestSettings chest, SpawnerSettings spawner) implements FeatureConfiguration.AnchorBased {
    @Override
    public PlacedFeature createInstance(Anchor anchor, Random random) {
        return new SarcophagusFeature(anchor, spawner.types().roll(random), chest.lootTable());
    }

    @Override
    public int type() {
        return PlacedFeature.SARCOPHAGUS;
    }

    public static class Serializer implements JsonSerializer<SarcophagusConfiguration>, JsonDeserializer<SarcophagusConfiguration> {
        @Override
        public SarcophagusConfiguration deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext context) throws JsonParseException {
            JsonObject object = jsonElement.getAsJsonObject();
            PlacementSettings placement = context.deserialize(object, PlacementSettings.class);
            ChestSettings chest = context.deserialize(object.get(SharedSerializationConstants.KEY_CHEST_SETTINGS), ChestSettings.class);
            SpawnerSettings spawner = context.deserialize(object.get(SharedSerializationConstants.KEY_SPAWNER_SETTINGS), SpawnerSettings.class);
            return new SarcophagusConfiguration(placement, chest, spawner);
        }

        @Override
        public JsonElement serialize(SarcophagusConfiguration configuration, Type type, JsonSerializationContext context) {
            JsonObject object = context.serialize(configuration.placement).getAsJsonObject();
            object.add(SharedSerializationConstants.KEY_CHEST_SETTINGS, context.serialize(configuration.chest));
            object.add(SharedSerializationConstants.KEY_SPAWNER_SETTINGS, context.serialize(configuration.spawner));
            object.addProperty(SharedSerializationConstants.KEY_FEATURE_TYPE, FeatureConfiguration.TYPE_SARCOPHAGUS);
            return object;
        }
    }
}
