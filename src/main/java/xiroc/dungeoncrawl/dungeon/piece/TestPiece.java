package xiroc.dungeoncrawl.dungeon.piece;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.StructureFeatureManager;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import xiroc.dungeoncrawl.dungeon.block.provider.BlockStateProvider;
import xiroc.dungeoncrawl.dungeon.block.provider.SingleBlock;
import xiroc.dungeoncrawl.init.ModStructurePieceTypes;
import xiroc.dungeoncrawl.worldgen.WorldEditor;

import java.util.Random;

public class TestPiece extends DungeonPiece {

    public TestPiece(BoundingBox boundingBox) {
        super(ModStructurePieceTypes.TEST_PIECE, boundingBox);
        this.position = new BlockPos(boundingBox.minX(), boundingBox.minY(), boundingBox.minZ());
    }

    public TestPiece(CompoundTag nbt) {
        super(ModStructurePieceTypes.TEST_PIECE, nbt);
    }

    @Override
    public void postProcess(WorldGenLevel level, StructureFeatureManager p_73428_, ChunkGenerator chunkGenerator, Random random, BoundingBox worldGenBounds, ChunkPos p_73432_, BlockPos pos) {
        BlockPos end = new BlockPos(this.boundingBox.maxX(), this.boundingBox.maxY(), this.boundingBox.maxZ());
        BlockStateProvider stoneBrick = new SingleBlock(Blocks.STONE_BRICKS);
        WorldEditor.fillWalls(level, stoneBrick, this.position, end, worldGenBounds, random, true, true);
        WorldEditor.fill(level, SingleBlock.AIR, this.position.offset(1, 1, 1), end.offset(-1, -1, -1), worldGenBounds, random, false, true);
    }
}
