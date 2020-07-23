package xiroc.dungeoncrawl.dungeon.piece.room;

/*
 * DungeonCrawl (C) 2019 - 2020 XYROC (XIROC1337), All Rights Reserved
 */

import net.minecraft.block.BlockState;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.Rotation;
import net.minecraft.util.Tuple;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.MutableBoundingBox;
import net.minecraft.world.ISeedReader;
import net.minecraft.world.IWorld;
import net.minecraft.world.gen.ChunkGenerator;
import net.minecraft.world.gen.feature.structure.StructureManager;
import net.minecraft.world.gen.feature.template.TemplateManager;
import xiroc.dungeoncrawl.DungeonCrawl;
import xiroc.dungeoncrawl.dungeon.DungeonBuilder;
import xiroc.dungeoncrawl.dungeon.StructurePieceTypes;
import xiroc.dungeoncrawl.dungeon.block.WeightedRandomBlock;
import xiroc.dungeoncrawl.dungeon.model.*;
import xiroc.dungeoncrawl.dungeon.piece.DungeonPiece;
import xiroc.dungeoncrawl.dungeon.treasure.Treasure;
import xiroc.dungeoncrawl.theme.Theme;

import java.util.Random;

public class DungeonSecretRoom extends DungeonPiece {

    public DungeonSecretRoom(TemplateManager manager, CompoundNBT nbt) {
        super(StructurePieceTypes.SECRET_ROOM, nbt);
    }

    @Override
    public boolean func_230383_a_(ISeedReader worldIn, StructureManager p_230383_2_, ChunkGenerator p_230383_3_, Random randomIn, MutableBoundingBox structureBoundingBoxIn, ChunkPos p_230383_6_, BlockPos p_230383_7_) {
        buildRotatedFull(DungeonModels.MODELS.get(modelID), worldIn, structureBoundingBoxIn, new BlockPos(x, y, z),
                Theme.get(theme), Theme.getSub(subTheme), Treasure.MODEL_TREASURE_TYPES.getOrDefault(modelID, Treasure.Type.DEFAULT),
                stage, rotation, false);
        return true;
    }

    @Override
    public int getType() {
        return 14;
    }

    @Override
    public int determineModel(DungeonBuilder builder, Random rand) {
        return DungeonModels.SECRET_ROOM.id;
    }

    @Override
    public void setRealPosition(int x, int y, int z) {
        switch (rotation) {
            case NONE:
                super.setRealPosition(x + 1, y, z);
                break;
            case CLOCKWISE_90:
                super.setRealPosition(x, y, z + 1);
                break;
            default:
                super.setRealPosition(x, y, z);
                break;
        }
    }

    @Override
    public void setupBoundingBox() {
        if (rotation == Rotation.NONE || rotation == Rotation.CLOCKWISE_180) {
            this.boundingBox = new MutableBoundingBox(x, y, z, x + 16, y + 8, z + 8);
        } else {
            this.boundingBox = new MutableBoundingBox(x, y, z, x + 8, y + 8, z + 16);
        }
    }

    @Override
    public void build(DungeonModel model, IWorld world, MutableBoundingBox boundsIn, BlockPos pos, Theme theme,
                      Theme.SubTheme subTheme, Treasure.Type treasureType, int lootLevel, boolean fillAir) {
        for (int x = 0; x < model.width; x++) {
            for (int y = 0; y < model.height; y++) {
                for (int z = 0; z < model.length; z++) {
                    BlockPos position = new BlockPos(pos.getX() + x, pos.getY() + y, pos.getZ() + z);
                    if (boundsIn.isVecInside(position)) {
                        if (model.model[x][y][z] == null) {
                            setBlockState(CAVE_AIR, world, boundsIn, treasureType, position.getX(), position.getY(), position.getZ(),
                                    this.theme, lootLevel, PlacementBehaviour.NON_SOLID);
                        } else {
                            Tuple<BlockState, Boolean> result = DungeonModelBlock.getBlockState(model.model[x][y][z], Rotation.NONE, world,
                                    position, theme, subTheme,
                                    WeightedRandomBlock.RANDOM, variation, lootLevel);
                            if (result == null)
                                continue;
                            setBlockState(result.getA(), world, boundsIn, treasureType, position.getX(), position.getY(), position.getZ(),
                                    this.theme, lootLevel, fillAir ? PlacementBehaviour.SOLID : model.model[x][y][z].type.placementBehavior);

                            if (result.getB()) {
                                world.getChunk(position).markBlockForPostprocessing(position);
                            }

                            if (y == 0 && model.height > 1
                                    && world.isAirBlock(position.down()) && model.model[x][1][z] != null
                                    && model.model[x][0][z].type == DungeonModelBlockType.SOLID
                                    && model.model[x][1][z].type == DungeonModelBlockType.SOLID) {
                                DungeonBuilder.buildPillar(world, theme, pos.getX() + x, pos.getY(), pos.getZ() + z, boundsIn);
                            }
                        }
                    }
                }
            }
        }
    }

