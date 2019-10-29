package xiroc.dungeoncrawl.dungeon;

import java.util.HashMap;

/*
 * DungeonCrawl (C) 2019 XYROC (XIROC1337), All Rights Reserved 
 */

import java.util.List;
import java.util.Random;

import com.google.common.collect.Lists;

import net.minecraft.util.Tuple;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.IWorld;
import net.minecraft.world.gen.ChunkGenerator;
import xiroc.dungeoncrawl.DungeonCrawl;
import xiroc.dungeoncrawl.config.JsonConfig;
import xiroc.dungeoncrawl.dungeon.DungeonPieces.DungeonPiece;
import xiroc.dungeoncrawl.dungeon.DungeonPieces.EntranceBuilder;
import xiroc.dungeoncrawl.dungeon.DungeonPieces.Hole;
import xiroc.dungeoncrawl.dungeon.DungeonPieces.Stairs;
import xiroc.dungeoncrawl.dungeon.segment.DungeonSegmentModel;
import xiroc.dungeoncrawl.dungeon.segment.DungeonSegmentModelBlock;
import xiroc.dungeoncrawl.dungeon.segment.DungeonSegmentModelBlockType;
import xiroc.dungeoncrawl.dungeon.segment.DungeonSegmentModelRegistry;
import xiroc.dungeoncrawl.dungeon.segment.RandomDungeonSegmentModel;
import xiroc.dungeoncrawl.dungeon.treasure.Treasure;
import xiroc.dungeoncrawl.part.block.WeightedRandomBlock;
import xiroc.dungeoncrawl.theme.Theme;
import xiroc.dungeoncrawl.util.BossEntry;
import xiroc.dungeoncrawl.util.IRandom;
import xiroc.dungeoncrawl.util.Position2D;

public class DungeonBuilder {

	public static final HashMap<Integer, Tuple<Integer, Integer>> ENTRANCE_OFFSET_DATA;
	public static final HashMap<Integer, EntranceProcessor> ENTRANCE_PROCESSORS;

	public static final EntranceProcessor DEFAULT_PROCESSOR = (world, pos, theme, piece) -> {
		;
	};

	public Random rand;
	public Position2D start;

	public DungeonLayer[] layers;
	public DungeonStatTracker statTracker;

	public BlockPos startPos;

	public int theme;

	static {
		ENTRANCE_OFFSET_DATA = new HashMap<Integer, Tuple<Integer, Integer>>();
		ENTRANCE_OFFSET_DATA.put(20, new Tuple<Integer, Integer>(0, 0));
		ENTRANCE_OFFSET_DATA.put(32, new Tuple<Integer, Integer>(-3, -3));

		ENTRANCE_PROCESSORS = new HashMap<Integer, EntranceProcessor>();
		ENTRANCE_PROCESSORS.put(20, (world, pos, theme, piece) -> {
			int x = pos.getX(), y = pos.getY(), z = pos.getZ();
			int height = theme == 3 ? world.getSeaLevel() : DungeonPieces.getGroudHeight(world, x + 4, z + 4);
			int ch = y;
			Theme buildTheme = Theme.get(theme);
			while (ch < height) {
				piece.build(DungeonSegmentModelRegistry.STAIRS, world, new BlockPos(x, ch, z), buildTheme,
						Treasure.Type.DEFAULT, piece.stage);
				for (int x1 = 0; x1 < 8; x1++)
					for (int y1 = 0; y1 < 8; y1++)
						piece.setBlockState(buildTheme.wall.get(), world, null, x + x1, ch + y1, z + 7, theme, 0);
				for (int z1 = 0; z1 < 8; z1++)
					for (int y1 = 0; y1 < 8; y1++)
						piece.setBlockState(buildTheme.wall.get(), world, null, x + 7, ch + y1, z + z1, theme, 0);
				ch += 8;
			}
		});
		ENTRANCE_PROCESSORS.put(32, (world, pos, theme, piece) -> {
			int x = pos.getX(), y = pos.getY(), z = pos.getZ();

			buildWallPillar(world, theme, new BlockPos(x + 4, y, z + 2), piece);
			buildWallPillar(world, theme, new BlockPos(x + 5, y, z + 2), piece);
			buildWallPillar(world, theme, new BlockPos(x + 6, y, z + 2), piece);
			buildWallPillar(world, theme, new BlockPos(x + 7, y, z + 2), piece);
			buildWallPillar(world, theme, new BlockPos(x + 8, y, z + 2), piece);

			buildWallPillar(world, theme, new BlockPos(x + 2, y, z + 4), piece);
			buildWallPillar(world, theme, new BlockPos(x + 2, y, z + 5), piece);
			buildWallPillar(world, theme, new BlockPos(x + 2, y, z + 6), piece);
			buildWallPillar(world, theme, new BlockPos(x + 2, y, z + 7), piece);
			buildWallPillar(world, theme, new BlockPos(x + 2, y, z + 8), piece);

			buildWallPillar(world, theme, new BlockPos(x + 4, y, z + 10), piece);
			buildWallPillar(world, theme, new BlockPos(x + 5, y, z + 10), piece);
			buildWallPillar(world, theme, new BlockPos(x + 6, y, z + 10), piece);
			buildWallPillar(world, theme, new BlockPos(x + 7, y, z + 10), piece);
			buildWallPillar(world, theme, new BlockPos(x + 8, y, z + 10), piece);

			buildWallPillar(world, theme, new BlockPos(x + 10, y, z + 4), piece);
			buildWallPillar(world, theme, new BlockPos(x + 10, y, z + 5), piece);
			buildWallPillar(world, theme, new BlockPos(x + 10, y, z + 6), piece);
			buildWallPillar(world, theme, new BlockPos(x + 10, y, z + 7), piece);
			buildWallPillar(world, theme, new BlockPos(x + 10, y, z + 8), piece);

			buildWallPillar(world, theme, new BlockPos(x + 5, y, z + 1), piece);
			buildWallPillar(world, theme, new BlockPos(x + 7, y, z + 1), piece);

			buildWallPillar(world, theme, new BlockPos(x + 1, y, z + 5), piece);
			buildWallPillar(world, theme, new BlockPos(x + 1, y, z + 7), piece);

			buildWallPillar(world, theme, new BlockPos(x + 5, y, z + 11), piece);
			buildWallPillar(world, theme, new BlockPos(x + 7, y, z + 11), piece);

			buildWallPillar(world, theme, new BlockPos(x + 11, y, z + 5), piece);
			buildWallPillar(world, theme, new BlockPos(x + 11, y, z + 7), piece);
		});
	}

