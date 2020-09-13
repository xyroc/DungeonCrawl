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
import xiroc.dungeoncrawl.dungeon.DungeonBuilder;
import xiroc.dungeoncrawl.dungeon.StructurePieceTypes;
import xiroc.dungeoncrawl.dungeon.block.WeightedRandomBlock;
import xiroc.dungeoncrawl.dungeon.model.DungeonModel;
import xiroc.dungeoncrawl.dungeon.model.DungeonModelBlock;
import xiroc.dungeoncrawl.dungeon.model.DungeonModelBlockType;
import xiroc.dungeoncrawl.dungeon.model.DungeonModels;
import xiroc.dungeoncrawl.dungeon.treasure.Treasure;
import xiroc.dungeoncrawl.theme.Theme;
import xiroc.dungeoncrawl.theme.Theme.SubTheme;

import java.util.Random;

public class DungeonEntrance extends DungeonPiece {

    public DungeonEntrance() {
        super(StructurePieceTypes.ENTRANCE);
    }

    public DungeonEntrance(TemplateManager manager, CompoundNBT nbt) {
        super(StructurePieceTypes.ENTRANCE, nbt);
    }

    @Override
    public int determineModel(DungeonBuilder builder, Random rand) {
        if (DungeonModels.ModelCategory.ENTRANCE.members.isEmpty()) {
            DungeonCrawl.LOGGER.warn("The entrance model list is empty. Using the roguelike entrance.");
            return DungeonModels.ENTRANCE.id;
        }
        return DungeonModels.ModelCategory.ENTRANCE.members.get(rand.nextInt(DungeonModels.ModelCategory.ENTRANCE.members.size())).id;
    }

    @Override
    public boolean func_225577_a_(IWorld worldIn, ChunkGenerator<?> chunkGenerator, Random randomIn, MutableBoundingBox structureBoundingBoxIn,
                                  ChunkPos p_74875_4_) {
        int height = worldIn.getHeight(Heightmap.Type.WORLD_SURFACE_WG, x + 4, z + 4);

        Theme buildTheme = Theme.get(theme);
        SubTheme sub = Theme.getSub(subTheme);
        int cursorHeight = y;

        while (cursorHeight < height) {
            if (height - cursorHeight <= 4)
                break;
            super.build(DungeonModels.STAIRCASE, worldIn, structureBoundingBoxIn,
                    new BlockPos(x + 2, cursorHeight, z + 2), buildTheme, sub, Treasure.Type.DEFAULT, stage, true);
            cursorHeight += 8;
        }

        DungeonModel entrance = DungeonModels.MODELS.get(modelID);

        Vec3i offset = DungeonModels.getOffset(entrance.id);

        BlockPos pos = new BlockPos(x + offset.getX(), cursorHeight + offset.getY(), z + offset.getZ());

        buildFull(entrance, worldIn, structureBoundingBoxIn, pos, Theme.get(theme),
                Theme.getSub(subTheme), Treasure.Type.SUPPLY, stage, true);

        if (entrance.metadata != null && entrance.metadata.feature != null && featurePositions != null) {
            entrance.metadata.feature.build(worldIn, randomIn, pos, featurePositions, structureBoundingBoxIn, theme, subTheme, stage);
        }

        decorate(worldIn, pos, entrance.width, entrance.height, entrance.length, Theme.get(theme), structureBoundingBoxIn, boundingBox, entrance);
        return true;
    }

    @Override
    public void setupBoundingBox() {
        DungeonModel model = DungeonModels.MODELS.get(modelID);
        Vec3i offset = DungeonModels.getOffset(modelID);
        this.boundingBox = new MutableBoundingBox(x + offset.getX(), y + offset.getY(), z + offset.getZ(),
                x + offset.getX() + model.width - 1, y + offset.getY() + model.height - 1, z + offset.getZ() + model.length - 1);
    }

    @Override
    public int getType() {
        return 6;
    }

