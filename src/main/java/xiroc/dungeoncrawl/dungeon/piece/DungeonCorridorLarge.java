package xiroc.dungeoncrawl.dungeon.piece;

import java.util.Random;

import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.MutableBoundingBox;
import net.minecraft.world.IWorld;
import net.minecraft.world.gen.feature.template.TemplateManager;
import xiroc.dungeoncrawl.dungeon.StructurePieceTypes;

public class DungeonCorridorLarge extends DungeonPiece {

	public int type; // 0: start/end 1: middle

	public DungeonCorridorLarge(DungeonCorridor corridor, int type) {
		super(StructurePieceTypes.LARGE_CORRIDOR, DEFAULT_NBT);
		this.sides = corridor.sides;
		this.connectedSides = corridor.connectedSides;
		this.rotation = corridor.rotation;
		this.stage = corridor.stage;
		this.posX = corridor.posX;
		this.posZ = corridor.posZ;
		this.type = type;
	}

	public DungeonCorridorLarge(TemplateManager manager, CompoundNBT nbt) {
		super(StructurePieceTypes.LARGE_CORRIDOR, nbt);
		this.type = nbt.getInt("type");
	}

	@Override
	public int determineModel(Random rand) {
		return type == 0 ? 0 : 0;
	}

	@Override
	public boolean addComponentParts(IWorld worldIn, Random randomIn, MutableBoundingBox structureBoundingBoxIn,
			ChunkPos chunkPosIn) {
		return false;
	}

	@Override
	public void setupBoundingBox() {
		this.boundingBox = new MutableBoundingBox(x, y, z, x + 8, y + 8, z + 8);
	}

	@Override
	public int getType() {
		return 7;
	}

	@Override
	public void readAdditional(CompoundNBT tagCompound) {
		super.readAdditional(tagCompound);
		tagCompound.putInt("type", type);
	}

}
