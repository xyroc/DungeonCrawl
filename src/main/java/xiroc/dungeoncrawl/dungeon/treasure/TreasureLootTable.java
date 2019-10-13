package xiroc.dungeoncrawl.dungeon.treasure;

/*
 * DungeonCrawl (C) 2019 XYROC (XIROC1337), All Rights Reserved 
 */

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

import com.google.common.collect.Lists;

import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.server.ServerWorld;
import net.minecraft.world.storage.loot.LootContext;
import net.minecraft.world.storage.loot.LootTable;
import net.minecraft.world.storage.loot.LootTableManager;
import net.minecraft.world.storage.loot.RandomValueRange;
import xiroc.dungeoncrawl.DungeonCrawl;

public class TreasureLootTable {

	private static boolean BUILT = false;
	public static final List<TreasureLootTable> LOOT_TABLES = Lists.newArrayList();

	public String name;
	public RandomValueRange rolls;
	public float minRolls, maxRolls;
	public VanillaImport[] vanillaImports;
	public ArrayList<TreasureEntry> entries;
	public Integer totalWeight;

	public TreasureLootTable(String name, RandomValueRange rolls, TreasureEntry... entries) {
		this.name = name;
		if (rolls != null) {
			this.minRolls = rolls.getMin();
			this.maxRolls = rolls.getMax();
		}
		this.entries = new ArrayList<TreasureEntry>();
		for (TreasureEntry entry : entries)
			this.entries.add(entry);
		vanillaImports = new VanillaImport[0];
	}

	public TreasureLootTable(String name, RandomValueRange rolls, TreasureLootTable entries) {
		this.name = name;
		if (rolls != null) {
			this.minRolls = rolls.getMin();
			this.maxRolls = rolls.getMax();
		}
		this.entries = new ArrayList<TreasureEntry>();
		for (TreasureEntry entry : entries.entries)
			this.entries.add(entry);
		vanillaImports = new VanillaImport[0];
	}

	public void build(LootTableManager manager) {
		if (vanillaImports != null)
			for (VanillaImport importRessource : vanillaImports) {
				DungeonCrawl.LOGGER.debug("Importing {} into {}", importRessource.location, name);
				importRessource.build(manager);
				entries.add(new TreasureEntry.VanillaLootTable(importRessource));
			}

		totalWeight = 0;

		for (TreasureEntry entry : entries) {
			entry.readResourceLocation();
			totalWeight += entry.weight;
		}

		this.rolls = new RandomValueRange(minRolls, maxRolls);
	}

	private List<ItemStack> roll(ServerWorld world, Random rand, int theme, int lootLevel) {
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
			list.add(getItemStack(world, rand, theme, lootLevel));
		}

		return list;
	}

	public ItemStack getItemStack(ServerWorld world, Random rand, int theme, int lootLevel) {
		int item = rand.nextInt(totalWeight);
		int k = 0;

		for (TreasureEntry entry : entries) {

			if (k + entry.weight > item)
				return entry.generate(world, rand, theme, lootLevel);

			k += entry.weight;
		}
		DungeonCrawl.LOGGER.error("Could not find an item with weight {} in {}. Maximum weight is {}.", item, name,
				totalWeight);
		return ItemStack.EMPTY;
	}

	public void fillInventory(IInventory inventory, ServerWorld world, Random rand, int theme, int lootLevel) {
//		DungeonCrawl.LOGGER.debug("Loot table: {}", name);
		List<ItemStack> list = roll(world, rand, theme, lootLevel);
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

	public void setVanillaImports(VanillaImport... vanillaImports) {
		this.vanillaImports = vanillaImports;
	}

	public static void buildAll(LootTableManager manager) {
		if (BUILT)
			return;
		DungeonCrawl.LOGGER.info("Building loot tables");
		BUILT = true;
		for (TreasureLootTable lootTable : LOOT_TABLES) {
			DungeonCrawl.LOGGER.debug("Building {}", lootTable.name);
			lootTable.build(manager);
		}
	}

	public static class VanillaImport {

		String location;
		int weight;
		LootTable table;

		public VanillaImport(String location) {
			this(location, 1);
		}

		public VanillaImport(String location, int weight) {
			this.location = location;
			this.weight = weight;
		}

		public void build(LootTableManager manager) {
			this.table = manager.getLootTableFromLocation(new ResourceLocation(location));
		}

		public ItemStack generate(LootContext lootContext) {
			List<ItemStack> stacks = table.generate(lootContext);
			if (stacks.isEmpty())
				return ItemStack.EMPTY;
			return stacks.get(lootContext.getRandom().nextInt(stacks.size()));
		}

	}

}
