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

import com.google.gson.JsonObject;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.VineBlock;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import xiroc.dungeoncrawl.dungeon.DungeonBuilder;
import xiroc.dungeoncrawl.dungeon.model.DungeonModel;
import xiroc.dungeoncrawl.dungeon.piece.DungeonPiece;

public record VineDecoration(float chance) implements DungeonDecoration {

    @Override
    public void decorate(DungeonModel model, LevelAccessor world, BlockPos pos, RandomSource random, BoundingBox worldGenBounds, BoundingBox structureBounds, DungeonPiece piece) {
        boolean ew = piece.rotation == Rotation.NONE || piece.rotation == Rotation.CLOCKWISE_180;
        int maxX = ew ? model.width : model.length;
        int maxZ = ew ? model.length : model.width;
        for (int x = 0; x < maxX; x++) {
            for (int y = 0; y < model.height; y++) {
                for (int z = 0; z < maxZ; z++) {
                    BlockPos currentPos = new BlockPos(pos.getX() + x, pos.getY() + y, pos.getZ() + z);
                    if (worldGenBounds.isInside(currentPos)
                            && structureBounds.isInside(currentPos)
                            && !DungeonBuilder.isBlockProtected(world, currentPos)
                            && world.isEmptyBlock(currentPos)) {
                        BlockPos north = currentPos.north();
                        BlockPos east = currentPos.east();
                        BlockPos south = currentPos.south();
                        BlockPos west = currentPos.west();
                        BlockPos up = currentPos.above();

                        boolean _north = worldGenBounds.isInside(north) && structureBounds.isInside(north) && VineBlock.isAcceptableNeighbour(world, north, Direction.NORTH);
                        boolean _east = worldGenBounds.isInside(east) && structureBounds.isInside(east) && VineBlock.isAcceptableNeighbour(world, east, Direction.EAST);
                        boolean _south = worldGenBounds.isInside(south) && structureBounds.isInside(south) && VineBlock.isAcceptableNeighbour(world, south, Direction.SOUTH);
                        boolean _west = worldGenBounds.isInside(west) && structureBounds.isInside(west) && VineBlock.isAcceptableNeighbour(world, west, Direction.WEST);
                        boolean _up = worldGenBounds.isInside(up) && structureBounds.isInside(up) && VineBlock.isAcceptableNeighbour(world, up, Direction.UP);

                        if ((_north || _east || _south || _west || _up) && random.nextFloat() < chance) {
                            world.setBlock(currentPos, Blocks.VINE.defaultBlockState().setValue(BlockStateProperties.NORTH, _north)
                                    .setValue(BlockStateProperties.EAST, _east).setValue(BlockStateProperties.SOUTH, _south)
                                    .setValue(BlockStateProperties.WEST, _west).setValue(BlockStateProperties.UP, _up), 2);
                        }
                    }
                }
            }
        }
    }

    @Override
    public JsonObject serialize() {
        JsonObject object = new JsonObject();
        object.addProperty("type", DungeonDecoration.VINE_DECORATION);
        object.addProperty("chance", this.chance);
        return object;
    }
}