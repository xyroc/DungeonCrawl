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
import com.google.gson.reflect.TypeToken;
import xiroc.dungeoncrawl.datapack.registry.Delegate;
import xiroc.dungeoncrawl.datapack.registry.InheritingBuilder;
import xiroc.dungeoncrawl.dungeon.blueprint.Blueprint;
import xiroc.dungeoncrawl.util.random.IRandom;

import javax.annotation.Nullable;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public record DungeonType(IRandom<Delegate<Blueprint>> entrances, ImmutableList<DungeonSection> sections, ImmutableList<SecretRoom> secretRooms) {
    /**
     * Holds types representing the different contexts this class is serialized in.
     */
    public interface Types {
        Type DELEGATE = new TypeToken<Delegate<DungeonType>>() {}.getType();
        Type RANDOM_BUILDER = new TypeToken<IRandom.Builder<Delegate<DungeonType>>>() {}.getType();
    }

    public static class Builder extends InheritingBuilder<DungeonType, Builder> {
        @Nullable
        private IRandom.Builder<Delegate<Blueprint>> entrances = null;
        private final List<DungeonSection> sections = new ArrayList<>();
        private List<SecretRoom> secretRooms = new ArrayList<>();

        public Builder entrances(IRandom.Builder<Delegate<Blueprint>> entrances) {
            this.entrances = entrances;
            return this;
        }

        public Builder section(DungeonSection section) {
            this.sections.add(section);
            return this;
        }

        public Builder secretRoom(SecretRoom.Builder secretRoom) {
            this.secretRooms.add(secretRoom.build());
            return this;
        }

        @Override
        public Builder inherit(Builder from) {
            if (!doesReplace && !from.sections.isEmpty()) {
                sections.addAll(from.sections);
            }
            this.entrances = InheritingBuilder.inheritOrReplaceOrChoose(this.entrances, from.entrances);
            this.secretRooms = InheritingBuilder.choose(this.secretRooms, from.secretRooms);
            return this;
        }

        @Override
        public DungeonType build() {
            Objects.requireNonNull(entrances);
            if (sections.isEmpty()) {
                throw new IllegalStateException("A dungeon type must have at least one section");
            }
            return new DungeonType(entrances.build(), ImmutableList.copyOf(sections), ImmutableList.copyOf(secretRooms));
        }
    }

    public static class BuilderSerializer implements JsonSerializer<Builder>, JsonDeserializer<Builder> {
        private static final String KEY_ENTRANCES = "entrances";
        private static final String KEY_SECTIONS = "sections";
        private static final String KEY_SECRET_ROOMS = "secret_rooms";

        @Override
        public Builder deserialize(JsonElement json, Type type, JsonDeserializationContext context) throws JsonParseException {
            final JsonObject object = json.getAsJsonObject();
            final Builder builder = new Builder();
            if (object.has(KEY_ENTRANCES)) {
                builder.entrances = context.deserialize(object.get(KEY_ENTRANCES), Blueprint.Types.RANDOM_BUILDER);
            }
            if (object.has(KEY_SECTIONS)) {
                for (final JsonElement section : object.getAsJsonArray(KEY_SECTIONS)) {
                    builder.sections.add(context.deserialize(section, DungeonSection.class));
                }
            }
            if (object.has(KEY_SECRET_ROOMS)) {
                for (final JsonElement secretRoom : object.getAsJsonArray(KEY_SECRET_ROOMS)) {
                    builder.secretRooms.add(context.deserialize(secretRoom, SecretRoom.class));
                }
            }
            return builder;
        }

        @Override
        public JsonElement serialize(Builder builder, Type type, JsonSerializationContext context) {
            final JsonObject object = new JsonObject();
            if (builder.entrances != null) {
                object.add(KEY_ENTRANCES, context.serialize(builder.entrances, Blueprint.Types.RANDOM_BUILDER));
            }
            if (!builder.sections.isEmpty()) {
                final JsonArray sections = new JsonArray();
                for (final DungeonSection section : builder.sections) {
                    sections.add(context.serialize(section));
                }
                object.add(KEY_SECTIONS, sections);
            }
            if (!builder.secretRooms.isEmpty()) {
                final JsonArray secretRooms = new JsonArray();
                for (final SecretRoom secretRoom : builder.secretRooms) {
                    secretRooms.add(context.serialize(secretRoom, SecretRoom.class));
                }
                object.add(KEY_SECRET_ROOMS, secretRooms);
            }
            return object;
        }
    }
}
