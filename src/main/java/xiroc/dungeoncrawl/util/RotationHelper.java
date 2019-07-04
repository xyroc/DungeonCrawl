package xiroc.dungeoncrawl.util;

import net.minecraft.block.BlockState;
import net.minecraft.block.FourWayBlock;
import net.minecraft.util.Direction;
import net.minecraft.util.Rotation;

public class RotationHelper {

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
			return Rotation.COUNTERCLOCKWISE_90;
		case EAST:
			return Rotation.NONE;
		case SOUTH:
			return Rotation.CLOCKWISE_90;
		case WEST:
			return Rotation.CLOCKWISE_180;
		default:
			return Rotation.NONE;
		}
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

}
