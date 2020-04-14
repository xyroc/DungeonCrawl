package xiroc.dungeoncrawl.dungeon;

/*
 * DungeonCrawl (C) 2019 - 2020 XYROC (XIROC1337), All Rights Reserved 
 */

import java.util.List;
import java.util.Random;

import com.google.common.collect.Lists;

import net.minecraft.util.Direction;
import net.minecraft.util.Rotation;
import net.minecraft.util.Tuple;
import xiroc.dungeoncrawl.DungeonCrawl;
import xiroc.dungeoncrawl.dungeon.DungeonStatTracker.LayerStatTracker;
import xiroc.dungeoncrawl.dungeon.piece.DungeonCorridor;
import xiroc.dungeoncrawl.dungeon.piece.DungeonPiece;
import xiroc.dungeoncrawl.dungeon.piece.DungeonStairs;
import xiroc.dungeoncrawl.dungeon.piece.PlaceHolder;
import xiroc.dungeoncrawl.dungeon.piece.room.DungeonNodeRoom;
import xiroc.dungeoncrawl.dungeon.piece.room.DungeonRoom;
import xiroc.dungeoncrawl.dungeon.piece.room.DungeonSideRoom;
import xiroc.dungeoncrawl.dungeon.treasure.Treasure;
import xiroc.dungeoncrawl.util.Position2D;
import xiroc.dungeoncrawl.util.RotationHelper;

public class DungeonLayer {

	public PlaceHolder[][] segments;

	public Position2D start;
	public Position2D end;

	public int width; // x
	public int length; // z
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

	public void buildMap(DungeonBuilder builder, List<DungeonPiece> pieces, Random rand, Position2D start, int layer,
			boolean lastLayer) {
//		if (!map.markPositionAsOccupied(start))
//			DungeonCrawl.LOGGER.error("Failed to mark start [" + start.x + ", " + start.z + "] as occupied.");
//		this.start = start;
//		this.end = lastLayer ? findLargeRoomPosWithMaxDistance(builder, start, layer) : map.getRandomFreePosition(rand);
//		this.segments[start.x][start.z] = new PlaceHolder(new DungeonStairs(null, DungeonPiece.DEFAULT_NBT).bottom());
//		if (lastLayer) {
//			if (end != null)
//				createLootRoom();
//			else {
//				end = findLargeRoomPosAtArea(builder, map.getRandomFreePosition(rand), layer);
//				if (end != null) {
//					createLootRoom();
//				} else {
//					DungeonCrawl.LOGGER.debug("Failed to find a position for the loot room.");
//					end = forceLargeRoomPosWithMaxDistance(start);
//					createLootRoom();
//				}
//			}
//
//		} else
//			this.segments[end.x][end.z] = new PlaceHolder(new DungeonStairs(null, DungeonPiece.DEFAULT_NBT).top());
//
//		this.buildConnection(start, end);
//		this.extend(builder, map, start, end, rand, layer);
		this.start = start;
		this.createLayout(builder, rand, layer, lastLayer);
	}

