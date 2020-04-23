package xiroc.dungeoncrawl.dungeon.piece;

import java.util.Random;

/*
 * DungeonCrawl (C) 2019 - 2020 XYROC (XIROC1337), All Rights Reserved 
 */

import java.util.Set;

import javax.annotation.Nullable;

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
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.state.properties.Half;
import net.minecraft.util.Direction;
import net.minecraft.util.Rotation;
import net.minecraft.util.Tuple;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MutableBoundingBox;
import net.minecraft.world.IWorld;
import net.minecraft.world.gen.feature.structure.IStructurePieceType;
import net.minecraft.world.gen.feature.structure.StructurePiece;
import xiroc.dungeoncrawl.DungeonCrawl;
import xiroc.dungeoncrawl.config.Config;
import xiroc.dungeoncrawl.dungeon.DungeonBuilder;
import xiroc.dungeoncrawl.dungeon.model.DungeonModel;
import xiroc.dungeoncrawl.dungeon.model.DungeonModelBlock;
import xiroc.dungeoncrawl.dungeon.model.DungeonModelBlockType;
import xiroc.dungeoncrawl.dungeon.treasure.Treasure;
import xiroc.dungeoncrawl.part.block.Spawner;
import xiroc.dungeoncrawl.part.block.WeightedRandomBlock;
import xiroc.dungeoncrawl.theme.Theme;
import xiroc.dungeoncrawl.theme.Theme.SubTheme;
import xiroc.dungeoncrawl.util.IBlockPlacementHandler;
import xiroc.dungeoncrawl.util.Position2D;
import xiroc.dungeoncrawl.util.RotationHelper;

public abstract class DungeonPiece extends StructurePiece {

	// 0 corridor
	// 1 stairs
	// 2 corridor hole
	// 3 corridor trap
	// 4 corridor room
	// 5 part
	// 6 entrance builder
	// 7 large corridor
	// 8 room
	// 9 side room
	// 10 node room
	// 11 node connector

	public static final CompoundNBT DEFAULT_NBT;

	private static final Set<Block> BLOCKS_NEEDING_POSTPROCESSING = ImmutableSet.<Block>builder()
			.add(Blocks.NETHER_BRICK_FENCE).add(Blocks.TORCH).add(Blocks.WALL_TORCH).add(Blocks.OAK_FENCE)
			.add(Blocks.SPRUCE_FENCE).add(Blocks.DARK_OAK_FENCE).add(Blocks.ACACIA_FENCE).add(Blocks.BIRCH_FENCE)
			.add(Blocks.JUNGLE_FENCE).add(Blocks.LADDER).add(Blocks.IRON_BARS).add(Blocks.STONE_BRICK_STAIRS)
			.add(Blocks.COBBLESTONE_STAIRS).add(Blocks.NETHER_BRICK_STAIRS).add(Blocks.OAK_STAIRS)
			.add(Blocks.BIRCH_STAIRS).add(Blocks.JUNGLE_STAIRS).add(Blocks.DARK_OAK_STAIRS).add(Blocks.ACACIA_STAIRS)
			.add(Blocks.SPRUCE_STAIRS).add(Blocks.PRISMARINE_STAIRS).add(Blocks.PRISMARINE_BRICK_STAIRS)
			.add(Blocks.DARK_PRISMARINE_STAIRS).add(Blocks.SANDSTONE_STAIRS).add(Blocks.SMOOTH_SANDSTONE_STAIRS)
			.add(Blocks.RED_SANDSTONE_STAIRS).add(Blocks.SMOOTH_RED_SANDSTONE_STAIRS)
			.add(Blocks.RED_NETHER_BRICK_STAIRS).add(Blocks.GRANITE_STAIRS).add(Blocks.ANDESITE_STAIRS)
			.add(Blocks.POLISHED_ANDESITE_STAIRS).add(Blocks.POLISHED_GRANITE_STAIRS)
			.add(Blocks.MOSSY_COBBLESTONE_STAIRS).add(Blocks.MOSSY_STONE_BRICK_STAIRS).add(Blocks.TRIPWIRE)
			.add(Blocks.REDSTONE_WIRE).build();

