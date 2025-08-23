package xiroc.dungeoncrawl.dungeon.component;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import xiroc.dungeoncrawl.dungeon.blueprint.Entrance;
import xiroc.dungeoncrawl.dungeon.blueprint.anchor.Anchor;
import xiroc.dungeoncrawl.util.bounds.BoundingBoxBuilder;
import xiroc.dungeoncrawl.worldgen.DungeonWorldGenContext;

public record EntranceComponent(Anchor placement, Entrance.Decoration decoration) implements DungeonComponent {
    public static final MapCodec<EntranceComponent> CODEC = RecordCodecBuilder.mapCodec(builder ->
            builder.group(Anchor.CODEC.fieldOf("placement").forGetter(EntranceComponent::placement),
                            Entrance.Decoration.CODEC.fieldOf("decoration").forGetter(EntranceComponent::decoration))
                    .apply(builder, EntranceComponent::new));

    @Override
    public void generate(LevelAccessor level, BoundingBox worldGenBounds, RandomSource random, DungeonWorldGenContext worldGenContext) {
        decoration.generate(level, placement, worldGenBounds, random, worldGenContext);
    }

    @Override
    public BoundingBoxBuilder boundingBox() {
        return BoundingBoxBuilder.fromCorners(
                placement.position().relative(placement.direction().getCounterClockWise()),
                placement.position().relative(placement.direction().getClockWise()).above(2)
        );
    }

    @Override
    public MapCodec<? extends DungeonComponent> codec() {
        return CODEC;
    }
}
