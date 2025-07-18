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

package xiroc.dungeoncrawl.data.themes;

import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.SlabType;
import xiroc.dungeoncrawl.data.SharedKeys;
import xiroc.dungeoncrawl.dungeon.block.provider.RandomBlock;
import xiroc.dungeoncrawl.dungeon.block.provider.SingleBlock;
import xiroc.dungeoncrawl.dungeon.theme.PrimaryTheme;
import xiroc.dungeoncrawl.util.random.IRandom;

public interface PrimaryThemes {
    static void generate(BootstrapContext<PrimaryTheme> context) {
        context.register(SharedKeys.Theme.Primary.FOREST, PrimaryTheme.builder()
                .masonry(new RandomBlock(new IRandom.Builder<BlockState>()
                        .add(Blocks.STONE_BRICKS.defaultBlockState(), 4)
                        .add(Blocks.CRACKED_STONE_BRICKS.defaultBlockState(), 2)
                        .add(Blocks.MOSSY_STONE_BRICKS.defaultBlockState())
                        .build()))
                .pillar(new RandomBlock(new IRandom.Builder<BlockState>()
                        .add(Blocks.CRACKED_STONE_BRICKS.defaultBlockState(), 3)
                        .add(Blocks.MOSSY_STONE_BRICKS.defaultBlockState())
                        .build()))
                .floor(new SingleBlock(Blocks.SMOOTH_STONE_SLAB.defaultBlockState().setValue(BlockStateProperties.SLAB_TYPE, SlabType.DOUBLE)))
                .stairs(new SingleBlock(Blocks.STONE_BRICK_STAIRS))
                .slab(new SingleBlock(Blocks.STONE_BRICK_SLAB))
                .fluid(new SingleBlock(Blocks.WATER))
                .fencing(new SingleBlock(Blocks.IRON_BARS))
                .wall(new SingleBlock(Blocks.MOSSY_STONE_BRICK_WALL))
                .build());
    }
}