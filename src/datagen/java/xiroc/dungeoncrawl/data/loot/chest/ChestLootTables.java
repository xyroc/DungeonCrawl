/*
        Dungeon Crawl, a procedural dungeon generator for Minecraft 1.14 and later.
        Copyright (C) 2020

        This program is free software: you can redistribute it and/or modify
        it under the terms of the GNU General Public License as published by
        the Free Software Foundation, either version 3 of the License, or
        (at your option) any later version.

        This program is distributed in the hope that it will be useful,
        but WITHOUT ANY WARRANTY; without even the implied warranty of
        MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
        GNU General Public License for more details.

        You should have received a copy of the GNU General Public License
        along with this program.  If not, see <https://www.gnu.org/licenses/>.
*/

package xiroc.dungeoncrawl.data.loot.chest;

import net.minecraft.core.HolderLookup;
import net.minecraft.data.loot.LootTableSubProvider;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.storage.loot.BuiltInLootTables;
import net.minecraft.world.level.storage.loot.LootPool;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.entries.LootItem;
import net.minecraft.world.level.storage.loot.entries.NestedLootTable;
import net.minecraft.world.level.storage.loot.functions.SetItemCountFunction;
import net.minecraft.world.level.storage.loot.predicates.LootItemRandomChanceCondition;
import net.minecraft.world.level.storage.loot.providers.number.ConstantValue;
import net.minecraft.world.level.storage.loot.providers.number.UniformGenerator;

import java.util.function.BiConsumer;

