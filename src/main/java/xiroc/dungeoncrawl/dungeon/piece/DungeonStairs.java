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
import xiroc.dungeoncrawl.dungeon.PlacementContext;
import xiroc.dungeoncrawl.dungeon.StructurePieceTypes;
import xiroc.dungeoncrawl.dungeon.model.DungeonModel;
import xiroc.dungeoncrawl.dungeon.model.DungeonModels;
import xiroc.dungeoncrawl.dungeon.model.ModelSelector;

import java.util.List;
import java.util.Random;

public class DungeonStairs extends DungeonPiece {

    private static final BlockState IRON_BARS = Blocks.IRON_BARS.defaultBlockState();

    public int stairType; // 0: bottom stairs, 1: top stairs

    public DungeonStairs() {
        this(null, DEFAULT_NBT);
    }

    public DungeonStairs(TemplateManager manager, CompoundNBT p_i51343_2_) {
        super(StructurePieceTypes.STAIRS, p_i51343_2_);
        this.stairType = p_i51343_2_.getInt("stairType");
    }

    @Override
    public void setupModel(DungeonBuilder builder, ModelSelector modelSelector, List<DungeonPiece> pieces, Random rand) {
        switch (stairType) {
            case 0:
                this.model = stage > 0 ? DungeonModels.KEY_TO_MODEL.get(DungeonModels.BOTTOM_STAIRS_2) : DungeonModels.KEY_TO_MODEL.get(DungeonModels.BOTTOM_STAIRS);
                return;
            case 1:
                this.model = DungeonModels.KEY_TO_MODEL.get(DungeonModels.TOP_STAIRS);
        }
    }

    @Override
    public boolean postProcess(ISeedReader worldIn, StructureManager p_230383_2_, ChunkGenerator p_230383_3_, Random randomIn, MutableBoundingBox structureBoundingBoxIn, ChunkPos p_230383_6_, BlockPos p_230383_7_) {
        if (model == null) {
            DungeonCrawl.LOGGER.warn("Missing model for {}", this);
            return true;
        }
        BlockPos pos = new BlockPos(x, y, z).offset(model.getOffset(rotation));
        switch (stairType) {
            case 0: {
                build(model, worldIn, structureBoundingBoxIn, pos, theme, secondaryTheme, stage, context, false);
                ironBars(worldIn, structureBoundingBoxIn, model, context);
                placeFeatures(worldIn, context, structureBoundingBoxIn, theme, secondaryTheme, randomIn, stage);
                decorate(worldIn, pos, context, model.width, model.height, model.length, theme, structureBoundingBoxIn, boundingBox, model);
                return true;
            }
            case 1: {
                build(model, worldIn, structureBoundingBoxIn, pos, theme, secondaryTheme, stage, context, false);
                entrances(worldIn, structureBoundingBoxIn, model);
                placeFeatures(worldIn, context, structureBoundingBoxIn, theme, secondaryTheme, randomIn, stage);
                decorate(worldIn, pos, context, model.width, model.height, model.length, theme, structureBoundingBoxIn, boundingBox, model);
                return true;
            }
            default:
                return true;
        }

    }

    public void ironBars(IWorld world, MutableBoundingBox bounds, DungeonModel model, PlacementContext context) {
        int pathStartX = (model.width - 3) / 2, pathStartZ = (model.length - 3) / 2;

        if (sides[0]) {
            for (int x0 = pathStartX; x0 < pathStartX + 3; x0++)
                for (int y0 = 1; y0 < 4; y0++)
                    replaceBlockState(world, theme.fencing.get(world, new BlockPos(x + x0, y + y0, z)), x + x0, y + y0, z, bounds, context);
        }
        if (sides[1]) {
            for (int z0 = pathStartZ; z0 < pathStartZ + 3; z0++)
                for (int y0 = 1; y0 < 4; y0++)
                    replaceBlockState(world, theme.fencing.get(world, new BlockPos(x + model.width, y + y0, z + z0)), x + model.width - 1, y + y0, z + z0, bounds, context);
        }
        if (sides[2]) {
            for (int x0 = pathStartX; x0 < pathStartX + 3; x0++)
                for (int y0 = 1; y0 < 4; y0++)
                    replaceBlockState(world, theme.fencing.get(world, new BlockPos(x + x0, y + y0, z + model.length)), x + x0, y + y0, z + model.length - 1, bounds, context);
        }
        if (sides[3]) {
            for (int z0 = pathStartZ; z0 < pathStartZ + 3; z0++)
                for (int y0 = 1; y0 < 4; y0++)
                    replaceBlockState(world, theme.fencing.get(world, new BlockPos(x, y + y0, z + z0)), x, y + y0, z + z0, bounds, context);
        }

    }

    @Override
    public void setupBoundingBox() {
        if (model != null) {
            this.boundingBox = model.createBoundingBoxWithOffset(x, y, z, rotation);
        }
    }

    @Override
    public boolean canConnect(Direction side, int x, int z) {
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
    public void addAdditionalSaveData(CompoundNBT tagCompound) {
        super.addAdditionalSaveData(tagCompound);
        tagCompound.putInt("stairType", stairType);
    }

    @Override
    public int getDungeonPieceType() {
        return STAIRS;
    }

}