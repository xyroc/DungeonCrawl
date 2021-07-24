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
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import xiroc.dungeoncrawl.dungeon.DungeonStatTracker;

import java.util.ArrayList;

public class Book {

    public static ItemStack createStatBook(String title, DungeonStatTracker statTracker) {
        ItemStack book = new ItemStack(Items.WRITTEN_BOOK);
        CompoundTag tag = new CompoundTag();
        ListTag pages = new ListTag();
        pages.add(StringTag.valueOf("----  Statistics  ----\n  Objectives: " + statTracker.totalObjectives + "\n  Layers: "
                + statTracker.stats.length + "\n  Chests: " + statTracker.chests + "\n  Spawners: "
                + statTracker.spawners));
        ArrayList<String> lines = statTracker.getObjectives();
        if (lines.size() > 14) {
            for (int i = 0; i < lines.size() / 14 + 1; i++)
                pages.add(createPage(lines, i * 14));
        } else {
            pages.add(createPage(lines, 0));
        }
        tag.put("pages", pages);
        tag.putString("author", "XIROC");
        tag.putString("title", title);
        tag.putBoolean("resolved", true);
        book.setTag(tag);
        return book;
    }

    public static StringTag createPage(ArrayList<String> lines, int start) {
        StringBuilder text = new StringBuilder();
        for (int i = start; i < Math.max(lines.size(), 14); i++)
            text.append(lines.get(i)).append("\n");
        return StringTag.valueOf(text.toString());
    }

}
