package xiroc.dungeoncrawl.dungeon.type.level;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.Holder;
import xiroc.dungeoncrawl.dungeon.blueprint.Blueprint;
import xiroc.dungeoncrawl.util.random.IRandom;
import xiroc.dungeoncrawl.util.random.value.RandomValue;

public record SpecialRoom(IRandom<Holder<Blueprint>> variants, RandomValue minDepth, RandomValue amount) {
    public static final Codec<SpecialRoom> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Blueprint.RANDOM_HOLDER_CODEC.fieldOf("variants").forGetter(SpecialRoom::variants),
            RandomValue.CODEC.fieldOf("min_depth").forGetter(SpecialRoom::minDepth),
            RandomValue.CODEC.fieldOf("amount").forGetter(SpecialRoom::amount)
    ).apply(instance, SpecialRoom::new));
}
