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
import net.minecraft.core.Vec3i;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.StructureFeatureManager;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
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

    public DungeonEntrance(CompoundTag nbt) {
        super(StructurePieceTypes.ENTRANCE, nbt);
    }

    @Override
    public void setupModel(DungeonBuilder builder, ModelSelector modelSelector, List<DungeonPiece> pieces, Random rand) {
    }

    @Override
    public void postProcess(WorldGenLevel worldIn, StructureFeatureManager p_230383_2_, ChunkGenerator p_230383_3_, Random randomIn, BoundingBox structureBoundingBoxIn, ChunkPos p_230383_6_, BlockPos p_230383_7_) {
        if (model == null) {
            DungeonCrawl.LOGGER.warn("Missing model for {}", this);
            return;
        }

        Heightmap.Types heightmapType = worldGen ? Heightmap.Types.WORLD_SURFACE_WG : Heightmap.Types.WORLD_SURFACE;

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

        BoundingBox populationBox = model.createBoundingBox(pos, rotation);
        decorate(worldIn, pos, model.width, model.height, model.length, theme, structureBoundingBoxIn, populationBox, model, worldGen);
    }

    private static Vec3i rotatedOffset(Vec3i offset, Rotation rotation, DungeonModel model) {
        return switch (rotation) {
            case CLOCKWISE_90 -> new Vec3i(-model.length - offset.getZ() + 1, offset.getY(), offset.getX());
            case CLOCKWISE_180 -> new Vec3i(-model.width - offset.getX() + 1, offset.getY(), -model.length - offset.getZ() + 1);
            case COUNTERCLOCKWISE_90 -> new Vec3i(offset.getZ(), offset.getY(), -model.width - offset.getX() + 1);
            default -> offset;
        };
    }

    @Override
    public void createBoundingBox() {
        if (model != null) {
            Vec3i offset = model.getOffset(rotation);
            int x = this.x + 4 + offset.getX();
            int z = this.z + 4 + offset.getZ();
            switch (rotation) {
                case NONE, CLOCKWISE_180 -> this.boundingBox = new BoundingBox(x, 0, z, x + model.width - 1, 256, z + model.length - 1);
                case CLOCKWISE_90, COUNTERCLOCKWISE_90 -> this.boundingBox = new BoundingBox(x, 0, z, x + model.length - 1, 256, z + model.width - 1);
                default -> {
                    DungeonCrawl.LOGGER.warn("Unknown entrance rotation: {}", rotation);
                    this.boundingBox = new BoundingBox(x, 0, z, x + model.width - 1, 256, z + model.length - 1);
                }
            }
        }
    }

    @Override
    public int getDungeonPieceType() {
        return ENTRANCE;
    }

}