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
import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.EntityType;
import xiroc.dungeoncrawl.util.IRandom;
import xiroc.dungeoncrawl.util.Pair;

import java.util.ArrayList;
import java.util.List;

public class WeightedRandomEntity implements IRandom<EntityType<?>> {

    public static final WeightedRandomEntity EMPTY = new WeightedRandomEntity(List.of());

    private final List<Pair<EntityType<?>, Float>> entries;

    private WeightedRandomEntity(List<Pair<String, Integer>> entries) {
        int weight = 0;
        for (Pair<String, Integer> entry : entries)
            weight += entry.right();
        this.entries = new ArrayList<>(entries.size());
        this.assign(entries, weight);
    }

    private void assign(List<Pair<String, Integer>> entries, int totalWeight) {
        float f = 0.0F;
        for (Pair<String, Integer> entry : entries) {
            float weight = (float) entry.right() / (float) totalWeight;
            this.entries.add( new Pair<>(BuiltInRegistries.ENTITY_TYPE.get(Identifier.parse(entry.left()))
                    .map(Holder::value)
                    .orElseThrow(), weight + f));
            f += weight;
        }
    }

    @Override
    public EntityType<?> roll(RandomSource rand) {
        float f = rand.nextFloat();
        for (Pair<EntityType<?>, Float> entry : entries)
            if (entry.right() >= f)
                return entry.left();
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

    public static class Builder {

        private final ArrayList<Pair<String, Integer>> list;

        public Builder() {
            list = new ArrayList<>();
        }

        public WeightedRandomEntity.Builder add(String item, int weight) {
            list.add(new Pair<>(item, weight));
            return this;
        }

        public WeightedRandomEntity build() {
            if (list.isEmpty()) {
                return EMPTY;
            }
            return new WeightedRandomEntity(list);
        }

    }

}
