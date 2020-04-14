package xiroc.dungeoncrawl.dungeon.piece;

import java.util.List;

import com.google.common.collect.Lists;

/*
 * DungeonCrawl (C) 2019 - 2020 XYROC (XIROC1337), All Rights Reserved 
 */

/*
 * Used for objects that contain data for the dungeon layout generation process.
 */
public class PlaceHolder {

	public final DungeonPiece reference;
	public List<Flag> flags;
	
	public PlaceHolder(DungeonPiece reference) {
		this.reference = reference;
		this.flags = Lists.newArrayList();
	}

	public PlaceHolder withFlag(Flag flag) {
		flags.add(flag);
		return this;
	}

	public PlaceHolder withFlags(Flag... flags) {
		for (Flag flag : flags)
			this.flags.add(flag);
		return this;
	}

	public boolean hasFlag(Flag flag) {
		return flags.contains(flag);
	}

	public enum Flag {

		PLACEHOLDER, FIXED_POSITION, FIXED_ROTATION, FIXED_MODEL;

	}

}
