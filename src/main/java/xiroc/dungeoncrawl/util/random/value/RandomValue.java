package xiroc.dungeoncrawl.util.random.value;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;

import java.lang.reflect.Type;
import java.util.Random;

public interface RandomValue {
    Gson GSON = new GsonBuilder()
            .registerTypeAdapter(RandomValue.class, new Deserializer())
            .registerTypeAdapter(Range.class, new Range.Serializer())
            .registerTypeAdapter(Constant.class, new Constant.Serializer())
            .create();

    static RandomValue deserialize(JsonElement json) {
        return GSON.fromJson(json, RandomValue.class);
    }

    int nextInt(Random random);

    /**
     * @return whether any value provided will be within the given bounds which are considered inclusive.
     */
    boolean isAlwaysWithin(int lowerBound, int upperBound);

    /**
     * @return whether any value provided will be greater than zero.
     */
    default boolean isAlwaysPositive() {
        return isAlwaysWithin(1, Integer.MAX_VALUE);
    }

    /**
     * @return whether any value provided will be greater than or equal to zero.
     */
    default boolean isAlwaysNonNegative() {
        return isAlwaysWithin(0, Integer.MAX_VALUE);
    }

    /**
     * @return whether any value provided will be less than zero.
     */
    default boolean isAlwaysNegative() {
        return isAlwaysWithin(Integer.MIN_VALUE, -1);
    }

    /**
     * @return whether any value provided will be less than or equal to zero.
     */
    default boolean isAlwaysNonPositive() {
        return isAlwaysWithin(Integer.MIN_VALUE, 0);
    }

    class Deserializer implements JsonDeserializer<RandomValue> {
        @Override
        public RandomValue deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
            if (json.isJsonPrimitive()) {
                return context.deserialize(json, Constant.class);
            }
            return context.deserialize(json, Range.class);
        }
    }
}
