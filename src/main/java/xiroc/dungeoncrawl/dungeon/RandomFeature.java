package xiroc.dungeoncrawl.dungeon;

import xiroc.dungeoncrawl.dungeon.piece.DungeonPiece;
import xiroc.dungeoncrawl.dungeon.piece.room.DungeonSideRoom;
import xiroc.dungeoncrawl.util.IRandom;

/*
 * DungeonCrawl (C) 2019 - 2020 XYROC (XIROC1337), All Rights Reserved
 */

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
