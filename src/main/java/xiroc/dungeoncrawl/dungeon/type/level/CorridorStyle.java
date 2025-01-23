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

import javax.annotation.Nullable;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public record CorridorStyle(ImmutableList<IRandom<Delegate<Blueprint>>> segments, IRandom<Delegate<Blueprint>> sideSegments) {
    public static class Serializer implements JsonSerializer<CorridorStyle>, JsonDeserializer<CorridorStyle> {
        private static final String KEY_SEGMENTS = "segments";
        private static final String KEY_SIDE_SEGMENTS = "side_segments";

        @Override
        public CorridorStyle deserialize(JsonElement json, Type type, JsonDeserializationContext context) throws JsonParseException {
            JsonObject object = json.getAsJsonObject();
            final var segments = JSONUtils.deserializeList(object.getAsJsonArray(KEY_SEGMENTS), IRandom.BLUEPRINT::deserialize);
            final var sideSegments = IRandom.BLUEPRINT.deserialize(object.get(KEY_SIDE_SEGMENTS));
            return new CorridorStyle(segments, sideSegments);
        }

        @Override
        public JsonElement serialize(CorridorStyle corridorStyle, Type type, JsonSerializationContext context) {
            JsonObject object = new JsonObject();
            object.add(KEY_SEGMENTS, JSONUtils.serializeList(corridorStyle.segments, IRandom.BLUEPRINT::serialize));
            object.add(KEY_SIDE_SEGMENTS, IRandom.BLUEPRINT.serialize(corridorStyle.sideSegments));
            return object;
        }
    }

    public static class Builder {
        private final List<IRandom<Delegate<Blueprint>>> segments = new ArrayList<>();

        @Nullable
        private IRandom.Builder<Delegate<Blueprint>> sideSegments = null;

        public Builder segment(IRandom.Builder<Delegate<Blueprint>> segment) {
            segments.add(segment.build());
            return this;
        }

        public Builder sideSegments(@Nullable IRandom.Builder<Delegate<Blueprint>> sideSegments) {
            this.sideSegments = sideSegments;
            return this;
        }

        public CorridorStyle build() {
            Objects.requireNonNull(sideSegments, "No side segment blueprints were provided");
            return new CorridorStyle(ImmutableList.copyOf(segments), sideSegments.build());
        }
    }
}
