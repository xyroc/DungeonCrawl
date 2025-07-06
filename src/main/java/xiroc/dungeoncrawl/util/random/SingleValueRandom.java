package xiroc.dungeoncrawl.util.random;

import net.minecraft.util.RandomSource;

import java.util.function.BiConsumer;

public record SingleValueRandom<T>(T value) implements IRandom<T> {
    @Override
    public T roll(RandomSource rand) {
        return value;
    }

    @Override
    public int totalWeight() {
        return 1;
    }

    @Override
    public void forEach(BiConsumer<T, Integer> consumer) {
        consumer.accept(value, 1);
    }
}
