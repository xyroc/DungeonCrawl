package xiroc.dungeoncrawl.dungeon.monster;

/*
 * DungeonCrawl (C) 2019 - 2020 XYROC (XIROC1337), All Rights Reserved 
 */

import java.util.Random;

import net.minecraft.enchantment.Enchantment;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.INBT;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.registries.ForgeRegistries;
import xiroc.dungeoncrawl.DungeonCrawl;
import xiroc.dungeoncrawl.config.JsonConfig;
import xiroc.dungeoncrawl.util.IRandom;

public class RandomEquipment {

	public static final int HIGHEST_STAGE = 4;

	public static final int[] ARMOR_COLORS = new int[] { 11546150, 16701501, 3949738, 6192150, 16351261, 16383998,
			15961002, 1908001, 8439583, 4673362, 1481884, 8991416, 3847130 };

	public static final IRandom<ItemStack> BOW = (rand) -> {
		ItemStack item = new ItemStack(getItem(JsonConfig.BOWS[rand.nextInt(JsonConfig.BOWS.length)]));
		if (item.getItem() == null)
			return ItemStack.EMPTY;
		applyDamage(item, rand);
		return item;
	};

	public static final IRandom<ItemStack> SWORD = (rand) -> {
		ItemStack item = new ItemStack(
				getItem(rand.nextFloat() < 0.05 ? JsonConfig.SWORDS_RARE[rand.nextInt(JsonConfig.SWORDS_RARE.length)]
						: JsonConfig.SWORDS[rand.nextInt(JsonConfig.SWORDS.length)]));
		if (item.getItem() == null)
			return ItemStack.EMPTY;
		applyDamage(item, rand);
		return item;
	};

	public static final IRandom<ItemStack> PICKAXE = (rand) -> {
		ItemStack item = new ItemStack(getItem(JsonConfig.PICKAXES[rand.nextInt(JsonConfig.PICKAXES.length)]));
		if (item.getItem() == null)
			return ItemStack.EMPTY;
		applyDamage(item, rand);
		return item;
	};

	public static final IRandom<ItemStack> AXE = (rand) -> {
		ItemStack item = new ItemStack(getItem(JsonConfig.AXES[rand.nextInt(JsonConfig.AXES.length)]));
		if (item.getItem() == null)
			return ItemStack.EMPTY;
		applyDamage(item, rand);
		return item;
	};

	public static final IRandom<ItemStack[]> ARMOR_1 = (rand) -> {
		ItemStack[] items = new ItemStack[4];
		ArmorSet armor = JsonConfig.ARMOR_SETS_1[rand.nextInt(JsonConfig.ARMOR_SETS_1.length)];
		for (int i = 0; i < 4; i++) {
			if (rand.nextFloat() < 0.5) {
				ItemStack item = new ItemStack(getItem(armor.items[i]));
				if (item.getItem() != null) {
					enchantArmor(item, rand, 0.25);
					applyDamage(item, rand);
					if (JsonConfig.COLORED_ARMOR.contains(armor.items[i].toString()))
						setArmorColor(item, getRandomColor(rand));
					items[i] = item;
				} else
					items[i] = ItemStack.EMPTY;
			} else
				items[i] = ItemStack.EMPTY;
		}
		return items;
	};

	public static final IRandom<ItemStack[]> ARMOR_2 = (rand) -> {
		ItemStack[] items = new ItemStack[4];
		ArmorSet armor = JsonConfig.ARMOR_SETS_2[rand.nextInt(JsonConfig.ARMOR_SETS_2.length)];
		for (int i = 0; i < 4; i++) {
			if (rand.nextFloat() < 0.5) {
				ItemStack item = new ItemStack(getItem(armor.items[i]));
				if (item.getItem() != null) {
					enchantArmor(item, rand, 0.5);
					applyDamage(item, rand);
					if (JsonConfig.COLORED_ARMOR.contains(armor.items[i].toString()))
						setArmorColor(item, getRandomColor(rand));
					items[i] = item;
				} else
					items[i] = ItemStack.EMPTY;
			} else
				items[i] = ItemStack.EMPTY;
		}
		return items;
	};

	public static final IRandom<ItemStack[]> ARMOR_3 = (rand) -> {
		ItemStack[] items = new ItemStack[4];
		ArmorSet armor = rand.nextFloat() < 0.05
				? JsonConfig.ARMOR_SETS_RARE[rand.nextInt(JsonConfig.ARMOR_SETS_RARE.length)]
				: JsonConfig.ARMOR_SETS_3[rand.nextInt(JsonConfig.ARMOR_SETS_3.length)];
		for (int i = 0; i < 4; i++) {
			if (rand.nextFloat() < 0.5) {
				ItemStack item = new ItemStack(getItem(armor.items[i]));
				if (item.getItem() != null) {
					enchantArmor(item, rand, 1);
					applyDamage(item, rand);
					if (JsonConfig.COLORED_ARMOR.contains(armor.items[i].toString()))
						setArmorColor(item, getRandomColor(rand));
					items[i] = item;
				} else
					items[i] = ItemStack.EMPTY;
			} else
				items[i] = ItemStack.EMPTY;
		}
		return items;
	};

