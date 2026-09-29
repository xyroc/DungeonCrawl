package xiroc.dungeoncrawl.dungeon.blueprint.feature;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.Rotation;
import org.jetbrains.annotations.Nullable;
import xiroc.dungeoncrawl.dungeon.blueprint.Blueprint;
import xiroc.dungeoncrawl.dungeon.blueprint.anchor.Anchor;
import xiroc.dungeoncrawl.dungeon.blueprint.feature.settings.PlacementSettings;
import xiroc.dungeoncrawl.dungeon.component.DungeonComponent;
import xiroc.dungeoncrawl.dungeon.generator.level.LevelGenerator;
import xiroc.dungeoncrawl.util.random.IRandom;

import java.util.ArrayList;
import java.util.function.Consumer;

public record RandomFeature(PlacementSettings placement, IRandom<BlueprintFeature> features) implements BlueprintFeature {
    public static final MapCodec<RandomFeature> CODEC = RecordCodecBuilder.mapCodec(builder -> builder.group(
            PlacementSettings.CODEC.fieldOf("placement").forGetter(RandomFeature::placement),
            BlueprintFeature.RANDOM_CODEC.fieldOf("features").forGetter(RandomFeature::features)
    ).apply(builder, RandomFeature::new));

    @Override
    public void create(LevelGenerator levelGenerator, Consumer<DungeonComponent> consumer, @Nullable ArrayList<Anchor> positions, Blueprint blueprint, BlockPos offset, Rotation rotation) {
        positions = BlueprintFeature.gatherPositions(positions, blueprint, placement);
        if (positions == null) {
            return;
        }
        int amount = placement.amount().nextInt(levelGenerator.random);
        for (int i = 0; i < amount; i++) {
            features.roll(levelGenerator.random).create(levelGenerator, consumer, positions, blueprint, offset, rotation);
        }
    }

    @Override
    public MapCodec<? extends BlueprintFeature> type() {
        return CODEC;
    }
}
