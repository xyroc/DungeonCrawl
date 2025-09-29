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

package xiroc.dungeoncrawl.dungeon.decoration;

import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import xiroc.dungeoncrawl.dungeon.block.provider.BlockStateProvider;
import xiroc.dungeoncrawl.dungeon.blueprint.Blueprint;
import xiroc.dungeoncrawl.worldgen.WorldEditor;

public record ScatteredDecoration(BlockStateProvider blockStateProvider, float chance) implements DungeonDecoration {
    @Override
    public void decorate(Blueprint blueprint, LevelAccessor world, BlockPos pos, Rotation rotation, RandomSource random, BoundingBox worldGenBounds, BoundingBox structureBounds) {
        boolean ew = rotation == Rotation.NONE || rotation == Rotation.CLOCKWISE_180;
        int maxX = ew ? blueprint.xSpan() : blueprint.zSpan();
        int maxZ = ew ? blueprint.zSpan() : blueprint.xSpan();
        for (int x = 1; x < maxX - 1; x++) {
            for (int y = 0; y < blueprint.ySpan(); y++) {
                for (int z = 1; z < maxZ - 1; z++) {
                    BlockPos currentPos = new BlockPos(pos.getX() + x, pos.getY() + y, pos.getZ() + z);
                    if (worldGenBounds.isInside(currentPos)
                            && structureBounds.isInside(currentPos)
                            && !WorldEditor.Unsafe.isBlockProtected(world, currentPos)
                            && world.isEmptyBlock(currentPos)
                            && random.nextFloat() < chance) {
                        BlockPos north = currentPos.north();
                        BlockPos east = currentPos.east();
                        BlockPos south = currentPos.south();
                        BlockPos west = currentPos.west();
                        BlockPos up = currentPos.above();

                        boolean _north = worldGenBounds.isInside(north) && structureBounds.isInside(north) && world.getBlockState(north).canOcclude();
                        boolean _east = worldGenBounds.isInside(east) && structureBounds.isInside(east) && world.getBlockState(east).canOcclude();
                        boolean _south = worldGenBounds.isInside(south) && structureBounds.isInside(south) && world.getBlockState(south).canOcclude();
                        boolean _west = worldGenBounds.isInside(west) && structureBounds.isInside(west) && world.getBlockState(west).canOcclude();
                        boolean _up = worldGenBounds.isInside(up) && structureBounds.isInside(up) && world.getBlockState(up).canOcclude();

                        if (_north || _east || _south || _west || _up) {
                            world.setBlock(currentPos, blockStateProvider.get(currentPos, random), 2);
                        }
                    }
                }
            }
        }
    }
}