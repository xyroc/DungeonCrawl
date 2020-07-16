package xiroc.dungeoncrawl.dungeon;

/*
 * DungeonCrawl (C) 2019 - 2020 XYROC (XIROC1337), All Rights Reserved
 */

import com.google.common.collect.Lists;
import net.minecraft.util.Direction;
import net.minecraft.util.Rotation;
import net.minecraft.util.Tuple;
import xiroc.dungeoncrawl.DungeonCrawl;
import xiroc.dungeoncrawl.dungeon.DungeonStatTracker.LayerStatTracker;
import xiroc.dungeoncrawl.dungeon.model.DungeonModels;
import xiroc.dungeoncrawl.dungeon.piece.DungeonCorridor;
import xiroc.dungeoncrawl.dungeon.piece.DungeonPiece;
import xiroc.dungeoncrawl.dungeon.piece.DungeonStairs;
import xiroc.dungeoncrawl.dungeon.piece.PlaceHolder;
import xiroc.dungeoncrawl.dungeon.piece.room.DungeonNodeRoom;
import xiroc.dungeoncrawl.dungeon.piece.room.DungeonRoom;
import xiroc.dungeoncrawl.dungeon.piece.room.DungeonSecretRoom;
import xiroc.dungeoncrawl.dungeon.piece.room.DungeonSideRoom;
import xiroc.dungeoncrawl.dungeon.treasure.Treasure;
import xiroc.dungeoncrawl.util.Orientation;
import xiroc.dungeoncrawl.util.Position2D;

import java.util.List;
import java.util.Random;

public class DungeonLayer {

    public PlaceHolder[][] segments;

    public Position2D start;
    public Position2D end;

    public int width; // x
    public int length; // z

    /*
     * Tracking values for recursive layer generation
     */
    public int nodes, rooms, nodesLeft, roomsLeft;
    public boolean stairsPlaced;

    public LayerStatTracker statTracker;

    public DungeonLayerMap map;

    /*
     * Contains all nodes that do not have a direct connection to the start
     * position. The only use case for this right now is that in the last layer, one
     * of these nodes will be chosen to become the loot room.
     */
    public List<Position2D> distantNodes;

    public DungeonLayer() {
        this(16, 16);
    }

    public DungeonLayer(int width, int length) {
        this.width = width;
        this.length = length;
        this.statTracker = new LayerStatTracker();
        this.segments = new PlaceHolder[this.width][this.length];
        this.distantNodes = Lists.newArrayList();
    }

    public void buildMap(DungeonBuilder builder, List<DungeonPiece> pieces, Random rand, Position2D start, boolean secretRoom, int layer,
                         boolean lastLayer) {
        this.start = start;
        this.stairsPlaced = false;
        this.createLayout(builder, rand, layer, secretRoom, lastLayer);
    }

    public void extend(DungeonBuilder builder, DungeonLayerMap map, Random rand, int layer) {
        if (layer == 0) {
            Tuple<Position2D, Rotation> sideRoomData = findStarterRoomData(start, rand);
            if (sideRoomData != null) {
                DungeonSideRoom room = new DungeonSideRoom(null, DungeonPiece.DEFAULT_NBT);
                room.modelID = 76;

                Direction dir = sideRoomData.getB().rotate(Direction.WEST);
                room.openSide(dir);
                room.setPosition(sideRoomData.getA().x, sideRoomData.getA().z);
                room.setRotation(sideRoomData.getB());
                room.treasureType = Treasure.Type.SUPPLY;

                map.markPositionAsOccupied(sideRoomData.getA());
                this.segments[sideRoomData.getA().x][sideRoomData.getA().z] = new PlaceHolder(room);

                Position2D connectedSegment = sideRoomData.getA().shift(dir, 1);
                if (this.segments[connectedSegment.x][connectedSegment.z] != null) {
                    this.segments[connectedSegment.x][connectedSegment.z].reference.openSide(dir.getOpposite());
                    rotatePiece(this.segments[connectedSegment.x][connectedSegment.z]);
                }
            }
        }
    }

