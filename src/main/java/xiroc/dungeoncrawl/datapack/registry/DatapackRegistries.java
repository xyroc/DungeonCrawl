package xiroc.dungeoncrawl.datapack.registry;

import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.registries.DataPackRegistryEvent;
import xiroc.dungeoncrawl.datapack.DatapackDirectories;
import xiroc.dungeoncrawl.dungeon.blueprint.Blueprint;
import xiroc.dungeoncrawl.dungeon.monster.SpawnerEntityProperties;
import xiroc.dungeoncrawl.dungeon.monster.SpawnerEntityType;
import xiroc.dungeoncrawl.dungeon.monster.SpawnerType;
import xiroc.dungeoncrawl.dungeon.theme.PrimaryTheme;
import xiroc.dungeoncrawl.dungeon.theme.SecondaryTheme;
import xiroc.dungeoncrawl.dungeon.type.DungeonType;
import xiroc.dungeoncrawl.dungeon.type.level.LevelType;
import xiroc.dungeoncrawl.util.random.IRandom;
import xiroc.dungeoncrawl.util.random.RandomMapping;

public interface DatapackRegistries {
    ResourceKey<Registry<IRandom<BlockState>>> BLOCK_STATE_POOLS = DatapackDirectories.BLOCK_STATE_POOLS.asRegistry();
    ResourceKey<Registry<IRandom<Item>>> ITEM_POOLS = DatapackDirectories.ITEM_POOLS.asRegistry();

    ResourceKey<Registry<SpawnerEntityProperties>> SPAWNER_ENTITY_PROPERTIES = DatapackDirectories.SPAWNER_ENTITY_PROPERTIES.asRegistry();
    ResourceKey<Registry<SpawnerEntityType>> SPAWNER_ENTITY_TYPE = DatapackDirectories.SPAWNER_ENTITIES.asRegistry();
    ResourceKey<Registry<SpawnerType>> SPAWNER_TYPE = DatapackDirectories.SPAWNER_TYPES.asRegistry();
    ResourceKey<Registry<IRandom<Holder<SpawnerEntityType>>>> SPAWNER_ENTITY_TYPE_POOLS = DatapackDirectories.SPAWNER_ENTITY_TYPE_POOLS.asRegistry();
    ResourceKey<Registry<IRandom<Holder<SpawnerType>>>> SPAWNER_TYPE_POOLS = DatapackDirectories.SPAWNER_TYPE_POOLS.asRegistry();

    ResourceKey<Registry<LevelType>> LEVEL_TYPE = DatapackDirectories.DUNGEON_LAYER_TYPES.asRegistry();
    ResourceKey<Registry<DungeonType>> DUNGEON_TYPE = DatapackDirectories.DUNGEON_TYPES.asRegistry();
    ResourceKey<Registry<IRandom<Holder<DungeonType>>>> DUNGEON_TYPE_POOLS = DatapackDirectories.DUNGEON_TYPE_POOLS.asRegistry();
    ResourceKey<Registry<RandomMapping<Biome, DungeonType>>> DUNGEON_TYPE_MAPPINGS = DatapackDirectories.DUNGEON_TYPE_MAPPINGS.asRegistry();

    ResourceKey<Registry<PrimaryTheme>> PRIMARY_THEME = DatapackDirectories.PRIMARY_THEMES.asRegistry();
    ResourceKey<Registry<SecondaryTheme>> SECONDARY_THEME = DatapackDirectories.SECONDARY_THEMES.asRegistry();
    ResourceKey<Registry<IRandom<Holder<PrimaryTheme>>>> PRIMARY_THEME_POOLS = DatapackDirectories.PRIMARY_THEME_POOLS.asRegistry();
    ResourceKey<Registry<IRandom<Holder<SecondaryTheme>>>> SECONDARY_THEME_POOLS = DatapackDirectories.SECONDARY_THEME_POOLS.asRegistry();
    ResourceKey<Registry<RandomMapping<Biome, PrimaryTheme>>> PRIMARY_THEME_MAPPINGS = DatapackDirectories.PRIMARY_THEME_MAPPINGS.asRegistry();
    ResourceKey<Registry<RandomMapping<Biome, SecondaryTheme>>> SECONDARY_THEME_MAPPINGS = DatapackDirectories.SECONDARY_THEME_MAPPINGS.asRegistry();

    ResourceKey<Registry<Blueprint>> BLUEPRINT = DatapackDirectories.BLUEPRINTS.asRegistry();
    ResourceKey<Registry<IRandom<Holder<Blueprint>>>> BLUEPRINT_POOLS = DatapackDirectories.BLUEPRINT_POOLS.asRegistry();

    static void register(final DataPackRegistryEvent.NewRegistry event) {
        event.dataPackRegistry(BLOCK_STATE_POOLS, IRandom.BaseCodecs.BLOCK_STATE);
        event.dataPackRegistry(ITEM_POOLS, IRandom.BaseCodecs.ITEM);

        event.dataPackRegistry(SPAWNER_ENTITY_PROPERTIES, SpawnerEntityProperties.DIRECT_CODEC);
        event.dataPackRegistry(SPAWNER_ENTITY_TYPE, SpawnerEntityType.DIRECT_CODEC);
        event.dataPackRegistry(SPAWNER_TYPE, SpawnerType.DIRECT_CODEC);
        event.dataPackRegistry(SPAWNER_ENTITY_TYPE_POOLS, SpawnerEntityType.RANDOM_HOLDER_CODEC);
        event.dataPackRegistry(SPAWNER_TYPE_POOLS, SpawnerType.RANDOM_HOLDER_CODEC);

        event.dataPackRegistry(LEVEL_TYPE, LevelType.DIRECT_CODEC);
        event.dataPackRegistry(DUNGEON_TYPE, DungeonType.DIRECT_CODEC);
        event.dataPackRegistry(DUNGEON_TYPE_POOLS, DungeonType.RANDOM_HOLDER_CODEC);
        event.dataPackRegistry(DUNGEON_TYPE_MAPPINGS, DungeonType.BIOME_MAPPING_DIRECT_CODEC);

        event.dataPackRegistry(PRIMARY_THEME, PrimaryTheme.DIRECT_CODEC);
        event.dataPackRegistry(PRIMARY_THEME_POOLS, PrimaryTheme.RANDOM_HOLDER_CODEC);
        event.dataPackRegistry(PRIMARY_THEME_MAPPINGS, PrimaryTheme.BIOME_MAPPING_DIRECT_CODEC);

        event.dataPackRegistry(SECONDARY_THEME, SecondaryTheme.DIRECT_CODEC);
        event.dataPackRegistry(SECONDARY_THEME_POOLS, SecondaryTheme.RANDOM_HOLDER_CODEC);
        event.dataPackRegistry(SECONDARY_THEME_MAPPINGS, SecondaryTheme.BIOME_MAPPING_DIRECT_CODEC);

        event.dataPackRegistry(BLUEPRINT, Blueprint.DIRECT_CODEC);
        event.dataPackRegistry(BLUEPRINT_POOLS, Blueprint.RANDOM_HOLDER_CODEC);
    }
}
