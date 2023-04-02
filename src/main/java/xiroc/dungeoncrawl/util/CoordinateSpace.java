package xiroc.dungeoncrawl.util;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Vec3i;
import net.minecraft.world.level.block.Rotation;
import xiroc.dungeoncrawl.dungeon.blueprint.anchor.Anchor;

public record CoordinateSpace(BlockPos origin, int width, int length) {
    public static BlockPos rotate(int x, int y, int z, Rotation rotation, int width, int length) {
        return switch (rotation) {
            case CLOCKWISE_90 -> new BlockPos(length - 1 - z, y, x);
            case COUNTERCLOCKWISE_90 -> new BlockPos(z, y, width - 1 - x);
            case CLOCKWISE_180 -> new BlockPos(width - 1 - x, y, length - 1 - z);
            default -> new BlockPos(x, y, z);
        };
    }

    public Anchor rotateAndTranslateToOrigin(Anchor anchor, Rotation rotation) {
        return new Anchor(rotateAndTranslateToOrigin(anchor.position(), rotation), rotation.rotate(anchor.direction()));
    }

    public static BlockPos rotate(Vec3i coordinate, Rotation rotation, int width, int length) {
        return rotate(coordinate.getX(), coordinate.getY(), coordinate.getZ(), rotation, width, length);
    }

    public BlockPos rotateAndTranslateToOrigin(int x, int y, int z, Rotation rotation) {
        return origin.offset(rotate(x, y, z, rotation, width, length));
    }

    public BlockPos rotateAndTranslateToOrigin(Vec3i coordinate, Rotation rotation) {
        return rotateAndTranslateToOrigin(coordinate.getX(), coordinate.getY(), coordinate.getZ(), rotation);
    }
}
