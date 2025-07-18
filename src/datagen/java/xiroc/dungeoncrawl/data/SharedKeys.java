package xiroc.dungeoncrawl.data;

import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.storage.loot.LootTable;
import xiroc.dungeoncrawl.datapack.DatapackNamespaces;
import xiroc.dungeoncrawl.datapack.registry.DatapackRegistries;
import xiroc.dungeoncrawl.dungeon.blueprint.Blueprint;
import xiroc.dungeoncrawl.dungeon.monster.SpawnerEntityType;
import xiroc.dungeoncrawl.dungeon.monster.SpawnerType;
import xiroc.dungeoncrawl.dungeon.theme.PrimaryTheme;
import xiroc.dungeoncrawl.dungeon.theme.SecondaryTheme;
import xiroc.dungeoncrawl.dungeon.type.DungeonType;
import xiroc.dungeoncrawl.dungeon.type.level.LevelType;
import xiroc.dungeoncrawl.util.random.RandomMapping;

public interface SharedKeys {
    interface Loot {
        ResourceLocation __CHESTS = resource("chest");

        ResourceKey<LootTable> LEVEL_0 = ResourceKey.create(Registries.LOOT_TABLE, tiered(__CHESTS, 0));
        ResourceKey<LootTable> LEVEL_1 = ResourceKey.create(Registries.LOOT_TABLE, tiered(__CHESTS, 1));
        ResourceKey<LootTable> LEVEL_2 = ResourceKey.create(Registries.LOOT_TABLE, tiered(__CHESTS, 2));
        ResourceKey<LootTable> LEVEL_3 = ResourceKey.create(Registries.LOOT_TABLE, tiered(__CHESTS, 3));
        ResourceKey<LootTable> LEVEL_4 = ResourceKey.create(Registries.LOOT_TABLE, tiered(__CHESTS, 4));

        ResourceLocation __BASE = subdirectory(__CHESTS, "base");

        ResourceLocation __FOOD = subdirectory(__BASE, "food");
        ResourceKey<LootTable> FOOD_LEVEL_0 = ResourceKey.create(Registries.LOOT_TABLE, tiered(__FOOD, 0));
        ResourceKey<LootTable> FOOD_LEVEL_1 = ResourceKey.create(Registries.LOOT_TABLE, tiered(__FOOD, 1));
        ResourceKey<LootTable> FOOD_LEVEL_2 = ResourceKey.create(Registries.LOOT_TABLE, tiered(__FOOD, 2));

        ResourceLocation __BLOCKS = subdirectory(__BASE, "blocks");
        ResourceKey<LootTable> BLOCKS_LEVEL_0 = ResourceKey.create(Registries.LOOT_TABLE, tiered(__BLOCKS, 0));
        ResourceKey<LootTable> BLOCKS_LEVEL_1 = ResourceKey.create(Registries.LOOT_TABLE, tiered(__BLOCKS, 1));
        ResourceKey<LootTable> BLOCKS_LEVEL_2 = ResourceKey.create(Registries.LOOT_TABLE, tiered(__BLOCKS, 2));
        ResourceKey<LootTable> BLOCKS_LEVEL_3 = ResourceKey.create(Registries.LOOT_TABLE, tiered(__BLOCKS, 3));
        ResourceKey<LootTable> BLOCKS_LEVEL_4 = ResourceKey.create(Registries.LOOT_TABLE, tiered(__BLOCKS, 4));

        ResourceLocation __SCRAP = subdirectory(__BASE, "scrap");
        ResourceKey<LootTable> SCRAP_LEVEL_0 = ResourceKey.create(Registries.LOOT_TABLE, tiered(__SCRAP, 0));
        ResourceKey<LootTable> SCRAP_LEVEL_1 = ResourceKey.create(Registries.LOOT_TABLE, tiered(__SCRAP, 1));
        ResourceKey<LootTable> SCRAP_LEVEL_2 = ResourceKey.create(Registries.LOOT_TABLE, tiered(__SCRAP, 2));
        ResourceKey<LootTable> SCRAP_LEVEL_3 = ResourceKey.create(Registries.LOOT_TABLE, tiered(__SCRAP, 3));
        ResourceKey<LootTable> SCRAP_LEVEL_4 = ResourceKey.create(Registries.LOOT_TABLE, tiered(__SCRAP, 4));

