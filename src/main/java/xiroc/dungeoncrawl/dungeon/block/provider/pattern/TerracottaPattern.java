package xiroc.dungeoncrawl.dungeon.block.provider.pattern;

import com.google.gson.JsonObject;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import xiroc.dungeoncrawl.dungeon.block.DungeonBlocks;
import xiroc.dungeoncrawl.dungeon.block.provider.BlockStateProvider;
import xiroc.dungeoncrawl.util.Orientation;

public record TerracottaPattern(BlockStateProvider block) implements BlockStateProvider {

    public static final String TYPE = "pattern";
    public static final String PATTERN_TYPE = "terracotta";

    @Override
    public BlockState get(LevelAccessor world, BlockPos pos, RandomSource random, Rotation rotation) {
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