	public DungeonBuilder(ChunkGenerator<?> world, ChunkPos pos, Random rand) {
		this.rand = rand;
		this.start = new Position2D(rand.nextInt(Dungeon.SIZE), rand.nextInt(Dungeon.SIZE));
		this.startPos = new BlockPos(pos.x * 16, world.getGroundHeight() - 16, pos.z * 16);

		this.layers = new DungeonLayer[startPos.getY() / 16];

		DungeonCrawl.LOGGER.info("DungeonBuilder starts at (" + startPos.getX() + " / " + startPos.getY() + " / "
				+ startPos.getZ() + "), " + +this.layers.length + " layers, Theme: {}", theme);

		this.statTracker = new DungeonStatTracker(layers.length);

		theme = Theme.getTheme(world.getBiomeProvider().getBiome(startPos).getRegistryName().toString());
	}

	public List<DungeonPiece> build() {
		List<DungeonPiece> list = Lists.newArrayList();

		for (int i = 0; i < layers.length; i++) {
			this.layers[i] = new DungeonLayer(DungeonLayerType.NORMAL, Dungeon.SIZE, Dungeon.SIZE);
			this.layers[i].buildMap(this, list, rand, (i == 0) ? this.start : layers[i - 1].end, i,
					i == layers.length - 1);
		}

		for (int i = 0; i < layers.length; i++) {
			buildLayer(layers[i], i, startPos);
			// list.addAll(buildLayer(layers[i], i, startPos));
			DungeonPiece stairs = i == 0 ? new EntranceBuilder(null, DungeonPieces.DEFAULT_NBT)
					: new Stairs(null, DungeonPieces.DEFAULT_NBT);
			stairs.setRealPosition(startPos.getX() + layers[i].start.x * 8, startPos.getY() + 8 - i * 16,
					startPos.getZ() + layers[i].start.z * 8);
			stairs.stage = 0;
			list.add(stairs);
		}
		postProcessDungeon(list, rand, theme);
		for (DungeonPiece piece : list)
			if (piece.theme != 1)
				piece.theme = theme;
		return list;
	}

	public void buildLayer(DungeonLayer layer, int lyr, BlockPos startPos) {
		int stage = lyr > 2 ? 2 : lyr;
		for (int x = 0; x < layer.width; x++) {
			for (int z = 0; z < layer.length; z++) {
				if (layer.segments[x][z] != null) {
					layer.segments[x][z].setRealPosition(startPos.getX() + x * 8, startPos.getY() - lyr * 16,
							startPos.getZ() + z * 8);
					layer.segments[x][z].stage = stage;
				}
			}
		}
	}

