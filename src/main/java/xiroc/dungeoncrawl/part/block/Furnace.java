package xiroc.dungeoncrawl.part.block;

/*
 * DungeonCrawl (C) 2019 XYROC (XIROC1337), All Rights Reserved 
 */

import java.util.Random;

import net.minecraft.block.BlockState;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.tileentity.FurnaceTileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IWorld;
import net.minecraft.world.storage.loot.RandomValueRange;
import xiroc.dungeoncrawl.util.IBlockPlacementHandler;

public class Furnace implements IBlockPlacementHandler {

	public static final RandomValueRange COAL_AMOUNT = new RandomValueRange(1, 16);

	@Override
	public void setupBlock(IWorld world, BlockState state, BlockPos pos, Random rand, int theme, int lootLevel) {
		world.setBlockState(pos, state, 2);
		FurnaceTileEntity tile = (FurnaceTileEntity) world.getTileEntity(pos);
		tile.setInventorySlotContents(1, new ItemStack(Items.COAL, COAL_AMOUNT.generateInt(rand)));
	}

}
