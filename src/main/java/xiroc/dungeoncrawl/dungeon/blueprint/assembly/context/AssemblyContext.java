package xiroc.dungeoncrawl.dungeon.blueprint.assembly.context;

import org.jetbrains.annotations.Nullable;
import xiroc.dungeoncrawl.dungeon.blueprint.Entrance;
import xiroc.dungeoncrawl.dungeon.blueprint.anchor.Anchor;
import xiroc.dungeoncrawl.dungeon.component.BlueprintComponent;
import xiroc.dungeoncrawl.dungeon.generator.level.LevelGenerator;
import xiroc.dungeoncrawl.dungeon.piece.BlueprintPiece;

public interface AssemblyContext {
    void noteOpenEntrance(Entrance entrance, Anchor placement, @Nullable BlueprintComponent entrancePart);

    void assemble(BlueprintPiece piece, LevelGenerator levelGenerator);
}
