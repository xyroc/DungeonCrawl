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

import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.util.RandomSource;
import net.minecraft.util.Unit;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.alchemy.PotionContents;
import net.minecraft.world.item.alchemy.Potions;
import net.minecraft.world.item.component.ItemLore;

import java.util.List;
import java.util.Optional;

public class TreasureItems {

    // Potions from the original Roguelike Dungeons
    private static final ItemStack LAUDANUM;
    private static final ItemStack ANIMUS;
    private static final ItemStack NECTAR;
    private static final ItemStack LUMA;

    // New Potions
    private static final ItemStack VELOCITAS;

    private static final ItemStack POTION_HEALING;
    private static final ItemStack POTION_HEALING_II;
    private static final ItemStack POTION_REGENERATION;
    private static final ItemStack POTION_REGENERATION_LONG;
    private static final ItemStack POTION_REGENERATION_II;

    private static final ItemStack SPLASH_POISON;
    private static final ItemStack SPLASH_POISON_LONG;
    private static final ItemStack SPLASH_HARMING;
    private static final ItemStack SPLASH_HARMING_II;

    private static final ItemStack[] POTIONS;
    private static final ItemStack[] SPECIAL_POTIONS;

    static {
        LAUDANUM = new ItemStack(Items.POTION);
        LAUDANUM.set(DataComponents.POTION_CONTENTS, new PotionContents(Optional.empty(), Optional.of(7014144),
                List.of(new MobEffectInstance(MobEffects.CONFUSION, 100),
                        new MobEffectInstance(MobEffects.BLINDNESS, 100),
                        new MobEffectInstance(MobEffects.WEAKNESS, 100),
                        new MobEffectInstance(MobEffects.DIG_SLOWDOWN, 100),
                        new MobEffectInstance(MobEffects.REGENERATION, 160, 1))));
        LAUDANUM.set(DataComponents.CUSTOM_NAME, Component.literal("Laudanum"));
        LAUDANUM.set(DataComponents.LORE, new ItemLore(List.of(Component.literal("A medicinal tincture."))));
        LAUDANUM.set(DataComponents.HIDE_ADDITIONAL_TOOLTIP, Unit.INSTANCE);

        ANIMUS = new ItemStack(Items.POTION);
        ANIMUS.set(DataComponents.POTION_CONTENTS, new PotionContents(Optional.empty(), Optional.of(13050390),
                List.of(new MobEffectInstance(MobEffects.WITHER, 40),
                        new MobEffectInstance(MobEffects.BLINDNESS, 40),
                        new MobEffectInstance(MobEffects.DAMAGE_BOOST, 800))));
        ANIMUS.set(DataComponents.CUSTOM_NAME, Component.literal("Animus"));
        ANIMUS.set(DataComponents.LORE, new ItemLore(List.of(Component.literal("An unstable mixture."))));
        ANIMUS.set(DataComponents.HIDE_ADDITIONAL_TOOLTIP, Unit.INSTANCE);

        NECTAR = new ItemStack(Items.POTION);
        NECTAR.set(DataComponents.POTION_CONTENTS, new PotionContents(Optional.empty(), Optional.of(15446551),
                List.of(new MobEffectInstance(MobEffects.DAMAGE_RESISTANCE, 400),
                        new MobEffectInstance(MobEffects.BLINDNESS, 100),
                        new MobEffectInstance(MobEffects.ABSORPTION, 600, 8))));
        NECTAR.set(DataComponents.CUSTOM_NAME, Component.literal("Nectar"));
        NECTAR.set(DataComponents.LORE, new ItemLore(List.of(Component.literal("A floral extract."))));
        NECTAR.set(DataComponents.HIDE_ADDITIONAL_TOOLTIP, Unit.INSTANCE);

        LUMA = new ItemStack(Items.POTION);
        LUMA.set(DataComponents.POTION_CONTENTS, new PotionContents(Optional.empty(), Optional.of(16448000),
                List.of(new MobEffectInstance(MobEffects.GLOWING, 12000))));
        LUMA.set(DataComponents.CUSTOM_NAME, Component.literal("Luma"));
        LUMA.set(DataComponents.LORE, new ItemLore(List.of(Component.literal("A glowstone extract."))));
        LUMA.set(DataComponents.HIDE_ADDITIONAL_TOOLTIP, Unit.INSTANCE);

        VELOCITAS = new ItemStack(Items.POTION);
        VELOCITAS.set(DataComponents.POTION_CONTENTS, new PotionContents(Optional.empty(), Optional.of(65327),
                List.of(new MobEffectInstance(MobEffects.MOVEMENT_SPEED, 400, 1),
                        new MobEffectInstance(MobEffects.BLINDNESS, 40),
                        new MobEffectInstance(MobEffects.DIG_SPEED, 400))));
        VELOCITAS.set(DataComponents.CUSTOM_NAME, Component.literal("Velocitas"));
        VELOCITAS.set(DataComponents.LORE, new ItemLore(List.of(Component.literal("An energetic beverage."))));
        VELOCITAS.set(DataComponents.HIDE_ADDITIONAL_TOOLTIP, Unit.INSTANCE);

        POTION_HEALING = new ItemStack(Items.POTION);
        POTION_HEALING.set(DataComponents.POTION_CONTENTS, new PotionContents(Optional.of(Potions.HEALING), Optional.empty(), List.of()));

        POTION_HEALING_II = new ItemStack(Items.POTION);
        POTION_HEALING_II.set(DataComponents.POTION_CONTENTS, new PotionContents(Optional.of(Potions.STRONG_HEALING), Optional.empty(), List.of()));

        POTION_REGENERATION = new ItemStack(Items.POTION);
        POTION_REGENERATION.set(DataComponents.POTION_CONTENTS, new PotionContents(Optional.of(Potions.REGENERATION), Optional.empty(), List.of()));

        POTION_REGENERATION_LONG = new ItemStack(Items.POTION);
        POTION_REGENERATION_LONG.set(DataComponents.POTION_CONTENTS, new PotionContents(Optional.of(Potions.LONG_REGENERATION), Optional.empty(), List.of()));

        POTION_REGENERATION_II = new ItemStack(Items.POTION);
        POTION_HEALING_II.set(DataComponents.POTION_CONTENTS, new PotionContents(Optional.of(Potions.STRONG_REGENERATION), Optional.empty(), List.of()));

        SPLASH_POISON = new ItemStack(Items.SPLASH_POTION);
        SPLASH_POISON.set(DataComponents.POTION_CONTENTS, new PotionContents(Optional.of(Potions.POISON), Optional.empty(), List.of()));

        SPLASH_POISON_LONG = new ItemStack(Items.SPLASH_POTION);
        SPLASH_POISON_LONG.set(DataComponents.POTION_CONTENTS, new PotionContents(Optional.of(Potions.LONG_POISON), Optional.empty(), List.of()));

        SPLASH_HARMING = new ItemStack(Items.SPLASH_POTION);
        SPLASH_HARMING.set(DataComponents.POTION_CONTENTS, new PotionContents(Optional.of(Potions.HARMING), Optional.empty(), List.of()));

        SPLASH_HARMING_II = new ItemStack(Items.SPLASH_POTION);
        SPLASH_HARMING_II.set(DataComponents.POTION_CONTENTS, new PotionContents(Optional.of(Potions.STRONG_HARMING), Optional.empty(), List.of()));

        POTIONS = new ItemStack[]{POTION_HEALING, POTION_HEALING_II, POTION_REGENERATION, POTION_REGENERATION_LONG,
                POTION_REGENERATION_II, SPLASH_HARMING, SPLASH_HARMING_II, SPLASH_POISON, SPLASH_POISON_LONG};
        SPECIAL_POTIONS = new ItemStack[]{LAUDANUM, ANIMUS, NECTAR, LUMA, VELOCITAS};
    }

    public static ItemStack getRandomSpecialPotion(RandomSource rand, int stage) {
        if (rand.nextFloat() < 0.4) {
            return POTIONS[rand.nextInt(POTIONS.length)].copy();
        } else {
            if (stage == 0) {
                return LAUDANUM.copy();
            } else {
                return SPECIAL_POTIONS[rand.nextInt(SPECIAL_POTIONS.length)].copy();
            }
        }
    }

}