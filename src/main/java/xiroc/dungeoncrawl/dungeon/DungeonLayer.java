package xiroc.dungeoncrawl.dungeon;

/*
 * DungeonCrawl (C) 2019 - 2020 XYROC (XIROC1337), All Rights Reserved 
 */

import java.util.List;
import java.util.Random;

import net.minecraft.util.Direction;
import net.minecraft.util.Rotation;
import net.minecraft.util.Tuple;
import xiroc.dungeoncrawl.DungeonCrawl;
import xiroc.dungeoncrawl.config.Config;
import xiroc.dungeoncrawl.dungeon.DungeonStatTracker.LayerStatTracker;
import xiroc.dungeoncrawl.dungeon.piece.DungeonCorridor;
import xiroc.dungeoncrawl.dungeon.piece.DungeonPart;
import xiroc.dungeoncrawl.dungeon.piece.DungeonPiece;
import xiroc.dungeoncrawl.dungeon.piece.DungeonStairs;
import xiroc.dungeoncrawl.dungeon.piece.PlaceHolder;
import xiroc.dungeoncrawl.dungeon.piece.room.DungeonRoom;
import xiroc.dungeoncrawl.dungeon.piece.room.DungeonSideRoom;
import xiroc.dungeoncrawl.dungeon.treasure.Treasure;
import xiroc.dungeoncrawl.util.Position2D;
import xiroc.dungeoncrawl.util.RotationHelper;

public class DungeonLayer {

	/*
	 * NORTH|-Z EAST|+X SOUTH|+Z WEST|-X
	 */

	public PlaceHolder[][] segments;

	public Position2D start;
	public Position2D end;

	public int width; // x
	public int length; // z
	public LayerStatTracker statTracker;

	public DungeonLayerMap map;

	public int extraRooms; // If, for some odd reason, no final loot room position can be found, the layer
	// will have more rooms than usual to compensate the missing loot a bit. This is
	// only relevant for the last layer.

	public DungeonLayer() {
		this(16, 16);
	}

	public DungeonLayer(int width, int length) {
		this.width = width;
		this.length = length;
		this.statTracker = new LayerStatTracker();
		this.segments = new PlaceHolder[this.width][this.length];
	}

	public void buildMap(DungeonBuilder builder, List<DungeonPiece> pieces, Random rand, Position2D start, int layer,
			boolean lastLayer) {
		if (!map.markPositionAsOccupied(start))
			DungeonCrawl.LOGGER.error("Failed to mark start [" + start.x + ", " + start.z + "] as occupied.");
		this.start = start;
		this.end = lastLayer ? findLargeRoomPosWithMaxDistance(builder, start, layer) : map.getRandomFreePosition(rand);
		this.segments[start.x][start.z] = new PlaceHolder(new DungeonStairs(null, DungeonPiece.DEFAULT_NBT).bottom());
		if (lastLayer) {
			if (end != null)
				createLootRoom();
			else {
				end = findLargeRoomPosAtArea(builder, map.getRandomFreePosition(rand), layer);
				if (end != null) {
					createLootRoom();
				} else {
					DungeonCrawl.LOGGER.debug("Failed to find a position for the loot room.");
					end = forceLargeRoomPosWithMaxDistance(start);
					createLootRoom();
				}
			}

		} else
			this.segments[end.x][end.z] = new PlaceHolder(new DungeonStairs(null, DungeonPiece.DEFAULT_NBT).top());

		this.buildConnection(start, end);
//		this.extend(builder, map, start, end, rand, layer);
	}

