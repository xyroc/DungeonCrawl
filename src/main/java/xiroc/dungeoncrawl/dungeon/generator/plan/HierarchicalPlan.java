package xiroc.dungeoncrawl.dungeon.generator.plan;

import xiroc.dungeoncrawl.dungeon.generator.element.DungeonElement;
import xiroc.dungeoncrawl.util.bounds.Bounded;

import java.util.function.Consumer;
import java.util.function.Predicate;

/**
 * Logically combines two dungeon plans into one.
 * <p>
 * Except for adding an element, operations are executed first on the primary and then on the secondary plan.
 * Elements are only added to the primary plan.
 * @param primary The primary plan
 * @param secondary The secondary plan
 */
public record HierarchicalPlan(DungeonPlan primary, DungeonPlan secondary) implements DungeonPlan {
    @Override
    public void add(DungeonElement element) {
        primary.add(element);
    }

    @Override
    public boolean isFree(Bounded boundingBox) {
        return primary.isFree(boundingBox) && secondary.isFree(boundingBox);
    }

    @Override
    public void forEach(Consumer<DungeonElement> consumer) {
        primary.forEach(consumer);
        secondary.forEach(consumer);
    }

    @Override
    public void forEachIn(Bounded boundingBox, Consumer<DungeonElement> consumer) {
        primary.forEachIn(boundingBox, consumer);
        secondary.forEachIn(boundingBox, consumer);
    }

    @Override
    public boolean anyMatch(Bounded boundingBox, Predicate<DungeonElement> predicate) {
        return primary.anyMatch(boundingBox, predicate) && secondary.anyMatch(boundingBox, predicate);
    }

    @Override
    public int pieceCount() {
        return primary.pieceCount() + secondary.pieceCount();
    }
}
