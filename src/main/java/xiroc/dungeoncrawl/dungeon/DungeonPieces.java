package xiroc.dungeoncrawl.dungeon;

/*
 * DungeonCrawl (C) 2019 XYROC (XIROC1337), All Rights Reserved 
 */

import java.util.Random;
import java.util.Set;

import com.google.common.collect.ImmutableSet;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.state.properties.Half;
import net.minecraft.util.Direction;
import net.minecraft.util.Rotation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.MutableBoundingBox;
import net.minecraft.world.IWorld;
import net.minecraft.world.gen.feature.structure.IStructurePieceType;
import net.minecraft.world.gen.feature.structure.StructurePiece;
import net.minecraft.world.gen.feature.template.TemplateManager;
import xiroc.dungeoncrawl.DungeonCrawl;
import xiroc.dungeoncrawl.dungeon.segment.DungeonSegmentModel;
import xiroc.dungeoncrawl.dungeon.segment.DungeonSegmentModelBlock;
import xiroc.dungeoncrawl.dungeon.segment.DungeonSegmentModelRegistry;
import xiroc.dungeoncrawl.theme.Theme;
import xiroc.dungeoncrawl.util.IBlockPlacementHandler;
import xiroc.dungeoncrawl.util.RotationHelper;

public class DungeonPieces {

//	  ID 	 PIECE 
//	   0	 Corridor 
//	   1	 StairsBot 
//	   2	 Stairs 
//	   3 	 StairsTop 
//	   4	 Hole
//	   5	 Room
//     6	 Corridor Trap
//     7 	 Corridor Room
//	   8	 Hole Trap
//	  11	 EntranceBuilder 
//	  12	 Part
//	  99	 RoomLargePlaceholder

	public static final CompoundNBT DEFAULT_NBT = getDefaultNBT();

	private static final Set<Block> BLOCKS_NEEDING_POSTPROCESSING = ImmutableSet.<Block>builder()
			.add(Blocks.NETHER_BRICK_FENCE).add(Blocks.TORCH).add(Blocks.WALL_TORCH).add(Blocks.OAK_FENCE)
			.add(Blocks.SPRUCE_FENCE).add(Blocks.DARK_OAK_FENCE).add(Blocks.ACACIA_FENCE).add(Blocks.BIRCH_FENCE)
			.add(Blocks.JUNGLE_FENCE).add(Blocks.LADDER).add(Blocks.IRON_BARS).add(Blocks.STONE_BRICK_STAIRS)
			.add(Blocks.COBBLESTONE_STAIRS).add(Blocks.NETHER_BRICK_STAIRS).add(Blocks.OAK_STAIRS)
			.add(Blocks.BIRCH_STAIRS).add(Blocks.JUNGLE_STAIRS).add(Blocks.DARK_OAK_STAIRS).add(Blocks.ACACIA_STAIRS)
			.add(Blocks.SPRUCE_STAIRS).add(Blocks.PRISMARINE_STAIRS).add(Blocks.PRISMARINE_BRICK_STAIRS)
			.add(Blocks.DARK_PRISMARINE_STAIRS).add(Blocks.SANDSTONE_STAIRS).add(Blocks.SMOOTH_SANDSTONE_STAIRS)
			.add(Blocks.RED_SANDSTONE_STAIRS).add(Blocks.SMOOTH_RED_SANDSTONE_STAIRS).add(Blocks.TRIPWIRE)
			.add(Blocks.REDSTONE_WIRE).build();

	public static CompoundNBT getDefaultNBT() {
		CompoundNBT nbt = new CompoundNBT();
		nbt.putBoolean("north", false);
		nbt.putBoolean("east", false);
		nbt.putBoolean("south", false);
		nbt.putBoolean("west", false);
		nbt.putBoolean("up", false);
		nbt.putBoolean("down", false);
		nbt.putInt("connectedSides", 0);
		nbt.putInt("posX", -1);
		nbt.putInt("posZ", -1);
		nbt.putInt("theme", 0);
		nbt.putInt("stage", -1);
		nbt.putInt("rotation", 0);
		return nbt;
	}

	public static class Part extends DungeonPiece {

		private int modelID, startX, startY, startZ, width, height, length;

