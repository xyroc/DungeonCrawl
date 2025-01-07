package xiroc.dungeoncrawl.dungeon.type.level;

import com.google.common.collect.ImmutableList;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import xiroc.dungeoncrawl.datapack.registry.Delegate;
import xiroc.dungeoncrawl.dungeon.blueprint.Blueprint;
import xiroc.dungeoncrawl.util.JSONUtils;
import xiroc.dungeoncrawl.util.random.IRandom;

import java.lang.reflect.Type;

public record CorridorStyle(ImmutableList<IRandom<Delegate<Blueprint>>> segments) {
    public static class Serializer implements JsonSerializer<CorridorStyle>, JsonDeserializer<CorridorStyle> {
        private static final String KEY_SEGMENTS = "segments";

        @Override
        public CorridorStyle deserialize(JsonElement json, Type type, JsonDeserializationContext context) throws JsonParseException {
            JsonObject object = json.getAsJsonObject();
            final var segments = JSONUtils.deserializeList(object.getAsJsonArray(KEY_SEGMENTS), IRandom.BLUEPRINT::deserialize);
            return new CorridorStyle(segments);
        }

        @Override
        public JsonElement serialize(CorridorStyle corridorStyle, Type type, JsonSerializationContext context) {
            JsonObject object = new JsonObject();
            object.add(KEY_SEGMENTS, JSONUtils.serializeList(corridorStyle.segments, IRandom.BLUEPRINT::serialize));
            return object;
        }
    }
}