	public void postProcessDungeon(List<DungeonPiece> list, Random rand, int theme) {
		int lyrs = layers.length;
		for (int i = 0; i < layers.length; i++) {
			int stage = i > 2 ? 2 : i;
			DungeonLayer layer = layers[i];
			for (int x = 0; x < layer.width; x++) {
				for (int z = 0; z < layer.length; z++) {
					if (layer.segments[x][z] != null) {
						if (layer.segments[x][z].getType() == 0) {
							if ((i < lyrs - 1 ? layers[i + 1].segments[x][z] == null : true)
									&& rand.nextDouble() < 0.02) {
								Hole hole = new Hole(null, DungeonPieces.DEFAULT_NBT);
								hole.sides = layer.segments[x][z].sides;
								hole.connectedSides = layer.segments[x][z].connectedSides;
								hole.setRealPosition(startPos.getX() + x * 8, startPos.getY() - i * 16,
										startPos.getZ() + z * 8);
								hole.stage = stage;
								hole.lava = stage == 2;
								layer.segments[x][z] = hole;
							} else {
								DungeonFeatures.processCorridor(this, layer, x, z, rand, i, stage, startPos);
							}
						}
					}
				}
			}

			for (int x = 0; x < layer.width; x++)
				for (int z = 0; z < layer.length; z++) {
					if (layer.segments[x][z] != null) {
						if (i == lyrs - 1)
							layer.segments[x][z].theme = 1;
						list.add(layer.segments[x][z]);
					}
				}
		}
	}

	/*
	 * Builds a 1x?x1 pillar to the ground
	 */
	public static void buildWallPillar(IWorld world, int theme, BlockPos pos, DungeonPiece piece) {
		DungeonSegmentModelBlock block = new DungeonSegmentModelBlock(DungeonSegmentModelBlockType.RAND_WALL_AIR);
		Theme buildTheme = Theme.get(theme);
		int x = pos.getX(), z = pos.getZ();
		int height = DungeonPieces.getGroudHeightFrom(world, x, z, pos.getY() - 1);
//		DungeonCrawl.LOGGER.debug("{} | {}", pos, height);
		for (int y = pos.getY() - 1; y > height; y--)
			piece.setBlockState(DungeonSegmentModelBlock.PROVIDERS.get(DungeonSegmentModelBlockType.RAND_WALL_AIR)
					.get(block, buildTheme, WeightedRandomBlock.RANDOM, 0), world, null, x, y, z, theme, 0);
	}

	public static DungeonSegmentModel getModel(DungeonPiece piece, Random rand) {
		boolean north = piece.sides[0];
		boolean east = piece.sides[1];
		boolean south = piece.sides[2];
		boolean west = piece.sides[3];
		switch (piece.getType()) {
		case 0:
			switch (piece.connectedSides) {
			case 2:
				switch (((DungeonPieces.Corridor) piece).specialType) {
				case 1:
					return DungeonSegmentModelRegistry.CORRIDOR_FIRE;
				case 2:
					return DungeonSegmentModelRegistry.CORRIDOR_GRASS;
				default:
					if (north && south || east && west)
						return RandomDungeonSegmentModel.CORRIDOR_STRAIGHT.roll(rand);
					return RandomDungeonSegmentModel.CORRIDOR_TURN.roll(rand);
				}

			case 3:
				return RandomDungeonSegmentModel.CORRIDOR_OPEN.roll(rand);
			case 4:
				return RandomDungeonSegmentModel.CORRIDOR_ALL_OPEN.roll(rand);
			default:
				return null;
			}
		case 1:
			return DungeonSegmentModelRegistry.STAIRS_BOTTOM;
		case 2:
			return DungeonSegmentModelRegistry.STAIRS;
		case 3:
			return DungeonSegmentModelRegistry.STAIRS_TOP;
		case 5:
			return DungeonSegmentModelRegistry.ROOM;
		default:
			return null;
		}
	}

	public static final IRandom<DungeonSegmentModel> ENTRANCE = (rand) -> {
		switch (rand.nextInt(2)) {
		case 0:
			return DungeonSegmentModelRegistry.ENTRANCE_TOWER_1;
		case 1:
			return DungeonSegmentModelRegistry.ENTRANCE_TOWER_1;
		default:
			return DungeonSegmentModelRegistry.ENTRANCE_TOWER_1;
		}
	};

	public static BossEntry getRandomBoss(Random rand) {
		if (JsonConfig.DUNGEON_BOSSES.length < 1)
			return null;
		return JsonConfig.DUNGEON_BOSSES[rand.nextInt(JsonConfig.DUNGEON_BOSSES.length)];
	}

	@FunctionalInterface
	public static interface EntranceProcessor {

		void process(IWorld world, BlockPos pos, int theme, DungeonPiece piece);

	}

}
