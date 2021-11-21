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

import com.google.common.collect.ImmutableList;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.vector.Vector3i;
import xiroc.dungeoncrawl.DungeonCrawl;

public interface ModelLoader {

    ModelLoader VERSION_1 = (nbt, file, key) -> {
        ImmutableList.Builder<DungeonModelBlock> modelBlocks = new ImmutableList.Builder<>();

        ListNBT blocks = nbt.getList("blocks", 10);

        for (int i = 0; i < blocks.size(); i++) {
            DungeonModelBlock block = DungeonModelBlock.fromNBT(blocks.getCompound(i));
            if (block != null) {
                modelBlocks.add(block);
            } else {
                DungeonCrawl.LOGGER.error("Loading a block from {} (version 1) returned null", file);
            }
        }

        return new DungeonModel(key, modelBlocks.build(), nbt.getInt("width"), nbt.getInt("height"), nbt.getInt("length"));
    };

    ModelLoader LEGACY = (nbt, file, key) -> {
        int width = nbt.getInt("width"), height = nbt.getInt("height"), length = nbt.getInt("length");

        ListNBT blocks = nbt.getList("model", 9);

        ImmutableList.Builder<DungeonModelBlock> modelBlocks = new ImmutableList.Builder<>();

        for (int x = 0; x < width; x++) {
            ListNBT blocks2 = blocks.getList(x);
            for (int y = 0; y < height; y++) {
                ListNBT blocks3 = blocks2.getList(y);
                for (int z = 0; z < length; z++) {
                    DungeonModelBlock block = DungeonModelBlock.fromNBT(blocks3.getCompound(z), new Vector3i(x, y, z));
                    if (block != null) {
                        modelBlocks.add(block);
                    } else {
                        DungeonCrawl.LOGGER.error("Loading a block from {} (legacy) returned null", file);
                    }
                }
            }
        }

        return new DungeonModel(key, modelBlocks.build(), width, height, length);
    };

    DungeonModel load(CompoundNBT nbt, ResourceLocation file, ResourceLocation key);

}
