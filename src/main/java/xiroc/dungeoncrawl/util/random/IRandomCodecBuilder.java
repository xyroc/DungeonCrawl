package xiroc.dungeoncrawl.util.random;

import com.mojang.serialization.Codec;
import net.minecraft.core.HolderSet;
import net.minecraft.core.Registry;
import net.minecraft.core.RegistryCodecs;
import net.minecraft.resources.ResourceKey;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;

/**
 * Handles the construction of a {@link Codec} for {@link IRandom}, taking care of the circular references that need
 *  to be established between the direct codec and the pool codec.
 * @param <T>
 */
public class IRandomCodecBuilder<T> {
    @Nullable
    private Codec<T> valueCodec = null;
    @Nullable
    private String valueKey = null;
    @Nullable
    private ResourceKey<Registry<IRandom<T>>> poolRegistry = null;

    @Nullable
    private Codec<IRandom<T>> directCodec = null;

    public IRandomCodecBuilder<T> valueCodec(String valueKey, Codec<T> valueCodec) {
        this.valueKey = valueKey;
        this.valueCodec = valueCodec;
        return this;
    }

    public IRandomCodecBuilder<T> pools(ResourceKey<Registry<IRandom<T>>> poolRegistry) {
        this.poolRegistry = poolRegistry;
        return this;
    }

    public Codec<IRandom<T>> build() {
        Objects.requireNonNull(valueKey, "The required value key was not specified");
        Objects.requireNonNull(valueCodec, "The required value codec was not specified");

        Codec<HolderSet<IRandom<T>>> poolCodec = poolRegistry != null ? Codec.lazyInitialized(this::makePoolCodec) : null;
        // Update state of the builder so that the pool codec can be lazily constructed using the direct codec
        this.directCodec = IRandom.makeCodec(IRandom.makeBuilderCodec(valueCodec, valueKey, poolCodec));
        return this.directCodec;
    }

    private Codec<HolderSet<IRandom<T>>> makePoolCodec() {
        Objects.requireNonNull(directCodec, "Cannot construct the pool codec without the direct codec");
        Objects.requireNonNull(poolRegistry);
        return RegistryCodecs.homogeneousList(poolRegistry, directCodec, true);
    }
}
