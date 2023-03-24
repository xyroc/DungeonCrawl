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

package xiroc.dungeoncrawl.util.random;

import com.google.common.collect.Lists;
import com.google.gson.JsonArray;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Tuple;
import net.minecraft.world.item.Item;
import xiroc.dungeoncrawl.dungeon.monster.RandomEquipment;
import xiroc.dungeoncrawl.util.JSONUtils;

import java.lang.reflect.Type;
import java.util.Collection;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Function;

public interface WeightedRandom<T> extends IRandom<T> {
    Serializer<Item> ITEM = Serializer.of(
            (json) -> RandomEquipment.getItem(new ResourceLocation(json.getAsString())),
            (item) -> {
                ResourceLocation registryName = item.getRegistryName();
                if (registryName == null) {
                    throw new JsonParseException("Item does not have a registry name");
                }
                return new JsonPrimitive(registryName.toString());
            }, "item");

    boolean isEmpty();

    void forEach(BiConsumer<T, Integer> consumer);

    class Builder<T> {
        public final List<Tuple<T, Integer>> entries;

        public Builder() {
            entries = Lists.newArrayList();
        }

        public WeightedRandom.Builder<T> add(T value) {
            return add(value, 1);
        }

        public WeightedRandom.Builder<T> add(T value, int weight) {
            entries.add(new Tuple<>(value, weight));
            return this;
        }

        public void addAll(Collection<Tuple<T, Integer>> entries) {
            this.entries.addAll(entries);
        }

        public WeightedRandom<T> build() {
            return entries.size() > 32 ? new AVLTreeWeightedRandom<>(entries) : new ListWeightedRandom<>(entries);
        }
    }

    interface Serializer<T> extends JsonSerializer<WeightedRandom<T>>, JsonDeserializer<WeightedRandom<T>> {
        WeightedRandom<T> deserialize(JsonElement entries);

        void deserialize(JsonElement entries, WeightedRandom.Builder<T> builder);

        JsonElement serialize(WeightedRandom<T> random);

        static <P> Serializer<P> of(Function<JsonElement, P> deserializer, Function<P, JsonElement> serializer, String key) {
            return new Serializer<>() {
                @Override
                public JsonElement serialize(WeightedRandom<P> src, Type typeOfSrc, JsonSerializationContext context) {
                    return serialize(src);
                }

                @Override
                public WeightedRandom<P> deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
                    return deserialize(json);
                }

                @Override
                public WeightedRandom<P> deserialize(JsonElement entries) {
                    WeightedRandom.Builder<P> builder = new WeightedRandom.Builder<>();
                    deserialize(entries, builder);
                    return builder.build();
                }

                public void deserialize(JsonElement entries, WeightedRandom.Builder<P> builder) {
                    entries.getAsJsonArray().forEach((entry) -> {
                        JsonObject object = entry.getAsJsonObject();
                        builder.add(deserializer.apply(object.get(key)), JSONUtils.getWeight(object));
                    });
                }

                @Override
                public JsonElement serialize(WeightedRandom<P> random) {
                    JsonArray serialized = new JsonArray();
                    random.forEach((value, weight) -> {
                        JsonObject object = new JsonObject();
                        object.add(key, serializer.apply(value));
                        object.addProperty("weight", weight);
                        serialized.add(object);
                    });
                    return serialized;
                }
            };
        }
    }
}