package xiroc.dungeoncrawl.dungeon.block.provider;

import com.google.gson.JsonObject;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.util.Rotation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IWorld;
import xiroc.dungeoncrawl.util.JSONUtils;

public class SingleBlock implements BlockStateProvider {

    public static final SingleBlock AIR = new SingleBlock(Blocks.CAVE_AIR.defaultBlockState());

    protected static final String TYPE = "block";
    private final BlockState state;

    public SingleBlock(Block block) {
        this(block.defaultBlockState());
    }

    public SingleBlock(BlockState state) {
        this.state = state;
    }

    @Override
    public BlockState get(IWorld world, BlockPos pos, Rotation rotation) {
        return state;
    }

    @Override
    public JsonObject serialize() {
        JsonObject object = new JsonObject();
        object.addProperty("type", TYPE);
        return JSONUtils.serializeBlockState(object, state);
    }

}
