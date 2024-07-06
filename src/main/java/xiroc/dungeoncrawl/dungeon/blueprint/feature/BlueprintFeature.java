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
import xiroc.dungeoncrawl.exception.DatapackLoadException;
import xiroc.dungeoncrawl.util.CoordinateSpace;
import xiroc.dungeoncrawl.util.random.value.RandomValue;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Random;
import java.util.function.Consumer;

public interface BlueprintFeature {
    String TYPE_CHEST = "chest";
    String TYPE_SPAWNER = "spawner";
    String TYPE_FLOWER_POT = "flower_pot";
    String TYPE_SARCOPHAGUS = "sarcophagus";
    String TYPE_FEATURE_CHAIN = "chain";

    ImmutableMap<String, Class<? extends BlueprintFeature>> TYPES = ImmutableMap.<String, Class<? extends BlueprintFeature>>builder()
            .put(TYPE_CHEST, ChestFeature.class)
            .put(TYPE_SPAWNER, SpawnerFeature.class)
            .put(TYPE_FLOWER_POT, FlowerPotFeature.class)
            .put(TYPE_SARCOPHAGUS, SarcophagusFeature.class)
            .put(TYPE_FEATURE_CHAIN, Chain.class)
            .build();

    static GsonBuilder gsonAdapters(GsonBuilder builder) {
        return RandomValue.gsonAdapters(builder)
                .registerTypeAdapter(PlacementSettings.class, new PlacementSettings.Serializer())
                .registerTypeAdapter(ChestSettings.class, new ChestSettings.Serializer())
                .registerTypeAdapter(SpawnerSettings.class, new SpawnerSettings.Serializer())
                .registerTypeAdapter(BlueprintFeature.class, new Deserializer())
                .registerTypeAdapter(ChestFeature.class, new ChestFeature.Serializer())
                .registerTypeAdapter(SpawnerFeature.class, new SpawnerFeature.Serializer())
                .registerTypeAdapter(FlowerPotFeature.class, new FlowerPotFeature.Serializer())
                .registerTypeAdapter(SarcophagusFeature.class, new SarcophagusFeature.Serializer())
                .registerTypeAdapter(Chain.class, new Chain.Serializer());
    }

    void create(Consumer<DungeonComponent> consumer, @Nullable ArrayList<Anchor> positions, Blueprint blueprint, BlockPos offset, Rotation rotation, Random random, int stage);

    static @Nullable ArrayList<Anchor> gatherPositions(@Nullable ArrayList<Anchor> positions, Blueprint blueprint, PlacementSettings placement) {
        if (positions != null) {
            return positions;
        }
        return placement.anchors(blueprint).orElse(null);
    }

    record Chain(PlacementSettings placement, ImmutableList<BlueprintFeature> features) implements BlueprintFeature {
        @Override
        public void create(Consumer<DungeonComponent> consumer, @Nullable ArrayList<Anchor> positions, Blueprint blueprint, BlockPos offset, Rotation rotation, Random random, int stage) {
            var anchors = placement.anchors(blueprint);
            if (anchors.isEmpty()) {
                return;
            }
            positions = anchors.get();
            for (BlueprintFeature supplier : features) {
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
                object.addProperty(SharedSerializationConstants.KEY_FEATURE_TYPE, TYPE_FEATURE_CHAIN);
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
        default void create(Consumer<DungeonComponent> features, @Nullable ArrayList<Anchor> positions, Blueprint blueprint, BlockPos offset, Rotation rotation, Random random, int stage) {
            if (positions == null) {
                positions = placement().anchors(blueprint).orElse(null);
                if (positions == null) {
                    return;
                }
            }
            CoordinateSpace coordinateSpace = blueprint.coordinateSpace(offset);
            placement().drawPositions(positions, random, (anchor) -> features.accept(createInstance(coordinateSpace.rotateAndTranslateToOrigin(anchor, rotation), random)));
        }

        DungeonComponent createInstance(Anchor anchor, Random random);

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
