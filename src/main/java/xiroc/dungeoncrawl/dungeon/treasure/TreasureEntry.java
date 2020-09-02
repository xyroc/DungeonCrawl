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

import java.util.Random;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.IItemProvider;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.server.ServerWorld;
import net.minecraft.world.storage.loot.LootFunction;
import net.minecraftforge.registries.ForgeRegistries;
import xiroc.dungeoncrawl.util.ItemProcessor;

public class TreasureEntry {

	public ResourceLocation item;
	public String resourceName;

	public int min, max, weight;

	public ItemEnchantment[] enchantments;

	public CompoundNBT nbt;

	public LootFunction[] functions;

	private ItemProcessor<ServerWorld, Random, Integer, Integer> itemProcessor;

	public TreasureEntry(String resourceName, int weight) {
		this(resourceName, 1, 1, weight, null, null);
	}

	public TreasureEntry(String resourceName, int min, int max, int weight) {
		this(resourceName, min, max, weight, null, null);
	}

//	public TreasureEntry(String item, int min, int max, int weight) {
//		this(item, min, max, weight, null, null);
//	}

	public TreasureEntry(String resourceName, int min, int max, int weight, CompoundNBT nbt,
			ItemEnchantment[] enchantments) {
		this.resourceName = resourceName;
		this.min = min;
		this.max = max;
		this.weight = weight;
		this.nbt = nbt;
		this.enchantments = enchantments;
	}

	public TreasureEntry(String resourceName, int min, int max, int weight, CompoundNBT nbt,
			ItemEnchantment[] enchantments, ItemProcessor<ServerWorld, Random, Integer, Integer> itemProcessor) {
		this(resourceName, min, max, weight, nbt, enchantments);
		this.itemProcessor = itemProcessor;
	}

	public TreasureEntry withWeight(int weight) {
		return new TreasureEntry(this.resourceName, this.min, this.max, weight, this.nbt, this.enchantments,
				this.itemProcessor);
	}

	public TreasureEntry withEnchantments(ItemEnchantment[] enchantments) {
		return new TreasureEntry(this.resourceName, this.min, this.max, this.weight, this.nbt, enchantments,
				this.itemProcessor);
	}

	public TreasureEntry withProcessor(ItemProcessor<ServerWorld, Random, Integer, Integer> itemProcessor) {
		return new TreasureEntry(this.resourceName, this.min, this.max, this.weight, this.nbt, this.enchantments,
				itemProcessor);
	}

	public TreasureEntry setNBT(CompoundNBT nbt) {
		this.nbt = nbt.copy();
		return this;
	}

	public TreasureEntry setEnchantments(ItemEnchantment[] enchantments) {
		this.enchantments = enchantments;
		return this;
	}

	public ItemStack generate(ServerWorld world, Random rand, int theme, int lootLevel) {
		if (itemProcessor != null)
			return itemProcessor.generate(world, rand, theme, lootLevel);
		IItemProvider itemIn = ForgeRegistries.ITEMS.getValue(item), blockIn = ForgeRegistries.BLOCKS.getValue(item);
		ItemStack stack = new ItemStack(itemIn == null ? blockIn : itemIn);
		stack.setTag(nbt);
		if (max > 1)
			stack.setCount(min + rand.nextInt(max - min + 1));
		return stack;
	}

	public void readResourceLocation() {
//		String[] resource = resourceName.split(":");
		this.item = new ResourceLocation(resourceName);
//		DungeonCrawl.LOGGER.debug("Resource: {} Hash: {}", resourceName, this.item.hashCode());
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
