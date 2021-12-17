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

import net.minecraft.util.Rotation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IWorld;
import xiroc.dungeoncrawl.DungeonCrawl;
import xiroc.dungeoncrawl.dungeon.block.provider.BlockStateProvider;
import xiroc.dungeoncrawl.theme.SecondaryTheme;
import xiroc.dungeoncrawl.theme.Theme;

import javax.annotation.Nullable;
import java.util.Random;
import java.util.function.BiFunction;

public class PlacementBehaviour {

    public static final PlacementBehaviour NON_SOLID = new PlacementBehaviour((world, pos, rotation, rand) -> false);
    public static final PlacementBehaviour SOLID = new PlacementBehaviour((world, pos, rotation, rand) -> true);
    public static final PlacementBehaviour RANDOM_IF_SOLID_NEARBY = new PlacementBehaviour((world, pos, rotation, rand) -> {
        if (isSolid(world, pos.north()) || isSolid(world, pos.east()) || isSolid(world, pos.south()) || isSolid(world, pos.west())) {
            return rand.nextFloat() < 0.6F;
        } else {
            return false;
        }
    });
    public static PlacementBehaviour STRIPES = new PlacementBehaviour((world, pos, rotation, rand) -> {
        if (rotation == Rotation.NONE || rotation == Rotation.CLOCKWISE_180) {
            switch (pos.getZ() % 3) {
                case 0:
                    return rand.nextFloat() < 0.9F;
                case 1:
                case -1:
                    return rand.nextFloat() < 0.5F;
                case 2:
                case -2:
                    return rand.nextFloat() < 0.25F;
                default:
                    return false;
            }
        } else {
            switch (pos.getX() % 3) {
                case 0:
                    return rand.nextFloat() < 0.9F;
                case 1:
                case -1:
                    return rand.nextFloat() < 0.5F;
                case 2:
                case -2:
                    return rand.nextFloat() < 0.25F;
                default:
                    return false;
            }
        }
    });

    public static PlacementBehaviour SMALL_GRID = new PlacementBehaviour((world, pos, rotation, rand) ->
            rand.nextFloat() < (0.95F - (Math.abs(pos.getX() % 3) * 0.2F) - (Math.abs(pos.getZ() % 3) * 0.2F)));

    public static PlacementBehaviour LARGE_GRID = new PlacementBehaviour((world, pos, rotation, rand) ->
            rand.nextFloat() < (1F - (Math.abs(pos.getX() % 6) * 0.1F) - (Math.abs(pos.getZ() % 6) * 0.1F)));

    private final PlacementFunction function;
    @Nullable
    public final BiFunction<Theme, SecondaryTheme, BlockStateProvider> airBlock;

    public PlacementBehaviour(PlacementFunction function) {
        this(function, null);
    }

    public PlacementBehaviour(PlacementFunction function, BiFunction<Theme, SecondaryTheme, BlockStateProvider> airBlock) {
        this.function = function;
        this.airBlock = airBlock;
    }

    public PlacementBehaviour withAirBlock(BiFunction<Theme, SecondaryTheme, BlockStateProvider> airBlock) {
        return new PlacementBehaviour(this.function, airBlock);
    }

    public boolean isSolid(IWorld world, BlockPos pos, Rotation pieceRotation, Random rand) {
        return function.isSolid(world, pos, pieceRotation, rand);
    }

    private static boolean isSolid(IWorld world, BlockPos pos) {
        if (world.hasChunk(pos.getX() >> 4, pos.getZ() >> 4)) {
            return world.getBlockState(pos).canOcclude() || world.getBlockState(pos.below()).canOcclude();
        } else {
            return false;
        }
    }

    public interface PlacementFunction {

        boolean isSolid(IWorld world, BlockPos pos, Rotation rotation, Random rand);

    }

}
