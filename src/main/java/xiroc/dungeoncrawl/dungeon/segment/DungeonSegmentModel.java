package xiroc.dungeoncrawl.dungeon.segment;

import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.state.properties.Half;
import net.minecraft.util.Direction;
import net.minecraft.util.Rotation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import xiroc.dungeoncrawl.DungeonCrawl;
import xiroc.dungeoncrawl.build.block.Chest;
import xiroc.dungeoncrawl.build.block.Spawner;
import xiroc.dungeoncrawl.theme.Theme;

public class DungeonSegmentModel {

	public static final DungeonSegmentModelBlock NONE = new DungeonSegmentModelBlock(DungeonSegmentModelBlockType.NONE, Direction.NORTH, false);

	public static final DungeonSegmentModelBlock TORCH_DARK_NORTH = new DungeonSegmentModelBlock(DungeonSegmentModelBlockType.TORCH_DARK, Direction.NORTH, false);
	public static final DungeonSegmentModelBlock TORCH_DARK_EAST = new DungeonSegmentModelBlock(DungeonSegmentModelBlockType.TORCH_DARK, Direction.EAST, false);
	public static final DungeonSegmentModelBlock TORCH_DARK_SOUTH = new DungeonSegmentModelBlock(DungeonSegmentModelBlockType.TORCH_DARK, Direction.SOUTH, false);
	public static final DungeonSegmentModelBlock TORCH_DARK_WEST = new DungeonSegmentModelBlock(DungeonSegmentModelBlockType.TORCH_DARK, Direction.WEST, false);

	public static final DungeonSegmentModelBlock CEILING = new DungeonSegmentModelBlock(DungeonSegmentModelBlockType.CEILING, Direction.NORTH, false);
	public static final DungeonSegmentModelBlock CEILING_STAIRS_NORTH = new DungeonSegmentModelBlock(DungeonSegmentModelBlockType.CEILING_STAIRS, Direction.NORTH, false);
	public static final DungeonSegmentModelBlock CEILING_STAIRS_EAST = new DungeonSegmentModelBlock(DungeonSegmentModelBlockType.CEILING_STAIRS, Direction.EAST, false);
	public static final DungeonSegmentModelBlock CEILING_STAIRS_SOUTH = new DungeonSegmentModelBlock(DungeonSegmentModelBlockType.CEILING_STAIRS, Direction.SOUTH, false);
	public static final DungeonSegmentModelBlock CEILING_STAIRS_WEST = new DungeonSegmentModelBlock(DungeonSegmentModelBlockType.CEILING_STAIRS, Direction.WEST, false);
	public static final DungeonSegmentModelBlock CEILING_STAIRS_NORTH_UD = new DungeonSegmentModelBlock(DungeonSegmentModelBlockType.CEILING_STAIRS, Direction.NORTH, true);
	public static final DungeonSegmentModelBlock CEILING_STAIRS_EAST_UD = new DungeonSegmentModelBlock(DungeonSegmentModelBlockType.CEILING_STAIRS, Direction.EAST, true);
	public static final DungeonSegmentModelBlock CEILING_STAIRS_SOUTH_UD = new DungeonSegmentModelBlock(DungeonSegmentModelBlockType.CEILING_STAIRS, Direction.SOUTH, true);
	public static final DungeonSegmentModelBlock CEILING_STAIRS_WEST_UD = new DungeonSegmentModelBlock(DungeonSegmentModelBlockType.CEILING_STAIRS, Direction.WEST, true);

	public static final DungeonSegmentModelBlock WALL = new DungeonSegmentModelBlock(DungeonSegmentModelBlockType.WALL, Direction.NORTH, false);
	public static final DungeonSegmentModelBlock WALL_LOG = new DungeonSegmentModelBlock(DungeonSegmentModelBlockType.WALL_LOG, Direction.UP, false);

