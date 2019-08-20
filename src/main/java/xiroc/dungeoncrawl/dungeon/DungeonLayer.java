package xiroc.dungeoncrawl.dungeon;

/*
 * DungeonCrawl (C) 2019 XYROC (XIROC1337), All Rights Reserved 
 */

import java.util.Random;

import net.minecraft.block.Blocks;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import xiroc.dungeoncrawl.DungeonCrawl;
import xiroc.dungeoncrawl.dungeon.DungeonPieces.DungeonPiece;
import xiroc.dungeoncrawl.util.Config;
import xiroc.dungeoncrawl.util.Position2D;
import xiroc.dungeoncrawl.util.RotationHelper;

public class DungeonLayer {

	/*
	 * NORTH|-Z EAST|+X SOUTH|+Z WEST|-X
	 */

	public DungeonLayerType type;
	public DungeonPiece[][] segments;

	public Position2D start;
	public Position2D end;

	public int width; // x
	public int length; // z

	public DungeonLayer(DungeonLayerType type) {
		this(type, 16, 16);
	}

	public DungeonLayer(DungeonLayerType type, int width, int length) {
		this.type = type;
		this.width = width;
		this.length = length;
	}

	public void buildMap(Random rand, Position2D start, boolean noEnd) {
		this.segments = new DungeonPiece[this.width][this.length];
		DungeonLayerMap map = new DungeonLayerMap(this.width, this.length);
		if (!map.markPositionAsOccupied(start))
			DungeonCrawl.LOGGER.error("Failed to mark start [" + start.x + ", " + start.z + "] as occupied.");
		Position2D end = map.getRandomFreePosition(rand);
		this.start = start;
		this.end = end;
		this.segments[start.x][start.z] = new DungeonPieces.StairsBot(null, DungeonPieces.DEFAULT_NBT);
		this.segments[end.x][end.z] = noEnd ? new DungeonPieces.Room(null, DungeonPieces.DEFAULT_NBT) : new DungeonPieces.StairsTop(null, DungeonPieces.DEFAULT_NBT);
		this.buildConnection(start, end);
		this.extend(map, start, end, rand);
	}

