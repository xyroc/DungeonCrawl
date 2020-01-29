package xiroc.dungeoncrawl.util;

/*
 * DungeonCrawl (C) 2019 - 2020 XYROC (XIROC1337), All Rights Reserved 
 */

import java.util.HashMap;
import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IWorld;
import xiroc.dungeoncrawl.dungeon.treasure.Treasure;
import xiroc.dungeoncrawl.part.block.Chest;
import xiroc.dungeoncrawl.part.block.Dispenser;
import xiroc.dungeoncrawl.part.block.Furnace;
import xiroc.dungeoncrawl.part.block.Plants;
import xiroc.dungeoncrawl.part.block.Spawner;

public interface IBlockPlacementHandler {

	public static final HashMap<Block, IBlockPlacementHandler> PLACEMENT_HANDLERS = new HashMap<Block, IBlockPlacementHandler>();

	public static final IBlockPlacementHandler DEFAULT = (world, state, pos, rand, treasureType, theme, lootLevel) -> {
		world.setBlockState(pos, state, 2);
	};

	public static void load() {
		PLACEMENT_HANDLERS.put(Blocks.CHEST, new Chest());
		PLACEMENT_HANDLERS.put(Blocks.TRAPPED_CHEST, new Chest.TrappedChest());
		PLACEMENT_HANDLERS.put(Blocks.BARREL, new Chest());
		PLACEMENT_HANDLERS.put(Blocks.FURNACE, new Furnace());
		PLACEMENT_HANDLERS.put(Blocks.SMOKER, new Furnace.Smoker());
		PLACEMENT_HANDLERS.put(Blocks.SPAWNER, new Spawner());
		PLACEMENT_HANDLERS.put(Blocks.DISPENSER, new Dispenser());
		PLACEMENT_HANDLERS.put(Blocks.FARMLAND, new Plants.Farmland());
		PLACEMENT_HANDLERS.put(Blocks.FLOWER_POT, new Plants.FlowerPot());
	}

	public abstract void setupBlock(IWorld world, BlockState state, BlockPos pos, Random rand,
			Treasure.Type treasureType, int theme, int lootLevel);

	public static IBlockPlacementHandler getHandler(Block block) {
		return PLACEMENT_HANDLERS.getOrDefault(block, DEFAULT);
	}

}