    public void buildConnection(Position2D start, Position2D end) {
        int startX = start.x;
        int startZ = start.z;
        int endX = end.x;
        int endZ = end.z;

        if (startX == endX && startZ == endZ)
            return;

        if (startX > endX) {
            this.segments[startX][startZ].reference.openSide(Direction.WEST);
            for (int x = startX; x > (startZ == endZ ? endX + 1 : endX); x--) {
                final Direction side = (x - 1) == endX ? startZ < endZ ? Direction.SOUTH : Direction.NORTH : Direction.WEST;
                if (this.segments[x - 1][startZ] != null) {
                    this.segments[x - 1][startZ].reference.openSide(
                            side);
                    this.segments[x - 1][startZ].reference.openSide(Direction.EAST);
                    this.rotatePiece(this.segments[x - 1][startZ]);
                    continue;
                }
                DungeonPiece corridor = new DungeonCorridor(null, DungeonPiece.DEFAULT_NBT);
                corridor.setPosition(x - 1, startZ);
                corridor.setRotation(
                        (x - 1) == endX
                                ? Orientation.getRotationFromCW90DoubleFacing(
                                startZ > endZ ? Direction.NORTH : Direction.SOUTH, Direction.EAST)
                                : Orientation.getRotationFromFacing(Direction.WEST));
                corridor.openSide(side);
                corridor.openSide(Direction.EAST);
                this.segments[x - 1][startZ] = new PlaceHolder(corridor);
            }
            if (startZ > endZ) {
                this.segments[endX][endZ].reference.openSide(Direction.SOUTH);
                for (int z = startZ; z > endZ + 1; z--) {
                    if (this.segments[endX][z - 1] != null) {
                        this.segments[endX][z - 1].reference.openSide(Direction.SOUTH);
                        this.segments[endX][z - 1].reference
                                .openSide((z - 1) == endZ ? Direction.WEST : Direction.NORTH);
                        this.rotatePiece(this.segments[endX][z - 1]);
                        continue;
                    }
                    DungeonPiece corridor = new DungeonCorridor(null, DungeonPiece.DEFAULT_NBT);
                    corridor.setPosition(endX, z - 1);
                    corridor.setRotation((z - 1) == endZ
                            ? Orientation.getRotationFromCW90DoubleFacing(Direction.NORTH, Direction.WEST)
                            : Orientation.getRotationFromFacing(Direction.NORTH));
                    corridor.openSide(Direction.SOUTH);
                    corridor.openSide((z - 1) == endZ ? Direction.WEST : Direction.NORTH);
                    this.segments[endX][z - 1] = new PlaceHolder(corridor);
                }
            } else if (startZ < endZ) {
                this.segments[endX][endZ].reference.openSide(Direction.NORTH);
                for (int z = startZ; z < endZ - 1; z++) {
                    if (this.segments[endX][z + 1] != null) {
                        this.segments[endX][z + 1].reference
                                .openSide((z + 1) == endZ ? Direction.WEST : Direction.SOUTH);
                        this.segments[endX][z + 1].reference.openSide(Direction.NORTH);
                        this.rotatePiece(this.segments[endX][z + 1]);
                        continue;
                    }
                    DungeonPiece corridor = new DungeonCorridor(null, DungeonPiece.DEFAULT_NBT);
                    corridor.setPosition(endX, z + 1);
                    corridor.setRotation((z + 1) == endZ
                            ? Orientation.getRotationFromCW90DoubleFacing(Direction.NORTH, Direction.WEST)
                            : Orientation.getRotationFromFacing(Direction.SOUTH));
                    corridor.openSide((z + 1) == endZ ? Direction.WEST : Direction.SOUTH);
                    corridor.openSide(Direction.NORTH);
                    this.segments[endX][z + 1] = new PlaceHolder(corridor);
                }
            } else
                this.segments[endX][endZ].reference.openSide(Direction.EAST);
        } else if (startX < endX) {
            this.segments[startX][startZ].reference.openSide(Direction.EAST);
            for (int x = startX; x < (startZ == endZ ? endX - 1 : endX); x++) {
                final Direction side = (x + 1) == endX ? startZ < endZ ? Direction.SOUTH : Direction.NORTH : Direction.EAST;
                if (this.segments[x + 1][startZ] != null) {
                    this.segments[x + 1][startZ].reference.openSide(
                            side);
                    this.segments[x + 1][startZ].reference.openSide(Direction.WEST);
                    this.rotatePiece(this.segments[x + 1][startZ]);
                    continue;
                }
                DungeonPiece corridor = new DungeonCorridor(null, DungeonPiece.DEFAULT_NBT);
                corridor.setPosition(x + 1, startZ);
                corridor.setRotation(
                        (x + 1) == endX
                                ? Orientation.getRotationFromCW90DoubleFacing(
                                startZ > endZ ? Direction.NORTH : Direction.SOUTH, Direction.WEST)
                                : Orientation.getRotationFromFacing(Direction.EAST));
                corridor.openSide(side);
                corridor.openSide(Direction.WEST);
                this.segments[x + 1][startZ] = new PlaceHolder(corridor);
            }
            if (startZ > endZ) {
                this.segments[endX][endZ].reference.openSide(Direction.SOUTH);
                for (int z = startZ; z > endZ + 1; z--) {
                    if (this.segments[endX][z - 1] != null) {
                        this.segments[endX][z - 1].reference.openSide(Direction.SOUTH);
                        this.segments[endX][z - 1].reference
                                .openSide((z - 1) == endZ ? Direction.EAST : Direction.NORTH);
                        this.rotatePiece(this.segments[endX][z - 1]);
                        continue;
                    }
                    DungeonPiece corridor = new DungeonCorridor(null, DungeonPiece.DEFAULT_NBT);
                    corridor.setPosition(endX, z - 1);
                    corridor.setRotation((z - 1) == endZ
                            ? Orientation.getRotationFromCW90DoubleFacing(Direction.NORTH, Direction.WEST)
                            : Orientation.getRotationFromFacing(Direction.NORTH));
                    corridor.openSide(Direction.SOUTH);
                    corridor.openSide((z - 1) == endZ ? Direction.WEST : Direction.NORTH);
                    this.segments[endX][z - 1] = new PlaceHolder(corridor);
                }
            } else if (startZ < endZ) {
                this.segments[endX][endZ].reference.openSide(Direction.NORTH);
                for (int z = startZ; z < endZ - 1; z++) {
                    if (this.segments[endX][z + 1] != null) {
                        this.segments[endX][z + 1].reference
                                .openSide((z + 1) == endZ ? Direction.EAST : Direction.SOUTH);
                        this.segments[endX][z + 1].reference.openSide(Direction.NORTH);
                        this.rotatePiece(this.segments[endX][z + 1]);
                        continue;
                    }
                    DungeonPiece corridor = new DungeonCorridor(null, DungeonPiece.DEFAULT_NBT);
                    corridor.setPosition(endX, z + 1);
                    corridor.setRotation((z + 1) == endZ
                            ? Orientation.getRotationFromCW90DoubleFacing(Direction.NORTH, Direction.WEST)
                            : Orientation.getRotationFromFacing(Direction.SOUTH));
                    corridor.openSide((z + 1) == endZ ? Direction.WEST : Direction.SOUTH);
                    corridor.openSide(Direction.NORTH);
                    this.segments[endX][z + 1] = new PlaceHolder(corridor);
                }
            } else
                this.segments[endX][endZ].reference.openSide(Direction.WEST);
        } else {
            if (startZ > endZ) {
                this.segments[startX][startZ].reference.openSide(Direction.NORTH);
                this.segments[endX][endZ].reference.openSide(Direction.SOUTH);
                for (int z = startZ; z > endZ + 1; z--) {
                    if (this.segments[endX][z - 1] != null) {
                        this.segments[endX][z - 1].reference.openSide(Direction.NORTH);
                        this.segments[endX][z - 1].reference.openSide(Direction.SOUTH);
                        this.rotatePiece(this.segments[endX][z - 1]);
                        continue;
                    }
                    DungeonPiece corridor = new DungeonCorridor(null, DungeonPiece.DEFAULT_NBT);
                    corridor.setPosition(endX, z - 1);
                    corridor.setRotation(Orientation.getRotationFromFacing(Direction.NORTH));
                    corridor.openSide(Direction.SOUTH);
                    corridor.openSide(Direction.NORTH);
                    this.segments[endX][z - 1] = new PlaceHolder(corridor);
                }
            } else if (startZ < endZ) {
                this.segments[startX][startZ].reference.openSide(Direction.SOUTH);
                this.segments[endX][endZ].reference.openSide(Direction.NORTH);
                for (int z = startZ; z < endZ - 1; z++) {
                    if (this.segments[endX][z + 1] != null) {
                        this.segments[endX][z + 1].reference.openSide(Direction.SOUTH);
                        this.segments[endX][z + 1].reference.openSide(Direction.NORTH);
                        this.rotatePiece(this.segments[endX][z + 1]);
                        continue;
                    }
                    this.segments[endX][endZ].reference.openSide(Direction.NORTH);
                    DungeonPiece corridor = new DungeonCorridor(null, DungeonPiece.DEFAULT_NBT);
                    corridor.setPosition(endX, z + 1);
                    corridor.setRotation(Orientation.getRotationFromFacing(Direction.SOUTH));
                    corridor.openSide(Direction.SOUTH);
                    corridor.openSide(Direction.NORTH);
                    this.segments[endX][z + 1] = new PlaceHolder(corridor);
                }
            }
        }
    }

