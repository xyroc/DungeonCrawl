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

package xiroc.dungeoncrawl.init;

import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.level.levelgen.structure.pieces.StructurePieceType;
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

public interface ModStructurePieceTypes {
    StructurePieceType ENTRANCE = register("entrance", DungeonEntrance::new);
    StructurePieceType ROOM = register("room", DungeonRoom::new);
    StructurePieceType CORRIDOR = register("corridor", DungeonCorridor::new);
    StructurePieceType STAIRS = register("stairs", DungeonStairs::new);
    StructurePieceType SIDE_ROOM = register("side_room", DungeonSideRoom::new);
    StructurePieceType NODE_ROOM = register("node_room", DungeonNodeRoom::new);
    StructurePieceType NODE_CONNECTOR = register("node_connector", DungeonNodeConnector::new);
    StructurePieceType SECRET_ROOM = register("secret_room", DungeonSecretRoom::new);
    StructurePieceType SPIDER_ROOM = register("spider_room", DungeonSpiderRoom::new);
    StructurePieceType MULTIPART_MODEL_PIECE = register("multipart_model_piece", DungeonMultipartModelPiece::new);
    StructurePieceType MEGA_NODE_PART = register("mega_node_part", DungeonMegaNodePart::new);

    private static StructurePieceType register(String name, StructurePieceType.ContextlessType type) {
        return Registry.register(BuiltInRegistries.STRUCTURE_PIECE, DungeonCrawl.locate(name), type);
    }

    static void init() {
    }
}
