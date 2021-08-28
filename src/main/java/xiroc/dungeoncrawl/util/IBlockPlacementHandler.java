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
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.FallingBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.tileentity.LecternTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IWorld;
import xiroc.dungeoncrawl.DungeonCrawl;
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

    IBlockPlacementHandler LECTERN = (world, state, pos, rand, theme, secondaryTheme, lootLevel, worldGen) -> {
        world.setBlock(pos, state, 2);
        TileEntity tile = world.getBlockEntity(pos);
        DungeonCrawl.LOGGER.info("Lectern at {}, tile: {}", pos, tile);
        if (tile instanceof LecternTileEntity) {
            ((LecternTileEntity) tile).setBook(new ItemStack(Items.BOOK));
        }
    };

    ImmutableMap<Block, IBlockPlacementHandler> PLACEMENT_HANDLERS = new ImmutableMap.Builder<Block, IBlockPlacementHandler>()
            .put(Blocks.FURNACE, new Furnace())
            .put(Blocks.SMOKER, new Furnace.Smoker())
            .put(Blocks.SPAWNER, SPAWNER)
            .put(Blocks.FARMLAND, new Plants.Farmland())
            .put(Blocks.FLOWER_POT, new Plants.FlowerPot())
            .put(Blocks.PODZOL, new Plants.Podzol())
            .put(Blocks.LECTERN, LECTERN)
            .build();

    void place(IWorld world, BlockState state, BlockPos pos, Random rand, Theme theme, Theme.SecondaryTheme secondaryTheme, int lootLevel, boolean worldGen);

    static IBlockPlacementHandler getHandler(Block block) {
        return PLACEMENT_HANDLERS.getOrDefault(block, DEFAULT);
    }

}