	public static final DungeonSegmentModelBlock FLOOR = new DungeonSegmentModelBlock(DungeonSegmentModelBlockType.FLOOR, Direction.NORTH, false);
	public static final DungeonSegmentModelBlock FLOOR_STAIRS_NORTH = new DungeonSegmentModelBlock(DungeonSegmentModelBlockType.FLOOR_STAIRS, Direction.NORTH, false);
	public static final DungeonSegmentModelBlock FLOOR_STAIRS_EAST = new DungeonSegmentModelBlock(DungeonSegmentModelBlockType.FLOOR_STAIRS, Direction.EAST, false);
	public static final DungeonSegmentModelBlock FLOOR_STAIRS_SOUTH = new DungeonSegmentModelBlock(DungeonSegmentModelBlockType.FLOOR_STAIRS, Direction.SOUTH, false);
	public static final DungeonSegmentModelBlock FLOOR_STAIRS_WEST = new DungeonSegmentModelBlock(DungeonSegmentModelBlockType.FLOOR_STAIRS, Direction.WEST, false);
	public static final DungeonSegmentModelBlock FLOOR_STAIRS_NORTH_UD = new DungeonSegmentModelBlock(DungeonSegmentModelBlockType.FLOOR_STAIRS, Direction.NORTH, true);
	public static final DungeonSegmentModelBlock FLOOR_STAIRS_EAST_UD = new DungeonSegmentModelBlock(DungeonSegmentModelBlockType.FLOOR_STAIRS, Direction.EAST, true);
	public static final DungeonSegmentModelBlock FLOOR_STAIRS_SOUTH_UD = new DungeonSegmentModelBlock(DungeonSegmentModelBlockType.FLOOR_STAIRS, Direction.SOUTH, true);
	public static final DungeonSegmentModelBlock FLOOR_STAIRS_WEST_UD = new DungeonSegmentModelBlock(DungeonSegmentModelBlockType.FLOOR_STAIRS, Direction.WEST, true);

	public static final DungeonSegmentModelBlock SPAWNER = new DungeonSegmentModelBlock(DungeonSegmentModelBlockType.SPAWNER, Direction.NORTH, false);
	public static final DungeonSegmentModelBlock CHEST_COMMON_NORTH = new DungeonSegmentModelBlock(DungeonSegmentModelBlockType.CHEST_COMMON, Direction.NORTH, false);
	public static final DungeonSegmentModelBlock CHEST_COMMON_EAST = new DungeonSegmentModelBlock(DungeonSegmentModelBlockType.CHEST_COMMON, Direction.EAST, false);
	public static final DungeonSegmentModelBlock CHEST_COMMON_SOUTH = new DungeonSegmentModelBlock(DungeonSegmentModelBlockType.CHEST_COMMON, Direction.SOUTH, false);
	public static final DungeonSegmentModelBlock CHEST_COMMON_WEST = new DungeonSegmentModelBlock(DungeonSegmentModelBlockType.CHEST_COMMON, Direction.WEST, false);
	public static final DungeonSegmentModelBlock RND_CC_FLOOR_SPWN_NORTH = new DungeonSegmentModelBlock(DungeonSegmentModelBlockType.RAND_FLOOR_CHESTCOMMON_SPAWNER, Direction.NORTH, false);
	public static final DungeonSegmentModelBlock RND_CC_FLOOR_SPWN_EAST = new DungeonSegmentModelBlock(DungeonSegmentModelBlockType.RAND_FLOOR_CHESTCOMMON_SPAWNER, Direction.EAST, false);
	public static final DungeonSegmentModelBlock RND_CC_FLOOR_SPWN_SOUTH = new DungeonSegmentModelBlock(DungeonSegmentModelBlockType.RAND_FLOOR_CHESTCOMMON_SPAWNER, Direction.SOUTH, false);
	public static final DungeonSegmentModelBlock RND_CC_FLOOR_SPWN_WEST = new DungeonSegmentModelBlock(DungeonSegmentModelBlockType.RAND_FLOOR_CHESTCOMMON_SPAWNER, Direction.WEST, false);

	public static final DungeonSegmentModelTrapDoorBlock TRAPDOOR_NORTH = new DungeonSegmentModelTrapDoorBlock(DungeonSegmentModelBlockType.TRAPDOOR, Direction.NORTH, true, Half.BOTTOM, false);
	public static final DungeonSegmentModelTrapDoorBlock TRAPDOOR_EAST = new DungeonSegmentModelTrapDoorBlock(DungeonSegmentModelBlockType.TRAPDOOR, Direction.EAST, true, Half.BOTTOM, false);
	public static final DungeonSegmentModelTrapDoorBlock TRAPDOOR_SOUTH = new DungeonSegmentModelTrapDoorBlock(DungeonSegmentModelBlockType.TRAPDOOR, Direction.SOUTH, true, Half.BOTTOM, false);
	public static final DungeonSegmentModelTrapDoorBlock TRAPDOOR_WEST = new DungeonSegmentModelTrapDoorBlock(DungeonSegmentModelBlockType.TRAPDOOR, Direction.WEST, true, Half.BOTTOM, false);

