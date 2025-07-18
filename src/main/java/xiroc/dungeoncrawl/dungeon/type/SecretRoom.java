package xiroc.dungeoncrawl.dungeon.type;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.Holder;
import org.jetbrains.annotations.Nullable;
import xiroc.dungeoncrawl.dungeon.blueprint.Blueprint;
import xiroc.dungeoncrawl.util.StorageHelper;
import xiroc.dungeoncrawl.util.random.IRandom;
import xiroc.dungeoncrawl.util.random.value.RandomValue;

import java.util.Objects;

public record SecretRoom(IRandom<Holder<Blueprint>> variants, RandomValue level, RandomValue amount,
                         IRandom<Holder<Blueprint>> entrances) {
    public static final Codec<SecretRoom> CODEC = Builder.CODEC.comapFlatMap(StorageHelper.tryToApply(Builder::build), Builder::fromInstance);

    public static class Builder {
        public static final Codec<Builder> CODEC = RecordCodecBuilder.create(instance -> instance.group(
                RandomValue.CODEC.fieldOf("level").forGetter(builder -> builder.level),
                RandomValue.CODEC.fieldOf("amount").forGetter(builder -> builder.amount),
                Blueprint.RANDOM_HOLDER_CODEC.fieldOf("variants").forGetter(builder -> builder.variants),
                Blueprint.RANDOM_HOLDER_CODEC.fieldOf("entrances").forGetter(builder -> builder.entrances)
        ).apply(instance, (level, amount, variants, entrances) -> {
            final Builder builder = new Builder();
            builder.level = level;
            builder.amount = amount;
            builder.variants = variants;
            builder.entrances = entrances;
            return builder;
        }));

        @Nullable
        private IRandom<Holder<Blueprint>> variants;
        @Nullable
        private RandomValue level;
        @Nullable
        private RandomValue amount;
        @Nullable
        private IRandom<Holder<Blueprint>> entrances;

        public static Builder fromInstance(SecretRoom instance) {
            final Builder builder = new Builder();
            builder.variants = instance.variants;
            builder.level = instance.level;
            builder.amount = instance.amount;
            builder.entrances = instance.entrances;
            return builder;
        }

        public Builder variants(IRandom<Holder<Blueprint>> variants) {
            this.variants = variants;
            return this;
        }

        public Builder level(RandomValue level) {
            this.level = level;
            return this;
        }

        public Builder amount(RandomValue amount) {
            this.amount = amount;
            return this;
        }

        public Builder entrances(IRandom<Holder<Blueprint>> entrances) {
            this.entrances = entrances;
            return this;
        }

        public SecretRoom build() {
            Objects.requireNonNull(variants, "No variants were specified");
            Objects.requireNonNull(level, "No range of levels to generate in was specified");
            Objects.requireNonNull(amount, "No amount was specified");
            Objects.requireNonNull(entrances, "No entrance segments were specified");
            return new SecretRoom(variants, level, amount, entrances);
        }
    }
}
