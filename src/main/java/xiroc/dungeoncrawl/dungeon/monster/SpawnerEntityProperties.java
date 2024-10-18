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
import xiroc.dungeoncrawl.datapack.registry.InheritingBuilder;
import xiroc.dungeoncrawl.util.JSONUtils;
import xiroc.dungeoncrawl.util.random.IRandom;

import java.lang.reflect.Type;
import java.util.Optional;

public record SpawnerEntityProperties(Optional<IRandom<Item>> mainHand,
                                      Optional<IRandom<Item>> offHand,
                                      Optional<IRandom<Item>> helmet,
                                      Optional<IRandom<Item>> chestplate,
                                      Optional<IRandom<Item>> leggings,
                                      Optional<IRandom<Item>> boots,
                                      Optional<Float> handDropChance,
                                      Optional<Float> armorDropChance) {

    public static class Builder extends InheritingBuilder<SpawnerEntityProperties, Builder> {
        private IRandom<Item> mainHand = null;
        private IRandom<Item> offHand = null;

        private IRandom<Item> helmet = null;
        private IRandom<Item> chestplate = null;
        private IRandom<Item> leggings = null;
        private IRandom<Item> boots = null;

        private Float armorDropChance = null;
        private Float handDropChance = null;

        public Builder copy(SpawnerEntityProperties properties) {
            this.mainHand = properties.mainHand().orElse(null);
            this.offHand = properties.offHand().orElse(null);
            this.helmet = properties.helmet().orElse(null);
            this.chestplate = properties.chestplate().orElse(null);
            this.leggings = properties.leggings().orElse(null);
            this.boots = properties.boots().orElse(null);
            this.armorDropChance = properties.armorDropChance().orElse(null);
            this.handDropChance = properties.handDropChance().orElse(null);
            return this;
        }

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

        @Override
        public Builder inherit(Builder from) {
            this.mainHand = InheritingBuilder.choose(this.mainHand, from.mainHand);
            this.offHand = InheritingBuilder.choose(this.offHand, from.offHand);

            this.helmet = InheritingBuilder.choose(this.helmet, from.helmet);
            this.chestplate = InheritingBuilder.choose(this.chestplate, from.chestplate);
            this.leggings = InheritingBuilder.choose(this.leggings, from.leggings);
            this.boots = InheritingBuilder.choose(this.boots, from.boots);

            this.handDropChance = InheritingBuilder.choose(this.handDropChance, from.handDropChance);
            this.armorDropChance = InheritingBuilder.choose(this.armorDropChance, from.armorDropChance);
            return this;
        }

        public SpawnerEntityProperties build() {
            return new SpawnerEntityProperties(Optional.ofNullable(mainHand),
                    Optional.ofNullable(offHand),
                    Optional.ofNullable(helmet),
                    Optional.ofNullable(chestplate),
                    Optional.ofNullable(leggings),
                    Optional.ofNullable(boots),
                    Optional.ofNullable(handDropChance),
                    Optional.ofNullable(armorDropChance));
        }
    }

    private interface SerializationKeys {
        String KEY_EQUIPMENT = "equipment";
        String KEY_EQUIPMENT_MAIN_HAND = "main_hand";
        String KEY_EQUIPMENT_OFF_HAND = "off_hand";
        String KEY_EQUIPMENT_HELMET = "helmet";
        String KEY_EQUIPMENT_CHESTPLATE = "chestplate";
        String KEY_EQUIPMENT_LEGGINGS = "leggings";
        String KEY_EQUIPMENT_BOOTS = "boots";
        String KEY_EQUIPMENT_DROP_CHANCES = "drop_chances";
        String KEY_EQUIPMENT_DROP_CHANCE_HAND = "hand";
        String KEY_EQUIPMENT_DROP_CHANCE_ARMOR = "armor";
    }

    public static class BuilderSerializer implements SerializationKeys, JsonSerializer<Builder>, JsonDeserializer<Builder> {
        @Override
        public Builder deserialize(JsonElement json, Type type, JsonDeserializationContext context) throws JsonParseException {
            JsonObject object = json.getAsJsonObject();
            Builder builder = new Builder();
            builder.deserializeBase(object);

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
            return builder;
        }

        @Override
        public JsonElement serialize(Builder builder, Type type, JsonSerializationContext context) {
            JsonObject properties = new JsonObject();
            builder.serializeBase(properties);

            JsonObject equipment = new JsonObject();

            JSONUtils.serializeIfNonNull(equipment, KEY_EQUIPMENT_MAIN_HAND, builder.mainHand, IRandom.ITEM::serialize);
            JSONUtils.serializeIfNonNull(equipment, KEY_EQUIPMENT_OFF_HAND, builder.offHand, IRandom.ITEM::serialize);
            JSONUtils.serializeIfNonNull(equipment, KEY_EQUIPMENT_HELMET, builder.helmet, IRandom.ITEM::serialize);
            JSONUtils.serializeIfNonNull(equipment, KEY_EQUIPMENT_CHESTPLATE, builder.chestplate, IRandom.ITEM::serialize);
            JSONUtils.serializeIfNonNull(equipment, KEY_EQUIPMENT_LEGGINGS, builder.leggings, IRandom.ITEM::serialize);
            JSONUtils.serializeIfNonNull(equipment, KEY_EQUIPMENT_BOOTS, builder.boots, IRandom.ITEM::serialize);

            JsonObject dropChances = new JsonObject();

            JSONUtils.serializeIfNonNull(dropChances, KEY_EQUIPMENT_DROP_CHANCE_HAND, builder.handDropChance, JsonPrimitive::new);
            JSONUtils.serializeIfNonNull(dropChances, KEY_EQUIPMENT_DROP_CHANCE_ARMOR, builder.armorDropChance, JsonPrimitive::new);

            if (!dropChances.entrySet().isEmpty()) {
                equipment.add(KEY_EQUIPMENT_DROP_CHANCES, dropChances);
            }

            if (!equipment.entrySet().isEmpty()) {
                properties.add(KEY_EQUIPMENT, equipment);
            }
            return properties;
        }
    }
}