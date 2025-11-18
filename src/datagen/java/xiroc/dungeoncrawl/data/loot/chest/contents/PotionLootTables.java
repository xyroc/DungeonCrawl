package xiroc.dungeoncrawl.data.loot.chest.contents;

import net.minecraft.core.HolderLookup;
import net.minecraft.core.component.DataComponents;
import net.minecraft.data.loot.LootTableSubProvider;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.util.Unit;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.alchemy.PotionContents;
import net.minecraft.world.item.alchemy.Potions;
import net.minecraft.world.level.storage.loot.LootPool;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.entries.LootItem;
import net.minecraft.world.level.storage.loot.entries.NestedLootTable;
import net.minecraft.world.level.storage.loot.functions.SetComponentsFunction;
import net.minecraft.world.level.storage.loot.functions.SetLoreFunction;
import net.minecraft.world.level.storage.loot.functions.SetNameFunction;
import net.minecraft.world.level.storage.loot.functions.SetPotionFunction;
import net.minecraft.world.level.storage.loot.providers.number.ConstantValue;
import xiroc.dungeoncrawl.data.loot.chest.ChestLootTableKeys;

import java.util.List;
import java.util.Optional;
import java.util.function.BiConsumer;

@SuppressWarnings("DuplicatedCode") // Disable IntelliJ duplicate code detection
public record PotionLootTables(HolderLookup.Provider registries) implements LootTableSubProvider {
    private static final int TICKS_PER_SECOND = 20;

