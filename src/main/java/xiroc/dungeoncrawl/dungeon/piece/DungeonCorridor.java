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
import net.minecraft.core.Vec3i;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.StructureFeatureManager;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import xiroc.dungeoncrawl.DungeonCrawl;
import xiroc.dungeoncrawl.dungeon.DungeonBuilder;
import xiroc.dungeoncrawl.dungeon.StructurePieceTypes;
import xiroc.dungeoncrawl.dungeon.model.ModelSelector;

import java.util.List;
import java.util.Random;

public class DungeonCorridor extends DungeonPiece {

    public DungeonCorridor() {
        super(StructurePieceTypes.CORRIDOR);
    }

    public DungeonCorridor(ServerLevel serverLevel, CompoundTag p_i51343_2_) {
        super(StructurePieceTypes.CORRIDOR, p_i51343_2_);
    }

    @Override
    public void setupModel(DungeonBuilder builder, ModelSelector modelSelector, List<DungeonPiece> pieces, Random rand) {
        if (connectedSides == 2 && isStraight()) {
            this.model = modelSelector.corridors.roll(rand);
        } else {
            this.model = modelSelector.corridorLinkers.roll(rand);
        }
    }


    public boolean postProcess(WorldGenLevel worldIn, StructureFeatureManager p_230383_2_, ChunkGenerator p_230383_3_, Random randomIn, BoundingBox structureBoundingBoxIn, ChunkPos p_230383_6_, BlockPos p_230383_7_) {
        if (model == null) {
            DungeonCrawl.LOGGER.warn("Missing model for {}", this);
            return true;
        }

        Vec3i offset = model.getOffset(rotation);
        BlockPos pos = new BlockPos(x, y, z).offset(offset);

        buildRotated(model, worldIn, structureBoundingBoxIn, pos, theme, secondaryTheme, stage, rotation, context, false);
        if (connectedSides != 2 || !isStraight()) {
            entrances(worldIn, structureBoundingBoxIn, model);
        }
        placeFeatures(worldIn, context, structureBoundingBoxIn, theme, secondaryTheme, randomIn, stage);
        decorate(worldIn, pos, context, model.width, model.height, model.length, theme, structureBoundingBoxIn, boundingBox, model);
        return true;
    }

    @Override
    public void setupBoundingBox() {
        if (model != null) {
            this.boundingBox = model.createBoundingBoxWithOffset(x, y, z, rotation);
        }
    }

    @Override
    public int getDungeonPieceType() {
        return 0;
    }

    public boolean isStraight() {
        return sides[0] && sides[2] || sides[1] && sides[3];
    }

}
