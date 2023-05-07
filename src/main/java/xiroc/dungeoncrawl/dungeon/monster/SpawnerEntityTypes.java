package xiroc.dungeoncrawl.dungeon.monster;

import com.google.common.collect.ImmutableMap;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import xiroc.dungeoncrawl.datapack.DatapackDirectories;
import xiroc.dungeoncrawl.exception.DatapackLoadException;

import java.io.IOException;
import java.io.InputStreamReader;

public class SpawnerEntityTypes {
    private static ImmutableMap<ResourceLocation, SpawnerEntityType> TYPES;

    public static void load(ResourceManager resourceManager) {
        ImmutableMap.Builder<ResourceLocation, SpawnerEntityType> builder = ImmutableMap.builder();
        resourceManager.listResources(DatapackDirectories.SPAWNER_ENTITIES.path(), (s) -> s.endsWith(".json")).forEach((location) -> {
            try {
                builder.put(DatapackDirectories.SPAWNER_ENTITIES.key(location, ".json"),
                        SpawnerSerializers.ENTITY_TYPES.fromJson(new InputStreamReader(resourceManager.getResource(location).getInputStream()), SpawnerEntityType.class));
            } catch (IOException e) {
                throw new DatapackLoadException("Failed to load spawner entity type " + location + " : " + e.getMessage());
            }
        });
        TYPES = builder.build();
    }

    public static SpawnerEntityType get(ResourceLocation key) {
        return TYPES.get(key);
    }
}