    public void createLayout(DungeonBuilder builder, Random rand, int layer, boolean secretRoom, boolean lastLayer) {
//		Direction facing = Orientation.RANDOM_FACING_FLAT.roll(rand);

        DungeonStairs s = new DungeonStairs(null, DungeonPiece.DEFAULT_NBT).bottom();
        s.setPosition(start.x, start.z);
        this.segments[s.posX][s.posZ] = new PlaceHolder(s).withFlag(PlaceHolder.Flag.FIXED_ROTATION);

        this.nodesLeft = 3 + layer;
        this.roomsLeft = 4 + (int) (1.5 * layer);
        this.nodes = this.rooms = 0;

        Direction[] directions = Orientation.FLAT_FACINGS;

        int maxDirections = 3 + rand.nextInt(2);
        int counter = 0;
        int start = rand.nextInt(4);

        for (int i = 0; i < 4; i++) {
            if (counter < maxDirections) {
                Direction direction = directions[(i + start) % 4];
                if (findPositionAndContinue(builder, this.start, direction, rand, 2, 3, layer, 1)) {
                    counter++;
                }
            }
        }

        DungeonCrawl.LOGGER.debug("Finished Layer {}: Generated {}/{} nodes and {}/{} rooms.", layer, nodes,
                nodes + nodesLeft, rooms, rooms + roomsLeft);

        DungeonCrawl.LOGGER.debug("There are {} distant nodes", distantNodes.size());

        if (secretRoom) {
            List<Tuple<DungeonCorridor, Position2D>> corridors = Lists.newArrayList();
            for (int x = 0; x < width; x++) {
                for (int z = 0; z < length; z++) {
                    if (segments[x][z] != null && segments[x][z].reference.getType() == 0) {
                        corridors.add(new Tuple<>((DungeonCorridor) segments[x][z].reference, new Position2D(x, z)));
                    }
                }
            }
            if (!corridors.isEmpty()) {
                for (int i = 0; i < 5; i++) {
                    Tuple<DungeonCorridor, Position2D> corridor = corridors.get(rand.nextInt(corridors.size()));
                    if (placeSecretRoom(corridor.getA(), corridor.getB(), rand)) {
                        break;
                    }
                }
            }
        }

        if (lastLayer && !distantNodes.isEmpty()) {
            Position2D pos = distantNodes.get(rand.nextInt(distantNodes.size()));
            if (segments[pos.x][pos.z] != null && segments[pos.x][pos.z].reference instanceof DungeonNodeRoom) {
                DungeonNodeRoom room = (DungeonNodeRoom) segments[pos.x][pos.z].reference;
                room.lootRoom = true;
                room.large = true;
            }
        }

//		List<Position2D> nodeList = Lists.newArrayList(), allNodes = Lists.newArrayList();
//
//		if (!lastLayer) {
//			createEndStairs(builder, start, Orientation.RANDOM_FACING_FLAT.roll(rand), nodeList, rand, layer, 0);
//
//			nodeList.add(end);
//			allNodes.add(end);
//
//			buildConnection(start, end);
//
//		}
//
//		nodeList.add(start);
//		allNodes.add(start);
//
//		int nodes = 3 + layer, rooms = 4 + 2 * layer;
//
//		while (nodes > 0) {
//			if (nodeList.isEmpty()) {
//				DungeonCrawl.LOGGER.debug("Nodelist is empty; breaking out of node generation. {} nodes left.", nodes);
//				break;
//			}
//
//			DungeonCrawl.LOGGER.debug("-- Node Iteration of Layer {} --", layer);
//			nodeList.forEach((pos) -> {
//				DungeonCrawl.LOGGER.debug("Node: [{},{}]", pos.x, pos.z);
//			});
//			DungeonCrawl.LOGGER.debug("---");
//
//			Position2D nodePos = nodeList.get(rand.nextInt(nodeList.size()));
//
//			PlaceHolder node = this.segments[nodePos.x][nodePos.z];
//
//			Direction direction = findNext(node.reference, Orientation.RANDOM_FACING_FLAT.roll(rand));
//
//			if (direction == null) {
//				nodeList.remove(nodePos);
//				continue;
//			}
//
//			if (createNodeRoom(builder, nodePos, direction, nodeList, allNodes, rand, layer, 0))
//				nodes--;
//			else
//				nodeList.remove(nodePos);
//		}
//
//		if (lastLayer) {
//
//			DungeonCrawl.LOGGER.debug("There are {} distant nodes.", distantNodes.size());
//			if (distantNodes.isEmpty()) {
//				rooms += 8;
//			} else {
//				Position2D nodePos = distantNodes.get(rand.nextInt(distantNodes.size()));
//
//				DungeonNodeRoom node = (DungeonNodeRoom) segments[nodePos.x][nodePos.z].reference;
//
//				node.large = true;
//				node.lootRoom = true;
//
//			}
//
//		}
//
//		while (rooms > 0) {
//			if (allNodes.isEmpty()) {
//				DungeonCrawl.LOGGER.debug("allNodes is empty; breaking out of room generation. {} rooms left.", rooms);
//				break;
//			}
//			Position2D nodePos = allNodes.get(rand.nextInt(allNodes.size()));
//
//			PlaceHolder node = this.segments[nodePos.x][nodePos.z];
//
//			Direction direction = findNext(node.reference, Orientation.RANDOM_FACING_FLAT.roll(rand));
//
//			if (direction == null) {
//				allNodes.remove(nodePos);
//				continue;
//			}
//
//			if (createRoom(builder, nodePos, direction, allNodes, layer, 0))
//				rooms--;
//			else
//				allNodes.remove(nodePos);
//		}

    }

