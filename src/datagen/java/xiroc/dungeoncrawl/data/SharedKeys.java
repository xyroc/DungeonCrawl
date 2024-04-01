package xiroc.dungeoncrawl.data;

import net.minecraft.resources.ResourceLocation;
import xiroc.dungeoncrawl.datapack.DatapackNamespaces;

public interface SharedKeys {
    private static ResourceLocation external(String path) {
        return new ResourceLocation(DatapackNamespaces.DEFAULT, path);
    }

    private static ResourceLocation builtin(String path) {
        return new ResourceLocation(DatapackNamespaces.BUILT_IN, path);
    }
}
