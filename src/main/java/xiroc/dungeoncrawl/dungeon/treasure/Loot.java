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
import net.minecraft.loot.LootTables;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tileentity.LockableLootTileEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Tuple;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.IWorld;
import xiroc.dungeoncrawl.DungeonCrawl;
import xiroc.dungeoncrawl.dungeon.treasure.function.EnchantedBook;
import xiroc.dungeoncrawl.dungeon.treasure.function.MaterialBlocks;
import xiroc.dungeoncrawl.dungeon.treasure.function.RandomItem;
import xiroc.dungeoncrawl.dungeon.treasure.function.RandomPotion;
import xiroc.dungeoncrawl.dungeon.treasure.function.Shield;
import xiroc.dungeoncrawl.dungeon.treasure.function.SuspiciousStew;
import xiroc.dungeoncrawl.theme.SecondaryTheme;
import xiroc.dungeoncrawl.theme.Theme;

import java.util.Random;

public class Loot {

    public static final String LOOT_LEVEL = "loot_level";

    /* ************************************************************************************** */
    /*                                    LOOT FUNCTIONS                                      */
    /* ************************************************************************************** */

    public static final LootFunctionType ENCHANTED_BOOK = new LootFunctionType(new EnchantedBook.Serializer());
    public static final LootFunctionType MATERIAL_BLOCKS = new LootFunctionType(new MaterialBlocks.Serializer());
    public static final LootFunctionType RANDOM_ITEM = new LootFunctionType(new RandomItem.Serializer());
    public static final LootFunctionType RANDOM_POTION = new LootFunctionType(new RandomPotion.Serializer());
    public static final LootFunctionType SHIELD = new LootFunctionType(new Shield.Serializer());
    public static final LootFunctionType SUSPICIOUS_STEW = new LootFunctionType(new SuspiciousStew.Serializer());

    /* ************************************************************************************** */
    /*                                      LOOT TABLES                                       */
    /* ************************************************************************************** */

    public static final ResourceLocation CHEST_STAGE_1 = DungeonCrawl.locate("chests/stage_1");
    public static final ResourceLocation CHEST_STAGE_2 = DungeonCrawl.locate("chests/stage_2");
    public static final ResourceLocation CHEST_STAGE_3 = DungeonCrawl.locate("chests/stage_3");
    public static final ResourceLocation CHEST_STAGE_4 = DungeonCrawl.locate("chests/stage_4");
    public static final ResourceLocation CHEST_STAGE_5 = DungeonCrawl.locate("chests/stage_5");

    /* ************************************************************************************** */
    /*                                   ENTITY LOOT TABLES                                   */
    /* ************************************************************************************** */

    public static final ResourceLocation WITHER_SKELETON = DungeonCrawl.locate("monster_overrides/wither_skeleton");

    private static void registerLootFunctionType(ResourceLocation registryName, LootFunctionType type) {
        Registry.register(Registry.LOOT_FUNCTION_TYPE, registryName, type);
    }

    public static void init() {
        registerLootFunctionType(DungeonCrawl.locate("enchanted_book"), ENCHANTED_BOOK);
        registerLootFunctionType(DungeonCrawl.locate("material_blocks"), MATERIAL_BLOCKS);
        registerLootFunctionType(DungeonCrawl.locate("random_item"), RANDOM_ITEM);
        registerLootFunctionType(DungeonCrawl.locate("random_potion"), RANDOM_POTION);
        registerLootFunctionType(DungeonCrawl.locate("shield"), SHIELD);
        registerLootFunctionType(DungeonCrawl.locate("suspicious_stew"), SUSPICIOUS_STEW);
    }

    public static void setLoot(IWorld world, BlockPos pos, LockableLootTileEntity tile, ResourceLocation lootTable, Theme theme, SecondaryTheme secondaryTheme, Random rand) {
        LockableLootTileEntity.setLootTable(world, rand, pos, lootTable);
        setLootInformation(tile.getTileData(), theme, secondaryTheme);
    }

    public static ResourceLocation getLootTable(int lootLevel, Random rand) {
        switch (lootLevel) {
            case 0:
                return rand.nextFloat() < 0.1 ? LootTables.JUNGLE_TEMPLE : CHEST_STAGE_1;
            case 1:
                return rand.nextFloat() < 0.1 ? LootTables.SIMPLE_DUNGEON : CHEST_STAGE_2;
            case 2:
                return rand.nextFloat() < 0.1 ? LootTables.SIMPLE_DUNGEON : CHEST_STAGE_3;
            case 3:
                return rand.nextFloat() < 0.1 ? LootTables.STRONGHOLD_CROSSING : CHEST_STAGE_4;
            case 4:
                return rand.nextFloat() < 0.1 ? LootTables.STRONGHOLD_CROSSING : CHEST_STAGE_5;
            default:
                return Loot.CHEST_STAGE_5;
        }
    }

    public static void setLootInformation(CompoundNBT nbt, Theme theme, SecondaryTheme secondaryTheme) {
        CompoundNBT data = new CompoundNBT();
        data.putString("theme", theme.getKey().toString());
        data.putString("secondaryTheme", secondaryTheme.getKey().toString());
        nbt.put(DungeonCrawl.MOD_ID, data);
    }

    public static Tuple<Theme, SecondaryTheme> getLootInformation(CompoundNBT nbt) {
        CompoundNBT data = nbt.getCompound(DungeonCrawl.MOD_ID);
        return new Tuple<>(Theme.getTheme(new ResourceLocation(data.getString("theme"))), Theme.getSecondaryTheme(new ResourceLocation(data.getString("secondaryTheme"))));
    }

}
