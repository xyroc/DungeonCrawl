package xiroc.dungeoncrawl.data.spawner;

import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import xiroc.dungeoncrawl.data.DataGen;
import xiroc.dungeoncrawl.datapack.registry.DatapackRegistries;
import xiroc.dungeoncrawl.dungeon.monster.SpawnerEntityType;

public interface SpawnerEntityTypeKeys {
    ResourceLocation __CAVE_SPIDER__ = DataGen.resource("cave_spider");
    ResourceKey<SpawnerEntityType> CAVE_SPIDER_LEVEL_0 = key(DataGen.resourceLevel(__CAVE_SPIDER__, 0));
    ResourceKey<SpawnerEntityType> CAVE_SPIDER_LEVEL_1 = key(DataGen.resourceLevel(__CAVE_SPIDER__, 1));
    ResourceKey<SpawnerEntityType> CAVE_SPIDER_LEVEL_2 = key(DataGen.resourceLevel(__CAVE_SPIDER__, 2));
    ResourceKey<SpawnerEntityType> CAVE_SPIDER_LEVEL_3 = key(DataGen.resourceLevel(__CAVE_SPIDER__, 3));
    ResourceKey<SpawnerEntityType> CAVE_SPIDER_LEVEL_4 = key(DataGen.resourceLevel(__CAVE_SPIDER__, 4));

    ResourceLocation __ZOMBIE__ = DataGen.resource("zombie");
    ResourceKey<SpawnerEntityType> ZOMBIE_LEVEL_0 = key(DataGen.resourceLevel(__ZOMBIE__, 0));
    ResourceKey<SpawnerEntityType> ZOMBIE_LEVEL_1 = key(DataGen.resourceLevel(__ZOMBIE__, 1));
    ResourceKey<SpawnerEntityType> ZOMBIE_LEVEL_2 = key(DataGen.resourceLevel(__ZOMBIE__, 2));
    ResourceKey<SpawnerEntityType> ZOMBIE_LEVEL_3 = key(DataGen.resourceLevel(__ZOMBIE__, 3));
    ResourceKey<SpawnerEntityType> ZOMBIE_LEVEL_4 = key(DataGen.resourceLevel(__ZOMBIE__, 4));

    private static ResourceKey<SpawnerEntityType> key(ResourceLocation identifier) {
        return ResourceKey.create(DatapackRegistries.SPAWNER_ENTITY_TYPE, identifier);
    }
}
