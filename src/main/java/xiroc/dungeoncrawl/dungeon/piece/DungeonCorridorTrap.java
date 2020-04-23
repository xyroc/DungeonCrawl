package xiroc.dungeoncrawl.dungeon.piece;

/*
 * DungeonCrawl (C) 2019 - 2020 XYROC (XIROC1337), All Rights Reserved 
 */

import java.util.Random;

import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.MutableBoundingBox;
import net.minecraft.world.IWorld;
import net.minecraft.world.gen.feature.template.TemplateManager;
import xiroc.dungeoncrawl.dungeon.DungeonBuilder;
import xiroc.dungeoncrawl.dungeon.StructurePieceTypes;

public class DungeonCorridorTrap extends DungeonPiece {

	public DungeonCorridorTrap(TemplateManager manager, CompoundNBT p_i51343_2_) {
		super(StructurePieceTypes.CORRIDOR_TRAP, p_i51343_2_);
	}

	@Override
	public int getType() {
		return 3;
	}

	@Override
	public int determineModel(DungeonBuilder builder, Random rand) {
		return 0;
	}

	@Override
	public boolean addComponentParts(IWorld worldIn, Random randomIn, MutableBoundingBox structureBoundingBoxIn,
			ChunkPos p_74875_4_) {
//		buildRotated(DungeonModels.CORRIDOR_TRAP, worldIn, structureBoundingBoxIn, new BlockPos(x, y, z),
//				Theme.get(theme), Theme.getSub(subTheme), Treasure.Type.DEFAULT, stage, getRotation(), true);
		return true;
	}

	@Override
	public void setupBoundingBox() {
		this.boundingBox = new MutableBoundingBox(x, y, z, x + 7, y + 7, z + 7);
	}

}