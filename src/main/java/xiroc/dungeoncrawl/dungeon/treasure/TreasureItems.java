package xiroc.dungeoncrawl.dungeon.treasure;

import java.util.Random;

/*
 * DungeonCrawl (C) 2019 XYROC (XIROC1337), All Rights Reserved 
 */

import net.minecraft.enchantment.Enchantment;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;

import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.nbt.StringNBT;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraftforge.registries.ForgeRegistries;
import xiroc.dungeoncrawl.dungeon.monster.RandomEquipment;
import xiroc.dungeoncrawl.theme.ThemeItems;

public class TreasureItems {

	// Potions from the original Roguelike Dungeons
	public static final ItemStack LAUDANUM;
	public static final ItemStack ANIMUS;
	public static final ItemStack NECTAR;
	public static final ItemStack LUMA;

	// New Potions
	public static final ItemStack VELOCITAS;

	public static final ItemStack POTION_HEALING;
	public static final ItemStack POTION_HEALING_II;
	public static final ItemStack POTION_REGENERATION;
	public static final ItemStack POTION_REGENERATION_LONG;
	public static final ItemStack POTION_REGENERATION_II;

	public static final ItemStack SPLASH_POISON;
	public static final ItemStack SPLASH_POISON_LONG;
	public static final ItemStack SPLASH_HARMING;
	public static final ItemStack SPLASH_HARMING_II;

	public static ItemStack[] SPECIAL_POTIONS;

	public static final TreasureEntry RANDOM_SPECIAL_ITEM;

	public static final TreasureEntry ENCHANTED_BOOK;

	public static final TreasureEntry MATERIAL_BLOCKS;

	// Enchantments

