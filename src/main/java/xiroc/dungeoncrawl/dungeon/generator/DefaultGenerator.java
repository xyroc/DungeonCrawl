package xiroc.dungeoncrawl.dungeon.generator;

import com.google.common.collect.Lists;
import net.minecraft.util.Direction;
import net.minecraft.util.Rotation;
import net.minecraft.util.Tuple;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.gen.ChunkGenerator;
import xiroc.dungeoncrawl.DungeonCrawl;
import xiroc.dungeoncrawl.dungeon.Dungeon;
import xiroc.dungeoncrawl.dungeon.DungeonBuilder;
import xiroc.dungeoncrawl.dungeon.DungeonFeatures;
import xiroc.dungeoncrawl.dungeon.DungeonLayer;
import xiroc.dungeoncrawl.dungeon.piece.DungeonCorridor;
import xiroc.dungeoncrawl.dungeon.piece.DungeonPiece;
import xiroc.dungeoncrawl.dungeon.piece.DungeonStairs;
import xiroc.dungeoncrawl.dungeon.piece.PlaceHolder;
import xiroc.dungeoncrawl.dungeon.piece.room.DungeonNodeRoom;
import xiroc.dungeoncrawl.dungeon.piece.room.DungeonRoom;
import xiroc.dungeoncrawl.dungeon.piece.room.DungeonSideRoom;
import xiroc.dungeoncrawl.dungeon.treasure.Treasure;
import xiroc.dungeoncrawl.util.Orientation;
import xiroc.dungeoncrawl.util.Position2D;

import java.util.List;
import java.util.Random;

public class DefaultGenerator extends DungeonGenerator {

    private int layers;
    private int[] nodesLeft, roomsLeft;
    private int[] nodes, rooms;
    private boolean[] secretRoom;

    /*
     * Contains all nodes that do not have a direct connection to the start
     * position. The only use case for this right now is that in the last layer, one
     * of these nodes will be chosen to become the loot room.
     */
    public List<Position2D> distantNodes;

    public DefaultGenerator(DungeonGeneratorSettings settings) {
        super(settings);
    }

    @Override
    public void initialize(ChunkGenerator<?> chunkGenerator, DungeonBuilder dungeonBuilder, ChunkPos chunkPos, Random rand) {
        this.nodesLeft = new int[layers];
        this.roomsLeft = new int[layers];
        this.nodes = new int[layers];
        this.rooms = new int[layers];
        this.secretRoom = new boolean[layers];

        for (int layer = 0; layer < layers; layer++) {
            this.nodesLeft[layer] = settings.maxNodes.apply(rand, layer);
            this.roomsLeft[layer] = settings.maxRooms.apply(rand, layer);
        }

        this.secretRoom[rand.nextInt(2)] = true;

        this.distantNodes = Lists.newArrayList();
    }

    @Override
    public int calculateLayerCount(ChunkGenerator<?> chunkGenerator, Random rand, int height) {
        this.layers = Math.min(maxLayers, height / 9);
        return layers;
    }

    @Override
    public void generateLayer(DungeonBuilder dungeonBuilder, DungeonLayer dungeonLayer, int layer, Random rand, Position2D startPosition) {
        DungeonStairs s = new DungeonStairs(null, DungeonPiece.DEFAULT_NBT).bottom();
        s.setPosition(startPosition.x, startPosition.z);
        dungeonLayer.segments[s.posX][s.posZ] = new PlaceHolder(s).withFlag(PlaceHolder.Flag.FIXED_ROTATION);

        dungeonLayer.stairsPlaced = false;
        dungeonLayer.start = startPosition;

        Direction[] directions = Orientation.FLAT_FACINGS;

        int maxDirections = 3 + rand.nextInt(2);
        int counter = 0;
        int start = rand.nextInt(4);

        for (int i = 0; i < 4; i++) {
            if (counter < maxDirections) {
                Direction direction = directions[(i + start) % 4];
                if (findPositionAndContinue(dungeonBuilder, dungeonLayer, startPosition, direction, rand, 2, 3, layer, 1)) {
                    counter++;
                }
            }
        }

        DungeonCrawl.LOGGER.debug("Finished basic generation of layer {}: Generated {}/{} nodes and {}/{} rooms.", layer, nodes,
                nodes[layer] + nodesLeft[layer], rooms, rooms[layer] + roomsLeft[layer]);

        DungeonCrawl.LOGGER.debug("There are {} distant nodes", distantNodes.size());

        if (layer == 0) {
            createStarterRoom(dungeonBuilder, dungeonLayer, rand, layer);
        }

        if (secretRoom[layer]) {
            List<Tuple<DungeonCorridor, Position2D>> corridors = Lists.newArrayList();
            for (int x = 0; x < dungeonLayer.width; x++) {
                for (int z = 0; z < dungeonLayer.length; z++) {
                    if (dungeonLayer.segments[x][z] != null && dungeonLayer.segments[x][z].reference.getType() == 0) {
                        corridors.add(new Tuple<>((DungeonCorridor) dungeonLayer.segments[x][z].reference, new Position2D(x, z)));
                    }
                }
            }
            if (!corridors.isEmpty()) {
                for (int i = 0; i < 5; i++) {
                    Tuple<DungeonCorridor, Position2D> corridor = corridors.get(rand.nextInt(corridors.size()));
                    if (dungeonLayer.placeSecretRoom(corridor.getA(), corridor.getB(), rand)) {
                        break;
                    }
                }
            }
        }



        if (layer == layers && !distantNodes.isEmpty()) {
            Position2D pos = distantNodes.get(rand.nextInt(distantNodes.size()));
            if (dungeonLayer.segments[pos.x][pos.z] != null && dungeonLayer.segments[pos.x][pos.z].reference instanceof DungeonNodeRoom) {
                DungeonNodeRoom room = (DungeonNodeRoom) dungeonLayer.segments[pos.x][pos.z].reference;
                room.lootRoom = true;
                room.large = true;
            }
        }

        DungeonCrawl.LOGGER.debug("Finished generation of layer {}", layer);
    }

