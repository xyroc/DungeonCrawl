package xiroc.dungeoncrawl.dungeon.block.provider.pattern;


import net.minecraft.core.BlockPos;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.state.BlockState;
import xiroc.dungeoncrawl.dungeon.block.provider.BlockStateProvider;

public record CheckerboardPattern(BlockStateProvider block1,
                                  BlockStateProvider block2) implements BlockStateProvider {

    @Override
    public BlockState get(LevelAccessor world, BlockPos pos, Rotation rotation) {
        if (((pos.getX() & 1) ^ (pos.getZ() & 1)) == 1) { // X is odd XOR Z is odd
            return block1.get(world, pos, rotation);
        } else {
            return block2.get(world, pos, rotation);
        }
    }
}
