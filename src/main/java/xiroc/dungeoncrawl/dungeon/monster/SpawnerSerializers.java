package xiroc.dungeoncrawl.dungeon.monster;

import com.google.gson.GsonBuilder;
import xiroc.dungeoncrawl.datapack.registry.DatapackRegistries;
import xiroc.dungeoncrawl.datapack.registry.Delegate;
import xiroc.dungeoncrawl.datapack.registry.InheritingBuilder;
import xiroc.dungeoncrawl.util.json.AdapterSerializer;
import xiroc.dungeoncrawl.util.random.IRandom;

public interface SpawnerSerializers {
    static void gsonAdapters(GsonBuilder builder) {
        builder.registerTypeAdapter(SpawnerEntityProperties.Builder.class, InheritingBuilder.WrappedSerializer.of(new SpawnerEntityProperties.BuilderSerializer()))
                .registerTypeAdapter(SpawnerEntityType.Builder.class, InheritingBuilder.WrappedSerializer.of(new SpawnerEntityType.BuilderSerializer()))
                .registerTypeAdapter(SpawnerEntityType.class, new AdapterSerializer<>(SpawnerEntityType.Builder.class, SpawnerEntityType.Builder::build, SpawnerEntityType.Builder::fromInstance))
                .registerTypeAdapter(SpawnerEntityType.Types.DELEGATE, new Delegate.Serializer<>(DatapackRegistries.SPAWNER_ENTITY_TYPE, SpawnerEntityType.class))
                .registerTypeAdapter(SpawnerEntityType.Types.RANDOM_BUILDER, new IRandom.BuilderSerializer<Delegate<SpawnerEntityType>>(SpawnerEntityType.Types.DELEGATE, "entity").wrapped())
                .registerTypeAdapter(SpawnerType.Builder.class, InheritingBuilder.WrappedSerializer.of(new SpawnerType.BuilderSerializer()))
                .registerTypeAdapter(SpawnerType.Types.DELEGATE, new Delegate.Serializer<>(DatapackRegistries.SPAWNER_TYPE, null))
                .registerTypeAdapter(SpawnerType.Types.RANDOM_BUILDER, new IRandom.BuilderSerializer<Delegate<SpawnerType>>(SpawnerType.Types.DELEGATE, "type").wrapped())
                .registerTypeAdapter(SpawnerType.Types.RANDOM, new IRandom.DirectSerializer<Delegate<SpawnerType>>(SpawnerType.Types.RANDOM_BUILDER));
    }
}