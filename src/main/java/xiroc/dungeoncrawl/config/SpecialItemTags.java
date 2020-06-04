package xiroc.dungeoncrawl.config;

/*
 * DungeonCrawl (C) 2019 - 2020 XYROC (XIROC1337), All Rights Reserved 
 */

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import com.google.gson.JsonObject;

import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentData;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.item.ArmorItem;
import net.minecraft.item.BowItem;
import net.minecraft.item.CrossbowItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.SwordItem;
import net.minecraft.item.ToolItem;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.INBT;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.StringTextComponent;
import net.minecraftforge.fml.loading.FMLPaths;
import net.minecraftforge.registries.ForgeRegistries;
import xiroc.dungeoncrawl.DungeonCrawl;
import xiroc.dungeoncrawl.dungeon.monster.RandomEquipment;
import xiroc.dungeoncrawl.util.IJsonConfigurable;

public class SpecialItemTags implements IJsonConfigurable {

	public static final HashMap<String, TagProcessor> TAG_PROCESSORS;

	public static final String EFFECT_ENCHANT = "enchant", EFFECT_ENCHANTMENT_MULTIPLIER = "multiply_enchantment_level",
			EFFECT_RANDOM_ENCHANT = "enchant_randomly";

	public static final HashMap<String, Object> DEFAULTS;

	public static final String KEY_GENERIC = "generic", KEY_ARMOR = "armor", KEY_TOOL = "tool", KEY_SWORD = "sword",
			KEY_RANGED_WEAPON = "ranged_weapon", KEY_PREFIX = "generic_prefix", KEY_PREFIX_ARMOR = "armor_prefix",
			KEY_PREFIX_TOOL = "tool_prefix", KEY_PREFIX_SWORD = "sword_prefix",
			KEY_PREFIX_RANGED_WEAPON = "ranged_weapon_prefix";

	public static final String[] KEYS = new String[] { KEY_GENERIC, KEY_ARMOR, KEY_TOOL, KEY_SWORD, KEY_RANGED_WEAPON,
			KEY_PREFIX, KEY_PREFIX_ARMOR, KEY_PREFIX_TOOL, KEY_PREFIX_SWORD, KEY_PREFIX_RANGED_WEAPON };

	public static SpecialItemTag[] GENERIC, ARMOR, TOOL, SWORD, RANGED_WEAPON, PREFIX_GENERIC, PREFIX_ARMOR,
			PREFIX_TOOL, PREFIX_SWORD, PREFIX_RANGED_WEAPON;

