package xiroc.dungeoncrawl.dungeon;

import xiroc.dungeoncrawl.dungeon.DungeonPieces.DungeonPiece;
import xiroc.dungeoncrawl.util.IRandom;

public class RandomFeature {

    public static final IRandom<DungeonPiece> CORRIDOR_FEATURE = (rand) -> {
	switch (rand.nextInt(2)) {
	case 0:
	    return new DungeonPieces.CorridorTrap(null, DungeonPieces.DEFAULT_NBT);
	case 1:
	    return new DungeonPieces.CorridorRoom(null, DungeonPieces.DEFAULT_NBT);
	}
	return null;
    };

}
