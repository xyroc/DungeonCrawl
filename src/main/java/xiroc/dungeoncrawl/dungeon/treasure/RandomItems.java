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

import com.google.gson.JsonArray;
import com.google.gson.JsonParser;
import com.google.gson.stream.JsonReader;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.loot.RandomValueRange;
import net.minecraft.resources.IResourceManager;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.registries.ForgeRegistries;
import xiroc.dungeoncrawl.DungeonCrawl;
import xiroc.dungeoncrawl.dungeon.misc.Banner;
import xiroc.dungeoncrawl.dungeon.monster.RandomEquipment;

import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Random;

public class RandomItems {

    public static final int COLOR = 3847130;

    public static final ItemStack[] ITEMS;

    public static final ItemStack[] RARE_ITEMS;

    public static final ItemStack REINFORCED_BOW, BOOTS_OF_BATTLE, PANTS_OF_DEFLECTION, LUMBERJACKET, YOKEL_AXE, DOOM,
            THE_SLAYER, DEMON_HUNTER_CROSSBOW, THIEF_DAGGER, THE_GREAT_CLEAVER, ARCHANGEL_SWORD, REPULSER, ELB_BOW;

    public static WeightedRandomTreasureItem STAGE_1, STAGE_2, STAGE_3, STAGE_4, STAGE_5;

    private static final RandomValueRange[] UNBREAKING_LEVELS = {new RandomValueRange(1, 1), new RandomValueRange(1, 2),
            new RandomValueRange(2, 2), new RandomValueRange(2, 3), new RandomValueRange(3, 3)};

