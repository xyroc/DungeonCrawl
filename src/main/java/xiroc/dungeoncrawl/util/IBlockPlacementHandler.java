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

import com.google.common.collect.ImmutableMap;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.FallingBlock;
import net.minecraft.world.level.block.state.BlockState;
import xiroc.dungeoncrawl.config.Config;
import xiroc.dungeoncrawl.dungeon.block.Furnace;
import xiroc.dungeoncrawl.dungeon.block.Plants;
import xiroc.dungeoncrawl.dungeon.block.Spawner;
import xiroc.dungeoncrawl.theme.Theme;

import java.util.Random;

public interface IBlockPlacementHandler {

    IBlockPlacementHandler SPAWNER = new Spawner();

    IBlockPlacementHandler DEFAULT = (world, state, pos, rand, theme, secondaryTheme, lootLevel, worldGen) -> {
        if (Config.TICK_FALLING_BLOCKS.get() && state.getBlock() instanceof FallingBlock) {
            world.getChunk(pos).getBlockTicks().scheduleTick(pos, state.getBlock(), 1);
        }
        world.setBlock(pos, state, 2);
    };

    ImmutableMap<Block, IBlockPlacementHandler> PLACEMENT_HANDLERS = new ImmutableMap.Builder<Block, IBlockPlacementHandler>()
            .put(Blocks.FURNACE, new Furnace())
            .put(Blocks.SMOKER, new Furnace.Smoker())
            .put(Blocks.SPAWNER, SPAWNER)
            .put(Blocks.FARMLAND, new Plants.Farmland())
            .put(Blocks.FLOWER_POT, new Plants.FlowerPot())
            .put(Blocks.PODZOL, new Plants.Podzol())
            .build();

    void place(LevelAccessor world, BlockState state, BlockPos pos, Random rand, Theme theme, Theme.SecondaryTheme secondaryTheme, int lootLevel, boolean worldGen);

    static IBlockPlacementHandler getHandler(Block block) {
        return PLACEMENT_HANDLERS.getOrDefault(block, DEFAULT);
    }

}
