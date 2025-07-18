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

import com.google.common.collect.ImmutableList;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.DynamicOps;
import com.mojang.serialization.JsonOps;
import xiroc.dungeoncrawl.dungeon.block.provider.BlockStateProvider;
import xiroc.dungeoncrawl.dungeon.decoration.DungeonDecoration;
import xiroc.dungeoncrawl.exception.DatapackLoadException;

import java.lang.reflect.Type;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;

public interface JSONUtils {
    Gson GSON = withTypeAdapters(List.of(
            BlockStateProvider::gsonAdapters,
            DungeonDecoration::gsonAdapters
    )).create();

    private static GsonBuilder withTypeAdapters(List<Consumer<GsonBuilder>> adapterProviders) {
        GsonBuilder builder = new GsonBuilder();
        for (var adapterProvider : adapterProviders) {
            adapterProvider.accept(builder);
        }
        return builder;
    }

    static <T> JsonElement encode(T instance, Codec<T> codec) {
        return codec.encodeStart(JsonOps.INSTANCE, instance).result().orElseThrow();
    }

    static <T> T parse(JsonElement json, Codec<T> codec) {
        return codec.parse(JsonOps.INSTANCE, json).getOrThrow(DatapackLoadException::new);
    }

    static <T> void serializeIfNonNull(JsonObject parent, String key, T thing, Function<T, JsonElement> serializer) {
        if (thing != null) {
            parent.add(key, serializer.apply(thing));
        }
    }

    static <T> T deserializeOrNull(JsonObject parent, String key, Function<JsonElement, T> deserializer) {
        if (parent.has(key)) {
            return deserializer.apply(parent.get(key));
        }
        return null;
    }

    /**
     * Deserialize a json array into an immutable list of any type.
     *
     * @param list    the json array
     * @param entryType    the type used to fetch the type adapter to deserialize the list entries
     * @param context the deserialization context
     */
    static <T> ImmutableList<T> deserializeList(JsonArray list, Type entryType, JsonDeserializationContext context) {
        final ImmutableList.Builder<T> listBuilder = ImmutableList.builder();
        for (JsonElement entry : list) {
            listBuilder.add(context.<T>deserialize(entry, entryType));
        }
        return listBuilder.build();
    }

    static <T> JsonArray serializeList(List<T> list, Type entryType, JsonSerializationContext context) {
        final JsonArray jsonArray = new JsonArray();
        for (T entry : list) {
            jsonArray.add(context.serialize(entry, entryType));
        }
        return jsonArray;
    }

    // Convenience function to create a codec that converts from/to JSON and delegates the actual serialization to a Gson serializer.
    // Used in a handful of places where a JSON serializer is more concise and readable than any codec (that I could write).
    static <T, S extends JsonSerializer<T> & JsonDeserializer<T>> Codec<T> codecFromJsonAdapter(Type type, S adapter) {
        return new Codec<>() {
            private final Gson GSON = new GsonBuilder().registerTypeAdapter(type, adapter).create();

            @Override
            public <T1> DataResult<Pair<T, T1>> decode(DynamicOps<T1> ops, T1 input) {
                try {
                    final JsonElement jsonInput = ops.convertTo(JsonOps.INSTANCE, input);
                    return DataResult.success(Pair.of(GSON.fromJson(jsonInput, type), ops.empty()));
                } catch (Exception e) {
                    return DataResult.error(e::getMessage);
                }
            }

            @Override
            public <T1> DataResult<T1> encode(T input, DynamicOps<T1> ops, T1 prefix) {
                try {
                    final JsonElement jsonOutput = GSON.toJsonTree(input, type);
                    return DataResult.success(JsonOps.INSTANCE.convertTo(ops, jsonOutput));
                } catch (Exception e) {
                    return DataResult.error(e::getMessage);
                }
            }
        };
    }
}
