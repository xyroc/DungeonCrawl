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
import xiroc.dungeoncrawl.data.pool.theme.SecondaryThemePoolKeys;
import xiroc.dungeoncrawl.datapack.registry.DatapackRegistries;
import xiroc.dungeoncrawl.dungeon.theme.SecondaryTheme;
import xiroc.dungeoncrawl.util.random.IRandom;

import java.util.concurrent.CompletableFuture;

public class SecondaryThemePoolTags extends TagsProvider<IRandom<Holder<SecondaryTheme>>> {
    public SecondaryThemePoolTags(PackOutput pOutput, CompletableFuture<HolderLookup.Provider> pLookupProvider, @Nullable ExistingFileHelper existingFileHelper) {
        super(pOutput, DatapackRegistries.SECONDARY_THEME_POOLS, pLookupProvider, DungeonCrawl.MOD_ID, existingFileHelper);
    }

    private static TagKey<IRandom<Holder<SecondaryTheme>>> key(ResourceLocation identifier) {
        return TagKey.create(DatapackRegistries.SECONDARY_THEME_POOLS, identifier);
    }

    public static final TagKey<IRandom<Holder<SecondaryTheme>>> OAK = key(SecondaryThemePoolKeys.OAK.location());

    @Override
    protected void addTags(HolderLookup.Provider provider) {
        tag(OAK)
                .add(SecondaryThemePoolKeys.OAK);
    }
}
