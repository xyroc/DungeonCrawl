package xiroc.dungeoncrawl.part.block;

import java.util.Random;

import net.minecraft.block.BlockState;
import net.minecraft.tileentity.LockableLootTileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IWorld;
import xiroc.dungeoncrawl.util.IBlockPlacementHandler;

public class Barrel implements IBlockPlacementHandler {

	@Override
	public void setupBlock(IWorld world, BlockState state, BlockPos pos, Random rand, int lootLevel) {
		world.setBlockState(pos, state, 2);
		LockableLootTileEntity.setLootTable(world, world.getRandom(), pos, Chest.getLootTable(lootLevel));
	}

}
