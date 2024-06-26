package xiroc.dungeoncrawl.datapack;

import net.minecraft.resources.ResourceLocation;
import xiroc.dungeoncrawl.dungeon.blueprint.Blueprint;
import xiroc.dungeoncrawl.dungeon.blueprint.builtin.BuiltinBlueprints;
import xiroc.dungeoncrawl.dungeon.blueprint.template.TemplateBlueprint;
import xiroc.dungeoncrawl.dungeon.monster.SpawnerEntityProperties;
import xiroc.dungeoncrawl.dungeon.monster.SpawnerEntityType;
import xiroc.dungeoncrawl.dungeon.monster.SpawnerSerializers;
import xiroc.dungeoncrawl.dungeon.monster.SpawnerType;
import xiroc.dungeoncrawl.dungeon.theme.BuiltinThemes;
import xiroc.dungeoncrawl.dungeon.theme.PrimaryTheme;
import xiroc.dungeoncrawl.dungeon.theme.SecondaryTheme;
import xiroc.dungeoncrawl.dungeon.theme.Themes;
import xiroc.dungeoncrawl.dungeon.type.LevelType;

import java.util.function.BiConsumer;
import java.util.function.Consumer;

public interface DatapackRegistries {
    DatapackRegistry<SpawnerType> SPAWNER_TYPE = new DatapackRegistry<>(DatapackDirectories.SPAWNER_TYPES, none(),
            (reader) -> SpawnerSerializers.SPAWNER_TYPES.fromJson(reader, SpawnerType.class));

    DatapackRegistry<SpawnerEntityType> SPAWNER_ENTITY_TYPE = new DatapackRegistry<>(DatapackDirectories.SPAWNER_ENTITIES, none(),
            (reader) -> SpawnerSerializers.ENTITY_TYPES.fromJson(reader, SpawnerEntityType.class));

    DatapackRegistry<SpawnerEntityProperties> SPAWNER_ENTITY_PROPERTIES = new DatapackRegistry<>(DatapackDirectories.SPAWNER_ENTITIES, none(),
            (reader) -> SpawnerSerializers.ENTITY_PROPERTIES.fromJson(reader, SpawnerEntityProperties.class));

    DatapackRegistry<PrimaryTheme> PRIMARY_THEME = new DatapackRegistry<>(DatapackDirectories.PRIMARY_THEMES, BuiltinThemes::registerPrimary,
            (reader) -> Themes.GSON.fromJson(reader, PrimaryTheme.class));

    DatapackRegistry<SecondaryTheme> SECONDARY_THEME = new DatapackRegistry<>(DatapackDirectories.SECONDARY_THEMES, BuiltinThemes::registerSecondary,
            (reader) -> Themes.GSON.fromJson(reader, SecondaryTheme.class));

    DatapackRegistry<Blueprint> BLUEPRINT = new DatapackRegistry<>(DatapackDirectories.BLUEPRINTS, BuiltinBlueprints::register, TemplateBlueprint::load);

    DatapackRegistry<LevelType> LEVEL_TYPE = new DatapackRegistry<>(DatapackDirectories.DUNGEON_LAYER_TYPES, none(),
            (reader) -> null);

    private static <T> Consumer<BiConsumer<ResourceLocation, T>> none() {
        return (collector) -> {
        };
    }
}
