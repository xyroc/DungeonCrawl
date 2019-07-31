package xiroc.dungeoncrawl.dungeon.segment;

import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import xiroc.dungeoncrawl.util.IBlockPlacementHandler;

public class DungeonSegmentModel {

	public int width;
	public int height;
	public int length;
	public DungeonSegmentModelBlock[][][] model;
	public DungeonSegmentModelFourWayBlock[] fourWayBlocks;
	public DungeonSegmentModelTrapDoorBlock[] trapDoors;

	public DungeonSegmentModel() {
		this.model = new DungeonSegmentModelBlock[8][8][8];
		this.fourWayBlocks = new DungeonSegmentModelFourWayBlock[64];
		this.trapDoors = new DungeonSegmentModelTrapDoorBlock[16];
		this.width = this.height = this.length = 8;
	}

	public DungeonSegmentModel(DungeonSegmentModelBlock[][][] model) {
		this.model = model;
		this.fourWayBlocks = new DungeonSegmentModelFourWayBlock[64];
		this.trapDoors = new DungeonSegmentModelTrapDoorBlock[16];
		this.width = model.length;
		this.height = model[0].length;
		this.length = model[0][0].length;
	}

	public DungeonSegmentModel(DungeonSegmentModelBlock[][][] model, DungeonSegmentModelTrapDoorBlock[] trapDoors, DungeonSegmentModelFourWayBlock[] fourWayBlocks) {
		this.model = model;
		this.fourWayBlocks = fourWayBlocks;
		this.trapDoors = trapDoors;
		this.width = model.length;
		this.height = model[0].length;
		this.length = model[0][0].length;
	}

	public static void setupBlockState(BlockState state, World world, BlockPos pos) {
		if (state == null)
			return;
		IBlockPlacementHandler.getHandler(state.getBlock()).setupBlock(world, state, pos, world.getRandom(), 0); // lootLevel
	}

	public BlockState[][][] transform() {
		return null;
	}

}
