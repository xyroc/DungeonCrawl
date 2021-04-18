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
import xiroc.dungeoncrawl.dungeon.DungeonBuilder;
import xiroc.dungeoncrawl.dungeon.PlacementContext;
import xiroc.dungeoncrawl.dungeon.block.WeightedRandomBlock;
import xiroc.dungeoncrawl.dungeon.model.DungeonModel;
import xiroc.dungeoncrawl.dungeon.model.DungeonModelBlockType;
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
    public void decorate(DungeonModel model, IWorld world, BlockPos origin, PlacementContext context, int width, int height, int length, MutableBoundingBox worldGenBounds, MutableBoundingBox structureBounds, DungeonPiece piece, int stage) {
        model.blocks.forEach((block) -> {
            BlockPos pos = IDungeonDecoration.getRotatedBlockPos(block.position.getX(), block.position.getY() + 1, block.position.getZ(), origin, model, piece.rotation);
            if (!DungeonBuilder.isBlockProtected(world, origin, context)
                    && block.type == DungeonModelBlockType.FLOOR && block.position.getY() < model.height - 1
                    && worldGenBounds.isVecInside(pos)
                    && structureBounds.isVecInside(pos)
                    && world.isAirBlock(origin.add(block.position).up())
                    && checkSolid(world, origin.add(block.position), worldGenBounds, structureBounds)
                    && WeightedRandomBlock.RANDOM.nextFloat() < chance) {
                world.setBlockState(pos, blockStateProvider.get(pos), 2);
            }
        });
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
        public void decorate(DungeonModel model, IWorld world, BlockPos origin, PlacementContext context, int width, int height, int length, MutableBoundingBox worldGenBounds, MutableBoundingBox structureBounds, DungeonPiece piece, int stage) {
            model.blocks.forEach((block) -> {
                if (block.type == DungeonModelBlockType.FLOOR && block.position.getY() < model.height - 1) {
                    BlockPos pos = IDungeonDecoration.getRotatedBlockPos(block.position.getX(), block.position.getY() + 1, block.position.getZ(), origin, model, piece.rotation);

                    if (worldGenBounds.isVecInside(pos)
                            && structureBounds.isVecInside(pos) && world.isAirBlock(pos)
                            && world.getBlockState(pos.down()).isSolid()
                            && !DungeonBuilder.isBlockProtected(world, pos, context)
                            && (checkSolid(world, pos.north(), worldGenBounds, structureBounds)
                            || checkSolid(world, pos.east(), worldGenBounds, structureBounds)
                            || checkSolid(world, pos.south(), worldGenBounds, structureBounds)
                            || checkSolid(world, pos.west(), worldGenBounds, structureBounds))
                            && WeightedRandomBlock.RANDOM.nextFloat() < chance) {
                        world.setBlockState(pos, blockStateProvider.get(pos), 2);
                    }
                }
            });
        }

    }
}
