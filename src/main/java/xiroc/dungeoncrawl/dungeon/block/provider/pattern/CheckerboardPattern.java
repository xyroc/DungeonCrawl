package xiroc.dungeoncrawl.dungeon.block.provider.pattern;

import net.minecraft.block.BlockState;
import net.minecraft.util.Rotation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IWorld;
import xiroc.dungeoncrawl.dungeon.block.provider.IBlockStateProvider;

public class CheckerboardPattern implements IBlockStateProvider {

    private final IBlockStateProvider block1, block2;

    public CheckerboardPattern(IBlockStateProvider block1, IBlockStateProvider block2) {
        this.block1 = block1;
        this.block2 = block2;
    }

    @Override
    public BlockState get(IWorld world, BlockPos pos, Rotation rotation) {
        if(((pos.getX() & 1) ^ (pos.getZ() & 1)) == 1) { // X is odd XOR Z is odd
            return block1.get(world, pos, rotation);
        } else {
            return block2.get(world, pos, rotation);
        }
    }
}
