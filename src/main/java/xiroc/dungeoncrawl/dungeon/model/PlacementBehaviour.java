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

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.LevelAccessor;

import java.util.Random;

public enum PlacementBehaviour {

    NON_SOLID((world, pos, rand, rx, ry, rz) -> false),
    RANDOM_IF_SOLID_NEARBY((world, pos, rand, rx, ry, rz)
            -> {
        if (isSolid(world, pos.north()) || isSolid(world, pos.east()) || isSolid(world, pos.south()) || isSolid(world, pos.west())) {
            return rand.nextFloat() < 0.6F;
        } else {
            return false;
        }
    }), SOLID((world, pos, rand, rx, ry, rz) -> true);

    public final PlacementFunction function;

    PlacementBehaviour(PlacementFunction function) {
        this.function = function;
    }

    private static boolean isSolid(LevelAccessor world, BlockPos pos) {
        if (world.hasChunk(pos.getX() >> 4, pos.getZ() >> 4)) {
            return world.getBlockState(pos).canOcclude() || world.getBlockState(pos.below()).canOcclude();
        } else {
            return false;
        }
    }

    public interface PlacementFunction {

        boolean isSolid(LevelAccessor world, BlockPos pos, Random rand, int relativeX, int relativeY, int relativeZ);

    }

}
