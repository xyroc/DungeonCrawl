package xiroc.dungeoncrawl.dungeon.segment;

public class DungeonSegmentLarge extends DungeonSegment {

	// north1, north2, east1, east2, south1, south2, west1, west2
	public boolean[] sides;

	public DungeonSegmentLarge(DungeonSegmentType type) {
		super(type);
		this.sides = new boolean[8];
	}

	public void openSide(int side) {
		if (this.sides[side])
			return;
		this.sides[side] = true;
		connectedSegments++;
	}

	public boolean[] getSides() {
		return this.sides;
	}

}
