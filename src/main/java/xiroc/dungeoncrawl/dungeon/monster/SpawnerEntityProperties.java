package xiroc.dungeoncrawl.dungeon.monster;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import net.minecraft.world.item.Item;
import xiroc.dungeoncrawl.util.JSONUtils;
import xiroc.dungeoncrawl.util.random.WeightedRandom;

import java.lang.reflect.Type;

public class SpawnerEntityProperties {
    protected final WeightedRandom<Item> mainHand;
    protected final WeightedRandom<Item> offHand;

    protected final WeightedRandom<Item> helmet;
    protected final WeightedRandom<Item> chestplate;
    protected final WeightedRandom<Item> leggings;
    protected final WeightedRandom<Item> boots;

    protected final Float handDropChance;
    protected final Float armorDropChance;

    public SpawnerEntityProperties(WeightedRandom<Item> mainHand,
                                   WeightedRandom<Item> offHand,
                                   WeightedRandom<Item> helmet,
                                   WeightedRandom<Item> chestplate,
                                   WeightedRandom<Item> leggings,
                                   WeightedRandom<Item> boots,
                                   Float handDropChance,
                                   Float armorDropChance) {
        this.mainHand = mainHand;
        this.offHand = offHand;
        this.helmet = helmet;
        this.chestplate = chestplate;
        this.leggings = leggings;
        this.boots = boots;
        this.handDropChance = handDropChance;
        this.armorDropChance = armorDropChance;
    }

    public static class Serializer implements JsonSerializer<SpawnerEntityProperties>, JsonDeserializer<SpawnerEntityProperties> {
        private static final String KEY_EQUIPMENT = "equipment";
        private static final String KEY_EQUIPMENT_MAIN_HAND = "main_hand";
        private static final String KEY_EQUIPMENT_OFF_HAND = "off_hand";
        private static final String KEY_EQUIPMENT_HELMET = "helmet";
        private static final String KEY_EQUIPMENT_CHESTPLATE = "chestplate";
        private static final String KEY_EQUIPMENT_LEGGINGS = "leggings";
        private static final String KEY_EQUIPMENT_BOOTS = "boots";
        private static final String KEY_EQUIPMENT_DROP_CHANCES = "drop_chances";
        private static final String KEY_EQUIPMENT_DROP_CHANCE_HAND = "hand";
        private static final String KEY_EQUIPMENT_DROP_CHANCE_ARMOR = "armor";

        @Override
        public SpawnerEntityProperties deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
            Builder builder = new Builder();
            JsonObject object = json.getAsJsonObject();
            if (object.has(KEY_EQUIPMENT)) {
                JsonObject equipment = object.getAsJsonObject(KEY_EQUIPMENT);
                builder.mainHand = JSONUtils.deserializeOrNull(equipment, KEY_EQUIPMENT_MAIN_HAND, WeightedRandom.ITEM::deserialize);
                builder.offHand = JSONUtils.deserializeOrNull(equipment, KEY_EQUIPMENT_OFF_HAND, WeightedRandom.ITEM::deserialize);
                builder.helmet = JSONUtils.deserializeOrNull(equipment, KEY_EQUIPMENT_HELMET, WeightedRandom.ITEM::deserialize);
                builder.chestplate = JSONUtils.deserializeOrNull(equipment, KEY_EQUIPMENT_CHESTPLATE, WeightedRandom.ITEM::deserialize);
                builder.leggings = JSONUtils.deserializeOrNull(equipment, KEY_EQUIPMENT_LEGGINGS, WeightedRandom.ITEM::deserialize);
                builder.boots = JSONUtils.deserializeOrNull(equipment, KEY_EQUIPMENT_BOOTS, WeightedRandom.ITEM::deserialize);
                if (equipment.has(KEY_EQUIPMENT_DROP_CHANCES)) {
                    JsonObject dropChances = equipment.getAsJsonObject(KEY_EQUIPMENT_DROP_CHANCES);
                    builder.handDropChance = JSONUtils.deserializeOrNull(dropChances, KEY_EQUIPMENT_DROP_CHANCE_HAND, JsonElement::getAsFloat);
                    builder.armorDropChance = JSONUtils.deserializeOrNull(dropChances, KEY_EQUIPMENT_DROP_CHANCE_ARMOR, JsonElement::getAsFloat);
                }
            }
            return builder.build();
        }

