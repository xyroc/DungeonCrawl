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
import net.minecraft.resources.Identifier;
import net.minecraft.util.RandomSource;
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
            builder.add(RandomEquipment.getItem(Identifier.parse(entry.get("item").getAsString())), weight);
        });
        return builder.build();
    };

    private final int totalWeight;
    private final ImmutableList<Pair<T, Integer>> entries;

    // All the entries and their absolute weight values.
    private final ImmutableList<Pair<T, Integer>> originalEntries;

    public WeightedRandom(List<Pair<T, Integer>> entries) {
        this.originalEntries = ImmutableList.copyOf(entries);
        ImmutableList.Builder<Pair<T, Integer>> builder = new ImmutableList.Builder<>();
        int weight = 0;
        for (Pair<T, Integer> entry : entries) {
            if (entry.right() > 0) {
                weight += entry.right();
                builder.add(new Pair<>(entry.left(), weight));
            }
        }
        this.entries = builder.build();
        this.totalWeight = weight;
    }

    @Override
    public T roll(RandomSource rand) {
        int r = rand.nextInt(totalWeight);
        for (Pair<T, Integer> entry : entries) {
            if (r < entry.right()) {
                return entry.left();
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

    public ImmutableList<Pair<T, Integer>> getEntries() {
        return originalEntries;
    }

    public static class Builder<T> {

        public final List<Pair<T, Integer>> entries;

        public Builder() {
            entries = Lists.newArrayList();
        }

        public WeightedRandom.Builder<T> add(T t, int weight) {
            entries.add(new Pair<>(t, weight));
            return this;
        }

        public void addAll(Collection<Pair<T, Integer>> entries) {
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