	static {
		TAG_PROCESSORS = new HashMap<String, TagProcessor>();
		TAG_PROCESSORS.put(EFFECT_ENCHANT, (item, tag, data, rand, stage) -> {
			for (ResourceLocation enchantment : tag.enchantmentResources) {
				Enchantment ench = ForgeRegistries.ENCHANTMENTS.getValue(enchantment);
				if (ench != null && EnchantmentHelper
						.areAllCompatibleWith(EnchantmentHelper.getEnchantments(item).keySet(), ench)) {
					if (tag.level != null)
						item.addEnchantment(ench, tag.level);
					else
						RandomEquipment.enchantItem(item, ench, data.enchantmentLevelMultiplier);
				}
			}
		});
		TAG_PROCESSORS.put(EFFECT_ENCHANTMENT_MULTIPLIER, (item, tag, data, rand, stage) -> {
			data.enchantmentLevelMultiplier = tag.enchantmentMultiplier;
		});
		TAG_PROCESSORS.put(EFFECT_RANDOM_ENCHANT, (item, tag, data, rand, stage) -> {
			List<EnchantmentData> enchantments = EnchantmentHelper.buildEnchantmentList(rand, item, 5 + 10 * stage,
					stage > 0);
			for (Map.Entry<Enchantment, Integer> entry : EnchantmentHelper.getEnchantments(item).entrySet())
				EnchantmentHelper.removeIncompatible(enchantments,
						new EnchantmentData(entry.getKey(), entry.getValue()));
			if (enchantments.size() < 1)
				return;
			EnchantmentData enchantment = enchantments.get(rand.nextInt(enchantments.size()));
			double stageMultiplier = RandomEquipment.getStageMultiplier(stage);
			int level = (int) (data.enchantmentLevelMultiplier > stageMultiplier
					? enchantment.enchantment.getMaxLevel() * data.enchantmentLevelMultiplier
					: enchantment.enchantment.getMaxLevel() * stageMultiplier);
			if (level < 1)
				level = 1;
			item.addEnchantment(enchantment.enchantment, level);
		});

		DEFAULTS = new HashMap<String, Object>();
		DEFAULTS.put(KEY_GENERIC, new SpecialItemTag[0]);
		DEFAULTS.put(KEY_ARMOR,
				new SpecialItemTag[] { new SpecialItemTag("Deflection", EFFECT_ENCHANT, 0, "minecraft:thorns"),
						new SpecialItemTag("Protection", EFFECT_ENCHANT, 0, "minecraft:protection"),
						new SpecialItemTag("the Apprentice", EFFECT_RANDOM_ENCHANT, 0),
						new SpecialItemTag("the Master Blacksmith", EFFECT_ENCHANT, 4, 3, "minecraft:protection",
								"minecraft:unbreaking") });
		DEFAULTS.put(KEY_TOOL, new SpecialItemTag[] {
				new SpecialItemTag("the Harvester", EFFECT_ENCHANT, 0, "minecraft:efficiency") });
		DEFAULTS.put(KEY_SWORD,
				new SpecialItemTag[] {
						new SpecialItemTag("the Cinder Lord", EFFECT_ENCHANT, 0, "minecraft:fire_aspect"),
						new SpecialItemTag("the Rogue Knight", EFFECT_ENCHANT, 0, "minecraft:sweeping"),
						new SpecialItemTag("the Monster Slayer", EFFECT_ENCHANT, 0, "minecraft:smite"),
						new SpecialItemTag("the Goblin Slayer", EFFECT_ENCHANT, 0, "minecraft:sharpness") });
		DEFAULTS.put(KEY_RANGED_WEAPON, new SpecialItemTag[0]);
		DEFAULTS.put(KEY_PREFIX, new SpecialItemTag[] { new SpecialItemTag("Arcane", EFFECT_RANDOM_ENCHANT, 0) });
		DEFAULTS.put(KEY_PREFIX_ARMOR,
				new SpecialItemTag[] { new SpecialItemTag("Tempered", EFFECT_ENCHANT, 0, "minecraft:protection"),
						new SpecialItemTag("Fire Proof", EFFECT_ENCHANT, 0, "minecraft:fire_protection"),
						new SpecialItemTag("Resistant", EFFECT_ENCHANT, 0, "minecraft:blast_protection"),
						new SpecialItemTag("Reinforced", EFFECT_ENCHANT, 0, "minecraft:projectile_protection"),
						new SpecialItemTag("Cursed", EFFECT_ENCHANT, 0, "minecraft:vanishing_curse"),
						new SpecialItemTag("Cursed", EFFECT_ENCHANT, 0, "minecraft:binding_curse"),
						new SpecialItemTag("Reforged", EFFECT_ENCHANTMENT_MULTIPLIER, 1, 0.5D),
						new SpecialItemTag("Surplus", EFFECT_ENCHANTMENT_MULTIPLIER, 1, 0.75),
						new SpecialItemTag("Legendary", EFFECT_ENCHANTMENT_MULTIPLIER, 1, 1),
						new SpecialItemTag("Genuine", EFFECT_ENCHANTMENT_MULTIPLIER, 1, 1) });
		DEFAULTS.put(KEY_PREFIX_TOOL,
				new SpecialItemTag[] { new SpecialItemTag("Durable", EFFECT_ENCHANT, 0, "minecraft:unbreaking"),
						new SpecialItemTag("Hardened", EFFECT_ENCHANT, 0, "minecraft:unbreaking"),
						new SpecialItemTag("Blessed", EFFECT_ENCHANT, 0, "minecraft:efficiency"),
						new SpecialItemTag("Reforged", EFFECT_ENCHANTMENT_MULTIPLIER, 1, 0.5D) });
		DEFAULTS.put(KEY_PREFIX_SWORD,
				new SpecialItemTag[] { new SpecialItemTag("Holy", EFFECT_ENCHANT, 0, "minecraft:smite"),
						new SpecialItemTag("Tempered", "enchant", 0, "minecraft:sharpness", "minecraft:unbreaking"),
						new SpecialItemTag("Reforged", EFFECT_ENCHANTMENT_MULTIPLIER, 1, 0.5D) });
		DEFAULTS.put(KEY_PREFIX_RANGED_WEAPON,
				new SpecialItemTag[] {
						new SpecialItemTag("Refined", EFFECT_ENCHANT, 0, "minecraft:power", "minecraft:unbreaking"),
						new SpecialItemTag("Powerful", EFFECT_ENCHANT, 0, "minecraft:punch") });

//		DEFAULTS.put(KEY_ARMOR_ITEMS,
//				new String[] { "minecraft:leather_boots", "minecraft:leather_leggings", "minecraft:leather_chestplate",
//						"minecraft:leather_helmet", "minecraft:chainmail_boots", "minecraft:chainmail_leggings",
//						"minecraft:chainmail_chestplate", "minecraft:chainmail_helmet", "minecraft:golden_boots",
//						"minecraft:golden_leggings", "minecraft:golden_chestplate", "minecraft:golden_helmet",
//						"minecraft:iron_boots", "minecraft:iron_leggings", "minecraft:iron_chestplate",
//						"minecraft:iron_helmet", "minecraft:diamond_boots", "minecraft:diamond_leggings",
//						"minecraft:diamond_chestplate", "minecraft:diamond_helmet" });

	}

