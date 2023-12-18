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
import com.mojang.serialization.Codec;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.RandomSource;
import net.minecraft.util.Tuple;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.entity.RandomizableContainerBlockEntity;
import net.minecraft.world.level.storage.loot.BuiltInLootTables;
import net.minecraft.world.level.storage.loot.functions.LootItemFunction;
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

public interface Loot {

    LootItemFunctionType ENCHANTED_BOOK = register("enchanted_book", EnchantedBook.CODEC);
    LootItemFunctionType MATERIAL_BLOCKS = register("material_blocks", MaterialBlocks.CODEC);
    LootItemFunctionType RANDOM_ITEM = register("random_item", RandomItem.CODEC);
    LootItemFunctionType RANDOM_POTION = register("random_potion", RandomPotion.CODEC);
    LootItemFunctionType SHIELD = register("shield", Shield.CODEC);
    LootItemFunctionType SUSPICIOUS_STEW = register("suspicious_stew", SuspiciousStew.CODEC);

    String KEY_LOOT_LEVEL = "loot_level";

    ResourceLocation CHEST_FOOD = DungeonCrawl.locate("chests/food");
    ResourceLocation CHEST_SECRET_ROOM = DungeonCrawl.locate("chests/secret_room");
    ResourceLocation CHEST_SUPPLY = DungeonCrawl.locate("chests/supply");
    ResourceLocation CHEST_TREASURE = DungeonCrawl.locate("chests/treasure");

    ResourceLocation CHEST_STAGE_1 = DungeonCrawl.locate("chests/stage_1");
    ResourceLocation CHEST_STAGE_2 = DungeonCrawl.locate("chests/stage_2");
    ResourceLocation CHEST_STAGE_3 = DungeonCrawl.locate("chests/stage_3");
    ResourceLocation CHEST_STAGE_4 = DungeonCrawl.locate("chests/stage_4");
    ResourceLocation CHEST_STAGE_5 = DungeonCrawl.locate("chests/stage_5");

    ImmutableSet<ResourceLocation> ALL_LOOT_TABLES = ImmutableSet.<ResourceLocation>builder()
            .add(CHEST_FOOD)
            .add(CHEST_SECRET_ROOM)
            .add(CHEST_SUPPLY)
            .add(CHEST_TREASURE)
            .add(CHEST_STAGE_1)
            .add(CHEST_STAGE_2)
            .add(CHEST_STAGE_3)
            .add(CHEST_STAGE_4)
            .add(CHEST_STAGE_5).build();

    ResourceLocation WITHER_SKELETON = DungeonCrawl.locate("monster_overrides/wither_skeleton");

    static void init() {
    }

    private static LootItemFunctionType register(String name, Codec<? extends LootItemFunction> codec) {
        return Registry.register(BuiltInRegistries.LOOT_FUNCTION_TYPE, DungeonCrawl.locate(name), new LootItemFunctionType(codec));
    }

    static void setLoot(LevelAccessor world, BlockPos pos, RandomizableContainerBlockEntity tile, ResourceLocation lootTable, Theme theme, SecondaryTheme secondaryTheme, RandomSource rand) {
        RandomizableContainerBlockEntity.setLootTable(world, rand, pos, lootTable);
        setLootInformation(tile.getPersistentData(), theme, secondaryTheme);
    }

    static ResourceLocation getLootTable(int lootLevel, RandomSource rand) {
        return switch (lootLevel) {
            case 0 -> rand.nextFloat() < 0.1 ? BuiltInLootTables.JUNGLE_TEMPLE : CHEST_STAGE_1;
            case 1 -> rand.nextFloat() < 0.1 ? BuiltInLootTables.SIMPLE_DUNGEON : CHEST_STAGE_2;
            case 2 -> rand.nextFloat() < 0.1 ? BuiltInLootTables.SIMPLE_DUNGEON : CHEST_STAGE_3;
            case 3 -> rand.nextFloat() < 0.1 ? BuiltInLootTables.STRONGHOLD_CROSSING : CHEST_STAGE_4;
            case 4 -> rand.nextFloat() < 0.1 ? BuiltInLootTables.STRONGHOLD_CROSSING : CHEST_STAGE_5;
            default -> Loot.CHEST_STAGE_5;
        };
    }

    static void setLootInformation(CompoundTag nbt, Theme theme, SecondaryTheme secondaryTheme) {
        CompoundTag data = new CompoundTag();
        data.putString("theme", theme.getKey().toString());
        data.putString("secondaryTheme", secondaryTheme.getKey().toString());
        nbt.put(DungeonCrawl.MOD_ID, data);
    }

    static Tuple<Theme, SecondaryTheme> getLootInformation(CompoundTag nbt) {
        CompoundTag data = nbt.getCompound(DungeonCrawl.MOD_ID);
        return new Tuple<>(Theme.getTheme(new ResourceLocation(data.getString("theme"))), Theme.getSecondaryTheme(new ResourceLocation(data.getString("secondaryTheme"))));
    }

}
