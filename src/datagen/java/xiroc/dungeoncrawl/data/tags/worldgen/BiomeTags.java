package xiroc.dungeoncrawl.data.tags.worldgen;

import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.PackOutput;
import net.minecraft.data.tags.TagsProvider;
import net.minecraft.world.level.biome.Biome;
import net.minecraftforge.common.data.ExistingFileHelper;
import xiroc.dungeoncrawl.DungeonCrawl;
import xiroc.dungeoncrawl.init.ModTags;

import java.util.concurrent.CompletableFuture;

import static net.minecraft.tags.BiomeTags.IS_BADLANDS;
import static net.minecraft.tags.BiomeTags.IS_FOREST;
import static net.minecraft.tags.BiomeTags.IS_HILL;
import static net.minecraft.tags.BiomeTags.IS_JUNGLE;
import static net.minecraft.tags.BiomeTags.IS_MOUNTAIN;
import static net.minecraft.tags.BiomeTags.IS_TAIGA;

public class BiomeTags extends TagsProvider<Biome> {

    public BiomeTags(PackOutput packOutput, CompletableFuture<HolderLookup.Provider> provider, ExistingFileHelper existingFileHelper) {
        super(packOutput, Registries.BIOME, provider, DungeonCrawl.MOD_ID, existingFileHelper);
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
