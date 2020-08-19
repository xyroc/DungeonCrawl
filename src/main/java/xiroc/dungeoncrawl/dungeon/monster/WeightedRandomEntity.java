package xiroc.dungeoncrawl.dungeon.monster;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import net.minecraft.entity.EntityType;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Tuple;
import net.minecraftforge.registries.ForgeRegistries;
import xiroc.dungeoncrawl.util.IRandom;

import java.util.ArrayList;
import java.util.Random;

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
            this.entries[i] = new WeightedRandomEntity.WeightedEntry(ForgeRegistries.ENTITIES.getValue(new ResourceLocation(entry.getA())), weight + f);
            f += weight;
            i++;
        }
    }

    @Override
    public EntityType<?> roll(Random rand) {
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
