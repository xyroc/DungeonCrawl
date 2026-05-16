package xiroc.dungeoncrawl.data.spawner;

import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import xiroc.dungeoncrawl.data.DataGen;
import xiroc.dungeoncrawl.datapack.registry.DatapackRegistries;
import xiroc.dungeoncrawl.dungeon.monster.SpawnerEntityProperties;

public interface SpawnerEntityProfileKeys {
    ResourceLocation __ARCHER__ = DataGen.resource("archer");
    ResourceKey<SpawnerEntityProperties> ARCHER_LEVEL_0 = key(DataGen.resourceLevel(__ARCHER__, 0));
    ResourceKey<SpawnerEntityProperties> ARCHER_LEVEL_1 = key(DataGen.resourceLevel(__ARCHER__, 1));
    ResourceKey<SpawnerEntityProperties> ARCHER_LEVEL_2 = key(DataGen.resourceLevel(__ARCHER__, 2));
    ResourceKey<SpawnerEntityProperties> ARCHER_LEVEL_3 = key(DataGen.resourceLevel(__ARCHER__, 3));
    ResourceKey<SpawnerEntityProperties> ARCHER_LEVEL_4 = key(DataGen.resourceLevel(__ARCHER__, 4));

    ResourceLocation __ARMORED__ = DataGen.resource("armored");
    ResourceKey<SpawnerEntityProperties> ARMORED_LEVEL_0 = key(DataGen.resourceLevel(__ARMORED__, 0));
    ResourceKey<SpawnerEntityProperties> ARMORED_LEVEL_1 = key(DataGen.resourceLevel(__ARMORED__, 1));
    ResourceKey<SpawnerEntityProperties> ARMORED_LEVEL_2 = key(DataGen.resourceLevel(__ARMORED__, 2));
    ResourceKey<SpawnerEntityProperties> ARMORED_LEVEL_3 = key(DataGen.resourceLevel(__ARMORED__, 3));
    ResourceKey<SpawnerEntityProperties> ARMORED_LEVEL_4 = key(DataGen.resourceLevel(__ARMORED__, 4));

    ResourceLocation __MELEE__ = DataGen.resource("melee");
    ResourceKey<SpawnerEntityProperties> MELEE_LEVEL_0 = key(DataGen.resourceLevel(__MELEE__, 0));
    ResourceKey<SpawnerEntityProperties> MELEE_LEVEL_1 = key(DataGen.resourceLevel(__MELEE__, 1));
    ResourceKey<SpawnerEntityProperties> MELEE_LEVEL_2 = key(DataGen.resourceLevel(__MELEE__, 2));
    ResourceKey<SpawnerEntityProperties> MELEE_LEVEL_3 = key(DataGen.resourceLevel(__MELEE__, 3));
    ResourceKey<SpawnerEntityProperties> MELEE_LEVEL_4 = key(DataGen.resourceLevel(__MELEE__, 4));

    ResourceLocation __SHIELD_BEARER__ = DataGen.resource("shield_bearer");
    ResourceKey<SpawnerEntityProperties> SHIELD_BEARER_LEVEL_0 = key(DataGen.resourceLevel(__SHIELD_BEARER__, 0));
    ResourceKey<SpawnerEntityProperties> SHIELD_BEARER_LEVEL_1 = key(DataGen.resourceLevel(__SHIELD_BEARER__, 1));
    ResourceKey<SpawnerEntityProperties> SHIELD_BEARER_LEVEL_2 = key(DataGen.resourceLevel(__SHIELD_BEARER__, 2));
    ResourceKey<SpawnerEntityProperties> SHIELD_BEARER_LEVEL_3 = key(DataGen.resourceLevel(__SHIELD_BEARER__, 3));
    ResourceKey<SpawnerEntityProperties> SHIELD_BEARER_LEVEL_4 = key(DataGen.resourceLevel(__SHIELD_BEARER__, 4));

    private static ResourceKey<SpawnerEntityProperties> key(ResourceLocation identifier) {
        return ResourceKey.create(DatapackRegistries.SPAWNER_ENTITY_PROPERTIES, identifier);
    }
}
