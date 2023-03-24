package xiroc.dungeoncrawl.data.mappings;

import net.minecraft.data.DataGenerator;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.biome.Biome;
import xiroc.dungeoncrawl.data.JsonDataProvider;
import xiroc.dungeoncrawl.datapack.DatapackDirectories;
import xiroc.dungeoncrawl.dungeon.theme.PrimaryTheme;
import xiroc.dungeoncrawl.util.random.RandomMapping;
import xiroc.dungeoncrawl.util.random.WeightedRandom;

import java.util.function.BiConsumer;
import java.util.function.Function;

public class PrimaryThemeMappings extends JsonDataProvider<RandomMapping<String, PrimaryTheme>> {
    public PrimaryThemeMappings(DataGenerator generator) {
        super(generator, "Primary Theme Mappings", DatapackDirectories.PRIMARY_THEME_MAPPINGS.path(),
                (mapping) -> RandomMapping.serialize(mapping, Function.identity(), WeightedRandom.PRIMARY_THEME));
    }

    @Override
    public void collect(BiConsumer<ResourceLocation, RandomMapping<String, PrimaryTheme>> collector) {
        // TODO: add primary theme mappings
    }

    private static String biome(ResourceKey<Biome> biome) {
        return biome.getRegistryName().toString();
    }

    private static RandomMapping.Builder<String, PrimaryTheme> builder() {
        return new RandomMapping.Builder<>();
    }
}
