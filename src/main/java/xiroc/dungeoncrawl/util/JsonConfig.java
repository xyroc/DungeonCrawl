package xiroc.dungeoncrawl.util;

/*
 * DungeonCrawl (C) 2019 XYROC (XIROC1337), All Rights Reserved 
 */

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Lists;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.loading.FMLPaths;
import xiroc.dungeoncrawl.DungeonCrawl;
import xiroc.dungeoncrawl.dungeon.monster.ArmorSet;

public class JsonConfig {

	public static final String KEY_BIOME_BLACKLIST = "biome_blacklist",
			KEY_BIOME_OVERWORLD_BLACKLIST = "biome_overworld_blacklist", KEY_BOWS = "bows",
			KEY_COLORED_ARMOR = "colored_armor", KEY_SWORDS = "swords", KEY_SWORDS_RARE = "swords_rare",
			KEY_PICKAXES = "pickaxes", KEY_AXES = "axes", KEY_ARMOR_STAGE_1 = "armor_sets_stage_1",
			KEY_ARMOR_STAGE_2 = "armor_sets_stage_2", KEY_ARMOR_STAGE_3 = "armor_sets_stage_3",
			KEY_ARMOR_RARE = "armor_rare", KEY_BOW_ENCHANTMENTS = "bow_enchantments",
			KEY_SWORD_ENCHANTMENTS = "sword_enchantments", KEY_PICKAXE_ENCHANTMENTS = "pickaxe_enchantments",
			KEY_AXE_ENCHANTMENTS = "axe_enchantments", KEY_ARMOR_ENCHANTMENTS = "armor_enchantments",
			KEY_ASSUMPTION_SEARCHLIST = "assumption_searchlist";

	public static final String[] KEYS = new String[] { KEY_ARMOR_ENCHANTMENTS, KEY_ARMOR_RARE, KEY_ARMOR_STAGE_1,
			KEY_ARMOR_STAGE_2, KEY_ARMOR_STAGE_3, KEY_AXE_ENCHANTMENTS, KEY_AXES, KEY_BIOME_BLACKLIST,
			KEY_BIOME_OVERWORLD_BLACKLIST, KEY_BOW_ENCHANTMENTS, KEY_BOWS, KEY_COLORED_ARMOR, KEY_PICKAXE_ENCHANTMENTS,
			KEY_PICKAXES, KEY_SWORD_ENCHANTMENTS, KEY_SWORDS, KEY_SWORDS_RARE, KEY_ASSUMPTION_SEARCHLIST };

	public static List<?> BIOME_BLACKLIST, BIOME_OVERWORLD_BLACKLIST;

	public static String[] ASSUMPTION_SEARCHLIST;

	public static Set<?> COLORED_ARMOR = ImmutableSet.<String>builder().add("minecraft:leather_boots")
			.add("minecraft:leather_pants").add("minecraft:leather_chestplate").add("minecraft:leather_helmet").build();

	public static ResourceLocation[] BOWS, SWORDS, SWORDS_RARE, PICKAXES, AXES, BOW_ENCHANTMENTS, SWORD_ENCHANTMENTS,
			PICKAXE_ENCHANTMENTS, AXE_ENCHANTMENTS, ARMOR_ENCHANTMENTS;

	public static ArmorSet[] ARMOR_SETS_1, ARMOR_SETS_2, ARMOR_SETS_3, ARMOR_SETS_RARE;

