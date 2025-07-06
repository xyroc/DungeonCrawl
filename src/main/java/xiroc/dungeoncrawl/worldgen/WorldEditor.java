package xiroc.dungeoncrawl.worldgen;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Vec3i;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.FallingBlock;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.Half;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import net.minecraft.world.level.material.FluidState;
import org.jetbrains.annotations.Nullable;
import xiroc.dungeoncrawl.config.Config;
import xiroc.dungeoncrawl.dungeon.block.provider.BlockStateProvider;
import xiroc.dungeoncrawl.util.bounds.BoundingBoxUtils;


public interface WorldEditor {
    default void placeBlock(BlockStateProvider block, Vec3i pos, BoundingBox boundingBox, RandomSource random, boolean fillAir, boolean fillSolid, boolean postProcess) {
        placeBlock(block, pos, boundingBox, Rotation.NONE, random, fillAir, fillSolid, postProcess);
    }

    void placeBlock(BlockStateProvider block, Vec3i pos, BoundingBox boundingBox, Rotation blockRotation, RandomSource random, boolean fillAir, boolean fillSolid, boolean postProcess);

    void placeBlock(BlockState block, Vec3i pos, BoundingBox boundingBox, boolean fillAir, boolean fillSolid, boolean postProcess);

    void fill(BlockStateProvider stateProvider, Vec3i from, Vec3i to, BoundingBox boundingBox, RandomSource random, boolean fillAir, boolean fillSolid, boolean postProcess);

    void fillRing(BlockStateProvider blocks, BlockPos center, int radius, int thickness, int height, BoundingBox boundingBox, RandomSource random, boolean fillAir,
                  boolean fillSolid, boolean postProcess);

    void placeStairs(BlockStateProvider stairs, BlockPos pos, Half half, Direction facing, BoundingBox boundingBox, RandomSource random, boolean fillAir, boolean fillSolid,
                     boolean postProcess);

    static void fill(LevelAccessor world, @Nullable BlockStateProvider solid, @Nullable BlockStateProvider nonSolid, Vec3i from, Vec3i to, BoundingBox boundingBox, RandomSource random, boolean postProcess) {
        Vec3i startVec = BoundingBoxUtils.start(from, to, boundingBox);
        Vec3i endVec = BoundingBoxUtils.end(from, to, boundingBox);
        Unsafe.fill(world, solid, nonSolid, startVec, endVec, random, postProcess);
    }

    static void fill(LevelAccessor world, BlockStateProvider blocks, Vec3i from, Vec3i to, BoundingBox boundingBox, RandomSource random, boolean fillAir, boolean fillSolid, boolean postProcess) {
        Vec3i startVec = BoundingBoxUtils.start(from, to, boundingBox);
        Vec3i endVec = BoundingBoxUtils.end(from, to, boundingBox);
        Unsafe.fill(world, blocks, startVec, endVec, random, fillAir, fillSolid, postProcess);
    }

    static void fillWalls(LevelAccessor world, BlockStateProvider stateProvider, BlockPos from, BlockPos to, BoundingBox boundingBox, RandomSource random, boolean fillAir, boolean fillSolid) {
        Vec3i startVec = BoundingBoxUtils.start(from, to, boundingBox);
        Vec3i endVec = BoundingBoxUtils.end(from, to, boundingBox);

        for (int x = startVec.getX(); x <= endVec.getX(); x++) {
            for (int z = startVec.getZ(); z <= endVec.getZ(); z++) {
                BlockPos bottom = new BlockPos(x, from.getY(), z);
                BlockPos top = new BlockPos(x, to.getY(), z);
                Unsafe.placeBlock(world, bottom, stateProvider.get(bottom, random), fillAir, fillSolid, false);
                Unsafe.placeBlock(world, top, stateProvider.get(top, random), fillAir, fillSolid, false);
            }
        }

        if (startVec.getX() == Math.min(from.getX(), to.getX())) {
            for (int z = startVec.getZ(); z <= endVec.getZ(); z++) {
                for (int y = startVec.getY() + 1; y < endVec.getY(); y++) {
                    BlockPos pos = new BlockPos(startVec.getX(), y, z);
                    Unsafe.placeBlock(world, pos, stateProvider.get(pos, random), fillAir, fillSolid, false);
                }
            }
        }

        if (endVec.getX() == Math.max(from.getX(), to.getX())) {
            for (int z = startVec.getZ(); z <= endVec.getZ(); z++) {
                for (int y = startVec.getY() + 1; y < endVec.getY(); y++) {
                    BlockPos pos = new BlockPos(endVec.getX(), y, z);
                    Unsafe.placeBlock(world, pos, stateProvider.get(pos, random), fillAir, fillSolid, false);
                }
            }
        }

        if (startVec.getZ() == Math.min(from.getZ(), to.getZ())) {
            for (int x = startVec.getX(); x <= endVec.getX(); x++) {
                for (int y = startVec.getY() + 1; y < endVec.getY(); y++) {
                    BlockPos pos = new BlockPos(x, y, startVec.getZ());
                    Unsafe.placeBlock(world, pos, stateProvider.get(pos, random), fillAir, fillSolid, false);
                }
            }
        }

        if (endVec.getZ() == Math.max(from.getZ(), to.getZ())) {
            for (int x = startVec.getX(); x <= endVec.getX(); x++) {
                for (int y = startVec.getY() + 1; y < endVec.getY(); y++) {
                    BlockPos pos = new BlockPos(x, y, endVec.getZ());
                    Unsafe.placeBlock(world, pos, stateProvider.get(pos, random), fillAir, fillSolid, false);
                }
            }
        }
    }

