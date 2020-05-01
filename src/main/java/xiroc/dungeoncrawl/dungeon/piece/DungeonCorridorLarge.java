package xiroc.dungeoncrawl.dungeon.piece;

import java.util.Random;

import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.MutableBoundingBox;
import net.minecraft.world.IWorld;
import net.minecraft.world.gen.feature.template.TemplateManager;
import xiroc.dungeoncrawl.DungeonCrawl;
import xiroc.dungeoncrawl.dungeon.DungeonBuilder;
import xiroc.dungeoncrawl.dungeon.StructurePieceTypes;
import xiroc.dungeoncrawl.dungeon.model.DungeonModels;
import xiroc.dungeoncrawl.dungeon.treasure.Treasure;
import xiroc.dungeoncrawl.theme.Theme;

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
	public int determineModel(DungeonBuilder builder, Random rand) {
		if (type == 0)
			return DungeonModels.LARGE_CORRIDOR_START.id;
		else if (type == 1) {
			switch (connectedSides) {
			case 2:
				if ((sides[0] && sides[2]) || (sides[1] && sides[3]))
					return DungeonModels.LARGE_CORRIDOR_STRAIGHT.id;
				return DungeonModels.LARGE_CORRIDOR_TURN.id;
			case 3:
				return DungeonModels.LARGE_CORRIDOR_OPEN.id;
			}
			return DungeonModels.LARGE_CORRIDOR_STRAIGHT.id;
		}
		// end of the world
		return 0;
	}

	@Override
	public boolean addComponentParts(IWorld worldIn, Random randomIn, MutableBoundingBox structureBoundingBoxIn,
			ChunkPos chunkPosIn) {

		DungeonCrawl.LOGGER.debug("x: {}, y: {}, z: {}, rotation: {}", x, y, z, rotation);

		buildRotated(DungeonModels.MAP.get(modelID), worldIn, structureBoundingBoxIn, new BlockPos(x, y, z),
				Theme.get(theme), Theme.getSub(subTheme), Treasure.Type.DEFAULT, stage, rotation, false);

		return true;
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
