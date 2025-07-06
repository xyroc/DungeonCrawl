package xiroc.dungeoncrawl.dungeon.type.level;

import com.google.gson.*;
import xiroc.dungeoncrawl.datapack.registry.Delegate;
import xiroc.dungeoncrawl.dungeon.blueprint.Blueprint;
import xiroc.dungeoncrawl.util.JSONUtils;
import xiroc.dungeoncrawl.util.random.IRandom;
import xiroc.dungeoncrawl.util.random.value.RandomValue;

import java.lang.reflect.Type;

public record SpecialRoom(IRandom<Delegate<Blueprint>> variants, RandomValue minDepth, RandomValue amount) {
    public static class Serializer implements JsonSerializer<SpecialRoom>, JsonDeserializer<SpecialRoom> {
        private static final String KEY_VARIANTS = "variants";
        private static final String KEY_MIN_DEPTH = "min_depth";
        private static final String KEY_AMOUNT = "amount";

        @Override
        public SpecialRoom deserialize(JsonElement json, Type type, JsonDeserializationContext context) throws JsonParseException {
            JsonObject object = json.getAsJsonObject();
            final IRandom<Delegate<Blueprint>> variants = context.deserialize(object.get(KEY_VARIANTS), Blueprint.Types.RANDOM);
            final RandomValue minDepth = JSONUtils.parse(object.get(KEY_MIN_DEPTH), RandomValue.CODEC);
            final RandomValue amount = JSONUtils.parse(object.get(KEY_AMOUNT), RandomValue.CODEC);
            return new SpecialRoom(variants, minDepth, amount);
        }

        @Override
        public JsonElement serialize(SpecialRoom specialRoom, Type type, JsonSerializationContext context) {
            JsonObject object = new JsonObject();
            object.add(KEY_VARIANTS, context.serialize(specialRoom.variants, Blueprint.Types.RANDOM));
            object.add(KEY_MIN_DEPTH, JSONUtils.encode(specialRoom.minDepth, RandomValue.CODEC));
            object.add(KEY_AMOUNT, JSONUtils.encode(specialRoom.amount, RandomValue.CODEC));
            return object;
        }
    }
}
