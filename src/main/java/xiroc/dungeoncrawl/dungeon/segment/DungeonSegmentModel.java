package xiroc.dungeoncrawl.dungeon.segment;

import net.minecraft.block.BlockState;
import net.minecraft.state.properties.Half;
import net.minecraft.util.Direction;

public class DungeonSegmentModel {

	public static final DungeonSegmentModelBlock CEILING = new DungeonSegmentModelBlock(DungeonSegmentModelBlockType.CEILING, Direction.NORTH, false);
	public static final DungeonSegmentModelBlock CEILING_STAIRS_NORTH = new DungeonSegmentModelBlock(DungeonSegmentModelBlockType.CEILING_STAIRS_NORTH, Direction.NORTH, false);
	public static final DungeonSegmentModelBlock CEILING_STAIRS_EAST = new DungeonSegmentModelBlock(DungeonSegmentModelBlockType.CEILING_STAIRS_EAST, Direction.EAST, false);
	public static final DungeonSegmentModelBlock CEILING_STAIRS_SOUTH = new DungeonSegmentModelBlock(DungeonSegmentModelBlockType.CEILING_STAIRS_SOUTH, Direction.SOUTH, false);
	public static final DungeonSegmentModelBlock CEILING_STAIRS_WEST = new DungeonSegmentModelBlock(DungeonSegmentModelBlockType.CEILING_STAIRS_WEST, Direction.WEST, false);
	public static final DungeonSegmentModelBlock CEILING_STAIRS_NORTH_UD = new DungeonSegmentModelBlock(DungeonSegmentModelBlockType.CEILING_STAIRS_NORTH, Direction.NORTH, true);
	public static final DungeonSegmentModelBlock CEILING_STAIRS_EAST_UD = new DungeonSegmentModelBlock(DungeonSegmentModelBlockType.CEILING_STAIRS_EAST, Direction.EAST, true);
	public static final DungeonSegmentModelBlock CEILING_STAIRS_SOUTH_UD = new DungeonSegmentModelBlock(DungeonSegmentModelBlockType.CEILING_STAIRS_SOUTH, Direction.SOUTH, true);
	public static final DungeonSegmentModelBlock CEILING_STAIRS_WEST_UD = new DungeonSegmentModelBlock(DungeonSegmentModelBlockType.CEILING_STAIRS_WEST, Direction.WEST, true);

	public static final DungeonSegmentModelBlock WALL = new DungeonSegmentModelBlock(DungeonSegmentModelBlockType.WALL, Direction.NORTH, false);
	public static final DungeonSegmentModelBlock WALL_LOG = new DungeonSegmentModelBlock(DungeonSegmentModelBlockType.WALL_LOG, Direction.UP, false);

	public static final DungeonSegmentModelBlock FLOOR = new DungeonSegmentModelBlock(DungeonSegmentModelBlockType.FLOOR, Direction.NORTH, false);
	public static final DungeonSegmentModelBlock FLOOR_STAIRS_NORTH = new DungeonSegmentModelBlock(DungeonSegmentModelBlockType.FLOOR_STAIRS_NORTH, Direction.NORTH, false);
	public static final DungeonSegmentModelBlock FLOOR_STAIRS_EAST = new DungeonSegmentModelBlock(DungeonSegmentModelBlockType.FLOOR_STAIRS_EAST, Direction.EAST, false);
	public static final DungeonSegmentModelBlock FLOOR_STAIRS_SOUTH = new DungeonSegmentModelBlock(DungeonSegmentModelBlockType.FLOOR_STAIRS_SOUTH, Direction.SOUTH, false);
	public static final DungeonSegmentModelBlock FLOOR_STAIRS_WEST = new DungeonSegmentModelBlock(DungeonSegmentModelBlockType.FLOOR_STAIRS_WEST, Direction.WEST, false);
	public static final DungeonSegmentModelBlock FLOOR_STAIRS_NORTH_UD = new DungeonSegmentModelBlock(DungeonSegmentModelBlockType.FLOOR_STAIRS_NORTH, Direction.NORTH, true);
	public static final DungeonSegmentModelBlock FLOOR_STAIRS_EAST_UD = new DungeonSegmentModelBlock(DungeonSegmentModelBlockType.FLOOR_STAIRS_EAST, Direction.EAST, true);
	public static final DungeonSegmentModelBlock FLOOR_STAIRS_SOUTH_UD = new DungeonSegmentModelBlock(DungeonSegmentModelBlockType.FLOOR_STAIRS_SOUTH, Direction.SOUTH, true);
	public static final DungeonSegmentModelBlock FLOOR_STAIRS_WEST_UD = new DungeonSegmentModelBlock(DungeonSegmentModelBlockType.FLOOR_STAIRS_WEST, Direction.WEST, true);

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