		public Part(TemplateManager manager, CompoundNBT p_i51343_2_) {
			super(Dungeon.PART, p_i51343_2_);
			modelID = p_i51343_2_.getInt("model");
			startX = p_i51343_2_.getInt("startX");
			startY = p_i51343_2_.getInt("startY");
			startZ = p_i51343_2_.getInt("startZ");
			width = p_i51343_2_.getInt("width");
			height = p_i51343_2_.getInt("height");
			length = p_i51343_2_.getInt("length");
		}

		public void set(int modelID, int startX, int startY, int startZ, int width, int height, int length) {
			this.modelID = modelID;
			this.startX = startX;
			this.startY = startY;
			this.startZ = startZ;
			this.width = width;
			this.height = height;
			this.length = length;
		}

		public void adjustSize() {
			DungeonSegmentModel model = DungeonSegmentModelRegistry.MAP.get(modelID);
			if (model == null) {
				DungeonCrawl.LOGGER.warn("Failed to adjust the size of a dungeon part. ID: {}", modelID);
				return;
			}
			width = startX + width > model.width ? width - (startX + width - model.width) : width;
			height = startY + height > model.height ? height - (startY + height - model.height) : height;
			length = startZ + length > model.length ? length - (startZ + length - model.length) : length;
		}

		@Override
		public int getType() {
			return 12;
		}

		@Override
		public boolean addComponentParts(IWorld worldIn, Random randomIn, MutableBoundingBox structureBoundingBoxIn,
				ChunkPos p_74875_4_) {
			this.adjustSize();
			if (theme != 1)
				theme = Theme.BIOME_TO_THEME_MAP
						.getOrDefault(worldIn.getBiome(new BlockPos(x, y, z)).getRegistryName().toString(), 0);
			DungeonSegmentModel model = DungeonSegmentModelRegistry.MAP.get(modelID);
			BlockPos pos = new BlockPos(x, y, z);
			for (int x = startX; x < startX + width; x++) {
				for (int y = startY; y < startY + height; y++) {
					for (int z = startZ; z < startZ + length; z++) {
						BlockState state;
						if (model.model[x][y][z] == null)
							state = Blocks.AIR.getDefaultState();
						else
							state = DungeonSegmentModelBlock.getBlockState(model.model[x][y][z], Theme.get(theme),
									worldIn.getRandom());
						if (state == null)
							continue;
						setBlockState(state, worldIn, pos.getX() + x - startX, pos.getY() + y - startY,
								pos.getZ() + z - startZ, theme, stage);
					}
				}
			}
			addWalls(this, worldIn, theme);
			if (theme == 3 && getBlocks(worldIn, Blocks.WATER, x, y - 1, z, 8, 8) > 5)
				addColumns(this, worldIn, 1, theme);

			return true;
		}

		@Override
		public void readAdditional(CompoundNBT tagCompound) {
			super.readAdditional(tagCompound);
			tagCompound.putInt("model", modelID);
			tagCompound.putInt("startX", startX);
			tagCompound.putInt("startY", startY);
			tagCompound.putInt("startZ", startZ);
			tagCompound.putInt("width", width);
			tagCompound.putInt("height", height);
			tagCompound.putInt("length", length);
		}

	}

	public static class HoleTrap extends DungeonPiece {

		public HoleTrap(TemplateManager manager, CompoundNBT p_i51343_2_) {
			super(Dungeon.HOLE_TRAP, p_i51343_2_);
		}

		@Override
		public boolean addComponentParts(IWorld worldIn, Random randomIn, MutableBoundingBox structureBoundingBoxIn,
				ChunkPos p_74875_4_) {
			if (theme != 1)
				theme = Theme.BIOME_TO_THEME_MAP
						.getOrDefault(worldIn.getBiome(new BlockPos(x, y, z)).getRegistryName().toString(), 0);
			buildRotated(null, worldIn, new BlockPos(x, y, z), Theme.get(theme), stage, rotation); // TODO model
			return true;
		}

		@Override
		public int getType() {
			return 8;
		}

	}

	public static class Room extends DungeonPiece {

		public Room(TemplateManager manager, CompoundNBT p_i51343_2_) {
			super(Dungeon.ROOM, p_i51343_2_);
		}

