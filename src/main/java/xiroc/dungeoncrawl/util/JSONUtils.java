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
import com.google.gson.JsonPrimitive;
import net.minecraft.core.Registry;
import net.minecraft.core.Vec3i;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraftforge.fml.ModList;
import xiroc.dungeoncrawl.DungeonCrawl;

import java.util.Objects;
import java.util.Optional;
import java.util.function.Function;

public class JSONUtils {
    public static <T> void serializeIfNonNull(JsonObject parent, String key, T thing, Function<T, JsonElement> serializer) {
        if (thing != null) {
            parent.add(key, serializer.apply(thing));
        }
    }

    public static <T> T deserializeOrNull(JsonObject parent, String key, Function<JsonElement, T> deserializer) {
        if (parent.has(key)) {
            return deserializer.apply(parent.get(key));
        }
        return null;
    }

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
            return Vec3i.ZERO;
        }
        return new Vec3i(x, y, z);
    }

    /**
     * Applies the property value to the BlockState. It is required that the BlockState supports that property.
     *
     * @return the resulting block state
     */
    private static <T extends Comparable<T>> BlockState parseProperty(BlockState state, Property<T> property, String value) {
        Optional<T> optional = property.getValue(value);
        if (optional.isPresent()) {
            T t = optional.get();
            return state.setValue(property, t);
        } else {
            DungeonCrawl.LOGGER.warn("Couldn't apply property {} with value {} to {}", property.getName(), value, state.getBlock().getRegistryName());
        }
        return state;
    }

    private static final String KEY_BLOCK_NAME = "name";
    private static final String KEY_BLOCK_PROPERTIES = "properties";

    public static BlockState deserializeBlockState(JsonElement json) {
        if (json.isJsonPrimitive()) {
            return Registry.BLOCK.get(new ResourceLocation(json.getAsString())).defaultBlockState();
        }
        JsonObject object = json.getAsJsonObject();
        BlockState state = Registry.BLOCK.get(new ResourceLocation(object.get(KEY_BLOCK_NAME).getAsString())).defaultBlockState();

        if (object.has(KEY_BLOCK_PROPERTIES)) {
            JsonObject properties = object.getAsJsonObject(KEY_BLOCK_PROPERTIES);
            for (Property<?> property : state.getProperties()) {
                if (properties.has(property.getName())) {
                    state = parseProperty(state, property, properties.get(property.getName()).getAsString());
                }
            }
        }

        return state;
    }

    /**
     * Serializes the given block state.
     *
     * @param state the block state
     * @return the serialized form of the block state
     */
    public static JsonElement serializeBlockState(BlockState state) {
        String name = Objects.requireNonNull(state.getBlock().getRegistryName()).toString();
        if (state.equals(state.getBlock().defaultBlockState())) {
            return new JsonPrimitive(name);
        }
        JsonObject object = new JsonObject();
        Block block = state.getBlock();
        object.addProperty(KEY_BLOCK_NAME, name);

        BlockState defaultState = block.defaultBlockState();
        JsonObject properties = new JsonObject();
        state.getProperties().forEach((property) -> {
            if (!state.getValue(property).equals(defaultState.getValue(property))) {
                properties.addProperty(property.getName(), state.getValue(property).toString());
            }
        });

        if (properties.size() > 0) {
            object.add(KEY_BLOCK_PROPERTIES, properties);
        }
        return object;
    }

}
