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

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Tuple;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.state.BlockState;
import xiroc.dungeoncrawl.datapack.registry.DatapackRegistries;
import xiroc.dungeoncrawl.datapack.registry.DatapackRegistry;
import xiroc.dungeoncrawl.datapack.registry.Delegate;
import xiroc.dungeoncrawl.datapack.registry.InheritingBuilder;
import xiroc.dungeoncrawl.dungeon.blueprint.Blueprint;
import xiroc.dungeoncrawl.dungeon.monster.EquipmentHelper;
import xiroc.dungeoncrawl.dungeon.monster.SpawnerEntityType;
import xiroc.dungeoncrawl.dungeon.monster.SpawnerType;
import xiroc.dungeoncrawl.dungeon.theme.PrimaryTheme;
import xiroc.dungeoncrawl.dungeon.theme.SecondaryTheme;
import xiroc.dungeoncrawl.dungeon.type.DungeonType;
import xiroc.dungeoncrawl.dungeon.type.level.CorridorStyle;
import xiroc.dungeoncrawl.util.JSONUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Random;
import java.util.function.Function;

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

    Serializer<Item> ITEM = new Serializer<>(
            (json) -> EquipmentHelper.getItem(new ResourceLocation(json.getAsString())),
            (item) -> new JsonPrimitive(Objects.requireNonNull(item.getRegistryName()).toString()),
            "item"
    );

    Serializer<BlockState> BLOCK_STATE = new Serializer<>(
            JSONUtils::deserializeBlockState,
            JSONUtils::serializeBlockState,
            "block"
    );

    Serializer<Delegate<SpawnerEntityType>> SPAWNER_ENTITY = Serializer.referenceOrInlined(
            DatapackRegistries.SPAWNER_ENTITY_TYPE,
            "entity",
            (json) -> JSONUtils.GSON.fromJson(json, SpawnerEntityType.Builder.class).build(),
            (type) -> JSONUtils.GSON.toJsonTree(new SpawnerEntityType.Builder().copy(type))
    );

    Serializer<Delegate<SpawnerType>> SPAWNER_TYPE = Serializer.reference(DatapackRegistries.SPAWNER_TYPE, "type");
    Serializer<Delegate<Blueprint>> BLUEPRINT = Serializer.reference(DatapackRegistries.BLUEPRINT, "blueprint");
    Serializer<Delegate<PrimaryTheme>> PRIMARY_THEME = Serializer.reference(DatapackRegistries.PRIMARY_THEME, "theme");
    Serializer<Delegate<SecondaryTheme>> SECONDARY_THEME = Serializer.reference(DatapackRegistries.SECONDARY_THEME, "theme");

    Serializer<CorridorStyle> CORRIDOR_STYLE = new Serializer<>(
            json -> JSONUtils.GSON.fromJson(json, CorridorStyle.class),
            style -> JSONUtils.GSON.toJsonTree(style, CorridorStyle.class),
            "style"
    );

    Serializer<Delegate<DungeonType>> DUNGEON_TYPE = Serializer.reference(DatapackRegistries.DUNGEON_TYPE, "type");

    class Serializer<T> {
        public static <T> Serializer<Delegate<T>> reference(DatapackRegistry<T> registry, String valueKey) {
            return new Serializer<>(json -> Delegate.deserialize(json, registry), delegate -> new JsonPrimitive(delegate.key().toString()), valueKey);
        }

        public static <T> Serializer<Delegate<T>> referenceOrInlined(DatapackRegistry<T> registry, String valueKey,
                                                                     Function<JsonElement, T> deserializer, Function<T, JsonElement> serializer) {
            return new Serializer<>(json -> Delegate.deserialize(json, registry, deserializer), delegate -> delegate.serialize(serializer), valueKey);
        }

        private static final boolean REPLACE_BY_DEFAULT = InheritingBuilder.REPLACE_BY_DEFAULT;

        private static final String KEY_REPLACE = InheritingBuilder.KEY_REPLACE;
        private static final String KEY_VALUES = "values";
        private static final String KEY_WEIGHT = "weight";

        private final Function<JsonElement, T> deserializer;
        private final Function<T, JsonElement> serializer;
        private final String valueKey;

        public Serializer(Function<JsonElement, T> deserializer, Function<T, JsonElement> serializer, String valueKey) {
            this.deserializer = deserializer;
            this.serializer = serializer;
            this.valueKey = valueKey;
        }

        public IRandom.Builder<T> deserializeBuilder(JsonElement json) {
            Builder<T> builder = new Builder<>();
            if (json.isJsonPrimitive()) {
                builder.add(deserializer.apply(json));
            } else {
                if (json.isJsonObject()) {
                    JsonObject object = json.getAsJsonObject();
                    builder.replace(object.has(KEY_REPLACE) ? object.get(KEY_REPLACE).getAsBoolean() : REPLACE_BY_DEFAULT);
                    json = object.get(KEY_VALUES);
                }
                for (JsonElement element : json.getAsJsonArray()) {
                    if (element.isJsonPrimitive()) {
                        builder.add(deserializer.apply(element));
                        continue;
                    }
                    JsonObject object = element.getAsJsonObject();
                    int weight = object.has(KEY_WEIGHT) ? object.get(KEY_WEIGHT).getAsInt() : Builder.DEFAULT_WEIGHT;
                    builder.add(deserializer.apply(object.get(valueKey)), weight);
                }
            }
            return builder;
        }

        public IRandom<T> deserialize(JsonElement json) {
            return deserializeBuilder(json).build();
        }

        public JsonElement serializeBuilder(Builder<T> builder) {
            if (builder.entries.isEmpty()) {
                throw new IllegalStateException("Need at least one entry");
            }

            JsonArray entries = new JsonArray();
            builder.entries.forEach(entry -> {
                final T value = entry.getA();
                final int weight = entry.getB();

                JsonElement jsonEntry = serializer.apply(value);
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

        public JsonElement serialize(IRandom<T> random) {
            return serializeBuilder(Builder.copy(random));
        }
    }
}
