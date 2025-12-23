package xiroc.dungeoncrawl.data.pool.theme;

import net.minecraft.core.Holder;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import xiroc.dungeoncrawl.data.DataGen;
import xiroc.dungeoncrawl.datapack.registry.DatapackRegistries;
import xiroc.dungeoncrawl.dungeon.theme.SecondaryTheme;
import xiroc.dungeoncrawl.util.random.IRandom;

public interface SecondaryThemePoolKeys {
    ResourceKey<IRandom<Holder<SecondaryTheme>>> OAK = key(DataGen.resource("oak"));

    private static ResourceKey<IRandom<Holder<SecondaryTheme>>> key(ResourceLocation identifier) {
        return ResourceKey.create(DatapackRegistries.SECONDARY_THEME_POOLS, identifier);
    }
}
