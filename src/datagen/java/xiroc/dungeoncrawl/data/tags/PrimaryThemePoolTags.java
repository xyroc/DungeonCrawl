package xiroc.dungeoncrawl.data.tags;

import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.data.tags.TagsProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import org.jetbrains.annotations.Nullable;
import xiroc.dungeoncrawl.DungeonCrawl;
import xiroc.dungeoncrawl.data.pool.theme.PrimaryThemePoolKeys;
import xiroc.dungeoncrawl.datapack.registry.DatapackRegistries;
import xiroc.dungeoncrawl.dungeon.theme.PrimaryTheme;
import xiroc.dungeoncrawl.util.random.IRandom;

import java.util.concurrent.CompletableFuture;

public class PrimaryThemePoolTags extends TagsProvider<IRandom<Holder<PrimaryTheme>>> {
    public PrimaryThemePoolTags(PackOutput pOutput, CompletableFuture<HolderLookup.Provider> pLookupProvider, @Nullable ExistingFileHelper existingFileHelper) {
        super(pOutput, DatapackRegistries.PRIMARY_THEME_POOLS, pLookupProvider, DungeonCrawl.MOD_ID, existingFileHelper);
    }

    private static TagKey<IRandom<Holder<PrimaryTheme>>> key(ResourceLocation identifier) {
        return TagKey.create(DatapackRegistries.PRIMARY_THEME_POOLS, identifier);
    }

    public static final TagKey<IRandom<Holder<PrimaryTheme>>> FOREST = key(PrimaryThemePoolKeys.FOREST.location());

    @Override
    protected void addTags(HolderLookup.Provider provider) {
        tag(FOREST)
                .add(PrimaryThemePoolKeys.FOREST);
    }
}
