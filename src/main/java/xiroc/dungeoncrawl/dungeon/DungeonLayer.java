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
import xiroc.dungeoncrawl.dungeon.piece.PlaceHolder;
import xiroc.dungeoncrawl.dungeon.piece.room.DungeonNodeRoom;
import xiroc.dungeoncrawl.dungeon.piece.room.DungeonSecretRoom;
import xiroc.dungeoncrawl.util.Orientation;
import xiroc.dungeoncrawl.util.Position2D;

import java.util.List;
import java.util.Random;

public class DungeonLayer {

    public final PlaceHolder[][] grid;

    public Position2D start;
    public Position2D end;

    public int width; // x
    public int length; // z

    /*
     * Contains the positions all nodes that do not have a
     * direct connection to the start position.
     */
    public List<Position2D> distantNodes;

    public boolean stairsPlaced;

    public LayerStatTracker statTracker;

    public DungeonLayerMap map;

    public DungeonLayer(int width, int length) {
        this.width = width;
        this.length = length;
        this.statTracker = new LayerStatTracker();
        this.grid = new PlaceHolder[this.width][this.length];
        this.distantNodes = Lists.newArrayList();
    }

    /**
     * Returns whether the tile at the given position in the layout grid is free or not.
     * The given position is expected to be within the grid bounds.
     *
     * @return True if the selected tile in the layout grid is free, false if not.
     */
    public boolean isTileFree(int x, int z) {
        return grid[x][z] == null && map.isPositionFree(x, z);
    }

    /**
     * Returns whether the tile at the given position in the layout grid is free or not.
     * The given position is expected to be within the grid bounds.
     *
     * @return True if the selected tile in the layout grid is free, false if not.
     */
    public boolean isTileFree(Position2D pos) {
        return grid[pos.x][pos.z] == null && map.isPositionFree(pos.x, pos.z);
    }

    /**
     * Calculates a distance value for the two positions by adding
     * the absolute values of the differences of their coordinates
     * together. ( Math.abs(a.x - b.x) + Math.abs(a.z - b.z) )
     *
     * @param a Position a
     * @param b Position b
     * @return the distance value. The higher the value the higher the distance.
     */
    public int distance(Position2D a, Position2D b) {
        return Math.abs(a.x - b.x) + Math.abs(a.z - b.z);
    }

