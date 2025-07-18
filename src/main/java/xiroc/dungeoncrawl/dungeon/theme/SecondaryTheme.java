package xiroc.dungeoncrawl.dungeon.theme;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderSet;
import net.minecraft.core.RegistryCodecs;
import net.minecraft.resources.RegistryFileCodec;
import net.minecraft.world.level.biome.Biome;
import xiroc.dungeoncrawl.datapack.registry.DatapackRegistries;
import xiroc.dungeoncrawl.dungeon.block.provider.BlockStateProvider;
import xiroc.dungeoncrawl.dungeon.block.provider.SingleBlock;
import xiroc.dungeoncrawl.util.random.IRandom;
import xiroc.dungeoncrawl.util.random.RandomMapping;

import java.util.Objects;

public record SecondaryTheme(BlockStateProvider material,
                             BlockStateProvider pillar,
                             BlockStateProvider stairs,
                             BlockStateProvider slab,
                             BlockStateProvider door,
                             BlockStateProvider trapDoor,
                             BlockStateProvider fence,
                             BlockStateProvider fenceGate,
                             BlockStateProvider button,
                             BlockStateProvider pressurePlate) {

    public static final Codec<SecondaryTheme> DIRECT_CODEC = RecordCodecBuilder.create(instance -> instance.group(
            BlockStateProvider.CODEC.fieldOf("material").forGetter(SecondaryTheme::material),
            BlockStateProvider.CODEC.fieldOf("pillar").forGetter(SecondaryTheme::pillar),
            BlockStateProvider.CODEC.fieldOf("stairs").forGetter(SecondaryTheme::stairs),
            BlockStateProvider.CODEC.fieldOf("slab").forGetter(SecondaryTheme::slab),
            BlockStateProvider.CODEC.fieldOf("door").forGetter(SecondaryTheme::door),
            BlockStateProvider.CODEC.fieldOf("trapdoor").forGetter(SecondaryTheme::trapDoor),
            BlockStateProvider.CODEC.fieldOf("fence").forGetter(SecondaryTheme::fence),
            BlockStateProvider.CODEC.fieldOf("fence_gate").forGetter(SecondaryTheme::fenceGate),
            BlockStateProvider.CODEC.fieldOf("button").forGetter(SecondaryTheme::button),
            BlockStateProvider.CODEC.fieldOf("pressure_plate").forGetter(SecondaryTheme::pressurePlate)
    ).apply(instance, SecondaryTheme::new));

    public static final Codec<Holder<SecondaryTheme>> HOLDER_CODEC = RegistryFileCodec.create(DatapackRegistries.SECONDARY_THEME, DIRECT_CODEC, false);
    public static final Codec<IRandom<Holder<SecondaryTheme>>> RANDOM_HOLDER_CODEC = IRandom.makeCodec(IRandom.makeBuilderCodec(HOLDER_CODEC, "theme", null));
    public static final Codec<HolderSet<IRandom<Holder<SecondaryTheme>>>> LIST_OF_RANDOM_HOLDER_CODEC = RegistryCodecs.homogeneousList(DatapackRegistries.SECONDARY_THEME_POOLS, RANDOM_HOLDER_CODEC, true);
    public static final Codec<RandomMapping<Biome, SecondaryTheme>> BIOME_MAPPING_DIRECT_CODEC = RandomMapping.makeDirectCodec(Biome.LIST_CODEC, LIST_OF_RANDOM_HOLDER_CODEC);
    public static final Codec<Holder<RandomMapping<Biome, SecondaryTheme>>> BIOME_MAPPING_HOLDER_CODEC = RegistryFileCodec.create(DatapackRegistries.SECONDARY_THEME_MAPPINGS, BIOME_MAPPING_DIRECT_CODEC);

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private BlockStateProvider button = SingleBlock.AIR;
        private BlockStateProvider door = SingleBlock.AIR;
        private BlockStateProvider fence = SingleBlock.AIR;
        private BlockStateProvider fenceGate = SingleBlock.AIR;
        private BlockStateProvider material = SingleBlock.AIR;
        private BlockStateProvider pillar = SingleBlock.AIR;
        private BlockStateProvider pressurePlate = SingleBlock.AIR;
        private BlockStateProvider slab = SingleBlock.AIR;
        private BlockStateProvider stairs = SingleBlock.AIR;
        private BlockStateProvider trapdoor = SingleBlock.AIR;

        public Builder button(BlockStateProvider provider) {
            this.button = provider;
            return this;
        }

        public Builder door(BlockStateProvider provider) {
            this.door = provider;
            return this;
        }

        public Builder fence(BlockStateProvider provider) {
            this.fence = provider;
            return this;
        }

        public Builder fenceGate(BlockStateProvider provider) {
            this.fenceGate = provider;
            return this;
        }

        public Builder material(BlockStateProvider provider) {
            this.material = provider;
            return this;
        }

        public Builder pillar(BlockStateProvider provider) {
            this.pillar = provider;
            return this;
        }

        public Builder pressurePlate(BlockStateProvider provider) {
            this.pressurePlate = provider;
            return this;
        }

        public Builder slab(BlockStateProvider provider) {
            this.slab = provider;
            return this;
        }

        public Builder stairs(BlockStateProvider provider) {
            this.stairs = provider;
            return this;
        }

        public Builder trapdoor(BlockStateProvider provider) {
            this.trapdoor = provider;
            return this;
        }

        public SecondaryTheme build() {
            Objects.requireNonNull(material);
            Objects.requireNonNull(pillar);
            Objects.requireNonNull(stairs);
            Objects.requireNonNull(slab);
            Objects.requireNonNull(door);
            Objects.requireNonNull(trapdoor);
            Objects.requireNonNull(fence);
            Objects.requireNonNull(fenceGate);
            Objects.requireNonNull(button);
            Objects.requireNonNull(pressurePlate);
            return new SecondaryTheme(material, pillar, stairs, slab, door, trapdoor, fence, fenceGate, button, pressurePlate);
        }
    }
}