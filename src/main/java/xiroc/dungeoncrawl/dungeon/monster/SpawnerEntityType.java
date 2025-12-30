package xiroc.dungeoncrawl.dungeon.monster;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.RegistryFileCodec;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.entity.EntityType;
import xiroc.dungeoncrawl.datapack.registry.DatapackRegistries;
import xiroc.dungeoncrawl.util.StorageHelper;
import xiroc.dungeoncrawl.util.random.IRandom;

import javax.annotation.Nullable;
import java.util.Objects;
import java.util.Optional;

public record SpawnerEntityType(ResourceKey<EntityType<?>> entity, Optional<Holder<SpawnerEntityProperties>> properties) {
    public static final Codec<SpawnerEntityType> DIRECT_CODEC = Builder.CODEC.comapFlatMap(StorageHelper.tryToApply(Builder::build), Builder::new);
    public static final Codec<Holder<SpawnerEntityType>> HOLDER_CODEC = RegistryFileCodec.create(DatapackRegistries.SPAWNER_ENTITY_TYPE, DIRECT_CODEC, true);
    public static final Codec<IRandom<Holder<SpawnerEntityType>>> RANDOM_HOLDER_CODEC = IRandom.<Holder<SpawnerEntityType>>codecBuilder()
            .valueCodec("type", HOLDER_CODEC)
            .pools(DatapackRegistries.SPAWNER_ENTITY_TYPE_POOLS)
            .build();

    public static class Builder {
        public static final Codec<Builder> CODEC = RecordCodecBuilder.create(instance -> instance
                .group(
                        ResourceKey.codec(Registries.ENTITY_TYPE).fieldOf("entity_type").forGetter(builder -> builder.entity),
                        SpawnerEntityProperties.HOLDER_CODEC.optionalFieldOf("properties").forGetter(builder -> Optional.ofNullable(builder.properties))
                ).apply(instance, (entityType, properties) -> {
                    final Builder builder = new Builder();
                    builder.entity = entityType;
                    builder.properties = properties.orElse(null);
                    return builder;
                }));

        @Nullable
        private ResourceKey<EntityType<?>> entity = null;

        @Nullable
        private Holder<SpawnerEntityProperties> properties;

        public Builder() {
        }

        public Builder(SpawnerEntityType instance) {
            this.entity = instance.entity;
            this.properties = instance.properties.orElse(null);
        }


        public Builder entity(@Nullable ResourceKey<EntityType<?>> entity) {
            this.entity = entity;
            return this;
        }

        public Builder properties(Holder<SpawnerEntityProperties> properties) {
            this.properties = properties;
            return this;
        }

        public SpawnerEntityType build() {
            Objects.requireNonNull(entity);
            return new SpawnerEntityType(entity, Optional.ofNullable(properties));
        }
    }
}