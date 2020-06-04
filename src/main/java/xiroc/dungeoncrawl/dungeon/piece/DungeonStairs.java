package xiroc.dungeoncrawl.dungeon.piece;

/*
 * DungeonCrawl (C) 2019 - 2020 XYROC (XIROC1337), All Rights Reserved 
 */

import java.util.Random;

import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.MutableBoundingBox;
import net.minecraft.world.IWorld;
import net.minecraft.world.gen.feature.template.TemplateManager;
import xiroc.dungeoncrawl.DungeonCrawl;
import xiroc.dungeoncrawl.dungeon.DungeonBuilder;
import xiroc.dungeoncrawl.dungeon.StructurePieceTypes;
import xiroc.dungeoncrawl.dungeon.model.DungeonModel;
import xiroc.dungeoncrawl.dungeon.model.DungeonModels;
import xiroc.dungeoncrawl.dungeon.treasure.Treasure;
import xiroc.dungeoncrawl.theme.Theme;

public class DungeonStairs extends DungeonPiece {

	public int stairType; // 0: staircase, 1: bottom stairs, 2: top stairs

	public DungeonStairs(TemplateManager manager, CompoundNBT p_i51343_2_) {
		super(StructurePieceTypes.STAIRS, p_i51343_2_, false);
		this.stairType = p_i51343_2_.getInt("stairType");
		setupBoundingBox();
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
			Theme buildTheme = Theme.get(theme);
			build(model, worldIn, structureBoundingBoxIn, new BlockPos(x, y, z), buildTheme, Theme.getSub(subTheme),
					Treasure.Type.DEFAULT, stage, true);
			return true;
		}
		case 1: {
			DungeonModel model = stage > 0 ? DungeonModels.STAIRS_BOTTOM_2 : DungeonModels.STAIRS_BOTTOM;
			build(model, worldIn, structureBoundingBoxIn, new BlockPos(x, y, z), Theme.get(theme),
					Theme.getSub(subTheme), Treasure.Type.DEFAULT, stage, false);
			DungeonCrawl.LOGGER.debug("Stairs bottom Boundingbox: {}, {}, {} -> {}, {}, {}", boundingBox.minX, boundingBox.minY,
					boundingBox.minZ, boundingBox.maxX, boundingBox.maxY, boundingBox.maxZ);
			ironBars(worldIn, structureBoundingBoxIn);
			return true;
		}
		case 2: {
			DungeonModel model = DungeonModels.STAIRS_TOP;
			build(model, worldIn, structureBoundingBoxIn, new BlockPos(x, y, z), Theme.get(theme),
					Theme.getSub(subTheme), Treasure.Type.DEFAULT, stage, false);
			DungeonCrawl.LOGGER.debug("Stairs top Boundingbox: {}, {}, {} -> {}, {}, {}", boundingBox.minX, boundingBox.minY,
					boundingBox.minZ, boundingBox.maxX, boundingBox.maxY, boundingBox.maxZ);
			entrances(worldIn, structureBoundingBoxIn, Theme.get(theme), model);
			return true;
		}
		default:
			return true;
		}

	}

	public void ironBars(IWorld world, MutableBoundingBox bounds) {
		BlockState ironBars = Blocks.IRON_BARS.getDefaultState();

		if (sides[0]) {
			for (int x0 = 3; x0 < 6; x0++)
				if (world.getBlockState(new BlockPos(x + x0, y, z)).isSolid())
					for (int y0 = 1; y0 < 4; y0++)
						setBlockState(world, ironBars, x + x0, y + y0, z, bounds);
		}

		if (sides[1]) {
			for (int z0 = 3; z0 < 6; z0++)
				if (world.getBlockState(new BlockPos(x + 8, y, z + z0)).isSolid())
					for (int y0 = 1; y0 < 4; y0++)
						setBlockState(world, ironBars, x + 8, y + y0, z + z0, bounds);
		}

		if (sides[2]) {
			for (int x0 = 3; x0 < 6; x0++)
				if (world.getBlockState(new BlockPos(x + x0, y, z + 8)).isSolid())
					for (int y0 = 1; y0 < 4; y0++)
						setBlockState(world, ironBars, x + x0, y + y0, z + 8, bounds);
		}

		if (sides[3]) {
			for (int z0 = 3; z0 < 6; z0++)
				if (world.getBlockState(new BlockPos(x, y, z + z0)).isSolid())
					for (int y0 = 1; y0 < 4; y0++)
						setBlockState(world, ironBars, x, y + y0, z + z0, bounds);
		}
	}

	@Override
	public void setupBoundingBox() {
		if (stairType == 0) {
			this.boundingBox = new MutableBoundingBox(x, y, z, x + 4, y + 8, z + 4);
		} else {
			this.boundingBox = new MutableBoundingBox(x, y, z, x + 8, y + 8, z + 8);
		}
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