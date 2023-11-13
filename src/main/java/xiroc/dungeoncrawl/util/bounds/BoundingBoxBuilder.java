package xiroc.dungeoncrawl.util.bounds;

import net.minecraft.core.Direction;
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

    public void move(int offsetX, int offsetY, int offsetZ) {
        minX += offsetX;
        minY += offsetY;
        minZ += offsetZ;
        maxX += offsetX;
        maxY += offsetY;
        maxZ += offsetZ;
    }

    public void move(Vec3i offset) {
        move(offset.getX(), offset.getY(), offset.getZ());
    }

    public void shrink(Direction side, int amount) {
        switch (side) {
            case NORTH -> minZ += amount;
            case EAST -> maxX -= amount;
            case SOUTH -> maxZ -= amount;
            case WEST -> minX += amount;
            case UP -> maxY -= amount;
            case DOWN -> minY += amount;
        }
    }

    public void shrinkHorizontally(int amount) {
        this.minX += amount;
        this.minZ += amount;
        this.maxX -= amount;
        this.maxY -= amount;
    }

    public BoundingBox cut(Direction side, int amount) {
        return switch (side) {
            case NORTH -> {
                int minZ = this.minZ;
                this.minZ += amount;
                yield new BoundingBox(this.minX, this.minY, minZ, this.maxX, this.maxY, this.minZ - 1);
            }
            case EAST -> {
                int maxX = this.maxX;
                this.maxX -= amount;
                yield new BoundingBox(this.maxX + 1, this.minY, this.minZ, maxX, this.maxY, this.maxZ);
            }
            case SOUTH -> {
                int maxZ = this.maxZ;
                this.maxZ -= amount;
                yield new BoundingBox(this.minX, this.minY, this.maxZ + 1, this.maxX, this.maxY, maxZ);
            }
            case WEST -> {
                int minX = this.minX;
                this.minX += amount;
                yield new BoundingBox(minX, this.minY, this.minZ, this.minX - 1, this.maxY, this.maxZ);
            }
            case UP -> {
                int maxY = this.maxY;
                this.maxY -= amount;
                yield new BoundingBox(this.minX, this.maxY + 1, minZ, this.maxX, maxY, this.maxZ);
            }
            case DOWN -> {
                int minY = this.minY;
                this.minY += amount;
                yield new BoundingBox(this.minX, minY, minZ, this.maxX, this.minY - 1, this.maxZ);
            }
        };
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
