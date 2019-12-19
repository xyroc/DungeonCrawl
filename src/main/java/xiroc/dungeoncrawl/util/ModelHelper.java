package xiroc.dungeoncrawl.util;

/*
 * DungeonCrawl (C) 2019 XYROC (XIROC1337), All Rights Reserved 
 */

import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.InputStream;
import java.io.InputStreamReader;

import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import xiroc.dungeoncrawl.DungeonCrawl;
import xiroc.dungeoncrawl.dungeon.segment.DungeonSegmentModel;
import xiroc.dungeoncrawl.dungeon.segment.DungeonSegmentModelBlock;
import xiroc.dungeoncrawl.dungeon.segment.DungeonSegmentModelBlockType;

public class ModelHelper {

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
				((ServerWorld) world).getSaveHandler().getWorldDirectory().getAbsolutePath() + "model_"
						+ System.currentTimeMillis() + ".nbt");
	}

	public static void writeModelToFile(DungeonSegmentModel model, String file) {
		try {
//			FileWriter writer = new FileWriter(file);
//			DungeonCrawl.GSON.toJson(model, writer);
//			writer.flush();
//			writer.close();
			convertModelToNBT(model).write(new DataOutputStream(new FileOutputStream(file)));
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

	public static CompoundNBT convertModelToNBT(DungeonSegmentModel model) {
		byte width = (byte) (model.width > 127 ? -(model.width - 127) : model.width),
				length = (byte) (model.length > 127 ? -(model.length - 127) : model.length),
				height = (byte) (model.height > 127 ? -(model.height - 127) : model.height);

		CompoundNBT newModel = new CompoundNBT();
		newModel.putByte("length", length);
		newModel.putByte("height", height);
		newModel.putByte("width", width);

		ListNBT blocks = new ListNBT();

		for (int x = 0; x < model.width; x++) {
			ListNBT blocks2 = new ListNBT();

			for (int y = 0; y < model.height; y++) {
				ListNBT blocks3 = new ListNBT();

				for (int z = 0; z < model.length; z++) {
					if (model.model[x][y][z] != null)
						blocks3.add(model.model[x][y][z].getAsNBT());
					else
						blocks3.add(new CompoundNBT());
				}
				blocks2.add(blocks3);

			}
			blocks.add(blocks2);

		}

		newModel.put("model", blocks);

		return newModel;
	}

	public static DungeonSegmentModel getModelFromNBT(CompoundNBT nbt) {
		int width = nbt.getInt("width"), height = nbt.getInt("height"), length = nbt.getInt("length");

		ListNBT blocks = nbt.getList("model", 9);

		DungeonSegmentModelBlock[][][] model = new DungeonSegmentModelBlock[width][height][length];

		for (int x = 0; x < width; x++) {
			ListNBT blocks2 = blocks.getList(x);
			for (int y = 0; y < height; y++) {
				ListNBT blocks3 = blocks2.getList(y);
				for (int z = 0; z < length; z++)
					model[x][y][z] = DungeonSegmentModelBlock.fromNBT(blocks3.getCompound(z));
			}
		}

		return new DungeonSegmentModel(model);
	}

}
