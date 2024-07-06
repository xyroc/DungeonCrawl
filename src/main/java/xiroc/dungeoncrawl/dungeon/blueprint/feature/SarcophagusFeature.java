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
import xiroc.dungeoncrawl.dungeon.blueprint.feature.settings.SpawnerSettings;
import xiroc.dungeoncrawl.dungeon.component.DungeonComponent;
import xiroc.dungeoncrawl.dungeon.component.feature.SarcophagusComponent;

import java.lang.reflect.Type;
import java.util.Random;

public record SarcophagusFeature(PlacementSettings placement, ChestSettings chest, SpawnerSettings spawner) implements BlueprintFeature.AnchorBased {
    @Override
    public DungeonComponent createInstance(Anchor anchor, Random random) {
        return new SarcophagusComponent(anchor, spawner.types().roll(random), chest.lootTable());
    }

    public static class Serializer implements JsonSerializer<SarcophagusFeature>, JsonDeserializer<SarcophagusFeature> {
        @Override
        public SarcophagusFeature deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext context) throws JsonParseException {
            JsonObject object = jsonElement.getAsJsonObject();
            PlacementSettings placement = context.deserialize(object, PlacementSettings.class);
            ChestSettings chest = context.deserialize(object.get(SharedSerializationConstants.KEY_CHEST_SETTINGS), ChestSettings.class);
            SpawnerSettings spawner = context.deserialize(object.get(SharedSerializationConstants.KEY_SPAWNER_SETTINGS), SpawnerSettings.class);
            return new SarcophagusFeature(placement, chest, spawner);
        }

        @Override
        public JsonElement serialize(SarcophagusFeature configuration, Type type, JsonSerializationContext context) {
            JsonObject object = context.serialize(configuration.placement).getAsJsonObject();
            object.add(SharedSerializationConstants.KEY_CHEST_SETTINGS, context.serialize(configuration.chest));
            object.add(SharedSerializationConstants.KEY_SPAWNER_SETTINGS, context.serialize(configuration.spawner));
            object.addProperty(SharedSerializationConstants.KEY_FEATURE_TYPE, BlueprintFeature.TYPE_SARCOPHAGUS);
            return object;
        }
    }
}
