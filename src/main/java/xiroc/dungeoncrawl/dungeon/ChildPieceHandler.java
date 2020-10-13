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

import net.minecraft.util.Rotation;
import net.minecraft.util.math.Vec3i;
import xiroc.dungeoncrawl.DungeonCrawl;
import xiroc.dungeoncrawl.dungeon.model.DungeonModel;
import xiroc.dungeoncrawl.dungeon.model.DungeonModels;

public class ChildPieceHandler {

    public static ChildPieceHandler LARGE_CORRIDOR_START, LARGE_CORRIDOR_STRAIGHT, LARGE_CORRIDOR_TURN,
            LARGE_CORRIDOR_OPEN;

    public int modelID;

    public ChildPieceSpot[] none, clockwise_90, clockwise_180, counterclockwise_90;

    public ChildPieceHandler(int modelID, ChildPieceSpot[] base) {
        this.modelID = modelID;
        this.none = base;
        this.clockwise_90 = rotate(base, modelID, Rotation.CLOCKWISE_90);
        this.clockwise_180 = rotate(base, modelID, Rotation.CLOCKWISE_180);
        this.counterclockwise_90 = rotate(base, modelID, Rotation.COUNTERCLOCKWISE_90);
    }

    public ChildPieceSpot[] getChildPieceSpots(Rotation rotation) {
        switch (rotation) {
            case NONE:
                return none;
            case CLOCKWISE_90:
                return clockwise_90;
            case CLOCKWISE_180:
                return clockwise_180;
            case COUNTERCLOCKWISE_90:
                return counterclockwise_90;
            default:
                return null;
        }
    }

    public static void load() {
        LARGE_CORRIDOR_START = new ChildPieceHandler(DungeonModels.LARGE_CORRIDOR_START.id,
                new ChildPieceSpot[]{new ChildPieceSpot(1, 1, 0, Rotation.COUNTERCLOCKWISE_90),
                        new ChildPieceSpot(5, 1, 0, Rotation.COUNTERCLOCKWISE_90),
                        new ChildPieceSpot(1, 1, 8, Rotation.CLOCKWISE_90),
                        new ChildPieceSpot(5, 1, 8, Rotation.CLOCKWISE_90)});
        LARGE_CORRIDOR_STRAIGHT = new ChildPieceHandler(DungeonModels.LARGE_CORRIDOR_STRAIGHT.id,
                new ChildPieceSpot[]{new ChildPieceSpot(0, 1, 0, Rotation.COUNTERCLOCKWISE_90),
                        new ChildPieceSpot(6, 1, 0, Rotation.COUNTERCLOCKWISE_90),
                        new ChildPieceSpot(0, 1, 8, Rotation.CLOCKWISE_90),
                        new ChildPieceSpot(6, 1, 8, Rotation.CLOCKWISE_90)});
        LARGE_CORRIDOR_TURN = new ChildPieceHandler(DungeonModels.LARGE_CORRIDOR_TURN.id,
                new ChildPieceSpot[]{new ChildPieceSpot(0, 1, 0, Rotation.COUNTERCLOCKWISE_90),
                        new ChildPieceSpot(8, 1, 6, Rotation.NONE)});
        LARGE_CORRIDOR_OPEN = new ChildPieceHandler(DungeonModels.LARGE_CORRIDOR_OPEN.id,
                new ChildPieceSpot[]{new ChildPieceSpot(0, 1, 0, Rotation.COUNTERCLOCKWISE_90),
                        new ChildPieceSpot(6, 1, 0, Rotation.COUNTERCLOCKWISE_90)});
    }

    public static ChildPieceSpot[] rotate(ChildPieceSpot[] array, int modelID, Rotation rotation) {
        DungeonModel model = DungeonModels.MODELS.get(modelID);

        for (int i = 0; i < array.length; i++) {
            ChildPieceSpot spot = array[i];
            if (spot != null) {
                spot.rotation = rotation;
                spot.offset = rotateOffset(spot.offset, rotation, model);
            }
        }

        return array;
    }

    public static Vec3i rotateOffset(Vec3i offset, Rotation rotation, DungeonModel model) {
        switch (rotation) {
            case CLOCKWISE_90:
                return new Vec3i(offset.getZ(), offset.getY(), offset.getX());
            case CLOCKWISE_180:
                return new Vec3i(model.width - offset.getX() - 1, offset.getY(), model.length - offset.getZ() - 1);
            case COUNTERCLOCKWISE_90:
                return new Vec3i(model.width - offset.getZ() - 1, offset.getY(), model.length - offset.getX() - 1);
            default:
                return offset;
        }
    }

    public static class ChildPieceSpot {

        public Vec3i offset;
        public Rotation rotation;

        public ChildPieceSpot(int xOffset, int yOffset, int zOffset, Rotation rotation) {
            this.offset = new Vec3i(xOffset, yOffset, zOffset);
            this.rotation = rotation;
        }

    }

}
