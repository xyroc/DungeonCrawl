package xiroc.dungeoncrawl.data.pool.block;

import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import xiroc.dungeoncrawl.util.random.IRandom;

public interface BlockPools {
    static void generate(BootstrapContext<IRandom<BlockState>> context) {
        context.register(BlockPoolKeys.FARMLAND_CROPS, IRandom.<BlockState>builder()
                .add(Blocks.WHEAT.defaultBlockState())
                .add(Blocks.CARROTS.defaultBlockState())
                .add(Blocks.POTATOES.defaultBlockState())
                .add(Blocks.BEETROOTS.defaultBlockState())
                .build());
    }
}