	public static void applyDamage(ItemStack item, Random rand) {
		if (item.isDamageable())
			item.setDamage(rand.nextInt(item.getMaxDamage()));
	}

	public static ItemStack enchantItem(ItemStack item, Random rand, Enchantment enchantment, double multiplier) {
		int minLevel = enchantment.getMinLevel();
		int maxLevel = (int) ((double) enchantment.getMaxLevel() * multiplier);
		item.addEnchantment(enchantment, minLevel < maxLevel ? minLevel + rand.nextInt(maxLevel - minLevel) : minLevel);
		return item;
	}

	public static ItemStack enchantItem(ItemStack item, Enchantment enchantment, double multiplier) {
		int level = (int) ((double) enchantment.getMaxLevel() * multiplier);
		if (level < 1)
			level = 1;
		item.addEnchantment(enchantment, level);
		return item;
	}

	public static ItemStack enchantBow(ItemStack item, Random rand, double multiplier) {
		Enchantment enchantment = ForgeRegistries.ENCHANTMENTS
				.getValue(JsonConfig.BOW_ENCHANTMENTS[rand.nextInt(JsonConfig.BOW_ENCHANTMENTS.length)]);
		enchantItem(item, rand, enchantment, multiplier);
		return item;
	}

	public static ItemStack enchantArmor(ItemStack item, Random rand, double multiplier) {
		Enchantment enchantment = ForgeRegistries.ENCHANTMENTS
				.getValue(JsonConfig.ARMOR_ENCHANTMENTS[rand.nextInt(JsonConfig.ARMOR_ENCHANTMENTS.length)]);
		enchantItem(item, rand, enchantment, multiplier);
		return item;
	}

	public static ItemStack enchantSword(ItemStack item, Random rand, double multiplier) {
		Enchantment enchantment = ForgeRegistries.ENCHANTMENTS
				.getValue(JsonConfig.SWORD_ENCHANTMENTS[rand.nextInt(JsonConfig.SWORD_ENCHANTMENTS.length)]);
		enchantItem(item, rand, enchantment, multiplier);
		return item;
	}

	public static ItemStack enchantPickaxe(ItemStack item, Random rand, double multiplier) {
		enchantSword(item, rand, multiplier);
		Enchantment enchantment = ForgeRegistries.ENCHANTMENTS
				.getValue(JsonConfig.PICKAXE_ENCHANTMENTS[rand.nextInt(JsonConfig.PICKAXE_ENCHANTMENTS.length)]);
		enchantItem(item, rand, enchantment, multiplier);
		return item;
	}

	public static ItemStack enchantAxe(ItemStack item, Random rand, double multiplier) {
		enchantSword(item, rand, multiplier);
		Enchantment enchantment = ForgeRegistries.ENCHANTMENTS
				.getValue(JsonConfig.AXE_ENCHANTMENTS[rand.nextInt(JsonConfig.AXE_ENCHANTMENTS.length)]);
		enchantItem(item, rand, enchantment, multiplier);
		return item;
	}

	public static ItemStack setArmorColor(ItemStack item, int color) {
		CompoundNBT tag = item.getTag();
		if (tag == null)
			tag = new CompoundNBT();
		INBT displayNBT = tag.get("display");
		CompoundNBT display;
		if (displayNBT == null)
			display = new CompoundNBT();
		else
			display = (CompoundNBT) displayNBT;
		display.putInt("color", color);
		tag.put("display", display);
		item.setTag(tag);
		return item;
	}

	public static ItemStack getMeleeWeapon(Random rand, int stage) {
		switch (rand.nextInt(3)) {
		case 0:
			return enchantSword(SWORD.roll(rand), rand, getStageMultiplier(stage));
		case 1:
			return enchantPickaxe(PICKAXE.roll(rand), rand, getStageMultiplier(stage));
		case 2:
			return enchantAxe(AXE.roll(rand), rand, getStageMultiplier(stage));
		}
		return null;
	}

	public static ItemStack getRangedWeapon(Random rand, int stage) {
		return enchantBow(BOW.roll(rand), rand, getStageMultiplier(stage));
	}

	public static double getStageMultiplier(int stage) {
		if (stage > 2)
			return 1.0D;
		return 1D * Math.pow(0.5, HIGHEST_STAGE - stage);
	}

	public static int getRandomColor(Random rand) {
		return ARMOR_COLORS[rand.nextInt(ARMOR_COLORS.length)];
	}

	public static Item getItem(ResourceLocation resourceLocation) {
		if (ForgeRegistries.ITEMS.containsKey(resourceLocation))
			return ForgeRegistries.ITEMS.getValue(resourceLocation);
		DungeonCrawl.LOGGER.warn("Failed to get {} from the item registry.", resourceLocation.toString());
		return null;
	}

}
