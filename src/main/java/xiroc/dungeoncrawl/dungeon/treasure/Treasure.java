package xiroc.dungeoncrawl.dungeon.treasure;

/*
 * DungeonCrawl (C) 2019 - 2020 XYROC (XIROC1337), All Rights Reserved
 */

import net.minecraft.loot.ILootSerializer;
import net.minecraft.loot.LootFunctionType;
import net.minecraft.loot.LootTables;
import net.minecraft.loot.functions.ILootFunction;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.registry.Registry;
import xiroc.dungeoncrawl.dungeon.treasure.function.EnchantedBook;
import xiroc.dungeoncrawl.dungeon.treasure.function.*;

import java.util.HashMap;

public class Treasure {

    public static final HashMap<Treasure.Type, ResourceLocation> SPECIAL_LOOT_TABLES;
    public static final HashMap<Integer, Treasure.Type> MODEL_TREASURE_TYPES;

    public static final LootFunctionType ENCHANTED_BOOK = func_237451_a_("dungeoncrawl:enchanted_book", new EnchantedBook.Serializer());
    public static final LootFunctionType MATERIAL_BLOCKS = func_237451_a_("dungeoncrawl:material_blocks", new EnchantedBook.Serializer());
    public static final LootFunctionType RANDOM_ITEM = func_237451_a_("dungeoncrawl:random_item", new RandomItem.Serializer());
    public static final LootFunctionType RANDOM_POTION = func_237451_a_("dungeoncrawl:random_potion", new RandomPotion.Serializer());
    public static final LootFunctionType SHIELD = func_237451_a_("dungeoncrawl:shield", new Shield.Serializer());
    public static final LootFunctionType SUSPICIOUS_STEW = func_237451_a_("dungeoncrawl:suspicious_stew", new SuspiciousStew.Serializer());

    static {
        SPECIAL_LOOT_TABLES = new HashMap<>();

        MODEL_TREASURE_TYPES = new HashMap<>();

        MODEL_TREASURE_TYPES.put(32, Type.FORGE);

        MODEL_TREASURE_TYPES.put(34, Type.LIBRARY);
        MODEL_TREASURE_TYPES.put(35, Type.TREASURE);

        MODEL_TREASURE_TYPES.put(70, Type.SECRET_ROOM);
    }

    public static void init() {
        SPECIAL_LOOT_TABLES.put(Type.SUPPLY, Loot.SUPPLY_CHEST);
        SPECIAL_LOOT_TABLES.put(Type.KITCHEN, Loot.KITCHEN_CHEST);
        SPECIAL_LOOT_TABLES.put(Type.TREASURE, Loot.TREASURE_ROOM);
        SPECIAL_LOOT_TABLES.put(Type.LIBRARY, Loot.LIBRARY);
        SPECIAL_LOOT_TABLES.put(Type.SECRET_ROOM, Loot.SECRET_ROOM);
        SPECIAL_LOOT_TABLES.put(Type.FORGE, LootTables.CHESTS_VILLAGE_VILLAGE_WEAPONSMITH);

    }

    /**
     * An enum to determine which LootTable should be used for chests, barrels,
     * etc...
     */
    public enum Type {

        DEFAULT, KITCHEN, FORGE, CATACOMB, SECRET_ROOM, LIBRARY, BUILDERS_ROOM, TREASURE, SUPPLY;

        public static final HashMap<Integer, Type> INT_TO_TYPE_MAP;
        public static final HashMap<Type, Integer> TYPE_TO_INT_MAP;

        static {
            INT_TO_TYPE_MAP = new HashMap<>();
            INT_TO_TYPE_MAP.put(0, DEFAULT);
            INT_TO_TYPE_MAP.put(1, KITCHEN);
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

    private static LootFunctionType func_237451_a_(String p_237451_0_, ILootSerializer<? extends ILootFunction> p_237451_1_) {
        return Registry.register(Registry.LOOT_FUNCTION_TYPE, new ResourceLocation(p_237451_0_), new LootFunctionType(p_237451_1_));
    }

}
