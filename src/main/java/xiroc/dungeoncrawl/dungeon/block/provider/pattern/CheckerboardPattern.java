package xiroc.dungeoncrawl.dungeon.block.provider.pattern;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.state.BlockState;
import xiroc.dungeoncrawl.dungeon.block.provider.BlockStateProvider;

public record CheckerboardPattern(BlockStateProvider block1, BlockStateProvider block2) implements BlockStateProvider {
    public static final MapCodec<CheckerboardPattern> CODEC = RecordCodecBuilder.mapCodec(builder -> builder.group(
            BlockStateProvider.CODEC.fieldOf("block_1").forGetter(CheckerboardPattern::block1),
            BlockStateProvider.CODEC.fieldOf("block_2").forGetter(CheckerboardPattern::block2)
    ).apply(builder, CheckerboardPattern::new));

    @Override
    public BlockState get(BlockPos pos, RandomSource random) {
        if (((pos.getX() & 1) ^ (pos.getZ() & 1)) == 1) { // X is odd XOR Z is odd
            return block1.get(pos, random);
        } else {
            return block2.get(pos, random);
        }
    }

    @Override
    public MapCodec<? extends BlockStateProvider> type() {
        return CODEC;
    }
}