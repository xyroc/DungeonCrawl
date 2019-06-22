package xiroc.dungeoncrawl.theme;

import net.minecraft.block.BlockState;
import xiroc.dungeoncrawl.util.IBlockStateProvider;

public class Theme {

	// public static final Theme DEFAULT = new Theme();

	public final IBlockStateProvider ceiling;
	public final IBlockStateProvider wall;
	public final IBlockStateProvider floor;
	public final IBlockStateProvider stairs;
	public final IBlockStateProvider ceilingStairs;
	public final IBlockStateProvider floorStairs;

	public Theme(IBlockStateProvider ceiling, IBlockStateProvider wall, IBlockStateProvider floor, IBlockStateProvider stairs, IBlockStateProvider ceilingStairs, IBlockStateProvider floorStairs) {
		this.ceiling = ceiling;
		this.wall = wall;
		this.floor = floor;
		this.stairs = stairs;
		this.ceilingStairs = ceilingStairs;
		this.floorStairs = floorStairs;
	}

	public Theme(BlockState ceiling, BlockState wall, BlockState floor, BlockState stairs, BlockState ceilingStairs, BlockState floorStairs) {
		this.ceiling = () -> ceiling;
		this.wall = () -> wall;
		this.floor = () -> floor;
		this.stairs = () -> stairs;
		this.ceilingStairs = () -> ceilingStairs;
		this.floorStairs = () -> floorStairs;
	}

}
