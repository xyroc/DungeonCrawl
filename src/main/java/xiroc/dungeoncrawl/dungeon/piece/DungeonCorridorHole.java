package xiroc.dungeoncrawl.dungeon.piece;

/*
 * DungeonCrawl (C) 2019 - 2020 XYROC (XIROC1337), All Rights Reserved 
 */

import java.util.Random;

import net.minecraft.block.Blocks;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.MutableBoundingBox;
import net.minecraft.world.IWorld;
import net.minecraft.world.gen.feature.template.TemplateManager;
import xiroc.dungeoncrawl.dungeon.StructurePieceTypes;
import xiroc.dungeoncrawl.dungeon.segment.DungeonSegmentModelRegistry;
import xiroc.dungeoncrawl.dungeon.treasure.Treasure;
import xiroc.dungeoncrawl.theme.Theme;

public class DungeonCorridorHole extends DungeonPiece {

	public boolean lava;

	public DungeonCorridorHole(TemplateManager p_i51343_1_, CompoundNBT p_i51343_2_) {
		super(StructurePieceTypes.HOLE, p_i51343_2_);
		lava = p_i51343_2_.getBoolean("lava");
	}

	@Override
	public boolean addComponentParts(IWorld worldIn, Random randomIn, MutableBoundingBox structureBoundingBoxIn,
			ChunkPos p_74875_4_) {
		build(lava ? DungeonSegmentModelRegistry.HOLE_LAVA : DungeonSegmentModelRegistry.HOLE, worldIn,
				new BlockPos(x, y - 15, z), Theme.get(theme), Theme.getSub(subTheme), Treasure.Type.DEFAULT, stage, true);
		addWalls(this, worldIn, theme);
		if (theme == 3 && getBlocks(worldIn, Blocks.WATER, x, y - 16, z, 8, 8) > 5)
			addColumns(this, worldIn, 16, theme);
		return false;
	}

	@Override
	public void readAdditional(CompoundNBT tagCompound) {
		super.readAdditional(tagCompound);
		tagCompound.putBoolean("lava", lava);
	}
	
	@Override
	public int getType() {
		return 2;
	}

}