    public void buildConnection(Position2D start, Position2D end, Random rand) {
        int startX = start.x;
        int startZ = start.z;
        int endX = end.x;
        int endZ = end.z;

        if (startX == endX && startZ == endZ)
            return;

        if (startX > endX) {
            openSideIfExistent(start, Direction.WEST);
            for (int x = startX; x > (startZ == endZ ? endX + 1 : endX); x--) {
                final Direction side = (x - 1) == endX ? startZ < endZ ? Direction.SOUTH : Direction.NORTH : Direction.WEST;
                if (this.grid[x - 1][startZ] != null) {
                    this.grid[x - 1][startZ].reference.openSide(
                            side);
                    this.grid[x - 1][startZ].reference.openSide(Direction.EAST);
                    this.rotatePiece(this.grid[x - 1][startZ], rand);
                    continue;
                }
                DungeonPiece corridor = new DungeonCorridor();
                corridor.setGridPosition(x - 1, startZ);
                corridor.setRotation(
                        (x - 1) == endX
                                ? Orientation.getRotationFromCW90DoubleFacing(
                                startZ > endZ ? Direction.NORTH : Direction.SOUTH, Direction.EAST)
                                : Orientation.getRotationFromFacing(Direction.WEST));
                corridor.openSide(side);
                corridor.openSide(Direction.EAST);
                this.grid[x - 1][startZ] = new PlaceHolder(corridor);
            }
            if (startZ > endZ) {
                openSideIfExistent(end, Direction.SOUTH);
                for (int z = startZ; z > endZ + 1; z--) {
                    if (this.grid[endX][z - 1] != null) {
                        this.grid[endX][z - 1].reference.openSide(Direction.SOUTH);
                        this.grid[endX][z - 1].reference
                                .openSide((z - 1) == endZ ? Direction.WEST : Direction.NORTH);
                        this.rotatePiece(this.grid[endX][z - 1], rand);
                        continue;
                    }
                    DungeonPiece corridor = new DungeonCorridor();
                    corridor.setGridPosition(endX, z - 1);
                    corridor.setRotation((z - 1) == endZ
                            ? Orientation.getRotationFromCW90DoubleFacing(Direction.NORTH, Direction.WEST)
                            : Orientation.getRotationFromFacing(Direction.NORTH));
                    corridor.openSide(Direction.SOUTH);
                    corridor.openSide((z - 1) == endZ ? Direction.WEST : Direction.NORTH);
                    this.grid[endX][z - 1] = new PlaceHolder(corridor);
                }
            } else if (startZ < endZ) {
                openSideIfExistent(end, Direction.NORTH);
                for (int z = startZ; z < endZ - 1; z++) {
                    if (this.grid[endX][z + 1] != null) {
                        this.grid[endX][z + 1].reference
                                .openSide((z + 1) == endZ ? Direction.WEST : Direction.SOUTH);
                        this.grid[endX][z + 1].reference.openSide(Direction.NORTH);
                        this.rotatePiece(this.grid[endX][z + 1], rand);
                        continue;
                    }
                    DungeonPiece corridor = new DungeonCorridor();
                    corridor.setGridPosition(endX, z + 1);
                    corridor.setRotation((z + 1) == endZ
                            ? Orientation.getRotationFromCW90DoubleFacing(Direction.NORTH, Direction.WEST)
                            : Orientation.getRotationFromFacing(Direction.SOUTH));
                    corridor.openSide((z + 1) == endZ ? Direction.WEST : Direction.SOUTH);
                    corridor.openSide(Direction.NORTH);
                    this.grid[endX][z + 1] = new PlaceHolder(corridor);
                }
            } else {
                openSideIfExistent(end, Direction.EAST);
            }
        } else if (startX < endX) {
            openSideIfExistent(start, Direction.EAST);
            for (int x = startX; x < (startZ == endZ ? endX - 1 : endX); x++) {
                final Direction side = (x + 1) == endX ? startZ < endZ ? Direction.SOUTH : Direction.NORTH : Direction.EAST;
                if (this.grid[x + 1][startZ] != null) {
                    this.grid[x + 1][startZ].reference.openSide(
                            side);
                    this.grid[x + 1][startZ].reference.openSide(Direction.WEST);
                    this.rotatePiece(this.grid[x + 1][startZ], rand);
                    continue;
                }
                DungeonPiece corridor = new DungeonCorridor();
                corridor.setGridPosition(x + 1, startZ);
                corridor.setRotation(
                        (x + 1) == endX
                                ? Orientation.getRotationFromCW90DoubleFacing(
                                startZ > endZ ? Direction.NORTH : Direction.SOUTH, Direction.WEST)
                                : Orientation.getRotationFromFacing(Direction.EAST));
                corridor.openSide(side);
                corridor.openSide(Direction.WEST);
                this.grid[x + 1][startZ] = new PlaceHolder(corridor);
            }
            if (startZ > endZ) {
                openSideIfExistent(end, Direction.SOUTH);
                for (int z = startZ; z > endZ + 1; z--) {
                    if (this.grid[endX][z - 1] != null) {
                        this.grid[endX][z - 1].reference.openSide(Direction.SOUTH);
                        this.grid[endX][z - 1].reference
                                .openSide((z - 1) == endZ ? Direction.EAST : Direction.NORTH);
                        this.rotatePiece(this.grid[endX][z - 1], rand);
                        continue;
                    }
                    DungeonPiece corridor = new DungeonCorridor();
                    corridor.setGridPosition(endX, z - 1);
                    corridor.setRotation((z - 1) == endZ
                            ? Orientation.getRotationFromCW90DoubleFacing(Direction.NORTH, Direction.WEST)
                            : Orientation.getRotationFromFacing(Direction.NORTH));
                    corridor.openSide(Direction.SOUTH);
                    corridor.openSide((z - 1) == endZ ? Direction.WEST : Direction.NORTH);
                    this.grid[endX][z - 1] = new PlaceHolder(corridor);
                }
            } else if (startZ < endZ) {
                openSideIfExistent(end, Direction.NORTH);
                for (int z = startZ; z < endZ - 1; z++) {
                    if (this.grid[endX][z + 1] != null) {
                        this.grid[endX][z + 1].reference
                                .openSide((z + 1) == endZ ? Direction.EAST : Direction.SOUTH);
                        this.grid[endX][z + 1].reference.openSide(Direction.NORTH);
                        this.rotatePiece(this.grid[endX][z + 1], rand);
                        continue;
                    }
                    DungeonPiece corridor = new DungeonCorridor();
                    corridor.setGridPosition(endX, z + 1);
                    corridor.setRotation((z + 1) == endZ
                            ? Orientation.getRotationFromCW90DoubleFacing(Direction.NORTH, Direction.WEST)
                            : Orientation.getRotationFromFacing(Direction.SOUTH));
                    corridor.openSide((z + 1) == endZ ? Direction.WEST : Direction.SOUTH);
                    corridor.openSide(Direction.NORTH);
                    this.grid[endX][z + 1] = new PlaceHolder(corridor);
                }
            } else {
                openSideIfExistent(end, Direction.WEST);
            }
        } else {
            if (startZ > endZ) {
                openSideIfExistent(start, Direction.NORTH);
                openSideIfExistent(end, Direction.SOUTH);
                for (int z = startZ; z > endZ + 1; z--) {
                    if (this.grid[endX][z - 1] != null) {
                        this.grid[endX][z - 1].reference.openSide(Direction.NORTH);
                        this.grid[endX][z - 1].reference.openSide(Direction.SOUTH);
                        this.rotatePiece(this.grid[endX][z - 1], rand);
                        continue;
                    }
                    DungeonPiece corridor = new DungeonCorridor();
                    corridor.setGridPosition(endX, z - 1);
                    corridor.setRotation(Orientation.getRotationFromFacing(Direction.NORTH));
                    corridor.openSide(Direction.SOUTH);
                    corridor.openSide(Direction.NORTH);
                    this.grid[endX][z - 1] = new PlaceHolder(corridor);
                }
            } else {
                openSideIfExistent(start, Direction.SOUTH);
                openSideIfExistent(end, Direction.NORTH);
                for (int z = startZ; z < endZ - 1; z++) {
                    if (this.grid[endX][z + 1] != null) {
                        this.grid[endX][z + 1].reference.openSide(Direction.SOUTH);
                        this.grid[endX][z + 1].reference.openSide(Direction.NORTH);
                        this.rotatePiece(this.grid[endX][z + 1], rand);
                        continue;
                    }
                    this.grid[endX][endZ].reference.openSide(Direction.NORTH);
                    DungeonPiece corridor = new DungeonCorridor();
                    corridor.setGridPosition(endX, z + 1);
                    corridor.setRotation(Orientation.getRotationFromFacing(Direction.SOUTH));
                    corridor.openSide(Direction.SOUTH);
                    corridor.openSide(Direction.NORTH);
                    this.grid[endX][z + 1] = new PlaceHolder(corridor);
                }
            }
        }
    }

