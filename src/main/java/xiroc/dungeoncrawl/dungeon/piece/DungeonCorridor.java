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
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.StructureManager;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import xiroc.dungeoncrawl.DungeonCrawl;
import xiroc.dungeoncrawl.dungeon.DungeonBuilder;
import xiroc.dungeoncrawl.dungeon.PlacementConfiguration;
import xiroc.dungeoncrawl.init.ModStructurePieceTypes;
import xiroc.dungeoncrawl.dungeon.model.ModelSelector;

import java.util.List;

public class DungeonCorridor extends DungeonPiece {

    public DungeonCorridor() {
        super(ModStructurePieceTypes.CORRIDOR);
    }

    public DungeonCorridor(CompoundTag p_i51343_2_) {
        super(ModStructurePieceTypes.CORRIDOR, p_i51343_2_);
    }

    @Override
    public void setupModel(DungeonBuilder builder, ModelSelector modelSelector, List<DungeonPiece> pieces, RandomSource rand) {
        if (connectedSides == 2 && isStraight()) {
            this.model = modelSelector.corridors.roll(rand);
        } else {
            this.model = modelSelector.corridorLinkers.roll(rand);
        }
    }

    public void postProcess(WorldGenLevel worldIn, StructureManager p_230383_2_, ChunkGenerator p_230383_3_, RandomSource randomIn, BoundingBox structureBoundingBoxIn, ChunkPos p_230383_6_, BlockPos p_230383_7_) {
        if (model == null) {
            DungeonCrawl.LOGGER.warn("Missing model for {}", this);
            return;
        }

        Vec3i offset = model.getOffset(rotation);
        BlockPos pos = new BlockPos(x, y, z).offset(offset);

        buildModel(model, worldIn, structureBoundingBoxIn, pos, randomIn, PlacementConfiguration.CORRIDOR, theme, secondaryTheme, stage, rotation, false, false);
        if (connectedSides != 2 || !isStraight()) {
            entrances(worldIn, structureBoundingBoxIn, model, randomIn);
        }

        placeFeatures(worldIn, structureBoundingBoxIn, theme, secondaryTheme, randomIn, stage);
        decorate(worldIn, pos, theme, randomIn, structureBoundingBoxIn, boundingBox, model);
    }

    @Override
    protected boolean hasPillarAt(BlockPos pos) {
        if (rotation == Rotation.NONE || rotation == Rotation.CLOCKWISE_180) {
            return pos.getX() % 6 == 0 && pos.getZ() % 3 == 0;
        } else {
            return pos.getX() % 3 == 0 && pos.getZ() % 6 == 0;
        }
    }

    @Override
    public void createBoundingBox() {
        if (model != null) {
            this.boundingBox = model.createBoundingBoxWithOffset(x, y, z, rotation);
        }
    }

    @Override
    public int getDungeonPieceType() {
        return CORRIDOR;
    }

    public boolean isStraight() {
        return sides[0] && sides[2] || sides[1] && sides[3];
    }

}
