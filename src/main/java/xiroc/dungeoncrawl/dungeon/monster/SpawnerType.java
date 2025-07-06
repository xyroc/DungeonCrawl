package xiroc.dungeoncrawl.dungeon.monster;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import com.google.gson.reflect.TypeToken;
import net.minecraft.core.RegistryAccess;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.FloatTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.ItemStack;
import xiroc.dungeoncrawl.config.Config;
import xiroc.dungeoncrawl.datapack.registry.DatapackRegistries;
import xiroc.dungeoncrawl.datapack.registry.Delegate;
import xiroc.dungeoncrawl.datapack.registry.InheritingBuilder;
import xiroc.dungeoncrawl.datapack.registry.InheritingDelegate;
import xiroc.dungeoncrawl.util.JSONUtils;
import xiroc.dungeoncrawl.util.StorageHelper;
import xiroc.dungeoncrawl.util.random.IRandom;
import xiroc.dungeoncrawl.util.random.value.RandomValue;
import xiroc.dungeoncrawl.util.random.value.Range;

import javax.annotation.Nullable;
import java.lang.reflect.Type;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Function;

public record SpawnerType(IRandom<Delegate<SpawnerEntityType>> entities,
                          Optional<Delegate<SpawnerEntityProperties>> properties,
                          RandomValue spawnAmount,
                          Range spawnDelay,
                          RandomValue initialSpawnDelay,
                          int maxLightLevel,
                          short activationRange) {

    /**
     * Holds types representing the different contexts this class is serialized in.
     */
    public interface Types {
        Type DELEGATE = new TypeToken<Delegate<SpawnerType>>() {
        }.getType();
        Type RANDOM = new TypeToken<IRandom<Delegate<SpawnerType>>>() {
        }.getType();
        Type RANDOM_BUILDER = new TypeToken<IRandom.Builder<Delegate<SpawnerType>>>() {
        }.getType();
    }

    public CompoundTag createData(RandomSource random, int stage, RegistryAccess registryAccess) {
        CompoundTag nbt = new CompoundTag();
        SpawnerEntityType entityType = entities.roll(random).get();
        ListTag potentialSpawns = new ListTag();
        for (int i = 0; i < 3; ++i) {
            CompoundTag potentialSpawn = new CompoundTag();
            CompoundTag data = new CompoundTag();
            CompoundTag entity = new CompoundTag();

            entity.putString("id", entityType.entity().toString());
            putEquipment(entity, entityType, random, stage, registryAccess);
            putDropChances(entity, entityType);
            if (!Config.NATURAL_DESPAWN.get()) {
                data.putBoolean("PersistenceRequired", true);
            }
            data.put("entity", entity);

            if (maxLightLevel >= 0) {
                CompoundTag spawnRules = new CompoundTag();
                CompoundTag lightLimit = new CompoundTag();
                lightLimit.putInt("min_inclusive", 0);
                lightLimit.putInt("max_inclusive", maxLightLevel);
                spawnRules.put("block_light_limit", lightLimit);
                spawnRules.put("sky_light_limit", lightLimit);
                data.put("custom_spawn_rules", spawnRules);
            }

            potentialSpawn.put("data", data);
            potentialSpawn.putInt("weight", 1);
            potentialSpawns.add(potentialSpawn);
        }
        nbt.put("SpawnPotentials", potentialSpawns);
        nbt.putShort("MinSpawnDelay", (short) spawnDelay.min());
        nbt.putShort("MaxSpawnDelay", (short) spawnDelay.max());
        nbt.putShort("SpawnCount", (short) spawnAmount.nextInt(random));
        nbt.putShort("RequiredPlayerRange", activationRange > 0 ? activationRange : Config.SPAWNER_RANGE.get().shortValue());
        return nbt;
    }

    private void putEquipment(CompoundTag nbt, SpawnerEntityType entityType, RandomSource random, int stage, RegistryAccess registryAccess) {
        var helmet = property(entityType, SpawnerEntityProperties::helmet);
        var chestplate = property(entityType, SpawnerEntityProperties::chestplate);
        var leggings = property(entityType, SpawnerEntityProperties::leggings);
        var boots = property(entityType, SpawnerEntityProperties::boots);

        ListTag armor = new ListTag();

        armor.add(StorageHelper.encode(boots.map(items -> EquipmentHelper.createArmorPiece(items.roll(random), random, stage, registryAccess)).orElse(ItemStack.EMPTY), ItemStack.CODEC));
        armor.add(StorageHelper.encode(leggings.map(items -> EquipmentHelper.createArmorPiece(items.roll(random), random, stage, registryAccess)).orElse(ItemStack.EMPTY), ItemStack.CODEC));
        armor.add(StorageHelper.encode(chestplate.map(items -> EquipmentHelper.createArmorPiece(items.roll(random), random, stage, registryAccess)).orElse(ItemStack.EMPTY), ItemStack.CODEC));
        armor.add(StorageHelper.encode(helmet.map(items -> EquipmentHelper.createArmorPiece(items.roll(random), random, stage, registryAccess)).orElse(ItemStack.EMPTY), ItemStack.CODEC));

        nbt.put("ArmorItems", armor);

        var mainHand = property(entityType, SpawnerEntityProperties::mainHand);
        var offHand = property(entityType, SpawnerEntityProperties::offHand);

        ListTag handItems = new ListTag();

        handItems.add(StorageHelper.encode(mainHand.map(items -> EquipmentHelper.enchantAndDamage(items.roll(random), random, stage, registryAccess)).orElse(ItemStack.EMPTY), ItemStack.CODEC));
        handItems.add(StorageHelper.encode(offHand.map(items -> EquipmentHelper.enchantAndDamage(items.roll(random), random, stage, registryAccess)).orElse(ItemStack.EMPTY), ItemStack.CODEC));

        nbt.put("HandItems", handItems);
    }

    private void putDropChances(CompoundTag nbt, SpawnerEntityType entityType) {
        property(entityType, SpawnerEntityProperties::armorDropChance).ifPresent(chance -> {
            ListTag armorDropChances = new ListTag();
            armorDropChances.add(FloatTag.valueOf(chance));
            armorDropChances.add(FloatTag.valueOf(chance));
            armorDropChances.add(FloatTag.valueOf(chance));
            armorDropChances.add(FloatTag.valueOf(chance));
            nbt.put("ArmorDropChances", armorDropChances);
        });

        property(entityType, SpawnerEntityProperties::handDropChance).ifPresent(chance -> {
            ListTag handDropChances = new ListTag();
            handDropChances.add(FloatTag.valueOf(chance));
            handDropChances.add(FloatTag.valueOf(chance));
            nbt.put("HandDropChances", handDropChances);
        });
    }

    private <T> Optional<T> property(SpawnerEntityType entityType, Function<SpawnerEntityProperties, Optional<T>> mapper) {
        return entityType.properties().map(Delegate::get).flatMap(mapper).or(() -> properties.map(Delegate::get).flatMap(mapper));
    }

    public static class Builder extends InheritingBuilder<SpawnerType, Builder> {
        @Nullable
        private IRandom.Builder<Delegate<SpawnerEntityType>> entities = null;

        @Nullable
        private InheritingDelegate<SpawnerEntityProperties, SpawnerEntityProperties.Builder> properties = null;

        @Nullable
        private RandomValue spawnAmount = null;

        @Nullable
        private Range spawnDelay = null;

        @Nullable
        private RandomValue initialSpawnDelay = null;

        private int maxLightLevel = -1;
        private short activationRange = 0;

        public Builder entities(IRandom.Builder<Delegate<SpawnerEntityType>> entities) {
            this.entities = entities;
            return this;
        }

        public Builder defaultProperties(SpawnerEntityProperties.Builder properties) {
            this.properties = InheritingDelegate.ofBuilder(properties);
            return this;
        }

        public Builder defaultProperties(ResourceLocation properties) {
            this.properties = InheritingDelegate.ofKey(properties);
            return this;
        }

        public Builder spawnAmount(RandomValue amount) {
            this.spawnAmount = amount;
            return this;
        }

        public Builder spawnDelay(Range delay) {
            this.initialSpawnDelay = delay;
            this.spawnDelay = delay;
            return this;
        }

        public Builder spawnDelay(RandomValue initialSpawnDelay, Range delay) {
            this.initialSpawnDelay = initialSpawnDelay;
            this.spawnDelay = delay;
            return this;
        }

        public Builder maxLightLevel(int maxLightLevel) {
            this.maxLightLevel = maxLightLevel;
            return this;
        }

        public Builder activationRange(int activationRange) {
            // Accepting an int just to cast it to a short purely for convenience
            if (activationRange < 0 || activationRange > Short.MAX_VALUE) {
                throw new IllegalArgumentException("Invalid  spawner activation range: " + activationRange);
            }
            this.activationRange = (short) activationRange;
            return this;
        }

        @Override
        public Builder inherit(Builder from) {
            entities = InheritingBuilder.inheritOrReplaceOrChoose(entities, from.entities);
            properties = InheritingDelegate.inheritOrChoose(properties, from.properties);
            spawnAmount = choose(spawnAmount, from.spawnAmount);
            spawnDelay = choose(spawnDelay, from.spawnDelay);
            initialSpawnDelay = choose(initialSpawnDelay, from.initialSpawnDelay);
            if (maxLightLevel < 0) {
                maxLightLevel = from.maxLightLevel;
            }
            if (activationRange <= 0) {
                activationRange = from.activationRange;
            }
            return this;
        }

        public SpawnerType build() {
            Objects.requireNonNull(this.entities, "Cannot create a spawner type without spawner entities");
            Objects.requireNonNull(this.spawnAmount, "Cannot create a spawner type without a set spawn amount");
            Objects.requireNonNull(this.spawnDelay, "Cannot create a spawner type without a set spawn delay");
            final var initialSpawnDelay = this.initialSpawnDelay != null ? this.initialSpawnDelay : spawnDelay;
            final var properties = Optional.ofNullable(this.properties).map(prop -> prop.transform(DatapackRegistries.SPAWNER_ENTITY_PROPERTIES));
            return new SpawnerType(entities.build(), properties, spawnAmount, spawnDelay, initialSpawnDelay, maxLightLevel, activationRange);
        }
    }

    private interface SerializationKeys {
        String KEY_ENTITIES = "entities";
        String KEY_ENTITY_PROPERTIES = "entity_properties";
        String KEY_SPAWN_AMOUNT = "amount";
        String KEY_SPAWN_DELAY = "delay";
        String KEY_INITIAL_SPAWN_DELAY = "initial_delay";
        String KEY_ACTIVATION_RANGE = "activation_range";
        String KEY_MAX_LIGHT_LEVEL = "max_light_level";
    }

    public static class BuilderSerializer implements SerializationKeys, JsonSerializer<Builder>, JsonDeserializer<Builder> {
        @Override
        public Builder deserialize(JsonElement json, Type type, JsonDeserializationContext context) throws JsonParseException {
            Builder builder = new Builder();
            JsonObject object = json.getAsJsonObject();
            if (object.has(KEY_ENTITIES))
                builder.entities = context.deserialize(object.get(KEY_ENTITIES), SpawnerEntityType.Types.RANDOM_BUILDER);
            if (object.has(KEY_SPAWN_AMOUNT))
                builder.spawnAmount = JSONUtils.parse(object.get(KEY_SPAWN_DELAY), RandomValue.CODEC);
            if (object.has(KEY_SPAWN_DELAY))
                builder.spawnDelay = JSONUtils.parse(object.get(KEY_SPAWN_DELAY), Range.CODEC);
            if (object.has(KEY_INITIAL_SPAWN_DELAY))
                builder.initialSpawnDelay = JSONUtils.parse(object.get(KEY_INITIAL_SPAWN_DELAY), RandomValue.CODEC);
            if (object.has(KEY_ENTITY_PROPERTIES)) builder.properties = InheritingDelegate.ofBuilder(
                    context.<SpawnerEntityProperties.Builder>deserialize(object.get(KEY_ENTITY_PROPERTIES), SpawnerEntityProperties.Builder.class));
            if (object.has(KEY_ACTIVATION_RANGE))
                builder.activationRange = object.get(KEY_ACTIVATION_RANGE).getAsShort();
            if (object.has(KEY_MAX_LIGHT_LEVEL))
                builder.maxLightLevel = object.get(KEY_MAX_LIGHT_LEVEL).getAsInt();
            return builder;
        }

        @Override
        public JsonElement serialize(Builder builder, Type type, JsonSerializationContext context) {
            JsonObject object = new JsonObject();
            if (builder.entities != null)
                object.add(KEY_ENTITIES, context.serialize(builder.entities, SpawnerEntityType.Types.RANDOM_BUILDER));
            if (builder.spawnAmount != null)
                object.add(KEY_SPAWN_AMOUNT, JSONUtils.encode(builder.spawnAmount, RandomValue.CODEC));
            if (builder.spawnDelay != null)
                object.add(KEY_SPAWN_DELAY, JSONUtils.encode(builder.spawnDelay, Range.CODEC));
            if (builder.initialSpawnDelay != null)
                object.add(KEY_INITIAL_SPAWN_DELAY, JSONUtils.encode(builder.initialSpawnDelay, RandomValue.CODEC));
            if (builder.properties != null)
                object.add(KEY_ENTITY_PROPERTIES, builder.properties.serialize(context::serialize));
            if (builder.maxLightLevel >= 0)
                object.addProperty(KEY_MAX_LIGHT_LEVEL, builder.maxLightLevel);
            return object;
        }
    }
}