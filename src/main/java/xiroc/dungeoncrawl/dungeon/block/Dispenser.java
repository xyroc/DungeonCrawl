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
import net.minecraft.tileentity.DispenserTileEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IWorld;
import xiroc.dungeoncrawl.DungeonCrawl;
import xiroc.dungeoncrawl.dungeon.treasure.Loot;
import xiroc.dungeoncrawl.dungeon.treasure.Treasure;
import xiroc.dungeoncrawl.util.IBlockPlacementHandler;

import java.util.Random;

public class Dispenser implements IBlockPlacementHandler {

    @Override
    public void placeBlock(IWorld world, BlockState state, BlockPos pos, Random rand, Treasure.Type treasureType,
                           int theme, int lootLevel) {
        world.setBlockState(pos, state, 3);
        if (world.getTileEntity(pos) instanceof DispenserTileEntity) {
            DispenserTileEntity dispenser = (DispenserTileEntity) world.getTileEntity(pos);
            dispenser.setLootTable(getLootTable(lootLevel), rand.nextLong());
        } else
            DungeonCrawl.LOGGER.warn("Failed to fetch a dispenser entity at {}", pos.toString());
    }

    public static ResourceLocation getLootTable(int lootLevel) {
        switch (lootLevel) {
            case 0:
                return Loot.DISPENSER_STAGE_1;
            case 1:
                return Loot.DISPENSER_STAGE_2;
            default:
                return Loot.DISPENSER_STAGE_3;
        }
    }

}
