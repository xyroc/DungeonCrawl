package xiroc.dungeoncrawl.util;

import net.minecraft.util.Direction;
import net.minecraft.util.Rotation;

public class RotationHelper {
	
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

}