    public Tuple<Position2D, Rotation> findStarterRoomData(Position2D start, Random rand) {
//			for (int x = start.x - 2; x < start.x + 2; x++) {
//				for (int z = start.z - 2; z < start.z + 2; z++) {
//					if (Position2D.isValid(x, z, width, length)) {
//						if (segments[x][z] != null && segments[x][z].reference.getType() == 0
//								&& segments[x][z].reference.connectedSides < 4) {
//							Tuple<Position2D, Rotation> data = findSideRoomData(new Position2D(x, z));
//							if (data == null)
//								continue;
//							return data;
//						}
//					}
//				}
//			}
//			return null;

        int index = rand.nextInt(4);

        for (int i = 0; i < 4; i++) {
            index = (index + i) % 4;
            int length = 1 + rand.nextInt(2);
            Position2D current = start.shift(Orientation.FLAT_FACINGS[index], 1);
            for (int j = 1; j < length; j++) {
                if (current.isValid(Dungeon.SIZE) && segments[current.x][current.z] != null
                        && segments[current.x][current.z].reference.getType() == 0
                        && segments[current.x][current.z].reference.connectedSides < 4) {
                    Tuple<Position2D, Rotation> data = findSideRoomData(new Position2D(current.x, current.z));
                    if (data != null) {
                        return data;
                    }
                }
                current = start.shift(Orientation.FLAT_FACINGS[index], 1);
            }

        }

        return null;

    }

    public Tuple<Position2D, Rotation> findSideRoomData(Position2D base) {
        Position2D north = base.shift(Direction.NORTH, 1), east = base.shift(Direction.EAST, 1),
                south = base.shift(Direction.SOUTH, 1), west = base.shift(Direction.WEST, 1);

        if (north.isValid(width, length) && segments[north.x][north.z] == null)
            return new Tuple<Position2D, Rotation>(north, Rotation.COUNTERCLOCKWISE_90);

        if (east.isValid(width, length) && segments[east.x][east.z] == null)
            return new Tuple<Position2D, Rotation>(east, Rotation.NONE);

        if (south.isValid(width, length) && segments[south.x][south.z] == null)
            return new Tuple<Position2D, Rotation>(south, Rotation.CLOCKWISE_90);

        if (west.isValid(width, length) && segments[west.x][west.z] == null)
            return new Tuple<Position2D, Rotation>(west, Rotation.CLOCKWISE_180);

        return null;
    }

    public void rotatePiece(PlaceHolder placeHolder) {
        if (placeHolder.hasFlag(PlaceHolder.Flag.FIXED_ROTATION))
            return;
        DungeonPiece piece = placeHolder.reference;

        switch (piece.connectedSides) {
            case 1:
                piece.setRotation(Orientation.getRotationFromFacing(DungeonPiece.getOneWayDirection(piece)));
                return;
            case 2:
                if (piece.sides[0] && piece.sides[2])
                    piece.setRotation(Orientation.getRotationFromFacing(Direction.NORTH));
                else if (piece.sides[1] && piece.sides[3])
                    piece.setRotation(Orientation.getRotationFromFacing(Direction.EAST));
                else
                    piece.setRotation(Orientation.getRotationFromCW90DoubleFacing(DungeonPiece.getOpenSide(piece, 0),
                            DungeonPiece.getOpenSide(piece, 1)));
                return;
            case 3:
                piece.setRotation(Orientation.getRotationFromTripleFacing(DungeonPiece.getOpenSide(piece, 0),
                        DungeonPiece.getOpenSide(piece, 1), DungeonPiece.getOpenSide(piece, 2)));
        }
    }

    public void rotateNode(PlaceHolder placeHolder) {
        if (placeHolder.hasFlag(PlaceHolder.Flag.FIXED_ROTATION))
            return;
        DungeonNodeRoom node = (DungeonNodeRoom) placeHolder.reference;
        Rotation rotation = node.node.compare(new Node(node.sides[0], node.sides[1], node.sides[2], node.sides[3]));
        if (rotation != null) {
            node.rotation = rotation;
        } else {
            DungeonCrawl.LOGGER.error("Could not find a proper rotation for [{} {} {} {}].", node.sides[0],
                    node.sides[1], node.sides[2], node.sides[3]);
        }

    }

    public Direction findNext(DungeonPiece piece, Direction base) {
        if (piece.connectedSides >= 4)
            return null;
        if (piece.sides[(base.getHorizontalIndex() + 2) % 4])
            return findNext(piece, base.rotateY());
        else
            return base;
    }

