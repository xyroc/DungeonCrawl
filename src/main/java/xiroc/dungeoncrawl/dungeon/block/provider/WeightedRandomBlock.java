package xiroc.dungeoncrawl.dungeon.block.provider;

import com.google.gson.JsonArray;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.registries.ForgeRegistries;
import xiroc.dungeoncrawl.exception.DatapackLoadException;
import xiroc.dungeoncrawl.util.JSONUtils;
import xiroc.dungeoncrawl.util.random.WeightedRandom;

import java.lang.reflect.Type;
import java.util.Random;

public class WeightedRandomBlock implements BlockStateProvider {
    private final WeightedRandom<BlockState> randomBlockState;

    public WeightedRandomBlock(WeightedRandom<BlockState> randomBlockState) {
        this.randomBlockState = randomBlockState;
    }

    @Override
    public BlockState get(LevelAccessor world, BlockPos pos, Random random, Rotation rotation) {
        return randomBlockState.roll(random);
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {

        private final WeightedRandom.Builder<BlockState> builder = new WeightedRandom.Builder<>();

        public Builder add(Block block) {
            return add(block.defaultBlockState(), 1);
        }

        public Builder add(Block block, int weight) {
            return add(block.defaultBlockState(), weight);
        }

        public Builder add(BlockState state) {
            return add(state, 1);
        }

        public Builder add(BlockState state, int weight) {
            builder.add(state, weight);
            return this;
        }

        public WeightedRandomBlock build() {
            return new WeightedRandomBlock(builder.build());
        }

    }

    public static class Serializer implements JsonSerializer<WeightedRandomBlock>, JsonDeserializer<WeightedRandomBlock> {
        private static final String KEY_BLOCKS = "blocks";

        @Override
        public WeightedRandomBlock deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
            JsonArray blocks = json.getAsJsonObject().getAsJsonArray(KEY_BLOCKS);
            WeightedRandomBlock.Builder builder = builder();

            blocks.forEach((element) -> {
                JsonObject jsonBlock = element.getAsJsonObject();
                ResourceLocation blockName = new ResourceLocation(jsonBlock.get("block").getAsString());
                Block block = ForgeRegistries.BLOCKS.getValue(blockName);
                if (block == null) {
                    throw new DatapackLoadException("Unknown block: " + blockName);
                }
                builder.add(JSONUtils.deserializeBlockStateProperties(block, jsonBlock), JSONUtils.getWeight(jsonBlock));
            });

            return builder.build();
        }

        @Override
        public JsonElement serialize(WeightedRandomBlock src, Type typeOfSrc, JsonSerializationContext context) {
            JsonObject object = new JsonObject();
            object.addProperty(SharedSerializationConstants.KEY_PROVIDER_TYPE, SharedSerializationConstants.TYPE_RANDOM_BLOCK);

            JsonArray blocks = new JsonArray();
            src.randomBlockState.forEach((state, weight) -> {
                JsonObject block = JSONUtils.serializeBlockState(new JsonObject(), state);
                block.addProperty("weight", weight);
                blocks.add(block);
            });

            object.add(KEY_BLOCKS, blocks);
            return object;
        }
    }
}
