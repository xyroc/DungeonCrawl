package xiroc.dungeoncrawl.datapack.registry;

import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.registries.DataPackRegistryEvent;
import xiroc.dungeoncrawl.DungeonCrawl;
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
    ResourceKey<Registry<IRandom<BlockState>>> BLOCK_STATE_POOLS = ResourceKey.createRegistryKey(DungeonCrawl.locate(DatapackDirectories.BLOCK_STATE_POOLS.path()));
    ResourceKey<Registry<IRandom<Item>>> ITEM_POOLS = ResourceKey.createRegistryKey(DungeonCrawl.locate(DatapackDirectories.ITEM_POOLS.path()));

    ResourceKey<Registry<SpawnerEntityProperties>> SPAWNER_ENTITY_PROPERTIES = ResourceKey.createRegistryKey(DungeonCrawl.locate(DatapackDirectories.SPAWNER_ENTITY_PROPERTIES.path()));
    ResourceKey<Registry<SpawnerEntityType>> SPAWNER_ENTITY_TYPE = ResourceKey.createRegistryKey(DungeonCrawl.locate(DatapackDirectories.SPAWNER_ENTITIES.path()));
    ResourceKey<Registry<SpawnerType>> SPAWNER_TYPE = ResourceKey.createRegistryKey(DungeonCrawl.locate(DatapackDirectories.SPAWNER_TYPES.path()));

    ResourceKey<Registry<LevelType>> LEVEL_TYPE = ResourceKey.createRegistryKey(DungeonCrawl.locate(DatapackDirectories.DUNGEON_LAYER_TYPES.path()));
    ResourceKey<Registry<DungeonType>> DUNGEON_TYPE = ResourceKey.createRegistryKey(DungeonCrawl.locate(DatapackDirectories.DUNGEON_TYPES.path()));
    ResourceKey<Registry<IRandom<Holder<DungeonType>>>> DUNGEON_TYPE_POOLS = ResourceKey.createRegistryKey(DungeonCrawl.locate(DatapackDirectories.DUNGEON_TYPE_POOLS.path()));
    ResourceKey<Registry<RandomMapping<Biome, DungeonType>>> DUNGEON_TYPE_MAPPINGS = ResourceKey.createRegistryKey(DungeonCrawl.locate(DatapackDirectories.DUNGEON_TYPE_MAPPINGS.path()));

    ResourceKey<Registry<PrimaryTheme>> PRIMARY_THEME = ResourceKey.createRegistryKey(DungeonCrawl.locate(DatapackDirectories.PRIMARY_THEMES.path()));
    ResourceKey<Registry<SecondaryTheme>> SECONDARY_THEME = ResourceKey.createRegistryKey(DungeonCrawl.locate(DatapackDirectories.SECONDARY_THEMES.path()));
    ResourceKey<Registry<IRandom<Holder<PrimaryTheme>>>> PRIMARY_THEME_POOLS = ResourceKey.createRegistryKey(DungeonCrawl.locate(DatapackDirectories.PRIMARY_THEME_POOLS.path()));
    ResourceKey<Registry<IRandom<Holder<SecondaryTheme>>>> SECONDARY_THEME_POOLS = ResourceKey.createRegistryKey(DungeonCrawl.locate(DatapackDirectories.SECONDARY_THEME_POOLS.path()));
    ResourceKey<Registry<RandomMapping<Biome, PrimaryTheme>>> PRIMARY_THEME_MAPPINGS = ResourceKey.createRegistryKey(DungeonCrawl.locate(DatapackDirectories.PRIMARY_THEME_MAPPINGS.path()));
    ResourceKey<Registry<RandomMapping<Biome, SecondaryTheme>>> SECONDARY_THEME_MAPPINGS = ResourceKey.createRegistryKey(DungeonCrawl.locate(DatapackDirectories.SECONDARY_THEME_MAPPINGS.path()));

    ResourceKey<Registry<Blueprint>> BLUEPRINT = ResourceKey.createRegistryKey(DungeonCrawl.locate(DatapackDirectories.BLUEPRINTS.path()));
    ResourceKey<Registry<IRandom<Holder<Blueprint>>>> BLUEPRINT_POOLS = ResourceKey.createRegistryKey(DungeonCrawl.locate(DatapackDirectories.BLUEPRINT_POOLS.path()));

    static void register(final DataPackRegistryEvent.NewRegistry event) {
        event.dataPackRegistry(BLOCK_STATE_POOLS, IRandom.BaseCodecs.BLOCK_STATE);
        event.dataPackRegistry(ITEM_POOLS, IRandom.BaseCodecs.ITEM);

        event.dataPackRegistry(SPAWNER_ENTITY_PROPERTIES, SpawnerEntityProperties.DIRECT_CODEC);
        event.dataPackRegistry(SPAWNER_ENTITY_TYPE, SpawnerEntityType.DIRECT_CODEC);
        event.dataPackRegistry(SPAWNER_TYPE, SpawnerType.DIRECT_CODEC);

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
