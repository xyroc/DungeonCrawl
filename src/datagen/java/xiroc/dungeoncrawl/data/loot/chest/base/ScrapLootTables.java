package xiroc.dungeoncrawl.data.loot.chest.base;

import net.minecraft.core.HolderLookup;
import net.minecraft.data.loot.LootTableSubProvider;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.storage.loot.LootPool;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.entries.LootItem;
import net.minecraft.world.level.storage.loot.functions.SetItemCountFunction;
import net.minecraft.world.level.storage.loot.providers.number.ConstantValue;
import net.minecraft.world.level.storage.loot.providers.number.UniformGenerator;
import xiroc.dungeoncrawl.data.SharedKeys;

import java.util.function.BiConsumer;

public record ScrapLootTables(HolderLookup.Provider registries) implements LootTableSubProvider {
    @Override
    public void generate(BiConsumer<ResourceKey<LootTable>, LootTable.Builder> collector) {
        collector.accept(SharedKeys.Loot.SCRAP_LEVEL_0, new LootTable.Builder()
                .withPool(LootPool.lootPool()
                        .name("scrap")
                        .setRolls(ConstantValue.exactly(1))
                        .add(LootItem.lootTableItem(Items.IRON_NUGGET)
                                .apply(SetItemCountFunction.setCount(UniformGenerator.between(1, 8))))
                        .add(LootItem.lootTableItem(Items.BRICK)
                                .apply(SetItemCountFunction.setCount(UniformGenerator.between(1, 64))))
                        .add(LootItem.lootTableItem(Items.ARROW)
                                .apply(SetItemCountFunction.setCount(UniformGenerator.between(1, 16))))
                        .add(LootItem.lootTableItem(Items.AMETHYST_SHARD)
                                .apply(SetItemCountFunction.setCount(UniformGenerator.between(1, 8))))
                        .add(LootItem.lootTableItem(Items.CLOCK))
                        .add(LootItem.lootTableItem(Items.COMPASS))
                        .add(LootItem.lootTableItem(Items.SPYGLASS))
                        .add(LootItem.lootTableItem(Items.EGG)
                                .apply(SetItemCountFunction.setCount(UniformGenerator.between(1, 8))))
                        .add(LootItem.lootTableItem(Items.COAL)
                                .apply(SetItemCountFunction.setCount(UniformGenerator.between(1, 8))))
                        .add(LootItem.lootTableItem(Items.CHARCOAL)
                                .apply(SetItemCountFunction.setCount(UniformGenerator.between(1, 8))))
                        .add(LootItem.lootTableItem(Items.CAULDRON))
                        .add(LootItem.lootTableItem(Items.BOWL))
                        .add(LootItem.lootTableItem(Items.BONE)
                                .apply(SetItemCountFunction.setCount(UniformGenerator.between(1, 8)))
                                .setWeight(5))
                        .add(LootItem.lootTableItem(Items.ROTTEN_FLESH)
                                .apply(SetItemCountFunction.setCount(UniformGenerator.between(1, 8)))
                                .setWeight(3))
                        .add(LootItem.lootTableItem(Items.SPIDER_EYE)
                                .setWeight(2)
                                .apply(SetItemCountFunction.setCount(UniformGenerator.between(1, 8))))
                        .add(LootItem.lootTableItem(Items.CLAY_BALL)
                                .apply(SetItemCountFunction.setCount(UniformGenerator.between(1, 8))))
                        .add(LootItem.lootTableItem(Items.PAPER)
                                .apply(SetItemCountFunction.setCount(UniformGenerator.between(1, 8))))
                        .add(LootItem.lootTableItem(Items.BOOK)
                                .apply(SetItemCountFunction.setCount(UniformGenerator.between(1, 8))))
                        .add(LootItem.lootTableItem(Items.WHITE_CANDLE)
                                .apply(SetItemCountFunction.setCount(UniformGenerator.between(1, 8))))
                        .add(LootItem.lootTableItem(Items.GLASS_BOTTLE)
                                .apply(SetItemCountFunction.setCount(UniformGenerator.between(1, 8))))
                        .add(LootItem.lootTableItem(Items.MINECART))
                        .add(LootItem.lootTableItem(Items.FEATHER)
                                .apply(SetItemCountFunction.setCount(UniformGenerator.between(1, 8))))
                        .add(LootItem.lootTableItem(Items.STRING)
                                .apply(SetItemCountFunction.setCount(UniformGenerator.between(1, 8))))
                        .add(LootItem.lootTableItem(Items.FLINT))
                        .add(LootItem.lootTableItem(Items.COPPER_INGOT)
                                .apply(SetItemCountFunction.setCount(UniformGenerator.between(1, 32))))
                        .add(LootItem.lootTableItem(Items.FISHING_ROD))
                        .add(LootItem.lootTableItem(Items.FERMENTED_SPIDER_EYE)
                                .apply(SetItemCountFunction.setCount(UniformGenerator.between(1, 4))))
                        .add(LootItem.lootTableItem(Items.SADDLE)))
        );
    }
}
