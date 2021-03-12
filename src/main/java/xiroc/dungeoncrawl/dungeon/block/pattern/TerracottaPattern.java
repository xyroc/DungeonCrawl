package xiroc.dungeoncrawl.dungeon.block.pattern;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import xiroc.dungeoncrawl.dungeon.block.DungeonBlocks;
import xiroc.dungeoncrawl.util.IBlockStateProvider;

public class TerracottaPattern implements IBlockStateProvider {

    private final Block block;

    public TerracottaPattern(Block block) {
        this.block = block;
    }

    @Override
    public BlockState get(BlockPos pos) {
        BlockState state = block.getDefaultState();
        if ((pos.getX() & 1) == 0) {
            if ((pos.getZ() & 1) == 0) {
                return DungeonBlocks.applyProperty(state, BlockStateProperties.HORIZONTAL_FACING, Direction.NORTH);
            } else {
                return DungeonBlocks.applyProperty(state, BlockStateProperties.HORIZONTAL_FACING, Direction.EAST);
            }
        } else {
            if ((pos.getZ() & 1) == 0) {
                return DungeonBlocks.applyProperty(state, BlockStateProperties.HORIZONTAL_FACING, Direction.WEST);
            } else {
                return DungeonBlocks.applyProperty(state, BlockStateProperties.HORIZONTAL_FACING, Direction.SOUTH);
            }
        }
    }

}