		@Override
		public boolean addComponentParts(IWorld worldIn, Random randomIn, MutableBoundingBox structureBoundingBoxIn,
				ChunkPos p_74875_4_) {
			DungeonSegmentModel model = DungeonBuilder.getModel(this, randomIn);
			if (model == null)
				return false;
			if (theme != 1)
				theme = Theme.BIOME_TO_THEME_MAP
						.getOrDefault(worldIn.getBiome(new BlockPos(x, y, z)).getRegistryName().toString(), 0);
			build(model, worldIn, new BlockPos(x, y, z), Theme.get(theme), stage);
			addWalls(this, worldIn, theme);
			if (theme == 3 && getBlocks(worldIn, Blocks.WATER, x, y - 1, z, 8, 8) > 5)
				addColumns(this, worldIn, 1, theme);
			return false;
		}

		@Override
		public int getType() {
			return 5;
		}

	}

	public static class Corridor extends DungeonPiece {

		public Corridor(TemplateManager manager, CompoundNBT p_i51343_2_) {
			super(Dungeon.CORRIDOR, p_i51343_2_);
		}

		@Override
		public boolean addComponentParts(IWorld worldIn, Random randomIn, MutableBoundingBox structureBoundingBoxIn,
				ChunkPos p_74875_4_) {
			if (theme != 1)
				theme = Theme.BIOME_TO_THEME_MAP
						.getOrDefault(worldIn.getBiome(new BlockPos(x, y, z)).getRegistryName().toString(), 0);
			if (theme != 3 && getAirBlocks(worldIn, x, y, z, 8, 8) > 8) {
//				DungeonCrawl.LOGGER.info("Building a bridge piece at {} {} {}", x, y, z);
				boolean ew = rotation == Rotation.NONE || rotation == Rotation.CLOCKWISE_180;
				switch (connectedSides) {
				case 2:
					if (sides[0] && sides[2] || sides[1] && sides[3])
						buildRotated(DungeonSegmentModelRegistry.BRIDGE, worldIn,
								new BlockPos(ew ? x : x + 1, y - 1, ew ? z + 1 : z), Theme.get(theme), stage, rotation);
					else
						buildRotated(DungeonSegmentModelRegistry.BRIDGE_TURN, worldIn, new BlockPos(x, y - 1, z),
								Theme.get(theme), stage, rotation);
					return true;
				case 3:
					buildRotated(DungeonSegmentModelRegistry.BRIDGE_SIDE, worldIn, new BlockPos(x, y - 1, z),
							Theme.get(theme), stage, rotation);
					return true;
				case 4:
					buildRotated(DungeonSegmentModelRegistry.BRIDGE_ALL_SIDES, worldIn, new BlockPos(x, y - 1, z),
							Theme.get(theme), stage, rotation);
					return true;
				}
				return true;
			}
			DungeonSegmentModel model = DungeonBuilder.getModel(this, randomIn);
			if (model == null)
				return false;
			buildRotated(model, worldIn, new BlockPos(x, y, z), Theme.get(theme), stage, getRotation());
			if (theme == 3
					&& ((connectedSides == 2 && (!(sides[0] && sides[2] || sides[1] && sides[3]) || randomIn.nextDouble() < 0.2)) || connectedSides > 2)
					&& getBlocks(worldIn, Blocks.WATER, x, y - 1, z, 8, 8) > 5)
				addColumns(this, worldIn, 1, theme);
			return true;
		}

		@Override
		public int getType() {
			return 0;
		}

	}

	public static class CorridorRoom extends DungeonPiece {

		public CorridorRoom(TemplateManager manager, CompoundNBT p_i51343_2_) {
			super(Dungeon.CORRIDOR_ROOM, p_i51343_2_);
		}

		@Override
		public int getType() {
			return 7;
		}

		@Override
		public boolean addComponentParts(IWorld worldIn, Random randomIn, MutableBoundingBox structureBoundingBoxIn,
				ChunkPos p_74875_4_) {
			if (theme != 1)
				theme = Theme.BIOME_TO_THEME_MAP
						.getOrDefault(worldIn.getBiome(new BlockPos(x, y, z)).getRegistryName().toString(), 0);
			buildRotated(DungeonSegmentModelRegistry.CORRIDOR_ROOM, worldIn, new BlockPos(x, y - 6, z),
					Theme.get(theme), stage, getRotation());
			if (theme == 3 && getBlocks(worldIn, Blocks.WATER, x, y - 7, z, 8, 8) > 5)
				addColumns(this, worldIn, 7, theme);
			return true;
		}

	}

