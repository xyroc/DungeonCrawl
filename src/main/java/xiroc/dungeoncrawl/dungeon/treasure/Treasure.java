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

import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.storage.loot.Serializer;
import net.minecraft.world.level.storage.loot.functions.LootItemFunction;
import net.minecraft.world.level.storage.loot.functions.LootItemFunctionType;
import xiroc.dungeoncrawl.dungeon.treasure.function.EnchantedBook;
import xiroc.dungeoncrawl.dungeon.treasure.function.MaterialBlocks;
import xiroc.dungeoncrawl.dungeon.treasure.function.RandomItem;
import xiroc.dungeoncrawl.dungeon.treasure.function.RandomPotion;
import xiroc.dungeoncrawl.dungeon.treasure.function.Shield;
import xiroc.dungeoncrawl.dungeon.treasure.function.SuspiciousStew;

import java.util.HashMap;

public class Treasure {

    public static final LootItemFunctionType ENCHANTED_BOOK = register("dungeoncrawl:enchanted_book", new EnchantedBook.Serializer());
    public static final LootItemFunctionType MATERIAL_BLOCKS = register("dungeoncrawl:material_blocks", new MaterialBlocks.Serializer());
    public static final LootItemFunctionType RANDOM_ITEM = register("dungeoncrawl:random_item", new RandomItem.Serializer());
    public static final LootItemFunctionType RANDOM_POTION = register("dungeoncrawl:random_potion", new RandomPotion.Serializer());
    public static final LootItemFunctionType SHIELD = register("dungeoncrawl:shield", new Shield.Serializer());
    public static final LootItemFunctionType SUSPICIOUS_STEW = register("dungeoncrawl:suspicious_stew", new SuspiciousStew.Serializer());

    public static final HashMap<Treasure.Type, ResourceLocation> SPECIAL_LOOT_TABLES = new HashMap<>();

    public static void init() {
        SPECIAL_LOOT_TABLES.put(Type.SUPPLY, Loot.SUPPLY_CHEST);
        SPECIAL_LOOT_TABLES.put(Type.FOOD, Loot.FOOD);
        SPECIAL_LOOT_TABLES.put(Type.TREASURE, Loot.TREASURE_ROOM);
        SPECIAL_LOOT_TABLES.put(Type.LIBRARY, Loot.LIBRARY);
        SPECIAL_LOOT_TABLES.put(Type.SECRET_ROOM, Loot.SECRET_ROOM);
        SPECIAL_LOOT_TABLES.put(Type.FORGE, Loot.FORGE);
    }

    /**
     * An enum to determine which LootTable should be used for chests, barrels,
     * etc...
     */
    public enum Type {

        DEFAULT, FOOD, FORGE, CATACOMB, SECRET_ROOM, LIBRARY, BUILDERS_ROOM, TREASURE, SUPPLY;

        public static final HashMap<Integer, Type> INT_TO_TYPE_MAP;
        public static final HashMap<Type, Integer> TYPE_TO_INT_MAP;

        static {
            INT_TO_TYPE_MAP = new HashMap<>();
            INT_TO_TYPE_MAP.put(0, DEFAULT);
            INT_TO_TYPE_MAP.put(1, FOOD);
            INT_TO_TYPE_MAP.put(2, FORGE);
            INT_TO_TYPE_MAP.put(3, CATACOMB);
            INT_TO_TYPE_MAP.put(4, SECRET_ROOM);
            INT_TO_TYPE_MAP.put(5, LIBRARY);
            INT_TO_TYPE_MAP.put(6, BUILDERS_ROOM);
            INT_TO_TYPE_MAP.put(7, TREASURE);
            INT_TO_TYPE_MAP.put(8, SUPPLY);

            TYPE_TO_INT_MAP = new HashMap<>();
            INT_TO_TYPE_MAP.forEach((key, value) -> TYPE_TO_INT_MAP.put(value, key));
        }

    }

    private static LootItemFunctionType register(String p_237451_0_, Serializer<? extends LootItemFunction> p_237451_1_) {
        return Registry.register(Registry.LOOT_FUNCTION_TYPE, new ResourceLocation(p_237451_0_), new LootItemFunctionType(p_237451_1_));
    }

}
