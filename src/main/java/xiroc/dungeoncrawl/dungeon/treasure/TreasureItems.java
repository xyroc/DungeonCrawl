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

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.nbt.StringNBT;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;

import java.util.Random;

public class TreasureItems {

    // Potions from the original Roguelike Dungeons
    public static final ItemStack LAUDANUM;
    public static final ItemStack ANIMUS;
    public static final ItemStack NECTAR;
    public static final ItemStack LUMA;

    // New Potions
    public static final ItemStack VELOCITAS;

    public static final ItemStack POTION_HEALING;
    public static final ItemStack POTION_HEALING_II;
    public static final ItemStack POTION_REGENERATION;
    public static final ItemStack POTION_REGENERATION_LONG;
    public static final ItemStack POTION_REGENERATION_II;

    public static final ItemStack SPLASH_POISON;
    public static final ItemStack SPLASH_POISON_LONG;
    public static final ItemStack SPLASH_HARMING;
    public static final ItemStack SPLASH_HARMING_II;

    public static ItemStack[] POTIONS;
    public static ItemStack[] SPECIAL_POTIONS;

    static {
        {
            CompoundNBT nbt = new CompoundNBT();
            ListNBT customPotionEffects = new ListNBT();
            CompoundNBT nausea = new CompoundNBT();
            nausea.putInt("Id", 9);
            nausea.putInt("Duration", 100);
            CompoundNBT blindness = new CompoundNBT();
            blindness.putInt("Id", 15);
            blindness.putInt("Duration", 100);
            CompoundNBT weakness = new CompoundNBT();
            weakness.putInt("Id", 18);
            weakness.putInt("Duration", 100);
            CompoundNBT miningFatique = new CompoundNBT();
            miningFatique.putInt("Id", 4);
            miningFatique.putInt("Duration", 100);
            CompoundNBT regeneration = new CompoundNBT();
            regeneration.putInt("Id", 10);
            regeneration.putInt("Amplifier", 1);
            regeneration.putInt("Duration", 160);
            customPotionEffects.add(regeneration);
            customPotionEffects.add(blindness);
            customPotionEffects.add(weakness);
            customPotionEffects.add(miningFatique);
            customPotionEffects.add(nausea);
            nbt.put("CustomPotionEffects", customPotionEffects);
            nbt.putInt("CustomPotionColor", 7014144);
            //nbt.putInt("CustomPotionColor", 0xca03fc);
            nbt.putInt("HideFlags", 32);
            CompoundNBT display = new CompoundNBT();
            ListNBT lore = new ListNBT();
            lore.add(StringNBT.valueOf(
                    ITextComponent.Serializer.toJson(new StringTextComponent("A medicinal tincture."))));
            display.put("Lore", lore);
            display.put("Name",
                    StringNBT.valueOf(ITextComponent.Serializer.toJson(new StringTextComponent("Laudanum"))));
            nbt.put("display", display);
            LAUDANUM = new ItemStack(Items.POTION);
            LAUDANUM.setTag(nbt);
        }
        {
            CompoundNBT nbt = new CompoundNBT();
            ListNBT customPotionEffects = new ListNBT();
            CompoundNBT wither = new CompoundNBT();
            wither.putInt("Id", 20);
            wither.putInt("Duration", 40);
            CompoundNBT blindness = new CompoundNBT();
            blindness.putInt("Id", 15);
            blindness.putInt("Duration", 40);
            CompoundNBT strength = new CompoundNBT();
            strength.putInt("Id", 5);
            strength.putInt("Duration", 800);
            customPotionEffects.add(strength);
            customPotionEffects.add(blindness);
            customPotionEffects.add(wither);
            nbt.put("CustomPotionEffects", customPotionEffects);
            nbt.putInt("CustomPotionColor", 13050390);
            nbt.putInt("HideFlags", 32);
            CompoundNBT display = new CompoundNBT();
            ListNBT lore = new ListNBT();
            lore.add(StringNBT
                    .valueOf(ITextComponent.Serializer.toJson(new StringTextComponent("An unstable mixture."))));
            display.put("Lore", lore);
            display.put("Name",
                    StringNBT.valueOf(ITextComponent.Serializer.toJson(new StringTextComponent("Animus"))));
            nbt.put("display", display);
            ANIMUS = new ItemStack(Items.POTION);
            ANIMUS.setTag(nbt);
        }
        {
            CompoundNBT nbt = new CompoundNBT();
            ListNBT customPotionEffects = new ListNBT();
            CompoundNBT resistance = new CompoundNBT();
            resistance.putInt("Id", 11);
            resistance.putInt("Duration", 400);
            CompoundNBT blindness = new CompoundNBT();
            blindness.putInt("Id", 15);
            blindness.putInt("Duration", 100);
            CompoundNBT absorption = new CompoundNBT();
            absorption.putInt("Id", 22);
            absorption.putInt("Amplifier", 8);
            absorption.putInt("Duration", 600);
            customPotionEffects.add(absorption);
            customPotionEffects.add(resistance);
            customPotionEffects.add(blindness);
            nbt.put("CustomPotionEffects", customPotionEffects);
            nbt.putInt("CustomPotionColor", 15446551);
            nbt.putInt("HideFlags", 32);
            CompoundNBT display = new CompoundNBT();
            ListNBT lore = new ListNBT();
            lore.add(StringNBT
                    .valueOf(ITextComponent.Serializer.toJson(new StringTextComponent("A floral extract."))));
            display.put("Lore", lore);
            display.put("Name",
                    StringNBT.valueOf(ITextComponent.Serializer.toJson(new StringTextComponent("Nectar"))));
            nbt.put("display", display);
            NECTAR = new ItemStack(Items.POTION);
            NECTAR.setTag(nbt);
        }
        {
            CompoundNBT nbt = new CompoundNBT();
            ListNBT customPotionEffects = new ListNBT();
            CompoundNBT speed = new CompoundNBT();
            speed.putInt("Id", 1);
            speed.putInt("Amplifier", 1);
            speed.putInt("Duration", 400);
            CompoundNBT blindness = new CompoundNBT();
            blindness.putInt("Id", 15);
            blindness.putInt("Duration", 40);
            CompoundNBT haste = new CompoundNBT();
            haste.putInt("Id", 3);
            haste.putInt("Duration", 400);
            customPotionEffects.add(speed);
            customPotionEffects.add(haste);
            customPotionEffects.add(blindness);
            nbt.put("CustomPotionEffects", customPotionEffects);
            nbt.putInt("CustomPotionColor", 65327);
            nbt.putInt("HideFlags", 32);
            CompoundNBT display = new CompoundNBT();
            ListNBT lore = new ListNBT();
            lore.add(StringNBT.valueOf(
                    ITextComponent.Serializer.toJson(new StringTextComponent("An energetic beverage."))));
            display.put("Lore", lore);
            display.put("Name",
                    StringNBT.valueOf(ITextComponent.Serializer.toJson(new StringTextComponent("Velocitas"))));
            nbt.put("display", display);
            VELOCITAS = new ItemStack(Items.POTION);
            VELOCITAS.setTag(nbt);
        }
        {
            CompoundNBT nbt = new CompoundNBT();
            ListNBT customPotionEffects = new ListNBT();
            CompoundNBT glowing = new CompoundNBT();
            glowing.putInt("Id", 24);
            glowing.putInt("Duration", 12000);
            customPotionEffects.add(glowing);
            nbt.put("CustomPotionEffects", customPotionEffects);
            nbt.putInt("CustomPotionColor", 16448000);
            nbt.putInt("HideFlags", 32);
            CompoundNBT display = new CompoundNBT();
            ListNBT lore = new ListNBT();
            lore.add(StringNBT.valueOf(ITextComponent.Serializer.toJson(new StringTextComponent("A glowstone extract."))));
            display.put("Lore", lore);
            display.put("Name", StringNBT.valueOf(ITextComponent.Serializer.toJson(new StringTextComponent("Luma"))));
            nbt.put("display", display);
            LUMA = new ItemStack(Items.POTION);
            LUMA.setTag(nbt);
        }

        POTION_HEALING = createItemWithNbt(Items.POTION, createPotionTag("minecraft:healing"));
        POTION_HEALING_II = createItemWithNbt(Items.POTION, createPotionTag("minecraft:strong_healing"));

        POTION_REGENERATION = createItemWithNbt(Items.POTION, createPotionTag("minecraft:regeneration"));
        POTION_REGENERATION_LONG = createItemWithNbt(Items.POTION, createPotionTag("minecraft:long_regeneration"));
        POTION_REGENERATION_II = createItemWithNbt(Items.POTION, createPotionTag("minecraft:strong_regeneration"));

        SPLASH_POISON = createItemWithNbt(Items.POTION, createPotionTag("minecraft:poison"));
        SPLASH_POISON_LONG = createItemWithNbt(Items.POTION, createPotionTag("minecraft:long_poison"));
        SPLASH_HARMING = createItemWithNbt(Items.POTION, createPotionTag("minecraft:harming"));
        SPLASH_HARMING_II = createItemWithNbt(Items.POTION, createPotionTag("minecraft:strong_harming"));

        POTIONS = new ItemStack[]{POTION_HEALING, POTION_HEALING_II, POTION_REGENERATION, POTION_REGENERATION_LONG,
                POTION_REGENERATION_II, SPLASH_HARMING, SPLASH_HARMING_II, SPLASH_POISON, SPLASH_POISON_LONG};
        SPECIAL_POTIONS = new ItemStack[]{LAUDANUM, ANIMUS, NECTAR, LUMA, VELOCITAS};
    }

