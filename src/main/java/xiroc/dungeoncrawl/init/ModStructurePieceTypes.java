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

import net.minecraft.world.level.levelgen.structure.pieces.StructurePieceType;
import net.neoforged.neoforge.registries.DeferredHolder;
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
    DeferredHolder<StructurePieceType,?> ENTRANCE = DungeonCrawl.STRUCTURE_PIECE_TYPE.register("entrance", () -> noContext(DungeonEntrance::new));
    DeferredHolder<StructurePieceType,?> ROOM = DungeonCrawl.STRUCTURE_PIECE_TYPE.register("room", () -> noContext(DungeonRoom::new));
    DeferredHolder<StructurePieceType,?> CORRIDOR = DungeonCrawl.STRUCTURE_PIECE_TYPE.register("corridor", () -> noContext(DungeonCorridor::new));
    DeferredHolder<StructurePieceType,?> STAIRS = DungeonCrawl.STRUCTURE_PIECE_TYPE.register("stairs", () -> noContext(DungeonStairs::new));
    DeferredHolder<StructurePieceType,?> SIDE_ROOM = DungeonCrawl.STRUCTURE_PIECE_TYPE.register("side_room", () -> noContext(DungeonSideRoom::new));
    DeferredHolder<StructurePieceType,?> NODE_ROOM = DungeonCrawl.STRUCTURE_PIECE_TYPE.register("node_room", () -> noContext(DungeonNodeRoom::new));
    DeferredHolder<StructurePieceType,?> NODE_CONNECTOR = DungeonCrawl.STRUCTURE_PIECE_TYPE.register("node_connector", () -> noContext(DungeonNodeConnector::new));
    DeferredHolder<StructurePieceType,?> SECRET_ROOM = DungeonCrawl.STRUCTURE_PIECE_TYPE.register("secret_room", () -> noContext(DungeonSecretRoom::new));
    DeferredHolder<StructurePieceType,?> SPIDER_ROOM = DungeonCrawl.STRUCTURE_PIECE_TYPE.register("spider_room", () -> noContext(DungeonSpiderRoom::new));
    DeferredHolder<StructurePieceType,?> MULTIPART_MODEL_PIECE = DungeonCrawl.STRUCTURE_PIECE_TYPE.register("multipart_model_piece", () -> noContext(DungeonMultipartModelPiece::new));
    DeferredHolder<StructurePieceType,?> MEGA_NODE_PART = DungeonCrawl.STRUCTURE_PIECE_TYPE.register("mega_node_part", () -> noContext(DungeonMegaNodePart::new));

    private static StructurePieceType noContext(StructurePieceType.ContextlessType type) {
        return type;
    }

    static void init() {
    }
}
