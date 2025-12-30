package xiroc.dungeoncrawl.dungeon.blueprint.feature.settings;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.Holder;
import xiroc.dungeoncrawl.dungeon.generator.level.LevelGenerator;
import xiroc.dungeoncrawl.dungeon.monster.SpawnerType;
import xiroc.dungeoncrawl.util.random.IRandom;

import java.util.Optional;

public record SpawnerSettings(Optional<IRandom<Holder<SpawnerType>>> types) {
    public static final Codec<SpawnerSettings> CODEC = RecordCodecBuilder.create(instance -> instance
            .group(
                    SpawnerType.RANDOM_HOLDER_CODEC.optionalFieldOf("type").forGetter(SpawnerSettings::types)
            ).apply(instance, SpawnerSettings::new));

    public IRandom<Holder<SpawnerType>> getSpawnerTypes(LevelGenerator levelGenerator) {
        return types.orElse(levelGenerator.levelType.spawners());
    }
}
