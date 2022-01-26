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
import net.minecraft.util.Rotation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MutableBoundingBox;
import net.minecraft.world.IWorld;
import xiroc.dungeoncrawl.dungeon.DungeonBuilder;
import xiroc.dungeoncrawl.dungeon.block.DungeonBlocks;
import xiroc.dungeoncrawl.dungeon.block.provider.BlockStateProvider;
import xiroc.dungeoncrawl.dungeon.model.DungeonModel;
import xiroc.dungeoncrawl.dungeon.piece.DungeonPiece;

public class ScatteredDecoration implements DungeonDecoration {

    private final BlockStateProvider blockStateProvider;
    private final float chance;

    public ScatteredDecoration(BlockStateProvider blockStateProvider, float chance) {
        this.blockStateProvider = blockStateProvider;
        this.chance = chance;
    }

    @Override
    public void decorate(DungeonModel model, IWorld world, BlockPos pos, int width, int height, int length, MutableBoundingBox worldGenBounds, MutableBoundingBox structureBounds,
                         DungeonPiece piece, int stage, boolean worldGen) {
        boolean ew = piece.rotation == Rotation.NONE || piece.rotation == Rotation.CLOCKWISE_180;
        int maxX = ew ? width : length;
        int maxZ = ew ? length : width;
        for (int x = 1; x < maxX - 1; x++) {
            for (int y = 0; y < height; y++) {
                for (int z = 1; z < maxZ - 1; z++) {
                    BlockPos currentPos = new BlockPos(pos.getX() + x, pos.getY() + y, pos.getZ() + z);
                    if (worldGenBounds.isInside(currentPos)
                            && structureBounds.isInside(currentPos)
                            && !DungeonBuilder.isBlockProtected(world, currentPos)
                            && world.isEmptyBlock(currentPos)
                            && DungeonBlocks.RANDOM.nextFloat() < chance) {

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
                            world.setBlock(currentPos, blockStateProvider.get(world, currentPos), 2);
                        }

                    }
                }
            }
        }
    }

    @Override
    public JsonObject serialize() {
        JsonObject object = new JsonObject();
        object.addProperty("type", DungeonDecoration.SCATTERED_DECORATION);
        object.addProperty("chance", this.chance);

        object.add("block", this.blockStateProvider.serialize());
        return object;
    }

}