public record ChestLootTables(HolderLookup.Provider registries) implements LootTableSubProvider {
    @Override
    public void generate(BiConsumer<ResourceKey<LootTable>, LootTable.Builder> collector) {
        collector.accept(ChestLootTableKeys.LEVEL_0, LootTable.lootTable()
                .withPool(LootPool.lootPool()
                        .name("Level 0 Chest")
                        .setRolls(UniformGenerator.between(3, 5))
                        .setBonusRolls(UniformGenerator.between(1, 2))
                        .add(NestedLootTable.lootTableReference(ChestLootTableKeys.BLOCKS_LEVEL_0))
                        .add(NestedLootTable.lootTableReference(ChestLootTableKeys.EQUIPMENT_LEVEL_0))
                        .add(NestedLootTable.lootTableReference(ChestLootTableKeys.FOOD_LEVEL_0))
                        .add(NestedLootTable.lootTableReference(ChestLootTableKeys.SCRAP_LEVEL_0))
                        .add(NestedLootTable.lootTableReference(ChestLootTableKeys.VALUABLES_LEVEL_0)))
                .withPool(LootPool.lootPool()
                        .name("Level 0 Chest: Speciality")
                        .setRolls(ConstantValue.exactly(1))
                        .setBonusRolls(UniformGenerator.between(0, 1))
                        .when(LootItemRandomChanceCondition.randomChance(0.15f))
                        .add(NestedLootTable.lootTableReference(ChestLootTableKeys.Speciality.LEVEL_0)))
                .withPool(LootPool.lootPool()
                        .name("Level 0 Chest: Bonus")
                        .setRolls(ConstantValue.exactly(1))
                        .when(LootItemRandomChanceCondition.randomChance(0.1f))
                        .add(NestedLootTable.lootTableReference(BuiltInLootTables.ABANDONED_MINESHAFT))
                        .add(NestedLootTable.lootTableReference(BuiltInLootTables.SIMPLE_DUNGEON)))
        );

        collector.accept(ChestLootTableKeys.LEVEL_1, LootTable.lootTable()
                .withPool(LootPool.lootPool()
                        .name("Level 1 Chest")
                        .setRolls(UniformGenerator.between(3, 5))
                        .setBonusRolls(UniformGenerator.between(1, 2))
                        .add(NestedLootTable.lootTableReference(ChestLootTableKeys.BLOCKS_LEVEL_1))
                        .add(NestedLootTable.lootTableReference(ChestLootTableKeys.EQUIPMENT_LEVEL_1))
                        .add(NestedLootTable.lootTableReference(ChestLootTableKeys.FOOD_LEVEL_1))
                        .add(NestedLootTable.lootTableReference(ChestLootTableKeys.SCRAP_LEVEL_1))
                        .add(NestedLootTable.lootTableReference(ChestLootTableKeys.VALUABLES_LEVEL_1)))
                .withPool(LootPool.lootPool()
                        .name("Level 1 Chest: Speciality")
                        .setRolls(ConstantValue.exactly(1))
                        .setBonusRolls(UniformGenerator.between(0, 1))
                        .when(LootItemRandomChanceCondition.randomChance(0.15f))
                        .add(NestedLootTable.lootTableReference(ChestLootTableKeys.Speciality.LEVEL_1)))
                .withPool(LootPool.lootPool()
                        .name("Level 1 Chest: Bonus")
                        .setRolls(ConstantValue.exactly(1))
                        .when(LootItemRandomChanceCondition.randomChance(0.1f))
                        .add(NestedLootTable.lootTableReference(BuiltInLootTables.SIMPLE_DUNGEON)))
        );

        collector.accept(ChestLootTableKeys.LEVEL_2, LootTable.lootTable()
                .withPool(LootPool.lootPool()
                        .name("Level 2 Chest")
                        .setRolls(UniformGenerator.between(3, 5))
                        .setBonusRolls(UniformGenerator.between(1, 2))
                        .add(NestedLootTable.lootTableReference(ChestLootTableKeys.BLOCKS_LEVEL_2))
                        .add(NestedLootTable.lootTableReference(ChestLootTableKeys.EQUIPMENT_LEVEL_2))
                        .add(NestedLootTable.lootTableReference(ChestLootTableKeys.FOOD_LEVEL_2))
                        .add(NestedLootTable.lootTableReference(ChestLootTableKeys.SCRAP_LEVEL_2))
                        .add(NestedLootTable.lootTableReference(ChestLootTableKeys.VALUABLES_LEVEL_2)))
                .withPool(LootPool.lootPool()
                        .name("Level 2 Chest: Speciality")
                        .setRolls(ConstantValue.exactly(1))
                        .setBonusRolls(UniformGenerator.between(0, 1))
                        .when(LootItemRandomChanceCondition.randomChance(0.15f))
                        .add(NestedLootTable.lootTableReference(ChestLootTableKeys.Speciality.LEVEL_2)))
                .withPool(LootPool.lootPool()
                        .name("Level 2 Chest: Bonus")
                        .setRolls(ConstantValue.exactly(1))
                        .when(LootItemRandomChanceCondition.randomChance(0.1f))
                        .add(NestedLootTable.lootTableReference(BuiltInLootTables.JUNGLE_TEMPLE)))
        );

        collector.accept(ChestLootTableKeys.LEVEL_3, LootTable.lootTable()
                .withPool(LootPool.lootPool()
                        .name("Level 3 Chest")
                        .setRolls(UniformGenerator.between(3, 5))
                        .setBonusRolls(UniformGenerator.between(1, 2))
                        .add(NestedLootTable.lootTableReference(ChestLootTableKeys.BLOCKS_LEVEL_3))
                        .add(NestedLootTable.lootTableReference(ChestLootTableKeys.EQUIPMENT_LEVEL_3))
                        .add(NestedLootTable.lootTableReference(ChestLootTableKeys.FOOD_LEVEL_3))
                        .add(NestedLootTable.lootTableReference(ChestLootTableKeys.SCRAP_LEVEL_3))
                        .add(NestedLootTable.lootTableReference(ChestLootTableKeys.VALUABLES_LEVEL_3)))
                .withPool(LootPool.lootPool()
                        .name("Level 3 Chest: Speciality")
                        .setRolls(ConstantValue.exactly(1))
                        .setBonusRolls(UniformGenerator.between(0, 1))
                        .when(LootItemRandomChanceCondition.randomChance(0.15f))
                        .add(NestedLootTable.lootTableReference(ChestLootTableKeys.Speciality.LEVEL_3)))
                .withPool(LootPool.lootPool()
                        .name("Level 3 Chest: Bonus")
                        .setRolls(ConstantValue.exactly(1))
                        .when(LootItemRandomChanceCondition.randomChance(0.1f))
                        .add(NestedLootTable.lootTableReference(BuiltInLootTables.JUNGLE_TEMPLE)
                                .setWeight(2))
                        .add(NestedLootTable.lootTableReference(BuiltInLootTables.STRONGHOLD_CROSSING))
                        .add(NestedLootTable.lootTableReference(BuiltInLootTables.STRONGHOLD_CORRIDOR)))
        );

        collector.accept(ChestLootTableKeys.LEVEL_4, LootTable.lootTable()
                .withPool(LootPool.lootPool()
                        .name("Level 4 Chest")
                        .setRolls(UniformGenerator.between(3, 5))
                        .setBonusRolls(UniformGenerator.between(1, 2))
                        .add(NestedLootTable.lootTableReference(ChestLootTableKeys.BLOCKS_LEVEL_4))
                        .add(NestedLootTable.lootTableReference(ChestLootTableKeys.EQUIPMENT_LEVEL_4))
                        .add(NestedLootTable.lootTableReference(ChestLootTableKeys.FOOD_LEVEL_4))
                        .add(NestedLootTable.lootTableReference(ChestLootTableKeys.SCRAP_LEVEL_4))
                        .add(NestedLootTable.lootTableReference(ChestLootTableKeys.VALUABLES_LEVEL_4)))
                .withPool(LootPool.lootPool()
                        .name("Level 4 Chest: Speciality")
                        .setRolls(ConstantValue.exactly(1))
                        .setBonusRolls(UniformGenerator.between(0, 1))
                        .when(LootItemRandomChanceCondition.randomChance(0.15f))
                        .add(NestedLootTable.lootTableReference(ChestLootTableKeys.Speciality.LEVEL_4)))
                .withPool(LootPool.lootPool()
                        .name("Level 4 Chest: Bonus")
                        .setRolls(ConstantValue.exactly(1))
                        .when(LootItemRandomChanceCondition.randomChance(0.1f))
                        .add(NestedLootTable.lootTableReference(BuiltInLootTables.STRONGHOLD_CROSSING))
                        .add(NestedLootTable.lootTableReference(BuiltInLootTables.STRONGHOLD_CORRIDOR)))
        );

        collector.accept(ChestLootTableKeys.SECRET_ROOM, LootTable.lootTable()
                .withPool(LootPool.lootPool()
                        .name("Secret Room Chest")
                        .setRolls(UniformGenerator.between(8, 8))
                        .setBonusRolls(UniformGenerator.between(2, 4))
                        .add(LootItem.lootTableItem(Items.COBWEB)
                                .setWeight(9)
                                .apply(SetItemCountFunction.setCount(UniformGenerator.between(4, 8))))
                        .add(LootItem.lootTableItem(Items.EMERALD)
                                .setWeight(8)
                                .apply(SetItemCountFunction.setCount(UniformGenerator.between(4, 8))))
                        .add(LootItem.lootTableItem(Items.LAPIS_LAZULI)
                                .setWeight(3)
                                .apply(SetItemCountFunction.setCount(UniformGenerator.between(8, 16))))
                        .add(LootItem.lootTableItem(Items.BOOK)
                                .setWeight(3)
                                .apply(SetItemCountFunction.setCount(UniformGenerator.between(1, 12))))
                        .add(LootItem.lootTableItem(Items.MAP)
                                .setWeight(3))
                        .add(LootItem.lootTableItem(Items.MUSIC_DISC_11))
                        .add(LootItem.lootTableItem(Items.MUSIC_DISC_13))
                        .add(LootItem.lootTableItem(Items.MUSIC_DISC_BLOCKS))
                        .add(LootItem.lootTableItem(Items.MUSIC_DISC_CHIRP))
                        .add(LootItem.lootTableItem(Items.MUSIC_DISC_CAT))
                        .add(LootItem.lootTableItem(Items.MUSIC_DISC_FAR))
                        .add(LootItem.lootTableItem(Items.MUSIC_DISC_MALL))
                        .add(LootItem.lootTableItem(Items.MUSIC_DISC_MELLOHI))
                        .add(LootItem.lootTableItem(Items.MUSIC_DISC_STAL))
                        .add(LootItem.lootTableItem(Items.MUSIC_DISC_STRAD))
                        .add(LootItem.lootTableItem(Items.MUSIC_DISC_WAIT))
                        .add(LootItem.lootTableItem(Items.MUSIC_DISC_WARD)))
        );

        collector.accept(ChestLootTableKeys.SUPPLY, LootTable.lootTable());
        collector.accept(ChestLootTableKeys.TREASURE, LootTable.lootTable());
    }
}