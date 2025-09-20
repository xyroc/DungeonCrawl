package xiroc.dungeoncrawl.dungeon.generator.element;

import net.minecraft.world.level.levelgen.structure.BoundingBox;
import net.minecraft.world.level.levelgen.structure.StructurePiece;
import xiroc.dungeoncrawl.dungeon.blueprint.Entrance;
import xiroc.dungeoncrawl.dungeon.blueprint.anchor.Anchor;
import xiroc.dungeoncrawl.util.bounds.Bounded;

import java.util.function.Consumer;

/**
 * Signals that a nearby node has an unused entrance.
 * Used when placing new nodes to check if any connections to nearby existing nodes are feasible.
 * <p>
 * The bounding box of this element covers the axis of the unused entrance, starting from the entrance's position
 * and from there the maximum corridor length amount of blocks in the facing direction of the entrance.
 */
public class FreeEntranceElement extends DungeonElement {
    /**
     * The node that the entrance belongs to.
     */
    protected final NodeElement node;
    /**
     * The free entrance.
     */
    protected final Entrance entrance;
    /**
     * The placement of the entrance.
     */
    protected final Anchor placement;

    /**
     * Whether this entrance is still valid.
     * Invalidation may occur if the entrance is claimed or if a collision check finds that it is obstructed.
     */
    private boolean isValid = true;

    public FreeEntranceElement(NodeElement node, Entrance entrance, Anchor placement, BoundingBox boundingBox) {
        super(boundingBox);
        this.node = node;
        this.entrance = entrance;
        this.placement = placement;
    }

    public boolean isValid() {
        return isValid;
    }

    public void invalidate() {
        isValid = false;
    }

    @Override
    public void createPieces(Consumer<StructurePiece> consumer) {
    }

    @Override
    public boolean collides(Bounded bounded) {
        return false;
    }
}
