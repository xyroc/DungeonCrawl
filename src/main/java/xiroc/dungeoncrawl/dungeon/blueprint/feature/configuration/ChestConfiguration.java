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
import xiroc.dungeoncrawl.dungeon.blueprint.feature.type.ChestFeature;

import java.lang.reflect.Type;
import java.util.Random;

public record ChestConfiguration(PlacementSettings placement, ChestSettings chest) implements FeatureConfiguration.AnchorBased {
    @Override
    public PlacedFeature createInstance(Anchor anchor, Random random) {
        return new ChestFeature(anchor, chest.lootTable());
    }

    @Override
    public int type() {
        return PlacedFeature.CHEST;
    }

    public static class Serializer implements JsonSerializer<ChestConfiguration>, JsonDeserializer<ChestConfiguration> {
        @Override
        public ChestConfiguration deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
            JsonObject object = json.getAsJsonObject();
            PlacementSettings placement = context.deserialize(object, PlacementSettings.class);
            ChestSettings chest = context.deserialize(object.get(SharedSerializationConstants.KEY_CHEST_SETTINGS), ChestSettings.class);
            return new ChestConfiguration(placement, chest);
        }

        @Override
        public JsonElement serialize(ChestConfiguration configuration, Type typeOfSrc, JsonSerializationContext context) {
            JsonObject object = context.serialize(configuration.placement).getAsJsonObject();
            object.add(SharedSerializationConstants.KEY_CHEST_SETTINGS, context.serialize(configuration.chest));
            object.addProperty(SharedSerializationConstants.KEY_FEATURE_TYPE, FeatureConfiguration.TYPE_CHEST);
            return object;
        }
    }
}
