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
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnReason;
import net.minecraft.entity.monster.MonsterEntity;
import net.minecraft.fluid.FluidState;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.tileentity.LockableLootTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Rotation;
import net.minecraft.util.Tuple;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MutableBoundingBox;
import net.minecraft.util.math.vector.Vector3i;
import net.minecraft.world.ISeedReader;
import net.minecraft.world.IWorld;
import net.minecraft.world.gen.feature.structure.IStructurePieceType;
import net.minecraft.world.gen.feature.structure.StructurePiece;
import xiroc.dungeoncrawl.DungeonCrawl;
import xiroc.dungeoncrawl.config.Config;
import xiroc.dungeoncrawl.dungeon.DungeonBuilder;
import xiroc.dungeoncrawl.dungeon.PlacementContext;
import xiroc.dungeoncrawl.dungeon.block.Spawner;
import xiroc.dungeoncrawl.dungeon.block.WeightedRandomBlock;
import xiroc.dungeoncrawl.dungeon.decoration.IDungeonDecoration;
import xiroc.dungeoncrawl.dungeon.model.DungeonModel;
import xiroc.dungeoncrawl.dungeon.model.DungeonModelBlock;
import xiroc.dungeoncrawl.dungeon.model.DungeonModelBlockType;
import xiroc.dungeoncrawl.dungeon.model.DungeonModelFeature;
import xiroc.dungeoncrawl.dungeon.model.DungeonModels;
import xiroc.dungeoncrawl.dungeon.model.ModelSelector;
import xiroc.dungeoncrawl.dungeon.model.MultipartModelData;
import xiroc.dungeoncrawl.dungeon.monster.RandomMonster;
import xiroc.dungeoncrawl.dungeon.treasure.Loot;
import xiroc.dungeoncrawl.theme.Theme;
import xiroc.dungeoncrawl.theme.Theme.SecondaryTheme;
import xiroc.dungeoncrawl.util.DirectionalBlockPos;
import xiroc.dungeoncrawl.util.IBlockPlacementHandler;
import xiroc.dungeoncrawl.util.Orientation;
import xiroc.dungeoncrawl.util.Position2D;

import java.util.List;
import java.util.Random;

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

