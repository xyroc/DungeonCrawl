package xiroc.dungeoncrawl.dungeon.generator.staircase;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Holder;
import org.jetbrains.annotations.Nullable;
import xiroc.dungeoncrawl.dungeon.component.StaircaseComponent;
import xiroc.dungeoncrawl.dungeon.piece.DungeonPiece;
import xiroc.dungeoncrawl.dungeon.theme.PrimaryTheme;
import xiroc.dungeoncrawl.dungeon.theme.SecondaryTheme;
import xiroc.dungeoncrawl.util.Orientation;
import xiroc.dungeoncrawl.worldgen.DungeonWorldGenContext;

public class StaircasePlanner {
    /**
     * The y coordinate at which the staircase ends at the bottom.
     */
    private int staircaseBottom = 0;
    /**
     * The y coordinate at which the staircase ends at the top.
     */
    private int staircaseTop = 0;
    /**
     * The piece the staircase connects to at the top.
     */
    private DungeonPiece topPiece = null;
    /**
     * The piece the staircase connects to at the bottom.
     */
    private DungeonPiece bottomPiece = null;

    /**
     * The offset within the facings array used for this staircase.
     * Used to rotate the staircase to adhere to facing constraints.
     */
    private int rotation = 0;

    /**
     * The x coordinate of the staircase's central pillar.
     */
    private final int centerX;
    /**
     * The z coordinate of the staircase's central pillar.
     */
    private final int centerZ;

    public StaircasePlanner(int centerX, int centerZ) {
        this.centerX = centerX;
        this.centerZ = centerZ;
    }

    /**
     * Set the top of the staircase.
     *
     * @param piece       the piece the staircase connects to at the top.
     * @param topRelative the relative vertical offset from minRoomY at which the staircase begins.
     * @param constraint  if not null, the direction the staircase must be facing at the top.
     */
    public void setTop(DungeonPiece piece, int topRelative, @Nullable Direction constraint) {
        final int minRoomY = piece.getBoundingBox().minY();
        this.topPiece = piece;
        this.staircaseTop = minRoomY + topRelative;
        if (constraint != null) {
            final Direction facingWithoutRotation = StaircaseComponent.getFacingAt(staircaseTop, 0);
            this.rotation = Orientation.numberOfClockwise90DegreeRotations(facingWithoutRotation, constraint);
        }
    }

    /**
     * Set the bottom of the staircase.
     *
     * @param piece  the piece the staircase connects to at the bottom.
     * @param bottom the absolute y coordinate at which the staircase ends.
     */
    public void setBottom(DungeonPiece piece, int bottom) {
        this.staircaseBottom = bottom;
        this.bottomPiece = piece;
    }

    /**
     * Get the facing of the staircase at the specified absolute y coordinate.
     *
     * @param y the y coordinate to probe the facing at.
     * @return the staircase facing.
     */
    public Direction getFacingAt(int y) {
        return StaircaseComponent.getFacingAt(y, rotation);
    }

    /**
     * Get the position of the block in the center column at the specified height.
     *
     * @param y the height.
     * @return The position at that height.
     */
    public BlockPos getCenterAtY(int y) {
        return new BlockPos(centerX, y, centerZ);
    }

    public int getWallTop() {
        return topPiece.getBoundingBox().minY() - 1;
    }

    public int getWallBottom() {
        return bottomPiece.getBoundingBox().maxY() + 1;
    }

    public DungeonPiece make(int stage, Holder<PrimaryTheme> primaryTheme, Holder<SecondaryTheme> secondaryTheme) {
        int wallTop = getWallTop();
        int wallBottom = getWallBottom();
        if (staircaseTop > wallTop) {
            topPiece.addComponent(new StaircaseComponent(new BlockPos(centerX, wallTop + 1, centerZ), staircaseTop - wallTop, 1, 0, rotation));
        }
        if (staircaseBottom < wallBottom) {
            bottomPiece.addComponent(new StaircaseComponent(new BlockPos(centerX, staircaseBottom, centerZ), wallBottom - staircaseBottom, 1, 0, rotation));
        }
        BlockPos position = new BlockPos(centerX, wallBottom, centerZ);
        int height = wallTop - wallBottom + 1;
        StaircaseComponent staircase = new StaircaseComponent(position, height, wallBottom, wallTop, rotation);
        return new DungeonPiece(staircase, new DungeonWorldGenContext(primaryTheme, secondaryTheme, Integer.MIN_VALUE, stage));
    }
}
