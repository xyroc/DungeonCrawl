package xiroc.dungeoncrawl.util;

/*
 * DungeonCrawl (C) 2019 XYROC (XIROC1337), All Rights Reserved 
 */

import java.util.Random;

/**
 * Used to create random objects of various types.
 */
public interface IRandom<T> {

	T roll(Random rand);

}
