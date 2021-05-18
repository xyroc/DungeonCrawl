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

package xiroc.dungeoncrawl.dungeon.generator.layer;

import com.google.gson.JsonObject;
import net.minecraft.loot.RandomValueRange;
import net.minecraft.util.ResourceLocation;
import xiroc.dungeoncrawl.exception.DatapackLoadException;

public class LayerGeneratorSettings {

    /**
     * The minimum and maximum amount of rooms
     */
    public final int minRooms, maxRooms;
    public final RandomValueRange rooms;

    /**
     * The minimum and maximum amount of nodes
     */
    public final int minNodes, maxNodes;
    public final RandomValueRange nodes;

    /**
     * The minimum and maximum distance between rooms.
     */
    public final int minDistance, maxDistance;

    /**
     * The overall maximum depth for recursive layer generation.
     */
    public final int maxDepth;

    /**
     * The minimum and maximum depth requirements for nodes.
     */
    public final int minNodeDepth, maxNodeDepth;

    /**
     * The minimum and maximum depth requirements for rooms.
     */
    public final int minRoomDepth, maxRoomDepth;

    /**
     * The minimum depth for the stairs to the next layer.
     */
    public final int minStairsDepth;

    public LayerGeneratorSettings(int minRooms, int maxRooms, int minNodes, int maxNodes, int minRoomDepth,
                                  int maxRoomDepth, int minNodeDepth, int maxNodeDepth, int minStairsDepth,
                                  int maxDepth, int minDistance, int maxDistance) {
        this.minRooms = minRooms;
        this.maxRooms = maxRooms;
        this.rooms = new RandomValueRange(minRooms, maxRooms);

        this.minNodes = minNodes;
        this.maxNodes = maxNodes;
        this.nodes = new RandomValueRange(minNodes, maxNodes);

        this.minRoomDepth = minRoomDepth;
        this.maxRoomDepth = maxRoomDepth;

        this.minNodeDepth = minNodeDepth;
        this.maxNodeDepth = maxNodeDepth;

        this.minStairsDepth = minStairsDepth;

        this.maxDepth = maxDepth;

        this.minDistance = minDistance;
        this.maxDistance = maxDistance;
    }

    public static LayerGeneratorSettings fromJson(JsonObject settings, ResourceLocation resource) {
        return new LayerGeneratorSettings(
                getValue("min_rooms", settings, resource),
                getValue("max_rooms", settings, resource),
                getValue("min_nodes", settings, resource),
                getValue("max_nodes", settings, resource),
                getValue("min_room_depth", settings, resource),
                getValue("max_room_depth", settings, resource),
                getValue("min_node_depth", settings, resource),
                getValue("max_node_depth", settings, resource),
                getValue("min_stairs_depth", settings, resource),
                getValue("max_depth", settings, resource),
                getValue("min_distance", settings, resource),
                getValue("max_distance", settings, resource));
    }

    private static int getValue(String name, JsonObject object, ResourceLocation resource) {
        if (object.has(name)) {
            return object.getAsJsonPrimitive(name).getAsInt();
        } else {
            throw new DatapackLoadException("Missing layer settings field " + name + " in " + resource);
        }
    }

}
