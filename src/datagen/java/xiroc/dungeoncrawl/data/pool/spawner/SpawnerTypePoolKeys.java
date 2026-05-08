package xiroc.dungeoncrawl.data.pool.spawner;

import net.minecraft.core.Holder;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import xiroc.dungeoncrawl.data.DataGen;
import xiroc.dungeoncrawl.datapack.registry.DatapackRegistries;
import xiroc.dungeoncrawl.dungeon.monster.SpawnerType;
import xiroc.dungeoncrawl.util.random.IRandom;

public interface SpawnerTypePoolKeys {
    ResourceKey<IRandom<Holder<SpawnerType>>> LEVEL_O = key(DataGen.resourceLevel(0));
    ResourceKey<IRandom<Holder<SpawnerType>>> LEVEL_1 = key(DataGen.resourceLevel(1));
    ResourceKey<IRandom<Holder<SpawnerType>>> LEVEL_2 = key(DataGen.resourceLevel(2));
    ResourceKey<IRandom<Holder<SpawnerType>>> LEVEL_3 = key(DataGen.resourceLevel(3));
    ResourceKey<IRandom<Holder<SpawnerType>>> LEVEL_4 = key(DataGen.resourceLevel(4));

    private static ResourceKey<IRandom<Holder<SpawnerType>>> key(ResourceLocation identifier) {
        return ResourceKey.create(DatapackRegistries.SPAWNER_TYPE_POOLS, identifier);
    }
}
