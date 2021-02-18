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
import net.minecraft.util.Rotation;
import net.minecraft.util.math.Vec3i;
import xiroc.dungeoncrawl.dungeon.model.DungeonModel;

public class Orientation {

    private static final Direction[] FACINGS = new Direction[]{Direction.NORTH, Direction.EAST, Direction.SOUTH,
            Direction.WEST, Direction.DOWN, Direction.UP};

    public static final IRandom<Direction> RANDOM_FACING = (rand) -> FACINGS[rand.nextInt(FACINGS.length)];
    public static final IRandom<Direction> RANDOM_HORIZONTAL_FACING = (rand) -> FACINGS[rand.nextInt(4)];

    public static final Direction[] FLAT_FACINGS = new Direction[]{Direction.NORTH, Direction.EAST, Direction.SOUTH,
            Direction.WEST};

    public static final Direction[] EAST_SOUTH_WEST = new Direction[]{Direction.EAST, Direction.SOUTH,
            Direction.WEST};
    public static final Direction[] EAST_NORTH_WEST = new Direction[]{Direction.EAST, Direction.NORTH,
            Direction.WEST};
    public static final Direction[] NORTH_SOUTH_EAST = new Direction[]{Direction.NORTH, Direction.SOUTH,
            Direction.EAST};
    public static final Direction[] NORTH_SOUTH_WEST = new Direction[]{Direction.NORTH, Direction.SOUTH,
            Direction.WEST};

    public static Vec3i rotatedMultipartOffset(DungeonModel parent, DungeonModel multipart, Vec3i offset, Rotation parentRotation, Rotation fullRotation) {
        int ordinalBit = fullRotation.ordinal() & 1;
        switch (parentRotation) {
            case CLOCKWISE_90: {
                int multipartLength = ordinalBit == 0 ? multipart.width : multipart.length;
                return new Vec3i(parent.length - offset.getZ() - multipartLength, offset.getY(), offset.getX());
            }
            case CLOCKWISE_180: {
                int multipartWidth = ordinalBit == 1 ? multipart.length : multipart.width;
                int multipartLength = ordinalBit == 1 ? multipart.width : multipart.length;
                return new Vec3i(parent.width - offset.getX() - multipartWidth, offset.getY(), parent.length - offset.getZ() - multipartLength);
            }
            case COUNTERCLOCKWISE_90: {
                int multipartWidth = ordinalBit == 0 ? multipart.length : multipart.width;
                return new Vec3i(offset.getZ(), offset.getY(), parent.width - offset.getX() - multipartWidth);
            }
            default:
                return offset;
        }
    }

    public static Direction horizontalOpposite(Direction direction) {
        switch (direction) {
            case NORTH:
                return Direction.SOUTH;
            case EAST:
                return Direction.WEST;
            case SOUTH:
                return Direction.NORTH;
            case WEST:
                return Direction.EAST;
            default:
                return direction;
        }
    }

    public static Direction rotateY(Direction direction) {
        switch (direction) {
            case NORTH:
                return Direction.EAST;
            case EAST:
                return Direction.SOUTH;
            case SOUTH:
                return Direction.WEST;
            case WEST:
                return Direction.NORTH;
            default:
                return direction;
        }
    }

    public static Direction rotateYCCW(Direction direction) {
        switch (direction) {
            case NORTH:
                return Direction.WEST;
            case EAST:
                return Direction.NORTH;
            case SOUTH:
                return Direction.EAST;
            case WEST:
                return Direction.SOUTH;
            default:
                return direction;
        }
    }

    public static Direction[] getHorizontalFacingsWithout(Direction excludedDirection) {
        switch (excludedDirection) {
            case NORTH:
                return EAST_SOUTH_WEST;
            case EAST:
                return NORTH_SOUTH_WEST;
            case SOUTH:
                return EAST_NORTH_WEST;
            case WEST:
                return NORTH_SOUTH_EAST;
            default:
                throw new IllegalArgumentException(excludedDirection.getName() + " is not a horizontal Direction.");
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

    public static Rotation getOppositeRotationFromFacing(Direction facing) {
        switch (facing) {
            case NORTH:
                return Rotation.CLOCKWISE_90;
            case EAST:
                return Rotation.CLOCKWISE_180;
            case SOUTH:
                return Rotation.COUNTERCLOCKWISE_90;
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
