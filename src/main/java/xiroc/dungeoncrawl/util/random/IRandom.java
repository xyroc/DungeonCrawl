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
import com.google.gson.JsonParseException;
import com.google.gson.JsonPrimitive;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Tuple;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.state.BlockState;
import xiroc.dungeoncrawl.datapack.DatapackRegistries;
import xiroc.dungeoncrawl.datapack.DatapackRegistry;
import xiroc.dungeoncrawl.datapack.delegate.Delegate;
import xiroc.dungeoncrawl.dungeon.blueprint.Blueprint;
import xiroc.dungeoncrawl.dungeon.blueprint.Blueprints;
import xiroc.dungeoncrawl.dungeon.monster.RandomEquipment;
import xiroc.dungeoncrawl.dungeon.monster.SpawnerEntityType;
import xiroc.dungeoncrawl.dungeon.monster.SpawnerSerializers;
import xiroc.dungeoncrawl.dungeon.monster.SpawnerType;
import xiroc.dungeoncrawl.dungeon.theme.PrimaryTheme;
import xiroc.dungeoncrawl.dungeon.theme.SecondaryTheme;
import xiroc.dungeoncrawl.util.JSONUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.function.Function;

/**
 * Used to create random objects of various types.
 */
public interface IRandom<T> {
    T roll(Random rand);

    class Builder<T> {
        private final List<Tuple<T, Integer>> entries = new ArrayList<>();

        public Builder<T> add(T value) {
            return add(value, 1);
        }

        public Builder<T> add(T value, int weight) {
            entries.add(new Tuple<>(value, weight));
            return this;
        }

        public IRandom<T> build() {
            if (entries.isEmpty()) {
                throw new IllegalStateException("Cannot build an empty IRandom instance");
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
            (json) -> RandomEquipment.getItem(new ResourceLocation(json.getAsString())),
            (item) -> new JsonPrimitive(item.getRegistryName().toString()), "item");

    Serializer<BlockState> BLOCK_STATE = new Serializer<>(
            JSONUtils::deserializeBlockState,
            JSONUtils::serializeBlockState, "block"
    );

    Serializer<Delegate<SpawnerEntityType>> SPAWNER_ENTITY = Serializer.referenceOrInlined(
            DatapackRegistries.SPAWNER_ENTITY_TYPE, "entity",
            (json) -> SpawnerSerializers.ENTITY_TYPES.fromJson(json, SpawnerEntityType.class),
            SpawnerSerializers.ENTITY_TYPES::toJsonTree);

    Serializer<Delegate<SpawnerType>> SPAWNER_TYPE = Serializer.reference(DatapackRegistries.SPAWNER_TYPE, "type");

    Serializer<Delegate<Blueprint>> BLUEPRINT = new Serializer<>(
            (json) -> {
                ResourceLocation key = new ResourceLocation(json.getAsString());
                if (!Blueprints.exists(key)) {
                    throw new JsonParseException("The blueprint " + key + " does not exist");
                }
                return Delegate.of(Blueprints.getBlueprint(key), key);
            },
            (blueprint) -> new JsonPrimitive(blueprint.key().toString()),
            "blueprint"
    );

    Serializer<Delegate<PrimaryTheme>> PRIMARY_THEME = Serializer.reference(DatapackRegistries.PRIMARY_THEME, "theme");
    Serializer<Delegate<SecondaryTheme>> SECONDARY_THEME = Serializer.reference(DatapackRegistries.SECONDARY_THEME, "theme");

    Serializer<ResourceLocation> IDENTIFIER = new Serializer<>(
            (json) -> new ResourceLocation(json.getAsString()),
            (identifier) -> new JsonPrimitive(identifier.toString()),
            "key");

    class Serializer<T> {
        public static <T> Serializer<Delegate<T>> reference(DatapackRegistry<T> registry, String valueKey) {
            return new Serializer<>(json -> Delegate.deserialize(json, registry), delegate -> new JsonPrimitive(delegate.key().toString()), valueKey);
        }

        public static <T> Serializer<Delegate<T>> referenceOrInlined(DatapackRegistry<T> registry, String valueKey,
                                                                     Function<JsonElement, T> deserializer, Function<T, JsonElement> serializer) {
            return new Serializer<>(json -> Delegate.deserialize(json, registry, deserializer), delegate -> delegate.serialize(serializer), valueKey);
        }

        private static final String KEY_WEIGHT = "weight";

        private final Function<JsonElement, T> deserializer;
        private final Function<T, JsonElement> serializer;
        private final String valueKey;

        public Serializer(Function<JsonElement, T> deserializer, Function<T, JsonElement> serializer, String valueKey) {
            this.deserializer = deserializer;
            this.serializer = serializer;
            this.valueKey = valueKey;
        }

        public void deserializePartial(JsonElement json, IRandom.Builder<T> builder) {
            if (!json.isJsonArray()) {
                builder.add(deserializer.apply(json));
            } else {
                for (JsonElement element : json.getAsJsonArray()) {
                    JsonObject object = element.getAsJsonObject();
                    int weight = object.has(KEY_WEIGHT) ? object.get(KEY_WEIGHT).getAsInt() : 1;
                    builder.add(deserializer.apply(object.get(valueKey)), weight);
                }
            }
        }

        public IRandom<T> deserialize(JsonElement json) {
            if (!json.isJsonArray()) {
                return new SingleValueRandom<>(deserializer.apply(json));
            } else {
                IRandom.Builder<T> builder = new Builder<>();
                deserializePartial(json, builder);
                return builder.build();
            }
        }

        public JsonElement serialize(IRandom<T> random) {
            if (random instanceof SingleValueRandom<T> singleValueRandom) {
                JsonElement value = serializer.apply(singleValueRandom.value());
                if (value.isJsonArray()) {
                    // Value is an array itself, so we cannot use the simplified representation
                    JsonArray wrapped = new JsonArray();
                    wrapped.add(value);
                    return wrapped;
                }
                return value;
            } else if (random instanceof WeightedRandom<T> weightedRandom) {
                JsonArray entries = new JsonArray();
                weightedRandom.forEach((value, weight) -> {
                    JsonObject entry = new JsonObject();
                    entry.addProperty(KEY_WEIGHT, weight);
                    entry.add(valueKey, serializer.apply(value));
                    entries.add(entry);
                });
                return entries;
            } else {
                throw new IllegalArgumentException("Unsupported random type: " + random.getClass());
            }
        }
    }
}
