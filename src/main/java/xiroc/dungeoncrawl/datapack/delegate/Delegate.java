package xiroc.dungeoncrawl.datapack.delegate;

import com.google.gson.JsonElement;
import net.minecraft.resources.ResourceLocation;

import java.util.function.Supplier;

public interface Delegate<T> extends Supplier<T> {
    T get();
    ResourceLocation key();

    JsonElement serialize();

    static <T> Delegate<T> of(T value, ResourceLocation key) {
        return new HolderDelegate<>(value, key);
    }

    static <T> Delegate<T> of(ResourceLocation key) {
        return new ReferenceDelegate<>(key);
    }
}
