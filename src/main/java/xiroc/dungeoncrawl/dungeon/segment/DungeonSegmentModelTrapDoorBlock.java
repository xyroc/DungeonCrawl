package xiroc.dungeoncrawl.dungeon.segment;

import net.minecraft.state.properties.Half;
import net.minecraft.util.Direction;

public class DungeonSegmentModelTrapDoorBlock extends DungeonSegmentModelBlock {
	
	public boolean open;
	public Half half;

	public DungeonSegmentModelTrapDoorBlock(DungeonSegmentModelBlockType type, Direction facing, boolean open, Half half, boolean upsideDown) {
		super(type, facing, upsideDown);
		this.open = open;
		this.half = half;
	}
	
	

}
