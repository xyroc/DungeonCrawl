package xiroc.dungeoncrawl.dungeon.blueprint.feature;

import xiroc.dungeoncrawl.dungeon.blueprint.anchor.Anchor;
import xiroc.dungeoncrawl.dungeon.blueprint.feature.settings.ChestSettings;
import xiroc.dungeoncrawl.dungeon.blueprint.feature.settings.PlacementSettings;
import xiroc.dungeoncrawl.dungeon.blueprint.feature.settings.SpawnerSettings;
import xiroc.dungeoncrawl.dungeon.component.DungeonComponent;

import java.util.Random;

public record GraveFeature(PlacementSettings placement, ChestSettings chest, SpawnerSettings spawner) implements BlueprintFeature.AnchorBased {
    @Override
    public DungeonComponent createInstance(Anchor anchor, Random random) {
        return null;
    }

}
