package xiroc.dungeoncrawl.dungeon.block.pattern;

import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;
import xiroc.dungeoncrawl.util.IBlockStateProvider;

public class CheckedPattern implements IBlockStateProvider {

    private final IBlockStateProvider block1, block2;

    public CheckedPattern(IBlockStateProvider block1, IBlockStateProvider block2) {
        this.block1 = block1;
        this.block2 = block2;
    }

    @Override
    public BlockState get(BlockPos pos) {
        if(((pos.getX() & 1) ^ (pos.getZ() & 1)) == 1) { // X is odd XOR Z is odd
            return block1.get(pos);
        } else {
            return block2.get(pos);
        }
    }
}
