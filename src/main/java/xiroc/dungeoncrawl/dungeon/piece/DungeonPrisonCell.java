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
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.MutableBoundingBox;
import net.minecraft.world.IWorld;
import net.minecraft.world.gen.ChunkGenerator;
import net.minecraft.world.gen.feature.template.TemplateManager;
import xiroc.dungeoncrawl.DungeonCrawl;
import xiroc.dungeoncrawl.dungeon.DungeonBuilder;
import xiroc.dungeoncrawl.dungeon.StructurePieceTypes;
import xiroc.dungeoncrawl.dungeon.model.DungeonModel;
import xiroc.dungeoncrawl.dungeon.model.DungeonModels;
import xiroc.dungeoncrawl.dungeon.model.ModelCategory;
import xiroc.dungeoncrawl.theme.Theme;

import java.util.List;
import java.util.Random;

public class DungeonPrisonCell extends DungeonPiece {

    public DungeonPrisonCell() {
        super(StructurePieceTypes.PRISONER_CELL);
    }

    public DungeonPrisonCell(TemplateManager manager, CompoundNBT nbt) {
        super(StructurePieceTypes.PRISONER_CELL, nbt);
    }

    @Override
    public boolean create(IWorld worldIn, ChunkGenerator<?> chunkGenerator, Random randomIn, MutableBoundingBox structureBoundingBoxIn,
                          ChunkPos chunkPosIn) {

        DungeonModel model = DungeonModels.getModel(modelKey, modelID);
        if (model == null) {
            DungeonCrawl.LOGGER.warn("Missing model {} in {}", modelID != null ? modelID : modelKey, this);
            return true;
        }
        BlockPos pos = new BlockPos(x, y, z);

        buildRotated(model, worldIn, structureBoundingBoxIn, pos,
                Theme.get(theme), Theme.getSub(subTheme), model.getTreasureType(), stage, rotation, false);

        decorate(worldIn, pos, model.width, model.height, model.length, Theme.get(theme), structureBoundingBoxIn, boundingBox, model);
        return true;
    }

    @Override
    public int getType() {
        return 12;
    }

    @Override
    public void setupModel(DungeonBuilder builder, ModelCategory layerCategory, List<DungeonPiece> pieces, Random rand) {
        this.modelID = DungeonModels.PRISON_CELL.id;
    }

    @Override
    public void setupBoundingBox() {
        switch (rotation) {
            case NONE:
            case CLOCKWISE_180:
                this.boundingBox = new MutableBoundingBox(x, y, z, x + 3, y + 4, z + 4);
                return;
            case CLOCKWISE_90:
            case COUNTERCLOCKWISE_90:
                this.boundingBox = new MutableBoundingBox(x, y, z, x + 4, y + 4, z + 3);
        }
    }

}