	public static class CorridorTrap extends DungeonPiece {

		public CorridorTrap(TemplateManager manager, CompoundNBT p_i51343_2_) {
			super(Dungeon.CORRIDOR_TRAP, p_i51343_2_);
		}

		@Override
		public int getType() {
			return 6;
		}

		@Override
		public boolean addComponentParts(IWorld worldIn, Random randomIn, MutableBoundingBox structureBoundingBoxIn,
				ChunkPos p_74875_4_) {
			if (theme != 1)
				theme = Theme.BIOME_TO_THEME_MAP
						.getOrDefault(worldIn.getBiome(new BlockPos(x, y, z)).getRegistryName().toString(), 0);
			buildRotated(DungeonSegmentModelRegistry.CORRIDOR_TRAP, worldIn, new BlockPos(x, y, z), Theme.get(theme),
					stage, getRotation());
			return true;
		}

	}

	public static class Hole extends DungeonPiece {

		boolean lava;

		public Hole(TemplateManager p_i51343_1_, CompoundNBT p_i51343_2_) {
			super(Dungeon.HOLE, p_i51343_2_);
			lava = p_i51343_2_.getBoolean("lava");
		}

		@Override
		public boolean addComponentParts(IWorld worldIn, Random randomIn, MutableBoundingBox structureBoundingBoxIn,
				ChunkPos p_74875_4_) {
			if (theme != 1)
				theme = Theme.BIOME_TO_THEME_MAP
						.getOrDefault(worldIn.getBiome(new BlockPos(x, y, z)).getRegistryName().toString(), 0);
			build(lava ? DungeonSegmentModelRegistry.HOLE_LAVA : DungeonSegmentModelRegistry.HOLE, worldIn,
					new BlockPos(x, y - 15, z), Theme.get(theme), stage);
			addWalls(this, worldIn, theme);
			if (theme == 3 && getBlocks(worldIn, Blocks.WATER, x, y - 16, z, 8, 8) > 5)
				addColumns(this, worldIn, 16, theme);
			return false;
		}

		@Override
		public int getType() {
			return 4;
		}

		@Override
		public void readAdditional(CompoundNBT tagCompound) {
			super.readAdditional(tagCompound);
			tagCompound.putBoolean("lava", lava);
		}

	}

	public static class EntranceBuilder extends DungeonPiece {

		public EntranceBuilder(TemplateManager manager, CompoundNBT p_i51343_2_) {
			super(Dungeon.ENTRANCE_BUILDER, p_i51343_2_);
		}

		@Override
		public int getType() {
			return 11;
		}

		@Override
		public boolean addComponentParts(IWorld worldIn, Random randomIn, MutableBoundingBox structureBoundingBoxIn,
				ChunkPos p_74875_4_) {
			if (theme != 1)
				theme = Theme.BIOME_TO_THEME_MAP
						.getOrDefault(worldIn.getBiome(new BlockPos(x, y, z)).getRegistryName().toString(), 0);
			int height = getGroudHeight(worldIn, x + 4, z + 4);
			int ch = y;
			Theme buildTheme = Theme.get(theme);
			while (ch < height) {
				build(DungeonSegmentModelRegistry.STAIRS, worldIn, new BlockPos(x, ch, z), buildTheme, stage);
				for (int x1 = 0; x1 < 8; x1++)
					for (int y1 = 0; y1 < 8; y1++)
						setBlockState(buildTheme.wall.get(), worldIn, x + x1, ch + y1, z + 7, theme, 0);
				for (int z1 = 0; z1 < 8; z1++)
					for (int y1 = 0; y1 < 8; y1++)
						setBlockState(buildTheme.wall.get(), worldIn, x + 7, ch + y1, z + z1, theme, 0);
				ch += 8;
			}
			build(DungeonSegmentModelRegistry.ENTRANCE, worldIn, new BlockPos(x, ch, z), buildTheme, stage);
			return false;
		}

	}

