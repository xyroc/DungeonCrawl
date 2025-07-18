package xiroc.dungeoncrawl.dungeon.type;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.Holder;
import net.minecraft.world.level.biome.Biome;
import xiroc.dungeoncrawl.dungeon.theme.PrimaryTheme;
import xiroc.dungeoncrawl.dungeon.theme.SecondaryTheme;
import xiroc.dungeoncrawl.dungeon.type.level.LevelType;
import xiroc.dungeoncrawl.util.random.RandomMapping;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public record DungeonSection(List<Holder<LevelType>> levels,
                             Holder<RandomMapping<Biome, PrimaryTheme>> primaryThemes,
                             Holder<RandomMapping<Biome, SecondaryTheme>> secondaryThemes) {
    public static final Codec<DungeonSection> CODEC = RecordCodecBuilder.create(instance -> instance
            .group(
                    LevelType.HOLDER_CODEC.listOf().fieldOf("levels").forGetter(DungeonSection::levels),
                    PrimaryTheme.BIOME_MAPPING_HOLDER_CODEC.fieldOf("primary_themes").forGetter(DungeonSection::primaryThemes),
                    SecondaryTheme.BIOME_MAPPING_HOLDER_CODEC.fieldOf("secondary_themes").forGetter(DungeonSection::secondaryThemes)
            ).apply(instance, DungeonSection::new));

    public static class Builder {
        private final List<Holder<LevelType>> levels = new ArrayList<>();
        @Nullable
        private Holder<RandomMapping<Biome, PrimaryTheme>> primaryThemes = null;
        @Nullable
        private Holder<RandomMapping<Biome, SecondaryTheme>> secondaryThemes = null;

        public Builder() {}

        private Builder(DungeonSection instance) {
            this.levels.addAll(instance.levels);
            this.primaryThemes = instance.primaryThemes;
            this.secondaryThemes = instance.secondaryThemes;
        }

        public Builder primaryThemes(@Nullable Holder<RandomMapping<Biome, PrimaryTheme>> primaryThemes) {
            this.primaryThemes = primaryThemes;
            return this;
        }

        public Builder secondaryThemes(@Nullable Holder<RandomMapping<Biome, SecondaryTheme>> secondaryThemes) {
            this.secondaryThemes = secondaryThemes;
            return this;
        }

        public Builder level(Holder<LevelType> level) {
            levels.add(level);
            return this;
        }

        public DungeonSection build() {
            Objects.requireNonNull(primaryThemes, "No mapping for primary themes was specified");
            Objects.requireNonNull(secondaryThemes, "No mapping for secondary themes was specified");
            if (levels.isEmpty()) {
                throw new IllegalStateException("A section must contain at least one level");
            }
            return new DungeonSection(List.copyOf(levels), primaryThemes, secondaryThemes);
        }
    }
}
