package xiroc.dungeoncrawl.dungeon.generator.plan;

import xiroc.dungeoncrawl.dungeon.generator.element.DungeonElement;
import xiroc.dungeoncrawl.util.bounds.Bounded;

import java.util.function.Consumer;
import java.util.function.Predicate;

public interface DungeonPlan {
    void add(DungeonElement element);

    boolean isFree(Bounded boundingBox);

    void forEach(Consumer<DungeonElement> consumer);

    void forEachIn(Bounded boundingBox, Consumer<DungeonElement> consumer);

    boolean anyMatch(Bounded boundingBox, Predicate<DungeonElement> predicate);
}
