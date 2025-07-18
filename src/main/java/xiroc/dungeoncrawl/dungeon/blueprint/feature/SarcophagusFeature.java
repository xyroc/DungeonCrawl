package xiroc.dungeoncrawl.dungeon.blueprint.feature;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import xiroc.dungeoncrawl.dungeon.blueprint.anchor.Anchor;
import xiroc.dungeoncrawl.dungeon.blueprint.feature.settings.ChestSettings;
import xiroc.dungeoncrawl.dungeon.blueprint.feature.settings.PlacementSettings;
import xiroc.dungeoncrawl.dungeon.blueprint.feature.settings.SpawnerSettings;
import xiroc.dungeoncrawl.dungeon.component.DungeonComponent;
import xiroc.dungeoncrawl.dungeon.component.feature.SarcophagusComponent;
import xiroc.dungeoncrawl.dungeon.generator.level.LevelGenerator;

public record SarcophagusFeature(PlacementSettings placement, ChestSettings chest, SpawnerSettings spawner) implements BlueprintFeature.AnchorBased {
    public static final MapCodec<SarcophagusFeature> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            PlacementSettings.CODEC.fieldOf(SharedSerializationConstants.KEY_PLACEMENT_SETTINGS).forGetter(SarcophagusFeature::placement),
            ChestSettings.CODEC.fieldOf(SharedSerializationConstants.KEY_CHEST_SETTINGS).forGetter(SarcophagusFeature::chest),
            SpawnerSettings.CODEC.fieldOf(SharedSerializationConstants.KEY_SPAWNER_SETTINGS).forGetter(SarcophagusFeature::spawner)
    ).apply(instance, SarcophagusFeature::new));

    @Override
    public DungeonComponent createInstance(LevelGenerator levelGenerator, Anchor anchor) {
        var spawnerType = spawner.getSpawnerTypes(levelGenerator).roll(levelGenerator.random);
        var lootTable = chest.getLootTable(levelGenerator);
        return new SarcophagusComponent(anchor, spawnerType, lootTable);
    }

    @Override
    public MapCodec<? extends BlueprintFeature> type() {
        return CODEC;
    }
}
