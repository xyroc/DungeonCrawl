package xiroc.dungeoncrawl.part.block;

import java.util.Random;

import net.minecraft.block.BlockState;
import net.minecraft.tileentity.DispenserTileEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IWorld;
import xiroc.dungeoncrawl.DungeonCrawl;
import xiroc.dungeoncrawl.util.IBlockPlacementHandler;
import xiroc.dungeoncrawl.util.Loot;

public class Dispenser implements IBlockPlacementHandler {

	@Override
	public void setupBlock(IWorld world, BlockState state, BlockPos pos, Random rand, int lootLevel) {
		world.setBlockState(pos, state, 2);
		DispenserTileEntity dispenser = (DispenserTileEntity) world.getTileEntity(pos);
		dispenser.setLootTable(getLootTable(lootLevel), rand.nextLong());
		// dispenser.fillWithLoot(null);
	}

	public static ResourceLocation getLootTable(int lootLevel) {
		switch (lootLevel) {
		case 0:
			return Loot.DISPENSER_STAGE_1;
		case 1:
			return Loot.DISPENSER_STAGE_2;
		case 2:
			return Loot.DISPENSER_STAGE_3;
		default:
			DungeonCrawl.LOGGER.warn("Unknown Loot Level: " + lootLevel);
			return null;
		}
	}

}
