package xiroc.dungeoncrawl.dungeon;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.Half;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import xiroc.dungeoncrawl.dungeon.block.DungeonBlocks;
import xiroc.dungeoncrawl.theme.Theme;

public final class PillarGenerator {

    /**
     * Builds a pillar with decorative stairs at the top.
     */
    public static void generateFancyPillar(LevelAccessor world, BlockPos center, BoundingBox worldGenBounds, Theme primaryTheme) {
        if (worldGenBounds.isInside(center)) {
            generateSimplePillar(world, center, primaryTheme);
        }

        BlockPos north = center.north();
        if (world.hasChunk(north.getX() >> 4, north.getZ() >> 4)) {
            placeTopStair(world, north, Direction.SOUTH, primaryTheme);
        }

        BlockPos east = center.east();
        if (world.hasChunk(east.getX() >> 4, east.getZ() >> 4)) {
            placeTopStair(world, east, Direction.WEST, primaryTheme);
        }

        BlockPos south = center.south();
        if (world.hasChunk(south.getX() >> 4, south.getZ() >> 4)) {
            placeTopStair(world, south, Direction.NORTH, primaryTheme);
        }

        BlockPos west = center.west();
        if (world.hasChunk(west.getX() >> 4, west.getZ() >> 4)) {
            placeTopStair(world, west, Direction.EAST, primaryTheme);
        }
    }

    private static void placeTopStair(LevelAccessor world, BlockPos pos, Direction toCenter, Theme primaryTheme) {
        if (world.isEmptyBlock(pos) && world.getBlockState(pos.above()).canOcclude()) {
            BlockState stair = DungeonBlocks.applyProperty(primaryTheme.solidStairs.get(world, pos), BlockStateProperties.HORIZONTAL_FACING, toCenter);
            stair = DungeonBlocks.applyProperty(stair, BlockStateProperties.HALF, Half.TOP);
            world.setBlock(pos, stair, 2);
        }
    }

    public static void generateSimplePillar(LevelAccessor world, BlockPos pos, Theme primaryTheme) {
        for (; pos.getY() > world.getMinBuildHeight(); pos = pos.below()) {
            if (world.getBlockState(pos).canOcclude()) return;
            world.setBlock(pos, primaryTheme.solid.get(world, pos), 2);
        }
    }

}