    @Override
    public void generate(BiConsumer<ResourceKey<LootTable>, LootTable.Builder> collector) {
        collector.accept(ChestLootTableKeys.Potion.LEVEL_0, LootTable.lootTable()
                .withPool(LootPool.lootPool()
                        .setRolls(ConstantValue.exactly(1))
                        .add(NestedLootTable.inlineLootTable(LootTable.lootTable()
                                        .withPool(LootPool.lootPool()
                                                .add(LootItem.lootTableItem(Items.POTION)
                                                        .apply(SetPotionFunction.setPotion(Potions.HEALING))
                                                        .setWeight(2))
                                                .add(LootItem.lootTableItem(Items.POTION)
                                                        .apply(SetPotionFunction.setPotion(Potions.STRENGTH)))
                                                .add(LootItem.lootTableItem(Items.POTION)
                                                        .apply(SetPotionFunction.setPotion(Potions.POISON)))
                                                .add(LootItem.lootTableItem(Items.POTION)
                                                        .apply(SetPotionFunction.setPotion(Potions.REGENERATION)))
                                                .add(LootItem.lootTableItem(Items.SPLASH_POTION)
                                                        .apply(SetPotionFunction.setPotion(Potions.HEALING)))
                                                .add(LootItem.lootTableItem(Items.SPLASH_POTION)
                                                        .apply(SetPotionFunction.setPotion(Potions.STRENGTH)))
                                                .add(LootItem.lootTableItem(Items.SPLASH_POTION)
                                                        .apply(SetPotionFunction.setPotion(Potions.POISON)))
                                                .add(LootItem.lootTableItem(Items.SPLASH_POTION)
                                                        .apply(SetPotionFunction.setPotion(Potions.REGENERATION))))
                                        .build())
                                .setWeight(2))
                        .add(NestedLootTable.inlineLootTable(LootTable.lootTable()
                                .withPool(LootPool.lootPool()
                                        .add(NestedLootTable.lootTableReference(ChestLootTableKeys.Potion.LAUDANUM))
                                        .add(NestedLootTable.lootTableReference(ChestLootTableKeys.Potion.ANIMUS))
                                        .add(NestedLootTable.lootTableReference(ChestLootTableKeys.Potion.COFFEE))
                                        .add(NestedLootTable.lootTableReference(ChestLootTableKeys.Potion.LUMA))
                                        .add(NestedLootTable.lootTableReference(ChestLootTableKeys.Potion.VITAE))
                                        .add(NestedLootTable.lootTableReference(ChestLootTableKeys.Potion.NECTAR)))
                                .build())))
        );

        collector.accept(ChestLootTableKeys.Potion.LEVEL_1, LootTable.lootTable()
                .withPool(LootPool.lootPool()
                        .setRolls(ConstantValue.exactly(1))
                        .add(NestedLootTable.inlineLootTable(LootTable.lootTable()
                                        .withPool(LootPool.lootPool()
                                                .add(LootItem.lootTableItem(Items.POTION)
                                                        .apply(SetPotionFunction.setPotion(Potions.HEALING)))
                                                .add(LootItem.lootTableItem(Items.POTION)
                                                        .apply(SetPotionFunction.setPotion(Potions.STRONG_HEALING)))
                                                .add(LootItem.lootTableItem(Items.POTION)
                                                        .apply(SetPotionFunction.setPotion(Potions.STRENGTH)))
                                                .add(LootItem.lootTableItem(Items.POTION)
                                                        .apply(SetPotionFunction.setPotion(Potions.REGENERATION)))
                                                .add(LootItem.lootTableItem(Items.SPLASH_POTION)
                                                        .apply(SetPotionFunction.setPotion(Potions.HEALING)))
                                                .add(LootItem.lootTableItem(Items.SPLASH_POTION)
                                                        .apply(SetPotionFunction.setPotion(Potions.STRENGTH)))
                                                .add(LootItem.lootTableItem(Items.SPLASH_POTION)
                                                        .apply(SetPotionFunction.setPotion(Potions.POISON)))
                                                .add(LootItem.lootTableItem(Items.SPLASH_POTION)
                                                        .apply(SetPotionFunction.setPotion(Potions.REGENERATION)))
                                        ).build())
                                .setWeight(2))
                        .add(NestedLootTable.inlineLootTable(LootTable.lootTable()
                                .withPool(LootPool.lootPool()
                                        .add(NestedLootTable.lootTableReference(ChestLootTableKeys.Potion.LAUDANUM))
                                        .add(NestedLootTable.lootTableReference(ChestLootTableKeys.Potion.ANIMUS))
                                        .add(NestedLootTable.lootTableReference(ChestLootTableKeys.Potion.COFFEE))
                                        .add(NestedLootTable.lootTableReference(ChestLootTableKeys.Potion.LUMA))
                                        .add(NestedLootTable.lootTableReference(ChestLootTableKeys.Potion.VITAE))
                                        .add(NestedLootTable.lootTableReference(ChestLootTableKeys.Potion.NECTAR))
                                        .add(NestedLootTable.lootTableReference(ChestLootTableKeys.Potion.VILE_MIXTURE))
                                ).build())))
        );

        collector.accept(ChestLootTableKeys.Potion.LEVEL_2, LootTable.lootTable()
                .withPool(LootPool.lootPool()
                        .setRolls(ConstantValue.exactly(1))
                        .add(NestedLootTable.inlineLootTable(LootTable.lootTable()
                                        .withPool(LootPool.lootPool()
                                                .add(LootItem.lootTableItem(Items.POTION)
                                                        .apply(SetPotionFunction.setPotion(Potions.HEALING)))
                                                .add(LootItem.lootTableItem(Items.POTION)
                                                        .apply(SetPotionFunction.setPotion(Potions.STRONG_HEALING))
                                                        .setWeight(2))
                                                .add(LootItem.lootTableItem(Items.POTION)
                                                        .apply(SetPotionFunction.setPotion(Potions.STRENGTH)))
                                                .add(LootItem.lootTableItem(Items.POTION)
                                                        .apply(SetPotionFunction.setPotion(Potions.LONG_STRENGTH)))
                                                .add(LootItem.lootTableItem(Items.POTION)
                                                        .apply(SetPotionFunction.setPotion(Potions.REGENERATION)))
                                                .add(LootItem.lootTableItem(Items.POTION)
                                                        .apply(SetPotionFunction.setPotion(Potions.LONG_REGENERATION)))
                                                .add(LootItem.lootTableItem(Items.SPLASH_POTION)
                                                        .apply(SetPotionFunction.setPotion(Potions.HEALING)))
                                                .add(LootItem.lootTableItem(Items.SPLASH_POTION)
                                                        .apply(SetPotionFunction.setPotion(Potions.STRONG_HEALING)))
                                                .add(LootItem.lootTableItem(Items.SPLASH_POTION)
                                                        .apply(SetPotionFunction.setPotion(Potions.STRONG_STRENGTH)))
                                                .add(LootItem.lootTableItem(Items.SPLASH_POTION)
                                                        .apply(SetPotionFunction.setPotion(Potions.POISON)))
                                                .add(LootItem.lootTableItem(Items.SPLASH_POTION)
                                                        .apply(SetPotionFunction.setPotion(Potions.REGENERATION)))
                                                .add(LootItem.lootTableItem(Items.SPLASH_POTION)
                                                        .apply(SetPotionFunction.setPotion(Potions.WEAVING)))
                                                .add(LootItem.lootTableItem(Items.SPLASH_POTION)
                                                        .apply(SetPotionFunction.setPotion(Potions.INFESTED)))
                                                .add(LootItem.lootTableItem(Items.SPLASH_POTION)
                                                        .apply(SetPotionFunction.setPotion(Potions.OOZING))))
                                        .build())
                                .setWeight(2))
                        .add(NestedLootTable.inlineLootTable(LootTable.lootTable()
                                .withPool(LootPool.lootPool()
                                        .add(NestedLootTable.lootTableReference(ChestLootTableKeys.Potion.LAUDANUM))
                                        .add(NestedLootTable.lootTableReference(ChestLootTableKeys.Potion.ANIMUS))
                                        .add(NestedLootTable.lootTableReference(ChestLootTableKeys.Potion.COFFEE))
                                        .add(NestedLootTable.lootTableReference(ChestLootTableKeys.Potion.LUMA))
                                        .add(NestedLootTable.lootTableReference(ChestLootTableKeys.Potion.VITAE))
                                        .add(NestedLootTable.lootTableReference(ChestLootTableKeys.Potion.NECTAR))
                                        .add(NestedLootTable.lootTableReference(ChestLootTableKeys.Potion.VILE_MIXTURE)))
                                .build())))
        );

        collector.accept(ChestLootTableKeys.Potion.LEVEL_3, LootTable.lootTable()
                .withPool(LootPool.lootPool()
                        .setRolls(ConstantValue.exactly(1))
                        .add(NestedLootTable.inlineLootTable(LootTable.lootTable()
                                        .withPool(LootPool.lootPool()
                                                .add(LootItem.lootTableItem(Items.POTION)
                                                        .apply(SetPotionFunction.setPotion(Potions.HEALING)))
                                                .add(LootItem.lootTableItem(Items.POTION)
                                                        .apply(SetPotionFunction.setPotion(Potions.STRONG_HEALING))
                                                        .setWeight(2))
                                                .add(LootItem.lootTableItem(Items.POTION)
                                                        .apply(SetPotionFunction.setPotion(Potions.STRENGTH)))
                                                .add(LootItem.lootTableItem(Items.POTION)
                                                        .apply(SetPotionFunction.setPotion(Potions.LONG_STRENGTH))
                                                        .setWeight(2))
                                                .add(LootItem.lootTableItem(Items.POTION)
                                                        .apply(SetPotionFunction.setPotion(Potions.LONG_REGENERATION)))
                                                .add(LootItem.lootTableItem(Items.POTION)
                                                        .apply(SetPotionFunction.setPotion(Potions.STRONG_REGENERATION)))
                                                .add(LootItem.lootTableItem(Items.SPLASH_POTION)
                                                        .apply(SetPotionFunction.setPotion(Potions.HEALING)))
                                                .add(LootItem.lootTableItem(Items.SPLASH_POTION)
                                                        .apply(SetPotionFunction.setPotion(Potions.STRONG_HEALING))
                                                        .setWeight(2))
                                                .add(LootItem.lootTableItem(Items.SPLASH_POTION)
                                                        .apply(SetPotionFunction.setPotion(Potions.STRONG_STRENGTH))
                                                        .setWeight(2))
                                                .add(LootItem.lootTableItem(Items.SPLASH_POTION)
                                                        .apply(SetPotionFunction.setPotion(Potions.STRONG_POISON)))
                                                .add(LootItem.lootTableItem(Items.SPLASH_POTION)
                                                        .apply(SetPotionFunction.setPotion(Potions.REGENERATION)))
                                                .add(LootItem.lootTableItem(Items.SPLASH_POTION)
                                                        .apply(SetPotionFunction.setPotion(Potions.WEAVING)))
                                                .add(LootItem.lootTableItem(Items.SPLASH_POTION)
                                                        .apply(SetPotionFunction.setPotion(Potions.INFESTED)))
                                                .add(LootItem.lootTableItem(Items.SPLASH_POTION)
                                                        .apply(SetPotionFunction.setPotion(Potions.OOZING))))
                                        .build())
                                .setWeight(2))
                        .add(NestedLootTable.inlineLootTable(LootTable.lootTable()
                                .withPool(LootPool.lootPool()
                                        .add(NestedLootTable.lootTableReference(ChestLootTableKeys.Potion.LAUDANUM))
                                        .add(NestedLootTable.lootTableReference(ChestLootTableKeys.Potion.ANIMUS))
                                        .add(NestedLootTable.lootTableReference(ChestLootTableKeys.Potion.COFFEE))
                                        .add(NestedLootTable.lootTableReference(ChestLootTableKeys.Potion.LUMA))
                                        .add(NestedLootTable.lootTableReference(ChestLootTableKeys.Potion.VITAE))
                                        .add(NestedLootTable.lootTableReference(ChestLootTableKeys.Potion.NECTAR))
                                        .add(NestedLootTable.lootTableReference(ChestLootTableKeys.Potion.VILE_MIXTURE)))
                                .build())))
        );

        collector.accept(ChestLootTableKeys.Potion.LEVEL_4, LootTable.lootTable()
                .withPool(LootPool.lootPool()
                        .setRolls(ConstantValue.exactly(1))
                        .add(NestedLootTable.inlineLootTable(LootTable.lootTable()
                                        .withPool(LootPool.lootPool()
                                                .add(LootItem.lootTableItem(Items.POTION)
                                                        .apply(SetPotionFunction.setPotion(Potions.FIRE_RESISTANCE)))
                                                .add(LootItem.lootTableItem(Items.POTION)
                                                        .apply(SetPotionFunction.setPotion(Potions.LONG_FIRE_RESISTANCE)))
                                                .add(LootItem.lootTableItem(Items.POTION)
                                                        .apply(SetPotionFunction.setPotion(Potions.HEALING)))
                                                .add(LootItem.lootTableItem(Items.POTION)
                                                        .apply(SetPotionFunction.setPotion(Potions.STRONG_HEALING))
                                                        .setWeight(2))
                                                .add(LootItem.lootTableItem(Items.POTION)
                                                        .apply(SetPotionFunction.setPotion(Potions.STRENGTH)))
                                                .add(LootItem.lootTableItem(Items.POTION)
                                                        .apply(SetPotionFunction.setPotion(Potions.LONG_STRENGTH))
                                                        .setWeight(2))
                                                .add(LootItem.lootTableItem(Items.POTION)
                                                        .apply(SetPotionFunction.setPotion(Potions.LONG_REGENERATION)))
                                                .add(LootItem.lootTableItem(Items.POTION)
                                                        .apply(SetPotionFunction.setPotion(Potions.STRONG_REGENERATION)))
                                                .add(LootItem.lootTableItem(Items.SPLASH_POTION)
                                                        .apply(SetPotionFunction.setPotion(Potions.LONG_FIRE_RESISTANCE)))
                                                .add(LootItem.lootTableItem(Items.SPLASH_POTION)
                                                        .apply(SetPotionFunction.setPotion(Potions.HEALING)))
                                                .add(LootItem.lootTableItem(Items.SPLASH_POTION)
                                                        .apply(SetPotionFunction.setPotion(Potions.STRONG_HEALING))
                                                        .setWeight(2))
                                                .add(LootItem.lootTableItem(Items.SPLASH_POTION)
                                                        .apply(SetPotionFunction.setPotion(Potions.STRONG_STRENGTH))
                                                        .setWeight(2))
                                                .add(LootItem.lootTableItem(Items.SPLASH_POTION)
                                                        .apply(SetPotionFunction.setPotion(Potions.STRONG_POISON)))
                                                .add(LootItem.lootTableItem(Items.SPLASH_POTION)
                                                        .apply(SetPotionFunction.setPotion(Potions.REGENERATION)))
                                                .add(LootItem.lootTableItem(Items.SPLASH_POTION)
                                                        .apply(SetPotionFunction.setPotion(Potions.WEAVING)))
                                                .add(LootItem.lootTableItem(Items.SPLASH_POTION)
                                                        .apply(SetPotionFunction.setPotion(Potions.INFESTED)))
                                                .add(LootItem.lootTableItem(Items.SPLASH_POTION)
                                                        .apply(SetPotionFunction.setPotion(Potions.OOZING))))
                                        .build())
                                .setWeight(2))
                        .add(NestedLootTable.inlineLootTable(LootTable.lootTable()
                                .withPool(LootPool.lootPool()
                                        .add(NestedLootTable.lootTableReference(ChestLootTableKeys.Potion.LAUDANUM))
                                        .add(NestedLootTable.lootTableReference(ChestLootTableKeys.Potion.ANIMUS))
                                        .add(NestedLootTable.lootTableReference(ChestLootTableKeys.Potion.COFFEE))
                                        .add(NestedLootTable.lootTableReference(ChestLootTableKeys.Potion.LUMA))
                                        .add(NestedLootTable.lootTableReference(ChestLootTableKeys.Potion.VITAE))
                                        .add(NestedLootTable.lootTableReference(ChestLootTableKeys.Potion.NECTAR))
                                        .add(NestedLootTable.lootTableReference(ChestLootTableKeys.Potion.VILE_MIXTURE)))
                                .build())))
        );

        collector.accept(ChestLootTableKeys.Potion.ABSINTHE, LootTable.lootTable()
                .withPool(LootPool.lootPool()
                        .setRolls(ConstantValue.exactly(1))
                        .add(LootItem.lootTableItem(Items.POTION)
                                .apply(SetComponentsFunction.setComponent(DataComponents.POTION_CONTENTS, new PotionContents(
                                        Optional.empty(),
                                        Optional.of(0xC8FA96),
                                        List.of(new MobEffectInstance(MobEffects.POISON, 3 * TICKS_PER_SECOND, 0),
                                                new MobEffectInstance(MobEffects.NIGHT_VISION, 120 * TICKS_PER_SECOND, 0),
                                                new MobEffectInstance(MobEffects.JUMP, 120 * TICKS_PER_SECOND, 2)))
                                ))
                                .apply(SetComponentsFunction.setComponent(DataComponents.HIDE_ADDITIONAL_TOOLTIP, Unit.INSTANCE))
                                .apply(SetNameFunction.setName(Component.translatableWithFallback("item.dungeoncrawl.potion.absinthe", "Absinthe"), SetNameFunction.Target.ITEM_NAME))
                        )
                )
        );

        collector.accept(ChestLootTableKeys.Potion.ANIMUS, LootTable.lootTable()
                .withPool(LootPool.lootPool()
                        .setRolls(ConstantValue.exactly(1))
                        .add(LootItem.lootTableItem(Items.POTION)
                                .apply(SetComponentsFunction.setComponent(DataComponents.POTION_CONTENTS, new PotionContents(
                                        Optional.empty(),
                                        Optional.of(0xFF0000),
                                        List.of(new MobEffectInstance(MobEffects.DAMAGE_BOOST, 20 * TICKS_PER_SECOND, 2),
                                                new MobEffectInstance(MobEffects.BLINDNESS, 10 * TICKS_PER_SECOND, 0),
                                                new MobEffectInstance(MobEffects.WITHER, 3 * TICKS_PER_SECOND, 0)))
                                ))
                                .apply(SetComponentsFunction.setComponent(DataComponents.HIDE_ADDITIONAL_TOOLTIP, Unit.INSTANCE))
                                .apply(SetNameFunction.setName(Component.translatableWithFallback("item.dungeoncrawl.potion.animus", "Animus"), SetNameFunction.Target.ITEM_NAME))
                                .apply(SetLoreFunction.setLore()
                                        .addLine(Component.translatableWithFallback("item.dungeoncrawl.potion.animus.lore", "An unstable mixture.")))
                        )
                )
        );

        collector.accept(ChestLootTableKeys.Potion.COFFEE, LootTable.lootTable()
                .withPool(LootPool.lootPool()
                        .setRolls(ConstantValue.exactly(1))
                        .add(LootItem.lootTableItem(Items.POTION)
                                .apply(SetComponentsFunction.setComponent(DataComponents.POTION_CONTENTS, new PotionContents(
                                        Optional.empty(),
                                        Optional.of(0x14140A),
                                        List.of(new MobEffectInstance(MobEffects.DIG_SPEED, 600 * TICKS_PER_SECOND, 1),
                                                new MobEffectInstance(MobEffects.MOVEMENT_SPEED, 600 * TICKS_PER_SECOND, 0)))
                                ))
                                .apply(SetComponentsFunction.setComponent(DataComponents.HIDE_ADDITIONAL_TOOLTIP, Unit.INSTANCE))
                                .apply(SetNameFunction.setName(Component.translatableWithFallback("item.dungeoncrawl.potion.coffee", "Coffee"), SetNameFunction.Target.ITEM_NAME))
                                .apply(SetLoreFunction.setLore()
                                        .addLine(Component.translatableWithFallback("item.dungeoncrawl.potion.coffee.lore", "A dark roast bean brew.")))
                        )
                )
        );

        collector.accept(ChestLootTableKeys.Potion.LAUDANUM, LootTable.lootTable()
                .withPool(LootPool.lootPool()
                        .setRolls(ConstantValue.exactly(1))
                        .add(LootItem.lootTableItem(Items.POTION)
                                .apply(SetComponentsFunction.setComponent(DataComponents.POTION_CONTENTS, new PotionContents(
                                        Optional.empty(),
                                        Optional.of(0x963200),
                                        List.of(new MobEffectInstance(MobEffects.REGENERATION, 8 * TICKS_PER_SECOND, 2),
                                                new MobEffectInstance(MobEffects.WEAKNESS, 5 * TICKS_PER_SECOND, 1),
                                                new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, 5 * TICKS_PER_SECOND, 1),
                                                new MobEffectInstance(MobEffects.DIG_SLOWDOWN, 5 * TICKS_PER_SECOND, 1),
                                                new MobEffectInstance(MobEffects.CONFUSION, 5 * TICKS_PER_SECOND, 0)))
                                ))
                                .apply(SetComponentsFunction.setComponent(DataComponents.HIDE_ADDITIONAL_TOOLTIP, Unit.INSTANCE))
                                .apply(SetNameFunction.setName(Component.translatableWithFallback("item.dungeoncrawl.potion.laudanum", "Laudanum"), SetNameFunction.Target.ITEM_NAME))
                                .apply(SetLoreFunction.setLore()
                                        .addLine(Component.translatableWithFallback("item.dungeoncrawl.potion.laudanum.lore", "A medicinal tincture.")))
                        )
                )
        );