        ResourceLocation __VALUABLES = subdirectory(__BASE, "valuables");
        ResourceKey<LootTable> VALUABLES_LEVEL_0 = ResourceKey.create(Registries.LOOT_TABLE, tiered(__VALUABLES, 0));
        ResourceKey<LootTable> VALUABLES_LEVEL_1 = ResourceKey.create(Registries.LOOT_TABLE, tiered(__VALUABLES, 1));
        ResourceKey<LootTable> VALUABLES_LEVEL_2 = ResourceKey.create(Registries.LOOT_TABLE, tiered(__VALUABLES, 2));
        ResourceKey<LootTable> VALUABLES_LEVEL_3 = ResourceKey.create(Registries.LOOT_TABLE, tiered(__VALUABLES, 3));
        ResourceKey<LootTable> VALUABLES_LEVEL_4 = ResourceKey.create(Registries.LOOT_TABLE, tiered(__VALUABLES, 4));

        ResourceLocation __ARMOR = subdirectory(__BASE, "armor");
        ResourceKey<LootTable> ARMOR_LEVEL_0 = ResourceKey.create(Registries.LOOT_TABLE, tiered(__ARMOR, 0));
        ResourceKey<LootTable> ARMOR_LEVEL_1 = ResourceKey.create(Registries.LOOT_TABLE, tiered(__ARMOR, 1));
        ResourceKey<LootTable> ARMOR_LEVEL_2 = ResourceKey.create(Registries.LOOT_TABLE, tiered(__ARMOR, 2));
        ResourceKey<LootTable> ARMOR_LEVEL_3 = ResourceKey.create(Registries.LOOT_TABLE, tiered(__ARMOR, 3));
        ResourceKey<LootTable> ARMOR_LEVEL_4 = ResourceKey.create(Registries.LOOT_TABLE, tiered(__ARMOR, 4));

        ResourceLocation __WEAPONS = subdirectory(__BASE, "weapons");
        ResourceKey<LootTable> WEAPONS_LEVEL_0 = ResourceKey.create(Registries.LOOT_TABLE, tiered(__WEAPONS, 0));
        ResourceKey<LootTable> WEAPONS_LEVEL_1 = ResourceKey.create(Registries.LOOT_TABLE, tiered(__WEAPONS, 1));
        ResourceKey<LootTable> WEAPONS_LEVEL_2 = ResourceKey.create(Registries.LOOT_TABLE, tiered(__WEAPONS, 2));
        ResourceKey<LootTable> WEAPONS_LEVEL_3 = ResourceKey.create(Registries.LOOT_TABLE, tiered(__WEAPONS, 3));
        ResourceKey<LootTable> WEAPONS_LEVEL_4 = ResourceKey.create(Registries.LOOT_TABLE, tiered(__WEAPONS, 4));

        interface Specialities {
            ResourceLocation __SPECIALITY = subdirectory(__CHESTS, "speciality");

            ResourceKey<LootTable> TEMPERED_BLADE = ResourceKey.create(Registries.LOOT_TABLE, resource(__SPECIALITY, "tempered_blade"));

            ResourceKey<LootTable> CASE_HARDENED_PICK = ResourceKey.create(Registries.LOOT_TABLE, resource(__SPECIALITY, "case_hardened_pick"));
            ResourceKey<LootTable> CRYSTAL_PICK = ResourceKey.create(Registries.LOOT_TABLE, resource(__SPECIALITY, "crystal_pick"));

            ResourceKey<LootTable> WOODLAND_HATCHET = ResourceKey.create(Registries.LOOT_TABLE, resource(__SPECIALITY, "woodland_hatchet"));
            ResourceKey<LootTable> CRYSTAL_AXE = ResourceKey.create(Registries.LOOT_TABLE, resource(__SPECIALITY, "crystal_axe"));

            ResourceKey<LootTable> GRAVE_SPADE = ResourceKey.create(Registries.LOOT_TABLE, resource(__SPECIALITY, "grave_spade"));
            ResourceKey<LootTable> SOUL_SPADE = ResourceKey.create(Registries.LOOT_TABLE, resource(__SPECIALITY, "soul_spade"));
        }

        interface Novelties {
            ResourceLocation __NOVELTY = subdirectory(__CHESTS, "novelty");
        }
    }

    interface Anchor {
        interface Feature {
            ResourceLocation __FEATURE = resource("feature");

            ResourceLocation SPAWNER = resource(__FEATURE, "spawner");
            ResourceLocation CHEST = resource(__FEATURE, "chest");
            ResourceLocation SARCOPHAGUS = resource(__FEATURE, "sarcophagus");
            ResourceLocation FLOWER_POT = resource(__FEATURE, "flower_pot");
        }

        ResourceLocation FLOOR = resource("floor");
    }

    interface Template {
        interface Entrance {
            ResourceLocation __ENTRANCE = resource("entrance");

            ResourceLocation ENIKO_TOWER = resource(__ENTRANCE, "eniko_tower");
        }

        interface Room {
            ResourceLocation __ROOM = resource("room");

