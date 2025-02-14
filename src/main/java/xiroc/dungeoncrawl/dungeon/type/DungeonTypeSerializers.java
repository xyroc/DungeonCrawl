package xiroc.dungeoncrawl.dungeon.type;

import com.google.gson.GsonBuilder;
import xiroc.dungeoncrawl.datapack.registry.DatapackRegistries;
import xiroc.dungeoncrawl.datapack.registry.Delegate;
import xiroc.dungeoncrawl.datapack.registry.InheritingBuilder;
import xiroc.dungeoncrawl.dungeon.generator.level.LevelGeneratorSettings;
import xiroc.dungeoncrawl.dungeon.type.level.CorridorStyle;
import xiroc.dungeoncrawl.dungeon.type.level.LevelType;
import xiroc.dungeoncrawl.dungeon.type.level.SpecialRoom;
import xiroc.dungeoncrawl.util.json.AdapterSerializer;
import xiroc.dungeoncrawl.util.random.IRandom;

public interface DungeonTypeSerializers {
    static void gsonAdapters(GsonBuilder builder) {
        builder.registerTypeAdapter(LevelGeneratorSettings.Builder.class, InheritingBuilder.WrappedSerializer.of(new LevelGeneratorSettings.BuilderSerializer()))
                .registerTypeAdapter(LevelType.Builder.class, InheritingBuilder.WrappedSerializer.of(new LevelType.BuilderSerializer()))
                .registerTypeAdapter(LevelType.class, new AdapterSerializer<>(LevelType.Builder.class, LevelType.Builder::build, LevelType.Builder::fromInstance))
                .registerTypeAdapter(LevelType.Types.DELEGATE, new Delegate.Serializer<>(DatapackRegistries.LEVEL_TYPE, LevelType.class))
                .registerTypeAdapter(SpecialRoom.class, new SpecialRoom.Serializer())
                .registerTypeAdapter(CorridorStyle.class, new CorridorStyle.Serializer())
                .registerTypeAdapter(CorridorStyle.Types.RANDOM_BUILDER, new IRandom.BuilderSerializer<CorridorStyle>(CorridorStyle.class, "style"))
                .registerTypeAdapter(CorridorStyle.Types.RANDOM, new IRandom.DirectSerializer<CorridorStyle>(CorridorStyle.Types.RANDOM_BUILDER))
                .registerTypeAdapter(DungeonType.Builder.class, InheritingBuilder.WrappedSerializer.of(new DungeonType.BuilderSerializer()))
                .registerTypeAdapter(DungeonType.Types.DELEGATE, new Delegate.Serializer<>(DatapackRegistries.DUNGEON_TYPE, null))
                .registerTypeAdapter(DungeonType.Types.RANDOM_BUILDER, new IRandom.BuilderSerializer<Delegate<DungeonType>>(DungeonType.Types.DELEGATE, "type"))
                .registerTypeAdapter(DungeonSection.class, new DungeonSection.Serializer());
    }
}
