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

package xiroc.dungeoncrawl.dungeon;

import com.google.common.collect.Lists;
import net.minecraft.util.Direction;
import net.minecraft.util.Rotation;
import net.minecraft.util.Tuple;
import xiroc.dungeoncrawl.DungeonCrawl;
import xiroc.dungeoncrawl.dungeon.DungeonStatTracker.LayerStatTracker;
import xiroc.dungeoncrawl.dungeon.model.DungeonModels;
import xiroc.dungeoncrawl.dungeon.piece.DungeonCorridor;
import xiroc.dungeoncrawl.dungeon.piece.DungeonPiece;
import xiroc.dungeoncrawl.dungeon.piece.DungeonStairs;
import xiroc.dungeoncrawl.dungeon.piece.PlaceHolder;
import xiroc.dungeoncrawl.dungeon.piece.room.DungeonNodeRoom;
import xiroc.dungeoncrawl.dungeon.piece.room.DungeonRoom;
import xiroc.dungeoncrawl.dungeon.piece.room.DungeonSecretRoom;
import xiroc.dungeoncrawl.dungeon.piece.room.DungeonSideRoom;
import xiroc.dungeoncrawl.dungeon.treasure.Treasure;
import xiroc.dungeoncrawl.util.Orientation;
import xiroc.dungeoncrawl.util.Position2D;

import java.util.List;
import java.util.Random;

public class DungeonLayer {

    public PlaceHolder[][] segments;

    public Position2D start;
    public Position2D end;

    public int width; // x
    public int length; // z

    /*
     * Tracking values for recursive layer generation
     */
    public int nodes, rooms, nodesLeft, roomsLeft;
    public boolean stairsPlaced;

    public LayerStatTracker statTracker;

    public DungeonLayerMap map;

    /*
     * Contains all nodes that do not have a direct connection to the start
     * position. The only use case for this right now is that in the last layer, one
     * of these nodes will be chosen to become the loot room.
     */
    public List<Position2D> distantNodes;

    public DungeonLayer() {
        this(16, 16);
    }

    public DungeonLayer(int width, int length) {
        this.width = width;
        this.length = length;
        this.statTracker = new LayerStatTracker();
        this.segments = new PlaceHolder[this.width][this.length];
        this.distantNodes = Lists.newArrayList();
    }

