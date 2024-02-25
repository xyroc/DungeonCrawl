package xiroc.dungeoncrawl.dungeon.blueprint.template;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import xiroc.dungeoncrawl.dungeon.block.DungeonBlocks;
import xiroc.dungeoncrawl.dungeon.block.provider.BlockStateProvider;
import xiroc.dungeoncrawl.dungeon.theme.PrimaryTheme;
import xiroc.dungeoncrawl.dungeon.theme.SecondaryTheme;

import java.util.Random;
import java.util.function.Function;

public enum TemplateBlockType {

    AIR((block, rotation, world, pos, theme, secondaryTheme, rand, variation, stage) -> DungeonBlocks.CAVE_AIR, false),

    // Types with Primary Theme Factories
    MASONRY(tFactory(PrimaryTheme::masonry), false),
    SOLID_MASONRY(tFactory(PrimaryTheme::masonry), true),
    STAIRS(tFactory(PrimaryTheme::stairs), false),
    SOLID_STAIRS(tFactory(PrimaryTheme::stairs), true),
    SLAB(tFactory(PrimaryTheme::slab), false),
    SOLID_SLAB(tFactory(PrimaryTheme::slab), true),
    GENERIC_OR_FENCING(tFactory(PrimaryTheme::masonry), false),
    SOLID_PILLAR(tFactory(PrimaryTheme::pillar), true),
    SOLID_FLOOR(tFactory(PrimaryTheme::floor), false),
    FENCING(tFactory(PrimaryTheme::fencing), false),
    FLOOR(tFactory(PrimaryTheme::floor), false),
    FLUID(tFactory(PrimaryTheme::fluid), false),
    LOOSE_GROUND(tFactory(PrimaryTheme::floor), false),
    WALL(tFactory(PrimaryTheme::wall), false),

    // Types with Secondary-Theme Factories
    PILLAR(sFactory(SecondaryTheme::pillar), false),
    MATERIAL_STAIRS(sFactory(SecondaryTheme::stairs), false),
    TRAPDOOR(sFactory(SecondaryTheme::trapDoor), false),
    DOOR(sFactory(SecondaryTheme::door), false),
    FENCE(sFactory(SecondaryTheme::fence), false),
    FENCE_GATE(sFactory(SecondaryTheme::fenceGate), false),
    MATERIAL_SLAB(sFactory(SecondaryTheme::slab), false),
    MATERIAL_BUTTON(sFactory(SecondaryTheme::button), false),
    MATERIAL_PRESSURE_PLATE(sFactory(SecondaryTheme::pressurePlate), false),
    MATERIAL(sFactory(SecondaryTheme::material), false),

    // Other

    CHEST((block, rotation, world, pos, theme, secondaryTheme, rand, variation,
           stage) -> block.properties().apply(Blocks.CHEST.defaultBlockState()).rotate(world, pos, rotation), false),
    SKULL((block, rotation, world, pos, theme, secondaryTheme, rand, variation,
           stage) -> {
        BlockState state = block.properties().apply(Blocks.SKELETON_SKULL.defaultBlockState()).rotate(world, pos, rotation);
        if (state.hasProperty(BlockStateProperties.ROTATION_16)) {
            int r = state.getValue(BlockStateProperties.ROTATION_16);
            int add = rand.nextInt(3);
            if (rand.nextBoolean()) {
                r -= add;
                if (r < 0)
                    r += 16;
            } else {
                r = (r + add) % 16;
            }
            state = state.setValue(BlockStateProperties.ROTATION_16, r);
            return state;
        }
        return state;
    }, false),
    BLOCK((block, rotation, world, pos, theme, secondaryTheme, rand, variation, stage) -> block.properties().apply(block.block().defaultBlockState()).rotate(world, pos, rotation),
            false);

    public final BlockFactory blockFactory;
    public final boolean isSolid;

    private final TemplateBlock.PlacementProperties defaultProperties;

    TemplateBlockType(BlockFactory blockFactory, boolean isSolid) {
        this.blockFactory = blockFactory;
        this.isSolid = isSolid;
        this.defaultProperties = new TemplateBlock.PlacementProperties(this, isSolid);
    }

    public TemplateBlock.PlacementProperties defaultProperties() {
        return defaultProperties;
    }

    /**
     * A functional interface used to generate a BlockState from a template block.
     */
    @FunctionalInterface
    public interface BlockFactory {

        BlockState get(TemplateBlock block, Rotation rotation, LevelAccessor world, BlockPos pos, PrimaryTheme primaryTheme,
                       SecondaryTheme secondaryTheme, Random rand, byte[] variation, int stage);

    }

    /**
     * Creates a block factory that pulls from a theme
     *
     * @param blockSelector a function that selects the desired field from the theme
     * @return the block factory
     */
    private static BlockFactory tFactory(Function<PrimaryTheme, BlockStateProvider> blockSelector) {
        return (block, rotation, world, pos, theme, secondaryTheme, rand, variation, stage) -> block.properties()
                .apply(blockSelector.apply(theme).get(world, pos, rand, rotation))
                .rotate(world, pos, rotation);
    }

    /**
     * Creates a block factory that pulls from a secondary theme.
     *
     * @param blockSelector a function that selects the desired field from the secondary theme
     * @return the block factory
     */

    private static BlockFactory sFactory(Function<SecondaryTheme, BlockStateProvider> blockSelector) {
        return (block, rotation, world, pos, theme, secondaryTheme, rand, variation, stage) -> block.properties()
                .apply(blockSelector.apply(secondaryTheme).get(world, pos, rand, rotation))
                .rotate(world, pos, rotation);
    }

}