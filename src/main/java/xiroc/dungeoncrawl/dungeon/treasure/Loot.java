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
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.RandomSource;
import net.minecraft.util.Tuple;
import net.minecraft.world.RandomizableContainer;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.entity.RandomizableContainerBlockEntity;
import net.minecraft.world.level.storage.loot.BuiltInLootTables;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.functions.LootItemFunctionType;
import net.neoforged.neoforge.registries.DeferredHolder;
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

    DeferredHolder<LootItemFunctionType<?>, ?> ENCHANTED_BOOK = DungeonCrawl.LOOT_FUNCTION_TYPE.register("enchanted_book",
            () -> new LootItemFunctionType<>(EnchantedBook.CODEC));
    DeferredHolder<LootItemFunctionType<?>, ?> MATERIAL_BLOCKS = DungeonCrawl.LOOT_FUNCTION_TYPE.register("material_blocks",
            () -> new LootItemFunctionType<>(MaterialBlocks.CODEC));
    DeferredHolder<LootItemFunctionType<?>, ?> RANDOM_ITEM = DungeonCrawl.LOOT_FUNCTION_TYPE.register("random_item",
            () -> new LootItemFunctionType<>(RandomItem.CODEC));
    DeferredHolder<LootItemFunctionType<?>, ?> RANDOM_POTION = DungeonCrawl.LOOT_FUNCTION_TYPE.register("random_potion",
            () -> new LootItemFunctionType<>(RandomPotion.CODEC));
    DeferredHolder<LootItemFunctionType<?>, ?> SHIELD = DungeonCrawl.LOOT_FUNCTION_TYPE.register("shield",
            () -> new LootItemFunctionType<>(Shield.CODEC));
    DeferredHolder<LootItemFunctionType<?>, ?> SUSPICIOUS_STEW = DungeonCrawl.LOOT_FUNCTION_TYPE.register("suspicious_stew",
            () -> new LootItemFunctionType<>(SuspiciousStew.CODEC));

    String KEY_LOOT_LEVEL = "loot_level";

    ResourceKey<LootTable> CHEST_FOOD = lootTable("chests/food");
    ResourceKey<LootTable> CHEST_SECRET_ROOM = lootTable("chests/secret_room");
    ResourceKey<LootTable> CHEST_SUPPLY = lootTable("chests/supply");
    ResourceKey<LootTable> CHEST_TREASURE = lootTable("chests/treasure");

    ResourceKey<LootTable> CHEST_STAGE_1 = lootTable("chests/stage_1");
    ResourceKey<LootTable> CHEST_STAGE_2 = lootTable("chests/stage_2");
    ResourceKey<LootTable> CHEST_STAGE_3 = lootTable("chests/stage_3");
    ResourceKey<LootTable> CHEST_STAGE_4 = lootTable("chests/stage_4");
    ResourceKey<LootTable> CHEST_STAGE_5 = lootTable("chests/stage_5");

    ResourceKey<LootTable> WITHER_SKELETON = lootTable("monster_overrides/wither_skeleton");

    ImmutableSet<ResourceKey<LootTable>> ALL_LOOT_TABLES = ImmutableSet.<ResourceKey<LootTable>>builder()
            .add(CHEST_FOOD)
            .add(CHEST_SECRET_ROOM)
            .add(CHEST_SUPPLY)
            .add(CHEST_TREASURE)
            .add(CHEST_STAGE_1)
            .add(CHEST_STAGE_2)
            .add(CHEST_STAGE_3)
            .add(CHEST_STAGE_4)
            .add(CHEST_STAGE_5).build();

    private static ResourceKey<LootTable> lootTable(String path) {
        return ResourceKey.create(Registries.LOOT_TABLE, DungeonCrawl.locate(path));
    }

    static void init() {
    }

    static void setLoot(LevelAccessor world, BlockPos pos, RandomizableContainerBlockEntity tile, ResourceKey<LootTable> lootTable, Theme theme, SecondaryTheme secondaryTheme,
                        RandomSource rand) {
        RandomizableContainer.setBlockEntityLootTable(world, rand, pos, lootTable);
        setLootInformation(tile.getPersistentData(), theme, secondaryTheme);
    }

    static ResourceKey<LootTable> getLootTable(int lootLevel, RandomSource rand) {
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
        return new Tuple<>(Theme.getTheme(ResourceLocation.parse(data.getString("theme"))), Theme.getSecondaryTheme(ResourceLocation.parse(data.getString("secondaryTheme"))));
    }

}
