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
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.state.BlockState;
import xiroc.dungeoncrawl.dungeon.block.provider.BlockStateProvider;
import xiroc.dungeoncrawl.dungeon.blueprint.feature.BlueprintFeature;
import xiroc.dungeoncrawl.dungeon.blueprint.template.TemplateBlueprint;
import xiroc.dungeoncrawl.dungeon.decoration.DungeonDecoration;
import xiroc.dungeoncrawl.dungeon.monster.SpawnerSerializers;
import xiroc.dungeoncrawl.dungeon.theme.ThemeSerializers;
import xiroc.dungeoncrawl.dungeon.tier.TieredResource;
import xiroc.dungeoncrawl.dungeon.type.DungeonTypeSerializers;
import xiroc.dungeoncrawl.util.json.BlockStateSerializer;
import xiroc.dungeoncrawl.util.json.ItemSerializer;
import xiroc.dungeoncrawl.util.random.RandomMapping;
import xiroc.dungeoncrawl.util.random.value.RandomValue;

import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;

public interface JSONUtils {
    Gson GSON = withTypeAdapters(List.of(
            JSONUtils::gsonAdapters,
            BlockStateProvider::gsonAdapters,
            BlueprintFeature::gsonAdapters,
            TemplateBlueprint::gsonAdapters,
            DungeonDecoration::gsonAdapters,
            SpawnerSerializers::gsonAdapters,
            ThemeSerializers::gsonAdapters,
            DungeonTypeSerializers::gsonAdapters,
            RandomMapping::gsonAdapters,
            RandomValue::gsonAdapters,
            TieredResource::gsonAdapters)).create();

    static void gsonAdapters(GsonBuilder builder) {
        builder.registerTypeAdapter(Item.class, new ItemSerializer())
                .registerTypeAdapter(BlockState.class, new BlockStateSerializer());
    }

    private static GsonBuilder withTypeAdapters(List<Consumer<GsonBuilder>> adapterProviders) {
        GsonBuilder builder = new GsonBuilder();
        for (var adapterProvider : adapterProviders) {
            adapterProvider.accept(builder);
        }
        return builder;
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
     * @param list         the json array
     * @param deserializer the function to deserialize an element
     */
    static <T> ImmutableList<T> deserializeList(JsonArray list, Function<JsonElement, T> deserializer) {
        final ImmutableList.Builder<T> listBuilder = ImmutableList.builder();
        for (JsonElement entry : list) {
            listBuilder.add(deserializer.apply(entry));
        }
        return listBuilder.build();
    }

    /**
     * Serialize a list of any type into a json array.
     *
     * @param list       the list
     * @param serializer the function to serialize an element
     */
    static <T> JsonArray serializeList(List<T> list, Function<T, JsonElement> serializer) {
        final JsonArray jsonArray = new JsonArray();
        for (T thing : list) {
            jsonArray.add(serializer.apply(thing));
        }
        return jsonArray;
    }
}