	public static class StairsTop extends DungeonPiece {

		public StairsTop(TemplateManager manager, CompoundNBT p_i51343_2_) {
			super(Dungeon.STAIRSTOP, p_i51343_2_);
		}

		@Override
		public boolean addComponentParts(IWorld worldIn, Random randomIn, MutableBoundingBox structureBoundingBoxIn,
				ChunkPos p_74875_4_) {
			if (theme != 1)
				theme = Theme.BIOME_TO_THEME_MAP
						.getOrDefault(worldIn.getBiome(new BlockPos(x, y, z)).getRegistryName().toString(), 0);
			DungeonSegmentModel model = DungeonBuilder.getModel(this, randomIn);
			if (model == null)
				return false;
			build(model, worldIn, new BlockPos(x, y, z), Theme.get(theme), stage);
			addWalls(this, worldIn, theme);
			return true;
		}

		@Override
		public int getType() {
			return 3;
		}

	}

	public static class Stairs extends DungeonPiece {

		public Stairs(TemplateManager manager, CompoundNBT p_i51343_2_) {
			super(Dungeon.STAIRS, p_i51343_2_);
		}

		@Override
		public boolean addComponentParts(IWorld worldIn, Random randomIn, MutableBoundingBox structureBoundingBoxIn,
				ChunkPos p_74875_4_) {
			if (theme != 1)
				theme = Theme.BIOME_TO_THEME_MAP
						.getOrDefault(worldIn.getBiome(new BlockPos(x, y, z)).getRegistryName().toString(), 0);
			DungeonSegmentModel model = DungeonBuilder.getModel(this, randomIn);
			if (model == null)
				return false;
			Theme buildTheme = Theme.get(theme);
			build(model, worldIn, new BlockPos(x, y, z), buildTheme, stage);
			for (int x1 = 0; x1 < 8; x1++)
				for (int y1 = 0; y1 < 8; y1++)
					setBlockState(buildTheme.wall.get(), worldIn, x + x1, y + y1, z + 7, theme, 0);
			for (int z1 = 0; z1 < 8; z1++)
				for (int y1 = 0; y1 < 8; y1++)
					setBlockState(buildTheme.wall.get(), worldIn, x + 7, y + y1, z + z1, theme, 0);
			return true;
		}

		@Override
		public int getType() {
			return 2;
		}

	}

	public static class StairsBot extends DungeonPiece {

		public StairsBot(TemplateManager manager, CompoundNBT p_i51343_2_) {
			super(Dungeon.STAIRSBOT, p_i51343_2_);
		}

		@Override
		public boolean addComponentParts(IWorld worldIn, Random randomIn, MutableBoundingBox structureBoundingBoxIn,
				ChunkPos p_74875_4_) {
			if (theme != 1)
				theme = Theme.BIOME_TO_THEME_MAP
						.getOrDefault(worldIn.getBiome(new BlockPos(x, y, z)).getRegistryName().toString(), 0);
			DungeonSegmentModel model = DungeonBuilder.getModel(this, randomIn);
			if (model == null)
				return false;
			build(model, worldIn, new BlockPos(x, y, z), Theme.get(theme), stage);
			this.addWalls(this, worldIn, Theme.get(theme));
			return true;
		}

		@Override
		public int getType() {
			return 1;
		}

