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

package xiroc.dungeoncrawl.dungeon;

import com.google.common.collect.Lists;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.Tuple;
import net.minecraft.world.level.block.Rotation;
import xiroc.dungeoncrawl.dungeon.piece.room.DungeonSideRoom;
import xiroc.dungeoncrawl.util.Position2D;

import java.util.List;
import java.util.Random;

public class DungeonFeatures {

    public static final List<CorridorFeature> CORRIDOR_FEATURES;

    static {
        CORRIDOR_FEATURES = Lists.newArrayList();

        CORRIDOR_FEATURES.add(((builder, layer, x, z, rand, lyr, stage, startPos) -> {
            if (layer.grid[x][z].piece.connectedSides < 4 && rand.nextFloat() < 0.075) {
                Tuple<Position2D, Rotation> sideRoomData = layer.findSideRoomData(new Position2D(x, z), rand);
                if (sideRoomData != null) {
                    DungeonSideRoom sideRoom = new DungeonSideRoom();
                    Direction dir = sideRoomData.getB().rotate(Direction.WEST);
                    sideRoom.openSide(dir);
                    sideRoom.setGridPosition(sideRoomData.getA());
                    sideRoom.setRotation(sideRoomData.getB());
                    sideRoom.stage = stage;

                    layer.grid[sideRoomData.getA().x][sideRoomData.getA().z] = new Tile(sideRoom);
                    layer.grid[x][z].piece.openSide(dir.getOpposite());
                    layer.map.markPositionAsOccupied(sideRoomData.getA());
                    layer.rotatePiece(layer.grid[x][z], rand);
                    return true;
                }
            }
            return false;
        }));

    }

    public static void processCorridor(DungeonBuilder builder, DungeonLayer layer, int x, int z, Random rand, int lyr,
                                       int stage, BlockPos startPos) {
        for (CorridorFeature corridorFeature : CORRIDOR_FEATURES)
            if (corridorFeature.process(builder, layer, x, z, rand, lyr, stage, startPos))
                return;
    }

    @FunctionalInterface
    public interface CorridorFeature {

        boolean process(DungeonBuilder builder, DungeonLayer layer, int x, int z, Random rand, int lyr,
                        int stage, BlockPos startPos);

    }

}
