package xiroc.dungeoncrawl.dungeon;

import java.util.List;

/*
 * DungeonCrawl (C) 2019 XYROC (XIROC1337), All Rights Reserved 
 */

import java.util.Random;

import net.minecraft.block.Blocks;
import net.minecraft.util.Direction;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Rotation;
import net.minecraft.util.Tuple;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import xiroc.dungeoncrawl.DungeonCrawl;
import xiroc.dungeoncrawl.config.Config;
import xiroc.dungeoncrawl.dungeon.DungeonPieces.DungeonPiece;
import xiroc.dungeoncrawl.dungeon.DungeonStatTracker.LayerStatTracker;
import xiroc.dungeoncrawl.dungeon.treasure.Treasure;
import xiroc.dungeoncrawl.util.BossEntry;
import xiroc.dungeoncrawl.util.Position2D;
import xiroc.dungeoncrawl.util.RotationHelper;

public class DungeonLayer {

	/*
	 * NORTH|-Z EAST|+X SOUTH|+Z WEST|-X
	 */

	public static final int[] LARGE_ROOMS = new int[] { 25, 25, 35 };

	public DungeonLayerType type;
	public DungeonPiece[][] segments;

	public Position2D start;
	public Position2D end;

	public int width; // x
	public int length; // z
	public LayerStatTracker statTracker;

	public DungeonLayer(DungeonLayerType type) {
		this(type, 16, 16);
	}

	public DungeonLayer(DungeonLayerType type, int width, int length) {
		this.type = type;
		this.width = width;
		this.length = length;
		this.statTracker = new LayerStatTracker();
	}

