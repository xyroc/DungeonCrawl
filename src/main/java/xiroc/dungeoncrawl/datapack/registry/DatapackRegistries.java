package xiroc.dungeoncrawl.datapack.registry;

import com.google.gson.JsonParser;
import net.minecraft.resources.ResourceLocation;
import xiroc.dungeoncrawl.datapack.DatapackDirectories;
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
import xiroc.dungeoncrawl.dungeon.theme.ThemeSerializers;
import xiroc.dungeoncrawl.dungeon.type.LevelType;
import xiroc.dungeoncrawl.util.random.IRandom;
import xiroc.dungeoncrawl.util.random.RandomMapping;

import java.util.function.BiConsumer;
import java.util.function.Consumer;

public interface DatapackRegistries {
    DatapackRegistry<SpawnerType> SPAWNER_TYPE = new InheritingDatapackRegistry<>(DatapackDirectories.SPAWNER_TYPES, none(),
            (reader) -> SpawnerSerializers.SPAWNER_TYPES.fromJson(reader, SpawnerType.Builder.class));

    DatapackRegistry<SpawnerEntityType> SPAWNER_ENTITY_TYPE = new InheritingDatapackRegistry<>(DatapackDirectories.SPAWNER_ENTITIES, none(),
            (reader) -> SpawnerSerializers.ENTITY_TYPES.fromJson(reader, SpawnerEntityType.Builder.class));

    DatapackRegistry<SpawnerEntityProperties> SPAWNER_ENTITY_PROPERTIES = new InheritingDatapackRegistry<>(DatapackDirectories.SPAWNER_ENTITY_PROPERTIES, none(),
            (reader) -> SpawnerSerializers.ENTITY_PROPERTIES.fromJson(reader, SpawnerEntityProperties.Builder.class));

    DatapackRegistry<PrimaryTheme> PRIMARY_THEME = new DatapackRegistry<>(DatapackDirectories.PRIMARY_THEMES, BuiltinThemes::registerPrimary,
            (reader) -> ThemeSerializers.GSON.fromJson(reader, PrimaryTheme.class));

    DatapackRegistry<SecondaryTheme> SECONDARY_THEME = new DatapackRegistry<>(DatapackDirectories.SECONDARY_THEMES, BuiltinThemes::registerSecondary,
            (reader) -> ThemeSerializers.GSON.fromJson(reader, SecondaryTheme.class));

    DatapackRegistry<RandomMapping<ResourceLocation, PrimaryTheme>> PRIMARY_THEME_MAPPINGS = new InheritingDatapackRegistry<>(DatapackDirectories.PRIMARY_THEME_MAPPINGS, none(),
            (reader) -> new RandomMapping.Builder<ResourceLocation, PrimaryTheme>().deserialize(JsonParser.parseReader(reader), IRandom.PRIMARY_THEME, ResourceLocation::new));

    DatapackRegistry<RandomMapping<ResourceLocation, SecondaryTheme>> SECONDARY_THEME_MAPPINGS = new InheritingDatapackRegistry<>(DatapackDirectories.SECONDARY_THEME_MAPPINGS, none(),
            (reader) -> new RandomMapping.Builder<ResourceLocation, SecondaryTheme>().deserialize(JsonParser.parseReader(reader), IRandom.SECONDARY_THEME, ResourceLocation::new));

    DatapackRegistry<Blueprint> BLUEPRINT = new DatapackRegistry<>(DatapackDirectories.BLUEPRINTS, BuiltinBlueprints::register, TemplateBlueprint::load);

    DatapackRegistry<LevelType> LEVEL_TYPE = new DatapackRegistry<>(DatapackDirectories.DUNGEON_LAYER_TYPES, none(),
            (reader) -> null);

    private static <T> Consumer<BiConsumer<ResourceLocation, T>> none() {
        return (collector) -> {
        };
    }
}
