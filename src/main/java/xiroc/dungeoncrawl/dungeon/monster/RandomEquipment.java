package xiroc.dungeoncrawl.dungeon.monster;

/*
 * DungeonCrawl (C) 2019 XYROC (XIROC1337), All Rights Reserved 
 */

import java.util.Random;
import java.util.Set;

import com.google.common.collect.ImmutableSet;

import net.minecraft.enchantment.Enchantment;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.registries.ForgeRegistries;
import xiroc.dungeoncrawl.DungeonCrawl;
import xiroc.dungeoncrawl.util.IRandom;

public class RandomEquipment {

    public static final int HIGHEST_STAGE = 2;

    public static final int[] ARMOR_COLORS = new int[] { 11546150, 16701501, 3949738, 6192150, 16351261, 16383998,
	    15961002, 1908001, 8439583, 4673362, 1481884, 8991416, 3847130 };

    public static final Set<String> COLORED_ARMOR = ImmutableSet.<String>builder().add("minecraft:leather_boots")
	    .add("minecraft:leather_pants").add("minecraft:leather_chestplate").add("minecraft:leather_helmet").build();

    public static final ResourceLocation[] BOWS = new ResourceLocation[] { new ResourceLocation("minecraft:bow") };

    public static final ResourceLocation[] SWORDS = new ResourceLocation[] {
	    new ResourceLocation("minecraft:stone_sword"), new ResourceLocation("minecraft:golden_sword"),
	    new ResourceLocation("minecraft:iron_sword") };

    public static final ResourceLocation[] SWORDS_RARE = new ResourceLocation[] {
	    new ResourceLocation("minecraft:wooden_sword"), new ResourceLocation("minecraft:diamond_sword") };

    public static final ResourceLocation[] PICKAXES = new ResourceLocation[] {
	    new ResourceLocation("minecraft:stone_pickaxe"), new ResourceLocation("minecraft:golden_pickaxe"),
	    new ResourceLocation("minecraft:iron_pickaxe"), };
    public static final ResourceLocation[] AXES = new ResourceLocation[] { new ResourceLocation("minecraft:stone_axe"),
	    new ResourceLocation("minecraft:golden_axe"), new ResourceLocation("minecraft:iron_axe"),
	    new ResourceLocation("minecraft:diamond_axe") };

    public static final ArmorSet[] ARMOR_SETS_1 = new ArmorSet[] { new ArmorSet("minecraft:leather_boots",
	    "minecraft:leather_leggings", "minecraft:leather_chestplate", "minecraft:leather_helmet") };

    public static final ArmorSet[] ARMOR_SETS_2 = new ArmorSet[] {
	    new ArmorSet("minecraft:leather_boots", "minecraft:leather_leggings", "minecraft:leather_chestplate",
		    "minecraft:leather_helmet"),
	    new ArmorSet("minecraft:golden_boots", "minecraft:golden_leggings", "minecraft:golden_chestplate",
		    "minecraft:golden_helmet"),
	    new ArmorSet("minecraft:chainmail_boots", "minecraft:chainmail_leggings", "minecraft:chainmail_chestplate",
		    "minecraft:chainmail_helmet"),
	    new ArmorSet("minecraft:iron_boots", "minecraft:iron_leggings", "minecraft:iron_chestplate",
		    "minecraft:iron_helmet") };

    public static final ArmorSet[] ARMOR_SETS_3 = new ArmorSet[] {
	    new ArmorSet("minecraft:leather_boots", "minecraft:leather_leggings", "minecraft:leather_chestplate",
		    "minecraft:leather_helmet"),
	    new ArmorSet("minecraft:golden_boots", "minecraft:golden_leggings", "minecraft:golden_chestplate",
		    "minecraft:golden_helmet"),
	    new ArmorSet("minecraft:chainmail_boots", "minecraft:chainmail_leggings", "minecraft:chainmail_chestplate",
		    "minecraft:chainmail_helmet"),
	    new ArmorSet("minecraft:iron_boots", "minecraft:iron_leggings", "minecraft:iron_chestplate",
		    "minecraft:iron_helmet") };

    public static final ArmorSet[] ARMOR_SETS_RARE = new ArmorSet[] { new ArmorSet("minecraft:diamond_boots",
	    "minecraft:diamond_leggings", "minecraft:diamond_chestplate", "minecraft:diamond_helmet") };

    public static final ResourceLocation[] BOW_ENCHANTMENTS = new ResourceLocation[] {
	    new ResourceLocation("minecraft:power"), new ResourceLocation("minecraft:unbreaking"),
	    new ResourceLocation("minecraft:punch") };

    public static final ResourceLocation[] SWORD_ENCHANTMENTS = new ResourceLocation[] {
	    new ResourceLocation("minecraft:sharpness"), new ResourceLocation("minecraft:unbreaking"),
	    new ResourceLocation("minecraft:fire_aspect"), new ResourceLocation("minecraft:knockback") };

    public static final ResourceLocation[] PICKAXE_ENCHANTMENTS = new ResourceLocation[] {
	    new ResourceLocation("minecraft:efficiency") };

    public static final ResourceLocation[] AXE_ENCHANTMENTS = new ResourceLocation[] {
	    new ResourceLocation("minecraft:efficiency") };

    public static final ResourceLocation[] ARMOR_ENCHANTMENTS = new ResourceLocation[] {
	    new ResourceLocation("minecraft:protection"), new ResourceLocation("minecraft:unbreaking"),
	    new ResourceLocation("minecraft:thorns"), new ResourceLocation("minecraft:projectile_protection") };

    public static final IRandom<ItemStack> BOW = (rand) -> {
	ItemStack item = new ItemStack(getItem(BOWS[rand.nextInt(BOWS.length)]));
	if (item.getItem() == null)
	    return ItemStack.EMPTY;
	applyDamage(item, rand);
	return item;
    };

