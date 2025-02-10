package xiroc.dungeoncrawl.dungeon.blueprint.feature.settings;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import net.minecraft.resources.ResourceLocation;
import xiroc.dungeoncrawl.dungeon.generator.level.LevelGenerator;
import xiroc.dungeoncrawl.dungeon.tier.TieredResource;

import java.lang.reflect.Type;
import java.util.Optional;

public record ChestSettings(Optional<TieredResource<ResourceLocation>> lootTable) {
    public ChestSettings() {
        this(Optional.empty());
    }

    public ResourceLocation getLootTable(LevelGenerator levelGenerator) {
        return lootTable.map(lootTables -> lootTables.forTier(levelGenerator.stage))
                .orElse(levelGenerator.levelType.lootTable());
    }

    public static class Serializer implements JsonSerializer<ChestSettings>, JsonDeserializer<ChestSettings> {
        private static final String KEY_LOOT_TABLE = "loot_table";

        @Override
        public ChestSettings deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext context) throws JsonParseException {
            final JsonObject object = jsonElement.getAsJsonObject();
            final Optional<TieredResource<ResourceLocation>> lootTable = object.has(KEY_LOOT_TABLE) ?
                    Optional.of(context.<TieredResource.Builder<ResourceLocation>>deserialize(object.get(KEY_LOOT_TABLE), TieredResource.Types.IDENTIFIER).build()) :
                    Optional.empty();
            return new ChestSettings(lootTable);
        }

        @Override
        public JsonElement serialize(ChestSettings chestSettings, Type type, JsonSerializationContext context) {
            final JsonObject object = new JsonObject();
            chestSettings.lootTable.ifPresent(lootTables ->
                    object.add(KEY_LOOT_TABLE, context.serialize(new TieredResource.Builder<>(lootTables), TieredResource.Types.IDENTIFIER)));
            return object;
        }
    }
}
