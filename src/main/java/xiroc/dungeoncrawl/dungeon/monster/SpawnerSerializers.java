package xiroc.dungeoncrawl.dungeon.monster;

import com.google.gson.GsonBuilder;
import xiroc.dungeoncrawl.datapack.registry.InheritingBuilder;

public interface SpawnerSerializers {
    static void gsonAdapters(GsonBuilder builder) {
        builder.registerTypeAdapter(SpawnerEntityProperties.Builder.class, InheritingBuilder.WrappedSerializer.of(new SpawnerEntityProperties.BuilderSerializer()))
                .registerTypeAdapter(SpawnerEntityType.Builder.class, InheritingBuilder.WrappedSerializer.of(new SpawnerEntityType.BuilderSerializer()))
                .registerTypeAdapter(SpawnerType.Builder.class, InheritingBuilder.WrappedSerializer.of(new SpawnerType.BuilderSerializer()));
    }
}