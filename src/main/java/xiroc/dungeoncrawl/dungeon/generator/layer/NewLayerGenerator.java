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

package xiroc.dungeoncrawl.dungeon.generator.layer;

import com.google.common.collect.Lists;
import net.minecraft.util.Direction;
import net.minecraft.world.storage.loot.RandomValueRange;
import xiroc.dungeoncrawl.DungeonCrawl;
import xiroc.dungeoncrawl.dungeon.DungeonBuilder;
import xiroc.dungeoncrawl.dungeon.DungeonLayer;
import xiroc.dungeoncrawl.dungeon.Tile;
import xiroc.dungeoncrawl.dungeon.piece.DungeonCorridor;
import xiroc.dungeoncrawl.dungeon.piece.DungeonPiece;
import xiroc.dungeoncrawl.dungeon.piece.DungeonStairs;
import xiroc.dungeoncrawl.dungeon.piece.room.DungeonNodeRoom;
import xiroc.dungeoncrawl.dungeon.piece.room.DungeonRoom;
import xiroc.dungeoncrawl.util.Orientation;
import xiroc.dungeoncrawl.util.Position2D;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.function.Consumer;

public class NewLayerGenerator extends LayerGenerator {

    private int roomsLeft, nodesLeft;
    private int rooms, nodes;

    public static final NewLayerGenerator INSTANCE = new NewLayerGenerator();

    private boolean secretRoom;
    private final ArrayList<DungeonCorridor> corridors;
    private ArrayList<LayerElement> newElements;
    private boolean placeStairs;

    private static final Consumer<NewLayerGenerator> NOOP = (generator) -> {
    };

    private static final Consumer<NewLayerGenerator> ON_ROOM_PLACED = (generator) -> {
        generator.roomsLeft--;
        generator.rooms++;
    };

    private static final Consumer<NewLayerGenerator> ON_NODE_PLACED = (generator) -> {
        generator.nodesLeft--;
        generator.nodes++;
    };

    private static final Consumer<NewLayerGenerator> ON_STAIRS_PLACED = (generator) -> {
        generator.placeStairs = false;
    };

    private NewLayerGenerator() {
        this.corridors = new ArrayList<>();
    }

    @Override
    public void initializeLayer(LayerGeneratorSettings settings, DungeonBuilder dungeonBuilder, Random rand, int layer, boolean isLastLayer) {
        super.initializeLayer(settings, dungeonBuilder, rand, layer, isLastLayer);
        this.corridors.clear();
        this.placeStairs = !isLastLayer;
        this.secretRoom = false;
        this.roomsLeft = settings.rooms.generateInt(rand);
        this.nodesLeft = settings.nodes.generateInt(rand);
        this.rooms = 0;
        this.nodes = 0;
    }

    @Override
    public void generateLayer(DungeonBuilder dungeonBuilder, DungeonLayer dungeonLayer, int layer, Random rand, Position2D start) {
        dungeonLayer.start = start;
        this.newElements = new ArrayList<>();
        LayerElement stairs = new GenericElement(new DungeonStairs().bottom(), start, null, null, 0);
        stairs.place(dungeonLayer);
        stairs.update(this, dungeonLayer, rand);
        ArrayList<LayerElement> lastElements = this.newElements;
        do {
            this.newElements = new ArrayList<>();
            for (LayerElement element : lastElements) {
                element.update(this, dungeonLayer, rand);
            }
            lastElements = newElements;
        } while (!lastElements.isEmpty());

        if (secretRoom && !this.corridors.isEmpty()) {
            for (int i = 0; i < 8; i++) {
                DungeonCorridor corridor = this.corridors.get(rand.nextInt(corridors.size()));
                if (corridor.isStraight() && corridor.connectedSides == 2 && dungeonLayer.placeSecretRoom(corridor, corridor.gridPosition, rand)) {
                    break;
                }
                this.corridors.remove(corridor);
            }
        }

        corridors.clear();
        newElements.clear();

        DungeonCrawl.LOGGER.debug("Generated Layer {} with {}({}) Nodes and {}({}) Rooms", layer, nodes, nodesLeft, rooms, roomsLeft);
    }

