package xiroc.dungeoncrawl.dungeon.theme;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import xiroc.dungeoncrawl.dungeon.block.provider.BlockStateProvider;

public interface ThemeSerializers {
    Gson GSON = gsonAdapters(new GsonBuilder()).create();

    static GsonBuilder gsonAdapters(GsonBuilder builder) {
        return BlockStateProvider.gsonAdapters(builder)
                .registerTypeAdapter(PrimaryTheme.class, new PrimaryTheme.Serializer())
                .registerTypeAdapter(SecondaryTheme.class, new SecondaryTheme.Serializer());
    }
}
