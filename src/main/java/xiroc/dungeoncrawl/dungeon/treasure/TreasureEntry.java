package xiroc.dungeoncrawl.dungeon.treasure;

/*
 * DungeonCrawl (C) 2019 XYROC (XIROC1337), All Rights Reserved 
 */

import java.util.Random;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.IItemProvider;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.registries.ForgeRegistries;
import xiroc.dungeoncrawl.util.ItemProcessor;

public class TreasureEntry {

	public ResourceLocation item;

	public int min, max, weight;

	public ItemEnchantment[] enchantments;

	public CompoundNBT nbt;

	private ItemProcessor<Random, Integer, Integer> itemProcessor;

	public TreasureEntry(String item, int weight) {
		this(item, 1, 1, weight);
	}

	public TreasureEntry(String item, int min, int max, int weight) {
		this(new ResourceLocation(item), min, max, weight);
	}

	public TreasureEntry(ResourceLocation item, int min, int max, int weight) {
		this(item, min, max, weight, null, null);
	}

	public TreasureEntry(ResourceLocation item, int min, int max, int weight, CompoundNBT nbt,
			ItemEnchantment[] enchantments) {
		this.item = item;
		this.min = min;
		this.max = max;
		this.weight = weight;
		this.nbt = nbt;
		this.enchantments = enchantments;
	}

	public TreasureEntry(ResourceLocation item, int min, int max, int weight, CompoundNBT nbt,
			ItemEnchantment[] enchantments, ItemProcessor<Random, Integer, Integer> itemProcessor) {
		this(item, min, max, weight, nbt, enchantments);
		this.itemProcessor = itemProcessor;
	}

	public TreasureEntry withWeight(int weight) {
		return new TreasureEntry(this.item, this.min, this.max, weight, this.nbt, this.enchantments,
				this.itemProcessor);
	}

	public TreasureEntry withEnchantments(ItemEnchantment[] enchantments) {
		return new TreasureEntry(this.item, this.min, this.max, this.weight, this.nbt, enchantments,
				this.itemProcessor);
	}

	public TreasureEntry withProcessor(ItemProcessor<Random, Integer, Integer> itemProcessor) {
		return new TreasureEntry(this.item, this.min, this.max, this.weight, this.nbt, this.enchantments,
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

	public ItemStack generate(Random rand, int theme, int lootLevel) {
		if (itemProcessor != null)
			return itemProcessor.generate(rand, theme, lootLevel);
		IItemProvider itemIn = ForgeRegistries.ITEMS.getValue(item), blockIn = ForgeRegistries.BLOCKS.getValue(item);
		ItemStack stack = new ItemStack(itemIn == null ? blockIn : itemIn);
		stack.setTag(nbt);
		if (max > 1)
			stack.setCount(min + rand.nextInt(max - min + 1));
		return stack;
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
