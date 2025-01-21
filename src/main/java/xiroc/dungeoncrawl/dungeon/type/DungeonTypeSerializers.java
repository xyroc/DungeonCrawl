package xiroc.dungeoncrawl.dungeon.type;

import com.google.gson.GsonBuilder;
import xiroc.dungeoncrawl.datapack.registry.InheritingBuilder;
import xiroc.dungeoncrawl.dungeon.generator.level.LevelGeneratorSettings;
import xiroc.dungeoncrawl.dungeon.type.level.CorridorStyle;
import xiroc.dungeoncrawl.dungeon.type.level.LevelType;
import xiroc.dungeoncrawl.dungeon.type.level.SpecialRoom;

public interface DungeonTypeSerializers {
    static void gsonAdapters(GsonBuilder builder) {
        builder.registerTypeAdapter(LevelGeneratorSettings.Builder.class, InheritingBuilder.WrappedSerializer.of(new LevelGeneratorSettings.BuilderSerializer()))
                .registerTypeAdapter(LevelType.Builder.class, InheritingBuilder.WrappedSerializer.of(new LevelType.BuilderSerializer()))
                .registerTypeAdapter(SpecialRoom.class, new SpecialRoom.Serializer())
                .registerTypeAdapter(CorridorStyle.class, new CorridorStyle.Serializer())
                .registerTypeAdapter(DungeonType.Builder.class, InheritingBuilder.WrappedSerializer.of(new DungeonType.BuilderSerializer()));
    }
}