    @Override
    public void buildFull(DungeonModel model, IWorld world, MutableBoundingBox boundsIn, BlockPos pos, Theme theme,
                          Theme.SubTheme subTheme, Treasure.Type treasureType, int lootLevel, boolean fillAir) {

        for (int x = 0; x < model.width; x++) {
            for (int y = 0; y < model.height; y++) {
                for (int z = 0; z < model.length; z++) {
                    BlockPos position = new BlockPos(pos.getX() + x, pos.getY() + y, pos.getZ() + z);
                    if (boundsIn.isVecInside(position)) {
                        if (model.model[x][y][z] == null) {
                            setBlockState(CAVE_AIR, world, boundsIn, treasureType, position,
                                    this.theme, lootLevel, PlacementBehaviour.NON_SOLID);
                        } else {
                            Tuple<BlockState, Boolean> result = DungeonModelBlock.getBlockState(model.model[x][y][z],
                                    Rotation.NONE, world, position, theme, subTheme, WeightedRandomBlock.RANDOM, variation, lootLevel);
                            if (result == null)
                                continue;
                            setBlockState(result.getA(), world, boundsIn, treasureType, position, this.theme, lootLevel,
                                    fillAir ? PlacementBehaviour.SOLID : model.model[x][y][z].type.placementBehavior);

                            if (result.getB()) {
                                world.getChunk(position).markBlockForPostprocessing(position);
                            }

                            if (y == 0 && model.height > 1
                                    && world.isAirBlock(position.down()) && model.model[x][1][z] != null
                                    && model.model[x][0][z].type == DungeonModelBlockType.SOLID
                                    && model.model[x][1][z].type == DungeonModelBlockType.SOLID) {
                                DungeonBuilder.buildPillar(world, theme, pos.getX() + x, pos.getY(), pos.getZ() + z, boundsIn);
                            }
                        }
                    }
                }
            }
        }
    }

