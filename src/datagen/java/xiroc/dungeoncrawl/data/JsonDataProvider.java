package xiroc.dungeoncrawl.data;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.DataProvider;
import net.minecraft.data.HashCache;
import net.minecraft.resources.ResourceLocation;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import xiroc.dungeoncrawl.datapack.DatapackNamespaces;

import java.io.IOException;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.function.BiConsumer;
import java.util.function.Function;

public abstract class JsonDataProvider<T> implements DataProvider {
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().disableHtmlEscaping().create();

    private final DataGenerator generator;
    private final String name;
    private final String directory;
    private final Function<T, JsonElement> serializer;

    private final Logger logger;

    public JsonDataProvider(DataGenerator generator, String name, String directory, Function<T, JsonElement> serializer) {
        this.generator = generator;
        this.name = name;
        this.directory = directory;
        this.serializer = serializer;

        this.logger = LogManager.getLogger(name);
    }

    @Override
    public void run(HashCache directoryCache) {
        Path outputFolder = this.generator.getOutputFolder();
        HashMap<ResourceLocation, T> elements = new HashMap<>();

        collect(((resourceLocation, element) -> {
            if (elements.containsKey(resourceLocation)) {
                throw new IllegalStateException("Duplicate element: " + resourceLocation);
            }
            elements.put(resourceLocation, element);
        }));

        elements.forEach(((resourceLocation, element) -> {
            Path filePath = createPath(outputFolder, resourceLocation);
            try {
                DataProvider.save(GSON, directoryCache, serializer.apply(element), filePath);
            } catch (IOException e) {
                logger.error("Failed to save {}: {}", resourceLocation, e.getMessage());
            }
        }));
    }

    private Path createPath(Path base, ResourceLocation resourceLocation) {
        return base.resolve("data/" + resourceLocation.getNamespace() + '/' + directory + resourceLocation.getPath() + ".json");
    }

    public abstract void collect(BiConsumer<ResourceLocation, T> collector);

    @Override
    public String getName() {
        return name;
    }
}