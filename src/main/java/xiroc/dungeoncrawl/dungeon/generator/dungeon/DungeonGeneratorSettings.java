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

import xiroc.dungeoncrawl.dungeon.Dungeon;

import java.util.Random;
import java.util.function.BiFunction;
import java.util.function.Function;

public class DungeonGeneratorSettings {

    private static final Function<Integer, Integer> DEFAULT_GRID_SIZE = (layer) -> Dungeon.SIZE;

    public static final DungeonGeneratorSettings DEFAULT = new DungeonGeneratorSettings(
            (rand, layer) -> 6, (rand, layer) -> 8,
            5, 1, 3, 5, 1, 5, 1, 6, 3, true);

    public static final DungeonGeneratorSettings LARGE = new DungeonGeneratorSettings(
            (rand, layer) -> 5 + layer / 2, (rand, layer) -> 6 + layer,
            5, 1, 3, 5, 1, 4, 1, 5, 2, true);

    public static final DungeonGeneratorSettings COMPLEX = new DungeonGeneratorSettings(
            (rand, layer) -> 3 + layer / 2, (rand, layer) -> 10,
            5, 1, 1, 7, 1, 4, 1, 7, 3, true);

    public static final DungeonGeneratorSettings CATACOMB_LABYRINTH = new DungeonGeneratorSettings(
            (rand, layer) -> 6 + layer, (rand, layer) -> 20 + layer, (layer) -> 20 + layer,
            5, 1, 3, 20, 1, 15, 1, 20, 5, true);

    /**
     * A function to calculate the grid size for each layer.
     */
    public final Function<Integer, Integer> gridSize;

    /**
     * Functions to calculate the highest allowed amount of nodes and rooms for each layer.
     */
    public final BiFunction<Random, Integer, Integer> maxNodes, maxRooms;

    /**
     * The maximum allowed amount of layers.
     */
    public final int maxLayers;

    /**
     * The minimum and maximum distance between rooms.
     */
    public final int minDistance, maxDistance;

    /**
     * The overall maximum depth for recursive layer generation.
     */
    public final int maxDepth;

    /**
     * The minimum and maximum depth requirements for nodes.
     */
    public final int minNodeDepth, maxNodeDepth;

    /**
     * The minimum and maximum depth requirements for rooms.
     */
    public final int minRoomDepth, maxRoomDepth;

    /**
     * The minimum depth for the stairs to the next layer.
     */
    public final int minStairsDepth;

    /**
     * Determines whether distances between nodes, rooms, etc... should be random or just the lowest possible value.
     */
    public final boolean randomDistances;

    public DungeonGeneratorSettings(BiFunction<Random, Integer, Integer> maxNodes, BiFunction<Random, Integer, Integer> maxRooms, int maxLayers,
                                    int minDistance, int maxDistance, int maxDepth, int minNodeDepth, int maxNodeDepth, int minRoomDepth, int maxRoomDepth, int minStairsDepth,
                                    boolean randomDistances) {
        this.maxNodes = maxNodes;
        this.maxRooms = maxRooms;
        this.gridSize = DEFAULT_GRID_SIZE;

        this.maxLayers = maxLayers;
        this.minDistance = minDistance;
        this.maxDistance = maxDistance;
        this.maxDepth = maxDepth;
        this.minNodeDepth = minNodeDepth;
        this.maxNodeDepth = maxNodeDepth;
        this.minRoomDepth = minRoomDepth;
        this.maxRoomDepth = maxRoomDepth;
        this.minStairsDepth = minStairsDepth;

        this.randomDistances = randomDistances;
    }

    public DungeonGeneratorSettings(BiFunction<Random, Integer, Integer> maxNodes, BiFunction<Random, Integer, Integer> maxRooms, Function<Integer, Integer> gridSize, int maxLayers,
                                    int minDistance, int maxDistance, int maxDepth, int minNodeDepth, int maxNodeDepth, int minRoomDepth, int maxRoomDepth, int minStairsDepth,
                                    boolean randomDistances) {
        this.maxNodes = maxNodes;
        this.maxRooms = maxRooms;
        this.gridSize = gridSize;

        this.maxLayers = maxLayers;
        this.maxDistance = maxDistance;
        this.minDistance = minDistance;
        this.maxDepth = maxDepth;
        this.minNodeDepth = minNodeDepth;
        this.maxNodeDepth = maxNodeDepth;
        this.minRoomDepth = minRoomDepth;
        this.maxRoomDepth = maxRoomDepth;
        this.minStairsDepth = minStairsDepth;

        this.randomDistances = randomDistances;
    }

}
