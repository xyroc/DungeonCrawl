package xiroc.dungeoncrawl.data.mappings;

import net.minecraft.data.DataGenerator;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.biome.Biome;
import xiroc.dungeoncrawl.data.JsonDataProvider;
import xiroc.dungeoncrawl.datapack.DatapackDirectories;
import xiroc.dungeoncrawl.dungeon.theme.SecondaryTheme;
import xiroc.dungeoncrawl.util.random.IRandom;
import xiroc.dungeoncrawl.util.random.RandomMapping;

import java.util.function.BiConsumer;
import java.util.function.Function;

public class SecondaryThemeMappings extends JsonDataProvider<RandomMapping<String, SecondaryTheme>> {
    public SecondaryThemeMappings(DataGenerator generator) {
        super(generator, "Secondary Theme Mappings", DatapackDirectories.SECONDARY_THEME_MAPPINGS.path(),
                (mapping) -> RandomMapping.serialize(mapping, Function.identity(), IRandom.SECONDARY_THEME));
    }

    @Override
    public void collect(BiConsumer<ResourceLocation, RandomMapping<String, SecondaryTheme>> collector) {
        // TODO: add secondary theme mappings
    }

    private static String biome(ResourceKey<Biome> biome) {
        return biome.getRegistryName().toString();
    }

    private static RandomMapping.Builder<String, SecondaryTheme> builder() {
        return new RandomMapping.Builder<>();
    }
}
