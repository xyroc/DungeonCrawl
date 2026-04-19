package xiroc.dungeoncrawl.util.random.distribution;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;

import java.util.Optional;

public record UniformDistribution(float min, float max, boolean round) implements NumberDistribution {
    public static final MapCodec<UniformDistribution> MAP_CODEC = RecordCodecBuilder.mapCodec(builder -> builder.group(
            Codec.FLOAT.fieldOf("min").forGetter(UniformDistribution::min),
            Codec.FLOAT.fieldOf("max").forGetter(UniformDistribution::max),
            Codec.BOOL.optionalFieldOf("round").forGetter(in -> in.round ? Optional.of(true) : Optional.empty())
    ).apply(builder, (min, max, round) -> new UniformDistribution(
            min, max, round.orElse(false)
    )));

    @Override
    public float nextFloat(RandomSource random) {
        float number = Mth.nextFloat(random, min, max);
        if (round) {
            number = Math.round(number);
        }
        return number;
    }
}