    /**
     * Performs a single layer generation step at the cursor position.
     *
     * @param cursor       the current element
     * @param dungeonLayer the dungeon layer
     * @param rand         a random instance
     * @param depth        the current generation depth
     */
    private void generationStep(LayerElement cursor, DungeonLayer dungeonLayer, @Nullable Direction excludedDirection, Random rand, int depth) {
        if (depth > settings.maxDepth) {
            return;
        }
        List<Direction> directions = Lists.newArrayList(excludedDirection != null ? Orientation.getHorizontalFacingsWithout(excludedDirection) : Orientation.HORIZONTAL_FACINGS);
        int count = depth < 2 ? 2 + rand.nextInt(2) : 1 + rand.nextInt(3);
        for (; count > 0; count--) {
            Direction direction = directions.remove(rand.nextInt(directions.size()));
            int maxDistance = maxDistance(cursor.position, direction, dungeonLayer);
            if (maxDistance >= settings.minDistance) {
                Position2D nextPos = cursor.position.shift(direction, new RandomValueRange(settings.minDistance, maxDistance).generateInt(rand));
                if (nextPos.isValid(dungeonLayer.width, dungeonLayer.length)) {
                    LayerElement element = nextElement(dungeonLayer, nextPos, direction.getOpposite(), rand, depth);
                    if (element != null) {
                        boolean canConnect = canConnectStraight(dungeonLayer, cursor, element);
                        if (canConnect) {
                            element.place(dungeonLayer);
                            element.onPlaced.accept(this); // Update room / node counts.
                            connectStraight(dungeonLayer, cursor.getConnectionPoint(direction), element.getConnectionPoint(direction.getOpposite()));
                            this.newElements.add(element);
                        }
                    }
                }
            }
        }
    }

    @Nullable
    private LayerElement nextElement(DungeonLayer dungeonLayer, Position2D pos, Direction toOrigin, Random rand, int depth) {
        if (dungeonLayer.isTileFree(pos)) {
            if (placeStairs && depth <= settings.minStairsDepth) {
                dungeonLayer.end = pos;
                return new GenericElement(new DungeonStairs().top(), pos, toOrigin, ON_STAIRS_PLACED, depth);
            }
            if (depth <= settings.maxNodeDepth && nodesLeft > 0 && rand.nextFloat() < 0.65F) {
                if (dungeonLayer.canPlaceNode(pos)) {
                    return new NodeElement(pos, toOrigin, depth);
                }
            }
            if (depth <= settings.maxRoomDepth && roomsLeft > 0) {
                if (depth < 3 && rand.nextFloat() < 0.45) {
                    return new GenericElement(new DungeonCorridor(), pos, toOrigin, NOOP, depth);
                }
                return new GenericElement(new DungeonRoom(), pos, toOrigin, ON_ROOM_PLACED, depth);
            }
        }
        return null;
    }

    private int maxDistance(Position2D pos, Direction direction, DungeonLayer dungeonLayer) {
        switch (direction) {
            case NORTH:
                return Math.min(pos.z, settings.maxDistance);
            case SOUTH:
                return Math.min(dungeonLayer.length - pos.z - 1, settings.maxDistance);
            case WEST:
                return Math.min(pos.x, settings.maxDistance);
            case EAST:
                return Math.min(dungeonLayer.width - pos.x - 1, settings.maxDistance);
            default:
                return 0;
        }
    }

