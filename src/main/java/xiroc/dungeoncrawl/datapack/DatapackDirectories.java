package xiroc.dungeoncrawl.datapack;

public interface DatapackDirectories {
    DatapackDirectory BASE = new DatapackDirectory("roguelike");

    DatapackDirectory DUNGEON = BASE.subdirectory("dungeon");
    DatapackDirectory DUNGEON_TYPES = DUNGEON.subdirectory("types");
    DatapackDirectory DUNGEON_LAYER = DUNGEON.subdirectory("layer");
    DatapackDirectory DUNGEON_LAYER_TYPES = DUNGEON_LAYER.subdirectory("types");

    DatapackDirectory BLUEPRINTS = BASE.subdirectory("blueprints");

    DatapackDirectory THEMES = BASE.subdirectory("themes");
    DatapackDirectory PRIMARY_THEMES = THEMES.subdirectory("primary");
    DatapackDirectory SECONDARY_THEMES = THEMES.subdirectory("secondary");

    DatapackDirectory SPAWNER = BASE.subdirectory("spawner");
    DatapackDirectory SPAWNER_TYPES = SPAWNER.subdirectory("types");
    DatapackDirectory SPAWNER_ENTITIES = SPAWNER.subdirectory("entities");
    DatapackDirectory SPAWNER_ENTITY_PROPERTIES = SPAWNER.subdirectory("profiles");

    DatapackDirectory MAPPINGS = BASE.subdirectory("mappings");
    DatapackDirectory TYPE_MAPPINGS = MAPPINGS.subdirectory("types");
    DatapackDirectory DUNGEON_TYPE_MAPPINGS = TYPE_MAPPINGS.subdirectory("dungeon");
    DatapackDirectory THEME_MAPPINGS = MAPPINGS.subdirectory("themes");
    DatapackDirectory PRIMARY_THEME_MAPPINGS = THEME_MAPPINGS.subdirectory("primary");
    DatapackDirectory SECONDARY_THEME_MAPPINGS = THEME_MAPPINGS.subdirectory("secondary");
}
