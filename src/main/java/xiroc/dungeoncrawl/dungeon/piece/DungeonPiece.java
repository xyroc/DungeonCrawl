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

import com.google.common.collect.ImmutableSet;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnReason;
import net.minecraft.entity.monster.MonsterEntity;
import net.minecraft.fluid.IFluidState;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.Direction;
import net.minecraft.util.Rotation;
import net.minecraft.util.Tuple;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MutableBoundingBox;
import net.minecraft.util.math.Vec3i;
import net.minecraft.world.IWorld;
import net.minecraft.world.gen.feature.structure.IStructurePieceType;
import net.minecraft.world.gen.feature.structure.StructurePiece;
import xiroc.dungeoncrawl.DungeonCrawl;
import xiroc.dungeoncrawl.config.Config;
import xiroc.dungeoncrawl.dungeon.DungeonBuilder;
import xiroc.dungeoncrawl.dungeon.block.Spawner;
import xiroc.dungeoncrawl.dungeon.block.WeightedRandomBlock;
import xiroc.dungeoncrawl.dungeon.decoration.IDungeonDecoration;
import xiroc.dungeoncrawl.dungeon.model.*;
import xiroc.dungeoncrawl.dungeon.monster.RandomMonster;
import xiroc.dungeoncrawl.dungeon.treasure.Treasure;
import xiroc.dungeoncrawl.theme.Theme;
import xiroc.dungeoncrawl.theme.Theme.SubTheme;
import xiroc.dungeoncrawl.util.DirectionalBlockPos;
import xiroc.dungeoncrawl.util.IBlockPlacementHandler;
import xiroc.dungeoncrawl.util.Orientation;
import xiroc.dungeoncrawl.util.Position2D;

import java.util.List;
import java.util.Random;
import java.util.Set;

public abstract class DungeonPiece extends StructurePiece {

    // 0    corridor
    // 1    stairs
    // 2    (unused) corridor hole
    // 3    (unused) corridor trap
    // 4    corridor room
    // 5    <removed>
    // 6    entrance builder
    // 7    (unused) large corridor
    // 8    room
    // 9    side room
    // 10   node room
    // 11   node connector
    // 12   (unused) prisoner cell
    // 13   staircase
    // 14   secret room
    // 15   (unused) spider room
    // 16   multipart model piece

    public static final CompoundNBT DEFAULT_NBT;

    private static final Set<Block> BLOCKS_NEEDING_POSTPROCESSING = ImmutableSet.<Block>builder().add(Blocks.IRON_BARS).build();

    static {
        DEFAULT_NBT = new CompoundNBT();
        DEFAULT_NBT.putBoolean("north", false);
        DEFAULT_NBT.putBoolean("east", false);
        DEFAULT_NBT.putBoolean("south", false);
        DEFAULT_NBT.putBoolean("west", false);
        DEFAULT_NBT.putInt("connectedSides", 0);
        DEFAULT_NBT.putInt("posX", -1);
        DEFAULT_NBT.putInt("posZ", -1);
        DEFAULT_NBT.putInt("theme", 0);
        DEFAULT_NBT.putInt("stage", -1);
        DEFAULT_NBT.putInt("rotation", 0);
    }

    public Rotation rotation;
    public int connectedSides, gridX, gridZ, theme, subTheme, x, y, z, stage;
    public Integer modelID; // ID's are no longer the main way to identify models. Kept only for backwards compatibility.
    public String modelKey;
    public boolean[] sides; // N-E-S-W
    public DirectionalBlockPos[] featurePositions;
    public byte[] variation;

    public DungeonPiece(IStructurePieceType p_i51343_1_) {
        super(p_i51343_1_, DEFAULT_NBT);
        sides = new boolean[4];
        rotation = Rotation.NONE;
    }

    public DungeonPiece(IStructurePieceType p_i51343_1_, CompoundNBT p_i51343_2_) {
        super(p_i51343_1_, p_i51343_2_);
        sides = new boolean[4];
        sides[0] = p_i51343_2_.getBoolean("north");
        sides[1] = p_i51343_2_.getBoolean("east");
        sides[2] = p_i51343_2_.getBoolean("south");
        sides[3] = p_i51343_2_.getBoolean("west");
        connectedSides = p_i51343_2_.getInt("connectedSides");
        gridX = p_i51343_2_.getInt("posX");
        gridZ = p_i51343_2_.getInt("posZ");
        x = p_i51343_2_.getInt("x");
        y = p_i51343_2_.getInt("y");
        z = p_i51343_2_.getInt("z");
        theme = p_i51343_2_.getInt("theme");
        subTheme = p_i51343_2_.getInt("subTheme");
        stage = p_i51343_2_.getInt("stage");
        rotation = Orientation.getRotation(p_i51343_2_.getInt("rotation"));

        if (p_i51343_2_.contains("model")) {
            modelID = p_i51343_2_.getInt("model");
        }
        if (p_i51343_2_.contains("modelKey")) {
            modelKey = p_i51343_2_.getString("modelKey");
        }
        if (p_i51343_2_.contains("featurePositions", 9)) {
            featurePositions = readAllPositions(p_i51343_2_.getList("featurePositions", 10));
        }
        if (p_i51343_2_.contains("variation")) {
            variation = p_i51343_2_.getByteArray("variation");
        }
        setupBoundingBox();
    }

    public abstract int getType();

    /**
     * Called during the dungeon-post-processing to determine the model that will be
     * used to build this piece.
     */
    public abstract void setupModel(DungeonBuilder builder, DungeonModels.ModelCategory layerCategory, List<DungeonPiece> pieces, Random rand);

    public abstract void setupBoundingBox();

    public void openSide(Direction side) {
        switch (side) {
            case NORTH:
                if (sides[0])
                    return;
                sides[0] = true;
                connectedSides++;
                return;
            case EAST:
                if (sides[1])
                    return;
                sides[1] = true;
                connectedSides++;
                return;
            case SOUTH:
                if (sides[2])
                    return;
                sides[2] = true;
                connectedSides++;
                return;
            case WEST:
                if (sides[3])
                    return;
                sides[3] = true;
                connectedSides++;
                return;
            case UP:
                if (sides[4])
                    return;
                sides[4] = true;
                connectedSides++;
                return;
            case DOWN:
                if (sides[5])
                    return;
                sides[5] = true;
                connectedSides++;
                return;
            default:
                DungeonCrawl.LOGGER.warn("Failed to open a segment side: Unknown side " + side);
        }
    }

