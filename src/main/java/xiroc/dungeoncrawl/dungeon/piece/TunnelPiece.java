package xiroc.dungeoncrawl.dungeon.piece;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.StructureFeatureManager;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import net.minecraft.world.level.levelgen.structure.pieces.StructurePieceSerializationContext;
import xiroc.dungeoncrawl.dungeon.block.provider.BlockStateProvider;
import xiroc.dungeoncrawl.dungeon.block.provider.SingleBlock;
import xiroc.dungeoncrawl.dungeon.theme.PrimaryTheme;
import xiroc.dungeoncrawl.dungeon.theme.SecondaryTheme;
import xiroc.dungeoncrawl.init.ModStructurePieceTypes;
import xiroc.dungeoncrawl.worldgen.WorldEditor;

import java.util.Random;

public class TunnelPiece extends BaseDungeonPiece {
    private static final String NBT_KEY_DIRECTION = "Direction";

    private final Direction direction;

    public TunnelPiece(BoundingBox boundingBox, Direction direction, PrimaryTheme primaryTheme, SecondaryTheme secondaryTheme) {
        super(ModStructurePieceTypes.TUNNEL, boundingBox, new BlockPos(boundingBox.minX(), boundingBox.minY(), boundingBox.minZ()), Rotation.NONE, primaryTheme, secondaryTheme);
        this.direction = direction;
    }

    public TunnelPiece(CompoundTag nbt) {
        super(ModStructurePieceTypes.TUNNEL, nbt);
        this.direction = Direction.from3DDataValue(nbt.getInt(NBT_KEY_DIRECTION));
    }

    @Override
    protected void addAdditionalSaveData(StructurePieceSerializationContext context, CompoundTag nbt) {
        super.addAdditionalSaveData(context, nbt);
        nbt.putInt(NBT_KEY_DIRECTION, direction.get3DDataValue());
    }

    @Override
    public void postProcess(WorldGenLevel level, StructureFeatureManager p_73428_, ChunkGenerator chunkGenerator, Random random, BoundingBox worldGenBounds, ChunkPos p_73432_, BlockPos pos) {
        BlockPos end = new BlockPos(this.boundingBox.maxX(), this.boundingBox.maxY(), this.boundingBox.maxZ());
        WorldEditor.fillWalls(level, primaryTheme.masonry(), this.position, end, worldGenBounds, random, true, true);
        WorldEditor.fill(level, SingleBlock.AIR, this.position.offset(1, 1, 1), end.offset(-1, -1, -1), worldGenBounds, random, false, true);
    }
}
