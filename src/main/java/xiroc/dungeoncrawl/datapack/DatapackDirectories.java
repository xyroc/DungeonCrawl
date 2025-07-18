package xiroc.dungeoncrawl.datapack;

public interface DatapackDirectories {
    DatapackDirectory DUNGEON = new DatapackDirectory("dungeon");
    DatapackDirectory DUNGEON_TYPES = DUNGEON.subdirectory("types");
    DatapackDirectory DUNGEON_LAYER = DUNGEON.subdirectory("layer");
    DatapackDirectory DUNGEON_LAYER_TYPES = DUNGEON_LAYER.subdirectory("types");

    DatapackDirectory BLUEPRINTS = new DatapackDirectory("blueprints");

    DatapackDirectory THEMES = new DatapackDirectory("themes");
    DatapackDirectory PRIMARY_THEMES = THEMES.subdirectory("primary");
    DatapackDirectory SECONDARY_THEMES = THEMES.subdirectory("secondary");

    DatapackDirectory SPAWNER = new DatapackDirectory("spawner");
    DatapackDirectory SPAWNER_TYPES = SPAWNER.subdirectory("types");
    DatapackDirectory SPAWNER_ENTITIES = SPAWNER.subdirectory("entities");
    DatapackDirectory SPAWNER_ENTITY_PROPERTIES = SPAWNER.subdirectory("profiles");

    DatapackDirectory MAPPINGS = new DatapackDirectory("mappings");
    DatapackDirectory PRIMARY_THEME_MAPPINGS = MAPPINGS.subdirectory("primary_theme");
    DatapackDirectory SECONDARY_THEME_MAPPINGS = MAPPINGS.subdirectory("secondary_theme");
    DatapackDirectory DUNGEON_TYPE_MAPPINGS = MAPPINGS.subdirectory("dungeon_type");

    DatapackDirectory POOLS = new DatapackDirectory("pools");
    DatapackDirectory BLUEPRINT_POOLS = POOLS.subdirectory("blueprint");
    DatapackDirectory DUNGEON_TYPE_POOLS = POOLS.subdirectory("dungeon_type");
    DatapackDirectory PRIMARY_THEME_POOLS = POOLS.subdirectory("primary_theme");
    DatapackDirectory SECONDARY_THEME_POOLS = POOLS.subdirectory("secondary_theme");
}
