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

import net.minecraft.core.Direction;
import net.minecraft.util.Tuple;
import net.minecraft.world.level.block.Rotation;
import xiroc.dungeoncrawl.dungeon.DungeonBuilder;
import xiroc.dungeoncrawl.dungeon.DungeonLayer;
import xiroc.dungeoncrawl.dungeon.Tile;
import xiroc.dungeoncrawl.dungeon.model.DungeonModels;
import xiroc.dungeoncrawl.dungeon.piece.DungeonCorridor;
import xiroc.dungeoncrawl.dungeon.piece.room.DungeonNodeRoom;
import xiroc.dungeoncrawl.dungeon.piece.room.DungeonSideRoom;
import xiroc.dungeoncrawl.util.Orientation;
import xiroc.dungeoncrawl.util.Position2D;

import java.util.List;
import java.util.Random;

public abstract class LayerGenerator {

    /**
     * The settings in use.
     */
    public LayerGeneratorSettings settings;

    /**
     * Creates a node room at the given position in the specified layer.
     *
     * @param center       the position of the center of the node room
     * @param dungeonLayer the layer to place the node room in
     */
    public static void createNodeRoom(Position2D center, DungeonLayer dungeonLayer) {
        placeNodeRoom(new DungeonNodeRoom(), center, dungeonLayer);
    }

    /**
     * Places the given node room at the given position in the specified layer.
     *
     * @param center       the position of the center of the node room
     * @param dungeonLayer the layer to place the node room in
     */
    public static void placeNodeRoom(DungeonNodeRoom nodeRoom, Position2D center, DungeonLayer dungeonLayer) {
        nodeRoom.setGridPosition(center.x, center.z);

        Tile placeHolder = new Tile(nodeRoom).addFlag(Tile.Flag.PLACEHOLDER);
        for (int x = -1; x < 2; x++)
            for (int z = -1; z < 2; z++)
                if ((x != 0 || z != 0) && Position2D.isValid(center.x + x, center.z + z, dungeonLayer.width, dungeonLayer.length))
                    dungeonLayer.grid[center.x + x][center.z + z] = placeHolder;

        dungeonLayer.grid[center.x][center.z] = new Tile(nodeRoom);
    }

    public static DungeonCorridor createCorridor(DungeonLayer dungeonLayer, int x, int z, Direction from, Direction to) {
        DungeonCorridor corridor = new DungeonCorridor();
        corridor.setGridPosition(x, z);
        corridor.openSide(from);
        corridor.openSide(to);
        corridor.setRotation(Orientation.getRotationFromFacing(from));
        dungeonLayer.grid[corridor.gridPosition.x][corridor.gridPosition.z] = new Tile(corridor);
        return corridor;
    }

    public static void tryCreateSecretRoom(DungeonLayer dungeonLayer, List<DungeonCorridor> corridors, int maxAttempts, Random random) {
        for (int i = 0; i < maxAttempts; i++) {
            if (corridors.isEmpty()) {
                break;
            }
            DungeonCorridor corridor = corridors.get(random.nextInt(corridors.size()));
            if (corridor.isStraight() && corridor.connectedSides == 2 && dungeonLayer.placeSecretRoom(corridor, corridor.gridPosition, random)) {
                break;
            }
            corridors.remove(corridor);
        }
    }

    public static void createStarterRoom(DungeonLayer dungeonLayer, Random rand, int layer) {
        Tuple<Position2D, Rotation> sideRoomData = dungeonLayer.findStarterRoomData(dungeonLayer.start, rand);
        if (sideRoomData != null) {
            DungeonSideRoom room = new DungeonSideRoom();

            Direction dir = sideRoomData.getB().rotate(Direction.WEST);
            room.openSide(dir);
            room.setGridPosition(sideRoomData.getA().x, sideRoomData.getA().z);
            room.setRotation(sideRoomData.getB());
            room.model = DungeonModels.KEY_TO_MODEL.get(DungeonModels.STARTER_ROOM);
            room.stage = layer;

            dungeonLayer.map.markPositionAsOccupied(sideRoomData.getA());
            dungeonLayer.grid[sideRoomData.getA().x][sideRoomData.getA().z] = new Tile(room).addFlag(Tile.Flag.FIXED_MODEL);

            Position2D connectedSegment = sideRoomData.getA().shift(dir, 1);
            if (dungeonLayer.grid[connectedSegment.x][connectedSegment.z] != null) {
                dungeonLayer.grid[connectedSegment.x][connectedSegment.z].piece.openSide(dir.getOpposite());
                dungeonLayer.rotatePiece(dungeonLayer.grid[connectedSegment.x][connectedSegment.z], rand);
            }
        }
    }

    /**
     * Used to (re-)initialize the layer generator. Called before every layer generation.
     */
    public void initializeLayer(LayerGeneratorSettings settings, DungeonBuilder dungeonBuilder, Random rand,
                                int layer, boolean isLastLayer) {
        this.settings = settings;
    }

    /**
     * Generates a specific layer
     */
    public abstract void generateLayer(DungeonBuilder dungeonBuilder, DungeonLayer dungeonLayer, int layer, Random
            rand, Position2D start);

    public void enableSecretRoom() {
    }

}
