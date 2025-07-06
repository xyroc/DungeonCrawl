package xiroc.dungeoncrawl.dungeon.type;

import com.google.common.collect.ImmutableList;
import com.google.gson.JsonArray;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import net.minecraft.resources.ResourceLocation;
import xiroc.dungeoncrawl.datapack.registry.DatapackRegistries;
import xiroc.dungeoncrawl.datapack.registry.Delegate;
import xiroc.dungeoncrawl.dungeon.theme.PrimaryTheme;
import xiroc.dungeoncrawl.dungeon.theme.SecondaryTheme;
import xiroc.dungeoncrawl.dungeon.type.level.LevelType;
import xiroc.dungeoncrawl.util.random.RandomMapping;

import javax.annotation.Nullable;
import java.lang.reflect.Type;
import java.util.Objects;

public record DungeonSection(ImmutableList<Delegate<LevelType>> levels,
                             Delegate<RandomMapping<PrimaryTheme>> primaryThemes,
                             Delegate<RandomMapping<SecondaryTheme>> secondaryThemes) {
    public static class Builder {
        private final ImmutableList.Builder<Delegate<LevelType>> levels = ImmutableList.builder();
        @Nullable
        private Delegate<RandomMapping<PrimaryTheme>> primaryThemes = null;
        @Nullable
        private Delegate<RandomMapping<SecondaryTheme>> secondaryThemes = null;

        public Builder primaryThemes(@Nullable Delegate<RandomMapping<PrimaryTheme>> primaryThemes) {
            this.primaryThemes = primaryThemes;
            return this;
        }

        public Builder secondaryThemes(@Nullable Delegate<RandomMapping<SecondaryTheme>> secondaryThemes) {
            this.secondaryThemes = secondaryThemes;
            return this;
        }

        public Builder level(Delegate<LevelType> level) {
            levels.add(level);
            return this;
        }

        public DungeonSection build() {
            Objects.requireNonNull(primaryThemes, "No mapping for primary themes was specified");
            Objects.requireNonNull(secondaryThemes, "No mapping for secondary themes was specified");
            ImmutableList<Delegate<LevelType>> levels = this.levels.build();
            if (levels.isEmpty()) {
                throw new IllegalStateException("A section must contain at least one level");
            }
            return new DungeonSection(levels, primaryThemes, secondaryThemes);
        }
    }

    public static class Serializer implements JsonSerializer<DungeonSection>, JsonDeserializer<DungeonSection> {
        private static final String KEY_LEVELS = "levels";
        private static final String KEY_THEMES = "themes";
        private static final String KEY_THEME_PRIMARY = "primary";
        private static final String KEY_THEME_SECONDARY = "secondary";
        private static final String KEY_THEME_SHUFFLE_MODE = "shuffle";

        @Override
        public DungeonSection deserialize(JsonElement json, Type type, JsonDeserializationContext context) throws JsonParseException {
            JsonObject object = json.getAsJsonObject();
            JsonArray levels = object.get(KEY_LEVELS).getAsJsonArray();
            ImmutableList.Builder<Delegate<LevelType>> builder = ImmutableList.builder();
            for (JsonElement level : levels) {
                final Delegate<LevelType> levelType = context.deserialize(level, LevelType.Types.DELEGATE);
                builder.add(levelType);
            }
            JsonObject themes = object.get(KEY_THEMES).getAsJsonObject();
            var primaryThemes = DatapackRegistries.PRIMARY_THEME_MAPPINGS.delegateOrThrow(ResourceLocation.parse(themes.get(KEY_THEME_PRIMARY).getAsString()));
            var secondaryThemes = DatapackRegistries.SECONDARY_THEME_MAPPINGS.delegateOrThrow(ResourceLocation.parse(themes.get(KEY_THEME_SECONDARY).getAsString()));
            return new DungeonSection(builder.build(), primaryThemes, secondaryThemes);
        }

        @Override
        public JsonElement serialize(DungeonSection section, Type type, JsonSerializationContext context) {
            JsonObject object = new JsonObject();
            JsonArray levels = new JsonArray();
            for (var level : section.levels) {
                levels.add(context.serialize(level, LevelType.Types.DELEGATE));
            }
            object.add(KEY_LEVELS, levels);
            JsonObject themes = new JsonObject();
            themes.addProperty(KEY_THEME_PRIMARY, section.primaryThemes.key().toString());
            themes.addProperty(KEY_THEME_SECONDARY, section.secondaryThemes.key().toString());
            object.add(KEY_THEMES, themes);
            return object;
        }
    }
}
