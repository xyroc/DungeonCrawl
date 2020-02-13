package xiroc.dungeoncrawl.dungeon;

/*
 * DungeonCrawl (C) 2019 - 2020 XYROC (XIROC1337), All Rights Reserved 
 */

import xiroc.dungeoncrawl.dungeon.DungeonPieces.DungeonPiece;
import xiroc.dungeoncrawl.util.IRandom;

public class RandomFeature {

	public static final int[] SIDE_ROOM_IDS = new int[] { 30, 31, 33 };

	public static final int[] LARGE_ROOM_IDS = new int[] { 25, 25, 35 };

	public static final IRandom<DungeonPiece> CORRIDOR_FEATURE = (rand) -> {
		switch (rand.nextInt(2)) {
		case 0:
			return new DungeonPieces.CorridorTrap(null, DungeonPieces.DEFAULT_NBT);
		case 1:
			return new DungeonPieces.CorridorRoom(null, DungeonPieces.DEFAULT_NBT);
		}
		return null;
	};

	public static final IRandom<Integer> SIDE_ROOMS = (rand) -> SIDE_ROOM_IDS[rand.nextInt(SIDE_ROOM_IDS.length)];

	public static final IRandom<DungeonPiece> SIDE_ROOM = (rand) -> {
		DungeonPieces.SideRoom sideRoom = new DungeonPieces.SideRoom(null, DungeonPieces.DEFAULT_NBT);
		sideRoom.modelID = SIDE_ROOMS.roll(rand);
		return sideRoom;
	};

	public static final IRandom<Integer> LARGE_ROOMS = (rand) -> LARGE_ROOM_IDS[rand.nextInt(LARGE_ROOM_IDS.length)];

}
