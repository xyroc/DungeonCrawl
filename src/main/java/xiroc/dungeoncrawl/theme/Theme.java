package xiroc.dungeoncrawl.theme;

import xiroc.dungeoncrawl.util.IBlockStateProvider;

public class Theme {

	// public static final Theme DEFAULT = new Theme();

	public final IBlockStateProvider ceiling;
	public final IBlockStateProvider wall;
	public final IBlockStateProvider floor;

	public Theme(IBlockStateProvider ceiling, IBlockStateProvider wall, IBlockStateProvider floor) {
		this.ceiling = ceiling;
		this.wall = wall;
		this.floor = floor;
	}

}
