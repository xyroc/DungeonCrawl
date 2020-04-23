package xiroc.dungeoncrawl.dungeon.piece;

/*
 * DungeonCrawl (C) 2019 - 2020 XYROC (XIROC1337), All Rights Reserved 
 */

import java.util.Random;

import net.minecraft.block.Blocks;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.MutableBoundingBox;
import net.minecraft.world.IWorld;
import net.minecraft.world.gen.feature.template.TemplateManager;
import xiroc.dungeoncrawl.dungeon.DungeonBuilder;
import xiroc.dungeoncrawl.dungeon.StructurePieceTypes;
import xiroc.dungeoncrawl.dungeon.model.DungeonModel;
import xiroc.dungeoncrawl.dungeon.model.DungeonModels;
import xiroc.dungeoncrawl.dungeon.treasure.Treasure;
import xiroc.dungeoncrawl.theme.Theme;

public class DungeonStairs extends DungeonPiece {

	public int stairType; // 0: staircase, 1: bottom stairs, 2: top stairs

	public DungeonStairs(TemplateManager manager, CompoundNBT p_i51343_2_) {
		super(StructurePieceTypes.STAIRS, p_i51343_2_);
		this.stairType = p_i51343_2_.getInt("stairType");
	}
	
	@Override
	public int determineModel(DungeonBuilder builder, Random rand) {
		return DungeonModels.STAIRCASE.id;
	}

	@Override
	public boolean addComponentParts(IWorld worldIn, Random randomIn, MutableBoundingBox structureBoundingBoxIn,
			ChunkPos p_74875_4_) {
		switch (stairType) {
		case 0: {
			DungeonModel model = DungeonModels.STAIRCASE;
			if (model == null)
				return false;
			Theme buildTheme = Theme.get(theme);
			build(model, worldIn, structureBoundingBoxIn, new BlockPos(x, y, z), buildTheme, Theme.getSub(subTheme),
					Treasure.Type.DEFAULT, stage, true);
			return true;
		}
		case 1: {
			DungeonModel model = DungeonModels.STAIRS_BOTTOM;
			if (model == null)
				return false;
			build(model, worldIn, structureBoundingBoxIn, new BlockPos(x, y, z), Theme.get(theme),
					Theme.getSub(subTheme), Treasure.Type.DEFAULT, stage, false);
			addWalls(this, worldIn, structureBoundingBoxIn, Theme.get(theme));
			return true;
		}
		case 2: {
			DungeonModel model = DungeonModels.STAIRS_TOP;
			if (model == null)
				return false;
			build(model, worldIn, structureBoundingBoxIn, new BlockPos(x, y, z), Theme.get(theme),
					Theme.getSub(subTheme), Treasure.Type.DEFAULT, stage, false);
			return true;
		}
		default:
			return true;
		}

	}
	
	@Override
	public void setupBoundingBox() {
		this.boundingBox = new MutableBoundingBox(x, y, z, x + 7, y + 7, z + 7);
	}

	public void addWalls(DungeonPiece piece, IWorld world, MutableBoundingBox boundsIn, Theme theme) {
		if (!piece.sides[0])
			for (int x = 2; x < 6; x++)
				for (int y = 2; y < 6; y++)
					piece.setBlockState(theme.solid.get(), world, boundsIn, null, piece.x + x, piece.y + y, piece.z,
							this.theme, 0, true);
		else
			for (int x = 2; x < 6; x++)
				for (int y = 2; y < 6; y++)
					if (!world.getBlockState(new BlockPos(piece.x + x, piece.y + y, piece.z)).isSolid())
						piece.setBlockState(Blocks.IRON_BARS.getDefaultState(), world, boundsIn, null, piece.x + x,
								piece.y + y, piece.z, this.theme, 0, true);
		if (!piece.sides[1])
			for (int z = 2; z < 6; z++)
				for (int y = 2; y < 6; y++)
					piece.setBlockState(theme.solid.get(), world, boundsIn, null, piece.x + 7, piece.y + y, piece.z + z,
							this.theme, 0, true);
		else
			for (int z = 2; z < 6; z++)
				for (int y = 2; y < 6; y++)
					if (!world.getBlockState(new BlockPos(piece.x + 7, piece.y + y, piece.z + z)).isSolid())
						piece.setBlockState(Blocks.IRON_BARS.getDefaultState(), world, boundsIn, null, piece.x + 7,
								piece.y + y, piece.z + z, this.theme, 0, true);
		if (!piece.sides[2])
			for (int x = 2; x < 6; x++)
				for (int y = 2; y < 6; y++)
					piece.setBlockState(theme.solid.get(), world, boundsIn, null, piece.x + x, piece.y + y, piece.z + 7,
							this.theme, 0, true);
		else
			for (int x = 2; x < 6; x++)
				for (int y = 2; y < 6; y++)
					if (!world.getBlockState(new BlockPos(piece.x + x, piece.y + y, piece.z + 7)).isSolid())
						piece.setBlockState(Blocks.IRON_BARS.getDefaultState(), world, boundsIn, null, piece.x + x,
								piece.y + y, piece.z + 7, this.theme, 0, true);
		if (!piece.sides[3])
			for (int z = 2; z < 6; z++)
				for (int y = 2; y < 6; y++)
					piece.setBlockState(theme.solid.get(), world, boundsIn, null, piece.x, piece.y + y, piece.z + z,
							this.theme, 0, true);
		else
			for (int z = 2; z < 6; z++)
				for (int y = 2; y < 6; y++)
					if (!world.getBlockState(new BlockPos(piece.x, piece.y + y, piece.z + z)).isSolid())
						piece.setBlockState(Blocks.IRON_BARS.getDefaultState(), world, boundsIn, null, piece.x,
								piece.y + y, piece.z + z, this.theme, 0, true);
	}

	@Override
	public boolean canConnect(Direction side) {
		return stairType != 0;
	}

	public DungeonStairs bottom() {
		this.stairType = 1;
		return this;
	}

	public DungeonStairs top() {
		this.stairType = 2;
		return this;
	}

	@Override
	public void readAdditional(CompoundNBT tagCompound) {
		super.readAdditional(tagCompound);
		tagCompound.putInt("stairType", stairType);
	}

	@Override
	public int getType() {
		return 1;
	}

}