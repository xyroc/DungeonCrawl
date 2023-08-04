package xiroc.dungeoncrawl.datapack.delegate;

import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;
import net.minecraft.resources.ResourceLocation;

/**
 * Only used for datagen, not in production.
 */
record ReferenceDelegate<T>(ResourceLocation key) implements Delegate<T> {
    @Override
    public T get() {
        throw new UnsupportedOperationException("Attempted to resolve a reference delegate.");
    }

    @Override
    public JsonElement serialize() {
        return new JsonPrimitive(this.key.toString());
    }
}
