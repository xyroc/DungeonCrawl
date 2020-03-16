package xiroc.dungeoncrawl.dungeon.piece;

/*
 * DungeonCrawl (C) 2019 - 2020 XYROC (XIROC1337), All Rights Reserved 
 */

/*
 * Used for objects that contain data for the dungeon layout generation process.
 */
public class PlaceHolder {

	public final DungeonPiece reference;
	public boolean[] flags;

	public PlaceHolder(DungeonPiece reference) {
		this.reference = reference;
		this.flags = new boolean[3]; // size is the amount of existing flags
	}

	public PlaceHolder withFlag(Flag flag) {
		flags[flag.id] = true;
		return this;
	}
	
	public boolean hasFlag(Flag flag) {
		return flags[flag.id];
	}

	public enum Flag {

		PLACEHOLDER(0), FIXED_POSITION(1), FIXED_ROTATION(2);

		public int id;

		private Flag(int id) {
			this.id = id;
		}

	}

}
