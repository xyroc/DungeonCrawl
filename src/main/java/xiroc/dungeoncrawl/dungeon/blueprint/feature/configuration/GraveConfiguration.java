package xiroc.dungeoncrawl.dungeon.blueprint.feature.configuration;

import xiroc.dungeoncrawl.dungeon.blueprint.anchor.Anchor;
import xiroc.dungeoncrawl.dungeon.blueprint.feature.PlacedFeature;
import xiroc.dungeoncrawl.dungeon.blueprint.feature.configuration.settings.ChestSettings;
import xiroc.dungeoncrawl.dungeon.blueprint.feature.configuration.settings.PlacementSettings;
import xiroc.dungeoncrawl.dungeon.blueprint.feature.configuration.settings.SpawnerSettings;

import java.util.Random;

public record GraveConfiguration(PlacementSettings placement, ChestSettings chest, SpawnerSettings spawner) implements FeatureConfiguration.AnchorBased {
    @Override
    public PlacedFeature createInstance(Anchor anchor, Random random) {
        return null;
    }

    @Override
    public int type() {
        return 0;
    }
}
