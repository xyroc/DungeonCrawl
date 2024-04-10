package xiroc.dungeoncrawl.datapack;

import com.google.gson.JsonParser;
import net.minecraft.resources.ResourceLocation;
import xiroc.dungeoncrawl.dungeon.monster.SpawnerEntityType;
import xiroc.dungeoncrawl.dungeon.monster.SpawnerSerializers;
import xiroc.dungeoncrawl.dungeon.monster.SpawnerType;
import xiroc.dungeoncrawl.dungeon.theme.BuiltinThemes;
import xiroc.dungeoncrawl.dungeon.theme.PrimaryTheme;
import xiroc.dungeoncrawl.dungeon.theme.SecondaryTheme;

import java.util.function.BiConsumer;
import java.util.function.Consumer;

public interface DatapackRegistries {
    DatapackRegistry<SpawnerEntityType> SPAWNER_ENTITY_TYPE = new DatapackRegistry<>(DatapackDirectories.SPAWNER_ENTITIES, none(),
            (reader) -> SpawnerSerializers.ENTITY_TYPES.fromJson(reader, SpawnerEntityType.class));

    DatapackRegistry<SpawnerType> SPAWNER_TYPE = new DatapackRegistry<>(DatapackDirectories.SPAWNER_TYPES, none(),
            (reader) -> SpawnerSerializers.SPAWNER_TYPES.fromJson(reader, SpawnerType.class));

    DatapackRegistry<PrimaryTheme> PRIMARY_THEME = new DatapackRegistry<>(DatapackDirectories.PRIMARY_THEMES, BuiltinThemes::registerPrimary,
            (key, reader) -> PrimaryTheme.deserialize(key, JsonParser.parseReader(reader)));

    DatapackRegistry<SecondaryTheme> SECONDARY_THEME = new DatapackRegistry<>(DatapackDirectories.SECONDARY_THEMES, BuiltinThemes::registerSecondary,
            (key, reader) -> SecondaryTheme.deserialize(key, JsonParser.parseReader(reader)));

    private static <T> Consumer<BiConsumer<ResourceLocation, T>> none() {
        return (collector) -> {
        };
    }
}
