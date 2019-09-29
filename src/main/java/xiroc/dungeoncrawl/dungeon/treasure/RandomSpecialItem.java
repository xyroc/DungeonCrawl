package xiroc.dungeoncrawl.dungeon.treasure;

/*
 * DungeonCrawl (C) 2019 XYROC (XIROC1337), All Rights Reserved 
 */

import java.util.Random;

import net.minecraft.enchantment.Enchantments;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.StringTextComponent;
import net.minecraftforge.registries.ForgeRegistries;
import xiroc.dungeoncrawl.config.SpecialItemTags;
import xiroc.dungeoncrawl.dungeon.monster.RandomEquipment;
import xiroc.dungeoncrawl.util.ItemProcessor;

public class RandomSpecialItem implements ItemProcessor<Random, Integer, Integer> {

	public static final int COLOR = 3847130;

	public static final ItemStack[] ITEMS;

	public static final ItemStack[] RARE_ITEMS;

	public static final ItemStack REINFORCED_BOW, BOOTS_OF_BATTLE, PANTS_OF_DEFLECTION, LUMBERJACKET, YOKEL_AXE, DOOM,
			THE_SLAYER, DEMON_HUNTER_CROSSBOW;

	public static final TreasureEntry CAP, PANTALOONS, LEATHER_JACKET, LEATHER_BOOTS, CHAINMAIL_BOOTS,
			CHAINMAIL_LEGGINGS, CHAINMAIL_CHESTPLATE, CHAINMAIL_HELMET, IRON_SWORD, STONE_SWORD;

	public static final TreasureEntry[] SPECIAL_ITEMS;

