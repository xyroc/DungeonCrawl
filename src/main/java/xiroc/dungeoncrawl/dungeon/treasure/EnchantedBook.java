package xiroc.dungeoncrawl.dungeon.treasure;

/*
 * DungeonCrawl (C) 2019 XYROC (XIROC1337), All Rights Reserved 
 */

import java.io.File;
import java.util.HashMap;
import java.util.Random;

import com.google.gson.JsonObject;

import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.loading.FMLPaths;
import net.minecraftforge.registries.ForgeRegistries;
import xiroc.dungeoncrawl.DungeonCrawl;
import xiroc.dungeoncrawl.config.JsonConfig;
import xiroc.dungeoncrawl.util.IJsonConfigurable;

public class EnchantedBook implements IJsonConfigurable {

	public static final String KEY_ENCHANTMENTS_COMMON = "common_enchantments";
	public static final String KEY_ENCHANTMENTS_NORMAL = "normal_enchantments";
	public static final String KEY_ENCHANTMENTS_RARE = "rare_enchantments";

	public static final String[] KEYS = new String[] { KEY_ENCHANTMENTS_COMMON, KEY_ENCHANTMENTS_NORMAL,
			KEY_ENCHANTMENTS_RARE };

	public static final HashMap<String, Object> DEFAULTS;

	public static ResourceLocation[] ENCHANTMENTS_COMMON;
	public static ResourceLocation[] ENCHANTMENTS_NORMAL;
	public static ResourceLocation[] ENCHANTMENTS_RARE;

	static {
		DEFAULTS = new HashMap<String, Object>();
		DEFAULTS.put(KEY_ENCHANTMENTS_COMMON, new ResourceLocation[] { Enchantments.AQUA_AFFINITY.getRegistryName(),
				Enchantments.BANE_OF_ARTHROPODS.getRegistryName(), Enchantments.BLAST_PROTECTION.getRegistryName(),
				Enchantments.KNOCKBACK.getRegistryName(), Enchantments.PROJECTILE_PROTECTION.getRegistryName(),
				Enchantments.FIRE_PROTECTION.getRegistryName(), Enchantments.PROTECTION.getRegistryName(),
				Enchantments.UNBREAKING.getRegistryName(), Enchantments.SMITE.getRegistryName() });
		DEFAULTS.put(KEY_ENCHANTMENTS_NORMAL,
				new ResourceLocation[] { Enchantments.DEPTH_STRIDER.getRegistryName(),
						Enchantments.FIRE_ASPECT.getRegistryName(), Enchantments.IMPALING.getRegistryName(),
						Enchantments.THORNS.getRegistryName(), Enchantments.MULTISHOT.getRegistryName(),
						Enchantments.SHARPNESS.getRegistryName(), Enchantments.EFFICIENCY.getRegistryName(),
						Enchantments.LURE.getRegistryName(), Enchantments.POWER.getRegistryName(),
						Enchantments.PUNCH.getRegistryName(), Enchantments.RESPIRATION.getRegistryName() });
		DEFAULTS.put(KEY_ENCHANTMENTS_RARE,
				new ResourceLocation[] { Enchantments.FIRE_ASPECT.getRegistryName(),
						Enchantments.FLAME.getRegistryName(), Enchantments.FORTUNE.getRegistryName(),
						Enchantments.CHANNELING.getRegistryName(), Enchantments.INFINITY.getRegistryName(),
						Enchantments.LOOTING.getRegistryName(), Enchantments.LOYALTY.getRegistryName(),
						Enchantments.RIPTIDE.getRegistryName(), Enchantments.LUCK_OF_THE_SEA.getRegistryName(),
						Enchantments.PIERCING.getRegistryName(), Enchantments.QUICK_CHARGE.getRegistryName(),
						Enchantments.SILK_TOUCH.getRegistryName(), Enchantments.SWEEPING.getRegistryName() });
	}

	@Override
	public File getFile() {
		return FMLPaths.CONFIGDIR.get().resolve("DungeonCrawl/loot/enchanted_book.json").toFile();
	}

	@Override
	public void load(JsonObject object, File file) {
		ENCHANTMENTS_COMMON = DungeonCrawl.GSON.fromJson(JsonConfig.getOrRewrite(object, KEY_ENCHANTMENTS_COMMON, this),
				ResourceLocation[].class);
		ENCHANTMENTS_NORMAL = DungeonCrawl.GSON.fromJson(JsonConfig.getOrRewrite(object, KEY_ENCHANTMENTS_NORMAL, this),
				ResourceLocation[].class);
		ENCHANTMENTS_RARE = DungeonCrawl.GSON.fromJson(JsonConfig.getOrRewrite(object, KEY_ENCHANTMENTS_RARE, this),
				ResourceLocation[].class);
	}

	@Override
	public JsonObject create(JsonObject object) {
		object.add(KEY_ENCHANTMENTS_COMMON, DungeonCrawl.GSON.toJsonTree(DEFAULTS.get(KEY_ENCHANTMENTS_COMMON)));
		object.add(KEY_ENCHANTMENTS_NORMAL, DungeonCrawl.GSON.toJsonTree(DEFAULTS.get(KEY_ENCHANTMENTS_NORMAL)));
		object.add(KEY_ENCHANTMENTS_RARE, DungeonCrawl.GSON.toJsonTree(DEFAULTS.get(KEY_ENCHANTMENTS_RARE)));
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

	public static Enchantment getRandomEnchantment(Random rand, int lootLevel) {
		if (rand.nextDouble() < getChance(2, lootLevel))
			return ForgeRegistries.ENCHANTMENTS.getValue(ENCHANTMENTS_RARE[rand.nextInt(ENCHANTMENTS_RARE.length)]);
		if (rand.nextDouble() < getChance(1, lootLevel))
			return ForgeRegistries.ENCHANTMENTS.getValue(ENCHANTMENTS_NORMAL[rand.nextInt(ENCHANTMENTS_NORMAL.length)]);
		return ForgeRegistries.ENCHANTMENTS.getValue(ENCHANTMENTS_COMMON[rand.nextInt(ENCHANTMENTS_COMMON.length)]);
	}

	public static double getChance(int rarity, int lootLevel) {
		return (1D + lootLevel - rarity) / (1D + lootLevel);
	}

}
