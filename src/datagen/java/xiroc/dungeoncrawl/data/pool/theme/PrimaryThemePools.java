package xiroc.dungeoncrawl.data.pool.theme;

import net.minecraft.core.Holder;
import net.minecraft.data.worldgen.BootstrapContext;
import xiroc.dungeoncrawl.data.SharedKeys;
import xiroc.dungeoncrawl.datapack.registry.DatapackRegistries;
import xiroc.dungeoncrawl.dungeon.theme.PrimaryTheme;
import xiroc.dungeoncrawl.util.random.IRandom;

public interface PrimaryThemePools {
    static void generate(BootstrapContext<IRandom<Holder<PrimaryTheme>>> context) {
        final var themes = context.lookup(DatapackRegistries.PRIMARY_THEME);

        context.register(PrimaryThemePoolKeys.FOREST, IRandom.<Holder<PrimaryTheme>>builder()
                .add(themes.getOrThrow(SharedKeys.Theme.Primary.FOREST))
                .build());
    }
}
