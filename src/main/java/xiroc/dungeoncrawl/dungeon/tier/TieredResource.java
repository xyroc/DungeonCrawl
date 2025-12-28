package xiroc.dungeoncrawl.dungeon.tier;

import com.google.common.collect.ImmutableList;
import com.mojang.datafixers.util.Either;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.codecs.UnboundedMapCodec;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.storage.loot.LootTable;
import xiroc.dungeoncrawl.util.StorageHelper;
import xiroc.dungeoncrawl.util.storage.GlobalCodecs;

import java.util.*;

/**
 * Represents an n to 1 mapping from non-negative integers to objects of any type.
 * <p>
 * The mapping is defined through tiers.
 * For any given non-negative integer <b>n</b>, the mapping process is done by finding the tier
 * with the highest starting point less than or equal to <b>n</b>.
 * <p>
 * There must always be a tier starting at the number zero to ensure that there is a mapping for all non-negative integers.
 */
public interface TieredResource<T> {

    interface Codecs {
        static <T> Codec<TieredResource<T>> makeCodec(Codec<T> resourceCodec) {
            final Codec<Builder<T>> singleTierBuilderCodec = resourceCodec.flatComapMap(Builder::new, builder -> {
                if (!builder.followingTiers.isEmpty()) {
                    return DataResult.error(() -> "Cannot serialize a multi-tier builder as a single tier builder");
                }
                return DataResult.success(builder.firstTier);
            });
            final Codec<Builder<T>> multiTierBuilderCodec = new UnboundedMapCodec<>(Codec.STRING, resourceCodec).comapFlatMap(
                    Builder::fromMap,
                    Builder::toMap
            );
            final Codec<Builder<T>> builderCodec = Codec.either(singleTierBuilderCodec, multiTierBuilderCodec).xmap(
                    Either::unwrap,
                    builder -> {
                        if (builder.followingTiers.isEmpty()) {
                            return Either.left(builder);
                        }
                        return Either.right(builder);
                    }
            );

            return builderCodec.comapFlatMap(StorageHelper.tryToApply(Builder::build), Builder::new);
        }

        Codec<TieredResource<ResourceKey<LootTable>>> LOOT_TABLE = makeCodec(GlobalCodecs.LOOT_TABLE);
    }

    /**
     * Find the resource associated with the specified tier.
     *
     * @param tier The tier to find the resource for. Must not be negative.
     */
    T forTier(int tier);

    record SingleTier<T>(T resource) implements TieredResource<T> {
        @Override
        public T forTier(int tier) {
            return resource;
        }
    }

    record MultiTier<T>(T firstTier,
                        ImmutableList<TieredResource.Tier<T>> followingTiers) implements TieredResource<T> {
        @Override
        public T forTier(int tier) {
            T result = firstTier;
            for (TieredResource.Tier<T> followingTier : followingTiers) {
                if (followingTier.startingFrom <= tier) {
                    result = followingTier.resource;
                } else {
                    break;
                }
            }
            return result;
        }

    }

    record Tier<T>(T resource, int startingFrom) {
    }

    class Builder<T> {
        private final T firstTier;
        private final List<Tier<T>> followingTiers = new ArrayList<>();

        public Builder(TieredResource<T> instance) {
            if (instance instanceof SingleTier<T>(T resource)) {
                this.firstTier = resource;
            } else if (instance instanceof MultiTier<T>(T tier, ImmutableList<Tier<T>> tiers)) {
                this.firstTier = tier;
                this.followingTiers.addAll(tiers);
            } else {
                throw new IllegalStateException("Invalid TieredResource type: " + instance.getClass());
            }
        }

        public Builder(T firstTier) {
            this.firstTier = firstTier;
        }

        private static <T> DataResult<Builder<T>> fromMap(Map<String, T> map) {
            final T tierZero = map.get("0");
            if (tierZero == null) {
                return DataResult.error(() -> "Missing the required tier 0");
            }

            final Builder<T> builder = new Builder<>(tierZero);
            for (var entry : map.entrySet()) {
                if (entry.getKey().equals("0")) {
                    continue;
                }
                try {
                    final int tier = Integer.parseUnsignedInt(entry.getKey());
                    builder.tier(entry.getValue(), tier);
                } catch (NumberFormatException exception) {
                    return DataResult.error(() -> "Invalid tier: " + entry.getKey());
                }
            }

            return DataResult.success(builder);
        }

        private Map<String, T> toMap() {
            final Map<String, T> map = new HashMap<>();
            map.put("0", firstTier);
            for (var tier : followingTiers) {
                map.put(Integer.toString(tier.startingFrom), tier.resource);
            }
            return map;
        }

        public Builder<T> tier(T resource, int startingFrom) {
            followingTiers.add(new Tier<>(resource, startingFrom));
            return this;
        }

        public TieredResource<T> build() {
            if (followingTiers.isEmpty()) {
                return new SingleTier<>(firstTier);
            }
            sortTiers();
            return new MultiTier<>(firstTier, ImmutableList.copyOf(followingTiers));
        }

        private void sortTiers() {
            followingTiers.sort(Comparator.comparingInt(Tier::startingFrom));
        }
    }
}