    @Override
    public void enableSecretRoom() {
        this.secretRoom = true;
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
                            DungeonPiece piece = dungeonLayer.grid[start.x][z].piece;
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
                            dungeonLayer.grid[corridor.gridPosition.x][corridor.gridPosition.z] = new Tile(corridor);
                            this.corridors.add(corridor);
                        }
                    }
                } else {
                    // The corridor goes south from the start position (positive z)
                    dungeonLayer.openSideIfPresent(start, Direction.SOUTH);
                    dungeonLayer.openSideIfPresent(end, Direction.NORTH);
                    for (int z = start.z + 1; z < end.z; z++) {
                        if (dungeonLayer.grid[start.x][z] != null) {
                            DungeonPiece piece = dungeonLayer.grid[start.x][z].piece;
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
                            dungeonLayer.grid[corridor.gridPosition.x][corridor.gridPosition.z] = new Tile(corridor);
                            this.corridors.add(corridor);
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
                            DungeonPiece piece = dungeonLayer.grid[x][start.z].piece;
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
                            dungeonLayer.grid[corridor.gridPosition.x][corridor.gridPosition.z] = new Tile(corridor);
                            this.corridors.add(corridor);
                        }
                    }
                } else {
                    // The corridor goes east from the start position (positive x)
                    dungeonLayer.openSideIfPresent(start, Direction.EAST);
                    dungeonLayer.openSideIfPresent(end, Direction.WEST);
                    for (int x = start.x + 1; x < end.x; x++) {
                        if (dungeonLayer.grid[x][start.z] != null) {
                            DungeonPiece piece = dungeonLayer.grid[x][start.z].piece;
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
                            dungeonLayer.grid[corridor.gridPosition.x][corridor.gridPosition.z] = new Tile(corridor);
                            this.corridors.add(corridor);
                        }
                    }
                }
            } else {
                throw new IllegalArgumentException("The start and end positions of a straight connection must have either the same x-coordinate or the same z-coordinate.");
            }
        } else {
            throw new IllegalArgumentException("The start and end positions of a straight connection must not be the same.");
        }
    }

    /**
     * Evaluates whether the two elements can be connected through a straight corridor.
     *
     * @param dungeonLayer the dungeon layer
     * @param start        the start element
     * @param end          the end element
     * @return true if the connection can be built, false if not.
     */
    public boolean canConnectStraight(DungeonLayer dungeonLayer, LayerElement start, LayerElement end) {
        if (start.position.x != end.position.x || start.position.z != end.position.z) {
            if (start.position.x == end.position.x) {
                if (start.position.z > end.position.z) {
                    // The corridor goes north from the start.position position (negative z)
                    if (!start.piece.canConnect(Direction.NORTH, end.position.x, end.position.z)
                            || !end.piece.canConnect(Direction.SOUTH, start.position.x, start.position.z)) {
                        return false;
                    }
                    for (int z = start.position.z - 1; z > end.position.z; z--) {
                        if (dungeonLayer.grid[start.position.x][z] != null) {
                            DungeonPiece piece = dungeonLayer.grid[start.position.x][z].piece;
                            if (!piece.canConnect(Direction.NORTH, start.position.x, z) || !piece.canConnect(Direction.SOUTH, start.position.x, z)) {
                                return false;
                            }
                        }
                    }
                    return true;
                } else {
                    // The corridor goes south from the start.position position (positive z)
                    if (!start.piece.canConnect(Direction.SOUTH, end.position.x, end.position.z)
                            || !end.piece.canConnect(Direction.NORTH, start.position.x, start.position.z)) {
                        return false;
                    }
                    for (int z = start.position.z; z < end.position.z; z++) {
                        if (dungeonLayer.grid[start.position.x][z] != null) {
                            DungeonPiece piece = dungeonLayer.grid[start.position.x][z].piece;
                            if (!piece.canConnect(Direction.SOUTH, start.position.x, z) || !piece.canConnect(Direction.NORTH, start.position.x, z)) {
                                return false;
                            }
                        }
                    }
                    return true;
                }
            } else if (start.position.z == end.position.z) {
                if (start.position.x > end.position.x) {
                    // The corridor goes west from the start.position position (negative x)
                    if (!start.piece.canConnect(Direction.WEST, end.position.x, end.position.z)
                            || !end.piece.canConnect(Direction.EAST, start.position.x, start.position.z)) {
                        return false;
                    }
                    for (int x = start.position.x - 1; x > end.position.x; x--) {
                        if (dungeonLayer.grid[x][start.position.z] != null) {
                            DungeonPiece piece = dungeonLayer.grid[x][start.position.z].piece;
                            if (!piece.canConnect(Direction.WEST, x, start.position.z) || !piece.canConnect(Direction.EAST, x, start.position.z)) {
                                return false;
                            }
                        }
                    }
                    return true;
                } else {
                    // The corridor goes east from the start.position position (positive x)
                    if (!start.piece.canConnect(Direction.EAST, end.position.x, end.position.z)
                            || !end.piece.canConnect(Direction.WEST, start.position.x, start.position.z)) {
                        return false;
                    }
                    for (int x = start.position.x; x < end.position.x; x++) {
                        if (dungeonLayer.grid[x][start.position.z] != null) {
                            DungeonPiece piece = dungeonLayer.grid[x][start.position.z].piece;
                            if (!piece.canConnect(Direction.EAST, x, start.position.z) || !piece.canConnect(Direction.WEST, x, start.position.z)) {
                                return false;
                            }
                        }
                    }
                    return true;
                }
            } else { // The two elements aren't lined up.
                return false;
            }
        } else { // The two elements are in the same spot.
            return false;
        }
    }

    private static abstract class LayerElement {

        final Position2D position;
        final Direction toOrigin;
        final int depth;
        final DungeonPiece piece;
        final Consumer<NewLayerGenerator> onPlaced;

        LayerElement(DungeonPiece piece, Position2D position, Direction toOrigin, Consumer<NewLayerGenerator> onPlaced, int depth) {
            this.piece = piece;
            this.piece.setGridPosition(position);
            this.position = position;
            this.toOrigin = toOrigin;
            this.onPlaced = onPlaced;
            this.depth = depth;
        }

        public void place(DungeonLayer layer) {
            layer.grid[position.x][position.z] = new Tile(piece);
        }

        public void update(NewLayerGenerator layerGenerator, DungeonLayer dungeonLayer, Random rand) {
            layerGenerator.generationStep(this, dungeonLayer, toOrigin, rand, this.depth);
        }

        public abstract Position2D getConnectionPoint(Direction connectionSide);

    }

    private static class NodeElement extends LayerElement {

        NodeElement(Position2D center, Direction toOrigin, int depth) {
            super(new DungeonNodeRoom(), center, toOrigin, ON_NODE_PLACED, depth);
        }

        @Override
        public void place(DungeonLayer layer) {
            LayerGenerator.placeNodeRoom((DungeonNodeRoom) piece, position, layer);
        }

        @Override
        public Position2D getConnectionPoint(Direction connectionSide) {
            return position.shift(connectionSide, 1);
        }

    }

    private static class GenericElement extends LayerElement {

        GenericElement(DungeonPiece piece, Position2D position, Direction toOrigin, Consumer<NewLayerGenerator> onPlaced, int depth) {
            super(piece, position, toOrigin, onPlaced, depth);
        }

        @Override
        public Position2D getConnectionPoint(Direction connectionSide) {
            return position;
        }

    }

}
