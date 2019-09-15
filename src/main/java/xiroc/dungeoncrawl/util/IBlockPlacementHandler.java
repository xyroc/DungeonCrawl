package xiroc.dungeoncrawl.util;

/*
 * DungeonCrawl (C) 2019 XYROC (XIROC1337), All Rights Reserved 
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
import xiroc.dungeoncrawl.part.block.Spawner;

public interface IBlockPlacementHandler {

	public static final HashMap<Block, IBlockPlacementHandler> HANDLERS = new HashMap<Block, IBlockPlacementHandler>();

	public static final IBlockPlacementHandler DEFAULT = (world, state, pos, rand, treasureType, theme, lootLevel) -> {
		world.setBlockState(pos, state, 2);
	};

	public static void load() {
		HANDLERS.put(Blocks.BARREL, new Chest.Barrel());
		HANDLERS.put(Blocks.CHEST, new Chest());
		HANDLERS.put(Blocks.FURNACE, new Furnace());
		HANDLERS.put(Blocks.SMOKER, new Furnace.Smoker());
		HANDLERS.put(Blocks.SPAWNER, new Spawner());
		HANDLERS.put(Blocks.DISPENSER, new Dispenser());
	}

	public abstract void setupBlock(IWorld world, BlockState state, BlockPos pos, Random rand,
			Treasure.Type treasureType, int theme, int lootLevel);

	public static IBlockPlacementHandler getHandler(Block block) {
		return HANDLERS.getOrDefault(block, DEFAULT);
	}

}
