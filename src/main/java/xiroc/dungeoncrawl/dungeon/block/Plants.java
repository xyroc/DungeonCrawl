package xiroc.dungeoncrawl.dungeon.block;

/*
 * DungeonCrawl (C) 2019 - 2020 XYROC (XIROC1337), All Rights Reserved
 */

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.state.properties.DoubleBlockHalf;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IWorld;
import xiroc.dungeoncrawl.dungeon.treasure.Treasure.Type;
import xiroc.dungeoncrawl.util.IBlockPlacementHandler;

import java.util.Random;

public class Plants {

    public static class Farmland implements IBlockPlacementHandler {

        public static final Block[] CROPS = new Block[]{Blocks.WHEAT, Blocks.POTATOES, Blocks.CARROTS,
                Blocks.BEETROOTS, Blocks.MELON_STEM, Blocks.PUMPKIN_STEM, Blocks.CAVE_AIR};

        @Override
        public void placeBlock(IWorld world, BlockState state, BlockPos pos, Random rand, Type treasureType, int theme,
                               int lootLevel) {

            BlockPos cropPos = new BlockPos(pos.getX(), pos.getY() + 1, pos.getZ());
            if (theme == 1) {
                state = Blocks.SOUL_SAND.getDefaultState();
                world.setBlockState(pos, state, 3);

                BlockState netherWart = Blocks.NETHER_WART.getDefaultState();
                if (netherWart.has(BlockStateProperties.AGE_0_3))
                    netherWart = netherWart.with(BlockStateProperties.AGE_0_3, rand.nextInt(3));
                world.setBlockState(cropPos, netherWart, 3);

                return;
            }
            state = state.with(BlockStateProperties.MOISTURE_0_7, 7);
            world.setBlockState(pos, state, 3);
            BlockState crop = CROPS[rand.nextInt(CROPS.length)].getDefaultState();
            if (crop.has(BlockStateProperties.AGE_0_7))
                crop = crop.with(BlockStateProperties.AGE_0_7, rand.nextInt(8));
            world.setBlockState(cropPos, crop, 3);
        }

    }

    public static class FlowerPot implements IBlockPlacementHandler {

        public static final Block[] POTTED_FLOWERS = new Block[]{Blocks.POTTED_ALLIUM, Blocks.POTTED_AZURE_BLUET,
                Blocks.POTTED_BAMBOO, Blocks.POTTED_BLUE_ORCHID, Blocks.POTTED_BROWN_MUSHROOM, Blocks.POTTED_CACTUS,
                Blocks.POTTED_CORNFLOWER, Blocks.POTTED_DANDELION, Blocks.POTTED_DEAD_BUSH, Blocks.POTTED_FERN,
                Blocks.POTTED_LILY_OF_THE_VALLEY, Blocks.POTTED_ORANGE_TULIP, Blocks.POTTED_OXEYE_DAISY,
                Blocks.POTTED_PINK_TULIP, Blocks.POTTED_POPPY, Blocks.POTTED_RED_MUSHROOM, Blocks.POTTED_RED_TULIP,
                Blocks.POTTED_WHITE_TULIP};

        @Override
        public void placeBlock(IWorld world, BlockState state, BlockPos pos, Random rand, Type treasureType, int theme,
                               int lootLevel) {
            world.setBlockState(pos, POTTED_FLOWERS[rand.nextInt(POTTED_FLOWERS.length)].getDefaultState(), 3);
        }

    }

    public static class Podzol implements IBlockPlacementHandler {

        public static final Block[] LARGE_FLOWERS = new Block[]{Blocks.LILAC, Blocks.ROSE_BUSH, Blocks.LARGE_FERN, Blocks.PEONY};

        @Override
        public void placeBlock(IWorld world, BlockState state, BlockPos pos, Random rand, Type treasureType, int theme, int lootLevel) {
            world.setBlockState(pos, state, 2);
            BlockState flower = LARGE_FLOWERS[rand.nextInt(LARGE_FLOWERS.length)].getDefaultState();
            world.setBlockState(pos.up(), DungeonBlocks.applyProperty(flower, BlockStateProperties.DOUBLE_BLOCK_HALF, DoubleBlockHalf.LOWER), 2);
            world.setBlockState(pos.up(), DungeonBlocks.applyProperty(flower, BlockStateProperties.DOUBLE_BLOCK_HALF, DoubleBlockHalf.UPPER), 2);
        }
    }

}
