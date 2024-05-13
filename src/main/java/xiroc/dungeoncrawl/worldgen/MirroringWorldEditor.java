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

import java.util.Random;

public record MirroringWorldEditor(WorldEditor editor, CoordinateSpace coordinateSpace) implements WorldEditor {
    public MirroringWorldEditor(LevelAccessor level, CoordinateSpace coordinateSpace, Rotation rotation) {
        this(new RotatingWorldEditor(level, coordinateSpace, rotation));
    }
    
    public MirroringWorldEditor(RotatingWorldEditor editor) {
        this(editor, editor.coordinateSpace());
    }

    @Override
    public void placeBlock(BlockStateProvider block, Vec3i pos, BoundingBox boundingBox, Rotation blockRotation, Random random, boolean fillAir, boolean fillSolid,
                           boolean postProcess) {
        editor.placeBlock(block, pos, boundingBox, blockRotation, random, fillAir, fillSolid, postProcess);
        editor.placeBlock(block, coordinateSpace.rotate(pos, Rotation.CLOCKWISE_90), boundingBox, blockRotation.getRotated(Rotation.CLOCKWISE_90), random, fillAir, fillSolid, postProcess);
        editor.placeBlock(block, coordinateSpace.rotate(pos, Rotation.CLOCKWISE_180), boundingBox, blockRotation.getRotated(Rotation.CLOCKWISE_180), random, fillAir, fillSolid, postProcess);
        editor.placeBlock(block, coordinateSpace.rotate(pos, Rotation.COUNTERCLOCKWISE_90), boundingBox, blockRotation.getRotated(Rotation.COUNTERCLOCKWISE_90), random, fillAir, fillSolid, postProcess);
    }

    @Override
    public void placeBlock(BlockState block, Vec3i pos, BoundingBox boundingBox, boolean fillAir, boolean fillSolid, boolean postProcess) {
        editor.placeBlock(block, pos, boundingBox, fillAir, fillSolid, postProcess);
        editor.placeBlock(block, coordinateSpace.rotate(pos, Rotation.CLOCKWISE_90), boundingBox, fillAir, fillSolid, postProcess);
        editor.placeBlock(block, coordinateSpace.rotate(pos, Rotation.CLOCKWISE_180), boundingBox, fillAir, fillSolid, postProcess);
        editor.placeBlock(block, coordinateSpace.rotate(pos, Rotation.COUNTERCLOCKWISE_90), boundingBox, fillAir, fillSolid, postProcess);
    }

    @Override
    public void fill(BlockStateProvider blocks, Vec3i from, Vec3i to, BoundingBox boundingBox, Random random, boolean fillAir, boolean fillSolid, boolean postProcess) {
        editor.fill(blocks, from, to, boundingBox, random, fillAir, fillSolid, postProcess);
        editor.fill(blocks, coordinateSpace.rotate(from, Rotation.CLOCKWISE_90), coordinateSpace.rotate(to, Rotation.CLOCKWISE_90), boundingBox, random, fillAir, fillSolid, postProcess);
        editor.fill(blocks, coordinateSpace.rotate(from, Rotation.CLOCKWISE_180), coordinateSpace.rotate(to, Rotation.CLOCKWISE_180), boundingBox, random, fillAir, fillSolid, postProcess);
        editor.fill(blocks, coordinateSpace.rotate(from, Rotation.COUNTERCLOCKWISE_90), coordinateSpace.rotate(to, Rotation.COUNTERCLOCKWISE_90), boundingBox, random, fillAir, fillSolid, postProcess);
    }

    @Override
    public void fillRing(BlockStateProvider blocks, BlockPos center, int radius, int thickness, int height, BoundingBox boundingBox, Random random, boolean fillAir,
                         boolean fillSolid, boolean postProcess) {
        editor.fillRing(blocks, center, radius, thickness, height, boundingBox, random, fillAir, fillSolid, postProcess);
        editor.fillRing(blocks, coordinateSpace.rotate(center, Rotation.CLOCKWISE_90), radius, thickness, height, boundingBox, random, fillAir, fillSolid, postProcess);
        editor.fillRing(blocks, coordinateSpace.rotate(center, Rotation.CLOCKWISE_180), radius, thickness, height, boundingBox, random, fillAir, fillSolid, postProcess);
        editor.fillRing(blocks, coordinateSpace.rotate(center, Rotation.COUNTERCLOCKWISE_90), radius, thickness, height, boundingBox, random, fillAir, fillSolid, postProcess);
    }

    @Override
    public void placeStairs(BlockStateProvider stairs, BlockPos pos, Half half, Direction facing, BoundingBox boundingBox, Random random, boolean fillAir, boolean fillSolid, boolean postProcess) {
        editor.placeStairs(stairs, pos, half, facing, boundingBox, random, fillAir, fillSolid, postProcess);
        editor.placeStairs(stairs, coordinateSpace.rotate(pos, Rotation.CLOCKWISE_90), half, Rotation.CLOCKWISE_90.rotate(facing), boundingBox, random, fillAir, fillSolid, postProcess);
        editor.placeStairs(stairs, coordinateSpace.rotate(pos, Rotation.CLOCKWISE_180), half, Rotation.CLOCKWISE_180.rotate(facing), boundingBox, random, fillAir, fillSolid, postProcess);
        editor.placeStairs(stairs, coordinateSpace.rotate(pos, Rotation.COUNTERCLOCKWISE_90), half, Rotation.COUNTERCLOCKWISE_90.rotate(facing), boundingBox, random, fillAir, fillSolid, postProcess);
    }
}