	public void buildMap(DungeonBuilder builder, List<DungeonPiece> pieces, Random rand, Position2D start, int layer,
			boolean lastLayer) {
		this.segments = new DungeonPiece[this.width][this.length];
		DungeonLayerMap map = new DungeonLayerMap(this.width, this.length);
		if (!map.markPositionAsOccupied(start))
			DungeonCrawl.LOGGER.error("Failed to mark start [" + start.x + ", " + start.z + "] as occupied.");
		Position2D end = lastLayer ? findLargeRoomPosWithMaxDistance(start) : map.getRandomFreePosition(rand);
		this.start = start;
		this.end = end;
		this.segments[start.x][start.z] = new DungeonPieces.StairsBot(null, DungeonPieces.DEFAULT_NBT);
		if (lastLayer) {
			if (end != null) {
				if (Config.ENABLE_DUNGEON_BOSS.get()) {
					BossEntry boss = DungeonBuilder.getRandomBoss(rand);
					if (boss != null) {
						// --- Loot Room ---

						DungeonPieces.Part lootRoom1 = new DungeonPieces.Part(null, DungeonPieces.DEFAULT_NBT);
						DungeonPieces.Part lootRoom2 = new DungeonPieces.Part(null, DungeonPieces.DEFAULT_NBT);
						DungeonPieces.Part lootRoom3 = new DungeonPieces.Part(null, DungeonPieces.DEFAULT_NBT);
						DungeonPieces.Part lootRoom4 = new DungeonPieces.Part(null, DungeonPieces.DEFAULT_NBT);

						lootRoom1.treasureType = 7;
						lootRoom2.treasureType = 7;
						lootRoom3.treasureType = 7;
						lootRoom4.treasureType = 7;

						lootRoom1.rotation = Rotation.NONE;
						lootRoom2.rotation = Rotation.NONE;
						lootRoom3.rotation = Rotation.NONE;
						lootRoom4.rotation = Rotation.NONE;

						lootRoom1.walls = lootRoom2.walls = lootRoom3.walls = lootRoom4.walls = true;

						lootRoom1.set(28, 0, 0, 0, 8, 16, 8);
						lootRoom2.set(28, 8, 0, 0, 8, 16, 8);
						lootRoom3.set(28, 8, 0, 8, 8, 16, 8);
						lootRoom4.set(28, 0, 0, 8, 8, 16, 8);

						lootRoom1.setPosition(end.x, end.z);
						lootRoom2.setPosition(end.x + 1, end.z);
						lootRoom3.setPosition(end.x + 1, end.z + 1);
						lootRoom4.setPosition(end.x, end.z + 1);

						lootRoom1.setRealPosition(builder.startPos.getX() + lootRoom1.posX * 8,
								builder.startPos.getY() - layer * 16 - 16,
								builder.startPos.getZ() + lootRoom1.posZ * 8);
						lootRoom2.setRealPosition(builder.startPos.getX() + lootRoom2.posX * 8,
								builder.startPos.getY() - layer * 16 - 16,
								builder.startPos.getZ() + lootRoom2.posZ * 8);
						lootRoom3.setRealPosition(builder.startPos.getX() + lootRoom3.posX * 8,
								builder.startPos.getY() - layer * 16 - 16,
								builder.startPos.getZ() + lootRoom3.posZ * 8);
						lootRoom4.setRealPosition(builder.startPos.getX() + lootRoom4.posX * 8,
								builder.startPos.getY() - layer * 16 - 16,
								builder.startPos.getZ() + lootRoom4.posZ * 8);

						lootRoom1.theme = lootRoom2.theme = lootRoom3.theme = lootRoom4.theme = 1;

						lootRoom1.sides[0] = false;
						lootRoom1.sides[1] = true;
						lootRoom1.sides[2] = true;
						lootRoom1.sides[3] = false;

						lootRoom2.sides[0] = false;
						lootRoom2.sides[1] = false;
						lootRoom2.sides[2] = true;
						lootRoom2.sides[3] = true;

						lootRoom3.sides[0] = true;
						lootRoom3.sides[1] = false;
						lootRoom3.sides[2] = false;
						lootRoom3.sides[3] = true;

						lootRoom4.sides[0] = true;
						lootRoom4.sides[1] = true;
						lootRoom4.sides[2] = false;
						lootRoom4.sides[3] = false;

						pieces.add(lootRoom1);
						pieces.add(lootRoom2);
						pieces.add(lootRoom3);
						pieces.add(lootRoom4);
						
						// --- Boss Room ---

						DungeonPieces.PartWithEntity part1 = new DungeonPieces.PartWithEntity(null,
								DungeonPieces.DEFAULT_NBT);
						DungeonPieces.Part part2 = new DungeonPieces.Part(null, DungeonPieces.DEFAULT_NBT);
						DungeonPieces.Part part3 = new DungeonPieces.Part(null, DungeonPieces.DEFAULT_NBT);
						DungeonPieces.Part part4 = new DungeonPieces.Part(null, DungeonPieces.DEFAULT_NBT);

						part1.treasureType = 7;
						part2.treasureType = 7;
						part3.treasureType = 7;
						part4.treasureType = 7;

						part1.rotation = Rotation.NONE;
						part2.rotation = Rotation.NONE;
						part3.rotation = Rotation.NONE;
						part4.rotation = Rotation.NONE;

						part1.walls = part2.walls = part3.walls = part4.walls = true;

						part1.entityName = new ResourceLocation(boss.entityName);
						part1.nbt = boss.createTag();

						part1.set(36, 0, 0, 0, 8, 16, 8, 8, 2, 8);
						part2.set(36, 8, 0, 0, 8, 16, 8);
						part3.set(36, 8, 0, 8, 8, 16, 8);
						part4.set(36, 0, 0, 8, 8, 16, 8);

						part1.setPosition(end.x, end.z);
						part2.setPosition(end.x + 1, end.z);
						part3.setPosition(end.x + 1, end.z + 1);
						part4.setPosition(end.x, end.z + 1);

						part1.sides[0] = false;
						part1.sides[1] = true;
						part1.sides[2] = true;
						part1.sides[3] = false;

						part2.sides[0] = false;
						part2.sides[1] = false;
						part2.sides[2] = true;
						part2.sides[3] = true;

						part3.sides[0] = true;
						part3.sides[1] = false;
						part3.sides[2] = false;
						part3.sides[3] = true;

						part4.sides[0] = true;
						part4.sides[1] = true;
						part4.sides[2] = false;
						part4.sides[3] = false;

						this.segments[end.x][end.z] = part1;
						this.segments[end.x + 1][end.z] = part2;
						this.segments[end.x + 1][end.z + 1] = part3;
						this.segments[end.x][end.z + 1] = part4;

						Position2D part1Pos = end, part2Pos = new Position2D(end.x + 1, end.z),
								part3Pos = new Position2D(end.x + 1, end.z + 1),
								part4Pos = new Position2D(end.x, end.z + 1);
						
						map.markPositionAsOccupied(end);
						map.markPositionAsOccupied(part2Pos);
						map.markPositionAsOccupied(part3Pos);
						map.markPositionAsOccupied(part4Pos);

						DungeonPieces.Part connectedPart = lootRoom4;
						
						Tuple<Position2D, Rotation> data = findSideRoomData(part4Pos);

						if (data == null) {
							data = findSideRoomData(part3Pos);
							connectedPart = lootRoom3;
						}
						if (data == null) {
							data = findSideRoomData(part2Pos);
							connectedPart = lootRoom2;
						}
						if (data == null) {
							data = findSideRoomData(part1Pos);
							connectedPart = lootRoom1;
						}
						
						if (data != null) {
							Position2D stairPos = data.getA();
							Direction side = RotationHelper.translateDirection(Direction.EAST, data.getB());
							DungeonPieces.StairsTop stairsTop = new DungeonPieces.StairsTop(null,
									DungeonPieces.DEFAULT_NBT);
							stairsTop.setPosition(stairPos.x, stairPos.z);
							stairsTop.openSide(side.getOpposite());
							stairsTop.theme = 1;
							map.markPositionAsOccupied(stairPos);
							this.segments[stairPos.x][stairPos.z] = stairsTop;

							DungeonPieces.Stairs stairs = new DungeonPieces.Stairs(null, DungeonPieces.DEFAULT_NBT);
							stairs.setRealPosition(builder.startPos.getX() + stairPos.x * 8,
									builder.startPos.getY() - layer * 16 - 8, builder.startPos.getZ() + stairPos.z * 8);
							stairs.theme = 1;
							
							DungeonPieces.StairsBot stairsBot = new DungeonPieces.StairsBot(null,
									DungeonPieces.DEFAULT_NBT);
							stairsBot.setPosition(stairPos.x, stairPos.z);
							stairsBot.setRealPosition(builder.startPos.getX() + stairPos.x * 8,
									builder.startPos.getY() - layer * 16 - 16, builder.startPos.getZ() + stairPos.z * 8);
							stairsBot.openSide(side.getOpposite());
							stairsBot.theme = 1;
							
							pieces.add(stairs);
							pieces.add(stairsBot);
							
							connectedPart.openSide(side);
						}
					}
				} else {
					DungeonPieces.Part part1 = new DungeonPieces.Part(null, DungeonPieces.DEFAULT_NBT);
					DungeonPieces.Part part2 = new DungeonPieces.Part(null, DungeonPieces.DEFAULT_NBT);
					DungeonPieces.Part part3 = new DungeonPieces.Part(null, DungeonPieces.DEFAULT_NBT);
					DungeonPieces.Part part4 = new DungeonPieces.Part(null, DungeonPieces.DEFAULT_NBT);

					part1.treasureType = 7;
					part2.treasureType = 7;
					part3.treasureType = 7;
					part4.treasureType = 7;

					part1.rotation = Rotation.NONE;
					part2.rotation = Rotation.NONE;
					part3.rotation = Rotation.NONE;
					part4.rotation = Rotation.NONE;

					part1.walls = part2.walls = part3.walls = part4.walls = true;

					part1.set(28, 0, 0, 0, 8, 16, 8);
					part2.set(28, 8, 0, 0, 8, 16, 8);
					part3.set(28, 8, 0, 8, 8, 16, 8);
					part4.set(28, 0, 0, 8, 8, 16, 8);

					part1.setPosition(end.x, end.z);
					part2.setPosition(end.x + 1, end.z);
					part3.setPosition(end.x + 1, end.z + 1);
					part4.setPosition(end.x, end.z + 1);

					part1.sides[0] = false;
					part1.sides[1] = true;
					part1.sides[2] = true;
					part1.sides[3] = false;

					part2.sides[0] = false;
					part2.sides[1] = false;
					part2.sides[2] = true;
					part2.sides[3] = true;

					part3.sides[0] = true;
					part3.sides[1] = false;
					part3.sides[2] = false;
					part3.sides[3] = true;

					part4.sides[0] = true;
					part4.sides[1] = true;
					part4.sides[2] = false;
					part4.sides[3] = false;

					this.segments[end.x][end.z] = part1;
					this.segments[end.x + 1][end.z] = part2;
					this.segments[end.x + 1][end.z + 1] = part3;
					this.segments[end.x][end.z + 1] = part4;

					map.markPositionAsOccupied(end);
					map.markPositionAsOccupied(new Position2D(end.x + 1, end.z));
					map.markPositionAsOccupied(new Position2D(end.x + 1, end.z + 1));
					map.markPositionAsOccupied(new Position2D(end.x, end.z + 1));
				}
			} else {
				DungeonCrawl.LOGGER.warn(
						"Failed to find a final room position for the last layer of a dungeon. This should never happen by default, but might be caused by an invalid config. If you didnt change the config or are sure that the cause of this is something else, please contact the mod author (The best way to do this is to open an issue on https://github.com/XYROC/DungeonCrawl). Layer map start pos: ({}|{})",
						start.x, start.z);
				Position2D pos = map.getRandomFreePosition(rand);
				this.segments[pos.x][pos.z] = new DungeonPieces.Room(null, DungeonPieces.DEFAULT_NBT);
			}

		} else
			this.segments[end.x][end.z] = new DungeonPieces.StairsTop(null, DungeonPieces.DEFAULT_NBT);
		this.buildConnection(start, end);
		this.extend(map, start, end, rand);
		if (layer == 0) {
			Tuple<Position2D, Rotation> sideRoomData = findStarterRoomData(start);
			if (sideRoomData != null) {
				DungeonPieces.SideRoom room = new DungeonPieces.SideRoom(null, DungeonPieces.DEFAULT_NBT);
				room.modelID = 34;
				Direction dir = RotationHelper.translateDirection(Direction.WEST, sideRoomData.getB());
				room.openSide(dir);
				DungeonCrawl.LOGGER.info(dir);
				room.setPosition(sideRoomData.getA().x, sideRoomData.getA().z);
				room.setRotation(sideRoomData.getB());
				room.treasureType = Treasure.Type.SUPPLY;
				map.markPositionAsOccupied(sideRoomData.getA());
				this.segments[sideRoomData.getA().x][sideRoomData.getA().z] = room;
				Position2D connectedSegment = sideRoomData.getA().shift(dir, 1);
				DungeonCrawl.LOGGER.info(this.segments[connectedSegment.x][connectedSegment.z]);
				if (this.segments[connectedSegment.x][connectedSegment.z] != null) {
					this.segments[connectedSegment.x][connectedSegment.z].openSide(dir.getOpposite());
					rotatePiece(this.segments[connectedSegment.x][connectedSegment.z]);
				}
			}
		}
	}

