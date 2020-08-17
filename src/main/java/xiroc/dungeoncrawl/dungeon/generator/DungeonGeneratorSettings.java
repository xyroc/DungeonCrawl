package xiroc.dungeoncrawl.dungeon.generator;

import java.util.Random;
import java.util.function.BiFunction;

public class DungeonGeneratorSettings {

    public static final DungeonGeneratorSettings DEFAULT = new DungeonGeneratorSettings(
            (rand, layer) -> 3 + layer, (rand, layer) -> 4 + (int) (1.5 * layer),
            5, 2, 3, 5, 1, 3, 1, 5, 2, false);

    public static final DungeonGeneratorSettings LARGE = new DungeonGeneratorSettings(
            (rand, layer) -> 5 + layer / 2, (rand, layer) -> 6 + layer,
            5, 2, 3, 5, 1, 4, 1, 5, 1, true);

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
