/*
        Dungeon Crawl, a procedural dungeon generator for Minecraft 1.14 and later.
        Copyright (C) 2020

        This program is free software: you can redistribute it and/or modify
        it under the terms of the GNU General Public License as published by
        the Free Software Foundation, either version 3 of the License, or
        (at your option) any later version.

        This program is distributed in the hope that it will be useful,
        but WITHOUT ANY WARRANTY; without even the implied warranty of
        MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
        GNU General Public License for more details.

        You should have received a copy of the GNU General Public License
        along with this program.  If not, see <https://www.gnu.org/licenses/>.
*/

package xiroc.dungeoncrawl.util;

import net.minecraft.util.Direction;

public class Position2D {

    /**
     * A facing that can be brought together with the position.
     */
    public Direction facing;

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

    public boolean isValid(int bound) {
        return x > -1 && z > -1 && x < bound && z < bound;
    }

    /**
     * @return the direction you have to move to get from this position to the given
     * coordinates. X or Z have to be equal for this to make any sense.
     */
    public Direction directionTo(int x, int z) {
        if (x > this.x) {
            return Direction.EAST;
        }
        if (x < this.x) {
            return Direction.WEST;
        }
        if (z > this.z) {
            return Direction.SOUTH;
        }
        if (z < this.z) {
            return Direction.NORTH;
        }
        return null;
    }

    /**
     * @return the direction you have to move to get from this position to the given
     * one. X or Z have to be equal for this to make any sense.
     */
    public Direction directionTo(Position2D pos) {
        if (pos.x > this.x) {
            return Direction.EAST;
        }
        if (pos.x < this.x) {
            return Direction.WEST;
        }
        if (pos.z > this.z) {
            return Direction.SOUTH;
        }
        if (pos.z < this.z) {
            return Direction.NORTH;
        }
        return null;
    }

    /**
     * Returns true if x and z are greater than -1, x smaller than xBound and z
     * smaller than zBound. Used for DungeonLayer calculations.
     */
    public static boolean isValid(int x, int z, int xBound, int zBound) {
        return x > -1 && z > -1 && x < xBound && z < zBound;
    }

    public static boolean isValid(int x, int z, int bound) {
        return x > -1 && z > -1 && x < bound && z < bound;
    }

    /**
     * Creates a new position instance that is shifted by the given amount into the
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

    /**
     * Creates a new position instance that is shifted by the given amount into the
     * given direction. Used for DungeonLayer calculations.
     */
    public static Position2D shift(int x, int z, Direction direction, int amount) {
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
                return new Position2D(x, z);
        }
    }

    public boolean hasFacing() {
        return facing != null;
    }

}
