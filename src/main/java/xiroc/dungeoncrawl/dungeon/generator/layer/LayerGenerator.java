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

import xiroc.dungeoncrawl.dungeon.DungeonBuilder;
import xiroc.dungeoncrawl.dungeon.DungeonLayer;
import xiroc.dungeoncrawl.dungeon.PlaceHolder;
import xiroc.dungeoncrawl.dungeon.piece.room.DungeonNodeRoom;
import xiroc.dungeoncrawl.util.Position2D;

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
        DungeonNodeRoom nodeRoom = new DungeonNodeRoom();
        nodeRoom.setGridPosition(center.x, center.z);

        PlaceHolder placeHolder = new PlaceHolder(nodeRoom).addFlag(PlaceHolder.Flag.PLACEHOLDER);
        for (int x = -1; x < 2; x++)
            for (int z = -1; z < 2; z++)
                if ((x != 0 || z != 0) && Position2D.isValid(center.x + x, center.z + z, dungeonLayer.width, dungeonLayer.length))
                    dungeonLayer.grid[center.x + x][center.z + z] = placeHolder;

        dungeonLayer.grid[center.x][center.z] = new PlaceHolder(nodeRoom);
    }

    /**
     * Used to (re-)initialize the layer generator. Called before every layer generation.
     */
    public void initializeLayer(LayerGeneratorSettings settings, DungeonBuilder dungeonBuilder, Random rand, int layer) {
        this.settings = settings;
    }

    /**
     * Generates a specific layer
     */
    public abstract void generateLayer(DungeonBuilder dungeonBuilder, DungeonLayer dungeonLayer, int layer, Random rand, Position2D start);

    public void enableSecretRoom() {
    }

}
