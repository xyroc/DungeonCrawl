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

package xiroc.dungeoncrawl.dungeon.generator;

import net.minecraft.util.Direction;
import net.minecraft.util.Tuple;
import xiroc.dungeoncrawl.dungeon.DungeonBuilder;
import xiroc.dungeoncrawl.dungeon.DungeonLayer;
import xiroc.dungeoncrawl.dungeon.PlaceHolder;
import xiroc.dungeoncrawl.dungeon.piece.DungeonCorridor;
import xiroc.dungeoncrawl.dungeon.piece.DungeonPiece;
import xiroc.dungeoncrawl.dungeon.piece.DungeonStairs;
import xiroc.dungeoncrawl.util.Orientation;
import xiroc.dungeoncrawl.util.Position2D;

import java.util.Random;

public class CatacombLayerGenerator extends LayerGenerator {

    private int nodesLeft;
    private int roomsLeft;

    public CatacombLayerGenerator(DungeonGeneratorSettings settings) {
        super(settings);
    }

    @Override
    public void initializeLayer(DungeonBuilder dungeonBuilder, Random rand, int layer) {
//        this.nodesLeft = settings.maxNodes.apply(rand, layer);
        this.nodesLeft = 12;
        this.roomsLeft = settings.maxRooms.apply(rand, layer);
    }

    @Override
    public void generateLayer(DungeonBuilder dungeonBuilder, DungeonLayer dungeonLayer, int layer, Random rand, Position2D start) {
        DungeonStairs bottomStairs = new DungeonStairs(null, DungeonPiece.DEFAULT_NBT).bottom();
        bottomStairs.setGridPosition(start.x, start.z);
        dungeonLayer.grid[bottomStairs.gridX][bottomStairs.gridZ] = new PlaceHolder(bottomStairs).addFlag(PlaceHolder.Flag.FIXED_ROTATION);

        dungeonLayer.end = createCorridors(dungeonLayer, rand, createCorridors(dungeonLayer, rand, start));
        DungeonStairs topStairs = new DungeonStairs(null, DungeonPiece.DEFAULT_NBT).top();
        topStairs.setGridPosition(dungeonLayer.end);
        topStairs.takeOverProperties(dungeonLayer.grid[dungeonLayer.end.x][dungeonLayer.end.z].reference);
        dungeonLayer.grid[topStairs.gridX][topStairs.gridZ] = new PlaceHolder(topStairs).addFlag(PlaceHolder.Flag.FIXED_ROTATION);

        createCorridors(dungeonLayer, rand, dungeonLayer.end);
    }


    /**
     * Creates catacomb corridors in random directions
     *
     * @param origin the position for the corridors to start from
     * @return the position of the piece with the highest distance to its' corridors' origin
     */
    private Position2D createCorridors(DungeonLayer dungeonLayer, Random rand, Position2D origin) {
        // TODO: Generator Settings
        Direction[] directions = Orientation.FLAT_FACINGS;

        int maxDirections = 3 + rand.nextInt(2);
        int startDirection = rand.nextInt(4);

        int highestDistance = 0;
        Position2D farthestPos = origin;

        for (int i = 0; i < maxDirections; i++) {
            Direction direction = directions[(i + startDirection) % 4];
            Tuple<Position2D, Integer> pos = catacombCorridor(dungeonLayer, rand, origin, direction, 0);
            if (pos.getB() > highestDistance) {
                highestDistance = pos.getB();
                farthestPos = pos.getA();
            }
        }

        return farthestPos;
    }

