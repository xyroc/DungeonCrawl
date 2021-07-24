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
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.StructureFeatureManager;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
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
        super(StructurePieceTypes.STAIRS);
        this.stairType = 0;
    }

    public DungeonStairs(ServerLevel serverLevel, CompoundTag p_i51343_2_) {
        super(StructurePieceTypes.STAIRS, p_i51343_2_);
        this.stairType = p_i51343_2_.getInt("stairType");
    }

    @Override
    public void setupModel(DungeonBuilder builder, ModelSelector modelSelector, List<DungeonPiece> pieces, Random rand) {
        switch (stairType) {
            case 0 -> this.model = stage > 0 ? DungeonModels.KEY_TO_MODEL.get(DungeonModels.BOTTOM_STAIRS_2) : DungeonModels.KEY_TO_MODEL.get(DungeonModels.BOTTOM_STAIRS);
            case 1 -> this.model = DungeonModels.KEY_TO_MODEL.get(DungeonModels.TOP_STAIRS);
        }
    }

    @Override
    public boolean postProcess(WorldGenLevel worldIn, StructureFeatureManager p_230383_2_, ChunkGenerator p_230383_3_, Random randomIn, BoundingBox structureBoundingBoxIn, ChunkPos p_230383_6_, BlockPos p_230383_7_) {
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

    public void ironBars(LevelAccessor world, BoundingBox bounds, DungeonModel model, PlacementContext context) {
        int pathStartX = (model.width - 3) / 2, pathStartZ = (model.length - 3) / 2;

        if (sides[0]) {
            for (int x0 = pathStartX; x0 < pathStartX + 3; x0++)
                for (int y0 = 1; y0 < 4; y0++)
                    replaceBlockState(world, IRON_BARS, x + x0, y + y0, z, bounds, context);
        }
        if (sides[1]) {
            for (int z0 = pathStartZ; z0 < pathStartZ + 3; z0++)
                for (int y0 = 1; y0 < 4; y0++)
                    replaceBlockState(world, IRON_BARS, x + model.width - 1, y + y0, z + z0, bounds, context);
        }
        if (sides[2]) {
            for (int x0 = pathStartX; x0 < pathStartX + 3; x0++)
                for (int y0 = 1; y0 < 4; y0++)
                    replaceBlockState(world, IRON_BARS, x + x0, y + y0, z + model.length - 1, bounds, context);
        }
        if (sides[3]) {
            for (int z0 = pathStartZ; z0 < pathStartZ + 3; z0++)
                for (int y0 = 1; y0 < 4; y0++)
                    replaceBlockState(world, IRON_BARS, x, y + y0, z + z0, bounds, context);
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
    public void addAdditionalSaveData(ServerLevel serverLevel, CompoundTag tagCompound) {
        super.addAdditionalSaveData(serverLevel, tagCompound);
        tagCompound.putInt("stairType", stairType);
    }

    @Override
    public int getDungeonPieceType() {
        return 1;
    }

}