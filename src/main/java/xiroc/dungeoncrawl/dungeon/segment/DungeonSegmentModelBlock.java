package xiroc.dungeoncrawl.dungeon.segment;

import net.minecraft.util.Direction;

public class DungeonSegmentModelBlock implements ISegmentBlock {


	// ChestBlock TrapDoorBlock
	
	public DungeonSegmentModelBlockType type;
	public Direction facing;
	public boolean upsideDown;

	public DungeonSegmentModelBlock(DungeonSegmentModelBlockType type, Direction facing, boolean upsideDown) {
		this.type = type;
		this.facing = facing;
		this.upsideDown = upsideDown;
	}

}
