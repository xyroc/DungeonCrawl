package xiroc.dungeoncrawl.init;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.neoforged.neoforge.registries.NewRegistryEvent;
import net.neoforged.neoforge.registries.RegistryBuilder;
import xiroc.dungeoncrawl.DungeonCrawl;
import xiroc.dungeoncrawl.dungeon.blueprint.feature.BlueprintFeature;

public interface ModRegistries {
    ResourceKey<Registry<MapCodec<? extends BlueprintFeature>>> BLUEPRINT_FEATURE_KEY = ResourceKey.createRegistryKey(DungeonCrawl.locate("blueprint_feature"));

    Registry<MapCodec<? extends BlueprintFeature>> BLUEPRINT_FEATURE = new RegistryBuilder<>(BLUEPRINT_FEATURE_KEY)
            .sync(false)
            .create();

    static void register(final NewRegistryEvent event) {
        event.register(BLUEPRINT_FEATURE);
    }
}
