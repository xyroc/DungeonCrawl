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
import net.minecraft.core.Vec3i;
import net.minecraft.world.level.block.Rotation;
import xiroc.dungeoncrawl.dungeon.blueprint.Blueprint;

public class Orientation {
    public static final Direction[] HORIZONTAL_FACINGS = new Direction[]{Direction.NORTH, Direction.EAST, Direction.SOUTH,
            Direction.WEST};

    public static Vec3i rotatedMultipartOffset(Blueprint parent, Blueprint multipart, Vec3i offset, Rotation parentRotation, Rotation fullRotation) {
        int ordinalBit = fullRotation.ordinal() & 1;
        switch (parentRotation) {
            case CLOCKWISE_90: {
                int multipartLength = ordinalBit == 0 ? multipart.xSpan() : multipart.zSpan();
                return new Vec3i(parent.zSpan() - offset.getZ() - multipartLength, offset.getY(), offset.getX());
            }
            case CLOCKWISE_180: {
                int multipartWidth = ordinalBit == 1 ? multipart.zSpan() : multipart.xSpan();
                int multipartLength = ordinalBit == 1 ? multipart.xSpan() : multipart.zSpan();
                return new Vec3i(parent.xSpan() - offset.getX() - multipartWidth, offset.getY(), parent.zSpan() - offset.getZ() - multipartLength);
            }
            case COUNTERCLOCKWISE_90: {
                int multipartWidth = ordinalBit == 0 ? multipart.zSpan() : multipart.xSpan();
                return new Vec3i(offset.getZ(), offset.getY(), parent.xSpan() - offset.getX() - multipartWidth);
            }
            default:
                return offset;
        }
    }

    public static Rotation getOppositeRotation(Rotation rotation) {
        switch (rotation) {
            case CLOCKWISE_90:
                return Rotation.COUNTERCLOCKWISE_90;
            case COUNTERCLOCKWISE_90:
                return Rotation.CLOCKWISE_90;
            default:
                return rotation;
        }
    }

    public static Rotation getRotation(int rotation) {
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

    public static int rotationAsInt(Rotation rotation) {
        switch (rotation) {
            case CLOCKWISE_180:
                return 2;
            case CLOCKWISE_90:
                return 1;
            case COUNTERCLOCKWISE_90:
                return 3;
            default:
                return 0;
        }
    }
}