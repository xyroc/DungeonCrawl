package xiroc.dungeoncrawl.dungeon.treasure;

/*
 * DungeonCrawl (C) 2019 - 2020 XYROC (XIROC1337), All Rights Reserved
 */

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.server.ServerWorld;
import xiroc.dungeoncrawl.util.ItemProcessor;

import java.util.Random;

public class TreasureItem {

    public ResourceLocation item;
    public String resourceName;

    public ItemEnchantment[] enchantments;

    public CompoundNBT nbt;

    private ItemProcessor<ServerWorld, Random, Integer, Integer> itemProcessor;

    public TreasureItem(String resourceName) {
        this(resourceName, null, null);
    }

    public TreasureItem(String resourceName, CompoundNBT nbt,
                        ItemEnchantment[] enchantments) {
        this.resourceName = resourceName;
        this.nbt = nbt;
        this.enchantments = enchantments;
    }

    public TreasureItem(String resourceName, CompoundNBT nbt,
                        ItemEnchantment[] enchantments, ItemProcessor<ServerWorld, Random, Integer, Integer> itemProcessor) {
        this(resourceName, nbt, enchantments);
        this.itemProcessor = itemProcessor;
    }

    public TreasureItem withProcessor(ItemProcessor<ServerWorld, Random, Integer, Integer> itemProcessor) {
        return new TreasureItem(this.resourceName, this.nbt, this.enchantments,
                itemProcessor);
    }


    public ItemStack generate(ServerWorld world, Random rand, int theme, int lootLevel) {
        if (itemProcessor != null)
            return itemProcessor.generate(world, rand, theme, lootLevel);
        return ItemStack.EMPTY;
    }


    public static class ItemEnchantment {

        ResourceLocation id;
        int level;

        public ItemEnchantment(ResourceLocation id, int level) {
            this.id = id;
            this.level = level;
        }

    }

}
