package xiroc.dungeoncrawl.dungeon.segment;

import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.util.Rotation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import xiroc.dungeoncrawl.DungeonCrawl;
import xiroc.dungeoncrawl.part.block.Chest;
import xiroc.dungeoncrawl.part.block.Spawner;
import xiroc.dungeoncrawl.theme.Theme;

public class DungeonSegmentModel {

	public int width;
	public int height;
	public int length;
	public DungeonSegmentModelBlock[][][] model;
	public DungeonSegmentModelFourWayBlock[] fourWayBlocks;
	public DungeonSegmentModelTrapDoorBlock[] trapDoors;

	public DungeonSegmentModel() {
		this.model = new DungeonSegmentModelBlock[8][8][8];
		this.fourWayBlocks = new DungeonSegmentModelFourWayBlock[64];
		this.trapDoors = new DungeonSegmentModelTrapDoorBlock[16];
		this.width = this.height = this.length = 8;
	}

	public DungeonSegmentModel(DungeonSegmentModelBlock[][][] model) {
		this.model = model;
		this.fourWayBlocks = new DungeonSegmentModelFourWayBlock[64];
		this.trapDoors = new DungeonSegmentModelTrapDoorBlock[16];
		this.width = model.length;
		this.height = model[0].length;
		this.length = model[0][0].length;
	}

	public DungeonSegmentModel(DungeonSegmentModelBlock[][][] model, DungeonSegmentModelTrapDoorBlock[] trapDoors,
			DungeonSegmentModelFourWayBlock[] fourWayBlocks) {
		this.model = model;
		this.fourWayBlocks = fourWayBlocks;
		this.trapDoors = trapDoors;
		this.width = model.length;
		this.height = model[0].length;
		this.length = model[0][0].length;
	}

	public static void build(DungeonSegmentModel model, World world, BlockPos pos, Theme theme) {
		int fwb = 0;
		int td = 0;
		for (int x = 0; x < model.width; x++) {
			for (int y = 0; y < model.height; y++) {
				for (int z = 0; z < model.length; z++) {
					BlockState state;
					if (model.model[x][y][z] == null)
						state = Blocks.AIR.getDefaultState();
					else if (model.model[x][y][z].type == DungeonSegmentModelBlockType.FWB_PLACEHOLDER)
						state = DungeonSegmentModelBlock.getBlockState(model.fourWayBlocks[fwb++], theme);
					else if (model.model[x][y][z].type == DungeonSegmentModelBlockType.TRAPDOOR)
						state = DungeonSegmentModelBlock.getBlockState(model.trapDoors[td++], theme);
					else
						state = DungeonSegmentModelBlock.getBlockState(model.model[x][y][z], theme);
					if (state == null)
						continue;
					setupBlockState(state, world, new BlockPos(pos.getX() + x, pos.getY() + y, pos.getZ() + z));
					// world.setBlockState(new BlockPos(pos.getX() + x, pos.getY() + y, pos.getZ() +
					// z), DungeonSegmentModelBlock.getBlockState(model.model[x][y][z],
					// Theme.DEFAULT));
				}
			}
		}
	}

