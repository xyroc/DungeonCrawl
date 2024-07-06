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

import net.minecraft.core.Direction;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.Property;

public interface DungeonBlocks {
    MetaBlock TRAPDOOR_OPEN_NORTH = new MetaBlock(Blocks.OAK_TRAPDOOR.defaultBlockState()
            .setValue(BlockStateProperties.OPEN, true)
            .setValue(BlockStateProperties.HORIZONTAL_FACING, Direction.NORTH));
    MetaBlock TRAPDOOR_OPEN_EAST = new MetaBlock(Blocks.OAK_TRAPDOOR.defaultBlockState()
            .setValue(BlockStateProperties.OPEN, true)
            .setValue(BlockStateProperties.HORIZONTAL_FACING, Direction.EAST));
    MetaBlock TRAPDOOR_OPEN_SOUTH = new MetaBlock(Blocks.OAK_TRAPDOOR.defaultBlockState()
            .setValue(BlockStateProperties.OPEN, true)
            .setValue(BlockStateProperties.HORIZONTAL_FACING, Direction.NORTH));
    MetaBlock TRAPDOOR_OPEN_WEST = new MetaBlock(Blocks.OAK_TRAPDOOR.defaultBlockState()
            .setValue(BlockStateProperties.OPEN, true)
            .setValue(BlockStateProperties.HORIZONTAL_FACING, Direction.NORTH));

    static <T extends Comparable<T>, V extends T> BlockState applyProperty(BlockState state, Property<T> property, V value) {
        if (state.hasProperty(property)) {
            return state.setValue(property, value);
        }
        return state;
    }
}
