package xiroc.dungeoncrawl.dungeon.tier;

import com.google.common.collect.ImmutableList;
import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.DynamicOps;
import com.mojang.serialization.RecordBuilder;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.storage.loot.LootTable;
import xiroc.dungeoncrawl.util.StorageHelper;
import xiroc.dungeoncrawl.util.storage.GlobalCodecs;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

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
        private static <T> Codec<TieredResource<T>> makeCodec(Codec<T> resourceCodec) {
            return new BuilderCodec<>(resourceCodec).comapFlatMap(StorageHelper.tryToApply(Builder::build), Builder::new);
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

        private Builder(List<Pair<Integer, T>> tiers) {
            if (tiers.isEmpty()) {
                throw new IllegalArgumentException("Specify at least one tier.");
            }
            tiers.sort(Comparator.comparingInt(Pair::getFirst));

            var firstTier = tiers.getFirst();
            if (firstTier.getFirst() != 0) {
                throw new IllegalArgumentException("First tier must be zero, was " + firstTier.getFirst());
            }
            this.firstTier = firstTier.getSecond();

            tiers.subList(1, tiers.size()).stream()
                    .map(pair -> new Tier<>(pair.getSecond(), pair.getFirst()))
                    .forEach(followingTiers::add);
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

    record BuilderCodec<T>(Codec<T> resourceCodec) implements Codec<Builder<T>> {
        private static final String TIER_PREFIX = "tier_";

        @Override
        public <D> DataResult<Pair<Builder<T>, D>> decode(DynamicOps<D> dynamicOps, D input) {
            final var asMap = dynamicOps.getMapValues(input).result();
            if (asMap.isEmpty()) {
                return resourceCodec.decode(dynamicOps, input).map(pair -> pair.mapFirst(Builder::new));
            }

            final DataResult<List<Pair<Integer, T>>> tiers = asMap.get()
                    .map(pair -> pair
                            // Parse keys.
                            .mapFirst(dynamicOps::getStringValue)
                            // Parse values.
                            .mapSecond(d -> resourceCodec.decode(dynamicOps, d)))
                    // Parse tiers.
                    .reduce(DataResult.success(new ArrayList<>()), (tierListResult, rawTier) ->
                            tierListResult.flatMap(tierList -> {
                                DataResult<Pair<Integer, T>> tier = StorageHelper.unpack(rawTier).flatMap(tierDef -> {
                                    String tierName = tierDef.getFirst();
                                    if (!tierName.startsWith(TIER_PREFIX)) {
                                        return DataResult.error(() -> "Invalid tier: " + tierName + " does not start with " + TIER_PREFIX);
                                    }
                                    String tierAsString = tierName.substring(TIER_PREFIX.length());
                                    try {
                                        T resource = tierDef.getSecond().getFirst();
                                        return DataResult.success(Pair.of(Integer.parseUnsignedInt(tierAsString), resource));
                                    } catch (NumberFormatException e) {
                                        return DataResult.error(() -> "Invalid tier: " + tierAsString + " is not a non-negative integer");
                                    }
                                });
                                return StorageHelper.addToList(tierList, tier);
                            }), StorageHelper::concatenateLists);

            return tiers.flatMap(StorageHelper.tryToApply(Builder::new)).map(builder -> Pair.of(builder, dynamicOps.empty()));
        }

        @Override
        public <D> DataResult<D> encode(Builder<T> builder, DynamicOps<D> dynamicOps, D prefix) {
            if (builder.followingTiers.isEmpty()) {
                DataResult<D> encoded = resourceCodec.encode(builder.firstTier, dynamicOps, prefix);
                if (encoded.result().isPresent() && dynamicOps.getMap(encoded.result().get()).result().isEmpty()) {
                    return encoded;
                }
            }

            RecordBuilder<D> tiers = dynamicOps.mapBuilder();
            tiers.add(TIER_PREFIX + '0', resourceCodec.encodeStart(dynamicOps, builder.firstTier));

            for (Tier<T> tier : builder.followingTiers) {
                tiers.add(TIER_PREFIX + tier.startingFrom, resourceCodec.encodeStart(dynamicOps, tier.resource));
            }

            return tiers.build(prefix);
        }
    }
}
