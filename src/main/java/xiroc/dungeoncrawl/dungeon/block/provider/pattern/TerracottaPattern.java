package xiroc.dungeoncrawl.dungeon.block.provider.pattern;

import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import net.minecraft.block.BlockState;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.util.Direction;
import net.minecraft.util.Rotation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IWorld;
import xiroc.dungeoncrawl.dungeon.block.DungeonBlocks;
import xiroc.dungeoncrawl.dungeon.block.provider.BlockStateProvider;
import xiroc.dungeoncrawl.util.Orientation;

import java.util.Random;

public class TerracottaPattern implements BlockStateProvider {

    protected static final String TYPE = "pattern";
    protected static final String PATTERN_TYPE = "terracotta";

    private final BlockStateProvider block;

    public TerracottaPattern(BlockStateProvider block) {
        this.block = block;
    }

    @Override
    public BlockState get(IWorld world, BlockPos pos, Random random, Rotation rotation) {
        BlockState state = block.get(world, pos, random, rotation);
        if ((pos.getX() & 1) == 0) {
            if ((pos.getZ() & 1) == 0) {
                return DungeonBlocks.applyProperty(state, BlockStateProperties.HORIZONTAL_FACING, Direction.SOUTH).rotate(world, pos, Orientation.getOppositeRotation(rotation));
            } else {
                return DungeonBlocks.applyProperty(state, BlockStateProperties.HORIZONTAL_FACING, Direction.EAST).rotate(world, pos, Orientation.getOppositeRotation(rotation));
            }
        } else {
            if ((pos.getZ() & 1) == 0) {
                return DungeonBlocks.applyProperty(state, BlockStateProperties.HORIZONTAL_FACING, Direction.WEST).rotate(world, pos, Orientation.getOppositeRotation(rotation));
            } else {
                return DungeonBlocks.applyProperty(state, BlockStateProperties.HORIZONTAL_FACING, Direction.NORTH).rotate(world, pos, Orientation.getOppositeRotation(rotation));
            }
        }
    }

    @Override
    public JsonObject serialize() {
        JsonObject object = new JsonObject();
        object.addProperty("type", TYPE);
        object.addProperty("pattern_type", PATTERN_TYPE);
        object.add("block", block.serialize());
        return object;
    }

}