    @Override
    public void buildRotatedFull(DungeonModel model, IWorld world, MutableBoundingBox boundsIn, BlockPos pos, Theme theme, Theme.SubTheme subTheme, Treasure.Type treasureType, int lootLevel, Rotation rotation, boolean fillAir) {
        switch (rotation) {
            case CLOCKWISE_90: {
                for (int x = 0; x < model.width; x++) {
                    for (int y = 0; y < model.height; y++) {
                        for (int z = 0; z < model.length; z++) {
                            BlockPos position = new BlockPos(pos.getX() + model.length - z - 1, pos.getY() + y, pos.getZ() + x);
                            if (boundsIn.isVecInside(position)) {
                                if (model.model[x][y][z] == null) {
                                    setBlockState(CAVE_AIR, world, boundsIn, treasureType, position,
                                            this.theme, lootLevel, PlacementBehaviour.NON_SOLID);
                                } else {
                                    Tuple<BlockState, Boolean> result = DungeonModelBlock.getBlockState(model.model[x][y][z],
                                            Rotation.CLOCKWISE_90, world, position, theme, subTheme, WeightedRandomBlock.RANDOM, variation, lootLevel);
                                    if (result == null)
                                        continue;
                                    setBlockState(result.getA(), world, boundsIn, treasureType, position, this.theme, lootLevel,
                                            fillAir ? PlacementBehaviour.SOLID : model.model[x][y][z].type.placementBehavior);

                                    if (result.getB()) {
                                        world.getChunk(position).markBlockForPostprocessing(position);
                                    }

                                    if (y == 0 && model.height > 1
                                            && world.isAirBlock(position.down()) && model.model[x][1][z] != null
                                            && model.model[x][0][z].type == DungeonModelBlockType.SOLID
                                            && model.model[x][1][z].type == DungeonModelBlockType.SOLID) {
                                        DungeonBuilder.buildPillar(world, theme, position.getX(), position.getY(), position.getZ(),
                                                boundsIn);
                                    }
                                }
                            }
                        }
                    }
                }
                break;
            }
            case COUNTERCLOCKWISE_90: {
                for (int x = 0; x < model.width; x++) {
                    for (int y = 0; y < model.height; y++) {
                        for (int z = 0; z < model.length; z++) {
                            BlockPos position = new BlockPos(pos.getX() + z, pos.getY() + y, pos.getZ() + model.width - x - 1);
                            if (boundsIn.isVecInside(position)) {
                                if (model.model[x][y][z] == null) {
                                    setBlockState(CAVE_AIR, world, boundsIn, treasureType, position, this.theme, lootLevel, PlacementBehaviour.NON_SOLID);
                                } else {
                                    Tuple<BlockState, Boolean> result = DungeonModelBlock.getBlockState(model.model[x][y][z],
                                            Rotation.COUNTERCLOCKWISE_90, world, position, theme, subTheme,
                                            WeightedRandomBlock.RANDOM, variation, lootLevel);
                                    if (result == null)
                                        continue;
                                    setBlockState(result.getA(), world, boundsIn, treasureType, position, this.theme, lootLevel,
                                            fillAir ? PlacementBehaviour.SOLID : model.model[x][y][z].type.placementBehavior);

                                    if (result.getB()) {
                                        world.getChunk(position).markBlockForPostprocessing(position);
                                    }

                                    if (y == 0 && model.height > 1
                                            && world.isAirBlock(position.down()) && model.model[x][1][z] != null
                                            && model.model[x][0][z].type == DungeonModelBlockType.SOLID
                                            && model.model[x][1][z].type == DungeonModelBlockType.SOLID) {
                                        DungeonBuilder.buildPillar(world, theme, position.getX(), position.getY(), position.getZ(),
                                                boundsIn);
                                    }
                                }
                            }
                        }
                    }
                }
                break;
            }
            case CLOCKWISE_180: {
                for (int x = 0; x < model.width; x++) {
                    for (int y = 0; y < model.height; y++) {
                        for (int z = 0; z < model.length; z++) {
                            BlockPos position = new BlockPos(pos.getX() + model.width - x - 1, pos.getY() + y, pos.getZ() + model.length - z - 1);
                            if (boundsIn.isVecInside(position)) {
                                if (model.model[x][y][z] == null) {
                                    setBlockState(CAVE_AIR, world, boundsIn, treasureType, position, this.theme, lootLevel, PlacementBehaviour.NON_SOLID);
                                } else {
                                    Tuple<BlockState, Boolean> result = DungeonModelBlock.getBlockState(model.model[x][y][z], Rotation.CLOCKWISE_180, world,
                                            position, theme, subTheme, WeightedRandomBlock.RANDOM, variation, lootLevel);
                                    if (result == null)
                                        continue;
                                    setBlockState(result.getA(), world, boundsIn, treasureType, position, this.theme, lootLevel,
                                            fillAir ? PlacementBehaviour.SOLID : model.model[x][y][z].type.placementBehavior);

                                    if (result.getB()) {
                                        world.getChunk(position).markBlockForPostprocessing(position);
                                    }

                                    if (y == 0 && model.height > 1
                                            && world.isAirBlock(position.down()) && model.model[x][1][z] != null
                                            && model.model[x][0][z].type == DungeonModelBlockType.SOLID
                                            && model.model[x][1][z].type == DungeonModelBlockType.SOLID) {
                                        DungeonBuilder.buildPillar(world, theme, position.getX(), position.getY(), position.getZ(),
                                                boundsIn);
                                    }
                                }
                            }
                        }
                    }
                }
                break;
            }
            case NONE:
                buildFull(model, world, boundsIn, pos, theme, subTheme, treasureType, lootLevel, fillAir);
                break;
            default:
                DungeonCrawl.LOGGER.warn("Failed to build a rotated dungeon segment: Unsupported rotation " + rotation);
                break;
        }
    }

