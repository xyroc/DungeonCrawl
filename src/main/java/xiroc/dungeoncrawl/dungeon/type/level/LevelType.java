package xiroc.dungeoncrawl.dungeon.type.level;

import com.google.common.collect.ImmutableList;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import net.minecraft.resources.ResourceLocation;
import xiroc.dungeoncrawl.datapack.registry.Delegate;
import xiroc.dungeoncrawl.datapack.registry.InheritingBuilder;
import xiroc.dungeoncrawl.dungeon.blueprint.Blueprint;
import xiroc.dungeoncrawl.dungeon.generator.level.LevelGeneratorSettings;
import xiroc.dungeoncrawl.dungeon.monster.SpawnerType;
import xiroc.dungeoncrawl.util.JSONUtils;
import xiroc.dungeoncrawl.util.random.IRandom;

import javax.annotation.Nullable;
import java.lang.reflect.Type;
import java.util.List;
import java.util.Objects;

public record LevelType(LevelGeneratorSettings settings,
                        IRandom<Delegate<Blueprint>> rooms,
                        IRandom<Delegate<Blueprint>> corridorSegments,
                        IRandom<Delegate<Blueprint>> corridorSideSegments,
                        IRandom<Delegate<Blueprint>> upperStaircaseRooms,
                        IRandom<Delegate<Blueprint>> lowerStaircaseRooms,
                        @Nullable IRandom<IRandom<Delegate<Blueprint>>> clusterRooms,
                        ImmutableList<SpecialRoom> specialRooms,
                        IRandom<Delegate<SpawnerType>> spawners,
                        @Nullable ResourceLocation lootTable) {

    public static class Builder extends InheritingBuilder<LevelType, Builder> {
        @Nullable
        private LevelGeneratorSettings.Builder settings = null;
        @Nullable
        private IRandom.Builder<Delegate<Blueprint>> rooms = null;
        @Nullable
        private IRandom.Builder<Delegate<Blueprint>> corridorSegments = null;
        @Nullable
        private IRandom.Builder<Delegate<Blueprint>> corridorSideSegments = null;
        @Nullable
        private IRandom.Builder<Delegate<Blueprint>> upperStaircaseRooms = null;
        @Nullable
        private IRandom.Builder<Delegate<Blueprint>> lowerStaircaseRooms = null;
        @Nullable
        private IRandom.Builder<IRandom<Delegate<Blueprint>>> clusterRooms = null;
        @Nullable
        private IRandom.Builder<Delegate<SpawnerType>> spawnerTypes = null;
        @Nullable
        private List<SpecialRoom> specialRooms = null;
        @Nullable
        private ResourceLocation lootTable = null;
        @Override
        public Builder inherit(Builder from) {
            this.settings = InheritingBuilder.inheritOrReplaceOrChoose(this.settings, from.settings);
            this.rooms = InheritingBuilder.inheritOrReplaceOrChoose(this.rooms, from.rooms);
            this.corridorSegments = InheritingBuilder.inheritOrReplaceOrChoose(this.corridorSegments, from.corridorSegments);
            this.corridorSideSegments = InheritingBuilder.inheritOrReplaceOrChoose(this.corridorSideSegments, from.corridorSideSegments);
            this.upperStaircaseRooms = InheritingBuilder.inheritOrReplaceOrChoose(this.upperStaircaseRooms, from.upperStaircaseRooms);
            this.lowerStaircaseRooms = InheritingBuilder.inheritOrReplaceOrChoose(this.lowerStaircaseRooms, from.lowerStaircaseRooms);
            this.clusterRooms = InheritingBuilder.inheritOrReplaceOrChoose(this.clusterRooms, from.clusterRooms);
            this.specialRooms = InheritingBuilder.choose(this.specialRooms, from.specialRooms);
            this.spawnerTypes = InheritingBuilder.inheritOrReplaceOrChoose(this.spawnerTypes, from.spawnerTypes);
            this.lootTable = InheritingBuilder.choose(this.lootTable, from.lootTable);
            return this;
        }

        @Override
        public LevelType build() {
            if (specialRooms == null) {
                specialRooms = List.of();
            }
            Objects.requireNonNull(settings, "No generation settings were specified");
            Objects.requireNonNull(rooms, "No rooms were specified");
            Objects.requireNonNull(corridorSegments, "No corridor segments were specified");
            Objects.requireNonNull(corridorSideSegments, "No corridor segments were specified");
            Objects.requireNonNull(upperStaircaseRooms, "No upper staircase rooms were specified");
            Objects.requireNonNull(lowerStaircaseRooms, "No lower staircase rooms were specified");
            Objects.requireNonNull(spawnerTypes, "No spawner types were specified");
            return new LevelType(settings.build(),
                    rooms.build(),
                    corridorSegments.build(),
                    corridorSideSegments.build(),
                    upperStaircaseRooms.build(),
                    lowerStaircaseRooms.build(),
                    null,
                    ImmutableList.copyOf(specialRooms),
                    spawnerTypes.build(),
                    lootTable);
        }

        public Builder settings(@Nullable LevelGeneratorSettings.Builder settings) {
            this.settings = settings;
            return this;
        }

        public Builder rooms(@Nullable IRandom.Builder<Delegate<Blueprint>> rooms) {
            this.rooms = rooms;
            return this;
        }

        public Builder corridorSegments(@Nullable IRandom.Builder<Delegate<Blueprint>> corridorSegments) {
            this.corridorSegments = corridorSegments;
            return this;
        }

        public Builder corridorSideSegments(@Nullable IRandom.Builder<Delegate<Blueprint>> corridorSideSegments) {
            this.corridorSideSegments = corridorSideSegments;
            return this;
        }

        public Builder upperStaircaseRooms(@Nullable IRandom.Builder<Delegate<Blueprint>> upperStaircaseRooms) {
            this.upperStaircaseRooms = upperStaircaseRooms;
            return this;
        }

        public Builder lowerStaircaseRooms(@Nullable IRandom.Builder<Delegate<Blueprint>> lowerStaircaseRooms) {
            this.lowerStaircaseRooms = lowerStaircaseRooms;
            return this;
        }

        public Builder clusterRooms(@Nullable IRandom.Builder<IRandom<Delegate<Blueprint>>> clusterRooms) {
            this.clusterRooms = clusterRooms;
            return this;
        }

        public Builder specialRooms(@Nullable List<SpecialRoom> specialRooms) {
            this.specialRooms = specialRooms;
            return this;
        }

        public Builder spawnerTypes(@Nullable IRandom.Builder<Delegate<SpawnerType>> spawnerTypes) {
            this.spawnerTypes = spawnerTypes;
            return this;
        }

        public Builder lootTable(@Nullable ResourceLocation lootTable) {
            this.lootTable = lootTable;
            return this;
        }
    }

    public static class BuilderSerializer implements JsonSerializer<Builder>, JsonDeserializer<Builder> {
        private static final String KEY_SETTINGS = "settings";
        private static final String KEY_BLUEPRINTS = "blueprints";
        private static final String KEY_ROOMS = "rooms";
        private static final String KEY_CORRIDOR_SEGMENTS = "corridor_segments";
        private static final String KEY_CORRIDOR_SIDE_SEGMENTS = "corridor_side_segments";
        private static final String KEY_UPPER_STAIRCASE_ROOMS = "upper_staircase";
        private static final String KEY_LOWER_STAIRCASE_ROOMS = "lower_staircase";
        private static final String KEY_CLUSTER_ROOMS = "cluster_rooms";
        private static final String KEY_SPECIAL_ROOMS = "special_rooms";
        private static final String KEY_SPAWNER_TYPES = "spawners";
        private static final String KEY_LOOT_TABLE = "loot_table";

        private static final IRandom.Serializer<IRandom<Delegate<Blueprint>>> ROOM_SET =
                new IRandom.Serializer<>(IRandom.BLUEPRINT::deserialize, IRandom.BLUEPRINT::serialize, "rooms");

        @Override
        public Builder deserialize(JsonElement json, Type type, JsonDeserializationContext context) throws JsonParseException {
            Builder builder = new Builder();
            JsonObject object = json.getAsJsonObject();
            if (object.has(KEY_SETTINGS))
                builder.settings = context.deserialize(object.get(KEY_SETTINGS), LevelGeneratorSettings.Builder.class);
            if (object.has(KEY_BLUEPRINTS)) {
                JsonObject blueprints = object.get(KEY_BLUEPRINTS).getAsJsonObject();
                if (blueprints.has(KEY_ROOMS))
                    builder.rooms = IRandom.BLUEPRINT.deserializeBuilder(blueprints.get(KEY_ROOMS));
                if (blueprints.has(KEY_CORRIDOR_SEGMENTS))
                    builder.corridorSegments = IRandom.BLUEPRINT.deserializeBuilder(blueprints.get(KEY_CORRIDOR_SEGMENTS));
                if (blueprints.has(KEY_CORRIDOR_SIDE_SEGMENTS))
                    builder.corridorSideSegments = IRandom.BLUEPRINT.deserializeBuilder(blueprints.get(KEY_CORRIDOR_SIDE_SEGMENTS));
                if (blueprints.has(KEY_UPPER_STAIRCASE_ROOMS))
                    builder.upperStaircaseRooms = IRandom.BLUEPRINT.deserializeBuilder(blueprints.get(KEY_UPPER_STAIRCASE_ROOMS));
                if (blueprints.has(KEY_LOWER_STAIRCASE_ROOMS))
                    builder.lowerStaircaseRooms = IRandom.BLUEPRINT.deserializeBuilder(blueprints.get(KEY_LOWER_STAIRCASE_ROOMS));
                if (blueprints.has(KEY_CLUSTER_ROOMS)) {
                    builder.clusterRooms = ROOM_SET.deserializeBuilder(blueprints.get(KEY_CLUSTER_ROOMS));
                }
                if (blueprints.has(KEY_SPECIAL_ROOMS)) {
                    builder.specialRooms = JSONUtils.deserializeList(blueprints.getAsJsonArray(KEY_SPECIAL_ROOMS), elem -> context.deserialize(elem, SpecialRoom.class));
                }
            }
            if (object.has(KEY_SPAWNER_TYPES)) {
                builder.spawnerTypes = IRandom.SPAWNER_TYPE.deserializeBuilder(object.get(KEY_SPAWNER_TYPES));
            }
            if (object.has(KEY_LOOT_TABLE)) {
                builder.lootTable = new ResourceLocation(object.get(KEY_LOOT_TABLE).getAsString());
            }
            return builder;
        }

        @Override
        public JsonElement serialize(Builder builder, Type type, JsonSerializationContext context) {
            JsonObject object = new JsonObject();
            if (builder.settings != null)
                object.add(KEY_SETTINGS, context.serialize(builder.settings));

            JsonObject blueprints = new JsonObject();
            if (builder.rooms != null)
                blueprints.add(KEY_ROOMS, IRandom.BLUEPRINT.serializeBuilder(builder.rooms));
            if (builder.corridorSegments != null)
                blueprints.add(KEY_CORRIDOR_SEGMENTS, IRandom.BLUEPRINT.serializeBuilder(builder.corridorSegments));
            if (builder.corridorSideSegments != null)
                blueprints.add(KEY_CORRIDOR_SIDE_SEGMENTS, IRandom.BLUEPRINT.serializeBuilder(builder.corridorSideSegments));
            if (builder.upperStaircaseRooms != null)
                blueprints.add(KEY_UPPER_STAIRCASE_ROOMS, IRandom.BLUEPRINT.serializeBuilder(builder.upperStaircaseRooms));
            if (builder.lowerStaircaseRooms != null)
                blueprints.add(KEY_LOWER_STAIRCASE_ROOMS, IRandom.BLUEPRINT.serializeBuilder(builder.lowerStaircaseRooms));
            if (builder.clusterRooms != null) {
                blueprints.add(KEY_CLUSTER_ROOMS, ROOM_SET.serializeBuilder(builder.clusterRooms));
            }
            if (builder.specialRooms != null) {
                blueprints.add(KEY_SPECIAL_ROOMS, JSONUtils.serializeList(builder.specialRooms, context::serialize));
            }

            if (!blueprints.entrySet().isEmpty()) {
                object.add(KEY_BLUEPRINTS, blueprints);
            }

            if (builder.spawnerTypes != null) {
                object.add(KEY_SPAWNER_TYPES, IRandom.SPAWNER_TYPE.serializeBuilder(builder.spawnerTypes));
            }

            if (builder.lootTable != null) {
                object.addProperty(KEY_LOOT_TABLE, builder.lootTable.toString());
            }
            return object;
        }
    }
}
