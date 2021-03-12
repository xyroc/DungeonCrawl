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

import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.Rotation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.MutableBoundingBox;
import net.minecraft.world.IWorld;
import net.minecraft.world.gen.ChunkGenerator;
import net.minecraft.world.gen.feature.template.TemplateManager;
import xiroc.dungeoncrawl.DungeonCrawl;
import xiroc.dungeoncrawl.dungeon.DungeonBuilder;
import xiroc.dungeoncrawl.dungeon.StructurePieceTypes;
import xiroc.dungeoncrawl.dungeon.model.ModelCategory;

import java.util.List;
import java.util.Random;

public class DungeonNodeConnector extends DungeonPiece {

    public DungeonNodeConnector() {
        super(StructurePieceTypes.NODE_CONNECTOR);
    }

    public DungeonNodeConnector(TemplateManager manager, CompoundNBT nbt) {
        super(StructurePieceTypes.NODE_CONNECTOR, nbt);
    }

    @Override
    public boolean create(IWorld worldIn, ChunkGenerator<?> chunkGenerator, Random randomIn, MutableBoundingBox structureBoundingBoxIn,
                          ChunkPos chunkPosIn) {
        if (model == null) {
            DungeonCrawl.LOGGER.warn("Missing model for {}", this);
            return true;
        }
        BlockPos pos = new BlockPos(x, y + model.getOffset(rotation).getY(), z);

        buildRotated(model, worldIn, structureBoundingBoxIn, pos, theme, subTheme, model.getTreasureType(), stage, rotation, false);

        if (model.metadata != null && model.metadata.feature != null && featurePositions != null) {
            model.metadata.feature.build(worldIn, randomIn, pos, featurePositions, structureBoundingBoxIn, theme, subTheme, stage);
        }

        decorate(worldIn, pos, model.width, model.height, model.length, theme, structureBoundingBoxIn, boundingBox, model);
        return true;
    }

    @Override
    public int getType() {
        return 11;
    }

    @Override
    public void setupModel(DungeonBuilder builder, ModelCategory layerCategory, List<DungeonPiece> pieces, Random rand) {
        this.model = ModelCategory.get(ModelCategory.NODE_CONNECTOR, layerCategory).roll(rand);
    }

    public void adjustPositionAndBounds() {
        if (model == null) {
            return;
        }

        if (rotation == Rotation.NONE || rotation == Rotation.CLOCKWISE_180) {
            int minZ = z - (model.length - 3) / 2;
            this.boundingBox = new MutableBoundingBox(x, y, minZ, x + 4, y + model.height - 1,
                    minZ + model.length - 1);
        } else {
            int minX = x - (model.length - 3) / 2;
            this.boundingBox = new MutableBoundingBox(minX, y, z, minX + model.length - 1,
                    y + model.height - 1, z + 4);
        }

        setWorldPosition(this.boundingBox.minX, this.boundingBox.minY, this.boundingBox.minZ);
    }

    @Override
    public void setupBoundingBox() {
        if (model != null) {
            this.boundingBox = model.createBoundingBoxWithOffset(x, y, z, rotation);
        }
    }

}
