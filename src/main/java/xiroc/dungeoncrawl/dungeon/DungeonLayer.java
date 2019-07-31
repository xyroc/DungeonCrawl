package xiroc.dungeoncrawl.dungeon;

import java.util.Random;

import net.minecraft.block.Blocks;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import xiroc.dungeoncrawl.DungeonCrawl;
import xiroc.dungeoncrawl.dungeon.DungeonPieces.DungeonPiece;
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

	public void buildMap(Random rand) {
		this.segments = new DungeonPiece[this.width][this.length];
		DungeonLayerMap map = new DungeonLayerMap(this.width, this.length);
		Position2D start = map.getRandomFreePosition(rand);
		Position2D end = map.getRandomFreePosition(rand);
		this.start = start;
		this.end = end;
		this.segments[start.x][start.z] = new DungeonPieces.StairsBot(null, DungeonPieces.DEFAULT_NBT);
		this.segments[end.x][end.z] = new DungeonPieces.StairsTop(null, DungeonPieces.DEFAULT_NBT);
		this.buildConnection(start, end);
		this.extend(map, start, end, rand);
	}

	public void buildMap(Random rand, Position2D start, boolean noEnd) {
		this.segments = new DungeonPiece[this.width][this.length];
		DungeonLayerMap map = new DungeonLayerMap(this.width, this.length);
		Position2D end = map.getRandomFreePosition(rand);
//		DungeonCrawl.LOGGER.info("Start mark : " + map.markPositionAsOccupied(start));
		this.start = start;
		this.end = end;
		// DungeonCrawl.LOGGER.debug("Start is at (" + start.x + "/" + start.z + ") and
		// End is at (" + end.x + "/" + end.z + ")");
		this.segments[start.x][start.z] = new DungeonPieces.StairsBot(null, DungeonPieces.DEFAULT_NBT);
		this.segments[end.x][end.z] = new DungeonPieces.StairsTop(null, DungeonPieces.DEFAULT_NBT);
		this.buildConnection(start, end);
		this.extend(map, start, end, rand);
	}

	public void extend(DungeonLayerMap map, Position2D start, Position2D end, Random rand) {
		int additionalFeatures = 5 + rand.nextInt(6);
		Position2D[] additions = new Position2D[additionalFeatures];
		for (int i = 0; i < additionalFeatures; i++) {
			additions[i] = map.getRandomFreePosition(rand);
			DungeonPiece room = new DungeonPieces.Room(null, DungeonPieces.DEFAULT_NBT);
			room.setPosition(additions[i].x, additions[i].z);
			if (this.segments[additions[i].x][additions[i].z] != null) {
				DungeonCrawl.LOGGER.info("Placing a room into a piece at (" + additions[i].x + " / " + additions[i].z + " ) " + " ( " + this.segments[additions[i].x][additions[i].z] + " )");
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
					this.segments[x - 1][startZ].openSide((x - 1) == endX ? startZ < endZ ? Direction.SOUTH : Direction.NORTH : Direction.WEST); // ?
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
						this.segments[endX][z - 1].openSide((z - 1) == endZ ? Direction.WEST : Direction.NORTH); // changed
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
					this.segments[x + 1][startZ].openSide((x + 1) == endX ? startZ < endZ ? Direction.SOUTH : Direction.NORTH : Direction.EAST); // ?
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
						this.segments[endX][z - 1].openSide((z - 1) == endZ ? Direction.EAST : Direction.NORTH); // changed
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
						this.segments[endX][z + 1].openSide((z + 1) == endZ ? Direction.EAST : Direction.SOUTH); // changed
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
						// this.segments[endX][z - 1].openSide((z - 1) == endZ ? Direction.WEST :
						// Direction.SOUTH);
						this.segments[endX][z - 1].openSide(Direction.SOUTH);
						this.rotatePiece(this.segments[endX][z - 1]);
						continue;
					}
					DungeonPiece corridor = new DungeonPieces.Corridor(null, DungeonPieces.DEFAULT_NBT);
					corridor.setPosition(endX, z - 1);
					// corridor.setRotation((z - 1) == endZ ?
					// RotationHelper.getRotationFromCW90DoubleFacing(Direction.NORTH,
					// Direction.WEST) : RotationHelper.getRotationFromFacing(Direction.NORTH));
					corridor.setRotation(RotationHelper.getRotationFromFacing(Direction.NORTH));
					corridor.openSide(Direction.SOUTH);
					// corridor.openSide((z - 1) == endZ ? Direction.WEST : Direction.NORTH);
					corridor.openSide(Direction.NORTH);
					this.segments[endX][z - 1] = corridor;
				}
			} else if (startZ < endZ) {
				this.segments[startX][startZ].openSide(Direction.SOUTH);
				this.segments[endX][endZ].openSide(Direction.NORTH);
				for (int z = startZ; z < endZ - 1; z++) {
					if (this.segments[endX][z + 1] != null) {
						// this.segments[endX][z + 1].openSide((z + 1) == endZ ? Direction.WEST :
						// Direction.SOUTH);
						this.segments[endX][z + 1].openSide(Direction.SOUTH);
						this.segments[endX][z + 1].openSide(Direction.NORTH);
						this.rotatePiece(this.segments[endX][z + 1]);
						continue;
					}
					this.segments[endX][endZ].openSide(Direction.NORTH);
					DungeonPiece corridor = new DungeonPieces.Corridor(null, DungeonPieces.DEFAULT_NBT);
					corridor.setPosition(endX, z + 1);
					// corridor.setRotation((z + 1) == endZ ?
					// RotationHelper.getRotationFromCW90DoubleFacing(Direction.SOUTH,
					// Direction.WEST) : RotationHelper.getRotationFromFacing(Direction.SOUTH));
					corridor.setRotation(RotationHelper.getRotationFromFacing(Direction.SOUTH));
					// corridor.openSide((z + 1) == endZ ? Direction.WEST : Direction.SOUTH);
					corridor.openSide(Direction.SOUTH);
					corridor.openSide(Direction.NORTH);
					this.segments[endX][z + 1] = corridor;
				}
			} else
				DungeonCrawl.LOGGER.warn("Tried to build a connection between two positions but they were the same. ( " + startX + " / " + startZ + " ) -> ( " + endX + "/" + endZ + " )");
		}
	}

	// TODO not approved yet
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

	/**
	 * Test function
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
