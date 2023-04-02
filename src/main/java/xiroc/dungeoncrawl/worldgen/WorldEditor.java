package xiroc.dungeoncrawl.worldgen;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Vec3i;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.Half;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import net.minecraft.world.level.material.FluidState;
import xiroc.dungeoncrawl.dungeon.block.provider.BlockStateProvider;
import xiroc.dungeoncrawl.dungeon.block.provider.SingleBlock;
import xiroc.dungeoncrawl.util.CoordinateSpace;
import xiroc.dungeoncrawl.util.Orientation;
import xiroc.dungeoncrawl.util.bounds.BoundingBoxUtils;

import java.util.Random;

public record WorldEditor(LevelAccessor world, CoordinateSpace coordinateSpace, Rotation rotation) {
    public void fill(BlockStateProvider stateProvider, Vec3i from, Vec3i to, BoundingBox boundingBox, Random random, boolean fillAir, boolean fillSolid) {
        WorldEditor.fill(world, stateProvider,
                coordinateSpace.rotateAndTranslateToOrigin(from, rotation),
                coordinateSpace.rotateAndTranslateToOrigin(to, rotation),
                boundingBox, random, fillAir, fillSolid);
    }

    public void fillWalls(BlockStateProvider stateProvider, Vec3i from, Vec3i to, BoundingBox boundingBox, Random random, boolean fillAir, boolean fillSolid) {
        WorldEditor.fillWalls(world, stateProvider,
                coordinateSpace.rotateAndTranslateToOrigin(from, rotation),
                coordinateSpace.rotateAndTranslateToOrigin(to, rotation),
                boundingBox, random, fillAir, fillSolid);
    }

    public void fillRing(BlockStateProvider blocks, BlockPos center, int radius, int thickness, int height, BoundingBox boundingBox, Random random, boolean fillAir,
                         boolean fillSolid) {
        WorldEditor.fillRing(world, blocks,
                coordinateSpace.rotateAndTranslateToOrigin(center, rotation),
                radius, thickness, height, boundingBox, random, fillAir, fillSolid);
    }

    public void placeEntrance(BlockStateProvider stairs, Vec3i pos, Direction parallelTo, BoundingBox boundingBox, Random random, boolean fillAir, boolean fillSolid) {
        WorldEditor.placeEntrance(world, stairs,
                coordinateSpace.rotateAndTranslateToOrigin(pos, rotation),
                rotation.rotate(parallelTo), boundingBox, random, fillAir, fillSolid);
    }

    public void placePillar(BlockStateProvider pillar, BlockStateProvider stairs, Vec3i pos, int height,
                            boolean north, boolean east, boolean south, boolean west,
                            BoundingBox boundingBox, Random random, boolean fillAir, boolean fillSolid) {
        WorldEditor.placePillar(world, pillar, stairs,
                coordinateSpace.rotateAndTranslateToOrigin(pos, rotation),
                height, north, east, south, west, boundingBox, random, fillAir, fillSolid);
    }

    public void placeSpiralStairStep(BlockStateProvider pillar, BlockStateProvider stairs, Vec3i center, BoundingBox boundingBox, Random random, boolean postProcess) {
        WorldEditor.placeSpiralStairStep(world, pillar, stairs,
                coordinateSpace.rotateAndTranslateToOrigin(center, rotation),
                boundingBox, random, postProcess);
    }

    public void placeBlock(BlockStateProvider block, Vec3i pos, BoundingBox boundingBox, Random random, boolean fillAir, boolean fillSolid, boolean postProcess) {
        WorldEditor.placeBlock(world, block,
                coordinateSpace.rotateAndTranslateToOrigin(pos, rotation),
                boundingBox, random, fillAir, fillSolid, postProcess);
    }

    public void placeBlock(BlockStateProvider block, int x, int y, int z, BoundingBox boundingBox, Random random, boolean fillAir, boolean fillSolid, boolean postProcess) {
        WorldEditor.placeBlock(world, block,
                coordinateSpace.rotateAndTranslateToOrigin(x, y, z, rotation),
                boundingBox, random, fillAir, fillSolid, postProcess);
    }