	public void extend(DungeonLayerMap map, Position2D start, Position2D end, Random rand) {
		int additionalFeatures = Config.LAYER_ADDITIONS_MIN.get() + rand.nextInt(Config.LAYER_ADDITIONS_EXTRA.get());
		Position2D[] additions = new Position2D[additionalFeatures];
		for (int i = 0; i < additionalFeatures; i++) {
			additions[i] = map.getRandomFreePosition(rand);
			if (additions[i] == null) {
				DungeonCrawl.LOGGER.warn(
						"Failed to place {} more rooms because all free positions are already taken. Please decrease the layer_min_additions and/or the layer_extra_additions value in the config (dungeon_crawl.toml) to avoid this issue.",
						additionalFeatures - i);
				return;
			}
			if (rand.nextFloat() < 0.1) {
				Position2D largeRoomPos = getLargeRoomPos(additions[i]);
				if (largeRoomPos != null) {
					int roomID = getRandomLargeRoom(rand);
					DungeonPieces.Part part1 = new DungeonPieces.Part(null, DungeonPieces.DEFAULT_NBT);
					DungeonPieces.Part part2 = new DungeonPieces.Part(null, DungeonPieces.DEFAULT_NBT);
					DungeonPieces.Part part3 = new DungeonPieces.Part(null, DungeonPieces.DEFAULT_NBT);
					DungeonPieces.Part part4 = new DungeonPieces.Part(null, DungeonPieces.DEFAULT_NBT);

					part1.treasureType = 0;
					part2.treasureType = 0;
					part3.treasureType = 0;
					part4.treasureType = 0;

					part1.rotation = Rotation.NONE;
					part2.rotation = Rotation.NONE;
					part3.rotation = Rotation.NONE;
					part4.rotation = Rotation.NONE;

					part1.walls = part2.walls = part3.walls = part4.walls = true;

					part1.set(roomID, 0, 0, 0, 8, 16, 8);
					part2.set(roomID, 8, 0, 0, 8, 16, 8);
					part3.set(roomID, 8, 0, 8, 8, 16, 8);
					part4.set(roomID, 0, 0, 8, 8, 16, 8);

					part1.setPosition(largeRoomPos.x, largeRoomPos.z);
					part2.setPosition(largeRoomPos.x + 1, largeRoomPos.z);
					part3.setPosition(largeRoomPos.x + 1, largeRoomPos.z + 1);
					part4.setPosition(largeRoomPos.x, largeRoomPos.z + 1);

					part1.sides[0] = false;
					part1.sides[1] = true;
					part1.sides[2] = true;
					part1.sides[3] = false;

					part2.sides[0] = false;
					part2.sides[1] = false;
					part2.sides[2] = true;
					part2.sides[3] = true;

					part3.sides[0] = true;
					part3.sides[1] = false;
					part3.sides[2] = false;
					part3.sides[3] = true;

					part4.sides[0] = true;
					part4.sides[1] = true;
					part4.sides[2] = false;
					part4.sides[3] = false;

					this.segments[largeRoomPos.x][largeRoomPos.z] = part1;
					this.segments[largeRoomPos.x + 1][largeRoomPos.z] = part2;
					this.segments[largeRoomPos.x + 1][largeRoomPos.z + 1] = part3;
					this.segments[largeRoomPos.x][largeRoomPos.z + 1] = part4;

					map.markPositionAsOccupied(new Position2D(largeRoomPos.x, largeRoomPos.z));
					map.markPositionAsOccupied(new Position2D(largeRoomPos.x + 1, largeRoomPos.z));
					map.markPositionAsOccupied(new Position2D(largeRoomPos.x + 1, largeRoomPos.z + 1));
					map.markPositionAsOccupied(new Position2D(largeRoomPos.x, largeRoomPos.z + 1));
					continue;
				}
			}
			DungeonPiece room = new DungeonPieces.Room(null, DungeonPieces.DEFAULT_NBT);
			room.setPosition(additions[i].x, additions[i].z);
			if (this.segments[additions[i].x][additions[i].z] != null) {
//				DungeonCrawl.LOGGER.debug("Placing a room into a piece at (" + additions[i].x + " / " + additions[i].z + "). " + " Replaced piece: " + this.segments[additions[i].x][additions[i].z]);
				room.sides = this.segments[additions[i].x][additions[i].z].sides;
				room.connectedSides = this.segments[additions[i].x][additions[i].z].connectedSides;
			}
			this.segments[additions[i].x][additions[i].z] = room;
		}
		for (int i = 0; i < additionalFeatures; i++) {
			Position2D one = additions[i];
			switch (rand.nextInt(2)) {
			case 0:
				this.buildConnection(rand.nextBoolean() ? start : end, one);
				break;
			case 1:
				this.processAddition(additions, start, end, one, rand);
				break;
			}
		}
	}

