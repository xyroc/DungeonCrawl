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
	public static final Node DEAD_END = new Node(false, false, false, true);
	public static final Node STRAIGHT = new Node(false, true, false, true);
	public static final Node TURN = new Node(false, false, true, true);
	public static final Node OPEN = new Node(false, true, true, true);
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

	/**
	 * @return A rotation with which this node can fit into the given one, or null
	 *         if there is none.
	 */
	public Rotation compare(Node node) {
		return Node.compare(node, this, Rotation.NONE, 0);
	}

	private static Rotation compare(Node node, Node rotatedNode, Rotation currentRotation, int depth) {
		if (depth > 3)
			return null;
		for (int i = 0; i < node.sides.length; i++) {
			if (node.sides[i] && !rotatedNode.sides[i]) {
				return compare(node, rotatedNode.rotate(Rotation.CLOCKWISE_90),
						currentRotation.add(Rotation.CLOCKWISE_90), ++depth);
			}
		}
		return currentRotation;
	}

}
