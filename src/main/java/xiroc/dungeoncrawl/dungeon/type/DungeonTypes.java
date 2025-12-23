package xiroc.dungeoncrawl.dungeon.type;

import net.minecraft.core.RegistryAccess;
import net.minecraft.world.level.biome.Biome;
import xiroc.dungeoncrawl.DungeonCrawl;
import xiroc.dungeoncrawl.datapack.registry.DatapackRegistries;
import xiroc.dungeoncrawl.util.random.RandomMapping;

public class DungeonTypes {

    private static RandomMapping<Biome, DungeonType> BIOME_TO_DUNGEON_TYPE_MAPPING;

    public static void buildMapping(RegistryAccess registryAccess) {
        final var dungeonTypeMappings = registryAccess.registryOrThrow(DatapackRegistries.DUNGEON_TYPE_MAPPINGS);
        final RandomMapping.Builder<Biome, DungeonType> combinedMapping = new RandomMapping.Builder<>();
        dungeonTypeMappings.forEach(combinedMapping::combineWith);
        BIOME_TO_DUNGEON_TYPE_MAPPING = combinedMapping.build();
        DungeonCrawl.LOGGER.debug("Combined {} dungeon type mapping(s).", dungeonTypeMappings.size());
    }

    public static void updateMapping(RegistryAccess registryAccess) {
        final var dungeonTypeMappings = registryAccess.registryOrThrow(DatapackRegistries.DUNGEON_TYPE_MAPPINGS);
        dungeonTypeMappings.forEach(RandomMapping::compile);
        biomeMapping().compile();
    }

    public static RandomMapping<Biome, DungeonType> biomeMapping() {
        if (BIOME_TO_DUNGEON_TYPE_MAPPING == null) {
            throw new IllegalStateException("Dungeon type mapping has not been initialized");
        }
        return BIOME_TO_DUNGEON_TYPE_MAPPING;
    }
}
