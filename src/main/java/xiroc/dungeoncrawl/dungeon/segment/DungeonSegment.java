package xiroc.dungeoncrawl.dungeon.segment;

import net.minecraft.util.Direction;
import xiroc.dungeoncrawl.DungeonCrawl;

public class DungeonSegment {

	public static final int SIZE = 8;

	public DungeonSegmentType type;
	public Direction direction;
	public int connectedSegments, posX, posZ;
	public boolean[] sides; // north east south west up down

	public DungeonSegment(DungeonSegmentType type) {
		this.type = type;
		this.direction = Direction.NORTH;
		this.setPosition(-1, -1);
		this.sides = new boolean[6];
		this.connectedSegments = 0;
	}

	public DungeonSegment(DungeonSegmentType type, boolean[] sides, int posX, int posY, int connectedSegments) {
		this.type = type;
		this.direction = Direction.NORTH;
		this.setPosition(posX, posY);
		this.sides = sides;
		this.connectedSegments = connectedSegments;
	}

	public void setPosition(int x, int z) {
		this.posX = x;
		this.posZ = z;
	}

	public void openSide(Direction side) {
		switch (side) {
		case NORTH:
			if (sides[0])
				return;
			sides[0] = true;
			connectedSegments++;
			return;
		case EAST:
			if (sides[1])
				return;
			sides[1] = true;
			connectedSegments++;
			return;
		case SOUTH:
			if (sides[2])
				return;
			sides[2] = true;
			connectedSegments++;
			return;
		case WEST:
			if (sides[3])
				return;
			sides[3] = true;
			connectedSegments++;
			return;
		case UP:
			if (sides[4])
				return;
			sides[4] = true;
			connectedSegments++;
			return;
		case DOWN:
			if (sides[5])
				return;
			sides[5] = true;
			connectedSegments++;
			return;
		default:
			DungeonCrawl.LOGGER.warn("Failed to open a segment side: Unknown side " + side);
		}
	}

	public void setDirection(Direction direction) {
		this.direction = direction;
	}

	public boolean hasValidPosition() {
		return posX > -1 && posZ > -1;
	}

}
