package xiroc.dungeoncrawl.datapack.registry;

import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.Nullable;
import xiroc.dungeoncrawl.datapack.DatapackDirectory;

import java.io.Reader;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Function;

public class DatapackRegistryBuilder<T> {
    private final DatapackDirectory directory;
    @Nullable
    private Consumer<BiConsumer<ResourceLocation, T>> builtinEntries;


    public DatapackRegistryBuilder(DatapackDirectory directory) {
        this.directory = directory;
    }

    public DatapackRegistryBuilder<T> builtinEntries(Consumer<BiConsumer<ResourceLocation, T>> builtinEntries) {
        this.builtinEntries = builtinEntries;
        return this;
    }

    public DatapackRegistry<T> nonInheriting(DatapackRegistry.Parser<T> parser) {
        return new DatapackRegistry<>(directory, builtinEntries, parser);
    }

    public <B extends InheritingBuilder<T, B>> InheritingDatapackRegistry<T, B> inheriting(Function<Reader, B> parser) {
        return new InheritingDatapackRegistry<>(directory, builtinEntries, parser);
    }
}
