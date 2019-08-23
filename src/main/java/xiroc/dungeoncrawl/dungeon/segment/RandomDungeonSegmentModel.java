package xiroc.dungeoncrawl.dungeon.segment;

import xiroc.dungeoncrawl.util.IRandom;

public class RandomDungeonSegmentModel {

    public static final IRandom<DungeonSegmentModel> CORRIDOR_STRAIGHT = (rand) -> {
	switch (rand.nextInt(3)) {
	case 0:
	    return DungeonSegmentModelRegistry.CORRIDOR_EW;
	case 1:
	    return DungeonSegmentModelRegistry.CORRIDOR_EW_2;
	case 2:
	    return DungeonSegmentModelRegistry.CORRIDOR_EW_3;
	}
	return null;
    };

    public static final IRandom<DungeonSegmentModel> CORRIDOR_TURN = (rand) -> {
	switch (rand.nextInt(3)) {
	case 0:
	    return DungeonSegmentModelRegistry.CORRIDOR_EW_TURN;
	case 1:
	    return DungeonSegmentModelRegistry.CORRIDOR_EW_2_TURN;
	case 2:
	    return DungeonSegmentModelRegistry.CORRIDOR_EW_3_TURN;
	}
	return null;
    };

    public static final IRandom<DungeonSegmentModel> CORRIDOR_OPEN = (rand) -> {
	switch (rand.nextInt(3)) {
	case 0:
	    return DungeonSegmentModelRegistry.CORRIDOR_EW_OPEN;
	case 1:
	    return DungeonSegmentModelRegistry.CORRIDOR_EW_2_OPEN;
	case 2:
	    return DungeonSegmentModelRegistry.CORRIDOR_EW_3_OPEN;
	}
	return null;
    };

    public static final IRandom<DungeonSegmentModel> CORRIDOR_ALL_OPEN = (rand) -> {
	switch (rand.nextInt(3)) {
	case 0:
	    return DungeonSegmentModelRegistry.CORRIDOR_EW_ALL_OPEN;
	case 1:
	    return DungeonSegmentModelRegistry.CORRIDOR_EW_2_ALL_OPEN;
	case 2:
	    return DungeonSegmentModelRegistry.CORRIDOR_EW_3_ALL_OPEN;
	}
	return null;
    };

}