        @Override
        public JsonElement serialize(SpawnerEntityProperties src, Type typeOfSrc, JsonSerializationContext context) {
            JsonObject properties = new JsonObject();
            JsonObject equipment = new JsonObject();

            JSONUtils.serializeIfNonNull(equipment, KEY_EQUIPMENT_MAIN_HAND, src.mainHand, WeightedRandom.ITEM::serialize);
            JSONUtils.serializeIfNonNull(equipment, KEY_EQUIPMENT_OFF_HAND, src.offHand, WeightedRandom.ITEM::serialize);
            JSONUtils.serializeIfNonNull(equipment, KEY_EQUIPMENT_HELMET, src.helmet, WeightedRandom.ITEM::serialize);
            JSONUtils.serializeIfNonNull(equipment, KEY_EQUIPMENT_CHESTPLATE, src.chestplate, WeightedRandom.ITEM::serialize);
            JSONUtils.serializeIfNonNull(equipment, KEY_EQUIPMENT_LEGGINGS, src.leggings, WeightedRandom.ITEM::serialize);
            JSONUtils.serializeIfNonNull(equipment, KEY_EQUIPMENT_BOOTS, src.boots, WeightedRandom.ITEM::serialize);

            JsonObject dropChances = new JsonObject();
            JSONUtils.serializeIfNonNull(dropChances, KEY_EQUIPMENT_DROP_CHANCE_HAND, src.handDropChance, JsonPrimitive::new);
            JSONUtils.serializeIfNonNull(dropChances, KEY_EQUIPMENT_DROP_CHANCE_ARMOR, src.armorDropChance, JsonPrimitive::new);

            if (!dropChances.entrySet().isEmpty()) {
                equipment.add(KEY_EQUIPMENT_DROP_CHANCES, dropChances);
            }

            if (!equipment.entrySet().isEmpty()) {
                properties.add(KEY_EQUIPMENT, equipment);
            }
            return properties;
        }
    }

    public static class Builder {
        private WeightedRandom<Item> mainHand = null;
        private WeightedRandom<Item> offHand = null;

        private WeightedRandom<Item> helmet = null;
        private WeightedRandom<Item> chestplate = null;
        private WeightedRandom<Item> leggings = null;
        private WeightedRandom<Item> boots = null;

        private Float armorDropChance = null;
        private Float handDropChance = null;

        public Builder mainHand(WeightedRandom<Item> mainHand) {
            this.mainHand = mainHand;
            return this;
        }

        public Builder offHand(WeightedRandom<Item> offHand) {
            this.offHand = offHand;
            return this;
        }

        public Builder helmet(WeightedRandom<Item> helmet) {
            this.helmet = helmet;
            return this;
        }

        public Builder chestplate(WeightedRandom<Item> chestplate) {
            this.chestplate = chestplate;
            return this;
        }

        public Builder leggings(WeightedRandom<Item> leggings) {
            this.leggings = leggings;
            return this;
        }

        public Builder boots(WeightedRandom<Item> boots) {
            this.boots = boots;
            return this;
        }

        public Builder handDropChance(Float handDropChance) {
            this.handDropChance = handDropChance;
            return this;
        }

        public Builder armorDropChance(Float armorDropChance) {
            this.armorDropChance = armorDropChance;
            return this;
        }

        public SpawnerEntityProperties build() {
            return new SpawnerEntityProperties(mainHand, offHand, helmet, chestplate, leggings, boots, handDropChance, armorDropChance);
        }
    }
}