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
import net.minecraft.world.gen.Heightmap;
import net.minecraft.world.gen.feature.template.TemplateManager;
import xiroc.dungeoncrawl.DungeonCrawl;
import xiroc.dungeoncrawl.config.Config;
import xiroc.dungeoncrawl.dungeon.DungeonBuilder;
import xiroc.dungeoncrawl.dungeon.StructurePieceTypes;
import xiroc.dungeoncrawl.dungeon.block.WeightedRandomBlock;
import xiroc.dungeoncrawl.dungeon.model.*;
import xiroc.dungeoncrawl.dungeon.treasure.Treasure;
import xiroc.dungeoncrawl.theme.Theme;
import xiroc.dungeoncrawl.theme.Theme.SubTheme;

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
    public void setupModel(DungeonBuilder builder, ModelCategory layerCategory, List<DungeonPiece> pieces, Random rand) {
        if (ModelCategory.ENTRANCE.members.isEmpty()) {
            this.model = DungeonModels.DEFAULT_TOWER;
        } else {
            this.model = ModelCategory.ENTRANCE.members.get(rand.nextInt(ModelCategory.ENTRANCE.members.size()));
        }
    }

    @Override
    public boolean create(IWorld worldIn, ChunkGenerator<?> chunkGenerator, Random randomIn, MutableBoundingBox structureBoundingBoxIn,
                          ChunkPos p_74875_4_) {
        if (model == null) {
            DungeonCrawl.LOGGER.warn("Missing model for {}", this);
            return true;
        }

        int height = worldIn.getHeight(Heightmap.Type.WORLD_SURFACE_WG, x + 4, z + 4);
        int cursorHeight = y;

        while (cursorHeight < height) {
            if (height - cursorHeight <= 4)
                break;
            super.build(DungeonModels.STAIRCASE, worldIn, structureBoundingBoxIn,
                    new BlockPos(x + 2, cursorHeight, z + 2), theme, subTheme, Treasure.Type.DEFAULT, stage, true);
            cursorHeight += 8;
        }

        Vec3i offset = model.getOffset(rotation);
        BlockPos pos = new BlockPos(x + 4, cursorHeight, z + 4).add(offset);

        // Creating a custom bounding box because the cursor height was unknown during #setupBoundingBox.
        this.boundingBox = new MutableBoundingBox(pos.getX(), pos.getY(), pos.getZ(), pos.getX() + model.width - 1, pos.getY() + model.height - 1, pos.getZ() + model.length - 1);

        buildFull(model, worldIn, structureBoundingBoxIn, pos, theme, subTheme, model.getTreasureType(), stage, true);

        if (model.metadata != null && model.metadata.feature != null && featurePositions != null) {
            model.metadata.feature.build(worldIn, randomIn, pos, featurePositions, structureBoundingBoxIn, theme, subTheme, stage);
        }

        decorate(worldIn, pos, model.width, model.height, model.length, theme, structureBoundingBoxIn, boundingBox, model);
        return true;
    }

    @Override
    public void setupBoundingBox() {
        if (model != null) {
            this.boundingBox = model.createBoundingBoxWithOffset(x, y, z, rotation);
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

    public void buildFull(DungeonModel model, IWorld world, MutableBoundingBox boundsIn, BlockPos pos, Theme theme,
                          SubTheme subTheme, Treasure.Type treasureType, int lootLevel, boolean fillAir) {
        if (Config.EXTENDED_DEBUG.get()) {
            DungeonCrawl.LOGGER.debug("Building {} with model id {} at ({} | {} | {})", model.location, model.id, pos.getX(), pos.getY(), pos.getZ());
        }

        model.blocks.forEach((block) -> {
            BlockPos position = pos.add(block.position);
            if (boundsIn.isVecInside(position)) {
                Tuple<BlockState, Boolean> state = DungeonModelBlock.getBlockState(block,
                        Rotation.NONE, world, position, theme, subTheme, WeightedRandomBlock.RANDOM, variation, lootLevel);
                if (state == null)
                    return;

                setBlockState(state.getA(), world, treasureType, position, theme, subTheme, lootLevel,
                        fillAir ? DungeonModelBlockType.SOLID : block.type);

                if (state.getB()) {
                    world.getChunk(position).markBlockForPostprocessing(position);
                }

                if (block.position.getY() == 0
                        && model.height > 1
                        && world.isAirBlock(position.down())
                        && block.type == DungeonModelBlockType.SOLID) {
                    buildPillar(world, theme, pos.getX() + block.position.getX(), pos.getY(), pos.getZ() + block.position.getZ(), boundsIn);
                }
            }
        });

        if (Config.EXTENDED_DEBUG.get()) {
            DungeonCrawl.LOGGER.debug("Finished building {} with model id {} at ({} | {} | {})", model.location, model.id, pos.getX(), pos.getY(), pos.getZ());
        }
    }

}