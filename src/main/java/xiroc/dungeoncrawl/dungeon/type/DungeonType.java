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
import xiroc.dungeoncrawl.datapack.registry.Delegate;
import xiroc.dungeoncrawl.datapack.registry.InheritingBuilder;
import xiroc.dungeoncrawl.dungeon.blueprint.Blueprint;
import xiroc.dungeoncrawl.util.random.IRandom;

import javax.annotation.Nullable;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Objects;

public record DungeonType(IRandom<Delegate<Blueprint>> entrances, ImmutableList<DungeonSection> sections) {
    public static class Builder extends InheritingBuilder<DungeonType, Builder> {
        @Nullable
        private IRandom.Builder<Delegate<Blueprint>> entrances = null;
        private final ArrayList<DungeonSection> sections = new ArrayList<>();

        public Builder entrances(IRandom.Builder<Delegate<Blueprint>> entrances) {
            this.entrances = entrances;
            return this;
        }

        public Builder section(DungeonSection section) {
            this.sections.add(section);
            return this;
        }

        @Override
        public Builder inherit(Builder from) {
            if (!replace && !from.sections.isEmpty()) {
                sections.addAll(from.sections);
            }
            this.entrances = InheritingBuilder.inheritOrReplaceOrChoose(this.entrances, from.entrances);
            return this;
        }

        @Override
        public DungeonType build() {
            Objects.requireNonNull(entrances);
            if (sections.isEmpty()) {
                throw new IllegalStateException("A dungeon type must have at least one section");
            }
            return new DungeonType(entrances.build(), ImmutableList.copyOf(sections));
        }
    }

    public static class BuilderSerializer implements JsonSerializer<Builder>, JsonDeserializer<Builder> {
        private static final String KEY_ENTRANCES = "entrances";
        private static final String KEY_SECTIONS = "sections";

        @Override
        public Builder deserialize(JsonElement json, Type type, JsonDeserializationContext context) throws JsonParseException {
            final JsonObject object = json.getAsJsonObject();
            final Builder builder = new Builder();
            if (object.has(KEY_ENTRANCES)) {
                builder.entrances = IRandom.BLUEPRINT.deserializeBuilder(object.get(KEY_ENTRANCES));
            }
            if (object.has(KEY_SECTIONS)) {
                for (final JsonElement section : object.getAsJsonArray(KEY_SECTIONS)) {
                    builder.sections.add(context.deserialize(section, DungeonSection.class));
                }
            }
            return builder;
        }

        @Override
        public JsonElement serialize(Builder builder, Type type, JsonSerializationContext context) {
            final JsonObject object = new JsonObject();
            if (builder.entrances != null) {
                object.add(KEY_ENTRANCES, IRandom.BLUEPRINT.serializeBuilder(builder.entrances));
            }
            if (!builder.sections.isEmpty()) {
                final JsonArray sections = new JsonArray();
                for (final DungeonSection section : builder.sections) {
                    sections.add(context.serialize(section));
                }
            }
            return object;
        }
    }
}
