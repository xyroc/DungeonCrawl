package xiroc.dungeoncrawl.util.bounds;

import net.minecraft.core.Vec3i;
import net.minecraft.world.level.levelgen.structure.BoundingBox;

public class BoundingBoxBuilder implements Bounded {
    public static BoundingBoxBuilder fromCorners(Vec3i corner1, Vec3i corner2) {
        return new BoundingBoxBuilder(Math.min(corner1.getX(), corner2.getX()),
                Math.min(corner1.getY(), corner2.getY()),
                Math.min(corner1.getZ(), corner2.getZ()),
                Math.max(corner1.getX(), corner2.getX()),
                Math.max(corner1.getY(), corner2.getY()),
                Math.max(corner1.getZ(), corner2.getZ()));
    }

    public static BoundingBoxBuilder fromPosition(Vec3i position) {
        return new BoundingBoxBuilder(position.getX(),
                position.getY(),
                position.getZ(),
                position.getX(),
                position.getY(),
                position.getZ());
    }

    public int minX;
    public int minY;
    public int minZ;
    public int maxX;
    public int maxY;
    public int maxZ;

    public BoundingBoxBuilder(int minX, int minY, int minZ, int maxX, int maxY, int maxZ) {
        this.minX = minX;
        this.minY = minY;
        this.minZ = minZ;
        this.maxX = maxX;
        this.maxY = maxY;
        this.maxZ = maxZ;
    }

    public BoundingBoxBuilder(BoundingBox boundingBox) {
        this.minX = boundingBox.minX();
        this.minY = boundingBox.minY();
        this.minZ = boundingBox.minZ();
        this.maxX = boundingBox.maxX();
        this.maxY = boundingBox.maxY();
        this.maxZ = boundingBox.maxZ();
    }

    public boolean exists() {
        return maxX >= minX && maxY >= minY && maxZ >= minZ;
    }

    public BoundingBox create() {
        return new BoundingBox(minX, minY, minZ, maxX, maxY, maxZ);
    }

    public void encapsulate(Bounded bounded) {
        minX = Math.min(minX, bounded.minX());
        minY = Math.min(minY, bounded.minY());
        minZ = Math.min(minZ, bounded.minZ());
        maxX = Math.max(maxX, bounded.maxX());
        maxY = Math.max(maxY, bounded.maxY());
        maxZ = Math.max(maxZ, bounded.maxZ());
    }

    public BoundingBoxBuilder move(int offsetX, int offsetY, int offsetZ) {
        minX += offsetX;
        minY += offsetY;
        minZ += offsetZ;
        maxX += offsetX;
        maxY += offsetY;
        maxZ += offsetZ;
        return this;
    }

    public BoundingBoxBuilder move(Vec3i offset) {
        return move(offset.getX(), offset.getY(), offset.getZ());
    }

    @Override
    public int minX() {
        return minX;
    }

    @Override
    public int minY() {
        return minY;
    }

    @Override
    public int minZ() {
        return minZ;
    }

    @Override
    public int maxX() {
        return maxX;
    }

    @Override
    public int maxY() {
        return maxY;
    }

    @Override
    public int maxZ() {
        return maxZ;
    }
}