	public void buildConnection(Position2D start, Position2D end) {
		int startX = start.x;
		int startZ = start.z;
		int endX = end.x;
		int endZ = end.z;
		if (startX == endX && startZ == endZ)
			return;
		if (startX > endX) {
			this.segments[startX][startZ].openSide(Direction.WEST);
			for (int x = startX; x > (startZ == endZ ? endX + 1 : endX); x--) {
				if (this.segments[x - 1][startZ] != null) {
					this.segments[x - 1][startZ].openSide(
							(x - 1) == endX ? startZ < endZ ? Direction.SOUTH : Direction.NORTH : Direction.WEST);
					this.segments[x - 1][startZ].openSide(Direction.EAST);
					this.rotatePiece(this.segments[x - 1][startZ]);
					continue;
				}
				DungeonPiece corridor = new DungeonPieces.Corridor(null, DungeonPieces.DEFAULT_NBT);
				corridor.setPosition(x - 1, startZ);
				corridor.setRotation((x - 1) == endX
						? RotationHelper.getRotationFromCW90DoubleFacing(
								startZ > endZ ? Direction.NORTH : Direction.SOUTH, Direction.EAST)
						: RotationHelper.getRotationFromFacing(Direction.WEST));
				corridor.openSide((x - 1) == endX ? startZ < endZ ? Direction.SOUTH : Direction.NORTH : Direction.WEST);
				corridor.openSide(Direction.EAST);
				this.segments[x - 1][startZ] = corridor;
			}
			if (startZ > endZ) {
				this.segments[endX][endZ].openSide(Direction.SOUTH);
				for (int z = startZ; z > endZ + 1; z--) {
					if (this.segments[endX][z - 1] != null) {
						this.segments[endX][z - 1].openSide(Direction.SOUTH);
						this.segments[endX][z - 1].openSide((z - 1) == endZ ? Direction.WEST : Direction.NORTH);
						this.rotatePiece(this.segments[endX][z - 1]);
						continue;
					}
					DungeonPiece corridor = new DungeonPieces.Corridor(null, DungeonPieces.DEFAULT_NBT);
					corridor.setPosition(endX, z - 1);
					corridor.setRotation((z - 1) == endZ
							? RotationHelper.getRotationFromCW90DoubleFacing(Direction.NORTH, Direction.WEST)
							: RotationHelper.getRotationFromFacing(Direction.NORTH));
					corridor.openSide(Direction.SOUTH);
					corridor.openSide((z - 1) == endZ ? Direction.WEST : Direction.NORTH);
					this.segments[endX][z - 1] = corridor;
				}
			} else if (startZ < endZ) {
				this.segments[endX][endZ].openSide(Direction.NORTH);
				for (int z = startZ; z < endZ - 1; z++) {
					if (this.segments[endX][z + 1] != null) {
						this.segments[endX][z + 1].openSide((z + 1) == endZ ? Direction.WEST : Direction.SOUTH);
						this.segments[endX][z + 1].openSide(Direction.NORTH);
						this.rotatePiece(this.segments[endX][z + 1]);
						continue;
					}
					DungeonPiece corridor = new DungeonPieces.Corridor(null, DungeonPieces.DEFAULT_NBT);
					corridor.setPosition(endX, z + 1);
					corridor.setRotation((z + 1) == endZ
							? RotationHelper.getRotationFromCW90DoubleFacing(Direction.NORTH, Direction.WEST)
							: RotationHelper.getRotationFromFacing(Direction.SOUTH));
					corridor.openSide((z + 1) == endZ ? Direction.WEST : Direction.SOUTH);
					corridor.openSide(Direction.NORTH);
					this.segments[endX][z + 1] = corridor;
				}
			} else
				this.segments[endX][endZ].openSide(Direction.EAST);
		} else if (startX < endX) {
			this.segments[startX][startZ].openSide(Direction.EAST);
			for (int x = startX; x < (startZ == endZ ? endX - 1 : endX); x++) {
				if (this.segments[x + 1][startZ] != null) {
					this.segments[x + 1][startZ].openSide(
							(x + 1) == endX ? startZ < endZ ? Direction.SOUTH : Direction.NORTH : Direction.EAST);
					this.segments[x + 1][startZ].openSide(Direction.WEST);
					this.rotatePiece(this.segments[x + 1][startZ]);
					continue;
				}
				DungeonPiece corridor = new DungeonPieces.Corridor(null, DungeonPieces.DEFAULT_NBT);
				corridor.setPosition(x + 1, startZ);
				corridor.setRotation((x + 1) == endX
						? RotationHelper.getRotationFromCW90DoubleFacing(
								startZ > endZ ? Direction.NORTH : Direction.SOUTH, Direction.WEST)
						: RotationHelper.getRotationFromFacing(Direction.EAST));
				corridor.openSide((x + 1) == endX ? startZ < endZ ? Direction.SOUTH : Direction.NORTH : Direction.EAST);
				corridor.openSide(Direction.WEST);
				this.segments[x + 1][startZ] = corridor;
			}
			if (startZ > endZ) {
				this.segments[endX][endZ].openSide(Direction.SOUTH);
				for (int z = startZ; z > endZ + 1; z--) {
					if (this.segments[endX][z - 1] != null) {
						this.segments[endX][z - 1].openSide(Direction.SOUTH);
						this.segments[endX][z - 1].openSide((z - 1) == endZ ? Direction.EAST : Direction.NORTH);
						this.rotatePiece(this.segments[endX][z - 1]);
						continue;
					}
					DungeonPiece corridor = new DungeonPieces.Corridor(null, DungeonPieces.DEFAULT_NBT);
					corridor.setPosition(endX, z - 1);
					corridor.setRotation((z - 1) == endZ
							? RotationHelper.getRotationFromCW90DoubleFacing(Direction.NORTH, Direction.WEST)
							: RotationHelper.getRotationFromFacing(Direction.NORTH));
					corridor.openSide(Direction.SOUTH);
					corridor.openSide((z - 1) == endZ ? Direction.WEST : Direction.NORTH);
					this.segments[endX][z - 1] = corridor;
				}
			} else if (startZ < endZ) {
				this.segments[endX][endZ].openSide(Direction.NORTH);
				for (int z = startZ; z < endZ - 1; z++) {
					if (this.segments[endX][z + 1] != null) {
						this.segments[endX][z + 1].openSide((z + 1) == endZ ? Direction.EAST : Direction.SOUTH);
						this.segments[endX][z + 1].openSide(Direction.NORTH);
						this.rotatePiece(this.segments[endX][z + 1]);
						continue;
					}
					DungeonPiece corridor = new DungeonPieces.Corridor(null, DungeonPieces.DEFAULT_NBT);
					corridor.setPosition(endX, z + 1);
					corridor.setRotation((z + 1) == endZ
							? RotationHelper.getRotationFromCW90DoubleFacing(Direction.NORTH, Direction.WEST)
							: RotationHelper.getRotationFromFacing(Direction.SOUTH));
					corridor.openSide((z + 1) == endZ ? Direction.WEST : Direction.SOUTH);
					corridor.openSide(Direction.NORTH);
					this.segments[endX][z + 1] = corridor;
				}
			} else
				this.segments[endX][endZ].openSide(Direction.WEST);
		} else {
			if (startZ > endZ) {
				this.segments[startX][startZ].openSide(Direction.NORTH);
				this.segments[endX][endZ].openSide(Direction.SOUTH);
				for (int z = startZ; z > endZ + 1; z--) {
					if (this.segments[endX][z - 1] != null) {
						this.segments[endX][z - 1].openSide(Direction.NORTH);
						this.segments[endX][z - 1].openSide(Direction.SOUTH);
						this.rotatePiece(this.segments[endX][z - 1]);
						continue;
					}
					DungeonPiece corridor = new DungeonPieces.Corridor(null, DungeonPieces.DEFAULT_NBT);
					corridor.setPosition(endX, z - 1);
					corridor.setRotation(RotationHelper.getRotationFromFacing(Direction.NORTH));
					corridor.openSide(Direction.SOUTH);
					corridor.openSide(Direction.NORTH);
					this.segments[endX][z - 1] = corridor;
				}
			} else if (startZ < endZ) {
				this.segments[startX][startZ].openSide(Direction.SOUTH);
				this.segments[endX][endZ].openSide(Direction.NORTH);
				for (int z = startZ; z < endZ - 1; z++) {
					if (this.segments[endX][z + 1] != null) {
						this.segments[endX][z + 1].openSide(Direction.SOUTH);
						this.segments[endX][z + 1].openSide(Direction.NORTH);
						this.rotatePiece(this.segments[endX][z + 1]);
						continue;
					}
					this.segments[endX][endZ].openSide(Direction.NORTH);
					DungeonPiece corridor = new DungeonPieces.Corridor(null, DungeonPieces.DEFAULT_NBT);
					corridor.setPosition(endX, z + 1);
					corridor.setRotation(RotationHelper.getRotationFromFacing(Direction.SOUTH));
					corridor.openSide(Direction.SOUTH);
					corridor.openSide(Direction.NORTH);
					this.segments[endX][z + 1] = corridor;
				}
			} else
				DungeonCrawl.LOGGER.warn(
						"Tried to build a connection between two positions but they were the same. ({}, {}) -> ({}, {})",
						startX, startZ, endX, endZ);
		}
	}

