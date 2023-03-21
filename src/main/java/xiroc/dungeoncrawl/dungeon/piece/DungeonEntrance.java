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

package xiroc.dungeoncrawl.dungeon.piece;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.StructureFeatureManager;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import net.minecraft.world.level.levelgen.structure.pieces.StructurePieceSerializationContext;
import xiroc.dungeoncrawl.dungeon.StructurePieceTypes;

import java.util.Random;

public class DungeonEntrance extends DungeonPiece {
    private static final String NBT_KEY_SURFACE_HEIGHT = "SurfaceHeight";
    private Integer surfaceHeight;

    public DungeonEntrance() {
        super(StructurePieceTypes.ENTRANCE, new BoundingBox(0 ,0 ,0 ,0 ,0 ,0));
    }

    public DungeonEntrance(CompoundTag nbt) {
        super(StructurePieceTypes.ENTRANCE, nbt);
        if (nbt.contains(NBT_KEY_SURFACE_HEIGHT)) {
            this.surfaceHeight = nbt.getInt(NBT_KEY_SURFACE_HEIGHT);
        }
    }

    @Override
    public void postProcess(WorldGenLevel level, StructureFeatureManager p_230383_2_, ChunkGenerator p_230383_3_, Random random, BoundingBox worldGenBounds, ChunkPos p_230383_6_, BlockPos p_230383_7_) {
        if (this.surfaceHeight == null) {
            this.surfaceHeight = level.getHeight(Heightmap.Types.WORLD_SURFACE_WG, this.position.getX() + 4, this.position.getZ() + 4);
        }
        // TODO
    }

    @Override
    public void createBoundingBox() {
        // TODO
    }

    @Override
    public void addAdditionalSaveData(StructurePieceSerializationContext context, CompoundTag tagCompound) {
        super.addAdditionalSaveData(context, tagCompound);
        if (this.surfaceHeight != null) {
            tagCompound.putInt(NBT_KEY_SURFACE_HEIGHT, this.surfaceHeight);
        }
    }
}