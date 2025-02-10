package xiroc.dungeoncrawl.dungeon.tier;

import com.google.common.collect.ImmutableList;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import com.google.gson.reflect.TypeToken;
import net.minecraft.resources.ResourceLocation;

import java.lang.reflect.Type;
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

    interface Types {
        Type IDENTIFIER = new TypeToken<Builder<ResourceLocation>>() {}.getType();
    }

    static void gsonAdapters(GsonBuilder builder) {
        builder.registerTypeAdapter(ResourceLocation.class, new ResourceLocation.Serializer());
        builder.registerTypeAdapter(Types.IDENTIFIER, new BuilderSerializer<>(ResourceLocation.class));
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

    record MultiTier<T>(T firstTier, ImmutableList<TieredResource.Tier<T>> followingTiers) implements TieredResource<T> {
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

    record Tier<T>(T resource, int startingFrom) {}

    class Builder<T> {
        private final T firstTier;
        private final List<Tier<T>> followingTiers = new ArrayList<>();

        public Builder(TieredResource<T> instance) {
            if (instance instanceof TieredResource.SingleTier<T> singleTier) {
                this.firstTier = singleTier.resource;
            } else if (instance instanceof TieredResource.MultiTier<T> multiTier) {
                this.firstTier = multiTier.firstTier;
                this.followingTiers.addAll(multiTier.followingTiers);
            } else {
                throw new IllegalStateException("Invalid TieredResource type: " + instance.getClass());
            }
        }

        public Builder(T firstTier) {
            this.firstTier = firstTier;
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

    class BuilderSerializer<T> implements JsonSerializer<Builder<T>>, JsonDeserializer<Builder<T>> {

        private static final String TIER_PREFIX = "tier_";

        private final Type resourceType;

        public BuilderSerializer(Type resourceType) {
            this.resourceType = resourceType;
        }

        @Override
        public Builder<T> deserialize(JsonElement json, Type type, JsonDeserializationContext context) throws JsonParseException {
            if (!json.isJsonObject()) {
                final T firstTier = context.deserialize(json, resourceType);
                return new Builder<>(firstTier);
            }
            final JsonObject jsonObject = json.getAsJsonObject();

            final String firstTierKey = TIER_PREFIX + '0';
            if (!jsonObject.has(firstTierKey)) {
                throw new JsonParseException("tier_0 is missing");
            }
            final T firstTier = context.deserialize(jsonObject.remove(firstTierKey), resourceType);

            final Builder<T> builder = new Builder<>(firstTier);
            jsonObject.entrySet().forEach(entry -> {
                final String key = entry.getKey();
                if (!key.startsWith(TIER_PREFIX)) {
                    throw new JsonParseException("Invalid key: " + key + " does not start with " + TIER_PREFIX);
                }
                final String tierString = key.substring(TIER_PREFIX.length());
                try {
                    final int tier = Integer.parseUnsignedInt(tierString);
                    builder.tier(context.deserialize(entry.getValue(), resourceType), tier);
                } catch (NumberFormatException e) {
                    throw new JsonParseException("Invalid tier: " + tierString + " is not a non-negative integer");
                }
            });

            return builder;
        }

        @Override
        public JsonElement serialize(Builder<T> builder, Type type, JsonSerializationContext context) {
            final JsonElement firstTier = context.serialize(builder.firstTier, resourceType);
            if (builder.followingTiers.isEmpty() && !firstTier.isJsonObject()) {
                return firstTier;
            }

            builder.sortTiers();

            final JsonObject object = new JsonObject();
            object.add(TIER_PREFIX + '0', firstTier);
            for (Tier<T> followingTier : builder.followingTiers) {
                object.add(TIER_PREFIX + followingTier.startingFrom, context.serialize(followingTier.resource, resourceType));
            }
            return object;
        }

    }
}
