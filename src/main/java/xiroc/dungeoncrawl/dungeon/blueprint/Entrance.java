package xiroc.dungeoncrawl.dungeon.blueprint;

import com.google.common.collect.ImmutableBiMap;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.Holder;
import net.minecraft.core.IdMapper;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import xiroc.dungeoncrawl.dungeon.block.provider.SingleBlock;
import xiroc.dungeoncrawl.dungeon.blueprint.anchor.Anchor;
import xiroc.dungeoncrawl.dungeon.component.EntranceComponent;
import xiroc.dungeoncrawl.util.random.IRandom;
import xiroc.dungeoncrawl.worldgen.DungeonWorldGenContext;
import xiroc.dungeoncrawl.worldgen.WorldEditor;

import javax.annotation.Nullable;
import java.util.Objects;
import java.util.Optional;

public record Entrance(Anchor placement, Optional<Decoration> decoration, Optional<CustomParts> customParts) {
    public Entrance(Anchor placement) {
        this(placement, Optional.of(Decoration.PRIMARY), Optional.empty());
    }

    /**
     * Creates an {@link EntranceComponent} with this entrance's decoration at the provided anchor.
     *
     * @param placement the entrance anchor to place the entrance at
     * @return the {@link EntranceComponent}, or null if this entrance does not have a decoration
     */
    @Nullable
    public EntranceComponent place(Anchor placement) {
        return decoration.map(value -> new EntranceComponent(new Anchor(placement.position().above(), placement.direction()), value)).orElse(null);
    }

    public record CustomParts(IRandom<Holder<Blueprint>> open, IRandom<Holder<Blueprint>> closed) {
        public static final Codec<CustomParts> CODEC = RecordCodecBuilder.create(instance -> instance.group(
                Blueprint.RANDOM_HOLDER_CODEC.fieldOf("open").forGetter(CustomParts::open),
                Blueprint.RANDOM_HOLDER_CODEC.fieldOf("closed").forGetter(CustomParts::closed)
        ).apply(instance, CustomParts::new));

        public static class Builder {
            private IRandom<Holder<Blueprint>> open = null;
            private IRandom<Holder<Blueprint>> closed = null;

            public CustomParts build() {
                return new CustomParts(Objects.requireNonNull(open), Objects.requireNonNull(closed));
            }

            public Builder open(IRandom<Holder<Blueprint>> open) {
                this.open = Objects.requireNonNull(open);
                return this;
            }

            public Builder closed(IRandom<Holder<Blueprint>> closed) {
                this.closed = Objects.requireNonNull(closed);
                return this;
            }
        }
    }

    public interface Decoration {
        Decoration NONE = (level, placement, worldGenBounds, random, worldGenContext) ->
                WorldEditor.fill(level, SingleBlock.AIR,
                        null, placement.position().relative(placement.direction().getClockWise()),
                        placement.position().relative(placement.direction().getCounterClockWise()).above(2),
                        worldGenBounds, random, false);

        Decoration PRIMARY = (level, placement, worldGenBounds, random, worldGenContext) ->
                WorldEditor.placeEntrance(level, worldGenContext.primaryTheme().value().stairs(), placement.position(), placement.direction().getClockWise(), worldGenBounds, random, false, true);

        Decoration SECONDARY = (level, placement, worldGenBounds, random, worldGenContext) ->
                WorldEditor.placeEntrance(level, worldGenContext.secondaryTheme().value().stairs(), placement.position(), placement.direction().getClockWise(), worldGenBounds, random, false, true);

        private static IdMapper<Decoration> gatherDecorations() {
            IdMapper<Decoration> decorations = new IdMapper<>();
            decorations.addMapping(NONE, 0);
            decorations.addMapping(PRIMARY, 1);
            decorations.addMapping(SECONDARY, 2);
            return decorations;
        }

        IdMapper<Decoration> DECORATIONS = gatherDecorations();

        Codec<Decoration> CODEC = Codec.INT.xmap(DECORATIONS::byId, DECORATIONS::getId);

        ImmutableBiMap<String, Decoration> BY_NAME = ImmutableBiMap.<String, Entrance.Decoration>builder()
                .put("none", NONE)
                .put("primary", PRIMARY)
                .put("secondary", SECONDARY)
                .build();

        Codec<Decoration> BY_NAME_CODEC = Codec.STRING.flatXmap(name -> {
            Decoration decoration = BY_NAME.get(name);
            if (decoration == null) {
                return DataResult.error(() -> "Invalid decoration: " + name);
            }
            return DataResult.success(decoration);
        }, decoration -> {
            String name = BY_NAME.inverse().get(decoration);
            if (name == null) {
                return DataResult.error(() -> "Decoration does not have a name");
            }
            return DataResult.success(name);
        });

        void generate(LevelAccessor level, Anchor placement, BoundingBox worldGenBounds, RandomSource random, DungeonWorldGenContext worldGenContext);
    }
}
