package xiroc.dungeoncrawl.dungeon.piece.room;

import java.util.List;

/*
 * DungeonCrawl (C) 2019 - 2020 XYROC (XIROC1337), All Rights Reserved 
 */

import java.util.Random;

import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.Direction;
import net.minecraft.util.Tuple;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.MutableBoundingBox;
import net.minecraft.world.IWorld;
import net.minecraft.world.gen.feature.template.TemplateManager;
import xiroc.dungeoncrawl.DungeonCrawl;
import xiroc.dungeoncrawl.dungeon.DungeonBuilder;
import xiroc.dungeoncrawl.dungeon.Node;
import xiroc.dungeoncrawl.dungeon.StructurePieceTypes;
import xiroc.dungeoncrawl.dungeon.model.DungeonModel;
import xiroc.dungeoncrawl.dungeon.model.DungeonModels;
import xiroc.dungeoncrawl.dungeon.model.DungeonModels.ModelCategory;
import xiroc.dungeoncrawl.dungeon.piece.DungeonNodeConnector;
import xiroc.dungeoncrawl.dungeon.piece.DungeonPiece;
import xiroc.dungeoncrawl.dungeon.treasure.Treasure;
import xiroc.dungeoncrawl.theme.Theme;
import xiroc.dungeoncrawl.util.Position2D;
import xiroc.dungeoncrawl.util.RotationHelper;

public class DungeonNodeRoom extends DungeonPiece {

	public Node node;

	public boolean large, lootRoom;

	public DungeonNodeRoom() {
		super(StructurePieceTypes.NODE_ROOM);
	}

	public DungeonNodeRoom(TemplateManager manager, CompoundNBT nbt) {
		super(StructurePieceTypes.NODE_ROOM, nbt);
		this.large = nbt.getBoolean("large");
		this.lootRoom = nbt.getBoolean("lootRoom");
		setupBoundingBox();
//		DungeonCrawl.LOGGER.debug("Position: ({}|{}|{}), Bounds: {} {} {}, Large: {}", x, y, z,
//				boundingBox.maxX - boundingBox.minX, boundingBox.maxY - boundingBox.minY,
//				boundingBox.maxZ - boundingBox.minZ, large);
	}

	@Override
	public int determineModel(DungeonBuilder builder, Random rand) {
		if (lootRoom)
			return 0;

		large = stage < 2 ? false : rand.nextFloat() < 0.15;

		ModelCategory base = null;
		switch (connectedSides) {
		case 1:
			base = ModelCategory.NODE_DEAD_END;
		case 2:
			if (sides[0] && sides[2] || sides[1] && sides[3])
				base = ModelCategory.NODE_STRAIGHT;
			else
				base = ModelCategory.NODE_TURN;
		case 3:
			base = ModelCategory.NODE_OPEN;
		default:
			base = ModelCategory.NODE;
		}

		DungeonModel[] possibilities = large
				? ModelCategory.getIntersection(base, ModelCategory.LARGE_NODE,
						ModelCategory.getCategoryForStage(stage))
				: ModelCategory.getIntersection(base, ModelCategory.getCategoryForStage(stage));

		if (possibilities.length <= 0) {
			DungeonCrawl.LOGGER.error("Didnt find a model for {} in stage {}. Connected Sides: {}, Base: {}", this,
					stage, connectedSides, base);

			return large ? DungeonModels.LARGE_NODE.id : DungeonModels.NODE_2.id;
		}

		return possibilities[rand.nextInt(possibilities.length)].id;
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
		DungeonModel model = DungeonModels.MAP.get(modelID);

		buildRotated(model, worldIn, structureBoundingBoxIn, new BlockPos(x, y, z), Theme.get(theme),
				Theme.getSub(subTheme), Treasure.MODEL_TREASURE_TYPES.getOrDefault(modelID, Treasure.Type.DEFAULT),
				stage, rotation, false);

		entrances(worldIn, structureBoundingBoxIn, Theme.get(theme), model);
		return true;
	}

	@Override
	public void setupBoundingBox() {
		this.boundingBox = large ? new MutableBoundingBox(x, y, z, x + 26, y + 8, z + 26)
				: new MutableBoundingBox(x, y, z, x + 16, y + 8, z + 16);
	}

	@Override
	public int getType() {
		return 10;
	}

	@Override
	public boolean canConnect(Direction side) {
		return node.canConnect(side);
	}

	public void addConnectors(List<DungeonPiece> list, Random rand) {
		if (large)
			return;
		for (int i = 0; i < sides.length - 2; i++) {
			if (sides[i]) {
				switch (Direction.byHorizontalIndex((i + 2) % 4)) {
				case EAST: {
					DungeonNodeConnector connector = new DungeonNodeConnector();
					connector.rotation = RotationHelper.getOppositeRotationFromFacing(Direction.EAST);
					connector.modelID = connector.determineModel(null, rand);
					connector.setRealPosition(x + 17, y, z + 8);
					connector.setupBoundingBox();
					list.add(connector);
					continue;
				}
				case NORTH: {
					DungeonNodeConnector connector = new DungeonNodeConnector();
					connector.rotation = RotationHelper.getOppositeRotationFromFacing(Direction.NORTH);
					connector.modelID = connector.determineModel(null, rand);
					connector.setRealPosition(x + 8, y, z - 5);
					connector.setupBoundingBox();
					list.add(connector);
					continue;
				}
				case SOUTH: {
					DungeonNodeConnector connector = new DungeonNodeConnector();
					connector.rotation = RotationHelper.getOppositeRotationFromFacing(Direction.SOUTH);
					connector.modelID = connector.determineModel(null, rand);
					connector.setRealPosition(x + 8, y, z + 17);
					connector.setupBoundingBox();
					list.add(connector);
					continue;
				}
				case WEST: {
					DungeonNodeConnector connector = new DungeonNodeConnector();
					connector.rotation = RotationHelper.getOppositeRotationFromFacing(Direction.WEST);
					connector.modelID = connector.determineModel(null, rand);
					connector.setRealPosition(x - 5, y, z + 8);
					connector.setupBoundingBox();
					list.add(connector);
					continue;
				}
				default:
					continue;
				}
			}
		}
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