    public void placeBlock(BlockState block, Vec3i pos, BoundingBox boundingBox, boolean fillAir, boolean fillSolid, boolean postProcess) {
        WorldEditor.placeBlock(world, block,
                coordinateSpace.rotateAndTranslateToOrigin(pos, rotation),
                boundingBox, fillAir, fillSolid, postProcess);
    }

    public void placeBlock(BlockState block, int x, int y, int z, BoundingBox boundingBox, boolean fillAir, boolean fillSolid, boolean postProcess) {
        WorldEditor.placeBlock(world, block,
                coordinateSpace.rotateAndTranslateToOrigin(x, y, z, rotation),
                boundingBox, fillAir, fillSolid, postProcess);
    }

    public static void fill(LevelAccessor world, BlockStateProvider stateProvider, Vec3i from, Vec3i to, BoundingBox boundingBox, Random random, boolean fillAir, boolean fillSolid) {
        Vec3i startVec = BoundingBoxUtils.start(from, to, boundingBox);
        Vec3i endVec = BoundingBoxUtils.end(from, to, boundingBox);
        fillUnchecked(world, stateProvider, startVec, endVec, random, fillAir, fillSolid);
    }

    public static void fillUnchecked(LevelAccessor world, BlockStateProvider stateProvider, Vec3i from, Vec3i to, Random random, boolean fillAir, boolean fillSolid) {
        for (int x = from.getX(); x <= to.getX(); x++) {
            for (int y = from.getY(); y <= to.getY(); y++) {
                for (int z = from.getZ(); z <= to.getZ(); z++) {
                    BlockPos pos = new BlockPos(x, y, z);
                    placeBlockUnchecked(world, stateProvider.get(world, pos, random), pos, fillAir, fillSolid, false);
                }
            }
        }
    }

    public static void fillWalls(LevelAccessor world, BlockStateProvider stateProvider, BlockPos from, BlockPos to, BoundingBox boundingBox, Random random, boolean fillAir, boolean fillSolid) {
        Vec3i startVec = BoundingBoxUtils.start(from, to, boundingBox);
        Vec3i endVec = BoundingBoxUtils.end(from, to, boundingBox);

        for (int x = startVec.getX(); x <= endVec.getX(); x++) {
            for (int z = startVec.getZ(); z <= endVec.getZ(); z++) {
                BlockPos bottom = new BlockPos(x, from.getY(), z);
                BlockPos top = new BlockPos(x, to.getY(), z);
                placeBlockUnchecked(world, stateProvider.get(world, bottom, random), bottom, fillAir, fillSolid, false);
                placeBlockUnchecked(world, stateProvider.get(world, top, random), top, fillAir, fillSolid, false);
            }
        }

        if (startVec.getX() == Math.min(from.getX(), to.getX())) {
            for (int z = startVec.getZ(); z <= endVec.getZ(); z++) {
                for (int y = startVec.getY() + 1; y < endVec.getY(); y++) {
                    BlockPos pos = new BlockPos(startVec.getX(), y, z);
                    placeBlockUnchecked(world, stateProvider.get(world, pos, random), pos, fillAir, fillSolid, false);
                }
            }
        }

        if (endVec.getX() == Math.max(from.getX(), to.getX())) {
            for (int z = startVec.getZ(); z <= endVec.getZ(); z++) {
                for (int y = startVec.getY() + 1; y < endVec.getY(); y++) {
                    BlockPos pos = new BlockPos(endVec.getX(), y, z);
                    placeBlockUnchecked(world, stateProvider.get(world, pos, random), pos, fillAir, fillSolid, false);
                }
            }
        }

        if (startVec.getZ() == Math.min(from.getZ(), to.getZ())) {
            for (int x = startVec.getX(); x <= endVec.getX(); x++) {
                for (int y = startVec.getY() + 1; y < endVec.getY(); y++) {
                    BlockPos pos = new BlockPos(x, y, startVec.getZ());
                    placeBlockUnchecked(world, stateProvider.get(world, pos, random), pos, fillAir, fillSolid, false);
                }
            }
        }

        if (endVec.getZ() == Math.max(from.getZ(), to.getZ())) {
            for (int x = startVec.getX(); x <= endVec.getX(); x++) {
                for (int y = startVec.getY() + 1; y < endVec.getY(); y++) {
                    BlockPos pos = new BlockPos(x, y, endVec.getZ());
                    placeBlockUnchecked(world, stateProvider.get(world, pos, random), pos, fillAir, fillSolid, false);
                }
            }
        }
    }

