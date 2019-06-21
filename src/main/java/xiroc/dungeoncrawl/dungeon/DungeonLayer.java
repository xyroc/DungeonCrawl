package xiroc.dungeoncrawl.dungeon;

import java.util.Random;

import net.minecraft.block.Blocks;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import xiroc.dungeoncrawl.DungeonCrawl;
import xiroc.dungeoncrawl.dungeon.segment.DungeonSegment;
import xiroc.dungeoncrawl.dungeon.segment.DungeonSegmentType;
import xiroc.dungeoncrawl.util.Position2D;

public class DungeonLayer {

	/*
	 * NORTH|-Z EAST|+X SOUTH|+Z WEST|-X
	 */

	public DungeonLayerType type;
	public DungeonSegment[][] segments;

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
		this.segments = new DungeonSegment[this.width][this.length];
		DungeonLayerMap map = new DungeonLayerMap(this.width, this.length);
		Position2D start = map.getRandomFreePosition(rand);
		Position2D end = map.getRandomFreePosition(rand);
		DungeonCrawl.LOGGER.debug("Start is at (" + start.x + "/" + start.z + ") and End is at (" + end.x + "/" + end.z + ")");
		this.segments[start.x][start.z] = new DungeonSegment(this, DungeonSegmentType.START);
		this.segments[end.x][end.z] = new DungeonSegment(this, DungeonSegmentType.END);
		DungeonCrawl.LOGGER.info("Building connection from start to end...");
		this.buildConnection(start, end);
	}

	public void buildConnection(Position2D start, Position2D end) {
		int startX = start.x;
		int startZ = start.z;
		int endX = end.x;
		int endZ = end.z;
		if (startX > endX) {
			for (int x = startX; x > (startZ == endZ ? endX + 1 : endX); x--) {
				DungeonSegment corridor = new DungeonSegment(this, DungeonSegmentType.CORRIDOR);
				corridor.setPosition(x - 1, startZ);
				corridor.setDirection(Direction.WEST);
				corridor.openSide((x - 1) == endX ? Direction.NORTH : Direction.WEST);
				corridor.openSide(Direction.EAST);
				this.segments[x - 1][startZ] = corridor;
			}
			if (startZ > endZ) {
				for (int z = startZ; z > endZ + 1; z--) {
					DungeonSegment corridor = new DungeonSegment(this, DungeonSegmentType.CORRIDOR);
					corridor.setPosition(endX, z - 1);
					corridor.setDirection(Direction.NORTH);
					corridor.openSide(Direction.NORTH);
					corridor.openSide((z - 1) == endZ ? Direction.WEST : Direction.SOUTH);
					this.segments[endX][z - 1] = corridor;
				}
			} else if (startZ < endZ) {
				for (int z = startZ; z < endZ - 1; z++) {
					DungeonSegment corridor = new DungeonSegment(this, DungeonSegmentType.CORRIDOR);
					corridor.setPosition(endX, z + 1);
					corridor.setDirection(Direction.SOUTH);
					corridor.openSide((z + 1) == endZ ? Direction.WEST : Direction.SOUTH);
					corridor.openSide(Direction.NORTH);
					this.segments[endX][z + 1] = corridor;
				}
			}
		} else if (startX < endX) {
			for (int x = startX; x < (startZ == endZ ? endX - 1 : endX); x++) {
				DungeonSegment corridor = new DungeonSegment(this, DungeonSegmentType.CORRIDOR);
				corridor.setPosition(x + 1, startZ);
				corridor.setDirection(Direction.EAST);
				corridor.openSide((x + 1) == endX ? Direction.SOUTH : Direction.EAST);
				corridor.openSide(Direction.WEST);
				this.segments[x + 1][startZ] = corridor;
			}
			if (startZ > endZ) {
				for (int z = startZ; z > endZ + 1; z--) {
					DungeonSegment corridor = new DungeonSegment(this, DungeonSegmentType.CORRIDOR);
					corridor.setPosition(endX, z - 1);
					corridor.setDirection(Direction.NORTH);
					corridor.openSide(Direction.NORTH);
					corridor.openSide((z - 1) == endZ ? Direction.WEST : Direction.SOUTH);
					this.segments[endX][z - 1] = corridor;
				}
			} else if (startZ < endZ) {
				for (int z = startZ; z < endZ - 1; z++) {
					DungeonSegment corridor = new DungeonSegment(this, DungeonSegmentType.CORRIDOR);
					corridor.setPosition(endX, z + 1);
					corridor.setDirection(Direction.SOUTH);
					corridor.openSide((z + 1) == endZ ? Direction.WEST : Direction.SOUTH);
					corridor.openSide(Direction.NORTH);
					this.segments[endX][z + 1] = corridor;
				}
			}
		} else {
			if (startZ > endZ) {
				for (int z = startZ; z > endZ + 1; z--) {
					DungeonSegment corridor = new DungeonSegment(this, DungeonSegmentType.CORRIDOR);
					corridor.setPosition(endX, z - 1);
					corridor.setDirection(Direction.NORTH);
					corridor.openSide(Direction.NORTH);
					corridor.openSide((z - 1) == endZ ? Direction.WEST : Direction.SOUTH);
					this.segments[endX][z - 1] = corridor;
				}
			} else if (startZ < endZ) {
				for (int z = startZ; z < endZ - 1; z++) {
					DungeonSegment corridor = new DungeonSegment(this, DungeonSegmentType.CORRIDOR);
					corridor.setPosition(endX, z + 1);
					corridor.setDirection(Direction.SOUTH);
					corridor.openSide((z + 1) == endZ ? Direction.WEST : Direction.SOUTH);
					corridor.openSide(Direction.NORTH);
					this.segments[endX][z + 1] = corridor;
				}
			}
		}
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
				world.setBlockState(new BlockPos(posX + x, posY, posZ + z), segments[x][z] == null ? Blocks.AIR.getDefaultState()
						: segments[x][z].type == DungeonSegmentType.START ? Blocks.GREEN_WOOL.getDefaultState() : segments[x][z].type == DungeonSegmentType.END ? Blocks.RED_WOOL.getDefaultState() : Blocks.WHITE_WOOL.getDefaultState());
			}
		}
	}

	public boolean isInitialized() {
		return this.segments != null;
	}

	public DungeonSegment get(int x, int z) {
		return segments[x][z];
	}

}
