package xiroc.dungeoncrawl.data;

import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.storage.loot.LootTable;
import xiroc.dungeoncrawl.datapack.registry.DatapackRegistries;
import xiroc.dungeoncrawl.dungeon.monster.SpawnerEntityType;
import xiroc.dungeoncrawl.dungeon.monster.SpawnerType;
import xiroc.dungeoncrawl.dungeon.theme.PrimaryTheme;
import xiroc.dungeoncrawl.dungeon.theme.SecondaryTheme;
import xiroc.dungeoncrawl.dungeon.type.DungeonType;
import xiroc.dungeoncrawl.dungeon.type.level.LevelType;
import xiroc.dungeoncrawl.util.random.RandomMapping;

public interface SharedKeys {
    interface Loot {
        ResourceLocation __CHESTS = DataGen.resource("chest");

        ResourceKey<LootTable> LEVEL_0 = ResourceKey.create(Registries.LOOT_TABLE, DataGen.tieredResource(__CHESTS, 0));
        ResourceKey<LootTable> LEVEL_1 = ResourceKey.create(Registries.LOOT_TABLE, DataGen.tieredResource(__CHESTS, 1));
        ResourceKey<LootTable> LEVEL_2 = ResourceKey.create(Registries.LOOT_TABLE, DataGen.tieredResource(__CHESTS, 2));
        ResourceKey<LootTable> LEVEL_3 = ResourceKey.create(Registries.LOOT_TABLE, DataGen.tieredResource(__CHESTS, 3));
        ResourceKey<LootTable> LEVEL_4 = ResourceKey.create(Registries.LOOT_TABLE, DataGen.tieredResource(__CHESTS, 4));

        ResourceLocation __BASE = DataGen.subdirectory(__CHESTS, "base");

        ResourceLocation __FOOD = DataGen.subdirectory(__BASE, "food");
        ResourceKey<LootTable> FOOD_LEVEL_0 = ResourceKey.create(Registries.LOOT_TABLE, DataGen.tieredResource(__FOOD, 0));
        ResourceKey<LootTable> FOOD_LEVEL_1 = ResourceKey.create(Registries.LOOT_TABLE, DataGen.tieredResource(__FOOD, 1));
        ResourceKey<LootTable> FOOD_LEVEL_2 = ResourceKey.create(Registries.LOOT_TABLE, DataGen.tieredResource(__FOOD, 2));

        ResourceLocation __BLOCKS = DataGen.subdirectory(__BASE, "blocks");
        ResourceKey<LootTable> BLOCKS_LEVEL_0 = ResourceKey.create(Registries.LOOT_TABLE, DataGen.tieredResource(__BLOCKS, 0));
        ResourceKey<LootTable> BLOCKS_LEVEL_1 = ResourceKey.create(Registries.LOOT_TABLE, DataGen.tieredResource(__BLOCKS, 1));
        ResourceKey<LootTable> BLOCKS_LEVEL_2 = ResourceKey.create(Registries.LOOT_TABLE, DataGen.tieredResource(__BLOCKS, 2));
        ResourceKey<LootTable> BLOCKS_LEVEL_3 = ResourceKey.create(Registries.LOOT_TABLE, DataGen.tieredResource(__BLOCKS, 3));
        ResourceKey<LootTable> BLOCKS_LEVEL_4 = ResourceKey.create(Registries.LOOT_TABLE, DataGen.tieredResource(__BLOCKS, 4));