    public static void fillRing(LevelAccessor world, BlockStateProvider blocks, BlockPos center, int radius, int thickness, int height, BoundingBox boundingBox, Random random,
                                boolean fillAir, boolean fillSolid) {
        if (thickness >= radius) {
            fill(world, blocks, center.offset(-radius, 0, -radius), center.offset(radius, height - 1, radius), boundingBox, random, fillAir, fillSolid);
            return;
        }
        fill(world, blocks, center.offset(-radius, 0, -radius), center.offset(radius - thickness, height - 1, thickness - radius - 1), boundingBox, random, fillAir, fillSolid);
        fill(world, blocks, center.offset(radius - thickness + 1, 0, -radius), center.offset(radius, height - 1, radius - thickness), boundingBox, random, fillAir, fillSolid);
        fill(world, blocks, center.offset(thickness - radius, 0, radius - thickness + 1), center.offset(radius, height - 1, radius), boundingBox, random, fillAir, fillSolid);
        fill(world, blocks, center.offset(-radius, 0, thickness - radius), center.offset(thickness - radius - 1, height - 1, radius), boundingBox, random, fillAir, fillSolid);
    }

    public static void placeEntrance(LevelAccessor world, BlockStateProvider stairs, BlockPos pos, Direction parallelTo, BoundingBox boundingBox, Random random, boolean fillAir, boolean fillSolid) {
        for (int coordinate = -1; coordinate <= 1; coordinate++) {
            BlockPos cursor = pos.relative(parallelTo, coordinate);
            placeBlock(world, Blocks.CAVE_AIR.defaultBlockState(), cursor, boundingBox, fillAir, fillSolid, false);
            placeBlock(world, Blocks.CAVE_AIR.defaultBlockState(), cursor.above(), boundingBox, fillAir, fillSolid, false);
        }
        BlockPos top = pos.above(2);
        placeBlock(world, Blocks.CAVE_AIR.defaultBlockState(), top, boundingBox, fillAir, fillSolid, false);
        placeStairs(world, stairs, top.relative(parallelTo), boundingBox, Half.TOP, parallelTo, random, fillAir, fillSolid, false);
        placeStairs(world, stairs, top.relative(parallelTo.getOpposite()), boundingBox, Half.TOP, parallelTo.getOpposite(), random, fillAir, fillSolid, false);
    }

    public static void placePillar(LevelAccessor world, BlockStateProvider pillar, BlockStateProvider stairs, BlockPos pos,
                                   int height, boolean north, boolean east, boolean south, boolean west,
                                   BoundingBox boundingBox, Random random, boolean fillAir, boolean fillSolid) {
        if (boundingBox.isInside(pos)) {
            BlockPos.MutableBlockPos cursor = pos.mutable();
            for (int i = 0; i < height; i++) {
                placeBlockUnchecked(world, pillar.get(world, cursor, random), cursor, fillAir, fillSolid, false);
                cursor.move(0, 1, 0);
            }
        }
        if (north) {
            BlockPos stairNorth = pos.offset(0, height - 1, -1);
            placeStairs(world, stairs, stairNorth, boundingBox, Half.TOP, Direction.SOUTH, random, fillAir, fillSolid, false);
        }
        if (east) {
            BlockPos stairEast = pos.offset(1, height - 1, 0);
            placeStairs(world, stairs, stairEast, boundingBox, Half.TOP, Direction.WEST, random, fillAir, fillSolid, false);
        }
        if (south) {
            BlockPos stairSouth = pos.offset(0, height - 1, 1);
            placeStairs(world, stairs, stairSouth, boundingBox, Half.TOP, Direction.NORTH, random, fillAir, fillSolid, false);
        }
        if (west) {
            BlockPos stairWest = pos.offset(-1, height - 1, 0);
            placeStairs(world, stairs, stairWest, boundingBox, Half.TOP, Direction.EAST, random, fillAir, fillSolid, false);
        }
    }

