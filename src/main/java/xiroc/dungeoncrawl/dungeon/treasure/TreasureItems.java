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

import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.StringTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

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

    private static final ItemStack[] POTIONS;
    private static final ItemStack[] SPECIAL_POTIONS;

    static {
        {
            CompoundTag nbt = new CompoundTag();
            ListTag customPotionEffects = new ListTag();
            CompoundTag nausea = new CompoundTag();
            nausea.putInt("Id", 9);
            nausea.putInt("Duration", 100);
            CompoundTag blindness = new CompoundTag();
            blindness.putInt("Id", 15);
            blindness.putInt("Duration", 100);
            CompoundTag weakness = new CompoundTag();
            weakness.putInt("Id", 18);
            weakness.putInt("Duration", 100);
            CompoundTag miningFatigue = new CompoundTag();
            miningFatigue.putInt("Id", 4);
            miningFatigue.putInt("Duration", 100);
            CompoundTag regeneration = new CompoundTag();
            regeneration.putInt("Id", 10);
            regeneration.putInt("Amplifier", 1);
            regeneration.putInt("Duration", 160);
            customPotionEffects.add(regeneration);
            customPotionEffects.add(blindness);
            customPotionEffects.add(weakness);
            customPotionEffects.add(miningFatigue);
            customPotionEffects.add(nausea);
            nbt.put("CustomPotionEffects", customPotionEffects);
            nbt.putInt("CustomPotionColor", 7014144);
            //nbt.putInt("CustomPotionColor", 0xca03fc);
            nbt.putInt("HideFlags", 32);
            CompoundTag display = new CompoundTag();
            ListTag lore = new ListTag();
            lore.add(StringTag.valueOf(
                    Component.Serializer.toJson(new TextComponent("A medicinal tincture."))));
            display.put("Lore", lore);
            display.put("Name",
                    StringTag.valueOf(Component.Serializer.toJson(new TextComponent("Laudanum"))));
            nbt.put("display", display);
            LAUDANUM = new ItemStack(Items.POTION);
            LAUDANUM.setTag(nbt);
        }
        {
            CompoundTag nbt = new CompoundTag();
            ListTag customPotionEffects = new ListTag();
            CompoundTag wither = new CompoundTag();
            wither.putInt("Id", 20);
            wither.putInt("Duration", 40);
            CompoundTag blindness = new CompoundTag();
            blindness.putInt("Id", 15);
            blindness.putInt("Duration", 40);
            CompoundTag strength = new CompoundTag();
            strength.putInt("Id", 5);
            strength.putInt("Duration", 800);
            customPotionEffects.add(strength);
            customPotionEffects.add(blindness);
            customPotionEffects.add(wither);
            nbt.put("CustomPotionEffects", customPotionEffects);
            nbt.putInt("CustomPotionColor", 13050390);
            nbt.putInt("HideFlags", 32);
            CompoundTag display = new CompoundTag();
            ListTag lore = new ListTag();
            lore.add(StringTag
                    .valueOf(Component.Serializer.toJson(new TextComponent("An unstable mixture."))));
            display.put("Lore", lore);
            display.put("Name",
                    StringTag.valueOf(Component.Serializer.toJson(new TextComponent("Animus"))));
            nbt.put("display", display);
            ANIMUS = new ItemStack(Items.POTION);
            ANIMUS.setTag(nbt);
        }
        {
            CompoundTag nbt = new CompoundTag();
            ListTag customPotionEffects = new ListTag();
            CompoundTag resistance = new CompoundTag();
            resistance.putInt("Id", 11);
            resistance.putInt("Duration", 400);
            CompoundTag blindness = new CompoundTag();
            blindness.putInt("Id", 15);
            blindness.putInt("Duration", 100);
            CompoundTag absorption = new CompoundTag();
            absorption.putInt("Id", 22);
            absorption.putInt("Amplifier", 8);
            absorption.putInt("Duration", 600);
            customPotionEffects.add(absorption);
            customPotionEffects.add(resistance);
            customPotionEffects.add(blindness);
            nbt.put("CustomPotionEffects", customPotionEffects);
            nbt.putInt("CustomPotionColor", 15446551);
            nbt.putInt("HideFlags", 32);
            CompoundTag display = new CompoundTag();
            ListTag lore = new ListTag();
            lore.add(StringTag
                    .valueOf(Component.Serializer.toJson(new TextComponent("A floral extract."))));
            display.put("Lore", lore);
            display.put("Name",
                    StringTag.valueOf(Component.Serializer.toJson(new TextComponent("Nectar"))));
            nbt.put("display", display);
            NECTAR = new ItemStack(Items.POTION);
            NECTAR.setTag(nbt);
        }
        {
            CompoundTag nbt = new CompoundTag();
            ListTag customPotionEffects = new ListTag();
            CompoundTag speed = new CompoundTag();
            speed.putInt("Id", 1);
            speed.putInt("Amplifier", 1);
            speed.putInt("Duration", 400);
            CompoundTag blindness = new CompoundTag();
            blindness.putInt("Id", 15);
            blindness.putInt("Duration", 40);
            CompoundTag haste = new CompoundTag();
            haste.putInt("Id", 3);
            haste.putInt("Duration", 400);
            customPotionEffects.add(speed);
            customPotionEffects.add(haste);
            customPotionEffects.add(blindness);
            nbt.put("CustomPotionEffects", customPotionEffects);
            nbt.putInt("CustomPotionColor", 65327);
            nbt.putInt("HideFlags", 32);
            CompoundTag display = new CompoundTag();
            ListTag lore = new ListTag();
            lore.add(StringTag.valueOf(
                    Component.Serializer.toJson(new TextComponent("An energetic beverage."))));
            display.put("Lore", lore);
            display.put("Name",
                    StringTag.valueOf(Component.Serializer.toJson(new TextComponent("Velocitas"))));
            nbt.put("display", display);
            VELOCITAS = new ItemStack(Items.POTION);
            VELOCITAS.setTag(nbt);
        }
        {
            CompoundTag nbt = new CompoundTag();
            ListTag customPotionEffects = new ListTag();
            CompoundTag glowing = new CompoundTag();
            glowing.putInt("Id", 24);
            glowing.putInt("Duration", 12000);
            customPotionEffects.add(glowing);
            nbt.put("CustomPotionEffects", customPotionEffects);
            nbt.putInt("CustomPotionColor", 16448000);
            nbt.putInt("HideFlags", 32);
            CompoundTag display = new CompoundTag();
            ListTag lore = new ListTag();
            lore.add(StringTag.valueOf(Component.Serializer.toJson(new TextComponent("A glowstone extract."))));
            display.put("Lore", lore);
            display.put("Name", StringTag.valueOf(Component.Serializer.toJson(new TextComponent("Luma"))));
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

    public static CompoundTag createDisplayTag(String name, String... loreEntries) {
        CompoundTag display = new CompoundTag();
        display.put("Name", StringTag.valueOf(Component.Serializer.toJson(new TextComponent(name))));
        ListTag lore = new ListTag();
        if (loreEntries.length > 0) {
            for (String line : loreEntries) {
                lore.add(StringTag.valueOf(Component.Serializer.toJson(new TextComponent(line))));
            }
            display.put("Lore", lore);
        }
        return display;
    }

    public static CompoundTag createPotionTag(String potionName) {
        CompoundTag potion = new CompoundTag();
        potion.putString("Potion", potionName);
        return potion;
    }

        private static ItemStack createItemWithNbt (Item item, CompoundTag nbt){
            ItemStack stack = new ItemStack(item);
            stack.setTag(nbt);
            return stack;
        }

        public static ItemStack getRandomSpecialPotion (Random rand,int stage){
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