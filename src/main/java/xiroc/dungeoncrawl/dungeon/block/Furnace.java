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

package xiroc.dungeoncrawl.dungeon.block;

import net.minecraft.block.BlockState;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.loot.RandomValueRange;
import net.minecraft.tileentity.FurnaceTileEntity;
import net.minecraft.tileentity.SmokerTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IWorld;
import xiroc.dungeoncrawl.DungeonCrawl;
import xiroc.dungeoncrawl.dungeon.PlacementContext;
import xiroc.dungeoncrawl.theme.Theme;
import xiroc.dungeoncrawl.util.IBlockPlacementHandler;

import java.util.Random;

public class Furnace implements IBlockPlacementHandler {

    private static final Item[] FOOD = {Items.COOKED_BEEF, Items.COOKED_CHICKEN, Items.COOKED_MUTTON,
            Items.COOKED_COD, Items.COOKED_PORKCHOP, Items.COOKED_RABBIT, Items.COOKED_SALMON, Items.BAKED_POTATO};

    public static final RandomValueRange COAL_AMOUNT = new RandomValueRange(4, 16);

    @Override
    public void place(IWorld world, BlockState state, BlockPos pos, Random rand, PlacementContext context
            , Theme theme, Theme.SecondaryTheme secondaryTheme, int lootLevel) {
        world.setBlockState(pos, state, 2);
        TileEntity tile = world.getTileEntity(pos);
        if (tile instanceof FurnaceTileEntity) {
            FurnaceTileEntity furnace = (FurnaceTileEntity) tile;
            furnace.setInventorySlotContents(1, new ItemStack(Items.COAL, COAL_AMOUNT.generateInt(rand)));
        } else {
            DungeonCrawl.LOGGER.warn("Failed to fetch a furnace entity at {}", pos.toString());
        }
    }

    public static class Smoker implements IBlockPlacementHandler {

        @Override
        public void place(IWorld world, BlockState state, BlockPos pos, Random rand, PlacementContext context,
                          Theme theme, Theme.SecondaryTheme secondaryTheme, int lootLevel) {
            world.setBlockState(pos, state, 2);
            TileEntity tile = world.getTileEntity(pos);
            if (tile instanceof SmokerTileEntity) {
                SmokerTileEntity smoker = (SmokerTileEntity) tile;
                smoker.setInventorySlotContents(1, new ItemStack(Items.CHARCOAL, COAL_AMOUNT.generateInt(rand)));
                smoker.setInventorySlotContents(2, new ItemStack(FOOD[rand.nextInt(FOOD.length)], 1 + rand.nextInt(16)));
            } else {
                DungeonCrawl.LOGGER.warn("Failed to fetch a smoker entity at {}", pos.toString());
            }
        }
    }

}