    /**
     * Creates a catacomb corridor that goes into the given direction.
     *
     * @param origin    the origin from which the corridor starts.
     *                  No corridor piece will be placed at it.
     * @param direction the direction for the corridor to go into
     * @return a tuple of the position of the farthest piece in the corridor and its distance value
     */
    private Tuple<Position2D, Integer> catacombCorridor(DungeonLayer dungeonLayer, Random rand, Position2D origin, Direction direction, int depth) {
        // TODO: Generator settings
        int length = 3 + rand.nextInt(3) - depth / 2;
        int currentLength = 0;

        Position2D cursor = origin.shift(direction, 1);

        int highestDistance = 0;
        Position2D farthestPos = origin;

        while (cursor.isValid(dungeonLayer.width, dungeonLayer.length) && currentLength < length) {
            if (!dungeonLayer.isTileFree(cursor)) {
                PlaceHolder tile = dungeonLayer.grid[cursor.x][cursor.z];
                if (tile.reference.getType() != 10) {
                    tile.reference.openSide(direction.getOpposite());
                    dungeonLayer.openSideIfPresent(cursor.shift(direction.getOpposite(), 1), direction);

                    if (currentLength > 1 && depth > 0 && nodesLeft > 0 && dungeonLayer.canPlaceNode(cursor.shift(direction, 2)) && rand.nextFloat() < 0.75) {
                        createNode(dungeonLayer, cursor.shift(direction, 2), cursor, rand);
                        break;
                    }
                } else {
                    break;
                }
            } else {
                dungeonLayer.openSideIfPresent(cursor.shift(direction.getOpposite(), 1), direction);

                DungeonCorridor corridor = new DungeonCorridor();
                corridor.setGridPosition(cursor);
                corridor.openSide(direction.getOpposite());

                PlaceHolder placeHolder = new PlaceHolder(corridor);
                dungeonLayer.rotatePiece(placeHolder, rand);
                dungeonLayer.grid[cursor.x][cursor.z] = placeHolder;

                if (currentLength > 1 && depth > 0 && nodesLeft > 0 && dungeonLayer.canPlaceNode(cursor.shift(direction, 2)) && rand.nextFloat() < 0.75) {
                    createNode(dungeonLayer, cursor.shift(direction, 2), cursor, rand);
                    break;
                }

                int distance = dungeonLayer.distance(origin, cursor);
                if (distance > highestDistance) {
                    highestDistance = distance;
                    farthestPos = cursor;
                }

                if (depth < maxDepth && currentLength > 3 && rand.nextFloat() < (0.4F - depth * 0.15F)) {
                    Tuple<Position2D, Integer> pos = catacombCorridor(dungeonLayer, rand, cursor, rand.nextBoolean() ? direction.rotateY() : direction.rotateYCCW(), depth + 1);
                    if (pos.getB() > highestDistance) {
                        highestDistance = pos.getB();
                        farthestPos = pos.getA();
                    }
                }
            }

            cursor = cursor.shift(direction, 1);
            currentLength++;
        }

        return new Tuple<>(farthestPos, highestDistance);
    }

    private void createNode(DungeonLayer dungeonLayer, Position2D center, Position2D corridor, Random rand) {
        createNodeRoom(center, dungeonLayer);
        Direction toCorridor = center.directionTo(corridor);
        connectStraight(dungeonLayer, corridor, center.shift(toCorridor, 1));

        int corridors = rand.nextInt(4);
        int offset = rand.nextInt(3);
        Direction[] directions = Orientation.getHorizontalFacingsWithout(toCorridor);
        for (int i = 0; i < directions.length && corridors > 0; i++) {
            Direction direction = directions[(offset + i) % 3];
            Position2D p = center.shift(direction, 2);
            if (p.isValid(dungeonLayer.width, dungeonLayer.length) && dungeonLayer.isTileFree(p)) {
                catacombCorridor(dungeonLayer, rand, center.shift(direction, 1), direction, 0);
            }
        }

        nodesLeft--;
    }

