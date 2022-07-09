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
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.util.Mth;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.Enchantments;
import xiroc.dungeoncrawl.DungeonCrawl;
import xiroc.dungeoncrawl.dungeon.misc.Banner;
import xiroc.dungeoncrawl.util.Range;
import xiroc.dungeoncrawl.util.WeightedRandom;

import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Random;

public class RandomItems {

    private static WeightedRandom<Item> STAGE_1, STAGE_2, STAGE_3, STAGE_4, STAGE_5;

    private static final Range[] UNBREAKING_LEVELS = {
            new Range(1, 1),
            new Range(1, 2),
            new Range(2, 2),
            new Range(2, 3),
            new Range(3, 3)};

    public static void loadJson(ResourceManager resourceManager) {
        try {
            JsonParser parser = new JsonParser();

            // TODO: introduce a method to load single file
            {
                ResourceLocation stage1 = DungeonCrawl.locate("treasure/stage_1.json");
                DungeonCrawl.LOGGER.debug("Loading {}", stage1.toString());
                JsonArray array = parser.parse(new JsonReader(new InputStreamReader(resourceManager.getResource(stage1).getInputStream()))).getAsJsonArray();
                STAGE_1 = WeightedRandom.ITEM.fromJson(array);
            }

            {
                ResourceLocation stage2 = DungeonCrawl.locate("treasure/stage_2.json");
                DungeonCrawl.LOGGER.debug("Loading {}", stage2.toString());
                JsonArray array = parser.parse(new JsonReader(new InputStreamReader(resourceManager.getResource(stage2).getInputStream()))).getAsJsonArray();
                STAGE_2 = WeightedRandom.ITEM.fromJson(array);
            }

            {
                ResourceLocation stage3 = DungeonCrawl.locate("treasure/stage_3.json");
                DungeonCrawl.LOGGER.debug("Loading {}", stage3.toString());
                JsonArray array = parser.parse(new JsonReader(new InputStreamReader(resourceManager.getResource(stage3).getInputStream()))).getAsJsonArray();
                STAGE_3 = WeightedRandom.ITEM.fromJson(array);
            }

            {
                ResourceLocation stage4 = DungeonCrawl.locate("treasure/stage_4.json");
                DungeonCrawl.LOGGER.debug("Loading {}", stage4.toString());
                JsonArray array = parser.parse(new JsonReader(new InputStreamReader(resourceManager.getResource(stage4).getInputStream()))).getAsJsonArray();
                STAGE_4 = WeightedRandom.ITEM.fromJson(array);
            }

            {
                ResourceLocation stage5 = DungeonCrawl.locate("treasure/stage_5.json");
                DungeonCrawl.LOGGER.debug("Loading {}", stage5.toString());
                JsonArray array = parser.parse(new JsonReader(new InputStreamReader(resourceManager.getResource(stage5).getInputStream()))).getAsJsonArray();
                STAGE_5 = WeightedRandom.ITEM.fromJson(array);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static WeightedRandom<Item> itemProvider(int stage) {
        return switch (Mth.clamp(stage, 0, 4)) {
            case 0 -> STAGE_1;
            case 1 -> STAGE_2;
            case 2 -> STAGE_3;
            case 3 -> STAGE_4;
            default -> STAGE_5;
        };
    }

    public static ItemStack generate(Random rand, int lootLevel) {
        ItemStack stack = new ItemStack(itemProvider(lootLevel).roll(rand));
        if (rand.nextFloat() < 0.5F + 0.1F * lootLevel) {
            return EnchantmentHelper.enchantItem(rand, stack, 10 + 3 * lootLevel, lootLevel > 2);
        }
        return stack;
    }

    /**
     * Creates a shield item stack with random patterns.
     */
    public static ItemStack createShield(Random rand, int lootLevel) {
        ItemStack shield = new ItemStack(Items.SHIELD);
        lootLevel = Mth.clamp(lootLevel, 0, 4);
        float f = rand.nextFloat();
        if (f < 0.12F + lootLevel * 0.02F) {
            shield.enchant(Enchantments.UNBREAKING, UNBREAKING_LEVELS[Mth.clamp(lootLevel, 0, 4)].nextInt(rand));
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
