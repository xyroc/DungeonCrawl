package xiroc.dungeoncrawl.dungeon.segment;

import net.minecraft.util.Direction;
import xiroc.dungeoncrawl.DungeonCrawl;
import xiroc.dungeoncrawl.dungeon.DungeonLayer;

public class DungeonSegment {

	public DungeonLayer layer;
	public DungeonSegmentType type;
	public Direction direction;
	public int maxConnectedSegments, connectedSegments, posX, posZ;
	boolean[] sides; // north east south west up down

	public DungeonSegment(DungeonLayer layer, DungeonSegmentType type) {
		this.layer = layer;
		this.type = type;
		this.direction = Direction.NORTH;
		this.setPosition(-1, -1);
		this.sides = new boolean[6];
		for (int i = 0; i < sides.length; i++)
			sides[i] = false;
	}

	public void setPosition(int x, int z) {
		this.posX = x;
		this.posZ = z;
	}

	public void openSide(Direction side) {
		switch (side) {
		case NORTH:
			sides[0] = true;
			return;
		case EAST:
			sides[1] = true;
			return;
		case SOUTH:
			sides[2] = true;
			return;
		case WEST:
			sides[3] = true;
			return;
		case UP:
			sides[4] = true;
			return;
		case DOWN:
			sides[5] = true;
			return;
		default:
			DungeonCrawl.LOGGER.warn("Failed to open a segment side: Unknown side " + side);
		}
	}

	public void closeSide(Direction side) {
		switch (side) {
		case NORTH:
			sides[0] = false;
			return;
		case EAST:
			sides[1] = false;
			return;
		case SOUTH:
			sides[2] = false;
			return;
		case WEST:
			sides[3] = false;
			return;
		case UP:
			sides[4] = false;
			return;
		case DOWN:
			sides[5] = false;
			return;
		default:
			DungeonCrawl.LOGGER.warn("Failed to close a segment side: Unknown side " + side);
		}
	}

	/**
	 * 
	 * @param side the facing of the side you want to connect another segment to
	 */
	public boolean canConnectSegment(Direction side) {
		return connectedSegments >= maxConnectedSegments ? false : getNeighbourSegment(side) == null;
	}

	public DungeonSegment getNeighbourSegment(Direction side) {
		switch (side) {
		case NORTH:
			return layer.get(posX, posZ - 1);
		case EAST:
			return layer.get(posX + 1, posZ);
		case WEST:
			return layer.get(posX - 1, posZ);
		case SOUTH:
			return layer.get(posX, posZ + 1);
		default:
			return null;
		}
	}

	public void setDirection(Direction direction) {
		this.direction = direction;
	}

	public boolean hasValidPosition() {
		return posX > -1 && posZ > -1;
	}

}
