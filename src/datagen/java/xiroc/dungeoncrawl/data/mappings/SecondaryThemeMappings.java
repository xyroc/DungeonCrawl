package xiroc.dungeoncrawl.data.mappings;

import net.minecraft.data.DataGenerator;
import net.minecraft.resources.ResourceLocation;
import xiroc.dungeoncrawl.data.JsonDataProvider;
import xiroc.dungeoncrawl.datapack.DatapackDirectories;
import xiroc.dungeoncrawl.dungeon.theme.SecondaryTheme;
import xiroc.dungeoncrawl.util.JSONUtils;
import xiroc.dungeoncrawl.util.random.RandomMapping;

import java.util.function.BiConsumer;

public class SecondaryThemeMappings extends JsonDataProvider<RandomMapping.Builder<SecondaryTheme>> {
    public SecondaryThemeMappings(DataGenerator generator) {
        super(generator, "Secondary Theme Mappings", DatapackDirectories.SECONDARY_THEME_MAPPINGS.path(),
                (mapping) -> JSONUtils.GSON.toJsonTree(mapping, RandomMapping.Types.SECONDARY_THEME));
    }

    @Override
    public void collect(BiConsumer<ResourceLocation, RandomMapping.Builder<SecondaryTheme>> collector) {
        // TODO: add secondary theme mappings
    }
}
