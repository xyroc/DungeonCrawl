package xiroc.dungeoncrawl.dungeon.block.provider;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.util.Rotation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IWorld;
import xiroc.dungeoncrawl.dungeon.block.DungeonBlocks;
import xiroc.dungeoncrawl.util.JSONUtils;
import xiroc.dungeoncrawl.util.WeightedRandom;

public class WeightedRandomBlock implements BlockStateProvider {

    protected static final String TYPE = "random_block";

    private final WeightedRandom<BlockState> randomBlockState;

    public WeightedRandomBlock(WeightedRandom<BlockState> randomBlockState) {
        this.randomBlockState = randomBlockState;
    }

    @Override
    public BlockState get(IWorld world, BlockPos pos, Rotation rotation) {
        return randomBlockState.roll(DungeonBlocks.RANDOM);
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
