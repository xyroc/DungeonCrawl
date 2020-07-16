package xiroc.dungeoncrawl.dungeon.treasure;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import net.minecraft.util.Tuple;
import xiroc.dungeoncrawl.util.IRandom;

import java.util.ArrayList;
import java.util.Random;

public class WeightedRandomTreasureItem implements IRandom<TreasureItem> {

    private final WeightedEntry[] entries;

    private WeightedRandomTreasureItem(Tuple<String, Integer>[] entries) {
        int weight = 0;
        for (Tuple<String, Integer> entry : entries)
            weight += entry.getB();
        this.entries = new WeightedEntry[entries.length];
        this.assign(entries, weight);
    }

    private void assign(Tuple<String, Integer>[] entries, int totalWeight) {
        float f = 0.0F;
        int i = 0;
        for (Tuple<String, Integer> entry : entries) {
            float weight = (float) entry.getB() / (float) totalWeight;
            this.entries[i] = new WeightedEntry(RandomSpecialItem.createEnchantedSpecialItem(entry.getA()), weight + f);
            f += weight;
            i++;
        }
    }

    @Override
    public TreasureItem roll(Random rand) {
        float f = rand.nextFloat();
        for (WeightedEntry entry : entries)
            if (entry.getB() >= f)
                return entry.getA();
        return null;
    }

    public static WeightedRandomTreasureItem loadFromJSON(JsonArray array) {
        Builder builder = new Builder();
        array.forEach((element) -> {
            JsonObject object = element.getAsJsonObject();
            builder.add(object.get("item").getAsString(), object.get("weight").getAsInt());
        });
        return builder.build();
    }

    private static class WeightedEntry extends Tuple<TreasureItem, Float> {

        public WeightedEntry(TreasureItem aIn, Float bIn) {
            super(aIn, bIn);
        }
    }

    public static class Builder {

        private final ArrayList<Entry> list;

        public Builder() {
            list = new ArrayList<>();
        }

        public Builder add(String item, int weight) {
            list.add(new Entry(item, weight));
            return this;
        }

        public WeightedRandomTreasureItem build() {
            return new WeightedRandomTreasureItem(list.toArray(new Entry[0]));
        }

        private static class Entry extends Tuple<String, Integer> {

            public Entry(String aIn, Integer bIn) {
                super(aIn, bIn);
            }
        }

    }
}
