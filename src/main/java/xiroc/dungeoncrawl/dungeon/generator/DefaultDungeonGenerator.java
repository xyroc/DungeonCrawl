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

import net.minecraft.util.math.ChunkPos;
import xiroc.dungeoncrawl.DungeonCrawl;
import xiroc.dungeoncrawl.config.Config;
import xiroc.dungeoncrawl.dungeon.DungeonBuilder;
import xiroc.dungeoncrawl.dungeon.DungeonLayer;
import xiroc.dungeoncrawl.dungeon.DungeonType;
import xiroc.dungeoncrawl.dungeon.generator.layer.LayerGenerator;
import xiroc.dungeoncrawl.dungeon.generator.layer.LayerGeneratorSettings;
import xiroc.dungeoncrawl.util.Position2D;

import java.util.Random;

public class DefaultDungeonGenerator extends DungeonGenerator {

    private int secretRoomLayer;

    private LayerGenerator layerGenerator;

    @Override
    public void initializeDungeon(DungeonType type, DungeonBuilder dungeonBuilder, ChunkPos chunkPos, Random rand) {
        super.initializeDungeon(type, dungeonBuilder, chunkPos, rand);
        this.secretRoomLayer = rand.nextInt(2);
    }

    @Override
    public void initializeLayer(LayerGeneratorSettings settings, DungeonBuilder dungeonBuilder, Random rand, int layer, boolean isLastLayer) {
        this.layerGenerator = this.type.getLayer(layer).layerType.layerGenerator;
        this.layerGenerator.initializeLayer(settings, dungeonBuilder, rand, layer, isLastLayer);
    }

    @Override
    public int layerCount(Random rand, int height) {
        return Math.min(type.dungeonSettings.maxLayers, height / 9);
    }

    @Override
    public void generateLayer(DungeonBuilder dungeonBuilder, DungeonLayer dungeonLayer, int layer, Random rand, Position2D start) {
        DungeonCrawl.LOGGER.debug("Generating layout for layer {}", layer);
        if (Config.SECRET_ROOMS.get() && layer == secretRoomLayer) {
            this.layerGenerator.enableSecretRoom();
        }
        this.layerGenerator.generateLayer(dungeonBuilder, dungeonLayer, layer, rand, start);
    }

}
