package xiroc.dungeoncrawl.data.mappings;

import net.minecraft.core.Holder;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.world.level.biome.Biome;
import xiroc.dungeoncrawl.data.SharedKeys;
import xiroc.dungeoncrawl.datapack.registry.DatapackRegistries;
import xiroc.dungeoncrawl.dungeon.theme.PrimaryTheme;
import xiroc.dungeoncrawl.util.random.IRandom;
import xiroc.dungeoncrawl.util.random.RandomMapping;

public interface PrimaryThemeMappings {
    static void generate(BootstrapContext<RandomMapping<Biome, PrimaryTheme>> context) {
        var primaryThemes = context.lookup(DatapackRegistries.PRIMARY_THEME);

        context.register(SharedKeys.ThemeMappings.Primary.DEFAULT, new RandomMapping.Builder<Biome, PrimaryTheme>()
                .fallback(new IRandom.Builder<Holder<PrimaryTheme>>()
                        .add(primaryThemes.getOrThrow(SharedKeys.Theme.Primary.FOREST))
                        .build())
                .build());
    }
}