	static {
		DEFAULT_NBT = new CompoundNBT();
		DEFAULT_NBT.putBoolean("north", false);
		DEFAULT_NBT.putBoolean("east", false);
		DEFAULT_NBT.putBoolean("south", false);
		DEFAULT_NBT.putBoolean("west", false);
		DEFAULT_NBT.putBoolean("up", false);
		DEFAULT_NBT.putBoolean("down", false);
		DEFAULT_NBT.putInt("connectedSides", 0);
		DEFAULT_NBT.putInt("posX", -1);
		DEFAULT_NBT.putInt("posZ", -1);
		DEFAULT_NBT.putInt("theme", 0);
		DEFAULT_NBT.putInt("stage", -1);
		DEFAULT_NBT.putInt("rotation", 0);
	}

	public Rotation rotation;
	public int connectedSides, posX, posZ, theme, subTheme, x, y, z, stage, modelID;
	public boolean[] sides; // N-E-S-W-U-D

	public DungeonPiece(IStructurePieceType p_i51343_1_) {
		super(p_i51343_1_, DEFAULT_NBT);
		sides = new boolean[6];
		rotation = Rotation.NONE;
	}

	public DungeonPiece(IStructurePieceType p_i51343_1_, CompoundNBT p_i51343_2_) {
		super(p_i51343_1_, p_i51343_2_);
		sides = new boolean[6];
		sides[0] = p_i51343_2_.getBoolean("north");
		sides[1] = p_i51343_2_.getBoolean("east");
		sides[2] = p_i51343_2_.getBoolean("south");
		sides[3] = p_i51343_2_.getBoolean("west");
		sides[4] = p_i51343_2_.getBoolean("up");
		sides[5] = p_i51343_2_.getBoolean("down");
		connectedSides = p_i51343_2_.getInt("connectedSides");
		posX = p_i51343_2_.getInt("posX");
		posZ = p_i51343_2_.getInt("posZ");
		x = p_i51343_2_.getInt("x");
		y = p_i51343_2_.getInt("y");
		z = p_i51343_2_.getInt("z");
		theme = p_i51343_2_.getInt("theme");
		subTheme = p_i51343_2_.getInt("subTheme");
		stage = p_i51343_2_.getInt("stage");
		modelID = p_i51343_2_.getInt("model");
		rotation = RotationHelper.getRotation(p_i51343_2_.getInt("rotation"));
		setupBoundingBox();
	}

	public abstract int getType();

	/**
	 * Called during the dungeon-post-processing to determine the model that will be
	 * used to build this piece.
	 */
	public abstract int determineModel(DungeonBuilder builder, Random rand);

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

	public void setPosition(int x, int z) {
		this.posX = x;
		this.posZ = z;
	}

	public void setRealPosition(int x, int y, int z) {
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
		tagCompound.putBoolean("up", sides[4]);
		tagCompound.putBoolean("down", sides[5]);
		tagCompound.putInt("connectedSides", connectedSides);
		tagCompound.putInt("posX", posX);
		tagCompound.putInt("posZ", posZ);
		tagCompound.putInt("x", x);
		tagCompound.putInt("y", y);
		tagCompound.putInt("z", z);
		tagCompound.putInt("stage", stage);
		tagCompound.putInt("theme", theme);
		tagCompound.putInt("subTheme", subTheme);
		tagCompound.putInt("model", modelID);
		tagCompound.putInt("rotation", RotationHelper.getInt(this.rotation));
	}

