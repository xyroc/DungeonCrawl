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
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.effect.MobEffect;
import net.minecraftforge.registries.ForgeRegistries;
import xiroc.dungeoncrawl.util.IRandom;
import xiroc.dungeoncrawl.util.Range;

import java.util.ArrayList;
import java.util.Random;

public class WeightedRandomPotionEffect implements IRandom<WeightedRandomPotionEffect.WeightedEntry> {

    public static final WeightedRandomPotionEffect EMPTY = new WeightedRandomPotionEffect(new Builder.Entry[0]);

    private final WeightedRandomPotionEffect.WeightedEntry[] entries;

    private WeightedRandomPotionEffect(Builder.Entry[] entries) {
        int weight = 0;
        for (Builder.Entry entry : entries)
            weight += entry.weight;
        this.entries = new WeightedRandomPotionEffect.WeightedEntry[entries.length];
        this.assign(entries, weight);
    }

    private void assign(Builder.Entry[] entries, int totalWeight) {
        float f = 0.0F;
        int i = 0;
        for (Builder.Entry entry : entries) {
            float weight = (float) entry.weight / (float) totalWeight;
            this.entries[i] = new WeightedRandomPotionEffect.WeightedEntry(ForgeRegistries.POTIONS.getValue(new ResourceLocation(entry.effect)), entry.duration, entry.amplifier, weight + f);
            f += weight;
            i++;
        }
    }

    @Override
    public WeightedEntry roll(Random rand) {
        float f = rand.nextFloat();
        for (WeightedRandomPotionEffect.WeightedEntry entry : entries)
            if (entry.weight >= f)
                return entry;
        return null;
    }

    public static WeightedRandomPotionEffect fromJson(JsonArray array) {
        if (array.size() == 0) {
            return EMPTY;
        }
        WeightedRandomPotionEffect.Builder builder = new WeightedRandomPotionEffect.Builder();
        array.forEach((element) -> {
            JsonObject object = element.getAsJsonObject();
            int weight = object.has("weight") ? object.get("weight").getAsInt() : 1;
            int duration = object.get("duration").getAsInt();
            Range level = object.has("amplifier") ?
                    new Range(object.getAsJsonObject("amplifier").get("min").getAsInt(), object.getAsJsonObject("amplifier").get("max").getAsInt()) :
                    new Range(0, 0);
            builder.add(object.get("effect").getAsString(), duration, level, weight);
        });
        return builder.build();
    }

    public record WeightedEntry(MobEffect effect, int duration,
                                Range amplifier, float weight) {

    }

    public static class Builder {

        private final ArrayList<WeightedRandomPotionEffect.Builder.Entry> list;

        public Builder() {
            list = new ArrayList<>();
        }

        public WeightedRandomPotionEffect.Builder add(String effect, int duration, Range level, int weight) {
            list.add(new WeightedRandomPotionEffect.Builder.Entry(effect, duration, level, weight));
            return this;
        }

        public WeightedRandomPotionEffect build() {
            if (list.isEmpty())
                return EMPTY;
            return new WeightedRandomPotionEffect(list.toArray(new WeightedRandomPotionEffect.Builder.Entry[0]));
        }

        private static class Entry {

            public final String effect;
            public final int duration;
            public final Range amplifier;
            public final int weight;

            public Entry(String effect, int duration, Range amplifier, Integer weight) {
                this.effect = effect;
                this.duration = duration;
                this.amplifier = amplifier;
                this.weight = weight;
            }
        }
    }
}