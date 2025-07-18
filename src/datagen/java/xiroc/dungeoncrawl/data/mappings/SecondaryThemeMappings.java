package xiroc.dungeoncrawl.data.mappings;

import net.minecraft.core.Holder;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.world.level.biome.Biome;
import xiroc.dungeoncrawl.data.SharedKeys;
import xiroc.dungeoncrawl.datapack.registry.DatapackRegistries;
import xiroc.dungeoncrawl.dungeon.theme.SecondaryTheme;
import xiroc.dungeoncrawl.util.random.IRandom;
import xiroc.dungeoncrawl.util.random.RandomMapping;

public interface SecondaryThemeMappings {
    static void generate(BootstrapContext<RandomMapping<Biome, SecondaryTheme>> context) {
        var secondaryThemes = context.lookup(DatapackRegistries.SECONDARY_THEME);

        context.register(SharedKeys.ThemeMappings.Secondary.DEFAULT, new RandomMapping.Builder<Biome, SecondaryTheme>()
                .fallback(new IRandom.Builder<Holder<SecondaryTheme>>()
                        .add(secondaryThemes.getOrThrow(SharedKeys.Theme.Secondary.OAK))
                        .build())
                .build());
    }
}
