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
import net.minecraft.world.ISeedReader;
import net.minecraft.world.gen.ChunkGenerator;
import net.minecraft.world.gen.feature.structure.StructureManager;
import net.minecraft.world.gen.feature.template.TemplateManager;
import xiroc.dungeoncrawl.dungeon.DungeonBuilder;
import xiroc.dungeoncrawl.dungeon.StructurePieceTypes;
import xiroc.dungeoncrawl.dungeon.model.DungeonModel;
import xiroc.dungeoncrawl.dungeon.model.DungeonModels;
import xiroc.dungeoncrawl.dungeon.treasure.Treasure;
import xiroc.dungeoncrawl.theme.Theme;

import java.util.Random;

public class DungeonPrisonCell extends DungeonPiece {

    public DungeonPrisonCell() {
        super(StructurePieceTypes.PRISONER_CELL);
    }

    public DungeonPrisonCell(TemplateManager manager, CompoundNBT nbt) {
        super(StructurePieceTypes.PRISONER_CELL, nbt);
    }

    @Override
    public boolean func_230383_a_(ISeedReader worldIn, StructureManager p_230383_2_, ChunkGenerator p_230383_3_, Random randomIn, MutableBoundingBox structureBoundingBoxIn, ChunkPos p_230383_6_, BlockPos p_230383_7_) {


        DungeonModel model = DungeonModels.MODELS.get(modelID);
        BlockPos pos = new BlockPos(x, y, z);

        buildRotated(model, worldIn, structureBoundingBoxIn, pos,
                Theme.get(theme), Theme.getSub(subTheme), Treasure.Type.DEFAULT, stage, rotation, false);

        decorate(worldIn, pos, model.width, model.height, model.length, Theme.get(theme), structureBoundingBoxIn, boundingBox, model);
        return true;
    }

    @Override
    public int getType() {
        return 12;
    }

    @Override
    public int determineModel(DungeonBuilder builder, Random rand) {
        return DungeonModels.PRISON_CELL.id;
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
                return;
        }
    }

}