//    private static final Set<Block> BLOCKS_NEEDING_POSTPROCESSING = ImmutableSet.<Block>builder().add(Blocks.IRON_BARS).build();

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
    public boolean[] sides; // N-E-S-W
    public DirectionalBlockPos[] featurePositions;
    public byte[] variation;

    public DungeonModel model;
    public Theme theme;
    public SecondaryTheme secondaryTheme;

    public Position2D gridPosition;

    public final PlacementContext context;

    public DungeonPiece(IStructurePieceType p_i51343_1_) {
        super(p_i51343_1_, DEFAULT_NBT);
        this.sides = new boolean[4];
        this.rotation = Rotation.NONE;
        this.context = new PlacementContext();
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
            this.secondaryTheme = Theme.getSubThemeByID(p_i51343_2_.getInt("subTheme"));
        } else {
            this.secondaryTheme = Theme.getSecondaryTheme(new ResourceLocation(p_i51343_2_.getString("secondaryTheme")));
        }

        if (p_i51343_2_.contains("model", 99)) {
            this.model = DungeonModels.ID_TO_MODEL.get(p_i51343_2_.getInt("model"));
        } else {
            this.model = DungeonModels.KEY_TO_MODEL.get(new ResourceLocation(p_i51343_2_.getString("model")));
        }

        if (p_i51343_2_.contains("featurePositions", 9)) {
            this.featurePositions = readAllPositions(p_i51343_2_.getList("featurePositions", 10));
        }

        if (p_i51343_2_.contains("variation")) {
            this.variation = p_i51343_2_.getByteArray("variation");
        }

        this.context = new PlacementContext();

        setupBoundingBox();
    }

    @Override
    public void readAdditional(CompoundNBT tagCompound) {
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
        if (featurePositions != null) {
            ListNBT list = new ListNBT();
            writeAllPositions(featurePositions, list);
            tagCompound.put("featurePositions", list);
        }
        if (variation != null) {
            tagCompound.putByteArray("variation", variation);
        }
    }

    public abstract int getType();

    /**
     * Called during the dungeon-post-processing to determine the model that will be
     * used to build this piece.
     */
    public abstract void setupModel(DungeonBuilder builder, ModelSelector modelSelector, List<DungeonPiece> pieces, Random rand);

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
        if (model.metadata != null && model.metadata.featureMetadata != null && model.featurePositions != null && model.featurePositions.length > 0) {
            Vector3i offset = model.getOffset(rotation);
            DungeonModelFeature.setup(this, model, model.featurePositions, rotation, rand, model.metadata.featureMetadata,
                    x + offset.getX(), y + offset.getY(), z + offset.getZ());
        }
    }

    public void takeOverProperties(DungeonPiece piece) {
        this.sides = piece.sides;
        this.connectedSides = piece.connectedSides;
        this.rotation = piece.rotation;
    }

    public boolean hasChildPieces() {
        return model != null && model.multipartData != null;
    }

    public void addChildPieces(List<DungeonPiece> pieces, DungeonBuilder builder, ModelSelector modelSelector, int layer, Random rand) {
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

    public void setBlockState(BlockState state, IWorld world, BlockPos pos,
                              Theme theme, SecondaryTheme secondaryTheme, int lootLevel,
                              DungeonModelBlock block, DungeonModelBlockType type,
                              PlacementContext context) {
        if (state == null)
            return;

        if (DungeonBuilder.isBlockProtected(world, pos, context)
                || world.isAirBlock(pos) && !type.isSolid(world, pos, WeightedRandomBlock.RANDOM, 0, 0, 0)) {
            return;
        }

        Random rand = world.getRandom();

        IBlockPlacementHandler.getHandler(state.getBlock()).place(world, state, pos, rand,
                context, theme, secondaryTheme, lootLevel);

        TileEntity tile = world.getTileEntity(pos);
        if (tile instanceof LockableLootTileEntity) {
            if (block.lootTable != null) {
                Loot.setLoot((LockableLootTileEntity) tile, block.lootTable, theme, secondaryTheme, rand);
            } else {
                Loot.setLoot((LockableLootTileEntity) tile, Loot.getLootTable(stage, rand), theme, secondaryTheme, rand);
            }
        }

        FluidState ifluidstate = world.getFluidState(pos);
        if (ifluidstate.isSource()) {
            world.getPendingFluidTicks().scheduleTick(pos, ifluidstate.getFluid(), 0);
        }
    }

    public static void setBlockState(IWorld worldIn, BlockState blockstateIn,
                                     MutableBoundingBox boundingboxIn, BlockPos blockPos) {
        if (boundingboxIn.isVecInside(blockPos)) {

            worldIn.setBlockState(blockPos, blockstateIn, 2);

            FluidState ifluidstate = worldIn.getFluidState(blockPos);
            if (ifluidstate.isSource()) {
                worldIn.getPendingFluidTicks().scheduleTick(blockPos, ifluidstate.getFluid(), 0);
            }

//            if (BLOCKS_NEEDING_POSTPROCESSING.contains(blockstateIn.getBlock())) {
//                worldIn.getChunk(blockPos).markBlockForPostprocessing(blockPos);
//            }
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
                                  MutableBoundingBox boundingboxIn, PlacementContext context) {
        BlockPos blockPos = new BlockPos(x, y, z);
        if (boundingboxIn.isVecInside(blockPos)) {

            if (DungeonBuilder.isBlockProtected(worldIn, blockPos, context) || worldIn.isAirBlock(blockPos))
                return;

            worldIn.setBlockState(blockPos, blockstateIn, 2);
            if (context.postProcessing) {
                worldIn.getChunk(blockPos).markBlockForPostprocessing(blockPos);
            }

            FluidState fluidstate = worldIn.getFluidState(blockPos);
            if (fluidstate.isSource()) {
                worldIn.getPendingFluidTicks().scheduleTick(blockPos, fluidstate.getFluid(), 0);
            }

        }
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
            }
        });

        if (Config.EXTENDED_DEBUG.get()) {
            DungeonCrawl.LOGGER.debug("Finished building {} with model id {} at ({} | {} | {})", model.getKey(), model.id, pos.getX(), pos.getY(), pos.getZ());
        }
    }

    public void buildRotated(DungeonModel model, IWorld world, MutableBoundingBox boundsIn, BlockPos pos, Theme theme,
                             SecondaryTheme secondaryTheme, int lootLevel, Rotation rotation, PlacementContext context,
                             boolean fillAir) {
        if (Config.EXTENDED_DEBUG.get()) {
            DungeonCrawl.LOGGER.debug("Building {} with model id {} and rotation {} at ({} | {} | {})", model.getKey(), model.id, rotation, pos.getX(), pos.getY(), pos.getZ());
        }

        switch (rotation) {
            case CLOCKWISE_90: {
                model.blocks.forEach((block) -> {
                    BlockPos position = new BlockPos(
                            pos.getX() + model.length - block.position.getZ() - 1,
                            pos.getY() + block.position.getY(),
                            pos.getZ() + block.position.getX());

                    if (boundsIn.isVecInside(position)) {
                        Tuple<BlockState, Boolean> state = DungeonModelBlock.getBlockState(block,
                                Rotation.CLOCKWISE_90, world, position, theme, secondaryTheme, WeightedRandomBlock.RANDOM, variation, lootLevel);

                        if (state == null)
                            return;

                        placeBlock(block, world, context, theme, secondaryTheme, lootLevel, fillAir, position, state);
                    }
                });
                break;
            }
            case COUNTERCLOCKWISE_90: {
                model.blocks.forEach((block) -> {
                    BlockPos position = new BlockPos(
                            pos.getX() + block.position.getZ(),
                            pos.getY() + block.position.getY(),
                            pos.getZ() + model.width - block.position.getX() - 1);

                    if (boundsIn.isVecInside(position)) {
                        Tuple<BlockState, Boolean> state = DungeonModelBlock.getBlockState(block,
                                Rotation.COUNTERCLOCKWISE_90, world, position, theme, secondaryTheme,
                                WeightedRandomBlock.RANDOM, variation, lootLevel);

                        if (state == null)
                            return;

                        placeBlock(block, world, context, theme, secondaryTheme, lootLevel, fillAir, position, state);

                    }
                });
                break;
            }
            case CLOCKWISE_180: {
                model.blocks.forEach((block) -> {
                    BlockPos position = new BlockPos(
                            pos.getX() + model.width - block.position.getX() - 1,
                            pos.getY() + block.position.getY(),
                            pos.getZ() + model.length - block.position.getZ() - 1);

                    if (boundsIn.isVecInside(position)) {
                        Tuple<BlockState, Boolean> state = DungeonModelBlock.getBlockState(block, Rotation.CLOCKWISE_180, world,
                                position, theme, secondaryTheme, WeightedRandomBlock.RANDOM, variation, lootLevel);

                        if (state == null)
                            return;

                        placeBlock(block, world, context, theme, secondaryTheme, lootLevel, fillAir, position, state);

                    }
                });
                break;
            }
            case NONE:
                build(model, world, boundsIn, pos, theme, secondaryTheme, lootLevel, context, fillAir);
                break;
            default:
                DungeonCrawl.LOGGER.warn("Failed to build a rotated dungeon segment: Unsupported rotation " + rotation);
                break;
        }

        if (Config.EXTENDED_DEBUG.get()) {
            DungeonCrawl.LOGGER.debug("Finished building {} with model id {} and rotation {} at ({} | {} | {})", model.getKey(), model.id, rotation, pos.getX(), pos.getY(), pos.getZ());
        }
    }

    public void placeBlock(DungeonModelBlock block, IWorld world, PlacementContext context,
                           Theme theme, SecondaryTheme secondaryTheme, int lootLevel, boolean fillAir,
                           BlockPos position, Tuple<BlockState, Boolean> state) {
        setBlockState(state.getA(), world, position, theme, secondaryTheme, lootLevel, block,
                fillAir ? DungeonModelBlockType.SOLID : block.type, context);

        if (state.getB() && context.postProcessing) {
            world.getChunk(position).markBlockForPostprocessing(position);
        }

    }

    /**
     * Builds a 1x1 pillar to the ground
     */
    public void buildPillar(IWorld world, Theme theme, int x, int y, int z, MutableBoundingBox bounds) {
        int height = getGroundHeightFrom(world, x, y - 1, z);
        for (; y > height; y--) {
            BlockPos pos = new BlockPos(x, y, z);
            world.setBlockState(pos, theme.solid.get(pos), 2);
        }
    }

    public static int getGroundHeightFrom(IWorld world, int x, int y, int z) {
        for (; y > 0; y--)
            if (world.getBlockState(new BlockPos(x, y, z)).isSolid())
                return y;
        return 0;
    }

    public void entrances(ISeedReader world, MutableBoundingBox bounds, DungeonModel model) {
        int pathStartX = (model.width - 3) / 2, pathStartZ = (model.length - 3) / 2;
        Vector3i offset = model.getOffset(rotation);
        BlockPos pos = new BlockPos(x + offset.getX(), y, z + offset.getZ()); // Ignore the y offset

        if (sides[0]) {
            for (int x0 = pathStartX; x0 < pathStartX + 3; x0++)
                for (int y0 = 1; y0 < 4; y0++)
                    replaceBlockState(world, CAVE_AIR, pos.getX() + x0, pos.getY() + y0, pos.getZ(), bounds, context);
        }
        if (sides[1]) {
            for (int z0 = pathStartZ; z0 < pathStartZ + 3; z0++)
                for (int y0 = 1; y0 < 4; y0++)
                    replaceBlockState(world, CAVE_AIR, pos.getX() + model.width - 1, pos.getY() + y0, pos.getZ() + z0, bounds, context);
        }
        if (sides[2]) {
            for (int x0 = pathStartX; x0 < pathStartX + 3; x0++)
                for (int y0 = 1; y0 < 4; y0++)
                    replaceBlockState(world, CAVE_AIR, pos.getX() + x0, pos.getY() + y0, pos.getZ() + model.length - 1, bounds, context);
        }
        if (sides[3]) {
            for (int z0 = pathStartZ; z0 < pathStartZ + 3; z0++)
                for (int y0 = 1; y0 < 4; y0++)
                    replaceBlockState(world, CAVE_AIR, pos.getX(), pos.getY() + y0, pos.getZ() + z0, bounds, context);
        }

    }

    public void decorate(IWorld world, BlockPos pos, PlacementContext context, int width, int height, int length, Theme theme, MutableBoundingBox worldGenBounds, MutableBoundingBox structureBounds, DungeonModel model) {
        if (theme.hasDecorations()) {
            for (IDungeonDecoration decoration : theme.getDecorations()) {
                if (Config.EXTENDED_DEBUG.get()) {
                    DungeonCrawl.LOGGER.debug("Running decoration {} for {} ({}) at ({} | {} | {})", decoration.toString(), model.getKey(), model.id, pos.getX(), pos.getY(), pos.getZ());
                }

                decoration.decorate(model, world, pos, context, width, height, length, worldGenBounds, structureBounds, this, stage);

                if (Config.EXTENDED_DEBUG.get()) {
                    DungeonCrawl.LOGGER.debug("Finished decoration {} for {} ({}) at ({} | {} | {})", decoration.toString(), model.getKey(), model.id, pos.getX(), pos.getY(), pos.getZ());
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
     * the given side.
     *
     * @param side The side where the connection would take place
     * @param x    the x coordinate in the grid of the connecting piece
     * @param z    the z coordinate in the grid of the connecting piece
     */
    public boolean canConnect(Direction side, int x, int z) {
        return true;
    }

    /**
     * Used for corridor building. This method states if a corridor that would
     * intersect with this piece could use an alternative path provided by this
     * piece.
     */
    public boolean hasAlternativePath() {
        return false;
    }

    /**
     * Returns the data for an alternative corridor path, based on the given
     * positions.
     *
     * @param current The current position where the corridor builder is at.
     * @return A tuple with the entrance to this piece to where the corridor should
     * lead instead and the exit from which the corridor should continue
     * afterwards.
     */
    public Tuple<Position2D, Position2D> getAlternativePath(Position2D current, Position2D end) {
        return null;
    }

    public static void spawnMobs(ISeedReader world, DungeonPiece piece, int width, int length, int[] floors) {
        for (int floor : floors) {
            for (int x = 1; x < width; x++) {
                for (int z = 1; z < length; z++) {
                    BlockPos pos = new BlockPos(piece.x + x, piece.y + floor + 1, piece.z + z);
                    if (piece.boundingBox.isVecInside(pos) && world.isAirBlock(pos)
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

}