package xiroc.dungeoncrawl.data.mappings;

import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;
import xiroc.dungeoncrawl.data.JsonDataProvider;
import xiroc.dungeoncrawl.datapack.DatapackDirectories;
import xiroc.dungeoncrawl.dungeon.theme.PrimaryTheme;
import xiroc.dungeoncrawl.util.JSONUtils;
import xiroc.dungeoncrawl.util.random.RandomMapping;

import java.util.function.BiConsumer;

public class PrimaryThemeMappings extends JsonDataProvider<RandomMapping.Builder<PrimaryTheme>> {
    public PrimaryThemeMappings(PackOutput generator) {
        super(generator, "Primary Theme Mappings", DatapackDirectories.PRIMARY_THEME_MAPPINGS.path(),
                (mapping) -> JSONUtils.GSON.toJsonTree(mapping, RandomMapping.Types.PRIMARY_THEME));
    }

    @Override
    public void collect(BiConsumer<ResourceLocation, RandomMapping.Builder<PrimaryTheme>> collector) {
        // TODO: add primary theme mappings
    }
}