    public boolean createEndStairs(DungeonBuilder builder, Position2D start, Direction facing, List<Position2D> list,
                                   Random rand, int layer, int t) {
        if (t > 3) {
            return false;
        }

        switch (facing) {
            case EAST:
                if (Dungeon.SIZE - start.x - 1 > 3) {
                    int length = 2 + rand.nextInt(2);
                    end = new Position2D(start.x + length + 1, start.z);

                    DungeonStairs stairs = new DungeonStairs(null, DungeonPiece.DEFAULT_NBT).top();
                    stairs.openSide(Direction.WEST);
                    stairs.setPosition(end.x, end.z);
                    this.segments[stairs.posX][stairs.posZ] = new PlaceHolder(stairs)
                            .withFlag(PlaceHolder.Flag.FIXED_ROTATION);
                    return true;
                } else {
                    return createEndStairs(builder, start, facing.rotateY(), list, rand, layer, ++t);
                }
            case NORTH:
                if (start.z > 3) {
                    int length = 2 + rand.nextInt(2);
                    end = new Position2D(start.x, start.z - length - 1);

                    DungeonStairs stairs = new DungeonStairs(null, DungeonPiece.DEFAULT_NBT).top();
                    stairs.openSide(Direction.SOUTH);
                    stairs.setPosition(end.x, end.z);
                    this.segments[stairs.posX][stairs.posZ] = new PlaceHolder(stairs)
                            .withFlag(PlaceHolder.Flag.FIXED_ROTATION);
                    return true;
                } else {
                    return createEndStairs(builder, start, facing.rotateY(), list, rand, layer, ++t);

                }
            case SOUTH:
                if (Dungeon.SIZE - start.z - 1 > 3) {
                    int length = 2 + rand.nextInt(2);
                    end = new Position2D(start.x, start.z + length + 1);

                    DungeonStairs stairs = new DungeonStairs(null, DungeonPiece.DEFAULT_NBT).top();
                    stairs.openSide(Direction.NORTH);
                    stairs.setPosition(end.x, end.z);
                    this.segments[stairs.posX][stairs.posZ] = new PlaceHolder(stairs)
                            .withFlag(PlaceHolder.Flag.FIXED_ROTATION);
                    return true;
                } else {
                    return createEndStairs(builder, start, facing.rotateY(), list, rand, layer, ++t);

                }
            case WEST:
                if (start.x > 3) {
                    int length = 2 + rand.nextInt(2);
                    end = new Position2D(start.x - length - 1, start.z);

                    DungeonStairs stairs = new DungeonStairs(null, DungeonPiece.DEFAULT_NBT).top();
                    stairs.openSide(Direction.EAST);
                    stairs.setPosition(end.x, end.z);
                    this.segments[stairs.posX][stairs.posZ] = new PlaceHolder(stairs)
                            .withFlag(PlaceHolder.Flag.FIXED_ROTATION);
                    return true;
                } else {
                    return createEndStairs(builder, start, facing.rotateY(), list, rand, layer, ++t);
                }
            default:
                return false;
        }
    }

    /**
     * Recursive layer generation
     */
    public void layerGenerationStep(DungeonBuilder builder, Position2D currentPosition, Position2D lastPosition,
                                    Random rand, int layer, int depth) {
        if (depth > 5 || nodesLeft == 0 && roomsLeft == 0) {
            return;
        }

        if (depth == 1 && !stairsPlaced && layer != 4) {
            DungeonCrawl.LOGGER.debug("Placing exit stairs in layer {}", layer);
            Direction toLast = currentPosition.directionTo(lastPosition);

            this.end = currentPosition;

            DungeonStairs stairs = new DungeonStairs(null, DungeonPiece.DEFAULT_NBT).top();
            stairs.openSide(toLast);
            stairs.setPosition(end.x, end.z);
            this.segments[stairs.posX][stairs.posZ] = new PlaceHolder(stairs).withFlag(PlaceHolder.Flag.FIXED_ROTATION);

            stairsPlaced = true;

            buildConnection(lastPosition, currentPosition);

            Direction[] directions = Orientation.getFlatFacingsWithout(toLast);

            int maxDirections = depth < 3 ? 1 + rand.nextInt(3) : rand.nextInt(3);
            int counter = 0;
            int start = rand.nextInt(3);

            for (int i = 0; i < 3; i++) {
                if (counter < maxDirections) {
                    Direction direction = directions[(i + start) % 3];
                    if (findPositionAndContinue(builder, currentPosition, direction, rand, 2, 3, layer, ++depth)) {
                        counter++;
                    }
                }
            }

            return;
        }

        if (depth <= 4 && nodesLeft > 0) {
            Position2D center = currentPosition.shift(lastPosition.directionTo(currentPosition), 1);

            if (DungeonFeatures.canPlacePiece(this, center.x - 1, center.z - 1, 3, 3, false)) {
                createNodeRoom(center);
                this.nodes++;
                this.nodesLeft--;

                if (depth > 1) {
                    distantNodes.add(center);
                }

                buildConnection(lastPosition, currentPosition);

                Direction[] directions = Orientation.getFlatFacingsWithout(currentPosition.directionTo(lastPosition));

                int maxDirections = depth < 3 ? 1 + rand.nextInt(3) : rand.nextInt(3);
                int counter = 0;
                int start = rand.nextInt(3);

                for (int i = 0; i < 3; i++) {
                    if (counter < maxDirections) {
                        Direction direction = directions[(i + start) % 3];
                        if (findPositionAndContinue(builder,
                                currentPosition.shift(currentPosition.directionTo(center), 1).shift(direction, 1),
                                direction, rand, 2, 3, layer, ++depth)) {
                            counter++;
                        }
                    }
                }
                return;
            }
        }

        if (roomsLeft > 0) {
            DungeonRoom room = new DungeonRoom(null, DungeonPiece.DEFAULT_NBT);
            room.setPosition(currentPosition);
            this.segments[currentPosition.x][currentPosition.z] = new PlaceHolder(room);
            this.rooms++;
            this.roomsLeft--;

            buildConnection(lastPosition, currentPosition);

            Direction[] directions = Orientation.getFlatFacingsWithout(currentPosition.directionTo(lastPosition));

            int maxDirections = depth < 3 ? 1 + rand.nextInt(3) : rand.nextInt(3);
            int counter = 0;
            int start = rand.nextInt(3);

            for (int i = 0; i < 3; i++) {
                if (counter < maxDirections) {
                    Direction direction = directions[(i + start) % 3];
                    if (findPositionAndContinue(builder, currentPosition, direction, rand, 2, 3, layer, ++depth)) {
                        counter++;
                    }
                }
            }
        }
    }

    public boolean findPositionAndContinue(DungeonBuilder builder, Position2D origin, Direction direction, Random rand,
                                           int min, int max, int layer, int depth) {
        switch (direction) {
            case NORTH:
                if (origin.z > min) {
                    Position2D pos = origin.shift(direction, Math.min(max, origin.z));
                    if (segments[pos.x][pos.z] == null && map.isPositionFree(pos.x, pos.z)) {
                        layerGenerationStep(builder, pos, origin, rand, layer, depth);
                        return true;
                    }
                }
                return false;
            case EAST:
                int east = Dungeon.SIZE - origin.x - 1;
                if (east > min) {
                    Position2D pos = origin.shift(direction, Math.min(max, east));
                    if (segments[pos.x][pos.z] == null && map.isPositionFree(pos.x, pos.z)) {
                        layerGenerationStep(builder, pos, origin, rand, layer, depth);
                        return true;
                    }
                }
                return false;
            case SOUTH:
                int south = Dungeon.SIZE - origin.z - 1;
                if (south > min) {
                    Position2D pos = origin.shift(direction, Math.min(max, south));
                    if (segments[pos.x][pos.z] == null && map.isPositionFree(pos.x, pos.z)) {
                        layerGenerationStep(builder, pos, origin, rand, layer, depth);
                        return true;
                    }
                }
                return false;
            case WEST:
                if (origin.x > min) {
                    Position2D pos = origin.shift(direction, Math.min(max, origin.x));
                    if (segments[pos.x][pos.z] == null && map.isPositionFree(pos.x, pos.z)) {
                        layerGenerationStep(builder, pos, origin, rand, layer, depth);
                        return true;
                    }
                }
                return false;
            default:
                return false;
        }
    }

