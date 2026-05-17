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

import java.util.function.Consumer;

public record SarcophagusFeature(PlacementSettings placement, ChestSettings chest, SpawnerSettings spawner) implements BlueprintFeature.AnchorBased {
    public static final MapCodec<SarcophagusFeature> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            PlacementSettings.CODEC.fieldOf("placement").forGetter(SarcophagusFeature::placement),
            ChestSettings.CODEC.fieldOf("chest").forGetter(SarcophagusFeature::chest),
            SpawnerSettings.CODEC.fieldOf("spawner").forGetter(SarcophagusFeature::spawner)
    ).apply(instance, SarcophagusFeature::new));

    @Override
    public void createAt(Consumer<DungeonComponent> components, LevelGenerator levelGenerator, Anchor anchor) {
        var spawnerType = spawner.getSpawnerTypes(levelGenerator).roll(levelGenerator.random);
        var lootTable = chest.getLootTable(levelGenerator);
        components.accept(new SarcophagusComponent(anchor, spawnerType, lootTable));
    }

    @Override
    public MapCodec<? extends BlueprintFeature> type() {
        return CODEC;
    }
}
