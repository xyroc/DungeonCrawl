package xiroc.dungeoncrawl.dungeon.monster.attribute;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import xiroc.dungeoncrawl.util.random.distribution.UniformDistribution;

public record RandomizedAttributeModifier(ResourceLocation identifier,
                                          UniformDistribution amount,
                                          AttributeModifier.Operation operation) {
    public static final MapCodec<RandomizedAttributeModifier> MAP_CODEC = RecordCodecBuilder.mapCodec(builder ->
            builder.group(
                    ResourceLocation.CODEC.fieldOf("id").forGetter(RandomizedAttributeModifier::identifier),
                    UniformDistribution.MAP_CODEC.fieldOf("amount").forGetter(RandomizedAttributeModifier::amount),
                    AttributeModifier.Operation.CODEC.fieldOf("operation").forGetter(RandomizedAttributeModifier::operation)
            ).apply(builder, RandomizedAttributeModifier::new));

    public static final Codec<RandomizedAttributeModifier> CODEC = MAP_CODEC.codec();

    /**
     * Instantiate a random attribute modifier.
     *
     * @param randomSource The source of randomness.
     * @return The resulting attribute modifier.
     */
    public AttributeModifier createRandomModifier(RandomSource randomSource) {
        return new AttributeModifier(identifier, amount.nextFloat(randomSource), operation);
    }
}
