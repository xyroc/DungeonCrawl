package xiroc.dungeoncrawl.dungeon.treasure;

/*
 * DungeonCrawl (C) 2019 XYROC (XIROC1337), All Rights Reserved 
 */

import java.io.File;
import java.util.HashMap;

import com.google.gson.JsonObject;

import net.minecraft.world.storage.loot.RandomValueRange;
import net.minecraftforge.fml.loading.FMLPaths;
import xiroc.dungeoncrawl.DungeonCrawl;
import xiroc.dungeoncrawl.config.JsonConfig;
import xiroc.dungeoncrawl.config.Kitchen;
import xiroc.dungeoncrawl.config.TreasureRoom;
import xiroc.dungeoncrawl.util.IJsonConfigurable;

public class Treasure implements IJsonConfigurable {

	public static final HashMap<Treasure.Type, TreasureLootTable> SPECIAL_LOOT_TABLES;

	public static final HashMap<String, Object> DEFAULTS;
	public static final String[] KEYS;

	public static final String KEY_CHEST_STAGE_1 = "chest_stage_1", KEY_CHEST_STAGE_1_OCEAN = "chest_stage_1_ocean",
			KEY_CHEST_STAGE_2 = "chest_stage_2", KEY_CHEST_STAGE_2_OCEAN = "chest_stage_2_ocean",
			KEY_CHEST_STAGE_3 = "chest_stage_3", KEY_CHEST_STAGE_3_OCEAN = "chest_stage_3_ocean",
			KEY_DISPENSER_STAGE_1 = "dispenser_stage_1", KEY_DISPENSER_STAGE_2 = "dispenser_stage_2",
			KEY_DISPENSER_STAGE_3 = "dispenser_stage_3", KEY_SPIDER_STAGE_1 = "spider_chest_stage_1",
			KEY_SPIDER_STAGE_2 = "spider_chest_stage_2", KEY_SPIDER_STAGE_3 = "spider_chest_stage_3";

	public static TreasureLootTable CHEST_STAGE_1, CHEST_STAGE_1_OCEAN;
	public static TreasureLootTable CHEST_STAGE_2, CHEST_STAGE_2_OCEAN;
	public static TreasureLootTable CHEST_STAGE_3, CHEST_STAGE_3_OCEAN;

	public static TreasureLootTable SPIDER_STAGE_1, SPIDER_STAGE_2, SPIDER_STAGE_3;

	public static TreasureLootTable DISPENSER_STAGE_1, DISPENSER_STAGE_2, DISPENSER_STAGE_3;

	public static TreasureLootTable SMELTERY, MINECART, SECRET_ROOM, LIBRARY, BUILDERS_ROOM, TREASURE;

