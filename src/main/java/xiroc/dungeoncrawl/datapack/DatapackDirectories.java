package xiroc.dungeoncrawl.datapack;

public interface DatapackDirectories {
    DatapackDirectory __DUNGEON__ = new DatapackDirectory("dungeon");
    DatapackDirectory DUNGEON_TYPES = __DUNGEON__.subdirectory("types");
    DatapackDirectory DUNGEON_LAYER = __DUNGEON__.subdirectory("layer");
    DatapackDirectory DUNGEON_LAYER_TYPES = DUNGEON_LAYER.subdirectory("types");

    DatapackDirectory BLUEPRINTS = new DatapackDirectory("blueprints");

    DatapackDirectory __THEMES__ = new DatapackDirectory("themes");
    DatapackDirectory PRIMARY_THEMES = __THEMES__.subdirectory("primary");
    DatapackDirectory SECONDARY_THEMES = __THEMES__.subdirectory("secondary");

    DatapackDirectory __SPAWNER__ = new DatapackDirectory("spawner");
    DatapackDirectory SPAWNER_TYPES = __SPAWNER__.subdirectory("type");
    DatapackDirectory SPAWNER_ENTITIES = __SPAWNER__.subdirectory("entity");
    DatapackDirectory SPAWNER_ENTITY_PROPERTIES = __SPAWNER__.subdirectory("entity_profile");

    DatapackDirectory __MAPPINGS__ = new DatapackDirectory("mappings");
    DatapackDirectory PRIMARY_THEME_MAPPINGS = __MAPPINGS__.subdirectory("primary_theme");
    DatapackDirectory SECONDARY_THEME_MAPPINGS = __MAPPINGS__.subdirectory("secondary_theme");
    DatapackDirectory DUNGEON_TYPE_MAPPINGS = __MAPPINGS__.subdirectory("dungeon_type");

    DatapackDirectory __POOLS__ = new DatapackDirectory("pool");
    DatapackDirectory BLOCK_STATE_POOLS = __POOLS__.subdirectory("block");
    DatapackDirectory BLUEPRINT_POOLS = __POOLS__.subdirectory("blueprint");
    DatapackDirectory DUNGEON_TYPE_POOLS = __POOLS__.subdirectory("dungeon_type");
    DatapackDirectory ITEM_POOLS = __POOLS__.subdirectory("item");

    DatapackDirectory __THEME_POOLS__ = __POOLS__.subdirectory("theme");
    DatapackDirectory PRIMARY_THEME_POOLS = __THEME_POOLS__.subdirectory("primary");
    DatapackDirectory SECONDARY_THEME_POOLS = __THEME_POOLS__.subdirectory("theme");

    DatapackDirectory __SPAWNER_POOLS__ = __POOLS__.subdirectory("spawner");
    DatapackDirectory SPAWNER_ENTITY_TYPE_POOLS = __SPAWNER_POOLS__.subdirectory("entity");
    DatapackDirectory SPAWNER_TYPE_POOLS = __SPAWNER_POOLS__.subdirectory("type");
}
