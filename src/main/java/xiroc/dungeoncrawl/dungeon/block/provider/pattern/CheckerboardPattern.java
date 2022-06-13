package xiroc.dungeoncrawl.dungeon.block.provider.pattern;

import com.google.gson.JsonObject;
import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.state.BlockState;
import xiroc.dungeoncrawl.dungeon.block.provider.BlockStateProvider;

public record CheckerboardPattern(BlockStateProvider block1,
                                  BlockStateProvider block2) implements BlockStateProvider {

    public static final String TYPE = "pattern";
    public static final String PATTERN_TYPE = "checkerboard_pattern";

    @Override
    public BlockState get(LevelAccessor world, BlockPos pos, RandomSource random, Rotation rotation) {
        if (((pos.getX() & 1) ^ (pos.getZ() & 1)) == 1) { // X is odd XOR Z is odd
            return block1.get(world, pos, random, rotation);
        } else {
            return block2.get(world, pos, random, rotation);
        }
    }

    @Override
    public JsonObject serialize() {
        JsonObject object = new JsonObject();
        object.addProperty("type", TYPE);
        object.addProperty("pattern_type", PATTERN_TYPE);

        object.add("block_1", block1.serialize());
        object.add("block_2", block2.serialize());
        return object;
    }

}