	public void setBlockState(BlockState state, IWorld world, MutableBoundingBox boundsIn, Treasure.Type treasureType,
			int x, int y, int z, int theme, int lootLevel, boolean fillAir) {
		BlockPos pos = new BlockPos(x, y, z);
		if (state == null)
			return;
		if (!fillAir && world.isAirBlock(pos))
			return;
//		boundsIn.expandTo(new MutableBoundingBox(pos, pos));
		IBlockPlacementHandler.getHandler(state.getBlock()).setupBlock(world, state, pos, world.getRandom(),
				treasureType, theme, lootLevel);
		if (BLOCKS_NEEDING_POSTPROCESSING.contains(state.getBlock()))
			world.getChunk(pos).markBlockForPostprocessing(pos);
	}

	@Override
	public void setBlockState(IWorld worldIn, BlockState blockstateIn, int x, int y, int z,
			MutableBoundingBox boundingboxIn) {
		BlockPos blockpos = new BlockPos(x, y, z);
		if (boundingboxIn.isVecInside(blockpos)) {

			worldIn.setBlockState(blockpos, blockstateIn, 2);

			IFluidState ifluidstate = worldIn.getFluidState(blockpos);
			if (!ifluidstate.isEmpty()) {
				worldIn.getPendingFluidTicks().scheduleTick(blockpos, ifluidstate.getFluid(), 0);
			}

			if (BLOCKS_NEEDING_POSTPROCESSING.contains(blockstateIn.getBlock())) {
				worldIn.getChunk(blockpos).markBlockForPostprocessing(blockpos);
			}

		}
	}

	public void build(DungeonModel model, IWorld world, MutableBoundingBox boundsIn, BlockPos pos, Theme theme,
			SubTheme subTheme, Treasure.Type treasureType, int lootLevel, boolean fillAir) {

		int xStart = Math.max(boundsIn.minX, pos.getX()) - pos.getX(),
				width = Math.min(model.width, xStart + boundsIn.maxX - boundsIn.minX + 1);
		int zStart = Math.max(boundsIn.minZ, pos.getZ()) - pos.getZ(),
				length = Math.min(model.length, zStart + boundsIn.maxZ - boundsIn.minZ + 1);

		for (int x = xStart; x < width; x++) {
			for (int y = 0; y < model.height; y++) {
				for (int z = zStart; z < length; z++) {
					BlockState state;
					if (model.model[x][y][z] == null)
						state = CAVE_AIR;
					else
						state = DungeonModelBlock.getBlockState(model.model[x][y][z], theme, subTheme,
								WeightedRandomBlock.RANDOM, lootLevel); //
					if (state == null)
						continue;
					setBlockState(state, world, boundsIn, treasureType, pos.getX() + x, pos.getY() + y, pos.getZ() + z,
							this.theme, lootLevel,
							model.model[x][y][z] != null
									? fillAir || DungeonModelBlockType.SOLID_TYPES.contains(model.model[x][y][z].type)
									: false);
				}
			}
		}
	}

