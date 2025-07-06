package xiroc.dungeoncrawl.dungeon.blueprint.feature;

import xiroc.dungeoncrawl.dungeon.blueprint.anchor.Anchor;
import xiroc.dungeoncrawl.dungeon.blueprint.feature.settings.ChestSettings;
import xiroc.dungeoncrawl.dungeon.blueprint.feature.settings.PlacementSettings;
import xiroc.dungeoncrawl.dungeon.blueprint.feature.settings.SpawnerSettings;
import xiroc.dungeoncrawl.dungeon.component.DungeonComponent;
import xiroc.dungeoncrawl.dungeon.generator.level.LevelGenerator;

public record GraveFeature(PlacementSettings placement, ChestSettings chest, SpawnerSettings spawner) implements BlueprintFeature.AnchorBased {
    @Override
    public DungeonComponent createInstance(LevelGenerator levelGenerator, Anchor anchor) {
        return null;
    }

}
