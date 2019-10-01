package xiroc.dungeoncrawl.util;

import net.minecraft.util.Direction;

/*
 * DungeonCrawl (C) 2019 XYROC (XIROC1337), All Rights Reserved 
 */

public class Position2D {

	public int x, z;

	public Position2D(int x, int z) {
		this.x = x;
		this.z = z;
	}

	/**
	 * Returns true if x and z are greater than -1, x smaller than xBound and z
	 * smaller than zBound. Used for DungeonLayer calculations.
	 */

	public boolean isValid(int xBound, int zBound) {
		return x > -1 && z > -1 && x < xBound && z < zBound;
	}

	/**
	 * Creates a new position instance that is shifted by the given amount in the
	 * given direction. Used for DungeonLayer calculations.
	 */
	public Position2D shift(Direction direction, int amount) {
		switch (direction) {
		case NORTH:
			return new Position2D(x, z - amount);
		case EAST:
			return new Position2D(x + amount, z);
		case SOUTH:
			return new Position2D(x, z + amount);
		case WEST:
			return new Position2D(x - amount, z);
		default:
			return this;
		}
	}

}
