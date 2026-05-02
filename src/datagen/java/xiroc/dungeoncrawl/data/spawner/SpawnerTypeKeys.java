package xiroc.dungeoncrawl.data.spawner;

import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import xiroc.dungeoncrawl.data.DataGen;
import xiroc.dungeoncrawl.datapack.registry.DatapackRegistries;
import xiroc.dungeoncrawl.dungeon.monster.SpawnerType;

public interface SpawnerTypeKeys {
    ResourceLocation CAVE_SPIDER = DataGen.resource("cave_spider");
    ResourceKey<SpawnerType> CAVE_SPIDER_LEVEL_4 = key(DataGen.resourceLevel(CAVE_SPIDER, 4));
    ResourceKey<SpawnerType> CAVE_SPIDER_LEVEL_3 = key(DataGen.resourceLevel(CAVE_SPIDER, 3));
    ResourceKey<SpawnerType> CAVE_SPIDER_LEVEL_2 = key(DataGen.resourceLevel(CAVE_SPIDER, 2));
    ResourceKey<SpawnerType> CAVE_SPIDER_LEVEL_1 = key(DataGen.resourceLevel(CAVE_SPIDER, 1));
    ResourceKey<SpawnerType> CAVE_SPIDER_LEVEL_0 = key(DataGen.resourceLevel(CAVE_SPIDER, 0));

    ResourceKey<SpawnerType> DEFAULT = key(DataGen.resource("default"));

    ResourceLocation TNT = DataGen.resource("tnt");
    ResourceKey<SpawnerType> TNT_LEVEL_0 = key(DataGen.resourceLevel(TNT, 0));

    private static ResourceKey<SpawnerType> key(ResourceLocation identifier) {
        return ResourceKey.create(DatapackRegistries.SPAWNER_TYPE, identifier);
    }
}
