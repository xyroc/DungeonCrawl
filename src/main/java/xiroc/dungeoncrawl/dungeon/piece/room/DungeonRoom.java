package xiroc.dungeoncrawl.dungeon.piece.room;

import java.util.List;

/*
 * DungeonCrawl (C) 2019 - 2020 XYROC (XIROC1337), All Rights Reserved 
 */

import java.util.Random;

import com.google.common.collect.Lists;

import net.minecraft.block.Blocks;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.state.properties.BlockStateProperties;
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
import xiroc.dungeoncrawl.dungeon.model.DungeonModel.FeaturePosition;
import xiroc.dungeoncrawl.dungeon.model.DungeonModels;
import xiroc.dungeoncrawl.dungeon.model.RandomDungeonModel;
import xiroc.dungeoncrawl.dungeon.piece.DungeonPiece;
import xiroc.dungeoncrawl.dungeon.treasure.Treasure;
import xiroc.dungeoncrawl.theme.Theme;
import xiroc.dungeoncrawl.util.IBlockPlacementHandler;

public class DungeonRoom extends DungeonPiece {

	public DungeonRoom(TemplateManager manager, CompoundNBT p_i51343_2_) {
		super(StructurePieceTypes.ROOM, p_i51343_2_);
	}

	@Override
	public int determineModel(DungeonBuilder builder, Random rand) {
		if (rand.nextFloat() < 0.67 || connectedSides < 2) {
			return RandomDungeonModel.SPAWNER_ROOM.roll(rand).id;
		}
		return RandomDungeonModel.CORRIDOR_LINKER.roll(rand).id;
	}

	@Override
	public boolean addComponentParts(IWorld worldIn, Random randomIn, MutableBoundingBox structureBoundingBoxIn,
			ChunkPos p_74875_4_) {
		DungeonModel model = DungeonModels.MAP.get(modelID);

		if (model == null)
			return false;

		build(model, worldIn, structureBoundingBoxIn, new BlockPos(x, y, z), Theme.get(theme), Theme.getSub(subTheme),
				Treasure.Type.DEFAULT, stage, true);

		DungeonCrawl.LOGGER.debug("Room FeaturePositions: {}",
				model.featurePositions != null ? model.featurePositions.length : 0);

		if (model.featurePositions != null && model.featurePositions.length > 0) {
			int chests = 1 + randomIn.nextInt(Math.min(1, model.featurePositions.length));
			List<FeaturePosition> positions = Lists.newArrayList(model.featurePositions);
			for (int i = 0; i < chests; i++) {
				FeaturePosition position = positions.get(randomIn.nextInt(positions.size()));
				BlockPos pos = position.blockPos(x, y, z);
				if (structureBoundingBoxIn.isVecInside(pos)) {
					IBlockPlacementHandler.getHandler(Blocks.CHEST).setupBlock(worldIn,
							Blocks.CHEST.getDefaultState().with(BlockStateProperties.HORIZONTAL_FACING,
									position.facing),
							pos, randomIn, Treasure.MODEL_TREASURE_TYPES.getOrDefault(modelID, Treasure.Type.DEFAULT),
							chests, stage);
				}
				positions.remove(position);
			}
		}

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
