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

package xiroc.dungeoncrawl.dungeon.model;

import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IWorld;

import java.util.Random;

public enum PlacementBehaviour {

    NON_SOLID((world, pos, rand, rx, ry, rz) -> false), RANDOM_IF_SOLID_NEARBY((world, pos, rand, rx, ry, rz) -> {
        if (world.getBlockState(pos.north()).isSolid() || world.getBlockState(pos.east()).isSolid()
                || world.getBlockState(pos.south()).isSolid() || world.getBlockState(pos.west()).isSolid()) {
            return rand.nextFloat() < 0.5;
        } else {
            return false;
        }
    }), SOLID((world, pos, rand, rx, ry, rz) -> true);

    public final PlacementFunction function;

    PlacementBehaviour(PlacementFunction function) {
        this.function = function;
    }

    public interface PlacementFunction {

        boolean isSolid(IWorld world, BlockPos pos, Random rand, int relativeX, int relativeY, int relativeZ);

    }

}