    public PlaceHolder createNodeRoom(Position2D center) {
        DungeonNodeRoom nodeRoom = new DungeonNodeRoom();
        nodeRoom.setPosition(center.x, center.z);

        PlaceHolder placeHolder = new PlaceHolder(nodeRoom).withFlag(PlaceHolder.Flag.PLACEHOLDER);
        for (int x = -1; x < 2; x++)
            for (int z = -1; z < 2; z++)
                if (x != 0 || z != 0)
                    segments[center.x + x][center.z + z] = placeHolder;

        segments[center.x][center.z] = new PlaceHolder(nodeRoom);
        return segments[center.x][center.z];
    }

    public boolean createNodeRoom(DungeonBuilder builder, Position2D pos, Direction direction, List<Position2D> list,
                                  List<Position2D> allNodes, Random rand, int layer, int t) {

        if (t > 3) {
            return false;
        }

        if (this.segments[pos.x][pos.z].reference.sides[(direction.getHorizontalIndex() + 2) % 4]) {
            return createNodeRoom(builder, pos, direction.rotateY(), list, allNodes, rand, layer, ++t);
        }

        switch (direction) {
            case EAST:
                int east = Dungeon.SIZE - pos.x - 1;
                if (east > 3) {
                    int length = Math.min(east, 5);
                    Position2D center = new Position2D(pos.x + length, pos.z);

                    if (!DungeonFeatures.canPlacePieceWithHeight(builder, layer, center.x - 1, center.z - 1, 3, 3, 0,
                            false))
                        return createNodeRoom(builder, pos, direction.rotateY(), list, allNodes, rand, layer, ++t);

                    DungeonNodeRoom nodeRoom = new DungeonNodeRoom();
                    nodeRoom.setPosition(center.x, center.z);

                    PlaceHolder placeHolder = new PlaceHolder(nodeRoom).withFlag(PlaceHolder.Flag.PLACEHOLDER);
                    for (int x = -1; x < 2; x++)
                        for (int z = -1; z < 2; z++)
                            if (x != 0 || z != 0)
                                segments[center.x + x][center.z + z] = placeHolder;

                    segments[center.x][center.z] = new PlaceHolder(nodeRoom);

                    Position2D p = pos.shift(Direction.EAST, 1);
                    buildConnection(segments[p.x][p.z] == null ? pos : p, center.shift(Direction.WEST, 1));

                    if (pos.equals(start)) {
                        list.add(center);
                    } else {
                        distantNodes.add(center);
//					if (rand.nextFloat() < 0.25) {
                        list.add(center);
//					}
                    }
                    allNodes.add(center);
                    return true;
                } else
                    return createNodeRoom(builder, pos, direction.rotateY(), list, allNodes, rand, layer, ++t);
            case NORTH:
                int north = pos.z;
                if (north > 3) {
                    int length = Math.min(north, 5);
                    Position2D center = new Position2D(pos.x, pos.z - length);

                    if (!DungeonFeatures.canPlacePieceWithHeight(builder, layer, center.x - 1, center.z - 1, 3, 3, 0,
                            false))
                        return createNodeRoom(builder, pos, direction.rotateY(), list, allNodes, rand, layer, ++t);

                    DungeonNodeRoom nodeRoom = new DungeonNodeRoom();
                    nodeRoom.setPosition(center.x, center.z);

                    PlaceHolder placeHolder = new PlaceHolder(nodeRoom).withFlag(PlaceHolder.Flag.PLACEHOLDER);
                    for (int x = -1; x < 2; x++)
                        for (int z = -1; z < 2; z++)
                            if (x != 0 || z != 0)
                                segments[center.x + x][center.z + z] = placeHolder;

                    segments[center.x][center.z] = new PlaceHolder(nodeRoom);

                    Position2D p = pos.shift(Direction.NORTH, 1);
                    buildConnection(segments[p.x][p.z] == null ? pos : p, center.shift(Direction.SOUTH, 1));
                    if (pos.equals(start)) {
                        list.add(center);
                    } else {
                        distantNodes.add(center);
//					if (rand.nextFloat() < 0.25) {
                        list.add(center);
//					}
                    }
                    allNodes.add(center);
                    return true;
                } else
                    return createNodeRoom(builder, pos, direction.rotateY(), list, allNodes, rand, layer, ++t);
            case SOUTH:
                int south = Dungeon.SIZE - pos.z - 1;
                if (south > 3) {
                    int length = Math.min(south, 5);
                    Position2D center = new Position2D(pos.x, pos.z + length);

                    if (!DungeonFeatures.canPlacePieceWithHeight(builder, layer, center.x - 1, center.z - 1, 3, 3, 0,
                            false))
                        return createNodeRoom(builder, pos, direction.rotateY(), list, allNodes, rand, layer, ++t);

                    DungeonNodeRoom nodeRoom = new DungeonNodeRoom();
                    nodeRoom.setPosition(center.x, center.z);

                    PlaceHolder placeHolder = new PlaceHolder(nodeRoom).withFlag(PlaceHolder.Flag.PLACEHOLDER);
                    for (int x = -1; x < 2; x++)
                        for (int z = -1; z < 2; z++)
                            if (x != 0 || z != 0)
                                segments[center.x + x][center.z + z] = placeHolder;

                    segments[center.x][center.z] = new PlaceHolder(nodeRoom);

                    Position2D p = pos.shift(Direction.SOUTH, 1);
                    buildConnection(segments[p.x][p.z] == null ? pos : p, center.shift(Direction.NORTH, 1));
                    if (pos.equals(start)) {
                        list.add(center);
                    } else {
                        distantNodes.add(center);
//					if (rand.nextFloat() < 0.25) {
                        list.add(center);
//					}
                    }
                    allNodes.add(center);
                    return true;
                } else
                    return createNodeRoom(builder, pos, direction.rotateY(), list, allNodes, rand, layer, ++t);
            case WEST:
                int west = pos.x;
                if (west > 3) {
                    int length = Math.min(west, 5);
                    Position2D center = new Position2D(pos.x - length, pos.z);

                    if (!DungeonFeatures.canPlacePieceWithHeight(builder, layer, center.x - 1, center.z - 1, 3, 3, 0,
                            false))
                        return createNodeRoom(builder, pos, direction.rotateY(), list, allNodes, rand, layer, ++t);

                    DungeonNodeRoom nodeRoom = new DungeonNodeRoom();
                    nodeRoom.setPosition(center.x, center.z);

                    PlaceHolder placeHolder = new PlaceHolder(nodeRoom).withFlag(PlaceHolder.Flag.PLACEHOLDER);
                    for (int x = -1; x < 2; x++)
                        for (int z = -1; z < 2; z++)
                            if (x != 0 || z != 0)
                                segments[center.x + x][center.z + z] = placeHolder;

                    segments[center.x][center.z] = new PlaceHolder(nodeRoom);

                    Position2D p = pos.shift(Direction.WEST, 1);
                    buildConnection(segments[p.x][p.z] == null ? pos : p, center.shift(Direction.EAST, 1));
                    if (pos.equals(start)) {
                        list.add(center);
                    } else {
                        distantNodes.add(center);
//					if (rand.nextFloat() < 0.25) {
                        list.add(center);
//					}
                    }
                    allNodes.add(center);
                    return true;
                } else
                    return createNodeRoom(builder, pos, direction.rotateY(), list, allNodes, rand, layer, ++t);
            default:
                return false;

        }

    }

