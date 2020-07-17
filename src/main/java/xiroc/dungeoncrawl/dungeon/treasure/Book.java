package xiroc.dungeoncrawl.dungeon.treasure;

/*
 * DungeonCrawl (C) 2019 - 2020 XYROC (XIROC1337), All Rights Reserved
 */

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
