package xiroc.dungeoncrawl.dungeon.monster;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import xiroc.dungeoncrawl.util.random.value.RandomValue;

public interface SpawnerSerializers {
    Gson ENTITY_PROPERTIES = createEntityPropertiesSerializer().create();
    Gson ENTITY_TYPES = createEntityTypeSerializer().create();
    Gson SPAWNER_TYPES = createSpawnerTypeSerializer().create();

    private static GsonBuilder createEntityPropertiesSerializer() {
        return new GsonBuilder().registerTypeAdapter(SpawnerEntityProperties.class, new SpawnerEntityProperties.Serializer());
    }

    private static GsonBuilder createEntityTypeSerializer() {
        return createEntityPropertiesSerializer().registerTypeAdapter(SpawnerEntityType.class, new SpawnerEntityType.Serializer());
    }

    private static GsonBuilder createSpawnerTypeSerializer() {
        return RandomValue.gsonAdapters(createEntityTypeSerializer())
                .registerTypeAdapter(SpawnerType.class, new SpawnerType.Serializer());
    }
}