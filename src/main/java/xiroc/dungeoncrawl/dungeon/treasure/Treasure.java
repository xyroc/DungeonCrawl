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

import net.minecraft.util.ResourceLocation;
import net.minecraft.world.storage.loot.functions.LootFunctionManager;
import xiroc.dungeoncrawl.dungeon.treasure.function.EnchantedBook;
import xiroc.dungeoncrawl.dungeon.treasure.function.*;

import java.util.HashMap;

public class Treasure {

    public static final HashMap<Treasure.Type, ResourceLocation> SPECIAL_LOOT_TABLES = new HashMap<>();

    public static void init() {
        SPECIAL_LOOT_TABLES.put(Type.SUPPLY, Loot.SUPPLY_CHEST);
        SPECIAL_LOOT_TABLES.put(Type.FOOD, Loot.FOOD);
        SPECIAL_LOOT_TABLES.put(Type.TREASURE, Loot.TREASURE_ROOM);
        SPECIAL_LOOT_TABLES.put(Type.LIBRARY, Loot.LIBRARY);
        SPECIAL_LOOT_TABLES.put(Type.SECRET_ROOM, Loot.SECRET_ROOM);
        SPECIAL_LOOT_TABLES.put(Type.FORGE, Loot.FORGE);

        LootFunctionManager.registerFunction(new RandomItem.Serializer());
        LootFunctionManager.registerFunction(new RandomPotion.Serializer());
        LootFunctionManager.registerFunction(new EnchantedBook.Serializer());
        LootFunctionManager.registerFunction(new MaterialBlocks.Serializer());
        LootFunctionManager.registerFunction(new Shield.Serializer());
        LootFunctionManager.registerFunction(new SuspiciousStew.Serializer());
        LootFunctionManager.registerFunction(new SpecialItem.Serializer());
    }

    /**
     * An enum to determine which LootTable should get used for chest, barrels,
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

        public static Type fromInt(int typeID) {
            return INT_TO_TYPE_MAP.getOrDefault(typeID, DEFAULT);
        }

        public static int toInt(Type type) {
            return TYPE_TO_INT_MAP.get(type);
        }

    }

}
