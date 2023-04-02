package xiroc.dungeoncrawl.dungeon.blueprint;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

import java.lang.reflect.Type;

public record BlueprintSettings(int floorLevel) {
    private static final int DEFAULT_FLOOR_LEVEL = 0;

    public static Builder builder() {
        return new Builder();
    }

    public BlueprintSettings(Builder builder) {
        this(builder.floorLevel);
    }

    public static class Builder {
        private int floorLevel = DEFAULT_FLOOR_LEVEL;

        public Builder floorLevel(int floorLevel) {
            this.floorLevel = floorLevel;
            return this;
        }

        public BlueprintSettings build() {
            return new BlueprintSettings(this);
        }
    }

    public static class Serializer implements JsonSerializer<BlueprintSettings>, JsonDeserializer<BlueprintSettings> {
        private static final String KEY_FLOOR_LEVEL = "floor_level";

        @Override
        public BlueprintSettings deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
            JsonObject object = json.getAsJsonObject();
            Builder builder = new Builder();
            if (object.has(KEY_FLOOR_LEVEL)) {
                builder.floorLevel = object.get(KEY_FLOOR_LEVEL).getAsInt();
            }
            return new BlueprintSettings(builder);
        }

        @Override
        public JsonElement serialize(BlueprintSettings src, Type typeOfSrc, JsonSerializationContext context) {
            JsonObject json = new JsonObject();
            if (src.floorLevel != DEFAULT_FLOOR_LEVEL) {
                json.addProperty(KEY_FLOOR_LEVEL, src.floorLevel);
            }
            return json;
        }
    }
}
