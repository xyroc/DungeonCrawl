package xiroc.dungeoncrawl.data.tags;

import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.data.tags.TagsProvider;
import net.minecraft.resources.ResourceKey;
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

    private static TagKey<IRandom<Item>> key(ResourceKey<?> key) {
        return key(key.location());
    }

    private static TagKey<IRandom<Item>> key(ResourceLocation identifier) {
        return TagKey.create(DatapackRegistries.ITEM_POOLS, identifier);
    }

    public static final TagKey<IRandom<Item>> MELEE_WEAPON_LEVEL_0 = key(ItemPoolKeys.MELEE_WEAPON_LEVEL_0);
    public static final TagKey<IRandom<Item>> MELEE_WEAPON_LEVEL_1 = key(ItemPoolKeys.MELEE_WEAPON_LEVEL_1);
    public static final TagKey<IRandom<Item>> MELEE_WEAPON_LEVEL_2 = key(ItemPoolKeys.MELEE_WEAPON_LEVEL_2);
    public static final TagKey<IRandom<Item>> MELEE_WEAPON_LEVEL_3 = key(ItemPoolKeys.MELEE_WEAPON_LEVEL_3);
    public static final TagKey<IRandom<Item>> MELEE_WEAPON_LEVEL_4 = key(ItemPoolKeys.MELEE_WEAPON_LEVEL_4);

    public static final TagKey<IRandom<Item>> BOW_LEVEL_0 = key(ItemPoolKeys.BOW_LEVEL_0);
    public static final TagKey<IRandom<Item>> BOW_LEVEL_1 = key(ItemPoolKeys.BOW_LEVEL_1);
    public static final TagKey<IRandom<Item>> BOW_LEVEL_2 = key(ItemPoolKeys.BOW_LEVEL_2);
    public static final TagKey<IRandom<Item>> BOW_LEVEL_3 = key(ItemPoolKeys.BOW_LEVEL_3);
    public static final TagKey<IRandom<Item>> BOW_LEVEL_4 = key(ItemPoolKeys.BOW_LEVEL_4);

    public static final TagKey<IRandom<Item>> SHIELD_LEVEL_0 = key(ItemPoolKeys.SHIELD_LEVEL_0);
    public static final TagKey<IRandom<Item>> SHIELD_LEVEL_1 = key(ItemPoolKeys.SHIELD_LEVEL_1);
    public static final TagKey<IRandom<Item>> SHIELD_LEVEL_2 = key(ItemPoolKeys.SHIELD_LEVEL_2);
    public static final TagKey<IRandom<Item>> SHIELD_LEVEL_3 = key(ItemPoolKeys.SHIELD_LEVEL_3);
    public static final TagKey<IRandom<Item>> SHIELD_LEVEL_4 = key(ItemPoolKeys.SHIELD_LEVEL_4);

    public static final TagKey<IRandom<Item>> HELMET_LEVEL_0 = key(ItemPoolKeys.HELMET_LEVEL_0);
    public static final TagKey<IRandom<Item>> HELMET_LEVEL_1 = key(ItemPoolKeys.HELMET_LEVEL_1);
    public static final TagKey<IRandom<Item>> HELMET_LEVEL_2 = key(ItemPoolKeys.HELMET_LEVEL_2);
    public static final TagKey<IRandom<Item>> HELMET_LEVEL_3 = key(ItemPoolKeys.HELMET_LEVEL_3);
    public static final TagKey<IRandom<Item>> HELMET_LEVEL_4 = key(ItemPoolKeys.HELMET_LEVEL_4);

    public static final TagKey<IRandom<Item>> CHESTPLATE_LEVEL_0 = key(ItemPoolKeys.CHESTPLATE_LEVEL_0);
    public static final TagKey<IRandom<Item>> CHESTPLATE_LEVEL_1 = key(ItemPoolKeys.CHESTPLATE_LEVEL_1);
    public static final TagKey<IRandom<Item>> CHESTPLATE_LEVEL_2 = key(ItemPoolKeys.CHESTPLATE_LEVEL_2);
    public static final TagKey<IRandom<Item>> CHESTPLATE_LEVEL_3 = key(ItemPoolKeys.CHESTPLATE_LEVEL_3);
    public static final TagKey<IRandom<Item>> CHESTPLATE_LEVEL_4 = key(ItemPoolKeys.CHESTPLATE_LEVEL_4);

    public static final TagKey<IRandom<Item>> LEGGINGS_LEVEL_0 = key(ItemPoolKeys.LEGGINGS_LEVEL_0);
    public static final TagKey<IRandom<Item>> LEGGINGS_LEVEL_1 = key(ItemPoolKeys.LEGGINGS_LEVEL_1);
    public static final TagKey<IRandom<Item>> LEGGINGS_LEVEL_2 = key(ItemPoolKeys.LEGGINGS_LEVEL_2);
    public static final TagKey<IRandom<Item>> LEGGINGS_LEVEL_3 = key(ItemPoolKeys.LEGGINGS_LEVEL_3);
    public static final TagKey<IRandom<Item>> LEGGINGS_LEVEL_4 = key(ItemPoolKeys.LEGGINGS_LEVEL_4);

    public static final TagKey<IRandom<Item>> BOOTS_LEVEL_0 = key(ItemPoolKeys.BOOTS_LEVEL_0);
    public static final TagKey<IRandom<Item>> BOOTS_LEVEL_1 = key(ItemPoolKeys.BOOTS_LEVEL_1);
    public static final TagKey<IRandom<Item>> BOOTS_LEVEL_2 = key(ItemPoolKeys.BOOTS_LEVEL_2);
    public static final TagKey<IRandom<Item>> BOOTS_LEVEL_3 = key(ItemPoolKeys.BOOTS_LEVEL_3);
    public static final TagKey<IRandom<Item>> BOOTS_LEVEL_4 = key(ItemPoolKeys.BOOTS_LEVEL_4);

    @Override
    protected void addTags(HolderLookup.Provider provider) {
        tag(MELEE_WEAPON_LEVEL_0)
                .add(ItemPoolKeys.MELEE_WEAPON_LEVEL_0);
        tag(MELEE_WEAPON_LEVEL_1)
                .add(ItemPoolKeys.MELEE_WEAPON_LEVEL_1);
        tag(MELEE_WEAPON_LEVEL_2)
                .add(ItemPoolKeys.MELEE_WEAPON_LEVEL_2);
        tag(MELEE_WEAPON_LEVEL_3)
                .add(ItemPoolKeys.MELEE_WEAPON_LEVEL_3);
        tag(MELEE_WEAPON_LEVEL_4)
                .add(ItemPoolKeys.MELEE_WEAPON_LEVEL_4);

        tag(BOW_LEVEL_0)
                .add(ItemPoolKeys.BOW_LEVEL_0);
        tag(BOW_LEVEL_1)
                .add(ItemPoolKeys.BOW_LEVEL_1);
        tag(BOW_LEVEL_2)
                .add(ItemPoolKeys.BOW_LEVEL_2);
        tag(BOW_LEVEL_3)
                .add(ItemPoolKeys.BOW_LEVEL_3);
        tag(BOW_LEVEL_4)
                .add(ItemPoolKeys.BOW_LEVEL_4);

        tag(SHIELD_LEVEL_0)
                .add(ItemPoolKeys.SHIELD_LEVEL_0);
        tag(SHIELD_LEVEL_1)
                .add(ItemPoolKeys.SHIELD_LEVEL_1);
        tag(SHIELD_LEVEL_2)
                .add(ItemPoolKeys.SHIELD_LEVEL_2);
        tag(SHIELD_LEVEL_3)
                .add(ItemPoolKeys.SHIELD_LEVEL_3);
        tag(SHIELD_LEVEL_4)
                .add(ItemPoolKeys.SHIELD_LEVEL_4);

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

        tag(CHESTPLATE_LEVEL_0)
                .add(ItemPoolKeys.CHESTPLATE_LEVEL_0);
        tag(CHESTPLATE_LEVEL_1)
                .add(ItemPoolKeys.CHESTPLATE_LEVEL_1);
        tag(CHESTPLATE_LEVEL_2)
                .add(ItemPoolKeys.CHESTPLATE_LEVEL_2);
        tag(CHESTPLATE_LEVEL_3)
                .add(ItemPoolKeys.CHESTPLATE_LEVEL_3);
        tag(CHESTPLATE_LEVEL_4)
                .add(ItemPoolKeys.CHESTPLATE_LEVEL_4);

        tag(LEGGINGS_LEVEL_0)
                .add(ItemPoolKeys.LEGGINGS_LEVEL_0);
        tag(LEGGINGS_LEVEL_1)
                .add(ItemPoolKeys.LEGGINGS_LEVEL_1);
        tag(LEGGINGS_LEVEL_2)
                .add(ItemPoolKeys.LEGGINGS_LEVEL_2);
        tag(LEGGINGS_LEVEL_3)
                .add(ItemPoolKeys.LEGGINGS_LEVEL_3);
        tag(LEGGINGS_LEVEL_4)
                .add(ItemPoolKeys.LEGGINGS_LEVEL_4);

        tag(BOOTS_LEVEL_0)
                .add(ItemPoolKeys.BOOTS_LEVEL_0);
        tag(BOOTS_LEVEL_1)
                .add(ItemPoolKeys.BOOTS_LEVEL_1);
        tag(BOOTS_LEVEL_2)
                .add(ItemPoolKeys.BOOTS_LEVEL_2);
        tag(BOOTS_LEVEL_3)
                .add(ItemPoolKeys.BOOTS_LEVEL_3);
        tag(BOOTS_LEVEL_4)
                .add(ItemPoolKeys.BOOTS_LEVEL_4);
    }
}
