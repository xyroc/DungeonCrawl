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

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.commands.arguments.blocks.BlockStateParser;
import net.minecraft.core.Registry;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.Property;
import xiroc.dungeoncrawl.exception.DatapackLoadException;

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

    /**
     * Reads a block state from a json string
     *
     * @param json a json string representing the block state
     * @return the block state
     */
    public static BlockState deserializeBlockState(JsonElement json) {
        String state = json.getAsString();
        BlockStateParser parser = new BlockStateParser(new StringReader(state), false);
        try {
            parser.parse(false);
            if (parser.getState() == null) {
                throw new DatapackLoadException("Error while parsing block state: " + state);
            }
            return parser.getState();
        } catch (CommandSyntaxException e) {
            throw new DatapackLoadException("Could not parse block state: " + e.getMessage());
        }
    }

    /**
     * Serializes the given block state to a json string.
     *
     * @param state the block state
     * @return the serialized form of the block state
     */
    public static JsonElement serializeBlockState(BlockState state) {
        StringBuilder stateString = new StringBuilder(Registry.BLOCK.getKey(state.getBlock()).toString());

        if (!state.getProperties().isEmpty()) {
            StringBuilder properties = new StringBuilder();
            boolean comma = false;

            properties.append('[');
            for (Property<?> property : state.getProperties()) {
                if (state.getValue(property) == state.getBlock().defaultBlockState().getValue(property)) {
                    continue; // Only serialize non-default values
                }
                if (comma) {
                    properties.append(",");
                }
                comma = true;
                serializeProperty(properties, property, state.getValue(property));

            }
            properties.append(']');

            if (properties.length() > 2) {
                stateString.append(properties);
            }
        }

        return new JsonPrimitive(stateString.toString());
    }

    @SuppressWarnings("unchecked")
    private static <T extends Comparable<T>> void serializeProperty(StringBuilder builder, Property<T> property, Comparable<?> value) {
        builder.append(property.getName());
        builder.append("=");
        builder.append(property.getName((T) value));
    }
}
