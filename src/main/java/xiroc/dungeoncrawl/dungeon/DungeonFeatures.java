package xiroc.dungeoncrawl.dungeon;

/*
 * DungeonCrawl (C) 2019 - 2020 XYROC (XIROC1337), All Rights Reserved 
 */

import java.util.HashMap;
import java.util.List;
import java.util.Random;

import com.google.common.collect.Lists;

import net.minecraft.util.Direction;
import net.minecraft.util.Rotation;
import net.minecraft.util.math.BlockPos;
import xiroc.dungeoncrawl.dungeon.DungeonPieces.DungeonPiece;
import xiroc.dungeoncrawl.dungeon.DungeonPieces.Hole;
import xiroc.dungeoncrawl.dungeon.DungeonPieces.SideRoom;
import xiroc.dungeoncrawl.util.Position2D;
import xiroc.dungeoncrawl.util.RotationHelper;
import xiroc.dungeoncrawl.util.Triple;

public class DungeonFeatures {

	public static final HashMap<Integer, Triple<Integer, Integer, Integer>> OFFSET_DATA;
	public static final Triple<Integer, Integer, Integer> DEFAULT_OFFSET = new Triple<Integer, Integer, Integer>(0, 0,
			0);
	public static final List<CorridorFeature> CORRIDOR_FEATURES;

	static {
		OFFSET_DATA = new HashMap<Integer, Triple<Integer, Integer, Integer>>();
		OFFSET_DATA.put(33, new Triple<Integer, Integer, Integer>(0, -1, 0));
	}