	static {
		{
			CompoundNBT nbt = new CompoundNBT();
			ListNBT customPotionEffects = new ListNBT();
			CompoundNBT nausea = new CompoundNBT();
			nausea.putInt("Id", 9);
			nausea.putInt("Duration", 100);
			CompoundNBT blindness = new CompoundNBT();
			blindness.putInt("Id", 15);
			blindness.putInt("Duration", 100);
			CompoundNBT weakness = new CompoundNBT();
			weakness.putInt("Id", 18);
			weakness.putInt("Duration", 100);
			CompoundNBT miningFatique = new CompoundNBT();
			miningFatique.putInt("Id", 4);
			miningFatique.putInt("Duration", 100);
			CompoundNBT regeneration = new CompoundNBT();
			regeneration.putInt("Id", 10);
			regeneration.putInt("Amplifier", 1);
			regeneration.putInt("Duration", 160);
			customPotionEffects.add(regeneration);
			customPotionEffects.add(blindness);
			customPotionEffects.add(weakness);
			customPotionEffects.add(miningFatique);
			customPotionEffects.add(nausea);
			nbt.put("CustomPotionEffects", customPotionEffects);
			nbt.putInt("CustomPotionColor", 7014144);
			nbt.putInt("HideFlags", 32);
			CompoundNBT display = new CompoundNBT();
			ListNBT lore = new ListNBT();
			lore.add(new StringNBT(ITextComponent.Serializer.toJson(new StringTextComponent("A medicinal tincture."))));
			display.put("Lore", lore);
			display.put("Name", new StringNBT(ITextComponent.Serializer.toJson(new StringTextComponent("Laudanum"))));
			nbt.put("display", display);
			LAUDANUM = new ItemStack(Items.POTION);
			LAUDANUM.setTag(nbt);
		}
		{
			CompoundNBT nbt = new CompoundNBT();
			ListNBT customPotionEffects = new ListNBT();
			CompoundNBT wither = new CompoundNBT();
			wither.putInt("Id", 20);
			wither.putInt("Duration", 40);
			CompoundNBT blindness = new CompoundNBT();
			blindness.putInt("Id", 15);
			blindness.putInt("Duration", 40);
			CompoundNBT strength = new CompoundNBT();
			strength.putInt("Id", 5);
			strength.putInt("Duration", 800);
			customPotionEffects.add(strength);
			customPotionEffects.add(blindness);
			customPotionEffects.add(wither);
			nbt.put("CustomPotionEffects", customPotionEffects);
			nbt.putInt("CustomPotionColor", 13050390);
			nbt.putInt("HideFlags", 32);
			CompoundNBT display = new CompoundNBT();
			ListNBT lore = new ListNBT();
			lore.add(new StringNBT(ITextComponent.Serializer.toJson(new StringTextComponent("An unstable mixture."))));
			display.put("Lore", lore);
			display.put("Name", new StringNBT(ITextComponent.Serializer.toJson(new StringTextComponent("Animus"))));
			nbt.put("display", display);
			ANIMUS = new ItemStack(Items.POTION);
			ANIMUS.setTag(nbt);
		}
		{
			CompoundNBT nbt = new CompoundNBT();
			ListNBT customPotionEffects = new ListNBT();
			CompoundNBT resistance = new CompoundNBT();
			resistance.putInt("Id", 11);
			resistance.putInt("Duration", 400);
			CompoundNBT blindness = new CompoundNBT();
			blindness.putInt("Id", 15);
			blindness.putInt("Duration", 100);
			CompoundNBT absorption = new CompoundNBT();
			absorption.putInt("Id", 22);
			absorption.putInt("Amplifier", 14);
			absorption.putInt("Duration", 400);
			customPotionEffects.add(absorption);
			customPotionEffects.add(resistance);
			customPotionEffects.add(blindness);
			nbt.put("CustomPotionEffects", customPotionEffects);
			nbt.putInt("CustomPotionColor", 15446551);
			nbt.putInt("HideFlags", 32);
			CompoundNBT display = new CompoundNBT();
			ListNBT lore = new ListNBT();
			lore.add(new StringNBT(ITextComponent.Serializer.toJson(new StringTextComponent("A floral extract."))));
			display.put("Lore", lore);
			display.put("Name", new StringNBT(ITextComponent.Serializer.toJson(new StringTextComponent("Nectar"))));
			nbt.put("display", display);
			NECTAR = new ItemStack(Items.POTION);
			NECTAR.setTag(nbt);
		}
		{
			CompoundNBT nbt = new CompoundNBT();
			ListNBT customPotionEffects = new ListNBT();
			CompoundNBT speed = new CompoundNBT();
			speed.putInt("Id", 1);
			speed.putInt("Amplifier", 1);
			speed.putInt("Duration", 400);
			CompoundNBT blindness = new CompoundNBT();
			blindness.putInt("Id", 15);
			blindness.putInt("Duration", 40);
			CompoundNBT haste = new CompoundNBT();
			haste.putInt("Id", 3);
			haste.putInt("Duration", 400);
			customPotionEffects.add(speed);
			customPotionEffects.add(haste);
			customPotionEffects.add(blindness);
			nbt.put("CustomPotionEffects", customPotionEffects);
			nbt.putInt("CustomPotionColor", 65327);
			nbt.putInt("HideFlags", 32);
			CompoundNBT display = new CompoundNBT();
			ListNBT lore = new ListNBT();
			lore.add(
					new StringNBT(ITextComponent.Serializer.toJson(new StringTextComponent("An energetic beverage."))));
			display.put("Lore", lore);
			display.put("Name", new StringNBT(ITextComponent.Serializer.toJson(new StringTextComponent("Velocitas"))));
			nbt.put("display", display);
			VELOCITAS = new ItemStack(Items.POTION);
			VELOCITAS.setTag(nbt);
		}
		{
			CompoundNBT nbt = new CompoundNBT();
			ListNBT customPotionEffects = new ListNBT();
			CompoundNBT glowing = new CompoundNBT();
			glowing.putInt("Id", 24);
			glowing.putInt("Duration", 12000);
			customPotionEffects.add(glowing);
			nbt.put("CustomPotionEffects", customPotionEffects);
			nbt.putInt("CustomPotionColor", 16448000);
			nbt.putInt("HideFlags", 32);
			CompoundNBT display = new CompoundNBT();
			ListNBT lore = new ListNBT();
			lore.add(new StringNBT(ITextComponent.Serializer.toJson(new StringTextComponent("A glowstone extract."))));
			display.put("Lore", lore);
			display.put("Name", new StringNBT(ITextComponent.Serializer.toJson(new StringTextComponent("Luma"))));
			nbt.put("display", display);
			LUMA = new ItemStack(Items.POTION);
			LUMA.setTag(nbt);
		}

		RANDOM_SPECIAL_ITEM = new TreasureEntry("minecraft:air", 1);

		POTION_HEALING = createItemWithNbt(Items.POTION, createPotionTag("minecraft:healing"));
		POTION_HEALING_II = createItemWithNbt(Items.POTION, createPotionTag("minecraft:strong_healing"));

		POTION_REGENERATION = createItemWithNbt(Items.POTION, createPotionTag("minecraft:regeneration"));
		POTION_REGENERATION_LONG = createItemWithNbt(Items.POTION, createPotionTag("minecraft:long_regeneration"));

		POTION_REGENERATION_II = createItemWithNbt(Items.POTION, createPotionTag("minecraft:strong_regeneration"));

		SPLASH_POISON = createItemWithNbt(Items.POTION, createPotionTag("minecraft:poison"));
		SPLASH_POISON_LONG = createItemWithNbt(Items.POTION, createPotionTag("minecraft:long_poison"));
		SPLASH_HARMING = createItemWithNbt(Items.POTION, createPotionTag("minecraft:harming"));
		SPLASH_HARMING_II = createItemWithNbt(Items.POTION, createPotionTag("minecraft:strong_harming"));

		SPECIAL_POTIONS = new ItemStack[] { LAUDANUM, ANIMUS, NECTAR, LUMA, VELOCITAS };

		ENCHANTED_BOOK = new TreasureEntry("minecraft:book", 1).withProcessor((world, rand, theme, lootLevel) -> {
			ItemStack book = new ItemStack(Items.ENCHANTED_BOOK);
			Enchantment enchantment = EnchantedBook.getRandomEnchantment(rand, lootLevel);
			RandomEquipment.enchantItem(book, rand, enchantment, RandomEquipment.getStageMultiplier(lootLevel));
			return book;
		});

		MATERIAL_BLOCKS = new TreasureEntry("minecraft:air", 1).withProcessor((world, rand, theme,
				lootlevel) -> new ItemStack(ForgeRegistries.BLOCKS.getValue(ThemeItems.getMaterial(theme)),
						rand.nextInt(5 + lootlevel * 4)));

	}

