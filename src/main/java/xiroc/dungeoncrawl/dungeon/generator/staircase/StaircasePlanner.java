package xiroc.dungeoncrawl.dungeon.generator.staircase;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import org.jetbrains.annotations.Nullable;
import xiroc.dungeoncrawl.datapack.registry.Delegate;
import xiroc.dungeoncrawl.dungeon.component.StaircaseComponent;
import xiroc.dungeoncrawl.dungeon.piece.DungeonPiece;
import xiroc.dungeoncrawl.dungeon.theme.PrimaryTheme;
import xiroc.dungeoncrawl.dungeon.theme.SecondaryTheme;
import xiroc.dungeoncrawl.util.Orientation;

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
     * The y coordinate at which the walls surrounding the staircase end at the bottom.
     */
    private int wallBottom = 0;
    /**
     * The y coordinate at which the walls surrounding the staircase end at the top.
     */
    private int wallTop = 0;

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
     * @param topRelative the relative vertical offset from minRoomY at which the staircase begins.
     * @param minRoomY    the lowest absolute y coordinate within the room where the staircase should not be encased by walls.
     * @param constraint  if not null, the direction the staircase must be facing at the top.
     */
    public void setTop(int topRelative, int minRoomY, @Nullable Direction constraint) {
        this.staircaseTop = minRoomY + topRelative;
        this.wallTop = minRoomY - 1;
        if (constraint != null) {
            final Direction facingWithoutRotation = StaircaseComponent.getFacingAt(staircaseTop, 0);
            this.rotation = Orientation.numberOfClockwise90DegreeRotations(facingWithoutRotation, constraint);
        }
    }

    /**
     * Set the bottom of the staircase.
     *
     * @param bottom   the absolute y coordinate at which the staircase ends.
     * @param maxRoomY the highest absolute y coordinate at which the staircase should not be encased by walls.
     */
    public void setBottom(int bottom, int maxRoomY) {
        this.staircaseBottom = bottom;
        this.wallBottom = maxRoomY + 1;
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
        return wallTop;
    }

    public DungeonPiece make(int stage, Delegate<PrimaryTheme> primaryTheme, Delegate<SecondaryTheme> secondaryTheme) {
        BlockPos position = new BlockPos(centerX, staircaseBottom, centerZ);
        int height = staircaseTop - staircaseBottom + 1;
        StaircaseComponent staircase = new StaircaseComponent(position, height, wallBottom, wallTop, rotation);
        return new DungeonPiece(staircase, primaryTheme, secondaryTheme, stage);
    }
}
