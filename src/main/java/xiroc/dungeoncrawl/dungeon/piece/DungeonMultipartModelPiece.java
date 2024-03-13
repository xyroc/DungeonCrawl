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
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.StructureManager;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import xiroc.dungeoncrawl.DungeonCrawl;
import xiroc.dungeoncrawl.dungeon.DungeonBuilder;
import xiroc.dungeoncrawl.dungeon.PlacementConfiguration;
import xiroc.dungeoncrawl.init.ModStructurePieceTypes;
import xiroc.dungeoncrawl.dungeon.model.ModelSelector;

import java.util.List;

public class DungeonMultipartModelPiece extends DungeonPiece {

    public DungeonMultipartModelPiece() {
        super(ModStructurePieceTypes.MULTIPART_MODEL_PIECE.get());
    }

    public DungeonMultipartModelPiece(CompoundTag p_i51343_2_) {
        super(ModStructurePieceTypes.MULTIPART_MODEL_PIECE.get(), p_i51343_2_);
    }

    @Override
    public int getDungeonPieceType() {
        return MULTIPART_PIECE;
    }

    @Override
    public void setupModel(DungeonBuilder builder, ModelSelector modelSelector, List<DungeonPiece> pieces, RandomSource rand) {
    }

    @Override
    public void postProcess(WorldGenLevel world, StructureManager p_230383_2_, ChunkGenerator p_230383_3_, RandomSource random, BoundingBox structureBoundingBoxIn, ChunkPos p_230383_6_, BlockPos p_230383_7_) {
        if (model == null) {
            DungeonCrawl.LOGGER.warn("Missing model for {}", this);
            return;
        }
        BlockPos pos = new BlockPos(x, y, z);

        buildModel(model, world, boundingBox, pos, random, PlacementConfiguration.DEFAULT, theme, secondaryTheme, stage, rotation, false, false);
        placeFeatures(world, boundingBox, theme, secondaryTheme, random, stage);
        decorate(world, pos, theme, random, structureBoundingBoxIn, boundingBox, model);
    }

    public void createBoundingBox() {
        if (model != null) {
            this.boundingBox = model.createBoundingBox(x, y, z, rotation);
        }
    }

}