	public static final DungeonSegmentModelTrapDoorBlock TRAPDOOR_CLOSED_NORTH = new DungeonSegmentModelTrapDoorBlock(DungeonSegmentModelBlockType.TRAPDOOR, Direction.NORTH, false, Half.BOTTOM, false);
	public static final DungeonSegmentModelTrapDoorBlock TRAPDOOR_CLOSED_EAST = new DungeonSegmentModelTrapDoorBlock(DungeonSegmentModelBlockType.TRAPDOOR, Direction.EAST, false, Half.BOTTOM, false);
	public static final DungeonSegmentModelTrapDoorBlock TRAPDOOR_CLOSED_SOUTH = new DungeonSegmentModelTrapDoorBlock(DungeonSegmentModelBlockType.TRAPDOOR, Direction.SOUTH, false, Half.BOTTOM, false);
	public static final DungeonSegmentModelTrapDoorBlock TRAPDOOR_CLOSED_WEST = new DungeonSegmentModelTrapDoorBlock(DungeonSegmentModelBlockType.TRAPDOOR, Direction.WEST, false, Half.BOTTOM, false);

	public static final DungeonSegmentModel CORRIDOR_EAST_WEST = new DungeonSegmentModel(new DungeonSegmentModelBlock[][][] {
			{ /* x0 */ { WALL, WALL, WALL, WALL, WALL, WALL, WALL, WALL }, { WALL, WALL, FLOOR, FLOOR, FLOOR, FLOOR, WALL, WALL }, { WALL, WALL, CEILING_STAIRS_NORTH, null, null, CEILING_STAIRS_SOUTH, WALL, WALL },
					{ WALL, WALL, null, null, null, null, WALL, WALL }, { WALL, WALL, CEILING_STAIRS_NORTH_UD, null, null, CEILING_STAIRS_SOUTH_UD, WALL, WALL }, { WALL, WALL, WALL, WALL, WALL, WALL, WALL, WALL },
					{ NONE, NONE, NONE, NONE, NONE, NONE, NONE, NONE }, { NONE, NONE, NONE, NONE, NONE, NONE, NONE, NONE } },
			{ /* x1 */ { WALL, WALL, WALL, WALL, WALL, WALL, WALL, WALL }, { WALL, WALL, FLOOR, FLOOR, FLOOR, FLOOR, WALL, WALL }, { WALL, FLOOR_STAIRS_EAST, null, null, null, null, FLOOR_STAIRS_EAST, WALL },
					{ WALL, null, null, null, null, null, null, WALL }, { WALL, CEILING_STAIRS_EAST_UD, null, null, null, null, CEILING_STAIRS_EAST_UD, WALL }, { WALL, WALL, WALL, WALL, WALL, WALL, WALL, WALL },
					{ NONE, NONE, NONE, NONE, NONE, NONE, NONE, NONE }, { NONE, NONE, NONE, NONE, NONE, NONE, NONE, NONE } },
			{ /* x2 */ { WALL, WALL, WALL, WALL, WALL, WALL, WALL, WALL }, { WALL, WALL, RND_CC_FLOOR_SPWN_SOUTH, FLOOR, FLOOR, RND_CC_FLOOR_SPWN_NORTH, WALL, WALL },
					{ WALL, WALL_LOG, TRAPDOOR_SOUTH, null, null, TRAPDOOR_NORTH, WALL_LOG, WALL }, { WALL, SPAWNER, null, null, null, null, WALL_LOG, WALL },
					{ WALL, WALL_LOG, CEILING_STAIRS_NORTH_UD, null, null, CEILING_STAIRS_SOUTH_UD, WALL_LOG, WALL }, { WALL, WALL, WALL, WALL, WALL, WALL, WALL, WALL }, { NONE, NONE, NONE, NONE, NONE, NONE, NONE, NONE },
					{ NONE, NONE, NONE, NONE, NONE, NONE, NONE, NONE } },
			{ /* x3 */ { WALL, WALL, WALL, WALL, WALL, WALL, WALL, WALL }, { WALL, WALL, FLOOR, FLOOR, FLOOR, FLOOR, WALL, WALL }, { WALL, FLOOR_STAIRS_WEST, null, null, null, null, FLOOR_STAIRS_WEST, WALL },
					{ WALL, null, null, null, null, null, null, WALL }, { WALL, CEILING_STAIRS_WEST_UD, null, null, null, null, CEILING_STAIRS_WEST_UD, WALL }, { WALL, WALL, WALL, WALL, WALL, WALL, WALL, WALL },
					{ NONE, NONE, NONE, NONE, NONE, NONE, NONE, NONE }, { NONE, NONE, NONE, NONE, NONE, NONE, NONE, NONE } },
			{ /* x4 */ { WALL, WALL, WALL, WALL, WALL, WALL, WALL, WALL }, { WALL, WALL, FLOOR, FLOOR, FLOOR, FLOOR, WALL, WALL }, { WALL, FLOOR_STAIRS_EAST, null, null, null, null, FLOOR_STAIRS_EAST, WALL },
					{ WALL, null, null, null, null, null, null, WALL }, { WALL, CEILING_STAIRS_EAST_UD, null, null, null, null, CEILING_STAIRS_EAST_UD, WALL }, { WALL, WALL, WALL, WALL, WALL, WALL, WALL, WALL },
					{ NONE, NONE, NONE, NONE, NONE, NONE, NONE, NONE }, { NONE, NONE, NONE, NONE, NONE, NONE, NONE, NONE } },
			{ /* x5 */ { WALL, WALL, WALL, WALL, WALL, WALL, WALL, WALL }, { WALL, WALL, RND_CC_FLOOR_SPWN_SOUTH, FLOOR, FLOOR, RND_CC_FLOOR_SPWN_NORTH, WALL, WALL },
					{ WALL, WALL_LOG, TRAPDOOR_SOUTH, null, null, TRAPDOOR_NORTH, WALL_LOG, WALL }, { WALL, WALL_LOG, null, null, null, null, WALL_LOG, WALL },
					{ WALL, WALL_LOG, CEILING_STAIRS_NORTH_UD, null, null, CEILING_STAIRS_SOUTH_UD, WALL_LOG, WALL }, { WALL, WALL, WALL, WALL, WALL, WALL, WALL, WALL }, { NONE, NONE, NONE, NONE, NONE, NONE, NONE, NONE },
					{ NONE, NONE, NONE, NONE, NONE, NONE, NONE, NONE } },
			{ /* x6 */ { WALL, WALL, WALL, WALL, WALL, WALL, WALL, WALL }, { WALL, WALL, FLOOR, FLOOR, FLOOR, FLOOR, WALL, WALL }, { WALL, FLOOR_STAIRS_WEST, null, null, null, null, FLOOR_STAIRS_WEST, WALL },
					{ WALL, null, null, null, null, null, null, WALL }, { WALL, CEILING_STAIRS_WEST_UD, null, null, null, null, CEILING_STAIRS_WEST_UD, WALL }, { WALL, WALL, WALL, WALL, WALL, WALL, WALL, WALL },
					{ NONE, NONE, NONE, NONE, NONE, NONE, NONE, NONE }, { NONE, NONE, NONE, NONE, NONE, NONE, NONE, NONE } },
			{ /* x7 */ { WALL, WALL, WALL, WALL, WALL, WALL, WALL, WALL }, { WALL, WALL, FLOOR, FLOOR, FLOOR, FLOOR, WALL, WALL }, { WALL, WALL, CEILING_STAIRS_NORTH, null, null, CEILING_STAIRS_SOUTH, WALL, WALL },
					{ WALL, WALL, null, null, null, null, WALL, WALL }, { WALL, WALL, CEILING_STAIRS_NORTH_UD, null, null, CEILING_STAIRS_SOUTH_UD, WALL, WALL }, { WALL, WALL, WALL, WALL, WALL, WALL, WALL, WALL },
					{ NONE, NONE, NONE, NONE, NONE, NONE, NONE, NONE }, { NONE, NONE, NONE, NONE, NONE, NONE, NONE, NONE } } });

