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

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public record ChainedFeatures(PlacementSettings placement, List<BlueprintFeature> features) implements BlueprintFeature {
    public static final MapCodec<ChainedFeatures> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            PlacementSettings.CODEC.fieldOf(SharedSerializationConstants.KEY_PLACEMENT_SETTINGS).forGetter(ChainedFeatures::placement),
            BlueprintFeature.CODEC.listOf().fieldOf("features").forGetter(ChainedFeatures::features)
    ).apply(instance, ChainedFeatures::new));

    @Override
    public void create(LevelGenerator levelGenerator, Consumer<DungeonComponent> consumer, @Nullable ArrayList<Anchor> positions, Blueprint blueprint, BlockPos offset, Rotation rotation) {
        var anchors = placement.anchors(blueprint);
        if (anchors.isEmpty()) {
            return;
        }
        positions = anchors.get();
        for (BlueprintFeature supplier : features) {
            supplier.create(levelGenerator, consumer, positions, blueprint, offset, rotation);
        }
    }

    @Override
    public MapCodec<? extends BlueprintFeature> type() {
        return CODEC;
    }
}
