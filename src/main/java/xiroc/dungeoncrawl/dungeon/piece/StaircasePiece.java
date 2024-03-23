package xiroc.dungeoncrawl.dungeon.piece;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.StructureFeatureManager;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import net.minecraft.world.level.levelgen.structure.pieces.StructurePieceSerializationContext;
import xiroc.dungeoncrawl.DungeonCrawl;
import xiroc.dungeoncrawl.dungeon.theme.PrimaryTheme;
import xiroc.dungeoncrawl.dungeon.theme.SecondaryTheme;
import xiroc.dungeoncrawl.init.ModStructurePieceTypes;
import xiroc.dungeoncrawl.worldgen.WorldEditor;

import java.util.Random;

public class StaircasePiece extends BaseDungeonPiece {
    private static final String NBT_KEY_PROPERTIES = "StaircaseProperties";

    public final int height; // height of the staircase starting from the position
    public final int wallBottom; // height at which the wall surrounding the staircase starts (lowest y value that has a wall)
    public final int wallTop; // height at which the wall surrounding the staircase ends (greatest y value that was a wall)

    public StaircasePiece(int height, int wallBottom, int wallTop, BlockPos position, Rotation rotation, PrimaryTheme primaryTheme, SecondaryTheme secondaryTheme) {
        super(ModStructurePieceTypes.STAIRCASE, null, position, rotation, primaryTheme, secondaryTheme);
        this.height = height;
        this.wallBottom = wallBottom;
        this.wallTop = wallTop;
        makeBoundingBox();
    }

    public StaircasePiece(CompoundTag nbt) {
        super(ModStructurePieceTypes.STAIRCASE, nbt);
        int[] properties = nbt.getIntArray(NBT_KEY_PROPERTIES);
        if (properties.length == 3) {
            height = properties[0];
            wallBottom = properties[1];
            wallTop = properties[2];
        } else {
            DungeonCrawl.LOGGER.error("Invalid length of staircase properties array: {}", properties.length);
            height = 0;
            wallBottom = 0;
            wallTop = 0;
        }
        makeBoundingBox();
    }

    @Override
    public void postProcess(WorldGenLevel level, StructureFeatureManager p_192638_, ChunkGenerator p_192639_, Random random, BoundingBox worldGenBounds, ChunkPos p_192642_, BlockPos p_192643_) {
        for (int i = 0; i < height; ++i) {
            BlockPos center = position.above(i);
            WorldEditor.placeSpiralStairStep(level, primaryTheme.pillar(), primaryTheme.stairs(), center, worldGenBounds, random, true);
        }
        if (wallTop >= wallBottom) {
            WorldEditor.fillRing(level, primaryTheme.masonry(), position.atY(wallBottom), 2, 1, wallTop - wallBottom + 1, worldGenBounds, random, true, true);
        }
    }

    @Override
    protected void addAdditionalSaveData(StructurePieceSerializationContext context, CompoundTag nbt) {
        super.addAdditionalSaveData(context, nbt);
        final int[] properties = {height, wallBottom, wallTop};
        nbt.putIntArray(NBT_KEY_PROPERTIES, properties);
    }

    public void makeBoundingBox() {
        this.boundingBox = new BoundingBox(
                position.getX() - 2, position.getY(), position.getZ() - 2,
                position.getX() + 2, position.getY() + height - 1, position.getZ() + 2);
    }
}
