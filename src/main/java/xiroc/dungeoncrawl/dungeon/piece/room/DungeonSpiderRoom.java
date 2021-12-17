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

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.StructureFeatureManager;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import xiroc.dungeoncrawl.dungeon.DungeonBuilder;
import xiroc.dungeoncrawl.dungeon.StructurePieceTypes;
import xiroc.dungeoncrawl.dungeon.model.ModelSelector;
import xiroc.dungeoncrawl.dungeon.piece.DungeonPiece;

import java.util.List;
import java.util.Random;

public class DungeonSpiderRoom extends DungeonPiece {

    private BlockPos[] spawners, chests;

    public DungeonSpiderRoom(ServerLevel serverLevel, CompoundTag nbt) {
        super(StructurePieceTypes.SPIDER_ROOM, nbt);
        if (nbt.contains("spawners")) {
            ListTag list = nbt.getList("spawners", 10);
            spawners = new BlockPos[list.size()];
            for (int i = 0; i < spawners.length; i++) {
                CompoundTag pos = list.getCompound(i);
                spawners[i] = new BlockPos(pos.getInt("x"), pos.getInt("y"), pos.getInt("z"));
            }
        }
        if (nbt.contains("chests")) {
            ListTag list = nbt.getList("chests", 10);
            chests = new BlockPos[list.size()];
            for (int i = 0; i < chests.length; i++) {
                CompoundTag pos = list.getCompound(i);
                chests[i] = new BlockPos(pos.getInt("x"), pos.getInt("y"), pos.getInt("z"));
            }
        }
    }

    @Override
    public void setup(Random rand) {
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
    public boolean postProcess(WorldGenLevel p_230383_1_, StructureFeatureManager p_230383_2_, ChunkGenerator p_230383_3_, Random p_230383_4_, BoundingBox p_230383_5_, ChunkPos p_230383_6_, BlockPos p_230383_7_) {
        return true;
    }

    @Override
    public int getDungeonPieceType() {
        return SPIDER_ROOM;
    }

    @Override
    public void setupModel(DungeonBuilder builder, ModelSelector modelSelector, List<DungeonPiece> pieces, Random rand) {
    }

    @Override
    public void createBoundingBox() {
        this.boundingBox = new BoundingBox(x, y, z, x + 8, y + 8, z + 8);
    }

    @Override
    public void addAdditionalSaveData(ServerLevel serverLevel, CompoundTag tagCompound) {
        super.addAdditionalSaveData(serverLevel, tagCompound);
        if (spawners != null) {
            tagCompound.put("spawners", positionsToNbt(spawners));
        }
        if (chests != null) {
            tagCompound.put("chests", positionsToNbt(chests));
        }
    }
}
