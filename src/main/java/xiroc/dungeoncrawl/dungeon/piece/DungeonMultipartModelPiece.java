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
import xiroc.dungeoncrawl.dungeon.treasure.Treasure;
import xiroc.dungeoncrawl.theme.Theme;

import java.util.List;
import java.util.Random;

public class DungeonMultipartModelPiece extends DungeonPiece {

    public DungeonMultipartModelPiece(TemplateManager manager, CompoundNBT p_i51343_2_) {
        super(StructurePieceTypes.MULTIPART_MODEL_PIECE, p_i51343_2_);
    }

    @Override
    public int getType() {
        return 16;
    }

    @Override
    public void setupModel(DungeonBuilder builder, DungeonModels.ModelCategory layerCategory, List<DungeonPiece> pieces, Random rand) {
    }

    @Override
    public void setupBoundingBox() {
        DungeonModel model = DungeonModels.MODELS.get(modelID);
        if (model != null) {
            this.boundingBox = model.createBoundingBox(x, y, z, rotation);
        } else {
            this.boundingBox = new MutableBoundingBox();
            DungeonCrawl.LOGGER.warn("The multipart model piece {} does not have a valid model: {}", this, modelID);
        }
    }

    @Override
    public boolean func_225577_a_(IWorld p_225577_1_, ChunkGenerator<?> p_225577_2_, Random p_225577_3_, MutableBoundingBox p_225577_4_, ChunkPos p_225577_5_) {
        DungeonModel model = DungeonModels.MODELS.get(modelID);
        if (model != null) {
            DungeonCrawl.LOGGER.info("Building Multipart piece at {} {} {}", x, y, z);
            build(model, p_225577_1_, p_225577_4_, new BlockPos(x, y, z), Theme.get(theme), Theme.getSub(subTheme), Treasure.getModelTreasureType(modelID), stage, false);
        }
        return true;
    }
}