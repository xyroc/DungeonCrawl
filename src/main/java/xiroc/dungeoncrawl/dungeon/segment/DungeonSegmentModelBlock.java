package xiroc.dungeoncrawl.dungeon.segment;

/*
 * DungeonCrawl (C) 2019 XYROC (XIROC1337), All Rights Reserved 
 */

import java.util.HashMap;

/*
 * DungeonCrawl (C) 2019 XYROC (XIROC1337), All Rights Reserved 
 */

import java.util.Random;

import net.minecraft.block.BarrelBlock;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.ChestBlock;
import net.minecraft.block.DispenserBlock;
import net.minecraft.state.properties.AttachFace;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.state.properties.DoubleBlockHalf;
import net.minecraft.state.properties.Half;
import net.minecraft.util.Direction;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Rotation;
import net.minecraftforge.registries.ForgeRegistries;
import xiroc.dungeoncrawl.part.block.BlockRegistry;
import xiroc.dungeoncrawl.theme.Theme;

public class DungeonSegmentModelBlock {

	public static HashMap<DungeonSegmentModelBlockType, DungeonSegmentBlockStateProvider> PROVIDERS = new HashMap<DungeonSegmentModelBlockType, DungeonSegmentBlockStateProvider>();

	public DungeonSegmentModelBlockType type;

	public Direction facing;
	public Direction.Axis axis;
	public Boolean upsideDown, north, east, south, west, waterlogged, lit, open, disarmed, attached;

	public ResourceLocation registryName;
	public String resourceName;
	public Half half;
	public DoubleBlockHalf doubleBlockHalf;
	public AttachFace attachFace;

	public DungeonSegmentModelBlock(DungeonSegmentModelBlockType type) {
		this.type = type;
		// LeverBlock
	}

	/**
	 * Loads all existing properties from the given BlockState.
	 */
	public DungeonSegmentModelBlock set(BlockState state) {
		if (state.has(BlockStateProperties.FACING))
			facing = state.get(BlockStateProperties.FACING);
		else if (state.has(BlockStateProperties.HORIZONTAL_FACING))
			facing = state.get(BlockStateProperties.HORIZONTAL_FACING);
		else if (state.has(BlockStateProperties.AXIS))
			axis = state.get(BlockStateProperties.AXIS);
		else if (state.has(BlockStateProperties.HORIZONTAL_AXIS))
			axis = state.get(BlockStateProperties.HORIZONTAL_AXIS);
		if (state.has(BlockStateProperties.NORTH))
			north = state.get(BlockStateProperties.NORTH);
		if (state.has(BlockStateProperties.EAST))
			east = state.get(BlockStateProperties.EAST);
		if (state.has(BlockStateProperties.SOUTH))
			south = state.get(BlockStateProperties.SOUTH);
		if (state.has(BlockStateProperties.WEST))
			west = state.get(BlockStateProperties.WEST);
		if (state.has(BlockStateProperties.WATERLOGGED))
			waterlogged = state.get(BlockStateProperties.WATERLOGGED);
		if (state.has(BlockStateProperties.INVERTED))
			upsideDown = state.get(BlockStateProperties.INVERTED);
		if (state.has(BlockStateProperties.DISARMED))
			disarmed = state.get(BlockStateProperties.DISARMED);
		if (state.has(BlockStateProperties.ATTACHED))
			attached = state.get(BlockStateProperties.ATTACHED);
		if (state.has(BlockStateProperties.LIT))
			lit = state.get(BlockStateProperties.LIT);
		if (state.has(BlockStateProperties.OPEN))
			open = state.get(BlockStateProperties.OPEN);
		if (state.has(BlockStateProperties.HALF))
			half = state.get(BlockStateProperties.HALF);
		if (state.has(BlockStateProperties.DOUBLE_BLOCK_HALF))
			doubleBlockHalf = state.get(BlockStateProperties.DOUBLE_BLOCK_HALF);
		if (state.has(BlockStateProperties.FACE))
			attachFace = state.get(BlockStateProperties.FACE);
		if (type == DungeonSegmentModelBlockType.OTHER)
			resourceName = state.getBlock().getRegistryName().toString();
		return this;
	}

