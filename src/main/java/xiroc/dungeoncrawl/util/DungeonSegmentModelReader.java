package xiroc.dungeoncrawl.util;

/*
 * DungeonCrawl (C) 2019 XYROC (XIROC1337), All Rights Reserved 
 */

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.InputStream;
import java.io.InputStreamReader;

import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.FourWayBlock;
import net.minecraft.block.TrapDoorBlock;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.state.properties.Half;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import xiroc.dungeoncrawl.DungeonCrawl;
import xiroc.dungeoncrawl.dungeon.segment.DungeonSegmentModel;
import xiroc.dungeoncrawl.dungeon.segment.DungeonSegmentModelBlock;
import xiroc.dungeoncrawl.dungeon.segment.DungeonSegmentModelBlockType;

public class DungeonSegmentModelReader {

	public static void readModelToFile(World world, BlockPos pos, int width, int height, int length) {
		DungeonSegmentModelBlock[][][] model = new DungeonSegmentModelBlock[width][height][length];
		for (int x = 0; x < width; x++) {
			for (int y = 0; y < height; y++) {
				for (int z = 0; z < length; z++) {
					BlockState state = world
							.getBlockState(new BlockPos(pos.getX() + x, pos.getY() + y, pos.getZ() + z));
					if (state.getBlock() == Blocks.AIR) {
						model[x][y][z] = null;
						continue;
					}
					model[x][y][z] = new DungeonSegmentModelBlock(DungeonSegmentModelBlockType.get(state.getBlock()))
							.set(state);
				}
			}
		}
		writeModelToFile(new DungeonSegmentModel(model),
				new File(((ServerWorld) world).getSaveHandler().getWorldDirectory(),
						"model_" + System.currentTimeMillis() + ".json"));
	}

	/**
	 * THIS DOES NOT WORK ANYMORE
	 */
	public static String readModelToArrayString(World world, BlockPos pos, int width, int length, int height) {
		String array = "";
		array += "new DungeonSegmentModelBlock[][][] { ";
		for (int x = 0; x < width; x++) {
			array += "{ ";
			for (int y = 0; y < height; y++) {
				array += "{ ";
				for (int z = 0; z < length; z++) {
					BlockState state = world
							.getBlockState(new BlockPos(pos.getX() + x, pos.getY() + y, pos.getZ() + z));
					if (state.getBlock() instanceof TrapDoorBlock) {
						Direction direction = state.get(BlockStateProperties.HORIZONTAL_FACING);
						boolean open = state.get(BlockStateProperties.OPEN);
						boolean upsideDown = state.get(BlockStateProperties.HALF) == Half.TOP;
						array += DungeonSegmentModelBlockType.get(state.getBlock());
						if (!open)
							array += "_CLOSED";
						if (direction != null)
							array += "_" + direction.toString().toUpperCase();
						if (upsideDown)
							array += "_UD";
						if (z + 1 < length)
							array += ", ";
						continue;
					}
					if (state.getBlock() instanceof FourWayBlock) {
						array += DungeonSegmentModelBlockType.get(state.getBlock());
						if (state.get(BlockStateProperties.NORTH))
							array += "_NORTH";
						if (state.get(BlockStateProperties.EAST))
							array += "_EAST";
						if (state.get(BlockStateProperties.SOUTH))
							array += "_SOUTH";
						if (state.get(BlockStateProperties.WEST))
							array += "_WEST";
						if (state.get(BlockStateProperties.WATERLOGGED))
							array += "_WATERLOGGED";
						if (z + 1 < length)
							array += ", ";
						continue;
					}
					Direction direction = state.has(BlockStateProperties.FACING)
							? state.get(BlockStateProperties.FACING)
							: state.has(BlockStateProperties.HORIZONTAL_FACING)
									? state.get(BlockStateProperties.HORIZONTAL_FACING)
									: null;
					boolean upsideDown = state.has(BlockStateProperties.HALF)
							? state.get(BlockStateProperties.HALF) == Half.TOP
							: false;
					array += DungeonSegmentModelBlockType.get(state.getBlock());
					if (direction != null)
						array += "_" + direction.toString().toUpperCase();
					if (upsideDown)
						array += "_UD";
					if (z + 1 < length)
						array += ", ";
				}
				array += " }";
				if (y + 1 < height)
					array += ", ";
			}
			array += " }";
			if (x + 1 < width)
				array += ", ";
		}
		array += " }";
		return array;
	}

	public static void writeModelToFile(DungeonSegmentModel model, File file) {
		try {
			FileWriter writer = new FileWriter(file);
			DungeonCrawl.GSON.toJson(model, writer);
			writer.flush();
			writer.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static DungeonSegmentModel readModelFromFile(File file) {
		DungeonCrawl.LOGGER.info("Loading model from file " + file.getAbsolutePath());
		try {
			FileReader reader = new FileReader(file);
			return DungeonCrawl.GSON.fromJson(reader, DungeonSegmentModel.class);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	public static DungeonSegmentModel readModelFromInputStream(InputStream input) {
		try {
			InputStreamReader reader = new InputStreamReader(input);
			return DungeonCrawl.GSON.fromJson(reader, DungeonSegmentModel.class);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

}
