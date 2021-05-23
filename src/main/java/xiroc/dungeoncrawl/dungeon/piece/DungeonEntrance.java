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
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.Rotation;
import net.minecraft.util.Tuple;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.MutableBoundingBox;
import net.minecraft.util.math.Vec3i;
import net.minecraft.world.IWorld;
import net.minecraft.world.gen.ChunkGenerator;
import net.minecraft.world.gen.feature.template.TemplateManager;
import xiroc.dungeoncrawl.DungeonCrawl;
import xiroc.dungeoncrawl.config.Config;
import xiroc.dungeoncrawl.dungeon.DungeonBuilder;
import xiroc.dungeoncrawl.dungeon.PlacementContext;
import xiroc.dungeoncrawl.dungeon.StructurePieceTypes;
import xiroc.dungeoncrawl.dungeon.block.WeightedRandomBlock;
import xiroc.dungeoncrawl.dungeon.model.DungeonModel;
import xiroc.dungeoncrawl.dungeon.model.DungeonModelBlock;
import xiroc.dungeoncrawl.dungeon.model.DungeonModelBlockType;
import xiroc.dungeoncrawl.dungeon.model.DungeonModels;
import xiroc.dungeoncrawl.dungeon.model.ModelSelector;
import xiroc.dungeoncrawl.theme.Theme;
import xiroc.dungeoncrawl.theme.Theme.SecondaryTheme;

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
        // TODO: use model selector
        this.model = DungeonModels.KEY_TO_MODEL.get(DungeonModels.ROGUELIKE_TOWER);
    }

    @Override
    public boolean create(IWorld worldIn, ChunkGenerator<?> chunkGenerator, Random randomIn, MutableBoundingBox structureBoundingBoxIn,
                          ChunkPos p_74875_4_) {
        if (model == null) {
            DungeonCrawl.LOGGER.warn("Missing model for {}", this);
            return true;
        }

        int height = worldIn.getHeight(context.heightmapType, x + 4, z + 4);
        int cursorHeight = y;

        DungeonModel staircase = DungeonModels.KEY_TO_MODEL.get(DungeonModels.STAIRCASE);

        while (cursorHeight < height) {
            if (height - cursorHeight <= 4)
                break;
            super.build(staircase, worldIn, structureBoundingBoxIn,
                    new BlockPos(x + 2, cursorHeight, z + 2), theme, secondaryTheme, stage, context, true);
            cursorHeight += 8;
        }

        Vec3i offset = model.getOffset(rotation);
        BlockPos pos = new BlockPos(x + 4, cursorHeight, z + 4).add(offset);

        build(model, worldIn, structureBoundingBoxIn, pos, theme, secondaryTheme, stage, context, true);

        if (model.metadata != null && model.metadata.feature != null && featurePositions != null) {
            model.metadata.feature.build(worldIn, context, randomIn, pos, featurePositions, structureBoundingBoxIn, theme, secondaryTheme, stage);
        }

        // A custom bounding box for decorations (eg. vines placement).
        // The original bounding box of this piece goes from the bottom to the top of the world,
        //  since the ground height is unknown up the point of actual generation.
        // And because we dont want the decorations to decorate everything from top to bottom,
        //  we use a custom bounding box for them.
        MutableBoundingBox populationBox = model.createBoundingBox(pos, rotation);
        decorate(worldIn, pos, context, model.width, model.height, model.length, theme, structureBoundingBoxIn, populationBox, model);
        return true;
    }

    @Override
    public void setupBoundingBox() {
        if (model != null) {
            Vec3i offset = model.getOffset(rotation);
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
    public int getType() {
        return 6;
    }

    @Override
    public void readAdditional(CompoundNBT tagCompound) {
        super.readAdditional(tagCompound);
    }

    public void build(DungeonModel model, IWorld world, MutableBoundingBox boundsIn, BlockPos pos, Theme theme,
                      SecondaryTheme secondaryTheme, int lootLevel, PlacementContext context, boolean fillAir) {
        if (Config.EXTENDED_DEBUG.get()) {
            DungeonCrawl.LOGGER.debug("Building {} with model id {} at ({} | {} | {})", model.getKey(), model.id, pos.getX(), pos.getY(), pos.getZ());
        }

        model.blocks.forEach((block) -> {
            BlockPos position = pos.add(block.position);
            if (boundsIn.isVecInside(position)) {
                Tuple<BlockState, Boolean> state = DungeonModelBlock.getBlockState(block,
                        Rotation.NONE, world, position, theme, secondaryTheme, WeightedRandomBlock.RANDOM, variation, lootLevel);
                if (state == null)
                    return;

                placeBlock(block, world, context, theme, secondaryTheme, lootLevel, fillAir, position, state);

                if (block.position.getY() == 0
                        && model.height > 1
                        && world.isAirBlock(position.down())
                        && block.type == DungeonModelBlockType.SOLID) {
                    buildPillar(world, theme, pos.getX() + block.position.getX(), pos.getY(), pos.getZ() + block.position.getZ(), boundsIn);
                }
            }
        });

        if (Config.EXTENDED_DEBUG.get()) {
            DungeonCrawl.LOGGER.debug("Finished building {} with model id {} at ({} | {} | {})", model.getKey(), model.id, pos.getX(), pos.getY(), pos.getZ());
        }
    }

}