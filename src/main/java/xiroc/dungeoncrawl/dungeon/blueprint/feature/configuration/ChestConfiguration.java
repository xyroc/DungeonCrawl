package xiroc.dungeoncrawl.dungeon.blueprint.feature.configuration;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import net.minecraft.resources.ResourceLocation;

import javax.annotation.Nullable;
import java.lang.reflect.Type;

public class ChestConfiguration extends FeatureConfiguration {
    @Nullable
    public final ResourceLocation lootTable;

    public ChestConfiguration(FeatureConfiguration baseConfiguration, @Nullable ResourceLocation lootTable) {
        super(baseConfiguration);
        this.lootTable = lootTable;
    }

    public static class Serializer implements JsonSerializer<ChestConfiguration>, JsonDeserializer<ChestConfiguration> {
        @Override
        public ChestConfiguration deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
            FeatureConfiguration baseConfiguration = context.deserialize(json, FeatureConfiguration.class);
            JsonObject object = json.getAsJsonObject();
            ResourceLocation lootTable = object.has(SharedSerializationConstants.KEY_LOOT_TABLE) ?
                    new ResourceLocation(object.get(SharedSerializationConstants.KEY_LOOT_TABLE).getAsString()) : null;
            return new ChestConfiguration(baseConfiguration, lootTable);
        }

        @Override
        public JsonElement serialize(ChestConfiguration src, Type typeOfSrc, JsonSerializationContext context) {
            JsonObject object = context.serialize(src, FeatureConfiguration.class).getAsJsonObject();
            if (src.lootTable != null) {
                object.addProperty(SharedSerializationConstants.KEY_LOOT_TABLE, src.lootTable.toString());
            }
            return object;
        }
    }
}
