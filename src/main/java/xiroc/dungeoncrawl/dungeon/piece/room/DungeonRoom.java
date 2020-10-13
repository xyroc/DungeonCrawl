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

package xiroc.dungeoncrawl.dungeon.piece.room;

import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.MutableBoundingBox;
import net.minecraft.util.math.Vec3i;
import net.minecraft.world.IWorld;
import net.minecraft.world.gen.ChunkGenerator;
import net.minecraft.world.gen.feature.template.TemplateManager;
import xiroc.dungeoncrawl.config.Config;
import xiroc.dungeoncrawl.dungeon.DungeonBuilder;
import xiroc.dungeoncrawl.dungeon.StructurePieceTypes;
import xiroc.dungeoncrawl.dungeon.model.DungeonModel;
import xiroc.dungeoncrawl.dungeon.model.DungeonModelFeature;
import xiroc.dungeoncrawl.dungeon.model.DungeonModels;
import xiroc.dungeoncrawl.dungeon.piece.DungeonPiece;
import xiroc.dungeoncrawl.dungeon.treasure.Treasure;
import xiroc.dungeoncrawl.theme.Theme;

import java.util.Random;

public class DungeonRoom extends DungeonPiece {

    public DungeonRoom(TemplateManager manager, CompoundNBT p_i51343_2_) {
        super(StructurePieceTypes.ROOM, p_i51343_2_);
    }

    @Override
    public int determineModel(DungeonBuilder builder, DungeonModels.ModelCategory layerCategory, Random rand) {
        return DungeonModels.ModelCategory.get(DungeonModels.ModelCategory.ROOM, layerCategory).roll(rand);
    }

    @Override
    public boolean func_225577_a_(IWorld worldIn, ChunkGenerator<?> chunkGenerator, Random randomIn, MutableBoundingBox structureBoundingBoxIn,
                                  ChunkPos p_74875_4_) {
        DungeonModel model = DungeonModels.MODELS.get(modelID);

        if (model == null)
            return false;

        BlockPos pos = new BlockPos(x, y + DungeonModels.getOffset(modelID).getY(), z);

        build(model, worldIn, structureBoundingBoxIn, pos, Theme.get(theme), Theme.getSub(subTheme),
                Treasure.Type.DEFAULT, stage, false);

        entrances(worldIn, structureBoundingBoxIn, model);

        if (model.metadata != null && model.metadata.feature != null && featurePositions != null) {
            model.metadata.feature.build(worldIn, randomIn, pos, featurePositions, structureBoundingBoxIn, theme, subTheme, stage);
        }

        decorate(worldIn, pos, model.width, model.height, model.length, Theme.get(theme), structureBoundingBoxIn, boundingBox, model);

//        if (featurePositions != null) {
//            DungeonCrawl.LOGGER.info("SPAWNER ROOM {} {} {} ({}) BOUNDS: [{} {} {} {} {} {}]", x, y, z, featurePositions.length,
//                    structureBoundingBoxIn.minX, structureBoundingBoxIn.minY, structureBoundingBoxIn.minZ,
//                    structureBoundingBoxIn.maxX, structureBoundingBoxIn.maxY, structureBoundingBoxIn.maxZ);
//            for (DirectionalBlockPos pos : featurePositions) {
//                DungeonCrawl.LOGGER.info("VEC ({} {} {}) INSIDE: {}", pos.position.getX(), pos.position.getY(), pos.position.getZ(), structureBoundingBoxIn.isVecInside(pos.position));
//                if (structureBoundingBoxIn.isVecInside(pos.position)) {
//                    IBlockPlacementHandler.getHandler(Blocks.CHEST).placeBlock(worldIn,
//                            Blocks.CHEST.getDefaultState().with(BlockStateProperties.HORIZONTAL_FACING, pos.direction),
//                            pos.position, randomIn, Treasure.Type.DEFAULT, theme, stage);
//                }
//            }
//        }

        if (Config.NO_SPAWNERS.get())
            spawnMobs(worldIn, this, model.width, model.length, new int[]{0});
        return true;
    }

    @Override
    public void readAdditional(CompoundNBT tagCompound) {
        super.readAdditional(tagCompound);
    }

    @Override
    public void setupBoundingBox() {
        Vec3i offset = DungeonModels.getOffset(modelID);
        this.boundingBox = new MutableBoundingBox(x, y + offset.getY(), z, x + 8, y + offset.getY() + 8, z + 8);
    }

    @Override
    public int getType() {
        return 8;
    }

}