	static {
		CORRIDOR_FEATURES = Lists.newArrayList();
		CORRIDOR_FEATURES.add((builder, layer, x, z, rand, lyr, stage, startPos) -> {
			if (rand.nextDouble() < 0.06 && canPlacePieceWithHeight(builder, lyr, x, z, 1, 1, -2, true)) {
				Hole hole = new Hole(null, DungeonPieces.DEFAULT_NBT);
				hole.sides = layer.segments[x][z].sides;
				hole.connectedSides = layer.segments[x][z].connectedSides;
				hole.setRealPosition(startPos.getX() + x * 8, startPos.getY() - lyr * 8, startPos.getZ() + z * 8);
				hole.stage = stage;
				hole.lava = stage == 2;
				layer.segments[x][z] = hole;
				mark(builder, lyr, x, z, 1, 1, -2);
				return true;
			} else
				return false;
		});
		CORRIDOR_FEATURES.add((builder, layer, x, z, rand, lyr, stage, startPos) -> {
			if (layer.segments[x][z].connectedSides == 2
					&& (layer.segments[x][z].sides[0] && layer.segments[x][z].sides[2]
							|| layer.segments[x][z].sides[1] && layer.segments[x][z].sides[3])
					&& rand.nextDouble() < 0.08) {
				((DungeonPieces.Corridor) layer.segments[x][z]).specialType = lyr > 0 ? 1 : 2;
				return true;
			}
			return false;
		});
		CORRIDOR_FEATURES.add((builder, layer, x, z, rand, lyr, stage, startPos) -> {
			if (layer.segments[x][z].getType() == 0 && layer.segments[x][z].connectedSides < 4
					&& (lyr == 0 || (builder.layers[lyr - 1].segments[x][z] == null
							|| builder.layers[lyr - 1].segments[x][z].getType() != 8))) {
				Direction facing = RotationHelper.translateDirection(Direction.EAST, layer.segments[x][z].rotation);
				Position2D pos = new Position2D(x, z);
				Position2D roomPos = pos.shift(RotationHelper.translateDirectionLeft(facing), 1);
				if (roomPos.isValid(layer.width, layer.length) && layer.segments[roomPos.x][roomPos.z] == null
						&& builder.maps[lyr].isPositionFree(roomPos.x, roomPos.z) && rand.nextDouble() < 0.09) {
					layer.segments[x][z].openSide(RotationHelper.translateDirectionLeft(facing));
					DungeonPieces.SideRoom sideRoom = (SideRoom) RandomFeature.SIDE_ROOM.roll(rand);
					sideRoom.setOffset(OFFSET_DATA.getOrDefault(sideRoom.modelID, DEFAULT_OFFSET));
					sideRoom.setPosition(roomPos.x, roomPos.z);
					sideRoom.stage = stage;
					sideRoom.connectedSides = 1;
					sideRoom.setRealPosition(startPos.getX() + roomPos.x * 8, startPos.getY() - lyr * 8,
							startPos.getZ() + roomPos.z * 8);
					sideRoom.rotation = layer.segments[x][z].rotation.add(Rotation.COUNTERCLOCKWISE_90);
					layer.rotatePiece(layer.segments[x][z]);
					layer.segments[roomPos.x][roomPos.z] = sideRoom;
					builder.maps[lyr].markPositionAsOccupied(roomPos);
					return true;
				}
			}
			return false;
		});
		CORRIDOR_FEATURES.add((builder, layer, x, z, rand, lyr, stage, startPos) -> {
			if (layer.segments[x][z].connectedSides == 2 && rand.nextDouble() < 0.07
					&& (layer.segments[x][z].sides[0] && layer.segments[x][z].sides[2]
							|| layer.segments[x][z].sides[1] && layer.segments[x][z].sides[3])) {
				DungeonPiece feature = RandomFeature.CORRIDOR_FEATURE.roll(rand);
				if (feature.getType() == 7 && !canPlacePieceWithHeight(builder, lyr, x, z, 1, 1, -1, true))
					feature = new DungeonPieces.CorridorTrap(null, DungeonPieces.DEFAULT_NBT);
				feature.sides = layer.segments[x][z].sides;
				feature.connectedSides = layer.segments[x][z].connectedSides;
				feature.setRealPosition(startPos.getX() + x * 8, startPos.getY() - lyr * 8, startPos.getZ() + z * 8);
				feature.stage = stage;
				feature.rotation = layer.segments[x][z].rotation;
				layer.segments[x][z] = feature;
				return true;
			}
			return false;
		});
		CORRIDOR_FEATURES.add((builder, layer, x, z, rand, lyr, stage, startPos) -> {
			if (layer.segments[x][z].getType() == 0 && layer.segments[x][z].connectedSides < 4) {
				Direction facing = RotationHelper.translateDirection(Direction.EAST, layer.segments[x][z].rotation);
				Position2D pos = new Position2D(x, z);
				Position2D part1Pos = pos.shift(RotationHelper.translateDirectionLeft(facing), 1);
				Position2D part2Pos = part1Pos.shift(facing, 1);
				if (part1Pos.isValid(layer.width, layer.length) && part2Pos.isValid(layer.width, layer.length)
						&& layer.canPutDoubleRoom(part1Pos, facing) && rand.nextDouble() < 0.023) {

					layer.segments[x][z].openSide(RotationHelper.translateDirectionLeft(facing));

					DungeonPieces.Part part1 = new DungeonPieces.Part(null, DungeonPieces.DEFAULT_NBT);
					DungeonPieces.Part part2 = new DungeonPieces.Part(null, DungeonPieces.DEFAULT_NBT);

					part1.connectedSides = 1;
					part2.connectedSides = 1;

					part1.treasureType = 1;
					part2.treasureType = 1;

					part1.stage = stage;
					part2.stage = stage;

					part1.setPosition(part1Pos.x, part1Pos.z);
					part2.setPosition(part2Pos.x, part2Pos.z);

					part1.setRotation(layer.segments[x][z].rotation.add(Rotation.COUNTERCLOCKWISE_90));
					part2.setRotation(layer.segments[x][z].rotation.add(Rotation.COUNTERCLOCKWISE_90));

					layer.rotatePiece(layer.segments[x][z]);

					int id = 27;
					part1.set(id, 0, 0, 0, 8, 8, 8);
					part2.set(id, 0, 0, 8, 8, 8, 8);

					part1.setRealPosition(startPos.getX() + part1Pos.x * 8, startPos.getY() - lyr * 8,
							startPos.getZ() + part1Pos.z * 8);
					part2.setRealPosition(startPos.getX() + part2Pos.x * 8, startPos.getY() - lyr * 8,
							startPos.getZ() + part2Pos.z * 8);

					layer.segments[part1Pos.x][part1Pos.z] = part1;
					layer.segments[part2Pos.x][part2Pos.z] = part2;

//					DungeonCrawl.LOGGER.debug("Placing a kitchen at {} {} {}. Second part: {} {} {}. Facing: {}.", part1.x, part1.y, part1.z, part2.x, part2.y, part2.z, facing);
					return true;
				}
				return false;
			}
			return false;
		});
	}