    public boolean createRoom(DungeonBuilder builder, Position2D pos, Direction direction, List<Position2D> list,
                              int layer, int t) {

        if (t > 3)
            return false;

        if (this.segments[pos.x][pos.z].reference.sides[(direction.getHorizontalIndex() + 2) % 4])
            return createRoom(builder, pos, direction.rotateY(), list, layer, ++t);

        switch (direction) {
            case EAST:
                int east = Dungeon.SIZE - pos.x - 1;
                if (east > 1) {
                    int length = Math.min(east, 4);
                    Position2D roomPos = new Position2D(pos.x + length, pos.z);

                    if (!DungeonFeatures.canPlacePieceWithHeight(builder, layer, roomPos.x, roomPos.z, 1, 1, 0, false))
                        return createRoom(builder, pos, direction, list, layer, ++t);

                    DungeonRoom room = new DungeonRoom(null, DungeonPiece.DEFAULT_NBT);
                    room.setPosition(roomPos.x, roomPos.z);

                    this.segments[roomPos.x][roomPos.z] = new PlaceHolder(room);

                    Position2D p = pos.shift(Direction.EAST, 1);
                    buildConnection(segments[p.x][p.z] == null ? pos : p, roomPos);

                    if (this.segments[pos.x][pos.z].reference.getType() != 8)
                        list.add(roomPos);
                    return true;
                } else
                    return createRoom(builder, pos, direction.rotateY(), list, layer, ++t);
            case NORTH:
                int north = pos.z;
                if (north > 1) {
                    int length = Math.min(north, 4);
                    Position2D roomPos = new Position2D(pos.x, pos.z - length);

                    if (!DungeonFeatures.canPlacePieceWithHeight(builder, layer, roomPos.x, roomPos.z, 1, 1, 0, false))
                        return createRoom(builder, pos, direction, list, layer, ++t);

                    DungeonRoom room = new DungeonRoom(null, DungeonPiece.DEFAULT_NBT);
                    room.setPosition(roomPos.x, roomPos.z);

                    this.segments[roomPos.x][roomPos.z] = new PlaceHolder(room);

                    Position2D p = pos.shift(Direction.NORTH, 1);
                    buildConnection(segments[p.x][p.z] == null ? pos : p, roomPos);

                    if (this.segments[pos.x][pos.z].reference.getType() != 8)
                        list.add(roomPos);
                    return true;
                } else
                    return createRoom(builder, pos, direction.rotateY(), list, layer, ++t);
            case SOUTH:
                int south = Dungeon.SIZE - pos.z - 1;
                if (south > 1) {
                    int length = Math.min(south, 4);
                    Position2D roomPos = new Position2D(pos.x, pos.z + length);

                    if (!DungeonFeatures.canPlacePieceWithHeight(builder, layer, roomPos.x, roomPos.z, 1, 1, 0, false))
                        return createRoom(builder, pos, direction, list, layer, ++t);

                    DungeonRoom room = new DungeonRoom(null, DungeonPiece.DEFAULT_NBT);
                    room.setPosition(roomPos.x, roomPos.z);

                    this.segments[roomPos.x][roomPos.z] = new PlaceHolder(room);

                    Position2D p = pos.shift(Direction.SOUTH, 1);
                    buildConnection(segments[p.x][p.z] == null ? pos : p, roomPos);

                    if (this.segments[pos.x][pos.z].reference.getType() != 8)
                        list.add(roomPos);
                    return true;
                } else
                    return createRoom(builder, pos, direction.rotateY(), list, layer, ++t);
            case WEST:
                int west = pos.x;
                if (west > 1) {
                    int length = Math.min(west, 4);
                    Position2D roomPos = new Position2D(pos.x - length, pos.z);

                    if (!DungeonFeatures.canPlacePieceWithHeight(builder, layer, roomPos.x, roomPos.z, 1, 1, 0, false))
                        return createRoom(builder, pos, direction, list, layer, ++t);

                    DungeonRoom room = new DungeonRoom(null, DungeonPiece.DEFAULT_NBT);
                    room.setPosition(roomPos.x, roomPos.z);

                    this.segments[roomPos.x][roomPos.z] = new PlaceHolder(room);

                    Position2D p = pos.shift(Direction.WEST, 1);
                    buildConnection(segments[p.x][p.z] == null ? pos : p, roomPos);

                    if (this.segments[pos.x][pos.z].reference.getType() != 8)
                        list.add(roomPos);
                    return true;
                } else
                    return createRoom(builder, pos, direction.rotateY(), list, layer, ++t);
            default:
                return false;
        }

    }

