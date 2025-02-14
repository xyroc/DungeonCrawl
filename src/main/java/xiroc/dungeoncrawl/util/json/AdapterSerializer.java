package xiroc.dungeoncrawl.util.json;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

import java.lang.reflect.Type;
import java.util.function.Function;

public record AdapterSerializer<FROM,TO>(Type fromType, Function<FROM, TO> convert, Function<TO, FROM> convertBack) implements JsonSerializer<TO>, JsonDeserializer<TO> {
    @Override
    public TO deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        final FROM other = context.deserialize(json, fromType);
        return convert.apply(other);
    }

    @Override
    public JsonElement serialize(TO src, Type typeOfSrc, JsonSerializationContext context) {
        final FROM other = convertBack.apply(src);
        return context.serialize(other, fromType);
    }
}
