package xiroc.dungeoncrawl.dungeon.blueprint.feature;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import xiroc.dungeoncrawl.dungeon.block.provider.BlockStateProvider;
import xiroc.dungeoncrawl.dungeon.blueprint.anchor.Anchor;
import xiroc.dungeoncrawl.dungeon.blueprint.feature.settings.PlacementSettings;
import xiroc.dungeoncrawl.dungeon.component.DungeonComponent;
import xiroc.dungeoncrawl.dungeon.component.feature.FlowerPotComponent;

import java.lang.reflect.Type;
import java.util.Random;

public record FlowerPotFeature(PlacementSettings placement, Block soil, BlockStateProvider flowers) implements BlueprintFeature.AnchorBased {
    @Override
    public DungeonComponent createInstance(Anchor anchor, Random random) {
        return new FlowerPotComponent(anchor.position(), soil, flowers.get(anchor.position(), random).getBlock());
    }

    public static class Serializer implements JsonSerializer<FlowerPotFeature>, JsonDeserializer<FlowerPotFeature> {
        private static final String KEY_SOIL = "soil";
        private static final String KEY_FLOWERS = "flowers";

        @Override
        public FlowerPotFeature deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
            JsonObject object = json.getAsJsonObject();
            PlacementSettings placement = context.deserialize(object, PlacementSettings.class);
            Block soil = Blocks.PODZOL;
            if (object.has(KEY_SOIL)) {
                soil = Registry.BLOCK.get(new ResourceLocation(object.get(KEY_SOIL).getAsString()));
            }
            BlockStateProvider flowers = BlockStateProvider.deserialize(object.get(KEY_FLOWERS));
            return new FlowerPotFeature(placement, soil, flowers);
        }

        @Override
        public JsonElement serialize(FlowerPotFeature configuration, Type typeOfSrc, JsonSerializationContext context) {
            JsonObject object = context.serialize(configuration.placement).getAsJsonObject();
            object.addProperty(KEY_SOIL, Registry.BLOCK.getKey(configuration.soil).toString());
            object.add(KEY_FLOWERS, BlockStateProvider.GSON.toJsonTree(configuration.flowers));
            object.addProperty(SharedSerializationConstants.KEY_FEATURE_TYPE, BlueprintFeature.TYPE_FLOWER_POT);
            return object;
        }
    }
}
