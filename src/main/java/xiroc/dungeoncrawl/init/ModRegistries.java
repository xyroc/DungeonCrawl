package xiroc.dungeoncrawl.init;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.neoforged.neoforge.registries.NewRegistryEvent;
import net.neoforged.neoforge.registries.RegisterEvent;
import net.neoforged.neoforge.registries.RegistryBuilder;
import xiroc.dungeoncrawl.DungeonCrawl;
import xiroc.dungeoncrawl.dungeon.block.provider.BlockStateProvider;
import xiroc.dungeoncrawl.dungeon.blueprint.feature.BlueprintFeature;
import xiroc.dungeoncrawl.dungeon.blueprint.template.block.type.TemplateBlockType;
import xiroc.dungeoncrawl.dungeon.component.DungeonComponent;
import xiroc.dungeoncrawl.dungeon.decoration.DungeonDecoration;

public interface ModRegistries {
    ResourceKey<Registry<MapCodec<? extends BlockStateProvider>>> BLOCK_STATE_PROVIDER_TYPE_KEY = ResourceKey.createRegistryKey(DungeonCrawl.locate("block_state_provider_type"));
    ResourceKey<Registry<MapCodec<? extends BlueprintFeature>>> BLUEPRINT_FEATURE_KEY = ResourceKey.createRegistryKey(DungeonCrawl.locate("blueprint_feature"));
    ResourceKey<Registry<MapCodec<? extends DungeonDecoration>>> DECORATION_TYPE_KEY = ResourceKey.createRegistryKey(DungeonCrawl.locate("dungeon_decoration_type"));
    ResourceKey<Registry<MapCodec<? extends DungeonComponent>>> DUNGEON_COMPONENT_TYPE_KEY = ResourceKey.createRegistryKey(DungeonCrawl.locate("dungeon_component_type"));
    ResourceKey<Registry<MapCodec<? extends TemplateBlockType>>> TEMPLATE_BLOCK_TYPE_KEY = ResourceKey.createRegistryKey(DungeonCrawl.locate("template_block_type"));

    Registry<MapCodec<? extends BlockStateProvider>> BLOCK_STATE_PROVIDER_TYPE = new RegistryBuilder<>(BLOCK_STATE_PROVIDER_TYPE_KEY)
            .sync(false)
            .create();

    Registry<MapCodec<? extends BlueprintFeature>> BLUEPRINT_FEATURE = new RegistryBuilder<>(BLUEPRINT_FEATURE_KEY)
            .sync(false)
            .create();

    Registry<MapCodec<? extends DungeonDecoration>> DECORATION_TYPE = new RegistryBuilder<>(DECORATION_TYPE_KEY)
            .sync(false)
            .create();

    Registry<MapCodec<? extends DungeonComponent>> DUNGEON_COMPONENT_TYPE = new RegistryBuilder<>(DUNGEON_COMPONENT_TYPE_KEY)
            .sync(false)
            .create();

    Registry<MapCodec<? extends TemplateBlockType>> TEMPlATE_BLOCK_TYPE = new RegistryBuilder<>(TEMPLATE_BLOCK_TYPE_KEY)
            .sync(false)
            .create();

    static void addRegistries(final NewRegistryEvent event) {
        event.register(BLOCK_STATE_PROVIDER_TYPE);
        event.register(BLUEPRINT_FEATURE);
        event.register(DECORATION_TYPE);
        event.register(DUNGEON_COMPONENT_TYPE);
        event.register(TEMPlATE_BLOCK_TYPE);
    }

    static void registerEntries(final RegisterEvent event) {
        event.register(BLOCK_STATE_PROVIDER_TYPE_KEY, BlockStateProvider::register);
        event.register(BLUEPRINT_FEATURE_KEY, BlueprintFeature::register);
        event.register(DECORATION_TYPE_KEY, DungeonDecoration::register);
        event.register(DUNGEON_COMPONENT_TYPE_KEY, DungeonComponent::register);
        event.register(TEMPLATE_BLOCK_TYPE_KEY, TemplateBlockType::register);
    }
}
