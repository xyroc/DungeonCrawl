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
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import com.google.gson.reflect.TypeToken;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Tuple;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;
import xiroc.dungeoncrawl.datapack.registry.DatapackRegistry;
import xiroc.dungeoncrawl.datapack.registry.Delegate;
import xiroc.dungeoncrawl.datapack.registry.InheritingBuilder;
import xiroc.dungeoncrawl.exception.DatapackLoadException;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.function.BiConsumer;

/**
 * Used to provide random objects of various types.
 */
public interface IRandom<T> {
    T roll(Random rand);

    int totalWeight();

    void forEach(BiConsumer<T, Integer> consumer);

    class Builder<T> extends InheritingBuilder<IRandom<T>, Builder<T>> {
        private static final int DEFAULT_WEIGHT = 1;

        private final List<Tuple<T, Integer>> entries = new ArrayList<>();
        private final List<Delegate<IRandom<T>>> pools = new ArrayList<>();

        public static <T> Builder<T> copy(IRandom<T> instance) {
            return new Builder<T>().add(instance);
        }

        public Builder<T> addPool(Delegate<IRandom<T>> pool) {
            pools.add(pool);
            return this;
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
            random.forEach(this::add);
            return this;
        }

        @Override
        public Builder<T> inherit(Builder<T> from) {
            return add(from);
        }

        @Nullable
        private IRandom<T> buildEntries() {
            if (entries.isEmpty()) {
                return null;
            }
            if (entries.size() == 1) {
                return new SingleValueRandom<>(entries.get(0).getA());
            }
            if (entries.size() < 32) {
                return new ListWeightedRandom<>(entries);
            }
            return new AVLTreeWeightedRandom<>(entries);
        }

        @Override
        public IRandom<T> build() {
            IRandom<T> base = buildEntries();
            if (base != null) {
                if (pools.isEmpty()) {
                    return base;
                } else {
                    pools.add(Delegate.of(base));
                    return new PooledRandom<>(pools);
                }
            } else {
                if (pools.isEmpty()) {
                    throw new IllegalStateException("Random instance cannot be empty.");
                } else {
                    return new PooledRandom<>(pools);
                }
            }
        }
    }

    static void gsonAdapters(GsonBuilder builder) {
        builder.registerTypeAdapter(VanillaTypes.Builder.BLOCK_STATE, new BuilderSerializer<BlockState>(BlockState.class, "block", null).wrapped())
                .registerTypeAdapter(VanillaTypes.Builder.ITEM, new BuilderSerializer<Item>(Item.class, "item", null).wrapped())
                .registerTypeAdapter(VanillaTypes.BLOCK_STATE, new DirectSerializer<BlockState>(VanillaTypes.Builder.BLOCK_STATE));
    }

    interface VanillaTypes {
        Type BLOCK_STATE = new TypeToken<IRandom<BlockState>>() {}.getType();

        interface Builder {
            Type BLOCK_STATE = new TypeToken<IRandom.Builder<BlockState>>() {}.getType();
            Type ITEM = new TypeToken<IRandom.Builder<Item>>() {}.getType();
        }
    }

    record BuilderSerializer<T>(Type valueType, String valueKey, @Nullable DatapackRegistry<IRandom<T>> globalPools) implements JsonSerializer<Builder<T>>, JsonDeserializer<Builder<T>> {
        private static final String KEY_VALUES = "values";
        private static final String KEY_WEIGHT = "weight";
        private static final String KEY_POOLS = "pools";

        public BuilderSerializer(Type valueType, String valueKey) {
            this(valueType, valueKey, null);
        }

        private void loadPool(JsonElement json, Builder<T> builder) {
            if (globalPools != null) {
                ResourceLocation poolKey = new ResourceLocation(json.getAsString());
                builder.addPool(globalPools.delegateOrThrow(poolKey));
            } else {
                throw new DatapackLoadException("Global Pools are not supported for this type");
            }
        }

        public InheritingBuilder.WrappedSerializer<IRandom<T>, Builder<T>> wrapped() {
            return InheritingBuilder.WrappedSerializer.of(this);
        }

        @Override
        public Builder<T> deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
            final Builder<T> builder = new Builder<>();
            if (json.isJsonPrimitive()) {
                loadPool(json, builder);
            } else {
                if (json.isJsonObject()) {
                    JsonObject object = json.getAsJsonObject();
                    if (object.has(KEY_POOLS)) {
                        for (JsonElement poolName : object.getAsJsonArray(KEY_POOLS)) {
                            loadPool(poolName, builder);
                        }
                    }
                    if (!object.has(KEY_VALUES)) {
                        return builder;
                    }
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
            JsonArray entries = new JsonArray();
            for (Tuple<T, Integer> entry : builder.entries) {
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
            }

            boolean needJsonObject = InheritingBuilder.mustSerializeToJsonObject(builder);
            boolean serializeEntries = !entries.isEmpty();

            if (!needJsonObject && !serializeEntries && builder.pools.size() == 1) {
                return new JsonPrimitive(builder.pools.get(0).key().toString());
            }

            boolean serializePools = !builder.pools.isEmpty();
            boolean wrapResult = needJsonObject || serializePools;

            if (wrapResult) {
                JsonObject wrapped = new JsonObject();
                if (serializeEntries) {
                    wrapped.add(KEY_VALUES, entries);
                }
                if (serializePools) {
                    JsonArray pools = new JsonArray();
                    for (Delegate<IRandom<T>> pool : builder.pools) {
                        pools.add(pool.key().toString());
                    }
                    wrapped.add(KEY_POOLS, pools);
                }
                return wrapped;
            }

            return entries;
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
