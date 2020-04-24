package xiroc.dungeoncrawl.dungeon.piece.room;

/*
 * DungeonCrawl (C) 2019 - 2020 XYROC (XIROC1337), All Rights Reserved 
 */

import java.util.Random;

import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.MutableBoundingBox;
import net.minecraft.world.IWorld;
import net.minecraft.world.gen.feature.template.TemplateManager;
import xiroc.dungeoncrawl.config.Config;
import xiroc.dungeoncrawl.dungeon.DungeonBuilder;
import xiroc.dungeoncrawl.dungeon.StructurePieceTypes;
import xiroc.dungeoncrawl.dungeon.model.DungeonModel;
import xiroc.dungeoncrawl.dungeon.model.DungeonModels;
import xiroc.dungeoncrawl.dungeon.piece.DungeonPiece;
import xiroc.dungeoncrawl.dungeon.treasure.Treasure;
import xiroc.dungeoncrawl.theme.Theme;

public class DungeonRoom extends DungeonPiece {

	public DungeonRoom(TemplateManager manager, CompoundNBT p_i51343_2_) {
		super(StructurePieceTypes.ROOM, p_i51343_2_);
	}

	@Override
	public int determineModel(DungeonBuilder builder, Random rand) {
		return DungeonModels.CORRIDOR_LINKERS[rand.nextInt(DungeonModels.CORRIDOR_LINKERS.length)].id;
	}

	@Override
	public boolean addComponentParts(IWorld worldIn, Random randomIn, MutableBoundingBox structureBoundingBoxIn,
			ChunkPos p_74875_4_) {
		DungeonModel model = DungeonModels.MAP.get(modelID);
		
		if (model == null)
			return false;
		
		build(model, worldIn, structureBoundingBoxIn, new BlockPos(x, y, z), Theme.get(theme), Theme.getSub(subTheme),
				Treasure.Type.DEFAULT, stage, true);
		
		entrances(worldIn, structureBoundingBoxIn, Theme.get(theme), model);

		if (Config.NO_SPAWNERS.get())
			spawnMobs(worldIn, this, model.width, model.length, new int[] { 0 });
		return false;
	}

	@Override
	public void setupBoundingBox() {
		this.boundingBox = new MutableBoundingBox(x, y, z, x + 7, y + 7, z + 7);
	}

	@Override
	public int getType() {
		return 8;
	}
}
