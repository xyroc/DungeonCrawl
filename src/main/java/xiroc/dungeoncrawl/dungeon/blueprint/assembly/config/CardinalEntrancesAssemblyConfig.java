package xiroc.dungeoncrawl.dungeon.blueprint.assembly.config;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.Holder;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.Nullable;
import xiroc.dungeoncrawl.dungeon.blueprint.Blueprint;
import xiroc.dungeoncrawl.dungeon.blueprint.EntranceConfiguration;
import xiroc.dungeoncrawl.dungeon.blueprint.assembly.type.AssemblyTypeBuilder;
import xiroc.dungeoncrawl.dungeon.blueprint.assembly.type.CardinalEntrancesAssembly;
import xiroc.dungeoncrawl.util.random.IRandom;

import java.util.Objects;

public record CardinalEntrancesAssemblyConfig(EntranceConfiguration entranceConfiguration,
                                              int entranceHeight,
                                              ResourceLocation anchorType,
                                              IRandom<Holder<Blueprint>> cornerPieces,
                                              IRandom<Holder<Blueprint>> singlePieces) implements AssemblyConfig {

    private static final int DEFAULT_ENTRANCE_HEIGHT = 0;

    public static final MapCodec<CardinalEntrancesAssemblyConfig> MAP_CODEC = RecordCodecBuilder.mapCodec(builder -> builder.group(
            EntranceConfiguration.CODEC.fieldOf("entrance").forGetter(CardinalEntrancesAssemblyConfig::entranceConfiguration),
            Codec.INT.optionalFieldOf("entrance_height", DEFAULT_ENTRANCE_HEIGHT).forGetter(CardinalEntrancesAssemblyConfig::entranceHeight),
            ResourceLocation.CODEC.fieldOf("anchor_type").forGetter(CardinalEntrancesAssemblyConfig::anchorType),
            Blueprint.RANDOM_HOLDER_CODEC.fieldOf("corner_pieces").forGetter(CardinalEntrancesAssemblyConfig::cornerPieces),
            Blueprint.RANDOM_HOLDER_CODEC.fieldOf("single_pieces").forGetter(CardinalEntrancesAssemblyConfig::singlePieces)
    ).apply(builder, CardinalEntrancesAssemblyConfig::new));

    @Override
    public AssemblyTypeBuilder makeTypeBuilder() {
        return new CardinalEntrancesAssembly.Builder(this);
    }

    @Override
    public MapCodec<? extends AssemblyConfig> type() {
        return MAP_CODEC;
    }

    public static class Builder {
        @Nullable
        private EntranceConfiguration entranceConfiguration;

        private int entranceHeight = DEFAULT_ENTRANCE_HEIGHT;
        @Nullable
        private ResourceLocation anchorType;
        @Nullable
        private IRandom<Holder<Blueprint>> cornerPieces;
        @Nullable
        private IRandom<Holder<Blueprint>> singlePieces;

        public Builder entranceConfiguration(EntranceConfiguration entranceConfiguration) {
            this.entranceConfiguration = entranceConfiguration;
            return this;
        }

        public Builder entranceHeight(int entranceHeight) {
            this.entranceHeight = entranceHeight;
            return this;
        }

        public Builder anchorType(ResourceLocation anchorType) {
            this.anchorType = anchorType;
            return this;
        }

        public Builder cornerPieces(IRandom<Holder<Blueprint>> cornerPieces) {
            this.cornerPieces = cornerPieces;
            return this;
        }

        public Builder singlePieces(IRandom<Holder<Blueprint>> singlePieces) {
            this.singlePieces = singlePieces;
            return this;
        }

        public CardinalEntrancesAssemblyConfig build() {
            Objects.requireNonNull(entranceConfiguration, "Missing entrance configuration");
            Objects.requireNonNull(anchorType, "Missing anchor type");
            Objects.requireNonNull(cornerPieces, "Missing corner pieces");
            Objects.requireNonNull(singlePieces, "Missing single pieces");
            return new CardinalEntrancesAssemblyConfig(entranceConfiguration, entranceHeight, anchorType, cornerPieces, singlePieces);
        }
    }
}
