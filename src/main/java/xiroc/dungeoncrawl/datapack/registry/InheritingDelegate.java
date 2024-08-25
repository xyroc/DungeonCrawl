package xiroc.dungeoncrawl.datapack.registry;

import com.google.gson.JsonElement;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.Nullable;

import java.util.function.Function;

/**
 * Represents either a reference to an object or a builder, which can inherit, for an object.
 * <p>
 * Used mainly to handle cases where a field in a json file can be either a key (reference) or a direct definition with inheritance.
 */
public class InheritingDelegate<T, B extends InheritingBuilder<T, B>> extends Delegate<B> {
    protected InheritingDelegate(@Nullable B builder, @Nullable ResourceLocation key) {
        super(builder, key);
        if (builder != null && key != null) {
            throw new IllegalArgumentException("Providing both a key and a builder is not allowed");
        }
    }

    public Delegate<T> transform(DatapackRegistry<T> registry) {
        if (key != null) {
            return registry.delegateOrThrow(key);
        }
        return Delegate.of(get().build());
    }

    public InheritingDelegate<T, B> inherit(@Nullable InheritingDelegate<T, B> from) {
        if (from == null) {
            // No parent
            return this;
        }
        if (value != null) {
            if (from.value != null) {
                // We have a builder and are inheriting from a delegate with a builder, inherit from the builder unless replace was specified
                value = InheritingBuilder.inheritOrReplace(value, from.get());
            }
            return this;
        }
        if (key != null) {
            // We have a key, doesn't matter what the parent has since we cant merge keys
            return this;
        }
        // We have neither a key nor a builder, just take what the parent has
        return from;
    }

    public static <T, B extends InheritingBuilder<T, B>> InheritingDelegate<T, B> deserialize(JsonElement json, Function<JsonElement, B> deserializer) {
        if (json.isJsonPrimitive()) {
            return new InheritingDelegate<>(null, new ResourceLocation(json.getAsString()));
        }
        return new InheritingDelegate<>(deserializer.apply(json), null);
    }

    @Nullable
    public static <T, B extends InheritingBuilder<T, B>> InheritingDelegate<T, B> inheritOrChoose(@Nullable InheritingDelegate<T, B> primary, @Nullable InheritingDelegate<T, B> secondary) {
        if (primary != null && secondary != null) {
            return primary.inherit(secondary);
        }
        return InheritingBuilder.choose(primary, secondary);
    }

    public static <T, B extends InheritingBuilder<T, B>> InheritingDelegate<T, B> ofBuilder(B builder) {
        return new InheritingDelegate<>(builder, null);
    }

    public static <T, B extends InheritingBuilder<T, B>> InheritingDelegate<T, B> ofKey(ResourceLocation key) {
        return new InheritingDelegate<>(null, key);
    }
}
