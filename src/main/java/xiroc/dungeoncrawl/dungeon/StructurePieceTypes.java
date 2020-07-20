package xiroc.dungeoncrawl.dungeon;

/*
 * DungeonCrawl (C) 2019 - 2020 XYROC (XIROC1337), All Rights Reserved
 */

import net.minecraft.world.gen.feature.structure.IStructurePieceType;
import xiroc.dungeoncrawl.DungeonCrawl;
import xiroc.dungeoncrawl.dungeon.piece.*;
import xiroc.dungeoncrawl.dungeon.piece.room.DungeonNodeRoom;
import xiroc.dungeoncrawl.dungeon.piece.room.DungeonRoom;
import xiroc.dungeoncrawl.dungeon.piece.room.DungeonSecretRoom;
import xiroc.dungeoncrawl.dungeon.piece.room.DungeonSideRoom;

public class StructurePieceTypes {

    public static IStructurePieceType ENTRANCE;
    public static IStructurePieceType ROOM;
    public static IStructurePieceType CORRIDOR;
    public static IStructurePieceType LARGE_CORRIDOR;
    public static IStructurePieceType STAIRS;
    public static IStructurePieceType STAIRCASE;
    public static IStructurePieceType HOLE;
    public static IStructurePieceType SIDE_ROOM;
    public static IStructurePieceType NODE_ROOM;
    public static IStructurePieceType NODE_CONNECTOR;
    public static IStructurePieceType PRISONER_CELL;
    public static IStructurePieceType SECRET_ROOM;

    public static void registerAll() {
        DungeonCrawl.LOGGER.info("Registering Structure Piece Types");

        ENTRANCE = IStructurePieceType.register(DungeonEntrance::new, createKey("entrance"));
        ROOM = IStructurePieceType.register(DungeonRoom::new, createKey("room"));
        CORRIDOR = IStructurePieceType.register(DungeonCorridor::new, createKey("corridor"));
        LARGE_CORRIDOR = IStructurePieceType.register(DungeonCorridorLarge::new, createKey("large_corridor"));
        STAIRS = IStructurePieceType.register(DungeonStairs::new, createKey("stairs"));
        STAIRCASE = IStructurePieceType.register(DungeonStaircase::new, createKey("staircase"));
        HOLE = IStructurePieceType.register(DungeonCorridorHole::new, createKey("corridor_hole"));
        SIDE_ROOM = IStructurePieceType.register(DungeonSideRoom::new, createKey("side_room"));
        NODE_ROOM = IStructurePieceType.register(DungeonNodeRoom::new, createKey("node_room"));
        NODE_CONNECTOR = IStructurePieceType.register(DungeonNodeConnector::new, createKey("node_connector"));
        PRISONER_CELL = IStructurePieceType.register(DungeonPrisonCell::new, createKey("prison_cell"));
        SECRET_ROOM = IStructurePieceType.register(DungeonSecretRoom::new, createKey("secret_room"));
    }

    private static String createKey(String path) {
        return "dungeoncrawl:" + path;
    }

}