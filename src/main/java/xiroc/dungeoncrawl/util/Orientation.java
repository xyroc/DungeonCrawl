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
import net.minecraft.world.level.block.Rotation;

public interface Orientation {
    Direction[] HORIZONTAL_FACINGS = new Direction[]{Direction.NORTH, Direction.EAST, Direction.SOUTH, Direction.WEST};

    static Rotation horizontalRotation(Direction from, Direction to) {
        if (from.getAxis() == Direction.Axis.Y || to.getAxis() == Direction.Axis.Y) {
            throw new IllegalArgumentException();
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

    static Rotation rotationFromInt(int rotation) {
        return switch (rotation) {
            case 1 -> Rotation.CLOCKWISE_90;
            case 2 -> Rotation.CLOCKWISE_180;
            case 3 -> Rotation.COUNTERCLOCKWISE_90;
            default -> Rotation.NONE;
        };
    }

    static int rotationToInt(Rotation rotation) {
        return switch (rotation) {
            case CLOCKWISE_180 -> 2;
            case CLOCKWISE_90 -> 1;
            case COUNTERCLOCKWISE_90 -> 3;
            default -> 0;
        };
    }
}