    public void buildConnection(Position2D start, Position2D end) {
        int startX = start.x;
        int startZ = start.z;
        int endX = end.x;
        int endZ = end.z;

        if (startX == endX && startZ == endZ)
            return;

        if (startX > endX) {
            this.segments[startX][startZ].reference.openSide(Direction.WEST);
            for (int x = startX; x > (startZ == endZ ? endX + 1 : endX); x--) {
                final Direction side = (x - 1) == endX ? startZ < endZ ? Direction.SOUTH : Direction.NORTH : Direction.WEST;
                if (this.segments[x - 1][startZ] != null) {
                    this.segments[x - 1][startZ].reference.openSide(
                            side);
                    this.segments[x - 1][startZ].reference.openSide(Direction.EAST);
                    this.rotatePiece(this.segments[x - 1][startZ]);
                    continue;
                }
                DungeonPiece corridor = new DungeonCorridor(null, DungeonPiece.DEFAULT_NBT);
                corridor.setPosition(x - 1, startZ);
                corridor.setRotation(
                        (x - 1) == endX
                                ? Orientation.getRotationFromCW90DoubleFacing(
                                startZ > endZ ? Direction.NORTH : Direction.SOUTH, Direction.EAST)
                                : Orientation.getRotationFromFacing(Direction.WEST));
                corridor.openSide(side);
                corridor.openSide(Direction.EAST);
                this.segments[x - 1][startZ] = new PlaceHolder(corridor);
            }
            if (startZ > endZ) {
                this.segments[endX][endZ].reference.openSide(Direction.SOUTH);
                for (int z = startZ; z > endZ + 1; z--) {
                    if (this.segments[endX][z - 1] != null) {
                        this.segments[endX][z - 1].reference.openSide(Direction.SOUTH);
                        this.segments[endX][z - 1].reference
                                .openSide((z - 1) == endZ ? Direction.WEST : Direction.NORTH);
                        this.rotatePiece(this.segments[endX][z - 1]);
                        continue;
                    }
                    DungeonPiece corridor = new DungeonCorridor(null, DungeonPiece.DEFAULT_NBT);
                    corridor.setPosition(endX, z - 1);
                    corridor.setRotation((z - 1) == endZ
                            ? Orientation.getRotationFromCW90DoubleFacing(Direction.NORTH, Direction.WEST)
                            : Orientation.getRotationFromFacing(Direction.NORTH));
                    corridor.openSide(Direction.SOUTH);
                    corridor.openSide((z - 1) == endZ ? Direction.WEST : Direction.NORTH);
                    this.segments[endX][z - 1] = new PlaceHolder(corridor);
                }
            } else if (startZ < endZ) {
                this.segments[endX][endZ].reference.openSide(Direction.NORTH);
                for (int z = startZ; z < endZ - 1; z++) {
                    if (this.segments[endX][z + 1] != null) {
                        this.segments[endX][z + 1].reference
                                .openSide((z + 1) == endZ ? Direction.WEST : Direction.SOUTH);
                        this.segments[endX][z + 1].reference.openSide(Direction.NORTH);
                        this.rotatePiece(this.segments[endX][z + 1]);
                        continue;
                    }
                    DungeonPiece corridor = new DungeonCorridor(null, DungeonPiece.DEFAULT_NBT);
                    corridor.setPosition(endX, z + 1);
                    corridor.setRotation((z + 1) == endZ
                            ? Orientation.getRotationFromCW90DoubleFacing(Direction.NORTH, Direction.WEST)
                            : Orientation.getRotationFromFacing(Direction.SOUTH));
                    corridor.openSide((z + 1) == endZ ? Direction.WEST : Direction.SOUTH);
                    corridor.openSide(Direction.NORTH);
                    this.segments[endX][z + 1] = new PlaceHolder(corridor);
                }
            } else
                this.segments[endX][endZ].reference.openSide(Direction.EAST);
        } else if (startX < endX) {
            this.segments[startX][startZ].reference.openSide(Direction.EAST);
            for (int x = startX; x < (startZ == endZ ? endX - 1 : endX); x++) {
                final Direction side = (x + 1) == endX ? startZ < endZ ? Direction.SOUTH : Direction.NORTH : Direction.EAST;
                if (this.segments[x + 1][startZ] != null) {
                    this.segments[x + 1][startZ].reference.openSide(
                            side);
                    this.segments[x + 1][startZ].reference.openSide(Direction.WEST);
                    this.rotatePiece(this.segments[x + 1][startZ]);
                    continue;
                }
                DungeonPiece corridor = new DungeonCorridor(null, DungeonPiece.DEFAULT_NBT);
                corridor.setPosition(x + 1, startZ);
                corridor.setRotation(
                        (x + 1) == endX
                                ? Orientation.getRotationFromCW90DoubleFacing(
                                startZ > endZ ? Direction.NORTH : Direction.SOUTH, Direction.WEST)
                                : Orientation.getRotationFromFacing(Direction.EAST));
                corridor.openSide(side);
                corridor.openSide(Direction.WEST);
                this.segments[x + 1][startZ] = new PlaceHolder(corridor);
            }
            if (startZ > endZ) {
                this.segments[endX][endZ].reference.openSide(Direction.SOUTH);
                for (int z = startZ; z > endZ + 1; z--) {
                    if (this.segments[endX][z - 1] != null) {
                        this.segments[endX][z - 1].reference.openSide(Direction.SOUTH);
                        this.segments[endX][z - 1].reference
                                .openSide((z - 1) == endZ ? Direction.EAST : Direction.NORTH);
                        this.rotatePiece(this.segments[endX][z - 1]);
                        continue;
                    }
                    DungeonPiece corridor = new DungeonCorridor(null, DungeonPiece.DEFAULT_NBT);
                    corridor.setPosition(endX, z - 1);
                    corridor.setRotation((z - 1) == endZ
                            ? Orientation.getRotationFromCW90DoubleFacing(Direction.NORTH, Direction.WEST)
                            : Orientation.getRotationFromFacing(Direction.NORTH));
                    corridor.openSide(Direction.SOUTH);
                    corridor.openSide((z - 1) == endZ ? Direction.WEST : Direction.NORTH);
                    this.segments[endX][z - 1] = new PlaceHolder(corridor);
                }
            } else if (startZ < endZ) {
                this.segments[endX][endZ].reference.openSide(Direction.NORTH);
                for (int z = startZ; z < endZ - 1; z++) {
                    if (this.segments[endX][z + 1] != null) {
                        this.segments[endX][z + 1].reference
                                .openSide((z + 1) == endZ ? Direction.EAST : Direction.SOUTH);
                        this.segments[endX][z + 1].reference.openSide(Direction.NORTH);
                        this.rotatePiece(this.segments[endX][z + 1]);
                        continue;
                    }
                    DungeonPiece corridor = new DungeonCorridor(null, DungeonPiece.DEFAULT_NBT);
                    corridor.setPosition(endX, z + 1);
                    corridor.setRotation((z + 1) == endZ
                            ? Orientation.getRotationFromCW90DoubleFacing(Direction.NORTH, Direction.WEST)
                            : Orientation.getRotationFromFacing(Direction.SOUTH));
                    corridor.openSide((z + 1) == endZ ? Direction.WEST : Direction.SOUTH);
                    corridor.openSide(Direction.NORTH);
                    this.segments[endX][z + 1] = new PlaceHolder(corridor);
                }
            } else
                this.segments[endX][endZ].reference.openSide(Direction.WEST);
        } else {
            if (startZ > endZ) {
                this.segments[startX][startZ].reference.openSide(Direction.NORTH);
                this.segments[endX][endZ].reference.openSide(Direction.SOUTH);
                for (int z = startZ; z > endZ + 1; z--) {
                    if (this.segments[endX][z - 1] != null) {
                        this.segments[endX][z - 1].reference.openSide(Direction.NORTH);
                        this.segments[endX][z - 1].reference.openSide(Direction.SOUTH);
                        this.rotatePiece(this.segments[endX][z - 1]);
                        continue;
                    }
                    DungeonPiece corridor = new DungeonCorridor(null, DungeonPiece.DEFAULT_NBT);
                    corridor.setPosition(endX, z - 1);
                    corridor.setRotation(Orientation.getRotationFromFacing(Direction.NORTH));
                    corridor.openSide(Direction.SOUTH);
                    corridor.openSide(Direction.NORTH);
                    this.segments[endX][z - 1] = new PlaceHolder(corridor);
                }
            } else if (startZ < endZ) {
                this.segments[startX][startZ].reference.openSide(Direction.SOUTH);
                this.segments[endX][endZ].reference.openSide(Direction.NORTH);
                for (int z = startZ; z < endZ - 1; z++) {
                    if (this.segments[endX][z + 1] != null) {
                        this.segments[endX][z + 1].reference.openSide(Direction.SOUTH);
                        this.segments[endX][z + 1].reference.openSide(Direction.NORTH);
                        this.rotatePiece(this.segments[endX][z + 1]);
                        continue;
                    }
                    this.segments[endX][endZ].reference.openSide(Direction.NORTH);
                    DungeonPiece corridor = new DungeonCorridor(null, DungeonPiece.DEFAULT_NBT);
                    corridor.setPosition(endX, z + 1);
                    corridor.setRotation(Orientation.getRotationFromFacing(Direction.SOUTH));
                    corridor.openSide(Direction.SOUTH);
                    corridor.openSide(Direction.NORTH);
                    this.segments[endX][z + 1] = new PlaceHolder(corridor);
                }
            }
        }
    }