	static {
		File file = FMLPaths.CONFIGDIR.get().resolve("DungeonCrawl/").toFile();
		File configFile = new File(file, "config.json");
		if (!configFile.exists()) {
			DungeonCrawl.LOGGER.info("Creating the json config file at {}", configFile.getAbsolutePath());
			write(file);
		}
		try {
			Gson gson = DungeonCrawl.GSON;
			JsonObject object = gson.fromJson(new FileReader(configFile), JsonObject.class);
			BIOME_BLACKLIST = gson.fromJson(getOrRewrite(object, KEY_BIOME_BLACKLIST, file), ArrayList.class);
			BIOME_OVERWORLD_BLACKLIST = gson.fromJson(getOrRewrite(object, KEY_BIOME_OVERWORLD_BLACKLIST, file),
					ArrayList.class);

			COLORED_ARMOR = gson.fromJson(getOrRewrite(object, KEY_COLORED_ARMOR, file), Set.class);

			BOWS = gson.fromJson(getOrRewrite(object, KEY_BOWS, file), ResourceLocation[].class);
			SWORDS = gson.fromJson(getOrRewrite(object, KEY_SWORDS, file), ResourceLocation[].class);
			SWORDS_RARE = gson.fromJson(getOrRewrite(object, KEY_SWORDS_RARE, file), ResourceLocation[].class);
			PICKAXES = gson.fromJson(getOrRewrite(object, KEY_PICKAXES, file), ResourceLocation[].class);
			AXES = gson.fromJson(getOrRewrite(object, KEY_AXES, file), ResourceLocation[].class);

			ARMOR_SETS_1 = gson.fromJson(getOrRewrite(object, KEY_ARMOR_STAGE_1, file), ArmorSet[].class);
			ARMOR_SETS_2 = gson.fromJson(getOrRewrite(object, KEY_ARMOR_STAGE_2, file), ArmorSet[].class);
			ARMOR_SETS_3 = gson.fromJson(getOrRewrite(object, KEY_ARMOR_STAGE_3, file), ArmorSet[].class);

			ARMOR_SETS_RARE = gson.fromJson(getOrRewrite(object, KEY_ARMOR_RARE, file), ArmorSet[].class);

			BOW_ENCHANTMENTS = gson.fromJson(getOrRewrite(object, KEY_BOW_ENCHANTMENTS, file),
					ResourceLocation[].class);
			SWORD_ENCHANTMENTS = gson.fromJson(getOrRewrite(object, KEY_SWORD_ENCHANTMENTS, file),
					ResourceLocation[].class);
			AXE_ENCHANTMENTS = gson.fromJson(getOrRewrite(object, KEY_AXE_ENCHANTMENTS, file),
					ResourceLocation[].class);
			PICKAXE_ENCHANTMENTS = gson.fromJson(getOrRewrite(object, KEY_PICKAXE_ENCHANTMENTS, file),
					ResourceLocation[].class);
			ARMOR_ENCHANTMENTS = gson.fromJson(getOrRewrite(object, KEY_ARMOR_ENCHANTMENTS, file),
					ResourceLocation[].class);

			ASSUMPTION_SEARCHLIST = gson.fromJson(getOrRewrite(object, KEY_ASSUMPTION_SEARCHLIST, configFile),
					String[].class);

		} catch (Exception e) {
			DungeonCrawl.LOGGER.error("Failed to load the json config.");
			e.printStackTrace();
		}

	}

	public static void write(File file) {
		file.mkdirs();
		JsonObject object = new JsonObject();
		object.add(KEY_BIOME_BLACKLIST, DungeonCrawl.GSON.toJsonTree(JsonConfigManager.BIOME_BLACKLIST));
		object.add(KEY_BIOME_OVERWORLD_BLACKLIST,
				DungeonCrawl.GSON.toJsonTree(JsonConfigManager.BIOME_OVERWORLD_BLACKLIST));
		object.add(KEY_BOWS, DungeonCrawl.GSON.toJsonTree(JsonConfigManager.BOWS));
		object.add(KEY_COLORED_ARMOR, DungeonCrawl.GSON.toJsonTree(JsonConfigManager.COLORED_ARMOR));
		object.add(KEY_SWORDS, DungeonCrawl.GSON.toJsonTree(JsonConfigManager.SWORDS));
		object.add(KEY_SWORDS_RARE, DungeonCrawl.GSON.toJsonTree(JsonConfigManager.SWORDS_RARE));
		object.add(KEY_PICKAXES, DungeonCrawl.GSON.toJsonTree(JsonConfigManager.PICKAXES));
		object.add(KEY_AXES, DungeonCrawl.GSON.toJsonTree(JsonConfigManager.AXES));
		object.add(KEY_ARMOR_STAGE_1, DungeonCrawl.GSON.toJsonTree(JsonConfigManager.ARMOR_SETS_1));
		object.add(KEY_ARMOR_STAGE_2, DungeonCrawl.GSON.toJsonTree(JsonConfigManager.ARMOR_SETS_2));
		object.add(KEY_ARMOR_STAGE_3, DungeonCrawl.GSON.toJsonTree(JsonConfigManager.ARMOR_SETS_3));
		object.add(KEY_ARMOR_RARE, DungeonCrawl.GSON.toJsonTree(JsonConfigManager.ARMOR_SETS_RARE));
		object.add(KEY_BOW_ENCHANTMENTS, DungeonCrawl.GSON.toJsonTree(JsonConfigManager.BOW_ENCHANTMENTS));
		object.add(KEY_SWORD_ENCHANTMENTS, DungeonCrawl.GSON.toJsonTree(JsonConfigManager.SWORD_ENCHANTMENTS));
		object.add(KEY_PICKAXE_ENCHANTMENTS, DungeonCrawl.GSON.toJsonTree(JsonConfigManager.PICKAXE_ENCHANTMENTS));
		object.add(KEY_AXE_ENCHANTMENTS, DungeonCrawl.GSON.toJsonTree(JsonConfigManager.AXE_ENCHANTMENTS));
		object.add(KEY_ARMOR_ENCHANTMENTS, DungeonCrawl.GSON.toJsonTree(JsonConfigManager.ARMOR_ENCHANTMENTS));
		object.add(KEY_ASSUMPTION_SEARCHLIST, DungeonCrawl.GSON.toJsonTree(JsonConfigManager.ASSUMPTION_SEARCHLIST));
		try {
			FileWriter writer = new FileWriter(new File(file, "config.json"));
			DungeonCrawl.GSON.toJson(object, writer);
			writer.flush();
			writer.close();
		} catch (Exception e) {
			DungeonCrawl.LOGGER.error("An error occured whilst trying to create the json config file.");
			e.printStackTrace();
		}
	}

