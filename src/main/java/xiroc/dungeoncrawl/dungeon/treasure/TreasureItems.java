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

package xiroc.dungeoncrawl.dungeon.treasure;

import net.minecraft.core.component.DataComponentPatch;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.util.RandomSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemStackTemplate;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.alchemy.PotionContents;
import net.minecraft.world.item.alchemy.Potions;
import net.minecraft.world.item.component.ItemLore;
import net.minecraft.world.item.component.TooltipDisplay;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

public class TreasureItems {
    private static final TooltipDisplay TOOLTIP_DISPLAY = new TooltipDisplay(false, new LinkedHashSet<>(Set.of(
            DataComponents.POTION_CONTENTS
    )));

    // Potions from the original Roguelike Dungeons
    private static final ItemStackTemplate LAUDANUM;
    private static final ItemStackTemplate ANIMUS;
    private static final ItemStackTemplate NECTAR;
    private static final ItemStackTemplate LUMA;

    // New Potions
    private static final ItemStackTemplate VELOCITAS;

    private static final ItemStackTemplate POTION_HEALING;
    private static final ItemStackTemplate POTION_HEALING_II;
    private static final ItemStackTemplate POTION_REGENERATION;
    private static final ItemStackTemplate POTION_REGENERATION_LONG;
    private static final ItemStackTemplate POTION_REGENERATION_II;

    private static final ItemStackTemplate SPLASH_POISON;
    private static final ItemStackTemplate SPLASH_POISON_LONG;
    private static final ItemStackTemplate SPLASH_HARMING;
    private static final ItemStackTemplate SPLASH_HARMING_II;

    private static final ItemStackTemplate[] POTIONS;
    private static final ItemStackTemplate[] SPECIAL_POTIONS;

