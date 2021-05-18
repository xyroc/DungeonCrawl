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

import net.minecraft.util.Direction;
import xiroc.dungeoncrawl.dungeon.DungeonBuilder;
import xiroc.dungeoncrawl.dungeon.DungeonLayer;
import xiroc.dungeoncrawl.dungeon.piece.DungeonPiece;
import xiroc.dungeoncrawl.dungeon.piece.room.DungeonNodeRoom;
import xiroc.dungeoncrawl.util.Position2D;

import java.util.ArrayList;
import java.util.Random;

public class NewLayerGenerator extends LayerGenerator {

    private boolean secretRoom;
    private final ArrayList<LayerElement> elements, cursors;

    public NewLayerGenerator() {
        this.secretRoom = false;
        this.elements = new ArrayList<>();
        this.cursors = new ArrayList<>();
    }

    @Override
    public void initializeLayer(LayerGeneratorSettings settings, DungeonBuilder dungeonBuilder, Random rand, int layer) {
        super.initializeLayer(settings, dungeonBuilder, rand, layer);
        this.elements.clear();
    }

    @Override
    public void generateLayer(DungeonBuilder dungeonBuilder, DungeonLayer dungeonLayer, int layer, Random rand, Position2D start) {

    }

    /**
     * Performs a single layer generation step at the cursor position.
     *
     * @param cursor       the current element
     * @param dungeonLayer the dungeon layer
     * @param rand         a random instance
     * @param start        the position of the start piece
     * @param layer        the layer index
     * @param depth        the current generation depth
     */
    private void generationStep(LayerElement cursor, DungeonLayer dungeonLayer, Random rand, Position2D start, int layer, int depth) {
        int offset = rand.nextInt(4);
        int directions = layer < 2 ? 2 + rand.nextInt(2) : 1 + rand.nextInt(3);
        for (; directions > 0; directions--) {
//            Direction direction = cursor.
        }
    }

    @Override
    public void enableSecretRoom() {
        this.secretRoom = true;
    }

    private static abstract class LayerElement {

        final Position2D position;
        final Direction toOrigin;
        final int depth;
        final DungeonPiece piece;

        LayerElement(Position2D position, Direction toOrigin, int depth) {
            this.position = position;
            this.toOrigin = toOrigin;
            this.depth = depth;
            this.piece = createPiece();
        }

        abstract DungeonPiece createPiece();

        public abstract void update(NewLayerGenerator layerGenerator, DungeonLayer dungeonLayer, Random rand, Position2D start, int layer);

        public abstract Position2D getConnectionPoint(Direction connectionSide);

    }

    private static class NodeElement extends LayerElement {

        NodeElement(Position2D position, Direction toOrigin, int depth) {
            super(position, toOrigin, depth);
        }

        @Override
        DungeonPiece createPiece() {
            return new DungeonNodeRoom();
        }

        @Override
        public void update(NewLayerGenerator layerGenerator, DungeonLayer dungeonLayer, Random rand, Position2D start, int layer) {

        }

        @Override
        public Position2D getConnectionPoint(Direction connectionSide) {
            return null;
        }

    }

}
