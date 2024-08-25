package xiroc.dungeoncrawl.dungeon.blueprint.feature;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import xiroc.dungeoncrawl.DungeonCrawl;
import xiroc.dungeoncrawl.datapack.registry.DatapackRegistries;
import xiroc.dungeoncrawl.dungeon.blueprint.anchor.Anchor;
import xiroc.dungeoncrawl.dungeon.blueprint.feature.settings.PlacementSettings;
import xiroc.dungeoncrawl.dungeon.blueprint.feature.settings.SpawnerSettings;
import xiroc.dungeoncrawl.dungeon.component.DungeonComponent;
import xiroc.dungeoncrawl.dungeon.component.feature.SpawnerComponent;

import java.lang.reflect.Type;
import java.util.Random;

public record SpawnerFeature(PlacementSettings placement, SpawnerSettings spawner) implements BlueprintFeature.AnchorBased {
    @Override
    public DungeonComponent createInstance(Anchor anchor, Random random) {
        var spawnerType = spawner.types()
                .map(spawnerTypes -> spawnerTypes.roll(random))
                .orElse(DatapackRegistries.SPAWNER_TYPE.delegateOrThrow(DungeonCrawl.locate("default")));
        return new SpawnerComponent(anchor.position(), spawnerType);
    }

    public static class Serializer implements JsonSerializer<SpawnerFeature>, JsonDeserializer<SpawnerFeature> {
        @Override
        public SpawnerFeature deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
            JsonObject object = json.getAsJsonObject();
            PlacementSettings placement = context.deserialize(object, PlacementSettings.class);
            SpawnerSettings spawner = context.deserialize(object.get(SharedSerializationConstants.KEY_SPAWNER_SETTINGS), SpawnerSettings.class);
            return new SpawnerFeature(placement, spawner);
        }

        @Override
        public JsonElement serialize(SpawnerFeature configuration, Type typeOfSrc, JsonSerializationContext context) {
            JsonObject object = context.serialize(configuration.placement).getAsJsonObject();
            object.add(SharedSerializationConstants.KEY_SPAWNER_SETTINGS, context.serialize(configuration.spawner));
            object.addProperty(SharedSerializationConstants.KEY_FEATURE_TYPE, SharedSerializationConstants.TYPE_SPAWNER);
            return object;
        }
    }
}
