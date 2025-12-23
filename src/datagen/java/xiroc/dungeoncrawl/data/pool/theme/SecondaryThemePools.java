package xiroc.dungeoncrawl.data.pool.theme;

import net.minecraft.core.Holder;
import net.minecraft.data.worldgen.BootstrapContext;
import xiroc.dungeoncrawl.data.SharedKeys;
import xiroc.dungeoncrawl.datapack.registry.DatapackRegistries;
import xiroc.dungeoncrawl.dungeon.theme.SecondaryTheme;
import xiroc.dungeoncrawl.util.random.IRandom;

public interface SecondaryThemePools {
    static void generate(BootstrapContext<IRandom<Holder<SecondaryTheme>>> context) {
        final var themes = context.lookup(DatapackRegistries.SECONDARY_THEME);

        context.register(SecondaryThemePoolKeys.OAK, IRandom.<Holder<SecondaryTheme>>builder()
                .add(themes.getOrThrow(SharedKeys.Theme.Secondary.OAK))
                .build());
    }
}
