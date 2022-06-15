package xiroc.dungeoncrawl.dungeon.block.provider;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.state.BlockState;
import xiroc.dungeoncrawl.util.JSONUtils;
import xiroc.dungeoncrawl.util.WeightedRandom;

import java.util.Random;

public class WeightedRandomBlock implements BlockStateProvider {

    protected static final String TYPE = "random_block";

    private final WeightedRandom<BlockState> randomBlockState;

    public WeightedRandomBlock(WeightedRandom<BlockState> randomBlockState) {
        this.randomBlockState = randomBlockState;
    }

    @Override
    public BlockState get(LevelAccessor world, BlockPos pos, Random random, Rotation rotation) {
        return randomBlockState.roll(random);
    }

    @Override
    public JsonObject serialize() {
        JsonObject object = new JsonObject();
        object.addProperty("type", TYPE);

        JsonArray blocks = new JsonArray();
        randomBlockState.getEntries().forEach((entry) -> {
            JsonObject block = JSONUtils.serializeBlockState(new JsonObject(), entry.getA());
            block.addProperty("weight", entry.getB());
            blocks.add(block);
        });

        object.add("blocks", blocks);
        return object;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {

        private WeightedRandom.Builder<BlockState> builder = new WeightedRandom.Builder<>();

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

}
