package xiroc.dungeoncrawl.dungeon.segment;

public class DungeonSegmentModelTripWireBlock extends DungeonSegmentModelBlock {

	boolean north, east, south, west;

	public DungeonSegmentModelTripWireBlock() {
		super(DungeonSegmentModelBlockType.TRIPWIRE);
	}

}