    public static CompoundNBT createDisplayTag(String name, String... loreEntries) {
        CompoundNBT display = new CompoundNBT();
        display.put("Name", StringNBT.valueOf(ITextComponent.Serializer.toJson(new StringTextComponent(name))));
        ListNBT lore = new ListNBT();
        for (String line : loreEntries)
            lore.add(StringNBT.valueOf(ITextComponent.Serializer.toJson(new StringTextComponent(line))));
        if (lore.size() > 0)
            display.put("Lore", lore);
        return display;
    }

    public static CompoundNBT createPotionTag(String potionName) {
        CompoundNBT potion = new CompoundNBT();
        potion.putString("Potion", potionName);
        return potion;
    }

    public static CompoundNBT createEnchantmentTag(String enchantment, int level) {
        CompoundNBT enchantmentTag = new CompoundNBT();
        enchantmentTag.putString("id", enchantment);
        enchantmentTag.putInt("lvl", level);
        return enchantmentTag;
    }

    public static ItemStack createItemWithNbt(Item item, CompoundNBT nbt) {
        ItemStack stack = new ItemStack(item);
        stack.setTag(nbt);
        return stack;
    }

    public static ItemStack getRandomSpecialPotion(Random rand, int stage) {
        if (rand.nextFloat() < 0.4)
            return POTIONS[rand.nextInt(POTIONS.length)].copy();
        int bound = stage == 0 ? 1 : SPECIAL_POTIONS.length;
        return SPECIAL_POTIONS[rand.nextInt(bound)].copy();
    }

}