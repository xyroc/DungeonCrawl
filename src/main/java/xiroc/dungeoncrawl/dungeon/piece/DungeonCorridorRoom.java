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

public class DungeonCorridorRoom extends DungeonPiece {

	public DungeonCorridorRoom(TemplateManager manager, CompoundNBT p_i51343_2_) {
		super(StructurePieceTypes.CORRIDOR_ROOM, p_i51343_2_);
	}

	@Override
	public boolean addComponentParts(IWorld worldIn, Random randomIn, MutableBoundingBox structureBoundingBoxIn,
			ChunkPos p_74875_4_) {
		buildRotated(DungeonSegmentModelRegistry.CORRIDOR_ROOM, worldIn, new BlockPos(x, y - 6, z),
				Theme.get(theme), Theme.getSub(subTheme), Treasure.Type.DEFAULT, stage, getRotation(), true);
		if (theme == 3 && getBlocks(worldIn, Blocks.WATER, x, y - 7, z, 8, 8) > 5)
			addColumns(this, worldIn, 7, theme);
		return true;
	}
	
	@Override
	public int getType() {
		return 4;
	}

}