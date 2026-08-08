package xiroc.dungeoncrawl.data.loot.chest.contents;

import net.minecraft.core.HolderLookup;
import net.minecraft.data.loot.LootTableSubProvider;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.storage.loot.LootPool;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.entries.LootItem;
import net.minecraft.world.level.storage.loot.functions.EnchantWithLevelsFunction;
import net.minecraft.world.level.storage.loot.functions.SetItemCountFunction;
import net.minecraft.world.level.storage.loot.providers.number.ConstantValue;
import net.minecraft.world.level.storage.loot.providers.number.UniformGenerator;
import xiroc.dungeoncrawl.data.ScalingInteger;
import xiroc.dungeoncrawl.data.loot.chest.ChestLootTableKeys;

import java.util.function.BiConsumer;

public record ValuablesLootTables(HolderLookup.Provider registries) implements LootTableSubProvider {
    @Override
    public void generate(BiConsumer<ResourceKey<LootTable>, LootTable.Builder> collector) {
        final ScalingInteger bookMinLevel = new ScalingInteger(7, 0, 1.25f);
        final ScalingInteger bookMaxLevel = new ScalingInteger(10, 0, 1.25f);

        collector.accept(ChestLootTableKeys.VALUABLES_LEVEL_0, LootTable.lootTable()
                .withPool(LootPool.lootPool()
                        .name("Valuables: Level 0")
                        .setRolls(ConstantValue.exactly(1))
                        .add(LootItem.lootTableItem(Items.IRON_INGOT)
                                .setWeight(3)
                                .apply(SetItemCountFunction.setCount(UniformGenerator.between(3, 5))))
                        .add(LootItem.lootTableItem(Items.GOLD_INGOT)
                                .apply(SetItemCountFunction.setCount(UniformGenerator.between(1, 5))))
                        .add(LootItem.lootTableItem(Items.GOLD_NUGGET)
                                .apply(SetItemCountFunction.setCount(UniformGenerator.between(1, 36))))
                        .add(LootItem.lootTableItem(Items.BOOK)
                                .setQuality(1)
                                .apply(EnchantWithLevelsFunction.enchantWithLevels(registries, UniformGenerator.between(bookMinLevel.atStep(0), bookMaxLevel.atStep(0))))))
        );

        collector.accept(ChestLootTableKeys.VALUABLES_LEVEL_1, LootTable.lootTable()
                .withPool(LootPool.lootPool()
                        .name("Valuables: Level 1")
                        .setRolls(ConstantValue.exactly(1))
                        .add(LootItem.lootTableItem(Items.IRON_INGOT)
                                .setWeight(3)
                                .apply(SetItemCountFunction.setCount(UniformGenerator.between(3, 8))))
                        .add(LootItem.lootTableItem(Items.GOLD_INGOT)
                                .apply(SetItemCountFunction.setCount(UniformGenerator.between(1, 7))))
                        .add(LootItem.lootTableItem(Items.GOLD_NUGGET)
                                .apply(SetItemCountFunction.setCount(UniformGenerator.between(1, 45))))
                        .add(LootItem.lootTableItem(Items.GOLDEN_APPLE))
                        .add(LootItem.lootTableItem(Items.BOOK)
                                .setQuality(1)
                                .apply(EnchantWithLevelsFunction.enchantWithLevels(registries, UniformGenerator.between(bookMinLevel.atStep(1), bookMaxLevel.atStep(1))))))
        );

        collector.accept(ChestLootTableKeys.VALUABLES_LEVEL_2, LootTable.lootTable()
                .withPool(LootPool.lootPool()
                        .name("Valuables: Level 2")
                        .setRolls(ConstantValue.exactly(1))
                        .add(LootItem.lootTableItem(Items.IRON_INGOT)
                                .setWeight(3)
                                .apply(SetItemCountFunction.setCount(UniformGenerator.between(3, 12))))
                        .add(LootItem.lootTableItem(Items.GOLD_INGOT)
                                .apply(SetItemCountFunction.setCount(UniformGenerator.between(1, 12))))
                        .add(LootItem.lootTableItem(Items.GOLD_NUGGET)
                                .apply(SetItemCountFunction.setCount(UniformGenerator.between(1, 63))))
                        .add(LootItem.lootTableItem(Items.GOLDEN_APPLE)
                                .apply(SetItemCountFunction.setCount(UniformGenerator.between(1, 2))))
                        .add(LootItem.lootTableItem(Items.BOOK)
                                .setQuality(1)
                                .apply(EnchantWithLevelsFunction.enchantWithLevels(registries, UniformGenerator.between(bookMinLevel.atStep(2), bookMaxLevel.atStep(2))))))
        );

        collector.accept(ChestLootTableKeys.VALUABLES_LEVEL_3, LootTable.lootTable()
                .withPool(LootPool.lootPool()
                        .name("Valuables: Level 3")
                        .setRolls(ConstantValue.exactly(1))
                        .add(LootItem.lootTableItem(Items.IRON_INGOT)
                                .setWeight(2)
                                .apply(SetItemCountFunction.setCount(UniformGenerator.between(3, 12))))
                        .add(LootItem.lootTableItem(Items.GOLD_INGOT)
                                .setWeight(2)
                                .apply(SetItemCountFunction.setCount(UniformGenerator.between(6, 12))))
                        .add(LootItem.lootTableItem(Items.GOLD_NUGGET)
                                .apply(SetItemCountFunction.setCount(UniformGenerator.between(1, 63))))
                        .add(LootItem.lootTableItem(Items.GOLDEN_APPLE)
                                .apply(SetItemCountFunction.setCount(UniformGenerator.between(1, 2))))
                        .add(LootItem.lootTableItem(Items.EXPERIENCE_BOTTLE)
                                .apply(SetItemCountFunction.setCount(UniformGenerator.between(1, 6))))
                        .add(LootItem.lootTableItem(Items.DIAMOND)
                                .setQuality(1)
                                .apply(SetItemCountFunction.setCount(UniformGenerator.between(1, 4))))
                        .add(LootItem.lootTableItem(Items.BOOK)
                                .setWeight(2)
                                .setQuality(1)
                                .apply(EnchantWithLevelsFunction.enchantWithLevels(registries, UniformGenerator.between(bookMinLevel.atStep(3), bookMaxLevel.atStep(3))))))
        );

        collector.accept(ChestLootTableKeys.VALUABLES_LEVEL_4, LootTable.lootTable()
                .withPool(LootPool.lootPool()
                        .name("Valuables: Level 4")
                        .setRolls(ConstantValue.exactly(1))
                        .add(LootItem.lootTableItem(Items.IRON_INGOT)
                                .apply(SetItemCountFunction.setCount(UniformGenerator.between(3, 12))))
                        .add(LootItem.lootTableItem(Items.GOLD_INGOT)
                                .setWeight(2)
                                .apply(SetItemCountFunction.setCount(UniformGenerator.between(6, 12))))
                        .add(LootItem.lootTableItem(Items.GOLD_NUGGET)
                                .apply(SetItemCountFunction.setCount(UniformGenerator.between(36, 63))))
                        .add(LootItem.lootTableItem(Items.GOLDEN_APPLE)
                                .apply(SetItemCountFunction.setCount(UniformGenerator.between(1, 2))))
                        .add(LootItem.lootTableItem(Items.EXPERIENCE_BOTTLE)
                                .apply(SetItemCountFunction.setCount(UniformGenerator.between(1, 6))))
                        .add(LootItem.lootTableItem(Items.DIAMOND)
                                .setWeight(2)
                                .setQuality(1)
                                .apply(SetItemCountFunction.setCount(UniformGenerator.between(1, 4))))
                        .add(LootItem.lootTableItem(Items.BOOK)
                                .setWeight(2)
                                .setQuality(1)
                                .apply(EnchantWithLevelsFunction.enchantWithLevels(registries, UniformGenerator.between(bookMinLevel.atStep(4), bookMaxLevel.atStep(4)))))));
    }
}
