package xiroc.dungeoncrawl.data.loot.chest.speciality;

import net.minecraft.ChatFormatting;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.loot.LootTableSubProvider;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.storage.loot.LootPool;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.entries.LootItem;
import net.minecraft.world.level.storage.loot.functions.ListOperation;
import net.minecraft.world.level.storage.loot.functions.SetEnchantmentsFunction;
import net.minecraft.world.level.storage.loot.functions.SetLoreFunction;
import net.minecraft.world.level.storage.loot.functions.SetNameFunction;
import net.minecraft.world.level.storage.loot.predicates.LootItemRandomChanceCondition;
import net.minecraft.world.level.storage.loot.providers.number.ConstantValue;
import net.minecraft.world.level.storage.loot.providers.number.UniformGenerator;
import xiroc.dungeoncrawl.data.SharedKeys;

import java.util.function.BiConsumer;

public record SpecialityLootTables(HolderLookup.Provider registries) implements LootTableSubProvider {
    @Override
    public void generate(BiConsumer<ResourceKey<LootTable>, LootTable.Builder> collector) {
        final HolderLookup<Enchantment> enchantments = registries.lookupOrThrow(Registries.ENCHANTMENT);

        collector.accept(SharedKeys.Loot.Specialities.TEMPERED_BLADE, LootTable.lootTable()
                .withPool(LootPool.lootPool()
                        .name("Tempered Blade")
                        .setRolls(ConstantValue.exactly(1))
                        .add(LootItem.lootTableItem(Items.IRON_SWORD)
                                .apply(SetNameFunction.setName(Component.literal("Tempered Blade"), SetNameFunction.Target.ITEM_NAME))
                                .apply(SetLoreFunction.setLore()
                                        .setMode(ListOperation.Append.INSTANCE)
                                        .addLine(Component.literal("Highly Durable")
                                                .withStyle(ChatFormatting.DARK_GREEN)))
                                .apply(new SetEnchantmentsFunction.Builder()
                                        .withEnchantment(enchantments.getOrThrow(Enchantments.MENDING), ConstantValue.exactly(1))
                                        .withEnchantment(enchantments.getOrThrow(Enchantments.UNBREAKING), ConstantValue.exactly(3)))
                                .apply(new SetEnchantmentsFunction.Builder()
                                        .when(LootItemRandomChanceCondition.randomChance(0.5f))
                                        .withEnchantment(enchantments.getOrThrow(Enchantments.SHARPNESS), ConstantValue.exactly(1))))));

        collector.accept(SharedKeys.Loot.Specialities.CASE_HARDENED_PICK, LootTable.lootTable()
                .withPool(LootPool.lootPool()
                        .name("Case Hardened Pick")
                        .setRolls(ConstantValue.exactly(1))
                        .add(LootItem.lootTableItem(Items.IRON_PICKAXE)
                                .setWeight(8)
                                .apply(SetNameFunction.setName(Component.literal("Case Hardened Pick"), SetNameFunction.Target.ITEM_NAME))
                                .apply(new SetEnchantmentsFunction.Builder()
                                        .withEnchantment(enchantments.getOrThrow(Enchantments.EFFICIENCY), UniformGenerator.between(1, 2))
                                        .withEnchantment(enchantments.getOrThrow(Enchantments.UNBREAKING), UniformGenerator.between(1, 2)))
                                .apply(new SetEnchantmentsFunction.Builder()
                                        .when(LootItemRandomChanceCondition.randomChance(0.2f))
                                        .withEnchantment(enchantments.getOrThrow(Enchantments.MENDING), ConstantValue.exactly(1))))
                        .add(LootItem.lootTableItem(Items.IRON_PICKAXE)
                                .setWeight(1)
                                .apply(SetNameFunction.setName(Component.literal("Case Hardened Pick of Precision"), SetNameFunction.Target.ITEM_NAME))
                                .apply(new SetEnchantmentsFunction.Builder()
                                        .withEnchantment(enchantments.getOrThrow(Enchantments.EFFICIENCY), UniformGenerator.between(1, 2))
                                        .withEnchantment(enchantments.getOrThrow(Enchantments.UNBREAKING), UniformGenerator.between(1, 2))
                                        .withEnchantment(enchantments.getOrThrow(Enchantments.SILK_TOUCH), ConstantValue.exactly(1))))
                        .add(LootItem.lootTableItem(Items.IRON_PICKAXE)
                                .setWeight(1)
                                .apply(SetNameFunction.setName(Component.literal("Case Hardened Pick of Prospecting"), SetNameFunction.Target.ITEM_NAME))
                                .apply(new SetEnchantmentsFunction.Builder()
                                        .withEnchantment(enchantments.getOrThrow(Enchantments.EFFICIENCY), UniformGenerator.between(1, 2))
                                        .withEnchantment(enchantments.getOrThrow(Enchantments.UNBREAKING), UniformGenerator.between(1, 2))
                                        .withEnchantment(enchantments.getOrThrow(Enchantments.FORTUNE), UniformGenerator.between(1, 3))))));

        collector.accept(SharedKeys.Loot.Specialities.CRYSTAL_PICK, LootTable.lootTable()
                .withPool(LootPool.lootPool()
                        .name("Crystal Pick")
                        .setRolls(ConstantValue.exactly(1))
                        .add(LootItem.lootTableItem(Items.DIAMOND_PICKAXE)
                                .setWeight(8)
                                .apply(SetNameFunction.setName(Component.literal("Crystal Pick"), SetNameFunction.Target.ITEM_NAME))
                                .apply(new SetEnchantmentsFunction.Builder()
                                        .withEnchantment(enchantments.getOrThrow(Enchantments.EFFICIENCY), UniformGenerator.between(3, 5))
                                        .withEnchantment(enchantments.getOrThrow(Enchantments.UNBREAKING), ConstantValue.exactly(3))))
                        .add(LootItem.lootTableItem(Items.DIAMOND_PICKAXE)
                                .setWeight(1)
                                .apply(SetNameFunction.setName(Component.literal("Crystal Pick of Precision"), SetNameFunction.Target.ITEM_NAME))
                                .apply(new SetEnchantmentsFunction.Builder()
                                        .withEnchantment(enchantments.getOrThrow(Enchantments.EFFICIENCY), UniformGenerator.between(3, 5))
                                        .withEnchantment(enchantments.getOrThrow(Enchantments.UNBREAKING), ConstantValue.exactly(3))
                                        .withEnchantment(enchantments.getOrThrow(Enchantments.SILK_TOUCH), ConstantValue.exactly(1))))
                        .add(LootItem.lootTableItem(Items.DIAMOND_PICKAXE)
                                .setWeight(1)
                                .apply(SetNameFunction.setName(Component.literal("Crystal Pick of Prospecting"), SetNameFunction.Target.ITEM_NAME))
                                .apply(new SetEnchantmentsFunction.Builder()
                                        .withEnchantment(enchantments.getOrThrow(Enchantments.EFFICIENCY), UniformGenerator.between(3, 5))
                                        .withEnchantment(enchantments.getOrThrow(Enchantments.UNBREAKING), ConstantValue.exactly(3))
                                        .withEnchantment(enchantments.getOrThrow(Enchantments.FORTUNE), UniformGenerator.between(2, 3))))));

        collector.accept(SharedKeys.Loot.Specialities.WOODLAND_HATCHET, LootTable.lootTable()
                .withPool(LootPool.lootPool()
                        .name("Woodland Hatchet")
                        .setRolls(ConstantValue.exactly(1))
                        .add(LootItem.lootTableItem(Items.IRON_AXE)
                                .apply(SetNameFunction.setName(Component.literal("Woodland Hatchet"), SetNameFunction.Target.ITEM_NAME))
                                .apply(new SetEnchantmentsFunction.Builder()
                                        .withEnchantment(enchantments.getOrThrow(Enchantments.UNBREAKING), UniformGenerator.between(1, 2))
                                        .withEnchantment(enchantments.getOrThrow(Enchantments.EFFICIENCY), UniformGenerator.between(1, 2))))));

        collector.accept(SharedKeys.Loot.Specialities.CRYSTAL_AXE, LootTable.lootTable()
                .withPool(LootPool.lootPool()
                        .name("Crystal Head Axe")
                        .setRolls(ConstantValue.exactly(1))
                        .add(LootItem.lootTableItem(Items.DIAMOND_AXE)
                                .apply(SetNameFunction.setName(Component.literal("Crystal Head Axe"), SetNameFunction.Target.ITEM_NAME))
                                .apply(new SetEnchantmentsFunction.Builder()
                                        .withEnchantment(enchantments.getOrThrow(Enchantments.UNBREAKING), ConstantValue.exactly(3))
                                        .withEnchantment(enchantments.getOrThrow(Enchantments.EFFICIENCY), UniformGenerator.between(3, 5))))));

        collector.accept(SharedKeys.Loot.Specialities.GRAVE_SPADE, LootTable.lootTable()
                .withPool(LootPool.lootPool()
                        .name("Grave Spade")
                        .setRolls(ConstantValue.exactly(1))
                        .add(LootItem.lootTableItem(Items.IRON_SHOVEL)
                                .apply(SetNameFunction.setName(Component.literal("Grave Spade"), SetNameFunction.Target.ITEM_NAME))
                                .apply(new SetEnchantmentsFunction.Builder()
                                        .withEnchantment(enchantments.getOrThrow(Enchantments.UNBREAKING), UniformGenerator.between(1, 2))
                                        .withEnchantment(enchantments.getOrThrow(Enchantments.EFFICIENCY), UniformGenerator.between(1, 2))))));

        collector.accept(SharedKeys.Loot.Specialities.SOUL_SPADE, LootTable.lootTable()
                .withPool(LootPool.lootPool()
                        .name("Soul Spade")
                        .setRolls(ConstantValue.exactly(1))
                        .add(LootItem.lootTableItem(Items.DIAMOND_SHOVEL)
                                .apply(SetNameFunction.setName(Component.literal("Soul Spade"), SetNameFunction.Target.ITEM_NAME))
                                .apply(new SetEnchantmentsFunction.Builder()
                                        .withEnchantment(enchantments.getOrThrow(Enchantments.UNBREAKING), ConstantValue.exactly(3))
                                        .withEnchantment(enchantments.getOrThrow(Enchantments.EFFICIENCY), UniformGenerator.between(3, 5))))));
    }
}
