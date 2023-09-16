package xiroc.dungeoncrawl.dungeon.generator.plan;

import net.minecraft.world.level.levelgen.structure.BoundingBox;
import xiroc.dungeoncrawl.dungeon.generator.element.DungeonElement;
import xiroc.dungeoncrawl.util.bounds.Bounded;

import java.util.ArrayList;
import java.util.function.Consumer;
import java.util.function.Predicate;

public class ListPlan implements DungeonPlan {
    private final ArrayList<DungeonElement> elements = new ArrayList<>();

    @Override
    public void add(DungeonElement element) {
        elements.add(element);
    }

    @Override
    public boolean isFree(Bounded boundingBox) {
        return elements.stream().noneMatch((element) -> element.intersects(boundingBox));
    }

    @Override
    public void forEach(Consumer<DungeonElement> consumer) {
        elements.forEach(consumer);
    }

    @Override
    public void forEachIn(Bounded boundingBox, Consumer<DungeonElement> consumer) {
        elements.stream().filter((element) -> element.intersects(boundingBox)).forEach(consumer);
    }

    @Override
    public boolean anyMatch(Bounded boundingBox, Predicate<DungeonElement> predicate) {
        return elements.stream().anyMatch((element) -> element.intersects(boundingBox) && predicate.test(element));
    }
}