    static {
        LAUDANUM = new ItemStackTemplate(Items.POTION, DataComponentPatch.builder()
                .set(DataComponents.POTION_CONTENTS, new PotionContents(Optional.empty(), Optional.of(7014144),
                        List.of(new MobEffectInstance(MobEffects.NAUSEA, 100),
                                new MobEffectInstance(MobEffects.BLINDNESS, 100),
                                new MobEffectInstance(MobEffects.WEAKNESS, 100),
                                new MobEffectInstance(MobEffects.MINING_FATIGUE, 100),
                                new MobEffectInstance(MobEffects.REGENERATION, 160, 1)),
                        Optional.of("Laudanum")))
                .set(DataComponents.CUSTOM_NAME, Component.literal("Laudanum"))
                .set(DataComponents.LORE, new ItemLore(List.of(Component.literal("A medicinal tincture."))))
                .set(DataComponents.TOOLTIP_DISPLAY, TOOLTIP_DISPLAY)
                .build());

        ANIMUS = new ItemStackTemplate(Items.POTION, DataComponentPatch.builder()
                .set(DataComponents.POTION_CONTENTS, new PotionContents(Optional.empty(), Optional.of(13050390),
                        List.of(new MobEffectInstance(MobEffects.WITHER, 40),
                                new MobEffectInstance(MobEffects.BLINDNESS, 40),
                                new MobEffectInstance(MobEffects.STRENGTH, 800)),
                        Optional.of("Animus")))
                .set(DataComponents.CUSTOM_NAME, Component.literal("Animus"))
                .set(DataComponents.LORE, new ItemLore(List.of(Component.literal("An unstable mixture."))))
                .set(DataComponents.TOOLTIP_DISPLAY, TOOLTIP_DISPLAY)
                .build());

        NECTAR = new ItemStackTemplate(Items.POTION, DataComponentPatch.builder()
                .set(DataComponents.POTION_CONTENTS, new PotionContents(Optional.empty(), Optional.of(15446551),
                        List.of(new MobEffectInstance(MobEffects.RESISTANCE, 400),
                                new MobEffectInstance(MobEffects.BLINDNESS, 100),
                                new MobEffectInstance(MobEffects.ABSORPTION, 600, 8)),
                        Optional.of("Nectar")))
                .set(DataComponents.CUSTOM_NAME, Component.literal("Nectar"))
                .set(DataComponents.LORE, new ItemLore(List.of(Component.literal("A floral extract."))))
                .set(DataComponents.TOOLTIP_DISPLAY, TOOLTIP_DISPLAY)
                .build());

        LUMA = new ItemStackTemplate(Items.POTION, DataComponentPatch.builder()
                .set(DataComponents.POTION_CONTENTS, new PotionContents(Optional.empty(),
                        Optional.of(16448000),
                        List.of(new MobEffectInstance(MobEffects.GLOWING, 12000)),
                        Optional.of("Luma")))
                .set(DataComponents.CUSTOM_NAME, Component.literal("Luma"))
                .set(DataComponents.LORE, new ItemLore(List.of(Component.literal("A glowstone extract."))))
                .set(DataComponents.TOOLTIP_DISPLAY, TOOLTIP_DISPLAY)
                .build());

        VELOCITAS = new ItemStackTemplate(Items.POTION, DataComponentPatch.builder()
                .set(DataComponents.POTION_CONTENTS, new PotionContents(Optional.empty(), Optional.of(65327),
                        List.of(new MobEffectInstance(MobEffects.SPEED, 400, 1),
                                new MobEffectInstance(MobEffects.BLINDNESS, 40),
                                new MobEffectInstance(MobEffects.HASTE, 400)),
                        Optional.of("Velocitas")))
                .set(DataComponents.CUSTOM_NAME, Component.literal("Velocitas"))
                .set(DataComponents.LORE, new ItemLore(List.of(Component.literal("An energetic beverage."))))
                .set(DataComponents.TOOLTIP_DISPLAY, TOOLTIP_DISPLAY)
                .build());

        POTION_HEALING = new ItemStackTemplate(Items.POTION, DataComponentPatch.builder()
                .set(DataComponents.POTION_CONTENTS, new PotionContents(Optional.of(Potions.HEALING), Optional.empty(), List.of(), Optional.empty()))
                .build());
        POTION_HEALING_II = new ItemStackTemplate(Items.POTION, DataComponentPatch.builder()
                .set(DataComponents.POTION_CONTENTS, new PotionContents(Optional.of(Potions.STRONG_HEALING), Optional.empty(), List.of(), Optional.empty()))
                .build());

        POTION_REGENERATION = new ItemStackTemplate(Items.POTION, DataComponentPatch.builder()
                .set(DataComponents.POTION_CONTENTS, new PotionContents(Optional.of(Potions.REGENERATION), Optional.empty(), List.of(), Optional.empty()))
                .build());

        POTION_REGENERATION_LONG = new ItemStackTemplate(Items.POTION, DataComponentPatch.builder()
                .set(DataComponents.POTION_CONTENTS, new PotionContents(Optional.of(Potions.LONG_REGENERATION), Optional.empty(), List.of(), Optional.empty()))
                .build());

        POTION_REGENERATION_II = new ItemStackTemplate(Items.POTION, DataComponentPatch.builder()
                .set(DataComponents.POTION_CONTENTS, new PotionContents(Optional.of(Potions.STRONG_REGENERATION), Optional.empty(), List.of(), Optional.empty()))
                .build());

        SPLASH_POISON = new ItemStackTemplate(Items.SPLASH_POTION, DataComponentPatch.builder()
                .set(DataComponents.POTION_CONTENTS, new PotionContents(Optional.of(Potions.POISON), Optional.empty(), List.of(), Optional.empty()))
                .build());

        SPLASH_POISON_LONG = new ItemStackTemplate(Items.SPLASH_POTION, DataComponentPatch.builder()
                .set(DataComponents.POTION_CONTENTS, new PotionContents(Optional.of(Potions.LONG_POISON), Optional.empty(), List.of(), Optional.empty()))
                .build());

        SPLASH_HARMING = new ItemStackTemplate(Items.SPLASH_POTION, DataComponentPatch.builder()
                .set(DataComponents.POTION_CONTENTS, new PotionContents(Optional.of(Potions.HARMING), Optional.empty(), List.of(), Optional.empty()))
                .build());

        SPLASH_HARMING_II = new ItemStackTemplate(Items.SPLASH_POTION, DataComponentPatch.builder()
                .set(DataComponents.POTION_CONTENTS, new PotionContents(Optional.of(Potions.STRONG_HARMING), Optional.empty(), List.of(), Optional.empty()))
                .build());

        POTIONS = new ItemStackTemplate[]{POTION_HEALING, POTION_HEALING_II, POTION_REGENERATION, POTION_REGENERATION_LONG,
                POTION_REGENERATION_II, SPLASH_HARMING, SPLASH_HARMING_II, SPLASH_POISON, SPLASH_POISON_LONG};
        SPECIAL_POTIONS = new ItemStackTemplate[]{LAUDANUM, ANIMUS, NECTAR, LUMA, VELOCITAS};
    }

    public static ItemStack getRandomSpecialPotion(RandomSource rand, int stage) {
        if (rand.nextFloat() < 0.4) {
            return POTIONS[rand.nextInt(POTIONS.length)].create();
        } else {
            if (stage == 0) {
                return LAUDANUM.create();
            } else {
                return SPECIAL_POTIONS[rand.nextInt(SPECIAL_POTIONS.length)].create();
            }
        }
    }

}