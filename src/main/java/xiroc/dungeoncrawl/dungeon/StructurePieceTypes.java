package xiroc.dungeoncrawl.dungeon;

/*
 * DungeonCrawl (C) 2019 - 2020 XYROC (XIROC1337), All Rights Reserved 
 */

import net.minecraft.world.gen.feature.structure.IStructurePieceType;
import xiroc.dungeoncrawl.DungeonCrawl;
import xiroc.dungeoncrawl.dungeon.piece.DungeonCorridor;
import xiroc.dungeoncrawl.dungeon.piece.DungeonCorridorHole;
import xiroc.dungeoncrawl.dungeon.piece.DungeonCorridorRoom;
import xiroc.dungeoncrawl.dungeon.piece.DungeonCorridorTrap;
import xiroc.dungeoncrawl.dungeon.piece.DungeonEntranceBuilder;
import xiroc.dungeoncrawl.dungeon.piece.DungeonPart;
import xiroc.dungeoncrawl.dungeon.piece.DungeonStairs;
import xiroc.dungeoncrawl.dungeon.piece.room.DungeonRoom;
import xiroc.dungeoncrawl.dungeon.piece.room.DungeonSideRoom;

public class StructurePieceTypes {

	public static final IStructurePieceType ENTRANCE_BUILDER = IStructurePieceType
			.register(DungeonEntranceBuilder::new, create("entrance_builder"));
	public static final IStructurePieceType ROOM = IStructurePieceType.register(DungeonRoom::new,
			create("room"));
	public static final IStructurePieceType CORRIDOR = IStructurePieceType.register(DungeonCorridor::new,
			create("corridor"));
	public static final IStructurePieceType STAIRS = IStructurePieceType.register(DungeonStairs::new,
			create("stairs"));
	public static final IStructurePieceType HOLE = IStructurePieceType.register(DungeonCorridorHole::new,
			create("corridor_hole"));
	public static final IStructurePieceType CORRIDOR_ROOM = IStructurePieceType
			.register(DungeonCorridorRoom::new, create("corridor_room"));
	public static final IStructurePieceType CORRIDOR_TRAP = IStructurePieceType
			.register(DungeonCorridorTrap::new, create("trap"));
	public static final IStructurePieceType PART = IStructurePieceType.register(DungeonPart::new,
			create("part"));
	public static final IStructurePieceType SIDE_ROOM = IStructurePieceType.register(DungeonSideRoom::new,
			create("side_room"));
	
	private static String create(String path) {
		return DungeonCrawl.locate(path).toString();
	}

}