	public void extend(DungeonBuilder builder, DungeonLayerMap map, Random rand, int layer) {
		int additionalFeatures = Config.LAYER_ADDITIONS_MIN.get() + rand.nextInt(Config.LAYER_ADDITIONS_EXTRA.get())
				+ extraRooms;
		Position2D[] additions = new Position2D[additionalFeatures];
		for (int i = 0; i < additionalFeatures; i++) {
			additions[i] = map.getRandomFreePosition(rand);
			if (additions[i] == null) {
//				DungeonCrawl.LOGGER.warn(
//						"Failed to place {} more rooms because all free positions are already taken. Please decrease the layer_min_additions and/or the layer_extra_additions value in the config (dungeon_crawl.toml) to avoid this issue.",
//						additionalFeatures - i);
				return;
			}
			if (rand.nextFloat() < 0.5) {
				Position2D largeRoomPos = DungeonLayer.getLargeRoomPos(this,
						new Position2D(additions[i].x, additions[i].z));
				if (largeRoomPos != null && DungeonFeatures.canPlacePieceWithHeight(builder, layer, additions[i].x,
						additions[i].z, 2, 2, 1, true)) {
					int roomID = RandomFeature.LARGE_ROOMS.roll(rand);
					DungeonPart part1 = new DungeonPart(null, DungeonPiece.DEFAULT_NBT);
					DungeonPart part2 = new DungeonPart(null, DungeonPiece.DEFAULT_NBT);
					DungeonPart part3 = new DungeonPart(null, DungeonPiece.DEFAULT_NBT);
					DungeonPart part4 = new DungeonPart(null, DungeonPiece.DEFAULT_NBT);

					part1.treasureType = 0;
					part2.treasureType = 0;
					part3.treasureType = 0;
					part4.treasureType = 0;

					part1.rotation = Rotation.NONE;
					part2.rotation = Rotation.NONE;
					part3.rotation = Rotation.NONE;
					part4.rotation = Rotation.NONE;

					part1.walls = part2.walls = part3.walls = part4.walls = true;
//					part1.stage = part2.stage = part3.stage = part4.stage = 0;

					part1.set(roomID, 0, 0, 0, 8, 16, 8);
					part2.set(roomID, 8, 0, 0, 8, 16, 8);
					part3.set(roomID, 8, 0, 8, 8, 16, 8);
					part4.set(roomID, 0, 0, 8, 8, 16, 8);

					part1.setPosition(largeRoomPos.x, largeRoomPos.z);
					part2.setPosition(largeRoomPos.x + 1, largeRoomPos.z);
					part3.setPosition(largeRoomPos.x + 1, largeRoomPos.z + 1);
					part4.setPosition(largeRoomPos.x, largeRoomPos.z + 1);

					part1.sides[0] = false;
					part1.sides[1] = true;
					part1.sides[2] = true;
					part1.sides[3] = false;

					part2.sides[0] = false;
					part2.sides[1] = false;
					part2.sides[2] = true;
					part2.sides[3] = true;

					part3.sides[0] = true;
					part3.sides[1] = false;
					part3.sides[2] = false;
					part3.sides[3] = true;

					part4.sides[0] = true;
					part4.sides[1] = true;
					part4.sides[2] = false;
					part4.sides[3] = false;

					part1.openAdditionalSides(get(largeRoomPos.x, largeRoomPos.z));
					part2.openAdditionalSides(get(largeRoomPos.x + 1, largeRoomPos.z));
					part3.openAdditionalSides(get(largeRoomPos.x + 1, largeRoomPos.z + 1));
					part4.openAdditionalSides(get(largeRoomPos.x, largeRoomPos.z + 1));

//					part1.setRealPosition(startPos.getX() + part1.posX * 8, startPos.getY() - i * 8, startPos.getZ() + part1.posZ * 8);
//					part2.setRealPosition(startPos.getX() + part2.posX * 8, startPos.getY() - i * 8, startPos.getZ() + part2.posZ * 8);
//					part3.setRealPosition(startPos.getX() + part3.posX * 8, startPos.getY() - i * 8, startPos.getZ() + part3.posZ * 8);
//					part4.setRealPosition(startPos.getX() + part4.posX * 8, startPos.getY() - i * 8, startPos.getZ() + part4.posZ * 8);

					segments[largeRoomPos.x][largeRoomPos.z] = new PlaceHolder(part1).withFlag(PlaceHolder.Flag.FIXED_ROTATION);
					segments[largeRoomPos.x + 1][largeRoomPos.z] = new PlaceHolder(part2).withFlag(PlaceHolder.Flag.FIXED_ROTATION);;
					segments[largeRoomPos.x + 1][largeRoomPos.z + 1] = new PlaceHolder(part3).withFlag(PlaceHolder.Flag.FIXED_ROTATION);;
					segments[largeRoomPos.x][largeRoomPos.z + 1] = new PlaceHolder(part4).withFlag(PlaceHolder.Flag.FIXED_ROTATION);;

					DungeonFeatures.mark(builder, layer, largeRoomPos.x, largeRoomPos.z, 2, 2, 1);
					continue;
				}
			}
			DungeonPiece room = new DungeonRoom(null, DungeonPiece.DEFAULT_NBT);
			room.setPosition(additions[i].x, additions[i].z);
			if (this.segments[additions[i].x][additions[i].z] != null) {
//				DungeonCrawl.LOGGER.debug("Placing a room into a piece at (" + additions[i].x + " / " + additions[i].z + "). " + " Replaced piece: " + this.segments[additions[i].x][additions[i].z]);
				room.sides = this.segments[additions[i].x][additions[i].z].reference.sides;
				room.connectedSides = this.segments[additions[i].x][additions[i].z].reference.connectedSides;
			}
			this.segments[additions[i].x][additions[i].z] = new PlaceHolder(room);
		}
		for (int i = 0; i < additionalFeatures; i++) {
			Position2D one = additions[i];
			switch (rand.nextInt(2)) {
			case 0:
				this.buildConnection(rand.nextBoolean() ? start : end, one);
				break;
			case 1:
				this.processAddition(additions, start, end, one, rand);
				break;
			}
		}
		if (layer == 0) {
			Tuple<Position2D, Rotation> sideRoomData = findStarterRoomData(start);
			if (sideRoomData != null) {
				DungeonSideRoom room = new DungeonSideRoom(null, DungeonPiece.DEFAULT_NBT);
				room.modelID = 34;

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

	public void createLootRoom() {
		if (end == null) {
			DungeonCrawl.LOGGER.warn(
					"Failed to find a final room position for the last layer of a dungeon. Layer map start pos: ({}|{})",
					start.x, start.z);
			this.extraRooms = Dungeon.SIZE;
			return;
		}
		DungeonPart part1 = new DungeonPart(null, DungeonPiece.DEFAULT_NBT);
		DungeonPart part2 = new DungeonPart(null, DungeonPiece.DEFAULT_NBT);
		DungeonPart part3 = new DungeonPart(null, DungeonPiece.DEFAULT_NBT);
		DungeonPart part4 = new DungeonPart(null, DungeonPiece.DEFAULT_NBT);

		part1.treasureType = 7;
		part2.treasureType = 7;
		part3.treasureType = 7;
		part4.treasureType = 7;

		part1.rotation = Rotation.NONE;
		part2.rotation = Rotation.NONE;
		part3.rotation = Rotation.NONE;
		part4.rotation = Rotation.NONE;

		part1.walls = part2.walls = part3.walls = part4.walls = true;

		part1.set(28, 0, 0, 0, 8, 16, 8);
		part2.set(28, 8, 0, 0, 8, 16, 8);
		part3.set(28, 8, 0, 8, 8, 16, 8);
		part4.set(28, 0, 0, 8, 8, 16, 8);

		part1.setPosition(end.x, end.z);
		part2.setPosition(end.x + 1, end.z);
		part3.setPosition(end.x + 1, end.z + 1);
		part4.setPosition(end.x, end.z + 1);

		part1.sides[0] = false;
		part1.sides[1] = true;
		part1.sides[2] = true;
		part1.sides[3] = false;

		part2.sides[0] = false;
		part2.sides[1] = false;
		part2.sides[2] = true;
		part2.sides[3] = true;

		part3.sides[0] = true;
		part3.sides[1] = false;
		part3.sides[2] = false;
		part3.sides[3] = true;

		part4.sides[0] = true;
		part4.sides[1] = true;
		part4.sides[2] = false;
		part4.sides[3] = false;

		this.segments[end.x][end.z] = new PlaceHolder(part1);
		this.segments[end.x + 1][end.z] = new PlaceHolder(part2);
		this.segments[end.x + 1][end.z + 1] = new PlaceHolder(part3);
		this.segments[end.x][end.z + 1] = new PlaceHolder(part4);

		map.markPositionAsOccupied(end);
		map.markPositionAsOccupied(new Position2D(end.x + 1, end.z));
		map.markPositionAsOccupied(new Position2D(end.x + 1, end.z + 1));
		map.markPositionAsOccupied(new Position2D(end.x, end.z + 1));
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

	public void processAddition(Position2D[] additions, Position2D start, Position2D end, Position2D one, Random rand) {
		this.buildConnection(rand.nextBoolean() ? start : end, one);
		this.buildConnection(one, additions[rand.nextInt(additions.length)]);
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

	public boolean isInitialized() {
		return this.segments != null;
	}

	public DungeonPiece get(int x, int z) {
		return segments[x][z] == null ? null : segments[x][z].reference;
	}

}
