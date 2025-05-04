package xiroc.dungeoncrawl.util.random;

import com.google.common.base.Suppliers;
import xiroc.dungeoncrawl.datapack.registry.Delegate;

import java.util.List;
import java.util.Random;
import java.util.function.BiConsumer;
import java.util.function.Supplier;

/**
 * Wraps a list of {@code IRandom<T>} instances, referred to via delegate, and interprets it as a single
 *  instance of {@code IRandom<T>} which is logically equivalent to combining all instances into one
 *  via {@code IRandom.Builder<T>}.
 */
public class PooledRandom<T> implements IRandom<T> {
    private final Supplier<IRandom<IRandom<T>>> weightedPools;

    public PooledRandom(List<Delegate<IRandom<T>>> rawPools) {
        if (rawPools.isEmpty()) {
            throw new IllegalArgumentException("The list of pools must not be empty.");
        }
        this.weightedPools = Suppliers.memoize(() -> {
            IRandom.Builder<IRandom<T>> builder = new IRandom.Builder<>();
            for (Delegate<IRandom<T>> pool : rawPools) {
                IRandom<T> actualPool = pool.get();
                builder.add(actualPool, actualPool.totalWeight());
            }
            return builder.build();
        });
    }

    @Override
    public T roll(Random rand) {
        return weightedPools.get().roll(rand).roll(rand);
    }

    @Override
    public int totalWeight() {
        return weightedPools.get().totalWeight();
    }

    @Override
    public void forEach(BiConsumer<T, Integer> consumer) {
        weightedPools.get().forEach((pool, weight) -> pool.forEach(consumer));
    }
}
