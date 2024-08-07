package xiroc.dungeoncrawl.util.bounds;

import net.minecraft.world.level.levelgen.structure.BoundingBox;

public interface Bounded {
    int minX();

    int minY();

    int minZ();

    int maxX();

    int maxY();

    int maxZ();

    default boolean intersects(Bounded bounded) {
        return maxX() >= bounded.minX() && minX() <= bounded.maxX() &&
                maxY() >= bounded.minY() && minY() <= bounded.maxY() &&
                maxZ() >= bounded.minZ() && minZ() <= bounded.maxZ();
    }

    default boolean intersects(BoundingBox bounded) {
        return maxX() >= bounded.minX() && minX() <= bounded.maxX() &&
                maxY() >= bounded.minY() && minY() <= bounded.maxY() &&
                maxZ() >= bounded.minZ() && minZ() <= bounded.maxZ();
    }

    default boolean encapsulates(Bounded bounded) {
        return minX() <= bounded.minX() && maxX() >= bounded.maxX() &&
                minY() <= bounded.minY() && maxY() >= bounded.maxY() &&
                minZ() <= bounded.minZ() && maxZ() >= bounded.maxZ();
    }
}