	public static void processCorridor(DungeonBuilder builder, DungeonLayer layer, int x, int z, Random rand, int lyr,
			int stage, BlockPos startPos) {
		for (CorridorFeature corridorFeature : CORRIDOR_FEATURES)
			if (corridorFeature.process(builder, layer, x, z, rand, lyr, stage, startPos))
				return;
	}

	/**
	 * Checks if a piece can be placed at the given position and layer. This does
	 * also check if there are pieces on other layers (height variable) to avoid
	 * collisions. For example, a piece that goes 1 to 8 blocks below the height of
	 * its layer would have a height value of -1 (minus, because it goes down; 1
	 * layer = 8 blocks).
	 * 
	 * @return true if the piece can be placed, false if not
	 */
	public static boolean canPlacePieceWithHeight(DungeonBuilder builder, int layer, int x, int z, int width,
			int length, int layerHeight, boolean ignoreStartPosition) {

		int layers = builder.layers.length, lh = layer - layerHeight;
		if (layer > layers - 1 || layer < 0 || lh > layers || lh < 0)
			return false;
		if (x + width > Dungeon.SIZE - 1 || z + length > Dungeon.SIZE - 1)
			return false;

		boolean up = layerHeight > 0;
		int c = up ? -1 : 1, k = lh + c;

//		DungeonCrawl.LOGGER.debug(
//				"Checking, if a piece with a height of {} and a size of ({}|{}) can be placed at layer {} of {}. The c-variable is {}. Up: {}.",
//				layerHeight, width, length, layer, layers, c, up);

		for (int lyr = layer; up ? lyr > k : lyr < k; lyr += c) {
//			DungeonCrawl.LOGGER.debug("lyr: {}, k: {}", lyr, k);
			if (layers - lyr == 0)
				continue;
			else if (layers - lyr < 0)
				return false;
			for (int x0 = 0; x0 < width; x0++)
				for (int z0 = 0; z0 < length; z0++)
					if (!(ignoreStartPosition && lyr == layer && x0 == 0 && z0 == 0) && (builder.layers[lyr].segments[x + x0][z + z0] != null
							|| !builder.maps[lyr].isPositionFree(x + x0, z + z0)))
						return false;
		}
//		DungeonCrawl.LOGGER.debug("--");
		return true;
	}

	/**
	 * Marks the given area of the dungeon as occupied to prevent collision between
	 * multiple dungeon features. The given coordinates and size values are assumed
	 * to be correct. All parameters are the same as in canPlacePieceWithHeight().
	 */
	public static void mark(DungeonBuilder builder, int layer, int x, int z, int width, int length, int layerHeight) {
		int layers = builder.layers.length;
		boolean up = layerHeight > 0;
		int c = up ? -1 : 1, k = layer - layerHeight + c;

		for (int lyr = layer; up ? lyr > k : lyr < k; lyr += c) {
			if (layers - lyr == 0)
				continue;
			for (int x0 = 0; x0 < width; x0++)
				for (int z0 = 0; z0 < length; z0++)
					builder.maps[lyr].markPositionAsOccupied(new Position2D(x + x0, z + z0));
		}
	}

	@FunctionalInterface
	public static interface CorridorFeature {

		public boolean process(DungeonBuilder builder, DungeonLayer layer, int x, int z, Random rand, int lyr,
				int stage, BlockPos startPos);

	}

}
