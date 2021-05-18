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
import xiroc.dungeoncrawl.util.IJsonConfigurable;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

public class JsonConfig implements IJsonConfigurable {

    public static final String KEY_CONFIG_VERSION = "config_version";

    public static final String KEY_BIOME_BLOCKLIST = "biome_blocklist",
            KEY_BIOME_OVERWORLD_BLOCKLIST = "biome_overworld_blocklist",
            KEY_BOSSES = "dungeon_bosses";

    public static final String[] KEYS = new String[]{KEY_BOSSES, KEY_BIOME_BLOCKLIST, KEY_BIOME_OVERWORLD_BLOCKLIST};

    public static List<?> BIOME_BLOCKLIST, BIOME_OVERWORLD_BLOCKLIST;

    public static void load() {
        load(new JsonConfig());
        load(new SpecialItemTags());
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
                if (!configurable.deleteOldVersions()) {
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

        public static final HashMap<String, Object> DEFAULTS;

        static {
            DEFAULTS = new HashMap<>();
            DEFAULTS.put(KEY_BIOME_BLOCKLIST, BIOME_BLACKLIST);
            DEFAULTS.put(KEY_BIOME_OVERWORLD_BLOCKLIST, BIOME_OVERWORLD_BLACKLIST);
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
                DungeonCrawl.LOGGER.error("An error occurred whilst trying to rewrite {}", file.getAbsolutePath());
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
//		DUNGEON_BOSSES = DungeonCrawl.GSON.fromJson(getOrRewrite(object, KEY_BOSSES, this), BossEntry[].class);

        BIOME_BLOCKLIST = DungeonCrawl.GSON.fromJson(getOrRewrite(object, KEY_BIOME_BLOCKLIST, this), ArrayList.class);
        BIOME_OVERWORLD_BLOCKLIST = DungeonCrawl.GSON
                .fromJson(getOrRewrite(object, KEY_BIOME_OVERWORLD_BLOCKLIST, this), ArrayList.class);
    }

    @Override
    public JsonObject create(JsonObject object) {
        object.add(KEY_BIOME_BLOCKLIST, DungeonCrawl.GSON.toJsonTree(JsonConfigManager.BIOME_BLACKLIST));
        object.add(KEY_BIOME_OVERWORLD_BLOCKLIST,
                DungeonCrawl.GSON.toJsonTree(JsonConfigManager.BIOME_OVERWORLD_BLACKLIST));
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
