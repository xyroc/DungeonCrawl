package xiroc.dungeoncrawl.dungeon.monster;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.RegistryFileCodec;
import net.minecraft.resources.RegistryOps;
import net.minecraft.util.RandomSource;
import net.minecraft.util.random.SimpleWeightedRandomList;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentTable;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeMap;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.attributes.DefaultAttributes;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.SpawnData;
import net.minecraft.world.level.block.entity.SpawnerBlockEntity;
import xiroc.dungeoncrawl.DungeonCrawl;
import xiroc.dungeoncrawl.config.Config;
import xiroc.dungeoncrawl.datapack.registry.DatapackRegistries;
import xiroc.dungeoncrawl.dungeon.monster.attribute.RandomizedAttributeMap;
import xiroc.dungeoncrawl.util.random.IRandom;
import xiroc.dungeoncrawl.util.random.value.RandomValue;
import xiroc.dungeoncrawl.util.random.value.Range;
import xiroc.dungeoncrawl.worldgen.DungeonWorldGenContext;

import javax.annotation.Nullable;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Function;

public record SpawnerType(IRandom<Holder<SpawnerEntityType>> entities,
                          Optional<Holder<SpawnerEntityProperties>> properties,
                          RandomValue spawnAmount,
                          Range spawnDelay,
                          Optional<RandomValue> initialSpawnDelay,
                          Optional<SpawnData.CustomSpawnRules> spawnRules,
                          Optional<Short> activationRange) {

    public static final Codec<SpawnerType> DIRECT_CODEC = RecordCodecBuilder.create(instance -> instance
            .group(
                    SpawnerEntityType.RANDOM_HOLDER_CODEC.fieldOf("entities").forGetter(SpawnerType::entities),
                    SpawnerEntityProperties.HOLDER_CODEC.optionalFieldOf("properties").forGetter(SpawnerType::properties),
                    RandomValue.CODEC.fieldOf("spawn_amount").forGetter(SpawnerType::spawnAmount),
                    Range.CODEC.fieldOf("spawn_delay").forGetter(SpawnerType::spawnDelay),
                    RandomValue.CODEC.optionalFieldOf("initial_spawn_delay").forGetter(SpawnerType::initialSpawnDelay),
                    SpawnData.CustomSpawnRules.CODEC.optionalFieldOf("max_light_level").forGetter(SpawnerType::spawnRules),
                    Codec.SHORT.optionalFieldOf("activation_range").forGetter(SpawnerType::activationRange)
            )
            .apply(instance, SpawnerType::new));
    public static final Codec<Holder<SpawnerType>> HOLDER_CODEC = RegistryFileCodec.create(DatapackRegistries.SPAWNER_TYPE, DIRECT_CODEC, true);
    public static final Codec<IRandom<Holder<SpawnerType>>> RANDOM_HOLDER_CODEC = IRandom.<Holder<SpawnerType>>codecBuilder()
            .valueCodec("type", HOLDER_CODEC)
            .pools(DatapackRegistries.SPAWNER_TYPE_POOLS)
            .build();

    public void applyToSpawner(SpawnerBlockEntity spawner, RandomSource random, DungeonWorldGenContext context, RegistryAccess registryAccess) {
        spawner.getSpawner().load(spawner.getLevel(), spawner.getBlockPos(), createData(random, context.level(), registryAccess));
    }

    public CompoundTag createData(RandomSource random, int stage, RegistryAccess registryAccess) {
        CompoundTag nbt = new CompoundTag();
        SpawnerEntityType entityType = entities.roll(random).value();
        SimpleWeightedRandomList.Builder<SpawnData> potentialSpawns = SimpleWeightedRandomList.builder();
        for (int i = 0; i < Config.SPAWNER_ENTITIES.getAsInt(); ++i) {
            CompoundTag entity = property(entityType, SpawnerEntityProperties::nbt)
                    .map(CompoundTag::copy)
                    .orElseGet(CompoundTag::new);
            entity.putString("id", entityType.entity().location().toString());

            putEquipment(entity, entityType, random, stage, registryAccess);
            putAttributes(entity, entityType, random, registryAccess);
            final Optional<EquipmentTable> drops = property(entityType, SpawnerEntityProperties::drops);

            if (!Config.NATURAL_DESPAWN.get()) {
                entity.putBoolean("PersistenceRequired", true);
            }

            final SpawnData data = new SpawnData(entity, spawnRules, drops);
            potentialSpawns.add(data);
        }

        nbt.put("SpawnPotentials", SpawnData.LIST_CODEC.encodeStart(RegistryOps.create(NbtOps.INSTANCE, registryAccess), potentialSpawns.build()).getOrThrow());
        nbt.putShort("MinSpawnDelay", (short) spawnDelay.min());
        nbt.putShort("MaxSpawnDelay", (short) spawnDelay.max());
        nbt.putShort("SpawnCount", (short) spawnAmount.nextInt(random));
        nbt.putShort("MaxNearbyEntities", (short) 8);
        nbt.putShort("RequiredPlayerRange", activationRange.orElse(Config.SPAWNER_RANGE.get().shortValue()));
        nbt.putShort("SpawnDelay", (short) initialSpawnDelay.orElse(spawnDelay).nextInt(random));
        return nbt;
    }

    private void putEquipment(CompoundTag nbt, SpawnerEntityType entityType, RandomSource random, int stage, RegistryAccess registryAccess) {
        var helmet = property(entityType, SpawnerEntityProperties::helmet);
        var chestplate = property(entityType, SpawnerEntityProperties::chestplate);
        var leggings = property(entityType, SpawnerEntityProperties::leggings);
        var boots = property(entityType, SpawnerEntityProperties::boots);

        ListTag armor = new ListTag();

        RegistryOps<Tag> ops = RegistryOps.create(NbtOps.INSTANCE, registryAccess);

        armor.add(ItemStack.OPTIONAL_CODEC.encodeStart(ops, boots.map(items -> EquipmentHelper.createArmorPiece(items.roll(random), random, stage, registryAccess)).orElse(ItemStack.EMPTY)).getOrThrow());
        armor.add(ItemStack.OPTIONAL_CODEC.encodeStart(ops, leggings.map(items -> EquipmentHelper.createArmorPiece(items.roll(random), random, stage, registryAccess)).orElse(ItemStack.EMPTY)).getOrThrow());
        armor.add(ItemStack.OPTIONAL_CODEC.encodeStart(ops, chestplate.map(items -> EquipmentHelper.createArmorPiece(items.roll(random), random, stage, registryAccess)).orElse(ItemStack.EMPTY)).getOrThrow());
        armor.add(ItemStack.OPTIONAL_CODEC.encodeStart(ops, helmet.map(items -> EquipmentHelper.createArmorPiece(items.roll(random), random, stage, registryAccess)).orElse(ItemStack.EMPTY)).getOrThrow());

        nbt.put("ArmorItems", armor);

        var mainHand = property(entityType, SpawnerEntityProperties::mainHand);
        var offHand = property(entityType, SpawnerEntityProperties::offHand);

        ListTag handItems = new ListTag();

        handItems.add(ItemStack.OPTIONAL_CODEC.encodeStart(ops, mainHand.map(items -> EquipmentHelper.createArmorPiece(items.roll(random), random, stage, registryAccess)).orElse(ItemStack.EMPTY)).getOrThrow());
        handItems.add(ItemStack.OPTIONAL_CODEC.encodeStart(ops, offHand.map(items -> EquipmentHelper.createArmorPiece(items.roll(random), random, stage, registryAccess)).orElse(ItemStack.EMPTY)).getOrThrow());

        nbt.put("HandItems", handItems);
    }

    @SuppressWarnings("unchecked")
    private void putAttributes(CompoundTag entityNbt, SpawnerEntityType entityType, RandomSource randomSource, RegistryAccess registryAccess) {
        RandomizedAttributeMap attributes = property(entityType, SpawnerEntityProperties::attributes).orElse(null);
        if (attributes == null) {
            return;
        }
        Registry<EntityType<?>> entityTypes = registryAccess.registryOrThrow(Registries.ENTITY_TYPE);
        try {
            EntityType<? extends LivingEntity> entity = (EntityType<? extends LivingEntity>) entityTypes.get(entityType.entity());
            if (entity == null) {
                return;
            }
            AttributeMap mobAttributes = new AttributeMap(DefaultAttributes.getSupplier(entity));
            attributes.applyRandomModifiers(mobAttributes, randomSource);
            entityNbt.put("attributes", mobAttributes.save());
            // Set health to max health in case it was modified
            entityNbt.putFloat("Health", (float) (mobAttributes.getValue(Attributes.MAX_HEALTH)));
        } catch (ClassCastException ignored) {
            DungeonCrawl.LOGGER.warn("Cannot apply attribute modifications to entity of type {}", entityType.entity());
        }
    }

    private <T> Optional<T> property(SpawnerEntityType entityType, Function<SpawnerEntityProperties, Optional<T>> mapper) {
        return entityType.properties().map(Holder::value).flatMap(mapper).or(() -> properties.map(Holder::value).flatMap(mapper));
    }

    public static class Builder {
        @Nullable
        private IRandom.Builder<Holder<SpawnerEntityType>> entities = null;

        @Nullable
        private Holder<SpawnerEntityProperties> properties = null;

        @Nullable
        private RandomValue spawnAmount = null;

        @Nullable
        private Range spawnDelay = null;

        @Nullable
        private RandomValue initialSpawnDelay = null;

        @Nullable
        private SpawnData.CustomSpawnRules spawnRules;
        @Nullable
        private Short activationRange = null;

        public Builder() {
        }

        public Builder entities(IRandom.Builder<Holder<SpawnerEntityType>> entities) {
            this.entities = entities;
            return this;
        }

        public Builder defaultProperties(SpawnerEntityProperties.Builder properties) {
            this.properties = Holder.direct(properties.build());
            return this;
        }

        public Builder defaultProperties(Holder<SpawnerEntityProperties> properties) {
            this.properties = properties;
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

        public Builder spawnRules(SpawnData.CustomSpawnRules spawnRules) {
            this.spawnRules = spawnRules;
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

        public SpawnerType build() {
            Objects.requireNonNull(this.entities, "Cannot create a spawner type without spawner entities");
            Objects.requireNonNull(this.spawnAmount, "Cannot create a spawner type without a set spawn amount");
            Objects.requireNonNull(this.spawnDelay, "Cannot create a spawner type without a set spawn delay");
            return new SpawnerType(entities.build(), Optional.ofNullable(this.properties), spawnAmount, spawnDelay, Optional.ofNullable(initialSpawnDelay), Optional.ofNullable(spawnRules),
                    Optional.ofNullable(activationRange));
        }
    }
}