	/**
	 * Applies all existing properties to the given BlockState.
	 */
	public BlockState create(BlockState state) {
		if (facing != null && state.has(BlockStateProperties.FACING))
			state = state.with(BlockStateProperties.FACING, facing);
		else if (facing != null && state.has(BlockStateProperties.HORIZONTAL_FACING))
			state = state.with(BlockStateProperties.HORIZONTAL_FACING, facing);
		else if (axis != null && state.has(BlockStateProperties.AXIS))
			state = state.with(BlockStateProperties.AXIS, axis);
		else if (axis != null && state.has(BlockStateProperties.HORIZONTAL_AXIS))
			state = state.with(BlockStateProperties.HORIZONTAL_AXIS, axis);
		if (north != null && state.has(BlockStateProperties.NORTH))
			state = state.with(BlockStateProperties.NORTH, north);
		if (east != null && state.has(BlockStateProperties.EAST))
			state = state.with(BlockStateProperties.EAST, east);
		if (south != null && state.has(BlockStateProperties.SOUTH))
			state = state.with(BlockStateProperties.SOUTH, south);
		if (west != null && state.has(BlockStateProperties.WEST))
			state = state.with(BlockStateProperties.WEST, west);
		if (waterlogged != null && state.has(BlockStateProperties.WATERLOGGED))
			state = state.with(BlockStateProperties.WATERLOGGED, waterlogged);
		if (upsideDown != null && state.has(BlockStateProperties.INVERTED))
			state = state.with(BlockStateProperties.INVERTED, upsideDown);
		if (disarmed != null && state.has(BlockStateProperties.DISARMED))
			state = state.with(BlockStateProperties.DISARMED, disarmed);
		if (attached != null && state.has(BlockStateProperties.ATTACHED))
			state = state.with(BlockStateProperties.ATTACHED, attached);
		if (lit != null && state.has(BlockStateProperties.LIT))
			state = state.with(BlockStateProperties.LIT, lit);
		if (open != null && state.has(BlockStateProperties.OPEN))
			state = state.with(BlockStateProperties.OPEN, open);
		if (half != null && state.has(BlockStateProperties.HALF))
			state = state.with(BlockStateProperties.HALF, half);
		if (attachFace != null && state.has(BlockStateProperties.FACE))
			state = state.with(BlockStateProperties.FACE, attachFace);
		if (doubleBlockHalf != null && state.has(BlockStateProperties.DOUBLE_BLOCK_HALF))
			state = state.with(BlockStateProperties.DOUBLE_BLOCK_HALF, doubleBlockHalf);
		return state;
	}