    /**
     * Recursive layer generation
     */
    public void layerGenerationStep(DungeonBuilder builder, DungeonLayer dungeonLayer, Position2D currentPosition, Position2D lastPosition,
                                    Random rand, int layer, int depth) {
        if (depth > maxDepth || nodesLeft[layer] == 0 && roomsLeft[layer] == 0) {
            return;
        }

        if (depth >= minStairsDepth && !dungeonLayer.stairsPlaced && layer != 4) {
            DungeonCrawl.LOGGER.debug("Placing exit stairs in layer {}", layer);
            Direction toLast = currentPosition.directionTo(lastPosition);

            dungeonLayer.end = currentPosition;

            DungeonStairs stairs = new DungeonStairs(null, DungeonPiece.DEFAULT_NBT).top();
            stairs.openSide(toLast);
            stairs.setPosition(dungeonLayer.end.x, dungeonLayer.end.z);
            dungeonLayer.segments[stairs.posX][stairs.posZ] = new PlaceHolder(stairs).withFlag(PlaceHolder.Flag.FIXED_ROTATION);

            dungeonLayer.stairsPlaced = true;

            dungeonLayer.buildConnection(lastPosition, currentPosition);

            Direction[] directions = Orientation.getFlatFacingsWithout(toLast);

            int maxDirections = depth < 3 ? 1 + rand.nextInt(3) : rand.nextInt(3);
            int counter = 0;
            int start = rand.nextInt(3);

            for (int i = 0; i < 3; i++) {
                if (counter < maxDirections) {
                    Direction direction = directions[(i + start) % 3];
                    if (findPositionAndContinue(builder, dungeonLayer, currentPosition, direction, rand, 2, 3, layer, ++depth)) {
                        counter++;
                    }
                }
            }

            return;
        }

        if (depth <= maxNodeDepth && depth >= minNodeDepth && nodesLeft[layer] > 0) {
            Position2D center = currentPosition.shift(lastPosition.directionTo(currentPosition), 1);

            if (DungeonFeatures.canPlacePiece(dungeonLayer, center.x - 1, center.z - 1, 3, 3, false)) {
                createNodeRoom(center, dungeonLayer);
                this.nodes[layer]++;
                this.nodesLeft[layer]--;

                if (depth > 1) {
                    dungeonLayer.distantNodes.add(center);
                }

                dungeonLayer.buildConnection(lastPosition, currentPosition);

                Direction[] directions = Orientation.getFlatFacingsWithout(currentPosition.directionTo(lastPosition));

                int maxDirections = depth < 3 ? 1 + rand.nextInt(3) : rand.nextInt(3);
                int counter = 0;
                int start = rand.nextInt(3);

                for (int i = 0; i < 3; i++) {
                    if (counter < maxDirections) {
                        Direction direction = directions[(i + start) % 3];
                        if (findPositionAndContinue(builder, dungeonLayer,
                                currentPosition.shift(currentPosition.directionTo(center), 1).shift(direction, 1),
                                direction, rand, 2, 3, layer, ++depth)) {
                            counter++;
                        }
                    }
                }
                return;
            }
        }

        if (depth <= maxRoomDepth && depth >= minRoomDepth && roomsLeft[layer] > 0) {
            DungeonRoom room = new DungeonRoom(null, DungeonPiece.DEFAULT_NBT);
            room.setPosition(currentPosition);
            dungeonLayer.segments[currentPosition.x][currentPosition.z] = new PlaceHolder(room);
            this.rooms[layer]++;
            this.roomsLeft[layer]--;

            dungeonLayer.buildConnection(lastPosition, currentPosition);

            Direction[] directions = Orientation.getFlatFacingsWithout(currentPosition.directionTo(lastPosition));

            int maxDirections = depth < 3 ? 1 + rand.nextInt(3) : rand.nextInt(3);
            int counter = 0;
            int start = rand.nextInt(3);

            for (int i = 0; i < 3; i++) {
                if (counter < maxDirections) {
                    Direction direction = directions[(i + start) % 3];
                    if (findPositionAndContinue(builder, dungeonLayer, currentPosition, direction, rand, 2, 3, layer, ++depth)) {
                        counter++;
                    }
                }
            }
        }
    }

