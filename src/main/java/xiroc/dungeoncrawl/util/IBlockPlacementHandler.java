package xiroc.dungeoncrawl.util;

/*
 * DungeonCrawl (C) 2019 - 2020 XYROC (XIROC1337), All Rights Reserved
 */

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IWorld;
import xiroc.dungeoncrawl.dungeon.block.*;
import xiroc.dungeoncrawl.dungeon.treasure.Treasure;

import java.util.HashMap;
import java.util.Random;

public interface IBlockPlacementHandler {

    IBlockPlacementHandler CHEST = new Chest(), TRAPPED_CHEST = new Chest.TrappedChest(), SPAWNER = new Spawner();

    HashMap<Block, IBlockPlacementHandler> PLACEMENT_HANDLERS = new HashMap<Block, IBlockPlacementHandler>();

    IBlockPlacementHandler DEFAULT = (world, state, pos, rand, treasureType, theme, lootLevel) -> {
        world.setBlockState(pos, state, 2);
    };

    static void load() {
        PLACEMENT_HANDLERS.put(Blocks.WATER, new Water());
        PLACEMENT_HANDLERS.put(Blocks.CHEST, CHEST);
        PLACEMENT_HANDLERS.put(Blocks.TRAPPED_CHEST, TRAPPED_CHEST);
        PLACEMENT_HANDLERS.put(Blocks.BARREL, CHEST);
        PLACEMENT_HANDLERS.put(Blocks.FURNACE, new Furnace());
        PLACEMENT_HANDLERS.put(Blocks.SMOKER, new Furnace.Smoker());
        PLACEMENT_HANDLERS.put(Blocks.SPAWNER, SPAWNER);
        PLACEMENT_HANDLERS.put(Blocks.DISPENSER, new Dispenser());
        PLACEMENT_HANDLERS.put(Blocks.FARMLAND, new Plants.Farmland());
        PLACEMENT_HANDLERS.put(Blocks.FLOWER_POT, new Plants.FlowerPot());
        PLACEMENT_HANDLERS.put(Blocks.PODZOL, new Plants.Podzol());
    }

    void placeBlock(IWorld world, BlockState state, BlockPos pos, Random rand,
                    Treasure.Type treasureType, int theme, int lootLevel);

    static IBlockPlacementHandler getHandler(Block block) {
        return PLACEMENT_HANDLERS.getOrDefault(block, DEFAULT);
    }

}