	public void extend(DungeonLayerMap map, Position2D start, Position2D end, Random rand) {
		int additionalFeatures = Config.LAYER_ADDITIONS_MIN.get() + rand.nextInt(Config.LAYER_ADDITIONS_EXTRA.get());
		Position2D[] additions = new Position2D[additionalFeatures];
		for (int i = 0; i < additionalFeatures; i++) {
			additions[i] = map.getRandomFreePosition(rand);
			if (rand.nextFloat() < 0.1) {
				Position2D largeRoomPos = getLargeRoomPos(additions[i]);
				if (largeRoomPos != null) {
					int roomID = 25; // TODO random large room ID
					DungeonPieces.Part part1 = new DungeonPieces.Part(null, DungeonPieces.DEFAULT_NBT);
					DungeonPieces.Part part2 = new DungeonPieces.Part(null, DungeonPieces.DEFAULT_NBT);
					DungeonPieces.Part part3 = new DungeonPieces.Part(null, DungeonPieces.DEFAULT_NBT);
					DungeonPieces.Part part4 = new DungeonPieces.Part(null, DungeonPieces.DEFAULT_NBT);
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
					this.segments[largeRoomPos.x][largeRoomPos.z] = part1;
					this.segments[largeRoomPos.x + 1][largeRoomPos.z] = part2;
					this.segments[largeRoomPos.x + 1][largeRoomPos.z + 1] = part3;
					this.segments[largeRoomPos.x][largeRoomPos.z + 1] = part4;
					map.markPositionAsOccupied(new Position2D(largeRoomPos.x, largeRoomPos.z));
					map.markPositionAsOccupied(new Position2D(largeRoomPos.x + 1, largeRoomPos.z));
					map.markPositionAsOccupied(new Position2D(largeRoomPos.x + 1, largeRoomPos.z + 1));
					map.markPositionAsOccupied(new Position2D(largeRoomPos.x, largeRoomPos.z + 1));
					continue;
				}
			}
			DungeonPiece room = new DungeonPieces.Room(null, DungeonPieces.DEFAULT_NBT);
			room.setPosition(additions[i].x, additions[i].z);
			if (this.segments[additions[i].x][additions[i].z] != null) {
				DungeonCrawl.LOGGER.info("Placing a room into a piece at (" + additions[i].x + " / " + additions[i].z + "). " + " Replaced piece: " + this.segments[additions[i].x][additions[i].z]);
				room.sides = this.segments[additions[i].x][additions[i].z].sides;
				room.connectedSides = this.segments[additions[i].x][additions[i].z].connectedSides;
			}
			this.segments[additions[i].x][additions[i].z] = room;
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
	}

	public void buildConnection(Position2D start, Position2D end) {
		int startX = start.x;
		int startZ = start.z;
		int endX = end.x;
		int endZ = end.z;
		if (startX == endX && startZ == endZ)
			return;
		if (startX > endX) {
			this.segments[startX][startZ].openSide(Direction.WEST);
			for (int x = startX; x > (startZ == endZ ? endX + 1 : endX); x--) {
				if (this.segments[x - 1][startZ] != null) {
					this.segments[x - 1][startZ].openSide((x - 1) == endX ? startZ < endZ ? Direction.SOUTH : Direction.NORTH : Direction.WEST);
					this.segments[x - 1][startZ].openSide(Direction.EAST);
					this.rotatePiece(this.segments[x - 1][startZ]);
					continue;
				}
				DungeonPiece corridor = new DungeonPieces.Corridor(null, DungeonPieces.DEFAULT_NBT);
				corridor.setPosition(x - 1, startZ);
				corridor.setRotation((x - 1) == endX ? RotationHelper.getRotationFromCW90DoubleFacing(startZ > endZ ? Direction.NORTH : Direction.SOUTH, Direction.EAST) : RotationHelper.getRotationFromFacing(Direction.WEST));
				corridor.openSide((x - 1) == endX ? startZ < endZ ? Direction.SOUTH : Direction.NORTH : Direction.WEST);
				corridor.openSide(Direction.EAST);
				this.segments[x - 1][startZ] = corridor;
			}
			if (startZ > endZ) {
				this.segments[endX][endZ].openSide(Direction.SOUTH);
				for (int z = startZ; z > endZ + 1; z--) {
					if (this.segments[endX][z - 1] != null) {
						this.segments[endX][z - 1].openSide(Direction.SOUTH);
						this.segments[endX][z - 1].openSide((z - 1) == endZ ? Direction.WEST : Direction.NORTH);
						this.rotatePiece(this.segments[endX][z - 1]);
						continue;
					}
					DungeonPiece corridor = new DungeonPieces.Corridor(null, DungeonPieces.DEFAULT_NBT);
					corridor.setPosition(endX, z - 1);
					corridor.setRotation((z - 1) == endZ ? RotationHelper.getRotationFromCW90DoubleFacing(Direction.NORTH, Direction.WEST) : RotationHelper.getRotationFromFacing(Direction.NORTH));
					corridor.openSide(Direction.SOUTH);
					corridor.openSide((z - 1) == endZ ? Direction.WEST : Direction.NORTH);
					this.segments[endX][z - 1] = corridor;
				}
			} else if (startZ < endZ) {
				this.segments[endX][endZ].openSide(Direction.NORTH);
				for (int z = startZ; z < endZ - 1; z++) {
					if (this.segments[endX][z + 1] != null) {
						this.segments[endX][z + 1].openSide((z + 1) == endZ ? Direction.WEST : Direction.SOUTH);
						this.segments[endX][z + 1].openSide(Direction.NORTH);
						this.rotatePiece(this.segments[endX][z + 1]);
						continue;
					}
					DungeonPiece corridor = new DungeonPieces.Corridor(null, DungeonPieces.DEFAULT_NBT);
					corridor.setPosition(endX, z + 1);
					corridor.setRotation((z + 1) == endZ ? RotationHelper.getRotationFromCW90DoubleFacing(Direction.NORTH, Direction.WEST) : RotationHelper.getRotationFromFacing(Direction.SOUTH));
					corridor.openSide((z + 1) == endZ ? Direction.WEST : Direction.SOUTH);
					corridor.openSide(Direction.NORTH);
					this.segments[endX][z + 1] = corridor;
				}
			} else
				this.segments[endX][endZ].openSide(Direction.EAST);
		} else if (startX < endX) {
			this.segments[startX][startZ].openSide(Direction.EAST);
			for (int x = startX; x < (startZ == endZ ? endX - 1 : endX); x++) {
				if (this.segments[x + 1][startZ] != null) {
					this.segments[x + 1][startZ].openSide((x + 1) == endX ? startZ < endZ ? Direction.SOUTH : Direction.NORTH : Direction.EAST);
					this.segments[x + 1][startZ].openSide(Direction.WEST);
					this.rotatePiece(this.segments[x + 1][startZ]);
					continue;
				}
				DungeonPiece corridor = new DungeonPieces.Corridor(null, DungeonPieces.DEFAULT_NBT);
				corridor.setPosition(x + 1, startZ);
				corridor.setRotation((x + 1) == endX ? RotationHelper.getRotationFromCW90DoubleFacing(startZ > endZ ? Direction.NORTH : Direction.SOUTH, Direction.WEST) : RotationHelper.getRotationFromFacing(Direction.EAST));
				corridor.openSide((x + 1) == endX ? startZ < endZ ? Direction.SOUTH : Direction.NORTH : Direction.EAST);
				corridor.openSide(Direction.WEST);
				this.segments[x + 1][startZ] = corridor;
			}
			if (startZ > endZ) {
				this.segments[endX][endZ].openSide(Direction.SOUTH);
				for (int z = startZ; z > endZ + 1; z--) {
					if (this.segments[endX][z - 1] != null) {
						this.segments[endX][z - 1].openSide(Direction.SOUTH);
						this.segments[endX][z - 1].openSide((z - 1) == endZ ? Direction.EAST : Direction.NORTH);
						this.rotatePiece(this.segments[endX][z - 1]);
						continue;
					}
					DungeonPiece corridor = new DungeonPieces.Corridor(null, DungeonPieces.DEFAULT_NBT);
					corridor.setPosition(endX, z - 1);
					corridor.setRotation((z - 1) == endZ ? RotationHelper.getRotationFromCW90DoubleFacing(Direction.NORTH, Direction.WEST) : RotationHelper.getRotationFromFacing(Direction.NORTH));
					corridor.openSide(Direction.SOUTH);
					corridor.openSide((z - 1) == endZ ? Direction.WEST : Direction.NORTH);
					this.segments[endX][z - 1] = corridor;
				}
			} else if (startZ < endZ) {
				this.segments[endX][endZ].openSide(Direction.NORTH);
				for (int z = startZ; z < endZ - 1; z++) {
					if (this.segments[endX][z + 1] != null) {
						this.segments[endX][z + 1].openSide((z + 1) == endZ ? Direction.EAST : Direction.SOUTH);
						this.segments[endX][z + 1].openSide(Direction.NORTH);
						this.rotatePiece(this.segments[endX][z + 1]);
						continue;
					}
					DungeonPiece corridor = new DungeonPieces.Corridor(null, DungeonPieces.DEFAULT_NBT);
					corridor.setPosition(endX, z + 1);
					corridor.setRotation((z + 1) == endZ ? RotationHelper.getRotationFromCW90DoubleFacing(Direction.NORTH, Direction.WEST) : RotationHelper.getRotationFromFacing(Direction.SOUTH));
					corridor.openSide((z + 1) == endZ ? Direction.WEST : Direction.SOUTH);
					corridor.openSide(Direction.NORTH);
					this.segments[endX][z + 1] = corridor;
				}
			} else
				this.segments[endX][endZ].openSide(Direction.WEST);
		} else {
			if (startZ > endZ) {
				this.segments[startX][startZ].openSide(Direction.NORTH);
				this.segments[endX][endZ].openSide(Direction.SOUTH);
				for (int z = startZ; z > endZ + 1; z--) {
					if (this.segments[endX][z - 1] != null) {
						this.segments[endX][z - 1].openSide(Direction.NORTH);
						this.segments[endX][z - 1].openSide(Direction.SOUTH);
						this.rotatePiece(this.segments[endX][z - 1]);
						continue;
					}
					DungeonPiece corridor = new DungeonPieces.Corridor(null, DungeonPieces.DEFAULT_NBT);
					corridor.setPosition(endX, z - 1);
					corridor.setRotation(RotationHelper.getRotationFromFacing(Direction.NORTH));
					corridor.openSide(Direction.SOUTH);
					corridor.openSide(Direction.NORTH);
					this.segments[endX][z - 1] = corridor;
				}
			} else if (startZ < endZ) {
				this.segments[startX][startZ].openSide(Direction.SOUTH);
				this.segments[endX][endZ].openSide(Direction.NORTH);
				for (int z = startZ; z < endZ - 1; z++) {
					if (this.segments[endX][z + 1] != null) {
						this.segments[endX][z + 1].openSide(Direction.SOUTH);
						this.segments[endX][z + 1].openSide(Direction.NORTH);
						this.rotatePiece(this.segments[endX][z + 1]);
						continue;
					}
					this.segments[endX][endZ].openSide(Direction.NORTH);
					DungeonPiece corridor = new DungeonPieces.Corridor(null, DungeonPieces.DEFAULT_NBT);
					corridor.setPosition(endX, z + 1);
					corridor.setRotation(RotationHelper.getRotationFromFacing(Direction.SOUTH));
					corridor.openSide(Direction.SOUTH);
					corridor.openSide(Direction.NORTH);
					this.segments[endX][z + 1] = corridor;
				}
			} else
				DungeonCrawl.LOGGER.warn("Tried to build a connection between two positions but they were the same. ( " + startX + " / " + startZ + " ) -> ( " + endX + "/" + endZ + " )");
		}
	}

	// not tested specifically, but seems to be working anyway :D
	public void rotatePiece(DungeonPiece piece) {
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
				piece.setRotation(RotationHelper.getRotationFromCW90DoubleFacing(DungeonPiece.getOpenSide(piece, 0), DungeonPiece.getOpenSide(piece, 1)));
			break;
		case 3:
			piece.setRotation(RotationHelper.getRotationFromTripleFacing(DungeonPiece.getOpenSide(piece, 0), DungeonPiece.getOpenSide(piece, 1), DungeonPiece.getOpenSide(piece, 2)));
			break;
		}
	}

