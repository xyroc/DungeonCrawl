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

import com.google.common.collect.Lists;
import net.minecraft.util.Direction;
import net.minecraft.util.Rotation;
import net.minecraft.util.Tuple;
import net.minecraft.util.math.BlockPos;
import xiroc.dungeoncrawl.dungeon.model.DungeonModels;
import xiroc.dungeoncrawl.dungeon.piece.PlaceHolder;
import xiroc.dungeoncrawl.dungeon.piece.room.DungeonSideRoom;
import xiroc.dungeoncrawl.util.Position2D;
import xiroc.dungeoncrawl.util.Triple;
import xiroc.dungeoncrawl.util.WeightedRandomInteger;

import java.util.HashMap;
import java.util.List;
import java.util.Random;

public class DungeonFeatures {

    public static final HashMap<Integer, Triple<Integer, Integer, Integer>> OFFSET_DATA;

    public static final List<CorridorFeature> CORRIDOR_FEATURES;

    static {
        OFFSET_DATA = new HashMap<>();
        OFFSET_DATA.put(34, new Triple<>(0, -1, 0));
    }

    static {
        CORRIDOR_FEATURES = Lists.newArrayList();
//        CORRIDOR_FEATURES.add((builder, layer, x, z, rand, lyr, stage, startPos) -> {
//            if (stage > 1 && stage != 4 && rand.nextFloat() < 0.35) {
//                List<Direction> list = Lists.newArrayList();
//                Position2D center = new Position2D(x, z);
//
//                for (int i = 0; i < 4; i++) {
//                    if (layer.segments[x][z].reference.sides[i]) {
//                        Position2D pos = center.shift(Direction.byHorizontalIndex(i + 2), 1);
//                        PlaceHolder placeHolder = layer.segments[pos.x][pos.z];
//                        if (placeHolder != null) {
//                            if (placeHolder.reference.getType() == 0 && placeHolder.reference.connectedSides == 2
//                                    && ((DungeonCorridor) placeHolder.reference).isStraight()) {
//                                list.add(Direction.byHorizontalIndex(i + 2));
//                            } else {
//                                return false;
//                            }
//                        }
//                    }
//                }
//
//                if (list.size() > 1 && list.size() < 4) {
//
//                    for (Direction direction : list) {
//                        Position2D pos = center.shift(direction, 1);
//                        DungeonCorridorLarge corridor = new DungeonCorridorLarge(
//                                (DungeonCorridor) layer.segments[pos.x][pos.z].reference, 0);
//                        corridor.rotation = Orientation.getRotationFromFacing(direction.getOpposite());
//                        layer.segments[pos.x][pos.z] = new PlaceHolder(corridor)
//                                .withFlag(PlaceHolder.Flag.FIXED_ROTATION);
//                    }
//
//                    DungeonCorridorLarge corridor = new DungeonCorridorLarge(
//                            (DungeonCorridor) layer.segments[x][z].reference, 1);
//                    layer.segments[x][z] = new PlaceHolder(corridor).withFlag(PlaceHolder.Flag.FIXED_ROTATION);
//                    return true;
//                } else {
//                    return false;
//                }
//            }
//            return false;
//        });

        CORRIDOR_FEATURES.add(((builder, layer, x, z, rand, lyr, stage, startPos) -> {
            if (layer.segments[x][z].reference.connectedSides < 4 && rand.nextFloat() < 0.075) {
                Tuple<Position2D, Rotation> sideRoomData = layer.findSideRoomData(new Position2D(x, z));
                if (sideRoomData != null) {
                    DungeonSideRoom sideRoom = new DungeonSideRoom();
                    WeightedRandomInteger randomModel = DungeonModels.ModelCategory.get(DungeonModels.ModelCategory.SIDE_ROOM, DungeonModels.ModelCategory.getCategoryForStage(stage));
                    if (randomModel != null && randomModel.integers.length > 0) {
                        sideRoom.modelID = randomModel.roll(rand);
                    } else {
                        return false;
                    }

                    Direction dir = sideRoomData.getB().rotate(Direction.WEST);
                    sideRoom.openSide(dir);
                    sideRoom.setPosition(sideRoomData.getA());
                    sideRoom.setRotation(sideRoomData.getB());
                    sideRoom.stage = stage;

                    layer.segments[sideRoomData.getA().x][sideRoomData.getA().z] = new PlaceHolder(sideRoom).addFlag(PlaceHolder.Flag.FIXED_MODEL);
                    layer.segments[x][z].reference.openSide(dir.getOpposite());
                    layer.map.markPositionAsOccupied(sideRoomData.getA());
                    layer.rotatePiece(layer.segments[x][z]);
                    return true;
                }
            }
            return false;
        }));

//		CORRIDOR_FEATURES.add((builder, layer, x, z, rand, lyr, stage, startPos) -> {
//			if (rand.nextDouble() < 0.06 && canPlacePieceWithHeight(builder, lyr, x, z, 1, 1, -2, true)) {
//				DungeonCorridorHole hole = new DungeonCorridorHole(null, DungeonPiece.DEFAULT_NBT);
//				hole.sides = layer.segments[x][z].reference.sides;
//				hole.connectedSides = layer.segments[x][z].reference.connectedSides;
//				hole.setRealPosition(startPos.getX() + x * 8, startPos.getY() - lyr * 8, startPos.getZ() + z * 8);
//				hole.stage = stage;
//				hole.lava = stage == 2;
//				layer.segments[x][z] = new PlaceHolder(hole);
//				mark(builder, lyr, x, z, 1, 1, -2);
//				return true;
//			} else
//				return false;
//		});
//		CORRIDOR_FEATURES.add((builder, layer, x, z, rand, lyr, stage, startPos) -> {
//			if (layer.segments[x][z].reference.connectedSides == 2
//					&& (layer.segments[x][z].reference.sides[0] && layer.segments[x][z].reference.sides[2]
//							|| layer.segments[x][z].reference.sides[1] && layer.segments[x][z].reference.sides[3])
//					&& rand.nextDouble() < 0.08) {
//				((DungeonCorridor) layer.segments[x][z].reference).specialType = lyr > 0 ? 1 : 2;
//				return true;
//			}
//			return false;
//		});
//		CORRIDOR_FEATURES.add((builder, layer, x, z, rand, lyr, stage, startPos) -> {
//			if (layer.segments[x][z].reference.getType() == 0 && layer.segments[x][z].reference.connectedSides < 4
//					&& (lyr == 0 || (builder.layers[lyr - 1].segments[x][z] == null
//							|| builder.layers[lyr - 1].segments[x][z].reference.getType() != 2))) {
//				Direction facing = RotationHelper.translateDirection(Direction.EAST,
//						layer.segments[x][z].reference.rotation);
//				Position2D pos = new Position2D(x, z);
//				Position2D roomPos = pos.shift(RotationHelper.translateDirectionLeft(facing), 1);
//				if (roomPos.isValid(layer.width, layer.length) && layer.segments[roomPos.x][roomPos.z] == null
//						&& builder.maps[lyr].isPositionFree(roomPos.x, roomPos.z) && rand.nextDouble() < 0.09) {
//					layer.segments[x][z].reference.openSide(RotationHelper.translateDirectionLeft(facing));
//					DungeonSideRoom sideRoom = (DungeonSideRoom) RandomFeature.SIDE_ROOM.roll(rand);
//					sideRoom.setOffset(OFFSET_DATA.getOrDefault(sideRoom.modelID, DEFAULT_OFFSET));
//					sideRoom.setPosition(roomPos.x, roomPos.z);
//					sideRoom.stage = stage;
//					sideRoom.connectedSides = 1;
//					sideRoom.setRealPosition(startPos.getX() + roomPos.x * 8, startPos.getY() - lyr * 8,
//							startPos.getZ() + roomPos.z * 8);
//					sideRoom.rotation = layer.segments[x][z].reference.rotation.add(Rotation.COUNTERCLOCKWISE_90);
//					layer.rotatePiece(layer.segments[x][z]);
//					layer.segments[roomPos.x][roomPos.z] = new PlaceHolder(sideRoom);
//					builder.maps[lyr].markPositionAsOccupied(roomPos);
//					return true;
//				}
//			}
//			return false;
//		});
//		CORRIDOR_FEATURES.add((builder, layer, x, z, rand, lyr, stage, startPos) -> {
//			if (layer.segments[x][z].reference.connectedSides == 2 && rand.nextDouble() < 0.07
//					&& (layer.segments[x][z].reference.sides[0] && layer.segments[x][z].reference.sides[2]
//							|| layer.segments[x][z].reference.sides[1] && layer.segments[x][z].reference.sides[3])) {
//				DungeonPiece feature = RandomFeature.CORRIDOR_FEATURE.roll(rand);
//				if (feature.getType() == 4 && !canPlacePieceWithHeight(builder, lyr, x, z, 1, 1, -1, true))
//					feature = new DungeonCorridorTrap(null, DungeonPiece.DEFAULT_NBT);
//				feature.sides = layer.segments[x][z].reference.sides;
//				feature.connectedSides = layer.segments[x][z].reference.connectedSides;
//				feature.setRealPosition(startPos.getX() + x * 8, startPos.getY() - lyr * 8, startPos.getZ() + z * 8);
//				feature.stage = stage;
//				feature.rotation = layer.segments[x][z].reference.rotation;
//				layer.segments[x][z] = new PlaceHolder(feature);
//				return true;
//			}
//			return false;
//		});
//		CORRIDOR_FEATURES.add((builder, layer, x, z, rand, lyr, stage, startPos) -> {
//			if (layer.segments[x][z].reference.getType() == 0 && layer.segments[x][z].reference.connectedSides < 4) {
//				Direction facing = RotationHelper.translateDirection(Direction.EAST,
//						layer.segments[x][z].reference.rotation);
//				Position2D pos = new Position2D(x, z);
//				Position2D part1Pos = pos.shift(RotationHelper.translateDirectionLeft(facing), 1);
//				Position2D part2Pos = part1Pos.shift(facing, 1);
//				if (part1Pos.isValid(layer.width, layer.length) && part2Pos.isValid(layer.width, layer.length)
//						&& layer.canPutDoubleRoom(part1Pos, facing) && rand.nextDouble() < 0.023) {
//
//					layer.segments[x][z].reference.openSide(RotationHelper.translateDirectionLeft(facing));
//
//					DungeonPart part1 = new DungeonPart(null, DungeonPiece.DEFAULT_NBT);
//					DungeonPart part2 = new DungeonPart(null, DungeonPiece.DEFAULT_NBT);
//
//					part1.connectedSides = 1;
//					part2.connectedSides = 1;
//
//					part1.treasureType = 1;
//					part2.treasureType = 1;
//
//					part1.stage = stage;
//					part2.stage = stage;
//
//					part1.setPosition(part1Pos.x, part1Pos.z);
//					part2.setPosition(part2Pos.x, part2Pos.z);
//
//					part1.setRotation(layer.segments[x][z].reference.rotation.add(Rotation.COUNTERCLOCKWISE_90));
//					part2.setRotation(layer.segments[x][z].reference.rotation.add(Rotation.COUNTERCLOCKWISE_90));
//
//					layer.rotatePiece(layer.segments[x][z]);
//
//					int id = 27;
//					part1.set(id, 0, 0, 0, 8, 8, 8);
//					part2.set(id, 0, 0, 8, 8, 8, 8);
//
//					part1.setRealPosition(startPos.getX() + part1Pos.x * 8, startPos.getY() - lyr * 8,
//							startPos.getZ() + part1Pos.z * 8);
//					part2.setRealPosition(startPos.getX() + part2Pos.x * 8, startPos.getY() - lyr * 8,
//							startPos.getZ() + part2Pos.z * 8);
//
//					layer.segments[part1Pos.x][part1Pos.z] = new PlaceHolder(part1);
//					layer.segments[part2Pos.x][part2Pos.z] = new PlaceHolder(part2);
//
//					return true;
//				}
//				return false;
//			}
//			return false;
//		});
    }

