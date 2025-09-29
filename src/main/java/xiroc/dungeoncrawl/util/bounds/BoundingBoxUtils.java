package xiroc.dungeoncrawl.util.bounds;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Vec3i;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.structure.BoundingBox;

public interface BoundingBoxUtils {
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

    /**
     * Calculates a bounding box that encapsulates exactly all positions that are within both
     * the provided bounding boxes.
     *
     * @param boxOne The first bounding box.
     * @param boxTwo The second bounding box.
     * @return A bounding box covering the intersection of the two boxes.
     */
    static BoundingBox intersection(BoundingBox boxOne, BoundingBox boxTwo) {
        return new BoundingBox(
                Math.max(boxOne.minX(), boxTwo.minX()),
                Math.max(boxOne.minY(), boxTwo.minY()),
                Math.max(boxOne.minZ(), boxTwo.minZ()),
                Math.min(boxOne.maxX(), boxTwo.maxX()),
                Math.min(boxOne.maxY(), boxTwo.maxY()),
                Math.min(boxOne.maxZ(), boxTwo.maxZ())
        );
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