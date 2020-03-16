package xiroc.dungeoncrawl.dungeon.piece.room;

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
import xiroc.dungeoncrawl.config.Config;
import xiroc.dungeoncrawl.dungeon.StructurePieceTypes;
import xiroc.dungeoncrawl.dungeon.piece.DungeonPiece;
import xiroc.dungeoncrawl.dungeon.segment.DungeonSegmentModel;
import xiroc.dungeoncrawl.dungeon.segment.DungeonSegmentModelRegistry;
import xiroc.dungeoncrawl.dungeon.treasure.Treasure;
import xiroc.dungeoncrawl.theme.Theme;

public class DungeonRoom extends DungeonPiece {

	public DungeonRoom(TemplateManager manager, CompoundNBT p_i51343_2_) {
		super(StructurePieceTypes.ROOM, p_i51343_2_);
	}

	@Override
	public boolean addComponentParts(IWorld worldIn, Random randomIn, MutableBoundingBox structureBoundingBoxIn,
			ChunkPos p_74875_4_) {
		DungeonSegmentModel model = DungeonSegmentModelRegistry.ROOM;
		if (model == null)
			return false;
//		if (theme != 1)
//			theme = Theme.BIOME_TO_THEME_MAP
//					.getOrDefault(worldIn.getBiome(new BlockPos(x, y, z)).getRegistryName().toString(), 0);
		build(model, worldIn, new BlockPos(x, y, z), Theme.get(theme), Theme.getSub(subTheme), Treasure.Type.DEFAULT,
				stage, true);
		addWalls(this, worldIn, theme);

		if (Config.NO_SPAWNERS.get())
			spawnMobs(worldIn, this, model.width, model.length, new int[] { 1 });

		if (theme == 3 && getBlocks(worldIn, Blocks.WATER, x, y - 1, z, 8, 8) > 5)
			addColumns(this, worldIn, 1, theme);
		return false;
	}

	@Override
	public int getType() {
		return 8;
	}
}
