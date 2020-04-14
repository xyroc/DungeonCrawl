package xiroc.dungeoncrawl.dungeon.piece.room;

/*
 * DungeonCrawl (C) 2019 - 2020 XYROC (XIROC1337), All Rights Reserved 
 */

import java.util.Random;

import net.minecraft.block.Blocks;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.Direction;
import net.minecraft.util.Tuple;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.MutableBoundingBox;
import net.minecraft.world.IWorld;
import net.minecraft.world.gen.feature.template.TemplateManager;
import xiroc.dungeoncrawl.DungeonCrawl;
import xiroc.dungeoncrawl.dungeon.Node;
import xiroc.dungeoncrawl.dungeon.StructurePieceTypes;
import xiroc.dungeoncrawl.dungeon.piece.DungeonPiece;
import xiroc.dungeoncrawl.util.Position2D;

public class DungeonNodeRoom extends DungeonPiece {

	public Node node;

	public int model;
	public boolean large, lootRoom;

	public DungeonNodeRoom() {
		super(StructurePieceTypes.NODE_ROOM);
	}

	public DungeonNodeRoom(TemplateManager manager, CompoundNBT nbt) {
		super(StructurePieceTypes.NODE_ROOM, nbt);
		this.model = nbt.getInt("model");
		this.large = nbt.getBoolean("large");
		this.lootRoom = nbt.getBoolean("lootRoom");
		setupBoundingBox();
		DungeonCrawl.LOGGER.debug("Position: ({}|{}|{}), Bounds: {} {} {}, Large: {}", x, y, z,
				boundingBox.maxX - boundingBox.minX, boundingBox.maxY - boundingBox.minY,
				boundingBox.maxZ - boundingBox.minZ, large);
	}

	@Override
	public int determineModel(Random rand) {
		if (lootRoom)
			return 0;
		
		large = stage < 2 ? false : rand.nextFloat() < 0.15;
//		int sides = rand.nextFloat() < 0.25 || connectedSides == 4 ? connectedSides
//				: connectedSides + 1 + rand.nextInt(4 - connectedSides);
//		if (large) {
//			switch (sides) {
//			case 1:
//				if (DungeonModels.LARGE_NODE_1.length > 0)
//					return DungeonModels.LARGE_NODE_1[rand.nextInt(DungeonModels.LARGE_NODE_1.length)].id;
//			case 2:
//				if (DungeonModels.LARGE_NODE_2.length > 0)
//					return DungeonModels.LARGE_NODE_1[rand.nextInt(DungeonModels.LARGE_NODE_2.length)].id;
//			case 3:
//				if (DungeonModels.LARGE_NODE_3.length > 0)
//					return DungeonModels.LARGE_NODE_1[rand.nextInt(DungeonModels.LARGE_NODE_3.length)].id;
//			case 4:
//				if (DungeonModels.LARGE_NODE_4.length > 0)
//					return DungeonModels.LARGE_NODE_1[rand.nextInt(DungeonModels.LARGE_NODE_4.length)].id;
//			}
//		} else {
//			switch (sides) {
//			case 1:
//				if (DungeonModels.NODE_1.length > 0)
//					return DungeonModels.NODE_1[rand.nextInt(DungeonModels.NODE_1.length)].id;
//			case 2:
//				if (DungeonModels.NODE_2.length > 0)
//					return DungeonModels.NODE_1[rand.nextInt(DungeonModels.NODE_2.length)].id;
//			case 3:
//				if (DungeonModels.NODE_3.length > 0)
//					return DungeonModels.NODE_1[rand.nextInt(DungeonModels.NODE_3.length)].id;
//			case 4:
//				if (DungeonModels.NODE_4.length > 0)
//					return DungeonModels.NODE_1[rand.nextInt(DungeonModels.NODE_4.length)].id;
//			}
//		}
		return 0;
	}

	@Override
	public void setRealPosition(int x, int y, int z) {
		if (large)
			super.setRealPosition(x - 9, y, z - 9);
		else
			super.setRealPosition(x - 4, y, z - 4);
	}