    public static void placeSpiralStairStep(LevelAccessor world, BlockStateProvider pillar, BlockStateProvider stairs, BlockPos center, BoundingBox boundingBox, Random random, boolean postProcess) {
        fillRing(world, SingleBlock.AIR, center, 1, 1, 1, boundingBox, random, true, true);
        placeBlock(world, pillar, center, boundingBox, random, true, true, postProcess);
        int facing = center.getY() % Orientation.HORIZONTAL_FACINGS.length;
        if (facing < 0) {
            facing = Orientation.HORIZONTAL_FACINGS.length + facing;
        }
        Direction direction = Orientation.HORIZONTAL_FACINGS[facing];
        BlockPos cursor = center.relative(direction);
        placeStairs(world, stairs, cursor, boundingBox, Half.BOTTOM, direction.getClockWise(), random, true, true, postProcess);
        cursor = cursor.relative(direction.getClockWise());
        placeStairs(world, stairs, cursor, boundingBox, Half.TOP, direction.getOpposite(), random, true, true, postProcess);
        cursor = cursor.relative(direction.getOpposite());
        placeStairs(world, stairs, cursor, boundingBox, Half.TOP, direction.getCounterClockWise(), random, true, true, postProcess);
    }

    public void placeStairs(BlockStateProvider stairs, BlockPos pos, Half half, Direction direction, BoundingBox boundingBox, Random random, boolean fillAir, boolean fillSolid, boolean postProcess) {
        placeStairs(world, stairs, coordinateSpace.rotateAndTranslateToOrigin(pos, rotation), boundingBox, half, direction, random, fillAir, fillSolid, postProcess);
    }

    public static void placeStairs(LevelAccessor world, BlockStateProvider stairs, BlockPos pos, BoundingBox boundingBox, Half half, Direction facing, Random random, boolean fillAir, boolean fillSolid, boolean postProcess) {
        if (!boundingBox.isInside(pos)) {
            return;
        }
        BlockState stair = stairs.get(world, pos, random);
        if (stair.hasProperty(BlockStateProperties.HALF)) {
            stair = stair.setValue(BlockStateProperties.HALF, half);
        }
        if (stair.hasProperty(BlockStateProperties.HORIZONTAL_FACING)) {
            stair = stair.setValue(BlockStateProperties.HORIZONTAL_FACING, facing);
        }
        placeBlockUnchecked(world, stair, pos, fillAir, fillSolid, postProcess);
    }

    public static void placeBlock(LevelAccessor world, BlockStateProvider block, BlockPos pos, BoundingBox boundingBox, Random random, boolean fillAir, boolean fillSolid, boolean postProcess) {
        if (boundingBox.isInside(pos)) {
            placeBlockUnchecked(world, block.get(world, pos, random), pos, fillAir, fillSolid, postProcess);
        }
    }

    public static void placeBlock(LevelAccessor world, BlockState block, BlockPos pos, BoundingBox boundingBox, boolean fillAir, boolean fillSolid, boolean postProcess) {
        if (boundingBox.isInside(pos)) {
            placeBlockUnchecked(world, block, pos, fillAir, fillSolid, postProcess);
        }
    }

    public static void placeBlockUnchecked(LevelAccessor world, BlockState state, BlockPos pos, boolean fillAir, boolean fillSolid, boolean postProcess) {
        if (isBlockProtected(world, pos)) {
            return;
        }
        if (world.isEmptyBlock(pos)) {
            if (!fillAir) {
                return;
            }
        } else {
            if (!fillSolid) {
                return;
            }
        }
        world.setBlock(pos, state, 2);
        if (postProcess) {
            world.getChunk(pos).markPosForPostprocessing(pos);
        }
        FluidState fluidState = world.getFluidState(pos);
        if (!fluidState.isEmpty()) {
            world.scheduleTick(pos, fluidState.getType(), 0);
        }
    }

    public static boolean isBlockProtected(LevelAccessor world, BlockPos pos) {
        return world.getBlockState(pos).getDestroySpeed(world, pos) < 0;
    }
}