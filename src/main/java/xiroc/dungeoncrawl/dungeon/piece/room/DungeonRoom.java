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
import xiroc.dungeoncrawl.DungeonCrawl;
import xiroc.dungeoncrawl.dungeon.DungeonBuilder;
import xiroc.dungeoncrawl.dungeon.StructurePieceTypes;
import xiroc.dungeoncrawl.dungeon.model.ModelSelector;
import xiroc.dungeoncrawl.dungeon.piece.DungeonPiece;

import java.util.List;
import java.util.Random;

public class DungeonRoom extends DungeonPiece {

    public DungeonRoom(TemplateManager manager, CompoundNBT p_i51343_2_) {
        super(StructurePieceTypes.ROOM, p_i51343_2_);
    }

    @Override
    public void setupModel(DungeonBuilder builder, ModelSelector modelSelector, List<DungeonPiece> pieces, Random rand) {
        this.model = modelSelector.rooms.roll(rand);
    }

    @Override
    public boolean create(IWorld worldIn, ChunkGenerator<?> chunkGenerator, Random randomIn, MutableBoundingBox structureBoundingBoxIn,
                          ChunkPos p_74875_4_) {
        if (model == null) {
            DungeonCrawl.LOGGER.warn("Missing model for {}", this);
            return true;
        }

        Vec3i offset = model.getOffset(rotation);
        BlockPos pos = new BlockPos(x, y, z).add(offset);

        build(model, worldIn, structureBoundingBoxIn, pos, theme, secondaryTheme, stage, context, false);

        entrances(worldIn, structureBoundingBoxIn, model);

        if (model.metadata != null && model.metadata.feature != null && featurePositions != null) {
            model.metadata.feature.build(worldIn, context, randomIn, pos, featurePositions, structureBoundingBoxIn, theme, secondaryTheme, stage);
        }

        decorate(worldIn, pos, context, model.width, model.height, model.length, theme, structureBoundingBoxIn, boundingBox, model);
        return true;
    }

    @Override
    public void readAdditional(CompoundNBT tagCompound) {
        super.readAdditional(tagCompound);
    }

    @Override
    public void setupBoundingBox() {
        if (model != null) {
            this.boundingBox = model.createBoundingBoxWithOffset(x, y, z, rotation);
        }
    }

    @Override
    public int getType() {
        return 8;
    }

}
