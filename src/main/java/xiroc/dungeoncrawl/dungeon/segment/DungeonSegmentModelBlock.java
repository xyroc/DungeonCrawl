package xiroc.dungeoncrawl.dungeon.segment;

/*
 * DungeonCrawl (C) 2019 XYROC (XIROC1337), All Rights Reserved 
 */

import java.util.Random;

import net.minecraft.block.AnvilBlock;
import net.minecraft.block.BarrelBlock;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.ChestBlock;
import net.minecraft.block.FourWayBlock;
import net.minecraft.block.HorizontalBlock;
import net.minecraft.block.RotatedPillarBlock;
import net.minecraft.block.StairsBlock;
import net.minecraft.block.TrapDoorBlock;
import net.minecraft.state.properties.Half;
import net.minecraft.util.Direction;
import net.minecraft.util.Rotation;
import xiroc.dungeoncrawl.part.block.BlockRegistry;
import xiroc.dungeoncrawl.theme.Theme;
import xiroc.dungeoncrawl.util.RotationHelper;

public class DungeonSegmentModelBlock {

	public DungeonSegmentModelBlockType type;
	public Direction facing;
	public Boolean upsideDown;

	public DungeonSegmentModelBlock(DungeonSegmentModelBlockType type) {
		this.type = type;
	}

	public DungeonSegmentModelBlock(DungeonSegmentModelBlockType type, Direction facing, boolean upsideDown) {
		if (facing == null && !upsideDown) {
			this.type = type;
			return;
		}
		this.type = type;
		this.facing = facing;
		this.upsideDown = upsideDown;
	}

	public static BlockState getBlockState(DungeonSegmentModelBlock block, Theme theme, Random rand) {
		if (block == null || block.type == null)
			return Blocks.CAVE_AIR.getDefaultState();
		switch (block.type) {
		case NONE:
			return null;
		case WOOD:
			return Blocks.OAK_PLANKS.getDefaultState();
		case WATER:
			return Blocks.WATER.getDefaultState();
		case LAVA:
			return Blocks.LAVA.getDefaultState();
		case IRON_BARS:
			DungeonSegmentModelFourWayBlock fwb = (DungeonSegmentModelFourWayBlock) block;
			return Blocks.IRON_BARS.getDefaultState().with(FourWayBlock.NORTH, fwb.north).with(FourWayBlock.EAST, fwb.east).with(FourWayBlock.SOUTH, fwb.south).with(FourWayBlock.WEST, fwb.west).with(FourWayBlock.WATERLOGGED,
					fwb.waterlogged);
		case CEILING:
			return theme.ceiling.get();
		case CEILING_STAIRS:
			return theme.ceilingStairs.get().with(StairsBlock.FACING, block.facing).with(StairsBlock.HALF, block.upsideDown ? Half.TOP : Half.BOTTOM);
		case CHEST_COMMON:
			return Blocks.CHEST.getDefaultState().with(ChestBlock.FACING, block.facing);
		case FLOOR:
			return theme.floor.get();
		case FLOOR_STAIRS:
			return theme.floorStairs.get().with(StairsBlock.FACING, block.facing).with(StairsBlock.HALF, block.upsideDown ? Half.TOP : Half.BOTTOM);
		case RAND_FLOOR_WATER:
			switch (rand.nextInt(2)) {
			case 0:
				return theme.floor.get();
			case 1:
				return Blocks.WATER.getDefaultState();
			}
		case TORCH_DARK:
			return theme.torchDark.get().with(HorizontalBlock.HORIZONTAL_FACING, block.facing);
		case OTHER:
			return null;
		case RAND_FLOOR_CHESTCOMMON_SPAWNER:
			int i = rand.nextInt(10);
			if (i < 2)
				return BlockRegistry.SPAWNER;
			if (i == 5)
				return Blocks.CHEST.getDefaultState().with(ChestBlock.FACING, block.facing);
			return theme.floor.get();
		case SPAWNER:
			return BlockRegistry.SPAWNER;
		case STAIRS:
			return theme.stairs.get().with(StairsBlock.FACING, block.facing).with(StairsBlock.HALF, block.upsideDown ? Half.TOP : Half.BOTTOM);
		case WALL:
			return theme.wall.get();
		case WALL_LOG:
			return theme.wallLog.get().with(RotatedPillarBlock.AXIS, Direction.Axis.Y);
		case BARREL:
			return Blocks.BARREL.getDefaultState().with(BarrelBlock.PROPERTY_FACING, block.facing);
		case ANVIL:
			return Blocks.ANVIL.getDefaultState().with(AnvilBlock.FACING, block.facing);
		default:
			if (block instanceof DungeonSegmentModelTrapDoorBlock)
				return theme.trapDoorDecoration.get().with(TrapDoorBlock.OPEN, ((DungeonSegmentModelTrapDoorBlock) block).open).with(TrapDoorBlock.HALF, block.upsideDown ? Half.TOP : Half.BOTTOM).with(HorizontalBlock.HORIZONTAL_FACING,
						block.facing);
			return Blocks.AIR.getDefaultState();
		}
	}

