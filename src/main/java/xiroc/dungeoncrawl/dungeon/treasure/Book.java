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

import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.nbt.StringNBT;
import xiroc.dungeoncrawl.dungeon.DungeonStatTracker;

import java.util.ArrayList;

public class Book {

    public static ItemStack createStatBook(String title, DungeonStatTracker statTracker) {
        ItemStack book = new ItemStack(Items.WRITTEN_BOOK);
        CompoundNBT tag = new CompoundNBT();
        ListNBT pages = new ListNBT();
        pages.add(StringNBT.valueOf("----  Statistics  ----\n  Objectives: " + statTracker.totalObjectives + "\n  Layers: "
                + statTracker.stats.length + "\n  Chests: " + statTracker.chests + "\n  Spawners: "
                + statTracker.spawners));
        ArrayList<String> lines = statTracker.getObjectives();
        if (lines.size() > 14) {
            for (int i = 0; i < lines.size() / 14 + 1; i++)
                pages.add(createStatPage(lines, i * 14));
        } else {
            pages.add(createStatPage(lines, 0));
        }
        tag.put("pages", pages);
        tag.putString("author", "XIROC");
        tag.putString("title", title);
        tag.putBoolean("resolved", true);
        book.setTag(tag);
        return book;
    }

    public static StringNBT createStatPage(ArrayList<String> lines, int start) {
        String text = "";
        for (int i = start; i < Math.max(lines.size(), 14); i++)
            text += lines.get(i) + "\n";
        return StringNBT.valueOf(text);
    }

}
