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
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.StructureFeatureManager;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import xiroc.dungeoncrawl.DungeonCrawl;
import xiroc.dungeoncrawl.dungeon.DungeonBuilder;
import xiroc.dungeoncrawl.dungeon.StructurePieceTypes;
import xiroc.dungeoncrawl.dungeon.model.ModelSelector;

import java.util.List;
import java.util.Random;

public class DungeonNodeConnector extends DungeonPiece {

    public DungeonNodeConnector() {
        super(StructurePieceTypes.NODE_CONNECTOR);
    }

    public DungeonNodeConnector(ServerLevel serverLevel, CompoundTag nbt) {
        super(StructurePieceTypes.NODE_CONNECTOR, nbt);
    }

    @Override
    public boolean postProcess(WorldGenLevel worldIn, StructureFeatureManager p_230383_2_, ChunkGenerator p_230383_3_, Random randomIn, BoundingBox structureBoundingBoxIn, ChunkPos p_230383_6_, BlockPos p_230383_7_) {
        if (model == null) {
            DungeonCrawl.LOGGER.warn("Missing model for {}", this);
            return true;
        }
        BlockPos pos = new BlockPos(x, y + model.getOffset(rotation).getY(), z);

        buildRotated(model, worldIn, structureBoundingBoxIn, pos, theme, secondaryTheme, stage, rotation, worldGen, false, false);
        placeFeatures(worldIn, structureBoundingBoxIn, theme, secondaryTheme, randomIn, stage, worldGen);
        decorate(worldIn, pos, model.width, model.height, model.length, theme, structureBoundingBoxIn, boundingBox, model, worldGen);
        return true;
    }

    @Override
    public int getDungeonPieceType() {
        return NODE_CONNECTOR;
    }

    @Override
    public void setupModel(DungeonBuilder builder, ModelSelector modelSelector, List<DungeonPiece> pieces, Random rand) {
        this.model = modelSelector.nodeConnectors.roll(rand);
    }

    public void adjustPositionAndBounds() {
        if (model == null) {
            return;
        }

        if (rotation == Rotation.NONE || rotation == Rotation.CLOCKWISE_180) {
            int minZ = z - (model.length - 3) / 2;
            this.boundingBox = new BoundingBox(x, y, minZ, x + 4, y + model.height - 1,
                    minZ + model.length - 1);
        } else {
            int minX = x - (model.length - 3) / 2;
            this.boundingBox = new BoundingBox(minX, y, z, minX + model.length - 1,
                    y + model.height - 1, z + 4);
        }

        setWorldPosition(this.boundingBox.minX(), this.boundingBox.minY(), this.boundingBox.minZ());
    }

    @Override
    public void createBoundingBox() {
        if (model != null) {
            this.boundingBox = model.createBoundingBoxWithOffset(x, y, z, rotation);
        }
    }

}