	public static final DungeonSegmentModel CORRIDOR_EAST_WEST_OPEN = new DungeonSegmentModel(new DungeonSegmentModelBlock[][][] {
			{ /* x0 */ { WALL, WALL, WALL, WALL, WALL, WALL, WALL, WALL }, { WALL, WALL, FLOOR, FLOOR, FLOOR, FLOOR, WALL, WALL }, { WALL, WALL, CEILING_STAIRS_NORTH, null, null, CEILING_STAIRS_SOUTH, WALL, WALL },
					{ WALL, WALL, null, null, null, null, WALL, WALL }, { WALL, WALL, CEILING_STAIRS_NORTH_UD, null, null, CEILING_STAIRS_SOUTH_UD, WALL, WALL }, { WALL, WALL, WALL, WALL, WALL, WALL, WALL, WALL },
					{ NONE, NONE, NONE, NONE, NONE, NONE, NONE, NONE }, { NONE, NONE, NONE, NONE, NONE, NONE, NONE, NONE } },
			{ /* x1 */{ WALL, WALL, WALL, WALL, WALL, WALL, WALL, WALL }, { WALL, WALL, FLOOR, FLOOR, FLOOR, FLOOR, WALL, WALL }, { WALL, FLOOR_STAIRS_EAST, null, null, null, FLOOR_STAIRS_SOUTH, WALL_LOG, WALL },
					{ WALL, null, null, null, null, null, WALL_LOG, WALL }, { WALL, CEILING_STAIRS_EAST_UD, null, null, null, CEILING_STAIRS_SOUTH_UD, WALL_LOG, WALL }, { WALL, WALL, WALL, WALL, WALL, WALL, WALL, WALL },
					{ NONE, NONE, NONE, NONE, NONE, NONE, NONE, NONE }, { NONE, NONE, NONE, NONE, NONE, NONE, NONE, NONE } },
			{ /* x2 */ { WALL, WALL, WALL, WALL, WALL, WALL, WALL, WALL }, { WALL, WALL, FLOOR, FLOOR, FLOOR, RND_CC_FLOOR_SPWN_NORTH, FLOOR, FLOOR },
					{ WALL, WALL_LOG, TRAPDOOR_SOUTH, null, null, null, FLOOR_STAIRS_WEST, CEILING_STAIRS_WEST }, { WALL, WALL_LOG, null, null, null, null, null, null },
					{ WALL, WALL_LOG, CEILING_STAIRS_NORTH_UD, null, null, null, CEILING_STAIRS_WEST_UD, CEILING_STAIRS_WEST_UD }, { WALL, WALL, WALL, WALL, WALL, WALL, WALL, WALL }, { NONE, NONE, NONE, NONE, NONE, NONE, NONE, NONE },
					{ NONE, NONE, NONE, NONE, NONE, NONE, NONE, NONE } },
			{ /* x3 */ { WALL, WALL, WALL, WALL, WALL, WALL, WALL, WALL }, { WALL, WALL, FLOOR, FLOOR, FLOOR, FLOOR, FLOOR, FLOOR }, { WALL, FLOOR_STAIRS_WEST, null, null, null, null, null, null, null },
					{ WALL, null, null, null, null, null, null, null }, { WALL, CEILING_STAIRS_WEST_UD, null, null, null, null, null, null, null }, { WALL, WALL, WALL, WALL, WALL, WALL, WALL, WALL },
					{ NONE, NONE, NONE, NONE, NONE, NONE, NONE, NONE }, { NONE, NONE, NONE, NONE, NONE, NONE, NONE, NONE } },
			{ /* x4 */ { WALL, WALL, WALL, WALL, WALL, WALL, WALL, WALL }, { WALL, WALL, FLOOR, FLOOR, FLOOR, FLOOR, FLOOR, FLOOR }, { WALL, FLOOR_STAIRS_EAST, null, null, null, null, null, null, null },
					{ WALL, null, null, null, null, null, null, null }, { WALL, CEILING_STAIRS_EAST_UD, null, null, null, null, null, null, null }, { WALL, WALL, WALL, WALL, WALL, WALL, WALL, WALL },
					{ NONE, NONE, NONE, NONE, NONE, NONE, NONE, NONE }, { NONE, NONE, NONE, NONE, NONE, NONE, NONE, NONE } },
			{ /* x5 */ { WALL, WALL, WALL, WALL, WALL, WALL, WALL, WALL }, { WALL, WALL, FLOOR, FLOOR, FLOOR, RND_CC_FLOOR_SPWN_NORTH, FLOOR, FLOOR },
					{ WALL, WALL_LOG, TRAPDOOR_SOUTH, null, null, null, FLOOR_STAIRS_EAST, CEILING_STAIRS_EAST }, { WALL, WALL_LOG, null, null, null, null, null, null },
					{ WALL, WALL_LOG, CEILING_STAIRS_NORTH_UD, null, null, null, CEILING_STAIRS_EAST_UD, CEILING_STAIRS_EAST_UD }, { WALL, WALL, WALL, WALL, WALL, WALL, WALL, WALL }, { NONE, NONE, NONE, NONE, NONE, NONE, NONE, NONE },
					{ NONE, NONE, NONE, NONE, NONE, NONE, NONE, NONE } },
			{ /* x6 */{ WALL, WALL, WALL, WALL, WALL, WALL, WALL, WALL }, { WALL, WALL, FLOOR, FLOOR, FLOOR, FLOOR, WALL, WALL }, { WALL, FLOOR_STAIRS_WEST, null, null, null, FLOOR_STAIRS_SOUTH, WALL_LOG, WALL },
					{ WALL, null, null, null, null, null, WALL_LOG, WALL }, { WALL, CEILING_STAIRS_WEST_UD, null, null, null, CEILING_STAIRS_SOUTH_UD, WALL_LOG, WALL }, { WALL, WALL, WALL, WALL, WALL, WALL, WALL, WALL },
					{ NONE, NONE, NONE, NONE, NONE, NONE, NONE, NONE }, { NONE, NONE, NONE, NONE, NONE, NONE, NONE, NONE } },
			{ /* x7 */ { WALL, WALL, WALL, WALL, WALL, WALL, WALL, WALL }, { WALL, WALL, FLOOR, FLOOR, FLOOR, FLOOR, WALL, WALL }, { WALL, WALL, CEILING_STAIRS_NORTH, null, null, CEILING_STAIRS_SOUTH, WALL, WALL },
					{ WALL, WALL, null, null, null, null, WALL, WALL }, { WALL, WALL, CEILING_STAIRS_NORTH_UD, null, null, CEILING_STAIRS_SOUTH_UD, WALL, WALL }, { WALL, WALL, WALL, WALL, WALL, WALL, WALL, WALL },
					{ NONE, NONE, NONE, NONE, NONE, NONE, NONE, NONE }, { NONE, NONE, NONE, NONE, NONE, NONE, NONE, NONE } } });

