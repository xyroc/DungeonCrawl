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

    private static ResourceKey<IRandom<Holder<SpawnerEntityType>>> key(ResourceLocation identifier) {
        return ResourceKey.create(DatapackRegistries.SPAWNER_ENTITY_TYPE_POOLS, identifier);
    }
}
