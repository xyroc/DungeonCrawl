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

import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MutableBoundingBox;
import net.minecraft.world.IWorld;
import xiroc.dungeoncrawl.dungeon.block.WeightedRandomBlock;
import xiroc.dungeoncrawl.dungeon.model.DungeonModel;
import xiroc.dungeoncrawl.dungeon.model.DungeonModelBlockType;
import xiroc.dungeoncrawl.dungeon.model.PlacementBehaviour;
import xiroc.dungeoncrawl.dungeon.piece.DungeonPiece;
import xiroc.dungeoncrawl.util.IBlockStateProvider;

public class FloorDecoration implements IDungeonDecoration {

    private final float chance;

    private final IBlockStateProvider blockStateProvider;

    public FloorDecoration(IBlockStateProvider blockStateProvider, float chance) {
        this.blockStateProvider = blockStateProvider;
        this.chance = chance;
    }

    @Override
    public void decorate(DungeonModel model, IWorld world, BlockPos pos, int width, int height, int length, MutableBoundingBox worldGenBounds, MutableBoundingBox structureBounds, DungeonPiece piece, int stage) {
        for (int x = 0; x < model.width; x++) {
            for (int z = 0; z < model.length; z++) {
                for (int y = 0; y < model.height; y++) {
                    BlockPos blockPos = IDungeonDecoration.getRotatedBlockPos(x, y + 1, z, pos, model, piece.rotation);
                    if (model.model[x][y][z] != null && model.model[x][y][z].type == DungeonModelBlockType.FLOOR && y < model.height - 1
                            && worldGenBounds.isVecInside(blockPos) && structureBounds.isVecInside(blockPos) && world.isAirBlock(new BlockPos(pos.getX() + x, pos.getY() + y + 1, pos.getZ() + z))
                            && checkSolid(world, new BlockPos(pos.getX() + x, pos.getY() + y, pos.getZ() + z), worldGenBounds, structureBounds)
                            && WeightedRandomBlock.RANDOM.nextFloat() < chance) {
                        piece.setBlockState(blockStateProvider.get(), world, worldGenBounds, null, blockPos,
                                0, 0, PlacementBehaviour.SOLID);
                    }
                }
            }
        }
    }

    private static boolean checkSolid(IWorld world, BlockPos pos, MutableBoundingBox worldGenBounds, MutableBoundingBox structureBounds) {
        return worldGenBounds.isVecInside(pos) && structureBounds.isVecInside(pos) && world.getBlockState(pos).isSolid();
    }

    public static class NextToSolid implements IDungeonDecoration {

        private final float chance;

        private final IBlockStateProvider blockStateProvider;

        public NextToSolid(IBlockStateProvider blockStateProvider, float chance) {
            this.blockStateProvider = blockStateProvider;
            this.chance = chance;
        }

        @Override
        public void decorate(DungeonModel model, IWorld world, BlockPos pos, int width, int height, int length, MutableBoundingBox worldGenBounds, MutableBoundingBox structureBounds, DungeonPiece piece, int stage) {
            for (int x = 0; x < model.width; x++) {
                for (int z = 0; z < model.length; z++) {
                    for (int y = 0; y < model.height; y++) {
                        if (model.model[x][y][z] != null && model.model[x][y][z].type == DungeonModelBlockType.FLOOR && y < model.height - 1) {
                            BlockPos p = new BlockPos(pos.getX() + x, pos.getY() + y + 1, pos.getZ() + z);
                            BlockPos blockPos = IDungeonDecoration.getRotatedBlockPos(x, y + 1, z, pos, model, piece.rotation);

                            if (worldGenBounds.isVecInside(blockPos) && structureBounds.isVecInside(blockPos) && world.isAirBlock(p) && world.getBlockState(p.down()).isSolid() &&
                                    (checkSolid(world, p.north(), worldGenBounds, structureBounds) || checkSolid(world, p.east(), worldGenBounds, structureBounds)
                                            || checkSolid(world, p.south(), worldGenBounds, structureBounds) || checkSolid(world, p.west(), worldGenBounds, structureBounds))
                                    && WeightedRandomBlock.RANDOM.nextFloat() < chance) {
                                piece.setBlockState(blockStateProvider.get(), world, worldGenBounds, null, blockPos, 0, 0, PlacementBehaviour.SOLID);

                            }
                        }
                    }
                }
            }
        }

    }
}
