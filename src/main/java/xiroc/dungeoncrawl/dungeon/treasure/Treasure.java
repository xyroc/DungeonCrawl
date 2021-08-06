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

import net.minecraft.loot.LootFunctionType;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.registry.Registry;
import xiroc.dungeoncrawl.DungeonCrawl;
import xiroc.dungeoncrawl.dungeon.treasure.function.EnchantedBook;
import xiroc.dungeoncrawl.dungeon.treasure.function.MaterialBlocks;
import xiroc.dungeoncrawl.dungeon.treasure.function.RandomItem;
import xiroc.dungeoncrawl.dungeon.treasure.function.RandomPotion;
import xiroc.dungeoncrawl.dungeon.treasure.function.Shield;
import xiroc.dungeoncrawl.dungeon.treasure.function.SuspiciousStew;

public class Treasure {

    public static final LootFunctionType ENCHANTED_BOOK = new LootFunctionType(new EnchantedBook.Serializer());
    public static final LootFunctionType MATERIAL_BLOCKS = new LootFunctionType(new MaterialBlocks.Serializer());
    public static final LootFunctionType RANDOM_ITEM = new LootFunctionType(new RandomItem.Serializer());
    public static final LootFunctionType RANDOM_POTION = new LootFunctionType(new RandomPotion.Serializer());
    public static final LootFunctionType SHIELD = new LootFunctionType(new Shield.Serializer());
    public static final LootFunctionType SUSPICIOUS_STEW = new LootFunctionType(new SuspiciousStew.Serializer());

    private static void register(ResourceLocation registryName, LootFunctionType type) {
        Registry.register(Registry.LOOT_FUNCTION_TYPE, registryName, type);
    }

    public static void init() {
        register(DungeonCrawl.locate("enchanted_book"), ENCHANTED_BOOK);
        register(DungeonCrawl.locate("material_blocks"), MATERIAL_BLOCKS);
        register(DungeonCrawl.locate("random_item"), RANDOM_ITEM);
        register(DungeonCrawl.locate("random_potion"), RANDOM_POTION);
        register(DungeonCrawl.locate("shield"), SHIELD);
        register(DungeonCrawl.locate("suspicious_stew"), SUSPICIOUS_STEW);
    }

}
