package xiroc.dungeoncrawl.dungeon.generator.element;

import net.minecraft.world.level.levelgen.structure.BoundingBox;
import net.minecraft.world.level.levelgen.structure.StructurePiece;
import xiroc.dungeoncrawl.util.bounds.Bounded;

import java.util.function.Consumer;

public abstract class DungeonElement implements Bounded {
    public final BoundingBox boundingBox;

    public DungeonElement(BoundingBox boundingBox) {
        this.boundingBox = boundingBox;
    }

    public abstract void createPieces(Consumer<StructurePiece> consumer);

    /**
     * Determines if this element blocks the placements of other elements at some position within the given area.
     * By default, this is a simple intersection check.
     * However, piece types can change the behavior of this method, potentially allowing for intersections.
     * @param bounded The area to check for collision.
     * @return {@code true} if there is a collision, {@code false} otherwise.
     */
    public boolean collides(Bounded bounded) {
        return intersects(bounded);
    }

    @Override
    public int minX() {
        return boundingBox.minX();
    }

    @Override
    public int minY() {
        return boundingBox.minY();
    }

    @Override
    public int minZ() {
        return boundingBox.minZ();
    }

    @Override
    public int maxX() {
        return boundingBox.maxX();
    }

    @Override
    public int maxY() {
        return boundingBox.maxY();
    }

    @Override
    public int maxZ() {
        return boundingBox.maxZ();
    }
}
