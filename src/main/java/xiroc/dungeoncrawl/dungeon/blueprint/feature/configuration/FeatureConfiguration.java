package xiroc.dungeoncrawl.dungeon.blueprint.feature.configuration;

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
import xiroc.dungeoncrawl.dungeon.blueprint.feature.FeatureSet;
import xiroc.dungeoncrawl.dungeon.blueprint.feature.PlacedFeature;
import xiroc.dungeoncrawl.dungeon.blueprint.feature.configuration.settings.ChestSettings;
import xiroc.dungeoncrawl.dungeon.blueprint.feature.configuration.settings.PlacementSettings;
import xiroc.dungeoncrawl.dungeon.blueprint.feature.configuration.settings.SpawnerSettings;
import xiroc.dungeoncrawl.exception.DatapackLoadException;
import xiroc.dungeoncrawl.util.CoordinateSpace;
import xiroc.dungeoncrawl.util.random.value.RandomValue;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Random;
import java.util.function.Consumer;

public interface FeatureConfiguration {
    String TYPE_CHEST = "chest";
    String TYPE_SPAWNER = "spawner";
    String TYPE_FLOWER_POT = "flower_pot";
    String TYPE_SARCOPHAGUS = "sarcophagus";
    String TYPE_FEATURE_CHAIN = "chain";

    ImmutableMap<String, Class<? extends FeatureConfiguration>> TYPES = ImmutableMap.<String, Class<? extends FeatureConfiguration>>builder()
            .put(TYPE_CHEST, ChestConfiguration.class)
            .put(TYPE_SPAWNER, SpawnerConfiguration.class)
            .put(TYPE_FLOWER_POT, FlowerPotConfiguration.class)
            .put(TYPE_SARCOPHAGUS, SarcophagusConfiguration.class)
            .put(TYPE_FEATURE_CHAIN, Chain.class)
            .build();

    static GsonBuilder gsonAdapters(GsonBuilder builder) {
        return RandomValue.gsonAdapters(builder)
                .registerTypeAdapter(PlacementSettings.class, new PlacementSettings.Serializer())
                .registerTypeAdapter(ChestSettings.class, new ChestSettings.Serializer())
                .registerTypeAdapter(SpawnerSettings.class, new SpawnerSettings.Serializer())
                .registerTypeAdapter(FeatureConfiguration.class, new Deserializer())
                .registerTypeAdapter(ChestConfiguration.class, new ChestConfiguration.Serializer())
                .registerTypeAdapter(SpawnerConfiguration.class, new SpawnerConfiguration.Serializer())
                .registerTypeAdapter(FlowerPotConfiguration.class, new FlowerPotConfiguration.Serializer())
                .registerTypeAdapter(SarcophagusConfiguration.class, new SarcophagusConfiguration.Serializer())
                .registerTypeAdapter(Chain.class, new Chain.Serializer());
    }

    void create(Consumer<FeatureSet> consumer, @Nullable ArrayList<Anchor> positions, Blueprint blueprint, BlockPos offset, Rotation rotation, Random random, int stage);

    static @Nullable ArrayList<Anchor> gatherPositions(@Nullable ArrayList<Anchor> positions, Blueprint blueprint, PlacementSettings placement) {
        if (positions != null) {
            return positions;
        }
        return placement.anchors(blueprint).orElse(null);
    }

    record Chain(PlacementSettings placement, ImmutableList<FeatureConfiguration> features) implements FeatureConfiguration {
        @Override
        public void create(Consumer<FeatureSet> consumer, @Nullable ArrayList<Anchor> positions, Blueprint blueprint, BlockPos offset, Rotation rotation, Random random, int stage) {
            var anchors = placement.anchors(blueprint);
            if (anchors.isEmpty()) {
                return;
            }
            positions = anchors.get();
            for (FeatureConfiguration supplier : features) {
                supplier.create(consumer, positions, blueprint, offset, rotation, random, stage);
            }
        }

        public static class Serializer implements JsonSerializer<Chain>, JsonDeserializer<Chain> {
            String KEY_FEATURES = "features";

            @Override
            public Chain deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext context) throws JsonParseException {
                PlacementSettings placement = context.deserialize(jsonElement, PlacementSettings.class);
                JsonObject object = jsonElement.getAsJsonObject();
                JsonArray jsonFeatures = object.getAsJsonArray(KEY_FEATURES);
                ImmutableList.Builder<FeatureConfiguration> features = ImmutableList.builder();
                for (JsonElement jsonFeature : jsonFeatures) {
                    FeatureConfiguration configuration = context.deserialize(jsonFeature, FeatureConfiguration.class);
                    features.add(configuration);
                }
                return new Chain(placement, features.build());
            }

            @Override
            public JsonElement serialize(Chain chain, Type type, JsonSerializationContext context) {
                JsonObject object = context.serialize(chain.placement).getAsJsonObject();
                object.addProperty(SharedSerializationConstants.KEY_FEATURE_TYPE, TYPE_FEATURE_CHAIN);
                JsonArray features = new JsonArray();
                for (FeatureConfiguration configuration : chain.features) {
                    features.add(context.serialize(configuration));
                }
                object.add(KEY_FEATURES, features);
                return object;
            }
        }
    }

    interface AnchorBased extends FeatureConfiguration {
        @Override
        default void create(Consumer<FeatureSet> consumer, @Nullable ArrayList<Anchor> positions, Blueprint blueprint, BlockPos offset, Rotation rotation, Random random, int stage) {
            if (positions == null) {
                positions = placement().anchors(blueprint).orElse(null);
                if (positions == null) {
                    return;
                }
            }
            CoordinateSpace coordinateSpace = blueprint.coordinateSpace(offset);
            ArrayList<PlacedFeature> features = new ArrayList<>();
            placement().drawPositions(positions, random, (anchor) -> features.add(createInstance(coordinateSpace.rotateAndTranslateToOrigin(anchor, rotation), random)));
            consumer.accept(new FeatureSet(type(), features));
        }

        PlacedFeature createInstance(Anchor anchor, Random random);

        int type();

        PlacementSettings placement();
    }

    class Deserializer implements JsonDeserializer<FeatureConfiguration> {
        @Override
        public FeatureConfiguration deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
            JsonObject object = jsonElement.getAsJsonObject();
            String typeName = object.get(SharedSerializationConstants.KEY_FEATURE_TYPE).getAsString();
            Class<? extends FeatureConfiguration> featureType = TYPES.get(typeName);
            if (featureType == null) {
                throw new DatapackLoadException("Unknown blueprint feature type: \"" + typeName + "\"");
            }
            return jsonDeserializationContext.deserialize(object, featureType);
        }
    }
}
