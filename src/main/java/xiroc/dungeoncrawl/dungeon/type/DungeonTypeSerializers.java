package xiroc.dungeoncrawl.dungeon.type;

import com.google.gson.GsonBuilder;
import xiroc.dungeoncrawl.datapack.registry.InheritingBuilder;
import xiroc.dungeoncrawl.dungeon.generator.level.LevelGeneratorSettings;
import xiroc.dungeoncrawl.util.random.value.RandomValue;

public interface DungeonTypeSerializers {
    static void gsonAdapters(GsonBuilder builder) {
        builder.registerTypeAdapter(LevelGeneratorSettings.Builder.class, InheritingBuilder.WrappedSerializer.of(new LevelGeneratorSettings.BuilderSerializer()))
                .registerTypeAdapter(LevelType.Builder.class, InheritingBuilder.WrappedSerializer.of(new LevelType.BuilderSerializer()));
    }
}
