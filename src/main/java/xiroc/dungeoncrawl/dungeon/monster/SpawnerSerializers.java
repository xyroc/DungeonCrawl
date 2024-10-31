package xiroc.dungeoncrawl.dungeon.monster;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import xiroc.dungeoncrawl.datapack.registry.InheritingBuilder;
import xiroc.dungeoncrawl.util.random.value.RandomValue;

public interface SpawnerSerializers {
    Gson ENTITY_PROPERTIES = createEntityPropertiesSerializer().create();
    Gson ENTITY_TYPES = createEntityTypeSerializer().create();
    Gson SPAWNER_TYPES = createSpawnerTypeSerializer().create();

    private static GsonBuilder createEntityPropertiesSerializer() {
        return new GsonBuilder()
                .registerTypeAdapter(SpawnerEntityProperties.Builder.class, InheritingBuilder.WrappedSerializer.of(new SpawnerEntityProperties.BuilderSerializer()));
    }

    private static GsonBuilder createEntityTypeSerializer() {
        return createEntityPropertiesSerializer()
                .registerTypeAdapter(SpawnerEntityType.Builder.class, InheritingBuilder.WrappedSerializer.of(new SpawnerEntityType.BuilderSerializer()));
    }

    private static GsonBuilder createSpawnerTypeSerializer() {
        return RandomValue.gsonAdapters(createEntityTypeSerializer())
                .registerTypeAdapter(SpawnerType.Builder.class, InheritingBuilder.WrappedSerializer.of(new SpawnerType.BuilderSerializer()));
    }
}