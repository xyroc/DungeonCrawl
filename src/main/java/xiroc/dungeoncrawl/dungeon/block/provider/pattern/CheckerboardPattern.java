package xiroc.dungeoncrawl.dungeon.block.provider.pattern;

import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import net.minecraft.block.BlockState;
import net.minecraft.util.Rotation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IWorld;
import xiroc.dungeoncrawl.dungeon.block.provider.BlockStateProvider;

import java.util.Random;

public class CheckerboardPattern implements BlockStateProvider {

    protected static final String TYPE = "pattern";
    protected static final String PATTERN_TYPE = "checkerboard_pattern";

    private final BlockStateProvider block1;
    private final BlockStateProvider block2;

    public CheckerboardPattern(BlockStateProvider block1, BlockStateProvider block2) {
        this.block1 = block1;
        this.block2 = block2;
    }

    @Override
    public BlockState get(IWorld world, BlockPos pos, Random random, Rotation rotation) {
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