	public static final DungeonSegmentModel CORRIDOR_EAST_WEST_ALL_OPEN = new DungeonSegmentModel(new DungeonSegmentModelBlock[][][] { { /* x0 */ { WALL, WALL, WALL, WALL, WALL, WALL, WALL, WALL },
			{ WALL, WALL, FLOOR, FLOOR, FLOOR, FLOOR, WALL, WALL }, { WALL, WALL, CEILING_STAIRS_NORTH, null, null, CEILING_STAIRS_SOUTH, WALL, WALL }, { WALL, WALL, null, null, null, null, WALL, WALL },
			{ WALL, WALL, CEILING_STAIRS_NORTH_UD, null, null, CEILING_STAIRS_SOUTH_UD, WALL, WALL }, { WALL, WALL, WALL, WALL, WALL, WALL, WALL, WALL }, { NONE, NONE, NONE, NONE, NONE, NONE, NONE, NONE },
			{ NONE, NONE, NONE, NONE, NONE, NONE, NONE, NONE } },
			{ /* x1 */ { WALL, WALL, WALL, WALL, WALL, WALL, WALL, WALL }, { WALL, WALL, FLOOR, FLOOR, FLOOR, FLOOR, WALL, WALL }, { WALL, WALL_LOG, FLOOR_STAIRS_NORTH, null, null, FLOOR_STAIRS_SOUTH, WALL_LOG, WALL },
					{ WALL, WALL_LOG, null, null, null, TORCH_DARK_NORTH, WALL_LOG, WALL }, { WALL, WALL_LOG, CEILING_STAIRS_NORTH_UD, null, null, CEILING_STAIRS_SOUTH_UD, WALL_LOG, WALL },
					{ WALL, WALL, WALL, WALL, WALL, WALL, WALL, WALL }, { NONE, NONE, NONE, NONE, NONE, NONE, NONE, NONE }, { NONE, NONE, NONE, NONE, NONE, NONE, NONE, NONE } },
			{ /* x2 */ { WALL, WALL, WALL, WALL, WALL, WALL, WALL, WALL }, { FLOOR, FLOOR, RND_CC_FLOOR_SPWN_SOUTH, FLOOR, FLOOR, RND_CC_FLOOR_SPWN_NORTH, FLOOR, FLOOR },
					{ CEILING_STAIRS_WEST, FLOOR_STAIRS_WEST, TRAPDOOR_CLOSED_EAST, null, null, TRAPDOOR_CLOSED_EAST, FLOOR_STAIRS_WEST, CEILING_STAIRS_WEST }, { null, null, null, null, null, null, null, null },
					{ CEILING_STAIRS_WEST_UD, CEILING_STAIRS_WEST_UD, null, null, null, null, CEILING_STAIRS_WEST_UD, CEILING_STAIRS_WEST_UD }, { WALL, WALL, WALL, WALL, WALL, WALL, WALL, WALL },
					{ NONE, NONE, NONE, NONE, NONE, NONE, NONE, NONE }, { NONE, NONE, NONE, NONE, NONE, NONE, NONE, NONE } },
			{ /* x3 */ { WALL, WALL, WALL, WALL, WALL, WALL, WALL, WALL }, { FLOOR, FLOOR, FLOOR, FLOOR, FLOOR, FLOOR, FLOOR, FLOOR }, { null, null, null, null, null, null, null, null }, { null, null, null, null, null, null, null, null },
					{ null, null, null, null, null, null, null, null }, { WALL, WALL, WALL, WALL, WALL, WALL, WALL, WALL }, { NONE, NONE, NONE, NONE, NONE, NONE, NONE, NONE }, { NONE, NONE, NONE, NONE, NONE, NONE, NONE, NONE } },
			{ /* x4 */ { WALL, WALL, WALL, WALL, WALL, WALL, WALL, WALL }, { FLOOR, FLOOR, FLOOR, FLOOR, FLOOR, FLOOR, FLOOR, FLOOR }, { null, null, null, null, null, null, null, null }, { null, null, null, null, null, null, null, null },
					{ null, null, null, null, null, null, null, null }, { WALL, WALL, WALL, WALL, WALL, WALL, WALL, WALL }, { NONE, NONE, NONE, NONE, NONE, NONE, NONE, NONE }, { NONE, NONE, NONE, NONE, NONE, NONE, NONE, NONE } },
			{ /* x5 */ { WALL, WALL, WALL, WALL, WALL, WALL, WALL, WALL }, { FLOOR, FLOOR, RND_CC_FLOOR_SPWN_SOUTH, FLOOR, FLOOR, RND_CC_FLOOR_SPWN_NORTH, FLOOR, FLOOR },
					{ CEILING_STAIRS_EAST, FLOOR_STAIRS_EAST, TRAPDOOR_CLOSED_WEST, null, null, TRAPDOOR_CLOSED_WEST, FLOOR_STAIRS_EAST, CEILING_STAIRS_EAST }, { null, null, null, null, null, null, null, null },
					{ CEILING_STAIRS_EAST_UD, CEILING_STAIRS_EAST_UD, null, null, null, null, CEILING_STAIRS_EAST_UD, CEILING_STAIRS_EAST_UD }, { WALL, WALL, WALL, WALL, WALL, WALL, WALL, WALL },
					{ NONE, NONE, NONE, NONE, NONE, NONE, NONE, NONE }, { NONE, NONE, NONE, NONE, NONE, NONE, NONE, NONE } },
			{ /* x6 */ { WALL, WALL, WALL, WALL, WALL, WALL, WALL, WALL }, { WALL, WALL, FLOOR, FLOOR, FLOOR, FLOOR, WALL, WALL }, { WALL, WALL_LOG, FLOOR_STAIRS_NORTH, null, null, FLOOR_STAIRS_SOUTH, WALL_LOG, WALL },
					{ WALL, WALL_LOG, TORCH_DARK_SOUTH, null, null, null, WALL_LOG, WALL }, { WALL, WALL_LOG, CEILING_STAIRS_NORTH_UD, null, null, CEILING_STAIRS_SOUTH_UD, WALL_LOG, WALL },
					{ WALL, WALL, WALL, WALL, WALL, WALL, WALL, WALL }, { NONE, NONE, NONE, NONE, NONE, NONE, NONE, NONE }, { NONE, NONE, NONE, NONE, NONE, NONE, NONE, NONE } },
			{ /* x7 */ { WALL, WALL, WALL, WALL, WALL, WALL, WALL, WALL }, { WALL, WALL, FLOOR, FLOOR, FLOOR, FLOOR, WALL, WALL }, { WALL, WALL, CEILING_STAIRS_NORTH, null, null, CEILING_STAIRS_SOUTH, WALL, WALL },
					{ WALL, WALL, null, null, null, null, WALL, WALL }, { WALL, WALL, CEILING_STAIRS_NORTH_UD, null, null, CEILING_STAIRS_SOUTH_UD, WALL, WALL }, { WALL, WALL, WALL, WALL, WALL, WALL, WALL, WALL },
					{ NONE, NONE, NONE, NONE, NONE, NONE, NONE, NONE }, { NONE, NONE, NONE, NONE, NONE, NONE, NONE, NONE } } });