    public void setRotation(Rotation rotation) {
        this.rotation = rotation;
    }

    public Rotation getRotation() {
        return this.rotation;
    }

    public void setGridPosition(int x, int z) {
        this.gridX = x;
        this.gridZ = z;
    }

    public void setGridPosition(Position2D position) {
        this.gridX = position.x;
        this.gridZ = position.z;
    }

    public void setWorldPosition(int x, int y, int z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    @Override
    public void readAdditional(CompoundNBT tagCompound) {
        tagCompound.putBoolean("north", sides[0]);
        tagCompound.putBoolean("east", sides[1]);
        tagCompound.putBoolean("south", sides[2]);
        tagCompound.putBoolean("west", sides[3]);
        tagCompound.putInt("connectedSides", connectedSides);
        tagCompound.putInt("posX", gridX);
        tagCompound.putInt("posZ", gridZ);
        tagCompound.putInt("x", x);
        tagCompound.putInt("y", y);
        tagCompound.putInt("z", z);
        tagCompound.putInt("stage", stage);
        tagCompound.putInt("theme", theme);
        tagCompound.putInt("subTheme", subTheme);
        tagCompound.putInt("rotation", Orientation.rotationAsInt(this.rotation));
        if (modelID != null) {
            tagCompound.putInt("model", modelID);
        }
        if (modelKey != null) {
            tagCompound.putString("modelKey", modelKey);
        }
        if (featurePositions != null) {
            ListNBT list = new ListNBT();
            writeAllPositions(featurePositions, list);
            tagCompound.put("featurePositions", list);
        }
        if (variation != null) {
            tagCompound.putByteArray("variation", variation);
        }
    }

    public void customSetup(Random rand) {
        DungeonModel model = DungeonModels.getModel(modelKey, modelID);
        if (model == null) {
            return;
        }
        if (model.metadata != null && model.metadata.featureMetadata != null && model.featurePositions != null && model.featurePositions.length > 0) {
            Vec3i offset = model.getOffset(rotation);
            DungeonModelFeature.setup(this, model, model.featurePositions, rotation, rand, model.metadata.featureMetadata,
                    x + offset.getX(), y + offset.getY(), z + offset.getZ());
        }
    }

    public void setBlockState(BlockState state, IWorld world, Treasure.Type treasureType,
                              BlockPos pos, int theme, int lootLevel, DungeonModelBlockType type) {
        if (state == null)
            return;

        if (isBlockProtected(world, state, pos)
                || world.isAirBlock(pos) && !type.isSolid(world, pos, WeightedRandomBlock.RANDOM, 0, 0, 0)) {
            return;
        }

        IBlockPlacementHandler.getHandler(state.getBlock()).placeBlock(world, state, pos, world.getRandom(),
                treasureType, theme, lootLevel);

        IFluidState ifluidstate = world.getFluidState(pos);
        if (ifluidstate.isSource()) {
            world.getPendingFluidTicks().scheduleTick(pos, ifluidstate.getFluid(), 0);
        }

        if (BLOCKS_NEEDING_POSTPROCESSING.contains(state.getBlock())) {
            world.getChunk(pos).markBlockForPostprocessing(pos);
        }
    }

    public void setBlockState(BlockState state, IWorld world, Treasure.Type treasureType,
                              BlockPos pos, int theme, int lootLevel, PlacementBehaviour placementBehaviour) {
        if (state == null)
            return;

        if (isBlockProtected(world, state, pos)
                || world.isAirBlock(pos) && !placementBehaviour.function.isSolid(world, pos, WeightedRandomBlock.RANDOM, 0, 0, 0)) {
            return;
        }

        IBlockPlacementHandler.getHandler(state.getBlock()).placeBlock(world, state, pos, world.getRandom(),
                treasureType, theme, lootLevel);

        IFluidState ifluidstate = world.getFluidState(pos);
        if (ifluidstate.isSource()) {
            world.getPendingFluidTicks().scheduleTick(pos, ifluidstate.getFluid(), 0);
        }

        if (BLOCKS_NEEDING_POSTPROCESSING.contains(state.getBlock())) {
            world.getChunk(pos).markBlockForPostprocessing(pos);
        }
    }

    public void setBlockState(BlockState state, IWorld world, Treasure.Type treasureType,
                              int x, int y, int z, int theme, int lootLevel, DungeonModelBlockType type) {
        setBlockState(state, world, treasureType, new BlockPos(x, y, z), theme, lootLevel, type);
    }

    public void setBlockState(BlockState state, IWorld world, Treasure.Type treasureType,
                              int x, int y, int z, int theme, int lootLevel, PlacementBehaviour placementBehaviour) {
        setBlockState(state, world, treasureType, new BlockPos(x, y, z), theme, lootLevel, placementBehaviour);
    }

    public void setBlockState(BlockState state, IWorld world, Treasure.Type treasureType,
                              int x, int y, int z, int theme, int lootLevel, boolean fillAir) {
        BlockPos pos = new BlockPos(x, y, z);

        if (state == null)
            return;

        if (isBlockProtected(world, state, pos) || world.isAirBlock(pos) && !fillAir) {
            return;
        }

        IBlockPlacementHandler.getHandler(state.getBlock()).placeBlock(world, state, pos, world.getRandom(),
                treasureType, theme, lootLevel);

        IFluidState ifluidstate = world.getFluidState(pos);
        if (ifluidstate.isSource()) {
            world.getPendingFluidTicks().scheduleTick(pos, ifluidstate.getFluid(), 0);
        }

        if (BLOCKS_NEEDING_POSTPROCESSING.contains(state.getBlock())) {
            world.getChunk(pos).markBlockForPostprocessing(pos);
        }
    }

    @Override
    public void setBlockState(IWorld worldIn, BlockState blockstateIn, int x, int y, int z,
                              MutableBoundingBox boundingboxIn) {
        BlockPos blockPos = new BlockPos(x, y, z);

        if (isBlockProtected(worldIn, blockstateIn, blockPos))
            return;

        setBlockState(worldIn, blockstateIn, boundingboxIn, blockPos);
    }

    private static void setBlockState(IWorld worldIn, BlockState blockstateIn, MutableBoundingBox boundingboxIn, BlockPos blockPos) {
        if (boundingboxIn.isVecInside(blockPos)) {

            worldIn.setBlockState(blockPos, blockstateIn, 2);

            IFluidState ifluidstate = worldIn.getFluidState(blockPos);
            if (ifluidstate.isSource()) {
                worldIn.getPendingFluidTicks().scheduleTick(blockPos, ifluidstate.getFluid(), 0);
            }

            if (BLOCKS_NEEDING_POSTPROCESSING.contains(blockstateIn.getBlock())) {
                worldIn.getChunk(blockPos).markBlockForPostprocessing(blockPos);
            }

        }
    }

    public static void setBlockState(IWorld worldIn, BlockState state, int x, int y, int z,
                                     MutableBoundingBox worldGenBounds, boolean fillAir) {
        BlockPos blockPos = new BlockPos(x, y, z);

        if (!fillAir && worldIn.isAirBlock(blockPos))
            return;

        setBlockState(worldIn, state, worldGenBounds, blockPos);
    }

    public void replaceBlockState(IWorld worldIn, BlockState blockstateIn, int x, int y, int z,
                                  MutableBoundingBox boundingboxIn) {
        BlockPos blockPos = new BlockPos(x, y, z);

        if (isBlockProtected(worldIn, blockstateIn, blockPos) || worldIn.getBlockState(blockPos).isAir(worldIn, blockPos))
            return;

        setBlockState(worldIn, blockstateIn, boundingboxIn, blockPos);
    }

    public boolean hasChildPieces() {
        DungeonModel model = DungeonModels.getModel(modelKey, modelID);
        return model != null && model.multipartData != null;
    }

    public void addChildPieces(List<DungeonPiece> pieces, DungeonBuilder builder, DungeonModels.ModelCategory layerCategory, int layer, Random rand) {
        DungeonModel model = DungeonModels.getModel(modelKey, modelID);
        if (model != null && model.multipartData != null) {
            BlockPos pos = new BlockPos(x, y, z).add(model.getOffset(rotation));
            for (MultipartModelData data : model.multipartData) {
                if (data.checkConditions(this)) {
                    pieces.add(data.models.roll(rand).createMultipartPiece(this, model, this.rotation, pos.getX(), pos.getY(), pos.getZ()));
                } else if (data.alternatives != null) {
                    pieces.add(data.alternatives.roll(rand).createMultipartPiece(this, model, this.rotation, pos.getX(), pos.getY(), pos.getZ()));
                }
            }
        }
    }

    public void build(DungeonModel model, IWorld world, MutableBoundingBox boundsIn, BlockPos pos, Theme theme,
                      SubTheme subTheme, Treasure.Type treasureType, int lootLevel, boolean fillAir) {

        buildFull(model, world, boundsIn, pos, theme, subTheme, treasureType, lootLevel, fillAir);

//        int xStart = Math.max(boundsIn.minX, pos.getX()) - pos.getX(),
//                width = Math.min(model.width, boundsIn.maxX - pos.getX() + 1);
//        int zStart = Math.max(boundsIn.minZ, pos.getZ()) - pos.getZ(),
//                length = Math.min(model.length, boundsIn.maxZ - pos.getZ() + 1);
//
//        for (int x = xStart; x < width; x++) {
//            for (int y = 0; y < model.height; y++) {
//                for (int z = zStart; z < length; z++) {
//                    if (model.model[x][y][z] == null) {
//                        setBlockState(CAVE_AIR, world, boundsIn, treasureType, pos.getX() + x, pos.getY() + y, pos.getZ() + z,
//                                this.theme, lootLevel, PlacementBehaviour.NON_SOLID);
//                    } else {
//                        BlockPos position = new BlockPos(pos.getX() + x, pos.getY() + y, pos.getZ() + z);
//                        Tuple<BlockState, Boolean> result = DungeonModelBlock.getBlockState(model.model[x][y][z], Rotation.NONE, world,
//                                position, theme, subTheme,
//                                WeightedRandomBlock.RANDOM, variation, lootLevel);
//                        if (result == null)
//                            continue;
//                        setBlockState(result.getA(), world, boundsIn, treasureType, position.getX(), position.getY(), position.getZ(),
//                                this.theme, lootLevel, fillAir ? PlacementBehaviour.SOLID : model.model[x][y][z].type.placementBehavior);
//
//                        if (result.getB()) {
//                            world.getChunk(position).markBlockForPostprocessing(position);
//                        }
//
//                        if (y == 0 && model.height > 1
//                                && world.isAirBlock(position.down()) && model.model[x][1][z] != null
//                                && model.model[x][0][z].type == DungeonModelBlockType.SOLID
//                                && model.model[x][1][z].type == DungeonModelBlockType.SOLID) {
//                            DungeonBuilder.buildPillar(world, theme, pos.getX() + x, pos.getY(), pos.getZ() + z, boundsIn);
//                        }
//                    }
//
//                }
//            }
//        }
//
//        if (theme == Theme.MOSS) {
//            for (int x = xStart + 1; x < width - 1; x++) {
//                for (int y = 0; y < model.height; y++) {
//                    for (int z = zStart + 1; z < length - 1; z++) {
//                        if (model.model[x][y][z] == null) {
//                            BlockPos north = new BlockPos(pos.getX() + x, pos.getY() + y, pos.getZ() + z - 1);
//                            BlockPos east = new BlockPos(north.getX() + 1, north.getY(), north.getZ() + 1);
//                            BlockPos south = new BlockPos(north.getX(), north.getY(), east.getZ() + 1);
//                            BlockPos west = new BlockPos(north.getX() - 1, north.getY(), east.getZ());
//                            BlockPos up = new BlockPos(north.getX(), north.getY() + 1, east.getZ());
//
//                            boolean _north = boundsIn.isVecInside(north) && north.getZ() >= zStart && world.getBlockState(north).isNormalCube(world, north) && !world.isAirBlock(north);
//                            boolean _east = boundsIn.isVecInside(east) && east.getX() < width && world.getBlockState(east).isNormalCube(world, east) && !world.isAirBlock(east);
//                            boolean _south = boundsIn.isVecInside(south) && south.getZ() < length && world.getBlockState(south).isNormalCube(world, south) && !world.isAirBlock(south);
//                            boolean _west = boundsIn.isVecInside(west) && west.getX() >= xStart && world.getBlockState(west).isNormalCube(world, east) && !world.isAirBlock(west);
//                            boolean _up = boundsIn.isVecInside(up) && world.getBlockState(up).isNormalCube(world, up) && !world.isAirBlock(up);
//
//                            if ((_north || _east || _south || _west || _up) && WeightedRandomBlock.RANDOM.nextFloat() < 0.35) {
//                                BlockPos p = new BlockPos(north.getX(), north.getY(), east.getZ());
//                                world.setBlockState(p, Blocks.VINE.getDefaultState().with(BlockStateProperties.NORTH, _north)
//                                        .with(BlockStateProperties.EAST, _east).with(BlockStateProperties.SOUTH, _south)
//                                        .with(BlockStateProperties.WEST, _west).with(BlockStateProperties.UP, _up), 2);
//                                world.getChunk(p).markBlockForPostprocessing(p);
//                            }
//                        }
//                    }
//                }
//            }
//        }
    }

    public void buildFull(DungeonModel model, IWorld world, MutableBoundingBox boundsIn, BlockPos pos, Theme theme,
                          SubTheme subTheme, Treasure.Type treasureType, int lootLevel, boolean fillAir) {
        if (Config.EXTENDED_DEBUG.get()) {
            DungeonCrawl.LOGGER.debug("Building {} with model id {} at ({} | {} | {})", model.location, model.id, pos.getX(), pos.getY(), pos.getZ());
        }

        for (int x = 0; x < model.width; x++) {
            for (int y = model.height - 1; y >= 0; y--) {
                for (int z = 0; z < model.length; z++) {
                    BlockPos position = new BlockPos(pos.getX() + x, pos.getY() + y, pos.getZ() + z);
                    if (boundsIn.isVecInside(position)) {
                        if (model.model[x][y][z] == null) {
                            setBlockState(CAVE_AIR, world, treasureType, position,
                                    this.theme, lootLevel, PlacementBehaviour.NON_SOLID);
                        } else {
                            Tuple<BlockState, Boolean> result = DungeonModelBlock.getBlockState(model.model[x][y][z],
                                    Rotation.NONE, world, position, theme, subTheme, WeightedRandomBlock.RANDOM, variation, lootLevel);
                            if (result == null)
                                continue;
                            setBlockState(result.getA(), world, treasureType, position, this.theme, lootLevel,
                                    fillAir ? DungeonModelBlockType.SOLID : model.model[x][y][z].type);

                            if (result.getB()) {
                                world.getChunk(position).markBlockForPostprocessing(position);
                            }

                            if (y == 0 && model.height > 1
                                    && world.isAirBlock(position.down()) && model.model[x][1][z] != null
                                    && model.model[x][0][z].type == DungeonModelBlockType.SOLID
                                    && model.model[x][1][z].type == DungeonModelBlockType.SOLID) {
                                buildPillar(world, theme, pos.getX() + x, pos.getY(), pos.getZ() + z, boundsIn);
                            }
                        }
                    }
                }
            }
        }
        if (Config.EXTENDED_DEBUG.get()) {
            DungeonCrawl.LOGGER.debug("Finished building {} with model id {} at ({} | {} | {})", model.location, model.id, pos.getX(), pos.getY(), pos.getZ());
        }
    }

    public void buildRotated(DungeonModel model, IWorld world, MutableBoundingBox boundsIn, BlockPos pos, Theme theme,
                             SubTheme subTheme, Treasure.Type treasureType, int lootLevel, Rotation rotation, boolean fillAir) {
        buildRotatedFull(model, world, boundsIn, pos, theme, subTheme, treasureType, lootLevel, rotation, fillAir);

//        int xStart = Math.max(boundsIn.minX, pos.getX()) - pos.getX(),
//                width = Math.min(model.width, boundsIn.maxX - pos.getX() + 1);
//        int zStart = Math.max(boundsIn.minZ, pos.getZ()) - pos.getZ(),
//                length = Math.min(model.length, boundsIn.maxZ - pos.getZ() + 1);
//        switch (rotation) {
//            case CLOCKWISE_90: {
//                for (int x = xStart; x < width; x++) {
//                    for (int y = 0; y < model.height; y++) {
//                        for (int z = zStart; z < length; z++) {
//                            if (model.model[x][y][z] == null) {
//                                setBlockState(CAVE_AIR, world, boundsIn, treasureType, new BlockPos(pos.getX() + model.length - z - 1, pos.getY() + y, pos.getZ() + x),
//                                        this.theme, lootLevel, PlacementBehaviour.NON_SOLID);
//                            } else {
//                                BlockPos position = new BlockPos(pos.getX() + model.length - z - 1, pos.getY() + y, pos.getZ() + x);
//                                Tuple<BlockState, Boolean> result = DungeonModelBlock.getBlockState(model.model[x][y][z],
//                                        Rotation.CLOCKWISE_90, world, position, theme, subTheme, WeightedRandomBlock.RANDOM, variation, lootLevel);
//                                if (result == null)
//                                    continue;
//                                setBlockState(result.getA(), world, boundsIn, treasureType, position, this.theme, lootLevel,
//                                        fillAir ? PlacementBehaviour.SOLID : model.model[x][y][z].type.placementBehavior);
//
//                                if (result.getB()) {
//                                    world.getChunk(position).markBlockForPostprocessing(position);
//                                }
//
//                                if (y == 0 && model.height > 1
//                                        && world.isAirBlock(position.down()) && model.model[x][1][z] != null
//                                        && model.model[x][0][z].type == DungeonModelBlockType.SOLID
//                                        && model.model[x][1][z].type == DungeonModelBlockType.SOLID) {
//                                    DungeonBuilder.buildPillar(world, theme, position.getX(), position.getY(), position.getZ(),
//                                            boundsIn);
//                                }
//                            }
//                        }
//                    }
//                }
//
//                if (theme == Theme.MOSS) {
//                    for (int x = xStart + 1; x < width - 1; x++) {
//                        for (int y = 0; y < model.height; y++) {
//                            for (int z = zStart + 1; z < length - 1; z++) {
//                                if (model.model[x][y][z] == null && boundsIn.isVecInside(new BlockPos(pos.getX() + model.length - z - 1, pos.getY() + y, pos.getZ() + x))) {
//                                    BlockPos north = new BlockPos(pos.getX() + model.length - z - 1, pos.getY() + y, pos.getZ() + x - 1);
//                                    BlockPos east = new BlockPos(north.getX() + 1, north.getY(), north.getZ() + 1);
//                                    BlockPos south = new BlockPos(north.getX(), north.getY(), east.getZ() + 1);
//                                    BlockPos west = new BlockPos(north.getX() - 1, north.getY(), east.getZ());
//                                    BlockPos up = new BlockPos(north.getX(), north.getY() + 1, east.getZ());
//
//                                    boolean _north = boundsIn.isVecInside(north) && north.getZ() >= 1 && world.getBlockState(north).isNormalCube(world, north) && !world.isAirBlock(north);
//                                    boolean _east = boundsIn.isVecInside(east) && east.getX() < model.width - 1 && world.getBlockState(east).isNormalCube(world, east) && !world.isAirBlock(east);
//                                    boolean _south = boundsIn.isVecInside(south) && south.getZ() < model.length - 1 && world.getBlockState(south).isNormalCube(world, south) && !world.isAirBlock(south);
//                                    boolean _west = boundsIn.isVecInside(west) && west.getX() >= 1 && world.getBlockState(west).isNormalCube(world, east) && !world.isAirBlock(west);
//                                    boolean _up = boundsIn.isVecInside(up) && world.getBlockState(up).isNormalCube(world, up) && !world.isAirBlock(up);
//
//                                    if ((_north || _east || _south || _west || _up) && WeightedRandomBlock.RANDOM.nextFloat() < 0.25) {
//                                        BlockPos p = new BlockPos(north.getX(), north.getY(), east.getZ());
//                                        world.setBlockState(p,
//                                                Blocks.VINE.getDefaultState().with(BlockStateProperties.NORTH, _north)
//                                                        .with(BlockStateProperties.EAST, _east).with(BlockStateProperties.SOUTH, _south)
//                                                        .with(BlockStateProperties.WEST, _west).with(BlockStateProperties.UP, _up), 2);
//                                        world.getChunk(p).markBlockForPostprocessing(p);
//                                    }
//
//                                }
//                            }
//                        }
//                    }
//                }
//                break;
//            }
//            case COUNTERCLOCKWISE_90: {
//                for (int x = 0; x < model.width; x++) {
//                    for (int y = 0; y < model.height; y++) {
//                        for (int z = 0; z < model.length; z++) {
//                            if (model.model[x][y][z] == null) {
//                                setBlockState(CAVE_AIR, world, boundsIn, treasureType, pos.getX() + z, pos.getY() + y, pos.getZ() + model.width - x - 1,
//                                        this.theme, lootLevel, PlacementBehaviour.NON_SOLID);
//                            } else {
//                                BlockPos position = new BlockPos(pos.getX() + z, pos.getY() + y, pos.getZ() + model.width - x - 1);
//                                Tuple<BlockState, Boolean> result = DungeonModelBlock.getBlockState(model.model[x][y][z],
//                                        Rotation.COUNTERCLOCKWISE_90, world, position, theme, subTheme,
//                                        WeightedRandomBlock.RANDOM, variation, lootLevel);
//                                if (result == null)
//                                    continue;
//                                setBlockState(result.getA(), world, boundsIn, treasureType, position.getX(), position.getY(), position.getZ(),
//                                        this.theme, lootLevel, fillAir ? PlacementBehaviour.SOLID : model.model[x][y][z].type.placementBehavior);
//
//                                if (result.getB()) {
//                                    world.getChunk(position).markBlockForPostprocessing(position);
//                                }
//
//                                if (y == 0 && model.height > 1
//                                        && world.isAirBlock(position.down()) && model.model[x][1][z] != null
//                                        && model.model[x][0][z].type == DungeonModelBlockType.SOLID
//                                        && model.model[x][1][z].type == DungeonModelBlockType.SOLID) {
//                                    DungeonBuilder.buildPillar(world, theme, position.getX(), position.getY(), position.getZ(),
//                                            boundsIn);
//                                }
//                            }
//                        }
//                    }
//                }
//
//                if (theme == Theme.MOSS) {
//                    for (int x = xStart + 1; x < width - 1; x++) {
//                        for (int y = 0; y < model.height; y++) {
//                            for (int z = zStart + 1; z < length - 1; z++) {
//                                if (model.model[x][y][z] == null) {
//                                    BlockPos north = new BlockPos(pos.getX() + z, pos.getY() + y, pos.getZ() + model.width - x - 2);
//                                    BlockPos east = new BlockPos(north.getX() + 1, north.getY(), north.getZ() + 1);
//                                    BlockPos south = new BlockPos(north.getX(), north.getY(), east.getZ() + 1);
//                                    BlockPos west = new BlockPos(north.getX() - 1, north.getY(), east.getZ());
//                                    BlockPos up = new BlockPos(north.getX(), north.getY() + 1, east.getZ());
//
//                                    boolean _north = boundsIn.isVecInside(north) && north.getZ() >= zStart && world.getBlockState(north).isNormalCube(world, north) && !world.isAirBlock(north);
//                                    boolean _east = boundsIn.isVecInside(east) && east.getX() < width && world.getBlockState(east).isNormalCube(world, east) && !world.isAirBlock(east);
//                                    boolean _south = boundsIn.isVecInside(south) && south.getZ() < length && world.getBlockState(south).isNormalCube(world, south) && !world.isAirBlock(south);
//                                    boolean _west = boundsIn.isVecInside(west) && west.getX() >= xStart && world.getBlockState(west).isNormalCube(world, east) && !world.isAirBlock(west);
//                                    boolean _up = boundsIn.isVecInside(up) && world.getBlockState(up).isNormalCube(world, up) && !world.isAirBlock(up);
//
//                                    if ((_north || _east || _south || _west || _up) && WeightedRandomBlock.RANDOM.nextFloat() < 0.25) {
//                                        BlockPos p = new BlockPos(north.getX(), north.getY(), east.getZ());
//                                        world.setBlockState(p, Blocks.VINE.getDefaultState().with(BlockStateProperties.NORTH, _north)
//                                                .with(BlockStateProperties.EAST, _east).with(BlockStateProperties.SOUTH, _south)
//                                                .with(BlockStateProperties.WEST, _west).with(BlockStateProperties.UP, _up), 2);
//                                        world.getChunk(p).markBlockForPostprocessing(p);
//                                    }
//                                }
//                            }
//                        }
//                    }
//                }
//                //buildBoundingBox(world, new MutableBoundingBox(pos.getX() + xStart, pos.getY(), pos.getZ() + zStart,
//                //        pos.getX() + xStart + width - 1, pos.getY() + 8, pos.getZ() + zStart + length - 1), Blocks.ACACIA_FENCE);
//                break;
//            }
//            case CLOCKWISE_180: {
//                for (int x = xStart; x < width; x++) {
//                    for (int y = 0; y < model.height; y++) {
//                        for (int z = zStart; z < length; z++) {
//                            if (model.model[x][y][z] == null) {
//                                setBlockState(CAVE_AIR, world, boundsIn, treasureType, pos.getX() + model.width - x - 1, pos.getY() + y, pos.getZ() + model.length - z - 1,
//                                        this.theme, lootLevel, PlacementBehaviour.NON_SOLID);
//                            } else {
//                                BlockPos position = new BlockPos(pos.getX() + model.width - x - 1, pos.getY() + y, pos.getZ() + model.length - z - 1);
//                                Tuple<BlockState, Boolean> result = DungeonModelBlock.getBlockState(model.model[x][y][z], Rotation.CLOCKWISE_180, world,
//                                        position, theme, subTheme,
//                                        WeightedRandomBlock.RANDOM, variation, lootLevel);
//                                if (result == null)
//                                    continue;
//                                setBlockState(result.getA(), world, boundsIn, treasureType, position.getX(), position.getY(), position.getZ(),
//                                        this.theme, lootLevel, fillAir ? PlacementBehaviour.SOLID : model.model[x][y][z].type.placementBehavior);
//
//                                if (result.getB()) {
//                                    world.getChunk(position).markBlockForPostprocessing(position);
//                                }
//
//                                if (y == 0 && model.height > 1
//                                        && world.isAirBlock(position.down()) && model.model[x][1][z] != null
//                                        && model.model[x][0][z].type == DungeonModelBlockType.SOLID
//                                        && model.model[x][1][z].type == DungeonModelBlockType.SOLID) {
//                                    DungeonBuilder.buildPillar(world, theme, position.getX(), position.getY(), position.getZ(),
//                                            boundsIn);
//                                }
//                            }
//                        }
//                    }
//                }
//
//                if (theme == Theme.MOSS) {
//                    for (int x = xStart + 1; x < width - 1; x++) {
//                        for (int y = 0; y < model.height; y++) {
//                            for (int z = zStart + 1; z < length - 1; z++) {
//                                if (model.model[x][y][z] == null) {
//                                    BlockPos north = new BlockPos(pos.getX() + model.width - x - 1, pos.getY() + y, pos.getZ() + model.length - z - 2);
//                                    BlockPos east = new BlockPos(north.getX() + 1, north.getY(), north.getZ() + 1);
//                                    BlockPos south = new BlockPos(north.getX(), north.getY(), east.getZ() + 1);
//                                    BlockPos west = new BlockPos(north.getX() - 1, north.getY(), east.getZ());
//                                    BlockPos up = new BlockPos(north.getX(), pos.getY() + y + 1, east.getZ());
//
//                                    boolean _north = boundsIn.isVecInside(north) && north.getZ() >= zStart && world.getBlockState(north).isNormalCube(world, north) && !world.isAirBlock(north);
//                                    boolean _east = boundsIn.isVecInside(east) && east.getX() < width && world.getBlockState(east).isNormalCube(world, east) && !world.isAirBlock(east);
//                                    boolean _south = boundsIn.isVecInside(south) && south.getZ() < length && world.getBlockState(south).isNormalCube(world, south) && !world.isAirBlock(south);
//                                    boolean _west = boundsIn.isVecInside(west) && west.getX() >= xStart && world.getBlockState(west).isNormalCube(world, east) && !world.isAirBlock(west);
//                                    boolean _up = boundsIn.isVecInside(up) && world.getBlockState(up).isNormalCube(world, up) && !world.isAirBlock(up);
//
//                                    if ((_north || _east || _south || _west || _up) && WeightedRandomBlock.RANDOM.nextFloat() < 0.25) {
//                                        BlockPos p = new BlockPos(north.getX(), north.getY(), east.getZ());
//                                        world.setBlockState(p, Blocks.VINE.getDefaultState().with(BlockStateProperties.NORTH, _north)
//                                                .with(BlockStateProperties.EAST, _east).with(BlockStateProperties.SOUTH, _south)
//                                                .with(BlockStateProperties.WEST, _west).with(BlockStateProperties.UP, _up), 2);
//                                        world.getChunk(p).markBlockForPostprocessing(p);
//                                    }
//
//                                }
//                            }
//                        }
//                    }
//                }
//                //buildBoundingBox(world, new MutableBoundingBox(pos.getX() + xStart, pos.getY(), pos.getZ() + zStart,
//                //        pos.getX() + xStart + width - 1, pos.getY() + 8, pos.getZ() + zStart + length - 1), Blocks.ACACIA_FENCE);
//                break;
//            }
//            case NONE:
//                build(model, world, boundsIn, pos, theme, subTheme, treasureType, lootLevel, fillAir);
//                break;
//            default:
//                DungeonCrawl.LOGGER.warn("Failed to build a rotated dungeon segment: Unsupported rotation " + rotation);
//                break;
//        }

    }

    public void buildRotatedFull(DungeonModel model, IWorld world, MutableBoundingBox boundsIn, BlockPos pos, Theme theme,
                                 SubTheme subTheme, Treasure.Type treasureType, int lootLevel, Rotation rotation, boolean fillAir) {
        if (Config.EXTENDED_DEBUG.get()) {
            DungeonCrawl.LOGGER.debug("Building {} with model id {} and rotation {} at ({} | {} | {})", model.location, model.id, rotation, pos.getX(), pos.getY(), pos.getZ());
        }

        switch (rotation) {
            case CLOCKWISE_90: {
                for (int x = 0; x < model.width; x++) {
                    for (int y = model.height - 1; y >= 0; y--) {
                        for (int z = 0; z < model.length; z++) {
                            BlockPos position = new BlockPos(pos.getX() + model.length - z - 1, pos.getY() + y, pos.getZ() + x);
                            if (boundsIn.isVecInside(position)) {
                                if (model.model[x][y][z] == null) {
                                    setBlockState(CAVE_AIR, world, treasureType, position,
                                            this.theme, lootLevel, PlacementBehaviour.NON_SOLID);
                                } else {
                                    Tuple<BlockState, Boolean> result = DungeonModelBlock.getBlockState(model.model[x][y][z],
                                            Rotation.CLOCKWISE_90, world, position, theme, subTheme, WeightedRandomBlock.RANDOM, variation, lootLevel);

                                    if (result == null)
                                        continue;

                                    placeBlock(model, world, boundsIn, theme, treasureType, lootLevel, fillAir, x, y, z, position, result);
                                }
                            }
                        }
                    }
                }
                break;
            }
            case COUNTERCLOCKWISE_90: {
                for (int x = 0; x < model.width; x++) {
                    for (int y = model.height - 1; y >= 0; y--) {
                        for (int z = 0; z < model.length; z++) {
                            BlockPos position = new BlockPos(pos.getX() + z, pos.getY() + y, pos.getZ() + model.width - x - 1);
                            if (boundsIn.isVecInside(position)) {
                                if (model.model[x][y][z] == null) {
                                    setBlockState(CAVE_AIR, world, treasureType, position, this.theme, lootLevel, PlacementBehaviour.NON_SOLID);
                                } else {
                                    Tuple<BlockState, Boolean> result = DungeonModelBlock.getBlockState(model.model[x][y][z],
                                            Rotation.COUNTERCLOCKWISE_90, world, position, theme, subTheme,
                                            WeightedRandomBlock.RANDOM, variation, lootLevel);

                                    if (result == null)
                                        continue;

                                    placeBlock(model, world, boundsIn, theme, treasureType, lootLevel, fillAir, x, y, z, position, result);
                                }
                            }
                        }
                    }
                }
                break;
            }
            case CLOCKWISE_180: {
                for (int x = 0; x < model.width; x++) {
                    for (int y = model.height - 1; y >= 0; y--) {
                        for (int z = 0; z < model.length; z++) {
                            BlockPos position = new BlockPos(pos.getX() + model.width - x - 1, pos.getY() + y, pos.getZ() + model.length - z - 1);
                            if (boundsIn.isVecInside(position)) {
                                if (model.model[x][y][z] == null) {
                                    setBlockState(CAVE_AIR, world, treasureType, position, this.theme, lootLevel, PlacementBehaviour.NON_SOLID);
                                } else {
                                    Tuple<BlockState, Boolean> result = DungeonModelBlock.getBlockState(model.model[x][y][z], Rotation.CLOCKWISE_180, world,
                                            position, theme, subTheme, WeightedRandomBlock.RANDOM, variation, lootLevel);

                                    if (result == null)
                                        continue;

                                    placeBlock(model, world, boundsIn, theme, treasureType, lootLevel, fillAir, x, y, z, position, result);
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

        if (Config.EXTENDED_DEBUG.get()) {
            DungeonCrawl.LOGGER.debug("Finished building {} with model id {} and rotation {} at ({} | {} | {})", model.location, model.id, rotation, pos.getX(), pos.getY(), pos.getZ());
        }
    }

    public void placeBlock(DungeonModel model, IWorld world, MutableBoundingBox boundsIn, Theme theme, Treasure.Type treasureType, int lootLevel, boolean fillAir, int x, int y, int z, BlockPos position, Tuple<BlockState, Boolean> result) {
        setBlockState(result.getA(), world, treasureType, position, this.theme, lootLevel,
                fillAir ? DungeonModelBlockType.SOLID : model.model[x][y][z].type);

        if (result.getB()) {
            world.getChunk(position).markBlockForPostprocessing(position);
        }

        if (y == 0 && model.height > 1
                && world.isAirBlock(position.down()) && model.model[x][1][z] != null
                && model.model[x][0][z].type == DungeonModelBlockType.SOLID
                && model.model[x][1][z].type == DungeonModelBlockType.SOLID) {
            buildPillar(world, theme, position.getX(), position.getY(), position.getZ(), boundsIn);
        }
    }

    /**
     * Builds a 1x1 pillar to the ground
     */
    public void buildPillar(IWorld world, Theme theme, int x, int y, int z, MutableBoundingBox bounds) {
        int height = DungeonPiece.getGroundHeightFrom(world, x, z, y - 1);
        for (int y0 = y - 1; y0 > height; y0--) {
            DungeonPiece.setBlockState(world, theme.solid.get(), x, y0, z, bounds, true);
        }
    }

    public void entrances(IWorld world, MutableBoundingBox bounds, DungeonModel model) {
        int pathStartX = (model.width - 3) / 2, pathStartZ = (model.length - 3) / 2;
        BlockPos pos = new BlockPos(x, y, z).add(model.getOffset(rotation));

        if (sides[0]) {
            for (int x0 = pathStartX; x0 < pathStartX + 3; x0++)
                for (int y0 = 1; y0 < 4; y0++)
                    setBlockState(world, CAVE_AIR, pos.getX() + x0, pos.getY() + y0, pos.getZ(), bounds);
        }
        if (sides[1]) {
            for (int z0 = pathStartZ; z0 < pathStartZ + 3; z0++)
                for (int y0 = 1; y0 < 4; y0++)
                    setBlockState(world, CAVE_AIR, pos.getX() + model.width - 1, pos.getY() + y0, pos.getZ() + z0, bounds);
        }
        if (sides[2]) {
            for (int x0 = pathStartX; x0 < pathStartX + 3; x0++)
                for (int y0 = 1; y0 < 4; y0++)
                    setBlockState(world, CAVE_AIR, pos.getX() + x0, pos.getY() + y0, pos.getZ() + model.length - 1, bounds);
        }
        if (sides[3]) {
            for (int z0 = pathStartZ; z0 < pathStartZ + 3; z0++)
                for (int y0 = 1; y0 < 4; y0++)
                    setBlockState(world, CAVE_AIR, pos.getX(), pos.getY() + y0, pos.getZ() + z0, bounds);
        }

    }

    public void decorate(IWorld world, BlockPos pos, int width, int height, int length, Theme theme, MutableBoundingBox worldGenBounds, MutableBoundingBox structureBounds, DungeonModel model) {
        if (theme.decorations != null) {
            for (IDungeonDecoration decoration : theme.decorations) {
                if (Config.EXTENDED_DEBUG.get()) {
                    DungeonCrawl.LOGGER.debug("Running decoration {} of {} ({}) at ({} | {} | {})", decoration.toString(), model.location, model.id, pos.getX(), pos.getY(), pos.getZ());
                }

                decoration.decorate(model, world, pos, width, height, length, worldGenBounds, structureBounds, this, stage);

                if (Config.EXTENDED_DEBUG.get()) {
                    DungeonCrawl.LOGGER.debug("Finished decoration {} of {} ({}) at ({} | {} | {})", decoration.toString(), model.location, model.id, pos.getX(), pos.getY(), pos.getZ());
                }
            }
        }
    }

    /**
     * A debug method to visualize bounding boxes in the game
     */
    public static void buildBoundingBox(IWorld world, MutableBoundingBox box, Block block) {
        BlockState state = block.getDefaultState();

        for (int x0 = box.minX; x0 < box.maxX; x0++) {
            world.setBlockState(new BlockPos(x0, box.minY, box.minZ), state, 2);
            world.setBlockState(new BlockPos(x0, box.minY, box.maxZ), state, 2);

            world.setBlockState(new BlockPos(x0, box.maxY, box.minZ), state, 2);
            world.setBlockState(new BlockPos(x0, box.maxY, box.maxZ), state, 2);
        }

        for (int y0 = box.minY; y0 < box.maxY; y0++) {
            world.setBlockState(new BlockPos(box.minX, y0, box.minZ), state, 2);
            world.setBlockState(new BlockPos(box.minX, y0, box.maxZ), state, 2);

            world.setBlockState(new BlockPos(box.maxX, y0, box.minZ), state, 2);
            world.setBlockState(new BlockPos(box.maxX, y0, box.maxZ), state, 2);
        }

        for (int z0 = box.minZ; z0 < box.maxZ; z0++) {
            world.setBlockState(new BlockPos(box.minX, box.minY, z0), state, 2);
            world.setBlockState(new BlockPos(box.minX, box.maxY, z0), state, 2);

            world.setBlockState(new BlockPos(box.maxX, box.minY, z0), state, 2);
            world.setBlockState(new BlockPos(box.maxX, box.maxY, z0), state, 2);
        }

        world.setBlockState(new BlockPos(box.maxX, box.maxY, box.maxZ), state, 2);
    }

    public static Direction getOneWayDirection(DungeonPiece piece) {
        if (piece.sides[0])
            return Direction.NORTH;
        if (piece.sides[1])
            return Direction.EAST;
        if (piece.sides[2])
            return Direction.SOUTH;
        if (piece.sides[3])
            return Direction.WEST;
        return Direction.NORTH;
    }

    /**
     * Returns if it is possible to connect another room / corridor to this piece at
     * the given side. (currently unused)
     *
     * @param side The side where the connection would take place
     */
    public boolean canConnect(Direction side) {
        return true;
    }

    public static void spawnMobs(IWorld world, DungeonPiece piece, int width, int length, int[] floors) {
        for (int floor : floors) {
            for (int x = 1; x < width; x++) {
                for (int z = 1; z < length; z++) {
                    BlockPos pos = new BlockPos(piece.x + x, piece.y + floor + 1, piece.z + z);
                    if (piece.boundingBox.isVecInside(pos) && world.getBlockState(pos).isAir(world, pos)
                            && world.getRandom().nextDouble() < Config.MOB_SPAWN_RATE.get()) {
                        EntityType<?> mob = RandomMonster.randomMonster(world.getRandom(), piece.stage);
                        Entity entity = mob.create(world.getWorld());
                        if (entity instanceof MonsterEntity) {
                            MonsterEntity mobEntity = (MonsterEntity) entity;
                            mobEntity.heal(mobEntity.getMaxHealth());
                            mobEntity.setLocationAndAngles(pos.getX(), pos.getY(), pos.getZ(), 0, 0);
                            Spawner.equipMonster(mobEntity, world.getRandom(), piece.stage);
                            mobEntity.onInitialSpawn(world, world.getDifficultyForLocation(pos), SpawnReason.STRUCTURE,
                                    null, null);
                            world.addEntity(mobEntity);
                        }
                    }
                }
            }
        }
    }

    public static Direction getOpenSide(DungeonPiece piece, int n) {
        int c = 0;
        for (int i = 0; i < 4; i++) {
            if (piece.sides[i] && c++ == n)
                return getDirectionFromInt(i);
        }
        throw new IllegalStateException(piece + " does not have " + n + "or more open sides.");
    }

    public static Direction getDirectionFromInt(int dir) {
        switch (dir) {
            case 1:
                return Direction.EAST;
            case 2:
                return Direction.SOUTH;
            case 3:
                return Direction.WEST;
            default:
                return Direction.NORTH;
        }
    }

    public static DirectionalBlockPos[] readAllPositions(ListNBT nbt) {
        DirectionalBlockPos[] positions = new DirectionalBlockPos[nbt.size()];

        for (int i = 0; i < positions.length; i++) {
            positions[i] = DirectionalBlockPos.fromNBT(nbt.getCompound(i));
        }

        return positions;
    }

    public static void writeAllPositions(DirectionalBlockPos[] positions, ListNBT nbt) {
        for (DirectionalBlockPos directionalBlockPos : positions) {
            CompoundNBT position = new CompoundNBT();
            directionalBlockPos.writeToNBT(position);
            nbt.add(position);
        }
    }

    public static int getGroundHeightFrom(IWorld world, int x, int z, int yStart) {
        for (int y = yStart; y > 0; y--)
            if (world.getBlockState(new BlockPos(x, y, z)).isSolid())
                return y;
        return 0;
    }

    public static boolean isBlockProtected(IWorld world, BlockState state, BlockPos pos) {
        return state.getBlockHardness(world, pos) < 0 || BlockTags.PORTALS.contains(state.getBlock());
    }

}