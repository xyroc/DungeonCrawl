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

import com.google.common.collect.ImmutableSet;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Registry;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.RandomSource;
import net.minecraft.util.Tuple;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.entity.RandomizableContainerBlockEntity;
import net.minecraft.world.level.storage.loot.BuiltInLootTables;
import net.minecraft.world.level.storage.loot.functions.LootItemFunctionType;
import xiroc.dungeoncrawl.DungeonCrawl;
import xiroc.dungeoncrawl.dungeon.treasure.function.EnchantedBook;
import xiroc.dungeoncrawl.dungeon.treasure.function.MaterialBlocks;
import xiroc.dungeoncrawl.dungeon.treasure.function.RandomItem;
import xiroc.dungeoncrawl.dungeon.treasure.function.RandomPotion;
import xiroc.dungeoncrawl.dungeon.treasure.function.Shield;
import xiroc.dungeoncrawl.dungeon.treasure.function.SuspiciousStew;
import xiroc.dungeoncrawl.theme.SecondaryTheme;
import xiroc.dungeoncrawl.theme.Theme;

public class Loot {

    public static final String LOOT_LEVEL = "loot_level";

    /* ************************************************************************************** */
    /*                                    LOOT FUNCTIONS                                      */
    /* ************************************************************************************** */

    public static final LootItemFunctionType ENCHANTED_BOOK = new LootItemFunctionType(new EnchantedBook.Serializer());
    public static final LootItemFunctionType MATERIAL_BLOCKS = new LootItemFunctionType(new MaterialBlocks.Serializer());
    public static final LootItemFunctionType RANDOM_ITEM = new LootItemFunctionType(new RandomItem.Serializer());
    public static final LootItemFunctionType RANDOM_POTION = new LootItemFunctionType(new RandomPotion.Serializer());
    public static final LootItemFunctionType SHIELD = new LootItemFunctionType(new Shield.Serializer());
    public static final LootItemFunctionType SUSPICIOUS_STEW = new LootItemFunctionType(new SuspiciousStew.Serializer());

    /* ************************************************************************************** */
    /*                                      LOOT TABLES                                       */
    /* ************************************************************************************** */

    public static final ResourceLocation CHEST_FOOD = DungeonCrawl.locate("chests/food");
    public static final ResourceLocation CHEST_SECRET_ROOM = DungeonCrawl.locate("chests/secret_room");
    public static final ResourceLocation CHEST_SUPPLY = DungeonCrawl.locate("chests/supply");
    public static final ResourceLocation CHEST_TREASURE = DungeonCrawl.locate("chests/treasure");

    public static final ResourceLocation CHEST_STAGE_1 = DungeonCrawl.locate("chests/stage_1");
    public static final ResourceLocation CHEST_STAGE_2 = DungeonCrawl.locate("chests/stage_2");
    public static final ResourceLocation CHEST_STAGE_3 = DungeonCrawl.locate("chests/stage_3");
    public static final ResourceLocation CHEST_STAGE_4 = DungeonCrawl.locate("chests/stage_4");
    public static final ResourceLocation CHEST_STAGE_5 = DungeonCrawl.locate("chests/stage_5");

    public static final ImmutableSet<ResourceLocation> ALL_LOOT_TABLES = ImmutableSet.<ResourceLocation>builder()
            .add(CHEST_FOOD)
            .add(CHEST_SECRET_ROOM)
            .add(CHEST_SUPPLY)
            .add(CHEST_TREASURE)
            .add(CHEST_STAGE_1)
            .add(CHEST_STAGE_2)
            .add(CHEST_STAGE_3)
            .add(CHEST_STAGE_4)
            .add(CHEST_STAGE_5).build();

    /* ************************************************************************************** */
    /*                                   ENTITY LOOT TABLES                                   */
    /* ************************************************************************************** */

    public static final ResourceLocation WITHER_SKELETON = DungeonCrawl.locate("monster_overrides/wither_skeleton");

        private static void registerLootFunctionType(ResourceLocation registryName, LootItemFunctionType type) {
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

    public static void setLoot(LevelAccessor world, BlockPos pos, RandomizableContainerBlockEntity tile, ResourceLocation lootTable, Theme theme, SecondaryTheme secondaryTheme, RandomSource rand) {
        RandomizableContainerBlockEntity.setLootTable(world, rand, pos, lootTable);
        setLootInformation(tile.getTileData(), theme, secondaryTheme);
    }

    public static ResourceLocation getLootTable(int lootLevel, RandomSource rand) {
        return switch (lootLevel) {
            case 0 -> rand.nextFloat() < 0.1 ? BuiltInLootTables.JUNGLE_TEMPLE : CHEST_STAGE_1;
            case 1 -> rand.nextFloat() < 0.1 ? BuiltInLootTables.SIMPLE_DUNGEON : CHEST_STAGE_2;
            case 2 -> rand.nextFloat() < 0.1 ? BuiltInLootTables.SIMPLE_DUNGEON : CHEST_STAGE_3;
            case 3 -> rand.nextFloat() < 0.1 ? BuiltInLootTables.STRONGHOLD_CROSSING : CHEST_STAGE_4;
            case 4 -> rand.nextFloat() < 0.1 ? BuiltInLootTables.STRONGHOLD_CROSSING : CHEST_STAGE_5;
            default -> Loot.CHEST_STAGE_5;
        };
    }

    public static void setLootInformation(CompoundTag nbt, Theme theme, SecondaryTheme secondaryTheme) {
        CompoundTag data = new CompoundTag();
        data.putString("theme", theme.getKey().toString());
        data.putString("secondaryTheme", secondaryTheme.getKey().toString());
        nbt.put(DungeonCrawl.MOD_ID, data);
    }

    public static Tuple<Theme, SecondaryTheme> getLootInformation(CompoundTag nbt) {
        CompoundTag data = nbt.getCompound(DungeonCrawl.MOD_ID);
        return new Tuple<>(Theme.getTheme(new ResourceLocation(data.getString("theme"))), Theme.getSecondaryTheme(new ResourceLocation(data.getString("secondaryTheme"))));
    }

}