	/**
	 * Registers all BlockState providers.
	 */
	public static void load() {
		PROVIDERS.put(DungeonSegmentModelBlockType.NONE, (block, theme, rand, stage) -> null);
		PROVIDERS.put(DungeonSegmentModelBlockType.BARREL, (block, theme, rand, stage) -> Blocks.BARREL
				.getDefaultState().with(BarrelBlock.PROPERTY_FACING, block.facing));
		PROVIDERS.put(DungeonSegmentModelBlockType.MATERIAL,
				(block, theme, rand, stage) -> block.create(theme.material.get()));
		PROVIDERS.put(DungeonSegmentModelBlockType.CEILING, (block, theme, rand, stage) -> theme.ceiling.get());
		PROVIDERS.put(DungeonSegmentModelBlockType.CEILING_STAIRS,
				(block, theme, rand, stage) -> block.create(theme.ceilingStairs.get()));
		PROVIDERS.put(DungeonSegmentModelBlockType.CHEST,
				(block, theme, rand, stage) -> Blocks.CHEST.getDefaultState().with(ChestBlock.FACING, block.facing));
		PROVIDERS.put(DungeonSegmentModelBlockType.DISPENSER, (block, theme, rand, stage) -> Blocks.DISPENSER
				.getDefaultState().with(DispenserBlock.FACING, block.facing));
		PROVIDERS.put(DungeonSegmentModelBlockType.FLOOR, (block, theme, rand, stage) -> theme.floor.get());
		PROVIDERS.put(DungeonSegmentModelBlockType.FLOOR_STAIRS,
				(block, theme, rand, stage) -> block.create(theme.floorStairs.get()));
		PROVIDERS.put(DungeonSegmentModelBlockType.RAND_FLOOR_CHESTCOMMON_SPAWNER, (block, theme, rand, stage) -> {
			int i = rand.nextInt(10);
			if (i < 1 + stage)
				return BlockRegistry.SPAWNER;
			if (i == 5)
				return Blocks.CHEST.getDefaultState().with(ChestBlock.FACING, block.facing);
			return theme.floor.get();
		});
		PROVIDERS.put(DungeonSegmentModelBlockType.RAND_FLOOR_LAVA, (block, theme, rand, stage) -> {
			switch (rand.nextInt(2)) {
			case 0:
				return theme.floor.get();
			case 1:
				return Blocks.LAVA.getDefaultState();
			default:
				return Blocks.LAVA.getDefaultState();
			}
		});
		PROVIDERS.put(DungeonSegmentModelBlockType.RAND_FLOOR_WATER, (block, theme, rand, stage) -> {
			switch (rand.nextInt(2)) {
			case 0:
				return theme.floor.get();
			case 1:
				return Blocks.WATER.getDefaultState();
			default:
				return Blocks.WATER.getDefaultState();
			}
		});
		PROVIDERS.put(DungeonSegmentModelBlockType.RAND_WALL_AIR, (block, theme, rand, stage) -> {
			if (rand.nextFloat() < 0.75)
				return theme.wall.get();
			return Blocks.CAVE_AIR.getDefaultState();
		});
		PROVIDERS.put(DungeonSegmentModelBlockType.RAND_WALL_SPAWNER, (block, theme, rand, stage) -> {
			if (rand.nextInt(2 + (2 - stage)) == 0)
				return BlockRegistry.SPAWNER;
			return theme.wall.get();
		});
		PROVIDERS.put(DungeonSegmentModelBlockType.STAIRS,
				(block, theme, rand, stage) -> block.create(theme.stairs.get()));
		PROVIDERS.put(DungeonSegmentModelBlockType.TRAPDOOR,
				(block, theme, rand, stage) -> block.create(theme.trapDoorDecoration.get()));
		PROVIDERS.put(DungeonSegmentModelBlockType.WALL, (block, theme, rand, stage) -> theme.wall.get());
		PROVIDERS.put(DungeonSegmentModelBlockType.WALL_LOG,
				(block, theme, rand, stage) -> block.create(theme.wallLog.get()));
		PROVIDERS.put(DungeonSegmentModelBlockType.DOOR, (block, theme, rand, stage) -> block.create(theme.door.get()));
		PROVIDERS.put(DungeonSegmentModelBlockType.OTHER, (block, theme, rand, stage) -> block
				.create(ForgeRegistries.BLOCKS.getValue(block.registryName).getDefaultState()));
	}

	public void readResourceLocation() {
		String[] resource = this.resourceName.split(":");
		this.registryName = new ResourceLocation(resource[0], resource[1]);
	}

	/**
	 * Creates a BlockState from a DungeonSegmentModelBlock using a
	 * DungeonSegmentBlockStateProvider from the provider-map.
	 */
	public static BlockState getBlockState(DungeonSegmentModelBlock block, Theme theme, Random rand, int stage) {
		DungeonSegmentBlockStateProvider provider = PROVIDERS.get(block.type);
		if (provider == null)
			return Blocks.CAVE_AIR.getDefaultState();
		BlockState state = provider.get(block, theme, rand, stage);
		if (state == null)
			return null;
		return state;
	}

	/**
	 * Creates a rotated BlockState from a DungeonSegmentModelBlock using a
	 * DungeonSegmentBlockStateProvider from the provider-map.
	 */
	public static BlockState getBlockState(DungeonSegmentModelBlock block, Theme theme, Random rand, int stage,
			Rotation rotation) {
		BlockState state = getBlockState(block, theme, rand, stage);
		return state == null ? null : state.rotate(rotation);
	}

	/**
	 * A functional interface used to generate a BlockState from a
	 * DungeonSegmentModelBlock.
	 */
	@FunctionalInterface
	public static interface DungeonSegmentBlockStateProvider {

		BlockState get(DungeonSegmentModelBlock block, Theme theme, Random rand, int stage);

	}

}