		public void addWalls(DungeonPiece piece, IWorld world, Theme theme) {
			if (!piece.sides[0])
				for (int x = 2; x < 6; x++)
					for (int y = 2; y < 6; y++)
						piece.setBlockState(theme.wall.get(), world, piece.x + x, piece.y + y, piece.z, this.theme, 0);
			else
				for (int x = 2; x < 6; x++)
					for (int y = 2; y < 6; y++)
						if (!world.getBlockState(new BlockPos(piece.x + x, piece.y + y, piece.z)).isSolid())
							piece.setBlockState(Blocks.IRON_BARS.getDefaultState(), world, piece.x + x, piece.y + y,
									piece.z, this.theme, 0);
			if (!piece.sides[1])
				for (int z = 2; z < 6; z++)
					for (int y = 2; y < 6; y++)
						piece.setBlockState(theme.wall.get(), world, piece.x + 7, piece.y + y, piece.z + z, this.theme,
								0);
			else
				for (int z = 2; z < 6; z++)
					for (int y = 2; y < 6; y++)
						if (!world.getBlockState(new BlockPos(piece.x + 7, piece.y + y, piece.z + z)).isSolid())
							piece.setBlockState(Blocks.IRON_BARS.getDefaultState(), world, piece.x + 7, piece.y + y,
									piece.z + z, this.theme, 0);
			if (!piece.sides[2])
				for (int x = 2; x < 6; x++)
					for (int y = 2; y < 6; y++)
						piece.setBlockState(theme.wall.get(), world, piece.x + x, piece.y + y, piece.z + 7, this.theme,
								0);
			else
				for (int x = 2; x < 6; x++)
					for (int y = 2; y < 6; y++)
						if (!world.getBlockState(new BlockPos(piece.x + x, piece.y + y, piece.z + 7)).isSolid())
							piece.setBlockState(Blocks.IRON_BARS.getDefaultState(), world, piece.x + x, piece.y + y,
									piece.z + 7, this.theme, 0);
			if (!piece.sides[3])
				for (int z = 2; z < 6; z++)
					for (int y = 2; y < 6; y++)
						piece.setBlockState(theme.wall.get(), world, piece.x, piece.y + y, piece.z + z, this.theme, 0);
			else
				for (int z = 2; z < 6; z++)
					for (int y = 2; y < 6; y++)
						if (!world.getBlockState(new BlockPos(piece.x, piece.y + y, piece.z + z)).isSolid())
							piece.setBlockState(Blocks.IRON_BARS.getDefaultState(), world, piece.x, piece.y + y,
									piece.z + z, this.theme, 0);
		}

	}

	public static abstract class DungeonPiece extends StructurePiece {

		public Rotation rotation;
		public int connectedSides, posX, posZ, theme, x, y, z, stage;
		public boolean[] sides;

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
			stage = p_i51343_2_.getInt("stage");
			rotation = RotationHelper.getRotationFromInt(p_i51343_2_.getInt("rotation"));
			this.boundingBox = new MutableBoundingBox(x, y, z, x + 7, y + 7, z + 7);
		}

