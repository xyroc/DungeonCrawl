package xiroc.dungeoncrawl.dungeon.treasure;

import java.util.Random;

import net.minecraft.enchantment.Enchantments;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.text.StringTextComponent;
import xiroc.dungeoncrawl.dungeon.monster.RandomEquipment;
import xiroc.dungeoncrawl.util.ItemProcessor;

public class RandomSpecialItem implements ItemProcessor<Random, Integer, Integer> {

	public static final int COLOR = 15961002;

	public static final ItemStack[] ITEMS;

	public static final ItemStack[] RARE_ITEMS;

	public static final ItemStack REINFORCED_BOW, BOOTS_OF_BATTLE, LEATHER_CHESTPLATE, LEATHER_CHESTPLATE_1,
			CHAINMAIL_CHESTPLATE, CHAINMAIL_CHESTPLATE_1, DOOM, THE_SLAYER, DEMON_HUNTER_CROSSBOW;

	static {
		REINFORCED_BOW = new ItemStack(Items.BOW);
		REINFORCED_BOW.addEnchantment(Enchantments.UNBREAKING, 0);
		REINFORCED_BOW.addEnchantment(Enchantments.POWER, 0);
		REINFORCED_BOW.setDisplayName(new StringTextComponent("Reinforced Bow"));

		BOOTS_OF_BATTLE = new ItemStack(Items.LEATHER_BOOTS);
		RandomEquipment.setArmorColor(BOOTS_OF_BATTLE, 15961002);
		BOOTS_OF_BATTLE.addEnchantment(Enchantments.UNBREAKING, 0);
		BOOTS_OF_BATTLE.addEnchantment(Enchantments.PROTECTION, 0);
		BOOTS_OF_BATTLE.setDisplayName(new StringTextComponent("Boots of Battle"));

		LEATHER_CHESTPLATE = new ItemStack(Items.LEATHER_CHESTPLATE);
		RandomEquipment.setArmorColor(LEATHER_CHESTPLATE, COLOR);
		LEATHER_CHESTPLATE.addEnchantment(Enchantments.PROTECTION, 0);

		LEATHER_CHESTPLATE_1 = new ItemStack(Items.LEATHER_CHESTPLATE);
		RandomEquipment.setArmorColor(LEATHER_CHESTPLATE_1, COLOR);
		LEATHER_CHESTPLATE_1.addEnchantment(Enchantments.UNBREAKING, 0);
		LEATHER_CHESTPLATE_1.addEnchantment(Enchantments.PROTECTION, 0);

		CHAINMAIL_CHESTPLATE = new ItemStack(Items.CHAINMAIL_CHESTPLATE);
		CHAINMAIL_CHESTPLATE.addEnchantment(Enchantments.PROTECTION, 1);

		CHAINMAIL_CHESTPLATE_1 = new ItemStack(Items.CHAINMAIL_CHESTPLATE);
		CHAINMAIL_CHESTPLATE_1.addEnchantment(Enchantments.PROTECTION, 0);
		CHAINMAIL_CHESTPLATE_1.addEnchantment(Enchantments.THORNS, 0);
		
		DOOM = new ItemStack(Items.GOLDEN_SWORD);
		DOOM.addEnchantment(Enchantments.SHARPNESS, 0);
		DOOM.addEnchantment(Enchantments.FIRE_ASPECT, 1);
		DOOM.addEnchantment(Enchantments.UNBREAKING, 0);
		
		THE_SLAYER = new ItemStack(Items.DIAMOND_SWORD);
		THE_SLAYER.addEnchantment(Enchantments.SHARPNESS, 3);
		
		DEMON_HUNTER_CROSSBOW = new ItemStack(Items.CROSSBOW);
		DEMON_HUNTER_CROSSBOW.addEnchantment(Enchantments.PIERCING, 1);
		DEMON_HUNTER_CROSSBOW.addEnchantment(Enchantments.MULTISHOT, 0);
		DEMON_HUNTER_CROSSBOW.addEnchantment(Enchantments.QUICK_CHARGE, 0);
		DEMON_HUNTER_CROSSBOW.setDisplayName(new StringTextComponent("Demon Hunter"));

		ITEMS = new ItemStack[] { REINFORCED_BOW, BOOTS_OF_BATTLE, LEATHER_CHESTPLATE, LEATHER_CHESTPLATE_1,
				CHAINMAIL_CHESTPLATE, CHAINMAIL_CHESTPLATE_1, DOOM };

		RARE_ITEMS = new ItemStack[] { THE_SLAYER, DEMON_HUNTER_CROSSBOW };
	}

	@Override
	public ItemStack generate(Random rand, Integer theme, Integer lootLevel) {
		if (rand.nextDouble() < 0.075 * lootLevel)
			return RARE_ITEMS[rand.nextInt(RARE_ITEMS.length)].copy();
		return ITEMS[rand.nextInt(ITEMS.length)].copy();
	}

}