	public static void build(DungeonSegmentModel model, World world, BlockPos pos) {
		for (int x = 0; x < DungeonSegment.SIZE; x++) {
			for (int y = 0; y < DungeonSegment.SIZE; y++) {
				for (int z = 0; z < DungeonSegment.SIZE; z++) {
					setupBlockState(DungeonSegmentModelBlock.getBlockState(model.model[x][y][z], Theme.DEFAULT), world, new BlockPos(pos.getX() + x, pos.getY() + y, pos.getZ() + z));
					// world.setBlockState(new BlockPos(pos.getX() + x, pos.getY() + y, pos.getZ() +
					// z), DungeonSegmentModelBlock.getBlockState(model.model[x][y][z],
					// Theme.DEFAULT));
				}
			}
		}
	}

	public static void buildRotated(DungeonSegmentModel model, World world, BlockPos pos, Rotation rotation) {
		switch (rotation) {
		case CLOCKWISE_90:
			for (int x = 0; x < DungeonSegment.SIZE; x++) {
				for (int y = 0; y < DungeonSegment.SIZE; y++) {
					for (int z = 0; z < DungeonSegment.SIZE; z++) {
						setupBlockState(DungeonSegmentModelBlock.getBlockState(model.model[x][y][z], Theme.DEFAULT, Rotation.CLOCKWISE_90), world, new BlockPos(pos.getX() + DungeonSegment.SIZE - z - 1, pos.getY() + y, pos.getZ() + x));
						// world.setBlockState(new BlockPos(pos.getX() + DungeonSegment.SIZE - z - 1,
						// pos.getY() + y, pos.getZ() + x),
						// DungeonSegmentModelBlock.getBlockState(model.model[x][y][z], Theme.DEFAULT,
						// Rotation.CLOCKWISE_90));
					}
				}
			}
			break;
		case COUNTERCLOCKWISE_90:
			for (int x = 0; x < DungeonSegment.SIZE; x++) {
				for (int y = 0; y < DungeonSegment.SIZE; y++) {
					for (int z = 0; z < DungeonSegment.SIZE; z++) {
						setupBlockState(DungeonSegmentModelBlock.getBlockState(model.model[x][y][z], Theme.DEFAULT, Rotation.COUNTERCLOCKWISE_90), world,
								new BlockPos(pos.getX() + z, pos.getY() + y, pos.getZ() + DungeonSegment.SIZE - x - 1));
						// world.setBlockState(new BlockPos(pos.getX() + z, pos.getY() + y, pos.getZ() +
						// DungeonSegment.SIZE - x - 1),
						// DungeonSegmentModelBlock.getBlockState(model.model[x][y][z], Theme.DEFAULT,
						// Rotation.COUNTERCLOCKWISE_90));
					}
				}
			}
			break;
		case CLOCKWISE_180:
			for (int x = 0; x < DungeonSegment.SIZE; x++) {
				for (int y = 0; y < DungeonSegment.SIZE; y++) {
					for (int z = 0; z < DungeonSegment.SIZE; z++) {
						setupBlockState(DungeonSegmentModelBlock.getBlockState(model.model[x][y][z], Theme.DEFAULT, Rotation.CLOCKWISE_180), world,
								new BlockPos(pos.getX() + DungeonSegment.SIZE - x - 1, pos.getY() + y, pos.getZ() + DungeonSegment.SIZE - z - 1));
						// world.setBlockState(new BlockPos(pos.getX() + DungeonSegment.SIZE - x - 1,
						// pos.getY() + y, pos.getZ() + DungeonSegment.SIZE - z - 1),
						// DungeonSegmentModelBlock.getBlockState(model.model[x][y][z], Theme.DEFAULT,
						// Rotation.CLOCKWISE_180));
					}
				}
			}
			break;
		case NONE:
			build(model, world, pos);
			break;
		default:
			DungeonCrawl.LOGGER.warn("Failed to build a rotated dungeon segment: Unknown rotation " + rotation);
			break;
		}
	}

	public static void setupBlockState(BlockState state, World world, BlockPos pos) {
		if (state == null)
			return;
		if (state.getBlock() == Blocks.SPAWNER) {
			Spawner.setupSpawner(world, pos, Spawner.getRandomEntityType(world.rand));
			return;
		} else if (state.getBlock() == Blocks.CHEST) {
			Chest.setupChest(world, state, pos, 0); // TODO Lootlevel
			return;
		}
		world.setBlockState(pos, state);
	}

	public DungeonSegmentModelBlock[][][] model;

	public DungeonSegmentModel() {
		this.model = new DungeonSegmentModelBlock[8][8][8];
	}

	public DungeonSegmentModel(DungeonSegmentModelBlock[][][] model) {
		this.model = model;
	}

	public BlockState[][][] transform() {
		return null;
	}

}
