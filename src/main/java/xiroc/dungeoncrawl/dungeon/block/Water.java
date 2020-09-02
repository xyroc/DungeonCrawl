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
import net.minecraft.block.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IWorld;
import xiroc.dungeoncrawl.dungeon.treasure.Treasure.Type;
import xiroc.dungeoncrawl.util.IBlockPlacementHandler;

import java.util.Random;

public class Water implements IBlockPlacementHandler {

    private static final BlockState LAVA = Blocks.LAVA.getDefaultState();

    @Override
    public void placeBlock(IWorld world, BlockState state, BlockPos pos, Random rand, Type treasureType, int theme,
                           int lootLevel) {
        if (theme == 1)
            world.setBlockState(pos, LAVA, 3);
        else
            world.setBlockState(pos, state, 3);

    }

}
