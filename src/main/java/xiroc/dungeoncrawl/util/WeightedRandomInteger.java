package xiroc.dungeoncrawl.util;

import java.util.Collection;
import java.util.List;

/*
 * DungeonCrawl (C) 2019 - 2020 XYROC (XIROC1337), All Rights Reserved 
 */

import java.util.Random;

import com.google.common.collect.Lists;

import net.minecraft.util.Tuple;
import xiroc.dungeoncrawl.DungeonCrawl;
import xiroc.dungeoncrawl.util.WeightedRandomInteger.Builder.IntegerEntry;

public class WeightedRandomInteger implements IRandom<Integer> {

	public static final Random RANDOM = new Random();

	private int totalWeight;
	public WeightedInteger[] integers;

	public WeightedRandomInteger(IntegerEntry[] entries) {
		int weight = 0;
		for (IntegerEntry entry : entries)
			weight += entry.getA();
		this.totalWeight = weight;
		this.integers = new WeightedInteger[entries.length];
		this.assign(entries);
		DungeonCrawl.LOGGER.info("WeightedRandomIntger: {} entries", entries.length);
	}

	private void assign(IntegerEntry[] values) {
		float f = 0.0F;
		int i = 0;
		for (IntegerEntry entry : values) {
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
		DungeonCrawl.LOGGER.debug("weightedrandominteger returning null");
		return null;
	}

	public static class WeightedInteger extends Tuple<Float, Integer> {

		public WeightedInteger(Float aIn, Integer bIn) {
			super(aIn, bIn);
		}

	}

	public static class Builder {

		public List<IntegerEntry> entries;
		
		public Builder() {
			entries = Lists.newArrayList();
		}

		public WeightedRandomInteger.Builder add(IntegerEntry[] entries) {
			for (IntegerEntry entry : entries) {
				this.entries.add(entry);
			}
			return this;
		}
		
		public WeightedRandomInteger.Builder addAll(Collection<IntegerEntry> entries) {
			for (IntegerEntry entry : entries) {
				this.entries.add(entry);
			}
			return this;
		}
		
		public WeightedRandomInteger build() {
			return new WeightedRandomInteger(entries.toArray(new IntegerEntry[entries.size()]));
		}
		
		public static class IntegerEntry extends Tuple<Integer, Integer>{

			public IntegerEntry(Integer aIn, Integer bIn) {
				super(aIn, bIn);
			}
			
		}

	}

}
