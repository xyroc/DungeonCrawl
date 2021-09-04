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
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import xiroc.dungeoncrawl.dungeon.DungeonBuilder;
import xiroc.dungeoncrawl.dungeon.block.DungeonBlocks;
import xiroc.dungeoncrawl.dungeon.model.DungeonModel;
import xiroc.dungeoncrawl.dungeon.model.DungeonModelBlockType;
import xiroc.dungeoncrawl.dungeon.piece.DungeonPiece;
import xiroc.dungeoncrawl.util.IBlockStateProvider;

public record FloorDecoration(IBlockStateProvider blockStateProvider,
                              float chance) implements IDungeonDecoration {

    @Override

    public void decorate(DungeonModel model, LevelAccessor world, BlockPos origin, int width, int height, int length, BoundingBox worldGenBounds, BoundingBox structureBounds,
                         DungeonPiece piece, int stage, boolean worldGen) {
        model.blocks.forEach((block) -> {
            BlockPos pos = IDungeonDecoration.getRotatedBlockPos(block.position.getX(), block.position.getY() + 1, block.position.getZ(), origin, model, piece.rotation);
            if (!DungeonBuilder.isBlockProtected(world, origin)
                    && block.type == DungeonModelBlockType.FLOOR && block.position.getY() < model.height - 1
                    && worldGenBounds.isInside(pos)
                    && structureBounds.isInside(pos)
                    && world.isEmptyBlock(origin.offset(block.position).above())
                    && checkSolid(world, origin.offset(block.position), worldGenBounds, structureBounds)
                    && DungeonBlocks.RANDOM.nextFloat() < chance) {
                world.setBlock(pos, blockStateProvider.get(world, pos), 2);
            }
        });
    }

    private static boolean checkSolid(LevelAccessor world, BlockPos pos, BoundingBox worldGenBounds, BoundingBox structureBounds) {
        return worldGenBounds.isInside(pos) && structureBounds.isInside(pos) && world.getBlockState(pos).canOcclude();
    }

    public record NextToSolid(IBlockStateProvider blockStateProvider,
                              float chance) implements IDungeonDecoration {

        @Override
        public void decorate(DungeonModel model, LevelAccessor world, BlockPos origin, int width, int height, int length, BoundingBox worldGenBounds, BoundingBox structureBounds,
                             DungeonPiece piece, int stage, boolean worldGen) {
            model.blocks.forEach((block) -> {
                if (block.type == DungeonModelBlockType.FLOOR && block.position.getY() < model.height - 1) {
                    BlockPos pos = IDungeonDecoration.getRotatedBlockPos(block.position.getX(), block.position.getY() + 1, block.position.getZ(), origin, model, piece.rotation);

                    if (worldGenBounds.isInside(pos)
                            && structureBounds.isInside(pos) && world.isEmptyBlock(pos)
                            && world.getBlockState(pos.below()).canOcclude()
                            && !DungeonBuilder.isBlockProtected(world, pos)
                            && (checkSolid(world, pos.north(), worldGenBounds, structureBounds)
                            || checkSolid(world, pos.east(), worldGenBounds, structureBounds)
                            || checkSolid(world, pos.south(), worldGenBounds, structureBounds)
                            || checkSolid(world, pos.west(), worldGenBounds, structureBounds))
                            && DungeonBlocks.RANDOM.nextFloat() < chance) {
                        world.setBlock(pos, blockStateProvider.get(world, pos), 2);
                    }
                }
            });
        }

    }
}
