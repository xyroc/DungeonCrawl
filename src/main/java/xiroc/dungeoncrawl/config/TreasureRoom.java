/*
        Dungeon Crawl, a procedural dungeon generator for Minecraft 1.14 and later.
        Copyright (C) 2020

        This program is free software: you can redistribute it and/or modify
        it under the terms of the GNU General Public License as published by
        the Free Software Foundation, either version 3 of the License, or
        (at your option) any later version.

        This program is distributed in the hope that it will be useful,
        but WITHOUT ANY WARRANTY; without even the implied warranty of
        MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
        GNU General Public License for more details.

        You should have received a copy of the GNU General Public License
        along with this program.  If not, see <https://www.gnu.org/licenses/>.
*/

package xiroc.dungeoncrawl.config;

import com.google.gson.JsonObject;
import net.minecraftforge.fml.loading.FMLPaths;
import xiroc.dungeoncrawl.DungeonCrawl;
import xiroc.dungeoncrawl.util.IJsonConfigurable;

import java.io.File;
import java.util.HashMap;

public class TreasureRoom implements IJsonConfigurable {

    public static final String KEY_TREASURE = "treasure_chest";

    public static final String[] KEYS = new String[]{KEY_TREASURE};

    public static final HashMap<String, Object> DEFAULTS;

//	public static TreasureLootTable TREASURE;

    static {
        DEFAULTS = new HashMap<String, Object>();
//		DEFAULTS.put(KEY_TREASURE, new TreasureLootTable(KEY_TREASURE, new RandomValueRange(12, 12),
//				new TreasureEntry[] { new TreasureEntry("minecraft:experience_bottle", 12, 36, 2),
//						new TreasureEntry("minecraft:gold_nugget", 8, 56, 4),
//						new TreasureEntry("minecraft:diamond", 1, 4, 5),
//						new TreasureEntry("minecraft:iron_ingot", 8, 24, 6),
//						new TreasureEntry("minecraft:cobweb", 2, 6, 2),
//						new TreasureEntry("minecraft:enchanted_golden_apple", 1),
//						new TreasureEntry("minecraft:diamond_helmet", 1),
//						new TreasureEntry("minecraft:diamond_chestplate", 1),
//						new TreasureEntry("minecraft:diamond_leggings", 1),
//						new TreasureEntry("minecraft:diamond_boots", 1),
//						new TreasureEntry("minecraft:diamond_sword", 1), new TreasureEntry("minecraft:golden_sword", 3),
//						new TreasureEntry("minecraft:golden_chestplate", 1),
//						new TreasureEntry("minecraft:nether_warts", 5, 26, 4),
//						new TreasureEntry("minecraft:arrow", 8, 16, 1), new TreasureEntry("minecraft:golden_carrot", 1),
//						new TreasureEntry("minecraft:golden_apple", 1),
//						new TreasureEntry("minecraft:slimeball", 2, 4, 3) }));
    }

    @Override
    public File getFile() {
        return FMLPaths.CONFIGDIR.get().resolve("DungeonCrawl/loot/treasureRoom.json").toFile();
    }

    @Override
    public void load(JsonObject object, File file) {
//		TREASURE = DungeonCrawl.GSON.fromJson(JsonConfig.getOrRewrite(object, KEY_TREASURE, this),
//				TreasureLootTable.class);
//
//		TREASURE.entries.add(TreasureItems.RANDOM_SPECIAL_ITEM.withWeight(10));
//		TREASURE.entries.add(TreasureItems.ENCHANTED_BOOK.withWeight(5));
//		TREASURE.entries.add(TreasureItems.POTION_REGENERATION_II);
//		TREASURE.entries.add(TreasureItems.NECTAR);
//		TREASURE.entries.add(TreasureItems.MATERIAL_BLOCKS);

//		TREASURE.build();

//		TreasureLootTable.LOOT_TABLES.add(TREASURE);

    }

    @Override
    public JsonObject create(JsonObject object) {
        object.add(KEY_TREASURE, DungeonCrawl.GSON.toJsonTree(DEFAULTS.get(KEY_TREASURE)));
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
