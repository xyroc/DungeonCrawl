package xiroc.dungeoncrawl.data.spawner;

import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import xiroc.dungeoncrawl.data.DataGen;
import xiroc.dungeoncrawl.datapack.registry.DatapackRegistries;
import xiroc.dungeoncrawl.dungeon.monster.SpawnerEntityType;

public interface SpawnerEntityTypeKeys {
    ResourceLocation CAVE_SPIDER = DataGen.resource("cave_spider");
    ResourceKey<SpawnerEntityType> CAVE_SPIDER_LEVEL_0 = key(DataGen.resourceLevel(CAVE_SPIDER, 0));
    ResourceKey<SpawnerEntityType> CAVE_SPIDER_LEVEL_1 = key(DataGen.resourceLevel(CAVE_SPIDER, 1));
    ResourceKey<SpawnerEntityType> CAVE_SPIDER_LEVEL_2 = key(DataGen.resourceLevel(CAVE_SPIDER, 2));
    ResourceKey<SpawnerEntityType> CAVE_SPIDER_LEVEL_3 = key(DataGen.resourceLevel(CAVE_SPIDER, 3));
    ResourceKey<SpawnerEntityType> CAVE_SPIDER_LEVEL_4 = key(DataGen.resourceLevel(CAVE_SPIDER, 4));

    ResourceKey<SpawnerEntityType> ZOMBIE = key(DataGen.resource("zombie"));

    private static ResourceKey<SpawnerEntityType> key(ResourceLocation identifier) {
        return ResourceKey.create(DatapackRegistries.SPAWNER_ENTITY_TYPE, identifier);
    }
}