	public void extend(DungeonBuilder builder, DungeonLayerMap map, Random rand, int layer) {
		if (layer == 0) {
			Tuple<Position2D, Rotation> sideRoomData = findStarterRoomData(start);
			if (sideRoomData != null) {
				DungeonSideRoom room = new DungeonSideRoom(null, DungeonPiece.DEFAULT_NBT);
				room.modelID = 35;

				Direction dir = RotationHelper.translateDirection(Direction.WEST, sideRoomData.getB());
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

		DungeonCrawl.LOGGER.debug("start: {}", segments[startX][startZ]);
		DungeonCrawl.LOGGER.debug("end: {}", segments[endX][endZ]);

		if (startX == endX && startZ == endZ)
			return;

		if (startX > endX) {
			this.segments[startX][startZ].reference.openSide(Direction.WEST);
			for (int x = startX; x > (startZ == endZ ? endX + 1 : endX); x--) {
				if (this.segments[x - 1][startZ] != null) {
					this.segments[x - 1][startZ].reference.openSide(
							(x - 1) == endX ? startZ < endZ ? Direction.SOUTH : Direction.NORTH : Direction.WEST);
					this.segments[x - 1][startZ].reference.openSide(Direction.EAST);
					this.rotatePiece(this.segments[x - 1][startZ]);
					continue;
				}
				DungeonPiece corridor = new DungeonCorridor(null, DungeonPiece.DEFAULT_NBT);
				corridor.setPosition(x - 1, startZ);
				corridor.setRotation((x - 1) == endX
						? RotationHelper.getRotationFromCW90DoubleFacing(
								startZ > endZ ? Direction.NORTH : Direction.SOUTH, Direction.EAST)
						: RotationHelper.getRotationFromFacing(Direction.WEST));
				corridor.openSide((x - 1) == endX ? startZ < endZ ? Direction.SOUTH : Direction.NORTH : Direction.WEST);
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
							? RotationHelper.getRotationFromCW90DoubleFacing(Direction.NORTH, Direction.WEST)
							: RotationHelper.getRotationFromFacing(Direction.NORTH));
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
							? RotationHelper.getRotationFromCW90DoubleFacing(Direction.NORTH, Direction.WEST)
							: RotationHelper.getRotationFromFacing(Direction.SOUTH));
					corridor.openSide((z + 1) == endZ ? Direction.WEST : Direction.SOUTH);
					corridor.openSide(Direction.NORTH);
					this.segments[endX][z + 1] = new PlaceHolder(corridor);
				}
			} else
				this.segments[endX][endZ].reference.openSide(Direction.EAST);
		} else if (startX < endX) {
			this.segments[startX][startZ].reference.openSide(Direction.EAST);
			for (int x = startX; x < (startZ == endZ ? endX - 1 : endX); x++) {
				if (this.segments[x + 1][startZ] != null) {
					this.segments[x + 1][startZ].reference.openSide(
							(x + 1) == endX ? startZ < endZ ? Direction.SOUTH : Direction.NORTH : Direction.EAST);
					this.segments[x + 1][startZ].reference.openSide(Direction.WEST);
					this.rotatePiece(this.segments[x + 1][startZ]);
					continue;
				}
				DungeonPiece corridor = new DungeonCorridor(null, DungeonPiece.DEFAULT_NBT);
				corridor.setPosition(x + 1, startZ);
				corridor.setRotation((x + 1) == endX
						? RotationHelper.getRotationFromCW90DoubleFacing(
								startZ > endZ ? Direction.NORTH : Direction.SOUTH, Direction.WEST)
						: RotationHelper.getRotationFromFacing(Direction.EAST));
				corridor.openSide((x + 1) == endX ? startZ < endZ ? Direction.SOUTH : Direction.NORTH : Direction.EAST);
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
							? RotationHelper.getRotationFromCW90DoubleFacing(Direction.NORTH, Direction.WEST)
							: RotationHelper.getRotationFromFacing(Direction.NORTH));
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
							? RotationHelper.getRotationFromCW90DoubleFacing(Direction.NORTH, Direction.WEST)
							: RotationHelper.getRotationFromFacing(Direction.SOUTH));
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
					corridor.setRotation(RotationHelper.getRotationFromFacing(Direction.NORTH));
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
					corridor.setRotation(RotationHelper.getRotationFromFacing(Direction.SOUTH));
					corridor.openSide(Direction.SOUTH);
					corridor.openSide(Direction.NORTH);
					this.segments[endX][z + 1] = new PlaceHolder(corridor);
				}
			}
		}
	}

	public void createLayout(DungeonBuilder builder, Random rand, int layer, boolean lastLayer) {
		Direction facing = RotationHelper.RANDOM_FACING_FLAT.roll(rand);

		DungeonStairs s = new DungeonStairs(null, DungeonPiece.DEFAULT_NBT).bottom();
		s.setPosition(start.x, start.z);
		this.segments[s.posX][s.posZ] = new PlaceHolder(s);

		List<Position2D> nodeList = Lists.newArrayList();

		if (!lastLayer) {
			int cycles = 0;
			loop: while (true) {
				if (cycles > 3)
					throw new RuntimeException("Unable to find a position for the exit in layer " + layer);
				switch (facing) {
				case EAST:
					if (Dungeon.SIZE - start.x - 1 > 3) {
						int length = 2 + rand.nextInt(2);
						end = new Position2D(start.x + length + 1, start.z);

						DungeonStairs stairs = new DungeonStairs(null, DungeonPiece.DEFAULT_NBT).top();
						stairs.openSide(Direction.WEST);
						stairs.setPosition(end.x, end.z);
						this.segments[stairs.posX][stairs.posZ] = new PlaceHolder(stairs);
						break loop;
					} else {
						facing = facing.rotateY();
						cycles++;
						continue;
					}
				case NORTH:
					if (start.z > 3) {
						int length = 2 + rand.nextInt(2);
						end = new Position2D(start.x, start.z - length - 1);

						DungeonStairs stairs = new DungeonStairs(null, DungeonPiece.DEFAULT_NBT).top();
						stairs.openSide(Direction.SOUTH);
						stairs.setPosition(end.x, end.z);
						this.segments[stairs.posX][stairs.posZ] = new PlaceHolder(stairs);
						break loop;
					} else {
						facing = facing.rotateY();
						cycles++;
						continue;
					}
				case SOUTH:
					if (Dungeon.SIZE - start.z - 1 > 3) {
						int length = 2 + rand.nextInt(2);
						end = new Position2D(start.x, start.z + length + 1);

						DungeonStairs stairs = new DungeonStairs(null, DungeonPiece.DEFAULT_NBT).top();
						stairs.openSide(Direction.NORTH);
						stairs.setPosition(end.x, end.z);
						this.segments[stairs.posX][stairs.posZ] = new PlaceHolder(stairs);
						break loop;
					} else {
						facing = facing.rotateY();
						cycles++;
						continue;
					}
				case WEST:
					if (start.x > 3) {
						int length = 2 + rand.nextInt(2);
						end = new Position2D(start.x - length - 1, start.z);

						DungeonStairs stairs = new DungeonStairs(null, DungeonPiece.DEFAULT_NBT).top();
						stairs.openSide(Direction.EAST);
						stairs.setPosition(end.x, end.z);
						this.segments[stairs.posX][stairs.posZ] = new PlaceHolder(stairs);
						break loop;
					} else {
						facing = facing.rotateY();
						cycles++;
						continue;
					}
				default:
					continue;
				}
			}

			nodeList.add(end);

			buildConnection(start, end);

		}

		nodeList.add(start);

		int nodes = 3 + layer, rooms = 4 + 2 * layer;

		while (nodes > 0) {
			if (nodeList.isEmpty()) {
				DungeonCrawl.LOGGER.debug("Nodelist is empty; breaking out of node generation. {} nodes left.", nodes);
				break;
			}

			Position2D nodePos = nodeList.get(rand.nextInt(nodeList.size()));

			PlaceHolder node = this.segments[nodePos.x][nodePos.z];

			Direction direction = findNext(node.reference, RotationHelper.RANDOM_FACING_FLAT.roll(rand));

			if (direction == null) {
				nodeList.remove(nodePos);
				continue;
			}

			if (createNodeRoom(builder, nodePos, direction, nodeList, rand, layer, 0))
				nodes--;
			else
				nodeList.remove(nodePos);
		}

		if (lastLayer) {

			DungeonCrawl.LOGGER.debug("There are {} distant nodes.", distantNodes.size());
			if (distantNodes.isEmpty()) {
				rooms += 8;
			} else {
				Position2D nodePos = distantNodes.get(rand.nextInt(distantNodes.size()));

				DungeonNodeRoom node = (DungeonNodeRoom) segments[nodePos.x][nodePos.z].reference;

				node.large = true;
				node.lootRoom = true;

			}

		}

		while (rooms > 0) {
			if (nodeList.isEmpty()) {
				DungeonCrawl.LOGGER.debug("Nodelist is empty; breaking out of room generation. {} rooms left.", rooms);
				break;
			}
			Position2D nodePos = nodeList.get(rand.nextInt(nodeList.size()));

			PlaceHolder node = this.segments[nodePos.x][nodePos.z];

			Direction direction = findNext(node.reference, RotationHelper.RANDOM_FACING_FLAT.roll(rand));

			if (direction == null) {
				nodeList.remove(nodePos);
				continue;
			}

			if (createRoom(builder, nodePos, direction, nodeList, layer, 0))
				rooms--;
			else
				nodeList.remove(nodePos);
		}

	}

	public Position2D findLargeRoomPosWithMaxDistance(DungeonBuilder builder, Position2D pos, int layer) {
		int x = pos.x, z = pos.z;
		int xHalf = width / 2 - 1, zHalf = length / 2 - 1;
		if (x > xHalf) {
			if (z > zHalf)
				return findLargeRoomPosAtArea(builder, new Position2D(0, 0), layer);
			else
				return findLargeRoomPosAtArea(builder, new Position2D(0, length - 1), layer);

		} else {
			if (z > zHalf)
				return findLargeRoomPosAtArea(builder, new Position2D(width - 1, 0), layer);
			else
				return findLargeRoomPosAtArea(builder, new Position2D(width - 1, length - 1), layer);
		}
	}

	public Position2D forceLargeRoomPosWithMaxDistance(Position2D pos) {
		int x = pos.x, z = pos.z;
		int xHalf = width / 2 - 1, zHalf = length / 2 - 1;
		if (x > xHalf) {
			if (z > zHalf)
				return getLargeRoomPos(new Position2D(0, 0));
			else
				return getLargeRoomPos(new Position2D(0, length - 1));
		} else {
			if (z > zHalf)
				return getLargeRoomPos(new Position2D(width - 1, 0));
			else
				return getLargeRoomPos(new Position2D(width - 1, length - 1));
		}
	}

	public Position2D findLargeRoomPosAtArea(DungeonBuilder builder, Position2D pos, int layer) {
		for (int i = 0; i < 16; i++) {
			for (int x = -i; x < i; x++)
				if (Position2D.isValid(pos.x + x, pos.z + i, width, length) && DungeonFeatures
						.canPlacePieceWithHeight(builder, layer, pos.x + x, pos.z + i, 2, 2, 1, false))
					return new Position2D(pos.x + x, pos.z + i);

			for (int z = -i; z < i; z++)
				if (Position2D.isValid(pos.x + i, pos.z + z, width, length) && DungeonFeatures
						.canPlacePieceWithHeight(builder, layer, pos.x + i, pos.z + z, 2, 2, 1, false))
					return new Position2D(pos.x + i, pos.z + z);

			for (int x = -i; x < i; x++)
				if (Position2D.isValid(pos.x + x, pos.z - i, width, length) && DungeonFeatures
						.canPlacePieceWithHeight(builder, layer, pos.x + x, pos.z - i, 2, 2, 1, false))
					return new Position2D(pos.x + x, pos.z - i);

			for (int z = -i; z < i; z++)
				if (Position2D.isValid(pos.x - i, pos.z + z, width, length) && DungeonFeatures
						.canPlacePieceWithHeight(builder, layer, pos.x - i, pos.z + z, 2, 2, 1, false))
					return new Position2D(pos.x - i, pos.z + z);
		}
		return null;
	}

	public Tuple<Position2D, Rotation> findStarterRoomData(Position2D start) {
		for (int x = start.x - 2; x < start.x + 2; x++) {
			for (int z = start.z - 2; z < start.z + 2; z++) {
				if (Position2D.isValid(x, z, width, length)) {
					if (segments[x][z] != null && segments[x][z].reference.getType() == 0
							&& segments[x][z].reference.connectedSides < 4) {
						Tuple<Position2D, Rotation> data = findSideRoomData(new Position2D(x, z));
						if (data == null)
							continue;
						return data;
					}
				}
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
			piece.setRotation(RotationHelper.getRotationFromFacing(DungeonPiece.getOneWayDirection(piece)));
			return;
		case 2:
			if (piece.sides[0] && piece.sides[2])
				piece.setRotation(RotationHelper.getRotationFromFacing(Direction.NORTH));
			else if (piece.sides[1] && piece.sides[3])
				piece.setRotation(RotationHelper.getRotationFromFacing(Direction.EAST));
			else
				piece.setRotation(RotationHelper.getRotationFromCW90DoubleFacing(DungeonPiece.getOpenSide(piece, 0),
						DungeonPiece.getOpenSide(piece, 1)));
			break;
		case 3:
			piece.setRotation(RotationHelper.getRotationFromTripleFacing(DungeonPiece.getOpenSide(piece, 0),
					DungeonPiece.getOpenSide(piece, 1), DungeonPiece.getOpenSide(piece, 2)));
			break;
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

	public boolean createNodeRoom(DungeonBuilder builder, Position2D pos, Direction direction, List<Position2D> list,
			Random rand, int layer, int t) {

		if (t > 3)
			return false;

		if (this.segments[pos.x][pos.z].reference.sides[(direction.getHorizontalIndex() + 2) % 4])
			return createNodeRoom(builder, pos, direction.rotateY(), list, rand, layer, ++t);

		switch (direction) {
		case EAST:
			int east = Dungeon.SIZE - pos.x - 1;
			if (east > 3) {
				int length = Math.min(east, 5);
				Position2D center = new Position2D(pos.x + length, pos.z);

				if (!DungeonFeatures.canPlacePieceWithHeight(builder, layer, center.x - 1, center.z - 1, 3, 3, 0,
						false))
					return createNodeRoom(builder, pos, direction, list, rand, layer, ++t);

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
				if (pos.equals(start))
					list.add(center);
				else {
					distantNodes.add(center);
					if (rand.nextFloat() < 0.25)
						list.add(center);
				}
				return true;
			} else
				return createNodeRoom(builder, pos, direction.rotateY(), list, rand, layer, ++t);
		case NORTH:
			int north = pos.z;
			if (north > 3) {
				int length = Math.min(north, 5);
				Position2D center = new Position2D(pos.x, pos.z - length);

				if (!DungeonFeatures.canPlacePieceWithHeight(builder, layer, center.x - 1, center.z - 1, 3, 3, 0,
						false))
					return createNodeRoom(builder, pos, direction, list, rand, layer, ++t);

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
				if (pos.equals(start))
					list.add(center);
				else {
					distantNodes.add(center);
					if (rand.nextFloat() < 0.25)
						list.add(center);
				}
				return true;
			} else
				return createNodeRoom(builder, pos, direction.rotateY(), list, rand, layer, ++t);
		case SOUTH:
			int south = Dungeon.SIZE - pos.z - 1;
			if (south > 3) {
				int length = Math.min(south, 5);
				Position2D center = new Position2D(pos.x, pos.z + length);

				if (!DungeonFeatures.canPlacePieceWithHeight(builder, layer, center.x - 1, center.z - 1, 3, 3, 0,
						false))
					return createNodeRoom(builder, pos, direction, list, rand, layer, ++t);

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
				if (pos.equals(start))
					list.add(center);
				else {
					distantNodes.add(center);
					if (rand.nextFloat() < 0.25)
						list.add(center);
				}
				return true;
			} else
				return createNodeRoom(builder, pos, direction.rotateY(), list, rand, layer, ++t);
		case WEST:
			int west = pos.x;
			if (west > 3) {
				int length = Math.min(west, 5);
				Position2D center = new Position2D(pos.x - length, pos.z);

				if (!DungeonFeatures.canPlacePieceWithHeight(builder, layer, center.x - 1, center.z - 1, 3, 3, 0,
						false))
					return createNodeRoom(builder, pos, direction, list, rand, layer, ++t);

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
				if (pos.equals(start))
					list.add(center);
				else {
					distantNodes.add(center);
					if (rand.nextFloat() < 0.25)
						list.add(center);
				}
				return true;
			} else
				return createNodeRoom(builder, pos, direction.rotateY(), list, rand, layer, ++t);
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
			if (east > 2) {
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
			if (north > 2) {
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
			if (south > 2) {
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
			if (west > 2) {
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

	public boolean canPutDoubleRoom(Position2D pos, Direction direction) {
//		DungeonCrawl.LOGGER.debug("[{}, {}] {}, {}", width, length, pos.x, pos.z);
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
