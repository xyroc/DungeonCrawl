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

package xiroc.dungeoncrawl.dungeon.model;

import com.google.common.collect.ImmutableSet;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import net.minecraft.resources.IResourceManager;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Tuple;
import xiroc.dungeoncrawl.DungeonCrawl;
import xiroc.dungeoncrawl.exception.DatapackLoadException;
import xiroc.dungeoncrawl.util.JSONUtils;

import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Hashtable;

public class ModelPools {

    public static final Hashtable<String, ImmutableSet<Tuple<DungeonModel, Integer>>> POOLS = new Hashtable<>();

    private static final ResourceLocation FILE = DungeonCrawl.locate("dungeon/model_pools.json");

    public static void load(IResourceManager resourceManager) {
        DungeonCrawl.LOGGER.debug("Loading {}", FILE);
        POOLS.clear();
        try {
            JsonObject file = DungeonCrawl.JSON_PARSER.parse(new InputStreamReader(resourceManager.getResource(FILE).getInputStream())).getAsJsonObject();
            JsonObject pools = file.getAsJsonObject("pools");

            pools.entrySet().forEach((entry) -> {
                ImmutableSet.Builder<Tuple<DungeonModel, Integer>> builder = new ImmutableSet.Builder<>();

                entry.getValue().getAsJsonArray().forEach((element) -> {
                    JsonObject modelEntry = element.getAsJsonObject();
                    String key = modelEntry.get("key").getAsString();
                    if (!DungeonModels.KEY_TO_MODEL.containsKey(key)) {
                        DungeonCrawl.LOGGER.warn("Cannot resolve model key " + key + " in " + FILE);
                    } else {
                        builder.add(new Tuple<>(DungeonModels.KEY_TO_MODEL.get(key), JSONUtils.getWeightOrDefault(modelEntry)));
                    }
                });

                ImmutableSet<Tuple<DungeonModel, Integer>> pool = builder.build();

                if (pool.isEmpty()) {
                    throw new DatapackLoadException("Empty model pool " + entry.getKey() + " in " + FILE);
                } else {
                    POOLS.put(entry.getKey(), pool);
                }
            });
        } catch (IOException e) {
            DungeonCrawl.LOGGER.error("Failed to load " + FILE);
            e.printStackTrace();
        }
    }

}
