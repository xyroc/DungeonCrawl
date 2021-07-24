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

import net.minecraft.core.BlockPos;
import net.minecraft.util.Mth;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.FurnaceBlockEntity;
import net.minecraft.world.level.block.entity.SmokerBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import xiroc.dungeoncrawl.DungeonCrawl;
import xiroc.dungeoncrawl.dungeon.PlacementContext;
import xiroc.dungeoncrawl.theme.Theme;
import xiroc.dungeoncrawl.util.IBlockPlacementHandler;

import java.util.Random;

public class Furnace implements IBlockPlacementHandler {

    private static final Item[] FOOD = {Items.COOKED_BEEF, Items.COOKED_CHICKEN, Items.COOKED_MUTTON,
            Items.COOKED_COD, Items.COOKED_PORKCHOP, Items.COOKED_RABBIT, Items.COOKED_SALMON, Items.BAKED_POTATO};

    @Override
    public void place(LevelAccessor world, BlockState state, BlockPos pos, Random rand, PlacementContext context,
                      Theme theme, Theme.SecondaryTheme secondaryTheme, int lootLevel) {
        world.setBlock(pos, state, 2);
        BlockEntity tile = world.getBlockEntity(pos);
        if (tile instanceof FurnaceBlockEntity furnace) {
            furnace.setItem(1, new ItemStack(Items.COAL, coalAmount(rand)));
        } else {
            DungeonCrawl.LOGGER.warn("Failed to fetch a furnace entity at {}", pos.toString());
        }
    }

    private static int coalAmount(Random rand) {
        return Mth.nextInt(rand, 2, 8);
    }

    public static class Smoker implements IBlockPlacementHandler {

        @Override
        public void place(LevelAccessor world, BlockState state, BlockPos pos, Random rand, PlacementContext context,
                          Theme theme, Theme.SecondaryTheme secondaryTheme, int lootLevel) {
            world.setBlock(pos, state, 2);
            BlockEntity tile = world.getBlockEntity(pos);
            if (tile instanceof SmokerBlockEntity smoker) {
                smoker.setItem(1, new ItemStack(Items.CHARCOAL, coalAmount(rand)));
                smoker.setItem(2, new ItemStack(FOOD[rand.nextInt(FOOD.length)], 1 + rand.nextInt(16)));
            } else {
                DungeonCrawl.LOGGER.warn("Failed to fetch a smoker entity at {}", pos.toString());
            }
        }
    }

}
