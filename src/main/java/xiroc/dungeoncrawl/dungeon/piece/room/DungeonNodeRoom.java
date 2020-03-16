package xiroc.dungeoncrawl.dungeon.piece.room;

/*
 * DungeonCrawl (C) 2019 - 2020 XYROC (XIROC1337), All Rights Reserved 
 */

import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.Direction;
import net.minecraft.util.Tuple;
import net.minecraft.world.gen.feature.structure.IStructurePieceType;
import xiroc.dungeoncrawl.DungeonCrawl;
import xiroc.dungeoncrawl.dungeon.Node;
import xiroc.dungeoncrawl.dungeon.piece.DungeonPiece;
import xiroc.dungeoncrawl.util.Position2D;

public abstract class DungeonNodeRoom extends DungeonPiece {

	public Node node;
	public Position2D center;

	public DungeonNodeRoom(IStructurePieceType type, CompoundNBT nbt) {
		super(type, nbt);
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

		if (pos.x > center.x) {
			if (pos.z > center.z)
				return Direction.SOUTH;
			else if (pos.z < center.z)
				return Direction.NORTH;
			else
				return Direction.EAST;
		} else if (pos.x < center.x) {
			if (pos.z > center.z)
				return Direction.SOUTH;
			else if (pos.z < center.z)
				return Direction.NORTH;
			else
				return Direction.WEST;
		} else {
			if (pos.z > center.z)
				return Direction.SOUTH;
			else if (pos.z < center.z)
				return Direction.NORTH;
			else {
				DungeonCrawl.LOGGER.error("Invalid Position: {},{}", pos.x, pos.z);
				return null;
			}
		}

	}

}
