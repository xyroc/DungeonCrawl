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

import com.google.gson.JsonObject;
import net.minecraft.util.ResourceLocation;
import xiroc.dungeoncrawl.DungeonCrawl;
import xiroc.dungeoncrawl.dungeon.generator.layer.LayerGeneratorSettings;
import xiroc.dungeoncrawl.exception.DatapackLoadException;
import xiroc.dungeoncrawl.util.JSONUtils;
import xiroc.dungeoncrawl.util.WeightedRandom;

public class ModelSelector {

    public WeightedRandom<DungeonModel> rooms;
    public WeightedRandom<DungeonModel> fullNodes, forkNodes, straightNodes, turnNodes, deadEndNodes;
    public WeightedRandom<DungeonModel> corridors, corridorLinkers, nodeConnectors;

    public ModelSelector(WeightedRandom<DungeonModel> rooms, WeightedRandom<DungeonModel> fullNodes,
                         WeightedRandom<DungeonModel> forkNodes, WeightedRandom<DungeonModel> straightNodes,
                         WeightedRandom<DungeonModel> turnNodes, WeightedRandom<DungeonModel> deadEndNodes,
                         WeightedRandom<DungeonModel> corridors, WeightedRandom<DungeonModel> corridorLinkers,
                         WeightedRandom<DungeonModel> nodeConnectors) {
        this.rooms = rooms;

        this.fullNodes = fullNodes;
        this.forkNodes = forkNodes;
        this.straightNodes = straightNodes;
        this.turnNodes = turnNodes;
        this.deadEndNodes = deadEndNodes;

        this.nodeConnectors = nodeConnectors;

        this.corridors = corridors;
        this.corridorLinkers = corridorLinkers;
    }

    public static ModelSelector fromJson(JsonObject object, ResourceLocation resource) {
        return new ModelSelector(
                loadRandom("rooms", object, resource),
                loadRandom("full_nodes", object, resource),
                loadRandom("fork_nodes", object, resource),
                loadRandom("straight_nodes", object, resource),
                loadRandom("turn_nodes", object, resource),
                loadRandom("dead_end_nodes", object, resource),
                loadRandom("corridors", object, resource),
                loadRandom("corridor_linkers", object, resource),
                loadRandom("node_connectors", object, resource)
        );
    }

    /**
     * Convenience method to load a single WeightedRandom instance from json.
     *
     * @param name     the name of the json object to load the WeightedRandom from.
     * @param object   the parent object
     * @param resource the file containing the json object
     * @return the WeightedRandom instance
     */
    private static WeightedRandom<DungeonModel> loadRandom(String name, JsonObject object, ResourceLocation resource) {
        WeightedRandom.Builder<DungeonModel> builder = new WeightedRandom.Builder<>();
        JsonObject models = object.getAsJsonObject(name);

        if (models.has("inherit")) {
            models.getAsJsonArray("inherit").forEach((element) -> {
                String key = element.getAsString();
                if (ModelPools.POOLS.containsKey(key)) {
                    builder.addAll(ModelPools.POOLS.get(key));
//                    DungeonCrawl.LOGGER.info(ModelPools.POOLS.get(key).size());
                } else {
                    throw new DatapackLoadException("Unknown model pool " + key + " in " + resource);
                }
            });
        }
        if (models.has("models")) {
            models.getAsJsonArray("models").forEach((element) -> {
                JsonObject entry = element.getAsJsonObject();
                String key = entry.get("key").getAsString();
                if (DungeonModels.KEY_TO_MODEL.containsKey(key)) {
                    builder.add(DungeonModels.KEY_TO_MODEL.get(key), JSONUtils.getWeightOrDefault(entry));
                } else {
                    throw new DatapackLoadException("Unknown model key " + key + " in " + resource);
                }
            });
        }
        if (builder.entries.isEmpty()) {
            throw new DatapackLoadException("Empty model list " + name + " in " + resource);
        }
        return builder.build();
    }

    public boolean verify(LayerGeneratorSettings settings) {
        if (settings.maxRooms > 0 && rooms.isEmpty()) {
            return false;
        }

        if (settings.maxNodes > 0
                && (deadEndNodes.isEmpty()
                || forkNodes.isEmpty()
                || straightNodes.isEmpty()
                || turnNodes.isEmpty()
                || fullNodes.isEmpty()
                || nodeConnectors.isEmpty())) {
            return false;
        }

        return !corridors.isEmpty() && !corridorLinkers.isEmpty();
    }

}
