package xiroc.dungeoncrawl.dungeon.monster;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.Holder;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.RegistryFileCodec;
import net.minecraft.world.entity.EquipmentTable;
import net.minecraft.world.item.Item;
import org.jetbrains.annotations.Nullable;
import xiroc.dungeoncrawl.datapack.registry.DatapackRegistries;
import xiroc.dungeoncrawl.dungeon.monster.attribute.RandomizedAttributeMap;
import xiroc.dungeoncrawl.util.random.IRandom;

import java.util.List;
import java.util.Optional;
import java.util.function.Function;

public record SpawnerEntityProperties(Optional<EquipmentWithChance> mainHand,
                                      Optional<EquipmentWithChance> offHand,
                                      Optional<EquipmentWithChance> helmet,
                                      Optional<EquipmentWithChance> chestplate,
                                      Optional<EquipmentWithChance> leggings,
                                      Optional<EquipmentWithChance> boots,
                                      Optional<EquipmentTable> drops,
                                      Optional<RandomizedAttributeMap> attributes,
                                      Optional<CompoundTag> nbt) {
    public static final Codec<SpawnerEntityProperties> DIRECT_CODEC = RecordCodecBuilder.create(instance -> instance
            .group(
                    EquipmentWithChance.CODEC.optionalFieldOf("main_hand").forGetter(SpawnerEntityProperties::mainHand),
                    EquipmentWithChance.CODEC.optionalFieldOf("off_hand").forGetter(SpawnerEntityProperties::offHand),
                    EquipmentWithChance.CODEC.optionalFieldOf("helmet").forGetter(SpawnerEntityProperties::helmet),
                    EquipmentWithChance.CODEC.optionalFieldOf("chestplate").forGetter(SpawnerEntityProperties::chestplate),
                    EquipmentWithChance.CODEC.optionalFieldOf("leggings").forGetter(SpawnerEntityProperties::leggings),
                    EquipmentWithChance.CODEC.optionalFieldOf("boots").forGetter(SpawnerEntityProperties::boots),
                    EquipmentTable.CODEC.optionalFieldOf("drops").forGetter(SpawnerEntityProperties::drops),
                    RandomizedAttributeMap.CODEC.optionalFieldOf("attributes").forGetter(SpawnerEntityProperties::attributes),
                    CompoundTag.CODEC.optionalFieldOf("nbt").forGetter(SpawnerEntityProperties::nbt)
            ).apply(instance, SpawnerEntityProperties::new));

    public static final Codec<Holder<SpawnerEntityProperties>> HOLDER_CODEC = RegistryFileCodec.create(DatapackRegistries.SPAWNER_ENTITY_PROPERTIES, DIRECT_CODEC, true);

    /**
     * Create a single instance that combines the list of layered properties.
     * For each property field, the value of the first entry in the list that specifies a value is used, or none.
     * <p>
     * The property holders must be bound at the time of the call.
     *
     * @param layeredProperties The list of properties in order of decreasing precedence.
     * @return The combined properties.
     */
    public static SpawnerEntityProperties combined(List<Holder<SpawnerEntityProperties>> layeredProperties) {
        if (layeredProperties.size() == 1) {
            return layeredProperties.getFirst().value();
        }
        return new SpawnerEntityProperties(
                findProperty(layeredProperties, SpawnerEntityProperties::mainHand),
                findProperty(layeredProperties, SpawnerEntityProperties::offHand),
                findProperty(layeredProperties, SpawnerEntityProperties::helmet),
                findProperty(layeredProperties, SpawnerEntityProperties::chestplate),
                findProperty(layeredProperties, SpawnerEntityProperties::leggings),
                findProperty(layeredProperties, SpawnerEntityProperties::boots),
                findProperty(layeredProperties, SpawnerEntityProperties::drops),
                findProperty(layeredProperties, SpawnerEntityProperties::attributes),
                findProperty(layeredProperties, SpawnerEntityProperties::nbt)
        );
    }

    /**
     * Find the first match for the property retrieved by the {@code mapper} function in the spawner entity type's list of profiles.
     * The spawner's default profile is considered last.
     *
     * @param layeredProperties The list of properties in order of decreasing precedence.
     * @param mapper            A function that maps a profile to an optional of the property to search for.
     * @param <T>               The type of the property.
     * @return An optional holding the property, or nothing.
     */
    private static <T> Optional<T> findProperty(List<Holder<SpawnerEntityProperties>> layeredProperties, Function<SpawnerEntityProperties, Optional<T>> mapper) {
        return layeredProperties.stream()
                .map(Holder::value)
                .map(mapper)
                .filter(Optional::isPresent)
                .findFirst()
                .flatMap(Function.identity());
    }

    public record EquipmentWithChance(IRandom<Item> items, float chance) {
        private static final float DEFAULT_CHANCE = 1.0f;

        public static final MapCodec<EquipmentWithChance> MAP_CODEC = RecordCodecBuilder.mapCodec(builder -> builder.group(
                IRandom.BaseCodecs.ITEM.fieldOf("items").forGetter(EquipmentWithChance::items),
                Codec.floatRange(0.0f, 1.0f).optionalFieldOf("chance").forGetter(instance -> instance.chance == 1.0f ? Optional.empty() : Optional.of(instance.chance))
        ).apply(builder, (items, chance) -> new EquipmentWithChance(items, chance.orElse(DEFAULT_CHANCE))));

        public static final Codec<EquipmentWithChance> CODEC = MAP_CODEC.codec();
    }

    public static class Builder {
        @Nullable
        private EquipmentWithChance mainHand = null;
        @Nullable
        private EquipmentWithChance offHand = null;

        @Nullable
        private EquipmentWithChance helmet = null;
        @Nullable
        private EquipmentWithChance chestplate = null;
        @Nullable
        private EquipmentWithChance leggings = null;
        @Nullable
        private EquipmentWithChance boots = null;

        @Nullable
        private EquipmentTable drops = null;

        @Nullable
        private RandomizedAttributeMap attributes = null;

        @Nullable
        private CompoundTag nbt = null;

        public Builder() {
        }

        public Builder(SpawnerEntityProperties instance) {
            this.mainHand = instance.mainHand().orElse(null);
            this.offHand = instance.offHand().orElse(null);
            this.helmet = instance.helmet().orElse(null);
            this.chestplate = instance.chestplate().orElse(null);
            this.leggings = instance.leggings().orElse(null);
            this.boots = instance.boots().orElse(null);
            this.drops = instance.drops().orElse(null);
        }

        public Builder mainHand(EquipmentWithChance mainHand) {
            this.mainHand = mainHand;
            return this;
        }

        public Builder offHand(EquipmentWithChance offHand) {
            this.offHand = offHand;
            return this;
        }

        public Builder helmet(EquipmentWithChance helmet) {
            this.helmet = helmet;
            return this;
        }

        public Builder chestplate(EquipmentWithChance chestplate) {
            this.chestplate = chestplate;
            return this;
        }

        public Builder leggings(EquipmentWithChance leggings) {
            this.leggings = leggings;
            return this;
        }

        public Builder boots(EquipmentWithChance boots) {
            this.boots = boots;
            return this;
        }

        public Builder drops(EquipmentTable drops) {
            this.drops = drops;
            return this;
        }

        public Builder attributes(RandomizedAttributeMap attributeMap) {
            this.attributes = attributeMap;
            return this;
        }

        public Builder nbtData(CompoundTag nbt) {
            this.nbt = nbt;
            return this;
        }

        public SpawnerEntityProperties build() {
            return new SpawnerEntityProperties(
                    Optional.ofNullable(mainHand),
                    Optional.ofNullable(offHand),
                    Optional.ofNullable(helmet),
                    Optional.ofNullable(chestplate),
                    Optional.ofNullable(leggings),
                    Optional.ofNullable(boots),
                    Optional.ofNullable(drops),
                    Optional.ofNullable(attributes),
                    Optional.ofNullable(nbt));
        }
    }
}