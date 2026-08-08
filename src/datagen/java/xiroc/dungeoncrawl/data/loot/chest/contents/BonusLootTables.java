package xiroc.dungeoncrawl.data.loot.chest.contents;

import net.minecraft.core.HolderLookup;
import net.minecraft.data.loot.LootTableSubProvider;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.alchemy.Potions;
import net.minecraft.world.level.storage.loot.LootPool;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.entries.LootItem;
import net.minecraft.world.level.storage.loot.functions.SetItemCountFunction;
import net.minecraft.world.level.storage.loot.functions.SetPotionFunction;
import net.minecraft.world.level.storage.loot.providers.number.ConstantValue;
import net.minecraft.world.level.storage.loot.providers.number.UniformGenerator;
import xiroc.dungeoncrawl.data.loot.chest.ChestLootTableKeys;

import java.util.function.BiConsumer;

public record BonusLootTables(HolderLookup.Provider registries) implements LootTableSubProvider {
    @Override
    public void generate(BiConsumer<ResourceKey<LootTable>, LootTable.Builder> collector) {
        collector.accept(ChestLootTableKeys.LUCK_BONUS_LEVEL_0, LootTable.lootTable()
                .withPool(LootPool.lootPool()
                        .name("Level 0 Chest: Lucky Bonus")
                        .setRolls(ConstantValue.exactly(1))
                        .add(LootItem.lootTableItem(Items.GOLD_NUGGET)
                                .apply(SetItemCountFunction.setCount(UniformGenerator.between(3, 5))))
                        .add(LootItem.lootTableItem(Items.RAW_GOLD)
                                .apply(SetItemCountFunction.setCount(UniformGenerator.between(2, 4))))
                        .add(LootItem.lootTableItem(Items.GOLD_INGOT)
                                .apply(SetItemCountFunction.setCount(UniformGenerator.between(1, 3)))))
        );

        collector.accept(ChestLootTableKeys.LUCK_BONUS_LEVEL_1, LootTable.lootTable()
                .withPool(LootPool.lootPool()
                        .name("Level 1 Chest: Lucky Bonus")
                        .setRolls(ConstantValue.exactly(1))
                        .add(LootItem.lootTableItem(Items.GOLD_NUGGET)
                                .apply(SetItemCountFunction.setCount(UniformGenerator.between(3, 5))))
                        .add(LootItem.lootTableItem(Items.RAW_GOLD)
                                .apply(SetItemCountFunction.setCount(UniformGenerator.between(2, 6))))
                        .add(LootItem.lootTableItem(Items.GOLD_INGOT)
                                .apply(SetItemCountFunction.setCount(UniformGenerator.between(1, 6))))
                        .add(LootItem.lootTableItem(Items.EMERALD)
                                .apply(SetItemCountFunction.setCount(UniformGenerator.between(1, 3)))
                                .setWeight(2)))
        );

        collector.accept(ChestLootTableKeys.LUCK_BONUS_LEVEL_2, LootTable.lootTable()
                .withPool(LootPool.lootPool()
                        .name("Level 2 Chest: Lucky Bonus")
                        .setRolls(ConstantValue.exactly(1))
                        .add(LootItem.lootTableItem(Items.RAW_GOLD)
                                .apply(SetItemCountFunction.setCount(UniformGenerator.between(2, 7))))
                        .add(LootItem.lootTableItem(Items.GOLD_INGOT)
                                .apply(SetItemCountFunction.setCount(UniformGenerator.between(1, 7))))
                        .add(LootItem.lootTableItem(Items.EMERALD)
                                .apply(SetItemCountFunction.setCount(UniformGenerator.between(1, 6)))
                                .setWeight(2))
                        .add(LootItem.lootTableItem(Items.DIAMOND)))
        );

        collector.accept(ChestLootTableKeys.LUCK_BONUS_LEVEL_3, LootTable.lootTable()
                .withPool(LootPool.lootPool()
                        .name("Level 3 Chest: Lucky Bonus")
                        .setRolls(ConstantValue.exactly(1))
                        .add(LootItem.lootTableItem(Items.POTION)
                                .apply(SetPotionFunction.setPotion(Potions.LUCK)))
                        .add(LootItem.lootTableItem(Items.RAW_GOLD)
                                .apply(SetItemCountFunction.setCount(UniformGenerator.between(2, 7))))
                        .add(LootItem.lootTableItem(Items.GOLD_INGOT)
                                .apply(SetItemCountFunction.setCount(UniformGenerator.between(1, 7)))
                                .setWeight(2))
                        .add(LootItem.lootTableItem(Items.EMERALD)
                                .apply(SetItemCountFunction.setCount(UniformGenerator.between(1, 6)))
                                .setWeight(2))
                        .add(LootItem.lootTableItem(Items.DIAMOND)
                                .apply(SetItemCountFunction.setCount(UniformGenerator.between(1, 2)))
                                .setQuality(1)))
        );

        collector.accept(ChestLootTableKeys.LUCK_BONUS_LEVEL_4, LootTable.lootTable()
                .withPool(LootPool.lootPool()
                        .name("Level 4 Chest: Lucky Bonus")
                        .setRolls(ConstantValue.exactly(1))
                        .add(LootItem.lootTableItem(Items.POTION)
                                .apply(SetPotionFunction.setPotion(Potions.LUCK)))
                        .add(LootItem.lootTableItem(Items.NETHERITE_SCRAP)
                                .apply(SetItemCountFunction.setCount(UniformGenerator.between(1, 3)))
                                .setQuality(1))
                        .add(LootItem.lootTableItem(Items.GOLD_INGOT)
                                .apply(SetItemCountFunction.setCount(UniformGenerator.between(1, 7)))
                                .setWeight(2))
                        .add(LootItem.lootTableItem(Items.EMERALD)
                                .apply(SetItemCountFunction.setCount(UniformGenerator.between(1, 6)))
                                .setWeight(2))
                        .add(LootItem.lootTableItem(Items.DIAMOND)
                                .apply(SetItemCountFunction.setCount(UniformGenerator.between(1, 3)))
                                .setQuality(1)))
        );
    }
}
