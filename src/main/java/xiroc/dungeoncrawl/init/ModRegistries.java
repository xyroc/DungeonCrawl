package xiroc.dungeoncrawl.init;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.neoforged.neoforge.registries.NewRegistryEvent;
import net.neoforged.neoforge.registries.RegisterEvent;
import net.neoforged.neoforge.registries.RegistryBuilder;
import xiroc.dungeoncrawl.DungeonCrawl;
import xiroc.dungeoncrawl.dungeon.blueprint.feature.BlueprintFeature;
import xiroc.dungeoncrawl.dungeon.blueprint.template.block.type.TemplateBlockType;

public interface ModRegistries {
    ResourceKey<Registry<MapCodec<? extends BlueprintFeature>>> BLUEPRINT_FEATURE_KEY = ResourceKey.createRegistryKey(DungeonCrawl.locate("blueprint_feature"));
    ResourceKey<Registry<MapCodec<? extends TemplateBlockType>>> TEMPLATE_BLOCK_TYPE_KEY = ResourceKey.createRegistryKey(DungeonCrawl.locate("template_block_type"));

    Registry<MapCodec<? extends BlueprintFeature>> BLUEPRINT_FEATURE = new RegistryBuilder<>(BLUEPRINT_FEATURE_KEY)
            .sync(false)
            .create();

    Registry<MapCodec<? extends TemplateBlockType>> TEMPlATE_BLOCK_TYPE = new RegistryBuilder<>(TEMPLATE_BLOCK_TYPE_KEY)
            .sync(false)
            .create();

    static void addRegistries(final NewRegistryEvent event) {
        event.register(BLUEPRINT_FEATURE);
        event.register(TEMPlATE_BLOCK_TYPE);
    }

    static void registerEntries(final RegisterEvent event) {
        event.register(BLUEPRINT_FEATURE_KEY, BlueprintFeature::register);
        event.register(TEMPLATE_BLOCK_TYPE_KEY, TemplateBlockType::register);
    }
}
