package xiroc.dungeoncrawl.util.random.value;

import com.mojang.serialization.Codec;
import net.minecraft.util.RandomSource;

public record Constant(int value) implements RandomValue {
    public static final Codec<Constant> CODEC = Codec.INT.xmap(Constant::new, Constant::value);

    @Override
    public int nextInt(RandomSource random) {
        return value;
    }

    @Override
    public boolean isAlwaysWithin(int lowerBound, int upperBound) {
        return value >= lowerBound && value <= upperBound;
    }
}
