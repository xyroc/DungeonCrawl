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

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.CompressedStreamTools;
import net.minecraft.nbt.ListNBT;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.vector.Vector3i;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import xiroc.dungeoncrawl.DungeonCrawl;

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

        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                for (int z = 0; z < length; z++) {

                    BlockPos position = new BlockPos(pos.getX() + x, pos.getY() + y, pos.getZ() + z);
                    BlockState state = world.getBlockState(position);
                    Block block = state.getBlock();
                    if (block == Blocks.BARRIER) {
                        continue;
                    }
                    blocks.add(DungeonModelBlock.fromBlockState(state, DungeonModelBlockType.get(block, definition), new Vector3i(x, y, z)));
                }
            }
        }
        writeToFile(toNbt(blocks, width, height, length),
                ((ServerWorld) world).getServer().getServerDirectory().getAbsolutePath() + "/models/" + name + ".nbt");
        DungeonCrawl.LOGGER.info("Done.");
    }

    public static void writeToFile(CompoundNBT nbt, String path) {
        try {
            DungeonCrawl.LOGGER.info("Writing a model to disk at {}. ", path);
            File file = new File(path);
            Files.createDirectories(file.getParentFile().toPath());
            Files.deleteIfExists(file.toPath());
            Files.createFile(file.toPath());
            CompressedStreamTools.writeCompressed(nbt, new FileOutputStream(file));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static CompoundNBT toNbt(DungeonModel model) {
        return toNbt(model.blocks, model.width, model.height, model.length);
    }

    public static CompoundNBT toNbt(List<DungeonModelBlock> blocks, int width, int height, int length) {
        CompoundNBT nbt = new CompoundNBT();
        nbt.putByte("format", LATEST_MODEL_FORMAT);

        nbt.putByte("width", (byte) width);
        nbt.putByte("height", (byte) height);
        nbt.putByte("length", (byte) length);

        ListNBT _blocks = new ListNBT();

        blocks.forEach((block) -> _blocks.add(block.toNBT()));

        nbt.put("blocks", _blocks);
        return nbt;
    }

    public static DungeonModel loadModelFromNBT(CompoundNBT nbt, ResourceLocation file, ResourceLocation key) {
        int format = nbt.getInt("format");

        if (format == 1) {
            return ModelLoader.VERSION_1.load(nbt, file, key);
        }
        return ModelLoader.LEGACY.load(nbt, file, key);
    }

    public static DungeonModel getModelFromJigsawNBT(CompoundNBT nbt) {
        return null;
    }

}
