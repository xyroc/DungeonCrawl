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

package xiroc.dungeoncrawl.dungeon.block;

import net.minecraft.block.BlockState;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.tileentity.LockableLootTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IWorld;
import net.minecraft.world.storage.loot.LootTables;
import xiroc.dungeoncrawl.DungeonCrawl;
import xiroc.dungeoncrawl.dungeon.PlacementContext;
import xiroc.dungeoncrawl.dungeon.treasure.Loot;
import xiroc.dungeoncrawl.dungeon.treasure.Treasure;
import xiroc.dungeoncrawl.theme.Theme;
import xiroc.dungeoncrawl.util.IBlockPlacementHandler;
import xiroc.dungeoncrawl.util.Orientation;

import java.util.Random;

public class Chest implements IBlockPlacementHandler {

    @Override
    public void place(IWorld world, BlockState state, BlockPos pos, Random rand, PlacementContext context,
                      Treasure.Type treasureType, Theme theme, Theme.SubTheme subTheme, int lootLevel) {
        world.setBlockState(pos, state, 3);
        if (world.getTileEntity(pos) instanceof LockableLootTileEntity) {
            setLootTable(world, pos, rand, treasureType, theme, subTheme, lootLevel);
        } else {
            DungeonCrawl.LOGGER.warn("Failed to fetch a chest/barrel entity at {}", pos.toString());
        }
    }

    public static ResourceLocation getLootTable(int lootLevel, Random rand) {
        switch (lootLevel) {
            case 0:
                return rand.nextFloat() < 0.1 ? LootTables.CHESTS_JUNGLE_TEMPLE : Loot.CHEST_STAGE_1;
            case 1:
                return rand.nextFloat() < 0.1 ? LootTables.CHESTS_SIMPLE_DUNGEON : Loot.CHEST_STAGE_2;
            case 2:
                return rand.nextFloat() < 0.1 ? LootTables.CHESTS_SIMPLE_DUNGEON : Loot.CHEST_STAGE_3;
            case 3:
                return rand.nextFloat() < 0.1 ? LootTables.CHESTS_STRONGHOLD_CROSSING : Loot.CHEST_STAGE_4;
            case 4:
                return rand.nextFloat() < 0.1 ? LootTables.CHESTS_STRONGHOLD_CROSSING : Loot.CHEST_STAGE_5;
            default:
                return Loot.CHEST_STAGE_5;
        }
    }

    private static void setLootTable(IWorld world, BlockPos pos, Random rand, Treasure.Type treasureType,
                                     Theme theme, Theme.SubTheme subTheme, int lootLevel) {
        ResourceLocation lootTable = Treasure.SPECIAL_LOOT_TABLES.get(treasureType);
        LockableLootTileEntity.setLootTable(world, world.getRandom(), pos,
                lootTable == null ? getLootTable(lootLevel, rand) : lootTable);

        // Provide context for the loot functions
        TileEntity tileEntity = world.getTileEntity(pos);
        if (tileEntity != null) {
            Loot.setLootInformation(tileEntity.getTileData(), theme, subTheme);
        }
    }

    public static class TrappedChest implements IBlockPlacementHandler {

        @Override
        public void place(IWorld world, BlockState state, BlockPos pos, Random rand, PlacementContext context,
                          Treasure.Type treasureType, Theme theme,
                          Theme.SubTheme subTheme, int lootLevel) {
            if (state.has(BlockStateProperties.HORIZONTAL_FACING))
                state = state.with(BlockStateProperties.HORIZONTAL_FACING, Orientation.RANDOM_HORIZONTAL_FACING.roll(rand));
            world.setBlockState(pos, state, 3);
            if (world.getTileEntity(pos) instanceof LockableLootTileEntity) {
                setLootTable(world, pos, rand, treasureType, theme, subTheme, lootLevel);
            } else {
                DungeonCrawl.LOGGER.warn("Failed to fetch a trapped chest entity at {}", pos.toString());
            }
        }

    }

}
