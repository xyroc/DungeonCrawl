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

import xiroc.dungeoncrawl.dungeon.piece.DungeonPiece;
import xiroc.dungeoncrawl.dungeon.piece.room.DungeonSideRoom;
import xiroc.dungeoncrawl.util.IRandom;

public class RandomFeature {

    public static final int[] SIDE_ROOM_IDS = new int[]{30, 31, 34};

    public static final int[] LARGE_ROOM_IDS = new int[]{25, 25, 36};

    public static final IRandom<Integer> SIDE_ROOMS = (rand) -> SIDE_ROOM_IDS[rand.nextInt(SIDE_ROOM_IDS.length)];

    public static final IRandom<DungeonPiece> SIDE_ROOM = (rand) -> {
        DungeonSideRoom sideRoom = new DungeonSideRoom(null, DungeonPiece.DEFAULT_NBT);
        sideRoom.modelID = SIDE_ROOMS.roll(rand);
        return sideRoom;
    };

    public static final IRandom<Integer> LARGE_ROOMS = (rand) -> LARGE_ROOM_IDS[rand.nextInt(LARGE_ROOM_IDS.length)];

}
