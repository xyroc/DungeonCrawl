package xiroc.dungeoncrawl.dungeon.generator;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Vec3i;
import xiroc.dungeoncrawl.datapack.DatapackRegistries;
import xiroc.dungeoncrawl.dungeon.blueprint.anchor.Anchor;
import xiroc.dungeoncrawl.dungeon.component.StaircaseComponent;
import xiroc.dungeoncrawl.dungeon.piece.DungeonPiece;
import xiroc.dungeoncrawl.dungeon.theme.BuiltinThemes;
import xiroc.dungeoncrawl.dungeon.theme.PrimaryTheme;
import xiroc.dungeoncrawl.dungeon.theme.SecondaryTheme;

public class StaircaseBuilder {
    private int staircaseBottom = 0;
    private int staircaseTop = 0;
    private int wallBottom = 0;
    private int wallTop = 0;

    private final int x;
    private final int z;

    public StaircaseBuilder(int x, int z) {
        this.x = x;
        this.z = z;
    }

    public StaircaseBuilder(Anchor anchor) {
        this.x = anchor.position().getX();
        this.z = anchor.position().getZ();
    }

    public void top(Vec3i top, int minRoomY) {
        this.staircaseTop = minRoomY + top.getY() - 1;
        this.wallTop = minRoomY - 1;
    }

    public void bottom(Vec3i bottom, int minRoomY, int maxRoomY) {
        this.staircaseBottom = minRoomY + bottom.getY() + 1;
        this.wallBottom = maxRoomY + 1;
    }

    public BlockPos atY(int y) {
        return new BlockPos(x, y, z);
    }

    public BlockPos wallTop() {
        return new BlockPos(x, wallTop, z);
    }

    public DungeonPiece make() {
        BlockPos position = new BlockPos(x, staircaseBottom, z);
        PrimaryTheme primaryTheme = DatapackRegistries.PRIMARY_THEME.get(BuiltinThemes.DEFAULT);
        SecondaryTheme secondaryTheme = DatapackRegistries.SECONDARY_THEME.get(BuiltinThemes.DEFAULT);
        int height = staircaseTop - staircaseBottom + 1;
        StaircaseComponent staircase = new StaircaseComponent(position, height, wallBottom, wallTop);
        return new DungeonPiece(staircase, primaryTheme, secondaryTheme, 0);
    }
}
