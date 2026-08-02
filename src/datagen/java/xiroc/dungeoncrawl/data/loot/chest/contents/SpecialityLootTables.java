package xiroc.dungeoncrawl.data.loot.chest.contents;

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
import xiroc.dungeoncrawl.data.loot.chest.ChestLootTableKeys;
import xiroc.dungeoncrawl.data.translation.ItemTranslationKeys;

import java.util.function.BiConsumer;

public record SpecialityLootTables(HolderLookup.Provider registries) implements LootTableSubProvider {
    @Override
    public void generate(BiConsumer<ResourceKey<LootTable>, LootTable.Builder> collector) {
        final HolderLookup<Enchantment> enchantments = registries.lookupOrThrow(Registries.ENCHANTMENT);

        collector.accept(ChestLootTableKeys.Speciality.LEVEL_0, LootTable.lootTable());
        collector.accept(ChestLootTableKeys.Speciality.LEVEL_1, LootTable.lootTable());
        collector.accept(ChestLootTableKeys.Speciality.LEVEL_2, LootTable.lootTable());
        collector.accept(ChestLootTableKeys.Speciality.LEVEL_3, LootTable.lootTable());
        collector.accept(ChestLootTableKeys.Speciality.LEVEL_4, LootTable.lootTable());

        collector.accept(ChestLootTableKeys.Speciality.ELDRITCH_BLADE, LootTable.lootTable()
                .withPool(LootPool.lootPool()
                        .name("Speciality: Eldritch Blade")
                        .setRolls(ConstantValue.exactly(1))
                        .add(LootItem.lootTableItem(Items.DIAMOND_SWORD)
                                .apply(SetNameFunction.setName(Component.translatableWithFallback(ItemTranslationKeys.ELDRITCH_BLADE, "Eldritch Blade"), SetNameFunction.Target.ITEM_NAME))
                                .apply(SetLoreFunction.setLore()
                                        .setMode(ListOperation.Append.INSTANCE)
                                        .addLine(Component.translatableWithFallback(ItemTranslationKeys.ELDRITCH_BLADE_LORE, "Rune Etched")
                                                .withStyle(ChatFormatting.DARK_GREEN)))
                                .apply(new SetEnchantmentsFunction.Builder()
                                        .withEnchantment(enchantments.getOrThrow(Enchantments.MENDING), ConstantValue.exactly(1)))
                                .setWeight(8))
                        .add(LootItem.lootTableItem(Items.DIAMOND_SWORD)
                                .apply(SetNameFunction.setName(Component.translatableWithFallback(ItemTranslationKeys.ELDRITCH_BLADE_OF_PLUNDERING, "Eldritch Blade of Plundering"), SetNameFunction.Target.ITEM_NAME))
                                .apply(SetLoreFunction.setLore()
                                        .setMode(ListOperation.Append.INSTANCE)
                                        .addLine(Component.translatableWithFallback(ItemTranslationKeys.ELDRITCH_BLADE_OF_PLUNDERING_LORE, "The loot taker")
                                                .withStyle(ChatFormatting.DARK_GREEN)))
                                .apply(new SetEnchantmentsFunction.Builder()
                                        .withEnchantment(enchantments.getOrThrow(Enchantments.LOOTING), UniformGenerator.between(2, 3)))
                                .setWeight(1)
                                .setQuality(2))
                        .add(LootItem.lootTableItem(Items.DIAMOND_SWORD)
                                .apply(SetNameFunction.setName(Component.translatableWithFallback(ItemTranslationKeys.ELDRITCH_BLADE_OF_THE_INFERNO, "Eldritch Blade of the Inferno"), SetNameFunction.Target.ITEM_NAME))
                                .apply(SetLoreFunction.setLore()
                                        .setMode(ListOperation.Append.INSTANCE)
                                        .addLine(Component.translatableWithFallback(ItemTranslationKeys.ELDRITCH_BLADE_OF_THE_INFERNO_LORE, "From the fiery depths")
                                                .withStyle(ChatFormatting.DARK_GREEN)))
                                .apply(new SetEnchantmentsFunction.Builder()
                                        .withEnchantment(enchantments.getOrThrow(Enchantments.FIRE_ASPECT), UniformGenerator.between(2, 3)))
                                .setWeight(1)
                                .setQuality(2))
                        .apply(new SetEnchantmentsFunction.Builder()
                                .withEnchantment(enchantments.getOrThrow(Enchantments.UNBREAKING), ConstantValue.exactly(3)))
                        .apply(new SetEnchantmentsFunction.Builder()
                                .when(LootItemRandomChanceCondition.randomChance(0.9f))
                                .withEnchantment(enchantments.getOrThrow(Enchantments.VANISHING_CURSE), ConstantValue.exactly(1)))
                )
        );

        collector.accept(ChestLootTableKeys.Speciality.TEMPERED_BLADE, LootTable.lootTable()
                .withPool(LootPool.lootPool()
                        .name("Speciality: Tempered Blade")
                        .setRolls(ConstantValue.exactly(1))
                        .add(LootItem.lootTableItem(Items.IRON_SWORD)
                                .apply(SetNameFunction.setName(Component.translatableWithFallback(ItemTranslationKeys.TEMPERED_BLADE, "Tempered Blade"), SetNameFunction.Target.ITEM_NAME))
                                .apply(SetLoreFunction.setLore()
                                        .setMode(ListOperation.Append.INSTANCE)
                                        .addLine(Component.translatableWithFallback(ItemTranslationKeys.TEMPERED_BLADE_LORE, "Highly Durable")
                                                .withStyle(ChatFormatting.DARK_GREEN)))
                                .apply(new SetEnchantmentsFunction.Builder()
                                        .withEnchantment(enchantments.getOrThrow(Enchantments.MENDING), ConstantValue.exactly(1))
                                        .withEnchantment(enchantments.getOrThrow(Enchantments.UNBREAKING), ConstantValue.exactly(3)))
                                .apply(new SetEnchantmentsFunction.Builder()
                                        .when(LootItemRandomChanceCondition.randomChance(0.5f))
                                        .withEnchantment(enchantments.getOrThrow(Enchantments.SHARPNESS), ConstantValue.exactly(1))))));

        collector.accept(ChestLootTableKeys.Speciality.CASE_HARDENED_PICK, LootTable.lootTable()
                .withPool(LootPool.lootPool()
                        .name("Speciality: Case Hardened Pick")
                        .setRolls(ConstantValue.exactly(1))
                        .add(LootItem.lootTableItem(Items.IRON_PICKAXE)
                                .setWeight(8)
                                .apply(SetNameFunction.setName(Component.translatableWithFallback(ItemTranslationKeys.CASE_HARDENED_PICK, "Case Hardened Pick"), SetNameFunction.Target.ITEM_NAME))
                                .apply(new SetEnchantmentsFunction.Builder()
                                        .withEnchantment(enchantments.getOrThrow(Enchantments.EFFICIENCY), UniformGenerator.between(1, 2))
                                        .withEnchantment(enchantments.getOrThrow(Enchantments.UNBREAKING), UniformGenerator.between(1, 2)))
                                .apply(new SetEnchantmentsFunction.Builder()
                                        .when(LootItemRandomChanceCondition.randomChance(0.2f))
                                        .withEnchantment(enchantments.getOrThrow(Enchantments.MENDING), ConstantValue.exactly(1))))
                        .add(LootItem.lootTableItem(Items.IRON_PICKAXE)
                                .setWeight(1)
                                .setQuality(2)
                                .apply(SetNameFunction.setName(Component.translatableWithFallback(ItemTranslationKeys.CASE_HARDENED_PICK_OF_PRECISION, "Case Hardened Pick of Precision"), SetNameFunction.Target.ITEM_NAME))
                                .apply(new SetEnchantmentsFunction.Builder()
                                        .withEnchantment(enchantments.getOrThrow(Enchantments.EFFICIENCY), UniformGenerator.between(1, 2))
                                        .withEnchantment(enchantments.getOrThrow(Enchantments.UNBREAKING), UniformGenerator.between(1, 2))
                                        .withEnchantment(enchantments.getOrThrow(Enchantments.SILK_TOUCH), ConstantValue.exactly(1))))
                        .add(LootItem.lootTableItem(Items.IRON_PICKAXE)
                                .setWeight(1)
                                .setQuality(2)
                                .apply(SetNameFunction.setName(Component.translatableWithFallback(ItemTranslationKeys.CASE_HARDENED_PICK_OF_PROSPECTING, "Case Hardened Pick of Prospecting"), SetNameFunction.Target.ITEM_NAME))
                                .apply(new SetEnchantmentsFunction.Builder()
                                        .withEnchantment(enchantments.getOrThrow(Enchantments.EFFICIENCY), UniformGenerator.between(1, 2))
                                        .withEnchantment(enchantments.getOrThrow(Enchantments.UNBREAKING), UniformGenerator.between(1, 2))
                                        .withEnchantment(enchantments.getOrThrow(Enchantments.FORTUNE), UniformGenerator.between(1, 3))))));

        collector.accept(ChestLootTableKeys.Speciality.CRYSTAL_PICK, LootTable.lootTable()
                .withPool(LootPool.lootPool()
                        .name("Speciality: Crystal Pick")
                        .setRolls(ConstantValue.exactly(1))
                        .add(LootItem.lootTableItem(Items.DIAMOND_PICKAXE)
                                .setWeight(8)
                                .apply(SetNameFunction.setName(Component.translatableWithFallback(ItemTranslationKeys.CRYSTAL_PICK, "Crystal Pick"), SetNameFunction.Target.ITEM_NAME))
                                .apply(new SetEnchantmentsFunction.Builder()
                                        .withEnchantment(enchantments.getOrThrow(Enchantments.EFFICIENCY), UniformGenerator.between(3, 5))
                                        .withEnchantment(enchantments.getOrThrow(Enchantments.UNBREAKING), ConstantValue.exactly(3))))
                        .add(LootItem.lootTableItem(Items.DIAMOND_PICKAXE)
                                .setWeight(1)
                                .setQuality(2)
                                .apply(SetNameFunction.setName(Component.translatableWithFallback(ItemTranslationKeys.CRYSTAL_PICK_OF_PRECISION, "Crystal Pick of Precision"), SetNameFunction.Target.ITEM_NAME))
                                .apply(new SetEnchantmentsFunction.Builder()
                                        .withEnchantment(enchantments.getOrThrow(Enchantments.EFFICIENCY), UniformGenerator.between(3, 5))
                                        .withEnchantment(enchantments.getOrThrow(Enchantments.UNBREAKING), ConstantValue.exactly(3))
                                        .withEnchantment(enchantments.getOrThrow(Enchantments.SILK_TOUCH), ConstantValue.exactly(1))))
                        .add(LootItem.lootTableItem(Items.DIAMOND_PICKAXE)
                                .setWeight(1)
                                .setQuality(2)
                                .apply(SetNameFunction.setName(Component.translatableWithFallback(ItemTranslationKeys.CRYSTAL_PICK_OF_PROSPECTING, "Crystal Pick of Prospecting"), SetNameFunction.Target.ITEM_NAME))
                                .apply(new SetEnchantmentsFunction.Builder()
                                        .withEnchantment(enchantments.getOrThrow(Enchantments.EFFICIENCY), UniformGenerator.between(3, 5))
                                        .withEnchantment(enchantments.getOrThrow(Enchantments.UNBREAKING), ConstantValue.exactly(3))
                                        .withEnchantment(enchantments.getOrThrow(Enchantments.FORTUNE), UniformGenerator.between(2, 3))))));

        collector.accept(ChestLootTableKeys.Speciality.WOODLAND_HATCHET, LootTable.lootTable()
                .withPool(LootPool.lootPool()
                        .name("Speciality: Woodland Hatchet")
                        .setRolls(ConstantValue.exactly(1))
                        .add(LootItem.lootTableItem(Items.IRON_AXE)
                                .apply(SetNameFunction.setName(Component.translatableWithFallback(ItemTranslationKeys.WOODLAND_HATCHET, "Woodland Hatchet"), SetNameFunction.Target.ITEM_NAME))
                                .apply(new SetEnchantmentsFunction.Builder()
                                        .withEnchantment(enchantments.getOrThrow(Enchantments.UNBREAKING), UniformGenerator.between(1, 2))
                                        .withEnchantment(enchantments.getOrThrow(Enchantments.EFFICIENCY), UniformGenerator.between(1, 2))))));

        collector.accept(ChestLootTableKeys.Speciality.CRYSTAL_AXE, LootTable.lootTable()
                .withPool(LootPool.lootPool()
                        .name("Speciality: Crystal Head Axe")
                        .setRolls(ConstantValue.exactly(1))
                        .add(LootItem.lootTableItem(Items.DIAMOND_AXE)
                                .apply(SetNameFunction.setName(Component.translatableWithFallback(ItemTranslationKeys.CRYSTAL_HEAD_AXE, "Crystal Head Axe"), SetNameFunction.Target.ITEM_NAME))
                                .apply(new SetEnchantmentsFunction.Builder()
                                        .withEnchantment(enchantments.getOrThrow(Enchantments.UNBREAKING), ConstantValue.exactly(3))
                                        .withEnchantment(enchantments.getOrThrow(Enchantments.EFFICIENCY), UniformGenerator.between(3, 5))))));

        collector.accept(ChestLootTableKeys.Speciality.GRAVE_SPADE, LootTable.lootTable()
                .withPool(LootPool.lootPool()
                        .name("Speciality: Grave Spade")
                        .setRolls(ConstantValue.exactly(1))
                        .add(LootItem.lootTableItem(Items.IRON_SHOVEL)
                                .apply(SetNameFunction.setName(Component.translatableWithFallback(ItemTranslationKeys.GRAVE_SPADE, "Grave Spade"), SetNameFunction.Target.ITEM_NAME))
                                .apply(new SetEnchantmentsFunction.Builder()
                                        .withEnchantment(enchantments.getOrThrow(Enchantments.UNBREAKING), UniformGenerator.between(1, 2))
                                        .withEnchantment(enchantments.getOrThrow(Enchantments.EFFICIENCY), UniformGenerator.between(1, 2))))));

        collector.accept(ChestLootTableKeys.Speciality.SOUL_SPADE, LootTable.lootTable()
                .withPool(LootPool.lootPool()
                        .name("Speciality: Soul Spade")
                        .setRolls(ConstantValue.exactly(1))
                        .add(LootItem.lootTableItem(Items.DIAMOND_SHOVEL)
                                .apply(SetNameFunction.setName(Component.translatableWithFallback(ItemTranslationKeys.SOUL_SPADE, "Soul Spade"), SetNameFunction.Target.ITEM_NAME))
                                .apply(new SetEnchantmentsFunction.Builder()
                                        .withEnchantment(enchantments.getOrThrow(Enchantments.UNBREAKING), ConstantValue.exactly(3))
                                        .withEnchantment(enchantments.getOrThrow(Enchantments.EFFICIENCY), UniformGenerator.between(3, 5))))));
    }
}
