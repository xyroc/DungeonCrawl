package xiroc.dungeoncrawl.dungeon.monster;

import com.google.common.collect.ImmutableMap;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import xiroc.dungeoncrawl.datapack.DatapackDirectories;
import xiroc.dungeoncrawl.exception.DatapackLoadException;

import java.io.IOException;
import java.io.InputStreamReader;

public class SpawnerTypes {
    private static ImmutableMap<ResourceLocation, SpawnerType> TYPES;

    public static void load(ResourceManager resourceManager) {
        ImmutableMap.Builder<ResourceLocation, SpawnerType> builder = ImmutableMap.builder();
        resourceManager.listResources(DatapackDirectories.SPAWNER_TYPES.path(), (s) -> s.endsWith(".json")).forEach((location -> {
            try {
                builder.put(DatapackDirectories.SPAWNER_TYPES.key(location, ".json"),
                        SpawnerSerializers.SPAWNER_TYPES.fromJson(new InputStreamReader(resourceManager.getResource(location).getInputStream()), SpawnerType.class));
            } catch (IOException e) {
                throw new DatapackLoadException("Failed to load spawner type " + location + " : " + e.getMessage());
            }
        }));
        TYPES = builder.build();
    }

    public static SpawnerType get(ResourceLocation key) {
        return TYPES.get(key);
    }
}
