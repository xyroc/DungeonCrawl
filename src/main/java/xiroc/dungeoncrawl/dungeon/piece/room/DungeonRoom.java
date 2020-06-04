package xiroc.dungeoncrawl.dungeon.piece.room;

/*
 * DungeonCrawl (C) 2019 - 2020 XYROC (XIROC1337), All Rights Reserved 
 */

import java.util.Random;

import net.minecraft.block.Blocks;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.MutableBoundingBox;
import net.minecraft.util.math.Vec3i;
import net.minecraft.world.IWorld;
import net.minecraft.world.gen.feature.template.TemplateManager;
import net.minecraft.world.storage.loot.RandomValueRange;
import xiroc.dungeoncrawl.config.Config;
import xiroc.dungeoncrawl.dungeon.DungeonBuilder;
import xiroc.dungeoncrawl.dungeon.StructurePieceTypes;
import xiroc.dungeoncrawl.dungeon.model.DungeonModel;
import xiroc.dungeoncrawl.dungeon.model.DungeonModels;
import xiroc.dungeoncrawl.dungeon.model.RandomDungeonModel;
import xiroc.dungeoncrawl.dungeon.piece.DungeonPiece;
import xiroc.dungeoncrawl.dungeon.treasure.Treasure;
import xiroc.dungeoncrawl.theme.Theme;
import xiroc.dungeoncrawl.util.DirectionalBlockPos;
import xiroc.dungeoncrawl.util.IBlockPlacementHandler;

public class DungeonRoom extends DungeonPiece {

	public DirectionalBlockPos[] chests;

	public DungeonRoom(TemplateManager manager, CompoundNBT p_i51343_2_) {
		super(StructurePieceTypes.ROOM, p_i51343_2_);
		if (p_i51343_2_.contains("chests", 9)) {
			this.chests = DungeonPiece.readAllPositions(p_i51343_2_.getList("chests", 10));
		}
	}

	@Override
	public int determineModel(DungeonBuilder builder, Random rand) {
		if (rand.nextFloat() < 0.4 || connectedSides < 2) {
			return RandomDungeonModel.SPAWNER_ROOM.roll(rand).id;
		}
		return RandomDungeonModel.CORRIDOR_LINKER.roll(rand).id;
	}

	@Override
	public void customSetup(Random rand) {
		DungeonModel model = DungeonModels.MAP.get(modelID);
		if (chests == null && model.featurePositions != null && model.featurePositions.length > 0) {
			chests = new DirectionalBlockPos[new RandomValueRange(1, Math.min(2, model.featurePositions.length))
					.generateInt(rand)];

			int[] checkArray = new int[model.featurePositions.length];
			for (int i = 0; i < checkArray.length; i++) {
				checkArray[i] = i;
			}

			for (int i = 0; i < chests.length; i++) {
				int a = rand.nextInt(model.featurePositions.length);
				while (checkArray[a] != a) {
					a = checkArray[a];
				}
				checkArray[a] = (a + 1) % checkArray.length;

				Vec3i offset = model.featurePositions[a].position;
				chests[i] = new DirectionalBlockPos(x + offset.getX(), y + offset.getY(), z + offset.getZ(),
						model.featurePositions[a].facing);
			}

		}
	}

	@Override
	public boolean addComponentParts(IWorld worldIn, Random randomIn, MutableBoundingBox structureBoundingBoxIn,
			ChunkPos p_74875_4_) {
		DungeonModel model = DungeonModels.MAP.get(modelID);

		if (model == null)
			return false;

		build(model, worldIn, structureBoundingBoxIn, new BlockPos(x, y, z), Theme.get(theme), Theme.getSub(subTheme),
				Treasure.Type.DEFAULT, stage, false);

		entrances(worldIn, structureBoundingBoxIn, Theme.get(theme), model);

		if (chests != null) {
			for (int i = 0; i < chests.length; i++) {
				DirectionalBlockPos pos = chests[i];
				if (structureBoundingBoxIn.isVecInside(pos.position)) {
					IBlockPlacementHandler.getHandler(Blocks.CHEST).placeBlock(worldIn,
							Blocks.CHEST.getDefaultState().with(BlockStateProperties.HORIZONTAL_FACING, pos.direction),
							pos.position, randomIn, Treasure.Type.DEFAULT, theme, stage);
				}
			}
		}

		if (Config.NO_SPAWNERS.get())
			spawnMobs(worldIn, this, model.width, model.length, new int[] { 0 });
		return true;
	}

	@Override
	public void readAdditional(CompoundNBT tagCompound) {
		super.readAdditional(tagCompound);
		if (chests != null) {
			ListNBT list = new ListNBT();
			DungeonPiece.writeAllPositions(chests, list);
			tagCompound.put("chests", list);
		}
	}

	@Override
	public void setupBoundingBox() {
		this.boundingBox = new MutableBoundingBox(x, y, z, x + 8, y + 8, z + 8);
	}

	@Override
	public int getType() {
		return 8;
	}

}
