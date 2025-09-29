package xiroc.dungeoncrawl.dungeon.theme;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderSet;
import net.minecraft.core.RegistryCodecs;
import net.minecraft.resources.RegistryFileCodec;
import net.minecraft.world.level.biome.Biome;
import xiroc.dungeoncrawl.datapack.registry.DatapackRegistries;
import xiroc.dungeoncrawl.dungeon.block.provider.BlockStateProvider;
import xiroc.dungeoncrawl.dungeon.block.provider.SingleBlock;
import xiroc.dungeoncrawl.dungeon.decoration.DungeonDecoration;
import xiroc.dungeoncrawl.util.random.IRandom;
import xiroc.dungeoncrawl.util.random.RandomMapping;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

public record PrimaryTheme(BlockStateProvider masonry,
                           BlockStateProvider pillar,
                           BlockStateProvider floor,
                           BlockStateProvider fluid,
                           BlockStateProvider fencing,
                           BlockStateProvider stairs,
                           BlockStateProvider slab,
                           BlockStateProvider wall,
                           List<DungeonDecoration> decorations) {

    public static final Codec<PrimaryTheme> DIRECT_CODEC = RecordCodecBuilder.create(instance -> instance.group(
            BlockStateProvider.CODEC.fieldOf("masonry").forGetter(PrimaryTheme::masonry),
            BlockStateProvider.CODEC.fieldOf("pillar").forGetter(PrimaryTheme::pillar),
            BlockStateProvider.CODEC.fieldOf("floor").forGetter(PrimaryTheme::floor),
            BlockStateProvider.CODEC.fieldOf("fluid").forGetter(PrimaryTheme::fluid),
            BlockStateProvider.CODEC.fieldOf("fencing").forGetter(PrimaryTheme::fencing),
            BlockStateProvider.CODEC.fieldOf("stairs").forGetter(PrimaryTheme::stairs),
            BlockStateProvider.CODEC.fieldOf("slab").forGetter(PrimaryTheme::slab),
            BlockStateProvider.CODEC.fieldOf("wall").forGetter(PrimaryTheme::wall),
            DungeonDecoration.CODEC.listOf().optionalFieldOf("decorations").forGetter(primaryTheme ->
                    primaryTheme.decorations.isEmpty() ? Optional.empty() : Optional.of(primaryTheme.decorations))
    ).apply(instance, (masonry,
                       pillar,
                       floor,
                       fluid,
                       fencing,
                       stairs,
                       slab,
                       wall,
                       decorations) ->
            new PrimaryTheme(
                    masonry,
                    pillar,
                    floor,
                    fluid,
                    fencing,
                    stairs,
                    slab,
                    wall,
                    decorations.orElse(List.of())
            )));

    public static final Codec<Holder<PrimaryTheme>> HOLDER_CODEC = RegistryFileCodec.create(DatapackRegistries.PRIMARY_THEME, DIRECT_CODEC, false);
    public static final Codec<IRandom<Holder<PrimaryTheme>>> RANDOM_HOLDER_CODEC = IRandom.makeCodec(IRandom.makeBuilderCodec(HOLDER_CODEC, "theme", null));
    public static final Codec<HolderSet<IRandom<Holder<PrimaryTheme>>>> LIST_OF_RANDOM_HOLDER_CODEC = RegistryCodecs.homogeneousList(DatapackRegistries.PRIMARY_THEME_POOLS, RANDOM_HOLDER_CODEC, true);
    public static final Codec<RandomMapping<Biome, PrimaryTheme>> BIOME_MAPPING_DIRECT_CODEC = RandomMapping.makeDirectCodec(Biome.LIST_CODEC, LIST_OF_RANDOM_HOLDER_CODEC);
    public static final Codec<Holder<RandomMapping<Biome, PrimaryTheme>>> BIOME_MAPPING_HOLDER_CODEC = RegistryFileCodec.create(DatapackRegistries.PRIMARY_THEME_MAPPINGS, BIOME_MAPPING_DIRECT_CODEC);

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private BlockStateProvider pillar = SingleBlock.AIR;
        private BlockStateProvider masonry = SingleBlock.AIR;
        private BlockStateProvider floor = SingleBlock.AIR;
        private BlockStateProvider stairs = SingleBlock.AIR;
        private BlockStateProvider wall = SingleBlock.AIR;
        private BlockStateProvider slab = SingleBlock.AIR;
        private BlockStateProvider fencing = SingleBlock.AIR;
        private BlockStateProvider fluid = SingleBlock.AIR;
        private final List<DungeonDecoration> decorations = new ArrayList<>(0);

        public Builder pillar(BlockStateProvider pillar) {
            this.pillar = pillar;
            return this;
        }

        public Builder masonry(BlockStateProvider generic) {
            this.masonry = generic;
            return this;
        }

        public Builder floor(BlockStateProvider floor) {
            this.floor = floor;
            return this;
        }

        public Builder stairs(BlockStateProvider stairs) {
            this.stairs = stairs;
            return this;
        }

        public Builder wall(BlockStateProvider wall) {
            this.wall = wall;
            return this;
        }

        public Builder slab(BlockStateProvider slab) {
            this.slab = slab;
            return this;
        }

        public Builder fencing(BlockStateProvider fencing) {
            this.fencing = fencing;
            return this;
        }

        public Builder fluid(BlockStateProvider fluid) {
            this.fluid = fluid;
            return this;
        }

        public Builder withDecoration(DungeonDecoration decoration) {
            this.decorations.add(decoration);
            return this;
        }

        public PrimaryTheme build() {
            Objects.requireNonNull(pillar);
            Objects.requireNonNull(masonry);
            Objects.requireNonNull(floor);
            Objects.requireNonNull(fluid);
            Objects.requireNonNull(fencing);
            Objects.requireNonNull(stairs);
            Objects.requireNonNull(slab);
            Objects.requireNonNull(wall);
            return new PrimaryTheme(masonry, pillar, floor, fluid, fencing, stairs, slab, wall, decorations);
        }
    }
}