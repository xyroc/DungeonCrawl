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
import xiroc.dungeoncrawl.config.Config;
import xiroc.dungeoncrawl.dungeon.DungeonBuilder;
import xiroc.dungeoncrawl.dungeon.StructurePieceTypes;
import xiroc.dungeoncrawl.dungeon.segment.DungeonSegmentModel;
import xiroc.dungeoncrawl.dungeon.segment.DungeonSegmentModelRegistry;
import xiroc.dungeoncrawl.dungeon.treasure.Treasure;
import xiroc.dungeoncrawl.theme.Theme;

public class DungeonCorridor extends DungeonPiece {

	public int specialType;

	public DungeonCorridor(TemplateManager manager, CompoundNBT p_i51343_2_) {
		super(StructurePieceTypes.CORRIDOR, p_i51343_2_);
		specialType = p_i51343_2_.getInt("specialType");
	}

	@Override
	public boolean addComponentParts(IWorld worldIn, Random randomIn, MutableBoundingBox structureBoundingBoxIn,
			ChunkPos p_74875_4_) {
//		if (theme != 1)
//			theme = Theme.BIOME_TO_THEME_MAP
//					.getOrDefault(worldIn.getBiome(new BlockPos(x, y, z)).getRegistryName().toString(), 0);

//		DungeonCrawl.LOGGER.info("Building Corridor at {} {} {}, Rotation: {}, Theme: {}", x, y, z, rotation.toString(), theme);
		
		if (theme != 3 && getAirBlocks(worldIn, x, y, z, 8, 8) > 8) {

			boolean ew = rotation == Rotation.NONE || rotation == Rotation.CLOCKWISE_180;
			switch (connectedSides) {
			case 2:
				if (sides[0] && sides[2] || sides[1] && sides[3])
					buildRotated(DungeonSegmentModelRegistry.BRIDGE, worldIn,
							new BlockPos(ew ? x : x + 1, y - 1, ew ? z + 1 : z), Theme.get(theme),
							Theme.getSub(subTheme), Treasure.Type.DEFAULT, stage, rotation, true);
				else
					buildRotated(DungeonSegmentModelRegistry.BRIDGE_TURN, worldIn,
							new BlockPos(x + (sides[1] ? 1 : 0), y - 1, z + (sides[2] ? 1 : 0)), Theme.get(theme),
							Theme.getSub(subTheme), Treasure.Type.DEFAULT, stage, rotation, true);
				return true;
			case 3:
				buildRotated(DungeonSegmentModelRegistry.BRIDGE_SIDE, worldIn,
						new BlockPos(sides[1] ? sides[3] ? x : x + 1 : x, y - 1,
								sides[2] ? sides[0] ? z : z + 1 : z),
						Theme.get(theme), Theme.getSub(subTheme), Treasure.Type.DEFAULT, stage, rotation, true);
				return true;
			case 4:
				buildRotated(DungeonSegmentModelRegistry.BRIDGE_ALL_SIDES, worldIn, new BlockPos(x, y - 1, z),
						Theme.get(theme), Theme.getSub(subTheme), Treasure.Type.DEFAULT, stage, rotation, true);
				return true;
			}

			return true;
		}

		DungeonSegmentModel model = DungeonBuilder.getModel(this, randomIn);
		if (model == null)
			return false;

		buildRotated(model, worldIn, new BlockPos(x, y, z), Theme.get(theme), Theme.getSub(subTheme),
				Treasure.Type.DEFAULT, stage, getRotation(), true);

		if (Config.NO_SPAWNERS.get())
			spawnMobs(worldIn, this, model.width, model.length, new int[] { 1 });

		if (theme == 3 && ((connectedSides == 2
				&& (!(sides[0] && sides[2] || sides[1] && sides[3]) || randomIn.nextDouble() < 0.2))
				|| connectedSides > 2) && getBlocks(worldIn, Blocks.WATER, x, y - 1, z, 8, 8) > 5)
			addColumns(this, worldIn, 1, theme);
		return true;
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

}
