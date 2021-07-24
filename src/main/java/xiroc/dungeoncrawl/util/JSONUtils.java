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

package xiroc.dungeoncrawl.util;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.minecraft.core.Vec3i;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraftforge.fml.ModList;
import xiroc.dungeoncrawl.DungeonCrawl;
import xiroc.dungeoncrawl.dungeon.model.DungeonModels;

import java.util.Optional;

public class JSONUtils {

    public static boolean areRequirementsMet(JsonObject object) {
        if (object.has("requirements")) {
            JsonObject conditions = object.getAsJsonObject("requirements");
            if (conditions.has("present")) {
                JsonArray present = conditions.getAsJsonArray("present");
                for (JsonElement mod : present) {
                    if (!ModList.get().isLoaded(mod.getAsString())) return false;
                }
            }
            if (conditions.has("absent")) {
                JsonArray present = conditions.getAsJsonArray("absent");
                for (JsonElement mod : present) {
                    if (ModList.get().isLoaded(mod.getAsString())) return false;
                }
            }
        }
        return true;
    }

    public static BlockState getBlockState(Block block, JsonObject element) {
        BlockState state = block.defaultBlockState();
        if (element.has("properties")) {
            JsonObject data = element.get("properties").getAsJsonObject();
            for (Property<?> property : state.getProperties()) {
                if (data.has(property.getName())) {
                    state = parseProperty(state, property, data.get(property.getName()).getAsString());
                }
            }
        }
        return state;
    }

    public static int getWeight(JsonObject object) {
        return object.has("weight") ? object.get("weight").getAsInt() : 1;
    }

    public static Vec3i getOffset(JsonObject jsonObject) {
        int x = 0, y = 0, z = 0;
        if (jsonObject.has("x")) {
            x = jsonObject.get("x").getAsInt();
        }
        if (jsonObject.has("y")) {
            y = jsonObject.get("y").getAsInt();
        }
        if (jsonObject.has("z")) {
            z = jsonObject.get("z").getAsInt();
        }
        if (x == 0 && y == 0 && z == 0) {
            return DungeonModels.NO_OFFSET;
        }
        return new Vec3i(x, y, z);
    }

    /**
     * Applies the property value to the BlockState. It is required that the BlockState supports that property.
     *
     * @return the resulting block state
     */
    private static <T extends Comparable<T>> BlockState parseProperty(BlockState state, Property<T> property,
                                                                      String value) {
        Optional<T> optional = property.getValue(value);
        if (optional.isPresent()) {
            T t = optional.get();
            return state.setValue(property, t);
        } else {
            DungeonCrawl.LOGGER.warn("Couldn't apply property {} with value {} to {}", property.getName(), value, state.getBlock().getRegistryName());
        }
        return state;
    }

}
