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

package xiroc.dungeoncrawl.theme;

import net.minecraft.util.ResourceLocation;

import java.util.Random;

public class ThemeItems {

    private static final Random RANDOM = new Random();

    public static ResourceLocation getMaterial(int theme, int subTheme) {
        return RANDOM.nextBoolean() ? Theme.get(theme).material.get().getBlock().getRegistryName()
                : Theme.getSub(subTheme).material.get().getBlock().getRegistryName();
    }

}
