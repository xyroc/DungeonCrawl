package xiroc.dungeoncrawl.data.tags.worldgen;

import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.data.tags.BiomeTagsProvider;
import net.minecraft.world.level.biome.Biomes;
import net.neoforged.neoforge.common.Tags;
import xiroc.dungeoncrawl.DungeonCrawl;
import xiroc.dungeoncrawl.init.ModTags;

import java.util.concurrent.CompletableFuture;


public class DBiomeTags extends BiomeTagsProvider {

    public DBiomeTags(PackOutput packOutput, CompletableFuture<HolderLookup.Provider> provider) {
        super(packOutput, provider, DungeonCrawl.MOD_ID);
    }

    @Override
    protected void addTags(HolderLookup.Provider provider) {
        tag(ModTags.HAS_DUNGEON)
                .addTag(Tags.Biomes.IS_BADLANDS)
                .addTag(Tags.Biomes.IS_BEACH)
                .addTag(Tags.Biomes.IS_BIRCH_FOREST)
                .addTag(Tags.Biomes.IS_DESERT)
                .addTag(Tags.Biomes.IS_FOREST)
                .addTag(Tags.Biomes.IS_HILL)
                .addTag(Tags.Biomes.IS_JUNGLE)
                .addTag(Tags.Biomes.IS_PLAINS)
                .addTag(Tags.Biomes.IS_MOUNTAIN)
                .addTag(Tags.Biomes.IS_SAVANNA)
                .addTag(Tags.Biomes.IS_SNOWY_PLAINS)
                .addTag(Tags.Biomes.IS_SWAMP)
                .addTag(Tags.Biomes.IS_TAIGA)
                .add(Biomes.ICE_SPIKES)
                .add(Biomes.STONY_SHORE);
    }

    @Override
    public String getName() {
        return "Dungeon Crawl Biome Tags";
    }
}