        ResourceLocation __SCRAP = DataGen.subdirectory(__BASE, "scrap");
        ResourceKey<LootTable> SCRAP_LEVEL_0 = ResourceKey.create(Registries.LOOT_TABLE, DataGen.tieredResource(__SCRAP, 0));
        ResourceKey<LootTable> SCRAP_LEVEL_1 = ResourceKey.create(Registries.LOOT_TABLE, DataGen.tieredResource(__SCRAP, 1));
        ResourceKey<LootTable> SCRAP_LEVEL_2 = ResourceKey.create(Registries.LOOT_TABLE, DataGen.tieredResource(__SCRAP, 2));
        ResourceKey<LootTable> SCRAP_LEVEL_3 = ResourceKey.create(Registries.LOOT_TABLE, DataGen.tieredResource(__SCRAP, 3));
        ResourceKey<LootTable> SCRAP_LEVEL_4 = ResourceKey.create(Registries.LOOT_TABLE, DataGen.tieredResource(__SCRAP, 4));

        ResourceLocation __VALUABLES = DataGen.subdirectory(__BASE, "valuables");
        ResourceKey<LootTable> VALUABLES_LEVEL_0 = ResourceKey.create(Registries.LOOT_TABLE, DataGen.tieredResource(__VALUABLES, 0));
        ResourceKey<LootTable> VALUABLES_LEVEL_1 = ResourceKey.create(Registries.LOOT_TABLE, DataGen.tieredResource(__VALUABLES, 1));
        ResourceKey<LootTable> VALUABLES_LEVEL_2 = ResourceKey.create(Registries.LOOT_TABLE, DataGen.tieredResource(__VALUABLES, 2));
        ResourceKey<LootTable> VALUABLES_LEVEL_3 = ResourceKey.create(Registries.LOOT_TABLE, DataGen.tieredResource(__VALUABLES, 3));
        ResourceKey<LootTable> VALUABLES_LEVEL_4 = ResourceKey.create(Registries.LOOT_TABLE, DataGen.tieredResource(__VALUABLES, 4));

        ResourceLocation __ARMOR = DataGen.subdirectory(__BASE, "armor");
        ResourceKey<LootTable> ARMOR_LEVEL_0 = ResourceKey.create(Registries.LOOT_TABLE, DataGen.tieredResource(__ARMOR, 0));
        ResourceKey<LootTable> ARMOR_LEVEL_1 = ResourceKey.create(Registries.LOOT_TABLE, DataGen.tieredResource(__ARMOR, 1));
        ResourceKey<LootTable> ARMOR_LEVEL_2 = ResourceKey.create(Registries.LOOT_TABLE, DataGen.tieredResource(__ARMOR, 2));
        ResourceKey<LootTable> ARMOR_LEVEL_3 = ResourceKey.create(Registries.LOOT_TABLE, DataGen.tieredResource(__ARMOR, 3));
        ResourceKey<LootTable> ARMOR_LEVEL_4 = ResourceKey.create(Registries.LOOT_TABLE, DataGen.tieredResource(__ARMOR, 4));

        ResourceLocation __WEAPONS = DataGen.subdirectory(__BASE, "weapons");
        ResourceKey<LootTable> WEAPONS_LEVEL_0 = ResourceKey.create(Registries.LOOT_TABLE, DataGen.tieredResource(__WEAPONS, 0));
        ResourceKey<LootTable> WEAPONS_LEVEL_1 = ResourceKey.create(Registries.LOOT_TABLE, DataGen.tieredResource(__WEAPONS, 1));
        ResourceKey<LootTable> WEAPONS_LEVEL_2 = ResourceKey.create(Registries.LOOT_TABLE, DataGen.tieredResource(__WEAPONS, 2));
        ResourceKey<LootTable> WEAPONS_LEVEL_3 = ResourceKey.create(Registries.LOOT_TABLE, DataGen.tieredResource(__WEAPONS, 3));
        ResourceKey<LootTable> WEAPONS_LEVEL_4 = ResourceKey.create(Registries.LOOT_TABLE, DataGen.tieredResource(__WEAPONS, 4));

        interface Specialities {
            ResourceLocation __SPECIALITY = DataGen.subdirectory(__CHESTS, "speciality");

            ResourceKey<LootTable> TEMPERED_BLADE = ResourceKey.create(Registries.LOOT_TABLE, DataGen.resource(__SPECIALITY, "tempered_blade"));