	public static JsonElement getOrRewrite(JsonObject object, String name, File file) {
		return getOrRewrite(object, name, file, false);
	}

	public static JsonElement getOrRewrite(JsonObject object, String name, File file, boolean rerun) {
		if (object.get(name) != null) {
			return object.get(name);
		} else {
			if (rerun) {
				DungeonCrawl.LOGGER.error("Cant find \"{}\" in {}, even after rewriting the file.", name,
						file.getAbsolutePath());
				return DungeonCrawl.GSON.toJsonTree(JsonConfigManager.DEFAULTS.get(name));
			}
			DungeonCrawl.LOGGER.info("Rewriting {} due to missing data.", file.getAbsolutePath());
			JsonConfigManager.rewrite(new File(file, "config.json"));
			try {
				return getOrRewrite(
						DungeonCrawl.GSON.fromJson(new FileReader(new File(file, "config.json")), JsonObject.class),
						name, file, true);
			} catch (Exception e) {
				e.printStackTrace();
				return DungeonCrawl.GSON.toJsonTree(JsonConfigManager.DEFAULTS.get(name));
			}
		}
	}

	public static class JsonConfigManager {

		public static final List<?> BIOME_BLACKLIST = Lists.newArrayList("minecraft:the_end", "minecraft:nether",
				"minecraft:small_end_islands", "minecraft:end_midlands", "minecraft:end_highlands",
				"minecraft:end_barrens", "minecraft:the_void", "biomesoplenty:ashen_inferno",
				"biomesopenty:undergarden", "biomesoplenty:boneyard", "biomesoplenty:visceral_heap");

		public static final List<?> BIOME_OVERWORLD_BLACKLIST = Lists.newArrayList();

		public static final Set<?> COLORED_ARMOR = ImmutableSet.<String>builder().add("minecraft:leather_boots")
				.add("minecraft:leather_pants").add("minecraft:leather_chestplate").add("minecraft:leather_helmet")
				.build();

		public static final ResourceLocation[] BOWS = new ResourceLocation[] { new ResourceLocation("minecraft:bow") };

		public static final ResourceLocation[] SWORDS = new ResourceLocation[] {
				new ResourceLocation("minecraft:stone_sword"), new ResourceLocation("minecraft:golden_sword"),
				new ResourceLocation("minecraft:iron_sword") };

		public static final ResourceLocation[] SWORDS_RARE = new ResourceLocation[] {
				new ResourceLocation("minecraft:wooden_sword"), new ResourceLocation("minecraft:diamond_sword") };

		public static final ResourceLocation[] PICKAXES = new ResourceLocation[] {
				new ResourceLocation("minecraft:stone_pickaxe"), new ResourceLocation("minecraft:golden_pickaxe"),
				new ResourceLocation("minecraft:iron_pickaxe"), };
		public static final ResourceLocation[] AXES = new ResourceLocation[] {
				new ResourceLocation("minecraft:stone_axe"), new ResourceLocation("minecraft:golden_axe"),
				new ResourceLocation("minecraft:iron_axe"), new ResourceLocation("minecraft:diamond_axe") };

