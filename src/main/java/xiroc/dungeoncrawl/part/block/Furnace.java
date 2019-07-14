package xiroc.dungeoncrawl.part.block;

import java.util.Random;

import net.minecraft.block.BlockState;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.tileentity.FurnaceTileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IWorld;
import xiroc.dungeoncrawl.util.IBlockPlacementHandler;

public class Furnace implements IBlockPlacementHandler {

	@Override
	public void setupBlock(IWorld world, BlockState state, BlockPos pos, Random rand, int lootLevel) {
		world.setBlockState(pos, state, 2);
		FurnaceTileEntity tile = (FurnaceTileEntity) world.getTileEntity(pos);
		tile.setInventorySlotContents(0, new ItemStack(Items.COAL, 1 + rand.nextInt(16)));
	}

}
