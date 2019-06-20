package xiroc.dungeoncrawl.theme;

import xiroc.dungeoncrawl.build.block.WeightedRandomBlock;

public class Theme {
	
	// public static final Theme DEFAULT = new Theme();

	public final WeightedRandomBlock ceiling;
	public final WeightedRandomBlock wall;
	public final WeightedRandomBlock floor;

	public Theme(WeightedRandomBlock ceiling, WeightedRandomBlock wall, WeightedRandomBlock floor) {
		this.ceiling = ceiling;
		this.wall = wall;
		this.floor = floor;
	}

}
