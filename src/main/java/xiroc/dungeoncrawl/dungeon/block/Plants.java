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

import net.minecraft.core.BlockPos;
import net.minecraft.core.Registry;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.DoubleBlockHalf;
import xiroc.dungeoncrawl.theme.SecondaryTheme;
import xiroc.dungeoncrawl.theme.Theme;
import xiroc.dungeoncrawl.util.IBlockPlacementHandler;

import java.util.Random;

public class Plants {

    public static class Farmland implements IBlockPlacementHandler {

        @Override
        public void place(LevelAccessor world, BlockState state, BlockPos pos, Random rand, Theme theme, SecondaryTheme secondaryTheme,
                          int lootLevel, boolean worldGen) {
            state = state.setValue(BlockStateProperties.MOISTURE, 7);
            world.setBlock(pos, state, 2);
            BlockPos cropPos = new BlockPos(pos.getX(), pos.getY() + 1, pos.getZ());
            if (rand.nextFloat() < 0.6) {
                Registry.BLOCK.getTag(BlockTags.CROPS).flatMap(crops -> crops.getRandomElement(rand)).ifPresent((cropBlock) -> {
                    BlockState crop = cropBlock.value().defaultBlockState();
                    if (crop.hasProperty(BlockStateProperties.AGE_7))
                        crop = crop.setValue(BlockStateProperties.AGE_7, 4 + rand.nextInt(4));
                    world.setBlock(cropPos, crop, 2);
                });
            } else {
                world.setBlock(cropPos, Blocks.CAVE_AIR.defaultBlockState(), 2);
            }
        }

    }

    public static class FlowerPot implements IBlockPlacementHandler {

        @Override
        public void place(LevelAccessor world, BlockState state, BlockPos pos, Random rand, Theme theme, SecondaryTheme secondaryTheme,
                          int lootLevel, boolean worldGen) {
            Registry.BLOCK.getTag(BlockTags.FLOWER_POTS).flatMap((pots) -> pots.getRandomElement(rand)).ifPresent((pot) -> world.setBlock(pos, pot.value().defaultBlockState(), 2));
        }

    }

    public static class Podzol implements IBlockPlacementHandler {

        @Override
        public void place(LevelAccessor world, BlockState state, BlockPos pos, Random rand, Theme theme, SecondaryTheme secondaryTheme,
                          int lootLevel, boolean worldGen) {
            world.setBlock(pos, state, 2);
            Registry.BLOCK.getTag(BlockTags.TALL_FLOWERS).flatMap((flowers) -> flowers.getRandomElement(rand)).ifPresent((flowerBlock) -> {
                BlockState flower = flowerBlock.value().defaultBlockState();
                BlockPos lowerPart = pos.above();
                BlockPos upperPart = lowerPart.above();
                world.setBlock(lowerPart, DungeonBlocks.applyProperty(flower, BlockStateProperties.DOUBLE_BLOCK_HALF, DoubleBlockHalf.LOWER), 2);
                world.setBlock(upperPart, DungeonBlocks.applyProperty(flower, BlockStateProperties.DOUBLE_BLOCK_HALF, DoubleBlockHalf.UPPER), 2);
            });

        }

    }

}
