package xiroc.dungeoncrawl.dungeon.piece;

import xiroc.dungeoncrawl.dungeon.component.BlueprintComponent;
import xiroc.dungeoncrawl.worldgen.DungeonWorldGenContext;

/**
 * Used for blueprint-based pieces to make the main blueprint component accessible.
 */
public class BlueprintPiece extends DungeonPiece {
    public final BlueprintComponent base;

    public BlueprintPiece(BlueprintComponent base, DungeonWorldGenContext worldGenContext) {
        super(base, worldGenContext);
        this.base = base;
    }
}
