package xiroc.dungeoncrawl.dungeon.piece;

/*
 * DungeonCrawl (C) 2019 - 2020 XYROC (XIROC1337), All Rights Reserved 
 */

import java.util.Random;

import net.minecraft.block.BlockState;
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
import xiroc.dungeoncrawl.dungeon.StructurePieceTypes;
import xiroc.dungeoncrawl.dungeon.model.DungeonModel;
import xiroc.dungeoncrawl.dungeon.model.DungeonModelBlock;
import xiroc.dungeoncrawl.dungeon.model.DungeonModels;
import xiroc.dungeoncrawl.dungeon.treasure.Treasure;
import xiroc.dungeoncrawl.theme.Theme;
import xiroc.dungeoncrawl.theme.Theme.SubTheme;

public class DungeonPart extends DungeonPiece {

	public boolean walls;
	private int startX, startY, startZ, width, height, length;
	public int treasureType;

	public DungeonPart(TemplateManager manager, CompoundNBT p_i51343_2_) {
		super(StructurePieceTypes.PART, p_i51343_2_);
		startX = p_i51343_2_.getInt("startX");
		startY = p_i51343_2_.getInt("startY");
		startZ = p_i51343_2_.getInt("startZ");
		width = p_i51343_2_.getInt("width");
		height = p_i51343_2_.getInt("height");
		length = p_i51343_2_.getInt("length");
		treasureType = p_i51343_2_.getInt("treasureType");
		walls = p_i51343_2_.getBoolean("walls");
	}

	public void set(int modelID, int startX, int startY, int startZ, int width, int height, int length) {
		this.modelID = modelID;
		this.startX = startX;
		this.startY = startY;
		this.startZ = startZ;
		this.width = width;
		this.height = height;
		this.length = length;
		setupBoundingBox();
		if (this.treasureType == 0)
			this.treasureType = Treasure.Type
					.toInt(Treasure.LARGE_ROOM_TREASURE_TYPES.getOrDefault(modelID, Treasure.Type.DEFAULT));
	}

	public void adjustSize() {
		DungeonModel model = DungeonModels.MAP.get(modelID);
		if (model == null) {
			DungeonCrawl.LOGGER.warn("Failed to adjust the size of a dungeon part. ID: {}", modelID);
			return;
		}
		width = startX + width > model.width ? width - (startX + width - model.width) : width;
		height = startY + height > model.height ? height - (startY + height - model.height) : height;
		length = startZ + length > model.length ? length - (startZ + length - model.length) : length;
		setupBoundingBox();
	}

	@Override
	public int getType() {
		return 5;
	}

	@Override
	public void setupBoundingBox() {
		this.boundingBox = new MutableBoundingBox(x, y, z, x + width - 1, y + height - 1, z + length - 1);
	}

	@Override
	public int determineModel(Random rand) {
		return 0;
	}

	@Override
	public boolean addComponentParts(IWorld worldIn, Random randomIn, MutableBoundingBox structureBoundingBoxIn,
			ChunkPos p_74875_4_) {
		this.adjustSize();
		DungeonModel model = DungeonModels.MAP.get(modelID);
		BlockPos pos = new BlockPos(x, y, z);
		Treasure.Type type = Treasure.Type.fromInt(treasureType);

		Theme buildTheme = Theme.get(theme);
		SubTheme sub = Theme.getSub(subTheme);
		if (rotation == Rotation.NONE) {
			for (int x = startX; x < startX + width; x++) {
				for (int y = startY; y < startY + height; y++) {
					for (int z = startZ; z < startZ + length; z++) {
						BlockState state;
						if (model.model[x][y][z] == null)
							state = Blocks.AIR.getDefaultState();
						else
							state = DungeonModelBlock.getBlockState(model.model[x][y][z], buildTheme, sub,
									worldIn.getRandom(), stage);
						if (state == null)
							continue;
						setBlockState(state, worldIn, structureBoundingBoxIn, type, pos.getX() + x - startX,
								pos.getY() + y - startY, pos.getZ() + z - startZ, theme, stage, true);
					}
				}
			}
		} else {
			buildRotatedPart(model, worldIn, structureBoundingBoxIn, pos, theme, subTheme,
					Treasure.Type.fromInt(treasureType), stage, rotation, startX, startY, startZ, width, height, length,
					true);
		}
		if (walls)
			addWalls(this, worldIn, structureBoundingBoxIn, theme);
		if (Config.NO_SPAWNERS.get())
			spawnMobs(worldIn, this, width, length, new int[] { 1, 5 });
		if (theme == 3 && getBlocks(worldIn, Blocks.WATER, x, y - 1, z, 8, 8) > 5)
			addColumns(this, worldIn, structureBoundingBoxIn, 1, theme);

		return true;
	}

	@Override
	public void readAdditional(CompoundNBT tagCompound) {
		super.readAdditional(tagCompound);
		tagCompound.putInt("startX", startX);
		tagCompound.putInt("startY", startY);
		tagCompound.putInt("startZ", startZ);
		tagCompound.putInt("width", width);
		tagCompound.putInt("height", height);
		tagCompound.putInt("length", length);
		tagCompound.putInt("treasureType", treasureType);
		tagCompound.putBoolean("walls", walls);
	}

}