package xiroc.dungeoncrawl.part.block;

/*
 * DungeonCrawl (C) 2019 XYROC (XIROC1337), All Rights Reserved 
 */

import java.util.Random;

import net.minecraft.block.BlockState;
import net.minecraft.tileentity.LockableLootTileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IWorld;
import xiroc.dungeoncrawl.util.IBlockPlacementHandler;

public class Barrel implements IBlockPlacementHandler {

	@Override
	public void setupBlock(IWorld world, BlockState state, BlockPos pos, Random rand, int theme, int lootLevel) {
		world.setBlockState(pos, state, 2);
		if (lootLevel < 1 || rand.nextDouble() < 0.25) {
			LockableLootTileEntity tile = (LockableLootTileEntity) world.getTileEntity(pos);
			Chest.getTreasureLootTable(theme, lootLevel).fillInventory(tile, rand, theme, lootLevel);
		} else
			LockableLootTileEntity.setLootTable(world, world.getRandom(), pos, Chest.getLootTable(theme, lootLevel));;
	}

}