	public static CompoundNBT createDisplayTag(String name, String... loreEntries) {
		CompoundNBT display = new CompoundNBT();
		display.put("Name", new StringNBT(ITextComponent.Serializer.toJson(new StringTextComponent(name))));
		ListNBT lore = new ListNBT();
		for (String line : loreEntries)
			lore.add(new StringNBT(ITextComponent.Serializer.toJson(new StringTextComponent(line))));
		if (lore.size() > 0)
			display.put("Lore", lore);
		return display;
	}

	public static CompoundNBT createPotionTag(String potionName) {
		CompoundNBT potion = new CompoundNBT();
		potion.putString("Potion", potionName);
		return potion;
	}

	public static CompoundNBT createEnchantmentTag(String enchantment, int level) {
		CompoundNBT enchantmentTag = new CompoundNBT();
		enchantmentTag.putString("id", enchantment);
		enchantmentTag.putInt("lvl", level);
		return enchantmentTag;
	}

	public static ItemStack createItemWithNbt(Item item, CompoundNBT nbt) {
		ItemStack stack = new ItemStack(item);
		stack.setTag(nbt);
		return stack;
	}

	public static ItemStack getRandomSpecialPotion(Random rand, int stage) {
		int bound = stage == 0 ? 1 : SPECIAL_POTIONS.length;
		return SPECIAL_POTIONS[rand.nextInt(bound)].copy();
	}

}