	@Override
	public boolean addComponentParts(IWorld worldIn, Random randomIn, MutableBoundingBox structureBoundingBoxIn,
			ChunkPos chunkPosIn) {

		DungeonCrawl.LOGGER.debug("Building; Position: ({}|{}|{}), Bounds: {} {} {}, Large: {}", x, y, z,
				boundingBox.maxX - boundingBox.minX, boundingBox.maxY - boundingBox.minY,
				boundingBox.maxZ - boundingBox.minZ, large);

		int startX = Math.max(x, structureBoundingBoxIn.minX), startZ = Math.max(z, structureBoundingBoxIn.minZ),
				maxX = Math.min(x + (large ? 26 : 16), structureBoundingBoxIn.maxX),
				maxZ = Math.min(z + (large ? 26 : 16), structureBoundingBoxIn.maxZ);

		for (int x1 = startX; x1 <= maxX; x1++)
			for (int z1 = startZ; z1 <= maxZ; z1++)
				for (int y1 = 0; y1 < 9; y1++)
					if (x1 == boundingBox.minX || z1 == boundingBox.minZ)
						worldIn.setBlockState(new BlockPos(x1, y + y1, z1), lootRoom ? Blocks.OBSIDIAN.getDefaultState() : Blocks.OAK_PLANKS.getDefaultState(), 2);
					else if (x1 == boundingBox.maxX || z1 == boundingBox.maxZ)
						worldIn.setBlockState(new BlockPos(x1, y + y1, z1), lootRoom ? Blocks.OBSIDIAN.getDefaultState() : Blocks.SPRUCE_PLANKS.getDefaultState(), 2);
					else if (y1 == 0 || y1 == 8)
						worldIn.setBlockState(new BlockPos(x1, y + y1, z1), lootRoom ? Blocks.OBSIDIAN.getDefaultState() : Blocks.BIRCH_PLANKS.getDefaultState(), 2);
					else
						worldIn.setBlockState(new BlockPos(x1, y + y1, z1), CAVE_AIR, 2);

		return true;
	}

	@Override
	public void setupBoundingBox() {
		this.boundingBox = large ? new MutableBoundingBox(x, y, z, x + 26, y + 8, z + 26)
				: new MutableBoundingBox(x, y, z, x + 16, y + 8, z + 16);
	}

	public void buildConnector(IWorld world, Direction direction, BlockPos position, MutableBoundingBox boundsIn) {

	}

	@Override
	public int getType() {
		return 10;
	}

	@Override
	public boolean canConnect(Direction side) {
		return node.canConnect(side);
	}

	@Override
	public Tuple<Position2D, Position2D> getAlternativePath(Position2D current, Position2D end) {

		if (!current.hasFacing()) {
			throw new RuntimeException("The current Position needs to provide a facing.");
		}

		Position2D center = new Position2D(posX, posZ);

		return new Tuple<Position2D, Position2D>(center.shift(node.findClosest(current.facing), 1),
				center.shift(findExitToPosition(end), 1));
	}

	@Override
	public boolean hasAlternativePath() {
		return true;
	}

	private Direction findExitToPosition(Position2D pos) {

		if (pos.hasFacing())
			return node.findClosest(pos.facing);

		if (pos.x > posX) {
			if (pos.z > posZ)
				return Direction.SOUTH;
			else if (pos.z < posZ)
				return Direction.NORTH;
			else
				return Direction.EAST;
		} else if (pos.x < posX) {
			if (pos.z > posZ)
				return Direction.SOUTH;
			else if (pos.z < posZ)
				return Direction.NORTH;
			else
				return Direction.WEST;
		} else {
			if (pos.z > posZ)
				return Direction.SOUTH;
			else if (pos.z < posZ)
				return Direction.NORTH;
			else {
				DungeonCrawl.LOGGER.error("Invalid Position: {},{}", pos.x, pos.z);
				return null;
			}
		}

	}

	@Override
	public void readAdditional(CompoundNBT tagCompound) {
		super.readAdditional(tagCompound);
		tagCompound.putBoolean("large", large);
		tagCompound.putBoolean("lootRoom", lootRoom);
	}

}
