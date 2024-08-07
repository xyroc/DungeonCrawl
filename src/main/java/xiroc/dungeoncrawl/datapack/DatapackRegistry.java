package xiroc.dungeoncrawl.datapack;

import com.google.common.collect.ImmutableMap;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import xiroc.dungeoncrawl.datapack.delegate.Delegate;
import xiroc.dungeoncrawl.exception.DatapackLoadException;

import java.io.InputStreamReader;
import java.io.Reader;
import java.util.HashMap;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Function;

public class DatapackRegistry<T> {
    private static final String FILE_ENDING = ".json";

    private final DatapackDirectories.Directory directory;
    private final Consumer<BiConsumer<ResourceLocation, T>> builtin;
    private final Parser<T> parser;

    private ImmutableMap<ResourceLocation, T> values = ImmutableMap.of();
    // True when this registry's contents were either not yet loaded or invalidated
    private boolean isUnloaded = true;
    private final HashMap<ResourceLocation, Delegate<T>> unresolvedReferences = new HashMap<>();

    DatapackRegistry(DatapackDirectories.Directory directory, Consumer<BiConsumer<ResourceLocation, T>> builtin, Function<Reader, T> fromJson) {
        this(directory, builtin, Parser.simple(fromJson));
    }

    DatapackRegistry(DatapackDirectories.Directory directory, Consumer<BiConsumer<ResourceLocation, T>> builtin, Parser<T> parser) {
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

    public T get(ResourceLocation key) {
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
