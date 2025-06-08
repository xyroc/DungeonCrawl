package xiroc.dungeoncrawl.dungeon.blueprint.feature.settings;

import com.google.common.collect.Lists;
import com.google.gson.*;
import net.minecraft.resources.ResourceLocation;
import xiroc.dungeoncrawl.dungeon.blueprint.Blueprint;
import xiroc.dungeoncrawl.dungeon.blueprint.anchor.Anchor;
import xiroc.dungeoncrawl.util.JSONUtils;
import xiroc.dungeoncrawl.util.random.value.RandomValue;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Optional;
import java.util.Random;
import java.util.function.Consumer;

public record PlacementSettings(Optional<ResourceLocation> positions, RandomValue amount) {
    public Optional<ArrayList<Anchor>> anchors(Blueprint blueprint) {
        return positions.map(blueprint.anchors()::get).map(Lists::newArrayList);
    }

    public void drawPositions(ArrayList<Anchor> positions, Random random, Consumer<Anchor> consumer) {
        for (int count = amount.nextInt(random); count > 0 && !positions.isEmpty(); --count) {
            int anchor = random.nextInt(positions.size());
            consumer.accept(positions.remove(anchor));
        }
    }

    public static class Serializer implements JsonSerializer<PlacementSettings>, JsonDeserializer<PlacementSettings> {
        private static final String KEY_POSITIONS = "positions";
        private static final String KEY_AMOUNT = "amount";

        @Override
        public PlacementSettings deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext context) throws JsonParseException {
            JsonObject object = jsonElement.getAsJsonObject();
            Optional<ResourceLocation> positions = Optional.ofNullable(object.has(KEY_POSITIONS) ? new ResourceLocation(object.get(KEY_POSITIONS).getAsString()) : null);
            RandomValue amount = JSONUtils.parse(object.get(KEY_AMOUNT), RandomValue.CODEC);
            return new PlacementSettings(positions, amount);
        }

        @Override
        public JsonElement serialize(PlacementSettings instance, Type type, JsonSerializationContext context) {
            JsonObject object = new JsonObject();
            instance.positions.ifPresent(positions -> object.addProperty(KEY_POSITIONS, positions.toString()));
            object.add(KEY_AMOUNT, JSONUtils.encode(instance.amount, RandomValue.CODEC));
            return object;
        }
    }
}
