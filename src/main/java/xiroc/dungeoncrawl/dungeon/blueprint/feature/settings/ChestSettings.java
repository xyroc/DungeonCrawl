package xiroc.dungeoncrawl.dungeon.blueprint.feature.settings;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import net.minecraft.resources.ResourceLocation;

import java.lang.reflect.Type;
import java.util.Optional;

public record ChestSettings(Optional<ResourceLocation> lootTable) {
    public ChestSettings() {
        this(Optional.empty());
    }

    public static class Serializer implements JsonSerializer<ChestSettings>, JsonDeserializer<ChestSettings> {
        private static final String KEY_LOOT_TABLE = "loot_table";

        @Override
        public ChestSettings deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext context) throws JsonParseException {
            JsonObject object = jsonElement.getAsJsonObject();
            Optional<ResourceLocation> lootTable = Optional.ofNullable(object.has(KEY_LOOT_TABLE) ? new ResourceLocation(object.get(KEY_LOOT_TABLE).getAsString()) : null);
            return new ChestSettings(lootTable);
        }

        @Override
        public JsonElement serialize(ChestSettings chestSettings, Type type, JsonSerializationContext context) {
            JsonObject object = new JsonObject();
            chestSettings.lootTable.ifPresent(resourceLocation -> object.addProperty(KEY_LOOT_TABLE, resourceLocation.toString()));
            return object;
        }
    }
}
