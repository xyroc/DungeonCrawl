package xiroc.dungeoncrawl.data;

import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.biome.Biome;
import xiroc.dungeoncrawl.datapack.registry.DatapackRegistries;
import xiroc.dungeoncrawl.dungeon.monster.SpawnerEntityType;
import xiroc.dungeoncrawl.dungeon.monster.SpawnerType;
import xiroc.dungeoncrawl.dungeon.theme.PrimaryTheme;
import xiroc.dungeoncrawl.dungeon.theme.SecondaryTheme;
import xiroc.dungeoncrawl.dungeon.type.DungeonType;
import xiroc.dungeoncrawl.dungeon.type.level.LevelType;
import xiroc.dungeoncrawl.util.random.RandomMapping;

public interface SharedKeys {

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