		public static final ArmorSet[] ARMOR_SETS_1 = new ArmorSet[] { new ArmorSet("minecraft:leather_boots",
				"minecraft:leather_leggings", "minecraft:leather_chestplate", "minecraft:leather_helmet") };

		public static final ArmorSet[] ARMOR_SETS_2 = new ArmorSet[] {
				new ArmorSet("minecraft:leather_boots", "minecraft:leather_leggings", "minecraft:leather_chestplate",
						"minecraft:leather_helmet"),
				new ArmorSet("minecraft:golden_boots", "minecraft:golden_leggings", "minecraft:golden_chestplate",
						"minecraft:golden_helmet"),
				new ArmorSet("minecraft:chainmail_boots", "minecraft:chainmail_leggings",
						"minecraft:chainmail_chestplate", "minecraft:chainmail_helmet"),
				new ArmorSet("minecraft:iron_boots", "minecraft:iron_leggings", "minecraft:iron_chestplate",
						"minecraft:iron_helmet") };

		public static final ArmorSet[] ARMOR_SETS_3 = new ArmorSet[] {
				new ArmorSet("minecraft:leather_boots", "minecraft:leather_leggings", "minecraft:leather_chestplate",
						"minecraft:leather_helmet"),
				new ArmorSet("minecraft:golden_boots", "minecraft:golden_leggings", "minecraft:golden_chestplate",
						"minecraft:golden_helmet"),
				new ArmorSet("minecraft:chainmail_boots", "minecraft:chainmail_leggings",
						"minecraft:chainmail_chestplate", "minecraft:chainmail_helmet"),
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

		public static final String[] ASSUMPTION_SEARCHLIST = new String[] { "nether", "end", "aether", "betweenlands",
				"twilight", "dimension", "mining", "rftools", "world" };

		public static final HashMap<String, Object> DEFAULTS;

		static {
			DEFAULTS = new HashMap<String, Object>();
			DEFAULTS.put(KEY_BIOME_BLACKLIST, BIOME_BLACKLIST);
			DEFAULTS.put(KEY_BIOME_OVERWORLD_BLACKLIST, BIOME_OVERWORLD_BLACKLIST);
			DEFAULTS.put(KEY_BOWS, BOWS);
			DEFAULTS.put(KEY_COLORED_ARMOR, COLORED_ARMOR);
			DEFAULTS.put(KEY_SWORDS, SWORDS);
			DEFAULTS.put(KEY_SWORDS_RARE, SWORDS_RARE);
			DEFAULTS.put(KEY_PICKAXES, PICKAXES);
			DEFAULTS.put(KEY_AXES, AXES);
			DEFAULTS.put(KEY_ARMOR_STAGE_1, ARMOR_SETS_1);
			DEFAULTS.put(KEY_ARMOR_STAGE_2, ARMOR_SETS_2);
			DEFAULTS.put(KEY_ARMOR_STAGE_3, ARMOR_SETS_3);
			DEFAULTS.put(KEY_ARMOR_RARE, ARMOR_SETS_RARE);
			DEFAULTS.put(KEY_BOW_ENCHANTMENTS, BOW_ENCHANTMENTS);
			DEFAULTS.put(KEY_SWORD_ENCHANTMENTS, SWORD_ENCHANTMENTS);
			DEFAULTS.put(KEY_PICKAXE_ENCHANTMENTS, PICKAXE_ENCHANTMENTS);
			DEFAULTS.put(KEY_AXE_ENCHANTMENTS, AXE_ENCHANTMENTS);
			DEFAULTS.put(KEY_ARMOR_ENCHANTMENTS, ARMOR_ENCHANTMENTS);
			DEFAULTS.put(KEY_ASSUMPTION_SEARCHLIST, ASSUMPTION_SEARCHLIST);
		}

		public static void rewrite(File file) {
			JsonObject object;
			try {
				object = file.exists() ? DungeonCrawl.GSON.fromJson(new FileReader(file), JsonObject.class)
						: new JsonObject();
				for (String key : KEYS) {
					if (!object.has(key))
						object.add(key, DungeonCrawl.GSON.toJsonTree(DEFAULTS.get(key)));
				}
				FileWriter writer = new FileWriter(file);
				DungeonCrawl.GSON.toJson(object, writer);
				writer.flush();
				writer.close();
			} catch (Exception e1) {
				DungeonCrawl.LOGGER.error("An error occured whilst trying to rewrite the json config file.");
				e1.printStackTrace();
			}
		}

	}

}
