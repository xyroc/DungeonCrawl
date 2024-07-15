package xiroc.dungeoncrawl.dungeon.blueprint.builtin.segment;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.Half;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import xiroc.dungeoncrawl.dungeon.block.DungeonBlocks;
import xiroc.dungeoncrawl.dungeon.block.provider.SingleBlock;
import xiroc.dungeoncrawl.dungeon.blueprint.Blueprint;
import xiroc.dungeoncrawl.dungeon.blueprint.anchor.Anchor;
import xiroc.dungeoncrawl.dungeon.blueprint.feature.BlueprintFeature;
import xiroc.dungeoncrawl.dungeon.theme.PrimaryTheme;
import xiroc.dungeoncrawl.dungeon.theme.SecondaryTheme;
import xiroc.dungeoncrawl.worldgen.RotatingWorldEditor;

import java.util.Random;

public record CorridorSideSegment(ImmutableMap<ResourceLocation, ImmutableList<Anchor>> anchors, ImmutableList<BlueprintFeature> features) implements Blueprint {
    @Override
    public void build(LevelAccessor world, BlockPos position, Rotation rotation, BoundingBox worldGenBounds, Random random, PrimaryTheme primaryTheme, SecondaryTheme secondaryTheme, int stage) {
        RotatingWorldEditor editor = new RotatingWorldEditor(world, coordinateSpace(position), rotation);
        BlockPos origin = BlockPos.ZERO;
        editor.fill(primaryTheme.floor(), origin, origin.offset(2, 0, 1), worldGenBounds, random, true, true, false);
        editor.fill(secondaryTheme.material(), origin.above(), origin.offset(2, 3, 0), worldGenBounds, random, true, true, false);
        editor.fill(SingleBlock.AIR, origin.offset(0, 1, 1), origin.offset(2, 2, 1), worldGenBounds, random, true, true, false);
        editor.placeBlock(SingleBlock.AIR, origin.offset(1, 3, 1), worldGenBounds, random, true, true, false);
        editor.fill(primaryTheme.masonry(), origin.above(4), origin.offset(2, 4, 1), worldGenBounds, random, true, true, false);

        BlockPos stair1Pos = origin.offset(0, 3, 1);
        BlockState stair1 = secondaryTheme.stairs().get(stair1Pos, random);
        stair1 = DungeonBlocks.applyProperty(stair1, BlockStateProperties.HALF, Half.TOP);
        stair1 = DungeonBlocks.applyProperty(stair1, BlockStateProperties.HORIZONTAL_FACING, Direction.WEST);
        editor.placeBlock(stair1, stair1Pos, worldGenBounds, true, true, false);

        BlockPos stair2Pos = origin.offset(2, 3, 1);
        BlockState stair2 = secondaryTheme.stairs().get(stair2Pos, random);
        stair2 = DungeonBlocks.applyProperty(stair2, BlockStateProperties.HALF, Half.TOP);
        stair2 = DungeonBlocks.applyProperty(stair2, BlockStateProperties.HORIZONTAL_FACING, Direction.EAST);
        editor.placeBlock(stair2, stair2Pos, worldGenBounds, true, true, false);
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
        return 2;
    }
}