	public void buildRotated(DungeonModel model, IWorld world, MutableBoundingBox boundsIn, BlockPos pos, Theme theme,
			SubTheme subTheme, Treasure.Type treasureType, int lootLevel, Rotation rotation, boolean fillAir) {

		int xStart = Math.max(boundsIn.minX, pos.getX()) - pos.getX(),
				xEnd = Math.min(model.width, xStart + boundsIn.maxX - boundsIn.minX + 1);
		int zStart = Math.max(boundsIn.minZ, pos.getZ()) - pos.getZ(),
				zEnd = Math.min(model.length, zStart + boundsIn.maxZ - boundsIn.minZ + 1);

		DungeonCrawl.LOGGER.debug("buildRotated: model: {}, xStart: {}, zStart: {}, xEnd: {}, zEnd: {}", model.id,
				xStart, zStart, xEnd, zEnd);

		switch (rotation) {
		case CLOCKWISE_90: {
			for (int x = xStart; x < xEnd; x++) {
				for (int y = 0; y < model.height; y++) {
					for (int z = zStart; z < zEnd; z++) {
						BlockState state;
						if (model.model[x][y][z] == null)
							state = CAVE_AIR;
						else
							state = DungeonModelBlock.getBlockState(model.model[x][y][z], theme, subTheme,
									WeightedRandomBlock.RANDOM, lootLevel, Rotation.CLOCKWISE_90); //
						if (state == null)
							continue;
						setBlockState(state, world, boundsIn, treasureType, pos.getX() + model.length - z - 1,
								pos.getY() + y, pos.getZ() + x, this.theme, lootLevel,
								model.model[x][y][z] != null
										? DungeonModelBlockType.SOLID_TYPES.contains(model.model[x][y][z].type)
										: false);
					}
				}
			}
			break;
		}
		case COUNTERCLOCKWISE_90: {
			for (int x = xStart; x < xEnd; x++) {
				for (int y = 0; y < model.height; y++) {
					for (int z = zStart; z < zEnd; z++) {
						BlockState state;
						if (model.model[x][y][z] == null)
							state = CAVE_AIR;
						else
							state = DungeonModelBlock.getBlockState(model.model[x][y][z], theme, subTheme,
									WeightedRandomBlock.RANDOM, lootLevel, Rotation.COUNTERCLOCKWISE_90); //
						if (state == null)
							continue;
						setBlockState(state, world, boundsIn, treasureType, pos.getX() + z, pos.getY() + y,
								pos.getZ() + model.width - x - 1, this.theme, lootLevel,
								model.model[x][y][z] != null
										? DungeonModelBlockType.SOLID_TYPES.contains(model.model[x][y][z].type)
										: false);
					}
				}
			}
			break;
		}
		case CLOCKWISE_180: {
			for (int x = xStart; x < xEnd; x++) {
				for (int y = 0; y < model.height; y++) {
					for (int z = zStart; z < zEnd; z++) {
						BlockState state;
						if (model.model[x][y][z] == null)
							state = CAVE_AIR;
						else
							state = DungeonModelBlock.getBlockState(model.model[x][y][z], theme, subTheme,
									WeightedRandomBlock.RANDOM, lootLevel, Rotation.CLOCKWISE_180); //
						if (state == null)
							continue;
						setBlockState(state, world, boundsIn, treasureType, pos.getX() + model.width - x - 1,
								pos.getY() + y, pos.getZ() + model.length - z - 1, this.theme, lootLevel,
								model.model[x][y][z] != null
										? DungeonModelBlockType.SOLID_TYPES.contains(model.model[x][y][z].type)
										: false);
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

	public void entrances(IWorld world, MutableBoundingBox bounds, Theme t, DungeonModel model) {

		boolean open = model.entranceType == DungeonModel.EntranceType.OPEN;

		DungeonModelBlock stairs = new DungeonModelBlock(DungeonModelBlockType.STAIRS);
		stairs.half = Half.TOP;

		int pathStartX = (model.width - 3) / 2, pathStartZ = (model.length - 3) / 2;

		DungeonCrawl.LOGGER.debug("Building entrances for model {}. pathStartX: {}, pathStartZ: {}", model.id,
				pathStartX, pathStartZ);

		for (int i = 0; i < sides.length - 2; i++) {
			switch (Direction.byHorizontalIndex((i + 2) % 4)) {
			case EAST:
				if (open) {
					if (!sides[i])
						for (int z0 = pathStartZ; z0 < pathStartZ + 3; z0++)
							for (int y0 = 1; y0 < 4; y0++)
								setBlockState(world, t.solid.get(), x + model.width, y + y0, z + z0, bounds);
				} else {
					if (sides[i]) {
						for (int z0 = pathStartZ; z0 < pathStartZ + 3; z0++)
							for (int y0 = 1; y0 < 3; y0++)
								setBlockState(world, CAVE_AIR, x + model.width, y + y0, z + z0, bounds);
						setBlockState(world, CAVE_AIR, x + model.width, y + 3, z + pathStartZ + 1, bounds);
						stairs.facing = Direction.NORTH;
						setBlockState(world, stairs.create(t.stairs.get()), x + model.width, y + 3, z + pathStartZ,
								bounds);
						stairs.facing = Direction.SOUTH;
						setBlockState(world, stairs.create(t.stairs.get()), x + model.width, y + 3, z + pathStartZ + 2,
								bounds);
					}
				}
				continue;
			case NORTH:
				if (open) {
					if (!sides[i])
						for (int x0 = pathStartX; x0 < pathStartX + 3; x0++)
							for (int y0 = 1; y0 < 4; y0++)
								setBlockState(world, t.solid.get(), x + x0, y + y0, z, bounds);
				} else {
					if (sides[i]) {
						for (int x0 = pathStartX; x0 < pathStartX + 3; x0++)
							for (int y0 = 1; y0 < 3; y0++)
								setBlockState(world, CAVE_AIR, x + x0, y + y0, z, bounds);
						setBlockState(world, CAVE_AIR, x + pathStartX + 1, y + 3, z, bounds);
						stairs.facing = Direction.WEST;
						setBlockState(world, stairs.create(t.stairs.get()), x + pathStartX, y + 3, z, bounds);
						stairs.facing = Direction.EAST;
						setBlockState(world, stairs.create(t.stairs.get()), x + pathStartX + 2, y + 3, z, bounds);
					}
				}
				continue;
			case SOUTH:
				if (open) {
					if (!sides[i])
						for (int x0 = pathStartX; x0 < pathStartX + 3; x0++)
							for (int y0 = 1; y0 < 4; y0++)
								setBlockState(world, t.solid.get(), x + x0, y + y0, z + model.length, bounds);
				} else {
					if (sides[i]) {
						for (int x0 = pathStartX; x0 < pathStartX + 3; x0++)
							for (int y0 = 1; y0 < 3; y0++)
								setBlockState(world, CAVE_AIR, x + x0, y + y0, z + model.length, bounds);
						setBlockState(world, CAVE_AIR, x + pathStartX + 1, y + 3, z + model.length, bounds);
						stairs.facing = Direction.WEST;
						setBlockState(world, stairs.create(t.stairs.get()), x + pathStartX, y + 3, z + model.length,
								bounds);
						stairs.facing = Direction.EAST;
						setBlockState(world, stairs.create(t.stairs.get()), x + pathStartX + 2, y + 3, z + model.length,
								bounds);
					}
				}
				continue;
			case WEST:
				if (open) {
					if (!sides[i])
						for (int z0 = pathStartZ; z0 < pathStartZ + 3; z0++)
							for (int y0 = 1; y0 < 4; y0++)
								setBlockState(world, t.solid.get(), x, y + y0, z + z0, bounds);
				} else {
					if (sides[i]) {
						for (int z0 = pathStartZ; z0 < pathStartZ + 3; z0++)
							for (int y0 = 1; y0 < 3; y0++)
								setBlockState(world, CAVE_AIR, x, y + y0, z + z0, bounds);
						setBlockState(world, CAVE_AIR, x, y + 3, z + pathStartZ + 1, bounds);
						stairs.facing = Direction.NORTH;
						setBlockState(world, stairs.create(t.stairs.get()), x, y + 3, z + pathStartX, bounds);
						stairs.facing = Direction.SOUTH;
						setBlockState(world, stairs.create(t.stairs.get()), x, y + 3, z + pathStartZ + 2, bounds);
					}
				}
				continue;
			default:
				continue;

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

	public int getAirBlocks(IWorld worldIn, int x, int y, int z, int width, int length) {
		int airBlocks = 0;
		for (int i = x; i < x + width; i++) {
			for (int j = z; j < z + length; j++) {
				Block block = worldIn.getBlockState(new BlockPos(i, y, j)).getBlock();
				if (block == Blocks.AIR || block == Blocks.CAVE_AIR)
					airBlocks++;
			}
		}
		return airBlocks;
	}

	public int getBlocks(IWorld worldIn, Block type, int x, int y, int z, int width, int length) {
		int blocks = 0;
		for (int i = x; i < x + width; i++) {
			for (int j = z; j < z + length; j++) {
				Block block = worldIn.getBlockState(new BlockPos(i, y, j)).getBlock();
				if (block == type)
					blocks++;
			}
		}
		return blocks;
	}

	public void openAdditionalSides(@Nullable DungeonPiece piece) {
		if (piece != null) {
			for (int i = 0; i < piece.sides.length; i++) {
				if (!this.sides[i] && piece.sides[i])
					this.sides[i] = true;
			}
		}
	}

	/**
	 * Returns if it is possible to connect another room / corridor to this piece at
	 * the given side.
	 * 
	 * @param side The side where the connection would take place
	 */
	public boolean canConnect(Direction side) {
		return true;
	}

	/**
	 * Searches for other sides to connect to, based on the given side. Used to find
	 * an alternative connection side if canConnect(side) returned false.
	 */

	public Direction getSideForConnection(Direction base) {

		switch (base) {

		case NORTH:
			if (canConnect(Direction.EAST))
				return Direction.EAST;
			if (canConnect(Direction.WEST))
				return Direction.WEST;
			if (canConnect(Direction.SOUTH))
				return Direction.SOUTH;
			return null;

		case EAST:
			if (canConnect(Direction.NORTH))
				return Direction.NORTH;
			if (canConnect(Direction.SOUTH))
				return Direction.SOUTH;
			if (canConnect(Direction.WEST))
				return Direction.WEST;
			return null;

		case SOUTH:
			if (canConnect(Direction.EAST))
				return Direction.EAST;
			if (canConnect(Direction.WEST))
				return Direction.WEST;
			if (canConnect(Direction.NORTH))
				return Direction.NORTH;
			return null;

		case WEST:
			if (canConnect(Direction.NORTH))
				return Direction.NORTH;
			if (canConnect(Direction.SOUTH))
				return Direction.SOUTH;
			if (canConnect(Direction.EAST))
				return Direction.EAST;
			return null;

		default:
			return null;
		}

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
	 *         lead instead and the exit from which the corridor should continue
	 *         afterwards.
	 */
	public Tuple<Position2D, Position2D> getAlternativePath(Position2D current, Position2D end) {
		return null;
	}

	public static void spawnMobs(IWorld world, DungeonPiece piece, int width, int length, int[] floors) {
		for (int floor : floors) {
			for (int x = 1; x < width; x++) {
				for (int z = 1; z < length; z++) {
					BlockPos pos = new BlockPos(piece.x + x, piece.y + floor + 1, piece.z + z);
					if (piece.boundingBox.isVecInside(pos) && world.getBlockState(pos).isAir(world, pos)
							&& world.getRandom().nextDouble() < Config.MOB_SPAWN_RATE.get()) {
						EntityType<?> mob = Spawner.getRandomEntityType(world.getRandom());
						Entity entity = mob.create(world.getWorld());
						if (entity instanceof MonsterEntity) {
//							DungeonCrawl.LOGGER.debug("{} at {}, Piece is at {}|{}|{}", mob, pos, piece.x, piece.y,
//									piece.z);
							MonsterEntity mobEntity = (MonsterEntity) entity;
							mobEntity.heal(mobEntity.getMaxHealth());
							mobEntity.setLocationAndAngles(pos.getX(), pos.getY(), pos.getZ(), 0, 0);
//							DungeonCrawl.LOGGER.debug("Spawning step 2");
							Spawner.equipMonster(mobEntity, world.getRandom(), piece.stage);
							mobEntity.onInitialSpawn(world, world.getDifficultyForLocation(pos), SpawnReason.STRUCTURE,
									null, null);
							world.addEntity(mobEntity);
//							DungeonCrawl.LOGGER.debug("Spawned. {}|{}|{}", mobEntity.posX, mobEntity.posY,
//									mobEntity.posZ);

						}

					}
				}
			}
		}
//		DungeonCrawl.LOGGER.debug("Finished Spawning mobs: {}", piece);
	}

	public static Direction getOpenSide(DungeonPiece piece, int n) {
		int c = 0;
		for (int i = 0; i < 4; i++) {
			if (piece.sides[i] && c++ == n)
				return getDirectionFromInt(i);
		}
		DungeonCrawl.LOGGER.error("getOpenSide(" + piece + ", " + n
				+ ") malfunctioned. This error did most likely occur due to an error in the mod and might result in wrongly formed dungeons. ("
				+ piece.connectedSides + " open sides)");
		return Direction.NORTH;
	}

	public static Direction getDirectionFromInt(int dir) {
		switch (dir) {
		case 0:
			return Direction.NORTH;
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

	public static void addWalls(DungeonPiece piece, IWorld world, MutableBoundingBox boundsIn, int theme) {
		Theme buildTheme = Theme.get(theme);
		if (!piece.sides[0])
			for (int x = 2; x < 6; x++)
				for (int y = 2; y < 6; y++)
					piece.setBlockState(buildTheme.solid.get(), world, boundsIn, null, piece.x + x, piece.y + y,
							piece.z, theme, 0, true);
		if (!piece.sides[1])
			for (int z = 2; z < 6; z++)
				for (int y = 2; y < 6; y++)
					piece.setBlockState(buildTheme.solid.get(), world, boundsIn, null, piece.x + 7, piece.y + y,
							piece.z + z, theme, 0, true);
		if (!piece.sides[2])
			for (int x = 2; x < 6; x++)
				for (int y = 2; y < 6; y++)
					piece.setBlockState(buildTheme.solid.get(), world, boundsIn, null, piece.x + x, piece.y + y,
							piece.z + 7, theme, 0, true);
		if (!piece.sides[3])
			for (int z = 2; z < 6; z++)
				for (int y = 2; y < 6; y++)
					piece.setBlockState(buildTheme.solid.get(), world, boundsIn, null, piece.x, piece.y + y,
							piece.z + z, theme, 0, true);
	}

	public static void addColumns(DungeonPiece piece, IWorld world, MutableBoundingBox boundsIn, int ySub, int theme) {
		Theme buildTheme = Theme.get(theme);
		piece.setBlockState(
				buildTheme.stairs.get().with(BlockStateProperties.HORIZONTAL_FACING, Direction.SOUTH)
						.with(BlockStateProperties.HALF, Half.TOP).with(BlockStateProperties.WATERLOGGED, true),
				world, boundsIn, null, piece.x + 2, piece.y - ySub, piece.z + 2, theme, 0, true);
		piece.setBlockState(
				buildTheme.stairs.get().with(BlockStateProperties.HORIZONTAL_FACING, Direction.SOUTH)
						.with(BlockStateProperties.HALF, Half.TOP).with(BlockStateProperties.WATERLOGGED, true),
				world, boundsIn, null, piece.x + 3, piece.y - ySub, piece.z + 2, theme, 0, true);
		piece.setBlockState(
				buildTheme.stairs.get().with(BlockStateProperties.HORIZONTAL_FACING, Direction.SOUTH)
						.with(BlockStateProperties.HALF, Half.TOP).with(BlockStateProperties.WATERLOGGED, true),
				world, boundsIn, null, piece.x + 4, piece.y - ySub, piece.z + 2, theme, 0, true);
		piece.setBlockState(
				buildTheme.stairs.get().with(BlockStateProperties.HORIZONTAL_FACING, Direction.SOUTH)
						.with(BlockStateProperties.HALF, Half.TOP).with(BlockStateProperties.WATERLOGGED, true),
				world, boundsIn, null, piece.x + 5, piece.y - ySub, piece.z + 2, theme, 0, true);

		piece.setBlockState(
				buildTheme.stairs.get().with(BlockStateProperties.HORIZONTAL_FACING, Direction.NORTH)
						.with(BlockStateProperties.HALF, Half.TOP).with(BlockStateProperties.WATERLOGGED, true),
				world, boundsIn, null, piece.x + 2, piece.y - ySub, piece.z + 5, theme, 0, true);
		piece.setBlockState(
				buildTheme.stairs.get().with(BlockStateProperties.HORIZONTAL_FACING, Direction.NORTH)
						.with(BlockStateProperties.HALF, Half.TOP).with(BlockStateProperties.WATERLOGGED, true),
				world, boundsIn, null, piece.x + 3, piece.y - ySub, piece.z + 5, theme, 0, true);
		piece.setBlockState(
				buildTheme.stairs.get().with(BlockStateProperties.HORIZONTAL_FACING, Direction.NORTH)
						.with(BlockStateProperties.HALF, Half.TOP).with(BlockStateProperties.WATERLOGGED, true),
				world, boundsIn, null, piece.x + 4, piece.y - ySub, piece.z + 5, theme, 0, true);
		piece.setBlockState(
				buildTheme.stairs.get().with(BlockStateProperties.HORIZONTAL_FACING, Direction.NORTH)
						.with(BlockStateProperties.HALF, Half.TOP).with(BlockStateProperties.WATERLOGGED, true),
				world, boundsIn, null, piece.x + 5, piece.y - ySub, piece.z + 5, theme, 0, true);

		piece.setBlockState(
				buildTheme.stairs.get().with(BlockStateProperties.HORIZONTAL_FACING, Direction.EAST)
						.with(BlockStateProperties.HALF, Half.TOP).with(BlockStateProperties.WATERLOGGED, true),
				world, boundsIn, null, piece.x + 2, piece.y - ySub, piece.z + 3, theme, 0, true);
		piece.setBlockState(
				buildTheme.stairs.get().with(BlockStateProperties.HORIZONTAL_FACING, Direction.EAST)
						.with(BlockStateProperties.HALF, Half.TOP).with(BlockStateProperties.WATERLOGGED, true),
				world, boundsIn, null, piece.x + 2, piece.y - ySub, piece.z + 4, theme, 0, true);

		piece.setBlockState(
				buildTheme.stairs.get().with(BlockStateProperties.HORIZONTAL_FACING, Direction.WEST)
						.with(BlockStateProperties.HALF, Half.TOP).with(BlockStateProperties.WATERLOGGED, true),
				world, boundsIn, null, piece.x + 5, piece.y - ySub, piece.z + 3, theme, 0, true);
		piece.setBlockState(
				buildTheme.stairs.get().with(BlockStateProperties.HORIZONTAL_FACING, Direction.WEST)
						.with(BlockStateProperties.HALF, Half.TOP).with(BlockStateProperties.WATERLOGGED, true),
				world, boundsIn, null, piece.x + 5, piece.y - ySub, piece.z + 4, theme, 0, true);

		for (int x = 3; x < 5; x++) {
			for (int z = 3; z < 5; z++) {
				int groundHeight = getGroudHeightFrom(world, piece.x + x, piece.z + z, piece.y - ySub);
				for (int y = piece.y - ySub; y > groundHeight; y--)
					piece.setBlockState(buildTheme.column.get(), world, boundsIn, null, piece.x + x, y, piece.z + z,
							theme, 0, true);
			}
		}

	}

	public static BlockPos shiftPosition(BlockPos start, Direction direction, int amount) {
		switch (direction) {
		case DOWN:
			return new BlockPos(start.getX(), start.getY() - amount, start.getZ());
		case EAST:
			return new BlockPos(start.getX() + amount, start.getY(), start.getZ());
		case NORTH:
			return new BlockPos(start.getX(), start.getY(), start.getZ() - amount);
		case SOUTH:
			return new BlockPos(start.getX(), start.getY(), start.getZ() + amount);
		case WEST:
			return new BlockPos(start.getX() - amount, start.getY(), start.getZ());
		case UP:
			return new BlockPos(start.getX(), start.getY() + amount, start.getZ());
		default:
			return start;
		}
	}

	public static int getGroudHeightFrom(IWorld world, int x, int z, int yStart) {
		for (int y = yStart; y > 0; y--)
			if (world.getBlockState(new BlockPos(x, y, z)).isSolid())
				return y;
		return 0;
	}

}