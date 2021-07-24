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
import net.minecraft.core.Vec3i;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.resources.ResourceLocation;

public interface ModelLoader {

    ModelLoader VERSION_1 = (nbt, file, key) -> {
        ImmutableList.Builder<DungeonModelBlock> modelBlocks = new ImmutableList.Builder<>();

        ListTag blocks = nbt.getList("blocks", 10);

        for (int i = 0; i < blocks.size(); i++) {
            modelBlocks.add(DungeonModelBlock.fromNBT(blocks.getCompound(i)));
        }

        return new DungeonModel(key, modelBlocks.build(), nbt.getInt("width"), nbt.getInt("height"), nbt.getInt("length"));
    };

    ModelLoader LEGACY = (nbt, file, key) -> {
        int width = nbt.getInt("width"), height = nbt.getInt("height"), length = nbt.getInt("length");

        ListTag blocks = nbt.getList("model", 9);

        ImmutableList.Builder<DungeonModelBlock> modelBlocks = new ImmutableList.Builder<>();

        for (int x = 0; x < width; x++) {
            ListTag blocks2 = blocks.getList(x);
            for (int y = 0; y < height; y++) {
                ListTag blocks3 = blocks2.getList(y);
                for (int z = 0; z < length; z++) {
                    modelBlocks.add(DungeonModelBlock.fromNBT(blocks3.getCompound(z), new Vec3i(x, y, z)));
                }
            }
        }

        return new DungeonModel(key, modelBlocks.build(), width, height, length);
    };

    DungeonModel load(CompoundTag nbt, ResourceLocation file, ResourceLocation key);

}
