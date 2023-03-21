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

import net.minecraft.core.Registry;
import net.minecraft.world.level.levelgen.structure.pieces.StructurePieceType;
import xiroc.dungeoncrawl.DungeonCrawl;
import xiroc.dungeoncrawl.dungeon.piece.DungeonEntrance;
import xiroc.dungeoncrawl.dungeon.piece.DungeonPiece;

import java.util.Locale;

public class StructurePieceTypes {
    public static StructurePieceType GENERIC_PIECE;
    public static StructurePieceType ENTRANCE;

    public static void register() {
        DungeonCrawl.LOGGER.info("Registering Structure Piece Types");
        ENTRANCE = setPieceId(DungeonEntrance::new, createKey("entrance"));
        GENERIC_PIECE = setPieceId(DungeonPiece::new, createKey("generic_piece"));
    }

    private static String createKey(String path) {
        return "dungeoncrawl:" + path;
    }

    private static StructurePieceType setFullContextPieceId(StructurePieceType p_191152_, String p_191153_) {
        return Registry.register(Registry.STRUCTURE_PIECE, p_191153_.toLowerCase(Locale.ROOT), p_191152_);
    }

    private static StructurePieceType setPieceId(StructurePieceType.ContextlessType p_191146_, String p_191147_) {
        return setFullContextPieceId(p_191146_, p_191147_);
    }
}
