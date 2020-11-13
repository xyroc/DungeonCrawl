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

package xiroc.dungeoncrawl.dungeon.piece.room;

import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.MutableBoundingBox;
import net.minecraft.world.ISeedReader;
import net.minecraft.world.gen.ChunkGenerator;
import net.minecraft.world.gen.feature.structure.StructureManager;
import net.minecraft.world.gen.feature.template.TemplateManager;
import xiroc.dungeoncrawl.dungeon.DungeonBuilder;
import xiroc.dungeoncrawl.dungeon.StructurePieceTypes;
import xiroc.dungeoncrawl.dungeon.model.DungeonModels;
import xiroc.dungeoncrawl.dungeon.piece.DungeonPiece;

import java.util.List;
import java.util.Random;

public class DungeonSpiderRoom extends DungeonPiece {

    private BlockPos[] spawners, chests;

    public DungeonSpiderRoom(TemplateManager manager, CompoundNBT nbt) {
        super(StructurePieceTypes.SPIDER_ROOM, nbt);
        if (nbt.contains("spawners")) {
            ListNBT list = nbt.getList("spawners", 10);
            spawners = new BlockPos[list.size()];
            for (int i = 0; i < spawners.length; i++) {
                CompoundNBT pos = list.getCompound(i);
                spawners[i] = new BlockPos(pos.getInt("x"), pos.getInt("y"), pos.getInt("z"));
            }
        }
        if (nbt.contains("chests")) {
            ListNBT list = nbt.getList("chests", 10);
            chests = new BlockPos[list.size()];
            for (int i = 0; i < chests.length; i++) {
                CompoundNBT pos = list.getCompound(i);
                chests[i] = new BlockPos(pos.getInt("x"), pos.getInt("y"), pos.getInt("z"));
            }
        }
    }

    @Override
    public void customSetup(Random rand) {
        int floor = y + 1;
        int chests = 1 + rand.nextInt(2);
        int spawners = chests + rand.nextInt(2);

        this.chests = new BlockPos[chests];
        this.spawners = new BlockPos[spawners];

        int x = 0, z = 0;
        for (int i = 0; i < spawners; i++) {
            x = (x + 1 + rand.nextInt(3)) % 9;
            z = (z + 1 + rand.nextInt(3)) % 9;
            this.spawners[i] = new BlockPos(this.x + x, floor + rand.nextInt(2), this.z + z);
            if (i < chests) {
                this.chests[i] = new BlockPos(this.spawners[i].getX(), this.spawners[i].getY() == floor ? floor + 1 : floor, this.spawners[i].getZ());
            }
        }
    }

    @Override
    public boolean func_230383_a_(ISeedReader p_230383_1_, StructureManager p_230383_2_, ChunkGenerator p_230383_3_, Random p_230383_4_, MutableBoundingBox p_230383_5_, ChunkPos p_230383_6_, BlockPos p_230383_7_) {
        return true;
    }

    @Override
    public int getType() {
        return 15;
    }

    @Override
    public void setupModel(DungeonBuilder builder, DungeonModels.ModelCategory layerCategory, List<DungeonPiece> pieces, Random rand) {
    }

    @Override
    public void setupBoundingBox() {
        this.boundingBox = new MutableBoundingBox(x, y, z, x + 8, y + 8, z + 8);
    }

    @Override
    public void readAdditional(CompoundNBT tagCompound) {
        super.readAdditional(tagCompound);
        if (spawners != null) {
            ListNBT list = new ListNBT();
            for (BlockPos pos : spawners) {
                CompoundNBT compoundNBT = new CompoundNBT();
                compoundNBT.putInt("x", pos.getX());
                compoundNBT.putInt("y", pos.getY());
                compoundNBT.putInt("z", pos.getZ());
                list.add(compoundNBT);
            }
            tagCompound.put("spawners", list);
        }
        if (chests != null) {
            ListNBT list = new ListNBT();
            for (BlockPos pos : chests) {
                CompoundNBT compoundNBT = new CompoundNBT();
                compoundNBT.putInt("x", pos.getX());
                compoundNBT.putInt("y", pos.getY());
                compoundNBT.putInt("z", pos.getZ());
                list.add(compoundNBT);
            }
            tagCompound.put("chests", list);
        }
    }
}