        collector.accept(ChestLootTableKeys.Potion.LUMA, LootTable.lootTable()
                .withPool(LootPool.lootPool()
                        .setRolls(ConstantValue.exactly(1))
                        .add(LootItem.lootTableItem(Items.POTION)
                                .apply(SetComponentsFunction.setComponent(DataComponents.POTION_CONTENTS, new PotionContents(
                                        Optional.empty(),
                                        Optional.of(0xFAFA00),
                                        List.of(new MobEffectInstance(MobEffects.GLOWING, 600 * TICKS_PER_SECOND, 0)))
                                ))
                                .apply(SetComponentsFunction.setComponent(DataComponents.HIDE_ADDITIONAL_TOOLTIP, Unit.INSTANCE))
                                .apply(SetNameFunction.setName(Component.translatableWithFallback("item.dungeoncrawl.potion.luma", "Luma"), SetNameFunction.Target.ITEM_NAME))
                                .apply(SetLoreFunction.setLore()
                                        .addLine(Component.translatableWithFallback("item.dungeoncrawl.potion.luma.lore", "A glowstone extract.")))
                        )
                )
        );

        collector.accept(ChestLootTableKeys.Potion.MOONSHINE, LootTable.lootTable()
                .withPool(LootPool.lootPool()
                        .setRolls(ConstantValue.exactly(1))
                        .add(LootItem.lootTableItem(Items.POTION)
                                .apply(SetComponentsFunction.setComponent(DataComponents.POTION_CONTENTS, new PotionContents(
                                        Optional.empty(),
                                        Optional.of(0xFAF0E6),
                                        List.of(new MobEffectInstance(MobEffects.HARM, 1, 0),
                                                new MobEffectInstance(MobEffects.BLINDNESS, 60 * TICKS_PER_SECOND, 0),
                                                new MobEffectInstance(MobEffects.DAMAGE_RESISTANCE, 45 * TICKS_PER_SECOND, 1)))
                                ))
                                .apply(SetComponentsFunction.setComponent(DataComponents.HIDE_ADDITIONAL_TOOLTIP, Unit.INSTANCE))
                                .apply(SetNameFunction.setName(Component.translatableWithFallback("item.dungeoncrawl.potion.moonshine", "Moonshine"), SetNameFunction.Target.ITEM_NAME))
                        )
                )
        );

