package xiroc.dungeoncrawl.dungeon.type;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import xiroc.dungeoncrawl.DungeonCrawl;
import xiroc.dungeoncrawl.datapack.DatapackDirectories;
import xiroc.dungeoncrawl.datapack.DatapackNamespaces;
import xiroc.dungeoncrawl.exception.DatapackLoadException;
import xiroc.dungeoncrawl.util.JSONUtils;
import xiroc.dungeoncrawl.util.random.RandomMapping;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class DungeonTypes {
    public static final ResourceLocation FILE_LOCATION = DatapackDirectories.TYPE_MAPPINGS.resource(DatapackNamespaces.DEFAULT, "dungeon_types.json");

    private static RandomMapping<DungeonType> BIOME_TO_DUNGEON_TYPE_MAPPING;

    public static void load(ResourceManager resourceManager) {
        try {
            DungeonCrawl.LOGGER.debug("Loading dungeon type mapping from {}", FILE_LOCATION);
            final InputStream inputStream = resourceManager.getResource(FILE_LOCATION).orElseThrow().open();
            BIOME_TO_DUNGEON_TYPE_MAPPING = JSONUtils.GSON.<RandomMapping.Builder<DungeonType>>fromJson(new InputStreamReader(inputStream), RandomMapping.Types.DUNGEON_TYPE).build();
        } catch (IOException e) {
            throw new DatapackLoadException("Failed to load " + FILE_LOCATION + ": " + e.getMessage());
        }
    }

    public static RandomMapping<DungeonType> biomeMapping() {
        if (BIOME_TO_DUNGEON_TYPE_MAPPING == null) {
            throw new IllegalStateException("Dungeon type mapping has not been initialized");
        }
        return BIOME_TO_DUNGEON_TYPE_MAPPING;
    }
}