	static {

		SPECIAL_LOOT_TABLES = new HashMap<Type, TreasureLootTable>();

		DEFAULTS = new HashMap<String, Object>();
		DEFAULTS.put(KEY_CHEST_STAGE_1, new TreasureLootTable("dungeon_chest_stage_1", new RandomValueRange(3, 9),
				new TreasureEntry("minecraft:coal", 1, 3, 3), new TreasureEntry("minecraft:iron_ingot", 1, 3, 2),
				new TreasureEntry("minecraft:gold_ingot", 1, 3, 1), new TreasureEntry("minecraft:arrow", 1, 8, 3),
				new TreasureEntry("minecraft:cobweb", 1, 3, 18), new TreasureEntry("minecraft:bone", 4, 6, 22),
				new TreasureEntry("minecraft:rotten_flesh", 3, 7, 18),
				new TreasureEntry("minecraft:golden_apple", 1, 3, 1), new TreasureEntry("minecraft:music_disc_13", 1),
				new TreasureEntry("minecraft:music_disc_cat", 1), new TreasureEntry("minecraft:torch", 1, 4, 4),
				new TreasureEntry("minecraft:brown_mushroom", 1, 4, 5),
				new TreasureEntry("minecraft:red_mushroom", 1, 4, 5), new TreasureEntry("minecraft:compass", 1),
				new TreasureEntry("minecraft:bowl", 5), new TreasureEntry("minecraft:clock", 1),
				new TreasureEntry("minecraft:fermented_spider_eye", 2),
				new TreasureEntry("minecraft:spider_eye", 1, 7, 5)));

		DEFAULTS.put(KEY_CHEST_STAGE_1_OCEAN, new TreasureLootTable("dungeon_chest_stage_1_ocean",
				new RandomValueRange(3, 9), new TreasureEntry("minecraft:coal", 1, 3, 4),
				new TreasureEntry("minecraft:iron_ingot", 1, 3, 1), new TreasureEntry("minecraft:gold_ingot", 1, 3, 1),
				new TreasureEntry("minecraft:arrow", 1, 8, 4), new TreasureEntry("minecraft:cobweb", 1, 3, 18),
				new TreasureEntry("minecraft:bone", 4, 6, 22), new TreasureEntry("minecraft:pufferfish", 3, 7, 18),
				new TreasureEntry("minecraft:golden_apple", 1, 3, 1), new TreasureEntry("minecraft:music_disc_13", 1),
				new TreasureEntry("minecraft:music_disc_cat", 1), new TreasureEntry("minecraft:torch", 1, 4, 2),
				new TreasureEntry("minecraft:kelp", 1, 4, 15), new TreasureEntry("minecraft:compass", 1),
				new TreasureEntry("minecraft:bowl", 8), new TreasureEntry("minecraft:clock", 1),
				new TreasureEntry("minecraft:fermented_spider_eye", 3),
				new TreasureEntry("minecraft:spider_eye", 1, 7, 2), new TreasureEntry("minecraft:book", 1, 3, 2),
				new TreasureEntry("minecraft:sand", 1, 8, 6)));

		DEFAULTS.put(KEY_CHEST_STAGE_2, new TreasureLootTable("dungeon_chest_stage_2", new RandomValueRange(7, 10),
				new TreasureEntry("minecraft:coal", 1, 3, 3), new TreasureEntry("minecraft:iron_ingot", 1, 9, 2),
				new TreasureEntry("minecraft:gold_ingot", 3, 6, 2), new TreasureEntry("minecraft:cobweb", 1, 3, 21),
				new TreasureEntry("minecraft:bone", 4, 6, 17), new TreasureEntry("minecraft:rotten_flesh", 3, 7, 13),
				new TreasureEntry("minecraft:iron_sword", 1), new TreasureEntry("minecraft:iron_axe", 1),
				new TreasureEntry("minecraft:golden_sword", 1), new TreasureEntry("minecraft:golden_axe", 2),
				new TreasureEntry("minecraft:arrow", 3, 9, 9), new TreasureEntry("minecraft:golden_apple", 1, 3, 2),
				new TreasureEntry("minecraft:enchanted_golden_apple", 1),
				new TreasureEntry("minecraft:music_disc_13", 1), new TreasureEntry("minecraft:music_disc_cat", 1),
				new TreasureEntry("minecraft:name_tag", 1), new TreasureEntry("minecraft:crossbow", 1),
				new TreasureEntry("minecraft:torch", 1, 7, 6), new TreasureEntry("minecraft:brown_mushroom", 1, 4, 7),
				new TreasureEntry("minecraft:red_mushroom", 1, 7, 4), new TreasureEntry("minecraft:bowl", 7),
				new TreasureEntry("minecraft:clock", 1), new TreasureEntry("minecraft:compass", 1),
				new TreasureEntry("minecraft:fermented_spider_eye", 3),
				new TreasureEntry("minecraft:spider_eye", 1, 7, 6)));

		DEFAULTS.put(KEY_CHEST_STAGE_2_OCEAN, new TreasureLootTable("dungeon_chest_stage_2_ocean",
				new RandomValueRange(7, 10), new TreasureEntry("minecraft:coal", 1, 3, 3),
				new TreasureEntry("minecraft:iron_ingot", 1, 9, 2), new TreasureEntry("minecraft:gold_ingot", 3, 6, 3),
				new TreasureEntry("minecraft:cobweb", 1, 3, 21), new TreasureEntry("minecraft:bone", 4, 6, 17),
				new TreasureEntry("minecraft:pufferfish", 3, 7, 13), new TreasureEntry("minecraft:iron_sword", 1),
				new TreasureEntry("minecraft:iron_axe", 1), new TreasureEntry("minecraft:golden_sword", 1),
				new TreasureEntry("minecraft:golden_axe", 2), new TreasureEntry("minecraft:arrow", 3, 9, 9),
				new TreasureEntry("minecraft:golden_apple", 1, 3, 2),
				new TreasureEntry("minecraft:enchanted_golden_apple", 1),
				new TreasureEntry("minecraft:music_disc_13", 1), new TreasureEntry("minecraft:music_disc_cat", 1),
				new TreasureEntry("minecraft:name_tag", 1), new TreasureEntry("minecraft:crossbow", 1),
				new TreasureEntry("minecraft:torch", 1, 7, 2), new TreasureEntry("minecraft:brown_mushroom", 1, 4, 7),
				new TreasureEntry("minecraft:kelp", 1, 7, 15), new TreasureEntry("minecraft:bowl", 7),
				new TreasureEntry("minecraft:clock", 1), new TreasureEntry("minecraft:compass", 1),
				new TreasureEntry("minecraft:fermented_spider_eye", 3),
				new TreasureEntry("minecraft:spider_eye", 1, 7, 2), new TreasureEntry("minecraft:book", 1, 3, 1),
				new TreasureEntry("minecraft:sand", 1, 8, 6)));

		DEFAULTS.put(KEY_CHEST_STAGE_3, new TreasureLootTable("dungeon_chest_stage_3", new RandomValueRange(7, 10),
				new TreasureEntry("minecraft:coal", 2, 5, 8), new TreasureEntry("minecraft:iron_ingot", 3, 7, 4),
				new TreasureEntry("minecraft:gold_ingot", 2, 4, 3), new TreasureEntry("minecraft:arrow", 3, 8, 7),
				new TreasureEntry("minecraft:diamond", 1), new TreasureEntry("minecraft:cobweb", 1, 3, 21),
				new TreasureEntry("minecraft:bone", 4, 6, 21), new TreasureEntry("minecraft:rotten_flesh", 3, 7, 19),
				new TreasureEntry("minecraft:iron_axe", 2), new TreasureEntry("minecraft:golden_sword", 1),
				new TreasureEntry("minecraft:golden_axe", 2), new TreasureEntry("minecraft:saddle", 1),
				new TreasureEntry("minecraft:golden_apple", 1, 3, 4),
				new TreasureEntry("minecraft:enchanted_golden_apple", 1),
				new TreasureEntry("minecraft:music_disc_13", 1), new TreasureEntry("minecraft:music_disc_cat", 1),
				new TreasureEntry("minecraft:name_tag", 3), new TreasureEntry("minecraft:torch", 1, 7, 8),
				new TreasureEntry("minecraft:brown_mushroom", 1, 4, 3),
				new TreasureEntry("minecraft:red_mushroom", 1, 4, 3), new TreasureEntry("minecraft:bowl", 5),
				new TreasureEntry("minecraft:clock", 1), new TreasureEntry("minecraft:compass", 1),
				new TreasureEntry("minecraft:fermented_spider_eye", 1),
				new TreasureEntry("minecraft:spider_eye", 1, 7, 6)));

		DEFAULTS.put(KEY_CHEST_STAGE_3_OCEAN, new TreasureLootTable("dungeon_chest_stage_3_ocean",
				new RandomValueRange(7, 10), new TreasureEntry("minecraft:coal", 2, 5, 8),
				new TreasureEntry("minecraft:iron_ingot", 3, 7, 4), new TreasureEntry("minecraft:gold_ingot", 2, 4, 3),
				new TreasureEntry("minecraft:arrow", 3, 8, 7), new TreasureEntry("minecraft:diamond", 1),
				new TreasureEntry("minecraft:cobweb", 1, 3, 21), new TreasureEntry("minecraft:bone", 4, 6, 21),
				new TreasureEntry("minecraft:pufferfish", 3, 7, 19), new TreasureEntry("minecraft:iron_axe", 2),
				new TreasureEntry("minecraft:golden_sword", 1), new TreasureEntry("minecraft:golden_axe", 2),
				new TreasureEntry("minecraft:saddle", 1), new TreasureEntry("minecraft:golden_apple", 1, 3, 4),
				new TreasureEntry("minecraft:enchanted_golden_apple", 1),
				new TreasureEntry("minecraft:music_disc_13", 1), new TreasureEntry("minecraft:music_disc_cat", 1),
				new TreasureEntry("minecraft:name_tag", 3), new TreasureEntry("minecraft:torch", 1, 7, 3),
				new TreasureEntry("minecraft:kelp", 4, 7, 15), new TreasureEntry("minecraft:bowl", 5),
				new TreasureEntry("minecraft:clock", 1), new TreasureEntry("minecraft:compass", 1),
				new TreasureEntry("minecraft:fermented_spider_eye", 1),
				new TreasureEntry("minecraft:spider_eye", 1, 7, 6), new TreasureEntry("minecraft:book", 1, 3, 2),
				new TreasureEntry("minecraft:sand", 1, 8, 6)));

		DEFAULTS.put(KEY_DISPENSER_STAGE_1, new TreasureLootTable("dungeon_dispenser_stage_1",
				new RandomValueRange(2, 4), new TreasureEntry("minecraft:arrow", 3, 9, 2)));

		DEFAULTS.put(KEY_DISPENSER_STAGE_2, new TreasureLootTable("dungeon_dispenser_stage_2",
				new RandomValueRange(2, 4), new TreasureEntry("minecraft:arrow", 3, 9, 2)));

		DEFAULTS.put(KEY_DISPENSER_STAGE_3, new TreasureLootTable("dungeon_dispenser_stage_3",
				new RandomValueRange(2, 4), new TreasureEntry("minecraft:arrow", 3, 9, 1)));

		KEYS = new String[] { KEY_CHEST_STAGE_1, KEY_CHEST_STAGE_1_OCEAN, KEY_CHEST_STAGE_2, KEY_CHEST_STAGE_2_OCEAN,
				KEY_CHEST_STAGE_3, KEY_CHEST_STAGE_3_OCEAN, KEY_DISPENSER_STAGE_1, KEY_DISPENSER_STAGE_2,
				KEY_DISPENSER_STAGE_3, KEY_SPIDER_STAGE_1, KEY_SPIDER_STAGE_2, KEY_SPIDER_STAGE_3 };

	}

