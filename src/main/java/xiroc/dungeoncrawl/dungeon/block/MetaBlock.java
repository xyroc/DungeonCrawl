package xiroc.dungeoncrawl.dungeon.block;

import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.Property;
import xiroc.dungeoncrawl.dungeon.block.provider.BlockStateProvider;

/**
 * A wrapper for a block state, allowing its properties to be applied safely to any block state.
 * Unsupported properties on the target block state are skipped.
 */
public record MetaBlock(BlockState base) {
    public BlockStateProvider attach(BlockStateProvider provider) {
        return new Attached(this, provider);
    }

    public BlockState applyProperties(BlockState state) {
        for (var property : base.getProperties()) {
            if (state.hasProperty(property)) {
                state = applyProperty(state, property, base.getValue(property));
            }
        }
        return state;
    }

    @SuppressWarnings("unchecked")
    private static <T extends Comparable<T>> BlockState applyProperty(BlockState state, Property<T> property, Comparable<?> value) {
        return state.setValue(property, (T) value);
    }

    private record Attached(MetaBlock metaBlock, BlockStateProvider provider) implements BlockStateProvider {
        @Override
        public BlockState get(BlockPos pos, RandomSource random) {
            return metaBlock.applyProperties(provider.get(pos, random));
        }
    }
}
