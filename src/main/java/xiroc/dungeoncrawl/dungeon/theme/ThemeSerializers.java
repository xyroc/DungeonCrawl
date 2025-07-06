package xiroc.dungeoncrawl.dungeon.theme;

import com.google.gson.GsonBuilder;
import xiroc.dungeoncrawl.datapack.registry.DatapackRegistries;
import xiroc.dungeoncrawl.datapack.registry.Delegate;
import xiroc.dungeoncrawl.util.random.IRandom;

public interface ThemeSerializers {
    static void gsonAdapters(GsonBuilder builder) {
        builder.registerTypeAdapter(PrimaryTheme.class, new PrimaryTheme.Serializer())
                .registerTypeAdapter(PrimaryTheme.Types.DELEGATE, new Delegate.Serializer<>(DatapackRegistries.PRIMARY_THEME, null))
                .registerTypeAdapter(PrimaryTheme.Types.RANDOM_BUILDER, new IRandom.BuilderSerializer<Delegate<PrimaryTheme>>(PrimaryTheme.Types.DELEGATE, "theme").wrapped())
                .registerTypeAdapter(SecondaryTheme.class, new SecondaryTheme.Serializer())
                .registerTypeAdapter(SecondaryTheme.Types.DELEGATE, new Delegate.Serializer<>(DatapackRegistries.SECONDARY_THEME, null))
                .registerTypeAdapter(SecondaryTheme.Types.RANDOM_BUILDER, new IRandom.BuilderSerializer<Delegate<SecondaryTheme>>(SecondaryTheme.Types.DELEGATE, "theme").wrapped());
    }
}
