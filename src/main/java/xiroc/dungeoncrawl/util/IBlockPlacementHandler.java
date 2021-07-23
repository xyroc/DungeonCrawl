/*
        Dungeon Crawl, a procedural dungeon generator for Minecraft 1.14 and later.
        Copyright (C) 2020

        This program is free software: you can redistribute it and/or modify
        it under the terms of the GNU General Public License as published by
        the Free Software Foundation, either version 3 of the License, or
        (at your option) any later version.

        This program is distributed in the hope that it will be useful,
        but WITHOUT ANY WARRANTY; without even the implied warranty of
        MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
        GNU General Public License for more details.

        You should have received a copy of the GNU General Public License
        along with this program.  If not, see <https://www.gnu.org/licenses/>.
*/

package xiroc.dungeoncrawl.util;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.FallingBlock;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IWorld;
import xiroc.dungeoncrawl.config.Config;
import xiroc.dungeoncrawl.dungeon.PlacementContext;
import xiroc.dungeoncrawl.dungeon.block.Furnace;
import xiroc.dungeoncrawl.dungeon.block.Plants;
import xiroc.dungeoncrawl.dungeon.block.Spawner;
import xiroc.dungeoncrawl.theme.Theme;

import java.util.Hashtable;
import java.util.Random;

public interface IBlockPlacementHandler {

    IBlockPlacementHandler SPAWNER = new Spawner();

    Hashtable<Block, IBlockPlacementHandler> PLACEMENT_HANDLERS = new Hashtable<>();

    IBlockPlacementHandler DEFAULT = (world, state, pos, rand, context, theme, subTheme, lootLevel) -> {
        if (Config.TICK_FALLING_BLOCKS.get() && state.getBlock() instanceof FallingBlock) {
            world.getChunk(pos).getBlocksToBeTicked().scheduleTick(pos, state.getBlock(), 1);
        }
        world.setBlockState(pos, state, 2);
    };

    static void init() {
//        PLACEMENT_HANDLERS.put(Blocks.CHEST, CHEST);
//        PLACEMENT_HANDLERS.put(Blocks.TRAPPED_CHEST, TRAPPED_CHEST);
//        PLACEMENT_HANDLERS.put(Blocks.BARREL, CHEST);
        PLACEMENT_HANDLERS.put(Blocks.FURNACE, new Furnace());
        PLACEMENT_HANDLERS.put(Blocks.SMOKER, new Furnace.Smoker());
        PLACEMENT_HANDLERS.put(Blocks.SPAWNER, SPAWNER);
//        PLACEMENT_HANDLERS.put(Blocks.DISPENSER, new Dispenser());
        PLACEMENT_HANDLERS.put(Blocks.FARMLAND, new Plants.Farmland());
        PLACEMENT_HANDLERS.put(Blocks.FLOWER_POT, new Plants.FlowerPot());
        PLACEMENT_HANDLERS.put(Blocks.PODZOL, new Plants.Podzol());
    }

    void place(IWorld world, BlockState state, BlockPos pos, Random rand,
               PlacementContext context, Theme theme, Theme.SecondaryTheme secondaryTheme, int lootLevel);

    static IBlockPlacementHandler getHandler(Block block) {
        return PLACEMENT_HANDLERS.getOrDefault(block, DEFAULT);
    }

}
