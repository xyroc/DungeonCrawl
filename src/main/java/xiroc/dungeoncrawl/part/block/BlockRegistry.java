package xiroc.dungeoncrawl.part.block;

/*
 * DungeonCrawl (C) 2019 - 2020 XYROC (XIROC1337), All Rights Reserved 
 */

import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.util.Tuple;
import xiroc.dungeoncrawl.DungeonCrawl;

public class BlockRegistry {

	public static final BlockState CAVE_AIR = Blocks.CAVE_AIR.getDefaultState();
	public static final BlockState SPAWNER = Blocks.SPAWNER.getDefaultState();
	public static final BlockState CHEST = Blocks.CHEST.getDefaultState();

	public static final BlockState GRASS = Blocks.GRASS.getDefaultState();
	public static final BlockState GRAVEL = Blocks.GRAVEL.getDefaultState();
	public static final BlockState COBBLESTONE = Blocks.COBBLESTONE.getDefaultState();

	public static final BlockState STONE = Blocks.STONE.getDefaultState();

	public static final BlockState ACACIA_LOG = Blocks.ACACIA_LOG.getDefaultState();
	public static final BlockState BIRCH_LOG = Blocks.BIRCH_LOG.getDefaultState();
	public static final BlockState JUNGLE_LOG = Blocks.JUNGLE_LOG.getDefaultState();
	public static final BlockState OAK_LOG = Blocks.OAK_LOG.getDefaultState();
	public static final BlockState DARK_OAK_LOG = Blocks.DARK_OAK_LOG.getDefaultState();
	public static final BlockState SPRUCE_LOG = Blocks.SPRUCE_LOG.getDefaultState();

	public static final BlockState STONE_BRICKS = Blocks.STONE_BRICKS.getDefaultState();
	public static final BlockState MOSSY_STONE_BRICKS = Blocks.MOSSY_STONE_BRICKS.getDefaultState();
	public static final BlockState CRACKED_STONE_BRICKS = Blocks.CRACKED_STONE_BRICKS.getDefaultState();
	public static final BlockState INFESTED_STONE_BRICKS = Blocks.INFESTED_STONE_BRICKS.getDefaultState();

	public static final BlockState STAIRS_STONE_BRICKS = Blocks.STONE_BRICK_STAIRS.getDefaultState();
	public static final BlockState STAIRS_COBBLESTONE = Blocks.COBBLESTONE_STAIRS.getDefaultState();

	public static final BlockState NETHERRACK = Blocks.NETHERRACK.getDefaultState();
	public static final BlockState NETHER_BRICK = Blocks.NETHER_BRICKS.getDefaultState();
	public static final BlockState SOUL_SAND = Blocks.SOUL_SAND.getDefaultState();

	public static final BlockState STAIRS_NETHER_BRICK = Blocks.NETHER_BRICK_STAIRS.getDefaultState();
	public static final BlockState STAIRS_QUARTZ = Blocks.QUARTZ_STAIRS.getDefaultState();

	public static final BlockState IRON_BARS_WATERLOGGED = Blocks.IRON_BARS.getDefaultState()
			.with(BlockStateProperties.WATERLOGGED, true);

	public static final TupleIntBlock TIB_GRASS = new TupleIntBlock(1, GRASS);
	public static final TupleIntBlock TIB_GRAVEL = new TupleIntBlock(2, GRAVEL);
	public static final TupleIntBlock TIB_COBBLESTONE = new TupleIntBlock(2, COBBLESTONE);
	public static final TupleIntBlock TIB_MOSSY_COBBLESTONE = new TupleIntBlock(2,
			Blocks.MOSSY_COBBLESTONE.getDefaultState());
	public static final TupleIntBlock TIB_NETHERRACK = new TupleIntBlock(2, NETHERRACK);
	public static final TupleIntBlock TIB_NETHER_BRICK = new TupleIntBlock(2, NETHER_BRICK);
	public static final TupleIntBlock TIB_SOUL_SAND = new TupleIntBlock(1, SOUL_SAND);