	public Position2D findLargeRoomPosWithMaxDistance(Position2D pos) {
		int x = pos.x, z = pos.z;
		int xHalf = width / 2 - 1, zHalf = length / 2 - 1;
		if (x > xHalf) {
			if (z > zHalf)
				return getLargeRoomPos(new Position2D(0, 0));
			else
				return getLargeRoomPos(new Position2D(0, length - 1));
		} else {
			if (z > zHalf)
				return getLargeRoomPos(new Position2D(width - 1, 0));
			else
				return getLargeRoomPos(new Position2D(width - 1, length - 1));
		}
	}

	public Tuple<Position2D, Rotation> findStarterRoomData(Position2D start) {
		for (int x = start.x - 2; x < start.x + 2; x++) {
			for (int z = start.z - 2; z < start.z + 2; z++) {
				if (Position2D.isValid(x, z, width, length)) {
					if (segments[x][z] != null && segments[x][z].getType() == 0 && segments[x][z].connectedSides < 4) {
						Tuple<Position2D, Rotation> data = findSideRoomData(new Position2D(x, z));
						if (data == null)
							continue;
						return data;
					}
				}
			}
		}
		return null;
	}

	public Tuple<Position2D, Rotation> findSideRoomData(Position2D base) {
		Position2D north = base.shift(Direction.NORTH, 1), east = base.shift(Direction.EAST, 1),
				south = base.shift(Direction.SOUTH, 1), west = base.shift(Direction.WEST, 1);

		if (north.isValid(width, length) && segments[north.x][north.z] == null)
			return new Tuple<Position2D, Rotation>(north, Rotation.COUNTERCLOCKWISE_90);

		if (east.isValid(width, length) && segments[east.x][east.z] == null)
			return new Tuple<Position2D, Rotation>(east, Rotation.NONE);

		if (south.isValid(width, length) && segments[south.x][south.z] == null)
			return new Tuple<Position2D, Rotation>(south, Rotation.CLOCKWISE_90);

		if (west.isValid(width, length) && segments[west.x][west.z] == null)
			return new Tuple<Position2D, Rotation>(west, Rotation.CLOCKWISE_180);

		return null;
	}

