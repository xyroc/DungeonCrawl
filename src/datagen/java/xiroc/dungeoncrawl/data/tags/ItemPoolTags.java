package xiroc.dungeoncrawl.data.tags;

import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.data.tags.TagsProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import org.jetbrains.annotations.Nullable;
import xiroc.dungeoncrawl.DungeonCrawl;
import xiroc.dungeoncrawl.data.pool.item.ItemPoolKeys;
import xiroc.dungeoncrawl.datapack.registry.DatapackRegistries;
import xiroc.dungeoncrawl.util.random.IRandom;

import java.util.concurrent.CompletableFuture;

public class ItemPoolTags extends TagsProvider<IRandom<Item>> {
    public ItemPoolTags(PackOutput pOutput, CompletableFuture<HolderLookup.Provider> pLookupProvider, @Nullable ExistingFileHelper existingFileHelper) {
        super(pOutput, DatapackRegistries.ITEM_POOLS, pLookupProvider, DungeonCrawl.MOD_ID, existingFileHelper);
    }

    private static TagKey<IRandom<Item>> key(ResourceLocation identifier) {
        return TagKey.create(DatapackRegistries.ITEM_POOLS, identifier);
    }

    public static final TagKey<IRandom<Item>> MAIN_HAND_LEVEL_0 = key(ItemPoolKeys.MAIN_HAND_LEVEL_0.location());
    public static final TagKey<IRandom<Item>> MAIN_HAND_LEVEL_1 = key(ItemPoolKeys.MAIN_HAND_LEVEL_1.location());

    @Override
    protected void addTags(HolderLookup.Provider provider) {
        tag(MAIN_HAND_LEVEL_0)
                .add(ItemPoolKeys.MAIN_HAND_LEVEL_0);

        tag(MAIN_HAND_LEVEL_1)
                .add(ItemPoolKeys.MAIN_HAND_LEVEL_1);
    }
}
