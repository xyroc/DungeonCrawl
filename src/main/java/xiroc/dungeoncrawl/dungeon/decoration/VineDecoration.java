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

import net.minecraft.block.Blocks;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.util.Rotation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MutableBoundingBox;
import net.minecraft.world.IWorld;
import xiroc.dungeoncrawl.DungeonCrawl;
import xiroc.dungeoncrawl.config.Config;
import xiroc.dungeoncrawl.dungeon.block.WeightedRandomBlock;
import xiroc.dungeoncrawl.dungeon.model.DungeonModel;
import xiroc.dungeoncrawl.dungeon.piece.DungeonPiece;

public class VineDecoration implements IDungeonDecoration {

    @Override
    public void decorate(DungeonModel model, IWorld world, BlockPos pos, int width, int height, int length, MutableBoundingBox worldGenBounds, MutableBoundingBox structureBounds, DungeonPiece piece, int stage) {
        boolean ew = piece.rotation == Rotation.NONE || piece.rotation == Rotation.CLOCKWISE_180;
        int maxX = ew ? width : length;
        int maxZ = ew ? length : width;
        for (int x = 0; x < maxX; x++) {
            for (int y = 0; y < height; y++) {
                for (int z = 0; z < maxZ; z++) {
                    BlockPos currentPos = new BlockPos(pos.getX() + x, pos.getY() + y, pos.getZ() + z);
                    if (worldGenBounds.isVecInside(currentPos) && structureBounds.isVecInside(currentPos) && world.isAirBlock(currentPos)) {
                        BlockPos north = new BlockPos(pos.getX() + x, pos.getY() + y, pos.getZ() + z - 1);
                        BlockPos east = new BlockPos(north.getX() + 1, north.getY(), pos.getZ() + z);
                        BlockPos south = new BlockPos(north.getX(), north.getY(), east.getZ() + 1);
                        BlockPos west = new BlockPos(north.getX() - 1, north.getY(), east.getZ());
                        BlockPos up = new BlockPos(north.getX(), north.getY() + 1, east.getZ());

                        boolean _north = worldGenBounds.isVecInside(north) && structureBounds.isVecInside(north) && world.getBlockState(north).isNormalCube(world, north) && !world.isAirBlock(north);
                        boolean _east = worldGenBounds.isVecInside(east) && structureBounds.isVecInside(east) && world.getBlockState(east).isNormalCube(world, east) && !world.isAirBlock(east);
                        boolean _south = worldGenBounds.isVecInside(south) && structureBounds.isVecInside(south) && world.getBlockState(south).isNormalCube(world, south) && !world.isAirBlock(south);
                        boolean _west = worldGenBounds.isVecInside(west) && structureBounds.isVecInside(west) && world.getBlockState(west).isNormalCube(world, east) && !world.isAirBlock(west);
                        boolean _up = worldGenBounds.isVecInside(up) && structureBounds.isVecInside(up) && world.getBlockState(up).isNormalCube(world, up) && !world.isAirBlock(up);

                        if ((_north || _east || _south || _west || _up) && WeightedRandomBlock.RANDOM.nextFloat() < 0.35F) {
                            BlockPos p = new BlockPos(north.getX(), north.getY(), east.getZ());
                            world.setBlockState(p, Blocks.VINE.getDefaultState().with(BlockStateProperties.NORTH, _north)
                                    .with(BlockStateProperties.EAST, _east).with(BlockStateProperties.SOUTH, _south)
                                    .with(BlockStateProperties.WEST, _west).with(BlockStateProperties.UP, _up), 2);
                        }
                    }
                }
            }
        }
    }
}