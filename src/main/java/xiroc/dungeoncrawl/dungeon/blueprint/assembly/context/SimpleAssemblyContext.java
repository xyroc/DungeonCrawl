package xiroc.dungeoncrawl.dungeon.blueprint.assembly.context;

import org.jetbrains.annotations.Nullable;
import xiroc.dungeoncrawl.dungeon.blueprint.Entrance;
import xiroc.dungeoncrawl.dungeon.blueprint.anchor.Anchor;
import xiroc.dungeoncrawl.dungeon.component.BlueprintComponent;
import xiroc.dungeoncrawl.dungeon.component.EntranceComponent;
import xiroc.dungeoncrawl.dungeon.generator.level.LevelGenerator;
import xiroc.dungeoncrawl.dungeon.piece.BlueprintPiece;

import java.util.ArrayList;
import java.util.List;

public class SimpleAssemblyContext implements AssemblyContext {

    private final List<EntranceComponent> openEntrances = new ArrayList<>(4);

    @Override
    public void noteOpenEntrance(Entrance entrance, Anchor placement, @Nullable BlueprintComponent entrancePart) {
        EntranceComponent entranceComponent = entrance.place(placement);
        if (entranceComponent != null) {
            openEntrances.add(entranceComponent);
        }
    }

    @Override
    public void assemble(BlueprintPiece piece, LevelGenerator levelGenerator) {
        for (EntranceComponent entranceComponent : openEntrances) {
            piece.addComponent(entranceComponent);
        }
    }

}
