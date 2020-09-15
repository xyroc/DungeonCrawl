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
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.server.ServerWorld;
import xiroc.dungeoncrawl.util.ItemProcessor;

import java.util.Random;

public class TreasureItem {

    public ResourceLocation item;
    public String resourceName;

    public ItemEnchantment[] enchantments;

    public CompoundNBT nbt;

    private ItemProcessor<ServerWorld, Random, Integer, Integer> itemProcessor;

    public TreasureItem(String resourceName) {
        this(resourceName, null, null);
    }

    public TreasureItem(String resourceName, CompoundNBT nbt,
                        ItemEnchantment[] enchantments) {
        this.resourceName = resourceName;
        this.nbt = nbt;
        this.enchantments = enchantments;
    }

    public TreasureItem(String resourceName, CompoundNBT nbt,
                        ItemEnchantment[] enchantments, ItemProcessor<ServerWorld, Random, Integer, Integer> itemProcessor) {
        this(resourceName, nbt, enchantments);
        this.itemProcessor = itemProcessor;
    }

    public TreasureItem withProcessor(ItemProcessor<ServerWorld, Random, Integer, Integer> itemProcessor) {
        return new TreasureItem(this.resourceName, this.nbt, this.enchantments,
                itemProcessor);
    }


    public ItemStack generate(ServerWorld world, Random rand, int theme, int lootLevel) {
        if (itemProcessor != null)
            return itemProcessor.generate(world, rand, theme, lootLevel);
        return ItemStack.EMPTY;
    }


    public static class ItemEnchantment {

        ResourceLocation id;
        int level;

        public ItemEnchantment(ResourceLocation id, int level) {
            this.id = id;
            this.level = level;
        }

    }

}
