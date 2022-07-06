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

package xiroc.dungeoncrawl.dungeon.misc;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.Random;

public class Banner {

    private static final int PATTERNS = 3;

    private static final ResourceLocation[] BANNERS = new ResourceLocation[]{
            new ResourceLocation("minecraft:white_banner"), new ResourceLocation("minecraft:red_banner"),
            new ResourceLocation("minecraft:green_banner"), new ResourceLocation("minecraft:blue_banner"),
            new ResourceLocation("minecraft:yellow_banner"), new ResourceLocation("minecraft:light_blue_banner"),
            new ResourceLocation("minecraft:orange_banner"), new ResourceLocation("minecraft:gray_banner"),
            new ResourceLocation("minecraft:light_gray_banner"), new ResourceLocation("minecraft:pink_banner"),
            new ResourceLocation("minecraft:magenta_banner"), new ResourceLocation("minecraft:cyan_banner"),
            new ResourceLocation("minecraft:purple_banner"), new ResourceLocation("minecraft:brown_banner"),
            new ResourceLocation("minecraft:lime_banner"), new ResourceLocation("minecraft:black_banner")};

    private static final String[] BANNER_PATTERNS = new String[]{"gru", "bl", "br", "bri", "tr", "bs", "ts", "ls",
            "rs", "bl", "tl", "hh", "vhr", "hhb", "bo", "cbo", "gra", "bts", "tts", "ld", "rd", "lud", "rud", "mc",
            "mr", "vh", "dls", "cs", "ms", "drs", "ss", "cr", "sc", "bt", "tt"};

    private static final String[] BANNER_PATTERNS_FINAL = new String[]{"sku", "cre"};

    private static final String[] BANNER_PATTERNS_FINAL_RARE = new String[]{"glb", "flo"};

    /**
     * Creates the BlockEntityTag for banners or shields which contains a list of
     * random patterns.
     */
    public static CompoundNBT createPatterns(Random rand) {
        CompoundNBT blockEntityTag = new CompoundNBT();
        ListNBT patterns = new ListNBT();
        for (int i = 0; i < PATTERNS; i++) {
            CompoundNBT pattern = new CompoundNBT();
            pattern.putInt("Color", rand.nextInt(15));
            pattern.putString("Pattern",
                    i == PATTERNS - 1 && rand.nextDouble() < 0.25
                            ? (rand.nextDouble() < 0.05
                            ? BANNER_PATTERNS_FINAL_RARE[rand.nextInt(BANNER_PATTERNS_FINAL_RARE.length)]
                            : BANNER_PATTERNS_FINAL[rand.nextInt(BANNER_PATTERNS_FINAL.length)])
                            : BANNER_PATTERNS[rand.nextInt(BANNER_PATTERNS.length)]);
            patterns.add(pattern);
        }
        blockEntityTag.put("Patterns", patterns);
        return blockEntityTag;
    }

    /**
     * Creates a banner item stack with random patterns.
     */
    public static ItemStack createBanner(Random rand) {
        ItemStack banner = new ItemStack(ForgeRegistries.ITEMS.getValue(BANNERS[rand.nextInt(BANNERS.length)]));
        CompoundNBT nbt = new CompoundNBT();
        nbt.put("BlockEntityTag", createPatterns(rand));
        banner.setTag(nbt);
        return banner;
    }

}
