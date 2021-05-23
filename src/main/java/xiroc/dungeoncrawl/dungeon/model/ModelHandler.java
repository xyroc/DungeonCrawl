/*
        Dungeon Crawl, a procedural dungeon generator for Minecraft 1.14 and later.
        Copyright (C) 2020

        This program is free software: you can redistribute it and/or modify
        it under the terms of the GNU General Public License as published by
        the Free Software Foundation, either version 3 of the License, or
        (at your option) any later version.

        This program is distributed in the hope that it will be useful,
        but WITHOUT ANY WARRANTY; without even the implied warranty of
        MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
        GNU General Public License for more details.

        You should have received a copy of the GNU General Public License
        along with this program.  If not, see <https://www.gnu.org/licenses/>.
*/

package xiroc.dungeoncrawl.dungeon.model;

import com.google.common.collect.Lists;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.CompressedStreamTools;
import net.minecraft.nbt.ListNBT;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3i;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import xiroc.dungeoncrawl.DungeonCrawl;
import xiroc.dungeoncrawl.dungeon.model.DungeonModel.FeaturePosition;

import java.io.File;
import java.io.FileOutputStream;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;

public class ModelHandler {

    public static final byte LATEST_MODEL_FORMAT = 1;

    public static void readAndSaveModelToFile(String name, ModelBlockDefinition definition, World world, BlockPos pos, int width, int height, int length) {
        DungeonCrawl.LOGGER.info("Reading and saving {} to disk. Size: {}, {}, {}", name, width, height, length);

        List<DungeonModelBlock> blocks = new ArrayList<>();

        List<FeaturePosition> featurePositions = Lists.newArrayList();

        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                for (int z = 0; z < length; z++) {
                    BlockPos position = new BlockPos(pos.getX() + x, pos.getY() + y, pos.getZ() + z);
                    BlockState state = world.getBlockState(position);
                    Block block = state.getBlock();
                    if (block == Blocks.BARRIER) {
                        continue;
                    } else if (block == Blocks.JIGSAW) {
                        featurePositions.add(new FeaturePosition(x, y, z, state.get(BlockStateProperties.FACING)));
                        blocks.add(new DungeonModelBlock(DungeonModelBlockType.AIR, new Vec3i(x, y, z)));
                        continue;
                    } else if (BlockTags.CARPETS.contains(block)) {
                        // TODO: carpets
                    }
                    blocks.add(new DungeonModelBlock(DungeonModelBlockType.get(state.getBlock(), definition), new Vec3i(x, y, z)).loadDataFromState(state));
                }
            }
        }
        writeModelToFile(
                new DungeonModel(blocks, featurePositions.isEmpty() ? null : featurePositions.toArray(new FeaturePosition[0]), width, height, length),
                ((ServerWorld) world).getServer().getDataDirectory().getAbsolutePath() + "/models/" + name + ".nbt");

        DungeonCrawl.LOGGER.info("Done.");
    }

    public static void writeModelToFile(DungeonModel model, String path) {
        try {
            DungeonCrawl.LOGGER.info("Writing a model to disk at {}. ", path);
            File file = new File(path);
            Files.createDirectories(file.getParentFile().toPath());
            Files.deleteIfExists(file.toPath());
            Files.createFile(file.toPath());
            CompressedStreamTools.writeCompressed(convertModelToNBT(model), new FileOutputStream(file));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static CompoundNBT convertModelToNBT(DungeonModel model) {
        byte width = (byte) (model.width > 127 ? -(model.width - 127) : model.width),
                length = (byte) (model.length > 127 ? -(model.length - 127) : model.length),
                height = (byte) (model.height > 127 ? -(model.height - 127) : model.height);

        CompoundNBT nbt = new CompoundNBT();
        nbt.putByte("format", LATEST_MODEL_FORMAT);

        nbt.putByte("length", length);
        nbt.putByte("height", height);
        nbt.putByte("width", width);

        ListNBT blocks = new ListNBT();

        model.blocks.forEach((block) -> blocks.add(block.toNBT()));

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
            nbt.put("featurePositions", list);
        }

        nbt.put("blocks", blocks);

        return nbt;
    }

    public static DungeonModel loadModelFromNBT(CompoundNBT nbt, ResourceLocation file) {
        int format = nbt.getInt("format");

        if (format == 1) {
            return ModelLoader.VERSION_1.load(nbt, file);
        }
        return ModelLoader.LEGACY.load(nbt, file);
    }

    public static DungeonModel getModelFromJigsawNBT(CompoundNBT nbt) {
        return null;
    }

}