    public static void processCorridor(DungeonBuilder builder, DungeonLayer layer, int x, int z, Random rand, int lyr,
                                       int stage, BlockPos startPos) {
        for (CorridorFeature corridorFeature : CORRIDOR_FEATURES)
            if (corridorFeature.process(builder, layer, x, z, rand, lyr, stage, startPos))
                return;
    }

    /**
     * Checks if a piece can be placed at the given position.
     *
     * @return true if the piece can be placed, false if not
     */
    public static boolean canPlacePiece(DungeonLayer layer, int x, int z, int width, int length,
                                        boolean ignoreStartPosition) {
        if (x + width > Dungeon.SIZE || z + length > Dungeon.SIZE || x < 0 || z < 0)
            return false;

        for (int x0 = 0; x0 < width; x0++) {
            for (int z0 = 0; z0 < length; z0++) {
                if (!(ignoreStartPosition && x0 == 0 && z0 == 0)
                        && (layer.segments[x + x0][z + z0] != null || !layer.map.isPositionFree(x + x0, z + z0))) {
                    return false;
                }
            }
        }
        return true;
    }

    /**
     * Checks if a piece can be placed at the given position and layer. This does
     * also check if there are pieces on other layers (height variable) to avoid
     * collisions. For example, a piece that goes 1 to 9 blocks below the height of
     * its layer would have a height value of -1 (minus, because it goes down; 1
     * layer = 9 blocks).
     *
     * @return true if the piece can be placed, false if not
     */
    public static boolean canPlacePieceWithHeight(DungeonBuilder builder, int layer, int x, int z, int width,
                                                  int length, int layerHeight, boolean ignoreStartPosition) {
        /*
         * x + width - 1 > Dungeon.SIZE -1 <=> x + width > Dungeon.SIZE
         * (same for z of course)
         */
        if (x + width > Dungeon.SIZE || z + length > Dungeon.SIZE || x < 0 || z < 0)
            return false;

        int layers = builder.layers.length, lh = layer - layerHeight;
        if (layer > layers - 1 || layer < 0 || lh > layers || lh < 0)
            return false;

        boolean up = layerHeight > 0;
        int c = up ? -1 : 1, k = lh + c;

        for (int lyr = layer; up ? lyr > k : lyr < k; lyr += c) {
            if (layers - lyr == 0)
                continue;
            else if (layers - lyr < 0)
                return false;

            for (int x0 = 0; x0 < width; x0++) {
                for (int z0 = 0; z0 < length; z0++) {
                    if (!(ignoreStartPosition && lyr == layer && x0 == 0 && z0 == 0)
                            && (builder.layers[lyr].segments[x + x0][z + z0] != null
                            || !builder.maps[lyr].isPositionFree(x + x0, z + z0))) {
                        return false;
                    }
                }
            }
        }
        return true;
    }

    /**
     * Marks the given area of the dungeon as occupied to prevent collision between
     * multiple dungeon features. The given coordinates and size values are assumed
     * to be correct. All parameters are the same as in #canPlacePiece.
     */
    public static void mark(DungeonLayer layer, int x, int z, int width, int length) {
        for (int x0 = 0; x0 < width; x0++) {
            for (int z0 = 0; z0 < length; z0++) {
                layer.map.map[x][z] = true;
            }
        }
    }

    /**
     * Marks the given area of the dungeon as occupied to prevent collision between
     * multiple dungeon features. The given coordinates and size values are assumed
     * to be correct. All parameters are the same as in #canPlacePieceWithHeight.
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
    public interface CorridorFeature {

        boolean process(DungeonBuilder builder, DungeonLayer layer, int x, int z, Random rand, int lyr,
                        int stage, BlockPos startPos);

    }

}
