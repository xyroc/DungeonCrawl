package xiroc.dungeoncrawl.dungeon.blueprint;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import org.jetbrains.annotations.Nullable;
import xiroc.dungeoncrawl.dungeon.blueprint.anchor.Anchor;

import java.util.Optional;

public record EntranceConfiguration(@Nullable Entrance.Decoration decoration,
                             @Nullable Entrance.CustomParts customParts,
                             BlueprintConfiguration.EntranceType type) {
    public static final Codec<EntranceConfiguration> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Entrance.Decoration.BY_NAME_CODEC.optionalFieldOf("decoration").forGetter(entrance -> Optional.ofNullable(entrance.decoration)),
            Entrance.CustomParts.CODEC.optionalFieldOf("custom_parts").forGetter(entrance -> Optional.ofNullable(entrance.customParts)),
            BlueprintConfiguration.EntranceType.CODEC.optionalFieldOf("type", BlueprintConfiguration.EntranceType.NORMAL).forGetter(EntranceConfiguration::type)
    ).apply(instance, (decoration, customParts, type) ->
            new EntranceConfiguration(decoration.orElse(null), customParts.orElse(null), type)));

    public Entrance make(Anchor placement) {
        return new Entrance(placement, decoration, customParts);
    }
}