    /**
     * Convenience method to build a straight connection from the start position to the end position.
     * No pieces will be placed at the start and end positions themselves, only in the space between them.
     * The start position and the end position need to have either the same x-coordinate or the same
     * z-coordinate. If this is not the case, an IllegalArgumentException will be thrown.
     *
     * @param start the start position
     * @param end   the end position
     */
    public void buildStraightConnection(Position2D start, Position2D end) {
        if (start.x != end.x || start.z != end.z) {
            if (start.x == end.x) {
                if (start.z > end.z) {
                    for (int z = end.z; z < start.z - 1; z++) {
                        DungeonCorridor corridor = new DungeonCorridor();
                        corridor.setGridPosition(start.x, z);
                        // The corridor goes north from the start position (negative z)
                        corridor.openSide(Direction.NORTH);
                        corridor.openSide(Direction.SOUTH);
                        corridor.setRotation(Orientation.getRotationFromFacing(Direction.NORTH));
                        this.grid[corridor.gridX][corridor.gridZ] = new PlaceHolder(corridor);
                    }
                } else {
                    for (int z = start.z; z < end.z - 1; z++) {
                        DungeonCorridor corridor = new DungeonCorridor();
                        corridor.setGridPosition(start.x, z);
                        // The corridor goes south from the start position (positive z)
                        corridor.openSide(Direction.SOUTH);
                        corridor.openSide(Direction.NORTH);
                        corridor.setRotation(Orientation.getRotationFromFacing(Direction.SOUTH));
                        this.grid[corridor.gridX][corridor.gridZ] = new PlaceHolder(corridor);
                    }
                }
            } else if (start.z == end.z) {
                if (start.x > end.x) {
                    for (int x = end.x; x < start.x - 1; x++) {
                        DungeonCorridor corridor = new DungeonCorridor();
                        corridor.setGridPosition(x, start.z);
                        // The corridor goes west from the start position (negative x)
                        corridor.openSide(Direction.WEST);
                        corridor.openSide(Direction.EAST);
                        corridor.setRotation(Orientation.getRotationFromFacing(Direction.WEST));
                        this.grid[corridor.gridX][corridor.gridZ] = new PlaceHolder(corridor);
                    }
                } else {
                    for (int x = start.x; x < end.x - 1; x++) {
                        DungeonCorridor corridor = new DungeonCorridor();
                        corridor.setGridPosition(x, start.z);
                        // The corridor goes east from the start position (positive x)
                        corridor.openSide(Direction.EAST);
                        corridor.openSide(Direction.WEST);
                        corridor.setRotation(Orientation.getRotationFromFacing(Direction.EAST));
                        this.grid[corridor.gridX][corridor.gridZ] = new PlaceHolder(corridor);
                    }
                }
            } else {
                throw new IllegalArgumentException("The start and end positions of a straight connection must have either the same x-coordinate or the same z-coordinate");
            }
        } else {
            throw new IllegalArgumentException("The start and end positions of a straight connection must not be the same.");
        }
    }

