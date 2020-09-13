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

/*
 * This class is used to store obfuscated names of values.
 */

public class ObfuscationValues implements IJsonConfigurable {

    public static String CHUNKGEN_WORLD, LOOT_POOL_ENTRIES;

    public static final String KEY_CHUNKGEN_WORLD = "net.minecraft.world.gen.ChunkGenerator # world";

    public static final String[] KEYS = new String[]{KEY_CHUNKGEN_WORLD};

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
    public boolean deleteOldVersions() {
        return true;
    }

}
