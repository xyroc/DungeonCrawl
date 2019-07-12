package xiroc.dungeoncrawl.part.block;

import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IWorld;

public class Chest {
	
	public static void setupChest(IWorld world, BlockState state, BlockPos pos, int lootLevel) {
		world.setBlockState(pos, state, 0);
		// TODO loot
	}

}
