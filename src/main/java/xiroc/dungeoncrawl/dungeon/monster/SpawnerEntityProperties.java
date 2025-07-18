package xiroc.dungeoncrawl.dungeon.monster;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.Holder;
import net.minecraft.resources.RegistryFileCodec;
import net.minecraft.world.entity.EquipmentTable;
import net.minecraft.world.item.Item;
import xiroc.dungeoncrawl.datapack.registry.DatapackRegistries;
import xiroc.dungeoncrawl.util.random.IRandom;

import javax.annotation.Nullable;
import java.util.Optional;

public record SpawnerEntityProperties(Optional<IRandom<Item>> mainHand,
                                      Optional<IRandom<Item>> offHand,
                                      Optional<IRandom<Item>> helmet,
                                      Optional<IRandom<Item>> chestplate,
                                      Optional<IRandom<Item>> leggings,
                                      Optional<IRandom<Item>> boots,
                                      Optional<EquipmentTable> drops) {
    public static final Codec<SpawnerEntityProperties> DIRECT_CODEC = RecordCodecBuilder.create(instance -> instance
            .group(
                    IRandom.BaseCodecs.ITEM.optionalFieldOf("main_hand").forGetter(SpawnerEntityProperties::mainHand),
                    IRandom.BaseCodecs.ITEM.optionalFieldOf("off_hand").forGetter(SpawnerEntityProperties::offHand),
                    IRandom.BaseCodecs.ITEM.optionalFieldOf("helmet").forGetter(SpawnerEntityProperties::helmet),
                    IRandom.BaseCodecs.ITEM.optionalFieldOf("chestplate").forGetter(SpawnerEntityProperties::chestplate),
                    IRandom.BaseCodecs.ITEM.optionalFieldOf("leggings").forGetter(SpawnerEntityProperties::leggings),
                    IRandom.BaseCodecs.ITEM.optionalFieldOf("boots").forGetter(SpawnerEntityProperties::boots),
                    EquipmentTable.CODEC.optionalFieldOf("drops").forGetter(SpawnerEntityProperties::drops)
            ).apply(instance, SpawnerEntityProperties::new));

    public static final Codec<Holder<SpawnerEntityProperties>> HOLDER_CODEC = RegistryFileCodec.create(DatapackRegistries.SPAWNER_ENTITY_PROPERTIES, DIRECT_CODEC, true);

    public static class Builder {
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
        private EquipmentTable drops = null;

        public Builder() {
        }

        public Builder(SpawnerEntityProperties instance) {
            this.mainHand = instance.mainHand().map(IRandom.Builder::copy).orElse(null);
            this.offHand = instance.offHand().map(IRandom.Builder::copy).orElse(null);
            this.helmet = instance.helmet().map(IRandom.Builder::copy).orElse(null);
            this.chestplate = instance.chestplate().map(IRandom.Builder::copy).orElse(null);
            this.leggings = instance.leggings().map(IRandom.Builder::copy).orElse(null);
            this.boots = instance.boots().map(IRandom.Builder::copy).orElse(null);
            this.drops = instance.drops().orElse(null);
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

        public Builder drops(EquipmentTable drops) {
            this.drops = drops;
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
                    Optional.ofNullable(drops));
        }
    }
}