    public Tuple<Position2D, Rotation> findStarterRoomData(Position2D start, Random rand) {
//			for (int x = start.x - 2; x < start.x + 2; x++) {
//				for (int z = start.z - 2; z < start.z + 2; z++) {
//					if (Position2D.isValid(x, z, width, length)) {
//						if (segments[x][z] != null && segments[x][z].reference.getType() == 0
//								&& segments[x][z].reference.connectedSides < 4) {
//							Tuple<Position2D, Rotation> data = findSideRoomData(new Position2D(x, z));
//							if (data == null)
//								continue;
//							return data;
//						}
//					}
//				}
//			}
//			return null;

        int index = rand.nextInt(4);

        for (int i = 0; i < 4; i++) {
            index = (index + i) % 4;
            //int length = 1 + rand.nextInt(2);
            for (int j = 0; j < 2; j++) {
                Position2D current = start.shift(Orientation.FLAT_FACINGS[index], j + 1);
                if (current.isValid(Dungeon.SIZE) && segments[current.x][current.z] != null
                        && segments[current.x][current.z].reference.getType() == 0
                        && segments[current.x][current.z].reference.connectedSides < 4) {
                    Tuple<Position2D, Rotation> data = findSideRoomData(new Position2D(current.x, current.z));
                    if (data != null) {
                        return data;
                    }
                }
                current = start.shift(Orientation.FLAT_FACINGS[index], 1);
            }
        }
        return null;
    }

