package xiroc.dungeoncrawl.util.bounds;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Vec3i;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import xiroc.dungeoncrawl.mixin.accessor.BoundingBoxAccessor;

public interface BoundingBoxUtils {
    static BoundingBox emptyBox() {
        return new BoundingBox(0, 0, 0, 0, 0, 0);
    }

    static Vec3i start(Vec3i from, Vec3i to, BoundingBox boundingBox) {
        int x = Math.max(boundingBox.minX(), Math.min(from.getX(), to.getX()));
        int y = Math.max(boundingBox.minY(), Math.min(from.getY(), to.getY()));
        int z = Math.max(boundingBox.minZ(), Math.min(from.getZ(), to.getZ()));
        return new Vec3i(x, y, z);
    }

    static Vec3i end(Vec3i from, Vec3i to, BoundingBox boundingBox) {
        int x = Math.min(boundingBox.maxX(), Math.max(from.getX(), to.getX()));
        int y = Math.min(boundingBox.maxY(), Math.max(from.getY(), to.getY()));
        int z = Math.min(boundingBox.maxZ(), Math.max(from.getZ(), to.getZ()));
        return new Vec3i(x, y, z);
    }

    static BoundingBoxBuilder tunnelBuilder(Vec3i start, Direction direction, int length, int height, int size) {
        Vec3i from = start.relative(direction.getCounterClockWise(), size);
        Vec3i to = start.relative(direction.getClockWise(), size)
                .relative(direction, length - 1)
                .relative(Direction.UP, height - 1);
        return BoundingBoxBuilder.fromCorners(from, to);
    }

    static BoundingBox centeredBoundingBox(Vec3i center, int radius, int height) {
        return new BoundingBox(center.getX() - radius,
                center.getY(),
                center.getZ() - radius,
                center.getX() + radius,
                center.getY() + height - 1,
                center.getZ() + radius);
    }

    static BoundingBoxBuilder centeredBuilder(Vec3i center, int radius, int height) {
        return new BoundingBoxBuilder(centeredBoundingBox(center, radius, height));
    }

    static void move(BoundingBox boundingBox, Vec3i offset) {
        BoundingBoxAccessor accessor = (BoundingBoxAccessor) boundingBox;
        accessor.setMinX(boundingBox.minX() + offset.getX());
        accessor.setMinY(boundingBox.minY() + offset.getY());
        accessor.setMinZ(boundingBox.minZ() + offset.getZ());
        accessor.setMaxX(boundingBox.maxX() + offset.getX());
        accessor.setMaxY(boundingBox.maxY() + offset.getY());
        accessor.setMaxZ(boundingBox.maxZ() + offset.getZ());
    }

    static void shrink(BoundingBox boundingBox, Direction direction, int amount) {
        BoundingBoxAccessor accessor = (BoundingBoxAccessor) boundingBox;
        switch (direction) {
            case NORTH -> accessor.setMinZ(boundingBox.minZ() + amount);
            case EAST -> accessor.setMaxX(boundingBox.maxX() - amount);
            case SOUTH -> accessor.setMaxZ(boundingBox.maxZ() - amount);
            case WEST -> accessor.setMinX(boundingBox.minX() + amount);
            case UP -> accessor.setMaxY(boundingBox.maxY() - amount);
            case DOWN -> accessor.setMinY(boundingBox.minY() + amount);
        }
    }

    static Vec3i snapOntoEdge(Vec3i vector, BoundingBox boundingBox, Direction direction) {
        return switch (direction) {
            case UP -> new Vec3i(vector.getX(), boundingBox.maxY() + 1, vector.getZ());
            case DOWN -> new Vec3i(vector.getX(), boundingBox.minY() - 1, vector.getZ());
            case SOUTH -> new Vec3i(vector.getX(), vector.getY(), boundingBox.maxZ() + 1);
            case NORTH -> new Vec3i(vector.getX(), vector.getY(), boundingBox.minZ() - 1);
            case EAST -> new Vec3i(boundingBox.maxX() + 1, vector.getY(), vector.getZ());
            case WEST -> new Vec3i(boundingBox.minX() - 1, vector.getY(), vector.getZ());
        };
    }

    /**
     * A debug method to visualize bounding boxes ingame.
     */
    static void build(WorldGenLevel world, BoundingBox box, Block block) {
        BlockState state = block.defaultBlockState();

        for (int x = box.minX(); x < box.maxX(); ++x) {
            world.setBlock(new BlockPos(x, box.minY(), box.minZ()), state, 2);
            world.setBlock(new BlockPos(x, box.minY(), box.maxZ()), state, 2);

            world.setBlock(new BlockPos(x, box.maxY(), box.minZ()), state, 2);
            world.setBlock(new BlockPos(x, box.maxY(), box.maxZ()), state, 2);
        }

        for (int y = box.minY(); y < box.maxY(); ++y) {
            world.setBlock(new BlockPos(box.minX(), y, box.minZ()), state, 2);
            world.setBlock(new BlockPos(box.minX(), y, box.maxZ()), state, 2);

            world.setBlock(new BlockPos(box.maxX(), y, box.minZ()), state, 2);
            world.setBlock(new BlockPos(box.maxX(), y, box.maxZ()), state, 2);
        }

        for (int z = box.minZ(); z < box.maxZ(); ++z) {
            world.setBlock(new BlockPos(box.minX(), box.minY(), z), state, 2);
            world.setBlock(new BlockPos(box.minX(), box.maxY(), z), state, 2);

            world.setBlock(new BlockPos(box.maxX(), box.minY(), z), state, 2);
            world.setBlock(new BlockPos(box.maxX(), box.maxY(), z), state, 2);
        }

        world.setBlock(new BlockPos(box.maxX(), box.maxY(), box.maxZ()), state, 2);
    }
}