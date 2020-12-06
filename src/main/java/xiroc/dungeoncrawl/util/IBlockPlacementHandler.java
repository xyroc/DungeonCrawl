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
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IWorld;
import xiroc.dungeoncrawl.dungeon.block.*;
import xiroc.dungeoncrawl.dungeon.treasure.Treasure;

import java.util.HashMap;
import java.util.Random;

public interface IBlockPlacementHandler {

    IBlockPlacementHandler CHEST = new Chest(), TRAPPED_CHEST = new Chest.TrappedChest(), SPAWNER = new Spawner();

    HashMap<Block, IBlockPlacementHandler> PLACEMENT_HANDLERS = new HashMap<>();

    IBlockPlacementHandler DEFAULT = (world, state, pos, rand, treasureType, theme, lootLevel) -> {
        world.setBlockState(pos, state, 2);
    };

    static void init() {
        // A temporary fix that prevents tripwire hooks from getting placed next to chunk borders.
        PLACEMENT_HANDLERS.put(Blocks.TRIPWIRE_HOOK, ((world, state, pos, rand, treasureType, theme, lootLevel) -> {
            int x = pos.getX() & 15, z = pos.getZ() & 15;
            if (x == 0 || z == 0 || x == 15 || z == 15) {
                world.setBlockState(pos, state, 2);
            } else {
                world.setBlockState(pos, DungeonBlocks.CAVE_AIR, 0);
            }
        }));

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
