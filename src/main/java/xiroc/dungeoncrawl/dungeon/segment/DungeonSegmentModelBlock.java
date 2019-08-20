package xiroc.dungeoncrawl.dungeon.segment;

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
import net.minecraft.state.IProperty;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.state.properties.Half;
import net.minecraft.util.Direction;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Rotation;
import net.minecraftforge.registries.ForgeRegistries;
import xiroc.dungeoncrawl.part.block.BlockRegistry;
import xiroc.dungeoncrawl.theme.Theme;
import xiroc.dungeoncrawl.util.IDungeonSegmentBlockStateProvider;

public class DungeonSegmentModelBlock {

//	public static final Set<IProperty<?>> PROPERTIES = ImmutableSet.<IProperty<?>>builder().add(BlockStateProperties.FACING).add(BlockStateProperties.HORIZONTAL_FACING).add(BlockStateProperties.AXIS)
//			.add(BlockStateProperties.HORIZONTAL_AXIS).add(BlockStateProperties.NORTH).add(BlockStateProperties.EAST).add(BlockStateProperties.SOUTH).add(BlockStateProperties.WEST).add(BlockStateProperties.INVERTED)
//			.add(BlockStateProperties.DISARMED).add(BlockStateProperties.ATTACHED).add(BlockStateProperties.LIT).build();
	public static HashMap<DungeonSegmentModelBlockType, IDungeonSegmentBlockStateProvider> PROVIDERS = new HashMap<DungeonSegmentModelBlockType, IDungeonSegmentBlockStateProvider>();

	public DungeonSegmentModelBlockType type;

	public Direction facing;
	public Direction.Axis axis;
	public Boolean upsideDown, north, east, south, west, waterlogged, lit, open, disarmed, attached;

	public ResourceLocation registryName;
	public Half half;

