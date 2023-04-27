package xiroc.dungeoncrawl.dungeon.blueprint.feature.configuration;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import net.minecraft.resources.ResourceLocation;
import xiroc.dungeoncrawl.util.random.value.RandomValue;

import java.lang.reflect.Type;

public class FeatureConfiguration {
    public final ResourceLocation positions;
    public final RandomValue amount;

    public FeatureConfiguration(ResourceLocation positions, RandomValue amount) {
        this.positions = positions;
        this.amount = amount;
    }

    // For convenient json deserialization
    public FeatureConfiguration(FeatureConfiguration configuration) {
        this(configuration.positions, configuration.amount);
    }

    public static class Serializer implements JsonSerializer<FeatureConfiguration>, JsonDeserializer<FeatureConfiguration> {
        @Override
        public FeatureConfiguration deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
            JsonObject object = json.getAsJsonObject();
            ResourceLocation positions = new ResourceLocation(object.get(SharedSerializationConstants.KEY_POSITIONS).getAsString());
            RandomValue amount = context.deserialize(object.get(SharedSerializationConstants.KEY_AMOUNT), RandomValue.class);
            return new FeatureConfiguration(positions, amount);
        }

        @Override
        public JsonElement serialize(FeatureConfiguration src, Type typeOfSrc, JsonSerializationContext context) {
            JsonObject object = new JsonObject();
            object.addProperty(SharedSerializationConstants.KEY_POSITIONS, src.positions.toString());
            object.add(SharedSerializationConstants.KEY_AMOUNT, context.serialize(src.amount));
            return object;
        }
    }

    interface SharedSerializationConstants {
        String KEY_POSITIONS = "positions";
        String KEY_AMOUNT = "amount";
        String KEY_LOOT_TABLE = "loot_table";
    }
}
