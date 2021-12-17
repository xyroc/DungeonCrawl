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
import net.minecraft.util.math.vector.Vector3i;
import net.minecraft.world.ISeedReader;
import net.minecraft.world.gen.ChunkGenerator;
import net.minecraft.world.gen.Heightmap;
import net.minecraft.world.gen.feature.structure.StructureManager;
import net.minecraft.world.gen.feature.template.TemplateManager;
import xiroc.dungeoncrawl.DungeonCrawl;
import xiroc.dungeoncrawl.dungeon.DungeonBuilder;
import xiroc.dungeoncrawl.dungeon.PlacementConfiguration;
import xiroc.dungeoncrawl.dungeon.StructurePieceTypes;
import xiroc.dungeoncrawl.dungeon.model.DungeonModel;
import xiroc.dungeoncrawl.dungeon.model.DungeonModels;
import xiroc.dungeoncrawl.dungeon.model.ModelSelector;

import java.util.List;
import java.util.Random;

public class DungeonEntrance extends DungeonPiece {

    public DungeonEntrance() {
        super(StructurePieceTypes.ENTRANCE);
    }

    public DungeonEntrance(TemplateManager manager, CompoundNBT nbt) {
        super(StructurePieceTypes.ENTRANCE, nbt);
    }

    @Override
    public void setupModel(DungeonBuilder builder, ModelSelector modelSelector, List<DungeonPiece> pieces, Random rand) {
    }

    @Override
    public boolean postProcess(ISeedReader worldIn, StructureManager p_230383_2_, ChunkGenerator p_230383_3_, Random randomIn, MutableBoundingBox structureBoundingBoxIn, ChunkPos p_230383_6_, BlockPos p_230383_7_) {
        if (model == null) {
            DungeonCrawl.LOGGER.warn("Missing model for {}", this);
            return true;
        }

        Heightmap.Type heightmapType = worldGen ? Heightmap.Type.WORLD_SURFACE_WG : Heightmap.Type.WORLD_SURFACE;

        int height = worldIn.getHeight(heightmapType, x + 4, z + 4);
        int cursorHeight = y;

        DungeonModel staircaseLayer = DungeonModels.KEY_TO_MODEL.get(DungeonModels.STAIRCASE_LAYER);
        Rotation layerRotation = Rotation.NONE;
        while (cursorHeight < height) {
            buildModel(staircaseLayer, worldIn, structureBoundingBoxIn,
                    new BlockPos(x + 2, cursorHeight, z + 2), PlacementConfiguration.DEFAULT, theme, secondaryTheme, stage, layerRotation, worldGen, true, false);
            layerRotation = layerRotation.getRotated(Rotation.CLOCKWISE_90);
            cursorHeight++;
        }

        BlockPos pos = new BlockPos(x + 4, cursorHeight, z + 4).offset(rotatedOffset(model.getOffset(), layerRotation, model));

        buildModel(model, worldIn, structureBoundingBoxIn, pos, PlacementConfiguration.DEFAULT, theme, secondaryTheme, stage, layerRotation, worldGen, true, true);
        placeFeatures(worldIn, structureBoundingBoxIn, theme, secondaryTheme, randomIn, stage, worldGen);

        // A custom bounding box for decorations (eg. vines placement).
        // The original bounding box of this piece goes from the bottom to the top of the world,
        //  since the ground height is unknown up the point of actual generation.
        // And because we dont want the decorations to decorate everything from top to bottom,
        //  we use a custom bounding box for them.
        MutableBoundingBox populationBox = model.createBoundingBox(pos, rotation);
        decorate(worldIn, pos, model.width, model.height, model.length, theme, structureBoundingBoxIn, populationBox, model, worldGen);
        return true;
    }

    private static Vector3i rotatedOffset(Vector3i offset, Rotation rotation, DungeonModel model) {
        switch (rotation) {
            case CLOCKWISE_90:
                return new Vector3i(-model.length - offset.getZ() + 1, offset.getY(), offset.getX());
            case CLOCKWISE_180:
                return new Vector3i(-model.width - offset.getX() + 1, offset.getY(), -model.length - offset.getZ() + 1);
            case COUNTERCLOCKWISE_90:
                return new Vector3i(offset.getZ(), offset.getY(), -model.width - offset.getX() + 1);
            default:
                return offset;
        }
    }

    @Override
    public void createBoundingBox() {
        if (model != null) {
            Vector3i offset = model.getOffset(rotation);
            int x = this.x + 4 + offset.getX();
            int z = this.z + 4 + offset.getZ();
            switch (rotation) {
                case NONE:
                case CLOCKWISE_180:
                    this.boundingBox = new MutableBoundingBox(x, 0, z, x + model.width - 1, 256, z + model.length - 1);
                    break;
                case CLOCKWISE_90:
                case COUNTERCLOCKWISE_90:
                    this.boundingBox = new MutableBoundingBox(x, 0, z, x + model.length - 1, 256, z + model.width - 1);
                    break;
                default:
                    DungeonCrawl.LOGGER.warn("Unknown entrance rotation: {}", rotation);
                    this.boundingBox = new MutableBoundingBox(x, 0, z, x + model.width - 1, 256, z + model.length - 1);
            }
        }
    }

    @Override
    public int getDungeonPieceType() {
        return ENTRANCE;
    }

    @Override
    public void addAdditionalSaveData(CompoundNBT tagCompound) {
        super.addAdditionalSaveData(tagCompound);
    }

}