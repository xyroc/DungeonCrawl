package xiroc.dungeoncrawl.dungeon.blueprint.template.block.type;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.DoubleBlockHalf;
import xiroc.dungeoncrawl.dungeon.block.provider.BlockStateProvider;
import xiroc.dungeoncrawl.dungeon.blueprint.template.block.BlockChooser;
import xiroc.dungeoncrawl.dungeon.theme.PrimaryTheme;
import xiroc.dungeoncrawl.dungeon.theme.SecondaryTheme;

public record DoubleBlockTemplateBlockType(BlockChooser blockChooser,
                                           boolean requiresSupportUnderneath,
                                           Codec<DoubleBlockTemplateBlockType> codec) implements TemplateBlockType {

    @Override
    public BlockStateProvider chooseProvider(PrimaryTheme primaryTheme, SecondaryTheme secondaryTheme) {
        return blockChooser.chooseProvider(primaryTheme, secondaryTheme);
    }

    @Override
    public void handlePlacement(LevelAccessor level, BlockPos position, BlockState state, boolean isSolid) {
        if (!state.hasProperty(BlockStateProperties.DOUBLE_BLOCK_HALF)) {
            // May happen if a non-double block is specified for a theme field that expects double blocks, like doors.
            // Handling the block placement normally.
            TemplateBlockType.super.handlePlacement(level, position, state, isSolid);
            return;
        }
        DoubleBlockHalf half = state.getValue(BlockStateProperties.DOUBLE_BLOCK_HALF);
        if (half == DoubleBlockHalf.UPPER) {
            // We're placing both blocks when handling the lower block.
            return;
        }
        if (!shouldPlace(level, position)) {
            return;
        }
        // When handing the lower part, use the block state to place both the lower and the upper part.
        // This prevents theming related issues where the two parts wouldn't fit together.
        TemplateBlockType.super.handlePlacement(level, position, state, true);
        TemplateBlockType.super.handlePlacement(level, position.above(), state.setValue(BlockStateProperties.DOUBLE_BLOCK_HALF, DoubleBlockHalf.UPPER), true);
    }

    private boolean shouldPlace(LevelAccessor level, BlockPos position) {
        if (requiresSupportUnderneath) {
            BlockPos below = position.below();
            return level.getBlockState(below).isFaceSturdy(level, below, Direction.UP);
        }
        return !level.getBlockState(position).isAir() && !level.getBlockState(position.above()).isAir();
    }

    public static class Factory {
        private final Codec<DoubleBlockTemplateBlockType> codec = RecordCodecBuilder.create(instance -> instance
                .group(Codec.BOOL.fieldOf("requires_support").forGetter(DoubleBlockTemplateBlockType::requiresSupportUnderneath))
                .apply(instance, this::create));

        private final BlockChooser blockChooser;

        public Factory(BlockChooser blockChooser) {
            this.blockChooser = blockChooser;
        }

        public Codec<DoubleBlockTemplateBlockType> getCodec() {
            return codec;
        }

        public DoubleBlockTemplateBlockType create(boolean requiresSupportUnderneath) {
            return new DoubleBlockTemplateBlockType(blockChooser, requiresSupportUnderneath, codec);
        }
    }
}