	public static final DungeonSegmentModel CORRIDOR_EAST_WEST = new DungeonSegmentModel(new DungeonSegmentModelBlock[][][] {
			{ /* x0 */ { WALL, WALL, WALL, WALL, WALL, WALL, WALL, WALL }, { WALL, WALL, FLOOR, FLOOR, FLOOR, FLOOR, WALL, WALL }, { WALL, WALL, CEILING_STAIRS_NORTH, null, null, CEILING_STAIRS_SOUTH, WALL, WALL },
					{ WALL, WALL, null, null, null, null, WALL, WALL }, { WALL, WALL, CEILING_STAIRS_NORTH_UD, null, null, CEILING_STAIRS_SOUTH_UD, WALL, WALL }, { WALL, WALL, WALL, WALL, WALL, WALL, WALL, WALL },
					{ null, null, null, null, null, null, null, null }, { null, null, null, null, null, null, null, null } },
			{ /* x1 */ { WALL, WALL, WALL, WALL, WALL, WALL, WALL, WALL }, { WALL, WALL, FLOOR, FLOOR, FLOOR, FLOOR, WALL, WALL }, { WALL, FLOOR_STAIRS_WEST, null, null, null, null, FLOOR_STAIRS_WEST, WALL },
					{ WALL, null, null, null, null, null, null, WALL }, { WALL, CEILING_STAIRS_WEST_UD, null, null, null, null, null, null, CEILING_STAIRS_WEST_UD, WALL }, { WALL, WALL, WALL, WALL, WALL, WALL, WALL, WALL },
					{ null, null, null, null, null, null, null, null }, { null, null, null, null, null, null, null, null } },
			{ /* x2 */ { WALL, WALL, WALL, WALL, WALL, WALL, WALL, WALL }, { WALL, WALL, RND_CC_FLOOR_SPWN_SOUTH, FLOOR, FLOOR, RND_CC_FLOOR_SPWN_NORTH, WALL, WALL },
					{ WALL, WALL_LOG, TRAPDOOR_NORTH, null, null, TRAPDOOR_SOUTH, WALL, WALL }, { WALL, WALL_LOG, null, null, null, null, WALL_LOG, WALL }, { WALL, WALL_LOG, null, null, null, null, WALL_LOG, WALL },
					{ WALL, WALL, WALL, WALL, WALL, WALL, WALL, WALL }, { null, null, null, null, null, null, null, null }, { null, null, null, null, null, null, null, null } },
			{ /* x3 */ { WALL, WALL, WALL, WALL, WALL, WALL, WALL, WALL }, { WALL, WALL, FLOOR, FLOOR, FLOOR, FLOOR, WALL, WALL }, { WALL, FLOOR_STAIRS_EAST, null, null, null, null, FLOOR_STAIRS_EAST, WALL },
					{ WALL, null, null, null, null, null, null, WALL }, { WALL, CEILING_STAIRS_EAST_UD, null, null, null, null, CEILING_STAIRS_EAST_UD, WALL }, { WALL, WALL, WALL, WALL, WALL, WALL, WALL, WALL },
					{ null, null, null, null, null, null, null, null }, { null, null, null, null, null, null, null, null } },
			{ /* x4 */ { WALL, WALL, WALL, WALL, WALL, WALL, WALL, WALL }, { WALL, WALL, FLOOR, FLOOR, FLOOR, FLOOR, WALL, WALL }, { WALL, FLOOR_STAIRS_WEST, null, null, null, null, FLOOR_STAIRS_WEST, WALL },
					{ WALL, null, null, null, null, null, null, WALL }, { WALL, CEILING_STAIRS_WEST_UD, null, null, null, null, CEILING_STAIRS_WEST_UD, WALL }, { WALL, WALL, WALL, WALL, WALL, WALL, WALL, WALL },
					{ null, null, null, null, null, null, null, null }, { null, null, null, null, null, null, null, null } },
			{ /* x5 */ { WALL, WALL, WALL, WALL, WALL, WALL, WALL, WALL }, { WALL, WALL, RND_CC_FLOOR_SPWN_SOUTH, FLOOR, FLOOR, RND_CC_FLOOR_SPWN_NORTH, WALL, WALL },
					{ WALL, WALL_LOG, TRAPDOOR_NORTH, null, null, TRAPDOOR_SOUTH, WALL, WALL }, { WALL, WALL_LOG, null, null, null, null, WALL_LOG, WALL }, { WALL, WALL_LOG, null, null, null, null, WALL_LOG, WALL },
					{ WALL, WALL, WALL, WALL, WALL, WALL, WALL, WALL }, { null, null, null, null, null, null, null, null }, { null, null, null, null, null, null, null, null } },
			{ /* x6 */ { WALL, WALL, WALL, WALL, WALL, WALL, WALL, WALL }, { WALL, WALL, FLOOR, FLOOR, FLOOR, FLOOR, WALL, WALL }, { WALL, FLOOR_STAIRS_EAST, null, null, null, null, FLOOR_STAIRS_EAST, WALL },
					{ WALL, null, null, null, null, null, null, WALL }, { WALL, CEILING_STAIRS_EAST_UD, null, null, null, null, CEILING_STAIRS_EAST_UD, WALL }, { WALL, WALL, WALL, WALL, WALL, WALL, WALL, WALL },
					{ null, null, null, null, null, null, null, null }, { null, null, null, null, null, null, null, null } },
			{ /* x7 */ { WALL, WALL, WALL, WALL, WALL, WALL, WALL, WALL }, { WALL, WALL, FLOOR, FLOOR, FLOOR, FLOOR, WALL, WALL }, { WALL, WALL, CEILING_STAIRS_NORTH, null, null, CEILING_STAIRS_SOUTH, WALL, WALL },
					{ WALL, WALL, null, null, null, null, WALL, WALL }, { WALL, WALL, CEILING_STAIRS_NORTH_UD, null, null, CEILING_STAIRS_SOUTH_UD, WALL, WALL }, { WALL, WALL, WALL, WALL, WALL, WALL, WALL, WALL },
					{ null, null, null, null, null, null, null, null }, { null, null, null, null, null, null, null, null } } });

	public ISegmentBlock[][][] model;

	public DungeonSegmentModel() {
		this.model = new ISegmentBlock[8][8][8];
	}

	public DungeonSegmentModel(ISegmentBlock[][][] model) {
		this.model = model;
	}

	public BlockState[][][] transform() {
		return null;
	}

}
