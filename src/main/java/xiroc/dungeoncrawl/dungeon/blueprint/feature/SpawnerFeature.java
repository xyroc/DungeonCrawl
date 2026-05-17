package xiroc.dungeoncrawl.dungeon.blueprint.feature;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import xiroc.dungeoncrawl.dungeon.blueprint.anchor.Anchor;
import xiroc.dungeoncrawl.dungeon.blueprint.feature.settings.PlacementSettings;
import xiroc.dungeoncrawl.dungeon.blueprint.feature.settings.SpawnerSettings;
import xiroc.dungeoncrawl.dungeon.component.DungeonComponent;
import xiroc.dungeoncrawl.dungeon.component.feature.SpawnerComponent;
import xiroc.dungeoncrawl.dungeon.generator.level.LevelGenerator;

import java.util.function.Consumer;

public record SpawnerFeature(PlacementSettings placement, SpawnerSettings spawner) implements BlueprintFeature.AnchorBased {
    public static final MapCodec<SpawnerFeature> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            PlacementSettings.CODEC.fieldOf("placement").forGetter(SpawnerFeature::placement),
            SpawnerSettings.CODEC.fieldOf("spawner").forGetter(SpawnerFeature::spawner)
    ).apply(instance, SpawnerFeature::new));

    @Override
    public void createAt(Consumer<DungeonComponent> components, LevelGenerator levelGenerator, Anchor anchor) {
        var spawnerType = spawner.getSpawnerTypes(levelGenerator).roll(levelGenerator.random);
        components.accept(new SpawnerComponent(anchor.position(), spawnerType));
    }

    @Override
    public MapCodec<? extends BlueprintFeature> type() {
        return CODEC;
    }
}
