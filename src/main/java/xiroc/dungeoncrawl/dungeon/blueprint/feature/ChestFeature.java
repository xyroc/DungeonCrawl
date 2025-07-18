package xiroc.dungeoncrawl.dungeon.blueprint.feature;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import xiroc.dungeoncrawl.dungeon.blueprint.anchor.Anchor;
import xiroc.dungeoncrawl.dungeon.blueprint.feature.settings.ChestSettings;
import xiroc.dungeoncrawl.dungeon.blueprint.feature.settings.PlacementSettings;
import xiroc.dungeoncrawl.dungeon.component.DungeonComponent;
import xiroc.dungeoncrawl.dungeon.component.feature.ChestComponent;
import xiroc.dungeoncrawl.dungeon.generator.level.LevelGenerator;

public record ChestFeature(PlacementSettings placement, ChestSettings chest) implements BlueprintFeature.AnchorBased {
    public static final MapCodec<ChestFeature> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            PlacementSettings.CODEC.fieldOf(SharedSerializationConstants.KEY_PLACEMENT_SETTINGS).forGetter(ChestFeature::placement),
            ChestSettings.CODEC.fieldOf(SharedSerializationConstants.KEY_CHEST_SETTINGS).forGetter(ChestFeature::chest)
    ).apply(instance, ChestFeature::new));

    @Override
    public DungeonComponent createInstance(LevelGenerator levelGenerator, Anchor anchor) {
        return new ChestComponent(anchor, chest.getLootTable(levelGenerator));
    }

    @Override
    public MapCodec<? extends BlueprintFeature> type() {
        return CODEC;
    }
}
