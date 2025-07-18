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
import xiroc.dungeoncrawl.data.SharedKeys;
import xiroc.dungeoncrawl.dungeon.block.provider.RandomBlock;
import xiroc.dungeoncrawl.dungeon.block.provider.SingleBlock;
import xiroc.dungeoncrawl.dungeon.theme.SecondaryTheme;
import xiroc.dungeoncrawl.util.random.IRandom;

public interface SecondaryThemes {
    static void generate(BootstrapContext<SecondaryTheme> context) {
        context.register(SharedKeys.Theme.Secondary.OAK, SecondaryTheme.builder()
                .material(new SingleBlock(Blocks.OAK_PLANKS))
                .pillar(new SingleBlock(Blocks.OAK_LOG))
                .stairs(new SingleBlock(Blocks.OAK_STAIRS))
                .slab(new SingleBlock(Blocks.OAK_SLAB))
                .door(new RandomBlock(new IRandom.Builder<BlockState>()
                        .add(Blocks.OAK_DOOR.defaultBlockState())
                        .add(Blocks.SPRUCE_DOOR.defaultBlockState())
                        .build()))
                .button(new SingleBlock(Blocks.OAK_BUTTON))
                .fence(new SingleBlock(Blocks.OAK_FENCE))
                .fenceGate(new SingleBlock(Blocks.OAK_FENCE_GATE))
                .pressurePlate(new SingleBlock(Blocks.OAK_PRESSURE_PLATE))
                .trapdoor(new SingleBlock(Blocks.OAK_TRAPDOOR))
                .build());
    }
}