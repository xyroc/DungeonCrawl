package xiroc.dungeoncrawl.dungeon.blueprint.feature;

import net.minecraft.nbt.CompoundTag;
import xiroc.dungeoncrawl.dungeon.blueprint.anchor.Anchor;
import xiroc.dungeoncrawl.dungeon.blueprint.feature.configuration.ChestConfiguration;
import xiroc.dungeoncrawl.dungeon.blueprint.feature.configuration.FeatureConfiguration;
import xiroc.dungeoncrawl.dungeon.blueprint.feature.configuration.FlowerPotConfiguration;
import xiroc.dungeoncrawl.dungeon.blueprint.feature.configuration.SpawnerConfiguration;
import xiroc.dungeoncrawl.dungeon.blueprint.feature.type.ChestFeature;
import xiroc.dungeoncrawl.dungeon.blueprint.feature.type.FlowerPotFeature;
import xiroc.dungeoncrawl.dungeon.blueprint.feature.type.SpawnerFeature;
import xiroc.dungeoncrawl.dungeon.blueprint.feature.type.TNTChestFeature;

import java.util.function.Function;

public record BlueprintFeatureType<T extends FeatureConfiguration>(BlueprintFeature.Constructor<T> constructor, Function<CompoundTag, BlueprintFeature<T, ?>> nbtReader) {
    public static final BlueprintFeatureType<ChestConfiguration> CHEST = new BlueprintFeatureType<>(ChestFeature::new, ChestFeature::new);
    public static final BlueprintFeatureType<ChestConfiguration> TNT_CHEST = new BlueprintFeatureType<>(TNTChestFeature::new, TNTChestFeature::new);
    public static final BlueprintFeatureType<SpawnerConfiguration> SPAWNER = new BlueprintFeatureType<>(SpawnerFeature::new, SpawnerFeature::new);
    public static final BlueprintFeatureType<FlowerPotConfiguration> FLOWER_POT = new BlueprintFeatureType<>(FlowerPotFeature::new, FlowerPotFeature::new);
}
