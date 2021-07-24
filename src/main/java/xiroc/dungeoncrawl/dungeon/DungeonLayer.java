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
import net.minecraft.core.Direction;
import net.minecraft.util.Tuple;
import net.minecraft.world.level.block.Rotation;
import xiroc.dungeoncrawl.DungeonCrawl;
import xiroc.dungeoncrawl.dungeon.DungeonStatTracker.LayerStatTracker;
import xiroc.dungeoncrawl.dungeon.model.DungeonModels;
import xiroc.dungeoncrawl.dungeon.piece.DungeonCorridor;
import xiroc.dungeoncrawl.dungeon.piece.DungeonPiece;
import xiroc.dungeoncrawl.dungeon.piece.room.DungeonNodeRoom;
import xiroc.dungeoncrawl.dungeon.piece.room.DungeonSecretRoom;
import xiroc.dungeoncrawl.util.Orientation;
import xiroc.dungeoncrawl.util.Position2D;

import java.util.List;
import java.util.Random;

public class DungeonLayer {

    public final Tile[][] grid;

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

    public DungeonLayer(int size) {
        this(size, size);
    }

    public DungeonLayer(int width, int length) {
        this.width = width;
        this.length = length;
        this.statTracker = new LayerStatTracker();
        this.grid = new Tile[this.width][this.length];
        this.distantNodes = Lists.newArrayList();
        this.map = new DungeonLayerMap(width, length);
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
     * together. The result is the amount of tiles you have to move
     * to get from position a to position b (or the other way around).
     *
     * @param a Position a
     * @param b Position b
     * @return the distance value. The higher the value the higher the distance.
     */
    public int distance(Position2D a, Position2D b) {
        return Math.abs(a.x - b.x) + Math.abs(a.z - b.z);
    }

    /**
     * Determines whether a node centered at the given position can be placed.
     *
     * @param center the center of the node.
     * @return whether such a node can be placed or not.
     */
    public boolean canPlaceNode(Position2D center) {
        for (int x = -1; x < 2; x++) {
            for (int z = -1; z < 2; z++) {
                // If we are at the center of the node, require the position of the center to be within the grid bounds and require the position to be free in the grid.
                // For the eight other positions, require the position to be free in the grid if it is within the grid bounds. Positions outside of the grid bounds are always valid.
                Position2D currentPos = new Position2D(center.x + x, center.z + z);
                if (x == 0 && z == 0) {
                    if (!currentPos.isValid(width, length)) {
                        return false;
                    } else if (!isTileFree(currentPos)) {
                        return false;
                    }
                } else {
                    if (currentPos.isValid(width, length) && !isTileFree(currentPos)) {
                        return false;
                    }
                }
            }
        }
        return true;
    }

    public Tuple<Position2D, Rotation> findStarterRoomData(Position2D start, Random rand) {
        int index = rand.nextInt(4);

        for (int i = 0; i < 4; i++) {
            index = (index + i) % 4;
            for (int j = 0; j < 2; j++) {
                Position2D current = start.shift(Orientation.HORIZONTAL_FACINGS[index], j + 1);
                if (current.isValid(Dungeon.SIZE) && grid[current.x][current.z] != null
                        && grid[current.x][current.z].piece.getDungeonPieceType() == 0
                        && grid[current.x][current.z].piece.connectedSides < 4) {
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
     * Opens a side of the piece at the given position if there is one. Before the grid is accessed,
     * the given position will be verified, hence positions outside of the grid bounds
     * and positions of empty tiles are legal arguments.
     *
     * @param position the grid position of the piece
     * @param side     the side to open
     */
    public void openSideIfPresent(Position2D position, Direction side) {
        if (position.isValid(width, length) && grid[position.x][position.z] != null) {
            grid[position.x][position.z].piece.openSide(side);
        }
    }

    /**
     * Rotates a dungeon piece according to its connections.
     * This is necessary to ensure that the model for this piece matches its connections.
     *
     * @param placeHolder the place holder of the piece
     * @param rand        an instance of Random which will be used to choose a random one of the valid rotations, should there be more than one
     */
    public void rotatePiece(Tile placeHolder, Random rand) {
        if (placeHolder.hasFlag(Tile.Flag.FIXED_ROTATION))
            return;
        DungeonPiece piece = placeHolder.piece;

        switch (piece.connectedSides) {
            case 1 -> piece.setRotation(Orientation.getRotationFromFacing(DungeonPiece.getOneWayDirection(piece)));
            case 2 -> {
                if (piece.sides[0] && piece.sides[2]) {
                    piece.setRotation(Orientation.getRotationFromFacing(rand.nextBoolean() ? Direction.NORTH : Direction.SOUTH));
                } else if (piece.sides[1] && piece.sides[3]) {
                    piece.setRotation(Orientation.getRotationFromFacing(rand.nextBoolean() ? Direction.EAST : Direction.WEST));
                } else {
                    piece.setRotation(Orientation.getRotationFromCW90DoubleFacing(DungeonPiece.getOpenSide(piece, 0),
                            DungeonPiece.getOpenSide(piece, 1)));
                }
            }
            case 3 -> piece.setRotation(Orientation.getRotationFromTripleFacing(DungeonPiece.getOpenSide(piece, 0),
                    DungeonPiece.getOpenSide(piece, 1), DungeonPiece.getOpenSide(piece, 2)));
            default -> piece.setRotation(Rotation.getRandom(rand));
        }
    }

    /**
     * Rotates a node piece according to its connections.
     * This is necessary to ensure that the model for this piece matches its connections.
     *
     * @param placeHolder the place holder of the node
     * @param rand        an instance of Random which will be used to choose in which direction (clockwise or counterclockwise)
     *                    the algorithm rotates the node until it matches to create more randomness.
     */
    public void rotateNode(Tile placeHolder, Random rand) {
        if (placeHolder.hasFlag(Tile.Flag.FIXED_ROTATION))
            return;
        DungeonNodeRoom node = (DungeonNodeRoom) placeHolder.piece;
        Rotation rotation = Node.getForNodeRoom(node).compare(new Node(node.sides[0], node.sides[1], node.sides[2], node.sides[3]), rand);
        if (rotation != null) {
            node.rotation = rotation;
        } else {
            DungeonCrawl.LOGGER.error("Could not find a proper node rotation for [{} {} {} {}].", node.sides[0],
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
                DungeonSecretRoom room = new DungeonSecretRoom();
                int x = Math.min(outerPosition.x, innerPosition.x), z = Math.min(outerPosition.z, innerPosition.z);
                room.setGridPosition(x, z);
                room.setRotation(Orientation.getRotationFromFacing(direction));
                grid[x][z] = new Tile(room);
                Position2D other = getOther(x, z, direction);
                grid[other.x][other.z] = new Tile(room).addFlag(Tile.Flag.PLACEHOLDER);
                corridor.rotation = Orientation.getRotationFromFacing(direction).getRotated(Rotation.CLOCKWISE_90);
                corridor.model = DungeonModels.KEY_TO_MODEL.get(DungeonModels.SECRET_ROOM_ENTRANCE);
                grid[corridorPos.x][corridorPos.z].addFlag(Tile.Flag.FIXED_MODEL);
                return true;
            }
        }
        return false;
    }

    private static Position2D getOther(int x, int z, Direction direction) {
        return switch (direction) {
            case EAST, WEST -> new Position2D(x + 1, z);
            case SOUTH, NORTH -> new Position2D(x, z + 1);
            default -> throw new UnsupportedOperationException("Can't get other position from direction " + direction);
        };
    }

}
