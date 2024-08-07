package xiroc.dungeoncrawl.dungeon.component;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.DynamicOps;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import xiroc.dungeoncrawl.dungeon.blueprint.Entrance;
import xiroc.dungeoncrawl.dungeon.blueprint.anchor.Anchor;
import xiroc.dungeoncrawl.dungeon.theme.PrimaryTheme;
import xiroc.dungeoncrawl.dungeon.theme.SecondaryTheme;
import xiroc.dungeoncrawl.util.bounds.BoundingBoxBuilder;

import java.util.Optional;
import java.util.Random;

public record EntranceComponent(Anchor placement, Optional<Entrance.Decoration> decoration) implements DungeonComponent {
    public static final Codec<EntranceComponent> CODEC = RecordCodecBuilder.create(builder ->
            builder.group(Anchor.CODEC.fieldOf("placement").forGetter(EntranceComponent::placement),
                            Entrance.Decoration.CODEC.optionalFieldOf("decoration").forGetter(EntranceComponent::decoration))
                    .apply(builder, EntranceComponent::new));

    @Override
    public void generate(LevelAccessor level, BoundingBox worldGenBounds, Random random, PrimaryTheme primaryTheme, SecondaryTheme secondaryTheme, int stage) {
        this.decoration.ifPresent(decoration -> decoration.generate(level, placement, worldGenBounds, random, primaryTheme, secondaryTheme, stage));
    }

    @Override
    public BoundingBoxBuilder boundingBox() {
        return BoundingBoxBuilder.fromCorners(
                placement.position().relative(placement.direction().getCounterClockWise()),
                placement.position().relative(placement.direction().getClockWise()).above(2)
        );
    }

    @Override
    public int componentType() {
        return DECODERS.getId(CODEC);
    }

    @Override
    public <T> DataResult<T> encode(DynamicOps<T> ops) {
        return CODEC.encodeStart(ops, this);
    }
}
