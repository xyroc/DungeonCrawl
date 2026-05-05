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

    public static final TagKey<IRandom<Item>> MELEE_WEAPON_LEVEL_0 = key(ItemPoolKeys.MELEE_WEAPON_LEVEL_0.location());
    public static final TagKey<IRandom<Item>> MELEE_WEAPON_LEVEL_1 = key(ItemPoolKeys.MELEE_WEAPON_LEVEL_1.location());

    public static final TagKey<IRandom<Item>> HELMET_LEVEL_0 = key(ItemPoolKeys.HELMET_LEVEL_0.location());
    public static final TagKey<IRandom<Item>> HELMET_LEVEL_1 = key(ItemPoolKeys.HELMET_LEVEL_1.location());
    public static final TagKey<IRandom<Item>> HELMET_LEVEL_2 = key(ItemPoolKeys.HELMET_LEVEL_2.location());
    public static final TagKey<IRandom<Item>> HELMET_LEVEL_3 = key(ItemPoolKeys.HELMET_LEVEL_3.location());
    public static final TagKey<IRandom<Item>> HELMET_LEVEL_4 = key(ItemPoolKeys.HELMET_LEVEL_4.location());

    @Override
    protected void addTags(HolderLookup.Provider provider) {
        tag(MELEE_WEAPON_LEVEL_0)
                .add(ItemPoolKeys.MELEE_WEAPON_LEVEL_0);
        tag(MELEE_WEAPON_LEVEL_1)
                .add(ItemPoolKeys.MELEE_WEAPON_LEVEL_1);

        tag(HELMET_LEVEL_0)
                .add(ItemPoolKeys.HELMET_LEVEL_0);
        tag(HELMET_LEVEL_1)
                .add(ItemPoolKeys.HELMET_LEVEL_1);
        tag(HELMET_LEVEL_2)
                .add(ItemPoolKeys.HELMET_LEVEL_2);
        tag(HELMET_LEVEL_3)
                .add(ItemPoolKeys.HELMET_LEVEL_3);
        tag(HELMET_LEVEL_4)
                .add(ItemPoolKeys.HELMET_LEVEL_4);
    }
}