    public boolean placeSecretRoom(DungeonCorridor corridor, Position2D position, Random rand) {
        Direction direction = (corridor.rotation == Rotation.NONE || corridor.rotation == Rotation.CLOCKWISE_180) ?
                (rand.nextBoolean() ? Direction.NORTH : Direction.SOUTH) : (rand.nextBoolean() ? Direction.EAST : Direction.WEST);
        Position2D pos = position.shift(direction, 2);
        if (pos.isValid(width, length)) {
            Position2D pos2 = position.shift(direction, 1);
            if (segments[pos.x][pos.z] == null && segments[pos2.x][pos2.z] == null) {
                DungeonSecretRoom room = new DungeonSecretRoom(null, DungeonPiece.DEFAULT_NBT);
                int x = Math.min(pos.x, pos2.x), z = Math.min(pos.z, pos2.z);
                room.setPosition(x, z);
                room.setRotation(Orientation.getRotationFromFacing(direction));
                segments[x][z] = new PlaceHolder(room);
                Position2D other = getOther(x, z, direction);
                segments[other.x][other.z] = new PlaceHolder(room).withFlag(PlaceHolder.Flag.PLACEHOLDER);
                corridor.rotation = Orientation.getRotationFromFacing(direction).add(Rotation.CLOCKWISE_90);
                corridor.modelID = DungeonModels.CORRIDOR_SECRET_ROOM_ENTRANCE.id;
                segments[position.x][position.z].withFlag(PlaceHolder.Flag.FIXED_MODEL);
                return true;
            }
        }
        direction = direction.getOpposite();
        pos = position.shift(direction, 2);
        if (pos.isValid(width, length)) {
            Position2D pos2 = position.shift(direction, 1);
            if (segments[pos.x][pos.z] == null && segments[pos2.x][pos2.z] == null) {
                DungeonSecretRoom room = new DungeonSecretRoom(null, DungeonPiece.DEFAULT_NBT);
                int x = Math.min(pos.x, pos2.x), z = Math.min(pos.z, pos2.z);
                room.setPosition(x, z);
                room.setRotation(Orientation.getRotationFromFacing(direction));
                segments[x][z] = new PlaceHolder(room);
                Position2D other = getOther(x, z, direction);
                segments[other.x][other.z] = new PlaceHolder(room).withFlag(PlaceHolder.Flag.PLACEHOLDER);
                corridor.rotation = Orientation.getRotationFromFacing(direction).add(Rotation.CLOCKWISE_90);
                corridor.modelID = DungeonModels.CORRIDOR_SECRET_ROOM_ENTRANCE.id;
                segments[position.x][position.z].withFlag(PlaceHolder.Flag.FIXED_MODEL);
                return true;
            }
        }
        return false;
    }

    private static Position2D getOther(int x, int z, Direction direction) {
        switch (direction) {
            case EAST:
            case WEST:
                return new Position2D(x + 1, z);
            case SOUTH:
            case NORTH:
                return new Position2D(x, z + 1);
            default:
                throw new UnsupportedOperationException("Can't get other position from direction " + direction.toString());
        }
    }

    public boolean canPutDoubleRoom(Position2D pos, Direction direction) {
        if (!pos.isValid(width, length) || segments[pos.x][pos.z] != null && map.isPositionFree(pos.x, pos.z))
            return false;
        switch (direction) {
            case NORTH:
                return pos.z > 0 && this.segments[pos.x][pos.z - 1] == null && map.isPositionFree(pos.x, pos.z - 1);
            case EAST:
                return pos.x < width - 1 && this.segments[pos.x + 1][pos.z] == null && map.isPositionFree(pos.x + 1, pos.z);
            case SOUTH:
                return pos.z < length - 1 && this.segments[pos.x][pos.z + 1] == null
                        && map.isPositionFree(pos.x, pos.z + 1);
            case WEST:
                return pos.x > 0 && this.segments[pos.x - 1][pos.z] == null && map.isPositionFree(pos.x - 1, pos.z);
            default:
                return false;
        }
    }

    public Position2D getLargeRoomPos(Position2D pos) {
        int a = Dungeon.SIZE - 1, x = pos.x, z = pos.z;

        if (x < a && z < a && get(x + 1, z) == null && get(x + 1, z + 1) == null && get(x, z + 1) == null)
            return pos;
        if (x < a && z > 0 && get(x + 1, z) == null && get(x + 1, z - 1) == null && get(x, z - 1) == null)
            return new Position2D(x, z - 1);
        if (x > 0 && z < a && get(x - 1, z) == null && get(x - 1, z + 1) == null && get(x, z + 1) == null)
            return new Position2D(x - 1, z);
        if (x > 0 && z > 0 && get(x - 1, z) == null && get(x - 1, z - 1) == null && get(x, z - 1) == null)
            return new Position2D(x - 1, z - 1);
        return null;
    }

    public static Position2D getLargeRoomPos(DungeonLayer layer, Position2D pos) {
        int a = Dungeon.SIZE - 1, x = pos.x, z = pos.z;

        if (x < a && z < a && layer.get(x + 1, z) == null && layer.get(x + 1, z + 1) == null
                && layer.get(x, z + 1) == null)
            return pos;
        if (x < a && z > 0 && layer.get(x + 1, z) == null && layer.get(x + 1, z - 1) == null
                && layer.get(x, z - 1) == null)
            return new Position2D(x, z - 1);
        if (x > 0 && z < a && layer.get(x - 1, z) == null && layer.get(x - 1, z + 1) == null
                && layer.get(x, z + 1) == null)
            return new Position2D(x - 1, z);
        if (x > 0 && z > 0 && layer.get(x - 1, z) == null && layer.get(x - 1, z - 1) == null
                && layer.get(x, z - 1) == null)
            return new Position2D(x - 1, z - 1);
        return null;
    }

    public DungeonPiece get(int x, int z) {
        return segments[x][z] == null ? null : segments[x][z].reference;
    }

}
