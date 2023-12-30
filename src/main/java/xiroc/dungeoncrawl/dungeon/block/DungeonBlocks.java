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

import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.Property;
import xiroc.dungeoncrawl.util.WeightedRandom;

public class DungeonBlocks {

    public static final Block[] CARPET = new Block[]{Blocks.ORANGE_CARPET, Blocks.MAGENTA_CARPET,
            Blocks.LIGHT_BLUE_CARPET, Blocks.YELLOW_CARPET, Blocks.LIME_CARPET, Blocks.PINK_CARPET,
            Blocks.CYAN_CARPET, Blocks.BLUE_CARPET, Blocks.PURPLE_CARPET, Blocks.GREEN_CARPET,
            Blocks.BROWN_CARPET, Blocks.RED_CARPET};

    public static final WeightedRandom<Block> CROPS = new WeightedRandom.Builder<Block>()
            .add(Blocks.POTATOES, 2)
            .add(Blocks.WHEAT, 2)
            .add(Blocks.CARROTS, 1)
            .add(Blocks.BEETROOTS, 1)
            .build();

    public static final WeightedRandom<Block> POTTED_FLOWERS = new WeightedRandom.Builder<Block>()
            .add(Blocks.POTTED_ACACIA_SAPLING, 1)
            .add(Blocks.POTTED_ALLIUM, 1)
            .add(Blocks.POTTED_AZALEA, 1)
            .add(Blocks.POTTED_AZURE_BLUET, 1)
            .add(Blocks.POTTED_BAMBOO, 1)
            .add(Blocks.POTTED_BIRCH_SAPLING, 1)
            .add(Blocks.POTTED_BLUE_ORCHID, 1)
            .add(Blocks.POTTED_BROWN_MUSHROOM, 1)
            .add(Blocks.POTTED_CACTUS, 1)
            .add(Blocks.POTTED_CORNFLOWER, 1)
            .add(Blocks.POTTED_CHERRY_SAPLING, 1)
            .add(Blocks.POTTED_DANDELION, 1)
            .add(Blocks.POTTED_DARK_OAK_SAPLING, 1)
            .add(Blocks.POTTED_DEAD_BUSH, 1)
            .add(Blocks.POTTED_FERN, 1)
            .add(Blocks.POTTED_JUNGLE_SAPLING, 1)
            .add(Blocks.POTTED_LILY_OF_THE_VALLEY, 1)
            .add(Blocks.POTTED_OAK_SAPLING, 1)
            .add(Blocks.POTTED_ORANGE_TULIP, 1)
            .add(Blocks.POTTED_OXEYE_DAISY, 1)
            .add(Blocks.POTTED_POPPY, 1)
            .add(Blocks.POTTED_PINK_TULIP, 1)
            .add(Blocks.POTTED_RED_MUSHROOM, 1)
            .add(Blocks.POTTED_RED_TULIP, 1)
            .add(Blocks.POTTED_SPRUCE_SAPLING, 1)
            .add(Blocks.POTTED_WHITE_TULIP, 1)
            .build();

    public static final WeightedRandom<Block> TALL_FLOWERS = new WeightedRandom.Builder<Block>()
            .add(Blocks.ROSE_BUSH, 1)
            .add(Blocks.LILAC, 1)
            .add(Blocks.PEONY, 1)
            .build();

    public static final BlockState SPAWNER = Blocks.SPAWNER.defaultBlockState();
    public static final BlockState CHEST = Blocks.CHEST.defaultBlockState();

    public static final BlockState CAVE_AIR = Blocks.CAVE_AIR.defaultBlockState();

    public static <T extends Comparable<T>, V extends T> BlockState applyProperty(BlockState state, Property<T> property, V value) {
        if (state.hasProperty(property)) {
            return state.setValue(property, value);
        }
        return state;
    }

}