    public Tuple<Position2D, Rotation> findStarterRoomData(Position2D start, Random rand) {
        int index = rand.nextInt(4);

        for (int i = 0; i < 4; i++) {
            index = (index + i) % 4;
            for (int j = 0; j < 2; j++) {
                Position2D current = start.shift(Orientation.FLAT_FACINGS[index], j + 1);
                if (current.isValid(Dungeon.SIZE) && grid[current.x][current.z] != null
                        && grid[current.x][current.z].reference.getType() == 0
                        && grid[current.x][current.z].reference.connectedSides < 4) {
                    Tuple<Position2D, Rotation> data = findSideRoomData(new Position2D(current.x, current.z), rand);
                    if (data != null) {
                        return data;
                    }
                }
            }
        }
        return null;
    }

    public Tuple<Position2D, Rotation> findSideRoomData(Position2D base, Random rand) {
        Position2D north = base.shift(Direction.NORTH, 1), east = base.shift(Direction.EAST, 1),
                south = base.shift(Direction.SOUTH, 1), west = base.shift(Direction.WEST, 1);

        if (rand.nextBoolean()) {
            if (north.isValid(width, length) && isTileFree(north))
                return new Tuple<>(north, Rotation.COUNTERCLOCKWISE_90);

            if (east.isValid(width, length) && isTileFree(east))
                return new Tuple<>(east, Rotation.NONE);

            if (south.isValid(width, length) && isTileFree(south))
                return new Tuple<>(south, Rotation.CLOCKWISE_90);

            if (west.isValid(width, length) && isTileFree(west))
                return new Tuple<>(west, Rotation.CLOCKWISE_180);
        } else {
            if (west.isValid(width, length) && isTileFree(west))
                return new Tuple<>(west, Rotation.CLOCKWISE_180);

            if (south.isValid(width, length) && isTileFree(south))
                return new Tuple<>(south, Rotation.CLOCKWISE_90);

            if (east.isValid(width, length) && isTileFree(east))
                return new Tuple<>(east, Rotation.NONE);

            if (north.isValid(width, length) && isTileFree(north))
                return new Tuple<>(north, Rotation.COUNTERCLOCKWISE_90);
        }

        return null;
    }

    /**
     * Convenience method to rotate a dungeon piece so that it matches its connections.
     * This is necessary to ensure that the model for this piece, which should be unknown at this point,
     * matches the pieces' connections.
     *
     * @param placeHolder the place holder of the piece
     * @param rand        an instance of Random which will be used to choose a random one of the valid rotations, should there be more than one
     */
    public void rotatePiece(PlaceHolder placeHolder, Random rand) {
        if (placeHolder.hasFlag(PlaceHolder.Flag.FIXED_ROTATION))
            return;
        DungeonPiece piece = placeHolder.reference;

        switch (piece.connectedSides) {
            case 1:
                piece.setRotation(Orientation.getRotationFromFacing(DungeonPiece.getOneWayDirection(piece)));
                return;
            case 2:
                if (piece.sides[0] && piece.sides[2]) {
                    piece.setRotation(Orientation.getRotationFromFacing(rand.nextBoolean() ? Direction.NORTH : Direction.SOUTH));
                } else if (piece.sides[1] && piece.sides[3]) {
                    piece.setRotation(Orientation.getRotationFromFacing(rand.nextBoolean() ? Direction.EAST : Direction.WEST));
                } else {
                    piece.setRotation(Orientation.getRotationFromCW90DoubleFacing(DungeonPiece.getOpenSide(piece, 0),
                            DungeonPiece.getOpenSide(piece, 1)));
                }
                return;
            case 3:
                piece.setRotation(Orientation.getRotationFromTripleFacing(DungeonPiece.getOpenSide(piece, 0),
                        DungeonPiece.getOpenSide(piece, 1), DungeonPiece.getOpenSide(piece, 2)));
                return;
            default:
                piece.setRotation(Rotation.randomRotation(rand));
        }
    }

