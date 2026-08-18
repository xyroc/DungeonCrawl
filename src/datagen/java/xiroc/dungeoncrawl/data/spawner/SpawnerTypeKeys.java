package xiroc.dungeoncrawl.data.spawner;

import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import xiroc.dungeoncrawl.data.DataGen;
import xiroc.dungeoncrawl.datapack.registry.DatapackRegistries;
import xiroc.dungeoncrawl.dungeon.monster.SpawnerType;

public interface SpawnerTypeKeys {
    ResourceKey<SpawnerType> LEVEL_0 = key(DataGen.resourceLevel(0));
    ResourceKey<SpawnerType> LEVEL_1 = key(DataGen.resourceLevel(1));
    ResourceKey<SpawnerType> LEVEL_2 = key(DataGen.resourceLevel(2));
    ResourceKey<SpawnerType> LEVEL_3 = key(DataGen.resourceLevel(3));
    ResourceKey<SpawnerType> LEVEL_4 = key(DataGen.resourceLevel(4));

    ResourceLocation CAVE_SPIDER = DataGen.resource("cave_spider");
    ResourceKey<SpawnerType> CAVE_SPIDER_LEVEL_4 = key(DataGen.resourceLevel(CAVE_SPIDER, 4));
    ResourceKey<SpawnerType> CAVE_SPIDER_LEVEL_3 = key(DataGen.resourceLevel(CAVE_SPIDER, 3));
    ResourceKey<SpawnerType> CAVE_SPIDER_LEVEL_2 = key(DataGen.resourceLevel(CAVE_SPIDER, 2));
    ResourceKey<SpawnerType> CAVE_SPIDER_LEVEL_1 = key(DataGen.resourceLevel(CAVE_SPIDER, 1));
    ResourceKey<SpawnerType> CAVE_SPIDER_LEVEL_0 = key(DataGen.resourceLevel(CAVE_SPIDER, 0));

    ResourceLocation __DROWNED__ = DataGen.resource("drowned");
    ResourceKey<SpawnerType> DROWNED_LEVEL_0 = key(DataGen.resourceLevel(__DROWNED__, 0));
    ResourceKey<SpawnerType> DROWNED_LEVEL_1 = key(DataGen.resourceLevel(__DROWNED__, 1));
    ResourceKey<SpawnerType> DROWNED_LEVEL_2 = key(DataGen.resourceLevel(__DROWNED__, 2));
    ResourceKey<SpawnerType> DROWNED_LEVEL_3 = key(DataGen.resourceLevel(__DROWNED__, 3));
    ResourceKey<SpawnerType> DROWNED_LEVEL_4 = key(DataGen.resourceLevel(__DROWNED__, 4));

    private static ResourceKey<SpawnerType> key(ResourceLocation identifier) {
        return ResourceKey.create(DatapackRegistries.SPAWNER_TYPE, identifier);
    }
}
