package xiroc.dungeoncrawl.dungeon;

import java.util.HashMap;
import java.util.List;
import java.util.Random;

import com.google.common.collect.Lists;

import net.minecraft.util.Direction;
import net.minecraft.util.Rotation;
import net.minecraft.util.math.BlockPos;
import xiroc.dungeoncrawl.dungeon.DungeonPieces.DungeonPiece;
import xiroc.dungeoncrawl.dungeon.DungeonPieces.SideRoom;
import xiroc.dungeoncrawl.dungeon.segment.DungeonSegmentModelRegistry;
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
		OFFSET_DATA.put(DungeonSegmentModelRegistry.SIDE_ROOM_TNT.id, new Triple<Integer, Integer, Integer>(0, -1, 0));
	}

	static {
		CORRIDOR_FEATURES = Lists.newArrayList();
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
						&& rand.nextDouble() < 0.09) {
					layer.segments[x][z].openSide(RotationHelper.translateDirectionLeft(facing));
					DungeonPieces.SideRoom sideRoom = (SideRoom) RandomFeature.SIDE_ROOM.roll(rand);
					sideRoom.setOffset(OFFSET_DATA.getOrDefault(sideRoom.modelID, DEFAULT_OFFSET));
					sideRoom.setPosition(roomPos.x, roomPos.z);
					sideRoom.stage = stage;
					sideRoom.connectedSides = 1;
					sideRoom.setRealPosition(startPos.getX() + roomPos.x * 8, startPos.getY() - lyr * 16,
							startPos.getZ() + roomPos.z * 8);
					sideRoom.rotation = layer.segments[x][z].rotation.add(Rotation.COUNTERCLOCKWISE_90);
					layer.rotatePiece(layer.segments[x][z]);
					layer.segments[roomPos.x][roomPos.z] = sideRoom;
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
				feature.sides = layer.segments[x][z].sides;
				feature.connectedSides = layer.segments[x][z].connectedSides;
				feature.setRealPosition(startPos.getX() + x * 8, startPos.getY() - lyr * 16, startPos.getZ() + z * 8);
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
						&& layer.canPutDoubleRoom(part1Pos, facing) && rand.nextDouble() < 0.019) {

					layer.segments[x][z].openSide(RotationHelper.translateDirectionLeft(facing));

					DungeonPieces.Part part1 = new DungeonPieces.Part(null, DungeonPieces.DEFAULT_NBT);
					DungeonPieces.Part part2 = new DungeonPieces.Part(null, DungeonPieces.DEFAULT_NBT);

					part1.connectedSides = 1;

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

					part1.setRealPosition(startPos.getX() + part1Pos.x * 8, startPos.getY() - lyr * 16,
							startPos.getZ() + part1Pos.z * 8);
					part2.setRealPosition(startPos.getX() + part2Pos.x * 8, startPos.getY() - lyr * 16,
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

	@FunctionalInterface
	public static interface CorridorFeature {

		public boolean process(DungeonBuilder builder, DungeonLayer layer, int x, int z, Random rand, int lyr,
				int stage, BlockPos startPos);

	}

}