    public boolean findPositionAndContinue(DungeonBuilder builder, DungeonLayer dungeonLayer, Position2D origin, Direction direction, Random rand,
                                           int min, int max, int layer, int depth) {
        switch (direction) {
            case NORTH:
                if (origin.z > min) {
                    Position2D pos = origin.shift(direction, Math.min(max, origin.z));
                    if (dungeonLayer.segments[pos.x][pos.z] == null && dungeonLayer.map.isPositionFree(pos.x, pos.z)) {
                        layerGenerationStep(builder, dungeonLayer, pos, origin, rand, layer, depth);
                        return true;
                    }
                }
                return false;
            case EAST:
                int east = Dungeon.SIZE - origin.x - 1;
                if (east > min) {
                    Position2D pos = origin.shift(direction, Math.min(max, east));
                    if (dungeonLayer.segments[pos.x][pos.z] == null && dungeonLayer.map.isPositionFree(pos.x, pos.z)) {
                        layerGenerationStep(builder, dungeonLayer, pos, origin, rand, layer, depth);
                        return true;
                    }
                }
                return false;
            case SOUTH:
                int south = Dungeon.SIZE - origin.z - 1;
                if (south > min) {
                    Position2D pos = origin.shift(direction, Math.min(max, south));
                    if (dungeonLayer.segments[pos.x][pos.z] == null && dungeonLayer.map.isPositionFree(pos.x, pos.z)) {
                        layerGenerationStep(builder, dungeonLayer, pos, origin, rand, layer, depth);
                        return true;
                    }
                }
                return false;
            case WEST:
                if (origin.x > min) {
                    Position2D pos = origin.shift(direction, Math.min(max, origin.x));
                    if (dungeonLayer.segments[pos.x][pos.z] == null && dungeonLayer.map.isPositionFree(pos.x, pos.z)) {
                        layerGenerationStep(builder, dungeonLayer, pos, origin, rand, layer, depth);
                        return true;
                    }
                }
                return false;
            default:
                return false;
        }
    }

    public void createNodeRoom(Position2D center, DungeonLayer dungeonLayer) {
        DungeonNodeRoom nodeRoom = new DungeonNodeRoom();
        nodeRoom.setPosition(center.x, center.z);

        PlaceHolder placeHolder = new PlaceHolder(nodeRoom).withFlag(PlaceHolder.Flag.PLACEHOLDER);
        for (int x = -1; x < 2; x++)
            for (int z = -1; z < 2; z++)
                if (x != 0 || z != 0)
                    dungeonLayer.segments[center.x + x][center.z + z] = placeHolder;

        dungeonLayer.segments[center.x][center.z] = new PlaceHolder(nodeRoom);
    }

    public void createStarterRoom(DungeonBuilder builder, DungeonLayer dungeonLayer, Random rand, int layer) {
        Tuple<Position2D, Rotation> sideRoomData = dungeonLayer.findStarterRoomData(dungeonLayer.start, rand);
        if (sideRoomData != null) {
            DungeonSideRoom room = new DungeonSideRoom(null, DungeonPiece.DEFAULT_NBT);
            room.modelID = 76;

            Direction dir = sideRoomData.getB().rotate(Direction.WEST);
            room.openSide(dir);
            room.setPosition(sideRoomData.getA().x, sideRoomData.getA().z);
            room.setRotation(sideRoomData.getB());
            room.treasureType = Treasure.Type.SUPPLY;
            room.stage = layer;

            dungeonLayer.map.markPositionAsOccupied(sideRoomData.getA());
            dungeonLayer.segments[sideRoomData.getA().x][sideRoomData.getA().z] = new PlaceHolder(room);

            Position2D connectedSegment = sideRoomData.getA().shift(dir, 1);
            if (dungeonLayer.segments[connectedSegment.x][connectedSegment.z] != null) {
                dungeonLayer.segments[connectedSegment.x][connectedSegment.z].reference.openSide(dir.getOpposite());
                dungeonLayer.rotatePiece(dungeonLayer.segments[connectedSegment.x][connectedSegment.z]);
            }
        }
    }

    public boolean supportsMutation() {
        return false;
    }

    @Override
    public void mutate(Random rand) {
    }
}