    /**
     * Convenience method to rotate a node piece so that it matches its connections.
     * This is necessary to ensure that the model for this piece, which should be unknown at this point,
     * matches the pieces' connections.
     *
     * @param placeHolder the place holder of the node
     * @param rand        an instance of Random which will be used to choose in which direction (clockwise or counterclockwise)
     *                    the algorithm rotates the node until it matches to create more randomness.
     */
    public void rotateNode(PlaceHolder placeHolder, Random rand) {
        if (placeHolder.hasFlag(PlaceHolder.Flag.FIXED_ROTATION))
            return;
        DungeonNodeRoom node = (DungeonNodeRoom) placeHolder.reference;
        Rotation rotation = node.node.compare(new Node(node.sides[0], node.sides[1], node.sides[2], node.sides[3]), rand);
        if (rotation != null) {
            node.rotation = rotation;
        } else {
            DungeonCrawl.LOGGER.error("Could not find a proper rotation for [{} {} {} {}].", node.sides[0],
                    node.sides[1], node.sides[2], node.sides[3]);
        }
    }

    public boolean placeSecretRoom(DungeonCorridor corridor, Position2D position, Random rand) {
        Direction direction = (corridor.rotation == Rotation.NONE || corridor.rotation == Rotation.CLOCKWISE_180) ?
                (rand.nextBoolean() ? Direction.NORTH : Direction.SOUTH) : (rand.nextBoolean() ? Direction.EAST : Direction.WEST);
        Position2D pos = position.shift(direction, 2);
        if (attemptSecretRoomPlacement(corridor, position, direction, pos)) {
            return true;
        }

        // Attempt to place the room on the other side of the corridor.
        direction = direction.getOpposite();
        pos = position.shift(direction, 2);
        return attemptSecretRoomPlacement(corridor, position, direction, pos);
    }

    public boolean attemptSecretRoomPlacement(DungeonCorridor corridor, Position2D corridorPos, Direction direction, Position2D outerPosition) {
        if (outerPosition.isValid(width, length)) {
            Position2D innerPosition = corridorPos.shift(direction, 1);
            if (isTileFree(outerPosition) && isTileFree(innerPosition)) {
                DungeonSecretRoom room = new DungeonSecretRoom(null, DungeonPiece.DEFAULT_NBT);
                int x = Math.min(outerPosition.x, innerPosition.x), z = Math.min(outerPosition.z, innerPosition.z);
                room.setGridPosition(x, z);
                room.setRotation(Orientation.getRotationFromFacing(direction));
                grid[x][z] = new PlaceHolder(room);
                Position2D other = getOther(x, z, direction);
                grid[other.x][other.z] = new PlaceHolder(room).addFlag(PlaceHolder.Flag.PLACEHOLDER);
                corridor.rotation = Orientation.getRotationFromFacing(direction).add(Rotation.CLOCKWISE_90);
                corridor.modelID = DungeonModels.CORRIDOR_SECRET_ROOM_ENTRANCE.id;
                grid[corridorPos.x][corridorPos.z].addFlag(PlaceHolder.Flag.FIXED_MODEL);
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

    /**
     * Convenience method to open a side of a piece at the given position. Before the grid is accessed,
     * the given position will be verified, hence positions outside of the grid bounds
     * and positions of empty tiles are legal arguments.
     *
     * @param position the grid position of the piece
     * @param side     the side to open
     */
    public void openSideIfExistent(Position2D position, Direction side) {
        if (position.isValid(width, length) && grid[position.x][position.z] != null) {
            grid[position.x][position.z].reference.openSide(side);
        }
    }

    /**
     * Convenience method to check if the layer grid contains a piece at the given position. Before the grid is accessed,
     * the given position will be verified, hence positions outside of the grid bounds are legal arguments.
     *
     * @param position the grid position to check
     * @return true if the position is valid and the tile at that position is not empty, false otherwise.
     */
    public boolean exists(Position2D position) {
        return position.isValid(width, length) && grid[position.x][position.z] != null;
    }

    public DungeonPiece get(int x, int z) {
        return grid[x][z] == null ? null : grid[x][z].reference;
    }

}
