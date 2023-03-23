package xiroc.dungeoncrawl.util.random.value;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

import java.lang.reflect.Type;
import java.util.Random;

public record Constant(int value) implements RandomValue {
    @Override
    public int nextInt(Random random) {
        return value;
    }

    @Override
    public boolean isAlwaysWithin(int lowerBound, int upperBound) {
        return value >= lowerBound && value <= upperBound;
    }

    public static class Serializer implements JsonSerializer<Constant>, JsonDeserializer<Constant> {
        @Override
        public Constant deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
            return new Constant(json.getAsInt());
        }

        @Override
        public JsonElement serialize(Constant src, Type typeOfSrc, JsonSerializationContext context) {
            return new JsonPrimitive(src.value);
        }
    }
}
