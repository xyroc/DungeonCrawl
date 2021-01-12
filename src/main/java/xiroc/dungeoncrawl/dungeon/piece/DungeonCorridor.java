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
import net.minecraft.util.math.Vec3i;
import net.minecraft.world.IWorld;
import net.minecraft.world.gen.ChunkGenerator;
import net.minecraft.world.gen.feature.template.TemplateManager;
import xiroc.dungeoncrawl.config.Config;
import xiroc.dungeoncrawl.dungeon.DungeonBuilder;
import xiroc.dungeoncrawl.dungeon.StructurePieceTypes;
import xiroc.dungeoncrawl.dungeon.model.DungeonModel;
import xiroc.dungeoncrawl.dungeon.model.DungeonModels;
import xiroc.dungeoncrawl.dungeon.model.MultipartModelData;
import xiroc.dungeoncrawl.dungeon.treasure.Treasure;
import xiroc.dungeoncrawl.theme.Theme;
import xiroc.dungeoncrawl.util.WeightedRandom;

import java.util.List;
import java.util.Random;

public class DungeonCorridor extends DungeonPiece {

    public DungeonCorridor() {
        this(null, DEFAULT_NBT);
    }

    public DungeonCorridor(TemplateManager manager, CompoundNBT p_i51343_2_) {
        super(StructurePieceTypes.CORRIDOR, p_i51343_2_);
    }

    @Override
    public void setupModel(DungeonBuilder builder, DungeonModels.ModelCategory layerCategory, List<DungeonPiece> pieces, Random rand) {
        if (connectedSides == 2 && isStraight()) {
            this.modelID = DungeonModels.ModelCategory.get(DungeonModels.ModelCategory.CORRIDOR, layerCategory).roll(rand);
        } else {
            this.modelID = DungeonModels.ModelCategory.get(DungeonModels.ModelCategory.CORRIDOR_LINKER, layerCategory).roll(rand);
        }
    }

    @Override
    public boolean create(IWorld worldIn, ChunkGenerator<?> chunkGen, Random randomIn, MutableBoundingBox structureBoundingBoxIn,
                          ChunkPos p_74875_4_) {
        DungeonModel model = DungeonModels.MODELS.get(modelID);

        boolean ew = rotation == Rotation.NONE || rotation == Rotation.CLOCKWISE_180;

        int x = ew ? this.x : this.x + (9 - model.length) / 2;
        int z = ew ? this.z + (9 - model.length) / 2 : this.z;

        if (modelID == DungeonModels.CORRIDOR_SECRET_ROOM_ENTRANCE.id || modelID == 7) {
            switch (rotation) {
                case NONE:
                    z--;
                    break;
                case CLOCKWISE_90:
                    x++;
                    break;
                case CLOCKWISE_180:
                    z++;
                    break;
                case COUNTERCLOCKWISE_90:
                    x--;
                    break;
            }
        }
        BlockPos pos = new BlockPos(x, y + DungeonModels.getOffset(modelID).getY(), z);
        buildRotated(model, worldIn, structureBoundingBoxIn, pos,
                Theme.get(theme), Theme.getSub(subTheme), Treasure.Type.DEFAULT, stage, rotation, false);

        if (model.metadata != null && model.metadata.feature != null && featurePositions != null) {
            model.metadata.feature.build(worldIn, randomIn, pos, featurePositions, structureBoundingBoxIn, theme, subTheme, stage);
        }

        if (connectedSides != 2 || !isStraight()) {
            entrances(worldIn, structureBoundingBoxIn, model);
        }

        decorate(worldIn, pos, model.width, model.height, model.length, Theme.get(theme), structureBoundingBoxIn, boundingBox, model);

        if (Config.NO_SPAWNERS.get())
            spawnMobs(worldIn, this, model.width, model.length, new int[]{1});
        return true;
    }

    @SuppressWarnings("unchecked")
    @Override
    public void addChildPieces(List<DungeonPiece> pieces, DungeonBuilder builder, DungeonModels.ModelCategory layerCategory, int layer, Random rand) {
        DungeonModel model = DungeonModels.MODELS.get(modelID);
        if (model != null && model.multipartData != null) {
            boolean ew = rotation == Rotation.NONE || rotation == Rotation.CLOCKWISE_180;

            int x = ew ? this.x : this.x + (9 - model.length) / 2;
            int z = ew ? this.z + (9 - model.length) / 2 : this.z;
            if (modelID == DungeonModels.CORRIDOR_SECRET_ROOM_ENTRANCE.id || modelID == 7) {
                switch (rotation) {
                    case NONE:
                        z--;
                        break;
                    case CLOCKWISE_90:
                        x++;
                        break;
                    case CLOCKWISE_180:
                        z++;
                        break;
                    case COUNTERCLOCKWISE_90:
                        x--;
                        break;
                }
            }
            for (WeightedRandom<?> randomData : model.multipartData) {
                pieces.add(((WeightedRandom<MultipartModelData>) randomData).roll(rand).createMultipartPiece(this, model, rotation, x, y, z));
            }
        }
    }

    @Override
    public void setupBoundingBox() {
        Vec3i offset = DungeonModels.getOffset(modelID);
        if (modelID == DungeonModels.CORRIDOR_SECRET_ROOM_ENTRANCE.id) {
            this.boundingBox = new MutableBoundingBox(x - 1, y + offset.getY(), z - 1, x + 10, y + offset.getY() + 8, z + 10);
        } else {
            this.boundingBox = new MutableBoundingBox(x, y + offset.getY(), z, x + 8, y + offset.getY() + 8, z + 8);
        }
    }

    @Override
    public int getType() {
        return 0;
    }

    @Override
    public void readAdditional(CompoundNBT tagCompound) {
        super.readAdditional(tagCompound);
    }

    public boolean isStraight() {
        return sides[0] && sides[2] || sides[1] && sides[3];
    }

}
