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

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.fluid.FluidState;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.state.properties.Half;
import net.minecraft.tileentity.LockableLootTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Rotation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MutableBoundingBox;
import net.minecraft.util.math.vector.Vector3i;
import net.minecraft.world.IWorld;
import net.minecraft.world.gen.feature.structure.IStructurePieceType;
import net.minecraft.world.gen.feature.structure.StructurePiece;
import xiroc.dungeoncrawl.DungeonCrawl;
import xiroc.dungeoncrawl.config.Config;
import xiroc.dungeoncrawl.dungeon.DungeonBuilder;
import xiroc.dungeoncrawl.dungeon.DungeonType;
import xiroc.dungeoncrawl.dungeon.PillarGenerator;
import xiroc.dungeoncrawl.dungeon.PlacementConfiguration;
import xiroc.dungeoncrawl.dungeon.block.DungeonBlocks;
import xiroc.dungeoncrawl.dungeon.block.provider.BlockStateProvider;
import xiroc.dungeoncrawl.dungeon.decoration.DungeonDecoration;
import xiroc.dungeoncrawl.dungeon.model.DungeonModel;
import xiroc.dungeoncrawl.dungeon.model.DungeonModelBlock;
import xiroc.dungeoncrawl.dungeon.model.DungeonModelFeature;
import xiroc.dungeoncrawl.dungeon.model.DungeonModels;
import xiroc.dungeoncrawl.dungeon.model.ModelSelector;
import xiroc.dungeoncrawl.dungeon.model.MultipartModelData;
import xiroc.dungeoncrawl.dungeon.model.PlacementBehaviour;
import xiroc.dungeoncrawl.dungeon.treasure.Loot;
import xiroc.dungeoncrawl.theme.SecondaryTheme;
import xiroc.dungeoncrawl.theme.Theme;
import xiroc.dungeoncrawl.util.IBlockPlacementHandler;
import xiroc.dungeoncrawl.util.Orientation;
import xiroc.dungeoncrawl.util.Position2D;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public abstract class DungeonPiece extends StructurePiece {

    public static final int CORRIDOR = 0;
    public static final int STAIRS = 1;
    public static final int ENTRANCE = 6;
    public static final int ROOM = 8;
    public static final int SIDE_ROOM = 9;
    public static final int NODE_ROOM = 10;
    public static final int NODE_CONNECTOR = 11;
    public static final int MEGA_NODE_PART = 12;
    public static final int SECRET_ROOM = 14;
    public static final int SPIDER_ROOM = 15;
    public static final int MULTIPART_PIECE = 16;

    public static final CompoundNBT DEFAULT_NBT;

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
    public int connectedSides, x, y, z, stage;
    public boolean[] sides; // Order: North, East, South, West

    @Nullable
    public DungeonModelFeature.Instance[] features;
    public byte[] variation;

    public DungeonModel model;
    public Theme theme;
    public SecondaryTheme secondaryTheme;

    public Position2D gridPosition;

    public boolean worldGen = true;

    public DungeonPiece(IStructurePieceType p_i51343_1_) {
        super(p_i51343_1_, DEFAULT_NBT);
        this.sides = new boolean[4];
        this.rotation = Rotation.NONE;
        this.gridPosition = new Position2D(0, 0);
    }

    public DungeonPiece(IStructurePieceType p_i51343_1_, CompoundNBT p_i51343_2_) {
        super(p_i51343_1_, p_i51343_2_);
        this.sides = new boolean[4];
        this.sides[0] = p_i51343_2_.getBoolean("north");
        this.sides[1] = p_i51343_2_.getBoolean("east");
        this.sides[2] = p_i51343_2_.getBoolean("south");
        this.sides[3] = p_i51343_2_.getBoolean("west");
        this.connectedSides = p_i51343_2_.getInt("connectedSides");
        this.gridPosition = new Position2D(p_i51343_2_.getInt("posX"), p_i51343_2_.getInt("posZ"));
        this.x = p_i51343_2_.getInt("x");
        this.y = p_i51343_2_.getInt("y");
        this.z = p_i51343_2_.getInt("z");
        this.stage = p_i51343_2_.getInt("stage");
        this.rotation = Orientation.getRotation(p_i51343_2_.getInt("rotation"));

        if (p_i51343_2_.contains("theme", 99)) {
            this.theme = Theme.getThemeByID(p_i51343_2_.getInt("theme"));
        } else {
            this.theme = Theme.getTheme(new ResourceLocation(p_i51343_2_.getString("theme")));
        }

        if (p_i51343_2_.contains("subTheme", 99)) {
            this.secondaryTheme = Theme.getSecondaryThemeByID(p_i51343_2_.getInt("subTheme"));
        } else {
            this.secondaryTheme = Theme.getSecondaryTheme(new ResourceLocation(p_i51343_2_.getString("secondaryTheme")));
        }

        if (p_i51343_2_.contains("model", 99)) {
            this.model = DungeonModels.ID_TO_MODEL.get(p_i51343_2_.getInt("model"));
        } else {
            this.model = DungeonModels.KEY_TO_MODEL.get(new ResourceLocation(p_i51343_2_.getString("model")));
        }

        if (p_i51343_2_.contains("features", 9)) {
            this.features = readAllFeatures(p_i51343_2_.getList("features", 10));
        }

        if (p_i51343_2_.contains("variation")) {
            this.variation = p_i51343_2_.getByteArray("variation");
        }

        createBoundingBox();
    }

    @Override
    public void addAdditionalSaveData(CompoundNBT tagCompound) {
        tagCompound.putBoolean("north", sides[0]);
        tagCompound.putBoolean("east", sides[1]);
        tagCompound.putBoolean("south", sides[2]);
        tagCompound.putBoolean("west", sides[3]);
        tagCompound.putInt("connectedSides", connectedSides);
        tagCompound.putInt("posX", gridPosition.x);
        tagCompound.putInt("posZ", gridPosition.z);
        tagCompound.putInt("x", x);
        tagCompound.putInt("y", y);
        tagCompound.putInt("z", z);
        tagCompound.putInt("stage", stage);
        tagCompound.putInt("rotation", Orientation.rotationAsInt(this.rotation));

        if (model != null) {
            tagCompound.putString("model", model.getKey().toString());
        }

        if (theme != null) {
            tagCompound.putString("theme", theme.getKey().toString());
        }

        if (secondaryTheme != null) {
            tagCompound.putString("secondaryTheme", secondaryTheme.getKey().toString());
        }

        if (features != null) {
            ListNBT list = new ListNBT();
            writeAllFeatures(features, list);
            tagCompound.put("features", list);
        }

        if (variation != null) {
            tagCompound.putByteArray("variation", variation);
        }
    }

    public abstract int getDungeonPieceType();

    /**
     * Called during the dungeon-post-processing to determine the model that will be
     * used to build this piece.
     */
    public abstract void setupModel(DungeonBuilder builder, ModelSelector modelSelector, List<DungeonPiece> pieces, Random rand);

    public void createBoundingBox() {
        if (model != null) {
            this.boundingBox = model.createBoundingBoxWithOffset(x, y, z, rotation);
        }
    }

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
        }
    }

    public void setRotation(Rotation rotation) {
        this.rotation = rotation;
    }

    @Override
    public Rotation getRotation() {
        return this.rotation;
    }

    public void setGridPosition(int x, int z) {
        this.gridPosition = new Position2D(x, z);
    }

    public void setGridPosition(Position2D position) {
        this.gridPosition = position;
    }

    public void setWorldPosition(int x, int y, int z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public void setup(Random rand) {
        if (model == null) {
            return;
        }

        if (model.hasFeatures()) {
            Vector3i offset = model.getOffset(rotation);
            List<DungeonModelFeature.Instance> features = new ArrayList<>();
            for (DungeonModelFeature feature : model.getFeatures()) {
                feature.setup(model, x + offset.getX(), y + offset.getY(), z + offset.getZ(), rotation, features, rand);
            }
            this.features = features.toArray(new DungeonModelFeature.Instance[0]);
        }
        if (model.isVariationEnabled()) {
            this.variation = new byte[8];
            for (int i = 0; i < variation.length; i++) {
                variation[i] = (byte) rand.nextInt(64);
            }
        }
    }

    protected boolean hasPillarAt(BlockPos pos) {
        return false;
    }

    public void takeOverProperties(DungeonPiece piece) {
        this.sides = piece.sides;
        this.connectedSides = piece.connectedSides;
        this.rotation = piece.rotation;
    }

    public boolean hasChildPieces() {
        return model != null && model.hasMultipart();
    }

    public void addChildPieces(List<DungeonPiece> pieces, DungeonBuilder builder, DungeonType type, ModelSelector modelSelector, int layer, Random rand) {
        if (model != null && (model.hasMultipart() || type.getLayer(layer).hasMultipartOverride(model))) {
            BlockPos pos = new BlockPos(x, y, z).offset(model.getOffset(rotation));
            for (MultipartModelData data : type.getLayer(layer).getMultipartData(model)) {
                if (data.checkConditions(this)) {
                    pieces.add(data.models.roll(rand).createMultipartPiece(this, model, this.rotation, pos.getX(), pos.getY(), pos.getZ(), rand));
                } else if (data.alternatives != null) {
                    pieces.add(data.alternatives.roll(rand).createMultipartPiece(this, model, this.rotation, pos.getX(), pos.getY(), pos.getZ(), rand));
                }
            }
        }
    }

    public void buildModel(DungeonModel model, IWorld world, MutableBoundingBox boundsIn, BlockPos pos, PlacementConfiguration configuration,
                           Theme theme, SecondaryTheme secondaryTheme, int lootLevel, Rotation rotation, boolean worldGen, boolean fillAir, boolean expandDownwards) {
        if (Config.EXTENDED_DEBUG.get()) {
            DungeonCrawl.LOGGER.debug("Building {} with rotation {} at ({} | {} | {})", model.getKey(), rotation, pos.getX(), pos.getY(), pos.getZ());
        }

        List<BlockPos> fancyPillars = new ArrayList<>(4);

        model.blocks.forEach((block) -> {
            BlockPos position = block.worldPos(model, rotation, pos);

            if (boundsIn.isInside(position)) {
                BlockState state = block.type.blockFactory.get(block, rotation, world, position, theme, secondaryTheme, world.getRandom(), variation, stage);

                placeBlock(world, state, position, block, rotation, configuration, theme, secondaryTheme, fancyPillars, lootLevel, fillAir, expandDownwards, worldGen);
            }
        });

        fancyPillars.forEach((pillar) -> PillarGenerator.generateFancyPillar(world, pillar, boundsIn,  theme));
        fancyPillars.clear();

        if (Config.EXTENDED_DEBUG.get()) {
            DungeonCrawl.LOGGER.debug("Finished building {} with rotation {} at ({} | {} | {})", model.getKey(), rotation, pos.getX(), pos.getY(), pos.getZ());
        }
    }

    public void placeBlock(IWorld world, BlockState state, BlockPos position, DungeonModelBlock block, Rotation rotation, PlacementConfiguration configuration,
                           Theme theme, SecondaryTheme secondaryTheme, List<BlockPos> fancyPillars, int lootLevel, boolean fillAir, boolean expandDownwards, boolean worldGen) {
        if (DungeonBuilder.isBlockProtected(world, position)) {
            return;
        }

        PlacementBehaviour placementBehaviour = block.type.placementBehaviourFromConfig.apply(configuration);

        if (world.isEmptyBlock(position) && !placementBehaviour.isSolid(world, position, rotation, world.getRandom())) {
            if (placementBehaviour.airBlock != null) {
                state = placementBehaviour.airBlock.apply(theme, secondaryTheme).get(world, position);
            } else if (!fillAir && !Config.SOLID.get()) {
                return;
            }
        }

        Random rand = world.getRandom();

        IBlockPlacementHandler.getHandler(state.getBlock()).place(world, state, position, rand, theme, secondaryTheme, lootLevel, worldGen);

        TileEntity tile = world.getBlockEntity(position);
        if (tile instanceof LockableLootTileEntity) {
            if (model.hasLootTable()) {
                Loot.setLoot(world, position, (LockableLootTileEntity) tile, model.getLootTable(), theme, secondaryTheme, rand);
            } else {
                Loot.setLoot(world, position, (LockableLootTileEntity) tile, Loot.getLootTable(stage, rand), theme, secondaryTheme, rand);
            }
        }

        FluidState fluidState = world.getFluidState(position);
        if (!fluidState.isEmpty()) {
            world.getLiquidTicks().scheduleTick(position, fluidState.getType(), 0);

        }

        if ((block.hasProperties || !state.getProperties().isEmpty()) && worldGen) {
            world.getChunk(position).markPosForPostprocessing(position);
        }

        if (expandDownwards) {
            if (block.type.isExpandable() && block.position.getY() == 0 && !world.getBlockState(position.below()).canOcclude()) {
                PillarGenerator.generateSimplePillar(world, position.below(), theme);
            }
        } else {
            if (block.position.getY() == 0 && (block.type.isPillar() || hasPillarAt(position)) && !world.getBlockState(position.below()).canOcclude()) {
                fancyPillars.add(position.below());
            }
        }
    }

    public void replaceBlockState(IWorld worldIn, BlockState blockState, int x, int y, int z,
                                  MutableBoundingBox bounds, boolean postProcessing) {
        BlockPos blockPos = new BlockPos(x, y, z);
        if (bounds.isInside(blockPos)) {

            if (DungeonBuilder.isBlockProtected(worldIn, blockPos) || worldIn.isEmptyBlock(blockPos))
                return;

            worldIn.setBlock(blockPos, blockState, 2);
            if (postProcessing) {
                worldIn.getChunk(blockPos).markPosForPostprocessing(blockPos);
            }

            FluidState fluidstate = worldIn.getFluidState(blockPos);
            if (!fluidstate.isEmpty()) {
                worldIn.getLiquidTicks().scheduleTick(blockPos, fluidstate.getType(), 0);
            }
        }
    }

    protected void entrances(IWorld world, MutableBoundingBox bounds, DungeonModel model, boolean worldGen) {
        int pathStartX = (model.width - 3) / 2, pathStartZ = (model.length - 3) / 2;
        Vector3i offset = model.getOffset(rotation);
        BlockPos pos = new BlockPos(x + offset.getX(), y, z + offset.getZ()); // Ignore the y offset

        if (sides[0]) {
            for (int x0 = pathStartX; x0 < pathStartX + 3; x0++) {
                replaceBlockState(world, CAVE_AIR, pos.getX() + x0, pos.getY() + 1, pos.getZ(), bounds, worldGen);
                replaceBlockState(world, CAVE_AIR, pos.getX() + x0, pos.getY() + 2, pos.getZ(), bounds, worldGen);
            }

            replaceBlockState(world, CAVE_AIR, pos.getX() + pathStartX + 1, pos.getY() + 3, pos.getZ(), bounds, worldGen);

            BlockStateProvider stateProvider = model.getEntranceType() == 0 ? theme.stairs : secondaryTheme.stairs;

            BlockPos pos1 = new BlockPos(pos.getX() + pathStartX, pos.getY() + 3, pos.getZ());
            BlockState stair1 = stateProvider.get(world, pos1);
            stair1 = DungeonBlocks.applyProperty(stair1, BlockStateProperties.HORIZONTAL_FACING, Direction.WEST);
            stair1 = DungeonBlocks.applyProperty(stair1, BlockStateProperties.HALF, Half.TOP);
            replaceBlockState(world, stair1, pos1.getX(), pos1.getY(), pos1.getZ(), bounds, worldGen);

            BlockPos pos2 = pos1.relative(Direction.EAST, 2);
            BlockState stair2 = stateProvider.get(world, pos2);
            stair2 = DungeonBlocks.applyProperty(stair2, BlockStateProperties.HORIZONTAL_FACING, Direction.EAST);
            stair2 = DungeonBlocks.applyProperty(stair2, BlockStateProperties.HALF, Half.TOP);
            replaceBlockState(world, stair2, pos2.getX(), pos2.getY(), pos2.getZ(), bounds, worldGen);

        }
        if (sides[1]) {
            for (int z0 = pathStartZ; z0 < pathStartZ + 3; z0++) {
                replaceBlockState(world, CAVE_AIR, pos.getX() + model.width - 1, pos.getY() + 1, pos.getZ() + z0, bounds, worldGen);
                replaceBlockState(world, CAVE_AIR, pos.getX() + model.width - 1, pos.getY() + 2, pos.getZ() + z0, bounds, worldGen);
            }

            replaceBlockState(world, CAVE_AIR, pos.getX() + model.width - 1, pos.getY() + 3, pos.getZ() + pathStartZ + 1, bounds, worldGen);

            BlockStateProvider stateProvider = model.getEntranceType() == 0 ? theme.stairs : secondaryTheme.stairs;

            BlockPos pos1 = new BlockPos(pos.getX() + model.width - 1, pos.getY() + 3, pos.getZ() + pathStartZ);
            BlockState stair1 = stateProvider.get(world, pos1);
            stair1 = DungeonBlocks.applyProperty(stair1, BlockStateProperties.HORIZONTAL_FACING, Direction.NORTH);
            stair1 = DungeonBlocks.applyProperty(stair1, BlockStateProperties.HALF, Half.TOP);
            replaceBlockState(world, stair1, pos1.getX(), pos1.getY(), pos1.getZ(), bounds, worldGen);

            BlockPos pos2 = pos1.relative(Direction.SOUTH, 2);
            BlockState stair2 = stateProvider.get(world, pos2);
            stair2 = DungeonBlocks.applyProperty(stair2, BlockStateProperties.HORIZONTAL_FACING, Direction.SOUTH);
            stair2 = DungeonBlocks.applyProperty(stair2, BlockStateProperties.HALF, Half.TOP);
            replaceBlockState(world, stair2, pos2.getX(), pos2.getY(), pos2.getZ(), bounds, worldGen);
        }
        if (sides[2]) {
            for (int x0 = pathStartX; x0 < pathStartX + 3; x0++) {
                replaceBlockState(world, CAVE_AIR, pos.getX() + x0, pos.getY() + 1, pos.getZ() + model.length - 1, bounds, worldGen);
                replaceBlockState(world, CAVE_AIR, pos.getX() + x0, pos.getY() + 2, pos.getZ() + model.length - 1, bounds, worldGen);
            }

            replaceBlockState(world, CAVE_AIR, pos.getX() + pathStartX + 1, pos.getY() + 3, pos.getZ() + model.length - 1, bounds, worldGen);

            BlockStateProvider stateProvider = model.getEntranceType() == 0 ? theme.stairs : secondaryTheme.stairs;

            BlockPos pos1 = new BlockPos(pos.getX() + pathStartX, pos.getY() + 3, pos.getZ() + model.length - 1);
            BlockState stair1 = stateProvider.get(world, pos1);
            stair1 = DungeonBlocks.applyProperty(stair1, BlockStateProperties.HORIZONTAL_FACING, Direction.WEST);
            stair1 = DungeonBlocks.applyProperty(stair1, BlockStateProperties.HALF, Half.TOP);
            replaceBlockState(world, stair1, pos1.getX(), pos1.getY(), pos1.getZ(), bounds, worldGen);

            BlockPos pos2 = pos1.relative(Direction.EAST, 2);
            BlockState stair2 = stateProvider.get(world, pos2);
            stair2 = DungeonBlocks.applyProperty(stair2, BlockStateProperties.HORIZONTAL_FACING, Direction.EAST);
            stair2 = DungeonBlocks.applyProperty(stair2, BlockStateProperties.HALF, Half.TOP);
            replaceBlockState(world, stair2, pos2.getX(), pos2.getY(), pos2.getZ(), bounds, worldGen);
        }
        if (sides[3]) {
            for (int z0 = pathStartZ; z0 < pathStartZ + 3; z0++) {
                replaceBlockState(world, CAVE_AIR, pos.getX(), pos.getY() + 1, pos.getZ() + z0, bounds, worldGen);
                replaceBlockState(world, CAVE_AIR, pos.getX(), pos.getY() + 2, pos.getZ() + z0, bounds, worldGen);
            }

            replaceBlockState(world, CAVE_AIR, pos.getX(), pos.getY() + 3, pos.getZ() + pathStartZ + 1, bounds, worldGen);

            BlockStateProvider stateProvider = model.getEntranceType() == 0 ? theme.stairs : secondaryTheme.stairs;

            BlockPos pos1 = new BlockPos(pos.getX(), pos.getY() + 3, pos.getZ() + pathStartZ);
            BlockState stair1 = stateProvider.get(world, pos1);
            stair1 = DungeonBlocks.applyProperty(stair1, BlockStateProperties.HORIZONTAL_FACING, Direction.NORTH);
            stair1 = DungeonBlocks.applyProperty(stair1, BlockStateProperties.HALF, Half.TOP);
            replaceBlockState(world, stair1, pos1.getX(), pos1.getY(), pos1.getZ(), bounds, worldGen);

            BlockPos pos2 = pos1.relative(Direction.SOUTH, 2);
            BlockState stair2 = stateProvider.get(world, pos2);
            stair2 = DungeonBlocks.applyProperty(stair2, BlockStateProperties.HORIZONTAL_FACING, Direction.SOUTH);
            stair2 = DungeonBlocks.applyProperty(stair2, BlockStateProperties.HALF, Half.TOP);
            replaceBlockState(world, stair2, pos2.getX(), pos2.getY(), pos2.getZ(), bounds, worldGen);
        }
    }

    protected void decorate(IWorld world, BlockPos pos, int width, int height, int length, Theme theme, MutableBoundingBox worldGenBounds, MutableBoundingBox structureBounds,
                            DungeonModel model, boolean worldGen) {
        if (theme.hasDecorations()) {
            for (DungeonDecoration decoration : theme.getDecorations()) {
                if (Config.EXTENDED_DEBUG.get()) {
                    DungeonCrawl.LOGGER.debug("Running decoration {} for {} at ({} | {} | {})", decoration.toString(), model.getKey(), pos.getX(), pos.getY(), pos.getZ());
                }

                decoration.decorate(model, world, pos, width, height, length, worldGenBounds, structureBounds, this, stage, worldGen);

                if (Config.EXTENDED_DEBUG.get()) {
                    DungeonCrawl.LOGGER.debug("Finished decoration {} for {} at ({} | {} | {})", decoration.toString(), model.getKey(), pos.getX(), pos.getY(), pos.getZ());
                }
            }
        }
    }

    protected void placeFeatures(IWorld world, MutableBoundingBox bounds, Theme
            theme, SecondaryTheme secondaryTheme, Random rand, int stage, boolean worldGen) {
        if (features != null) {
            for (DungeonModelFeature.Instance feature : features) {
                feature.place(world, bounds, rand, theme, secondaryTheme, stage, worldGen);
            }
        }
    }

    /**
     * A debug method to visualize bounding boxes ingame.
     */
    public static void buildBoundingBox(IWorld world, MutableBoundingBox box, Block block) {
        BlockState state = block.defaultBlockState();

        for (int x0 = box.x0; x0 < box.x1; x0++) {
            world.setBlock(new BlockPos(x0, box.y0, box.z0), state, 2);
            world.setBlock(new BlockPos(x0, box.y0, box.z1), state, 2);

            world.setBlock(new BlockPos(x0, box.y1, box.z0), state, 2);
            world.setBlock(new BlockPos(x0, box.y1, box.z1), state, 2);
        }

        for (int y0 = box.y0; y0 < box.y1; y0++) {
            world.setBlock(new BlockPos(box.x0, y0, box.z0), state, 2);
            world.setBlock(new BlockPos(box.x0, y0, box.z1), state, 2);

            world.setBlock(new BlockPos(box.x1, y0, box.z0), state, 2);
            world.setBlock(new BlockPos(box.x1, y0, box.z1), state, 2);
        }

        for (int z0 = box.z0; z0 < box.z1; z0++) {
            world.setBlock(new BlockPos(box.x0, box.y0, z0), state, 2);
            world.setBlock(new BlockPos(box.x0, box.y1, z0), state, 2);

            world.setBlock(new BlockPos(box.x1, box.y0, z0), state, 2);
            world.setBlock(new BlockPos(box.x1, box.y1, z0), state, 2);
        }

        world.setBlock(new BlockPos(box.x1, box.y1, box.z1), state, 2);
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
     * the given side.
     *
     * @param side The side where the connection would take place
     * @param x    the x coordinate in the grid of the connecting piece
     * @param z    the z coordinate in the grid of the connecting piece
     */
    public boolean canConnect(Direction side, int x, int z) {
        return true;
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

    protected static ListNBT positionsToNbt(BlockPos[] positions) {
        ListNBT nbtPositions = new ListNBT();
        for (BlockPos pillar : positions) {
            CompoundNBT p = new CompoundNBT();
            p.putInt("x", pillar.getX());
            p.putInt("y", pillar.getY());
            p.putInt("z", pillar.getZ());
            nbtPositions.add(p);
        }
        return nbtPositions;
    }

    protected static BlockPos[] positionsFromNbt(ListNBT nbt) {
        BlockPos[] positions = new BlockPos[nbt.size()];
        for (int i = 0; i < nbt.size(); i++) {
            CompoundNBT pillar = nbt.getCompound(i);
            positions[i] = new BlockPos(pillar.getInt("x"), pillar.getInt("y"), pillar.getInt("z"));
        }
        return positions;

    }

    protected static DungeonModelFeature.Instance[] readAllFeatures(ListNBT nbt) {
        DungeonModelFeature.Instance[] features = new DungeonModelFeature.Instance[nbt.size()];

        for (int i = 0; i < features.length; i++) {
            features[i] = DungeonModelFeature.Instance.read(nbt.getCompound(i));
        }

        return features;
    }

    protected static void writeAllFeatures(DungeonModelFeature.Instance[] positions, ListNBT nbt) {
        for (DungeonModelFeature.Instance feature : positions) {
            CompoundNBT position = new CompoundNBT();
            feature.write(position);
            nbt.add(position);
        }
    }

}