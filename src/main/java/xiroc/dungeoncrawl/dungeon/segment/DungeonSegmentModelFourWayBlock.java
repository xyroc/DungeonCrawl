package xiroc.dungeoncrawl.dungeon.segment;

import net.minecraft.util.Direction;

public class DungeonSegmentModelFourWayBlock extends DungeonSegmentModelBlock {

	public boolean north, east, south, west, waterlogged;

	public DungeonSegmentModelFourWayBlock(DungeonSegmentModelBlockType type, boolean north, boolean east,
			boolean south, boolean west, boolean waterlogged) {
		super(type, Direction.NORTH, false);
		this.north = north;
		this.east = east;
		this.south = south;
		this.west = west;
		this.waterlogged = waterlogged;
	}

}
