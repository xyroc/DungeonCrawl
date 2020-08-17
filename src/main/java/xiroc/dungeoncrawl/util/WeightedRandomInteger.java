package xiroc.dungeoncrawl.util;

/*
 * DungeonCrawl (C) 2019 - 2020 XYROC (XIROC1337), All Rights Reserved
 */

import com.google.common.collect.Lists;
import net.minecraft.util.Tuple;
import xiroc.dungeoncrawl.DungeonCrawl;

import java.util.Arrays;
import java.util.Collection;
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
        DungeonCrawl.LOGGER.debug("WeightedRandomIntger: {} entries", entries.length);
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
