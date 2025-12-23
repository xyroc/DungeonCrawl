package xiroc.dungeoncrawl.util.random;

import com.google.common.base.Suppliers;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderSet;
import net.minecraft.util.RandomSource;
import org.jetbrains.annotations.Nullable;

import java.util.function.BiConsumer;
import java.util.function.Supplier;

/**
 * Wraps a set of {@code IRandom<T>} instances and interprets it as a single instance of {@code IRandom<T>}
 * which is logically equivalent to combining all instances into one via {@code IRandom.Builder<T>}.
 */
public class PooledRandom<T> implements IRandom<T> {
    @Nullable
    private final IRandom<T> base;
    private final HolderSet<IRandom<T>> pools;
    private final Supplier<IRandom<IRandom<T>>> weightedPools;

    public PooledRandom(HolderSet<IRandom<T>> pools) {
        this(null, pools);
    }

    public PooledRandom(@Nullable IRandom<T> base, HolderSet<IRandom<T>> pools) {
        this.base = base;
        this.pools = pools;
        this.weightedPools = Suppliers.memoize(() -> {
            IRandom.Builder<IRandom<T>> builder = new IRandom.Builder<>();
            if (this.base != null) {
                builder.add(base, base.totalWeight());
            }
            for (Holder<IRandom<T>> pool : this.pools) {
                IRandom<T> actualPool = pool.value();
                builder.add(actualPool, actualPool.totalWeight());
            }
            return builder.build();
        });
    }

    @Override
    public T roll(RandomSource rand) {
        return weightedPools.get().roll(rand).roll(rand);
    }

    @Override
    public void addTo(Builder<T> builder) {
        builder.setPools(pools);
        if (base != null) {
            base.addTo(builder);
        }
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