	public void rotatePiece(DungeonPiece piece) {
		if (piece.getType() == 12)
			return;
		switch (piece.connectedSides) {
		case 1:
			piece.setRotation(RotationHelper.getRotationFromFacing(DungeonPiece.getOneWayDirection(piece)));
			return;
		case 2:
			if (piece.sides[0] && piece.sides[2])
				piece.setRotation(RotationHelper.getRotationFromFacing(Direction.NORTH));
			else if (piece.sides[1] && piece.sides[3])
				piece.setRotation(RotationHelper.getRotationFromFacing(Direction.EAST));
			else
				piece.setRotation(RotationHelper.getRotationFromCW90DoubleFacing(DungeonPiece.getOpenSide(piece, 0),
						DungeonPiece.getOpenSide(piece, 1)));
			break;
		case 3:
			piece.setRotation(RotationHelper.getRotationFromTripleFacing(DungeonPiece.getOpenSide(piece, 0),
					DungeonPiece.getOpenSide(piece, 1), DungeonPiece.getOpenSide(piece, 2)));
			break;
		}
	}

	public void processAddition(Position2D[] additions, Position2D start, Position2D end, Position2D one, Random rand) {
		this.buildConnection(rand.nextBoolean() ? start : end, one);
		this.buildConnection(one, additions[rand.nextInt(additions.length)]);
	}

