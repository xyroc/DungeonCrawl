package xiroc.dungeoncrawl.dungeon.generator;

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
    public DungeonGeneratorSettings settings;

    /**
     * The maximum allowed amount of layers.
     */
    public int maxLayers;

    /**
     * The overall maximum depth for recursive layer generation.
     */
    public int maxDepth;

    /**
     * The minimum and maximum distance
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

    public LayerGenerator(DungeonGeneratorSettings settings) {
        loadSettings(settings);
    }

    public void loadSettings(DungeonGeneratorSettings settings) {
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
     * Used to (re-)initialize the layer generator. Called before every layer generation .
     */
    public abstract void initializeLayer(DungeonBuilder dungeonBuilder, Random rand, int layer);

    /**
     * Generates a specific layer
     */
    public abstract void generateLayer(DungeonBuilder dungeonBuilder, DungeonLayer dungeonLayer, int layer, Random rand, Position2D start);

}
