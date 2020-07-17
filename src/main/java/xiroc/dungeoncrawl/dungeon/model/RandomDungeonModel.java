package xiroc.dungeoncrawl.dungeon.model;

/*
 * DungeonCrawl (C) 2019 - 2020 XYROC (XIROC1337), All Rights Reserved
 */

import xiroc.dungeoncrawl.util.IRandom;

public class RandomDungeonModel {

    // unused
    public static final IRandom<DungeonModel> NODE_CONNECTOR = (rand) -> null;

    public static final IRandom<DungeonModel> CORRIDOR_STRAIGHT = (rand) -> {
        switch (rand.nextInt(2)) {
            case 0:
                if (rand.nextFloat() < 0.1)
                    return DungeonModels.CORRIDOR_ROOM;
                return DungeonModels.CORRIDOR;
            case 1:
                return DungeonModels.CORRIDOR_2;
            case 2:
                return DungeonModels.CORRIDOR_3;
            case 3:
                return DungeonModels.CORRIDOR_STONE;
        }
        return null;
    };

}
