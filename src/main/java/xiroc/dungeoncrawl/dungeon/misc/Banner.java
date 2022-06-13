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

import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.util.RandomSource;

public class Banner {

    private static final int PATTERNS = 3;

    private static final String[] BANNER_PATTERNS = new String[]{"gru", "bl", "br", "bri", "tr", "bs", "ts", "ls",
            "rs", "bl", "tl", "hh", "vhr", "hhb", "bo", "cbo", "gra", "bts", "tts", "ld", "rd", "lud", "rud", "mc",
            "mr", "vh", "dls", "cs", "ms", "drs", "ss", "cr", "sc", "bt", "tt"};

    private static final String[] BANNER_PATTERNS_FINAL = new String[]{"sku", "cre"};

    private static final String[] BANNER_PATTERNS_FINAL_RARE = new String[]{"glb", "flo"};

    /**
     * Creates the BlockEntityTag for banners or shields which contains a list of
     * random patterns.
     */
    public static CompoundTag createPatterns(RandomSource rand) {
        CompoundTag blockEntityTag = new CompoundTag();
        ListTag patterns = new ListTag();
        for (int i = 0; i < PATTERNS; i++) {
            CompoundTag pattern = new CompoundTag();
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

}
