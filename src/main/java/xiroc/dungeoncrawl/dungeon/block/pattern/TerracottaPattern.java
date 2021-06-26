package xiroc.dungeoncrawl.dungeon.block.pattern;

import net.minecraft.block.BlockState;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.util.Direction;
import net.minecraft.util.Rotation;
import net.minecraft.util.math.BlockPos;
import xiroc.dungeoncrawl.dungeon.block.DungeonBlocks;
import xiroc.dungeoncrawl.util.IBlockStateProvider;
import xiroc.dungeoncrawl.util.Orientation;

public class TerracottaPattern implements IBlockStateProvider {

    private final IBlockStateProvider block;

    public TerracottaPattern(IBlockStateProvider block) {
        this.block = block;
    }

    @Override
    public BlockState get(BlockPos pos, Rotation rotation) {
        BlockState state = block.get(pos, rotation);
        if ((pos.getX() & 1) == 0) {
            if ((pos.getZ() & 1) == 0) {
                return DungeonBlocks.applyProperty(state, BlockStateProperties.HORIZONTAL_FACING, Direction.SOUTH).rotate(Orientation.getOppositeRotation(rotation));
            } else {
                return DungeonBlocks.applyProperty(state, BlockStateProperties.HORIZONTAL_FACING, Direction.EAST).rotate(Orientation.getOppositeRotation(rotation));
            }
        } else {
            if ((pos.getZ() & 1) == 0) {
                return DungeonBlocks.applyProperty(state, BlockStateProperties.HORIZONTAL_FACING, Direction.WEST).rotate(Orientation.getOppositeRotation(rotation));
            } else {
                return DungeonBlocks.applyProperty(state, BlockStateProperties.HORIZONTAL_FACING, Direction.NORTH).rotate(Orientation.getOppositeRotation(rotation));
            }
        }
    }

}
