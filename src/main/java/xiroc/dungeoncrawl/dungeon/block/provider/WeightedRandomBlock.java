package xiroc.dungeoncrawl.dungeon.block.provider;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.state.BlockState;
import xiroc.dungeoncrawl.util.random.IRandom;

import java.lang.reflect.Type;
import java.util.Random;

public class WeightedRandomBlock implements BlockStateProvider {
    private final IRandom<BlockState> randomBlockState;

    public WeightedRandomBlock(IRandom<BlockState> randomBlockState) {
        this.randomBlockState = randomBlockState;
    }

    @Override
    public BlockState get(BlockPos pos, Random random) {
        return randomBlockState.roll(random);
    }

    @Override
    public BlockState get(LevelAccessor world, BlockPos pos, Random random, Rotation rotation) {
        return randomBlockState.roll(random);
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private final IRandom.Builder<BlockState> builder = new IRandom.Builder<>();

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
            IRandom<BlockState> states = IRandom.BLOCK_STATE.deserialize(json.getAsJsonObject().get(KEY_BLOCKS));
            return new WeightedRandomBlock(states);
        }

        @Override
        public JsonElement serialize(WeightedRandomBlock src, Type typeOfSrc, JsonSerializationContext context) {
            JsonObject object = new JsonObject();
            object.addProperty(SharedSerializationConstants.KEY_PROVIDER_TYPE, SharedSerializationConstants.TYPE_RANDOM_BLOCK);
            object.add(KEY_BLOCKS, IRandom.BLOCK_STATE.serialize(src.randomBlockState));
            return object;
        }
    }
}
