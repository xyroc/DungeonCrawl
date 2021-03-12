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

import com.google.gson.JsonObject;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.state.IProperty;
import net.minecraft.util.math.Vec3i;
import xiroc.dungeoncrawl.DungeonCrawl;
import xiroc.dungeoncrawl.dungeon.model.DungeonModels;

import java.util.Optional;

public class JSONUtils {

    public static BlockState getBlockState(Block block, JsonObject element) {
        BlockState state = block.getDefaultState();
        if (element.has("data")) {
            JsonObject data = element.get("data").getAsJsonObject();
            for (IProperty<?> property : state.getProperties()) {
                if (data.has(property.getName())) {
                    state = parseProperty(state, property, data.get(property.getName()).getAsString());
                }
            }
        }
        return state;
    }

    public static int getWeightOrDefault(JsonObject object) {
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
     * Applies the property value to the state. It is required that the given state does have that property.
     *
     * @return the resulting block state
     */
    private static <T extends Comparable<T>> BlockState parseProperty(BlockState state, IProperty<T> property,
                                                                      String value) {
        Optional<T> optional = property.parseValue(value);
        if (optional.isPresent()) {
            T t = optional.get();
            return state.with(property, t);
        } else {
            DungeonCrawl.LOGGER.warn("Couldn't apply property {} with value {} to {}", property.getName(), value, state.getBlock().getRegistryName());
        }
        return state;
    }

}
