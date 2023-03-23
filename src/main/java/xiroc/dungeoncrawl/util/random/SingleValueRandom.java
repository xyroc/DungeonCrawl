package xiroc.dungeoncrawl.util.random;

import java.util.Random;

public record SingleValueRandom<T>(T value) implements IRandom<T> {
    @Override
    public T roll(Random rand) {
        return value;
    }
}
