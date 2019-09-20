package xiroc.dungeoncrawl.config;

/*
 * DungeonCrawl (C) 2019 XYROC (XIROC1337), All Rights Reserved 
 */

import java.io.File;
import java.util.HashMap;

import com.google.gson.JsonObject;

import net.minecraft.world.storage.loot.RandomValueRange;
import net.minecraftforge.fml.loading.FMLPaths;
import xiroc.dungeoncrawl.DungeonCrawl;
import xiroc.dungeoncrawl.dungeon.treasure.TreasureEntry;
import xiroc.dungeoncrawl.dungeon.treasure.TreasureLootTable;
import xiroc.dungeoncrawl.util.IJsonConfigurable;

public class Kitchen implements IJsonConfigurable {

	public static final String KEY_KITCHEN = "kitchen_chest", KEY_SMOKER = "kitchen_smoker",
			KEY_SMOKER_OCEAN = "kitchen_smoker_ocean";

	public static final String[] KEYS = new String[] { KEY_KITCHEN, KEY_SMOKER, KEY_SMOKER_OCEAN };

	public static final HashMap<String, Object> DEFAULTS;

	public static TreasureLootTable KITCHEN, SMOKER, SMOKER_OCEAN;

	static {
		DEFAULTS = new HashMap<String, Object>();
		DEFAULTS.put(KEY_KITCHEN, new TreasureLootTable("kitchen_chest", new RandomValueRange(6, 10),
				new TreasureEntry("minecraft:apple", 1, 4, 3), new TreasureEntry("minecraft:mushroom_stew", 1),
				new TreasureEntry("minecraft:bread", 1, 4, 2), new TreasureEntry("minecraft:porkchop", 1, 2, 3),
				new TreasureEntry("minecraft:cooked_porkchop", 1, 2, 1), new TreasureEntry("minecraft:cod", 1, 3, 1),
				new TreasureEntry("minecraft:salmon", 1, 3, 1), new TreasureEntry("minecraft:tropical_fish", 1),
				new TreasureEntry("minecraft:pufferfish", 1, 3, 1), new TreasureEntry("minecraft:cooked_cod", 1, 3, 1),
				new TreasureEntry("minecraft:cooked_salmon", 1, 3, 1), new TreasureEntry("minecraft:cake", 1),
				new TreasureEntry("minecraft:cookie", 1, 6, 4), new TreasureEntry("minecraft:melon_slice", 1, 4, 2),
				new TreasureEntry("minecraft:dried_kelp", 1), new TreasureEntry("minecraft:beef", 1, 2, 3),
				new TreasureEntry("minecraft:cooked_beef", 1, 2, 1), new TreasureEntry("minecraft:chicken", 1, 2, 2),
				new TreasureEntry("minecraft:cooked_chicken", 1, 2, 1),
				new TreasureEntry("minecraft:rotten_flesh", 3, 8, 16),
				new TreasureEntry("minecraft:spider_eye", 1, 4, 8), new TreasureEntry("minecraft:carrot", 3, 7, 3),
				new TreasureEntry("minecraft:potato", 2, 5, 3), new TreasureEntry("minecraft:baked_potato", 1, 2, 1),
				new TreasureEntry("minecraft:poisonous_potato", 1, 5, 9), new TreasureEntry("minecraft:pumpkin_pie", 1),
				new TreasureEntry("minecraft:rabbit", 1, 2, 2), new TreasureEntry("minecraft:cooked_rabbit", 1),
				new TreasureEntry("minecraft:rabbit_stew", 1), new TreasureEntry("minecraft:mutton", 1, 2, 2),
				new TreasureEntry("minecraft:cooked_mutton", 1, 2, 1), new TreasureEntry("minecraft:beetroot", 3, 6, 5),
				new TreasureEntry("minecraft:beetroot_soup", 3),
				new TreasureEntry("minecraft:sweet_berries", 1, 3, 2)));
		DEFAULTS.put(KEY_SMOKER,
				new TreasureLootTable("kitchen_smoker", null, new TreasureEntry("minecraft:cooked_beef", 2, 5, 1),
						new TreasureEntry("minecraft:cooked_porkchop", 2, 5, 1),
						new TreasureEntry("minecraft:cooked_chicken", 2, 5, 1),
						new TreasureEntry("minecraft:cooked_potato", 3, 7, 1),
						new TreasureEntry("minecraft:cooked_rabbit", 2, 5, 1),
						new TreasureEntry("minecraft:cooked_mutton", 2, 5, 1)));
		DEFAULTS.put(KEY_SMOKER_OCEAN,
				new TreasureLootTable("kitchen_smoker_ocean", null, new TreasureEntry("minecraft:cooked_cod", 2, 5, 1),
						new TreasureEntry("minecraft:cooked_salmon", 2, 5, 1)));
	}

	@Override
	public File getFile() {
		return FMLPaths.CONFIGDIR.get().resolve("DungeonCrawl/loot/kitchen.json").toFile();
	}

	@Override
	public void load(JsonObject object, File file) {
		KITCHEN = DungeonCrawl.GSON.fromJson(JsonConfig.getOrRewrite(object, KEY_KITCHEN, this),
				TreasureLootTable.class);
		SMOKER = DungeonCrawl.GSON.fromJson(JsonConfig.getOrRewrite(object, KEY_SMOKER, this), TreasureLootTable.class);
		SMOKER_OCEAN = DungeonCrawl.GSON.fromJson(JsonConfig.getOrRewrite(object, KEY_SMOKER_OCEAN, this),
				TreasureLootTable.class);

		KITCHEN.build();
		SMOKER.build();
		SMOKER_OCEAN.build();
	}

	@Override
	public JsonObject create(JsonObject object) {
		object.add(KEY_KITCHEN, DungeonCrawl.GSON.toJsonTree(DEFAULTS.get(KEY_KITCHEN)));
		object.add(KEY_SMOKER, DungeonCrawl.GSON.toJsonTree(DEFAULTS.get(KEY_SMOKER)));
		object.add(KEY_SMOKER_OCEAN, DungeonCrawl.GSON.toJsonTree(DEFAULTS.get(KEY_SMOKER_OCEAN)));
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

}
