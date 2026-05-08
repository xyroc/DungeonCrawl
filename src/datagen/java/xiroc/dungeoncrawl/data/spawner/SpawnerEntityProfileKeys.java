package xiroc.dungeoncrawl.data.spawner;

import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import xiroc.dungeoncrawl.data.DataGen;
import xiroc.dungeoncrawl.datapack.registry.DatapackRegistries;
import xiroc.dungeoncrawl.dungeon.monster.SpawnerEntityProperties;

public interface SpawnerEntityProfileKeys {
    ResourceLocation __MELEE_UNIT__ = DataGen.resource("melee_unit");
    ResourceKey<SpawnerEntityProperties> MELEE_UNIT_LEVEL_0 = key(DataGen.resourceLevel(__MELEE_UNIT__, 0));
    ResourceKey<SpawnerEntityProperties> MELEE_UNIT_LEVEL_1 = key(DataGen.resourceLevel(__MELEE_UNIT__, 1));
    ResourceKey<SpawnerEntityProperties> MELEE_UNIT_LEVEL_2 = key(DataGen.resourceLevel(__MELEE_UNIT__, 2));
    ResourceKey<SpawnerEntityProperties> MELEE_UNIT_LEVEL_3 = key(DataGen.resourceLevel(__MELEE_UNIT__, 3));
    ResourceKey<SpawnerEntityProperties> MELEE_UNIT_LEVEL_4 = key(DataGen.resourceLevel(__MELEE_UNIT__, 4));

    private static ResourceKey<SpawnerEntityProperties> key(ResourceLocation identifier) {
        return ResourceKey.create(DatapackRegistries.SPAWNER_ENTITY_PROPERTIES, identifier);
    }
}
