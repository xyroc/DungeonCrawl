package xiroc.dungeoncrawl.dungeon.monster;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.RegistryFileCodec;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.entity.EntityType;
import org.jetbrains.annotations.Nullable;
import xiroc.dungeoncrawl.datapack.registry.DatapackRegistries;
import xiroc.dungeoncrawl.util.StorageHelper;
import xiroc.dungeoncrawl.util.random.IRandom;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

public class SpawnerEntityType {
    public static final Codec<SpawnerEntityType> DIRECT_CODEC = Builder.CODEC.comapFlatMap(StorageHelper.tryToApply(Builder::build), Builder::new);
    public static final Codec<Holder<SpawnerEntityType>> HOLDER_CODEC = RegistryFileCodec.create(DatapackRegistries.SPAWNER_ENTITY_TYPE, DIRECT_CODEC, true);
    public static final Codec<IRandom<Holder<SpawnerEntityType>>> RANDOM_HOLDER_CODEC = IRandom.<Holder<SpawnerEntityType>>codecBuilder()
            .valueCodec("type", HOLDER_CODEC)
            .pools(DatapackRegistries.SPAWNER_ENTITY_TYPE_POOLS)
            .build();

    public final ResourceKey<EntityType<?>> entity;
    private final List<Holder<SpawnerEntityProperties>> layeredProperties;
    @Nullable
    private SpawnerEntityProperties combinedProperties;

    private SpawnerEntityType(ResourceKey<EntityType<?>> entity, List<Holder<SpawnerEntityProperties>> layeredProperties) {
        this.entity = entity;
        this.layeredProperties = layeredProperties;
    }

    public SpawnerEntityProperties properties() {
        if (combinedProperties == null) {
            combinedProperties = SpawnerEntityProperties.combined(layeredProperties);
        }
        return combinedProperties;
    }

    public static class Builder {
        public static final Codec<Builder> CODEC = RecordCodecBuilder.create(instance -> instance
                .group(
                        ResourceKey.codec(Registries.ENTITY_TYPE).fieldOf("entity_type").forGetter(builder -> builder.entity),
                        SpawnerEntityProperties.HOLDER_CODEC.listOf().optionalFieldOf("properties").forGetter(builder ->
                                builder.properties.isEmpty() ? Optional.empty() : Optional.of(builder.properties))
                ).apply(instance, (entityType, properties) -> {
                    final Builder builder = new Builder();
                    builder.entity = entityType;
                    builder.properties.addAll(properties.orElse(List.of()));
                    return builder;
                }));

        @Nullable
        private ResourceKey<EntityType<?>> entity = null;

        private final List<Holder<SpawnerEntityProperties>> properties = new ArrayList<>();

        public Builder() {
        }

        public Builder(SpawnerEntityType instance) {
            this.entity = instance.entity;
            this.properties.addAll(instance.layeredProperties);
        }

        public Builder entity(@Nullable ResourceKey<EntityType<?>> entity) {
            this.entity = entity;
            return this;
        }

        public Builder properties(Holder<SpawnerEntityProperties> properties) {
            this.properties.add(properties);
            return this;
        }

        public SpawnerEntityType build() {
            Objects.requireNonNull(entity);
            return new SpawnerEntityType(entity, properties);
        }
    }
}