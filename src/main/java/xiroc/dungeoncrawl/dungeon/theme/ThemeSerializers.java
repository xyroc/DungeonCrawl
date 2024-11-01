package xiroc.dungeoncrawl.dungeon.theme;

import com.google.gson.GsonBuilder;
import xiroc.dungeoncrawl.dungeon.block.provider.BlockStateProvider;

public interface ThemeSerializers {
    static void gsonAdapters(GsonBuilder builder) {
        builder.registerTypeAdapter(PrimaryTheme.class, new PrimaryTheme.Serializer())
                .registerTypeAdapter(SecondaryTheme.class, new SecondaryTheme.Serializer());
    }
}
