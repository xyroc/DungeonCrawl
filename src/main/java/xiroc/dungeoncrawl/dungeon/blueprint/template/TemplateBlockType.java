package xiroc.dungeoncrawl.dungeon.blueprint.template;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.state.BlockState;
import xiroc.dungeoncrawl.dungeon.block.provider.BlockStateProvider;
import xiroc.dungeoncrawl.dungeon.theme.PrimaryTheme;
import xiroc.dungeoncrawl.dungeon.theme.SecondaryTheme;

import java.util.Random;
import java.util.function.Function;

public enum TemplateBlockType {
    // The base block type
    BLOCK((block, level, pos, primaryTheme, secondaryTheme, rand) -> block.properties().applyProperties(block.block().defaultBlockState())),

    // Types with Primary Theme Factories
    MASONRY         (tFactory(PrimaryTheme::masonry)),
    MASONRY_STAIRS  (tFactory(PrimaryTheme::stairs)),
    MASONRY_SLAB    (tFactory(PrimaryTheme::slab)),
    MASONRY_PILLAR  (tFactory(PrimaryTheme::pillar)),
    FENCING         (tFactory(PrimaryTheme::fencing)),
    FLOOR           (tFactory(PrimaryTheme::floor)),
    FLUID           (tFactory(PrimaryTheme::fluid)),
    LOOSE_GROUND    (tFactory(PrimaryTheme::floor)),
    WALL            (tFactory(PrimaryTheme::wall)),

    // Types with Secondary-Theme Factories
    MATERIAL            (sFactory(SecondaryTheme::material)),
    MATERIAL_STAIRS     (sFactory(SecondaryTheme::stairs)),
    MATERIAL_SLAB       (sFactory(SecondaryTheme::slab)),
    MATERIAL_PILLAR     (sFactory(SecondaryTheme::pillar)),
    BUTTON              (sFactory(SecondaryTheme::button)),
    PRESSURE_PLATE      (sFactory(SecondaryTheme::pressurePlate)),
    DOOR                (sFactory(SecondaryTheme::door)),
    TRAPDOOR            (sFactory(SecondaryTheme::trapDoor)),
    FENCE               (sFactory(SecondaryTheme::fence)),
    FENCE_GATE          (sFactory(SecondaryTheme::fenceGate));

    public final BlockFactory blockFactory;

    private final TemplateBlock.PlacementProperties solid;
    private final TemplateBlock.PlacementProperties nonSolid;

    TemplateBlockType(BlockFactory blockFactory) {
        this.blockFactory = blockFactory;
        this.solid = new TemplateBlock.PlacementProperties(this, true);
        this.nonSolid = new TemplateBlock.PlacementProperties(this, false);
    }

    public TemplateBlock.PlacementProperties placementProperties(boolean solid) {
        return solid ? this.solid : this.nonSolid;
    }

    /**
     * A functional interface used to generate a BlockState from a template block.
     */
    @FunctionalInterface
    public interface BlockFactory {
        BlockState get(TemplateBlock block, LevelAccessor level, BlockPos pos, PrimaryTheme primaryTheme, SecondaryTheme secondaryTheme, Random rand);
    }

    /**
     * Creates a block factory that pulls from a theme
     *
     * @param blockSelector a function that selects the desired field from the theme
     * @return the block factory
     */
    private static BlockFactory tFactory(Function<PrimaryTheme, BlockStateProvider> blockSelector) {
        return (block, level, pos, primaryTheme, secondaryTheme, rand) -> block.properties().applyProperties(blockSelector.apply(primaryTheme).get(pos, rand));
    }

    /**
     * Creates a block factory that pulls from a secondary theme.
     *
     * @param blockSelector a function that selects the desired field from the secondary theme
     * @return the block factory
     */

    private static BlockFactory sFactory(Function<SecondaryTheme, BlockStateProvider> blockSelector) {
        return (block, level, pos, primaryTheme, secondaryTheme, rand) -> block.properties().applyProperties(blockSelector.apply(secondaryTheme).get(pos, rand));
    }
}