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
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.Rotation;

public interface Orientation {
    Direction[] HORIZONTAL_DIRECTIONS = {
            Direction.NORTH,
            Direction.EAST,
            Direction.SOUTH,
            Direction.WEST
    };

    /**
     * Represents a uniform distribution over all horizontal directions.
     *
     * @param random The source of randomness.
     * @return A random horizontal direction.
     */
    static Direction randomHorizontalDirection(RandomSource random) {
        return HORIZONTAL_DIRECTIONS[random.nextInt(HORIZONTAL_DIRECTIONS.length)];
    }

    /**
     * Determines the {@link Rotation} that must be applied to {@link Direction} {@code from} to end up at {@link Direction} {@code to}.
     * Both directions must be horizontal.
     *
     * @param from the direction to start off.
     * @param to   the direction we want to end up at.
     * @return The rotation necessary to do so.
     * @throws IllegalArgumentException if either of the two directions is not horizontal.
     */
    static Rotation horizontalRotation(Direction from, Direction to) {
        if (from.getAxis() == Direction.Axis.Y || to.getAxis() == Direction.Axis.Y) {
            throw new IllegalArgumentException("Directions must be horizontal");
        }
        if (to == from) {
            return Rotation.NONE;
        }
        if (to == from.getOpposite()) {
            return Rotation.CLOCKWISE_180;
        }
        if (to == from.getClockWise()) {
            return Rotation.CLOCKWISE_90;
        }
        return Rotation.COUNTERCLOCKWISE_90;
    }

    /**
     * Determines the number of 90 degree clockwise rotations needed to go from direction {@code from} to direction {@code to}.
     *
     * @param from The direction to start off.
     * @param to   The goal.
     * @return The number of rotations.
     */
    static int numberOfClockwise90DegreeRotations(Direction from, Direction to) {
        // Rotations are ordered NONE (0) -> 90 Deg CLK (1) -> 180 Deg (2) -> 90 Deg C-CLK (3)
        return horizontalRotation(from, to).ordinal();
    }
}