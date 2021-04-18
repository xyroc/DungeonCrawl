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

package xiroc.dungeoncrawl.dungeon.generator.dungeon;

import net.minecraft.util.math.ChunkPos;
import xiroc.dungeoncrawl.DungeonCrawl;
import xiroc.dungeoncrawl.dungeon.DungeonBuilder;
import xiroc.dungeoncrawl.dungeon.DungeonLayer;
import xiroc.dungeoncrawl.dungeon.generator.CatacombLayerGenerator;
import xiroc.dungeoncrawl.dungeon.generator.DefaultLayerGenerator;
import xiroc.dungeoncrawl.dungeon.generator.LayerGenerator;
import xiroc.dungeoncrawl.dungeon.model.ModelCategory;
import xiroc.dungeoncrawl.util.Position2D;

import java.util.Random;

public class DefaultDungeonGenerator extends DungeonGenerator {

    private int secretRoomLayer;

    private final DefaultLayerGenerator defaultGenerator;
    private final CatacombLayerGenerator catacombGenerator;
    private final LayerGenerator hellGenerator;

    public DefaultDungeonGenerator(DungeonGeneratorSettings settings) {
        super(settings);
        this.defaultGenerator = new DefaultLayerGenerator(settings);
        this.catacombGenerator = new CatacombLayerGenerator(settings);
        this.hellGenerator = new DefaultLayerGenerator(settings);
    }

    @Override
    public void initializeDungeon(DungeonBuilder dungeonBuilder, ChunkPos chunkPos, Random rand) {
        this.secretRoomLayer = rand.nextInt(2);
    }

    @Override
    public void initializeLayer(DungeonBuilder dungeonBuilder, Random rand, int layer) {
        this.defaultGenerator.initializeLayer(dungeonBuilder, rand, layer);
        this.catacombGenerator.initializeLayer(dungeonBuilder, rand, layer);
        this.hellGenerator.initializeLayer(dungeonBuilder, rand, layer);
    }

    @Override
    public int layerCount(Random rand, int height) {
        return Math.min(maxLayers, height / 9);
    }

    @Override
    public ModelCategory getCategoryForLayer(int layer) {
        return ModelCategory.getCategoryForStage(layer);
    }

    @Override
    public void generateLayer(DungeonBuilder dungeonBuilder, DungeonLayer dungeonLayer, int layer, Random rand, Position2D start) {
        DungeonCrawl.LOGGER.debug("Generating layout for layer {}.", layer);
        // TODO: Generator settings
        if (layer >= 4) {
            hellGenerator.generateLayer(dungeonBuilder, dungeonLayer, layer, rand, start);
//        } else if (layer >= 2) {
//            catacombGenerator.generateLayer(dungeonBuilder, dungeonLayer, layer, rand, start);
        } else {
            defaultGenerator.setSecretRoom(layer == secretRoomLayer);
            defaultGenerator.generateLayer(dungeonBuilder, dungeonLayer, layer, rand, start);
        }
    }

}
