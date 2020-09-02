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
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.tileentity.FurnaceTileEntity;
import net.minecraft.tileentity.SmokerTileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IWorld;
import net.minecraft.world.storage.loot.RandomValueRange;
import xiroc.dungeoncrawl.DungeonCrawl;
import xiroc.dungeoncrawl.dungeon.treasure.Treasure;
import xiroc.dungeoncrawl.dungeon.treasure.Treasure.Type;
import xiroc.dungeoncrawl.util.IBlockPlacementHandler;

import java.util.Random;

public class Furnace implements IBlockPlacementHandler {

    public static final RandomValueRange COAL_AMOUNT = new RandomValueRange(1, 16);

    @Override
    public void placeBlock(IWorld world, BlockState state, BlockPos pos, Random rand, Treasure.Type treasureType,
                           int theme, int lootLevel) {
        world.setBlockState(pos, state, 3);
        if (world.getTileEntity(pos) instanceof FurnaceTileEntity) {
            FurnaceTileEntity tile = (FurnaceTileEntity) world.getTileEntity(pos);
            tile.setInventorySlotContents(1, new ItemStack(Items.COAL, COAL_AMOUNT.generateInt(rand)));
        } else
            DungeonCrawl.LOGGER.warn("Failed to fetch a furnace entity at {}", pos.toString());

    }

    public static class Smoker implements IBlockPlacementHandler {

        @Override
        public void placeBlock(IWorld world, BlockState state, BlockPos pos, Random rand, Type treasureType, int theme,
                               int lootLevel) {
            world.setBlockState(pos, state, 3);
            if (world.getTileEntity(pos) instanceof SmokerTileEntity) {
                SmokerTileEntity tile = (SmokerTileEntity) world.getTileEntity(pos);
                tile.setInventorySlotContents(1, new ItemStack(Items.CHARCOAL, COAL_AMOUNT.generateInt(rand)));
//				tile.setInventorySlotContents(2, theme == 3 ? Kitchen.SMOKER_OCEAN.getItemStack((ServerWorld) world.getWorld(), rand, theme, lootLevel)
//				: Kitchen.SMOKER.getItemStack((ServerWorld) world.getWorld(), rand, theme, lootLevel));
            } else
                DungeonCrawl.LOGGER.warn("Failed to fetch a smoker entity at {}", pos.toString());

        }

    }

}
