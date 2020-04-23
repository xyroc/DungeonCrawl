package xiroc.dungeoncrawl.dungeon.piece;

/*
 * DungeonCrawl (C) 2019 - 2020 XYROC (XIROC1337), All Rights Reserved 
 */

import java.util.Random;

import net.minecraft.block.Blocks;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.Rotation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.MutableBoundingBox;
import net.minecraft.world.IWorld;
import net.minecraft.world.gen.feature.template.TemplateManager;
import xiroc.dungeoncrawl.DungeonCrawl;
import xiroc.dungeoncrawl.config.Config;
import xiroc.dungeoncrawl.dungeon.DungeonBuilder;
import xiroc.dungeoncrawl.dungeon.StructurePieceTypes;
import xiroc.dungeoncrawl.dungeon.model.DungeonModel;
import xiroc.dungeoncrawl.dungeon.model.DungeonModels;
import xiroc.dungeoncrawl.dungeon.treasure.Treasure;
import xiroc.dungeoncrawl.theme.Theme;

public class DungeonCorridor extends DungeonPiece {

	public int specialType;

	public DungeonCorridor(TemplateManager manager, CompoundNBT p_i51343_2_) {
		super(StructurePieceTypes.CORRIDOR, p_i51343_2_);
		specialType = p_i51343_2_.getInt("specialType");
	}

	@Override
	public int determineModel(DungeonBuilder builder, Random rand) {
		return DungeonBuilder.getModel(this, rand).id;
	}

	@Override
	public boolean addComponentParts(IWorld worldIn, Random randomIn, MutableBoundingBox structureBoundingBoxIn,
			ChunkPos p_74875_4_) {

		DungeonModel model = DungeonModels.MAP.get(modelID);

		if (model == null) {
			DungeonCrawl.LOGGER.warn("Corridor model is null.");
			return false;
		}

//		int startX = Math.max(x, structureBoundingBoxIn.minX) - x,
//				startZ = Math.max(z, structureBoundingBoxIn.minZ) - z;

		boolean ew = rotation == Rotation.NONE || rotation == Rotation.CLOCKWISE_180;

//		buildRotatedPart(model, worldIn, structureBoundingBoxIn,
//				new BlockPos(Math.max(x, structureBoundingBoxIn.minX), y, Math.max(z, structureBoundingBoxIn.minZ)),
//				theme, subTheme, Treasure.Type.DEFAULT, stage, getRotation(), startX, 0, startZ,
//				model.width - startX, model.height, model.length - startZ);

		buildRotated(model, worldIn, structureBoundingBoxIn,
				new BlockPos(ew ? x : x + (9 - model.length) / 2, y, ew ? z + (9 - model.length) / 2 : z),
				Theme.get(theme), Theme.getSub(subTheme), Treasure.Type.DEFAULT, stage, rotation, false);

		if (Config.NO_SPAWNERS.get())
			spawnMobs(worldIn, this, model.width, model.length, new int[] { 1 });

		if (theme == 3
				&& ((connectedSides == 2 && (!isStraight() || randomIn.nextDouble() < 0.2)) || connectedSides > 2)
				&& getBlocks(worldIn, Blocks.WATER, x, y - 1, z, 8, 8) > 5)
			addColumns(this, worldIn, structureBoundingBoxIn, 1, theme);
		return true;
	}

	@Override
	public void setupBoundingBox() {
		boolean ew = rotation == Rotation.NONE || rotation == Rotation.CLOCKWISE_180;
		this.boundingBox = new MutableBoundingBox(x, y, z, x + (ew ? 8 : 6), y + 8, z + (ew ? 6 : 8));
	}

	@Override
	public int getType() {
		return 0;
	}

	@Override
	public void readAdditional(CompoundNBT tagCompound) {
		super.readAdditional(tagCompound);
		tagCompound.putInt("specialType", specialType);
	}

	public boolean isStraight() {
		return sides[0] && sides[2] || sides[1] && sides[3];
	}

}
