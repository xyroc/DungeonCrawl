package xiroc.dungeoncrawl.dungeon.treasure;

/*
 * DungeonCrawl (C) 2019 XYROC (XIROC1337), All Rights Reserved 
 */

import java.util.ArrayList;

/*
 * DungeonCrawl (C) 2019 XYROC (XIROC1337), All Rights Reserved 
 */

import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

import com.google.common.collect.Lists;

import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.storage.loot.RandomValueRange;
import xiroc.dungeoncrawl.DungeonCrawl;

public class TreasureLootTable {

	public String name;
	public RandomValueRange rolls;
	public ArrayList<TreasureEntry> entries;
	public Integer totalWeight;

	public TreasureLootTable(String name, RandomValueRange rolls, TreasureEntry... entries) {
		this.name = name;
		this.rolls = rolls;
		this.entries = new ArrayList<TreasureEntry>();
		for (TreasureEntry entry : entries)
			this.entries.add(entry);
	}

	public void build() {
		totalWeight = 0;
		for (TreasureEntry entry : entries)
			totalWeight += entry.weight;
	}

	private List<ItemStack> roll(Random rand, int theme, int lootLevel) {
		List<ItemStack> list = Lists.newArrayList();

		for (int i = 0; i < rolls.generateInt(rand); i++) {
//			int item = rand.nextInt(totalWeight);
//			int k = 0;
//
//			for (TreasureEntry entry : entries) {
//
//				if (k + entry.weight > item) {
//					list.add(entry.generate(rand, theme, lootLevel));
//					continue;
//				}
//
//				k += entry.weight;
//			}
			list.add(getItemStack(rand, theme, lootLevel));
		}

		return list;
	}

	public ItemStack getItemStack(Random rand, int theme, int lootLevel) {
		int item = rand.nextInt(totalWeight);
		int k = 0;

		for (TreasureEntry entry : entries) {

			if (k + entry.weight > item)
				return entry.generate(rand, theme, lootLevel);

			k += entry.weight;
		}
		DungeonCrawl.LOGGER.error("Could not find an item with weight {} in {}. Maximum weight is {}.", item, name,
				totalWeight);
		return ItemStack.EMPTY;
	}

	public void fillInventory(IInventory inventory, Random rand, int theme, int lootLevel) {
		List<ItemStack> list = roll(rand, theme, lootLevel);
		List<Integer> slots = getEmptySlotsRandomized(inventory, rand);
		shuffleItems(list, slots.size(), rand);
		for (ItemStack itemStack : list) {
			if (slots.isEmpty()) {
				DungeonCrawl.LOGGER.warn("Tried to over-fill a container");
				return;
			}
			if (itemStack.isEmpty())
				inventory.setInventorySlotContents(slots.remove(slots.size() - 1), ItemStack.EMPTY);
			else
				inventory.setInventorySlotContents(slots.remove(slots.size() - 1), itemStack);
		}
	}

	private void shuffleItems(List<ItemStack> stacks, int p_186463_2_, Random rand) {
		List<ItemStack> list = Lists.newArrayList();
		Iterator<ItemStack> iterator = stacks.iterator();

		while (iterator.hasNext()) {
			ItemStack itemstack = iterator.next();

			if (itemstack.isEmpty()) {
				iterator.remove();
			} else if (itemstack.getCount() > 1) {
				list.add(itemstack);
				iterator.remove();
			}
		}

		while (p_186463_2_ - stacks.size() - list.size() > 0 && !list.isEmpty()) {
			ItemStack itemstack2 = list.remove(MathHelper.nextInt(rand, 0, list.size() - 1));
			int i = MathHelper.nextInt(rand, 1, itemstack2.getCount() / 2);
			ItemStack itemstack1 = itemstack2.split(i);

			if (itemstack2.getCount() > 1 && rand.nextBoolean()) {
				list.add(itemstack2);
			} else {
				stacks.add(itemstack2);
			}

			if (itemstack1.getCount() > 1 && rand.nextBoolean()) {
				list.add(itemstack1);
			} else {
				stacks.add(itemstack1);
			}
		}

		stacks.addAll(list);
		Collections.shuffle(stacks, rand);
	}

	private List<Integer> getEmptySlotsRandomized(IInventory inventory, Random rand) {
		List<Integer> list = Lists.newArrayList();

		for (int i = 0; i < inventory.getSizeInventory(); ++i)
			if (inventory.getStackInSlot(i).isEmpty())
				list.add(i);

		Collections.shuffle(list, rand);
		return list;
	}

}
