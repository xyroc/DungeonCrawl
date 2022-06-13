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
import com.google.common.collect.Lists;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.RandomSource;
import net.minecraft.util.Tuple;
import net.minecraft.world.item.Item;
import xiroc.dungeoncrawl.dungeon.monster.RandomEquipment;

import java.util.Collection;
import java.util.List;

public class WeightedRandom<T> implements IRandom<T> {

    public static final WeightedRandom.JsonReader<Item> ITEM = (entries) -> {
        WeightedRandom.Builder<Item> builder = new WeightedRandom.Builder<>();
        entries.forEach((element) -> {
            JsonObject entry = element.getAsJsonObject();
            int weight = JSONUtils.getWeight(entry);
            builder.add(RandomEquipment.getItem(new ResourceLocation(entry.get("item").getAsString())), weight);
        });
        return builder.build();
    };

    private final int totalWeight;
    private final ImmutableList<Tuple<T, Integer>> entries;

    // All the entries and their absolute weight values.
    private final ImmutableList<Tuple<T, Integer>> originalEntries;

    public WeightedRandom(List<Tuple<T, Integer>> entries) {
        this.originalEntries = ImmutableList.copyOf(entries);
        ImmutableList.Builder<Tuple<T, Integer>> builder = new ImmutableList.Builder<>();
        int weight = 0;
        for (Tuple<T, Integer> entry : entries) {
            if (entry.getB() > 0) {
                weight += entry.getB();
                builder.add(new Tuple<>(entry.getA(), weight));
            }
        }
        this.entries = builder.build();
        this.totalWeight = weight;
    }

    @Override
    public T roll(RandomSource rand) {
        int r = rand.nextInt(totalWeight);
        for (Tuple<T, Integer> entry : entries) {
            if (r < entry.getB()) {
                return entry.getA();
            }
        }
        return null;
    }

    public boolean isEmpty() {
        return entries.isEmpty();
    }

    public int size() {
        return entries.size();
    }

    public ImmutableList<Tuple<T, Integer>> getEntries() {
        return originalEntries;
    }

    public static class Builder<T> {

        public final List<Tuple<T, Integer>> entries;

        public Builder() {
            entries = Lists.newArrayList();
        }

        public WeightedRandom.Builder<T> add(T t, int weight) {
            entries.add(new Tuple<>(t, weight));
            return this;
        }

        public void addAll(Collection<Tuple<T, Integer>> entries) {
            this.entries.addAll(entries);
        }

        public WeightedRandom<T> build() {
            return new WeightedRandom<>(entries);
        }

    }

    @FunctionalInterface
    public interface JsonReader<T> {

        WeightedRandom<T> fromJson(JsonArray entries);

    }

}
