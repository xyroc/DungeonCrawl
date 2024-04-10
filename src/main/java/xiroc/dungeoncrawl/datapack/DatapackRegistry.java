package xiroc.dungeoncrawl.datapack;

import com.google.common.collect.ImmutableMap;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import xiroc.dungeoncrawl.exception.DatapackLoadException;

import java.io.InputStreamReader;
import java.io.Reader;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Function;

public class DatapackRegistry<T> {
    private static final String FILE_ENDING = ".json";

    private final DatapackDirectories.Directory directory;
    private final Consumer<BiConsumer<ResourceLocation, T>> builtin;
    private final BiFunction<ResourceLocation, Reader, T> fromJson;

    private ImmutableMap<ResourceLocation, T> values;

    public DatapackRegistry(DatapackDirectories.Directory directory, Consumer<BiConsumer<ResourceLocation, T>> builtin, Function<Reader, T> fromJson) {
        this(directory, builtin, (key, reader) -> fromJson.apply(reader));
    }

    public DatapackRegistry(DatapackDirectories.Directory directory, Consumer<BiConsumer<ResourceLocation, T>> builtin, BiFunction<ResourceLocation, Reader, T> fromJson) {
        this.directory = directory;
        this.builtin = builtin;
        this.fromJson = fromJson;
    }

    public void reload(ResourceManager resourceManager) {
        ImmutableMap.Builder<ResourceLocation, T> builder = ImmutableMap.builder();
        builtin.accept(builder::put); // register builtin entries
        resourceManager.listResources(directory.path(), path -> path.endsWith(FILE_ENDING)).forEach(resource -> {
            try {
                ResourceLocation key = directory.key(resource, FILE_ENDING);
                T value = fromJson.apply(key, new InputStreamReader(resourceManager.getResource(resource).getInputStream()));
                builder.put(key, value);
            } catch (Exception exception) {
                throw new DatapackLoadException("Failed to load " + resource.toString() + ": " + exception.getMessage());
            }
        });
        values = builder.build();
    }

    public T get(ResourceLocation key) {
        return values.get(key);
    }
}