	public static ItemStack rollForTagsAndApply(Item item, int lootLevel, Random rand) {
		ItemStack stack = new ItemStack(item);
		String name = stack.getDisplayName().getString();
		return rollForTagsAndApply(item, lootLevel, rand, name);
	}

	public static ItemStack rollForTagsAndApply(Item item, int lootLevel, Random rand, String name) {
		ItemData data = new ItemData(RandomEquipment.getStageMultiplier(lootLevel));
		ItemStack stack = new ItemStack(item);
		SpecialItemTag prefix = null, tag = null;
		if (item instanceof ArmorItem) {
			prefix = PREFIX_ARMOR.length > 3 || rand.nextDouble() > PREFIX_ARMOR.length * 0.25
					? PREFIX_ARMOR[rand.nextInt(PREFIX_ARMOR.length)]
					: null;
			tag = ARMOR.length > 3 || rand.nextDouble() > ARMOR.length * 0.25 ? ARMOR[rand.nextInt(ARMOR.length)]
					: null;
		} else if (item instanceof SwordItem) {
			prefix = PREFIX_SWORD.length > 0 ? PREFIX_SWORD[rand.nextInt(PREFIX_SWORD.length)] : null;
			tag = SWORD.length > 0 ? SWORD[rand.nextInt(SWORD.length)] : null;
		} else if (item instanceof ToolItem) {
			prefix = PREFIX_TOOL.length > 0 ? PREFIX_TOOL[rand.nextInt(PREFIX_TOOL.length)] : null;
			tag = TOOL.length > 0 ? TOOL[rand.nextInt(TOOL.length)] : null;
		} else if (item instanceof BowItem || item instanceof CrossbowItem) {
			prefix = PREFIX_RANGED_WEAPON.length > 0 ? PREFIX_RANGED_WEAPON[rand.nextInt(PREFIX_RANGED_WEAPON.length)]
					: null;
			tag = RANGED_WEAPON.length > 0 ? RANGED_WEAPON[rand.nextInt(RANGED_WEAPON.length)] : null;
		} else {
			prefix = PREFIX_GENERIC.length > 0 ? PREFIX_GENERIC[rand.nextInt(PREFIX_GENERIC.length)] : null;
			tag = GENERIC.length > 0 ? GENERIC[rand.nextInt(GENERIC.length)] : null;
		}
		if (prefix != null) {
			applyTag(prefix, stack, data, rand, lootLevel);
			name = prefix.name + " " + name;
		}
		if (tag != null) {
			applyTag(tag, stack, data, rand, lootLevel);
			name += " of " + tag.name;
		}
		stack.setDisplayName(new StringTextComponent(name));
		return stack;
	}

	public static void applyTag(SpecialItemTag tag, ItemStack stack, ItemData data, Random rand, int lootLevel) {
		TAG_PROCESSORS.get(tag.effect).apply(stack, tag, data, rand, lootLevel);
	}

	public static void buildList(SpecialItemTag[] list) {
		for (SpecialItemTag tag : list)
			tag.build();
	}

	public static boolean hasEnchantment(ItemStack stack, String enchantment) {
		for (INBT nbt : stack.getEnchantmentTagList()) {
			if (((CompoundNBT) nbt).getString("id").equals(enchantment))
				return true;
		}
		return false;
	}

	/*
	 * Used to store data to enchant special items.
	 */
	public static class ItemData {

		public double enchantmentLevelMultiplier;

		public ItemData() {
			this.enchantmentLevelMultiplier = 1.0;
		}

