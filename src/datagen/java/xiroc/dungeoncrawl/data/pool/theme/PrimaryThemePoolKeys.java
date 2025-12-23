package xiroc.dungeoncrawl.data.pool.theme;

import net.minecraft.core.Holder;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import xiroc.dungeoncrawl.data.DataGen;
import xiroc.dungeoncrawl.datapack.registry.DatapackRegistries;
import xiroc.dungeoncrawl.dungeon.theme.PrimaryTheme;
import xiroc.dungeoncrawl.util.random.IRandom;

public interface PrimaryThemePoolKeys {
    ResourceKey<IRandom<Holder<PrimaryTheme>>> FOREST = key(DataGen.resource("forest"));

    private static ResourceKey<IRandom<Holder<PrimaryTheme>>> key(ResourceLocation identifier) {
        return ResourceKey.create(DatapackRegistries.PRIMARY_THEME_POOLS, identifier);
    }
}
