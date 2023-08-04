package xiroc.dungeoncrawl.datapack.delegate;

import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;
import net.minecraft.resources.ResourceLocation;

/**
 * Only used in production, not for datagen.
 */
record HolderDelegate<T>(T value, ResourceLocation key) implements Delegate<T> {
    @Override
    public T get() {
        return this.value;
    }

    @Override
    public JsonElement serialize() {
        return new JsonPrimitive(key.toString());
    }
}