	public static final TupleIntBlock TIB_ACACIA_LOG = new TupleIntBlock(1, ACACIA_LOG);
	public static final TupleIntBlock TIB_BIRCH_LOG = new TupleIntBlock(1, BIRCH_LOG);
	public static final TupleIntBlock TIB_JUNGLE_LOG = new TupleIntBlock(1, JUNGLE_LOG);
	public static final TupleIntBlock TIB_OAK_LOG = new TupleIntBlock(1, OAK_LOG);
	public static final TupleIntBlock TIB_DARK_OAK_LOG = new TupleIntBlock(1, DARK_OAK_LOG);
	public static final TupleIntBlock TIB_SPRUCE_LOG = new TupleIntBlock(1, SPRUCE_LOG);

	public static final TupleIntBlock TIB_STONE_BRICKS = new TupleIntBlock(4, STONE_BRICKS);
	public static final TupleIntBlock TIB_MOSSY_STONE_BRICKS = new TupleIntBlock(2, MOSSY_STONE_BRICKS);
	public static final TupleIntBlock TIB_CRACKED_STONE_BRICKS = new TupleIntBlock(2, CRACKED_STONE_BRICKS);
	public static final TupleIntBlock TIB_INFESTED_STONE_BRICKS = new TupleIntBlock(1, INFESTED_STONE_BRICKS);

	public static final TupleIntBlock TIB_STAIRS_STONE_BRICKS = new TupleIntBlock(2, STAIRS_STONE_BRICKS);
	public static final TupleIntBlock TIB_STAIRS_COBBLESTONE = new TupleIntBlock(1, STAIRS_COBBLESTONE);
	public static final TupleIntBlock TIB_STAIRS_NETHER_BRICK = new TupleIntBlock(3, STAIRS_NETHER_BRICK);
	public static final TupleIntBlock TIB_STAIRS_QUARTZ = new TupleIntBlock(1, STAIRS_QUARTZ);

	public static WeightedRandomBlock STONE_BRICKS_NORMAL_CRACKED_COBBLESTONE, BRICKS_GRANITE, ANDESITE_STONE_BRICKS,
			OBSIDIAN_MOSSY;
	public static WeightedRandomBlock STONE_BRICK_FLOOR, STONE_BRICK_FLOOR_MOSSY, STONE_BRICK_FLOOR_VERY_MOSSY,
			ANDESITE_STONE_BRICKS_COBBLESTONE, OBSIDIAN_MOSSY_FLOOR;
	public static WeightedRandomBlock STONE_BRICKS_GRAVEL_COBBLESTONE;
	public static WeightedRandomBlock NETHERRACK_NETHERBRICK;
	public static WeightedRandomBlock NETHERRACK_NETHERBRICK_SOULSAND;
	public static WeightedRandomBlock NETHER_BRICK_STAIRS;
	public static WeightedRandomBlock STAIRS_STONE_COBBLESTONE, STAIRS_ANDESITE_STONE_COBBLESTONE,
			STAIRS_BRICKS_GRANITE;
	public static WeightedRandomBlock STAIRS_NETHERBRICK_QUARTZ;
	public static WeightedRandomBlock SANDSTONE_DEFAULT_CHSELED_SMOOTH;
	public static WeightedRandomBlock SANDSTONE_DEFAULT_SMOOTH_SAND;
	public static WeightedRandomBlock STAIRS_SANDSTONE_DEFAULT_SMOOTH;
	public static WeightedRandomBlock RED_SANDSTONE_DEFAULT_CHSELED_SMOOTH;
	public static WeightedRandomBlock RED_SANDSTONE_DEFAULT_SMOOTH_RED_SAND;
	public static WeightedRandomBlock STAIRS_RED_SANDSTONE_DEFAULT_SMOOTH;
	public static WeightedRandomBlock ICE_DEFAULT_PACKED;
	public static WeightedRandomBlock DARK_PRISMARINE_PRISMARINE;
	public static WeightedRandomBlock CLAY_FLOOR, BRICKS_GRANITE_FLOOR;
	public static WeightedRandomBlock STONE_WALL, NETHER_WALL, BRICKS_GRANITE_WALL, ANDESITE_STONE_WALL;
	public static WeightedRandomBlock MOSS, MOSS_FLOOR, MOSS_WALL, MOSS_STAIRS, MOSS_ANDESITE, MOSS_ANDESITE_FLOOR,
			MOSS_ANDESITE_WALL, MOSS_ANDESITE_STAIRS;