            ResourceKey<LootTable> CASE_HARDENED_PICK = ResourceKey.create(Registries.LOOT_TABLE, DataGen.resource(__SPECIALITY, "case_hardened_pick"));
            ResourceKey<LootTable> CRYSTAL_PICK = ResourceKey.create(Registries.LOOT_TABLE, DataGen.resource(__SPECIALITY, "crystal_pick"));

            ResourceKey<LootTable> WOODLAND_HATCHET = ResourceKey.create(Registries.LOOT_TABLE, DataGen.resource(__SPECIALITY, "woodland_hatchet"));
            ResourceKey<LootTable> CRYSTAL_AXE = ResourceKey.create(Registries.LOOT_TABLE, DataGen.resource(__SPECIALITY, "crystal_axe"));

            ResourceKey<LootTable> GRAVE_SPADE = ResourceKey.create(Registries.LOOT_TABLE, DataGen.resource(__SPECIALITY, "grave_spade"));
            ResourceKey<LootTable> SOUL_SPADE = ResourceKey.create(Registries.LOOT_TABLE, DataGen.resource(__SPECIALITY, "soul_spade"));
        }

        interface Novelties {
            ResourceLocation __NOVELTY = DataGen.subdirectory(__CHESTS, "novelty");
        }
    }

    interface Anchor {
        interface Feature {
            ResourceLocation __FEATURE = DataGen.resource("feature");

            ResourceLocation SPAWNER = DataGen.resource(__FEATURE, "spawner");
            ResourceLocation CHEST = DataGen.resource(__FEATURE, "chest");
            ResourceLocation SARCOPHAGUS = DataGen.resource(__FEATURE, "sarcophagus");
            ResourceLocation FLOWER_POT = DataGen.resource(__FEATURE, "flower_pot");
        }

        ResourceLocation FLOOR = DataGen.resource("floor");
    }

    interface Spawner {
        interface Type {
            ResourceKey<SpawnerType> DEFAULT = ResourceKey.create(DatapackRegistries.SPAWNER_TYPE, DataGen.resource("default"));
        }

        interface EntityType {
            ResourceKey<SpawnerEntityType> ZOMBIE = ResourceKey.create(DatapackRegistries.SPAWNER_ENTITY_TYPE, DataGen.resource("zombie"));
        }
    }

    interface Dungeon {
        ResourceKey<DungeonType> DEFAULT = ResourceKey.create(DatapackRegistries.DUNGEON_TYPE, DataGen.resource("default"));

        interface Level {
            ResourceKey<LevelType> DEFAULT = ResourceKey.create(DatapackRegistries.LEVEL_TYPE, DataGen.resource("default"));
        }
    }

    interface Theme {
        interface Primary {
            ResourceKey<PrimaryTheme> FOREST = ResourceKey.create(DatapackRegistries.PRIMARY_THEME, DataGen.resource("forest"));
        }

        interface Secondary {
            ResourceKey<SecondaryTheme> OAK = ResourceKey.create(DatapackRegistries.SECONDARY_THEME, DataGen.resource("oak"));
        }
    }

    interface ThemeMappings {
        interface Primary {
            ResourceKey<RandomMapping<Biome, PrimaryTheme>> DEFAULT = ResourceKey.create(DatapackRegistries.PRIMARY_THEME_MAPPINGS, DataGen.resource("default"));
        }

        interface Secondary {
            ResourceKey<RandomMapping<Biome, SecondaryTheme>> DEFAULT = ResourceKey.create(DatapackRegistries.SECONDARY_THEME_MAPPINGS, DataGen.resource("default"));
        }
    }

    interface DungeonTypeMappings {
        ResourceKey<RandomMapping<Biome, DungeonType>> DEFAULT = ResourceKey.create(DatapackRegistries.DUNGEON_TYPE_MAPPINGS, DataGen.resource("default"));
    }

}
