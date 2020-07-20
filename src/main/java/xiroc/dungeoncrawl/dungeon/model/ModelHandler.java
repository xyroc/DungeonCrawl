package xiroc.dungeoncrawl.dungeon.model;

/*
 *
 * DungeonCrawl (C) 2019 - 2020 XYROC (XIROC1337), All Rights Reserved
 */

import com.google.common.collect.Lists;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import xiroc.dungeoncrawl.DungeonCrawl;
import xiroc.dungeoncrawl.dungeon.model.DungeonModel.FeaturePosition;

import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.util.List;
import java.util.Locale;

public class ModelHandler {

    public static void readAndSaveModelToFile(String name, World world, BlockPos pos, int width, int height, int length,
                                              int spawnerType, int chestType) {
        DungeonCrawl.LOGGER.info("Reading and saving {} to disk. Size: {}, {}, {}", name, width, height, length);
        DungeonModelBlock[][][] model = new DungeonModelBlock[width][height][length];

        List<FeaturePosition> featurePositions = Lists.newArrayList();

        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                for (int z = 0; z < length; z++) {
                    BlockState state = world.getBlockState(new BlockPos(pos.getX() + x, pos.getY() + y, pos.getZ() + z));
                    if (state.getBlock() == Blocks.AIR || state.getBlock() == Blocks.BEDROCK) {
                        model[x][y][z] = null;
                        continue;
                    } else if (state.getBlock() == Blocks.JIGSAW) {
                        //DungeonCrawl.LOGGER.debug("Found a feature position at {} {} {}", x, y, z);
                        featurePositions.add(new FeaturePosition(x, y, z, state.get(BlockStateProperties.FACING)));
                        //model[x][y][z] = new DungeonModelBlock(DungeonModelBlockType.NONE);
                        continue;
                    }
                    model[x][y][z] = new DungeonModelBlock(
                            DungeonModelBlockType.get(state.getBlock(), spawnerType, chestType))
                            .loadDataFromState(state);
                }
            }
        }
        writeModelToFile(
                new DungeonModel(model,
                        featurePositions.isEmpty() ? null
                                : featurePositions.toArray(new FeaturePosition[0])),
                ((ServerWorld) world).getServer().getDataDirectory().getAbsolutePath() + "/models/" + name + ".nbt");
    }

    public static void writeModelToFile(DungeonModel model, String file) {
        try {
            DungeonCrawl.LOGGER.info("Writing a model to disk at {}. ", file);
            if (model.featurePositions != null)
                DungeonCrawl.LOGGER.info("There are {} feature positions.", model.featurePositions.length);
            File f = new File(file);
            if (!f.getParentFile().exists()) {
                f.getParentFile().mkdirs();
            }
            convertModelToNBT(model).write(new DataOutputStream(new FileOutputStream(file)));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static CompoundNBT convertModelToNBT(DungeonModel model) {
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

        if (model.featurePositions != null && model.featurePositions.length > 0) {
            ListNBT list = new ListNBT();
            CompoundNBT amount = new CompoundNBT();
            amount.putInt("amount", model.featurePositions.length);
            list.add(amount);
            for (FeaturePosition pos : model.featurePositions) {
                CompoundNBT vecCompound = new CompoundNBT();
                vecCompound.putInt("x", pos.position.getX());
                vecCompound.putInt("y", pos.position.getY());
                vecCompound.putInt("z", pos.position.getZ());
                if (pos.facing != null)
                    vecCompound.putString("facing", pos.facing.toString());
                list.add(vecCompound);
            }
            newModel.put("featurePositions", list);
        }

        newModel.put("model", blocks);

        return newModel;
    }

    public static DungeonModel getModelFromNBT(CompoundNBT nbt) {
        int width = nbt.getInt("width"), height = nbt.getInt("height"), length = nbt.getInt("length");

        ListNBT blocks = nbt.getList("model", 9);

        DungeonModelBlock[][][] model = new DungeonModelBlock[width][height][length];

        for (int x = 0; x < width; x++) {
            ListNBT blocks2 = blocks.getList(x);
            for (int y = 0; y < height; y++) {
                ListNBT blocks3 = blocks2.getList(y);
                for (int z = 0; z < length; z++)
                    model[x][y][z] = DungeonModelBlock.fromNBT(blocks3.getCompound(z));
            }
        }

        FeaturePosition[] featurePositions = null;

        if (nbt.contains("featurePositions", 9)) {
            ListNBT list = nbt.getList("featurePositions", 10);
            int amount = list.getCompound(0).getInt("amount");
            featurePositions = new FeaturePosition[amount];
            for (int i = 1; i < list.size(); i++) {
                CompoundNBT compound = list.getCompound(i);
                if (compound.contains("facing")) {
                    featurePositions[i - 1] = new FeaturePosition(compound.getInt("x"), compound.getInt("y"),
                            compound.getInt("z"),
                            Direction.valueOf(compound.getString("facing").toUpperCase(Locale.ROOT)));
                } else {
                    featurePositions[i - 1] = new FeaturePosition(compound.getInt("x"), compound.getInt("y"),
                            compound.getInt("z"));
                }
            }
        }

        return new DungeonModel(model, featurePositions);
    }

}
