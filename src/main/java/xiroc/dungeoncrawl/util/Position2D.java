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

import net.minecraft.core.Direction;

public class Position2D {

    public final int x, z;

    public Position2D(int x, int z) {
        this.x = x;
        this.z = z;
    }

    /**
     * Returns true if x and z are greater than -1, x smaller than xBound and z
     * smaller than zBound.
     */

    public boolean isValid(int xBound, int zBound) {
        return x > -1 && z > -1 && x < xBound && z < zBound;
    }

    public boolean isValid(int bound) {
        return x > -1 && z > -1 && x < bound && z < bound;
    }

    /**
     * @return the direction you have to move to get from this position to the given
     * one. Either X or Z have to be equal.
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
        throw new IllegalArgumentException("The target position must not be equal to the origin position.");
    }

    /**
     * Returns true if x and z are greater than or equals zero, x smaller than xBound and z
     * smaller than zBound.
     */
    public static boolean isValid(int x, int z, int xBound, int zBound) {
        return x >= 0 && z >= 0 && x < xBound && z < zBound;
    }

    /**
     * Creates a new position instance that is shifted by the given amount into the
     * given direction.
     */
    public Position2D shift(Direction direction, int amount) {
        return switch (direction) {
            case NORTH -> new Position2D(x, z - amount);
            case EAST -> new Position2D(x + amount, z);
            case SOUTH -> new Position2D(x, z + amount);
            case WEST -> new Position2D(x - amount, z);
            default -> this;
        };
    }

}
