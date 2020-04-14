package xiroc.dungeoncrawl.dungeon.model;

/*
 * DungeonCrawl (C) 2019 - 2020 XYROC (XIROC1337), All Rights Reserved 
 */

import xiroc.dungeoncrawl.util.IRandom;

public class RandomDungeonModel {
	
	public static final IRandom<DungeonModel> CONNECTOR = (rand) -> {
		return null;
	};

	public static final IRandom<DungeonModel> CORRIDOR_STRAIGHT = (rand) -> {
		switch (rand.nextInt(3)) {
		case 0:
			return DungeonModels.CORRIDOR;
		case 1:
			return DungeonModels.CORRIDOR_2;
		case 2:
			return DungeonModels.CORRIDOR_3;
		}
		return null;
	};

	public static final IRandom<DungeonModel> CORRIDOR_TURN = (rand) -> {
		switch (rand.nextInt(3)) {
		case 0:
			return DungeonModels.CORRIDOR_TURN;
		case 1:
			return DungeonModels.CORRIDOR_2_TURN;
		case 2:
			return DungeonModels.CORRIDOR_3_TURN;
		}
		return null;
	};

	public static final IRandom<DungeonModel> CORRIDOR_OPEN = (rand) -> {
		switch (rand.nextInt(3)) {
		case 0:
			return DungeonModels.CORRIDOR_OPEN;
		case 1:
			return DungeonModels.CORRIDOR_2_OPEN;
		case 2:
			return DungeonModels.CORRIDOR_3_OPEN;
		}
		return null;
	};

	public static final IRandom<DungeonModel> CORRIDOR_ALL_OPEN = (rand) -> {
		switch (rand.nextInt(3)) {
		case 0:
			return DungeonModels.CORRIDOR_ALL_OPEN;
		case 1:
			return DungeonModels.CORRIDOR_2_ALL_OPEN;
		case 2:
			return DungeonModels.CORRIDOR_3_ALL_OPEN;
		}
		return null;
	};
	
	public static final IRandom<DungeonModel> NETHER_CORRIDOR_STRAIGHT = (rand) -> {
		switch (rand.nextInt(2)) {
		case 0:
			return DungeonModels.CORRIDOR;
		case 1:
			return DungeonModels.CORRIDOR_2;
		}
		return null;
	};

	public static final IRandom<DungeonModel> NETHER_CORRIDOR_TURN = (rand) -> {
//		switch (rand.nextInt(2)) {
//		case 0:
//			return DungeonSegmentModelRegistry.CORRIDOR_TURN;
//		case 1:
//			return DungeonSegmentModelRegistry.CORRIDOR_2_TURN;
//		}
//		return null;
		return DungeonModels.CORRIDOR_2_TURN;
	};

	public static final IRandom<DungeonModel> NETHER_CORRIDOR_OPEN = (rand) -> {
		switch (rand.nextInt(2)) {
		case 0:
			return DungeonModels.CORRIDOR_OPEN;
		case 1:
			return DungeonModels.CORRIDOR_2_OPEN;
		}
		return null;
	};

	public static final IRandom<DungeonModel> NETHER_CORRIDOR_ALL_OPEN = (rand) -> {
//		switch (rand.nextInt(2)) {
//		case 0:
//			return DungeonSegmentModelRegistry.CORRIDOR_ALL_OPEN;
//		case 1:
//			return DungeonSegmentModelRegistry.CORRIDOR_2_ALL_OPEN;
//		}
//		return null;
		return DungeonModels.CORRIDOR_ALL_OPEN;
	};

}