    @Override
    public void buildRotated(DungeonModel model, IWorld world, MutableBoundingBox boundsIn, BlockPos pos, Theme theme,
                             Theme.SubTheme subTheme, Treasure.Type treasureType, int lootLevel, Rotation rotation, boolean fillAir) {
        switch (rotation) {
            case CLOCKWISE_90: {
                for (int x = 0; x < model.width; x++) {
                    for (int y = 0; y < model.height; y++) {
                        for (int z = 0; z < model.length; z++) {
                            BlockPos position = new BlockPos(pos.getX() + model.length - z - 1, pos.getY() + y, pos.getZ() + x);
                            if (boundsIn.isVecInside(position)) {
                                if (model.model[x][y][z] == null) {
                                    setBlockState(CAVE_AIR, world, boundsIn, treasureType, position.getX(), position.getY(), position.getZ(),
                                            this.theme, lootLevel, PlacementBehaviour.NON_SOLID);
                                } else {
                                    Tuple<BlockState, Boolean> result = DungeonModelBlock.getBlockState(model.model[x][y][z], Rotation.CLOCKWISE_90, world,
                                            position, theme, subTheme,
                                            WeightedRandomBlock.RANDOM, variation, lootLevel);
                                    if (result == null)
                                        continue;
                                    setBlockState(result.getA(), world, boundsIn, treasureType, position.getX(), position.getY(), position.getZ(),
                                            this.theme, lootLevel, fillAir ? PlacementBehaviour.SOLID : model.model[x][y][z].type.placementBehavior);

                                    if (result.getB()) {
                                        world.getChunk(position).markBlockForPostprocessing(position);
                                    }

                                    if (y == 0 && model.height > 1
                                            && world.isAirBlock(position.down()) && model.model[x][1][z] != null
                                            && model.model[x][0][z].type == DungeonModelBlockType.SOLID
                                            && model.model[x][1][z].type == DungeonModelBlockType.SOLID) {
                                        DungeonBuilder.buildPillar(world, theme, position.getX(), position.getY(), position.getZ(),
                                                boundsIn);
                                    }
                                }
                            }
                        }
                    }
                }
                break;
            }
            case COUNTERCLOCKWISE_90: {
                for (int x = 0; x < model.width; x++) {
                    for (int y = 0; y < model.height; y++) {
                        for (int z = 0; z < model.length; z++) {
                            BlockPos position = new BlockPos(pos.getX() + z, pos.getY() + y, pos.getZ() + model.width - x - 1);
                            if (boundsIn.isVecInside(position)) {
                                if (model.model[x][y][z] == null) {
                                    setBlockState(CAVE_AIR, world, boundsIn, treasureType, position.getX(), position.getY(), position.getZ(),
                                            this.theme, lootLevel, PlacementBehaviour.NON_SOLID);
                                } else {
                                    Tuple<BlockState, Boolean> result = DungeonModelBlock.getBlockState(model.model[x][y][z],
                                            Rotation.COUNTERCLOCKWISE_90, world, position, theme, subTheme,
                                            WeightedRandomBlock.RANDOM, variation, lootLevel);
                                    if (result == null)
                                        continue;
                                    setBlockState(result.getA(), world, boundsIn, treasureType, position.getX(), position.getY(), position.getZ(),
                                            this.theme, lootLevel, fillAir ? PlacementBehaviour.SOLID : model.model[x][y][z].type.placementBehavior);

                                    if (result.getB()) {
                                        world.getChunk(position).markBlockForPostprocessing(position);
                                    }

                                    if (y == 0 && model.height > 1
                                            && world.isAirBlock(position.down()) && model.model[x][1][z] != null
                                            && model.model[x][0][z].type == DungeonModelBlockType.SOLID
                                            && model.model[x][1][z].type == DungeonModelBlockType.SOLID) {
                                        DungeonBuilder.buildPillar(world, theme, position.getX(), position.getY(), position.getZ(),
                                                boundsIn);
                                    }
                                }
                            }
                        }
                    }
                }
                break;
            }
            case CLOCKWISE_180: {
                for (int x = 0; x < model.width; x++) {
                    for (int y = 0; y < model.height; y++) {
                        for (int z = 0; z < model.length; z++) {
                            BlockPos position = new BlockPos(pos.getX() + model.width - x - 1, pos.getY() + y, pos.getZ() + model.length - z - 1);
                            if (boundsIn.isVecInside(position)) {
                                if (model.model[x][y][z] == null) {
                                    setBlockState(CAVE_AIR, world, boundsIn, treasureType, position.getX(), position.getY(), position.getZ(),
                                            this.theme, lootLevel, PlacementBehaviour.NON_SOLID);
                                } else {
                                    Tuple<BlockState, Boolean> result = DungeonModelBlock.getBlockState(model.model[x][y][z], Rotation.CLOCKWISE_180, world,
                                            position, theme, subTheme,
                                            WeightedRandomBlock.RANDOM, variation, lootLevel);
                                    if (result == null)
                                        continue;
                                    setBlockState(result.getA(), world, boundsIn, treasureType, position.getX(), position.getY(), position.getZ(),
                                            this.theme, lootLevel, fillAir ? PlacementBehaviour.SOLID : model.model[x][y][z].type.placementBehavior);

                                    if (result.getB()) {
                                        world.getChunk(position).markBlockForPostprocessing(position);
                                    }

                                    if (y == 0 && model.height > 1
                                            && world.isAirBlock(position.down()) && model.model[x][1][z] != null
                                            && model.model[x][0][z].type == DungeonModelBlockType.SOLID
                                            && model.model[x][1][z].type == DungeonModelBlockType.SOLID) {
                                        DungeonBuilder.buildPillar(world, theme, position.getX(), position.getY(), position.getZ(),
                                                boundsIn);
                                    }
                                }
                            }
                        }
                    }
                }
                break;
            }
            case NONE:
                build(model, world, boundsIn, pos, theme, subTheme, treasureType, lootLevel, fillAir);
                break;
            default:
                DungeonCrawl.LOGGER.warn("Failed to build a rotated dungeon segment: Unsupported rotation " + rotation);
                break;
        }

    }

}