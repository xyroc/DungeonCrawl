package xiroc.dungeoncrawl.config;

/*
 * DungeonCrawl (C) 2019 - 2020 XYROC (XIROC1337), All Rights Reserved 
 */

import java.io.File;
import java.util.HashMap;

import com.google.gson.JsonObject;

import net.minecraftforge.fml.loading.FMLPaths;
import xiroc.dungeoncrawl.DungeonCrawl;
import xiroc.dungeoncrawl.util.IJsonConfigurable;

/*
 * This class is used to store obfuscated names of values. 
 */

public class ObfuscationValues implements IJsonConfigurable {

	public static String CHUNKGEN_WORLD, LOOT_POOL_ENTRIES;

	public static final String KEY_CHUNKGEN_WORLD = "net.minecraft.world.gen.ChunkGenerator # world";

	public static final String[] KEYS = new String[] { KEY_CHUNKGEN_WORLD };

	public static final HashMap<String, Object> DEFAULTS;

	static {
		DEFAULTS = new HashMap<String, Object>();
		DEFAULTS.put(KEY_CHUNKGEN_WORLD, "field_222540_a");
	}

	@Override
	public File getFile() {
		return FMLPaths.CONFIGDIR.get().resolve("DungeonCrawl/obfuscationValues.json").toFile();
	}

	@Override
	public void load(JsonObject object, File file) {
		CHUNKGEN_WORLD = DungeonCrawl.GSON.fromJson(JsonConfig.getOrRewrite(object, KEY_CHUNKGEN_WORLD, this), String.class);
	}

	@Override
	public JsonObject create(JsonObject object) {
		object.add(KEY_CHUNKGEN_WORLD, DungeonCrawl.GSON.toJsonTree(DEFAULTS.get(KEY_CHUNKGEN_WORLD)));
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
		return true;
	}

}
