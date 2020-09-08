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
import net.minecraft.world.gen.ChunkGenerator;
import xiroc.dungeoncrawl.dungeon.DungeonBuilder;
import xiroc.dungeoncrawl.dungeon.DungeonLayer;
import xiroc.dungeoncrawl.util.Position2D;

import java.util.Random;

/**
 * The base class for all dungeon generator types.
 */
public abstract class DungeonGenerator {

    /**
     * The settings in use.
     */
    public DungeonGeneratorSettings settings;

    /**
     * The highest allowed amount of nodes and rooms for each layer.
     */
    public int[] maxNodes, maxRooms;

    /**
     * The maximum allowed amount of layers.
     */
    public int maxLayers;

    /**
     * The overall maximum depth for recursive layer generation.
     */
    public int maxDepth;

    /**
     * The minimum and maximum distance between rooms.
     */
    public int minDistance, maxDistance;

    /**
     * The minimum and maximum depth requirements for nodes.
     */
    public int minNodeDepth, maxNodeDepth;

    /**
     * The minimum and maximum depth requirements for rooms.
     */
    public int minRoomDepth, maxRoomDepth;


    public int minStairsDepth;

    /**
     * Determines whether distances between nodes, rooms, etc... should be random or just the lowest possible value.
     */
    public boolean randomDistances;

    public DungeonGenerator(DungeonGeneratorSettings settings) {
        loadSettings(settings);
    }

    public void loadSettings(DungeonGeneratorSettings settings) {
        this.maxNodes = new int[settings.maxLayers];
        this.maxRooms = new int[settings.maxLayers];

        this.minDistance = settings.minDistance;
        this.maxDistance = settings.maxDistance;
        this.maxLayers = settings.maxLayers;
        this.maxDepth = settings.maxDepth;
        this.minNodeDepth = settings.minNodeDepth;
        this.maxNodeDepth = settings.maxNodeDepth;
        this.minRoomDepth = settings.minRoomDepth;
        this.maxRoomDepth = settings.maxRoomDepth;
        this.minStairsDepth = settings.minStairsDepth;

        this.randomDistances = settings.randomDistances;

        this.settings = settings;
    }

    /**
     * @return whether the given layer contains a secret room or not.
     */
    private boolean doesLayerHaveSecretRoom(int layer) {
        return false;
    }

    /**
     * Called in constructor of the dungeon builder. This should be used to setup
     */
    public abstract void initialize(ChunkGenerator<?> chunkGenerator, DungeonBuilder dungeonBuilder, ChunkPos chunkPos, Random rand);

    /**
     * @return the amount of layers the dungeon will have.
     */
    public abstract int calculateLayerCount(ChunkGenerator<?> chunkGenerator, Random rand, int height);

    /**
     * Generates a specific layer.
     */
    public abstract void generateLayer(DungeonBuilder dungeonBuilder, DungeonLayer dungeonLayer, int layer, Random rand, Position2D start);

    /**
     * @return whether this generator supports mutation or not.
     */
    public abstract boolean supportsMutation();

    /**
     * Used to mutate this generator after its initialization, if supported.
     */
    public abstract void mutate(Random rand);

}
