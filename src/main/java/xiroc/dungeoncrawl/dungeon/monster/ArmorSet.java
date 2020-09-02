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

package xiroc.dungeoncrawl.dungeon.monster;

import net.minecraft.util.ResourceLocation;
import xiroc.dungeoncrawl.config.JsonConfig;

public class ArmorSet {

    public ResourceLocation[] items;
    public String[] resourceNames;

    public ArmorSet() {
        resourceNames = new String[4];
    }

    public ArmorSet(String... armor) {
        this();
        for (int i = 0; i < 4; i++)
            resourceNames[i] = armor[i];
    }

    public void build() {
        items = JsonConfig.toResourceLocationArray(resourceNames);
    }

    public static void buildAll(ArmorSet[] armorSets) {
        for (ArmorSet armorSet : armorSets)
            armorSet.build();
    }

}
