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

package xiroc.dungeoncrawl.dungeon.treasure;

import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.IWorld;
import xiroc.dungeoncrawl.util.ItemProcessor;

import java.util.Random;

public class TreasureItem {

    public ResourceLocation item;
    public String resourceName;

    private ItemProcessor<IWorld, Random, Integer> itemProcessor;

    public TreasureItem(String resourceName) {
        this.resourceName = resourceName;
    }

    public TreasureItem(String resourceName, ItemProcessor<IWorld, Random, Integer> itemProcessor) {
        this.resourceName = resourceName;
        this.itemProcessor = itemProcessor;
    }

    public TreasureItem setProcessor(ItemProcessor<IWorld, Random, Integer> itemProcessor) {
        this.itemProcessor = itemProcessor;
        return this;
    }

    public ItemStack createItem(IWorld world, Random rand, int lootLevel) {
        if (itemProcessor != null)
            return itemProcessor.generate(world, rand,lootLevel);
        return ItemStack.EMPTY;
    }

}
