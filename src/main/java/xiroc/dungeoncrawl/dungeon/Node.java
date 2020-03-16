package xiroc.dungeoncrawl.dungeon;

/*
 * DungeonCrawl (C) 2019 - 2020 XYROC (XIROC1337), All Rights Reserved 
 */

import net.minecraft.util.Direction;
import net.minecraft.util.Rotation;

public class Node {

	/**
	 * Generic Node types
	 */
	public static final Node SHAPE_I = new Node(false, true, false, true);
	public static final Node SHAPE_L = new Node(false, false, true, true);
	public static final Node SHAPE_T = new Node(true, false, true, true);
	public static final Node ALL = new Node(true, true, true, true);

	private final boolean[] sides; // Order is N-E-S-W

	public Node(boolean north, boolean east, boolean south, boolean west) {
		this.sides = new boolean[] { north, east, south, west };
	}

	public Node(boolean[] values) {
		this.sides = values;
	}

	public boolean canConnect(Direction side) {
		return sides[(side.getHorizontalIndex() + 2) % 4];
	}

	public Direction findClosest(Direction base) {
		if (canConnect(base))
			return base;
		base = base.rotateY();
		if (canConnect(base))
			return base;
		base = base.getOpposite();
		if (canConnect(base))
			return base;
		base = base.rotateYCCW();
		if (canConnect(base))
			return base;
		return null;
	}

	public Node rotate(Rotation rotation) {
		switch (rotation) {
		case CLOCKWISE_90:
			return new Node(sides[3], sides[0], sides[1], sides[2]);
		case COUNTERCLOCKWISE_90:
			return new Node(sides[1], sides[2], sides[3], sides[0]);
		case CLOCKWISE_180:
			return new Node(sides[2], sides[3], sides[0], sides[1]);
		default:
			return this;
		}
	}

}