	public static BlockState getBlockState(DungeonSegmentModelBlock block, Theme theme, Random rand, Rotation rotation) {
		if (block == null || block.type == null)
			return Blocks.CAVE_AIR.getDefaultState();
		switch (block.type) {
		case NONE:
			return null;
		case WOOD:
			return Blocks.OAK_PLANKS.getDefaultState();
		case WATER:
			return Blocks.WATER.getDefaultState();
		case LAVA:
			return Blocks.LAVA.getDefaultState();
		case IRON_BARS:
			return RotationHelper.tanslateFourWayBlock(getBlockState(block, theme, rand), rotation);
		case CEILING:
			return theme.ceiling.get();
		case CEILING_STAIRS:
			return theme.ceilingStairs.get().with(StairsBlock.FACING, RotationHelper.translateDirection(block.facing, rotation)).with(StairsBlock.HALF, block.upsideDown ? Half.TOP : Half.BOTTOM);
		case CHEST_COMMON:
			return Blocks.CHEST.getDefaultState().with(ChestBlock.FACING, block.facing);
		case FLOOR:
			return theme.floor.get();
		case FLOOR_STAIRS:
			return theme.floorStairs.get().with(StairsBlock.FACING, RotationHelper.translateDirection(block.facing, rotation)).with(StairsBlock.HALF, block.upsideDown ? Half.TOP : Half.BOTTOM);
		case RAND_FLOOR_WATER:
			switch (rand.nextInt(2)) {
			case 0:
				return theme.floor.get();
			case 1:
				return Blocks.WATER.getDefaultState();
			}
		case TORCH_DARK:
			return theme.torchDark.get().with(HorizontalBlock.HORIZONTAL_FACING, RotationHelper.translateDirection(block.facing, rotation));
		case OTHER:
			return null;
		case RAND_FLOOR_CHESTCOMMON_SPAWNER:
			int i = rand.nextInt(10);
			if (i < 2)
				return BlockRegistry.SPAWNER;
			if (i == 5)
				return Blocks.CHEST.getDefaultState().with(ChestBlock.FACING, RotationHelper.translateDirection(block.facing, rotation));
			return theme.floor.get();
		case SPAWNER:
			return BlockRegistry.SPAWNER;
		case STAIRS:
			return theme.stairs.get().with(StairsBlock.FACING, RotationHelper.translateDirection(block.facing, rotation)).with(StairsBlock.HALF, block.upsideDown ? Half.TOP : Half.BOTTOM);
		case WALL:
			return theme.wall.get();
		case WALL_LOG:
			return theme.wallLog.get().with(RotatedPillarBlock.AXIS, Direction.Axis.Y);
		case BARREL:
			return Blocks.BARREL.getDefaultState().with(BarrelBlock.PROPERTY_FACING, RotationHelper.translateDirection(block.facing, rotation));
		case ANVIL:
			return Blocks.ANVIL.getDefaultState().with(AnvilBlock.FACING, RotationHelper.translateDirection(block.facing, rotation));
		default:
			if (block instanceof DungeonSegmentModelTrapDoorBlock)
				return theme.trapDoorDecoration.get().with(TrapDoorBlock.OPEN, ((DungeonSegmentModelTrapDoorBlock) block).open).with(TrapDoorBlock.HALF, block.upsideDown ? Half.TOP : Half.BOTTOM).with(HorizontalBlock.HORIZONTAL_FACING,
						RotationHelper.translateDirection(block.facing, rotation));
			return Blocks.AIR.getDefaultState();
		}
	}

}