            ResourceLocation DARK_HALL = resource(__ROOM, "dark_hall");
            ResourceLocation ENIKO = resource(__ROOM, "eniko");
            ResourceLocation LIBRARY = resource(__ROOM, "library");
            ResourceLocation LOWER_STAIRCASE = resource(__ROOM, "lower_staircase");
            ResourceLocation SARCOPHAGUS = resource(__ROOM, "sarcophagus");
            ResourceLocation SMITHY = resource(__ROOM, "smithy");
            ResourceLocation UPPER_STAIRCASE = resource(__ROOM, "upper_staircase");
        }

        interface Part {
            ResourceLocation __PART = resource("part");

            ResourceLocation __FLOOR = resource(__PART, "floor");
            ResourceLocation FLOOR_3X3 = resource(__FLOOR, "3x3");
            ResourceLocation FLOOR_5x5 = resource(__FLOOR, "5x5");

            ResourceLocation LIBRARY_ENTRANCE = resource(Room.LIBRARY, "entrance");
            ResourceLocation LIBRARY_DESK = resource(Room.LIBRARY, "desk");
            ResourceLocation LIBRARY_FLOWERS = resource(Room.LIBRARY, "flowers");

            ResourceLocation DARK_HALL_OPEN = resource(Room.DARK_HALL, "open");
            ResourceLocation DARK_HALL_CLOSED = resource(Room.DARK_HALL, "closed");
        }

        interface Corridor {
            ResourceLocation __CORRIDOR = resource("corridor");

            interface Side {
                ResourceLocation __SIDE = subdirectory(__CORRIDOR, "side");

                ResourceLocation BASE = resource(__SIDE, "base");
                ResourceLocation CROPS = resource(__SIDE, "crops");
                ResourceLocation DOOR = resource(__SIDE, "door");
                ResourceLocation FIRE = resource(__SIDE, "fire");
                ResourceLocation FLOWER_POT = resource(__SIDE, "flower_pot");
                ResourceLocation MASONRY = resource(__SIDE, "masonry");
            }

            interface Segment {
                ResourceLocation __SEGMENT = subdirectory(__CORRIDOR, "segment");

                ResourceLocation ARCH = resource(__SEGMENT, "arch");
                ResourceLocation BASE = resource(__SEGMENT, "base");
            }
        }

    }

    interface Blueprints {
        interface Entrance {
            ResourceKey<Blueprint> ENIKO_TOWER = ResourceKey.create(DatapackRegistries.BLUEPRINT, Template.Entrance.ENIKO_TOWER);
        }

        interface Room {
            ResourceKey<Blueprint> DARK_HALL = ResourceKey.create(DatapackRegistries.BLUEPRINT, Template.Room.DARK_HALL);
            ResourceKey<Blueprint> ENIKO = ResourceKey.create(DatapackRegistries.BLUEPRINT, Template.Room.ENIKO);
            ResourceKey<Blueprint> LIBRARY = ResourceKey.create(DatapackRegistries.BLUEPRINT, Template.Room.LIBRARY);
            ResourceKey<Blueprint> LOWER_STAIRCASE = ResourceKey.create(DatapackRegistries.BLUEPRINT, Template.Room.LOWER_STAIRCASE);
            ResourceKey<Blueprint> SARCOPHAGUS = ResourceKey.create(DatapackRegistries.BLUEPRINT, Template.Room.SARCOPHAGUS);
            ResourceKey<Blueprint> SMITHY = ResourceKey.create(DatapackRegistries.BLUEPRINT, Template.Room.SMITHY);
            ResourceKey<Blueprint> UPPER_STAIRCASE = ResourceKey.create(DatapackRegistries.BLUEPRINT, Template.Room.UPPER_STAIRCASE);
        }

        interface Part {
            ResourceLocation FLOOR_3X3 = Template.Part.FLOOR_3X3;
            ResourceKey<Blueprint> FLOOR_3x3_MASONRY = ResourceKey.create(DatapackRegistries.BLUEPRINT, resource(Template.Part.FLOOR_3X3, "masonry"));
            ResourceKey<Blueprint> FLOOR_3x3_SOLID = ResourceKey.create(DatapackRegistries.BLUEPRINT, resource(Template.Part.FLOOR_3X3, "solid"));

            ResourceKey<Blueprint> FLOOR_5x5_SOLID = ResourceKey.create(DatapackRegistries.BLUEPRINT, resource(Template.Part.FLOOR_5x5, "solid"));
            ResourceKey<Blueprint> FLOOR_5x5_FRAGILE = ResourceKey.create(DatapackRegistries.BLUEPRINT, resource(Template.Part.FLOOR_5x5, "fragile"));

