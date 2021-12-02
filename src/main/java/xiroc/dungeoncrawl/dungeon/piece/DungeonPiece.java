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
import net.minecraft.core.Direction;
import net.minecraft.core.Vec3i;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.RandomizableContainerBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.Half;
import net.minecraft.world.level.levelgen.feature.StructurePieceType;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import net.minecraft.world.level.levelgen.structure.StructurePiece;
import net.minecraft.world.level.levelgen.structure.pieces.StructurePieceSerializationContext;
import net.minecraft.world.level.material.FluidState;
import xiroc.dungeoncrawl.DungeonCrawl;
import xiroc.dungeoncrawl.config.Config;
import xiroc.dungeoncrawl.dungeon.DungeonBuilder;
import xiroc.dungeoncrawl.dungeon.DungeonType;
import xiroc.dungeoncrawl.dungeon.block.DungeonBlocks;
import xiroc.dungeoncrawl.dungeon.block.provider.BlockStateProvider;
import xiroc.dungeoncrawl.dungeon.decoration.DungeonDecoration;
import xiroc.dungeoncrawl.dungeon.model.DungeonModel;
import xiroc.dungeoncrawl.dungeon.model.DungeonModelBlock;
import xiroc.dungeoncrawl.dungeon.model.DungeonModelBlockType;
import xiroc.dungeoncrawl.dungeon.model.DungeonModelFeature;
import xiroc.dungeoncrawl.dungeon.model.DungeonModels;
import xiroc.dungeoncrawl.dungeon.model.ModelSelector;
import xiroc.dungeoncrawl.dungeon.model.MultipartModelData;
import xiroc.dungeoncrawl.dungeon.model.PlacementBehaviour;
import xiroc.dungeoncrawl.dungeon.treasure.Loot;
import xiroc.dungeoncrawl.theme.Theme;
import xiroc.dungeoncrawl.theme.Theme.SecondaryTheme;
import xiroc.dungeoncrawl.util.IBlockPlacementHandler;
import xiroc.dungeoncrawl.util.Orientation;
import xiroc.dungeoncrawl.util.Position2D;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public abstract class DungeonPiece extends StructurePiece {

    private static final BoundingBox EMPTY_BOX = new BoundingBox(0, 0, 0, 0, 0, 0);

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

    public DungeonPiece(StructurePieceType p_i51343_1_) {
        super(p_i51343_1_, 0, EMPTY_BOX);
        this.sides = new boolean[4];
        this.connectedSides = 0;
        this.rotation = Rotation.NONE;
        this.gridPosition = new Position2D(0, 0);
    }

    public DungeonPiece(StructurePieceType p_i51343_1_, CompoundTag p_i51343_2_) {
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
            this.secondaryTheme = Theme.getSubThemeByID(p_i51343_2_.getInt("subTheme"));
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
    public void addAdditionalSaveData(StructurePieceSerializationContext context, CompoundTag tagCompound) {
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
            ListTag list = new ListTag();
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
            case NORTH -> {
                if (sides[0])
                    return;
                sides[0] = true;
                connectedSides++;
            }
            case EAST -> {
                if (sides[1])
                    return;
                sides[1] = true;
                connectedSides++;
            }
            case SOUTH -> {
                if (sides[2])
                    return;
                sides[2] = true;
                connectedSides++;
            }
            case WEST -> {
                if (sides[3])
                    return;
                sides[3] = true;
                connectedSides++;
            }
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

    public void customSetup(Random rand) {
        if (model == null) {
            return;
        }

        if (model.hasFeatures()) {
            Vec3i offset = model.getOffset(rotation);
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


    public void setBlockState(LevelAccessor world, BlockState state, BlockPos pos, PlacementBehaviour placementBehaviour,
                              Theme theme, SecondaryTheme secondaryTheme, int lootLevel, boolean worldGen, boolean fillAir) {
        if (state == null)
            return;

        if (DungeonBuilder.isBlockProtected(world, pos)) {
            return;
        }

        if (world.isEmptyBlock(pos) && !placementBehaviour.isSolid(world, pos, world.getRandom())) {
            if (placementBehaviour.airBlock != null) {
                state = placementBehaviour.airBlock.apply(theme, secondaryTheme).get(world, pos);
            } else if (!fillAir && !Config.SOLID.get()) {
                return;
            }
        }

        Random rand = world.getRandom();

        IBlockPlacementHandler.getHandler(state.getBlock()).place(world, state, pos, rand, theme, secondaryTheme, lootLevel, worldGen);

        BlockEntity tile = world.getBlockEntity(pos);
        if (tile instanceof RandomizableContainerBlockEntity randomizableContainerBlockEntity) {
            if (model.hasLootTable()) {
                Loot.setLoot(world, pos, randomizableContainerBlockEntity, model.getLootTable(), theme, secondaryTheme, rand);
            } else {
                Loot.setLoot(world, pos, randomizableContainerBlockEntity, Loot.getLootTable(stage, rand), theme, secondaryTheme, rand);
            }
        }

        FluidState fluidState = world.getFluidState(pos);
        if (!fluidState.isEmpty()) {
            world.scheduleTick(pos, fluidState.getType(), 0);
        }
    }

    public void replaceBlockState(LevelAccessor worldIn, BlockState blockState, int x, int y, int z,
                                  BoundingBox bounds, boolean postProcessing) {
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
                worldIn.scheduleTick(blockPos, fluidstate.getType(), 0);
            }
        }
    }

    private void buildModelBlock(LevelAccessor world, BlockPos position, BlockState state, DungeonModelBlock block, Theme theme, SecondaryTheme secondaryTheme,
                                 int lootLevel, boolean worldGen, boolean fillAir, boolean expandDownwards) {
        if (state == null)
            return;

        placeBlock(world, state, position, block, theme, secondaryTheme, lootLevel, fillAir, worldGen);

        if (block.type == DungeonModelBlockType.PILLAR) {
            tryBuildFancyPillarPart(world, block, position);
        }

        if (expandDownwards && block.type.isExpandable() && block.position.getY() == 0 && !world.getBlockState(position.below()).canOcclude()) {
            buildPillar(world, position.below());
        }
    }

    public void build(DungeonModel model, LevelAccessor world, BoundingBox boundsIn, BlockPos pos, Theme theme,
                      SecondaryTheme secondaryTheme, int lootLevel, boolean worldGen, boolean fillAir, boolean expandDownwards) {
        if (Config.EXTENDED_DEBUG.get()) {
            DungeonCrawl.LOGGER.debug("Building {} at ({} | {} | {})", model.getKey(), pos.getX(), pos.getY(), pos.getZ());
        }

        model.blocks.forEach((block) -> {
            BlockPos position = pos.offset(block.position);
            if (boundsIn.isInside(position)) {
                BlockState state = block.type.blockFactory.get(block, Rotation.NONE, world, position, theme, secondaryTheme, world.getRandom(), variation, stage);

                buildModelBlock(world, position, state, block, theme, secondaryTheme, lootLevel, worldGen, fillAir, expandDownwards);
            }
        });

        if (Config.EXTENDED_DEBUG.get()) {
            DungeonCrawl.LOGGER.debug("Finished building {} at ({} | {} | {})", model.getKey(), pos.getX(), pos.getY(), pos.getZ());
        }
    }


    public void buildRotated(DungeonModel model, LevelAccessor world, BoundingBox boundsIn, BlockPos pos, Theme theme,
                             SecondaryTheme secondaryTheme, int lootLevel, Rotation rotation, boolean worldGen, boolean fillAir, boolean expandDownwards) {
        if (Config.EXTENDED_DEBUG.get()) {
            DungeonCrawl.LOGGER.debug("Building {} with rotation {} at ({} | {} | {})", model.getKey(), rotation, pos.getX(), pos.getY(), pos.getZ());
        }
        switch (rotation) {
            case CLOCKWISE_90 -> {
                model.blocks.forEach((block) -> {
                    BlockPos position = new BlockPos(
                            pos.getX() + model.length - block.position.getZ() - 1,
                            pos.getY() + block.position.getY(),
                            pos.getZ() + block.position.getX());

                    if (boundsIn.isInside(position)) {
                        BlockState state = block.type.blockFactory.get(block, Rotation.CLOCKWISE_90, world, position, theme, secondaryTheme, world.getRandom(), variation, stage);

                        buildModelBlock(world, position, state, block, theme, secondaryTheme, lootLevel, worldGen, fillAir, expandDownwards);
                    }
                });
            }
            case COUNTERCLOCKWISE_90 -> {
                model.blocks.forEach((block) -> {
                    BlockPos position = new BlockPos(
                            pos.getX() + block.position.getZ(),
                            pos.getY() + block.position.getY(),
                            pos.getZ() + model.width - block.position.getX() - 1);

                    if (boundsIn.isInside(position)) {
                        BlockState state = block.type.blockFactory.get(block, Rotation.COUNTERCLOCKWISE_90, world, position, theme, secondaryTheme, world.getRandom(), variation, stage);

                        buildModelBlock(world, position, state, block, theme, secondaryTheme, lootLevel, worldGen, fillAir, expandDownwards);
                    }
                });
            }
            case CLOCKWISE_180 -> {
                model.blocks.forEach((block) -> {
                    BlockPos position = new BlockPos(
                            pos.getX() + model.width - block.position.getX() - 1,
                            pos.getY() + block.position.getY(),
                            pos.getZ() + model.length - block.position.getZ() - 1);

                    if (boundsIn.isInside(position)) {
                        BlockState state = block.type.blockFactory.get(block, Rotation.CLOCKWISE_180, world, position, theme, secondaryTheme, world.getRandom(), variation, stage);

                        buildModelBlock(world, position, state, block, theme, secondaryTheme, lootLevel, worldGen, fillAir, expandDownwards);
                    }
                });
            }
            case NONE -> build(model, world, boundsIn, pos, theme, secondaryTheme, lootLevel, worldGen, fillAir, expandDownwards);
            default -> DungeonCrawl.LOGGER.warn("Failed to build a rotated dungeon segment: Unsupported rotation " + rotation);
        }

        if (Config.EXTENDED_DEBUG.get()) {
            DungeonCrawl.LOGGER.debug("Finished building {} with rotation {} at ({} | {} | {})", model.getKey(), rotation, pos.getX(), pos.getY(), pos.getZ());
        }
    }

    public void placeBlock(LevelAccessor world, BlockState state, BlockPos position, DungeonModelBlock block, Theme theme, SecondaryTheme secondaryTheme,
                           int lootLevel, boolean fillAir, boolean worldGen) {
        setBlockState(world, state, position, block.type.placementBehavior, theme, secondaryTheme, lootLevel, worldGen, fillAir);

        if (block.hasProperties && worldGen) {
            world.getChunk(position).markPosForPostprocessing(position);
        }

    }

    protected void tryBuildFancyPillarPart(LevelAccessor world, DungeonModelBlock block, BlockPos blockPos) {
        if (world.getBlockState(blockPos).canOcclude()) {
            BlockPos pos = blockPos.below(block.position.getY() + 1);
            if (!world.getBlockState(pos).canOcclude() && block.position.getY() == 0) {
                buildPillar(world, pos);
            }
        }
    }

    protected void buildPillar(LevelAccessor world, BlockPos pos) {
        for (; pos.getY() > 0; pos = pos.below()) {
            if (world.getBlockState(pos).canOcclude()) return;
            world.setBlock(pos, theme.solid.get(world, pos), 2);
        }
    }

    /**
     * Builds a pillar with stairs at the top.
     */
    protected void buildFancyPillarPart(LevelAccessor world, BlockPos pos) {
        int x = pos.getX() % 3;
        int z = pos.getZ() % 4;

        if (x == 0) {
            switch (z) {
                case 0 -> buildPillar(world, pos);
                case -2, 1 -> { // One block south from the pillar
                    BlockState stair = DungeonBlocks.applyProperty(theme.solidStairs.get(world, pos), BlockStateProperties.HORIZONTAL_FACING, Direction.NORTH);
                    stair = DungeonBlocks.applyProperty(stair, BlockStateProperties.HALF, Half.TOP);
                    world.setBlock(pos, stair, 2);
                }
                case -1, 2 -> { // Two blocks south from the pillar
                    BlockState stair = DungeonBlocks.applyProperty(theme.solidStairs.get(world, pos), BlockStateProperties.HORIZONTAL_FACING, Direction.SOUTH);
                    stair = DungeonBlocks.applyProperty(stair, BlockStateProperties.HALF, Half.TOP);
                    world.setBlock(pos, stair, 2);
                }
            }
        } else if (z == 0) {
            BlockState stair;
            if (x == 1 || x == -2) { // One block east from the pillar
                stair = DungeonBlocks.applyProperty(theme.solidStairs.get(world, pos), BlockStateProperties.HORIZONTAL_FACING, Direction.WEST);
            } else { // Two blocks east from the pillar
                stair = DungeonBlocks.applyProperty(theme.solidStairs.get(world, pos), BlockStateProperties.HORIZONTAL_FACING, Direction.EAST);
            }
            stair = DungeonBlocks.applyProperty(stair, BlockStateProperties.HALF, Half.TOP);
            world.setBlock(pos, stair, 2);
        }
    }

    protected void entrances(LevelAccessor world, BoundingBox bounds, DungeonModel model, boolean worldGen) {
        int pathStartX = (model.width - 3) / 2, pathStartZ = (model.length - 3) / 2;
        Vec3i offset = model.getOffset(rotation);
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


    protected void decorate(LevelAccessor world, BlockPos pos, int width, int height, int length, Theme theme, BoundingBox worldGenBounds, BoundingBox structureBounds,
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


    protected void placeFeatures(LevelAccessor world, BoundingBox bounds, Theme
            theme, SecondaryTheme secondaryTheme, Random rand, int stage, boolean worldGen) {
        if (features != null) {
            for (DungeonModelFeature.Instance feature : features) {
                feature.place(world, bounds, rand, theme, secondaryTheme, stage, worldGen);
            }
        }
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
        return switch (dir) {
            case 1 -> Direction.EAST;
            case 2 -> Direction.SOUTH;
            case 3 -> Direction.WEST;
            default -> Direction.NORTH;
        };
    }

    protected static DungeonModelFeature.Instance[] readAllFeatures(ListTag nbt) {
        DungeonModelFeature.Instance[] features = new DungeonModelFeature.Instance[nbt.size()];

        for (int i = 0; i < features.length; i++) {
            features[i] = DungeonModelFeature.Instance.read(nbt.getCompound(i));
        }

        return features;
    }

    protected static void writeAllFeatures(DungeonModelFeature.Instance[] positions, ListTag nbt) {
        for (DungeonModelFeature.Instance feature : positions) {
            CompoundTag position = new CompoundTag();
            feature.write(position);
            nbt.add(position);
        }
    }

}