    /**
     * Builds a straight connection from the start position to the end position.
     * No pieces will be placed at the start and end positions themselves, only in the space between them.
     * The start position and the end position need to have either the same x-coordinate or the same
     * z-coordinate. If this is not the case, an IllegalArgumentException will be thrown.
     *
     * @param start the start position
     * @param end   the end position
     */
    public void connectStraight(DungeonLayer dungeonLayer, Position2D start, Position2D end) {
        if (start.x != end.x || start.z != end.z) {
            if (start.x == end.x) {
                if (start.z > end.z) {
                    // The corridor goes north from the start position (negative z)
                    dungeonLayer.openSideIfPresent(start, Direction.NORTH);
                    dungeonLayer.openSideIfPresent(end, Direction.SOUTH);
                    for (int z = start.z - 1; z > end.z; z--) {
                        if (dungeonLayer.grid[start.x][z] != null) {
                            DungeonPiece piece = dungeonLayer.grid[start.x][z].reference;
                            if (piece.canConnect(Direction.NORTH, start.x, z) && piece.canConnect(Direction.SOUTH, start.x, z)) {
                                piece.openSide(Direction.NORTH);
                                piece.openSide(Direction.SOUTH);
                            }
                        } else {
                            DungeonCorridor corridor = new DungeonCorridor();
                            corridor.setGridPosition(start.x, z);
                            corridor.openSide(Direction.NORTH);
                            corridor.openSide(Direction.SOUTH);
                            corridor.setRotation(Orientation.getRotationFromFacing(Direction.NORTH));
                            dungeonLayer.grid[corridor.gridX][corridor.gridZ] = new PlaceHolder(corridor);
                        }
                    }
                } else {
                    // The corridor goes south from the start position (positive z)
                    dungeonLayer.openSideIfPresent(start, Direction.SOUTH);
                    dungeonLayer.openSideIfPresent(end, Direction.NORTH);
                    for (int z = start.z; z < end.z; z++) {
                        if (dungeonLayer.grid[start.x][z] != null) {
                            DungeonPiece piece = dungeonLayer.grid[start.x][z].reference;
                            if (piece.canConnect(Direction.SOUTH, start.x, z) && piece.canConnect(Direction.NORTH, start.x, z)) {
                                piece.openSide(Direction.SOUTH);
                                piece.openSide(Direction.NORTH);
                            }
                        } else {
                            DungeonCorridor corridor = new DungeonCorridor();
                            corridor.setGridPosition(start.x, z);
                            corridor.openSide(Direction.SOUTH);
                            corridor.openSide(Direction.NORTH);
                            corridor.setRotation(Orientation.getRotationFromFacing(Direction.SOUTH));
                            dungeonLayer.grid[corridor.gridX][corridor.gridZ] = new PlaceHolder(corridor);
                        }
                    }
                }
            } else if (start.z == end.z) {
                if (start.x > end.x) {
                    // The corridor goes west from the start position (negative x)
                    dungeonLayer.openSideIfPresent(start, Direction.WEST);
                    dungeonLayer.openSideIfPresent(end, Direction.EAST);
                    for (int x = start.x - 1; x > end.x; x--) {
                        if (dungeonLayer.grid[x][start.z] != null) {
                            DungeonPiece piece = dungeonLayer.grid[x][start.z].reference;
                            if (piece.canConnect(Direction.WEST, x, start.z) && piece.canConnect(Direction.EAST, x, start.z)) {
                                piece.openSide(Direction.WEST);
                                piece.openSide(Direction.EAST);
                            }
                        } else {
                            DungeonCorridor corridor = new DungeonCorridor();
                            corridor.setGridPosition(x, start.z);
                            corridor.openSide(Direction.WEST);
                            corridor.openSide(Direction.EAST);
                            corridor.setRotation(Orientation.getRotationFromFacing(Direction.WEST));
                            dungeonLayer.grid[corridor.gridX][corridor.gridZ] = new PlaceHolder(corridor);
                        }
                    }
                } else {
                    // The corridor goes east from the start position (positive x)
                    dungeonLayer.openSideIfPresent(start, Direction.EAST);
                    dungeonLayer.openSideIfPresent(end, Direction.WEST);
                    for (int x = start.x; x < end.x; x++) {
                        if (dungeonLayer.grid[x][start.z] != null) {
                            DungeonPiece piece = dungeonLayer.grid[x][start.z].reference;
                            if (piece.canConnect(Direction.EAST, x, start.z) && piece.canConnect(Direction.WEST, x, start.z)) {
                                piece.openSide(Direction.EAST);
                                piece.openSide(Direction.WEST);
                            }
                        } else {
                            DungeonCorridor corridor = new DungeonCorridor();
                            corridor.setGridPosition(x, start.z);
                            corridor.openSide(Direction.EAST);
                            corridor.openSide(Direction.WEST);
                            corridor.setRotation(Orientation.getRotationFromFacing(Direction.EAST));
                            dungeonLayer.grid[corridor.gridX][corridor.gridZ] = new PlaceHolder(corridor);
                        }
                    }
                }
            } else {
                throw new IllegalArgumentException("The start and end positions of a straight connection must have either the same x-coordinate or the same z-coordinate");
            }
        } else {
            throw new IllegalArgumentException("The start and end positions of a straight connection must not be the same.");
        }
    }

}
