package xiroc.dungeoncrawl.dungeon.treasure;

/*
 * DungeonCrawl (C) 2019 - 2020 XYROC (XIROC1337), All Rights Reserved 
 */

import java.util.Random;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.server.ServerWorld;
import xiroc.dungeoncrawl.util.ItemProcessor;

public class TreasureEntry {

	public ResourceLocation item;
	public String resourceName;

	public ItemEnchantment[] enchantments;

	public CompoundNBT nbt;

	private ItemProcessor<ServerWorld, Random, Integer, Integer> itemProcessor;

	public TreasureEntry(String resourceName) {
		this(resourceName, null, null);
	}

	public TreasureEntry(String resourceName, CompoundNBT nbt,
			ItemEnchantment[] enchantments) {
		this.resourceName = resourceName;
		this.nbt = nbt;
		this.enchantments = enchantments;
	}

	public TreasureEntry(String resourceName, CompoundNBT nbt,
			ItemEnchantment[] enchantments, ItemProcessor<ServerWorld, Random, Integer, Integer> itemProcessor) {
		this(resourceName, nbt, enchantments);
		this.itemProcessor = itemProcessor;
	}

	public TreasureEntry withWeight(int weight) {
		return new TreasureEntry(this.resourceName, this.nbt, this.enchantments,
				this.itemProcessor);
	}

	public TreasureEntry withEnchantments(ItemEnchantment[] enchantments) {
		return new TreasureEntry(this.resourceName, this.nbt, enchantments,
				this.itemProcessor);
	}

	public TreasureEntry withProcessor(ItemProcessor<ServerWorld, Random, Integer, Integer> itemProcessor) {
		return new TreasureEntry(this.resourceName, this.nbt, this.enchantments,
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
		return ItemStack.EMPTY;
	}

	public void readResourceLocation() {
		this.item = new ResourceLocation(resourceName);
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