	@Override
	public File getFile() {
		return FMLPaths.CONFIGDIR.get().resolve("DungeonCrawl/loot/loot.json").toFile();
	}

	@Override
	public void load(JsonObject object, File file) {
		CHEST_STAGE_1 = DungeonCrawl.GSON.fromJson(JsonConfig.getOrRewrite(object, KEY_CHEST_STAGE_1, this),
				TreasureLootTable.class);
		CHEST_STAGE_2 = DungeonCrawl.GSON.fromJson(JsonConfig.getOrRewrite(object, KEY_CHEST_STAGE_2, this),
				TreasureLootTable.class);
		CHEST_STAGE_3 = DungeonCrawl.GSON.fromJson(JsonConfig.getOrRewrite(object, KEY_CHEST_STAGE_3, this),
				TreasureLootTable.class);

		CHEST_STAGE_1_OCEAN = DungeonCrawl.GSON.fromJson(JsonConfig.getOrRewrite(object, KEY_CHEST_STAGE_1_OCEAN, this),
				TreasureLootTable.class);
		CHEST_STAGE_2_OCEAN = DungeonCrawl.GSON.fromJson(JsonConfig.getOrRewrite(object, KEY_CHEST_STAGE_2_OCEAN, this),
				TreasureLootTable.class);
		CHEST_STAGE_3_OCEAN = DungeonCrawl.GSON.fromJson(JsonConfig.getOrRewrite(object, KEY_CHEST_STAGE_3_OCEAN, this),
				TreasureLootTable.class);

		DISPENSER_STAGE_1 = DungeonCrawl.GSON.fromJson(JsonConfig.getOrRewrite(object, KEY_DISPENSER_STAGE_1, this),
				TreasureLootTable.class);
		DISPENSER_STAGE_2 = DungeonCrawl.GSON.fromJson(JsonConfig.getOrRewrite(object, KEY_DISPENSER_STAGE_2, this),
				TreasureLootTable.class);
		DISPENSER_STAGE_3 = DungeonCrawl.GSON.fromJson(JsonConfig.getOrRewrite(object, KEY_DISPENSER_STAGE_3, this),
				TreasureLootTable.class);

		CHEST_STAGE_1.entries.add(TreasureItems.LAUDANUM.withWeight(2));
		CHEST_STAGE_1.entries.add(TreasureItems.POTION_HEALING);
		CHEST_STAGE_1.entries.add(TreasureItems.ENCHANTED_BOOK);
		CHEST_STAGE_1.entries.add(TreasureItems.MATERIAL_BLOCKS);
		CHEST_STAGE_1.entries.add(TreasureItems.RANDOM_SPECIAL_ITEM.withWeight(5));

		CHEST_STAGE_1_OCEAN.entries.add(TreasureItems.LAUDANUM.withWeight(2));
		CHEST_STAGE_1_OCEAN.entries.add(TreasureItems.POTION_HEALING);
		CHEST_STAGE_1_OCEAN.entries.add(TreasureItems.ENCHANTED_BOOK);
		CHEST_STAGE_1_OCEAN.entries.add(TreasureItems.MATERIAL_BLOCKS);
		CHEST_STAGE_1_OCEAN.entries.add(TreasureItems.RANDOM_SPECIAL_ITEM.withWeight(5));

		CHEST_STAGE_2.entries.add(TreasureItems.LAUDANUM.withWeight(3));
		CHEST_STAGE_2.entries.add(TreasureItems.ANIMUS);
		CHEST_STAGE_2.entries.add(TreasureItems.NECTAR);
		CHEST_STAGE_2.entries.add(TreasureItems.LUMA);
		CHEST_STAGE_2.entries.add(TreasureItems.VELOCITAS);
		CHEST_STAGE_2.entries.add(TreasureItems.POTION_HEALING);
		CHEST_STAGE_2.entries.add(TreasureItems.POTION_REGENERATION);
		CHEST_STAGE_2.entries.add(TreasureItems.ENCHANTED_BOOK);
		CHEST_STAGE_2.entries.add(TreasureItems.MATERIAL_BLOCKS.withWeight(4));
		CHEST_STAGE_2.entries.add(TreasureItems.RANDOM_SPECIAL_ITEM.withWeight(5));

		CHEST_STAGE_2_OCEAN.entries.add(TreasureItems.LAUDANUM.withWeight(3));
		CHEST_STAGE_2_OCEAN.entries.add(TreasureItems.ANIMUS);
		CHEST_STAGE_2_OCEAN.entries.add(TreasureItems.NECTAR);
		CHEST_STAGE_2_OCEAN.entries.add(TreasureItems.LUMA);
		CHEST_STAGE_2_OCEAN.entries.add(TreasureItems.VELOCITAS);
		CHEST_STAGE_2_OCEAN.entries.add(TreasureItems.POTION_HEALING);
		CHEST_STAGE_2_OCEAN.entries.add(TreasureItems.POTION_REGENERATION);
		CHEST_STAGE_2_OCEAN.entries.add(TreasureItems.ENCHANTED_BOOK);
		CHEST_STAGE_2_OCEAN.entries.add(TreasureItems.MATERIAL_BLOCKS.withWeight(4));
		CHEST_STAGE_2_OCEAN.entries.add(TreasureItems.RANDOM_SPECIAL_ITEM.withWeight(5));

		CHEST_STAGE_3.entries.add(TreasureItems.LAUDANUM.withWeight(3));
		CHEST_STAGE_3.entries.add(TreasureItems.ANIMUS);
		CHEST_STAGE_3.entries.add(TreasureItems.NECTAR);
		CHEST_STAGE_3.entries.add(TreasureItems.LUMA);
		CHEST_STAGE_3.entries.add(TreasureItems.VELOCITAS);
		CHEST_STAGE_3.entries.add(TreasureItems.POTION_HEALING_II);
		CHEST_STAGE_3.entries.add(TreasureItems.POTION_REGENERATION_II);
		CHEST_STAGE_3.entries.add(TreasureItems.POTION_REGENERATION_LONG);
		CHEST_STAGE_3.entries.add(TreasureItems.ENCHANTED_BOOK);
		CHEST_STAGE_3.entries.add(TreasureItems.MATERIAL_BLOCKS.withWeight(3));
		CHEST_STAGE_3.entries.add(TreasureItems.RANDOM_SPECIAL_ITEM.withWeight(5));

		CHEST_STAGE_3_OCEAN.entries.add(TreasureItems.LAUDANUM.withWeight(3));
		CHEST_STAGE_3_OCEAN.entries.add(TreasureItems.ANIMUS);
		CHEST_STAGE_3_OCEAN.entries.add(TreasureItems.NECTAR);
		CHEST_STAGE_3_OCEAN.entries.add(TreasureItems.LUMA);
		CHEST_STAGE_3_OCEAN.entries.add(TreasureItems.VELOCITAS);
		CHEST_STAGE_3_OCEAN.entries.add(TreasureItems.POTION_HEALING_II);
		CHEST_STAGE_3_OCEAN.entries.add(TreasureItems.POTION_REGENERATION_II);
		CHEST_STAGE_3_OCEAN.entries.add(TreasureItems.POTION_REGENERATION_LONG);
		CHEST_STAGE_3_OCEAN.entries.add(TreasureItems.ENCHANTED_BOOK);
		CHEST_STAGE_3_OCEAN.entries.add(TreasureItems.MATERIAL_BLOCKS.withWeight(3));
		CHEST_STAGE_3_OCEAN.entries.add(TreasureItems.RANDOM_SPECIAL_ITEM.withWeight(5));

		DISPENSER_STAGE_1.entries.add(TreasureItems.SPLASH_POISON);

		DISPENSER_STAGE_2.entries.add(TreasureItems.SPLASH_POISON);
		DISPENSER_STAGE_2.entries.add(TreasureItems.SPLASH_HARMING);

		DISPENSER_STAGE_3.entries.add(TreasureItems.SPLASH_POISON_LONG);
		DISPENSER_STAGE_3.entries.add(TreasureItems.SPLASH_HARMING_II);

		CHEST_STAGE_1.build();
		CHEST_STAGE_1_OCEAN.build();
		CHEST_STAGE_2.build();
		CHEST_STAGE_2_OCEAN.build();
		CHEST_STAGE_3.build();
		CHEST_STAGE_3_OCEAN.build();

		DISPENSER_STAGE_1.build();
		DISPENSER_STAGE_2.build();
		DISPENSER_STAGE_3.build();

		SPECIAL_LOOT_TABLES.put(Type.KITCHEN, Kitchen.KITCHEN);

		SPECIAL_LOOT_TABLES.put(Type.TREASURE, TreasureRoom.TREASURE);
	}