	public static void buildRotated(DungeonSegmentModel model, World world, BlockPos pos, Theme theme,
			Rotation rotation) {
		int fwb = 0;
		int td = 0;
		switch (rotation) {
		case CLOCKWISE_90:
			for (int x = 0; x < model.width; x++) {
				for (int y = 0; y < model.height; y++) {
					for (int z = 0; z < model.length; z++) {
						BlockState state;
						if (model.model[x][y][z] == null)
							state = Blocks.AIR.getDefaultState();
						else if (model.model[x][y][z].type == DungeonSegmentModelBlockType.FWB_PLACEHOLDER)
							state = DungeonSegmentModelBlock.getBlockState(model.fourWayBlocks[fwb++], theme,
									Rotation.CLOCKWISE_90);
						else if (model.model[x][y][z].type == DungeonSegmentModelBlockType.TRAPDOOR)
							state = DungeonSegmentModelBlock.getBlockState(model.trapDoors[td++], theme,
									Rotation.CLOCKWISE_90);
						else
							state = DungeonSegmentModelBlock.getBlockState(model.model[x][y][z], theme,
									Rotation.CLOCKWISE_90);
						if (state == null)
							continue;
						setupBlockState(state, world,
								new BlockPos(pos.getX() + model.length - z - 1, pos.getY() + y, pos.getZ() + x));
						// world.setBlockState(new BlockPos(pos.getX() + DungeonSegment.SIZE - z - 1,
						// pos.getY() + y, pos.getZ() + x),
						// DungeonSegmentModelBlock.getBlockState(model.model[x][y][z], Theme.DEFAULT,
						// Rotation.CLOCKWISE_90));
					}
				}
			}
			break;
		case COUNTERCLOCKWISE_90:
			for (int x = 0; x < model.width; x++) {
				for (int y = 0; y < model.height; y++) {
					for (int z = 0; z < model.length; z++) {
						BlockState state;
						if (model.model[x][y][z] == null)
							state = Blocks.AIR.getDefaultState();
						else if (model.model[x][y][z].type == DungeonSegmentModelBlockType.FWB_PLACEHOLDER)
							state = DungeonSegmentModelBlock.getBlockState(model.fourWayBlocks[fwb++], theme,
									Rotation.COUNTERCLOCKWISE_90);
						else if (model.model[x][y][z].type == DungeonSegmentModelBlockType.TRAPDOOR)
							state = DungeonSegmentModelBlock.getBlockState(model.trapDoors[td++], theme,
									Rotation.COUNTERCLOCKWISE_90);
						else
							state = DungeonSegmentModelBlock.getBlockState(model.model[x][y][z], theme,
									Rotation.COUNTERCLOCKWISE_90);
						if (state == null)
							continue;
						setupBlockState(state, world,
								new BlockPos(pos.getX() + z, pos.getY() + y, pos.getZ() + model.width - x - 1));
						// world.setBlockState(new BlockPos(pos.getX() + z, pos.getY() + y, pos.getZ() +
						// DungeonSegment.SIZE - x - 1),
						// DungeonSegmentModelBlock.getBlockState(model.model[x][y][z], Theme.DEFAULT,
						// Rotation.COUNTERCLOCKWISE_90));
					}
				}
			}
			break;
		case CLOCKWISE_180:
			for (int x = 0; x < model.width; x++) {
				for (int y = 0; y < model.height; y++) {
					for (int z = 0; z < model.length; z++) {
						BlockState state;
						if (model.model[x][y][z] == null)
							state = Blocks.AIR.getDefaultState();
						else if (model.model[x][y][z].type == DungeonSegmentModelBlockType.FWB_PLACEHOLDER)
							state = DungeonSegmentModelBlock.getBlockState(model.fourWayBlocks[fwb++], theme,
									Rotation.CLOCKWISE_180);
						else if (model.model[x][y][z].type == DungeonSegmentModelBlockType.TRAPDOOR)
							state = DungeonSegmentModelBlock.getBlockState(model.trapDoors[td++], theme,
									Rotation.CLOCKWISE_180);
						else
							state = DungeonSegmentModelBlock.getBlockState(model.model[x][y][z], theme,
									Rotation.CLOCKWISE_180);
						if (state == null)
							continue;
						setupBlockState(state, world, new BlockPos(pos.getX() + model.width - x - 1, pos.getY() + y,
								pos.getZ() + model.length - z - 1));
						// world.setBlockState(new BlockPos(pos.getX() + DungeonSegment.SIZE - x - 1,
						// pos.getY() + y, pos.getZ() + DungeonSegment.SIZE - z - 1),
						// DungeonSegmentModelBlock.getBlockState(model.model[x][y][z], Theme.DEFAULT,
						// Rotation.CLOCKWISE_180));
					}
				}
			}
			break;
		case NONE:
			build(model, world, pos, theme);
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
			Chest.setupChest(world, state, pos, 0, world.getSeed()); // TODO Lootlevel
			return;
		}
		world.setBlockState(pos, state);
	}

	public BlockState[][][] transform() {
		return null;
	}

}
