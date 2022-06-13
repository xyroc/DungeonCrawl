package xiroc.dungeoncrawl.dungeon;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.Half;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import net.minecraft.world.level.material.WaterFluid;
import xiroc.dungeoncrawl.dungeon.block.DungeonBlocks;
import xiroc.dungeoncrawl.theme.Theme;

public final class PillarGenerator {

    /**
     * Builds a pillar with decorative stairs at the top.
     */
    public static void generateFancyPillar(LevelAccessor world, BlockPos center, RandomSource random, BoundingBox worldGenBounds, Theme primaryTheme) {
        if (worldGenBounds.isInside(center)) {
            generateSimplePillar(world, center, primaryTheme, random);
        }

        BlockPos north = center.north();
        if (world.hasChunk(north.getX() >> 4, north.getZ() >> 4)) {
            placeTopStair(world, north, Direction.SOUTH, primaryTheme, random);
        }

        BlockPos east = center.east();
        if (world.hasChunk(east.getX() >> 4, east.getZ() >> 4)) {
            placeTopStair(world, east, Direction.WEST, primaryTheme, random);
        }

        BlockPos south = center.south();
        if (world.hasChunk(south.getX() >> 4, south.getZ() >> 4)) {
            placeTopStair(world, south, Direction.NORTH, primaryTheme, random);
        }

        BlockPos west = center.west();
        if (world.hasChunk(west.getX() >> 4, west.getZ() >> 4)) {
            placeTopStair(world, west, Direction.EAST, primaryTheme, random);
        }
    }

    private static void placeTopStair(LevelAccessor world, BlockPos pos, Direction toCenter, Theme primaryTheme, RandomSource random) {
        if (world.isEmptyBlock(pos) && world.getBlockState(pos.above()).canOcclude()) {
            BlockState stair = DungeonBlocks.applyProperty(primaryTheme.solidStairs.get(world, pos, random), BlockStateProperties.HORIZONTAL_FACING, toCenter);
            stair = DungeonBlocks.applyProperty(stair, BlockStateProperties.HALF, Half.TOP);
            if (world.getFluidState(pos).getType() instanceof WaterFluid) {
                stair = DungeonBlocks.applyProperty(stair, BlockStateProperties.WATERLOGGED, true);
            }
            world.setBlock(pos, stair, 2);
        }
    }

    public static void generateSimplePillar(LevelAccessor world, BlockPos pos, Theme primaryTheme, RandomSource random) {
        for (; pos.getY() > 0; pos = pos.below()) {
            if (world.getBlockState(pos).canOcclude()) return;
            world.setBlock(pos, primaryTheme.solid.get(world, pos, random), 2);
        }
    }

}
