package xiroc.dungeoncrawl.datapack.registry;

import com.google.common.collect.ImmutableMap;
import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.DynamicOps;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import xiroc.dungeoncrawl.datapack.DatapackDirectory;
import xiroc.dungeoncrawl.exception.DatapackLoadException;

import java.io.InputStreamReader;
import java.io.Reader;
import java.util.HashMap;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Function;

public class DatapackRegistry<T> {
    static final String FILE_ENDING = ".json";

    final DatapackDirectory directory;
    final Consumer<BiConsumer<ResourceLocation, T>> builtin;
    final Parser<T> parser;

    ImmutableMap<ResourceLocation, T> values = ImmutableMap.of();
    // True when this registry's contents were either not yet loaded or invalidated
    boolean isUnloaded = true;
    final HashMap<ResourceLocation, Delegate<T>> unresolvedReferences = new HashMap<>();

    DatapackRegistry(DatapackDirectory directory, Consumer<BiConsumer<ResourceLocation, T>> builtin, Function<Reader, T> fromJson) {
        this(directory, builtin, Parser.simple(fromJson));
    }

    DatapackRegistry(DatapackDirectory directory, Consumer<BiConsumer<ResourceLocation, T>> builtin, Parser<T> parser) {
        this.directory = directory;
        this.builtin = builtin;
        this.parser = parser;
    }

    public void unload() {
        isUnloaded = true;
    }

    public void reload(ResourceManager resourceManager) {
        ImmutableMap.Builder<ResourceLocation, T> builder = ImmutableMap.builder();
        builtin.accept(builder::put); // register builtin entries
        resourceManager.listResources(directory.path(), path -> path.endsWith(FILE_ENDING)).forEach(resource -> {
            try {
                ResourceLocation key = directory.key(resource, FILE_ENDING);
                T value = parser.parse(resourceManager, key, new InputStreamReader(resourceManager.getResource(resource).getInputStream()));
                builder.put(key, value);
            } catch (Exception exception) {
                throw new DatapackLoadException("Failed to load " + resource.toString() + ": " + exception.getMessage());
            }
        });
        values = builder.build();
        isUnloaded = false;

        unresolvedReferences.forEach((key, reference) -> reference.resolve(this));
        unresolvedReferences.clear();
    }

    public int entryCount() {
        return values.size();
    }

    T get(ResourceLocation key) {
        if (isUnloaded) {
            throw new IllegalStateException("Attempted to retrieve " + key + " from unloaded registry");
        }
        return values.get(key);
    }

    public Delegate<T> delegateOrThrow(ResourceLocation key) {
        if (isUnloaded) {
            return unresolvedReferences.computeIfAbsent(key, Delegate::of);
        }
        T value = get(key);
        if (value == null) {
            throw new RuntimeException("No value for key " + key);
        }
        return Delegate.of(value, key);
    }

    ImmutableMap<ResourceLocation, T> getValues() {
        return values;
    }

    public Codec<Delegate<T>> delegateCodec() {
        return new Codec<>() {
            @Override
            public <A> DataResult<Pair<Delegate<T>, A>> decode(DynamicOps<A> ops, A input) {
                return Codec.STRING.decode(ops, input).flatMap( key -> {
                    try {
                        return DataResult.success(Pair.of(delegateOrThrow(new ResourceLocation(key.getFirst())), key.getSecond()));
                    } catch (Exception e) {
                        return DataResult.error("Could not decode for key " + key.getFirst() + ":" + e.getMessage());
                    }
                });
            }

            @Override
            public <A> DataResult<A> encode(Delegate<T> tDelegate, DynamicOps<A> ops, A prefix) {
                return DataResult.success(tDelegate.key().toString()).flatMap(key -> Codec.STRING.encode(key, ops, prefix));
            }
        };
    }

    interface Parser<T> {
        static <T> Parser<T> simple(Function<Reader, T> parser) {
            return keyed((key, reader) -> parser.apply(reader));
        }

        static <T> Parser<T> keyed(BiFunction<ResourceLocation, Reader, T> parser) {
            return (resourceManager, key, reader) -> parser.apply(key, reader);
        }

        T parse(ResourceManager resourceManager, ResourceLocation key, Reader reader) throws Exception;
    }
}
