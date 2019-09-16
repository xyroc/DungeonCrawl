package xiroc.dungeoncrawl.dungeon.treasure;

/*
 * DungeonCrawl (C) 2019 XYROC (XIROC1337), All Rights Reserved 
 */

import net.minecraft.enchantment.Enchantment;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;

/*
 * DungeonCrawl (C) 2019 XYROC (XIROC1337), All Rights Reserved 
 */

import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.nbt.StringNBT;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraftforge.registries.ForgeRegistries;
import xiroc.dungeoncrawl.dungeon.monster.RandomEquipment;

public class TreasureItems {

	// Potions from the original Roguelike Dungeons
	public static final TreasureEntry LAUDANUM;
	public static final TreasureEntry ANIMUS;
	public static final TreasureEntry NECTAR;
	public static final TreasureEntry LUMA;

	// New Potions
	public static final TreasureEntry VELOCITAS;

	public static final TreasureEntry POTION_HEALING;
	public static final TreasureEntry POTION_HEALING_II;
	public static final TreasureEntry POTION_REGENERATION;
	public static final TreasureEntry POTION_REGENERATION_LONG;
	public static final TreasureEntry POTION_REGENERATION_II;

	public static final TreasureEntry SPLASH_POISON;
	public static final TreasureEntry SPLASH_POISON_LONG;
	public static final TreasureEntry SPLASH_HARMING;
	public static final TreasureEntry SPLASH_HARMING_II;

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
			LAUDANUM = new TreasureEntry("minecraft:potion", 1).setNBT(nbt);
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
			ANIMUS = new TreasureEntry("minecraft:potion", 1).setNBT(nbt);
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
			NECTAR = new TreasureEntry("minecraft:potion", 1).setNBT(nbt);
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
			VELOCITAS = new TreasureEntry("minecraft:potion", 1).setNBT(nbt);
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
			LUMA = new TreasureEntry("minecraft:potion", 1).setNBT(nbt);
		}

		// {
		// CompoundNBT nbt = new CompoundNBT();
		// CompoundNBT display = createDisplayTag("Boots of Battle");
		// display.putInt("color", 14067655);
		// nbt.put("Display", display);
		// BOOTS_OF_BATTLE = new TreasureEntry("minecraft:leather_boots",
		// 1).setNBT(nbt)
		// .withEnchantments(new ItemEnchantment[] { new ItemEnchantment(0, 0)
		// });
		// }

		RANDOM_SPECIAL_ITEM = new TreasureEntry("minecraft:air", 1).withProcessor(new RandomSpecialItem());

		POTION_HEALING = new TreasureEntry("minecraft:potion", 1).setNBT(createPotionTag("minecraft:healing"));
		POTION_HEALING_II = new TreasureEntry("minecraft:potion", 1)
				.setNBT(createPotionTag("minecraft:strong_healing"));
		POTION_REGENERATION = new TreasureEntry("minecraft:potion", 1)
				.setNBT(createPotionTag("minecraft:regeneration"));
		POTION_REGENERATION_LONG = new TreasureEntry("minecraft:potion", 1)
				.setNBT(createPotionTag("minecraft:long_regeneration"));
		POTION_REGENERATION_II = new TreasureEntry("minecraft:potion", 1)
				.setNBT(createPotionTag("minecraft:strong_regeneration"));

		SPLASH_POISON = new TreasureEntry("minecraft:splash_potion", 1).setNBT(createPotionTag("minecraft:poison"));
		SPLASH_POISON_LONG = new TreasureEntry("minecraft:splash_potion", 1)
				.setNBT(createPotionTag("minecraft:long_poison"));
		SPLASH_HARMING = new TreasureEntry("minecraft:splash_potion", 1).setNBT(createPotionTag("minecraft:harming"));
		SPLASH_HARMING_II = new TreasureEntry("minecraft:splash_potion", 1)
				.setNBT(createPotionTag("minecraft:strong_harming"));

		ENCHANTED_BOOK = new TreasureEntry("minecraft:book", 1).withProcessor((rand, theme, lootLevel) -> {
			ItemStack book = new ItemStack(Items.ENCHANTED_BOOK);
			Enchantment enchantment = EnchantedBook.getRandomEnchantment(rand, lootLevel);
			RandomEquipment.enchantItem(book, rand, enchantment, RandomEquipment.getStageMultiplier(lootLevel));
			return book;
		});

		MATERIAL_BLOCKS = new TreasureEntry("minecraft:air", 1).withProcessor((rand, theme, lootlevel) -> new ItemStack(
				ForgeRegistries.BLOCKS.getValue(MaterialBlocks.getMaterial(theme)), rand.nextInt(5 + lootlevel * 4)));
		
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

}