	public DungeonSegmentModelBlock(DungeonSegmentModelBlockType type) {
		this.type = type;
	}

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

//		setIfExists(state, BlockStateProperties.FACING, facing);
//		setIfExists(state, BlockStateProperties.HORIZONTAL_FACING, facing);
//		setIfExists(state, BlockStateProperties.AXIS, axis);
//		setIfExists(state, BlockStateProperties.HORIZONTAL_AXIS, axis);
//		setIfExists(state, BlockStateProperties.NORTH, north);
//		setIfExists(state, BlockStateProperties.EAST, east);
//		setIfExists(state, BlockStateProperties.SOUTH, south);
//		setIfExists(state, BlockStateProperties.WEST, west);
//		setIfExists(state, BlockStateProperties.WATERLOGGED, waterlogged);
//		setIfExists(state, BlockStateProperties.INVERTED, upsideDown);
//		setIfExists(state, BlockStateProperties.DISARMED, disarmed);
//		setIfExists(state, BlockStateProperties.ATTACHED, attached);
//		setIfExists(state, BlockStateProperties.LIT, lit);
//		setIfExists(state, BlockStateProperties.HALF, half);
		if (type == DungeonSegmentModelBlockType.OTHER)
			registryName = state.getBlock().getRegistryName();
		return this;
	}

	public void setIfExists(BlockState state, IProperty<?> property, Object value) {
		if (state.has(property))
			value = state.get(property);
	}

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
		return state;
	}

	// static stuff

	public static void load() {
		PROVIDERS.put(DungeonSegmentModelBlockType.NONE, (block, theme, rand) -> null);
//		PROVIDERS.put(DungeonSegmentModelBlockType.ANVIL, (block, theme, rand) -> Blocks.ANVIL.getDefaultState().with(AnvilBlock.FACING, block.facing));
		PROVIDERS.put(DungeonSegmentModelBlockType.BARREL, (block, theme, rand) -> Blocks.BARREL.getDefaultState().with(BarrelBlock.PROPERTY_FACING, block.facing));
		PROVIDERS.put(DungeonSegmentModelBlockType.CEILING, (block, theme, rand) -> theme.ceiling.get());
		PROVIDERS.put(DungeonSegmentModelBlockType.CEILING_STAIRS, (block, theme, rand) -> block.create(theme.ceilingStairs.get()));
		PROVIDERS.put(DungeonSegmentModelBlockType.CHEST_COMMON, (block, theme, rand) -> Blocks.CHEST.getDefaultState().with(ChestBlock.FACING, block.facing));
		PROVIDERS.put(DungeonSegmentModelBlockType.DISPENSER, (block, theme, rand) -> Blocks.DISPENSER.getDefaultState().with(DispenserBlock.FACING, block.facing));
		PROVIDERS.put(DungeonSegmentModelBlockType.FLOOR, (block, theme, rand) -> theme.floor.get());
		PROVIDERS.put(DungeonSegmentModelBlockType.FLOOR_STAIRS, (block, theme, rand) -> block.create(theme.floorStairs.get()));
//		PROVIDERS.put(DungeonSegmentModelBlockType.FURNACE, (block, theme, rand) -> Blocks.FURNACE.getDefaultState().with(FurnaceBlock.FACING, block.facing).with(FurnaceBlock.LIT, true));
//		PROVIDERS.put(DungeonSegmentModelBlockType.IRON_BARS, (block, theme, rand) -> {
//			return Blocks.IRON_BARS.getDefaultState().with(FourWayBlock.NORTH, block.north).with(FourWayBlock.EAST, block.east).with(FourWayBlock.SOUTH, block.south).with(FourWayBlock.WEST, block.west).with(FourWayBlock.WATERLOGGED,
//					block.waterlogged);
//		});
//		PROVIDERS.put(DungeonSegmentModelBlockType.LAVA, (block, theme, rand) -> Blocks.LAVA.getDefaultState());
		PROVIDERS.put(DungeonSegmentModelBlockType.RAND_FLOOR_CHESTCOMMON_SPAWNER, (block, theme, rand) -> {
			int i = rand.nextInt(10);
			if (i < 2)
				return BlockRegistry.SPAWNER;
			if (i == 5)
				return Blocks.CHEST.getDefaultState().with(ChestBlock.FACING, block.facing);
			return theme.floor.get();
		});
		PROVIDERS.put(DungeonSegmentModelBlockType.RAND_FLOOR_LAVA, (block, theme, rand) -> {
			switch (rand.nextInt(2)) {
			case 0:
				return theme.floor.get();
			case 1:
				return Blocks.LAVA.getDefaultState();
			default:
				return Blocks.LAVA.getDefaultState();
			}
		});
		PROVIDERS.put(DungeonSegmentModelBlockType.RAND_FLOOR_WATER, (block, theme, rand) -> {
			switch (rand.nextInt(2)) {
			case 0:
				return theme.floor.get();
			case 1:
				return Blocks.WATER.getDefaultState();
			default:
				return Blocks.WATER.getDefaultState();
			}
		});
		PROVIDERS.put(DungeonSegmentModelBlockType.RAND_WALL_AIR, (block, theme, rand) -> {
			if (rand.nextFloat() < 0.75)
				return theme.wall.get();
			return Blocks.CAVE_AIR.getDefaultState();
		});
		PROVIDERS.put(DungeonSegmentModelBlockType.RAND_WALL_SPAWNER, (block, theme, rand) -> {
			switch (rand.nextInt(2)) {
			case 0:
				return BlockRegistry.SPAWNER;
			case 1:
				return theme.wall.get();
			default:
				return BlockRegistry.SPAWNER;
			}
		});
		PROVIDERS.put(DungeonSegmentModelBlockType.STAIRS, (block, theme, rand) -> block.create(theme.stairs.get()));
//		PROVIDERS.put(DungeonSegmentModelBlockType.TORCH_DARK, (block, theme, rand) -> theme.torchDark.get().with(HorizontalBlock.HORIZONTAL_FACING, block.facing));
		PROVIDERS.put(DungeonSegmentModelBlockType.TRAPDOOR,
				(block, theme, rand) -> block.create(theme.trapDoorDecoration.get()));
//		PROVIDERS.put(DungeonSegmentModelBlockType.TRIPWIRE, (block, theme, rand) -> Blocks.TRIPWIRE.getDefaultState().with(TripWireBlock.DISARMED, false));
//		PROVIDERS.put(DungeonSegmentModelBlockType.TRIPWIRE_HOOK, (block, theme, rand) -> Blocks.TRIPWIRE_HOOK.getDefaultState().with(TripWireHookBlock.FACING, block.facing).with(TripWireHookBlock.ATTACHED, true));
		PROVIDERS.put(DungeonSegmentModelBlockType.WALL, (block, theme, rand) -> theme.wall.get());
		PROVIDERS.put(DungeonSegmentModelBlockType.WALL_LOG, (block, theme, rand) -> block.create(theme.wallLog.get()));
//		PROVIDERS.put(DungeonSegmentModelBlockType.WATER, (block, theme, rand) -> Blocks.WATER.getDefaultState());
//		PROVIDERS.put(DungeonSegmentModelBlockType.WOOD, (block, theme, rand) -> Blocks.OAK_PLANKS.getDefaultState());
//		PROVIDERS.put(DungeonSegmentModelBlockType.REDSTONE_TORCH, (block, theme, rand) -> Blocks.REDSTONE_WALL_TORCH.getDefaultState().with(RedstoneTorchBlock.LIT, block.lit));
//		PROVIDERS.put(DungeonSegmentModelBlockType.REDSTONE, (block, theme, rand) -> Blocks.REDSTONE_WIRE.getDefaultState());
//		PROVIDERS.put(DungeonSegmentModelBlockType.STICKY_PISTON, (block, theme, rand) -> Blocks.STICKY_PISTON.getDefaultState().with(PistonBlock.FACING, block.facing));
		PROVIDERS.put(DungeonSegmentModelBlockType.OTHER, (block, theme, rand) -> block.create(ForgeRegistries.BLOCKS.getValue(block.registryName).getDefaultState()));
	}

	public static BlockState getBlockState(DungeonSegmentModelBlock block, Theme theme, Random rand) {
		IDungeonSegmentBlockStateProvider provider = PROVIDERS.get(block.type);
		if (provider == null)
			return Blocks.CAVE_AIR.getDefaultState();
		BlockState state = provider.get(block, theme, rand);
		if (state == null)
			return null;
		return state;
//		if (block == null || block.type == null)
//			return Blocks.CAVE_AIR.getDefaultState();
//		switch (block.type) {
//		case NONE:
//			return null;
//		case WOOD:
//			return Blocks.OAK_PLANKS.getDefaultState();
//		case WATER:
//			return Blocks.WATER.getDefaultState();
//		case LAVA:
//			return Blocks.LAVA.getDefaultState();
//		case IRON_BARS:
//			DungeonSegmentModelFourWayBlock fwb = (DungeonSegmentModelFourWayBlock) block;
//			return Blocks.IRON_BARS.getDefaultState().with(FourWayBlock.NORTH, fwb.north).with(FourWayBlock.EAST, fwb.east).with(FourWayBlock.SOUTH, fwb.south).with(FourWayBlock.WEST, fwb.west).with(FourWayBlock.WATERLOGGED,
//					fwb.waterlogged);
//		case CEILING:
//			return theme.ceiling.get();
//		case CEILING_STAIRS:
//			return theme.ceilingStairs.get().with(StairsBlock.FACING, block.facing).with(StairsBlock.HALF, block.upsideDown ? Half.TOP : Half.BOTTOM);
//		case CHEST_COMMON:
//			return Blocks.CHEST.getDefaultState().with(ChestBlock.FACING, block.facing);
//		case FLOOR:
//			return theme.floor.get();
//		case FLOOR_STAIRS:
//			return theme.floorStairs.get().with(StairsBlock.FACING, block.facing).with(StairsBlock.HALF, block.upsideDown ? Half.TOP : Half.BOTTOM);
//		case RAND_FLOOR_WATER:
//			switch (rand.nextInt(2)) {
//			case 0:
//				return theme.floor.get();
//			case 1:
//				return Blocks.WATER.getDefaultState();
//			}
//		case RAND_FLOOR_LAVA:
//			switch (rand.nextInt(2)) {
//			case 0:
//				return theme.floor.get();
//			case 1:
//				return Blocks.LAVA.getDefaultState();
//			}
//		case TORCH_DARK:
//			return theme.torchDark.get().with(HorizontalBlock.HORIZONTAL_FACING, block.facing);
//		case FURNACE:
//			return Blocks.FURNACE.getDefaultState().with(FurnaceBlock.LIT, true).with(FurnaceBlock.FACING, block.facing);
//		case TRIPWIRE:
//			return Blocks.TRIPWIRE.getDefaultState().with(TripWireBlock.DISARMED, false);
//		case TRIPWIRE_HOOK:
//			return Blocks.TRIPWIRE_HOOK.getDefaultState().with(TripWireHookBlock.FACING, block.facing).with(TripWireHookBlock.ATTACHED, true);
//		case DISPENSER:
//			return Blocks.DISPENSER.getDefaultState().with(DispenserBlock.FACING, block.facing);
//		case OTHER:
//			return null;
//		case RAND_FLOOR_CHESTCOMMON_SPAWNER:
//			int i = rand.nextInt(10);
//			if (i < 2)
//				return BlockRegistry.SPAWNER;
//			if (i == 5)
//				return Blocks.CHEST.getDefaultState().with(ChestBlock.FACING, block.facing);
//			return theme.floor.get();
//		case RAND_WALL_SPAWNER:
//			switch (rand.nextInt(2)) {
//			case 0:
//				return BlockRegistry.SPAWNER;
//			case 1:
//				return theme.wall.get();
//			}
//		case STAIRS:
//			return theme.stairs.get().with(StairsBlock.FACING, block.facing).with(StairsBlock.HALF, block.upsideDown ? Half.TOP : Half.BOTTOM);
//		case WALL:
//			return theme.wall.get();
//		case WALL_LOG:
//			return theme.wallLog.get().with(RotatedPillarBlock.AXIS, Direction.Axis.Y);
//		case BARREL:
//			return Blocks.BARREL.getDefaultState().with(BarrelBlock.PROPERTY_FACING, block.facing);
//		case ANVIL:
//			return Blocks.ANVIL.getDefaultState().with(AnvilBlock.FACING, block.facing);
//		default:
//			if (block instanceof DungeonSegmentModelTrapDoorBlock)
//				return theme.trapDoorDecoration.get().with(TrapDoorBlock.OPEN, ((DungeonSegmentModelTrapDoorBlock) block).open).with(TrapDoorBlock.HALF, block.upsideDown ? Half.TOP : Half.BOTTOM).with(HorizontalBlock.HORIZONTAL_FACING,
//						block.facing);
////			if (block instanceof DungeonSegmentModelTripWireBlock) {
////				DungeonSegmentModelTripWireBlock tripwire = (DungeonSegmentModelTripWireBlock) block;
////				return Blocks.TRIPWIRE.getDefaultState().with(TripWireBlock.NORTH, tripwire.north).with(TripWireBlock.EAST, tripwire.east).with(TripWireBlock.SOUTH, tripwire.south).with(TripWireBlock.WEST, tripwire.west)
////						.with(TripWireBlock.DISARMED, false);
////			}
//			return Blocks.AIR.getDefaultState();
//		}
	}

	public static BlockState getBlockState(DungeonSegmentModelBlock block, Theme theme, Random rand, Rotation rotation) {
		IDungeonSegmentBlockStateProvider provider = PROVIDERS.get(block.type);
		if (provider == null)
			return Blocks.CAVE_AIR.getDefaultState();
		BlockState state = provider.get(block, theme, rand);
		if (state == null)
			return null;
		return state.rotate(rotation);

//		if (block == null || block.type == null)
//			return Blocks.CAVE_AIR.getDefaultState();
//		switch (block.type) {
//		case NONE:
//			return null;
//		case WOOD:
//			return Blocks.OAK_PLANKS.getDefaultState();
//		case WATER:
//			return Blocks.WATER.getDefaultState();
//		case LAVA:
//			return Blocks.LAVA.getDefaultState();
//		case IRON_BARS:
//			return RotationHelper.tanslateFourWayBlock(getBlockState(block, theme, rand), rotation);
//		case CEILING:
//			return theme.ceiling.get();
//		case CEILING_STAIRS:
//			return theme.ceilingStairs.get().with(StairsBlock.FACING, RotationHelper.translateDirection(block.facing, rotation)).with(StairsBlock.HALF, block.upsideDown ? Half.TOP : Half.BOTTOM);
//		case CHEST_COMMON:
//			return Blocks.CHEST.getDefaultState().with(ChestBlock.FACING, block.facing);
//		case FLOOR:
//			return theme.floor.get();
//		case FLOOR_STAIRS:
//			return theme.floorStairs.get().with(StairsBlock.FACING, RotationHelper.translateDirection(block.facing, rotation)).with(StairsBlock.HALF, block.upsideDown ? Half.TOP : Half.BOTTOM);
//		case RAND_FLOOR_WATER:
//			switch (rand.nextInt(2)) {
//			case 0:
//				return theme.floor.get();
//			case 1:
//				return Blocks.WATER.getDefaultState();
//			}
//		case RAND_FLOOR_LAVA:
//			switch (rand.nextInt(2)) {
//			case 0:
//				return theme.floor.get();
//			case 1:
//				return Blocks.LAVA.getDefaultState();
//			}
//		case TORCH_DARK:
//			return theme.torchDark.get().with(HorizontalBlock.HORIZONTAL_FACING, RotationHelper.translateDirection(block.facing, rotation));
//		case FURNACE:
//			return Blocks.FURNACE.getDefaultState().with(FurnaceBlock.LIT, true).with(FurnaceBlock.FACING, RotationHelper.translateDirection(block.facing, rotation));
//		case TRIPWIRE:
//			return Blocks.TRIPWIRE.getDefaultState().with(TripWireBlock.DISARMED, false);
//		case TRIPWIRE_HOOK:
//			return Blocks.TRIPWIRE_HOOK.getDefaultState().with(TripWireHookBlock.FACING, RotationHelper.translateDirection(block.facing, rotation)).with(TripWireHookBlock.ATTACHED, true);
//		case DISPENSER:
//			return Blocks.DISPENSER.getDefaultState().with(DispenserBlock.FACING, RotationHelper.translateDirection(block.facing, rotation));
//		case OTHER:
//			return null;
//		case RAND_FLOOR_CHESTCOMMON_SPAWNER:
//			int i = rand.nextInt(10);
//			if (i < 2)
//				return BlockRegistry.SPAWNER;
//			if (i == 5)
//				return Blocks.CHEST.getDefaultState().with(ChestBlock.FACING, RotationHelper.translateDirection(block.facing, rotation));
//			return theme.floor.get();
//		case RAND_WALL_SPAWNER:
//			switch (rand.nextInt(2)) {
//			case 0:
//				return BlockRegistry.SPAWNER;
//			case 1:
//				return theme.wall.get();
//			}
//		case STAIRS:
//			return theme.stairs.get().with(StairsBlock.FACING, RotationHelper.translateDirection(block.facing, rotation)).with(StairsBlock.HALF, block.upsideDown ? Half.TOP : Half.BOTTOM);
//		case WALL:
//			return theme.wall.get();
//		case WALL_LOG:
//			return theme.wallLog.get().with(RotatedPillarBlock.AXIS, Direction.Axis.Y);
//		case BARREL:
//			return Blocks.BARREL.getDefaultState().with(BarrelBlock.PROPERTY_FACING, RotationHelper.translateDirection(block.facing, rotation));
//		case ANVIL:
//			return Blocks.ANVIL.getDefaultState().with(AnvilBlock.FACING, RotationHelper.translateDirection(block.facing, rotation));
//		default:
//			if (block instanceof DungeonSegmentModelTrapDoorBlock)
//				return theme.trapDoorDecoration.get().with(TrapDoorBlock.OPEN, ((DungeonSegmentModelTrapDoorBlock) block).open).with(TrapDoorBlock.HALF, block.upsideDown ? Half.TOP : Half.BOTTOM).with(HorizontalBlock.HORIZONTAL_FACING,
//						RotationHelper.translateDirection(block.facing, rotation));
//			return Blocks.AIR.getDefaultState();
//		}
	}

}
