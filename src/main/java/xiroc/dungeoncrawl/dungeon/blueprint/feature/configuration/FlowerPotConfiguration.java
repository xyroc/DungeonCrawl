package xiroc.dungeoncrawl.dungeon.blueprint.feature.configuration;

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

import java.lang.reflect.Type;

public class FlowerPotConfiguration extends FeatureConfiguration {
    public final Block soil;
    public final BlockStateProvider flowers;
    public FlowerPotConfiguration(FeatureConfiguration baseConfiguration, Block soil, BlockStateProvider flowers) {
        super(baseConfiguration);
        this.soil = soil;
        this.flowers = flowers;
    }

    public static class Serializer implements JsonSerializer<FlowerPotConfiguration>, JsonDeserializer<FlowerPotConfiguration> {
        private static final String KEY_SOIL = "soil";
        private static final String KEY_FLOWERS = "flowers";

        @Override
        public FlowerPotConfiguration deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
            FeatureConfiguration baseConfiguration = context.deserialize(json, FeatureConfiguration.class);
            JsonObject object = json.getAsJsonObject();
            Block soil = Blocks.PODZOL;
            if (object.has(KEY_SOIL)) {
                soil = Registry.BLOCK.get(new ResourceLocation(object.get(KEY_SOIL).getAsString()));
            }
            BlockStateProvider flowers = BlockStateProvider.deserialize(object.get(KEY_FLOWERS));
            return new FlowerPotConfiguration(baseConfiguration, soil, flowers);
        }

        @Override
        public JsonElement serialize(FlowerPotConfiguration src, Type typeOfSrc, JsonSerializationContext context) {
            JsonObject object = context.serialize(src, FeatureConfiguration.class).getAsJsonObject();
            object.addProperty(KEY_SOIL, Registry.BLOCK.getKey(src.soil).toString());
            object.add(KEY_FLOWERS, BlockStateProvider.GSON.toJsonTree(src.flowers));
            return object;
        }
    }
}
