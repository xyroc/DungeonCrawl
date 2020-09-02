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

package xiroc.dungeoncrawl.util;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.JsonToNBT;
import xiroc.dungeoncrawl.DungeonCrawl;


public class BossEntry {

    public String entityName, nbt;

    public BossEntry(String entityName, String nbt) {
        this.entityName = entityName;
        this.nbt = nbt;
    }

    public CompoundNBT createTag() {
        if (nbt == null)
            return null;
        try {
            return new JsonToNBT(new StringReader(nbt)).readStruct();
        } catch (CommandSyntaxException e) {
            DungeonCrawl.LOGGER.error("Failed to read a boss nbt tag.");
            e.printStackTrace();
            return null;
        }
    }

}
