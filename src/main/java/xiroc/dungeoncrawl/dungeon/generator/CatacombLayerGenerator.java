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
import xiroc.dungeoncrawl.dungeon.piece.DungeonCorridor;
import xiroc.dungeoncrawl.dungeon.piece.DungeonPiece;
import xiroc.dungeoncrawl.dungeon.piece.DungeonStairs;
import xiroc.dungeoncrawl.dungeon.piece.PlaceHolder;
import xiroc.dungeoncrawl.util.Orientation;
import xiroc.dungeoncrawl.util.Position2D;

import java.util.Random;

public class CatacombLayerGenerator extends LayerGenerator {

    private boolean stairsPlaced;

    private int nodesLeft;
    private int roomsLeft;

    private Position2D farthestRoom;

    public CatacombLayerGenerator(DungeonGeneratorSettings settings) {
        super(settings);
    }

    @Override
    public void initializeLayer(DungeonBuilder dungeonBuilder, Random rand, int layer) {
        this.stairsPlaced = false;
        this.nodesLeft = settings.maxRooms.apply(rand, layer);
    }

    @Override
    public void generateLayer(DungeonBuilder dungeonBuilder, DungeonLayer dungeonLayer, int layer, Random rand, Position2D start) {
        DungeonStairs bottomStairs = new DungeonStairs(null, DungeonPiece.DEFAULT_NBT).bottom();
        bottomStairs.setGridPosition(start.x, start.z);
        dungeonLayer.grid[bottomStairs.gridX][bottomStairs.gridZ] = new PlaceHolder(bottomStairs).addFlag(PlaceHolder.Flag.FIXED_ROTATION);

        dungeonLayer.end = findEndPosition(dungeonLayer, start);
        DungeonStairs topStairs = new DungeonStairs(null, DungeonPiece.DEFAULT_NBT).top();
        topStairs.setGridPosition(dungeonLayer.end);
        dungeonLayer.grid[topStairs.gridX][topStairs.gridZ] = new PlaceHolder(topStairs).addFlag(PlaceHolder.Flag.FIXED_ROTATION);

        Position2D pos1 = createCorridors(dungeonBuilder, dungeonLayer, rand, start, layer);
        Position2D pos2 = createCorridors(dungeonBuilder, dungeonLayer, rand, dungeonLayer.end, layer);

        dungeonLayer.buildConnection(pos1, pos2, rand);
    }

    /**
     * Creates a catacomb corridor in each direction
     *
     * @param origin the position for the corridors to start from
     * @return the position of the piece with the highest distance to its' corridors' origin
     */
    private Position2D createCorridors(DungeonBuilder dungeonBuilder, DungeonLayer dungeonLayer, Random rand, Position2D origin, int layer) {
        Direction[] directions = Orientation.FLAT_FACINGS;

        int maxDirections = 3 + rand.nextInt(2);
        int startDirection = rand.nextInt(4);
        int counter = 0;

        int highestDistance = 0;
        Position2D farthestPos = origin;

        for (int i = 0; i < 4; i++) {
            if (counter < maxDirections) {
                Direction direction = directions[(i + startDirection) % 4];
                Tuple<Position2D, Integer> pos = catacombCorridor(dungeonBuilder, dungeonLayer, layer, rand, origin, direction, 0);
                if (pos.getB() > highestDistance) {
                    highestDistance = pos.getB();
                    farthestPos = pos.getA();
                }
                counter++;
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
    private Tuple<Position2D, Integer> catacombCorridor(DungeonBuilder builder, DungeonLayer dungeonLayer, int layer, Random rand, Position2D origin, Direction direction, int depth) {
        int length = 2 + rand.nextInt(3);
        int currentLength = 0;

        Position2D cursor = origin.shift(direction, 1);

        int highestDistance = 0;
        Position2D farthestPos = origin;

        dungeonLayer.openSideIfExistent(origin, direction);

        while (cursor.isValid(dungeonLayer.width, dungeonLayer.length) && currentLength < length) {
            if (!dungeonLayer.isTileFree(cursor)) {
                PlaceHolder tile = dungeonLayer.grid[cursor.x][cursor.z];
                if (!(tile.reference.gridX == cursor.x && tile.reference.gridZ == cursor.z)) {
                    tile.reference.openSide(direction.getOpposite());
                    dungeonLayer.openSideIfExistent(cursor.shift(direction.getOpposite(), 1), direction);
                } else {
                    dungeonLayer.openSideIfExistent(cursor.shift(direction.getOpposite(), 1), direction);
                    tile.reference.openSide(direction.getOpposite());
                    if (tile.reference.getType() == 10) {
                        dungeonLayer.rotateNode(tile, rand);
                    } else {
                        dungeonLayer.rotatePiece(tile, rand);
                    }
                }
            } else {
                dungeonLayer.openSideIfExistent(cursor.shift(direction.getOpposite(), 1), direction);

                DungeonCorridor corridor = new DungeonCorridor();
                corridor.setGridPosition(cursor);
                corridor.openSide(direction.getOpposite());

                PlaceHolder placeHolder = new PlaceHolder(corridor);
                dungeonLayer.rotatePiece(placeHolder, rand);
                dungeonLayer.grid[cursor.x][cursor.z] = placeHolder;
            }

            int distance = dungeonLayer.distance(origin, cursor);
            if (distance > highestDistance) {
                highestDistance = distance;
                farthestPos = cursor;
            }

            if (depth < maxDepth && rand.nextFloat() < (0.3F - depth * 0.05F)) {
                Tuple<Position2D, Integer> pos = catacombCorridor(builder, dungeonLayer, layer, rand, cursor, rand.nextBoolean() ? direction.rotateY() : direction.rotateYCCW(), depth + 1);
                if (pos.getB() > highestDistance) {
                    highestDistance = pos.getB();
                    farthestPos = pos.getA();
                }
            }

            cursor = cursor.shift(direction, 1);
            currentLength++;
        }
        return new Tuple<>(farthestPos, highestDistance);
    }

    private Position2D findEndPosition(DungeonLayer dungeonLayer, Position2D start) {
        int w = dungeonLayer.width / 2;
        int l = dungeonLayer.length / 2;
        return start.shift(start.x > w ? Direction.WEST : Direction.EAST, w).shift(start.z > l ? Direction.NORTH : Direction.SOUTH, l);
    }

    @Override
    public boolean supportsMutation() {
        return false;
    }

    @Override
    public void mutate(Random rand) {

    }

}
