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
import net.minecraft.util.Tuple;

import java.util.Arrays;
import java.util.List;
import java.util.Random;

public class WeightedRandomInteger implements IRandom<Integer> {

    public static final Random RANDOM = new Random();

    private int totalWeight;
    public WeightedInteger[] integers;

    public WeightedRandomInteger(WeightedIntegerEntry[] entries) {
        int weight = 0;
        for (WeightedIntegerEntry entry : entries)
            weight += entry.getA();
        this.totalWeight = weight;
        this.integers = new WeightedInteger[entries.length];
        this.assign(entries);
    }

    private void assign(WeightedIntegerEntry[] values) {
        float f = 0.0F;
        int i = 0;
        for (WeightedIntegerEntry entry : values) {
            float weight = (float) entry.getA() / (float) totalWeight;
            integers[i] = new WeightedInteger(weight + f, entry.getB());
            f += weight;
            i++;
        }
    }

    @Override
    public Integer roll(Random rand) {
        float f = rand.nextFloat();
        for (WeightedInteger entry : integers)
            if (entry.getA() >= f)
                return entry.getB();
        return null;
    }

    public static class WeightedInteger extends Tuple<Float, Integer> {

        public WeightedInteger(Float aIn, Integer bIn) {
            super(aIn, bIn);
        }

    }

    public static class Builder {

        public List<WeightedIntegerEntry> entries;

        public Builder() {
            entries = Lists.newArrayList();
        }

        public WeightedRandomInteger.Builder add(WeightedIntegerEntry[] entries) {
            this.entries.addAll(Arrays.asList(entries));
            return this;
        }

        public WeightedRandomInteger build() {
            return new WeightedRandomInteger(entries.toArray(new WeightedIntegerEntry[0]));
        }

    }

}
