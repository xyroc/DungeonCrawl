package xiroc.dungeoncrawl.data.tags.worldgen;

import net.minecraft.data.BuiltinRegistries;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.tags.TagsProvider;
import net.minecraft.world.level.biome.Biome;
import xiroc.dungeoncrawl.DungeonCrawl;
import xiroc.dungeoncrawl.init.ModTags;

import static net.minecraft.tags.BiomeTags.IS_BADLANDS;
import static net.minecraft.tags.BiomeTags.IS_FOREST;
import static net.minecraft.tags.BiomeTags.IS_HILL;
import static net.minecraft.tags.BiomeTags.IS_JUNGLE;
import static net.minecraft.tags.BiomeTags.IS_MOUNTAIN;
import static net.minecraft.tags.BiomeTags.IS_TAIGA;

public class BiomeTags extends TagsProvider<Biome> {

    public BiomeTags(DataGenerator p_211094_) {
        super(p_211094_, BuiltinRegistries.BIOME, DungeonCrawl.MOD_ID, null);
    }

    @Override
    protected void addTags() {
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