		public abstract int getType();

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
			this.boundingBox = new MutableBoundingBox(x, y, z, x + 7, y + 7, z + 7);
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
			tagCompound.putInt("rotation", RotationHelper.getIntFromRotation(this.rotation));
		}

		public void setBlockState(BlockState state, IWorld world, int x, int y, int z, int theme, int lootLevel) {
			BlockPos pos = new BlockPos(x, y, z);
			if (state == null)
				return;
			IBlockPlacementHandler.getHandler(state.getBlock()).setupBlock(world, state, pos, world.getRandom(), theme,
					lootLevel);
			if (BLOCKS_NEEDING_POSTPROCESSING.contains(state.getBlock()))
				world.getChunk(pos).markBlockForPostprocessing(pos);
		}

		public void build(DungeonSegmentModel model, IWorld world, BlockPos pos, Theme theme, int lootLevel) {
			for (int x = 0; x < model.width; x++) {
				for (int y = 0; y < model.height; y++) {
					for (int z = 0; z < model.length; z++) {
						BlockState state;
						if (model.model[x][y][z] == null)
							state = Blocks.AIR.getDefaultState();
						else
							state = DungeonSegmentModelBlock.getBlockState(model.model[x][y][z], theme,
									world.getRandom());
						if (state == null)
							continue;
						setBlockState(state, world, pos.getX() + x, pos.getY() + y, pos.getZ() + z, this.theme,
								lootLevel);
					}
				}
			}
		}

		public void buildRotated(DungeonSegmentModel model, IWorld world, BlockPos pos, Theme theme, int lootLevel,
				Rotation rotation) {
			switch (rotation) {
			case CLOCKWISE_90:
				for (int x = 0; x < model.width; x++) {
					for (int y = 0; y < model.height; y++) {
						for (int z = 0; z < model.length; z++) {
							BlockState state;
							if (model.model[x][y][z] == null)
								state = Blocks.AIR.getDefaultState();
							else
								state = DungeonSegmentModelBlock.getBlockState(model.model[x][y][z], theme,
										world.getRandom(), Rotation.CLOCKWISE_90);
							if (state == null)
								continue;
							setBlockState(state, world, pos.getX() + model.length - z - 1, pos.getY() + y,
									pos.getZ() + x, this.theme, lootLevel);
						}
					}
				}
				break;
			case COUNTERCLOCKWISE_90:
				for (int x = 0; x < model.width; x++) {
					for (int y = 0; y < model.height; y++) {
						for (int z = 0; z < model.length; z++) {
							BlockState state;
							if (model.model[x][y][z] == null)
								state = Blocks.AIR.getDefaultState();
							else
								state = DungeonSegmentModelBlock.getBlockState(model.model[x][y][z], theme,
										world.getRandom(), Rotation.COUNTERCLOCKWISE_90);
							if (state == null)
								continue;
							setBlockState(state, world, pos.getX() + z, pos.getY() + y,
									pos.getZ() + model.width - x - 1, this.theme, lootLevel);
						}
					}
				}
				break;
			case CLOCKWISE_180:
				for (int x = 0; x < model.width; x++) {
					for (int y = 0; y < model.height; y++) {
						for (int z = 0; z < model.length; z++) {
							BlockState state;
							if (model.model[x][y][z] == null)
								state = Blocks.AIR.getDefaultState();
							else
								state = DungeonSegmentModelBlock.getBlockState(model.model[x][y][z], theme,
										world.getRandom(), Rotation.CLOCKWISE_180);
							if (state == null)
								continue;
							setBlockState(state, world, pos.getX() + model.width - x - 1, pos.getY() + y,
									pos.getZ() + model.length - z - 1, this.theme, lootLevel);
						}
					}
				}
				break;
			case NONE:
				build(model, world, pos, theme, lootLevel);
				break;
			default:
				DungeonCrawl.LOGGER.warn("Failed to build a rotated dungeon segment: Unknown rotation " + rotation);
				break;
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

	}

	public static void addWalls(DungeonPiece piece, IWorld world, int theme) {
		Theme buildTheme = Theme.get(theme);
		if (!piece.sides[0])
			for (int x = 2; x < 6; x++)
				for (int y = 2; y < 6; y++)
					piece.setBlockState(buildTheme.wall.get(), world, piece.x + x, piece.y + y, piece.z, theme, 0);
		if (!piece.sides[1])
			for (int z = 2; z < 6; z++)
				for (int y = 2; y < 6; y++)
					piece.setBlockState(buildTheme.wall.get(), world, piece.x + 7, piece.y + y, piece.z + z, theme, 0);
		if (!piece.sides[2])
			for (int x = 2; x < 6; x++)
				for (int y = 2; y < 6; y++)
					piece.setBlockState(buildTheme.wall.get(), world, piece.x + x, piece.y + y, piece.z + 7, theme, 0);
		if (!piece.sides[3])
			for (int z = 2; z < 6; z++)
				for (int y = 2; y < 6; y++)
					piece.setBlockState(buildTheme.wall.get(), world, piece.x, piece.y + y, piece.z + z, theme, 0);
	}

	public static void addColumns(DungeonPiece piece, IWorld world, int ySub, int theme) {
		Theme buildTheme = Theme.get(theme);
		piece.setBlockState(
				buildTheme.floorStairs.get().with(BlockStateProperties.HORIZONTAL_FACING, Direction.SOUTH)
						.with(BlockStateProperties.HALF, Half.TOP).with(BlockStateProperties.WATERLOGGED, true),
				world, piece.x + 2, piece.y - ySub, piece.z + 2, theme, 0);
		piece.setBlockState(
				buildTheme.floorStairs.get().with(BlockStateProperties.HORIZONTAL_FACING, Direction.SOUTH)
						.with(BlockStateProperties.HALF, Half.TOP).with(BlockStateProperties.WATERLOGGED, true),
				world, piece.x + 3, piece.y - ySub, piece.z + 2, theme, 0);
		piece.setBlockState(
				buildTheme.floorStairs.get().with(BlockStateProperties.HORIZONTAL_FACING, Direction.SOUTH)
						.with(BlockStateProperties.HALF, Half.TOP).with(BlockStateProperties.WATERLOGGED, true),
				world, piece.x + 4, piece.y - ySub, piece.z + 2, theme, 0);
		piece.setBlockState(
				buildTheme.floorStairs.get().with(BlockStateProperties.HORIZONTAL_FACING, Direction.SOUTH)
						.with(BlockStateProperties.HALF, Half.TOP).with(BlockStateProperties.WATERLOGGED, true),
				world, piece.x + 5, piece.y - ySub, piece.z + 2, theme, 0);

		piece.setBlockState(
				buildTheme.floorStairs.get().with(BlockStateProperties.HORIZONTAL_FACING, Direction.NORTH)
						.with(BlockStateProperties.HALF, Half.TOP).with(BlockStateProperties.WATERLOGGED, true),
				world, piece.x + 2, piece.y - ySub, piece.z + 5, theme, 0);
		piece.setBlockState(
				buildTheme.floorStairs.get().with(BlockStateProperties.HORIZONTAL_FACING, Direction.NORTH)
						.with(BlockStateProperties.HALF, Half.TOP).with(BlockStateProperties.WATERLOGGED, true),
				world, piece.x + 3, piece.y - ySub, piece.z + 5, theme, 0);
		piece.setBlockState(
				buildTheme.floorStairs.get().with(BlockStateProperties.HORIZONTAL_FACING, Direction.NORTH)
						.with(BlockStateProperties.HALF, Half.TOP).with(BlockStateProperties.WATERLOGGED, true),
				world, piece.x + 4, piece.y - ySub, piece.z + 5, theme, 0);
		piece.setBlockState(
				buildTheme.floorStairs.get().with(BlockStateProperties.HORIZONTAL_FACING, Direction.NORTH)
						.with(BlockStateProperties.HALF, Half.TOP).with(BlockStateProperties.WATERLOGGED, true),
				world, piece.x + 5, piece.y - ySub, piece.z + 5, theme, 0);

		piece.setBlockState(
				buildTheme.floorStairs.get().with(BlockStateProperties.HORIZONTAL_FACING, Direction.EAST)
						.with(BlockStateProperties.HALF, Half.TOP).with(BlockStateProperties.WATERLOGGED, true),
				world, piece.x + 2, piece.y - ySub, piece.z + 3, theme, 0);
		piece.setBlockState(
				buildTheme.floorStairs.get().with(BlockStateProperties.HORIZONTAL_FACING, Direction.EAST)
						.with(BlockStateProperties.HALF, Half.TOP).with(BlockStateProperties.WATERLOGGED, true),
				world, piece.x + 2, piece.y - ySub, piece.z + 4, theme, 0);

		piece.setBlockState(
				buildTheme.floorStairs.get().with(BlockStateProperties.HORIZONTAL_FACING, Direction.WEST)
						.with(BlockStateProperties.HALF, Half.TOP).with(BlockStateProperties.WATERLOGGED, true),
				world, piece.x + 5, piece.y - ySub, piece.z + 3, theme, 0);
		piece.setBlockState(
				buildTheme.floorStairs.get().with(BlockStateProperties.HORIZONTAL_FACING, Direction.WEST)
						.with(BlockStateProperties.HALF, Half.TOP).with(BlockStateProperties.WATERLOGGED, true),
				world, piece.x + 5, piece.y - ySub, piece.z + 4, theme, 0);

		for (int x = 3; x < 5; x++) {
			for (int z = 3; z < 5; z++) {
				int groundHeight = getGroudHeightFrom(world, piece.x + x, piece.z + z, piece.y - ySub);
				for (int y = piece.y - ySub; y > groundHeight; y--)
					piece.setBlockState(buildTheme.column.get(), world, piece.x + x, y, piece.z + z, theme, 0);
			}
		}

	}

	public static int getGroudHeight(IWorld world, int x, int z) {
		for (int y = 255; y > 0; y--)
			if (world.getBlockState(new BlockPos(x, y, z)).isSolid())
				return y;
		return 0;
	}

	public static int getGroudHeightFrom(IWorld world, int x, int z, int yStart) {
		for (int y = yStart; y > 0; y--)
			if (world.getBlockState(new BlockPos(x, y, z)).isSolid())
				return y;
		return 0;
	}

}
