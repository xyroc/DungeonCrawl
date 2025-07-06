package xiroc.dungeoncrawl.dungeon.blueprint.feature.settings;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import com.mojang.serialization.JsonOps;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.storage.loot.LootTable;
import xiroc.dungeoncrawl.dungeon.generator.level.LevelGenerator;
import xiroc.dungeoncrawl.dungeon.tier.TieredResource;

import java.lang.reflect.Type;
import java.util.Optional;

public record ChestSettings(Optional<TieredResource<ResourceKey<LootTable>>> lootTable) {
    public ChestSettings() {
        this(Optional.empty());
    }

    public ResourceKey<LootTable> getLootTable(LevelGenerator levelGenerator) {
        return lootTable.map(lootTables -> lootTables.forTier(levelGenerator.stage))
                .orElse(levelGenerator.levelType.lootTable());
    }

    public static class Serializer implements JsonSerializer<ChestSettings>, JsonDeserializer<ChestSettings> {
        private static final String KEY_LOOT_TABLE = "loot_table";

        @Override
        public ChestSettings deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext context) throws JsonParseException {
            final JsonObject object = jsonElement.getAsJsonObject();
            final Optional<TieredResource<ResourceKey<LootTable>>> lootTable = object.has(KEY_LOOT_TABLE) ?
                    Optional.of(TieredResource.Codecs.LOOT_TABLE.parse(JsonOps.INSTANCE, object.get(KEY_LOOT_TABLE)).result().orElseThrow().build()) :
                    Optional.empty();
            return new ChestSettings(lootTable);
        }

        @Override
        public JsonElement serialize(ChestSettings chestSettings, Type type, JsonSerializationContext context) {
            final JsonObject object = new JsonObject();
            chestSettings.lootTable.ifPresent(lootTables ->
                    object.add(KEY_LOOT_TABLE, TieredResource.Codecs.LOOT_TABLE.encodeStart(JsonOps.INSTANCE, new TieredResource.Builder<>(lootTables)).result().orElseThrow()));
            return object;
        }
    }
}
