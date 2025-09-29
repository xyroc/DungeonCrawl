package xiroc.dungeoncrawl.dungeon.block.provider;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import xiroc.dungeoncrawl.util.storage.GlobalCodecs;

public class SingleBlock implements BlockStateProvider {
    public static final SingleBlock AIR = new SingleBlock(Blocks.CAVE_AIR.defaultBlockState());

    public static final Codec<SingleBlock> COMPACT_CODEC = GlobalCodecs.BLOCK_STATE.xmap(SingleBlock::new, instance -> instance.state);

    public static final MapCodec<SingleBlock> VERBOSE_CODEC = RecordCodecBuilder.mapCodec(builder -> builder.group(
            GlobalCodecs.BLOCK_STATE.fieldOf("block").forGetter(instance -> instance.state)
    ).apply(builder, SingleBlock::new));

    private final BlockState state;

    public SingleBlock(Block block) {
        this(block.defaultBlockState());
    }

    public SingleBlock(BlockState state) {
        this.state = state;
    }

    @Override
    public BlockState get(BlockPos pos, RandomSource random) {
        return state;
    }

    @Override
    public MapCodec<? extends BlockStateProvider> type() {
        return VERBOSE_CODEC;
    }
}