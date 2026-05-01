package xiroc.dungeoncrawl.data.pool.spawner;

import net.minecraft.core.Holder;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import xiroc.dungeoncrawl.data.DataGen;
import xiroc.dungeoncrawl.datapack.registry.DatapackRegistries;
import xiroc.dungeoncrawl.dungeon.monster.SpawnerEntityType;
import xiroc.dungeoncrawl.util.random.IRandom;

public interface SpawnerEntityTypePoolKeys {
    ResourceLocation __DEFAULT = DataGen.resource("default");

    ResourceKey<IRandom<Holder<SpawnerEntityType>>> LEVEL_0 = key(DataGen.resourceLevel(__DEFAULT, 0));

    ResourceLocation __CAVE_SPIDER = DataGen.resource("cave_spider");
    ResourceKey<IRandom<Holder<SpawnerEntityType>>> CAVE_SPIDER_LEVEL_0 = key(DataGen.resourceLevel(__CAVE_SPIDER, 0));
    ResourceKey<IRandom<Holder<SpawnerEntityType>>> CAVE_SPIDER_LEVEL_1 = key(DataGen.resourceLevel(__CAVE_SPIDER, 1));
    ResourceKey<IRandom<Holder<SpawnerEntityType>>> CAVE_SPIDER_LEVEL_2 = key(DataGen.resourceLevel(__CAVE_SPIDER, 2));
    ResourceKey<IRandom<Holder<SpawnerEntityType>>> CAVE_SPIDER_LEVEL_3 = key(DataGen.resourceLevel(__CAVE_SPIDER, 3));
    ResourceKey<IRandom<Holder<SpawnerEntityType>>> CAVE_SPIDER_LEVEL_4 = key(DataGen.resourceLevel(__CAVE_SPIDER, 4));

    private static ResourceKey<IRandom<Holder<SpawnerEntityType>>> key(ResourceLocation identifier) {
        return ResourceKey.create(DatapackRegistries.SPAWNER_ENTITY_TYPE_POOLS, identifier);
    }
}