    public Tuple<Position2D, Rotation> findSideRoomData(Position2D base) {
        Position2D north = base.shift(Direction.NORTH, 1), east = base.shift(Direction.EAST, 1),
                south = base.shift(Direction.SOUTH, 1), west = base.shift(Direction.WEST, 1);

        if (north.isValid(width, length) && segments[north.x][north.z] == null)
            return new Tuple<Position2D, Rotation>(north, Rotation.COUNTERCLOCKWISE_90);

        if (east.isValid(width, length) && segments[east.x][east.z] == null)
            return new Tuple<Position2D, Rotation>(east, Rotation.NONE);

        if (south.isValid(width, length) && segments[south.x][south.z] == null)
            return new Tuple<Position2D, Rotation>(south, Rotation.CLOCKWISE_90);

        if (west.isValid(width, length) && segments[west.x][west.z] == null)
            return new Tuple<Position2D, Rotation>(west, Rotation.CLOCKWISE_180);

        return null;
    }

    public void rotatePiece(PlaceHolder placeHolder) {
        if (placeHolder.hasFlag(PlaceHolder.Flag.FIXED_ROTATION))
            return;
        DungeonPiece piece = placeHolder.reference;

        switch (piece.connectedSides) {
            case 1:
                piece.setRotation(Orientation.getRotationFromFacing(DungeonPiece.getOneWayDirection(piece)));
                return;
            case 2:
                if (piece.sides[0] && piece.sides[2])
                    piece.setRotation(Orientation.getRotationFromFacing(Direction.NORTH));
                else if (piece.sides[1] && piece.sides[3])
                    piece.setRotation(Orientation.getRotationFromFacing(Direction.EAST));
                else
                    piece.setRotation(Orientation.getRotationFromCW90DoubleFacing(DungeonPiece.getOpenSide(piece, 0),
                            DungeonPiece.getOpenSide(piece, 1)));
                return;
            case 3:
                piece.setRotation(Orientation.getRotationFromTripleFacing(DungeonPiece.getOpenSide(piece, 0),
                        DungeonPiece.getOpenSide(piece, 1), DungeonPiece.getOpenSide(piece, 2)));
        }
    }

    public void rotateNode(PlaceHolder placeHolder) {
        if (placeHolder.hasFlag(PlaceHolder.Flag.FIXED_ROTATION))
            return;
        DungeonNodeRoom node = (DungeonNodeRoom) placeHolder.reference;
        Rotation rotation = node.node.compare(new Node(node.sides[0], node.sides[1], node.sides[2], node.sides[3]));
        if (rotation != null) {
            node.rotation = rotation;
        } else {
            DungeonCrawl.LOGGER.error("Could not find a proper rotation for [{} {} {} {}].", node.sides[0],
                    node.sides[1], node.sides[2], node.sides[3]);
        }

    }

    public Direction findNext(DungeonPiece piece, Direction base) {
        if (piece.connectedSides >= 4)
            return null;
        if (piece.sides[(base.getHorizontalIndex() + 2) % 4])
            return findNext(piece, base.rotateY());
        else
            return base;
    }

    public boolean placeSecretRoom(DungeonCorridor corridor, Position2D position, Random rand) {
        Direction direction = (corridor.rotation == Rotation.NONE || corridor.rotation == Rotation.CLOCKWISE_180) ?
                (rand.nextBoolean() ? Direction.NORTH : Direction.SOUTH) : (rand.nextBoolean() ? Direction.EAST : Direction.WEST);
        Position2D pos = position.shift(direction, 2);
        if (pos.isValid(width, length)) {
            Position2D pos2 = position.shift(direction, 1);
            if (segments[pos.x][pos.z] == null && segments[pos2.x][pos2.z] == null) {
                DungeonSecretRoom room = new DungeonSecretRoom(null, DungeonPiece.DEFAULT_NBT);
                int x = Math.min(pos.x, pos2.x), z = Math.min(pos.z, pos2.z);
                room.setPosition(x, z);
                room.setRotation(Orientation.getRotationFromFacing(direction));
                segments[x][z] = new PlaceHolder(room);
                Position2D other = getOther(x, z, direction);
                segments[other.x][other.z] = new PlaceHolder(room).withFlag(PlaceHolder.Flag.PLACEHOLDER);
                corridor.rotation = Orientation.getRotationFromFacing(direction).add(Rotation.CLOCKWISE_90);
                corridor.modelID = DungeonModels.CORRIDOR_SECRET_ROOM_ENTRANCE.id;
                segments[position.x][position.z].withFlag(PlaceHolder.Flag.FIXED_MODEL);
                return true;
            }
        }
        direction = direction.getOpposite();
        pos = position.shift(direction, 2);
        if (pos.isValid(width, length)) {
            Position2D pos2 = position.shift(direction, 1);
            if (segments[pos.x][pos.z] == null && segments[pos2.x][pos2.z] == null) {
                DungeonSecretRoom room = new DungeonSecretRoom(null, DungeonPiece.DEFAULT_NBT);
                int x = Math.min(pos.x, pos2.x), z = Math.min(pos.z, pos2.z);
                room.setPosition(x, z);
                room.setRotation(Orientation.getRotationFromFacing(direction));
                segments[x][z] = new PlaceHolder(room);
                Position2D other = getOther(x, z, direction);
                segments[other.x][other.z] = new PlaceHolder(room).withFlag(PlaceHolder.Flag.PLACEHOLDER);
                corridor.rotation = Orientation.getRotationFromFacing(direction).add(Rotation.CLOCKWISE_90);
                corridor.modelID = DungeonModels.CORRIDOR_SECRET_ROOM_ENTRANCE.id;
                segments[position.x][position.z].withFlag(PlaceHolder.Flag.FIXED_MODEL);
                return true;
            }
        }
        return false;
    }

