package xiroc.dungeoncrawl.data;

import com.google.gson.JsonElement;
import net.minecraft.data.CachedOutput;
import net.minecraft.data.DataProvider;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;

import java.nio.file.Path;
import java.util.HashMap;
import java.util.concurrent.CompletableFuture;
import java.util.function.BiConsumer;
import java.util.function.Function;

public abstract class JsonDataProvider<T> implements DataProvider {
    private final PackOutput.PathProvider pathProvider;
    private final String name;
    private final Function<T, JsonElement> serializer;

    public JsonDataProvider(PackOutput packOutput, String name, String directory, Function<T, JsonElement> serializer) {
        this.pathProvider = packOutput.createPathProvider(PackOutput.Target.DATA_PACK, directory);
        this.name = name;
        this.serializer = serializer;
    }

    @Override
    public CompletableFuture<?> run(CachedOutput directoryCache) {
        HashMap<ResourceLocation, T> elements = new HashMap<>();

        collect(((resourceLocation, element) -> {
            if (elements.containsKey(resourceLocation)) {
                throw new IllegalStateException("Duplicate element: " + resourceLocation);
            }
            elements.put(resourceLocation, element);
        }));

        return CompletableFuture.allOf(elements.entrySet().stream().map((entry) -> {
            Path path = pathProvider.json(entry.getKey());
            return DataProvider.saveStable(directoryCache, serializer.apply(entry.getValue()), path);
        }).toArray(CompletableFuture[]::new));
    }

    public abstract void collect(BiConsumer<ResourceLocation, T> collector);

    @Override
    public String getName() {
        return name;
    }
}