        collector.accept(ChestLootTableKeys.Potion.NECTAR, LootTable.lootTable()
                .withPool(LootPool.lootPool()
                        .setRolls(ConstantValue.exactly(1))
                        .add(LootItem.lootTableItem(Items.POTION)
                                .apply(SetComponentsFunction.setComponent(DataComponents.POTION_CONTENTS, new PotionContents(
                                        Optional.empty(),
                                        Optional.of(0xFA96FA),
                                        List.of(new MobEffectInstance(MobEffects.ABSORPTION, 20 * TICKS_PER_SECOND, 9),
                                                new MobEffectInstance(MobEffects.DAMAGE_RESISTANCE, 20 * TICKS_PER_SECOND, 2),
                                                new MobEffectInstance(MobEffects.HEAL, 1, 1)))
                                ))
                                .apply(SetComponentsFunction.setComponent(DataComponents.HIDE_ADDITIONAL_TOOLTIP, Unit.INSTANCE))
                                .apply(SetNameFunction.setName(Component.translatableWithFallback("item.dungeoncrawl.potion.nectar", "Nectar"), SetNameFunction.Target.ITEM_NAME))
                                .apply(SetLoreFunction.setLore()
                                        .addLine(Component.translatableWithFallback("item.dungeoncrawl.potion.nectar.lore", "A floral extract.")))
                        )
                )
        );

