package xiroc.dungeoncrawl.datapack.registry;

import net.minecraft.resources.ResourceLocation;
import xiroc.dungeoncrawl.datapack.DatapackDirectories;
import xiroc.dungeoncrawl.dungeon.blueprint.Blueprint;
import xiroc.dungeoncrawl.dungeon.blueprint.builtin.BuiltinBlueprints;
import xiroc.dungeoncrawl.dungeon.blueprint.template.TemplateBlueprint;
import xiroc.dungeoncrawl.dungeon.monster.SpawnerEntityProperties;
import xiroc.dungeoncrawl.dungeon.monster.SpawnerEntityType;
import xiroc.dungeoncrawl.dungeon.monster.SpawnerType;
import xiroc.dungeoncrawl.dungeon.theme.BuiltinThemes;
import xiroc.dungeoncrawl.dungeon.theme.PrimaryTheme;
import xiroc.dungeoncrawl.dungeon.theme.SecondaryTheme;
import xiroc.dungeoncrawl.dungeon.type.DungeonType;
import xiroc.dungeoncrawl.dungeon.type.level.LevelType;
import xiroc.dungeoncrawl.util.JSONUtils;
import xiroc.dungeoncrawl.util.random.RandomMapping;

import java.util.function.BiConsumer;
import java.util.function.Consumer;

public interface DatapackRegistries {
    DatapackRegistry<SpawnerType> SPAWNER_TYPE = new InheritingDatapackRegistry<>(DatapackDirectories.SPAWNER_TYPES, none(),
            (reader) -> JSONUtils.GSON.fromJson(reader, SpawnerType.Builder.class));

    DatapackRegistry<SpawnerEntityType> SPAWNER_ENTITY_TYPE = new InheritingDatapackRegistry<>(DatapackDirectories.SPAWNER_ENTITIES, none(),
            (reader) -> JSONUtils.GSON.fromJson(reader, SpawnerEntityType.Builder.class));

    DatapackRegistry<SpawnerEntityProperties> SPAWNER_ENTITY_PROPERTIES = new InheritingDatapackRegistry<>(DatapackDirectories.SPAWNER_ENTITY_PROPERTIES, none(),
            (reader) -> JSONUtils.GSON.fromJson(reader, SpawnerEntityProperties.Builder.class));

    DatapackRegistry<PrimaryTheme> PRIMARY_THEME = new DatapackRegistry<>(DatapackDirectories.PRIMARY_THEMES, BuiltinThemes::registerPrimary,
            (reader) -> JSONUtils.GSON.fromJson(reader, PrimaryTheme.class));

    DatapackRegistry<SecondaryTheme> SECONDARY_THEME = new DatapackRegistry<>(DatapackDirectories.SECONDARY_THEMES, BuiltinThemes::registerSecondary,
            (reader) -> JSONUtils.GSON.fromJson(reader, SecondaryTheme.class));

    DatapackRegistry<RandomMapping<PrimaryTheme>> PRIMARY_THEME_MAPPINGS = new InheritingDatapackRegistry<>(DatapackDirectories.PRIMARY_THEME_MAPPINGS, none(),
            (reader) -> JSONUtils.GSON.<RandomMapping.Builder<PrimaryTheme>>fromJson(reader, RandomMapping.Types.PRIMARY_THEME));

    DatapackRegistry<RandomMapping<SecondaryTheme>> SECONDARY_THEME_MAPPINGS = new InheritingDatapackRegistry<>(DatapackDirectories.SECONDARY_THEME_MAPPINGS, none(),
            (reader) -> JSONUtils.GSON.<RandomMapping.Builder<SecondaryTheme>>fromJson(reader, RandomMapping.Types.SECONDARY_THEME));

    DatapackRegistry<Blueprint> BLUEPRINT = new DatapackRegistry<>(DatapackDirectories.BLUEPRINTS, BuiltinBlueprints::register, TemplateBlueprint::load);

    DatapackRegistry<LevelType> LEVEL_TYPE = new InheritingDatapackRegistry<>(DatapackDirectories.DUNGEON_LAYER_TYPES, none(),
            (reader) -> JSONUtils.GSON.fromJson(reader, LevelType.Builder.class));

    DatapackRegistry<DungeonType> DUNGEON_TYPE = new InheritingDatapackRegistry<>(DatapackDirectories.DUNGEON_TYPES, none(),
            (reader) -> JSONUtils.GSON.fromJson(reader, DungeonType.Builder.class));

    private static <T> Consumer<BiConsumer<ResourceLocation, T>> none() {
        return (collector) -> {
        };
    }
}
