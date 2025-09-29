package xiroc.dungeoncrawl.dungeon.block.provider;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.state.BlockState;
import org.checkerframework.checker.units.qual.C;
import xiroc.dungeoncrawl.util.random.IRandom;

public class RandomBlock implements BlockStateProvider {
    public static final Codec<RandomBlock> COMPACT_CODEC = IRandom.BaseCodecs.BLOCK_STATE.xmap(RandomBlock::new, instance -> instance.states);

    public static final MapCodec<RandomBlock> VERBOSE_CODEC = RecordCodecBuilder.mapCodec(builder -> builder.group(
            IRandom.BaseCodecs.BLOCK_STATE.fieldOf("blocks").forGetter(instance -> instance.states)
    ).apply(builder, RandomBlock::new));

    private final IRandom<BlockState> states;

    public RandomBlock(IRandom<BlockState> states) {
        this.states = states;
    }

    @Override
    public BlockState get(BlockPos pos, RandomSource random) {
        return states.roll(random);
    }

    @Override
    public MapCodec<? extends BlockStateProvider> type() {
        return VERBOSE_CODEC;
    }
}
