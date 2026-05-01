package xiroc.dungeoncrawl.dungeon.blueprint.feature.settings;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.Holder;
import xiroc.dungeoncrawl.dungeon.generator.level.LevelGenerator;
import xiroc.dungeoncrawl.dungeon.monster.SpawnerType;
import xiroc.dungeoncrawl.dungeon.tier.TieredResource;
import xiroc.dungeoncrawl.util.random.IRandom;

import java.util.Optional;

public record SpawnerSettings(Optional<TieredResource<IRandom<Holder<SpawnerType>>>> types) {
    public static final Codec<SpawnerSettings> CODEC = RecordCodecBuilder.create(instance -> instance
            .group(
                    TieredResource.Codecs.makeCodec(SpawnerType.RANDOM_HOLDER_CODEC).optionalFieldOf("type").forGetter(SpawnerSettings::types)
            ).apply(instance, SpawnerSettings::new));

    public IRandom<Holder<SpawnerType>> getSpawnerTypes(LevelGenerator levelGenerator) {
        if (types.isEmpty()) {
            return levelGenerator.levelType.spawners();
        }
        return types.get().forTier(levelGenerator.stage);
    }
}
