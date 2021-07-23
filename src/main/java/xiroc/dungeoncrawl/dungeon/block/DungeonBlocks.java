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
import net.minecraft.state.Property;
import net.minecraft.util.Tuple;


import java.util.Random;

public class DungeonBlocks {

    public static final Random RANDOM = new Random();

    public static final Block[] CARPET = new Block[]{Blocks.ORANGE_CARPET, Blocks.MAGENTA_CARPET,
            Blocks.LIGHT_BLUE_CARPET, Blocks.YELLOW_CARPET, Blocks.LIME_CARPET, Blocks.PINK_CARPET,
            Blocks.CYAN_CARPET, Blocks.BLUE_CARPET, Blocks.PURPLE_CARPET, Blocks.GREEN_CARPET,
            Blocks.BROWN_CARPET, Blocks.RED_CARPET};

    public static final BlockState SPAWNER = Blocks.SPAWNER.getDefaultState();
    public static final BlockState CHEST = Blocks.CHEST.getDefaultState();

    public static final BlockState CAVE_AIR = Blocks.CAVE_AIR.getDefaultState();

    public static <T extends Comparable<T>, V extends T> BlockState applyProperty(BlockState state, Property<T> property, V value) {
        if (state.hasProperty(property)) {
            return state.with(property, value);
        }
        return state;
    }

}