        collector.accept(ChestLootTableKeys.Potion.STOUT, LootTable.lootTable()
                .withPool(LootPool.lootPool()
                        .setRolls(ConstantValue.exactly(1))
                        .add(LootItem.lootTableItem(Items.POTION)
                                .apply(SetComponentsFunction.setComponent(DataComponents.POTION_CONTENTS, new PotionContents(
                                        Optional.empty(),
                                        Optional.of(0x322814),
                                        List.of(new MobEffectInstance(MobEffects.REGENERATION, 5 * TICKS_PER_SECOND, 0),
                                                new MobEffectInstance(MobEffects.SATURATION, TICKS_PER_SECOND, 1),
                                                new MobEffectInstance(MobEffects.HEALTH_BOOST, 120 * TICKS_PER_SECOND, 1),
                                                new MobEffectInstance(MobEffects.DAMAGE_RESISTANCE, 120 * TICKS_PER_SECOND, 0)))
                                ))
                                .apply(SetComponentsFunction.setComponent(DataComponents.HIDE_ADDITIONAL_TOOLTIP, Unit.INSTANCE))
                                .apply(SetNameFunction.setName(Component.translatableWithFallback("item.dungeoncrawl.potion.stout", "Stout"), SetNameFunction.Target.ITEM_NAME))
                                .apply(SetLoreFunction.setLore()
                                        .addLine(Component.translatableWithFallback("item.dungeoncrawl.potion.stout.lore", "\"It's good for you\"")))
                        )
                )
        );

