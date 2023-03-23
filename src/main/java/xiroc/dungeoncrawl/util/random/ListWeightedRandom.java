package xiroc.dungeoncrawl.util.random;

import com.google.common.collect.ImmutableList;
import net.minecraft.util.Tuple;

import java.util.List;
import java.util.Random;
import java.util.function.BiConsumer;

public class ListWeightedRandom<T> implements WeightedRandom<T> {
    private final ImmutableList<Entry<T>> entries;
    private final int totalWeight;

    public ListWeightedRandom(List<Tuple<T, Integer>> entries) {
        ImmutableList.Builder<Entry<T>> builder = ImmutableList.builder();
        int weight = 0;
        for (Tuple<T, Integer> entry : entries) {
            if (entry.getB() > 0) {
                weight += entry.getB();
                builder.add(new Entry<>(entry.getA(), weight));
            }
        }
        this.entries = builder.build();
        this.totalWeight = weight;
    }

    @Override
    public T roll(Random rand) {
        int r = rand.nextInt(totalWeight);
        for (Entry<T> entry : entries) {
            if (r < entry.threshold) {
                return entry.value;
            }
        }
        return null;
    }

    @Override
    public boolean isEmpty() {
        return entries.isEmpty();
    }

    @Override
    public void forEach(BiConsumer<T, Integer> consumer) {
        int lastThreshold = 0;
        for (Entry<T> entry : entries) {
            consumer.accept(entry.value, entry.threshold - lastThreshold);
            lastThreshold = entry.threshold;
        }
    }

    private record Entry<T>(T value, int threshold) {
    }
}
