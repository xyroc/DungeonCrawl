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

    ResourceLocation __DROWNED__ = DataGen.resource("drowned");
    ResourceKey<SpawnerEntityType> DROWNED_LEVEL_0 = key(DataGen.resourceLevel(__DROWNED__, 0));
    ResourceKey<SpawnerEntityType> DROWNED_LEVEL_1 = key(DataGen.resourceLevel(__DROWNED__, 1));
    ResourceKey<SpawnerEntityType> DROWNED_LEVEL_2 = key(DataGen.resourceLevel(__DROWNED__, 2));
    ResourceKey<SpawnerEntityType> DROWNED_LEVEL_3 = key(DataGen.resourceLevel(__DROWNED__, 3));
    ResourceKey<SpawnerEntityType> DROWNED_LEVEL_4 = key(DataGen.resourceLevel(__DROWNED__, 4));

    ResourceLocation __ZOMBIE__ = DataGen.resource("zombie");
    ResourceKey<SpawnerEntityType> ZOMBIE_LEVEL_0 = key(DataGen.resourceLevel(__ZOMBIE__, 0));
    ResourceKey<SpawnerEntityType> ZOMBIE_LEVEL_1 = key(DataGen.resourceLevel(__ZOMBIE__, 1));
    ResourceKey<SpawnerEntityType> ZOMBIE_LEVEL_2 = key(DataGen.resourceLevel(__ZOMBIE__, 2));
    ResourceKey<SpawnerEntityType> ZOMBIE_LEVEL_3 = key(DataGen.resourceLevel(__ZOMBIE__, 3));
    ResourceKey<SpawnerEntityType> ZOMBIE_LEVEL_4 = key(DataGen.resourceLevel(__ZOMBIE__, 4));

     ResourceLocation __SKELETON__ = DataGen.resource("skeleton");
    ResourceLocation __SKELETON_ARCHER__ = DataGen.resource(__SKELETON__, "archer");
    ResourceKey<SpawnerEntityType> SKELETON_ARCHER_LEVEL_0 = key(DataGen.resourceLevel(__SKELETON_ARCHER__, 0));
    ResourceKey<SpawnerEntityType> SKELETON_ARCHER_LEVEL_1 = key(DataGen.resourceLevel(__SKELETON_ARCHER__, 1));
    ResourceKey<SpawnerEntityType> SKELETON_ARCHER_LEVEL_2 = key(DataGen.resourceLevel(__SKELETON_ARCHER__, 2));
    ResourceKey<SpawnerEntityType> SKELETON_ARCHER_LEVEL_3 = key(DataGen.resourceLevel(__SKELETON_ARCHER__, 3));
    ResourceKey<SpawnerEntityType> SKELETON_ARCHER_LEVEL_4 = key(DataGen.resourceLevel(__SKELETON_ARCHER__, 4));
    ResourceLocation __SKELETON_WARRIOR__ = DataGen.resource(__SKELETON__, "warrior");
    ResourceKey<SpawnerEntityType> SKELETON_WARRIOR_LEVEL_0 = key(DataGen.resourceLevel(__SKELETON_WARRIOR__, 0));
    ResourceKey<SpawnerEntityType> SKELETON_WARRIOR_LEVEL_1 = key(DataGen.resourceLevel(__SKELETON_WARRIOR__, 1));
    ResourceKey<SpawnerEntityType> SKELETON_WARRIOR_LEVEL_2 = key(DataGen.resourceLevel(__SKELETON_WARRIOR__, 2));
    ResourceKey<SpawnerEntityType> SKELETON_WARRIOR_LEVEL_3 = key(DataGen.resourceLevel(__SKELETON_WARRIOR__, 3));
    ResourceKey<SpawnerEntityType> SKELETON_WARRIOR_LEVEL_4 = key(DataGen.resourceLevel(__SKELETON_WARRIOR__, 4));

    private static ResourceKey<SpawnerEntityType> key(ResourceLocation identifier) {
        return ResourceKey.create(DatapackRegistries.SPAWNER_ENTITY_TYPE, identifier);
    }
}
