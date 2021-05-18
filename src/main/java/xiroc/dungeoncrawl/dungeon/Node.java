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

package xiroc.dungeoncrawl.dungeon;

import net.minecraft.util.Rotation;
import xiroc.dungeoncrawl.dungeon.piece.room.DungeonNodeRoom;

import java.util.Random;

public class Node {

    /**
     * Generic Node types
     */
    public static final Node DEAD_END = new Node(false, false, false, true);
    public static final Node STRAIGHT = new Node(false, true, false, true);
    public static final Node TURN = new Node(false, false, true, true);
    public static final Node FORK = new Node(false, true, true, true);
    public static final Node FULL = new Node(true, true, true, true);

    private final boolean[] sides; // Order is N-E-S-W

    public Node(boolean north, boolean east, boolean south, boolean west) {
        this.sides = new boolean[]{north, east, south, west};
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
     * if there is none. "Fit into the given node" means that wherever the given node does have an exit,
     * this one needs to have one as well.
     */
    public Rotation compare(Node node, Random rand) {
        return Node.compare(node, this, Rotation.NONE, rand.nextBoolean() ? Rotation.CLOCKWISE_90 : Rotation.COUNTERCLOCKWISE_90, 0);
    }

    private static Rotation compare(Node node, Node rotatedNode, Rotation currentRotation, Rotation step, int depth) {
        if (depth > 3)
            return null;
        for (int i = 0; i < node.sides.length; i++) {
            if (node.sides[i] && !rotatedNode.sides[i]) {
                return compare(node, rotatedNode.rotate(step),
                        currentRotation.add(step), step, ++depth);
            }
        }
        return currentRotation;
    }

    public static Node getForNodeRoom(DungeonNodeRoom nodeRoom) {
        switch (nodeRoom.connectedSides) {
            case 1:
                return DEAD_END;
            case 2: {
                if (nodeRoom.sides[0] && nodeRoom.sides[2] || nodeRoom.sides[1] && nodeRoom.sides[3]) {
                    return STRAIGHT;
                } else {
                    return TURN;
                }
            }
            case 3:
                return FORK;
            default:
                return FULL;
        }
    }

}
