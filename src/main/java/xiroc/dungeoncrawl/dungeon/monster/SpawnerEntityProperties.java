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
import xiroc.dungeoncrawl.util.random.IRandom;

import java.lang.reflect.Type;

public class SpawnerEntityProperties {
    protected final IRandom<Item> mainHand;
    protected final IRandom<Item> offHand;

    protected final IRandom<Item> helmet;
    protected final IRandom<Item> chestplate;
    protected final IRandom<Item> leggings;
    protected final IRandom<Item> boots;

    protected final Float handDropChance;
    protected final Float armorDropChance;

    public SpawnerEntityProperties(IRandom<Item> mainHand,
                                   IRandom<Item> offHand,
                                   IRandom<Item> helmet,
                                   IRandom<Item> chestplate,
                                   IRandom<Item> leggings,
                                   IRandom<Item> boots,
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
                builder.mainHand = JSONUtils.deserializeOrNull(equipment, KEY_EQUIPMENT_MAIN_HAND, IRandom.ITEM::deserialize);
                builder.offHand = JSONUtils.deserializeOrNull(equipment, KEY_EQUIPMENT_OFF_HAND, IRandom.ITEM::deserialize);
                builder.helmet = JSONUtils.deserializeOrNull(equipment, KEY_EQUIPMENT_HELMET, IRandom.ITEM::deserialize);
                builder.chestplate = JSONUtils.deserializeOrNull(equipment, KEY_EQUIPMENT_CHESTPLATE, IRandom.ITEM::deserialize);
                builder.leggings = JSONUtils.deserializeOrNull(equipment, KEY_EQUIPMENT_LEGGINGS, IRandom.ITEM::deserialize);
                builder.boots = JSONUtils.deserializeOrNull(equipment, KEY_EQUIPMENT_BOOTS, IRandom.ITEM::deserialize);
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

            JSONUtils.serializeIfNonNull(equipment, KEY_EQUIPMENT_MAIN_HAND, src.mainHand, IRandom.ITEM::serialize);
            JSONUtils.serializeIfNonNull(equipment, KEY_EQUIPMENT_OFF_HAND, src.offHand, IRandom.ITEM::serialize);
            JSONUtils.serializeIfNonNull(equipment, KEY_EQUIPMENT_HELMET, src.helmet, IRandom.ITEM::serialize);
            JSONUtils.serializeIfNonNull(equipment, KEY_EQUIPMENT_CHESTPLATE, src.chestplate, IRandom.ITEM::serialize);
            JSONUtils.serializeIfNonNull(equipment, KEY_EQUIPMENT_LEGGINGS, src.leggings, IRandom.ITEM::serialize);
            JSONUtils.serializeIfNonNull(equipment, KEY_EQUIPMENT_BOOTS, src.boots, IRandom.ITEM::serialize);

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
        private IRandom<Item> mainHand = null;
        private IRandom<Item> offHand = null;

        private IRandom<Item> helmet = null;
        private IRandom<Item> chestplate = null;
        private IRandom<Item> leggings = null;
        private IRandom<Item> boots = null;

        private Float armorDropChance = null;
        private Float handDropChance = null;

        public Builder mainHand(IRandom<Item> mainHand) {
            this.mainHand = mainHand;
            return this;
        }

        public Builder offHand(IRandom<Item> offHand) {
            this.offHand = offHand;
            return this;
        }

        public Builder helmet(IRandom<Item> helmet) {
            this.helmet = helmet;
            return this;
        }

        public Builder chestplate(IRandom<Item> chestplate) {
            this.chestplate = chestplate;
            return this;
        }

        public Builder leggings(IRandom<Item> leggings) {
            this.leggings = leggings;
            return this;
        }

        public Builder boots(IRandom<Item> boots) {
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