		public ItemData(double enchantmentLevelMultiplier) {
			this.enchantmentLevelMultiplier = enchantmentLevelMultiplier;
		}

	}

	public static class SpecialItemTag {

		public String name, effect;
		public int rarity;
		public String[] enchantments;
		public ResourceLocation[] enchantmentResources;
		public Double enchantmentMultiplier;
		public Integer level;

		public SpecialItemTag(String name, String effect, int rarity) {
			this.name = name;
			this.effect = effect;
			this.rarity = rarity;
		}

		public SpecialItemTag(String name, String effect, int rarity, String... enchantments) {
			this(name, effect, rarity);
			this.enchantments = enchantments;
		}

		public SpecialItemTag(String name, String effect, int rarity, int level, String... enchantments) {
			this(name, effect, rarity);
			this.enchantments = enchantments;
			this.level = level;
		}

		public SpecialItemTag(String name, String effect, int rarity, double enchantmentMultiplier) {
			this(name, effect, rarity);
			this.enchantmentMultiplier = enchantmentMultiplier;
		}

		public SpecialItemTag build() {
			if (enchantments != null) {
				enchantmentResources = new ResourceLocation[enchantments.length];
				for (int i = 0; i < enchantments.length; i++)
					enchantmentResources[i] = new ResourceLocation(enchantments[i]);
			}
			return this;
		}

	}

	@FunctionalInterface
	public static interface TagProcessor {

		public void apply(ItemStack itemStack, SpecialItemTag tag, ItemData data, Random rand, int stage);

	}

	@Override
	public File getFile() {
		return FMLPaths.CONFIGDIR.get().resolve("DungeonCrawl/loot/specialItemTags.json").toFile();
	}

	@Override
	public void load(JsonObject object, File file) {
		GENERIC = DungeonCrawl.GSON.fromJson(JsonConfig.getOrRewrite(object, KEY_GENERIC, this),
				SpecialItemTag[].class);
		ARMOR = DungeonCrawl.GSON.fromJson(JsonConfig.getOrRewrite(object, KEY_ARMOR, this), SpecialItemTag[].class);
		TOOL = DungeonCrawl.GSON.fromJson(JsonConfig.getOrRewrite(object, KEY_TOOL, this), SpecialItemTag[].class);
		SWORD = DungeonCrawl.GSON.fromJson(JsonConfig.getOrRewrite(object, KEY_SWORD, this), SpecialItemTag[].class);
		RANGED_WEAPON = DungeonCrawl.GSON.fromJson(JsonConfig.getOrRewrite(object, KEY_RANGED_WEAPON, this),
				SpecialItemTag[].class);
		PREFIX_GENERIC = DungeonCrawl.GSON.fromJson(JsonConfig.getOrRewrite(object, KEY_PREFIX, this),
				SpecialItemTag[].class);
		PREFIX_ARMOR = DungeonCrawl.GSON.fromJson(JsonConfig.getOrRewrite(object, KEY_PREFIX_ARMOR, this),
				SpecialItemTag[].class);
		PREFIX_TOOL = DungeonCrawl.GSON.fromJson(JsonConfig.getOrRewrite(object, KEY_PREFIX_TOOL, this),
				SpecialItemTag[].class);
		PREFIX_SWORD = DungeonCrawl.GSON.fromJson(JsonConfig.getOrRewrite(object, KEY_PREFIX_SWORD, this),
				SpecialItemTag[].class);
		PREFIX_RANGED_WEAPON = DungeonCrawl.GSON
				.fromJson(JsonConfig.getOrRewrite(object, KEY_PREFIX_RANGED_WEAPON, this), SpecialItemTag[].class);

		buildList(GENERIC);
		buildList(ARMOR);
		buildList(TOOL);
		buildList(SWORD);
		buildList(RANGED_WEAPON);
		buildList(PREFIX_GENERIC);
		buildList(PREFIX_ARMOR);
		buildList(PREFIX_TOOL);
		buildList(PREFIX_SWORD);
		buildList(PREFIX_RANGED_WEAPON);
	}

	@Override
	public JsonObject create(JsonObject object) {
		for (String key : KEYS)
			object.add(key, DungeonCrawl.GSON.toJsonTree(DEFAULTS.get(key)));
		return object;
	}

	@Override
	public HashMap<String, Object> getDefaults() {
		return DEFAULTS;
	}

	@Override
	public String[] getKeys() {
		return KEYS;
	}

	@Override
	public int getVersion() {
		return 0;
	}

}