        collector.accept(ChestLootTableKeys.Potion.TEQUILA, LootTable.lootTable()
                .withPool(LootPool.lootPool()
                        .setRolls(ConstantValue.exactly(1))
                        .add(LootItem.lootTableItem(Items.POTION)
                                .apply(SetComponentsFunction.setComponent(DataComponents.POTION_CONTENTS, new PotionContents(
                                        Optional.empty(),
                                        Optional.of(0x322814),
                                        List.of(new MobEffectInstance(MobEffects.DAMAGE_BOOST, 60 * TICKS_PER_SECOND, 2),
                                                new MobEffectInstance(MobEffects.DIG_SLOWDOWN, 60 * TICKS_PER_SECOND, 0)))
                                ))
                                .apply(SetComponentsFunction.setComponent(DataComponents.HIDE_ADDITIONAL_TOOLTIP, Unit.INSTANCE))
                                .apply(SetNameFunction.setName(Component.translatableWithFallback("item.dungeoncrawl.potion.tequila", "Tequila"), SetNameFunction.Target.ITEM_NAME))
                        )
                )
        );

        // Not actually the "Vile Mixture" from RLD, simply because that one uses totally random effects, amplifiers, and durations, which isn't really feasible to replicate with vanilla loot functions.
        collector.accept(ChestLootTableKeys.Potion.VILE_MIXTURE, LootTable.lootTable()
                .withPool(LootPool.lootPool()
                        .setRolls(ConstantValue.exactly(1))
                        .add(LootItem.lootTableItem(Items.SPLASH_POTION)
                                .setWeight(2))
                        .add(LootItem.lootTableItem(Items.LINGERING_POTION))
                        .apply(SetComponentsFunction.setComponent(DataComponents.POTION_CONTENTS, new PotionContents(
                                Optional.empty(),
                                Optional.of(0x369936),
                                List.of(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, 20 * TICKS_PER_SECOND, 0),
                                        new MobEffectInstance(MobEffects.DIG_SLOWDOWN, 20 * TICKS_PER_SECOND, 0),
                                        new MobEffectInstance(MobEffects.WEAKNESS, 20 * TICKS_PER_SECOND, 0)))
                        ))
                        .apply(SetNameFunction.setName(Component.translatableWithFallback("item.dungeoncrawl.potion.vile_mixture", "Vile Mixture"), SetNameFunction.Target.ITEM_NAME))
                )
        );

        collector.accept(ChestLootTableKeys.Potion.VITAE, LootTable.lootTable()
                .withPool(LootPool.lootPool()
                        .setRolls(ConstantValue.exactly(1))
                        .add(LootItem.lootTableItem(Items.POTION)
                                .apply(SetComponentsFunction.setComponent(DataComponents.POTION_CONTENTS, new PotionContents(
                                        Optional.empty(),
                                        Optional.of(0xE63214),
                                        List.of(new MobEffectInstance(MobEffects.SATURATION, TICKS_PER_SECOND, 9),
                                                new MobEffectInstance(MobEffects.MOVEMENT_SPEED, 120 * TICKS_PER_SECOND, 1),
                                                new MobEffectInstance(MobEffects.DIG_SPEED, 120 * TICKS_PER_SECOND, 1),
                                                new MobEffectInstance(MobEffects.JUMP, 120 * TICKS_PER_SECOND, 2)))
                                ))
                                .apply(SetComponentsFunction.setComponent(DataComponents.HIDE_ADDITIONAL_TOOLTIP, Unit.INSTANCE))
                                .apply(SetNameFunction.setName(Component.translatableWithFallback("item.dungeoncrawl.potion.vitae", "Vitae"), SetNameFunction.Target.ITEM_NAME))
                                .apply(SetLoreFunction.setLore()
                                        .addLine(Component.translatableWithFallback("item.dungeoncrawl.potion.vitae.lore", "Essence of life.")))
                        )
                ));
    }
}
