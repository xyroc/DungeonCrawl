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

package xiroc.dungeoncrawl.dungeon.monster;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.RandomSource;
import net.minecraft.util.Tuple;
import net.minecraft.world.entity.EntityType;
import xiroc.dungeoncrawl.util.IRandom;

import java.util.ArrayList;

public class WeightedRandomEntity implements IRandom<EntityType<?>> {

    public static final WeightedRandomEntity EMPTY = new WeightedRandomEntity(new Builder.Entry[0]);

    private final WeightedRandomEntity.WeightedEntry[] entries;

    private WeightedRandomEntity(Tuple<String, Integer>[] entries) {
        int weight = 0;
        for (Tuple<String, Integer> entry : entries)
            weight += entry.getB();
        this.entries = new WeightedRandomEntity.WeightedEntry[entries.length];
        this.assign(entries, weight);
    }

    private void assign(Tuple<String, Integer>[] entries, int totalWeight) {
        float f = 0.0F;
        int i = 0;
        for (Tuple<String, Integer> entry : entries) {
            float weight = (float) entry.getB() / (float) totalWeight;
            this.entries[i] = new WeightedRandomEntity.WeightedEntry(BuiltInRegistries.ENTITY_TYPE.get(ResourceLocation.parse(entry.getA())), weight + f);
            f += weight;
            i++;
        }
    }

    @Override
    public EntityType<?> roll(RandomSource rand) {
        float f = rand.nextFloat();
        for (WeightedRandomEntity.WeightedEntry entry : entries)
            if (entry.getB() >= f)
                return entry.getA();
        return null;
    }

    public static WeightedRandomEntity fromJson(JsonArray array) {
        if (array.size() == 0) {
            return EMPTY;
        }
        WeightedRandomEntity.Builder builder = new WeightedRandomEntity.Builder();
        array.forEach((element) -> {
            JsonObject object = element.getAsJsonObject();
            int weight = object.has("weight") ? object.get("weight").getAsInt() : 1;
            builder.add(object.get("entity").getAsString(), weight);
        });
        return builder.build();
    }

    private static class WeightedEntry extends Tuple<EntityType<?>, Float> {

        public WeightedEntry(EntityType<?> aIn, Float bIn) {
            super(aIn, bIn);
        }
    }

    public static class Builder {

        private final ArrayList<WeightedRandomEntity.Builder.Entry> list;

        public Builder() {
            list = new ArrayList<>();
        }

        public WeightedRandomEntity.Builder add(String item, int weight) {
            list.add(new WeightedRandomEntity.Builder.Entry(item, weight));
            return this;
        }

        public WeightedRandomEntity build() {
            if (list.isEmpty()) {
                return EMPTY;
            }
            return new WeightedRandomEntity(list.toArray(new WeightedRandomEntity.Builder.Entry[0]));
        }

        private static class Entry extends Tuple<String, Integer> {

            public Entry(String aIn, Integer bIn) {
                super(aIn, bIn);
            }
        }

    }

}