	static {
		REINFORCED_BOW = new ItemStack(Items.BOW);
		REINFORCED_BOW.addEnchantment(Enchantments.UNBREAKING, 1);
		REINFORCED_BOW.addEnchantment(Enchantments.POWER, 1);
		REINFORCED_BOW.setDisplayName(new StringTextComponent("Reinforced Bow"));

		BOOTS_OF_BATTLE = new ItemStack(Items.LEATHER_BOOTS);
		RandomEquipment.setArmorColor(BOOTS_OF_BATTLE, COLOR);
		BOOTS_OF_BATTLE.addEnchantment(Enchantments.UNBREAKING, 1);
		BOOTS_OF_BATTLE.addEnchantment(Enchantments.PROTECTION, 1);
		BOOTS_OF_BATTLE.setDisplayName(new StringTextComponent("Boots of Battle"));

		PANTS_OF_DEFLECTION = new ItemStack(Items.LEATHER_LEGGINGS);
		RandomEquipment.setArmorColor(PANTS_OF_DEFLECTION, COLOR);
		PANTS_OF_DEFLECTION.addEnchantment(Enchantments.PROTECTION, 2);
		PANTS_OF_DEFLECTION.addEnchantment(Enchantments.THORNS, 1);
		PANTS_OF_DEFLECTION.setDisplayName(new StringTextComponent("Pants of Deflection"));

		LUMBERJACKET = new ItemStack(Items.LEATHER_CHESTPLATE);
		RandomEquipment.setArmorColor(LUMBERJACKET, 11546150);
		LUMBERJACKET.addEnchantment(Enchantments.UNBREAKING, 3);
		LUMBERJACKET.addEnchantment(Enchantments.FIRE_PROTECTION, 1);
		LUMBERJACKET.setDisplayName(new StringTextComponent("Lumberjacket"));

		YOKEL_AXE = new ItemStack(Items.IRON_AXE);
		YOKEL_AXE.addEnchantment(Enchantments.EFFICIENCY, 2);
		YOKEL_AXE.addEnchantment(Enchantments.SHARPNESS, 1);
		YOKEL_AXE.addEnchantment(Enchantments.UNBREAKING, 1);
		YOKEL_AXE.setDisplayName(new StringTextComponent("Yokel's Axe"));

		DOOM = new ItemStack(ForgeRegistries.ITEMS.getValue(new ResourceLocation("minecraft:golden_sword")));
		DOOM.addEnchantment(Enchantments.SHARPNESS, 1);
		DOOM.addEnchantment(Enchantments.FIRE_ASPECT, 2);
		DOOM.addEnchantment(Enchantments.UNBREAKING, 1);
		DOOM.setDisplayName(new StringTextComponent("Doom"));

		THE_SLAYER = new ItemStack(ForgeRegistries.ITEMS.getValue(new ResourceLocation("minecraft:diamond_sword")));
		THE_SLAYER.addEnchantment(Enchantments.SHARPNESS, 4);
		THE_SLAYER.setDisplayName(new StringTextComponent("The Slayer"));

		DEMON_HUNTER_CROSSBOW = new ItemStack(Items.CROSSBOW);
		DEMON_HUNTER_CROSSBOW.addEnchantment(Enchantments.PIERCING, 2);
		DEMON_HUNTER_CROSSBOW.addEnchantment(Enchantments.MULTISHOT, 1);
		DEMON_HUNTER_CROSSBOW.addEnchantment(Enchantments.QUICK_CHARGE, 1);
		DEMON_HUNTER_CROSSBOW.addEnchantment(Enchantments.POWER, 4);
		DEMON_HUNTER_CROSSBOW.setDisplayName(new StringTextComponent("Demon Hunter's Crossbow"));

		ITEMS = new ItemStack[] { REINFORCED_BOW, BOOTS_OF_BATTLE, PANTS_OF_DEFLECTION, LUMBERJACKET, YOKEL_AXE, DOOM };

		RARE_ITEMS = new ItemStack[] { THE_SLAYER, DEMON_HUNTER_CROSSBOW };

		CAP = new TreasureEntry("minecraft:air", 1).withProcessor((rand, theme,
				lootLevel) -> RandomEquipment.setArmorColor(SpecialItemTags.rollForTagsAndApply(
						ForgeRegistries.ITEMS.getValue(new ResourceLocation("minecraft:leather_helmet")), lootLevel,
						rand, "Cap"), RandomEquipment.getRandomColor(rand)));
		LEATHER_JACKET = new TreasureEntry(
				"minecraft:air", 1)
						.withProcessor(
								(rand, theme, lootLevel) -> RandomEquipment
										.setArmorColor(
												SpecialItemTags.rollForTagsAndApply(
														ForgeRegistries.ITEMS.getValue(
																new ResourceLocation("minecraft:leather_chestplate")),
														lootLevel, rand, "Jacket"),
												RandomEquipment.getRandomColor(rand)));

		PANTALOONS = new TreasureEntry(
				"minecraft:air", 1)
						.withProcessor(
								(rand, theme, lootLevel) -> RandomEquipment
										.setArmorColor(
												SpecialItemTags.rollForTagsAndApply(
														ForgeRegistries.ITEMS.getValue(
																new ResourceLocation("minecraft:leather_leggings")),
														lootLevel, rand, "Pantaloons"),
												RandomEquipment.getRandomColor(rand)));

		LEATHER_BOOTS = new TreasureEntry(
				"minecraft:air", 1)
						.withProcessor(
								(rand, theme, lootLevel) -> RandomEquipment
										.setArmorColor(
												SpecialItemTags.rollForTagsAndApply(
														ForgeRegistries.ITEMS.getValue(
																new ResourceLocation("minecraft:leather_boots")),
														lootLevel, rand, "Boots"),
												RandomEquipment.getRandomColor(rand)));

		CHAINMAIL_BOOTS = getDefaultSpecialItem("minecraft:chainmail_boots");
		CHAINMAIL_LEGGINGS = getDefaultSpecialItem("minecraft:chainmail_leggings");
		CHAINMAIL_CHESTPLATE = getDefaultSpecialItem("minecraft:chainmail_chestplate");
		CHAINMAIL_HELMET = getDefaultSpecialItem("minecraft:chainmail_helmet");

		STONE_SWORD = getDefaultSpecialItem("minecraft:stone_sword");

		IRON_SWORD = new TreasureEntry("minecraft:air", 1).withProcessor((rand, theme, lootLevel) -> SpecialItemTags
				.rollForTagsAndApply(ForgeRegistries.ITEMS.getValue(new ResourceLocation("minecraft:iron_sword")),
						lootLevel, rand, "Blade"));

		SPECIAL_ITEMS = new TreasureEntry[] { CAP, PANTALOONS, LEATHER_JACKET, LEATHER_BOOTS, CHAINMAIL_BOOTS,
				CHAINMAIL_LEGGINGS, CHAINMAIL_CHESTPLATE, CHAINMAIL_HELMET, STONE_SWORD, IRON_SWORD };
	}

	@Override
	public ItemStack generate(Random rand, Integer theme, Integer lootLevel) {
		if (lootLevel > 4 || rand.nextDouble() < 0.025 * lootLevel)
			return RARE_ITEMS[rand.nextInt(RARE_ITEMS.length)].copy();
		return rand.nextDouble() < 0.8 ? SPECIAL_ITEMS[rand.nextInt(SPECIAL_ITEMS.length)].generate(rand, theme, lootLevel)
				: ITEMS[rand.nextInt(ITEMS.length)].copy();
	}

	private static TreasureEntry getDefaultSpecialItem(String item) {
		return new TreasureEntry("minecraft:air", 1).withProcessor((rand, theme, lootLevel) -> SpecialItemTags
				.rollForTagsAndApply(ForgeRegistries.ITEMS.getValue(new ResourceLocation(item)), lootLevel, rand));
	}

}