    private static Position2D getOther(int x, int z, Direction direction) {
        switch (direction) {
            case EAST:
            case WEST:
                return new Position2D(x + 1, z);
            case SOUTH:
            case NORTH:
                return new Position2D(x, z + 1);
            default:
                throw new UnsupportedOperationException("Can't get other position from direction " + direction.toString());
        }
    }

    public boolean canPutDoubleRoom(Position2D pos, Direction direction) {
        if (!pos.isValid(width, length) || segments[pos.x][pos.z] != null && map.isPositionFree(pos.x, pos.z))
            return false;
        switch (direction) {
            case NORTH:
                return pos.z > 0 && this.segments[pos.x][pos.z - 1] == null && map.isPositionFree(pos.x, pos.z - 1);
            case EAST:
                return pos.x < width - 1 && this.segments[pos.x + 1][pos.z] == null && map.isPositionFree(pos.x + 1, pos.z);
            case SOUTH:
                return pos.z < length - 1 && this.segments[pos.x][pos.z + 1] == null
                        && map.isPositionFree(pos.x, pos.z + 1);
            case WEST:
                return pos.x > 0 && this.segments[pos.x - 1][pos.z] == null && map.isPositionFree(pos.x - 1, pos.z);
            default:
                return false;
        }
    }

    public Position2D getLargeRoomPos(Position2D pos) {
        int a = Dungeon.SIZE - 1, x = pos.x, z = pos.z;

        if (x < a && z < a && get(x + 1, z) == null && get(x + 1, z + 1) == null && get(x, z + 1) == null)
            return pos;
        if (x < a && z > 0 && get(x + 1, z) == null && get(x + 1, z - 1) == null && get(x, z - 1) == null)
            return new Position2D(x, z - 1);
        if (x > 0 && z < a && get(x - 1, z) == null && get(x - 1, z + 1) == null && get(x, z + 1) == null)
            return new Position2D(x - 1, z);
        if (x > 0 && z > 0 && get(x - 1, z) == null && get(x - 1, z - 1) == null && get(x, z - 1) == null)
            return new Position2D(x - 1, z - 1);
        return null;
    }

    public static Position2D getLargeRoomPos(DungeonLayer layer, Position2D pos) {
        int a = Dungeon.SIZE - 1, x = pos.x, z = pos.z;

        if (x < a && z < a && layer.get(x + 1, z) == null && layer.get(x + 1, z + 1) == null
                && layer.get(x, z + 1) == null)
            return pos;
        if (x < a && z > 0 && layer.get(x + 1, z) == null && layer.get(x + 1, z - 1) == null
                && layer.get(x, z - 1) == null)
            return new Position2D(x, z - 1);
        if (x > 0 && z < a && layer.get(x - 1, z) == null && layer.get(x - 1, z + 1) == null
                && layer.get(x, z + 1) == null)
            return new Position2D(x - 1, z);
        if (x > 0 && z > 0 && layer.get(x - 1, z) == null && layer.get(x - 1, z - 1) == null
                && layer.get(x, z - 1) == null)
            return new Position2D(x - 1, z - 1);
        return null;
    }

    public DungeonPiece get(int x, int z) {
        return segments[x][z] == null ? null : segments[x][z].reference;
    }

}
