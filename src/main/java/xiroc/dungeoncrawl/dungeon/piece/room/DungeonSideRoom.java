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
import xiroc.dungeoncrawl.DungeonCrawl;
import xiroc.dungeoncrawl.config.Config;
import xiroc.dungeoncrawl.dungeon.StructurePieceTypes;
import xiroc.dungeoncrawl.dungeon.piece.DungeonPiece;
import xiroc.dungeoncrawl.dungeon.segment.DungeonSegmentModel;
import xiroc.dungeoncrawl.dungeon.segment.DungeonSegmentModelRegistry;
import xiroc.dungeoncrawl.dungeon.treasure.Treasure;
import xiroc.dungeoncrawl.theme.Theme;
import xiroc.dungeoncrawl.util.Triple;

public class DungeonSideRoom extends DungeonPiece {

	public Treasure.Type treasureType;
	public int modelID, offsetX, offsetY, offsetZ;

	public DungeonSideRoom(TemplateManager manager, CompoundNBT p_i51343_2_) {
		super(StructurePieceTypes.SIDE_ROOM, p_i51343_2_);
		modelID = p_i51343_2_.getInt("modelID");
		offsetX = p_i51343_2_.getInt("offsetX");
		offsetY = p_i51343_2_.getInt("offsetY");
		offsetZ = p_i51343_2_.getInt("offsetZ");
		treasureType = Treasure.Type.fromInt(p_i51343_2_.getInt("treasureType"));
	}

	@Override
	public boolean addComponentParts(IWorld worldIn, Random randomIn, MutableBoundingBox structureBoundingBoxIn,
			ChunkPos chunkPosIn) {
		DungeonSegmentModel model = DungeonSegmentModelRegistry.MAP.get(modelID);
		if (model != null) {
//			if (theme != 1)
//				theme = Theme.BIOME_TO_THEME_MAP
//						.getOrDefault(worldIn.getBiome(new BlockPos(x, y, z)).getRegistryName().toString(), 0);
			buildRotated(model, worldIn, new BlockPos(x + offsetX, y + offsetY, z + offsetZ), Theme.get(theme),
					Theme.getSub(subTheme), Treasure.Type.DEFAULT, stage, rotation, true);

			if (Config.NO_SPAWNERS.get())
				spawnMobs(worldIn, this, model.width, model.length, new int[] { 1 });
			return true;
		} else {
			DungeonCrawl.LOGGER.error("Side Room Model doesnt exist: {}", modelID);
			return false;
		}
	}

	public void setOffset(int x, int y, int z) {
		this.offsetX = x;
		this.offsetY = y;
		this.offsetZ = z;
	}

	public void setOffset(Triple<Integer, Integer, Integer> offset) {
		this.offsetX = offset.l;
		this.offsetY = offset.m;
		this.offsetZ = offset.r;
	}

	@Override
	public int getType() {
		return 9;
	}

	@Override
	public void readAdditional(CompoundNBT tagCompound) {
		super.readAdditional(tagCompound);
		tagCompound.putInt("modelID", modelID);
		tagCompound.putInt("offsetX", offsetX);
		tagCompound.putInt("offsetY", offsetY);
		tagCompound.putInt("offsetZ", offsetZ);
		tagCompound.putInt("treasureType", Treasure.Type.toInt(treasureType));
	}

}