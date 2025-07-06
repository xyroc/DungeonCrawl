package xiroc.dungeoncrawl.util.random;

import net.minecraft.util.RandomSource;
import xiroc.dungeoncrawl.util.collections.AVLTree;

import java.util.List;
import java.util.function.BiConsumer;

public class AVLTreeWeightedRandom<T> implements IRandom<T> {
    private final int totalWeight;
    private final AVLTree<T> entries;

    public AVLTreeWeightedRandom(List<IRandom.Entry<T>> entries) {
        this.entries = new AVLTree<>();
        int weight = 0;
        for (IRandom.Entry<T> entry : entries) {
            if (entry.weight() > 0) {
                weight += entry.weight();
                this.entries.insert(entry.value(), weight);
            }
        }
        this.totalWeight = weight;
    }

    @Override
    public T roll(RandomSource rand) {
        return this.entries.findSupremum(rand.nextInt(totalWeight));
    }

    @Override
    public int totalWeight() {
        return totalWeight;
    }

    @Override
    public void forEach(BiConsumer<T, Integer> consumer) {
        this.entries.traverseInOrder(new BiConsumer<>() {
            int totalWeight = 0;

            @Override
            public void accept(T t, Integer key) {
                final int weight = key - totalWeight;
                totalWeight += weight;
                consumer.accept(t, weight);
            }
        });
    }
}
