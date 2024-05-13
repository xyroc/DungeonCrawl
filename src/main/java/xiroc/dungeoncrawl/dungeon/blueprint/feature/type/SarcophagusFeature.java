package xiroc.dungeoncrawl.dungeon.blueprint.feature.type;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.state.properties.Half;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import xiroc.dungeoncrawl.datapack.DatapackRegistries;
import xiroc.dungeoncrawl.datapack.delegate.Delegate;
import xiroc.dungeoncrawl.dungeon.blueprint.anchor.Anchor;
import xiroc.dungeoncrawl.dungeon.blueprint.feature.PlacedFeature;
import xiroc.dungeoncrawl.dungeon.monster.SpawnerType;
import xiroc.dungeoncrawl.dungeon.theme.PrimaryTheme;
import xiroc.dungeoncrawl.dungeon.theme.SecondaryTheme;
import xiroc.dungeoncrawl.worldgen.WorldEditor;

import java.util.Optional;
import java.util.Random;

public record SarcophagusFeature(Anchor placement, Delegate<SpawnerType> spawnerType, Optional<ResourceLocation> lootTable) implements PlacedFeature {
    public static final Codec<PlacedFeature> CODEC = RecordCodecBuilder.create(builder -> builder.group(
            Anchor.CODEC.fieldOf("placement").forGetter(feature -> ((SarcophagusFeature) feature).placement),
            ResourceLocation.CODEC.xmap(key -> Delegate.of(DatapackRegistries.SPAWNER_TYPE.get(key), key), Delegate::key)
                    .fieldOf("spawner_type").forGetter(feature -> ((SarcophagusFeature) feature).spawnerType),
            ResourceLocation.CODEC.optionalFieldOf("loot_table").forGetter(feature -> ((SarcophagusFeature) feature).lootTable)
    ).apply(builder, SarcophagusFeature::new));

    @Override
    public void place(WorldGenLevel level, Random random, BoundingBox worldGenBounds, PrimaryTheme primaryTheme, SecondaryTheme secondaryTheme, int stage) {
        BlockPos pos = placement.position().relative(placement.direction());
        final var stair = primaryTheme.stairs();

        WorldEditor.placeStairs(level, stair, pos, worldGenBounds, Half.BOTTOM, placement.direction(), random, true, true, true);
        WorldEditor.placeStairs(level, stair, pos.relative(placement.direction(), 4), worldGenBounds, Half.BOTTOM, placement.direction().getOpposite(), random, true, true, true);
        WorldEditor.fill(level, primaryTheme.masonry(), pos.relative(placement.direction()), pos.relative(placement.direction(), 3), worldGenBounds, random, true, true);

        WorldEditor.placeStairs(level, stair, pos.above(), worldGenBounds, Half.TOP, placement.direction(), random, true, true, true);
        WorldEditor.placeStairs(level, stair, pos.above().relative(placement.direction(), 4), worldGenBounds, Half.TOP, placement.direction().getOpposite(), random, true, true,
                true);

        WorldEditor.placeStairs(level, stair, pos.above(2), worldGenBounds, Half.BOTTOM, placement.direction(), random, true, true, true);
        WorldEditor.placeStairs(level, stair, pos.above(2).relative(placement.direction(), 4), worldGenBounds, Half.BOTTOM, placement.direction().getOpposite(), random, true, true, true);
        WorldEditor.placeStairs(level, stair, pos.above(2).relative(placement.direction()), worldGenBounds, Half.TOP, placement.direction().getOpposite(), random, true, true, true);
        WorldEditor.placeStairs(level, stair, pos.above(2).relative(placement.direction(), 3), worldGenBounds, Half.TOP, placement.direction(), random, true, true, true);

        Rotation[] rotations = {Rotation.CLOCKWISE_90, Rotation.COUNTERCLOCKWISE_90};
        for (Rotation rotation : rotations) {
            Direction direction = rotation.rotate(placement.direction());
            BlockPos base = pos.relative(direction);
            WorldEditor.fill(level, stair, base, base.relative(placement.direction(), 4), worldGenBounds, random, true, true);
        }
    }
}