    static {
        REINFORCED_BOW = new ItemStack(Items.BOW);
        REINFORCED_BOW.enchant(Enchantments.UNBREAKING, 1);
        REINFORCED_BOW.enchant(Enchantments.POWER_ARROWS, 1);
        REINFORCED_BOW.setHoverName(new StringTextComponent("Reinforced Bow"));

        BOOTS_OF_BATTLE = new ItemStack(Items.LEATHER_BOOTS);
        RandomEquipment.setArmorColor(BOOTS_OF_BATTLE, COLOR);
        BOOTS_OF_BATTLE.enchant(Enchantments.UNBREAKING, 1);
        BOOTS_OF_BATTLE.enchant(Enchantments.ALL_DAMAGE_PROTECTION, 1);
        BOOTS_OF_BATTLE.setHoverName(new StringTextComponent("Boots of Battle"));

        PANTS_OF_DEFLECTION = new ItemStack(Items.LEATHER_LEGGINGS);
        RandomEquipment.setArmorColor(PANTS_OF_DEFLECTION, COLOR);
        PANTS_OF_DEFLECTION.enchant(Enchantments.ALL_DAMAGE_PROTECTION, 2);
        PANTS_OF_DEFLECTION.enchant(Enchantments.THORNS, 1);
        PANTS_OF_DEFLECTION.setHoverName(new StringTextComponent("Pants of Deflection"));

        LUMBERJACKET = new ItemStack(Items.LEATHER_CHESTPLATE);
        RandomEquipment.setArmorColor(LUMBERJACKET, 11546150);
        LUMBERJACKET.enchant(Enchantments.UNBREAKING, 3);
        LUMBERJACKET.enchant(Enchantments.FIRE_PROTECTION, 1);
        LUMBERJACKET.setHoverName(new StringTextComponent("Lumberjacket"));

        YOKEL_AXE = new ItemStack(Items.IRON_AXE);
        YOKEL_AXE.enchant(Enchantments.BLOCK_EFFICIENCY, 2);
        YOKEL_AXE.enchant(Enchantments.SHARPNESS, 1);
        YOKEL_AXE.enchant(Enchantments.UNBREAKING, 1);
        YOKEL_AXE.setHoverName(new StringTextComponent("Yokel's Axe"));

        DOOM = new ItemStack(ForgeRegistries.ITEMS.getValue(new ResourceLocation("minecraft:golden_sword")));
        DOOM.enchant(Enchantments.SHARPNESS, 1);
        DOOM.enchant(Enchantments.FIRE_ASPECT, 2);
        DOOM.enchant(Enchantments.UNBREAKING, 1);
        DOOM.setHoverName(new StringTextComponent("Doom"));

        THE_SLAYER = new ItemStack(ForgeRegistries.ITEMS.getValue(new ResourceLocation("minecraft:diamond_sword")));
        THE_SLAYER.enchant(Enchantments.SHARPNESS, 4);
        THE_SLAYER.setHoverName(new StringTextComponent("The Slayer"));

        DEMON_HUNTER_CROSSBOW = new ItemStack(Items.CROSSBOW);
        DEMON_HUNTER_CROSSBOW.enchant(Enchantments.PIERCING, 2);
        DEMON_HUNTER_CROSSBOW.enchant(Enchantments.MULTISHOT, 1);
        DEMON_HUNTER_CROSSBOW.enchant(Enchantments.QUICK_CHARGE, 1);
        DEMON_HUNTER_CROSSBOW.enchant(Enchantments.POWER_ARROWS, 4);
        DEMON_HUNTER_CROSSBOW.setHoverName(new StringTextComponent("Demon Hunter's Crossbow"));

        THIEF_DAGGER = new ItemStack(Items.IRON_SWORD);
        THIEF_DAGGER.enchant(Enchantments.SHARPNESS, 1);
        THIEF_DAGGER.enchant(Enchantments.MOB_LOOTING, 3);
        THIEF_DAGGER.setHoverName(new StringTextComponent("Thief's Dagger"));

        THE_GREAT_CLEAVER = new ItemStack(Items.DIAMOND_SWORD);
        THE_GREAT_CLEAVER.enchant(Enchantments.SWEEPING_EDGE, 3);
        THE_GREAT_CLEAVER.enchant(Enchantments.SMITE, 4);
        THE_GREAT_CLEAVER.enchant(Enchantments.UNBREAKING, 3);
        THE_GREAT_CLEAVER.setHoverName(new StringTextComponent("The Great Cleaver"));

        ARCHANGEL_SWORD = new ItemStack(Items.GOLDEN_SWORD);
        ARCHANGEL_SWORD.enchant(Enchantments.SHARPNESS, 4);
        ARCHANGEL_SWORD.enchant(Enchantments.UNBREAKING, 2);
        ARCHANGEL_SWORD.enchant(Enchantments.VANISHING_CURSE, 1);
        ARCHANGEL_SWORD.setHoverName(new StringTextComponent("Archangel's Sword"));

        REPULSER = new ItemStack(Items.IRON_SWORD);
        REPULSER.enchant(Enchantments.KNOCKBACK, 2);
        REPULSER.enchant(Enchantments.SWEEPING_EDGE, 1);
        REPULSER.setHoverName(new StringTextComponent("Repulser"));

        ELB_BOW = new ItemStack(Items.BOW);
        ELB_BOW.enchant(Enchantments.POWER_ARROWS, 4);
        ELB_BOW.enchant(Enchantments.PIERCING, 3);
        ELB_BOW.enchant(Enchantments.MENDING, 1);
        ELB_BOW.setHoverName(new StringTextComponent("Bow of the Elbs"));

        ITEMS = new ItemStack[]{REINFORCED_BOW, BOOTS_OF_BATTLE, LUMBERJACKET, YOKEL_AXE, DOOM, ARCHANGEL_SWORD,
                REPULSER};

        RARE_ITEMS = new ItemStack[]{THE_SLAYER, DEMON_HUNTER_CROSSBOW, THIEF_DAGGER, THE_GREAT_CLEAVER};

    }

