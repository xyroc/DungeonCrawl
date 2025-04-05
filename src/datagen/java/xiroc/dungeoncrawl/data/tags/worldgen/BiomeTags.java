package xiroc.dungeoncrawl.data.tags.worldgen;

import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.data.tags.BiomeTagsProvider;
import xiroc.dungeoncrawl.DungeonCrawl;
import xiroc.dungeoncrawl.init.ModTags;

import java.util.concurrent.CompletableFuture;

import static net.minecraft.tags.BiomeTags.*;

public class BiomeTags extends BiomeTagsProvider {

    public BiomeTags(PackOutput packOutput, CompletableFuture<HolderLookup.Provider> provider) {
        super(packOutput, provider, DungeonCrawl.MOD_ID);
    }

    @Override
    protected void addTags(HolderLookup.Provider provider) {
        this.tag(ModTags.HAS_DUNGEON)
                .addTag(IS_BADLANDS)
                .addTag(IS_FOREST)
                .addTag(IS_HILL)
                .addTag(IS_JUNGLE)
                .addTag(IS_MOUNTAIN)
                .addTag(IS_TAIGA);
    }

    @Override
    public String getName() {
        return "Dungeon Crawl Biome Tags";
    }
}
