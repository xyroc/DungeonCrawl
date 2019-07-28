package xiroc.dungeoncrawl.util;

import net.minecraft.block.BlockState;
import net.minecraft.block.FourWayBlock;
import net.minecraft.util.Direction;
import net.minecraft.util.Rotation;

public class RotationHelper {

	public static final Direction[] EAST_SOUTH_WEST = new Direction[] { Direction.EAST, Direction.SOUTH, Direction.WEST };
	public static final Direction[] EAST_NORTH_WEST = new Direction[] { Direction.EAST, Direction.NORTH, Direction.WEST };
	public static final Direction[] NORTH_SOUTH_EAST = new Direction[] { Direction.NORTH, Direction.SOUTH, Direction.EAST };
	public static final Direction[] NORTH_SOUTH_WEST = new Direction[] { Direction.NORTH, Direction.SOUTH, Direction.WEST };

	public static BlockState tanslateFourWayBlock(BlockState state, Rotation rotation) {
		boolean north = state.get(FourWayBlock.NORTH);
		boolean east = state.get(FourWayBlock.EAST);
		boolean south = state.get(FourWayBlock.SOUTH);
		boolean west = state.get(FourWayBlock.WEST);
		boolean waterlogged = state.get(FourWayBlock.WATERLOGGED);
		switch (rotation) {
		case NONE:
			return state;
		case CLOCKWISE_180:
			return state.with(FourWayBlock.NORTH, south).with(FourWayBlock.EAST, west).with(FourWayBlock.SOUTH, north).with(FourWayBlock.WEST, east).with(FourWayBlock.WATERLOGGED, waterlogged);
		case CLOCKWISE_90:
			return state.with(FourWayBlock.NORTH, west).with(FourWayBlock.EAST, north).with(FourWayBlock.SOUTH, east).with(FourWayBlock.WEST, south).with(FourWayBlock.WATERLOGGED, waterlogged);
		case COUNTERCLOCKWISE_90:
			return state.with(FourWayBlock.NORTH, east).with(FourWayBlock.EAST, south).with(FourWayBlock.SOUTH, west).with(FourWayBlock.WEST, north).with(FourWayBlock.WATERLOGGED, waterlogged);
		default:
			return state;
		}
	}

	public static Direction translateDirection(Direction direction, Rotation rotation) {
		switch (rotation) {
		case CLOCKWISE_180:
			return translateDirectionInverse(direction);
		case CLOCKWISE_90:
			return translateDirectionRight(direction);
		case COUNTERCLOCKWISE_90:
			return translateDirectionLeft(direction);
		case NONE:
			return direction;
		default:
			return direction;
		}
	}

	public static Direction translateDirectionRight(Direction direction) {
		switch (direction) {
		case DOWN:
			return Direction.DOWN;
		case UP:
			return Direction.UP;
		case EAST:
			return Direction.SOUTH;
		case NORTH:
			return Direction.EAST;
		case SOUTH:
			return Direction.WEST;
		case WEST:
			return Direction.NORTH;
		default:
			return null;
		}
	}

	public static Direction translateDirectionLeft(Direction direction) {
		switch (direction) {
		case DOWN:
			return Direction.DOWN;
		case UP:
			return Direction.UP;
		case EAST:
			return Direction.NORTH;
		case NORTH:
			return Direction.WEST;
		case SOUTH:
			return Direction.EAST;
		case WEST:
			return Direction.SOUTH;
		default:
			return null;
		}
	}

	public static Direction translateDirectionInverse(Direction direction) {
		switch (direction) {
		case DOWN:
			return Direction.DOWN;
		case UP:
			return Direction.UP;
		case EAST:
			return Direction.WEST;
		case NORTH:
			return Direction.SOUTH;
		case SOUTH:
			return Direction.NORTH;
		case WEST:
			return Direction.EAST;
		default:
			return null;
		}
	}

	public static Rotation getRotationFromFacing(Direction facing) {
		switch (facing) {
		case NORTH:
			return Rotation.CLOCKWISE_90;
		case EAST:
			return Rotation.NONE;
		case SOUTH:
			return Rotation.COUNTERCLOCKWISE_90;
		case WEST:
			return Rotation.CLOCKWISE_180;
		default:
			return Rotation.NONE;
		}
	}

	public static Rotation getOppositeRotationFromFacing(Direction facing) {
		switch (facing) {
		case NORTH:
			return Rotation.COUNTERCLOCKWISE_90;
		case EAST:
			return Rotation.CLOCKWISE_180;
		case SOUTH:
			return Rotation.CLOCKWISE_90;
		case WEST:
			return Rotation.NONE;
		default:
			return Rotation.NONE;
		}
	}

	public static Rotation getRotationFromCW90DoubleFacing(Direction dir1, Direction dir2) {
		switch (dir1) {
		case WEST:
			switch (dir2) {
			case SOUTH:
				return Rotation.NONE;
			case NORTH:
				return Rotation.CLOCKWISE_90;
			default:
				return Rotation.NONE;
			}
		case NORTH:
			switch (dir2) {
			case WEST:
				return Rotation.CLOCKWISE_90;
			case EAST:
				return Rotation.CLOCKWISE_180;
			default:
				return Rotation.NONE;
			}
		case EAST:
			switch (dir2) {
			case NORTH:
				return Rotation.CLOCKWISE_180;
			case SOUTH:
				return Rotation.COUNTERCLOCKWISE_90;
			default:
				return Rotation.NONE;
			}
		case SOUTH:
			switch (dir2) {
			case WEST:
				return Rotation.NONE;
			case EAST:
				return Rotation.COUNTERCLOCKWISE_90;
			default:
				return Rotation.NONE;
			}
		default:
			return Rotation.NONE;
		}
	}

	public static Rotation getRotationFromTripleFacing(Direction dir1, Direction dir2, Direction dir3) {
		if (containsAllThree(dir1, dir2, dir3, EAST_SOUTH_WEST))
			return Rotation.NONE;
		else if (containsAllThree(dir1, dir2, dir3, EAST_NORTH_WEST))
			return Rotation.CLOCKWISE_180;
		else if (containsAllThree(dir1, dir2, dir3, NORTH_SOUTH_EAST))
			return Rotation.COUNTERCLOCKWISE_90;
		else if (containsAllThree(dir1, dir2, dir3, NORTH_SOUTH_WEST))
			return Rotation.CLOCKWISE_90;
		return Rotation.NONE;

	}

	public static Rotation getRotationFromInt(int rotation) {
		switch (rotation) {
		case 0:
			return Rotation.NONE;
		case 1:
			return Rotation.CLOCKWISE_90;
		case 2:
			return Rotation.CLOCKWISE_180;
		case 3:
			return Rotation.COUNTERCLOCKWISE_90;
		default:
			return Rotation.NONE;
		}
	}

	public static int getIntFromRotation(Rotation rotation) {
		switch (rotation) {
		case CLOCKWISE_180:
			return 2;
		case CLOCKWISE_90:
			return 1;
		case COUNTERCLOCKWISE_90:
			return 3;
		case NONE:
			return 0;
		default:
			return 0;
		}
	}

	public static boolean containsAllThree(Direction dir1, Direction dir2, Direction dir3, Direction[] directions) {
		boolean d1 = false, d2 = false, d3 = false;
		for (Direction d : directions) {
			if (d == dir1)
				d1 = true;
			else if (d == dir2)
				d2 = true;
			else if (d == dir3)
				d3 = true;
		}
		return d1 && d2 && d3;
	}

}
