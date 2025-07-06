package xiroc.dungeoncrawl.dungeon.type;

import com.google.gson.*;
import org.jetbrains.annotations.Nullable;
import xiroc.dungeoncrawl.datapack.registry.Delegate;
import xiroc.dungeoncrawl.dungeon.blueprint.Blueprint;
import xiroc.dungeoncrawl.util.JSONUtils;
import xiroc.dungeoncrawl.util.random.IRandom;
import xiroc.dungeoncrawl.util.random.value.RandomValue;

import java.lang.reflect.Type;
import java.util.Objects;

public record SecretRoom(IRandom<Delegate<Blueprint>> variants, @Nullable RandomValue level, RandomValue amount,
                         IRandom<Delegate<Blueprint>> entrances) {
    public static class Builder {
        @Nullable
        private IRandom.Builder<Delegate<Blueprint>> variants;
        @Nullable
        private RandomValue level;
        @Nullable
        private RandomValue amount;
        @Nullable
        private IRandom.Builder<Delegate<Blueprint>> entrances;

        public static Builder fromInstance(SecretRoom instance) {
            final Builder builder = new Builder();
            builder.variants = new IRandom.Builder<Delegate<Blueprint>>().add(instance.variants);
            builder.level = instance.level;
            builder.amount = instance.amount;
            builder.entrances = new IRandom.Builder<Delegate<Blueprint>>().add(instance.entrances);
            return builder;
        }

        public Builder variants(IRandom.Builder<Delegate<Blueprint>> variants) {
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

        public Builder entrances(IRandom.Builder<Delegate<Blueprint>> entrances) {
            this.entrances = entrances;
            return this;
        }

        public SecretRoom build() {
            Objects.requireNonNull(variants, "No variants were specified");
            Objects.requireNonNull(amount, "No amount was specified");
            Objects.requireNonNull(entrances, "No entrance segments were specified");
            return new SecretRoom(variants.build(), level, amount, entrances.build());
        }
    }

    public static class BuilderSerializer implements JsonSerializer<Builder>, JsonDeserializer<Builder> {
        private static final String KEY_VARIANTS = "variants";
        private static final String KEY_LEVEL = "level";
        private static final String KEY_AMOUNT = "amount";
        private static final String KEY_ENTRANCES = "entrances";

        @Override
        public Builder deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
            final Builder builder = new Builder();
            final JsonObject object = json.getAsJsonObject();
            builder.variants = context.deserialize(object.get(KEY_VARIANTS), Blueprint.Types.RANDOM_BUILDER);
            if (object.has(KEY_LEVEL)) {
                builder.level = JSONUtils.parse(object.get(KEY_LEVEL), RandomValue.CODEC);
            }
            builder.amount = JSONUtils.parse(object.get(KEY_AMOUNT), RandomValue.CODEC);
            builder.entrances = context.deserialize(object.get(KEY_ENTRANCES), Blueprint.Types.RANDOM_BUILDER);
            return builder;
        }

        @Override
        public JsonElement serialize(Builder src, Type typeOfSrc, JsonSerializationContext context) {
            final JsonObject object = new JsonObject();
            object.add(KEY_VARIANTS, context.serialize(src.variants, Blueprint.Types.RANDOM_BUILDER));
            if (src.level != null) {
                object.add(KEY_LEVEL, JSONUtils.encode(src.level, RandomValue.CODEC));
            }
            object.add(KEY_AMOUNT, JSONUtils.encode(src.amount, RandomValue.CODEC));
            object.add(KEY_ENTRANCES, context.serialize(src.entrances, Blueprint.Types.RANDOM_BUILDER));
            return object;
        }
    }
}
