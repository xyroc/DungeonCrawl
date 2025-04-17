package xiroc.dungeoncrawl.worldgen;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Vec3i;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.Half;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import xiroc.dungeoncrawl.dungeon.block.provider.BlockStateProvider;
import xiroc.dungeoncrawl.util.CoordinateSpace;
import xiroc.dungeoncrawl.util.bounds.BoundingBoxUtils;

import java.util.Random;

public record RotatingWorldEditor(LevelAccessor level, CoordinateSpace coordinateSpace, Rotation rotation) implements WorldEditor {
    @Override
    public void fill(BlockStateProvider blocks, Vec3i from, Vec3i to, BoundingBox boundingBox, Random random, boolean fillAir, boolean fillSolid, boolean postProcess) {
        final Vec3i fromRotated = coordinateSpace.rotateAndTranslateToOrigin(from, rotation);
        final Vec3i toRotated = coordinateSpace.rotateAndTranslateToOrigin(to, rotation);

        final Vec3i start = BoundingBoxUtils.start(fromRotated, toRotated, boundingBox);
        final Vec3i end = BoundingBoxUtils.end(fromRotated, toRotated, boundingBox);

        BlockPos.MutableBlockPos pos = new BlockPos.MutableBlockPos();
        for (int x = start.getX(); x <= end.getX(); x++) {
            for (int z = start.getZ(); z <= end.getZ(); z++) {
                for (int y = start.getY(); y <= end.getY(); y++) {
                    pos.set(x, y, z);
                    Unsafe.placeBlock(level, pos, blocks.get(pos, random).rotate(level, pos, rotation), fillAir, fillSolid, postProcess);
                }
            }
        }
    }

    @Override
    public void fillRing(BlockStateProvider blocks, BlockPos center, int radius, int thickness, int height, BoundingBox boundingBox, Random random, boolean fillAir,
                         boolean fillSolid, boolean postProcess) {
        if (thickness >= radius) {
            fill(blocks, center.offset(-radius, 0, -radius), center.offset(radius, height - 1, radius), boundingBox, random, fillAir, fillSolid, postProcess);
            return;
        }
        fill(blocks, center.offset(-radius, 0, -radius), center.offset(radius - thickness, height - 1, thickness - radius - 1), boundingBox, random, fillAir, fillSolid, postProcess);
        fill(blocks, center.offset(radius - thickness + 1, 0, -radius), center.offset(radius, height - 1, radius - thickness), boundingBox, random, fillAir, fillSolid, postProcess);
        fill(blocks, center.offset(thickness - radius, 0, radius - thickness + 1), center.offset(radius, height - 1, radius), boundingBox, random, fillAir, fillSolid, postProcess);
        fill(blocks, center.offset(-radius, 0, thickness - radius), center.offset(thickness - radius - 1, height - 1, radius), boundingBox, random, fillAir, fillSolid, postProcess);
    }

    @Override
    public void placeStairs(BlockStateProvider stairs, BlockPos pos, Half half, Direction facing, BoundingBox boundingBox, Random random, boolean fillAir, boolean fillSolid, boolean postProcess) {
        WorldEditor.placeStairs(level, stairs, coordinateSpace.rotateAndTranslateToOrigin(pos, rotation), boundingBox, half, rotation.rotate(facing), random, fillAir, fillSolid, postProcess);
    }

    @Override
    public void placeBlock(BlockStateProvider block, Vec3i pos, BoundingBox boundingBox, Rotation blockRotation, Random random, boolean fillAir, boolean fillSolid,
                           boolean postProcess) {
        BlockPos position = coordinateSpace.rotateAndTranslateToOrigin(pos, rotation);
        WorldEditor.placeBlock(level, block.get(position, random).rotate(level, position, rotation.getRotated(blockRotation)), position, boundingBox, fillAir, fillSolid, postProcess);
    }

    @Override
    public void placeBlock(BlockState block, Vec3i pos, BoundingBox boundingBox, boolean fillAir, boolean fillSolid, boolean postProcess) {
        BlockPos position = coordinateSpace.rotateAndTranslateToOrigin(pos, rotation);
        WorldEditor.placeBlock(level, block.rotate(level, position, rotation), position, boundingBox, fillAir, fillSolid, postProcess);
    }
}