	/**
	 * Provides a random large room id.
	 */
	public static int getRandomLargeRoom(Random rand) {
		return LARGE_ROOMS[rand.nextInt(LARGE_ROOMS.length)];
	}

	public boolean canPutDoubleRoom(Position2D pos, Direction direction) {
//		DungeonCrawl.LOGGER.debug("[{}, {}] {}, {}", width, length, pos.x, pos.z);
		if (!pos.isValid(width, length) || segments[pos.x][pos.z] != null)
			return false;
		switch (direction) {
		case NORTH:
			return pos.z > 0 && this.segments[pos.x][pos.z - 1] == null;
		case EAST:
			return pos.x < width - 1 && this.segments[pos.x + 1][pos.z] == null;
		case SOUTH:
			return pos.z < length - 1 && this.segments[pos.x][pos.z + 1] == null;
		case WEST:
			return pos.x > 0 && this.segments[pos.x - 1][pos.z] == null;
		default:
			return false;
		}
	}

	public Position2D getLargeRoomPos(Position2D pos) {
		int a = Dungeon.SIZE - 1, x = pos.x, z = pos.z;

		if (x < a && z < a && get(x + 1, z) == null && get(x + 1, z + 1) == null && get(x, z + 1) == null)
			return pos;
		if (x < a && z > 0 && get(x + 1, z) == null && get(x + 1, z - 1) == null && get(x, z - 1) == null)
			return new Position2D(x, z - 1);
		if (x > 0 && z < a && get(x - 1, z) == null && get(x - 1, z + 1) == null && get(x, z + 1) == null)
			return new Position2D(x - 1, z);
		if (x > 0 && z > 0 && get(x - 1, z) == null && get(x - 1, z - 1) == null && get(x, z - 1) == null)
			return new Position2D(x - 1, z - 1);
		return null;
	}

	/**
	 * Test function: builds a dungeon layer in miniature form with wool for testing
	 * purposes (Size 16x1x16 Blocks)
	 */
	public void testBuildToWorld(World world, BlockPos start) {
		int posX = start.getX();
		int posY = start.getY();
		int posZ = start.getZ();
		for (int x = 0; x < width; x++) {
			for (int z = 0; z < length; z++) {
				world.setBlockState(new BlockPos(posX + x, posY, posZ + z), segments[x][z] == null
						? Blocks.AIR.getDefaultState()
						: segments[x][z] instanceof DungeonPieces.StairsBot ? Blocks.GREEN_WOOL.getDefaultState()
								: segments[x][z] instanceof DungeonPieces.StairsTop ? Blocks.RED_WOOL.getDefaultState()
										: segments[x][z] instanceof DungeonPieces.Room
												? Blocks.BLUE_WOOL.getDefaultState()
												: Blocks.WHITE_WOOL.getDefaultState());
			}
		}
	}

	public boolean isInitialized() {
		return this.segments != null;
	}

	public DungeonPiece get(int x, int z) {
		return segments[x][z];
	}

}