    public static void loadJson(IResourceManager resourceManager) {
        try {
            JsonParser parser = new JsonParser();

            // TODO: introduce a method to load single file
            {
                ResourceLocation stage1 = DungeonCrawl.locate("treasure/stage_1.json");
                DungeonCrawl.LOGGER.debug("Loading {}", stage1.toString());
                JsonArray array = parser.parse(new JsonReader(new InputStreamReader(resourceManager.getResource(stage1).getInputStream()))).getAsJsonArray();
                STAGE_1 = WeightedRandomTreasureItem.fromJson(array);
            }

            {
                ResourceLocation stage2 = DungeonCrawl.locate("treasure/stage_2.json");
                DungeonCrawl.LOGGER.debug("Loading {}", stage2.toString());
                JsonArray array = parser.parse(new JsonReader(new InputStreamReader(resourceManager.getResource(stage2).getInputStream()))).getAsJsonArray();
                STAGE_2 = WeightedRandomTreasureItem.fromJson(array);
            }

            {
                ResourceLocation stage3 = DungeonCrawl.locate("treasure/stage_3.json");
                DungeonCrawl.LOGGER.debug("Loading {}", stage3.toString());
                JsonArray array = parser.parse(new JsonReader(new InputStreamReader(resourceManager.getResource(stage3).getInputStream()))).getAsJsonArray();
                STAGE_3 = WeightedRandomTreasureItem.fromJson(array);
            }

            {
                ResourceLocation stage4 = DungeonCrawl.locate("treasure/stage_4.json");
                DungeonCrawl.LOGGER.debug("Loading {}", stage4.toString());
                JsonArray array = parser.parse(new JsonReader(new InputStreamReader(resourceManager.getResource(stage4).getInputStream()))).getAsJsonArray();
                STAGE_4 = WeightedRandomTreasureItem.fromJson(array);
            }

            {
                ResourceLocation stage5 = DungeonCrawl.locate("treasure/stage_5.json");
                DungeonCrawl.LOGGER.debug("Loading {}", stage5.toString());
                JsonArray array = parser.parse(new JsonReader(new InputStreamReader(resourceManager.getResource(stage5).getInputStream()))).getAsJsonArray();
                STAGE_5 = WeightedRandomTreasureItem.fromJson(array);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static ItemStack generate(ServerWorld world, Random rand, Integer lootLevel) {
        switch (lootLevel) {
            case 0:
                return STAGE_1.roll(rand).createItem(world, rand, lootLevel);
            case 1:
                return STAGE_2.roll(rand).createItem(world, rand, lootLevel);
            case 2:
                return STAGE_3.roll(rand).createItem(world, rand, lootLevel);
            case 3:
                return STAGE_4.roll(rand).createItem(world, rand, lootLevel);
            default:
                return STAGE_5.roll(rand).createItem(world, rand, lootLevel);
        }
    }

    public static TreasureItem createEnchantedSpecialItem(String itemName) {
        return new TreasureItem("minecraft:air").setProcessor((world, rand, lootLevel) -> {
            Item item = ForgeRegistries.ITEMS.getValue(new ResourceLocation(itemName));
            if (item != null) {
                ItemStack stack = new ItemStack(item);
                if (rand.nextFloat() < 0.5F + 0.1F * lootLevel) {
                    return EnchantmentHelper.enchantItem(rand, stack, 10 + 3 * lootLevel, lootLevel > 2);
                } else {
                    return stack;
                }
            } else {
                DungeonCrawl.LOGGER.error("The item {} does not exist.", itemName);
                return ItemStack.EMPTY;
            }
        });
    }

    /**
     * Creates a shield item stack with random patterns.
     */
    public static ItemStack createShield(Random rand, int lootLevel) {
        ItemStack shield = new ItemStack(Items.SHIELD);
        lootLevel = Math.min(4, lootLevel);
        float f = rand.nextFloat();
        if (f < 0.12F + lootLevel * 0.02F) {
            shield.enchant(Enchantments.UNBREAKING, UNBREAKING_LEVELS[lootLevel].getInt(rand));
            if (f < 0.04F + lootLevel * 0.01F) {
                shield.enchant(Enchantments.MENDING, 1);
            }
            if (rand.nextFloat() < 0.75F) {
                shield.enchant(Enchantments.VANISHING_CURSE, 1);
            }
        }
        shield.getOrCreateTag().put("BlockEntityTag", Banner.createPatterns(rand));
        return shield;
    }

}
