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
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.state.properties.DoubleBlockHalf;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IWorld;
import xiroc.dungeoncrawl.dungeon.PlacementContext;
import xiroc.dungeoncrawl.dungeon.treasure.Treasure;
import xiroc.dungeoncrawl.theme.Theme;
import xiroc.dungeoncrawl.util.IBlockPlacementHandler;

import java.util.Random;

public class Plants {

    public static class Farmland implements IBlockPlacementHandler {

        @Override
        public void place(IWorld world, BlockState state, BlockPos pos, Random rand, PlacementContext context,
                          Treasure.Type treasureType, Theme theme, Theme.SubTheme subTheme, int lootLevel) {
            state = state.with(BlockStateProperties.MOISTURE_0_7, 7);
            world.setBlockState(pos, state, 2);
            BlockPos cropPos = new BlockPos(pos.getX(), pos.getY() + 1, pos.getZ());
            if (rand.nextFloat() < 0.6) {
                BlockState crop = BlockTags.CROPS.getRandomElement(rand).getDefaultState();
                if (crop.hasProperty(BlockStateProperties.AGE_0_7))
                    crop = crop.with(BlockStateProperties.AGE_0_7, rand.nextInt(8));
                world.setBlockState(cropPos, crop, 2);
                context.protectedBlocks.add(cropPos);
            } else {
                world.setBlockState(cropPos, Blocks.CAVE_AIR.getDefaultState(), 2);
            }
        }

    }

    public static class FlowerPot implements IBlockPlacementHandler {

        @Override
        public void place(IWorld world, BlockState state, BlockPos pos, Random rand, PlacementContext context,
                          Treasure.Type treasureType, Theme theme, Theme.SubTheme subTheme, int lootLevel) {
            world.setBlockState(pos, BlockTags.FLOWER_POTS.getRandomElement(rand).getDefaultState(), 2);
        }

    }

    public static class Podzol implements IBlockPlacementHandler {

        @Override
        public void place(IWorld world, BlockState state, BlockPos pos, Random rand, PlacementContext context,
                          Treasure.Type treasureType, Theme theme, Theme.SubTheme subTheme, int lootLevel) {
            world.setBlockState(pos, state, 2);
            BlockState flower = BlockTags.TALL_FLOWERS.getRandomElement(rand).getDefaultState();
            BlockPos lowerPart = pos.up();
            BlockPos upperPart = lowerPart.up();
            world.setBlockState(lowerPart, DungeonBlocks.applyProperty(flower, BlockStateProperties.DOUBLE_BLOCK_HALF, DoubleBlockHalf.LOWER), 2);
            world.setBlockState(upperPart, DungeonBlocks.applyProperty(flower, BlockStateProperties.DOUBLE_BLOCK_HALF, DoubleBlockHalf.UPPER), 2);
            context.protectedBlocks.add(lowerPart);
            context.protectedBlocks.add(upperPart);
        }

    }

}