    public static final IRandom<ItemStack> SWORD = (rand) -> {
	ItemStack item = new ItemStack(getItem(rand.nextFloat() < 0.05 ? SWORDS_RARE[rand.nextInt(SWORDS_RARE.length)]
		: SWORDS[rand.nextInt(SWORDS.length)]));
	if (item.getItem() == null)
	    return ItemStack.EMPTY;
	applyDamage(item, rand);
	return item;
    };

    public static final IRandom<ItemStack> PICKAXE = (rand) -> {
	ItemStack item = new ItemStack(getItem(PICKAXES[rand.nextInt(PICKAXES.length)]));
	if (item.getItem() == null)
	    return ItemStack.EMPTY;
	applyDamage(item, rand);
	return item;
    };

    public static final IRandom<ItemStack> AXE = (rand) -> {
	ItemStack item = new ItemStack(getItem(AXES[rand.nextInt(AXES.length)]));
	if (item.getItem() == null)
	    return ItemStack.EMPTY;
	applyDamage(item, rand);
	return item;
    };

    public static final IRandom<ItemStack[]> ARMOR_1 = (rand) -> {
	ItemStack[] items = new ItemStack[4];
	ArmorSet armor = ARMOR_SETS_1[rand.nextInt(ARMOR_SETS_1.length)];
	for (int i = 0; i < 4; i++) {
	    if (rand.nextFloat() < 0.5) {
		ItemStack item = new ItemStack(getItem(armor.items[i]));
		if (item.getItem() != null) {
		    enchantArmor(item, rand, 0.25);
		    applyDamage(item, rand);
		    if (COLORED_ARMOR.contains(armor.items[i].toString()))
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
	ArmorSet armor = ARMOR_SETS_2[rand.nextInt(ARMOR_SETS_2.length)];
	for (int i = 0; i < 4; i++) {
	    if (rand.nextFloat() < 0.5) {
		ItemStack item = new ItemStack(getItem(armor.items[i]));
		if (item.getItem() != null) {
		    enchantArmor(item, rand, 0.5);
		    applyDamage(item, rand);
		    if (COLORED_ARMOR.contains(armor.items[i].toString()))
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
	ArmorSet armor = rand.nextFloat() < 0.05 ? ARMOR_SETS_RARE[rand.nextInt(ARMOR_SETS_RARE.length)]
		: ARMOR_SETS_3[rand.nextInt(ARMOR_SETS_3.length)];
	for (int i = 0; i < 4; i++) {
	    if (rand.nextFloat() < 0.5) {
		ItemStack item = new ItemStack(getItem(armor.items[i]));
		if (item.getItem() != null) {
		    enchantArmor(item, rand, 1);
		    applyDamage(item, rand);
		    if (COLORED_ARMOR.contains(armor.items[i].toString()))
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

    public static ItemStack enchantBow(ItemStack item, Random rand, double multiplier) {
	Enchantment enchantment = ForgeRegistries.ENCHANTMENTS
		.getValue(BOW_ENCHANTMENTS[rand.nextInt(BOW_ENCHANTMENTS.length)]);
	enchantItem(item, rand, enchantment, multiplier);
	return item;
    }

    public static ItemStack enchantArmor(ItemStack item, Random rand, double multiplier) {
	Enchantment enchantment = ForgeRegistries.ENCHANTMENTS
		.getValue(ARMOR_ENCHANTMENTS[rand.nextInt(ARMOR_ENCHANTMENTS.length)]);
	enchantItem(item, rand, enchantment, multiplier);
	return item;
    }

    public static ItemStack enchantSword(ItemStack item, Random rand, double multiplier) {
	Enchantment enchantment = ForgeRegistries.ENCHANTMENTS
		.getValue(SWORD_ENCHANTMENTS[rand.nextInt(SWORD_ENCHANTMENTS.length)]);
	enchantItem(item, rand, enchantment, multiplier);
	return item;
    }

    public static ItemStack enchantPickaxe(ItemStack item, Random rand, double multiplier) {
	enchantSword(item, rand, multiplier);
	Enchantment enchantment = ForgeRegistries.ENCHANTMENTS
		.getValue(PICKAXE_ENCHANTMENTS[rand.nextInt(PICKAXE_ENCHANTMENTS.length)]);
	enchantItem(item, rand, enchantment, multiplier);
	return item;
    }

    public static ItemStack enchantAxe(ItemStack item, Random rand, double multiplier) {
	enchantSword(item, rand, multiplier);
	Enchantment enchantment = ForgeRegistries.ENCHANTMENTS
		.getValue(AXE_ENCHANTMENTS[rand.nextInt(AXE_ENCHANTMENTS.length)]);
	enchantItem(item, rand, enchantment, multiplier);
	return item;
    }

    public static void setArmorColor(ItemStack item, int color) {
	CompoundNBT tag = item.getTag();
	if (tag == null)
	    tag = new CompoundNBT();
	CompoundNBT display = new CompoundNBT();
	display.putInt("color", color);
	tag.put("display", display);
	item.setTag(tag);
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
	return 1D * Math.pow(0.5, HIGHEST_STAGE - stage);
    }

    public static int getRandomColor(Random rand) {
	return ARMOR_COLORS[rand.nextInt(ARMOR_COLORS.length)];
    }

    public static Item getItem(ResourceLocation resourceLocation) {
	if (ForgeRegistries.ITEMS.containsKey(resourceLocation))
	    return ForgeRegistries.ITEMS.getValue(resourceLocation);
	DungeonCrawl.LOGGER.warn("Failed to get {} from the item registry.",
		resourceLocation.toString());
	return null;
    }

}