            ResourceKey<Blueprint> LIBRARY_ENTRANCE = ResourceKey.create(DatapackRegistries.BLUEPRINT, Template.Part.LIBRARY_ENTRANCE);
            ResourceKey<Blueprint> LIBRARY_DESK = ResourceKey.create(DatapackRegistries.BLUEPRINT, Template.Part.LIBRARY_DESK);
            ResourceKey<Blueprint> LIBRARY_FLOWERS = ResourceKey.create(DatapackRegistries.BLUEPRINT, Template.Part.LIBRARY_FLOWERS);

            ResourceKey<Blueprint> DARK_HALL_OPEN = ResourceKey.create(DatapackRegistries.BLUEPRINT, Template.Part.DARK_HALL_OPEN);
            ResourceKey<Blueprint> DARK_HALL_CLOSED = ResourceKey.create(DatapackRegistries.BLUEPRINT, Template.Part.DARK_HALL_CLOSED);
        }

        interface Corridor {
            interface Segment {
                ResourceKey<Blueprint> ARCH = ResourceKey.create(DatapackRegistries.BLUEPRINT, Template.Corridor.Segment.ARCH);
                ResourceKey<Blueprint> BASE = ResourceKey.create(DatapackRegistries.BLUEPRINT, Template.Corridor.Segment.BASE);
            }

            interface Side {
                ResourceKey<Blueprint> BASE = ResourceKey.create(DatapackRegistries.BLUEPRINT, Template.Corridor.Side.BASE);
                ResourceKey<Blueprint> CROPS = ResourceKey.create(DatapackRegistries.BLUEPRINT, Template.Corridor.Side.CROPS);
                ResourceKey<Blueprint> DOOR = ResourceKey.create(DatapackRegistries.BLUEPRINT, Template.Corridor.Side.DOOR);
                ResourceKey<Blueprint> FIRE = ResourceKey.create(DatapackRegistries.BLUEPRINT, Template.Corridor.Side.FIRE);
                ResourceKey<Blueprint> FLOWER_POT = ResourceKey.create(DatapackRegistries.BLUEPRINT, Template.Corridor.Side.FLOWER_POT);
                ResourceKey<Blueprint> MASONRY = ResourceKey.create(DatapackRegistries.BLUEPRINT, Template.Corridor.Side.MASONRY);
            }
        }
    }

    interface Spawner {
        interface Type {
            ResourceKey<SpawnerType> DEFAULT = ResourceKey.create(DatapackRegistries.SPAWNER_TYPE, resource("default"));
        }

        interface EntityType {
            ResourceKey<SpawnerEntityType> ZOMBIE = ResourceKey.create(DatapackRegistries.SPAWNER_ENTITY_TYPE, resource("zombie"));
        }
    }

    interface Dungeon {
        ResourceKey<DungeonType> DEFAULT = ResourceKey.create(DatapackRegistries.DUNGEON_TYPE, resource("default"));

        interface Level {
            ResourceKey<LevelType> DEFAULT = ResourceKey.create(DatapackRegistries.LEVEL_TYPE, resource("default"));
        }
    }

    interface Theme {
        interface Primary {
            ResourceKey<PrimaryTheme> FOREST = ResourceKey.create(DatapackRegistries.PRIMARY_THEME, resource("forest"));
        }

        interface Secondary {
            ResourceKey<SecondaryTheme> OAK = ResourceKey.create(DatapackRegistries.SECONDARY_THEME, resource("oak"));
        }
    }

    interface ThemeMappings {
        interface Primary {
            ResourceKey<RandomMapping<Biome, PrimaryTheme>> DEFAULT = ResourceKey.create(DatapackRegistries.PRIMARY_THEME_MAPPINGS, resource("default"));
        }

        interface Secondary {
            ResourceKey<RandomMapping<Biome, SecondaryTheme>> DEFAULT = ResourceKey.create(DatapackRegistries.SECONDARY_THEME_MAPPINGS, resource("default"));
        }
    }

    interface DungeonTypeMappings {
        ResourceKey<RandomMapping<Biome, DungeonType>> DEFAULT = ResourceKey.create(DatapackRegistries.DUNGEON_TYPE_MAPPINGS, resource("default"));
    }

    private static ResourceLocation resource(String path) {
        return ResourceLocation.fromNamespaceAndPath(DatapackNamespaces.DEFAULT, path);
    }

    private static ResourceLocation resource(ResourceLocation directory, String name) {
        return ResourceLocation.fromNamespaceAndPath(directory.getNamespace(), directory.getPath() + "/" + name);
    }

    private static ResourceLocation tiered(ResourceLocation directory, int tier) {
        return ResourceLocation.fromNamespaceAndPath(directory.getNamespace(), directory.getPath() + "/level_" + tier);
    }

    private static ResourceLocation subdirectory(ResourceLocation directory, String subdirectory) {
        return ResourceLocation.fromNamespaceAndPath(directory.getNamespace(), directory.getPath() + "/" + subdirectory);
    }
}
