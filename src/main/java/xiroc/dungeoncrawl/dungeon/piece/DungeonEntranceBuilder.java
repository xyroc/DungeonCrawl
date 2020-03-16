package xiroc.dungeoncrawl.dungeon.piece;

/*
 * DungeonCrawl (C) 2019 - 2020 XYROC (XIROC1337), All Rights Reserved 
 */

import java.util.Random;

import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.Tuple;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.MutableBoundingBox;
import net.minecraft.world.IWorld;
import net.minecraft.world.gen.feature.template.TemplateManager;
import xiroc.dungeoncrawl.DungeonCrawl;
import xiroc.dungeoncrawl.dungeon.DungeonBuilder;
import xiroc.dungeoncrawl.dungeon.StructurePieceTypes;
import xiroc.dungeoncrawl.dungeon.segment.DungeonSegmentModel;
import xiroc.dungeoncrawl.dungeon.segment.DungeonSegmentModelRegistry;
import xiroc.dungeoncrawl.dungeon.treasure.Treasure;
import xiroc.dungeoncrawl.theme.Theme;
import xiroc.dungeoncrawl.theme.Theme.SubTheme;

public class DungeonEntranceBuilder extends DungeonPiece {

	public DungeonEntranceBuilder(TemplateManager manager, CompoundNBT p_i51343_2_) {
		super(StructurePieceTypes.ENTRANCE_BUILDER, p_i51343_2_);
	}

	@Override
	public boolean addComponentParts(IWorld worldIn, Random randomIn, MutableBoundingBox structureBoundingBoxIn,
			ChunkPos p_74875_4_) {
		int height = theme == 3 ? worldIn.getSeaLevel() : getGroudHeight(worldIn, x + 4, z + 4);
		int ch = y;
		Theme buildTheme = Theme.get(theme);
		SubTheme sub = Theme.getSub(subTheme);
		while (ch < height) {
			build(DungeonSegmentModelRegistry.STAIRS, worldIn, new BlockPos(x, ch, z), buildTheme, sub,
					Treasure.Type.DEFAULT, stage, true);
			ch += 8;
		}

		Random rand = worldIn.getRandom();
		if (rand == null) {
			DungeonCrawl.LOGGER.warn("Failed to receive a random object from worldIn: {}, {}", worldIn,
					worldIn.getClass());
			rand = new Random();
		}

		DungeonSegmentModel entrance = DungeonBuilder.ENTRANCE.roll(rand);
		Tuple<Integer, Integer> offset = DungeonBuilder.ENTRANCE_OFFSET_DATA.get(entrance.id);

		DungeonCrawl.LOGGER.info(
				"Entrance data: Position: ({}|{}|{}), Model: {}, Entrance id: {}, Offset: {}; ({}|{})", x, ch, z,
				entrance, entrance.id, offset, offset.getA(), offset.getB());

		build(entrance, worldIn, new BlockPos(x + offset.getA(), ch, z + offset.getB()), buildTheme, sub,
				Treasure.Type.SUPPLY, stage, true);
		DungeonBuilder.ENTRANCE_PROCESSORS.getOrDefault(entrance.id, DungeonBuilder.DEFAULT_PROCESSOR)
				.process(worldIn, new BlockPos(x + offset.getA(), ch, z + offset.getB()), theme, this);
		return true;
	}

	@Override
	public int getType() {
		return 6;
	}

}