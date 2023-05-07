package xiroc.dungeoncrawl.dungeon.monster;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import xiroc.dungeoncrawl.util.random.value.Constant;
import xiroc.dungeoncrawl.util.random.value.RandomValue;
import xiroc.dungeoncrawl.util.random.value.Range;

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
        return createEntityTypeSerializer()
                .registerTypeAdapter(RandomValue.class, new RandomValue.Deserializer())
                .registerTypeAdapter(Range.class, new Range.Serializer())
                .registerTypeAdapter(Constant.class, new Constant.Serializer())
                .registerTypeAdapter(SpawnerType.class, new SpawnerType.Serializer());
    }
}