    @Override
    public void readAdditional(CompoundNBT tagCompound) {
        super.readAdditional(tagCompound);
    }

    @Override
    public void build(DungeonModel model, IWorld world, MutableBoundingBox boundsIn, BlockPos pos, Theme theme,
                      SubTheme subTheme, Treasure.Type treasureType, int lootLevel, boolean fillAir) {

        int xStart = Math.max(boundsIn.minX, pos.getX()) - pos.getX(),
                width = Math.min(model.width, boundsIn.maxX - pos.getX() - xStart + 1);
        int zStart = Math.max(boundsIn.minZ, pos.getZ()) - pos.getZ(),
                length = Math.min(model.length, boundsIn.maxZ - pos.getZ() - zStart + 1);

        for (int x = xStart; x < width; x++) {
            for (int y = 0; y < model.height; y++) {
                for (int z = zStart; z < length; z++) {
                    if (model.model[x][y][z] == null) {
                        setBlockState(CAVE_AIR, world, boundsIn, treasureType, pos.getX() + x, pos.getY() + y, pos.getZ() + z,
                                this.theme, lootLevel, DungeonModelBlockType.SOLID);
                    } else {
                        BlockPos position = new BlockPos(pos.getX() + x, pos.getY() + y, pos.getZ() + z);
                        Tuple<BlockState, Boolean> result = DungeonModelBlock.getBlockState(model.model[x][y][z], Rotation.NONE, world,
                                position, theme, subTheme,
                                WeightedRandomBlock.RANDOM, variation, lootLevel);
                        if (result == null)
                            continue;
                        setBlockState(result.getA(), world, boundsIn, treasureType, pos.getX() + x, pos.getY() + y, pos.getZ() + z,
                                this.theme, lootLevel, fillAir ? DungeonModelBlockType.SOLID : model.model[x][y][z].type);

                        if (result.getB()) {
                            world.getChunk(position).markBlockForPostprocessing(position);
                        }

                        if (y == 0 && world.isAirBlock(position.down())
                                && model.model[x][0][z].type == DungeonModelBlockType.SOLID) {
                            DungeonBuilder.buildPillar(world, theme, position.getX(), position.getY(), position.getZ(), boundsIn);
                        }
                    }
                }
            }
        }
    }

    @Override
    public void buildFull(DungeonModel model, IWorld world, MutableBoundingBox boundsIn, BlockPos pos, Theme theme,
                          SubTheme subTheme, Treasure.Type treasureType, int lootLevel, boolean fillAir) {

        for (int x = 0; x < model.width; x++) {
            for (int y = 0; y < model.height; y++) {
                for (int z = 0; z < model.length; z++) {
                    BlockPos position = new BlockPos(pos.getX() + x, pos.getY() + y, pos.getZ() + z);
                    if (boundsIn.isVecInside(position)) {
                        if (model.model[x][y][z] == null) {
                            setBlockState(CAVE_AIR, world, boundsIn, treasureType, position,
                                    this.theme, lootLevel, DungeonModelBlockType.SOLID);
                        } else {
                            Tuple<BlockState, Boolean> result = DungeonModelBlock.getBlockState(model.model[x][y][z],
                                    Rotation.NONE, world, position, theme, subTheme, WeightedRandomBlock.RANDOM, variation, lootLevel);
                            if (result == null)
                                continue;
                            setBlockState(result.getA(), world, boundsIn, treasureType, position, this.theme, lootLevel,
                                    fillAir ? DungeonModelBlockType.SOLID : model.model[x][y][z].type);

                            if (result.getB()) {
                                world.getChunk(position).markBlockForPostprocessing(position);
                            }

                            if (y == 0 && world.isAirBlock(position.down())
                                    && model.model[x][0][z].type == DungeonModelBlockType.SOLID) {
                                DungeonBuilder.buildPillar(world, theme, position.getX(), position.getY(), position.getZ(), boundsIn);
                            }
                        }
                    }
                }
            }
        }
    }

}