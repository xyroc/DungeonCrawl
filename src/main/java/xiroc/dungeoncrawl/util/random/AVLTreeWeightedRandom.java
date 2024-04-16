package xiroc.dungeoncrawl.util.random;

import net.minecraft.util.Tuple;
import xiroc.dungeoncrawl.util.collections.AVLTree;

import java.util.List;
import java.util.Random;
import java.util.function.BiConsumer;

public class AVLTreeWeightedRandom<T> implements WeightedRandom<T> {
    private final int totalWeight;
    private final AVLTree<T> entries;

    public AVLTreeWeightedRandom(List<Tuple<T, Integer>> entries) {
        this.entries = new AVLTree<>();
        int weight = 0;
        for (Tuple<T, Integer> entry : entries) {
            if (entry.getB() > 0) {
                weight += entry.getB();
                this.entries.insert(entry.getA(), weight);
            }
        }
        this.totalWeight = weight;
    }

    @Override
    public T roll(Random rand) {
        return this.entries.findSupremum(rand.nextInt(totalWeight));
    }

    @Override
    public void forEach(BiConsumer<T, Integer> consumer) {
        this.entries.traverseRecursively(consumer);
    }
}
