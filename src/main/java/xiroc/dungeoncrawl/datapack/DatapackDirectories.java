package xiroc.dungeoncrawl.datapack;

import net.minecraft.resources.ResourceLocation;

public interface DatapackDirectories {
    Directory BASE = new Directory("roguelike");

    Directory DUNGEON = BASE.subdirectory("dungeon");
    Directory DUNGEON_TYPES = DUNGEON.subdirectory("types");
    Directory DUNGEON_LAYER = DUNGEON.subdirectory("layer");
    Directory DUNGEON_LAYER_TYPES = DUNGEON_LAYER.subdirectory("types");

    Directory BLUEPRINTS = BASE.subdirectory("blueprints");
    Directory BLUEPRINTS_ROOMS = BLUEPRINTS.subdirectory("rooms");
    Directory BLUEPRINTS_SIDE_ROOMS = BLUEPRINTS.subdirectory("side_rooms");
    Directory BLUEPRINTS_CORRIDOR_SEGMENTS = BLUEPRINTS.subdirectory("corridor_segments");
    Directory BLUEPRINTS_MULTIPART_SEGMENTS = BLUEPRINTS.subdirectory("multipart_segments");

    Directory THEMES = BASE.subdirectory("themes");
    Directory PRIMARY_THEMES = THEMES.subdirectory("primary");
    Directory SECONDARY_THEMES = THEMES.subdirectory("secondary");

    Directory SPAWNER = BASE.subdirectory("spawner");
    Directory SPAWNER_TYPES = SPAWNER.subdirectory("types");
    Directory SPAWNER_ENTITIES = SPAWNER.subdirectory("entities");
    Directory SPAWNER_EQUIPMENT = SPAWNER.subdirectory("equipment");
    Directory SPAWNER_EQUIPMENT_WEAPON = SPAWNER_EQUIPMENT.subdirectory("weapon");
    Directory SPAWNER_EQUIPMENT_ARMOR = SPAWNER_EQUIPMENT.subdirectory("armor");
    Directory SPAWNER_POTION_EFFECTS = SPAWNER.subdirectory("potion_effects");

    Directory MAPPINGS = BASE.subdirectory("mappings");
    Directory TYPE_MAPPINGS = MAPPINGS.subdirectory("types");
    Directory DUNGEON_TYPE_MAPPINGS = TYPE_MAPPINGS.subdirectory("dungeon");
    Directory THEME_MAPPINGS = MAPPINGS.subdirectory("themes");
    Directory PRIMARY_THEME_MAPPINGS = THEME_MAPPINGS.subdirectory("primary");
    Directory SECONDARY_THEME_MAPPINGS = THEME_MAPPINGS.subdirectory("secondary");

    class Directory {
        private final String path;

        private Directory(String path) {
            this.path = path + '/';
        }

        public ResourceLocation file(String namespace, String fileName) {
            return new ResourceLocation(namespace, path + fileName);
        }

        /**
         * Creates a key for a given resource location. Removes the base directory, the following slash and the file ending.
         *
         * @param resourceLocation the initial resource location.
         * @param fileEnding       the file ending to remove at the end of the path
         * @return the key
         */
        public ResourceLocation key(ResourceLocation resourceLocation, String fileEnding) {
            String path = resourceLocation.getPath();
            return new ResourceLocation(resourceLocation.getNamespace(), path.substring(this.path.length(), path.length() - fileEnding.length()));
        }

        private Directory subdirectory(String directory) {
            return new Directory(this.path + directory);
        }

        public String path() {
            return path;
        }

    }
}
