package xiroc.dungeoncrawl.util.random;

import com.google.common.collect.ImmutableList;
import net.minecraft.util.RandomSource;

import java.util.List;
import java.util.function.BiConsumer;

public class ListWeightedRandom<T> implements IRandom<T> {
    private final ImmutableList<Entry<T>> entries;
    private final int totalWeight;

    public ListWeightedRandom(List<IRandom.Entry<T>> entries) {
        ImmutableList.Builder<Entry<T>> builder = ImmutableList.builder();
        int weight = 0;
        for (IRandom.Entry<T> entry : entries) {
            if (entry.weight() > 0) {
                weight += entry.weight();
                builder.add(new Entry<>(entry.value(), weight));
            }
        }
        this.entries = builder.build();
        this.totalWeight = weight;
    }

    @Override
    public T roll(RandomSource rand) {
        int r = rand.nextInt(totalWeight);
        for (Entry<T> entry : entries) {
            if (r < entry.threshold) {
                return entry.value;
            }
        }
        return null;
    }

    @Override
    public void forEach(BiConsumer<T, Integer> consumer) {
        int lastThreshold = 0;
        for (Entry<T> entry : entries) {
            consumer.accept(entry.value, entry.threshold - lastThreshold);
            lastThreshold = entry.threshold;
        }
    }

    @Override
    public void addTo(Builder<T> builder) {
        forEach(builder::add);
    }

    @Override
    public int totalWeight() {
        return totalWeight;
    }

    private record Entry<T>(T value, int threshold) {
    }
}
