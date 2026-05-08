package xiroc.dungeoncrawl.data.pool.spawner;

import net.minecraft.core.Holder;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import xiroc.dungeoncrawl.data.DataGen;
import xiroc.dungeoncrawl.datapack.registry.DatapackRegistries;
import xiroc.dungeoncrawl.dungeon.monster.SpawnerEntityType;
import xiroc.dungeoncrawl.util.random.IRandom;

public interface SpawnerEntityTypePoolKeys {
    ResourceLocation __CAVE_SPIDER__ = DataGen.resource("cave_spider");
    ResourceKey<IRandom<Holder<SpawnerEntityType>>> CAVE_SPIDER_LEVEL_0 = key(DataGen.resourceLevel(__CAVE_SPIDER__, 0));
    ResourceKey<IRandom<Holder<SpawnerEntityType>>> CAVE_SPIDER_LEVEL_1 = key(DataGen.resourceLevel(__CAVE_SPIDER__, 1));
    ResourceKey<IRandom<Holder<SpawnerEntityType>>> CAVE_SPIDER_LEVEL_2 = key(DataGen.resourceLevel(__CAVE_SPIDER__, 2));
    ResourceKey<IRandom<Holder<SpawnerEntityType>>> CAVE_SPIDER_LEVEL_3 = key(DataGen.resourceLevel(__CAVE_SPIDER__, 3));
    ResourceKey<IRandom<Holder<SpawnerEntityType>>> CAVE_SPIDER_LEVEL_4 = key(DataGen.resourceLevel(__CAVE_SPIDER__, 4));

    ResourceLocation __ZOMBIE_LIKE__ = DataGen.resource("zombie_like");
    ResourceKey<IRandom<Holder<SpawnerEntityType>>> ZOMBIE_LIKE_LEVEL_0 = key(DataGen.resourceLevel(__ZOMBIE_LIKE__, 0));
    ResourceKey<IRandom<Holder<SpawnerEntityType>>> ZOMBIE_LIKE_LEVEL_1 = key(DataGen.resourceLevel(__ZOMBIE_LIKE__, 1));
    ResourceKey<IRandom<Holder<SpawnerEntityType>>> ZOMBIE_LIKE_LEVEL_2 = key(DataGen.resourceLevel(__ZOMBIE_LIKE__, 2));
    ResourceKey<IRandom<Holder<SpawnerEntityType>>> ZOMBIE_LIKE_LEVEL_3 = key(DataGen.resourceLevel(__ZOMBIE_LIKE__, 3));
    ResourceKey<IRandom<Holder<SpawnerEntityType>>> ZOMBIE_LIKE_LEVEL_4 = key(DataGen.resourceLevel(__ZOMBIE_LIKE__, 4));

    private static ResourceKey<IRandom<Holder<SpawnerEntityType>>> key(ResourceLocation identifier) {
        return ResourceKey.create(DatapackRegistries.SPAWNER_ENTITY_TYPE_POOLS, identifier);
    }
}
