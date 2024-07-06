package xiroc.dungeoncrawl.dungeon.blueprint.feature;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import xiroc.dungeoncrawl.dungeon.blueprint.anchor.Anchor;
import xiroc.dungeoncrawl.dungeon.blueprint.feature.settings.ChestSettings;
import xiroc.dungeoncrawl.dungeon.blueprint.feature.settings.PlacementSettings;
import xiroc.dungeoncrawl.dungeon.component.DungeonComponent;
import xiroc.dungeoncrawl.dungeon.component.feature.ChestComponent;

import java.lang.reflect.Type;
import java.util.Random;

public record ChestFeature(PlacementSettings placement, ChestSettings chest) implements BlueprintFeature.AnchorBased {
    @Override
    public DungeonComponent createInstance(Anchor anchor, Random random) {
        return new ChestComponent(anchor, chest.lootTable());
    }

    public static class Serializer implements JsonSerializer<ChestFeature>, JsonDeserializer<ChestFeature> {
        @Override
        public ChestFeature deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
            JsonObject object = json.getAsJsonObject();
            PlacementSettings placement = context.deserialize(object, PlacementSettings.class);
            ChestSettings chest = context.deserialize(object.get(SharedSerializationConstants.KEY_CHEST_SETTINGS), ChestSettings.class);
            return new ChestFeature(placement, chest);
        }

        @Override
        public JsonElement serialize(ChestFeature configuration, Type typeOfSrc, JsonSerializationContext context) {
            JsonObject object = context.serialize(configuration.placement).getAsJsonObject();
            object.add(SharedSerializationConstants.KEY_CHEST_SETTINGS, context.serialize(configuration.chest));
            object.addProperty(SharedSerializationConstants.KEY_FEATURE_TYPE, BlueprintFeature.TYPE_CHEST);
            return object;
        }
    }
}
