package xiroc.dungeoncrawl.dungeon.blueprint.builtin.segment;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.state.properties.Half;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import xiroc.dungeoncrawl.dungeon.block.provider.SingleBlock;
import xiroc.dungeoncrawl.dungeon.blueprint.Blueprint;
import xiroc.dungeoncrawl.dungeon.blueprint.BlueprintSettings;
import xiroc.dungeoncrawl.dungeon.blueprint.anchor.Anchor;
import xiroc.dungeoncrawl.dungeon.blueprint.feature.configuration.FeatureConfiguration;
import xiroc.dungeoncrawl.dungeon.theme.PrimaryTheme;
import xiroc.dungeoncrawl.dungeon.theme.SecondaryTheme;
import xiroc.dungeoncrawl.worldgen.WorldEditor;

import java.util.Random;

public record CorridorArchSegment(ResourceLocation key, ImmutableMap<ResourceLocation, ImmutableList<Anchor>> anchors,
                                  ImmutableList<FeatureConfiguration> features, BlueprintSettings settings) implements Blueprint {
    @Override
    public void build(LevelAccessor world, BlockPos position, Rotation rotation, BoundingBox worldGenBounds, Random random, PrimaryTheme primaryTheme, SecondaryTheme secondaryTheme, int stage) {
        WorldEditor editor = new WorldEditor(world, coordinateSpace(position), rotation);
        BlockPos origin = BlockPos.ZERO;
        // floor
        editor.fill(primaryTheme.floor(), origin, origin.offset(2, 0, 4), worldGenBounds, random, true, true);
        // ceiling
        editor.fill(primaryTheme.masonry(), origin.above(4), origin.offset(2, 4, 4), worldGenBounds, random, true, true);
        // air
        editor.fill(SingleBlock.AIR, origin.offset(0, 1, 1), origin.offset(2, 3, 4), worldGenBounds, random, true, true);
        // pillars
        editor.fill(secondaryTheme.pillar(), origin.above(), origin.above(3), worldGenBounds, random, true, true);
        editor.fill(secondaryTheme.pillar(), origin.offset(2, 1, 0), origin.offset(2, 3, 0), worldGenBounds, random, true, true);
        editor.fill(secondaryTheme.pillar(), origin.offset(0, 1, 4), origin.offset(0, 3, 4), worldGenBounds, random, true, true);
        editor.fill(secondaryTheme.pillar(), origin.offset(2, 1, 4), origin.offset(2, 3, 4), worldGenBounds, random, true, true);
        // air between the pillars
        editor.fill(SingleBlock.AIR, origin.offset(1, 1, 0), origin.offset(1, 2, 0), worldGenBounds, random, true, true);
        editor.fill(SingleBlock.AIR, origin.offset(1, 1, 4), origin.offset(1, 2, 4), worldGenBounds, random, true, true);
        // air below the ceiling
        editor.placeBlock(Blocks.CAVE_AIR.defaultBlockState(), origin.offset(0, 3, 2), worldGenBounds, true, true, true);
        editor.fill(SingleBlock.AIR, origin.offset(1, 3, 1), origin.offset(1, 3, 3), worldGenBounds, random, true, true);
        // arches
        editor.placeStairs(secondaryTheme.stairs(), origin.offset(0, 3, 1), Half.TOP, rotation.rotate(Direction.NORTH), worldGenBounds, random, true, true, false);
        editor.placeStairs(secondaryTheme.stairs(), origin.offset(2, 3, 1), Half.TOP, rotation.rotate(Direction.NORTH), worldGenBounds, random, true, true, false);
        editor.placeStairs(secondaryTheme.stairs(), origin.offset(0, 3, 3), Half.TOP, rotation.rotate(Direction.SOUTH), worldGenBounds, random, true, true, false);
        editor.placeStairs(secondaryTheme.stairs(), origin.offset(2, 3, 3), Half.TOP, rotation.rotate(Direction.SOUTH), worldGenBounds, random, true, true, false);
        editor.placeStairs(secondaryTheme.stairs(), origin.offset(1, 3, 0), Half.TOP, rotation.rotate(Direction.NORTH), worldGenBounds, random, true, true, false);
        editor.placeStairs(secondaryTheme.stairs(), origin.offset(1, 3, 4), Half.TOP, rotation.rotate(Direction.SOUTH), worldGenBounds, random, true, true, false);
    }

    @Override
    public int xSpan() {
        return 3;
    }

    @Override
    public int ySpan() {
        return 5;
    }

    @Override
    public int zSpan() {
        return 5;
    }
}