	@Override
	public JsonObject create(JsonObject object) {
		object.add(KEY_CHEST_STAGE_1, DungeonCrawl.GSON.toJsonTree(DEFAULTS.get(KEY_CHEST_STAGE_1)));
		object.add(KEY_CHEST_STAGE_2, DungeonCrawl.GSON.toJsonTree(DEFAULTS.get(KEY_CHEST_STAGE_2)));
		object.add(KEY_CHEST_STAGE_3, DungeonCrawl.GSON.toJsonTree(DEFAULTS.get(KEY_CHEST_STAGE_3)));

		object.add(KEY_CHEST_STAGE_1_OCEAN, DungeonCrawl.GSON.toJsonTree(DEFAULTS.get(KEY_CHEST_STAGE_1_OCEAN)));
		object.add(KEY_CHEST_STAGE_2_OCEAN, DungeonCrawl.GSON.toJsonTree(DEFAULTS.get(KEY_CHEST_STAGE_2_OCEAN)));
		object.add(KEY_CHEST_STAGE_3_OCEAN, DungeonCrawl.GSON.toJsonTree(DEFAULTS.get(KEY_CHEST_STAGE_3_OCEAN)));

		object.add(KEY_DISPENSER_STAGE_1, DungeonCrawl.GSON.toJsonTree(DEFAULTS.get(KEY_DISPENSER_STAGE_1)));
		object.add(KEY_DISPENSER_STAGE_2, DungeonCrawl.GSON.toJsonTree(DEFAULTS.get(KEY_DISPENSER_STAGE_2)));
		object.add(KEY_DISPENSER_STAGE_3, DungeonCrawl.GSON.toJsonTree(DEFAULTS.get(KEY_DISPENSER_STAGE_3)));

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

	@Override
	public boolean deleteOldVersion() {
		return false;
	}

	/**
	 * An enum to determine which LootTable should get used for chest, barrels,
	 * etc...
	 */
	public enum Type {

		DEFAULT, KITCHEN, SMELTERY, MINECART, SECRET_ROOM, LIBRARY, BUILDERS_ROOM, TREASURE;

		public static final HashMap<Integer, Type> INT_TO_TYPE_MAP;
		public static final HashMap<Type, Integer> TYPE_TO_INT_MAP;

		static {
			INT_TO_TYPE_MAP = new HashMap<Integer, Type>();
			INT_TO_TYPE_MAP.put(0, DEFAULT);
			INT_TO_TYPE_MAP.put(1, KITCHEN);
			INT_TO_TYPE_MAP.put(2, SMELTERY);
			INT_TO_TYPE_MAP.put(3, MINECART);
			INT_TO_TYPE_MAP.put(4, SECRET_ROOM);
			INT_TO_TYPE_MAP.put(5, LIBRARY);
			INT_TO_TYPE_MAP.put(6, BUILDERS_ROOM);
			INT_TO_TYPE_MAP.put(7, TREASURE);

			TYPE_TO_INT_MAP = new HashMap<Type, Integer>();
			INT_TO_TYPE_MAP.forEach((key, value) -> TYPE_TO_INT_MAP.put(value, key));
		}

		public static Type fromInt(int typeID) {
			return INT_TO_TYPE_MAP.getOrDefault(typeID, DEFAULT);
		}

		public static int toInt(Type type) {
			return TYPE_TO_INT_MAP.get(type);
		}

	}

}
