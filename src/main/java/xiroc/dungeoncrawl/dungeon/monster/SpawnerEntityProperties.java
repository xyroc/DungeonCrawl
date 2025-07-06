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

import javax.annotation.Nullable;
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
        @Nullable
        private IRandom.Builder<Item> mainHand = null;
        @Nullable
        private IRandom.Builder<Item> offHand = null;

        @Nullable
        private IRandom.Builder<Item> helmet = null;
        @Nullable
        private IRandom.Builder<Item> chestplate = null;
        @Nullable
        private IRandom.Builder<Item> leggings = null;
        @Nullable
        private IRandom.Builder<Item> boots = null;

        @Nullable
        private Float armorDropChance = null;
        @Nullable
        private Float handDropChance = null;

        public Builder copy(SpawnerEntityProperties properties) {
            this.mainHand = properties.mainHand().map(IRandom.Builder::copy).orElse(null);
            this.offHand = properties.offHand().map(IRandom.Builder::copy).orElse(null);
            this.helmet = properties.helmet().map(IRandom.Builder::copy).orElse(null);
            this.chestplate = properties.chestplate().map(IRandom.Builder::copy).orElse(null);
            this.leggings = properties.leggings().map(IRandom.Builder::copy).orElse(null);
            this.boots = properties.boots().map(IRandom.Builder::copy).orElse(null);
            this.armorDropChance = properties.armorDropChance().orElse(null);
            this.handDropChance = properties.handDropChance().orElse(null);
            return this;
        }

        public Builder mainHand(IRandom.Builder<Item> mainHand) {
            this.mainHand = mainHand;
            return this;
        }

        public Builder offHand(IRandom.Builder<Item> offHand) {
            this.offHand = offHand;
            return this;
        }

        public Builder helmet(IRandom.Builder<Item> helmet) {
            this.helmet = helmet;
            return this;
        }

        public Builder chestplate(IRandom.Builder<Item> chestplate) {
            this.chestplate = chestplate;
            return this;
        }

        public Builder leggings(IRandom.Builder<Item> leggings) {
            this.leggings = leggings;
            return this;
        }

        public Builder boots(IRandom.Builder<Item> boots) {
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
            this.mainHand = InheritingBuilder.inheritOrReplaceOrChoose(this.mainHand, from.mainHand);
            this.offHand = InheritingBuilder.inheritOrReplaceOrChoose(this.offHand, from.offHand);

            this.helmet = InheritingBuilder.inheritOrReplaceOrChoose(this.helmet, from.helmet);
            this.chestplate = InheritingBuilder.inheritOrReplaceOrChoose(this.chestplate, from.chestplate);
            this.leggings = InheritingBuilder.inheritOrReplaceOrChoose(this.leggings, from.leggings);
            this.boots = InheritingBuilder.inheritOrReplaceOrChoose(this.boots, from.boots);

            this.handDropChance = InheritingBuilder.choose(this.handDropChance, from.handDropChance);
            this.armorDropChance = InheritingBuilder.choose(this.armorDropChance, from.armorDropChance);
            return this;
        }

        public SpawnerEntityProperties build() {
            return new SpawnerEntityProperties(
                    Optional.ofNullable(mainHand).map(IRandom.Builder::build),
                    Optional.ofNullable(offHand).map(IRandom.Builder::build),
                    Optional.ofNullable(helmet).map(IRandom.Builder::build),
                    Optional.ofNullable(chestplate).map(IRandom.Builder::build),
                    Optional.ofNullable(leggings).map(IRandom.Builder::build),
                    Optional.ofNullable(boots).map(IRandom.Builder::build),
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
            Builder builder = new Builder();
            JsonObject object = json.getAsJsonObject();
            if (object.has(KEY_EQUIPMENT)) {
                JsonObject equipment = object.getAsJsonObject(KEY_EQUIPMENT);
                builder.mainHand = JSONUtils.deserializeOrNull(equipment, KEY_EQUIPMENT_MAIN_HAND, items -> JSONUtils.parse(items, IRandom.BaseCodecs.ITEM));
                builder.offHand = JSONUtils.deserializeOrNull(equipment, KEY_EQUIPMENT_OFF_HAND, items -> JSONUtils.parse(items, IRandom.BaseCodecs.ITEM));
                builder.helmet = JSONUtils.deserializeOrNull(equipment, KEY_EQUIPMENT_HELMET, items -> JSONUtils.parse(items, IRandom.BaseCodecs.ITEM));
                builder.chestplate = JSONUtils.deserializeOrNull(equipment, KEY_EQUIPMENT_CHESTPLATE, items -> JSONUtils.parse(items, IRandom.BaseCodecs.ITEM));
                builder.leggings = JSONUtils.deserializeOrNull(equipment, KEY_EQUIPMENT_LEGGINGS, items -> JSONUtils.parse(items, IRandom.BaseCodecs.ITEM));
                builder.boots = JSONUtils.deserializeOrNull(equipment, KEY_EQUIPMENT_BOOTS, items -> JSONUtils.parse(items, IRandom.BaseCodecs.ITEM));

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
            JsonObject equipment = new JsonObject();

            JSONUtils.serializeIfNonNull(equipment, KEY_EQUIPMENT_MAIN_HAND, builder.mainHand, items -> JSONUtils.encode(items, IRandom.BaseCodecs.ITEM));
            JSONUtils.serializeIfNonNull(equipment, KEY_EQUIPMENT_OFF_HAND, builder.offHand, items -> JSONUtils.encode(items, IRandom.BaseCodecs.ITEM));
            JSONUtils.serializeIfNonNull(equipment, KEY_EQUIPMENT_HELMET, builder.helmet, items -> JSONUtils.encode(items, IRandom.BaseCodecs.ITEM));
            JSONUtils.serializeIfNonNull(equipment, KEY_EQUIPMENT_CHESTPLATE, builder.chestplate, items -> JSONUtils.encode(items, IRandom.BaseCodecs.ITEM));
            JSONUtils.serializeIfNonNull(equipment, KEY_EQUIPMENT_LEGGINGS, builder.leggings, items -> JSONUtils.encode(items, IRandom.BaseCodecs.ITEM));
            JSONUtils.serializeIfNonNull(equipment, KEY_EQUIPMENT_BOOTS, builder.boots, items -> JSONUtils.encode(items, IRandom.BaseCodecs.ITEM));

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