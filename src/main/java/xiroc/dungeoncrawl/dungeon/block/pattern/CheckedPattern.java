package xiroc.dungeoncrawl.dungeon.block.pattern;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.state.BlockState;
import xiroc.dungeoncrawl.util.IBlockStateProvider;

public record CheckedPattern(IBlockStateProvider block1,
                             IBlockStateProvider block2) implements IBlockStateProvider {

    @Override
    public BlockState get(LevelAccessor world, BlockPos pos, Rotation rotation) {
        if (((pos.getX() & 1) ^ (pos.getZ() & 1)) == 1) { // X is odd XOR Z is odd
            return block1.get(world, pos, rotation);
        } else {
            return block2.get(world, pos, rotation);
        }
    }
}
