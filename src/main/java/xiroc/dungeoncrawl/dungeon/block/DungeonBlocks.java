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

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.state.IProperty;
import net.minecraft.util.Tuple;

public class DungeonBlocks {

    public static final Block[] CARPET = new Block[]{Blocks.WHITE_CARPET, Blocks.ORANGE_CARPET, Blocks.MAGENTA_CARPET,
            Blocks.LIGHT_BLUE_CARPET, Blocks.YELLOW_CARPET, Blocks.LIME_CARPET, Blocks.PINK_CARPET, Blocks.GRAY_CARPET,
            Blocks.LIGHT_GRAY_CARPET, Blocks.CYAN_CARPET, Blocks.BLUE_CARPET, Blocks.PURPLE_CARPET, Blocks.GREEN_CARPET,
            Blocks.BROWN_CARPET, Blocks.RED_CARPET, Blocks.BLACK_CARPET};

    public static final BlockState CAVE_AIR = Blocks.CAVE_AIR.getDefaultState();
    public static final BlockState SPAWNER = Blocks.SPAWNER.getDefaultState();
    public static final BlockState CHEST = Blocks.CHEST.getDefaultState();

    public static final BlockState STONE_BRICKS = Blocks.STONE_BRICKS.getDefaultState();

    public static WeightedRandomBlock STONE_BRICKS_NORMAL_CRACKED_COBBLESTONE;
    public static WeightedRandomBlock STONE_BRICK_FLOOR;
    public static WeightedRandomBlock STONE_BRICKS_GRAVEL_COBBLESTONE;
    public static WeightedRandomBlock STAIRS_STONE_COBBLESTONE;

    public static WeightedRandomBlock STONE_WALL;

    /*
     * Calculate the WeightedRandomBlocks
     */
    public static void init() {
        STONE_BRICKS_NORMAL_CRACKED_COBBLESTONE = new WeightedRandomBlock(new TupleIntBlock[]{
                new TupleIntBlock(5, STONE_BRICKS), new TupleIntBlock(2, Blocks.CRACKED_STONE_BRICKS.getDefaultState()),
                new TupleIntBlock(2, Blocks.COBBLESTONE.getDefaultState()), new TupleIntBlock(1, Blocks.MOSSY_STONE_BRICKS.getDefaultState())});

        STONE_BRICK_FLOOR = new WeightedRandomBlock(
                new TupleIntBlock[]{new TupleIntBlock(8, Blocks.STONE_BRICKS.getDefaultState()),
                        new TupleIntBlock(2, Blocks.CRACKED_STONE_BRICKS.getDefaultState()),
                        new TupleIntBlock(2, Blocks.COBBLESTONE.getDefaultState()),
                        new TupleIntBlock(1, Blocks.MOSSY_STONE_BRICKS.getDefaultState())});

        STONE_BRICKS_GRAVEL_COBBLESTONE = new WeightedRandomBlock(new TupleIntBlock[]{new TupleIntBlock(2, STONE_BRICKS),
                new TupleIntBlock(1, Blocks.GRAVEL.getDefaultState()),
                new TupleIntBlock(1, Blocks.COBBLESTONE.getDefaultState()), new TupleIntBlock(1, Blocks.MOSSY_COBBLESTONE.getDefaultState()),
                new TupleIntBlock(1, Blocks.MOSSY_STONE_BRICKS.getDefaultState())});

        STONE_WALL = new WeightedRandomBlock(
                new TupleIntBlock[]{new TupleIntBlock(1, Blocks.STONE_BRICK_WALL.getDefaultState()),
                        new TupleIntBlock(1, Blocks.COBBLESTONE_WALL.getDefaultState()),
                        new TupleIntBlock(1, Blocks.ANDESITE_WALL.getDefaultState()),
                        new TupleIntBlock(1, Blocks.MOSSY_STONE_BRICK_WALL.getDefaultState()),
                        new TupleIntBlock(1, Blocks.MOSSY_COBBLESTONE_WALL.getDefaultState()),
                        new TupleIntBlock(1, Blocks.DIORITE_WALL.getDefaultState())});
    }

    public static <T extends Comparable<T>, V extends T> BlockState applyProperty(BlockState state, IProperty<T> property, V value) {
        if (state.has(property)) {
            return state.with(property, value);
        }
        return state;
    }

    public static final class TupleIntBlock extends Tuple<Integer, BlockState> {

        public TupleIntBlock(Integer aIn, BlockState bIn) {
            super(aIn, bIn);
        }

    }

    public static final class TupleFloatBlock extends Tuple<Float, BlockState> {

        public TupleFloatBlock(Float aIn, BlockState bIn) {
            super(aIn, bIn);
        }

    }

}