	public void processAddition(Position2D[] additions, Position2D start, Position2D end, Position2D one, Random rand) {
		this.buildConnection(rand.nextBoolean() ? start : end, one);
		this.buildConnection(one, additions[rand.nextInt(additions.length)]);
	}

	public Position2D getLargeRoomPos(Position2D pos) {
		int x = pos.x;
		int z = pos.z;
		if (x < 15 && z < 15 && get(x + 1, z) == null && get(x + 1, z + 1) == null && get(x, z + 1) == null)
			return pos;
		if (x < 15 && z > 0 && get(x + 1, z) == null && get(x + 1, z - 1) == null && get(x, z - 1) == null)
			return new Position2D(x, z - 1);
		if (x > 0 && z < 15 && get(x - 1, z) == null && get(x - 1, z + 1) == null && get(x, z + 1) == null)
			return new Position2D(x - 1, z);
		if (x > 0 && z > 0 && get(x - 1, z) == null && get(x - 1, z - 1) == null && get(x, z - 1) == null)
			return new Position2D(x - 1, z - 1);
		return null;
	}

	/**
	 * Test function: builds a dungeon layer in miniature form with wool for testing
	 * purposes (Size 16x1x16 Blocks)
	 */
	public void testBuildToWorld(World world, BlockPos start) {
		int posX = start.getX();
		int posY = start.getY();
		int posZ = start.getZ();
		for (int x = 0; x < width; x++) {
			for (int z = 0; z < length; z++) {
				world.setBlockState(new BlockPos(posX + x, posY, posZ + z),
						segments[x][z] == null ? Blocks.AIR.getDefaultState()
								: segments[x][z] instanceof DungeonPieces.StairsBot ? Blocks.GREEN_WOOL.getDefaultState()
										: segments[x][z] instanceof DungeonPieces.StairsTop ? Blocks.RED_WOOL.getDefaultState()
												: segments[x][z] instanceof DungeonPieces.Room ? Blocks.BLUE_WOOL.getDefaultState() : Blocks.WHITE_WOOL.getDefaultState());
			}
		}
	}

	public boolean isInitialized() {
		return this.segments != null;
	}

	public DungeonPiece get(int x, int z) {
		return segments[x][z];
	}

}
