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
import xiroc.dungeoncrawl.dungeon.block.provider.BlockStateProvider;
import xiroc.dungeoncrawl.theme.Theme;

import javax.annotation.Nullable;
import java.util.Random;
import java.util.function.BiFunction;

public class PlacementBehaviour {

    public static final PlacementBehaviour NON_SOLID = new PlacementBehaviour((world, pos, rand) -> false);
    public static final PlacementBehaviour SOLID = new PlacementBehaviour((world, pos, rand) -> true);
    public static final PlacementBehaviour RANDOM_IF_SOLID_NEARBY = new PlacementBehaviour((world, pos, rand)
            -> {
        if (isSolid(world, pos.north()) || isSolid(world, pos.east()) || isSolid(world, pos.south()) || isSolid(world, pos.west())) {
            return rand.nextFloat() < 0.6F;
        } else {
            return false;
        }
    });

    private final PlacementFunction function;
    @Nullable
    public final BiFunction<Theme, Theme.SecondaryTheme, BlockStateProvider> airBlock;

    public PlacementBehaviour(PlacementFunction function) {
        this(function, null);
    }

    public PlacementBehaviour(PlacementFunction function, BiFunction<Theme, Theme.SecondaryTheme, BlockStateProvider> airBlock) {
        this.function = function;
        this.airBlock = airBlock;
    }

    public PlacementBehaviour withAirBlock(BiFunction<Theme, Theme.SecondaryTheme, BlockStateProvider> airBlock) {
        return new PlacementBehaviour(this.function, airBlock);
    }

    public boolean isSolid(LevelAccessor world, BlockPos pos, Random rand) {
        return function.isSolid(world, pos, rand);
    }

    private static boolean isSolid(LevelAccessor world, BlockPos pos) {
        if (world.hasChunk(pos.getX() >> 4, pos.getZ() >> 4)) {
            return world.getBlockState(pos).canOcclude() || world.getBlockState(pos.below()).canOcclude();
        } else {
            return false;
        }
    }

    public interface PlacementFunction {

        boolean isSolid(LevelAccessor world, BlockPos pos, Random rand);

    }

}
