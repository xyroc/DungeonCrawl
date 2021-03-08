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

import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.MutableBoundingBox;
import net.minecraft.world.ISeedReader;
import net.minecraft.world.IWorld;
import net.minecraft.world.gen.ChunkGenerator;
import net.minecraft.world.gen.feature.structure.StructureManager;
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

public class DungeonStairs extends DungeonPiece {

    private static final BlockState IRON_BARS = Blocks.IRON_BARS.getDefaultState();

    public int stairType; // 0: bottom stairs, 1: top stairs

    public DungeonStairs(TemplateManager manager, CompoundNBT p_i51343_2_) {
        super(StructurePieceTypes.STAIRS, p_i51343_2_);
        this.stairType = p_i51343_2_.getInt("stairType");
    }

    @Override
    public void setupModel(DungeonBuilder builder, ModelCategory layerCategory, List<DungeonPiece> pieces, Random rand) {
        switch (stairType) {
            case 0:
                this.modelKey = stage > 0 ? DungeonModels.STAIRS_BOTTOM_2.key : DungeonModels.STAIRS_BOTTOM.key;
                return;
            case 1:
                this.modelKey = DungeonModels.STAIRS_TOP.key;
        }
    }

    @Override
    public boolean func_230383_a_(ISeedReader worldIn, StructureManager p_230383_2_, ChunkGenerator p_230383_3_, Random randomIn, MutableBoundingBox structureBoundingBoxIn, ChunkPos p_230383_6_, BlockPos p_230383_7_) {
        DungeonModel model = DungeonModels.getModel(modelKey, modelID);
        if (model == null) {
            DungeonCrawl.LOGGER.warn("Missing model {} in {}", modelID != null ? modelID : modelKey, this);
            return true;
        }
        BlockPos pos = new BlockPos(x, y, z).add(model.getOffset(rotation));
        switch (stairType) {
            case 0: {
                build(model, worldIn, structureBoundingBoxIn, pos, Theme.get(theme),
                        Theme.getSub(subTheme), model.getTreasureType(), stage, false);
                ironBars(worldIn, structureBoundingBoxIn, model);

                if (model.metadata != null && model.metadata.feature != null && featurePositions != null) {
                    model.metadata.feature.build(worldIn, randomIn, pos, featurePositions, structureBoundingBoxIn, theme, subTheme, stage);
                }

                decorate(worldIn, pos, model.width, model.height, model.length, Theme.get(theme), structureBoundingBoxIn, boundingBox, model);
                return true;
            }
            case 1: {
                build(model, worldIn, structureBoundingBoxIn, pos, Theme.get(theme),
                        Theme.getSub(subTheme), model.getTreasureType(), stage, false);
                entrances(worldIn, structureBoundingBoxIn, model);

                if (model.metadata != null && model.metadata.feature != null && featurePositions != null) {
                    model.metadata.feature.build(worldIn, randomIn, pos, featurePositions, structureBoundingBoxIn, theme, subTheme, stage);
                }

                decorate(worldIn, pos, model.width, model.height, model.length, Theme.get(theme), structureBoundingBoxIn, boundingBox, model);
                return true;
            }
            default:
                return true;
        }

    }

    public void ironBars(IWorld world, MutableBoundingBox bounds, DungeonModel model) {
        int pathStartX = (model.width - 3) / 2, pathStartZ = (model.length - 3) / 2;

        if (sides[0]) {
            for (int x0 = pathStartX; x0 < pathStartX + 3; x0++)
                for (int y0 = 1; y0 < 4; y0++)
                    replaceBlockState(world, IRON_BARS, x + x0, y + y0, z, bounds);
        }
        if (sides[1]) {
            for (int z0 = pathStartZ; z0 < pathStartZ + 3; z0++)
                for (int y0 = 1; y0 < 4; y0++)
                    replaceBlockState(world, IRON_BARS, x + model.width - 1, y + y0, z + z0, bounds);
        }
        if (sides[2]) {
            for (int x0 = pathStartX; x0 < pathStartX + 3; x0++)
                for (int y0 = 1; y0 < 4; y0++)
                    replaceBlockState(world, IRON_BARS, x + x0, y + y0, z + model.length - 1, bounds);
        }
        if (sides[3]) {
            for (int z0 = pathStartZ; z0 < pathStartZ + 3; z0++)
                for (int y0 = 1; y0 < 4; y0++)
                    replaceBlockState(world, IRON_BARS, x, y + y0, z + z0, bounds);
        }

    }

    @Override
    public void setupBoundingBox() {
        DungeonModel model = DungeonModels.getModel(modelKey, modelID);
        if (model != null) {
            this.boundingBox = model.createBoundingBoxWithOffset(x, y, z, rotation);
        }
    }

    @Override
    public boolean canConnect(Direction side) {
        return true;
    }

    public DungeonStairs bottom() {
        this.stairType = 0;
        return this;
    }

    public DungeonStairs top() {
        this.stairType = 1;
        return this;
    }

    @Override
    public void readAdditional(CompoundNBT tagCompound) {
        super.readAdditional(tagCompound);
        tagCompound.putInt("stairType", stairType);
    }

    @Override
    public int getType() {
        return 1;
    }

}