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

import com.google.common.collect.Lists;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import net.minecraft.util.Tuple;
import xiroc.dungeoncrawl.dungeon.decoration.IDungeonDecoration;

import java.util.Arrays;
import java.util.List;
import java.util.Random;

public class WeightedRandom<T> implements IRandom<T> {

    // TODO: migrate other WeightedRandom classes to this

    public static final WeightedRandom.JsonReader<IDungeonDecoration> DECORATION = (array) -> {
        WeightedRandom.Builder<IDungeonDecoration> builder = new WeightedRandom.Builder<>();
        array.forEach((element) -> {
            JsonObject object = element.getAsJsonObject();
            int weight = object.has("weight") ? object.get("weight").getAsInt() : 1;
            IDungeonDecoration decoration = IDungeonDecoration.fromJson(object);
            if (decoration != null) {
                builder.entries.add(new WeightedEntry(weight, decoration));
            }
        });
        return builder.build();
    };

    private final int totalWeight;
    private final WeightedEntry[] entries;

    private WeightedRandom(WeightedEntry[] entries) {
        int weight = 0;
        for (WeightedEntry entry : entries)
            weight += entry.getA();
        this.totalWeight = weight;
        this.entries = entries;
        this.assign(this.entries);
    }

    private void assign(WeightedEntry[] values) {
        int i = 0;
        int totalWeight = 0;
        for (WeightedEntry entry : values) {
            totalWeight += entry.getA();
            entries[i] = new WeightedRandom.WeightedEntry(totalWeight, entry.getB());
            i++;
        }
    }

    @SuppressWarnings("unchecked")
    @Override
    public T roll(Random rand) {
        int r = rand.nextInt(totalWeight);
        for (WeightedEntry entry : entries) {
            if (r < entry.getA()) {
                return (T) entry.getB();
            }
        }
        return null;
    }

    private static class WeightedEntry extends Tuple<Integer, Object> {

        public WeightedEntry(Integer aIn, Object bIn) {
            super(aIn, bIn);
        }

    }

    public static class Builder<T> {

        public List<WeightedEntry> entries;

        public Builder() {
            entries = Lists.newArrayList();
        }

        public WeightedRandom.Builder<T> add(T t, int weight) {
            entries.add(new WeightedEntry(weight, t));
            return this;
        }

        public WeightedRandom.Builder<T> add(WeightedEntry[] entries) {
            this.entries.addAll(Arrays.asList(entries));
            return this;
        }

        public WeightedRandom<T> build() {
            return new WeightedRandom<>(entries.toArray(new WeightedEntry[0]));
        }

    }

    @FunctionalInterface
    public interface JsonReader<T> {

        WeightedRandom<T> fromJson(JsonArray array);

    }

}
