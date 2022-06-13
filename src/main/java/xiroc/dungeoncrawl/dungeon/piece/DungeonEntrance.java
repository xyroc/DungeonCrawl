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
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.StructureManager;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import net.minecraft.world.level.levelgen.structure.pieces.StructurePieceSerializationContext;
import xiroc.dungeoncrawl.DungeonCrawl;
import xiroc.dungeoncrawl.dungeon.DungeonBuilder;
import xiroc.dungeoncrawl.dungeon.PlacementConfiguration;
import xiroc.dungeoncrawl.init.ModStructurePieceTypes;
import xiroc.dungeoncrawl.dungeon.model.DungeonModel;
import xiroc.dungeoncrawl.dungeon.model.DungeonModels;
import xiroc.dungeoncrawl.dungeon.model.ModelSelector;

import java.util.List;

public class DungeonEntrance extends DungeonPiece {

    private static final String KEY_SURFACE_HEIGHT = "SurfaceHeight";
    private Integer surfaceHeight;

    public DungeonEntrance() {
        super(ModStructurePieceTypes.ENTRANCE);
    }

    public DungeonEntrance(CompoundTag nbt) {
        super(ModStructurePieceTypes.ENTRANCE, nbt);
        if (nbt.contains(KEY_SURFACE_HEIGHT)) {
            this.surfaceHeight = nbt.getInt(KEY_SURFACE_HEIGHT);
        }
    }

    @Override
    public void setupModel(DungeonBuilder builder, ModelSelector modelSelector, List<DungeonPiece> pieces, RandomSource rand) {
    }

    @Override
    public void postProcess(WorldGenLevel worldIn, StructureManager p_230383_2_, ChunkGenerator p_230383_3_, RandomSource randomIn, BoundingBox structureBoundingBoxIn, ChunkPos p_230383_6_, BlockPos p_230383_7_) {
        if (model == null) {
            DungeonCrawl.LOGGER.warn("Missing model for {}", this);
            return;
        }

        if (this.surfaceHeight == null) {
            this.surfaceHeight = worldIn.getHeight(Heightmap.Types.WORLD_SURFACE_WG, x + 4, z + 4);
        }
        int cursorHeight = y;

        DungeonModel staircaseLayer = DungeonModels.KEY_TO_MODEL.get(DungeonModels.STAIRCASE_LAYER);
        Rotation layerRotation = Rotation.NONE;
        while (cursorHeight < surfaceHeight) {
            buildModel(staircaseLayer, worldIn, structureBoundingBoxIn,
                    new BlockPos(x + 2, cursorHeight, z + 2), randomIn, PlacementConfiguration.DEFAULT, theme, secondaryTheme, stage, layerRotation, true, false);
            layerRotation = layerRotation.getRotated(Rotation.CLOCKWISE_90);
            cursorHeight++;
        }

        BlockPos pos = new BlockPos(x + 4, cursorHeight, z + 4).offset(rotatedOffset(model.getOffset(), layerRotation, model));

        buildModel(model, worldIn, structureBoundingBoxIn, pos, randomIn, PlacementConfiguration.DEFAULT, theme, secondaryTheme, stage, layerRotation, true, true);
        placeFeatures(worldIn, structureBoundingBoxIn, theme, secondaryTheme, randomIn, stage);

        // A custom bounding box for decorations (eg. vines placement).
        // The original bounding box of this piece goes from the bottom to the top of the world,
        //  since the ground height is unknown up to the point of actual generation.
        // And because we don't want the decorations to decorate everything from top to bottom,
        //  we use a custom bounding box for them.
        BoundingBox populationBox = model.createBoundingBox(pos, layerRotation);
        decorate(worldIn, pos, theme, randomIn, structureBoundingBoxIn, populationBox, model);
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
            /*
             * Since the rotation depends on the ground height, which is unknown,
             *  we have to create a bounding box that contains all four possible rotations of this piece.
             */
            Vec3i defaultOffset = model.getOffset();
            Vec3i clockwise90 = rotatedOffset(defaultOffset, Rotation.CLOCKWISE_90, model);
            Vec3i clockwise180 = rotatedOffset(defaultOffset, Rotation.CLOCKWISE_180, model);
            Vec3i counterClockwise90 = rotatedOffset(defaultOffset, Rotation.COUNTERCLOCKWISE_90, model);

            int minOffsetX = Math.min(Math.min(defaultOffset.getX(), clockwise90.getX()), Math.min(clockwise180.getX(), counterClockwise90.getX()));
            int minOffsetZ = Math.min(Math.min(defaultOffset.getZ(), clockwise90.getZ()), Math.min(clockwise180.getZ(), counterClockwise90.getZ()));

            int x = this.x + 4 + minOffsetX;
            int z = this.z + 4 + minOffsetZ;

            this.boundingBox = new BoundingBox(x, 0, z, x + Math.max(model.width, model.length) - 1, 512, z + Math.max(model.width, model.length) - 1);
        }
    }

    @Override
    public int getDungeonPieceType() {
        return ENTRANCE;
    }

    @Override
    public void addAdditionalSaveData(StructurePieceSerializationContext context, CompoundTag tagCompound) {
        super.addAdditionalSaveData(context, tagCompound);
        if (this.surfaceHeight != null) {
            tagCompound.putInt(KEY_SURFACE_HEIGHT, this.surfaceHeight);
        }
    }

}