package xiroc.dungeoncrawl.dungeon.blueprint.builtin.room;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Vec3i;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.Half;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import xiroc.dungeoncrawl.dungeon.block.provider.SingleBlock;
import xiroc.dungeoncrawl.dungeon.blueprint.Blueprint;
import xiroc.dungeoncrawl.dungeon.blueprint.BlueprintSettings;
import xiroc.dungeoncrawl.dungeon.blueprint.anchor.Anchor;
import xiroc.dungeoncrawl.dungeon.blueprint.feature.configuration.FeatureConfiguration;
import xiroc.dungeoncrawl.dungeon.theme.PrimaryTheme;
import xiroc.dungeoncrawl.dungeon.theme.SecondaryTheme;
import xiroc.dungeoncrawl.worldgen.MirroringWorldEditor;
import xiroc.dungeoncrawl.worldgen.WorldEditor;

import java.util.Random;

public record CornerRoomBlueprint(ImmutableMap<ResourceLocation, ImmutableList<Anchor>> anchors,
                                  ImmutableList<FeatureConfiguration> features, BlueprintSettings settings) implements Blueprint {
    private static final Vec3i CEILING_CENTER = new Vec3i(3, 5, 3);

    @Override
    public void build(LevelAccessor world, BlockPos position, Rotation rotation, BoundingBox worldGenBounds, Random random, PrimaryTheme primaryTheme, SecondaryTheme secondaryTheme, int stage) {
        MirroringWorldEditor editor = new MirroringWorldEditor(world, coordinateSpace(position), rotation);
        BlockPos origin = BlockPos.ZERO;
        // Floor
        WorldEditor.fill(world, primaryTheme.floor(), position, position.offset(6, 0, 6), worldGenBounds, random, false, true);

        // Walls
        editor.fill(primaryTheme.masonry(), origin.above(), origin.offset(5, 4, 0), worldGenBounds, random, true, true, false);

        // Pillars
        editor.fill(primaryTheme.pillar(), origin.offset(1, 1, 1), origin.offset(1, 4, 1), worldGenBounds, random, true, true, false);
        editor.placeStairs(primaryTheme.stairs(), origin.offset(2, 4, 1), Half.TOP, Direction.WEST, worldGenBounds, random, true, true, false);
        editor.placeStairs(primaryTheme.stairs(), origin.offset(1, 4, 2), Half.TOP, Direction.NORTH, worldGenBounds, random, true, true, false);

        // Ceiling
        editor.placeStairs(primaryTheme.stairs(), origin.offset(4, 5, 3), Half.TOP, Direction.EAST, worldGenBounds, random, false, true, false);
        editor.placeBlock(primaryTheme.masonry(), CEILING_CENTER.offset(1, 0, 1), worldGenBounds, random, false, true, false);

        BlockPos ceilingCenter = position.offset(CEILING_CENTER);
        WorldEditor.fillRing(world, primaryTheme.masonry(), ceilingCenter, 3, 2, 1, worldGenBounds, random, false, true);
        WorldEditor.placeBlock(world, primaryTheme.masonry(), ceilingCenter.above(), worldGenBounds, random, false, true, false);

        // Air
        BlockState air = Blocks.CAVE_AIR.defaultBlockState();
        WorldEditor.placeBlock(world, air, ceilingCenter, worldGenBounds, true, true, false);

        editor.placeBlock(air, CEILING_CENTER.offset(2, -1, 0), worldGenBounds, false, true, false);
        editor.fill(SingleBlock.AIR, origin.offset(2, 1, 1), origin.offset(4, 3, 1), worldGenBounds, random, true, true, false);

        WorldEditor.fill(world, SingleBlock.AIR, position.offset(2, 1, 2), position.offset(4, 4, 4), worldGenBounds, random, true, true);
    }

    @Override
    public int xSpan() {
        return 7;
    }

    @Override
    public int ySpan() {
        return 9;
    }

    @Override
    public int zSpan() {
        return 7;
    }
}