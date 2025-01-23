package xiroc.dungeoncrawl.dungeon.blueprint.feature;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.Rotation;
import org.jetbrains.annotations.Nullable;
import xiroc.dungeoncrawl.dungeon.blueprint.Blueprint;
import xiroc.dungeoncrawl.dungeon.blueprint.anchor.Anchor;
import xiroc.dungeoncrawl.dungeon.blueprint.feature.settings.ChestSettings;
import xiroc.dungeoncrawl.dungeon.blueprint.feature.settings.PlacementSettings;
import xiroc.dungeoncrawl.dungeon.blueprint.feature.settings.SpawnerSettings;
import xiroc.dungeoncrawl.dungeon.component.DungeonComponent;
import xiroc.dungeoncrawl.dungeon.generator.level.LevelGenerator;
import xiroc.dungeoncrawl.exception.DatapackLoadException;
import xiroc.dungeoncrawl.util.CoordinateSpace;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.function.Consumer;

public interface BlueprintFeature {
    ImmutableMap<String, Class<? extends BlueprintFeature>> TYPES = ImmutableMap.<String, Class<? extends BlueprintFeature>>builder()
            .put(SharedSerializationConstants.TYPE_CHEST, ChestFeature.class)
            .put(SharedSerializationConstants.TYPE_SPAWNER, SpawnerFeature.class)
            .put(SharedSerializationConstants.TYPE_FLOWER_POT, FlowerPotFeature.class)
            .put(SharedSerializationConstants.TYPE_SARCOPHAGUS, SarcophagusFeature.class)
            .put(SharedSerializationConstants.TYPE_FEATURE_CHAIN, Chain.class)
            .build();

    static void gsonAdapters(GsonBuilder builder) {
        builder.registerTypeAdapter(PlacementSettings.class, new PlacementSettings.Serializer())
                .registerTypeAdapter(ChestSettings.class, new ChestSettings.Serializer())
                .registerTypeAdapter(SpawnerSettings.class, new SpawnerSettings.Serializer())
                .registerTypeAdapter(BlueprintFeature.class, new Deserializer())
                .registerTypeAdapter(ChestFeature.class, new ChestFeature.Serializer())
                .registerTypeAdapter(SpawnerFeature.class, new SpawnerFeature.Serializer())
                .registerTypeAdapter(FlowerPotFeature.class, new FlowerPotFeature.Serializer())
                .registerTypeAdapter(SarcophagusFeature.class, new SarcophagusFeature.Serializer())
                .registerTypeAdapter(Chain.class, new Chain.Serializer());
    }

    void create(LevelGenerator levelGenerator, Consumer<DungeonComponent> consumer, @Nullable ArrayList<Anchor> positions, Blueprint blueprint, BlockPos offset, Rotation rotation);

    static @Nullable ArrayList<Anchor> gatherPositions(@Nullable ArrayList<Anchor> positions, Blueprint blueprint, PlacementSettings placement) {
        if (positions != null) {
            return positions;
        }
        return placement.anchors(blueprint).orElse(null);
    }

    record Chain(PlacementSettings placement, ImmutableList<BlueprintFeature> features) implements BlueprintFeature {
        @Override
        public void create(LevelGenerator levelGenerator, Consumer<DungeonComponent> consumer, @Nullable ArrayList<Anchor> positions, Blueprint blueprint, BlockPos offset, Rotation rotation) {
            var anchors = placement.anchors(blueprint);
            if (anchors.isEmpty()) {
                return;
            }
            positions = anchors.get();
            for (BlueprintFeature supplier : features) {
                supplier.create(levelGenerator, consumer, positions, blueprint, offset, rotation);
            }
        }

        public static class Serializer implements JsonSerializer<Chain>, JsonDeserializer<Chain> {
            String KEY_FEATURES = "features";

            @Override
            public Chain deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext context) throws JsonParseException {
                PlacementSettings placement = context.deserialize(jsonElement, PlacementSettings.class);
                JsonObject object = jsonElement.getAsJsonObject();
                JsonArray jsonFeatures = object.getAsJsonArray(KEY_FEATURES);
                ImmutableList.Builder<BlueprintFeature> features = ImmutableList.builder();
                for (JsonElement jsonFeature : jsonFeatures) {
                    BlueprintFeature configuration = context.deserialize(jsonFeature, BlueprintFeature.class);
                    features.add(configuration);
                }
                return new Chain(placement, features.build());
            }

            @Override
            public JsonElement serialize(Chain chain, Type type, JsonSerializationContext context) {
                JsonObject object = context.serialize(chain.placement).getAsJsonObject();
                object.addProperty(SharedSerializationConstants.KEY_FEATURE_TYPE, SharedSerializationConstants.TYPE_FEATURE_CHAIN);
                JsonArray features = new JsonArray();
                for (BlueprintFeature configuration : chain.features) {
                    features.add(context.serialize(configuration));
                }
                object.add(KEY_FEATURES, features);
                return object;
            }
        }
    }

    interface AnchorBased extends BlueprintFeature {
        @Override
        default void create(LevelGenerator levelGenerator, Consumer<DungeonComponent> features, @Nullable ArrayList<Anchor> positions, Blueprint blueprint, BlockPos offset, Rotation rotation) {
            if (positions == null) {
                positions = placement().anchors(blueprint).orElse(null);
                if (positions == null) {
                    return;
                }
            }
            CoordinateSpace coordinateSpace = blueprint.coordinateSpace(offset);
            placement().drawPositions(positions, levelGenerator.random, (anchor) -> features.accept(createInstance(levelGenerator, coordinateSpace.rotateAndTranslateToOrigin(anchor, rotation)
            )));
        }

        DungeonComponent createInstance(LevelGenerator levelGenerator, Anchor anchor);

        PlacementSettings placement();
    }

    class Deserializer implements JsonDeserializer<BlueprintFeature> {
        @Override
        public BlueprintFeature deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
            JsonObject object = jsonElement.getAsJsonObject();
            String typeName = object.get(SharedSerializationConstants.KEY_FEATURE_TYPE).getAsString();
            Class<? extends BlueprintFeature> featureType = TYPES.get(typeName);
            if (featureType == null) {
                throw new DatapackLoadException("Unknown blueprint feature type: \"" + typeName + "\"");
            }
            return jsonDeserializationContext.deserialize(object, featureType);
        }
    }
}
