package xiroc.dungeoncrawl.dungeon.piece;

import java.util.Random;

import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.MutableBoundingBox;
import net.minecraft.world.IWorld;
import net.minecraft.world.gen.feature.structure.IStructurePieceType;

public class DungeonCorridorLarge extends DungeonPiece {

	public DungeonCorridorLarge(IStructurePieceType p_i51343_1_, CompoundNBT p_i51343_2_) {
		super(p_i51343_1_, p_i51343_2_);
	}

	@Override
	public boolean addComponentParts(IWorld worldIn, Random randomIn, MutableBoundingBox structureBoundingBoxIn,
			ChunkPos chunkPosIn) {
		return false;
	}
	
	@Override
	public int getType() {
		return 7;
	}

}
