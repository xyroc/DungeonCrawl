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

import net.minecraft.world.level.levelgen.feature.StructurePieceType;
import xiroc.dungeoncrawl.DungeonCrawl;
import xiroc.dungeoncrawl.dungeon.piece.DungeonCorridor;
import xiroc.dungeoncrawl.dungeon.piece.DungeonEntrance;
import xiroc.dungeoncrawl.dungeon.piece.DungeonMultipartModelPiece;
import xiroc.dungeoncrawl.dungeon.piece.DungeonNodeConnector;
import xiroc.dungeoncrawl.dungeon.piece.DungeonStairs;
import xiroc.dungeoncrawl.dungeon.piece.room.DungeonMegaNodePart;
import xiroc.dungeoncrawl.dungeon.piece.room.DungeonNodeRoom;
import xiroc.dungeoncrawl.dungeon.piece.room.DungeonRoom;
import xiroc.dungeoncrawl.dungeon.piece.room.DungeonSecretRoom;
import xiroc.dungeoncrawl.dungeon.piece.room.DungeonSideRoom;
import xiroc.dungeoncrawl.dungeon.piece.room.DungeonSpiderRoom;

public class StructurePieceTypes {

    public static StructurePieceType ENTRANCE;
    public static StructurePieceType ROOM;
    public static StructurePieceType CORRIDOR;
    public static StructurePieceType STAIRS;
    public static StructurePieceType SIDE_ROOM;
    public static StructurePieceType NODE_ROOM;
    public static StructurePieceType NODE_CONNECTOR;
    public static StructurePieceType SECRET_ROOM;
    public static StructurePieceType SPIDER_ROOM;
    public static StructurePieceType MULTIPART_MODEL_PIECE;
    public static StructurePieceType MEGA_NODE_PART;

    public static StructurePieceType DUMMY;

    public static void register() {
        DungeonCrawl.LOGGER.info("Registering Structure Piece Types");

        ENTRANCE = StructurePieceType.setPieceId(DungeonEntrance::new, createKey("entrance"));
        ROOM = StructurePieceType.setPieceId(DungeonRoom::new, createKey("room"));
        CORRIDOR = StructurePieceType.setPieceId(DungeonCorridor::new, createKey("corridor"));
        STAIRS = StructurePieceType.setPieceId(DungeonStairs::new, createKey("stairs"));
        SIDE_ROOM = StructurePieceType.setPieceId(DungeonSideRoom::new, createKey("side_room"));
        NODE_ROOM = StructurePieceType.setPieceId(DungeonNodeRoom::new, createKey("node_room"));
        NODE_CONNECTOR = StructurePieceType.setPieceId(DungeonNodeConnector::new, createKey("node_connector"));
        SECRET_ROOM = StructurePieceType.setPieceId(DungeonSecretRoom::new, createKey("secret_room"));
        SPIDER_ROOM = StructurePieceType.setPieceId(DungeonSpiderRoom::new, createKey("spider_room"));
        MULTIPART_MODEL_PIECE = StructurePieceType.setPieceId(DungeonMultipartModelPiece::new, createKey("multipart_model_piece"));
        MEGA_NODE_PART = StructurePieceType.setPieceId(DungeonMegaNodePart::new, createKey("mega_node_part"));
    }

    private static String createKey(String path) {
        return "dungeoncrawl:" + path;
    }

}
