package xiroc.dungeoncrawl.dungeon.generator.element;

import net.minecraft.world.level.levelgen.structure.BoundingBox;
import net.minecraft.world.level.levelgen.structure.StructurePiece;
import xiroc.dungeoncrawl.util.bounds.Bounded;

import java.util.Random;
import java.util.function.Consumer;

public abstract class DungeonElement implements Bounded {
    public final BoundingBox boundingBox;

    public DungeonElement(BoundingBox boundingBox) {
        this.boundingBox = boundingBox;
    }

    public abstract void createPieces(Consumer<StructurePiece> consumer, Random random);

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
