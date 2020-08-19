package xiroc.dungeoncrawl.dungeon.monster;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import net.minecraft.potion.Effect;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.storage.loot.RandomValueRange;
import net.minecraftforge.registries.ForgeRegistries;
import xiroc.dungeoncrawl.util.IRandom;

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
            this.entries[i] = new WeightedRandomPotionEffect.WeightedEntry(ForgeRegistries.POTIONS.getValue(new ResourceLocation(entry.effect)), entry.duration, entry.level, weight + f);
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
            RandomValueRange level = object.has("level") ?
                    new RandomValueRange(object.getAsJsonObject("level").get("min").getAsInt(), object.getAsJsonObject("level").get("max").getAsInt()) :
                    new RandomValueRange(1);
            builder.add(object.get("effect").getAsString(), duration, level, weight);
        });
        return builder.build();
    }

    public static class WeightedEntry {

        public final Effect effect;
        public final int duration;
        public final RandomValueRange level;
        public final float weight;

        public WeightedEntry(Effect effect, int duration, RandomValueRange level, Float weight) {
            this.effect = effect;
            this.duration = duration;
            this.level = level;
            this.weight = weight;
        }

    }

    public static class Builder {

        private final ArrayList<WeightedRandomPotionEffect.Builder.Entry> list;

        public Builder() {
            list = new ArrayList<>();
        }

        public WeightedRandomPotionEffect.Builder add(String effect, int duration, RandomValueRange level, int weight) {
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
            public final RandomValueRange level;
            public final int weight;

            public Entry(String effect, int duration, RandomValueRange level, Integer weight) {
                this.effect = effect;
                this.duration = duration;
                this.level = level;
                this.weight = weight;
            }
        }
    }
}