package xiroc.dungeoncrawl.dungeon.piece;

import xiroc.dungeoncrawl.datapack.registry.Delegate;
import xiroc.dungeoncrawl.dungeon.component.BlueprintComponent;
import xiroc.dungeoncrawl.dungeon.theme.PrimaryTheme;
import xiroc.dungeoncrawl.dungeon.theme.SecondaryTheme;

/**
 * Used for blueprint-based pieces to make the main blueprint component accessible.
 */
public class BlueprintPiece extends DungeonPiece {
    public final BlueprintComponent base;

    public BlueprintPiece(BlueprintComponent base, Delegate<PrimaryTheme> primaryTheme, Delegate<SecondaryTheme> secondaryTheme, int stage) {
        super(base, primaryTheme, secondaryTheme, stage);
        this.base = base;
    }
}
