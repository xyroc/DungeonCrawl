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

import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Tuple;
import xiroc.dungeoncrawl.DungeonCrawl;
import xiroc.dungeoncrawl.theme.Theme;

public class Loot {

    /* ************************************************************************************** */
    /*                                      LOOT TABLES                                       */
    /* ************************************************************************************** */

    public static final ResourceLocation CHEST_STAGE_1 = DungeonCrawl.locate("chests/stage_1");
    public static final ResourceLocation CHEST_STAGE_2 = DungeonCrawl.locate("chests/stage_2");
    public static final ResourceLocation CHEST_STAGE_3 = DungeonCrawl.locate("chests/stage_3");
    public static final ResourceLocation CHEST_STAGE_4 = DungeonCrawl.locate("chests/stage_4");
    public static final ResourceLocation CHEST_STAGE_5 = DungeonCrawl.locate("chests/stage_5");

    public static final ResourceLocation FORGE = DungeonCrawl.locate("chests/forge");
    public static final ResourceLocation FOOD = DungeonCrawl.locate("chests/food");
    public static final ResourceLocation TREASURE_ROOM = DungeonCrawl.locate("chests/treasure_room");
    public static final ResourceLocation SUPPLY_CHEST = DungeonCrawl.locate("chests/supply_chest");
    public static final ResourceLocation LIBRARY = DungeonCrawl.locate("chests/library");
    public static final ResourceLocation SECRET_ROOM = DungeonCrawl.locate("chests/secret_room");

    public static final ResourceLocation DISPENSER_STAGE_1 = DungeonCrawl.locate("misc/dispenser_1");
    public static final ResourceLocation DISPENSER_STAGE_2 = DungeonCrawl.locate("misc/dispenser_2");
    public static final ResourceLocation DISPENSER_STAGE_3 = DungeonCrawl.locate("misc/dispenser_3");

    /* ************************************************************************************** */
    /*                                   ENTITY LOOT TABLES                                   */
    /* ************************************************************************************** */

    public static final ResourceLocation WITHER_SKELETON = DungeonCrawl.locate("monster_overrides/wither_skeleton");

    public static void setLootInformation(CompoundNBT nbt, Theme theme, Theme.SubTheme subTheme) {
        CompoundNBT data = new CompoundNBT();
        data.putString("theme", theme.getKey());
        data.putString("subTheme", subTheme.getKey());
        nbt.put("DungeonCrawl", data);
    }

    public static Tuple<Theme, Theme.SubTheme> getLootInformation(CompoundNBT nbt) {
        CompoundNBT data = nbt.getCompound("DungeonCrawl");
        return new Tuple<>(Theme.getTheme(data.getString("theme")), Theme.getSubTheme(data.getString("subTheme")));
    }

}
