package xiroc.dungeoncrawl.config;

/*
 * DungeonCrawl (C) 2019 - 2020 XYROC (XIROC1337), All Rights Reserved 
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
import com.google.common.io.Files;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.loading.FMLPaths;
import xiroc.dungeoncrawl.DungeonCrawl;
import xiroc.dungeoncrawl.dungeon.monster.ArmorSet;
import xiroc.dungeoncrawl.util.BossEntry;
import xiroc.dungeoncrawl.util.IJsonConfigurable;

public class JsonConfig implements IJsonConfigurable {

	public static final String KEY_CONFIG_VERSION = "config_version";

	public static final String KEY_BIOME_BLACKLIST = "biome_blacklist",
			KEY_BIOME_OVERWORLD_BLACKLIST = "biome_overworld_blacklist", KEY_BOWS = "bows",
			KEY_COLORED_ARMOR = "colored_armor", KEY_SWORDS = "swords", KEY_SWORDS_RARE = "swords_rare",
			KEY_PICKAXES = "pickaxes", KEY_AXES = "axes", KEY_ARMOR_STAGE_1 = "armor_sets_stage_1",
			KEY_ARMOR_STAGE_2 = "armor_sets_stage_2", KEY_ARMOR_STAGE_3 = "armor_sets_stage_3",
			KEY_ARMOR_RARE = "armor_rare", KEY_BOW_ENCHANTMENTS = "bow_enchantments",
			KEY_SWORD_ENCHANTMENTS = "sword_enchantments", KEY_PICKAXE_ENCHANTMENTS = "pickaxe_enchantments",
			KEY_AXE_ENCHANTMENTS = "axe_enchantments", KEY_ARMOR_ENCHANTMENTS = "armor_enchantments",
			KEY_BOSSES = "dungeon_bosses";

	public static final String[] KEYS = new String[] { KEY_BOSSES , KEY_ARMOR_ENCHANTMENTS, KEY_ARMOR_RARE, KEY_ARMOR_STAGE_1,
			KEY_ARMOR_STAGE_2, KEY_ARMOR_STAGE_3, KEY_AXE_ENCHANTMENTS, KEY_AXES, KEY_BIOME_BLACKLIST,
			KEY_BIOME_OVERWORLD_BLACKLIST, KEY_BOW_ENCHANTMENTS, KEY_BOWS, KEY_COLORED_ARMOR, KEY_PICKAXE_ENCHANTMENTS,
			KEY_PICKAXES, KEY_SWORD_ENCHANTMENTS, KEY_SWORDS, KEY_SWORDS_RARE };
	
	public static BossEntry[] DUNGEON_BOSSES;

	public static List<?> BIOME_BLACKLIST, BIOME_OVERWORLD_BLACKLIST;

	public static String[] ASSUMPTION_SEARCHLIST;

	public static Set<?> COLORED_ARMOR = ImmutableSet.<String>builder().add("minecraft:leather_boots")
			.add("minecraft:leather_pants").add("minecraft:leather_chestplate").add("minecraft:leather_helmet").build();

	public static ResourceLocation[] BOWS, SWORDS, SWORDS_RARE, PICKAXES, AXES, BOW_ENCHANTMENTS, SWORD_ENCHANTMENTS,
			PICKAXE_ENCHANTMENTS, AXE_ENCHANTMENTS, ARMOR_ENCHANTMENTS;

	public static ArmorSet[] ARMOR_SETS_1, ARMOR_SETS_2, ARMOR_SETS_3, ARMOR_SETS_RARE;

	static {
		load(new JsonConfig());
//		load(new EnchantedBook());
		load(new SpecialItemTags());
		load(new ObfuscationValues());
	}

	public static void load(IJsonConfigurable configurable) {
		File file = configurable.getFile();
		DungeonCrawl.LOGGER.info("Loading {}", file.getAbsolutePath());
		if (!file.exists()) {
			DungeonCrawl.LOGGER.info("Creating {}", file.getAbsolutePath());
			if (file.getParentFile() != null)
				file.getParentFile().mkdirs();
			JsonObject object = configurable.create(new JsonObject());
			object.add(KEY_CONFIG_VERSION, DungeonCrawl.GSON.toJsonTree(configurable.getVersion()));
			try {
				FileWriter writer = new FileWriter(file);
				DungeonCrawl.GSON.toJson(object, writer);
				writer.flush();
				writer.close();
			} catch (Exception e) {
				DungeonCrawl.LOGGER.error("Failed to create {}", file.getAbsolutePath());
				e.printStackTrace();
			}
		}
		Gson gson = DungeonCrawl.GSON;
		try {
			JsonObject object = gson.fromJson(new FileReader(file), JsonObject.class);
			if (object.get(KEY_CONFIG_VERSION).getAsInt() < configurable.getVersion()) {
				if (!configurable.deleteOldVersion()) {
					DungeonCrawl.LOGGER.info("Creating a backup of {} because it is outdated and will be replaced",
							file.getAbsolutePath());
					File backupFile = FMLPaths.CONFIGDIR.get()
							.resolve("DungeonCrawl/Config Backups/" + System.currentTimeMillis() + "_" + file.getName())
							.toFile();
					if (!backupFile.getParentFile().exists())
						backupFile.getParentFile().mkdirs();
					Files.write(Files.toByteArray(file), backupFile);
				}
				DungeonCrawl.LOGGER.info("Replacing {}", file.getAbsoluteFile());
				JsonObject newObject = configurable.create(new JsonObject());
				newObject.add(KEY_CONFIG_VERSION, DungeonCrawl.GSON.toJsonTree(configurable.getVersion()));
				try {
					FileWriter writer = new FileWriter(file);
					DungeonCrawl.GSON.toJson(newObject, writer);
					writer.flush();
					writer.close();
				} catch (Exception e) {
					DungeonCrawl.LOGGER.error("Failed to create {}", file.getAbsolutePath());
					e.printStackTrace();
				}
			}
			configurable.load(object, file);
		} catch (Exception e) {
			DungeonCrawl.LOGGER.error("Failed to load {}", file.getAbsolutePath());
			e.printStackTrace();
		}

	}

	@Override
	public int getVersion() {
		return 0;
	}

	@Override
	public boolean deleteOldVersion() {
		return false;
	}

	public static JsonElement getOrRewrite(JsonObject object, String name, IJsonConfigurable configurable) {
		return getOrRewrite(object, name, configurable, false);
	}

	public static JsonElement getOrRewrite(JsonObject object, String name, IJsonConfigurable configurable,
			boolean rerun) {
		if (object.get(name) != null) {
			return object.get(name);
		} else {
			File file = configurable.getFile();
			if (rerun) {
				DungeonCrawl.LOGGER.error("Cant find \"{}\" in {}, even after rewriting the file.", name,
						file.getAbsolutePath());
				return DungeonCrawl.GSON.toJsonTree(configurable.getDefaults().get(name));
			}
			DungeonCrawl.LOGGER.info("Rewriting {} due to missing data.", file.getAbsolutePath());
			JsonConfigManager.rewrite(configurable);
			try {
				return getOrRewrite(
						DungeonCrawl.GSON.fromJson(new FileReader(configurable.getFile()), JsonObject.class), name,
						configurable, true);
			} catch (Exception e) {
				e.printStackTrace();
				return DungeonCrawl.GSON.toJsonTree(configurable.getDefaults().get(name));
			}
		}
	}

	public static class JsonConfigManager {
		
		public static final List<?> BIOME_BLACKLIST = Lists.newArrayList("minecraft:the_end", "minecraft:nether",
				"minecraft:small_end_islands", "minecraft:end_midlands", "minecraft:end_highlands",
				"minecraft:end_barrens", "minecraft:the_void", "biomesoplenty:ashen_inferno",
				"biomesopenty:undergarden", "biomesoplenty:boneyard", "biomesoplenty:visceral_heap");

		public static final List<?> BIOME_OVERWORLD_BLACKLIST = Lists.newArrayList("minecraft:ocean",
				"minecraft:deep_ocean", "minecraft:lukewarm_ocean", "minecraft:deep_lukewarm_ocean",
				"minecraft:warm_ocean", "minecraft:deep_warm_ocean", "minecraft:cold_ocean",
				"minecraft:deep_cold_ocean", "minecraft:frozen_ocean", "minecraft:deep_frozen_ocean");

		public static final Set<?> COLORED_ARMOR = ImmutableSet.<String>builder().add("minecraft:leather_boots")
				.add("minecraft:leather_pants").add("minecraft:leather_chestplate").add("minecraft:leather_helmet")
				.build();

		public static final String[] BOWS = new String[] { "minecraft:bow" };

		public static final String[] SWORDS = new String[] { new String("minecraft:stone_sword"),
				"minecraft:golden_sword", new String("minecraft:iron_sword") };

		public static final String[] SWORDS_RARE = new String[] { "minecraft:wooden_sword", "minecraft:diamond_sword" };

		public static final String[] PICKAXES = new String[] { "minecraft:stone_pickaxe", "minecraft:golden_pickaxe",
				"minecraft:iron_pickaxe", };
		public static final String[] AXES = new String[] { "minecraft:stone_axe", "minecraft:golden_axe",
				"minecraft:iron_axe", "minecraft:diamond_axe" };

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

		public static final String[] BOW_ENCHANTMENTS = new String[] { "minecraft:power", "minecraft:unbreaking",
				"minecraft:punch" };

		public static final String[] SWORD_ENCHANTMENTS = new String[] { "minecraft:sharpness", "minecraft:unbreaking",
				"minecraft:fire_aspect", "minecraft:knockback" };

		public static final String[] PICKAXE_ENCHANTMENTS = new String[] { "minecraft:efficiency" };

		public static final String[] AXE_ENCHANTMENTS = new String[] { "minecraft:efficiency" };

		public static final String[] ARMOR_ENCHANTMENTS = new String[] { "minecraft:protection", "minecraft:unbreaking",
				"minecraft:thorns", "minecraft:projectile_protection" };

		public static final String[] ASSUMPTION_SEARCHLIST = new String[] { "nether", "end", "aether", "betweenlands",
				"twilight", "dimension", "mining", "rftools", "world" };

		public static final HashMap<String, Object> DEFAULTS;

		static {
			DEFAULTS = new HashMap<String, Object>();
			DEFAULTS.put(KEY_BOSSES, new BossEntry[0]);
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
//			DEFAULTS.put(KEY_ASSUMPTION_SEARCHLIST, ASSUMPTION_SEARCHLIST);
		}

		public static void rewrite(IJsonConfigurable configurable) {
			File file = configurable.getFile();
			JsonObject object;
			try {
				object = file.exists() ? DungeonCrawl.GSON.fromJson(new FileReader(file), JsonObject.class)
						: new JsonObject();
				for (String key : configurable.getKeys()) {
					if (!object.has(key))
						object.add(key, DungeonCrawl.GSON.toJsonTree(configurable.getDefaults().get(key)));
				}
				FileWriter writer = new FileWriter(file);
				DungeonCrawl.GSON.toJson(object, writer);
				writer.flush();
				writer.close();
			} catch (Exception e1) {
				DungeonCrawl.LOGGER.error("An error occured whilst trying to rewrite {}", file.getAbsolutePath());
				e1.printStackTrace();
			}
		}

	}

	@Override
	public File getFile() {
		return FMLPaths.CONFIGDIR.get().resolve("DungeonCrawl/config.json").toFile();
	}

	@Override
	public void load(JsonObject object, File file) {
		DUNGEON_BOSSES = DungeonCrawl.GSON.fromJson(getOrRewrite(object, KEY_BOSSES, this), BossEntry[].class);
		
		BIOME_BLACKLIST = DungeonCrawl.GSON.fromJson(getOrRewrite(object, KEY_BIOME_BLACKLIST, this), ArrayList.class);
		BIOME_OVERWORLD_BLACKLIST = DungeonCrawl.GSON
				.fromJson(getOrRewrite(object, KEY_BIOME_OVERWORLD_BLACKLIST, this), ArrayList.class);

		COLORED_ARMOR = DungeonCrawl.GSON.fromJson(getOrRewrite(object, KEY_COLORED_ARMOR, this), Set.class);

		BOWS = toResourceLocationArray(
				DungeonCrawl.GSON.fromJson(getOrRewrite(object, KEY_BOWS, this), String[].class));
		SWORDS = toResourceLocationArray(
				DungeonCrawl.GSON.fromJson(getOrRewrite(object, KEY_SWORDS, this), String[].class));
		SWORDS_RARE = toResourceLocationArray(
				DungeonCrawl.GSON.fromJson(getOrRewrite(object, KEY_SWORDS_RARE, this), String[].class));
		PICKAXES = toResourceLocationArray(
				DungeonCrawl.GSON.fromJson(getOrRewrite(object, KEY_PICKAXES, this), String[].class));
		AXES = toResourceLocationArray(
				DungeonCrawl.GSON.fromJson(getOrRewrite(object, KEY_AXES, this), String[].class));

		ARMOR_SETS_1 = DungeonCrawl.GSON.fromJson(getOrRewrite(object, KEY_ARMOR_STAGE_1, this), ArmorSet[].class);
		ARMOR_SETS_2 = DungeonCrawl.GSON.fromJson(getOrRewrite(object, KEY_ARMOR_STAGE_2, this), ArmorSet[].class);
		ARMOR_SETS_3 = DungeonCrawl.GSON.fromJson(getOrRewrite(object, KEY_ARMOR_STAGE_3, this), ArmorSet[].class);

		ARMOR_SETS_RARE = DungeonCrawl.GSON.fromJson(getOrRewrite(object, KEY_ARMOR_RARE, this), ArmorSet[].class);

		BOW_ENCHANTMENTS = toResourceLocationArray(
				DungeonCrawl.GSON.fromJson(getOrRewrite(object, KEY_BOW_ENCHANTMENTS, this), String[].class));
		SWORD_ENCHANTMENTS = toResourceLocationArray(
				DungeonCrawl.GSON.fromJson(getOrRewrite(object, KEY_SWORD_ENCHANTMENTS, this), String[].class));
		AXE_ENCHANTMENTS = toResourceLocationArray(
				DungeonCrawl.GSON.fromJson(getOrRewrite(object, KEY_AXE_ENCHANTMENTS, this), String[].class));
		PICKAXE_ENCHANTMENTS = toResourceLocationArray(
				DungeonCrawl.GSON.fromJson(getOrRewrite(object, KEY_PICKAXE_ENCHANTMENTS, this), String[].class));
		ARMOR_ENCHANTMENTS = toResourceLocationArray(
				DungeonCrawl.GSON.fromJson(getOrRewrite(object, KEY_ARMOR_ENCHANTMENTS, this), String[].class));

//		ASSUMPTION_SEARCHLIST = DungeonCrawl.GSON.fromJson(getOrRewrite(object, KEY_ASSUMPTION_SEARCHLIST, this), String[].class);

		ArmorSet.buildAll(ARMOR_SETS_1);
		ArmorSet.buildAll(ARMOR_SETS_2);
		ArmorSet.buildAll(ARMOR_SETS_3);
		ArmorSet.buildAll(ARMOR_SETS_RARE);
	}

	@Override
	public JsonObject create(JsonObject object) {
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
//		object.add(KEY_ASSUMPTION_SEARCHLIST, DungeonCrawl.GSON.toJsonTree(JsonConfigManager.ASSUMPTION_SEARCHLIST));
		return object;
	}

	public static ResourceLocation[] toResourceLocationArray(String[] resourceNames) {
		ResourceLocation[] resourceLocations = new ResourceLocation[resourceNames.length];
		for (int i = 0; i < resourceNames.length; i++) {
			String[] resource = resourceNames[i].split(":");
			resourceLocations[i] = new ResourceLocation(resource[0], resource[1]);
		}
		return resourceLocations;
	}

	@Override
	public HashMap<String, Object> getDefaults() {
		return JsonConfigManager.DEFAULTS;
	}

	@Override
	public String[] getKeys() {
		return KEYS;
	}

}
