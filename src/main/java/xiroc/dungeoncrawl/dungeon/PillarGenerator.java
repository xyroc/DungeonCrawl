package xiroc.dungeoncrawl.dungeon;

import net.minecraft.block.BlockState;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.state.properties.Half;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MutableBoundingBox;
import net.minecraft.world.IWorld;
import xiroc.dungeoncrawl.DungeonCrawl;
import xiroc.dungeoncrawl.dungeon.block.DungeonBlocks;
import xiroc.dungeoncrawl.theme.Theme;

public final class PillarGenerator {

    /**
     * Builds a pillar with decorative stairs at the top.
     */
    public static void generateFancyPillar(IWorld world, BlockPos center, MutableBoundingBox worldGenBounds, Theme primaryTheme) {
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

    private static void placeTopStair(IWorld world, BlockPos pos, Direction toCenter, Theme primaryTheme) {
        if (world.isEmptyBlock(pos) && world.getBlockState(pos.above()).canOcclude()) {
            BlockState stair = DungeonBlocks.applyProperty(primaryTheme.solidStairs.get(world, pos), BlockStateProperties.HORIZONTAL_FACING, toCenter);
            stair = DungeonBlocks.applyProperty(stair, BlockStateProperties.HALF, Half.TOP);
            world.setBlock(pos, stair, 2);
        }
    }

    public static void generateSimplePillar(IWorld world, BlockPos pos, Theme primaryTheme) {
        for (; pos.getY() > 0; pos = pos.below()) {
            if (world.getBlockState(pos).canOcclude()) return;
            world.setBlock(pos, primaryTheme.solid.get(world, pos), 2);
        }
    }

}
