package xiroc.dungeoncrawl.dungeon.type;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import net.minecraft.resources.ResourceLocation;
import xiroc.dungeoncrawl.datapack.registry.Delegate;
import xiroc.dungeoncrawl.dungeon.blueprint.Blueprint;
import xiroc.dungeoncrawl.dungeon.generator.level.LevelGeneratorSettings;
import xiroc.dungeoncrawl.dungeon.monster.SpawnerType;
import xiroc.dungeoncrawl.util.random.IRandom;

import javax.annotation.Nullable;
import java.lang.reflect.Type;

public record LevelType(LevelGeneratorSettings settings,
                        IRandom<Delegate<Blueprint>> rooms,
                        IRandom<Delegate<Blueprint>> corridorSegments,
                        IRandom<Delegate<Blueprint>> corridorSideSegments,
                        IRandom<Delegate<Blueprint>> upperStaircaseRooms,
                        IRandom<Delegate<Blueprint>> lowerStaircaseRooms,
                        @Nullable IRandom<IRandom<Delegate<Blueprint>>> clusterRooms,
                        IRandom<Delegate<SpawnerType>> spawners,
                        @Nullable ResourceLocation lootTable) {

    private static final IRandom.Serializer<IRandom<Delegate<Blueprint>>> ROOM_SET =
            new IRandom.Serializer<>(IRandom.BLUEPRINT::deserialize, IRandom.BLUEPRINT::serialize, "rooms");

    public static class Serializer implements JsonSerializer<LevelType>, JsonDeserializer<LevelType> {
        private static final String KEY_SETTINGS = "settings";
        private static final String KEY_BLUEPRINTS = "blueprints";
        private static final String KEY_ROOMS = "rooms";
        private static final String KEY_CORRIDOR_SEGMENTS = "corridor_segments";
        private static final String KEY_CORRIDOR_SIDE_SEGMENTS = "corridor_side_segments";
        private static final String KEY_CLUSTER_ROOMS = "cluster_rooms";
        private static final String KEY_UPPER_STAIRCASE_ROOMS = "upper_staircase";
        private static final String KEY_LOWER_STAIRCASE_ROOMS = "lower_staircase";
        private static final String KEY_SPAWNERS = "spawners";
        private static final String KEY_LOOT_TABLE = "loot_table";

        @Override
        public LevelType deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
            JsonObject object = jsonElement.getAsJsonObject();
            LevelGeneratorSettings settings = jsonDeserializationContext.deserialize(object.get(KEY_SETTINGS), LevelGeneratorSettings.class);
            JsonObject blueprints = object.get(KEY_BLUEPRINTS).getAsJsonObject();
            IRandom<Delegate<Blueprint>> rooms = IRandom.BLUEPRINT.deserialize(blueprints.get(KEY_ROOMS));
            IRandom<Delegate<Blueprint>> corridorSegments = IRandom.BLUEPRINT.deserialize(blueprints.get(KEY_CORRIDOR_SEGMENTS));
            IRandom<Delegate<Blueprint>> corridorSideSegments = IRandom.BLUEPRINT.deserialize(blueprints.get(KEY_CORRIDOR_SIDE_SEGMENTS));
            IRandom<Delegate<Blueprint>> upperStaircaseRooms = IRandom.BLUEPRINT.deserialize(blueprints.get(KEY_UPPER_STAIRCASE_ROOMS));
            IRandom<Delegate<Blueprint>> lowerStaircaseRooms = IRandom.BLUEPRINT.deserialize(blueprints.get(KEY_LOWER_STAIRCASE_ROOMS));
            IRandom<IRandom<Delegate<Blueprint>>> clusterRooms = null;
            if (object.has(KEY_CLUSTER_ROOMS)) {
                clusterRooms = ROOM_SET.deserialize(object.get(KEY_CLUSTER_ROOMS));
            }
            IRandom<Delegate<SpawnerType>> spawners = IRandom.SPAWNER_TYPE.deserialize(object.get(KEY_SPAWNERS));
            ResourceLocation lootTable = object.has(KEY_LOOT_TABLE) ? new ResourceLocation(object.get(KEY_LOOT_TABLE).getAsString()) : null;
            return new LevelType(settings, rooms, corridorSegments, corridorSideSegments, upperStaircaseRooms, lowerStaircaseRooms, clusterRooms, spawners, lootTable);
        }

        @Override
        public JsonElement serialize(LevelType levelType, Type type, JsonSerializationContext jsonSerializationContext) {
            JsonObject object = new JsonObject();
            object.add(KEY_SETTINGS, jsonSerializationContext.serialize(levelType.settings));
            JsonObject blueprints = new JsonObject();
            blueprints.add(KEY_ROOMS, IRandom.BLUEPRINT.serialize(levelType.rooms));
            blueprints.add(KEY_CORRIDOR_SEGMENTS, IRandom.BLUEPRINT.serialize(levelType.corridorSegments));
            blueprints.add(KEY_CORRIDOR_SIDE_SEGMENTS, IRandom.BLUEPRINT.serialize(levelType.corridorSideSegments));
            blueprints.add(KEY_UPPER_STAIRCASE_ROOMS, IRandom.BLUEPRINT.serialize(levelType.upperStaircaseRooms));
            blueprints.add(KEY_LOWER_STAIRCASE_ROOMS, IRandom.BLUEPRINT.serialize(levelType.lowerStaircaseRooms));
            if (levelType.clusterRooms != null) {
                blueprints.add(KEY_CLUSTER_ROOMS, ROOM_SET.serialize(levelType.clusterRooms));
            }
            object.add(KEY_BLUEPRINTS, blueprints);
            object.add(KEY_SPAWNERS, IRandom.SPAWNER_TYPE.serialize(levelType.spawners));
            if (levelType.lootTable != null) {
                object.addProperty(KEY_LOOT_TABLE, levelType.lootTable.toString());
            }
            return object;
        }
    }
}