	/*
	 * Calculate the WeightedRandomBlocks
	 */
	public static void load() {
		long time = System.currentTimeMillis();
		DungeonCrawl.LOGGER.info("Calculating WeightedRandomBlocks");

		STONE_BRICKS_NORMAL_CRACKED_COBBLESTONE = new WeightedRandomBlock(new TupleIntBlock[] {
				new TupleIntBlock(5, STONE_BRICKS), new TupleIntBlock(2, CRACKED_STONE_BRICKS),
				new TupleIntBlock(2, COBBLESTONE), new TupleIntBlock(1, Blocks.MOSSY_STONE_BRICKS.getDefaultState()) });

		STONE_BRICK_FLOOR = new WeightedRandomBlock(
				new TupleIntBlock[] { new TupleIntBlock(8, Blocks.STONE_BRICKS.getDefaultState()),
						new TupleIntBlock(2, Blocks.CRACKED_STONE_BRICKS.getDefaultState()),
						new TupleIntBlock(2, Blocks.COBBLESTONE.getDefaultState()),
						new TupleIntBlock(1, Blocks.MOSSY_STONE_BRICKS.getDefaultState()) });

		STONE_BRICK_FLOOR_MOSSY = new WeightedRandomBlock(
				new TupleIntBlock[] { new TupleIntBlock(5, Blocks.STONE_BRICKS.getDefaultState()),
						new TupleIntBlock(3, Blocks.MOSSY_STONE_BRICKS.getDefaultState()),
						new TupleIntBlock(1, Blocks.CRACKED_STONE_BRICKS.getDefaultState()),
						new TupleIntBlock(1, Blocks.COBBLESTONE.getDefaultState()) });

		STONE_BRICK_FLOOR_VERY_MOSSY = new WeightedRandomBlock(
				new TupleIntBlock[] { new TupleIntBlock(1, Blocks.STONE_BRICKS.getDefaultState()),
						new TupleIntBlock(3, Blocks.MOSSY_STONE_BRICKS.getDefaultState()),
						new TupleIntBlock(2, Blocks.MOSSY_COBBLESTONE.getDefaultState()),
						new TupleIntBlock(1, Blocks.CRACKED_STONE_BRICKS.getDefaultState()) });

		STONE_BRICKS_GRAVEL_COBBLESTONE = new WeightedRandomBlock(new TupleIntBlock[] { TIB_STONE_BRICKS, TIB_GRAVEL,
				TIB_COBBLESTONE, new TupleIntBlock(1, Blocks.MOSSY_COBBLESTONE.getDefaultState()),
				new TupleIntBlock(1, MOSSY_STONE_BRICKS) });

		NETHERRACK_NETHERBRICK = new WeightedRandomBlock(new TupleIntBlock[] { new TupleIntBlock(3, NETHERRACK),
				new TupleIntBlock(3, NETHER_BRICK), new TupleIntBlock(3, Blocks.RED_NETHER_BRICKS.getDefaultState()),
				new TupleIntBlock(1, Blocks.OBSIDIAN.getDefaultState()),
				new TupleIntBlock(1, Blocks.MAGMA_BLOCK.getDefaultState()) });

		NETHERRACK_NETHERBRICK_SOULSAND = new WeightedRandomBlock(new TupleIntBlock[] {
				new TupleIntBlock(3, NETHERRACK), new TupleIntBlock(3, NETHER_BRICK), new TupleIntBlock(1, SOUL_SAND),
				new TupleIntBlock(2, Blocks.RED_NETHER_BRICKS.getDefaultState()),
				new TupleIntBlock(1, Blocks.OBSIDIAN.getDefaultState()),
				new TupleIntBlock(1, Blocks.MAGMA_BLOCK.getDefaultState()) });

		NETHER_BRICK_STAIRS = new WeightedRandomBlock(
				new TupleIntBlock[] { new TupleIntBlock(1, Blocks.NETHER_BRICK_STAIRS.getDefaultState()),
						new TupleIntBlock(1, Blocks.RED_NETHER_BRICK_STAIRS.getDefaultState()) });

		STAIRS_STONE_COBBLESTONE = new WeightedRandomBlock(
				new TupleIntBlock[] { TIB_STAIRS_STONE_BRICKS, TIB_STAIRS_COBBLESTONE });

		STAIRS_NETHERBRICK_QUARTZ = new WeightedRandomBlock(
				new TupleIntBlock[] { TIB_STAIRS_NETHER_BRICK, TIB_STAIRS_QUARTZ });

		SANDSTONE_DEFAULT_CHSELED_SMOOTH = new WeightedRandomBlock(
				new TupleIntBlock[] { new TupleIntBlock(2, Blocks.SANDSTONE.getDefaultState()),
						new TupleIntBlock(1, Blocks.CHISELED_SANDSTONE.getDefaultState()),
						new TupleIntBlock(1, Blocks.SMOOTH_SANDSTONE.getDefaultState()) });

		SANDSTONE_DEFAULT_SMOOTH_SAND = new WeightedRandomBlock(
				new TupleIntBlock[] { new TupleIntBlock(2, Blocks.SAND.getDefaultState()),
						new TupleIntBlock(1, Blocks.SANDSTONE.getDefaultState()),
						new TupleIntBlock(1, Blocks.SMOOTH_SANDSTONE.getDefaultState()) });

		STAIRS_SANDSTONE_DEFAULT_SMOOTH = new WeightedRandomBlock(
				new TupleIntBlock[] { new TupleIntBlock(1, Blocks.SANDSTONE_STAIRS.getDefaultState()),
						new TupleIntBlock(1, Blocks.SMOOTH_SANDSTONE_STAIRS.getDefaultState()) });

		RED_SANDSTONE_DEFAULT_CHSELED_SMOOTH = new WeightedRandomBlock(
				new TupleIntBlock[] { new TupleIntBlock(2, Blocks.RED_SANDSTONE.getDefaultState()),
						new TupleIntBlock(1, Blocks.CHISELED_RED_SANDSTONE.getDefaultState()),
						new TupleIntBlock(1, Blocks.SMOOTH_RED_SANDSTONE.getDefaultState()) });

		RED_SANDSTONE_DEFAULT_SMOOTH_RED_SAND = new WeightedRandomBlock(
				new TupleIntBlock[] { new TupleIntBlock(2, Blocks.RED_SAND.getDefaultState()),
						new TupleIntBlock(1, Blocks.RED_SANDSTONE.getDefaultState()),
						new TupleIntBlock(1, Blocks.SMOOTH_RED_SANDSTONE.getDefaultState()) });

		STAIRS_RED_SANDSTONE_DEFAULT_SMOOTH = new WeightedRandomBlock(
				new TupleIntBlock[] { new TupleIntBlock(1, Blocks.RED_SANDSTONE_STAIRS.getDefaultState()),
						new TupleIntBlock(1, Blocks.SMOOTH_RED_SANDSTONE_STAIRS.getDefaultState()) });

		ICE_DEFAULT_PACKED = new WeightedRandomBlock(
				new TupleIntBlock[] { new TupleIntBlock(5, Blocks.ICE.getDefaultState()),
						new TupleIntBlock(3, Blocks.PACKED_ICE.getDefaultState()) });

		DARK_PRISMARINE_PRISMARINE = new WeightedRandomBlock(
				new TupleIntBlock[] { new TupleIntBlock(1, Blocks.DARK_PRISMARINE.getDefaultState()),
						new TupleIntBlock(1, Blocks.PRISMARINE.getDefaultState()) });

		CLAY_FLOOR = new WeightedRandomBlock(new TupleIntBlock[] { new TupleIntBlock(1, Blocks.CLAY.getDefaultState()),
				new TupleIntBlock(1, Blocks.SMOOTH_STONE.getDefaultState()),
				new TupleIntBlock(1, Blocks.STONE.getDefaultState()) });

		STONE_WALL = new WeightedRandomBlock(
				new TupleIntBlock[] { new TupleIntBlock(1, Blocks.STONE_BRICK_WALL.getDefaultState()),
						new TupleIntBlock(1, Blocks.COBBLESTONE_WALL.getDefaultState()),
						new TupleIntBlock(1, Blocks.ANDESITE_WALL.getDefaultState()),
						new TupleIntBlock(1, Blocks.MOSSY_STONE_BRICK_WALL.getDefaultState()),
						new TupleIntBlock(1, Blocks.MOSSY_COBBLESTONE_WALL.getDefaultState()),
						new TupleIntBlock(1, Blocks.DIORITE_WALL.getDefaultState()) });

		NETHER_WALL = new WeightedRandomBlock(
				new TupleIntBlock[] { new TupleIntBlock(1, Blocks.NETHER_BRICK_WALL.getDefaultState()),
						new TupleIntBlock(1, Blocks.RED_NETHER_BRICK_WALL.getDefaultState()) });

		BRICKS_GRANITE_FLOOR = new WeightedRandomBlock(
				new TupleIntBlock[] { new TupleIntBlock(1, Blocks.BRICKS.getDefaultState()),
						new TupleIntBlock(5, Blocks.POLISHED_GRANITE.getDefaultState()) });

		BRICKS_GRANITE = new WeightedRandomBlock(
				new TupleIntBlock[] { new TupleIntBlock(2, Blocks.BRICKS.getDefaultState()),
						new TupleIntBlock(1, Blocks.POLISHED_GRANITE.getDefaultState()) });

		ANDESITE_STONE_BRICKS = new WeightedRandomBlock(
				new TupleIntBlock[] { new TupleIntBlock(3, Blocks.POLISHED_ANDESITE.getDefaultState()),
						new TupleIntBlock(1, Blocks.STONE_BRICKS.getDefaultState()),
						new TupleIntBlock(1, Blocks.MOSSY_STONE_BRICKS.getDefaultState()),
						new TupleIntBlock(1, Blocks.CRACKED_STONE_BRICKS.getDefaultState()) });

		ANDESITE_STONE_BRICKS_COBBLESTONE = new WeightedRandomBlock(
				new TupleIntBlock[] { new TupleIntBlock(4, Blocks.POLISHED_ANDESITE.getDefaultState()),
						new TupleIntBlock(2, Blocks.STONE_BRICKS.getDefaultState()),
						new TupleIntBlock(1, Blocks.MOSSY_STONE_BRICKS.getDefaultState()),
						new TupleIntBlock(2, Blocks.CRACKED_STONE_BRICKS.getDefaultState()),
						new TupleIntBlock(2, Blocks.COBBLESTONE.getDefaultState()),
						new TupleIntBlock(2, Blocks.MOSSY_COBBLESTONE.getDefaultState()),
						new TupleIntBlock(1, Blocks.CHISELED_STONE_BRICKS.getDefaultState()),
						new TupleIntBlock(1, Blocks.SMOOTH_STONE.getDefaultState()) });

		STAIRS_ANDESITE_STONE_COBBLESTONE = new WeightedRandomBlock(
				new TupleIntBlock[] { new TupleIntBlock(2, Blocks.POLISHED_ANDESITE_STAIRS.getDefaultState()),
						new TupleIntBlock(1, Blocks.ANDESITE_STAIRS.getDefaultState()),
						new TupleIntBlock(2, Blocks.STONE_BRICK_STAIRS.getDefaultState()),
						new TupleIntBlock(1, Blocks.COBBLESTONE_STAIRS.getDefaultState()) });

		STAIRS_BRICKS_GRANITE = new WeightedRandomBlock(
				new TupleIntBlock[] { new TupleIntBlock(2, Blocks.BRICK_STAIRS.getDefaultState()),
						new TupleIntBlock(1, Blocks.POLISHED_GRANITE_STAIRS.getDefaultState()),
						new TupleIntBlock(1, Blocks.GRANITE_STAIRS.getDefaultState()) });

		BRICKS_GRANITE_WALL = new WeightedRandomBlock(
				new TupleIntBlock[] { new TupleIntBlock(1, Blocks.BRICK_WALL.getDefaultState()),
						new TupleIntBlock(1, Blocks.GRANITE_WALL.getDefaultState()) });

		ANDESITE_STONE_WALL = new WeightedRandomBlock(
				new TupleIntBlock[] { new TupleIntBlock(2, Blocks.ANDESITE_WALL.getDefaultState()),
						new TupleIntBlock(1, Blocks.STONE_BRICK_WALL.getDefaultState()),
						new TupleIntBlock(1, Blocks.MOSSY_COBBLESTONE_WALL.getDefaultState()),
						new TupleIntBlock(1, Blocks.COBBLESTONE_WALL.getDefaultState()),
						new TupleIntBlock(1, Blocks.MOSSY_STONE_BRICK_WALL.getDefaultState()) });

		MOSS = new WeightedRandomBlock(
				new TupleIntBlock[] { new TupleIntBlock(6, Blocks.MOSSY_STONE_BRICKS.getDefaultState()),
						new TupleIntBlock(5, Blocks.MOSSY_COBBLESTONE.getDefaultState()),
						new TupleIntBlock(1, Blocks.CRACKED_STONE_BRICKS.getDefaultState()) });

		MOSS_FLOOR = new WeightedRandomBlock(
				new TupleIntBlock[] { new TupleIntBlock(6, Blocks.MOSSY_STONE_BRICKS.getDefaultState()),
						new TupleIntBlock(5, Blocks.MOSSY_COBBLESTONE.getDefaultState()),
						new TupleIntBlock(1, Blocks.CRACKED_STONE_BRICKS.getDefaultState()),
						new TupleIntBlock(4, Blocks.GRAVEL.getDefaultState()) });

		MOSS_WALL = new WeightedRandomBlock(
				new TupleIntBlock[] { new TupleIntBlock(1, Blocks.STONE_BRICK_WALL.getDefaultState()),
						new TupleIntBlock(7, Blocks.MOSSY_COBBLESTONE_WALL.getDefaultState()),
						new TupleIntBlock(1, Blocks.COBBLESTONE_WALL.getDefaultState()),
						new TupleIntBlock(7, Blocks.MOSSY_STONE_BRICK_WALL.getDefaultState()) });

		MOSS_STAIRS = new WeightedRandomBlock(
				new TupleIntBlock[] { new TupleIntBlock(1, Blocks.MOSSY_COBBLESTONE_STAIRS.getDefaultState()),
						new TupleIntBlock(1, Blocks.MOSSY_STONE_BRICK_STAIRS.getDefaultState()) });

		MOSS_ANDESITE = new WeightedRandomBlock(
				new TupleIntBlock[] { new TupleIntBlock(1, Blocks.ANDESITE.getDefaultState()),
						new TupleIntBlock(3, Blocks.CRACKED_STONE_BRICKS.getDefaultState()),
						new TupleIntBlock(3, Blocks.COBBLESTONE.getDefaultState()),
						new TupleIntBlock(7, Blocks.MOSSY_STONE_BRICKS.getDefaultState()),
						new TupleIntBlock(7, Blocks.MOSSY_COBBLESTONE.getDefaultState()) });

		MOSS_ANDESITE_FLOOR = new WeightedRandomBlock(
				new TupleIntBlock[] { new TupleIntBlock(1, Blocks.ANDESITE.getDefaultState()),
						new TupleIntBlock(3, Blocks.CRACKED_STONE_BRICKS.getDefaultState()),
						new TupleIntBlock(3, Blocks.COBBLESTONE.getDefaultState()),
						new TupleIntBlock(5, Blocks.MOSSY_STONE_BRICKS.getDefaultState()),
						new TupleIntBlock(5, Blocks.MOSSY_COBBLESTONE.getDefaultState()),
						new TupleIntBlock(5, Blocks.GRAVEL.getDefaultState()) });

		MOSS_ANDESITE_WALL = new WeightedRandomBlock(
				new TupleIntBlock[] { new TupleIntBlock(2, Blocks.ANDESITE_WALL.getDefaultState()),
						new TupleIntBlock(1, Blocks.STONE_BRICK_WALL.getDefaultState()),
						new TupleIntBlock(7, Blocks.MOSSY_COBBLESTONE_WALL.getDefaultState()),
						new TupleIntBlock(1, Blocks.COBBLESTONE_WALL.getDefaultState()),
						new TupleIntBlock(7, Blocks.MOSSY_STONE_BRICK_WALL.getDefaultState()) });

		MOSS_ANDESITE_STAIRS = new WeightedRandomBlock(
				new TupleIntBlock[] { new TupleIntBlock(1, Blocks.ANDESITE_STAIRS.getDefaultState()),
						new TupleIntBlock(1, Blocks.STONE_BRICK_STAIRS.getDefaultState()),
						new TupleIntBlock(1, Blocks.COBBLESTONE_STAIRS.getDefaultState()),
						new TupleIntBlock(6, Blocks.MOSSY_COBBLESTONE_STAIRS.getDefaultState()),
						new TupleIntBlock(6, Blocks.MOSSY_STONE_BRICK_STAIRS.getDefaultState()) });

		OBSIDIAN_MOSSY = new WeightedRandomBlock(
				new TupleIntBlock[] { new TupleIntBlock(3, Blocks.MOSSY_STONE_BRICKS.getDefaultState()),
						new TupleIntBlock(3, Blocks.MOSSY_COBBLESTONE.getDefaultState()),
						new TupleIntBlock(1, Blocks.CRACKED_STONE_BRICKS.getDefaultState()),
						new TupleIntBlock(1, Blocks.CHISELED_STONE_BRICKS.getDefaultState()),
						new TupleIntBlock(3, Blocks.OBSIDIAN.getDefaultState()) });
		
		OBSIDIAN_MOSSY_FLOOR = new WeightedRandomBlock(
				new TupleIntBlock[] { new TupleIntBlock(2, Blocks.MOSSY_STONE_BRICKS.getDefaultState()),
						new TupleIntBlock(2, Blocks.MOSSY_COBBLESTONE.getDefaultState()),
						new TupleIntBlock(2, Blocks.CRACKED_STONE_BRICKS.getDefaultState()),
						new TupleIntBlock(5, Blocks.CHISELED_STONE_BRICKS.getDefaultState()),
						new TupleIntBlock(3, Blocks.OBSIDIAN.getDefaultState()) });

		DungeonCrawl.LOGGER.info("Finished calculations (" + (System.currentTimeMillis() - time) + " ms)");
	}

	public static final class TupleIntBlock extends Tuple<Integer, BlockState> {

		public TupleIntBlock(Integer aIn, BlockState bIn) {
			super(aIn, bIn);
		}

	}

	public static final class TupleFloatBlock extends Tuple<Float, BlockState> {

		public TupleFloatBlock(Float aIn, BlockState bIn) {
			super(aIn, bIn);
		}

	}

}
