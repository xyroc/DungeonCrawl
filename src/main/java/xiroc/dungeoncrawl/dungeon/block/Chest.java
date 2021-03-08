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
import net.minecraft.loot.LootTables;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.tileentity.LockableLootTileEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IWorld;
import xiroc.dungeoncrawl.DungeonCrawl;
import xiroc.dungeoncrawl.dungeon.treasure.Loot;
import xiroc.dungeoncrawl.dungeon.treasure.Treasure;
import xiroc.dungeoncrawl.dungeon.treasure.Treasure.Type;
import xiroc.dungeoncrawl.util.IBlockPlacementHandler;
import xiroc.dungeoncrawl.util.Orientation;

import java.util.Random;

public class Chest implements IBlockPlacementHandler {

    @Override
    public void placeBlock(IWorld world, BlockState state, BlockPos pos, Random rand, Treasure.Type treasureType,
                           int theme, int lootLevel) {
        world.setBlockState(pos, state, 3);
        if (world.getTileEntity(pos) instanceof LockableLootTileEntity) {
            ResourceLocation lootTable = Treasure.SPECIAL_LOOT_TABLES.get(treasureType);
            LockableLootTileEntity.setLootTable(world, world.getRandom(), pos,
                    lootTable == null ? getLootTable(lootLevel, rand) : lootTable);
        } else
            DungeonCrawl.LOGGER.warn("Failed to fetch a chest/barrel entity at {}", pos.toString());
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
                //DungeonCrawl.LOGGER.warn("Unknown Loot Level: {}", lootLevel);
                return Loot.CHEST_STAGE_5;
        }
    }

    public static class TrappedChest implements IBlockPlacementHandler {

        @Override
        public void placeBlock(IWorld world, BlockState state, BlockPos pos, Random rand, Type treasureType, int theme,
                               int lootLevel) {
            if (state.hasProperty(BlockStateProperties.HORIZONTAL_FACING)) {
                state = state.with(BlockStateProperties.HORIZONTAL_FACING, Orientation.RANDOM_HORIZONTAL_FACING.roll(rand));
            }
            world.setBlockState(pos, state, 3);
            if (world.getTileEntity(pos) instanceof LockableLootTileEntity) {
                ResourceLocation lootTable = Treasure.SPECIAL_LOOT_TABLES.get(treasureType);
                LockableLootTileEntity.setLootTable(world, world.getRandom(), pos,
                        lootTable == null ? getLootTable(lootLevel, rand) : lootTable);
            } else
                DungeonCrawl.LOGGER.warn("Failed to fetch a trapped chest entity at {}", pos.toString());
        }

    }

}
