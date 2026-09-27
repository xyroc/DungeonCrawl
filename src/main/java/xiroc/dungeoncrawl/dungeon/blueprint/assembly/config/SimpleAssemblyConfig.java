package xiroc.dungeoncrawl.dungeon.blueprint.assembly.config;

import com.google.common.collect.ImmutableMap;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.mojang.serialization.codecs.UnboundedMapCodec;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.Nullable;
import xiroc.dungeoncrawl.dungeon.blueprint.BlueprintConfiguration;
import xiroc.dungeoncrawl.dungeon.blueprint.Entrance;
import xiroc.dungeoncrawl.dungeon.blueprint.EntranceConfiguration;
import xiroc.dungeoncrawl.dungeon.blueprint.anchor.BuiltinAnchorTypes;
import xiroc.dungeoncrawl.dungeon.blueprint.assembly.type.AssemblyTypeBuilder;
import xiroc.dungeoncrawl.dungeon.blueprint.assembly.type.SimpleAssembly;
import xiroc.dungeoncrawl.util.StorageHelper;

import java.util.Map;
import java.util.Objects;
import java.util.Optional;

public record SimpleAssemblyConfig(Map<ResourceLocation, EntranceConfiguration> entranceTypes) implements AssemblyConfig {
    private static final ImmutableMap<ResourceLocation, EntranceConfiguration> DEFAULT_ENTRANCE_TYPES = ImmutableMap.of(
            BuiltinAnchorTypes.ENTRANCE, new EntranceConfiguration(Entrance.Decoration.PRIMARY, null, BlueprintConfiguration.EntranceType.NORMAL)
    );

    private static final Codec<Map<ResourceLocation, EntranceConfiguration>> ENTRANCE_TYPE_MAP_CODEC = new UnboundedMapCodec<>(ResourceLocation.CODEC, EntranceConfiguration.CODEC);

    public static final MapCodec<SimpleAssemblyConfig> MAP_CODEC = RecordCodecBuilder.mapCodec(builder -> builder.group(
            ENTRANCE_TYPE_MAP_CODEC.optionalFieldOf("entrance_types").forGetter(configuration -> {
                final var nonDefaultTypes = StorageHelper.mapDifference(configuration.entranceTypes, DEFAULT_ENTRANCE_TYPES);
                return nonDefaultTypes.isEmpty() ? Optional.empty() : Optional.of(nonDefaultTypes);
            })
    ).apply(builder, (entranceTypes) -> new SimpleAssemblyConfig(entranceTypes.orElse(DEFAULT_ENTRANCE_TYPES))));

    public static final SimpleAssemblyConfig DEFAULT = new SimpleAssemblyConfig(DEFAULT_ENTRANCE_TYPES);

    @Override
    public AssemblyTypeBuilder makeTypeBuilder() {
        return new SimpleAssembly.Builder(this);
    }

    @Override
    public MapCodec<? extends AssemblyConfig> type() {
        return MAP_CODEC;
    }

    // Equals (and hashcode) methods since an instance of this class is used as a default value in the BlueprintConfiguration codec
    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        SimpleAssemblyConfig that = (SimpleAssemblyConfig) o;
        return Objects.equals(entranceTypes, that.entranceTypes);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(entranceTypes);
    }

    public static class Builder {
        @Nullable
        private ImmutableMap.Builder<ResourceLocation, EntranceConfiguration> entranceTypes = null;

        public Builder entranceType(ResourceLocation anchorType, @Nullable Entrance.Decoration decoration, @Nullable Entrance.CustomParts customParts) {
            return entranceType(anchorType, BlueprintConfiguration.EntranceType.NORMAL, decoration, customParts);
        }

        public Builder entranceType(ResourceLocation anchorType, BlueprintConfiguration.EntranceType type, @Nullable Entrance.Decoration decoration, @Nullable Entrance.CustomParts customParts) {
            return entranceType(anchorType, new EntranceConfiguration(decoration, customParts, type));
        }

        private Builder entranceType(ResourceLocation anchorType, EntranceConfiguration type) {
            if (entranceTypes == null) {
                entranceTypes = ImmutableMap.builder();
            }
            entranceTypes.put(anchorType, type);
            return this;
        }

        public SimpleAssemblyConfig build() {
            var entranceTypes = this.entranceTypes == null ? DEFAULT_ENTRANCE_TYPES : this.entranceTypes.build();
            return new SimpleAssemblyConfig(entranceTypes);
        }
    }
}
