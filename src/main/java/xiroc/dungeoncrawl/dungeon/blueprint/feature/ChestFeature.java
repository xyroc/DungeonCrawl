package xiroc.dungeoncrawl.dungeon.blueprint.feature;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import xiroc.dungeoncrawl.dungeon.blueprint.anchor.Anchor;
import xiroc.dungeoncrawl.dungeon.blueprint.feature.settings.ChestSettings;
import xiroc.dungeoncrawl.dungeon.blueprint.feature.settings.PlacementSettings;
import xiroc.dungeoncrawl.dungeon.component.DungeonComponent;
import xiroc.dungeoncrawl.dungeon.generator.level.LevelGenerator;

import java.util.function.Consumer;

public record ChestFeature(PlacementSettings placement, ChestSettings chest) implements BlueprintFeature.AnchorBased {
    public static final MapCodec<ChestFeature> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            PlacementSettings.CODEC.fieldOf("placement").forGetter(ChestFeature::placement),
            ChestSettings.CODEC.fieldOf("chest").forGetter(ChestFeature::chest)
    ).apply(instance, ChestFeature::new));

    @Override
    public void createAt(Consumer<DungeonComponent> components, LevelGenerator levelGenerator, Anchor anchor) {
        components.accept(chest.create(anchor, levelGenerator));
    }

    @Override
    public MapCodec<? extends BlueprintFeature> type() {
        return CODEC;
    }
}
