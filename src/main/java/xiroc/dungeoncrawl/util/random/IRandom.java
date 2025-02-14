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

import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import com.google.gson.reflect.TypeToken;
import net.minecraft.util.Tuple;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.state.BlockState;
import xiroc.dungeoncrawl.datapack.registry.InheritingBuilder;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Used to provide random objects of various types.
 */
public interface IRandom<T> {
    T roll(Random rand);

    class Builder<T> extends InheritingBuilder<IRandom<T>, Builder<T>> {
        private static final int DEFAULT_WEIGHT = 1;

        private final List<Tuple<T, Integer>> entries = new ArrayList<>();

        public static <T> Builder<T> copy(IRandom<T> instance) {
            return new Builder<T>().add(instance);
        }

        public Builder<T> add(T value) {
            return add(value, DEFAULT_WEIGHT);
        }

        public Builder<T> add(T value, int weight) {
            entries.add(new Tuple<>(value, weight));
            return this;
        }

        public Builder<T> add(Builder<T> builder) {
            entries.addAll(builder.entries);
            return this;
        }

        public Builder<T> add(IRandom<T> random) {
            if (random instanceof SingleValueRandom<T> singleValueRandom) {
                add(singleValueRandom.value());
            } else if (random instanceof WeightedRandom<T> weightedRandom) {
                weightedRandom.forEach(this::add);
            } else {
                throw new IllegalArgumentException("Unsupported IRandom type: " + random.getClass());
            }
            return this;
        }

        @Override
        public Builder<T> inherit(Builder<T> from) {
            return add(from);
        }

        @Override
        public IRandom<T> build() {
            if (entries.isEmpty()) {
                throw new IllegalStateException("Need at least one entry");
            }
            if (entries.size() == 1) {
                return new SingleValueRandom<>(entries.get(0).getA());
            }
            if (entries.size() < 32) {
                return new ListWeightedRandom<>(entries);
            }
            return new AVLTreeWeightedRandom<>(entries);
        }
    }

    static void gsonAdapters(GsonBuilder builder) {
        builder.registerTypeAdapter(VanillaTypes.Builder.BLOCK_STATE, new BuilderSerializer<BlockState>(BlockState.class, "block"))
                .registerTypeAdapter(VanillaTypes.Builder.ITEM, new BuilderSerializer<Item>(Item.class, "item"))
                .registerTypeAdapter(VanillaTypes.BLOCK_STATE, new DirectSerializer<BlockState>(VanillaTypes.Builder.BLOCK_STATE));
    }

    interface VanillaTypes {
        Type BLOCK_STATE = new TypeToken<IRandom<BlockState>>() {}.getType();

        interface Builder {
            Type BLOCK_STATE = new TypeToken<IRandom.Builder<BlockState>>() {}.getType();
            Type ITEM = new TypeToken<IRandom.Builder<Item>>() {}.getType();
        }
    }

    record BuilderSerializer<T>(Type valueType, String valueKey) implements JsonSerializer<Builder<T>>, JsonDeserializer<Builder<T>> {
        private static final boolean REPLACE_BY_DEFAULT = InheritingBuilder.REPLACE_BY_DEFAULT;

        private static final String KEY_REPLACE = InheritingBuilder.KEY_REPLACE;
        private static final String KEY_VALUES = "values";
        private static final String KEY_WEIGHT = "weight";

        @Override
        public Builder<T> deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
            final Builder<T> builder = new Builder<>();
            if (json.isJsonPrimitive()) {
                builder.add(context.<T>deserialize(json, valueType));
            } else {
                if (json.isJsonObject()) {
                    JsonObject object = json.getAsJsonObject();
                    builder.replace(object.has(KEY_REPLACE) ? object.get(KEY_REPLACE).getAsBoolean() : REPLACE_BY_DEFAULT);
                    json = object.get(KEY_VALUES);
                }
                for (JsonElement element : json.getAsJsonArray()) {
                    if (element.isJsonPrimitive()) {
                        builder.add(context.<T>deserialize(element, valueType));
                        continue;
                    }
                    JsonObject object = element.getAsJsonObject();
                    int weight = object.has(KEY_WEIGHT) ? object.get(KEY_WEIGHT).getAsInt() : Builder.DEFAULT_WEIGHT;
                    builder.add(context.deserialize(object.get(valueKey), valueType), weight);
                }
            }
            return builder;
        }

        @Override
        public JsonElement serialize(Builder<T> builder, Type typeOfSrc, JsonSerializationContext context) {
            if (builder.entries.isEmpty()) {
                throw new IllegalStateException("Need at least one entry");
            }

            JsonArray entries = new JsonArray();
            builder.entries.forEach(entry -> {
                final T value = entry.getA();
                final int weight = entry.getB();

                JsonElement jsonEntry = context.serialize(value, valueType);
                if (weight != Builder.DEFAULT_WEIGHT || jsonEntry.isJsonObject()) {
                    JsonObject entryObject = new JsonObject();
                    entryObject.add(valueKey, jsonEntry);
                    if (weight != Builder.DEFAULT_WEIGHT) {
                        entryObject.addProperty(KEY_WEIGHT, weight);
                    }
                    jsonEntry = entryObject;
                }
                entries.add(jsonEntry);
            });

            JsonElement serialized = entries;

            if (entries.size() == 1 && entries.get(0).isJsonPrimitive()) {
                serialized = entries.get(0);
            }

            if (builder.replace() != REPLACE_BY_DEFAULT) {
                JsonObject wrapped = new JsonObject();
                wrapped.add(KEY_VALUES, serialized);
                wrapped.addProperty(KEY_REPLACE, builder.replace());
                return wrapped;
            }

            return serialized;
        }
    }

    record DirectSerializer<T>(Type builderType) implements JsonSerializer<IRandom<T>>, JsonDeserializer<IRandom<T>> {
        @Override
        public IRandom<T> deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
            final Builder<T> builder = context.deserialize(json, builderType);
            return builder.build();
        }

        @Override
        public JsonElement serialize(IRandom<T> src, Type typeOfSrc, JsonSerializationContext context) {
            final Builder<T> builder = new Builder<>();
            builder.add(src);
            return context.serialize(builder, builderType);
        }
    }
}
