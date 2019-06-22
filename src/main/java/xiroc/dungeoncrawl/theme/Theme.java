package xiroc.dungeoncrawl.theme;

import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import xiroc.dungeoncrawl.build.block.BlockRegistry;
import xiroc.dungeoncrawl.util.IBlockStateProvider;

public class Theme {

	public static final Theme DEFAULT = new Theme(() -> BlockRegistry.STONE_BRICKS, () -> BlockRegistry.STONE_BRICKS, BlockRegistry.STONE_BRICKS_GRAVEL_COBBLESTONE, () -> Blocks.OAK_STAIRS.getDefaultState(),
			() -> Blocks.STONE_BRICK_STAIRS.getDefaultState(), () -> Blocks.OAK_STAIRS.getDefaultState(), () -> BlockRegistry.OAK_LOG, () -> Blocks.OAK_TRAPDOOR.getDefaultState(), () -> Blocks.REDSTONE_WALL_TORCH.getDefaultState());

	public final IBlockStateProvider ceiling;
	public final IBlockStateProvider wall;
	public final IBlockStateProvider wallLog;
	public final IBlockStateProvider floor;
	public final IBlockStateProvider stairs;
	public final IBlockStateProvider ceilingStairs;
	public final IBlockStateProvider floorStairs;
	public final IBlockStateProvider trapDoorDecoration;
	public final IBlockStateProvider torchDark;

	public Theme(IBlockStateProvider ceiling, IBlockStateProvider wall, IBlockStateProvider floor, IBlockStateProvider stairs, IBlockStateProvider ceilingStairs, IBlockStateProvider floorStairs, IBlockStateProvider wallLog,
			IBlockStateProvider trapDoorDecoration, IBlockStateProvider torchDark) {
		this.ceiling = ceiling;
		this.wall = wall;
		this.wallLog = wallLog;
		this.floor = floor;
		this.stairs = stairs;
		this.ceilingStairs = ceilingStairs;
		this.floorStairs = floorStairs;
		this.trapDoorDecoration = trapDoorDecoration;
		this.torchDark = torchDark;
	}

	public Theme(BlockState ceiling, BlockState wall, BlockState floor, BlockState stairs, BlockState ceilingStairs, BlockState floorStairs, BlockState wallLog, BlockState trapDoorDecoration, BlockState torchDark) {
		this.ceiling = () -> ceiling;
		this.wall = () -> wall;
		this.wallLog = () -> wallLog;
		this.floor = () -> floor;
		this.stairs = () -> stairs;
		this.ceilingStairs = () -> ceilingStairs;
		this.floorStairs = () -> floorStairs;
		this.trapDoorDecoration = () -> trapDoorDecoration;
		this.torchDark = () -> torchDark;
	}

}