    static void fillRing(LevelAccessor world, BlockStateProvider blocks, BlockPos center, int radius, int thickness, int height, BoundingBox boundingBox, RandomSource random,
                         boolean fillAir, boolean fillSolid) {
        if (thickness >= radius) {
            fill(world, blocks, null, center.offset(-radius, 0, -radius), center.offset(radius, height - 1, radius), boundingBox, random, false);
            return;
        }
        fill(world, blocks, center.offset(-radius, 0, -radius), center.offset(radius - thickness, height - 1, thickness - radius - 1), boundingBox, random, fillAir, fillSolid, false);
        fill(world, blocks, center.offset(radius - thickness + 1, 0, -radius), center.offset(radius, height - 1, radius - thickness), boundingBox, random, fillAir, fillSolid, false);
        fill(world, blocks, center.offset(thickness - radius, 0, radius - thickness + 1), center.offset(radius, height - 1, radius), boundingBox, random, fillAir, fillSolid, false);
        fill(world, blocks, center.offset(-radius, 0, thickness - radius), center.offset(thickness - radius - 1, height - 1, radius), boundingBox, random, fillAir, fillSolid, false);
    }

    static void placeEntrance(LevelAccessor world, BlockStateProvider stairs, BlockPos pos, Direction parallelTo, BoundingBox boundingBox, RandomSource random, boolean fillAir, boolean fillSolid) {
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

    static void placeStairs(LevelAccessor world, BlockStateProvider stairs, BlockPos pos, BoundingBox boundingBox, Half half, Direction facing, RandomSource random, boolean fillAir, boolean fillSolid, boolean postProcess) {
        if (!boundingBox.isInside(pos)) {
            return;
        }
        BlockState stair = stairs.get(pos, random);
        if (stair.hasProperty(BlockStateProperties.HALF)) {
            stair = stair.setValue(BlockStateProperties.HALF, half);
        }
        if (stair.hasProperty(BlockStateProperties.HORIZONTAL_FACING)) {
            stair = stair.setValue(BlockStateProperties.HORIZONTAL_FACING, facing);
        }
        Unsafe.placeBlock(world, pos, stair, fillAir, fillSolid, postProcess);
    }

    static void placeBlock(LevelAccessor world, BlockPos pos, @Nullable BlockStateProvider solid, @Nullable BlockStateProvider nonSolid, BoundingBox boundingBox, RandomSource random, boolean postProcess) {
        if (boundingBox.isInside(pos)) {
            Unsafe.placeBlock(world, pos, solid, nonSolid, random, postProcess);
        }
    }

    static void placeBlock(LevelAccessor world, BlockStateProvider block, BlockPos pos, BoundingBox boundingBox, RandomSource random, boolean fillAir, boolean fillSolid, boolean postProcess) {
        if (boundingBox.isInside(pos)) {
            Unsafe.placeBlock(world, pos, block.get(pos, random), fillAir, fillSolid, postProcess);
        }
    }

    static void placeBlock(LevelAccessor world, BlockState block, BlockPos pos, BoundingBox boundingBox, boolean fillAir, boolean fillSolid, boolean postProcess) {
        if (boundingBox.isInside(pos)) {
            Unsafe.placeBlock(world, pos, block, fillAir, fillSolid, postProcess);
        }
    }

    static void buildFoundation(LevelAccessor world, BlockPos pos, RandomSource random, BoundingBox worldGenBounds, DungeonWorldGenContext context) {
        if (!worldGenBounds.isInside(pos)) {
            return;
        }
        boolean pillar = ((pos.getX() & 2) | (pos.getZ() & 2)) == 0;
        BlockStateProvider palette = context.primaryTheme().get().masonry();
        if (pillar) {
            int downwards = Math.max(Unsafe.countEmptyBlocksDownwards(world, pos.below()), 1);
            fill(world, palette, palette, pos.below(), pos.below(downwards), worldGenBounds, random, false);
        } else {
            placeBlock(world, palette, pos.below(), worldGenBounds, random, true, true, false);
        }
    }

    /**
     * Operations used for world generation that are "unsafe" in the sense that no bounding box checks are made.
     * Use these only if you made sure beforehand that they won't exceed valid bounding boxes.
     */
    interface Unsafe {
        static void fill(LevelAccessor world, BlockStateProvider blocks, Vec3i from, Vec3i to, RandomSource random, boolean fillAir, boolean fillSolid, boolean postProcess) {
            BlockPos.MutableBlockPos pos = new BlockPos.MutableBlockPos();
            for (int x = from.getX(); x <= to.getX(); x++) {
                for (int y = from.getY(); y <= to.getY(); y++) {
                    for (int z = from.getZ(); z <= to.getZ(); z++) {
                        pos.set(x, y, z);
                        placeBlock(world, pos, blocks.get(pos, random), fillAir, fillSolid, postProcess);
                    }
                }
            }
        }

        static void fill(LevelAccessor world, @Nullable BlockStateProvider solid, @Nullable BlockStateProvider nonSolid, Vec3i from, Vec3i to, RandomSource random, boolean postProcess) {
            BlockPos.MutableBlockPos pos = new BlockPos.MutableBlockPos();
            for (int x = from.getX(); x <= to.getX(); x++) {
                for (int y = from.getY(); y <= to.getY(); y++) {
                    for (int z = from.getZ(); z <= to.getZ(); z++) {
                        pos.set(x, y, z);
                        placeBlock(world, pos, solid, nonSolid, random, postProcess);
                    }
                }
            }
        }

        static void placeBlock(LevelAccessor world, BlockPos pos, BlockState state, boolean fillAir, boolean fillSolid, boolean postProcess) {
            if (isBlockProtected(world, pos)) {
                return;
            }
            if (!fillAir && world.isEmptyBlock(pos)) {
                return;
            } else if (!fillSolid && !world.isEmptyBlock(pos)) {
                return;
            }
            placeBlock(world, pos, state, postProcess);
        }

        static void placeBlock(LevelAccessor world, BlockPos pos, @Nullable BlockStateProvider solid, @Nullable BlockStateProvider nonSolid, RandomSource random, boolean postProcess) {
            if (isBlockProtected(world, pos)) {
                return;
            }
            BlockStateProvider state = world.isEmptyBlock(pos) ? nonSolid : solid;
            if (state == null) {
                return;
            }
            placeBlock(world, pos, state.get(pos, random), postProcess);
        }

        private static void placeBlock(LevelAccessor world, BlockPos pos, BlockState state, boolean postProcess) {
            world.setBlock(pos, state, 3);
            if (Config.TICK_FALLING_BLOCKS.get() && state.getBlock() instanceof FallingBlock) {
                world.scheduleTick(pos, state.getBlock(), 1);
            } else if (postProcess) {
                world.getChunk(pos).markPosForPostprocessing(pos);
            }
            FluidState fluidState = world.getFluidState(pos);
            if (!fluidState.isEmpty()) {
                world.scheduleTick(pos, fluidState.getType(), 0);
            }
        }

        static int countEmptyBlocksDownwards(LevelAccessor world, BlockPos pos) {
            int blocks = 0;
            final BlockPos.MutableBlockPos cursor = pos.mutable();
            while (cursor.getY() > world.getMinBuildHeight() && world.isEmptyBlock(cursor)) {
                ++blocks;
                cursor.move(0, -1, 0);
            }
            return blocks;
        }

        static boolean isBlockProtected(LevelAccessor world, BlockPos pos) {
            return world.getBlockState(pos).getDestroySpeed(world, pos) < 0;
        }
    }
}
